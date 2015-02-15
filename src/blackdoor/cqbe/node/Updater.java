package blackdoor.cqbe.node;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Map;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.Address.AddressComparator;
import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.output_logic.Router;
import blackdoor.cqbe.rpc.RPCException;
import blackdoor.cqbe.storage.StorageController;
import blackdoor.util.DBP;


/**
 * Responsibility - Continually update the address table of a node by pinging current AT members, removing non-responsive nodes,
 * 					and finding new neighbors to populate the address table.
 * @author Cyril Van Dyke
 * @version 1.1
 * 
 */

public class Updater implements Runnable{
	private Router r;
	volatile Boolean timer;
	private HashSet<Address> firstStrike = new HashSet<Address>();
	private HashSet<Address> secondStrike = new HashSet<Address>();
	
	public Updater() {

	}

	

	@Override
	public void run() {
		try {
			r = new Router(Node.getAddressTable());
			timer = true;
			Node.getAddressTable().addAll(r.iterativeLookup(Node.getOverlayAddress()).values());
			updateTimer(); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * Timer function that tells Updater when to check for updates
	 * When updaterTimer fires, update() is run.
	 * @throws UnknownHostException 
	 */
	private void updateTimer() throws UnknownHostException{
		update();
		try{
			while (timer){
				Thread.sleep(6000);
				update();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Stops updateTimer from firing, preventing update from happening.
	 */
	public void stopUpdater(){
		timer = false;
	}
	
	/**
	 * Starts updateTimer, allowing updates to occur.
	 */
	public void startUpdater(){
		timer = true;
	}
	
	/**
	 * Returns the boolean timer.
	 * @return boolean timer which enables updates to occur.
	 */
	public Boolean getTimer(){
		return timer;
	}
	
	/**
	 * Returns the firstStrike list, containing all neighbors who currently have 1 strike against them.
	 * @return HashSet firstStrike
	 */
	public HashSet<Address> getFS(){
		return firstStrike;
	}
	
	/**
	 * Returns the secondStrike list, containing all neighbors who currently have 2 strikes against them.
	 * @return HashSet secondStrike
	 */
	public HashSet<Address> getSS(){
		return secondStrike;
	}
	
	/**
	 * Checks for needed updates in the Node's AddressTable
	 * Updates the neighbors of a Node, removing nonresponsive nodes and searching to add new 
	 * @throws UnknownHostException 
	 */
	public void update() throws UnknownHostException{
		DBP.printdebug(Node.getAddressTable().size());
		if(Node.getAddressTable().isEmpty()){
			//Node has no neighbors, must populate AddressTable
			Node.getAddressTable().addAll(r.iterativeLookup(Node.getOverlayAddress()).values());
		}
		else
		{
			AddressTable toRemove = new AddressTable();
			toRemove = pingNeighbors();
			if(!toRemove.isEmpty()){
				for(Map.Entry<byte[], L3Address> entry : toRemove.entrySet())
				{
					//Remove all nonresponsive nodes
					DBP.printdemoln("Removing unresponsive " + entry.getValue() + " from address table");
					Node.getAddressTable().remove(entry.getValue());
				}
			}
		}
		//updateStorage();
	}

	/**
	 * Pings neighbors, returning list of neighbors that did not respond to ping
	 * @return List of neighbors that did not respond.
	 */
	public AddressTable pingNeighbors(){
		AddressTable add = Node.getAddressTable();
		AddressTable at = new AddressTable();
		for(L3Address a : add.values())
		{
			try {
				if(!Router.ping(a))
				{
					//If no response is received, move a up in strike priority
					DBP.printdevln("No response from " + a);
					if(firstStrike.contains(a)){
						//a already has a strike against it, go to second strike.
						secondStrike.add(a);
						firstStrike.remove(a);
					}
					else if(secondStrike.contains(a)){
						//a has two strikes against it, move to removal list.
						at.add(a);
						secondStrike.remove(a);

					}
					else{
						//a doesn't have any strikes against it, add it to the strike list.
						firstStrike.add(a);
					}
				} 
				else
				{
					//a has responded, decrement strike-value
					if(firstStrike.contains(a))
						firstStrike.remove(a);
					else if(secondStrike.contains(a))
						secondStrike.remove(a);
					Node.getAddressTable().addAll(addNewNeighbors(a).values());
					DBP.printdevln("Response Received from " + a);
					
				}
			} catch (RPCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return at;
		
	}
	
	/**
	 * Returns a list of new neighbors to be added to a Node's address table;
	 * @param current
	 * @param noResponse: The list of neighbors that did not respond 3 times in a row, to be removed.
	 * @return 
	 */
	public AddressTable addNewNeighbors(L3Address a){
		AddressTable toAdd = new AddressTable();
		AddressTable candidates = new AddressTable();
			try{
				candidates = Router.primitiveLookup(a, Node.getOverlayAddress());
			} catch(IOException ioE) {
				DBP.printerrorln(ioE);
			} catch (RPCException e) {
				DBP.printerrorln(e);
			}
			for(L3Address c : candidates.values()){
				if(Node.getAddressTable().contains(c))
					continue;
				DBP.printdemoln("Adding " + c + " to address table from updater");
				toAdd.add(c);
			}
		return toAdd;
	}
	
	
	public void updateStorage(){
		try {
			if(Node.getAddressTable().size() > 0)
				Node.getStorageController().deleteThirdBucket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Node.getStorageController().garbageCollectReferences();
		
	}
	
	
	
	
}
