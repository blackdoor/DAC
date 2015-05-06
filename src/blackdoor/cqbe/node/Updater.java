package blackdoor.cqbe.node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import java.util.concurrent.LinkedBlockingQueue;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.addressing.CASFileAddress;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.output_logic.Router;
import blackdoor.cqbe.rpc.RPCException;
import blackdoor.cqbe.rpc.RPCException.JSONRPCError;
import blackdoor.cqbe.settings.Config;
import blackdoor.net.SocketIOWrapper;
import blackdoor.util.DBP;

public class Updater implements Runnable {
	
	/**
	 * in seconds
	 */
	public static final long updateInterval = (int) Config.getReadOnly("node_update_interval","default.config");
	public static int PARALLELISM = (int) Config.getReadOnly("node_update_parallelism","default.config");
	private Thread updaterThread;
	private volatile boolean running;
	private Map<L3Address, Integer> strikeList;
	
	public Updater(){
		running = true;
		strikeList = new ConcurrentHashMap<L3Address, Integer>();
	}
	

	@Override
	public void run() {
		if(updaterThread == null){
			updaterThread = Thread.currentThread();
		}
		
		while(running){
			try {
				schedule();
			} catch (InterruptedException e) {
				DBP.printwarningln("Updater thread interrupted!");
				run();
			}
		}
		DBP.printwarningln("Updater is stopping!");
	}
	
	protected void schedule() throws InterruptedException{
		Thread.sleep(updateInterval * 1000);
		update();
	}
	
	public void stop(){
		running = false;
		updaterThread.interrupt();
	}
	
	protected void strike(L3Address addr){
		//DBP.printdemoln("striking " + addr);
		if(strikeList.containsKey(addr)){
			strikeList.put(addr, strikeList.get(addr) + 1);
			if(strikeList.get(addr) > 2){
				if(Node.getAddressTable().contains(addr))
					DBP.printdevln("Removing " + addr + " from address table due to 3d strike");
				Node.getAddressTable().remove(addr);
				strikeList.remove(addr);
			}
		}else{
			strikeList.put(addr, 1);
		}
	}
	
	protected void forgive(L3Address addr){
		if(strikeList.containsKey(addr)){
			strikeList.put(addr, strikeList.get(addr) - 1);
			if(strikeList.get(addr) <= 0)
				strikeList.remove(addr);
		}
	}
	
	private void pingHeadcount(InetAddress ip, int port) throws IOException{
		SocketIOWrapper io = new SocketIOWrapper(new Socket(ip, port));
		io.write(Node.getAddress().toJSONString());
		io.close();
	}

    private void updateAT() throws InterruptedException {
        Set<L3Address> every1ISee = Collections.newSetFromMap(new ConcurrentHashMap<L3Address, Boolean>());
        Router r = new Router(Node.getAddressTable());
        BlockingQueue<L3Address> q = new LinkedBlockingQueue<L3Address>();
        every1ISee.addAll(Node.getAddressTable().values());
        every1ISee.addAll(r.iterativeLookup(Node.getOverlayAddress()).values());
        q.addAll(every1ISee);
        ArrayList<Thread> pool = new ArrayList<Thread>();

        for(int i = 0; i < Runtime.getRuntime().availableProcessors() * PARALLELISM ; i++){
            Thread t = new Thread(new AddressUpdateThread(q, every1ISee, true, this));
            pool.add(t);
            t.start();
        }

        for(Thread t : pool){
            t.join();
        }

        pool.clear();
        q.clear();
        q.addAll(every1ISee);
        pool = new ArrayList<Thread>();

        for(int i = 0; i < Runtime.getRuntime().availableProcessors() * PARALLELISM ; i++){
            Thread t = new Thread(new AddressUpdateThread(q, every1ISee, false, this));
            pool.add(t);
            t.start();
        }

        for(Thread t : pool){
            t.join();
        }
    }

