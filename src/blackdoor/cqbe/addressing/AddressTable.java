package blackdoor.cqbe.addressing;

import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListMap;

import blackdoor.cqbe.addressing.Address.AddressComparator;


/**
 * 
 * @author Cj Buresch
 * @version v0.0.1 - Nov 5, 2014
 */
public class AddressTable extends ConcurrentSkipListMap<byte[], Address>{
	
	
	public static final int MAX_SIZE = 256;
	
	public AddressTable(Address refrence){
		super(new Address.OverlayComparator(refrence));
	}
	
	/**
	 * Associates the specified value with the specified key in this table. 
	 * If the table previously contained a mapping for the key, the old value is replaced.
	 * If the table did not previously contain a mapping for key and the table is full, then farthest entry is removed from the table.
	 * @param key overlay address with which the specified Address is to be associated
	 * @param value Address to be associated with the specified overlay address
	 * @return the previous value associated with the specified key, or null if there was no mapping for the key, or the value associated with the farthest entry which was removed as a result of this put
	 */
	public Address put(byte[] key, Address value){
		Address absent = putIfAbsent(key, value);
		if(size() >= MAX_SIZE && absent == null){
			return pollLastEntry().getValue();
		}
		return absent;
	}
	

	//private ConcurrentSkipListMap<Address, Address> table;// = new ConcurrentSkipListMap<OverlayAddress, Address>();;

	public AddressTable() {
	}

	/**
	 * Likely redundant. But who knows.
	 *
	public static void init() {

	}
	yea, redundant
	*/

	/**
	 * Adds an address to the address table.
	 * <p>
	 * 
	 * @param Address
	 * 
	 */
	public static void addAddress() {

	}

	/**
	 * Removes an address from the address table.
	 * <p>
	 * 
	 * @param Address
	 * 
	 */
	public static void removeAddress() {

	}

	/**
	 * Adds an address to the address table.
	 * <p>
	 * 
	 * @param Address
	 *            TODO might overload this with ones that will take a IPv6,
	 *            OverlayAddress or whatever.
	 * @return True or false depending on if the address table contains
	 */
	public static void containsAddress() {
	}

	/**
	 * Checks to see if an address belongs in this table.
	 * <p>
	 * 
	 * An address will be stored if the table is not full or if the new address
	 * is closer than the furthest address in the table.
	 * 
	 * @return boolean. True or False if it does belong in table. TODO might
	 *         store it as well?
	 */
	public static void belongsInTable() {
	}

	/**
	 * Sorts the address table based on distance.
	 * <p>
	 * 
	 */
	public static void sort() {
	}

	// More functions that are basically just a wrapper for the MAP

	/*
	 * GETTERS and SETTERS
	 */

}
