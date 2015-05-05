package blackdoor.cqbe.node;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.addressing.Address.OverlayComparator;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.node.server.Server;
import blackdoor.cqbe.node.server.ServerException;
import blackdoor.cqbe.settings.Config;
import blackdoor.cqbe.storage.StorageController;
import blackdoor.util.DBP;
import blackdoor.cqbe.node.NodeException.*;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

/**
 * The Node Singleton. Acts as a central hub for each independent part of the
 * node.
 * <p>
 * 
 * @author Yuryi Kravtsov
 * @version v1.0.0 - May 4, 2015
 */
public enum Node {
	INSTANCE;

	private static Node singleton;
	private Server server;
	private Updater updater;
	private Config config;
	private AddressTable addressTable;
	private StorageController storageController;
	private volatile int address_size = Address.DEFAULT_ADDRESS_SIZE;
	private volatile int o;

	private volatile L3Address me;
	private Thread serverThread;
	private Thread updaterThread;

	/**
	 * Checks to see if the node singleton has been created yet. Throws a
	 * ExceptionInInitializerError if the node does not exist yet.
	 * <p>
	 * 
	 * @throws ExceptionInInitializerError
	 */
	private static synchronized void checkAndThrow() {
		if (singleton == null) {
			throw new ExceptionInInitializerError(
					"Node singleton is null. Node has not been built yet.");
		}
	}

	/**
	 * Returns the address table of this instance of the node.
	 * <p>
	 * 
	 * @return Address Table of node
	 */
	public static AddressTable getAddressTable() {
		return getInstance().addressTable;
	}

	/**
	 * Returns this instance's L3Address.
	 * <p>
	 * 
	 * @return adrress
	 */
	public static L3Address getAddress() {
		return getInstance().me;
	}

	/**
	 * Get the configurations for this instance.
	 * <p>
	 * 
	 * @return config
	 */
	public static Config getConfig() {
		return getInstance().config;
	}

	/**
	 * Get this node's storagecontroller.
	 * 
	 * @return storagecontroller
	 */
	public static StorageController getStorageController() {
		return getInstance().storageController;
	}

	/**
	 * Get a reference to this node.
	 * <p>
	 * Also performs a check to make sure that the node has been initialized. If
	 * one has not been created a ExceptionInInitializerError will be thrown and
	 * things will likely not end well for everyone involved.
	 * 
	 * @return
	 */
	public static Node getInstance() {
		checkAndThrow();
		return singleton;
	}

	/**
	 * Returns the address size that this node is using.
	 * <p>
	 * 
	 * @return address size
	 */
	public static int getN() {
		return getInstance().address_size;
	}

	/**
	 * Returns this node's overlay address.
	 * <p>
	 * 
	 * @return overlay address
	 */
	public static Address getOverlayAddress() {
		OverlayComparator c = (OverlayComparator) getInstance().addressTable
				.comparator();

		return new Address(c.getReferenceAddress());

	}

	/**
	 * Start the node's server on the secific port on a new thread.
	 * <p>
	 * 
	 * @param port
	 * @throws ServerException
	 */
	private void startServer(int port) throws ServerException {
		server = new Server(port);
		serverThread = new Thread(server);
		serverThread.start();
	}

	/**
	 * Start the Node's updater on a new thread.
	 * <p>
	 */
	private void startUpdater() {
		updater = new Updater();
		updaterThread = new Thread(updater);
		updaterThread.start();
	}

