package blackdoor.cqbe.node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.addressing.CASFileAddress;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.output_logic.Router;
import blackdoor.cqbe.rpc.RPCException;
import blackdoor.cqbe.rpc.RPCException.JSONRPCError;
import blackdoor.cqbe.settings.Config;
import blackdoor.net.SocketIOWrapper;
import blackdoor.util.DBP;

/**
 * Performs actions on an interval to keep the node alive in the network and to
 * keep the contents of a nodes addresstable and storage viable.
 * <p>
 * 
 * @author Nathaniel Fischer <br>
 *         Cyril Van Dyke <br>
 * @version v1.0.0 - May 4, 2015
 */
public class Updater implements Runnable {

	/**
	 * in seconds
	 */
	public static final long updateInterval = (int) Config.getReadOnly(
			"node_update_interval", "default.config");
	public static int PARALLELISM = (int) Config.getReadOnly(
			"node_update_parallelism", "default.config");
	private Thread updaterThread;
	private volatile boolean running;
	private Map<L3Address, Integer> strikeList;

	public Updater() {
		running = true;
		strikeList = new ConcurrentHashMap<L3Address, Integer>();
	}

	/**
	 * The Update Loop. Runs an infinite loop with that alternates between
	 * sleeping the thread and performing a interrupt to trigger the update.
	 * <p>
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		if (updaterThread == null) {
			updaterThread = Thread.currentThread();
		}

		while (running) {
			try {
				schedule();
			} catch (InterruptedException e) {
				DBP.printwarningln("Updater thread interrupted!");
			}
		}
		DBP.printwarningln("Updater is stopping!");
	}

	/**
	 * Sleeps the thread for the defined interval (in seconds).
	 * <p>
	 * 
	 * @throws InterruptedException
	 */
	protected void schedule() throws InterruptedException {
		Thread.sleep(updateInterval * 1000);
		update();
	}

	/**
	 * Stops the update loop from continuing.
	 * <p>
	 */
	public void stop() {
		running = false;
		updaterThread.interrupt();
	}

	/**
	 * Add a strike to a node at an address because it is unreachable at this
	 * time. If the address has three strikes, it is out of the table.
	 * <p>
	 * 
	 * @param addr
	 */
	protected void strike(L3Address addr) {
		// DBP.printdemoln("striking " + addr);
		if (strikeList.containsKey(addr)) {
			strikeList.put(addr, strikeList.get(addr) + 1);
			if (strikeList.get(addr) > 2) {
				if (Node.getAddressTable().contains(addr))
					DBP.printdevln("Removing " + addr
							+ " from address table due to 3d strike");
				Node.getAddressTable().remove(addr);
				strikeList.remove(addr);
			}
		} else {
			strikeList.put(addr, 1);
		}
	}

	/**
	 * A previously unresponsive node at an address has come back, and has been
	 * forgiven.
	 * <p>
	 * 
	 * @param addr
	 */
	protected void forgive(L3Address addr) {
		if (strikeList.containsKey(addr)) {
			strikeList.put(addr, strikeList.get(addr) - 1);
			if (strikeList.get(addr) <= 0)
				strikeList.remove(addr);
		}
	}

	/**
	 * Handles a ping for the headcount functinality. Not a part of a node's
	 * operation defined by the protocol.
	 * <p>
	 * 
	 * @param ip
	 * @param port
	 * @throws IOException
	 */
	private void pingHeadcount(InetAddress ip, int port) throws IOException {
		SocketIOWrapper io = new SocketIOWrapper(new Socket(ip, port));
		io.write(Node.getAddress().toJSONString());
		io.close();
	}

