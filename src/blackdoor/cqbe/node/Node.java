package blackdoor.cqbe.node;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressException;
import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.addressing.Address.OverlayComparator;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.node.NodeException.RequiredParametersNotSetException;
import blackdoor.cqbe.node.server.Server;
import blackdoor.util.DBP;

/**
 * 
 * @author nfischer3
 *
 */
public class Node {

	private static Node singleton;

	private Server server;
	private AddressTable addressTable;
	private volatile int n;
	private volatile int o;

	protected Node(int port) {
		startServer(port);
	}

	private void startServer(int port) {
		server = new Server(port);
		new Thread(server).start();
	}

	/**
	 * Closes the node and returns a list of folders containing node data
	 * 
	 * @return - A list of strings containing the folder locations of the node
	 *         storage, address table, and updater
	 */
	public String[] destroyNode() {
		return null;
	}

	/**
	 * Prints a brief status of node
	 */
	public void statusCheck() {
	}

	/**
	 * Lists the current status of the node's storage including space used, etc.
	 */
	public void checkStorage() {

	}

	private static synchronized void checkAndThrow() {
		if (singleton == null) {
			throw new ExceptionInInitializerError(
					"Node singleton is null. Node has not been built yet.");
		}
	}

	/**
	 * Returns the address table of the node
	 * 
	 * @return Address Table of node
	 */
	public static AddressTable getAddressTable() {
		return getInstance().addressTable;
	}

	protected static Node getInstance() {
		checkAndThrow();
		return singleton;
	}

	public static int getN() {
		return getInstance().n;
	}

	public static Address getOverlayAddress() {
		OverlayComparator c = (OverlayComparator) getInstance().addressTable
				.comparator();
		try {
			return new Address(c.getReferenceAddress());
		} catch (AddressException e) {
			DBP.printerrorln("The address in the OverlayComparator is not valid for building a new Address object");
			DBP.printerrorln("THIS IS BAAAAADDDD GO TO GITHUB AND OPEN AN ISSUE NOOWWW!!!");
			DBP.printException(e);
		}
		return null;
	}

	public static class NodeBuilder {

		private int port;
		private String dir;
		private boolean daemon;
		private boolean adam;
		private L3Address bootstrapNode;

		/**
		 * Create a new NodeBuilder with no preset settings
		 */
		public NodeBuilder() {
			daemon = false;
			adam = true;
			port = -1;
		}

		/**
		 * Sets the port of the node to be built, passed in from DART.Join
		 * 
		 * @param port
		 *            - The port given to be set
		 */
		public void setPort(int port) {
			this.port = port;
		}

		/**
		 * Sets the directory where the node should be spawned and operated from
		 * Passed from DART.Join
		 * 
		 * @param dir
		 *            - Directory to be used by created node
		 */
		public void setDirectory(String dir) {
			this.dir = dir;
		}

		/**
		 * Sets whether the node spawned will be a Daemon or not
		 * 
		 * @param status
		 *            - If yes, daemon
		 */
		public void setDaemon(Boolean status) {
			daemon = status;
		}

		/**
		 * Sets whether or not the node spawned will be the first in the network
		 * 
		 * @param status
		 *            - If yes, Adam node.
		 */
		public void setAdam(Boolean status) {
			adam = status;
		}

		public void setBootstrapNode(L3Address bootstrapNode) {
			this.bootstrapNode = bootstrapNode;
		}

		/**
		 * Builds a node based on the current list of settings attributed to it.
		 */
		public void buildNode() throws RequiredParametersNotSetException {
			if (!daemon) {
				if (adam) {
					AddressTable builderAddressTable = new AddressTable();
					Node node = new Node(port);
					node.addressTable = builderAddressTable;
				} else if (!adam && bootstrapNode != null) {
					AddressTable builderAddressTable = new AddressTable();
					builderAddressTable.add(bootstrapNode);
					Node node = new Node(port);
					node.addressTable = builderAddressTable;
				} else
					throw new RequiredParametersNotSetException();
			}
		}

	}

}