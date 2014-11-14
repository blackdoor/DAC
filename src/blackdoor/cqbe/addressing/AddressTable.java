package blackdoor.cqbe.addressing;

import java.util.Comparator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import blackdoor.cqbe.addressing.Address.AddressComparator;
import blackdoor.cqbe.addressing.AddressException.MissingLayer3Exception;
import blackdoor.util.DBP;


/**
 * 
 * @author Cj Buresch
 * @version v0.0.1 - Nov 5, 2014
 */
public class AddressTable extends ConcurrentSkipListMap<byte[], Address>{
	
	
	public static final int MAX_SIZE = 256;

	public AddressTable() {
		this(new Address(Address.nullOverlay));
	}
	
	public AddressTable(Address reference){
		super(new Address.OverlayComparator(reference));
	}
	
	/**
	 * Adds the specified value to this table, they key is implied by the overlay address of the value. 
	 * If the table previously contained this address, the old address is replaced.
	 * If the table did not previously contain the address and the table is full, then farthest entry is removed from the table.
	 * @param value Address to be associated with the specified overlay address
	 * @return the previous value associated with the specified key, or null if there was no mapping for the key, or the value associated with the farthest entry which was removed as a result of this put
	 * @throws MissingLayer3Exception thrown if value does not have a layer 3 address defined.
	 */
	public Address add(Address value) throws MissingLayer3Exception{
		if(!value.hasLayer3())
			throw new AddressException.MissingLayer3Exception();
		Address absent = putIfAbsent(value.getOverlayAddress(), value);
		if(absent == null){
			absent = put(null, value);
			if(size() >= MAX_SIZE){
				return pollLastEntry().getValue();
			}
		}
		return absent;
	}
	
	public void addAll(Set<Address> c) throws MissingLayer3Exception{
		for(Address a : c){
			add(a);
		}
	}
	
	@Deprecated
	public Address put(byte[] key, Address value){
		return super.put(value.getOverlayAddress(), value);
	}
	
	public boolean containsValue(Address value){
		return containsKey(value.getOverlayAddress());
	}
	
	/**
	 * Obtains an Address table containing the k nearest addresses to reference
	 * @param k the number of nearby addresses to retrieve; less than k addresses may be returned, but not more
	 * @param reference 
	 * @return a new AddressTable (sorted with respect to reference) containing the k nearest addresses to reference
	 */
	public AddressTable getNearestAddresses(int k, Address reference){
		int i;
		AddressTable ret = new AddressTable(reference);
		NavigableSet<Address> sortedAddresses = new ConcurrentSkipListSet<Address>(new AddressComparator(reference));
		sortedAddresses.addAll(this.values());
		try {
			i = 0;
			for(Address a : sortedAddresses){
				ret.add(a);
				if(i++ > k)
					break;
			}
		} catch (MissingLayer3Exception e) {
			DBP.printException(e);
		}
		return ret;
	}
	
	//private ConcurrentSkipListMap<Address, Address> table;// = new ConcurrentSkipListMap<OverlayAddress, Address>();;



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