	/**
	 * Performs a single update.
	 * <p>
	 * Given each address in this node's address table, it iterates over each
	 * with a given subset of the whole. The ping operation of each address in
	 * the set is done in parallel based on the set "PARALLELISM" setting from
	 * the node's configuration.
	 * <p>
	 * The next poll each address left in the set decides whether or not there
	 * is an item in their storage that also belongs in this nodes's storage.
	 * This helps ensure the redundancy of data in the network!
	 * <p>
	 * After the update operations have been complete, there is a forced call to
	 * the garbage collector to help reduce the overall memory footprint of the
	 * node.
	 * <p>
	 * 
	 * @throws InterruptedException
	 */
	protected void update() throws InterruptedException {
		DBP.printdebugln(Node.getAddressTable());
		// DBP.printdemoln(Node.getAddressTable().size());
		// find new neighbors

		Set<L3Address> every1ISee = Collections
				.newSetFromMap(new ConcurrentHashMap<L3Address, Boolean>());
		Router r = new Router(Node.getAddressTable());
		BlockingQueue<L3Address> q = new LinkedBlockingQueue<>();
		every1ISee.addAll(Node.getAddressTable().values());
		every1ISee.addAll(r.iterativeLookup(Node.getOverlayAddress()).values());
		q.addAll(every1ISee);
		ArrayList<Thread> pool = new ArrayList<Thread>();

		for (int i = 0; i < Runtime.getRuntime().availableProcessors()
				* PARALLELISM; i++) {
			Thread t = new Thread(new AddressUpdateThread(q, every1ISee, true,
					this));
			pool.add(t);
			t.start();
		}

		for (Thread t : pool) {
			t.join();
		}

		pool.clear();
		q.clear();
		q.addAll(every1ISee);
		pool = new ArrayList<>();

		for (int i = 0; i < Runtime.getRuntime().availableProcessors()
				* PARALLELISM; i++) {
			Thread t = new Thread(new AddressUpdateThread(q, every1ISee, false,
					this));
			pool.add(t);
			t.start();
		}

		for (Thread t : pool) {
			t.join();
		}

		boolean strike = false;
		// poll for new storage values
		for (L3Address neighbor : Node.getAddressTable()) {
			try {
				Set<Address> keys = Router.getIndex(neighbor, 1);
				for (Address key : keys) {
					if (!Node.getStorageController().containsKey(key)
							&& Node.getOverlayAddress()
									.getComparator()
									.compare(
											key,
											Node.getStorageController()
													.getHighest()) <= 0) {
						byte[] value = Router.getValue(neighbor, key);
						Node.getStorageController().put(
								new CASFileAddress(Node.getStorageController()
										.getDomain(), value));
					}
				}
				// too much forgiving: forgive(neighbor);
			} catch (RPCException e) {
				DBP.printException(e);
				if (e.getRPCError().equals(JSONRPCError.INVALID_RESULT))
					strike(neighbor);
			} catch (IOException e) {
				// strike = true;
				DBP.printerrorln("IO error updating storage from " + neighbor);
				DBP.printerror(e);
				// DBP.printException(e);
			}
			// if(strike)
			//
		}

		// update storage controller
		Node.getStorageController().garbageCollectReferences();
		try {
			Node.getStorageController().deleteThirdBucket();
		} catch (IOException e) {
			DBP.printerrorln("trouble deleting 3rd bucket");// e.printStackTrace();
		}

		/*
		 * Map<String, Map> hb = new HashMap<>(); hb.put("table",
		 * Node.getAddressTable()); hb.put("storage",
		 * Node.getStorageController()); DBP.println("heartbeat",
		 * Node.getAddressTable(), "\n" ,Node.getStorageController());
		 */

		System.gc();
	}

	/**
	 * The Runnable used to perform an update in parallel.
	 * <p>
	 * 
	 * @author Nathaniel Fischer
	 * @version v1.0.0 - May 4, 2015
	 */
	private static class AddressUpdateThread implements Runnable {

		BlockingQueue<L3Address> q;
		Set<L3Address> everyone;
		boolean seek;
		Updater ref;

		public AddressUpdateThread(BlockingQueue<L3Address> q,
				Set<L3Address> everyone, boolean seek, Updater ref) {
			this.q = q;
			this.everyone = everyone;
			this.seek = seek;
			this.ref = ref;
		}

		/**
		 * Pings every address in a queue, and adds strikes or forgives based on
		 * a response or a timeout of the communication.
		 * <p>
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			for (L3Address node = q.poll(); node != null; node = q.poll()) {
				if (seek) {
					try {
						AddressTable nn = Router.primitiveLookup(node,
								Node.getOverlayAddress());
						everyone.addAll(nn.values());
					} catch (IOException e) {
					} catch (RPCException e) {
						e.printStackTrace();
					}
				} else {
					try {
						if (Router.ping(node)) {
							Node.getAddressTable().add(node);
							ref.forgive(node);
						} else {
							ref.strike(node);
						}
					} catch (RPCException e) {
						ref.strike(node);
						DBP.printException(e);
					}
				}
			}
		}
	}
}
