package blackdoor.cqbe.addressing;

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

	/**
	 * Associates the specified Address with the overlay address of the specified Address in this map (optional operation).
	 * If the map previously contained a mapping for the Address, the old value is replaced by the specified value.
	 * (A map m is said to contain a mapping for a key k if and only if m.containsKey(k) would return true.)
	 * In general add(Address) should be used instead. This method can be dangerous as it can overfill the table.
	 * @param value value to be added to the map
	 * @return the previous Address object at that overlay address, or null if there was Address with that overlay.
	 */
	public Address put(Address value){
		return put(value.getOverlayAddress(), value);
	}

	/**
	 * Same as the put contract for Map, except the key is implied by the value. In other words only the value parameter has any effect.
	 * @param _
	 * @param value
	 * @return
	 */
	@Deprecated
	public Address put(byte[] _, Address value){
		if(!value.hasLayer3()){
			throw new RuntimeException("NO LAYER 3 ADDRESS IN VALUE: " + value);
		}
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

	/**
	 * If the table contains more than MAX_SIZE entries, remove the farthest addresses until it is less than MAX_SIZE
	 * This has a horrible time complexity.
	 * Due to the non-consistent nature of finding the size in skip lists, this method is not guaranteed result in < MAX_SIZE entries.
	 * @return true if the table was affected by this call
	 */
	public boolean trim() {
		if(size() > MAX_SIZE){
			while(size() > MAX_SIZE){
				pollLastEntry();
			}
			return true;
		}else{
			return false;
		}
	}

	public String toString(){
		String ret = "AddressTable [\n";
		Set<Entry<byte[], Address>> order = this.entrySet();
		for(Entry<byte[], Address> entry : order){
			try {
				ret += entry.getValue().overlayAddressToString() + "\n"
						+ "\t" + entry.getValue().l3ToString() + "\n";
			} catch (MissingLayer3Exception e) {
				DBP.printerrorln("One of the addresses in the address table did not have a layer 3 address. "
						+ "That's a no-no, have you been modifying the values after insertion? "
						+ "Cause I tried real hard to ensure you didn't add addresses without a L3.");
				DBP.printException(e);
			}
		}
		return ret + "size()=" + size() + ", isEmpty()=" + isEmpty()
				+ ", comparator()=" + comparator() + "]";
	}
	
	
}
