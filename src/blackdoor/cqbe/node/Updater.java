package blackdoor.cqbe.node;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Map;

import org.json.JSONObject;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.output_logic.Router;
import blackdoor.cqbe.rpc.RPCBuilder;
import blackdoor.cqbe.rpc.RPCException;
import blackdoor.util.DBP;


/**
 * Responsibility - Continually update the address table of a node by pinging current AT members, removing non-responsive nodes,
 * 					and finding new neighbors to populate the address table.
 * @author Cyril Van Dyke
 * @version 1.0
 * 
 */

public class Updater implements Runnable{
	private Router r;
	volatile Boolean timer;
	private HashSet<Address> firstStrike = new HashSet<Address>();
	private HashSet<Address> secondStrike = new HashSet<Address>();
	
	public Updater() throws InterruptedException {

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
	 * @throws UnknownHostException 
	 */
	@SuppressWarnings("unused")
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
	
	public void stopUpdater(){
		timer = false;
	}
	
	public void startUpdater(){
		timer = true;
	}
	
	public Boolean getTimer(){
		return timer;
	}
	
	public HashSet<Address> getFS(){
		return firstStrike;
	}
	
	public HashSet<Address> getSS(){
		return secondStrike;
	}
	
	/**
	 * Checks for needed updates then calls appropriate helper functions to update the node
	 * @throws UnknownHostException 
	 */
	public void update() throws UnknownHostException{
		if(Node.getAddressTable().isEmpty()){
			//Node has no neighbors, let's fix that.
			AddressTable temp = Router.iterativeLookup(Node.getInstance().getOverlayAddress());
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
					if(toRemove.equals(Node.getInstance().getAddressTable())){
						//Most likely offline, that's a problem dude.
						DBP.printerrorln("Node is most likely offline, cannot connect to any neighbors");
					}
					else {
						Node.getInstance();
						//Remove all nonresponsive nodes
						Node.getAddressTable().remove(entry.getKey(),entry.getValue());
					}
				}
				Node.getInstance();
				Node.getAddressTable().addAll(addNewNeighbors(Node.getInstance().getAddressTable(),toRemove).values());
				updateStorage();
			}
		}
	}

	private void reviewStorage(){
		AddressTable neighborTable = new AddressTable();
		for(L3Address a : Node.getAddressTable().values()){
			try {
				neighborTable = r.primitiveLookup(a, a);
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
					if(b.getOverlayAddress() < Node.getAddressTable().lastKey())
					{
						//TODO Send get RPC with index 0 to that address
					}
				}
			}
		}
	}
	
	public AddressTable addNewNeighbors(AddressTable current,AddressTable noResponse){
		HashSet<L3Address> visited = new HashSet<L3Address>();
		AddressTable toAdd = new AddressTable();
		for(L3Address a : current.values()){
			if(noResponse.containsValue(a))
				continue;
				//This might totally be redundant
			AddressTable candidates = null;
			try{
				candidates = Router.primitiveLookup(a, Node.getInstance().getOverlayAddress());
			} catch(IOException ioE) {
				DBP.printerrorln(ioE);
			} catch (RPCException e) {
				DBP.printerrorln(e);
			}
			if(candidates == null){
				continue;
			}
			for(L3Address c : candidates.values()){
				if(visited.contains(c) || !Router.ping(c) || current.containsValue(c)){
					continue;
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
			Node.getInstance().getStorageController().deleteThirdBucket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Node.getInstance().getStorageController().garbageCollectReferences();
		
	}
	
	
	
	/**
	 * Pings neighbors, returning list of neighbors that did not respond to ping
	 * @return List of neighbors that did not respond.
	 */
	public AddressTable pingNeighbors(){
		Node.getInstance();
		AddressTable add = Node.getAddressTable();
		AddressTable at = new AddressTable();
	
		for(L3Address a : add.values())
		{
			if(!Router.ping(a))
			{
				//If no response is received, move a up in strike priority
				if(firstStrike.contains(a)){
					firstStrike.remove(a);
					secondStrike.add(a);
				}
				else if(secondStrike.contains(a)){
					secondStrike.remove(a);
					at.add(a);
				}
				else{
					firstStrike.add(a);
				}
			} 
			else
			{
				if(firstStrike.contains(a))
					firstStrike.remove(a);
				if(secondStrike.contains(a))
					secondStrike.remove(a);
				DBP.printdevln("Response Received from " + a);
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
