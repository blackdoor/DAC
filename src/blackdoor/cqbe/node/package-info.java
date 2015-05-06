/**
 * Contains classes for Node and Updater.  
 * <p>
 * Node interacts with Server, AddressTable, and Updater
 * The updater keeps the node's address table current by pinging other
 * nodes contained in the AddressTable, removing inactive nodes, and 
 * re-populating the AddressTable with new neighbors.
 * @author Yuryi Kravtsov <br>
 *         Nathaniel Fischer <br>
 *         Cyril Van Dyke <br>
 *         Cj Buresch <br>
 */
package blackdoor.cqbe.node;