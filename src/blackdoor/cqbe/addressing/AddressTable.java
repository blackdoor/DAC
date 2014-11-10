package blackdoor.cqbe.addressing;

import java.util.concurrent.ConcurrentSkipListMap;

import blackdoor.cqbe.addressing.Address.OverlayAddress;

/**
 * @author Cj Buresch
 * @version 0.0.1 11/3/2014
 * Responsibilities - Table containing the addresses of neighbors in a system for a specific node.
 */
public class AddressTable {

	private static ConcurrentSkipListMap<OverlayAddress, Address> table = new ConcurrentSkipListMap<OverlayAddress, Address>();;

	public AddressTable() {
	}

	/**
	 * Likely redundant. But who knows.
	 */
	public static void init() {

	}

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
	 * Returns the number of addresses in the table.
	 * <p>
	 * 
	 * @return Integer between 0 and 256.
	 */
	public static void size() {
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
