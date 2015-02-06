package blackdoor.cqbe.addressing;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.naming.OperationNotSupportedException;

import org.json.JSONArray;
import org.json.JSONException;

import blackdoor.cqbe.addressing.AddressException.MissingLayer3Exception;
import blackdoor.util.DBP;


/**
 * An address table for holding mappings from overlay addresses to layer 3 addresses.
 * AddressTable is threadsafe and concurrent, but not consistent.
 * @author Nathan Fischer
 * @version v1.0.0 - Nov 19, 2014
 */
public class AddressTable extends ConcurrentSkipListMap<byte[], L3Address> implements Serializable {
	public static final int MAX_SIZE = 256;

	/**
	 * Constructs an AddressTable which will sort entries based on their distance to Address.nullOverlay
	 */
	public AddressTable() {
		this(Address.getNullAddress());
	}

	/**
	 * Constructs an AddressTable which will sort entries based on their distance to reference.
	 * @param reference the address by which elements in this AddressTable will be sorted
	 */
	public AddressTable(Address reference){
		super(new Address.OverlayComparator(reference.getOverlayAddress()));
	}

	public Address getReferenceAddress(){
		Address.OverlayComparator c = (Address.OverlayComparator) comparator();
		try {
			return new Address(c.getReferenceAddress());
		} catch (AddressException e) {
			e.printStackTrace();
		}
		throw new RuntimeException();
	}
	
	public static AddressTable fromJSONArray(JSONArray arr) throws JSONException{
		AddressTable ret = new AddressTable();
		for(int i = 0; i < arr.length(); i++){
			try{
				ret.add(L3Address.fromJSON(arr.getJSONObject(i)));
			}catch (UnknownHostException e){
				DBP.printerrorln("Bad address in " + arr.getJSONObject(i));
				DBP.printException(e);
			}
		}
		return ret;
	}
	
	public JSONArray toJSONArray(){
		JSONArray ret = new JSONArray();
		
		for(L3Address entry : this.values()){
			ret.put(entry.toJSON());
		}
		
		return ret;
	}
	
	public String toJSONString(){
		return toJSONArray().toString();
	}
	
	/**
	 * Adds the specified value to this table, they key is implied by the overlay address of the value. 
	 * If the table previously contained this address, the old address is replaced.
	 * If the table did not previously contain the address and the table is full, then farthest entry is removed from the table.
	 * @param value L3Address to be associated with the specified overlay address
	 * @return the previous value associated with the specified key, or null if there was no mapping for the key, or the value associated with the farthest entry which was removed as a result of this put
	 */
	public L3Address add(L3Address value){
		L3Address absent = putIfAbsent(value.getOverlayAddress(), value);
		if(absent == null){
			absent = put(null, value);
			if(size() >= MAX_SIZE){
				return pollLastEntry().getValue();
			}
		}
		return absent;
	}

	/**
	 * Add all addresses in c to this AddressTable.
	 * @param c a set of L3Addresses
	 * @return true if the address table was changed by this operation
	 */
	public boolean addAll(Set<L3Address> c){
		if(c.size() == 0)
			return false;
		boolean changed = false;
		for(L3Address a : c){
			L3Address change = add(a);
			changed |= (change == null || !change.equals(a));
		}
		return changed;
	}
	
	/**
	 * Add all addresses in c to this AddressTable.
	 * @param c a collection of L3Addresses
	 * @return true if C IS A SET (not a Set type, but must not have duplicates) and the address table was changed by this operation
	 */
	public boolean addAll(Collection<L3Address> c){
		if(c.size() == 0)
			return false;
		boolean changed = false;
		for(L3Address a : c){
			L3Address change = add(a);
			changed |= (change == null || !change.equals(a));
		}
		return changed;
	}
	
	/**
	 * Associates the specified L3Address with the overlay address of the specified L3Address in this map (optional operation).
	 * If the map previously contained a mapping for the L3Address, the old value is replaced by the specified value.
	 * (A map m is said to contain a mapping for a key k if and only if m.containsKey(k) would return true.)
	 * In general add(L3Address) should 		super be used instead. This method can be dangerous as it can overfill the table.
	 * @param value value to be added to the map
	 * @return the previous L3Address object at that overlay address, or null if there was L3Address with that overlay.
	 */
	public L3Address put(L3Address value){
		return put(null, value);
	}

	/**
	 * Same as the put contract for Map, except the key is implied by the value. In other words only the value parameter has any effect.
	 * @param _ does nothing
	 * @param value
	 * @return
	 */
	@Deprecated
	public L3Address put(byte[] _, L3Address value){
		if(L3Address.isNonNodeAddress(value) || value.equals(getReferenceAddress()))
			return null;
		return super.put(value.getOverlayAddress(), value);
	}

	public boolean containsValue(L3Address value){
		return containsKey(value.getOverlayAddress());
	}
	
	@Deprecated
	public void putAll(Map m){
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Obtains an L3Address table containing the k nearest addresses to reference
	 * @param k the number of nearby addresses to retrieve; less than k addresses may be returned, but not more
	 * @param reference 
	 * @return a new AddressTable (sorted with respect to reference) containing the k nearest addresses to reference
	 */
	public AddressTable getNearestAddresses(int k, Address reference){
		int i;
		AddressTable ret = new AddressTable(reference);
		NavigableSet<L3Address> sortedAddresses = new ConcurrentSkipListSet<L3Address>(new Address.AddressComparator(reference));
		sortedAddresses.addAll(this.values());
		i = 0;
		for(L3Address a : sortedAddresses){
			ret.add(a);
			if(i++ > k)
				break;
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
	
	/**
	 * Probably complexity O(n) to build this set (depending on JVM implementation), but returns the same items as values() (which is constant time to build). 
	 * This set is not backed by the table so changes to the set will not be reflected in the table.
	 * This set is thread safe.
	 * @return
	 */
	public SortedSet<L3Address> valueSet(){
		return new ConcurrentSkipListSet<L3Address>(values());
	}
	
	/**
	 * O(n) to build. Set implementation is a LinkedHashSet.
	 * Only worth using if you plan to iterate through more than once or using contains often, else use values().
	 * @return
	 */
	public Set<L3Address> UnsafeValueSet(){
		return new LinkedHashSet<L3Address>(values());
	}
	
	public AddressTable clone(){
		AddressTable clone = (AddressTable) super.clone();
		return clone;
	}

	public String toString(){
		String ret = "AddressTable [\n";
		Set<Entry<byte[], L3Address>> order = this.entrySet();
		for(Entry<byte[], L3Address> entry : order){
			//try {
				ret += entry.getValue().overlayAddressToString() + "\n"
						+ "\t" + entry.getValue().l3ToString() + "\n";
			/*}
			catch (MissingLayer3Exception e) {
				DBP.printerrorln("One of the addresses in the address table did not have a layer 3 address. "
						+ "That's a no-no, have you been modifying the values after insertion? "
						+ "Cause I tried real hard to ensure you didn't add addresses without a L3.");
				DBP.printException(e);
			}*/
		}
		return ret + "size()=" + size() + ", isEmpty()=" + isEmpty()
				+ ", comparator()=" + comparator() + "]";
	}
	
	
}
