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
				Thread.sleep(60000);
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
		if(Node.getAddressTable().isEmpty()){
			//Node has no neighbors, let's fix that.
			AddressTable temp = r.iterativeLookup(Node.getOverlayAddress());
			Node.getAddressTable().addAll(temp.values());
		}
		else
		{
			AddressTable toRemove = new AddressTable();
			toRemove = pingNeighbors();
			/*if(Node.getInstance().hasComplement())
			{
				AddressTable dnrC = new AddressTable();
				dnrC = pingComplement(Node.getInstance().getComplementTable());
			}
			*/
			if(!toRemove.isEmpty()){
				if(toRemove.equals(Node.getAddressTable())){
					//We're probably offline boys...
					DBP.printerrorln("No one address table responded, Node most likely offline.");
				}
				for(Map.Entry<byte[], L3Address> entry : toRemove.entrySet())
				{
					if(toRemove.equals(Node.getAddressTable())){
						//Most likely offline, that's a problem dude.
						DBP.printerrorln("Node is most likely offline, cannot connect to any neighbors");
					}
					else {
						//Remove all nonresponsive nodes
						Node.getAddressTable().remove(entry.getKey(),entry.getValue());
					}
				}
				Node.getAddressTable().addAll(addNewNeighbors(Node.getAddressTable(),toRemove).values());
				updateStorage();
				reviewStorage();
			}
		}
	}

	/**
	 * Checks to add new AT values to storage using the storagecontroller
	 * 
	 */
	private void reviewStorage(){
		AddressTable neighborTable = new AddressTable();
		AddressComparator ac = new Address.AddressComparator(Node.getAddress());
		for(L3Address a : Node.getAddressTable().values()){
			try {
				neighborTable = Router.primitiveLookup(a, a);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RPCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(L3Address b : neighborTable.values())
			{
				if(Node.getAddressTable().containsValue(b)){
					continue;
				}
				else{
					if(ac.compare(Node.getAddressTable().lastEntry().getValue(), b) >= 0){
						//TODO Send get RPC with index 0 to that address
						try {
							Node.getAddressTable().add((L3Address)Router.getIndex(b,0).get(0));
						} catch (RPCException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	/**
	 * Returns a list of new neighbors to be added to a Node's address table;
	 * @param current
	 * @param noResponse: The list of neighbors that did not respond 3 times in a row, to be removed.
	 * @return 
	 */
	public AddressTable addNewNeighbors(AddressTable current,AddressTable noResponse){
		HashSet<L3Address> visited = new HashSet<L3Address>();
		AddressTable toAdd = new AddressTable();
		for(L3Address a : current.values()){
			if(noResponse.containsValue(a))
				continue;
				//This might totally be redundant
			AddressTable candidates = null;
			try{
				candidates = Router.primitiveLookup(a, Node.getOverlayAddress());
			} catch(IOException ioE) {
				DBP.printerrorln(ioE);
			} catch (RPCException e) {
				DBP.printerrorln(e);
			}
			if(candidates == null){
				continue;
			}
			for(L3Address c : candidates.values()){
				try {
					if(visited.contains(c) || !Router.ping(c) || current.containsValue(c)){
						continue;
					}
				} catch (RPCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				toAdd.add(c);
			}
			visited.add(a);
			current.addAll(toAdd.values());
			toAdd.clear();
		}
		return current;
	}
	
	
	
	public void updateStorage(){
		try {
			Node.getStorageController().deleteThirdBucket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Node.getStorageController().garbageCollectReferences();
		
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
					//a has responded, 
					if(firstStrike.contains(a))
						firstStrike.remove(a);
					if(secondStrike.contains(a))
						secondStrike.remove(a);
					DBP.printdevln("Response Received from " + a);
				}
			} catch (RPCException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return at;
		
	}




	
	
	/**
	 * Similar to pingNeighbors, but pings the members of the optional complement table
	 * @return List of nonresponsive complements 
	 */
/*
	public AddressTable pingComplement(AddressTable complement){
		AddressTable at = new AddressTable();
		L3Address adr = new L3Address();
		byte[] key = null;
		
		for(Map.Entry<byte[], L3Address> entry: complement.entrySet())
		{
			if(!r.ping(entry.getValue()))
			{
				//If no response is received from ping, add to return table.
				at.add(entry.getValue());
			}
			else
			{
				System.out.println("Response Received from " + entry.getKey());
			}
		}
		return at;
		
	}
*/
	
}
