/**
 * 
 */
/**
 * @author Cyril Van Dyke
 * Contains classes for Node and Updater.  
 * Node interacts with Server, AddressTable, and Updater
 * The updater keeps the node's address table current by pinging other
 * nodes contained in the AddressTable, removing inactive nodes, and 
 * re-populating the AddressTable with new neighbors.
 */
package blackdoor.cqbe.node;