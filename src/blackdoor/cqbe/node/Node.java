package blackdoor.cqbe.node;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressException;
import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.addressing.Address.OverlayComparator;
import blackdoor.util.DBP;

/**
 * 
 * @author nfischer3
 *
 */
public class Node {
	
	private static Node singleton;
		
	private AddressTable addressTable;
	private volatile int n;
	private volatile int o;

	protected Node() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Closes the node and returns a list of folders containing node data
	 * 
	 * @return - A list of strings containing the folder locations of the node
	 *         storage, address table, and updater
	 */
	public String[] destroyNode() {
		return null;
	}

	/**
	 * Prints a brief status of node
	 */
	public void statusCheck() {
	}

	/**
	 * Lists the current status of the node's storage including space used, etc.
	 */
	public void checkStorage() {

	}
	
	private static synchronized void checkAndThrow(){
		if(singleton == null){
			throw new ExceptionInInitializerError("Node singleton is null. Node has not been built yet.");
		}
	}

	/**
	 * Returns the address table of the node
	 * 
	 * @return Address Table of node
	 */
	public static AddressTable getAddressTable() {
		return getInstance().addressTable;
	}
	
	protected static Node getInstance(){
		checkAndThrow();
		return singleton;
	}
	
	public static int getN(){
		return getInstance().n;
	}
	
	public static Address getOverlayAddress(){
		OverlayComparator c = (OverlayComparator) getInstance().addressTable.comparator();
		try {
			return new Address(c.getReferenceAddress());
		} catch (AddressException e) {
			DBP.printerrorln("The address in the OverlayComparator is not valid for building a new Address object");
			DBP.printerrorln("THIS IS BAAAAADDDD GO TO GITHUB AND OPEN AN ISSUE NOOWWW!!!");
			DBP.printException(e);
		}
		return null;
	}
	
	
}
