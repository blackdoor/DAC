package blackdoor.cqbe.node;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.addressing.CASFileAddress;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.output_logic.Router;
import blackdoor.cqbe.rpc.RPCException;
import blackdoor.cqbe.rpc.RPCException.JSONRPCError;
import blackdoor.util.DBP;

public class Updater implements Runnable {
	
	/**
	 * in seconds
	 */
	public static final long updateInterval = 5;
	
	private Thread updaterThread;
	private volatile boolean running;
	private AddressTable atable;
	private Map<L3Address, Integer> strikeList;
	
	public Updater(){
		atable = Node.getAddressTable();
		running = true;
		strikeList = new HashMap<L3Address, Integer>();
	}
	
	public Updater(AddressTable atable){
		this.atable = atable;
		running = true;
		strikeList = new HashMap<L3Address, Integer>();
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
		updaterThread.sleep(updateInterval * 1000);
		update();
	}
	
	public void stop(){
		running = false;
		updaterThread.interrupt();
	}
	
	protected void strike(L3Address addr){
		if(strikeList.containsKey(addr)){
			strikeList.put(addr, strikeList.get(addr) + 1);
			if(strikeList.get(addr) > 2){
				Node.getAddressTable().remove(addr);
				//strikeList.remove(addr);
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
	
	protected void update(){
		DBP.printdebugln(atable);
		/*
		Router r = new Router(Node.getAddressTable());
		//alternative method to finding new neighbors (alternative to the foreach loop below)
		Set<L3Address> cans = Node.getAddressTable().valueSet();
		cans.addAll(r.iterativeLookup(Node.getOverlayAddress()).valueSet());
		for(L3Address can : cans){
			try {
				if(Router.ping(can)){
					Node.getAddressTable().add(can);
					forgive(can);
				}else{
					strike(can);
				}
			} catch (RPCException e) {
				DBP.printException(e);
				strike(can);
			}
		}
		*/
		
		//find new neighbors
		for(Entry<byte[], L3Address> neighbor : atable.entrySet()){
			try {
				AddressTable candidates = Router.primitiveLookup(neighbor.getValue(), Node.getOverlayAddress());
				for(Entry<byte[], L3Address> candidate : candidates.entrySet()){
					if(Router.ping(candidate.getValue())){
						Node.getAddressTable().add(candidate.getValue());
					}else{
						strike(candidate.getValue());
					}
					forgive(candidate.getValue());
				}
			} catch (IOException e) {
				strike(neighbor.getValue());
			} catch (RPCException e) {
				DBP.printException(e);
			}
		}
		
		//poll for new storage values
		for(Entry<byte[], L3Address> neighbor : atable.entrySet()){
			try {
				List<Address> keys = Router.getIndex(neighbor.getValue(), 1);
				for(Address key : keys){
					if (!Node.getStorageController().containsKey(key)) {
						byte[] value = Router.getValue(neighbor.getValue(), key);
						Node.getStorageController().put(
								new CASFileAddress(Node.getStorageController()
										.getDomain(), value));
					}
				}
				forgive(neighbor.getValue());
			} catch (RPCException e) {
				DBP.printException(e);
				if(e.getRPCError().equals(JSONRPCError.INVALID_RESULT))
					strike(neighbor.getValue());
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

}
