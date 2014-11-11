package blackdoor.struct;

import java.util.ArrayList;

//import blackdoor.util.Verbose;

/**
 * An object that maps String keys to multiple different generic values. A value
 * can be any type, class or primitive. A GenericMap need not all contain values
 * of the same type, and indeed should not. A map may only contain one of each key.
 * This class is pretty useless. Just use a Map<String, Object> and forget you ever saw this.
 * @author nfischer3
 * 
 */
public class GenericMap {
	//private Verbose verbose;
	private ArrayList<GenericEntry> values;
	private SortedArrayList<String> keys;
	public int size;
	public boolean isEmpty;

	public GenericMap() {
		values = new ArrayList<GenericEntry>();
		keys = new SortedArrayList<String>();
		size = 0;
		isEmpty = true;
		//verbose = new Verbose(true, true);
	}

	/**
	 * Removes all of the mappings from this map. The map will be empty after
	 * this call returns.
	 */
	public void clear() {
		values.clear();
		keys.clear();
		size = 0;
		isEmpty = true;
	}

	/**
	 * Returns true if this map contains a mapping for the specified key. More
	 * formally, returns true if and only if this map contains a mapping for a
	 * key k such that (key==null ? k==null : key.equals(k)). (There can be at
	 * most one such mapping.)
	 * 
	 * @param key
	 *            key whose presence in this map is to be tested
	 * @return true if this map contains a mapping for the specified key
	 */
	public boolean containsKey(String key) {
		return keys.contains(key);
	}

	/**
	 * Returns true if this map maps one or more keys to the specified value.
	 * More formally, returns true if and only if this map contains at least one
	 * mapping to a value v such that (value==null ? v==null : value.equals(v)).
	 * This operation will probably require time linear in the map size for most
	 * implementations of the Map interface.
	 * 
	 * @param value
	 * @return true if this map maps one or more keys to the specified value.
	 */
	public boolean containsValue(Object value) {
		for (GenericEntry e : values) {
			if (e.equals(value))
				return true;
		}
		return false;
	}

	private Object grab(String key) {
		return values.get(keys.indexOf(key)).value;
	}

	/**
	 * Returns the value to which the specified key is mapped, or null if this
	 * map contains no mapping for the key. More formally, if this map contains
	 * a mapping from a key k to a value v such that (key==null ? k==null :
	 * key.equals(k)), then this method returns v; otherwise it returns null.
	 * (There can be at most one such mapping.)
	 * 
	 * @param key
	 * @param type
	 * @return the value to which the specified key is mapped, or null if this
	 * map contains no mapping for the key
	 */
	public <T> T get(String key, Class<T> type) {
		return type.cast(grab(key));
	}

	/**
	 * Returns the value to which the specified key is mapped, or null if this
	 * map contains no mapping for the key. More formally, if this map contains
	 * a mapping from a key k to a value v such that (key==null ? k==null :
	 * key.equals(k)), then this method returns v; otherwise it returns null.
	 * (There can be at most one such mapping.)
	 * 
	 * @param key
	 * @return the value to which the specified key is mapped, or null if this
	 * map contains no mapping for the key
	 */
	public Object get(String key) {
		return grab(key);
	}
	/**
	 * Associates the specified value with the specified key in this map. If the
	 * map previously contained a mapping for the key, the old value is replaced
	 * by the specified value. (A map m is said to contain a mapping for a key k
	 * if and only if m.containsKey(k) would return true.)
	 * 
	 * @param key
	 * @param value
	 * @return the value previously associated with this key, null if key did not exist before
	 */
	public <T> Object put(String key, T value) {
		Object last = null;
		int index = keys.indexOf(key);
		if (index >= 0) {
			last = grab(key);
			values.remove(index);
			values.add(index, new GenericEntry<T>(value));
		} else {
			keys.addSorted(key);
			index = keys.indexOf(key);
			values.add(index, new GenericEntry<T>(value));
		}
		isEmpty = false;
		size++;
		return last;
	}

	/**
	 * Removes the mapping for a key from this map if it is present More
	 * formally, if this map contains a mapping from key k to value v such that
	 * (key==null ? k==null : key.equals(k)), that mapping is removed. (The map
	 * can contain at most one such mapping.)
	 * 
	 * @param key
	 * @return the object that is being removed
	 */
	public Object remove(String key) {
		Object last = null;
		int index = keys.indexOf(key);
		if (keys.remove(key)) {
			last = values.remove(index).value;
			size--;
			if (size < 1)
				isEmpty = true;
		}

		return last;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null) {
			//verbose.println("comparing object null");
			return false;
		}
		if (getClass() != obj.getClass()) {
			//verbose.println("not same class");
			return false;
		}
		GenericMap other = (GenericMap) obj;
		if (other.size != size) {
			//verbose.println("different sizes");
			return false;
		}
		if (keys == null) {
			if (other.keys != null) {
				//verbose.println("null difference in keys");
				return false;
			}
		} else if (!keys.equals(other.keys)) {
			//verbose.println("keys unequal");
			return false;
		}
		if (values == null) {
			if (other.values != null) {
			//	verbose.println("null difference in values");
				return false;
			}
		} else {
			int index = 0;
			for (String key : keys) {
				Class type = values.get(index).value.getClass();
				if (!get(key, type).equals(other.get(key, type))) {
					//verbose.println(get(key, type) + " not equal to "
					//		+ other.get(key, type));
					return false;
				}
				index++;
			}
		}
		return true;
	}

	public String toString() {
		String result = "";
		for (String key : keys) {
			result = result + key + ": " + grab(key) + "\n";
		}
		return result;
	}

	class GenericEntry<T> {
		public T value;

		public GenericEntry(T value) {
			this.value = value;
		}

		public String toString() {
			return value.toString();
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			return value.equals(obj);
		}

		public T getValue() {
			return value;
		}
	}
}
