package blackdoor.cqbe.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.naming.OperationNotSupportedException;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.addressing.FileAddress;
import blackdoor.util.DBP;

/**
 * StorageController provides indexing and bucketing of files in the file system. 
 * StorageControllers have domains, controllers should only control files which are in within domain(i.e. the domain is a folder and controlled files are in the folder or one of its subfolders)
 * 
 * Presence of a file in the storage controller does not guarantee its existence in the file system.
 * Presence of a file in the file system does not guarantee its existence in the storage controller.
 * @author nfischer3
 *
 */
public class StorageController implements Map<Address, FileAddress> {
	
	private static final int BUCKETS = 3;
	
	private BucketSchlager buckets;
	private volatile Address lowest;
	private volatile Address highest;
	private Address reference;
	private Path domain;
	private AddressTable addressTable;

	/**
	 * 
	 * @param domain the folder under which files will be tracked. It is invalid to try to add files to this storage controller which are not in domain or a subfolder of domain.
	 * @param reference 
	 */
	public StorageController(Path domain, Address reference) {
		buckets = new BucketSchlager(reference);
		this.reference = reference;
		this.domain = domain;
		highest = reference;
		lowest = reference;
		addressTable = null;
	}

	/**
	 * Creates a storage controller which has it's buckets auto mapped based on the content of table.
	 * @param domain
	 * @param table
	 */
	public StorageController(Path domain, AddressTable table){
		buckets = new BucketSchlager(table.getReferenceAddress());
		this.domain = domain;
		addressTable = table;
	}
	
	/**
	 * remove all entries in the storage controller that represent files that do not exist in the file system.
	 */
	public void garbageCollectReferences(){
		for(FileAddress fa : this.values()){
			if(!fa.getFile().exists()){
				this.remove(fa);
			}
		}
	}
	
	/**
	 * delete all the files in the third bucket from the file system and remove them from the storage controller.
	 * @throws IOException
	 */
	public void deleteThirdBucket() throws IOException{
		try{
			for(Address a : getBucket(3)){
				delete(a);
			}
		}catch(NullPointerException e){
			
		}
	}
	
	/**
	 * USE WITH GREAT CAUTION
	 * remove all files inside the domain of this storage controller that are not tracked by the storage controller.
	 * @throws OperationNotSupportedException 
	 */
	public void garbageCollectFiles() throws OperationNotSupportedException{
		throw new OperationNotSupportedException("not yet implemented");
	}
	
	public Path getDomain(){
		return domain;
	}
	
	public Address getReferenceAddress(){
		return isAutoRemappingEnabled() ? addressTable.getReferenceAddress() : reference;
	}

	public Address getLowest(){
		return isAutoRemappingEnabled() && !addressTable.isEmpty() ? addressTable.firstEntry().getValue() : lowest;
	}

	public Address getHighest(){
		return isAutoRemappingEnabled() && !addressTable.isEmpty() ? addressTable.lastEntry().getValue() : highest;
	}
	
	public synchronized void remap(Address nearest, Address farthest){
		if(isAutoRemappingEnabled())
			throw new AutoAddressingException("Cannot manually remap, auto remapping is enabled.");
		highest = farthest;
		lowest = nearest;
		
	}

	public boolean isAutoRemappingEnabled(){
		return addressTable != null;
	}
	
	@SuppressWarnings("unchecked")
	public NavigableSet<Address> getBucket(int i){
		switch(i){
		case 1:
			return buckets.buckets.headSet(getLowest());
		case 2:
			return buckets.buckets.subSet(getLowest(), getHighest());
		case 3:
			return buckets.buckets.tailSet(getHighest());
		default:
			throw new RuntimeException(i + " is not a valid bucket number");
		}
	}
	
	@Override
	public int size() {
		return buckets.size();
	}

	@Override
	public boolean isEmpty() {
		return buckets.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return buckets.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return buckets.containsValue(value);
	}

	@Override
	public FileAddress get(Object key) {
		return buckets.get(key);
	}

	@Override
	@Deprecated
	public FileAddress put(Address key, FileAddress value) {
		return put(value);
	}
	
	public FileAddress put(FileAddress value){
		if(!value.getFile().toPath().startsWith(domain)){
			throw new IndexOutOfBoundsException(value.getFile() + " is not in the domain of this storage controller (" + domain +")");
		}
		return buckets.put(value);
	}
	
