/**
 * 
 */
package blackdoor.cqbe.storage;

/**
 * 
 * @author Cj Buresch
 * @version v0.0.1 - Nov 5, 2014
 */
public class StorageLogic {

	/**
	 * 
	 */
	public StorageLogic() {
	}

	/**
	 * EXPERIMENTAL -- Loads indexes into memory for easy and fast access.
	 * <p>
	 * Will also be updated if in memory. Might want do decide which way to go,
	 * either memory or getting it via FIO.
	 */
	public void loadIndexes() {
	}

	/**
	 * EXPERIMENTAL -- When done using the storage logic, they are released and
	 * written back to disk.
	 * 
	 * <p>
	 * Updates the indexes if they are not current already, removes indexes from
	 * memory.
	 */
	public void storeIndexes() {
	}

	/**
	 * Finds an item in storage and returns it.
	 * 
	 * <p>
	 * Depending on the kind of item, and if it exists in the storage, it will
	 * be retrieved and returned to the caller.
	 * 
	 * @param Itemtype
	 *            The Type of Item will affect how this method retrieves it. *
	 * @param Name
	 *            The identifier for an item. Will find where it is stored, and
	 *            return the corresponding item.
	 * @return The desired item, if it exists, will be returned to whoever is
	 *         calling it.
	 */
	public void retrieveItem() {
	}

	/**
	 * Puts an item of a specific type into the storage of the node.
	 * 
	 * <p>
	 * Depending on the type of item, it will be handled and stored in the
	 * correct spot.
	 * 
	 * @param Itemtype
	 *            The Type of Item will affect how this method retrieves it.
	 * @param Item
	 *            The Item itself.
	 */
	public void putItem() {
	}

	/**
	 * EXPERIMENTAL
	 * 
	 * <p>
	 * If an item is in this node's storage, it must also be in it's neighbors
	 * storage. Puts an item of a specific type into the storage of the node.
	 * Generate PUT RPCs for the relevant neighbors.
	 * 
	 * @param Itemtype
	 *            The Type of Item will affect how this method retrieves it.
	 * @param Item
	 *            The Item itself.
	 * @return TODO need to decide how we'd like the item returned.
	 */
	public void store_passToNeighbors() {
	}

}