    private void updateStorage(){
        int numThreads = PARALLELISM * Runtime.getRuntime().availableProcessors();
        Map<Address, L3Address> keys = new ConcurrentHashMap<>();
        List<Thread> threads = new LinkedList<>();
        int i = 0;
        L3Address[] table = new L3Address[1];
        table = Node.getAddressTable().values().toArray(table);
        for(int t = 0; t < numThreads; t++){
            Set<L3Address> neighbors = new HashSet<>();
            for(int j = 0; j < (Node.getAddressTable().size()/numThreads) + 1 && i < table.length; j++){
                neighbors.add(table[i++]);
            }
            Thread thread = new Thread(new StorageUpdateThread(keys, neighbors));
            thread.start();
            threads.add(thread);
        }
        for(Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                DBP.printException(e);
            }
        }

        threads.clear();
        for(Map.Entry<Address, L3Address> entry : keys.entrySet()){
            final Map.Entry<Address, L3Address> e = entry;
            if(!Node.getStorageController().containsKey(entry.getKey())) {
                Thread t = new Thread() {
                    public void run() {
                        try {
                            CASFileAddress val = new CASFileAddress(Node.getStorageController().getDomain(), Router.getValue(e.getValue(), e.getKey()));
                            Node.getStorageController().put(val);
                        } catch (IOException |RPCException e1) {
                            DBP.printException(e1);
                        }
                    }
                };
                threads.add(t);
                t.start();
            }
        }
        for(Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                DBP.printException(e);
            }
        }

        //update storage controller
        Node.getStorageController().garbageCollectReferences();
        try {
            Node.getStorageController().deleteThirdBucket();
        } catch (IOException e) {
            // TODO probably shouldn't be throwing this, should change in StorageController
            DBP.printerrorln("trouble deleting 3rd bucket");//e.printStackTrace();
        }
    }

    private class StorageUpdateThread implements Runnable{
        Map<Address, L3Address> keys;
        Set<L3Address> neighbors;

        public StorageUpdateThread(Map<Address, L3Address> keys, Set<L3Address> neighbors){
            this.keys = keys;
            this.neighbors = neighbors;
        }

        @Override
        public void run() {

            for(L3Address neighbor : neighbors){
                try {
                    Set<Address> result = Router.getIndex(neighbor, 1);
                    for(Address a : result){
                        keys.put(a, neighbor);
                    }
                } catch (RPCException e) {
                    DBP.printException(e);
                    if(e.getRPCError().equals(JSONRPCError.INVALID_RESULT))
                        strike(neighbor);
                } catch (IOException e) {
                    DBP.printerrorln("IO error updating storage from " + neighbor);
                    DBP.printerror(e);
                }
            }

        }
    }
	
	protected void update() throws InterruptedException{
		DBP.printdebugln(Node.getAddressTable());

        updateAT();

        updateStorage();
		
		//update storage controller
		Node.getStorageController().garbageCollectReferences();
		try {
			Node.getStorageController().deleteThirdBucket();
		} catch (IOException e) {
			// TODO probably shouldn't be throwing this, should change in StorageController
			DBP.printerrorln("trouble deleting 3rd bucket");//e.printStackTrace();
		}

		System.gc();
	}
	
	private static class AddressUpdateThread implements Runnable{
		
		BlockingQueue<L3Address> q;
		Set<L3Address> everyone;
		boolean seek;
		Updater ref;
		
		public AddressUpdateThread(BlockingQueue<L3Address> q, Set<L3Address> everyone, boolean seek, Updater ref) {
			this.q = q;
			this.everyone = everyone;
			this.seek = seek;
			this.ref = ref;
		}

		@Override
		public void run() {
			for(L3Address node = q.poll(); node != null; node = q.poll()){
				if(seek){
					try {
						AddressTable nn = Router.primitiveLookup(node, Node.getOverlayAddress());
						everyone.addAll(nn.values());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//DBP.printException(e);
					} catch (RPCException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					try {
						if(Router.ping(node)){
							Node.getAddressTable().add(node);
							ref.forgive(node);
						}else{
							ref.strike(node);
						}
					} catch (RPCException e) {
						ref.strike(node);
						// TODO determine if exception was somehow not node's fault, else strike node
						DBP.printException(e);
					}
				}
			}
		}
	}
}
