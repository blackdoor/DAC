package blackdoor.cqbe.node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
					DBP.printdemoln("Removing " + addr.l3ToString() + " from table");
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
	
	protected void update() throws InterruptedException{
		DBP.printdebugln(Node.getAddressTable());
		//DBP.printdemoln(Node.getAddressTable().size());
		//find new neighbors
	
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
		
		/*
		 * serial code below replaced by parallel code above
		Set<L3Address> temp = new HashSet<L3Address>();
		for(L3Address node : every1ISee){
			try {
				temp.addAll(Router.primitiveLookup(node, Node.getOverlayAddress()).values());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				DBP.printException(e);
			} catch (RPCException e) {
				// TODO Auto-generated catch block
				DBP.printException(e);
			}
		}
		every1ISee.addAll(temp);
		temp = null;
		*/
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
		
		/* serial code below replaced by parallel code above
		for(L3Address node : every1ISee){
			try {
				if(Router.ping(node)){
					if(!Node.getAddressTable().contains(node))
						DBP.printdemoln("Adding " + node + " to table from updater");
					Node.getAddressTable().add(node);
					forgive(node);
				}else{
					strike(node);
				}
			} catch (RPCException e) {
				// TODO determine if exception was somehow not node's fault, else strike node
				DBP.printException(e);
			}
		}
		*/
		
		//poll for new storage values
		for(L3Address neighbor : Node.getAddressTable()){
			try {
				List<Address> keys = Router.getIndex(neighbor, 1);
				for(Address key : keys){
					if (!Node.getStorageController().containsKey(key)
							&& Node.getOverlayAddress()
									.getComparator()
									.compare(key, Node.getStorageController().getHighest()) <= 0) {
						byte[] value = Router.getValue(neighbor, key);
						Node.getStorageController().put(
								new CASFileAddress(Node.getStorageController()
										.getDomain(), value));
					}
				}
				// too much forgiving: forgive(neighbor);
			} catch (RPCException e) {
				DBP.printException(e);
				if(e.getRPCError().equals(JSONRPCError.INVALID_RESULT))
					strike(neighbor);
			} catch (IOException e) {
				//TODO
				DBP.printException(e);
			}
		}
		
		//update storage controller
		Node.getStorageController().garbageCollectReferences();
		try {
			Node.getStorageController().deleteThirdBucket();
		} catch (IOException e) {
			// TODO probably shouldn't be throwing this, should change in StorageController
			e.printStackTrace();
		}
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
						DBP.printException(e);
					} catch (RPCException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					try {
						if(Router.ping(node)){
							if(!Node.getAddressTable().contains(node))
								DBP.printdemoln("Adding " + node.l3ToString() + " to table from updater");
							Node.getAddressTable().add(node);
							ref.forgive(node);
						}else{
							ref.strike(node);
						}
					} catch (RPCException e) {
						// TODO determine if exception was somehow not node's fault, else strike node
						DBP.printException(e);
					}
				}
			}
		}
	}
}