	/**
	 * Configure Address Table based on ip and port
	 * <p>
	 * 
	 * @param port
	 *            the port to use
	 * @param local
	 *            is whether or not you want to use local ip or WAN ip
	 */
	private void configureAddressing(int port) throws NodeException {
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
	 * <p>
	 * Not really being used at this point. No real plans for it as of yet.
	 * <p>
	 * 
	 * @return - A list of strings containing the folder locations of the node
	 *         storage, address table, and updater
	 */
	@Deprecated
	public String[] destroyNode() {
		return null;
	}

	/**
	 * Shuts down each funtion happening inside the node so that it effectively
	 * leaves the network.
	 * <p>
	 * kill -9 (pid) should also work.
	 * <p>
	 */
	public static void shutdown() {
		Node inst = getInstance();
		inst.updater.stop();
		inst.server.stop();
	}

	/**
	 * Static class used to handle the creation of nodes with certain
	 * configurations. The configurations can be specifed by the commmand line
	 * tool or by using a configuration file.
	 * <p>
	 * 
	 * @author Yuryi Kravtsov
	 * @version v1.0.0 - May 4, 2015
	 */
	public static class NodeBuilder {

		private int port;
		private String storageDir;
		private String logDir;
		private boolean daemon;
		private boolean adam;
		private L3Address bootstrapNode;
		private Config config;

		/**
		 * Create a new NodeBuilder with default settings.
		 * <p>
		 */
		public NodeBuilder() {
			config = new Config(new File("default.config"));
			this.setPort((int) config.get("port"));
			this.setStorageDir((String) config.get("storage_directory"));
			this.setLogDir((String) config.get("log_directory"));
			daemon = false;
			adam = false;
		}

		/**
		 * Sets the port of the node to be built, passed in from DART.Join
		 * <p>
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
		 * Passed from dh256.Join()
		 * <p>
		 * 
		 * @param directory
		 *            - Directory to be used by created node
		 */
		public void setStorageDir(String storageDir) {
			this.storageDir = storageDir;
			config.put("storage_directory", storageDir);
		}

		/**
		 * Sets the settings file Passed from dh256.Join
		 * <p>
		 * 
		 * @param directory
		 *            - Directory to be used by created node
		 */
		public void setSettings(String filename) {
			config.loadSettings(new File(filename));

			this.setPort((int) config.get("port"));
			this.setStorageDir((String) config.get("storage_directory"));
			this.setLogDir((String) config.get("log_directory"));
		}

		/**
		 * Sets whether the node spawned will be a Daemon or not
		 * <p>
		 * 
		 * @param status
		 *            - If yes, daemon
		 */
		public void setDaemon(Boolean status) {
			daemon = status;
		}

		/**
		 * Sets whether or not the node spawned will be the first in the network
		 * <p>
		 * 
		 * @param status
		 *            - If yes, Adam node.
		 */
		public void setAdam(Boolean status) {
			adam = status;
		}

		/**
		 * Joins the network using a bootstrap address.
		 * 
		 * @param bootstrapNode
		 */
		public void setBootstrapNode(L3Address bootstrapNode) {
			this.bootstrapNode = bootstrapNode;
		}

		/**
		 * Overrides the default log directory.
		 * <p>
		 * 
		 * @param logDir
		 */
		public void setLogDir(String logDir) {
			this.logDir = logDir;
			config.put("log_directory", logDir);
		}

		/**
		 * Builds a node based on the current list of settings attributed to it.
		 * <p>
		 * 
		 * @throws ServerException
		 * @throws SingletonAlreadyInitializedException
		 * @throws IOException
		 * 
		 * @throws Exception
		 */
		public Node buildNode() throws NodeException, ServerException,
				IOException {
			config.saveSessionToFile();
			if (daemon) {
				config.put("save_file", "dmSettings.txt");
				config.saveSessionToFile();
				RuntimeMXBean runtimeMxBean = ManagementFactory
						.getRuntimeMXBean();
				List<String> commands = new ArrayList<>();
				commands.addAll(runtimeMxBean.getInputArguments());
				commands.add("java");
				commands.add("-jar");
				commands.add("dh256.jar");
				commands.add("join");
				if (!adam && bootstrapNode != null) {
					commands.add(bootstrapNode.l3ToString());
				} else
					commands.add("-a");
				commands.add("-s");
				commands.add("dmSettings.txt");
				ProcessBuilder pb = new ProcessBuilder(commands);
				pb.start();
				return null;
			}
			if (!logDir.equals(""))
				DBP.setLogFileLocation(logDir);
			Node.singleton = Node.INSTANCE;// node = new Node();
			Node node = Node.INSTANCE;
			node.configureAddressing(port);
			node.storageController = new StorageController(new File(
					this.storageDir).toPath(), node.addressTable);
			if (!adam && bootstrapNode != null) {
				node.addressTable.add(bootstrapNode);
			}
			node.config = config;
			Node.singleton = node;
			node.startServer(port);
			node.startUpdater();

			/*
			 * Channel heartbeat = new Channel("heartbeat", new
			 * PrintStream("log" + File.separator +
			 * Misc.getHexBytes(Node.getAddress().getOverlayAddress(), "_") +
			 * ".heartbeat")); heartbeat.disable();
			 * //heartbeat.printAsJson(true); heartbeat.setNeverLog(true);
			 * DBP.addChannel(heartbeat);
			 */
			return Node.getInstance();
		}

	}

}
