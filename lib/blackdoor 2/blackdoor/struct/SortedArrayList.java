package blackdoor.struct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A constantly sorted ArrayList. Elements are added in place and indexOf uses a
 * binary search. addSorted is used in place of add in order to keep contract
 * with List.
 * 
 * @author nfischer3
 * @version 1.0.0 rev0
 * @param <E>
 */
public class SortedArrayList<E> extends ArrayList<E> {

	/**
	 * adds an element to the list in its place based on natural order. allows duplicates.
	 * 
	 * @param element
	 *            element to be appended to this list
	 * @return true if this collection changed as a result of the call
	 */
	public boolean addSorted(E element) {
		boolean added = false;
		added = super.add(element);
		Comparable<E> cmp = (Comparable<E>) element;
		for (int i = size() - 1; i > 0 && cmp.compareTo(get(i - 1)) < 0; i--)
			Collections.swap(this, i, i - 1);
		return added;
	}
	
	/**
	 * adds an element to the list in its place based on natural order.
	 * 
	 * @param element
	 *            element to be appended to this list
	 * @param allowDuplicates if true will allow multiple of the same item to be added, else returns false.
	 * @return true if this collection changed as a result of the call
	 */
	public boolean addSorted(E element, boolean allowDuplicates) {
		boolean added = false;
		if(!allowDuplicates){
			if(this.contains(element)){
				System.err.println("item is a duplicate");
				return added;
			}}
		added = super.add(element);
		Comparable<E> cmp = (Comparable<E>) element;
		for (int i = size() - 1; i > 0 && cmp.compareTo(get(i - 1)) < 0; i--)
			Collections.swap(this, i, i - 1);
		return added;
	}

	/**
	 * Returns true if this list contains the specified element. 
	 * More formally, returns true if and only if this list contains at least one element e
	 * such that (o==null ? e==null : o.equals(e)).
	 * 
	 * @param o
	 *            - element whose presence in this list is to be tested
	 * @return true if this list contains the specified element
	 */
	@Override
	public boolean contains(Object o) {
		return indexOf(o) >= 0;
	}
	
	/**
	 * Appends all of the elements in the specified collection to the current
	 * collection in place based on natural order. The behavior of this
	 * operation is undefined if the specified collection is modified while the
	 * operation is in progress. (This implies that the behavior of this call is
	 * undefined if the specified collection is this list, and this list is
	 * nonempty.)
	 * 
	 * @param c
	 *            - collection containing elements to be added to this list
	 * @return true if this list changed as a result of the call
	 * @throws NullPointerException
	 *             - if the specified collection is null
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean ret = super.addAll(c);
		Collections.sort((List) this);
		return ret;
	}

	/**
	 * Ultimately the same as addAll(Collection c) Appends all of the elements
	 * in the specified collection to the current collection in place based on
	 * natural order. The behavior of this operation is undefined if the
	 * specified collection is modified while the operation is in progress.
	 * (This implies that the behavior of this call is undefined if the
	 * specified collection is this list, and this list is nonempty.) NOTE:
	 * ultimately the same as addAll(Collection c)
	 * 
	 * @param c
	 *            - collection containing elements to be added to this list
	 * @param index
	 *            - location in the list where items will be added initially.
	 *            NOTE: ultimately useless because list is sorted anyway.
	 * @return true if this list changed as a result of the call
	 * @throws NullPointerException
	 *             - if the specified collection is null
	 */
	public boolean addAll(int index, Collection<? extends E> c) {
		boolean ret = super.addAll(index, c);
		Collections.sort((List) this);
		return ret;
	}

	/**
	 * returns the index where the object can be found
	 * 
	 * @param o
	 *            element to search for
	 * @return the index of the search key, if it is contained in the list;
	 *         otherwise, (-(insertion point) - 1). The insertion point is
	 *         defined as the point at which the key would be inserted into the
	 *         list: the index of the first element greater than the key, or
	 *         list.size() if all elements in the list are less than the
	 *         specified key. Note that this guarantees that the return value
	 *         will be >= 0 if and only if the key is found.
	 * @see java.util.ArrayList#indexOf(java.lang.Object)
	 */
	@Override
	public int indexOf(Object o) {
		return Collections.binarySearch((List) this, o);
	}

	/**
	 * throws an UnsupportedOperationException
	 * 
	 * @throws UnsupportedOperationException
	 *             because adding an element to the back of the list would break
	 *             the natural order of the list and adding in sorted order
	 *             would break contract for List
	 * @deprecated
	 */
	@Override
	public boolean add(E e) throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"not supported for sorting, use addSorted");
	}

	/**
	 * throws an UnsupportedOperationException
	 * 
	 * @throws UnsupportedOperationException
	 *             because adding an element to an arbitrary position might
	 *             break the natural order of the list and adding in a sorted
	 *             position would have the same time complexity as addSorted
	 * @deprecated
	 */
	@Override
	public void add(int index, E element) throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"not supported for sorting, use " + "addSorted");
	}


}
