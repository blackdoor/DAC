/**
 *
 */
package blackdoor.cqbe.storage;

/**
 * @author Cj Buresch
 * @version 0.0.1
 *
 */
public class Storageio {

	public Storageio() {
	}

	/**
	 * Returns the index of certain itemtype.
	 * 
	 * <p>
	 * Each type might have a different index for the sake of organization. This
	 * will return the indexes for that type to wherever it is called.
	 * 
	 * @param Itemtype
	 *            The item type.
	 * @return TODO the index, in a certain format that needs to be decided.
	 */
	public void getIndex() {
	}

	/**
	 * Insert a new item into storage.
	 * 
	 * <p>
	 * Might need to also update the index if not already? Unless that is
	 * handled somewhere else.
	 * 
	 * @param Itemtype
	 *            The type of item.
	 * @param Item
	 *            The Item in a certain format.
	 */
	public void storeNewItem() {
	}

	/**
	 * Deletes an item from storage.
	 * 
	 * <p>
	 * Might need to also update the index if not already? Unless that is
	 * handled somewhere else.
	 * 
	 * @param Itemtype
	 *            The type of item.
	 * @param Name
	 *            The Item identifier.
	 */
	public void deleteItem() {
	}

	/**
	 * Returns a boolean that describes if an item is in storage or not.
	 * 
	 * <p>
	 * Might just check the index?? could be redundant from above.
	 * 
	 * @param Itemtype
	 *            The type of item.
	 * @param Name
	 *            The Item identifier.
	 * @return Boolean
	 */
	public void constainsItem() {
	}

	/**
	 * Returns an item if it exists in storage.
	 * 
	 * <p>
	 * 
	 * 
	 * @param Itemtype
	 *            The type of item.
	 * @param Name
	 *            The Item identifier.
	 * @return The item. should be formated in a certain way?
	 */
	public void retrieveItem() {
	}

}
