package blackdoor.cqbe.node;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressException;
import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.addressing.Address.OverlayComparator;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.node.server.Server;
import blackdoor.cqbe.node.server.ServerException;

import blackdoor.cqbe.settings.Config;
import blackdoor.cqbe.storage.StorageController;
import blackdoor.util.DBP;
import blackdoor.cqbe.node.NodeException.*;

import java.net.*;
import java.io.*;

/**
 * 
 * @author nfischer3
 *
 */
public class Node {

	private static Node singleton;
	private Server server;
	private Updater updater;
	private AddressTable addressTable;
	private StorageController storageController;
	private volatile int n = Address.ADDRESS_SIZE;
	private volatile int o;

	private volatile L3Address me;
	private Thread serverThread;
	private Thread updaterThread;
	
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

	public static L3Address getAddress() {
		return getInstance().me;
	}

	public static StorageController getStorageController() {
		return getInstance().storageController;
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

		return new Address(c.getReferenceAddress());

	}

	protected Node() {
	}

	private void startServer(int port) throws ServerException {
		server = new Server(port);
		serverThread = new Thread(server);
		serverThread.start();
	}
	
	private void startUpdater() {
		updater = new Updater();
		updaterThread = new Thread(updater);
		updaterThread.start();
	}

	/**
	 * Configure Address Table based on ip and port
	 * 
	 * @param port
	 *            the port to use
	 * @param local
	 *            is whether or not you want to use local ip or WAN ip
	 */
	private void configureAddressing(int port)
			throws NodeException {
		InetAddress address;
		try {

			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					whatismyip.openStream()));
			address = InetAddress.getByName(in.readLine());

			me = new L3Address(address, port);
			addressTable = new AddressTable(me);
		} catch (Exception e) {
			throw new CantGetAddress();
		}
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
	
	public static void shutdown(){
		Node inst = getInstance();
		inst.server.stop();
		inst.updater.stop();
		throw new UnsupportedOperationException("not yet implemented");
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

	public static class NodeBuilder {

		private int port;
		private String storageDir;
		private boolean daemon;
		private boolean adam;
		private L3Address bootstrapNode;
		private Config config;

		/**
		 * Create a new NodeBuilder with no preset settings
		 */
		public NodeBuilder() {
			loadDefaultSettings();
			daemon = false;
			adam = false;
		}

		/**
		 * Sets the port of the node to be built, passed in from DART.Join
		 * 
		 * @param port
		 *            - The port given to be set
		 */
		public void setPort(int port) {
			this.port = port;
			config.put("port", port);
		}

		/**
		 * Sets the directory where the node should be spawned and operated from
		 * Passed from DART.Join
		 * 
		 * @param directory
		 *            - Directory to be used by created node
		 */
		public void setStorageDir(String storageDir) {
			this.storageDir = storageDir;
			config.put("storageDir", storageDir);
		}

		/**
		 * Sets the settings file directory Passed from DART.Join
		 * 
		 * @param directory
		 *            - Directory to be used by created node
		 */
		public void setSettings(String settingsDir) {
			loadSettings("settingsDir");

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
		 * TODO add and start updater
		 * @throws ServerException 
		 * 
		 * @throws Exception
		 */
		public Node buildNode() throws NodeException, ServerException {
			config.saveSessionToFile();
			if (daemon) {
				// TODO start a demon prossess depending on platform
			}
			Node node = new Node();
			node.configureAddressing(port);
			node.storageController = new StorageController(new File(
					this.storageDir).toPath(), node.addressTable);
			if (!adam && bootstrapNode != null) {
				node.addressTable.add(bootstrapNode);
			}
			Node.singleton = node;
			node.startServer(port);
			node.startUpdater();
			return Node.getInstance();
		}

		private void loadDefaultSettings() {
			config = new Config(new File("dflt.txt"));
			this.setPort((int) config.get("port"));
			this.setStorageDir((String) config.get("storageDir"));
		}

		private void loadSettings(String dir) {
			config.loadSettings(new File(dir));
			if (config.containsKey("port")) {
				this.setPort((int) config.get("port"));
			}
			if (config.containsKey("storageDir")) {
				this.setStorageDir((String) config.get("storageDir"));
			}
		}

	}

}
