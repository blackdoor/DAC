package blackdoor.cqbe.addressing;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.json.JSONArray;
import org.json.JSONException;

import blackdoor.util.DBP;


/**
 * An address table for holding mappings from overlay addresses to layer 3 addresses.
 * AddressTable is threadsafe and concurrent, but not consistent.
 * @author Nathan Fischer
 * @version v1.0.0 - Nov 19, 2014
 */
public class AddressTable extends ConcurrentSkipListMap<byte[], L3Address> implements Serializable, Iterable<L3Address> {
	public static final int DEFAULT_MAX_SIZE = 256;
	
	private int maxSize = DEFAULT_MAX_SIZE;

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
		return new Address(c.getReferenceAddress());
	}
	
	public void setMaxSize(int s){
		this.maxSize = s;
	}
	
	public int getMaxSize(){
		return maxSize;
	}
	
	public L3Address first(){
		return this.firstEntry().getValue();
	}
	
	public L3Address last(){
		return this.lastEntry().getValue();
	}
	
	/**
	 * Returns an AddressTable built from a JSONArray. Elements of the JSON array should be in a format parsable by L3Address
	 * @param arr
	 * @return An address table with the values in arr.
	 * @throws JSONException
	 */
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
	
	/**
	 * Returns this address table as a JSONArray
	 * @return this address table as a JSONArray
	 */
	public JSONArray toJSONArray(){
		JSONArray ret = new JSONArray();
		
		for(L3Address entry : this.values()){
			ret.put(entry.toJSON());
		}
		
		return ret;
	}
	
	/**
	 * Returns this address table as a json array string
	 * @return this address table as a json array string
	 */
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
			if(size() >= maxSize){
				return pollLastEntry().getValue();
			}
		}
		return absent;
	}
	
	public L3Address get(Address address){
		return super.get(address.getOverlayAddress());//TODO change to get shallow overlay address
	}
	
	public L3Address putIfAbsent(byte[] _, L3Address value){
		if (!contains(value))
		       return put(value);
		   else
		       return get(value);
	}
	
	public L3Address remove(Address value){
		return super.remove(value.getOverlayAddress());
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
	
	public boolean contains(Address a){
		return super.containsKey(a.getShallowOverlayAddress());
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
		if(size() > maxSize){
			while(size() > maxSize){
				pollLastEntry();
			}
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public Iterator<L3Address> iterator() {
		return values().iterator();
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
		//Set<Entry<byte[], L3Address>> order = this.entrySet();
		for(L3Address entry : this){
			//try {
				ret += '\t' + entry.toString() + "\n";
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