	/**
	 * delete the file associated with key from the file system and remove it from the storage controller.
	 * @param key
	 * @return
	 * @throws IOException 
	 */
	public FileAddress delete(Object key) throws IOException{
		FileAddress fa = buckets.get(key);
		if(fa != null){
			Files.delete(fa.getFile().toPath());
		}
		return this.remove(key);
	}
	
	@Override
	public FileAddress remove(Object key) {
		return buckets.remove(key);
	}

	@Override
	public void putAll(Map<? extends Address, ? extends FileAddress> m) {
		for(FileAddress fa : m.values()){
			if(!fa.getFile().toPath().startsWith(domain)){
				throw new IndexOutOfBoundsException(fa.getFile() + " is not in the domain of this storage controller (" + domain +")");
			}
		}
		buckets.putAll(m);
	}

	@Override
	public void clear() {
		buckets.clear();
	}

	@Override
	public Set<Address> keySet() {
		return buckets.keySet();
	}

	@Override
	public Collection<FileAddress> values() {
		return buckets.values();
	}

	@Override
	public Set<java.util.Map.Entry<Address, FileAddress>> entrySet() {
		return buckets.entrySet();
	}
	

	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StorageController [lowest=" + getLowest()
				+ ", highest=" + getHighest() + ", reference=" + getReferenceAddress()
				+ ", domain=" + domain + ", buckets=" + buckets+"]";
	}




	/**
	 * Combines a concurrent set and map to give the best efficiency in operations and to ensure eventual consistency between the map and set.
	 * 
	 * @author nfischer3
	 *
	 */
	private static class BucketSchlager implements Map<Address, FileAddress>{
		
		public ConcurrentHashMap<Address, FileAddress> items;
		public ConcurrentSkipListSet<Address> buckets;
		private Object writeLock = new Object();
		private Address refrence;
		
		public BucketSchlager(Address refrence){
			items = new ConcurrentHashMap<>();
			this.refrence = refrence;
			buckets = new ConcurrentSkipListSet<Address>(refrence.getComparator());
		}

		@Override
		public int size() {
			return items.size();
		}

		@Override
		public boolean isEmpty() {
			if(items.isEmpty()^buckets.isEmpty()){
				DBP.printerrorln("bucketSchlager maps have different isEmpty definitions");
			}
			return items.isEmpty() && items.isEmpty();
		}

		@Override
		public boolean containsKey(Object key) {
			return items.contains(key);
		}

		@Override
		public boolean containsValue(Object value) {
			return buckets.contains(value);
		}

		@Override
		public FileAddress get(Object key) {
			return items.get(key);
		}

		@Override
		@Deprecated
		public FileAddress put(Address _, FileAddress value) {
			synchronized(writeLock){
				if(items.contains(value)){
					return items.get(value);
				}else{
					items.put(value, value);
					buckets.add(value);
					return null;
				}
			}
		}
		
		/**
		 * this method breaks the map contract. this map does not support overwriting keys. attempting to put a value that already exists will return the existing value, but will not change the mapping.
		 * first remove values before adding new ones with the same key
		 * @param value
		 * @return null if value was added
		 */
		public FileAddress put(FileAddress value){
			return put(null, value);
		}

		@Override
		public FileAddress remove(Object key) {
			synchronized(writeLock){
				buckets.remove(key);
				return items.remove(key);
			}
		}

		@Override
		@Deprecated
		public void putAll(Map<? extends Address, ? extends FileAddress> m) {
			synchronized(writeLock){
				items.putAll(m);
				buckets.addAll(m.values());
			}
		}
		
		public void putAll(Set<? extends FileAddress> s){
			synchronized(writeLock){
				buckets.addAll(s);
				for(FileAddress a : s){
					items.put(a, a);
				}
			}
		}

		@Override
		public void clear() {
			synchronized(writeLock){
				buckets.clear();
				items.clear();
			}
		}

		@Override
		public Set<Address> keySet() {
			return buckets.clone();
		}

		@Override
		public Collection<FileAddress> values() {
			return items.values();
		}

		@Override
		public Set<java.util.Map.Entry<Address, FileAddress>> entrySet() {
			return items.entrySet();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			String ret = "Buckets [\n";
			for(FileAddress fa : items.values()){
				ret += fa+ "\n";
			}
			return ret + "]";
		}
		
		
		
	}

	private static class AutoAddressingException extends RuntimeException{
		public AutoAddressingException(String s){
			super(s);
		}

	}

}
