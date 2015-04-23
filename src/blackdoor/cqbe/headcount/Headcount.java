package blackdoor.cqbe.headcount;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.headcount.HeartbeatServerThread.HeartbeatServerThreadFactory;
import blackdoor.net.Server;
import blackdoor.util.Watch;

public class Headcount {

	// final static
	final static long interval = 5000;
	final static int minimumTime = 30 * 1000; // in seconds

	public static void main(String[] args) {
		File outfile = new File(args[0]);
		int port = Integer.parseInt(args[1]);
		// System.out.println("Listening on port " + port);
		Map<L3Address, Node> network = new ConcurrentHashMap<>();
		HeartbeatServerThreadFactory b = new HeartbeatServerThreadFactory(
				network);
		Server s = new Server(b, port);
		HeartFlusher flusher = new HeartFlusher(network, outfile);
		Thread flush = new Thread(flusher);
		Thread serverThread = new Thread(s);
		flush.start();
		serverThread.start();

	}

	public static class HeartFlusher implements Runnable {

		private Map<L3Address, Node> network;
		private File outfile = null;

		private final static Object lock = new Object();

		public HeartFlusher(Map<L3Address, Node> s, File outfile) {
			this.network = s;
			this.outfile = outfile;
		}

		@Override
		public synchronized void run() {
			synchronized (lock) {
				while (true) {

					try {
						Thread.sleep(interval);
					} catch (InterruptedException e1) {
						Thread.currentThread().interrupt();
					}
					System.out.println("looping");
					HeartbeatServerThread.removeOld(network, minimumTime);
					System.out.println(network.size());
					JSONObject obj = HeartbeatServerThread.buildSHIT(network);

					try {
						BufferedWriter bw = new BufferedWriter(new FileWriter(
								outfile));
						bw.write(obj.toString(3));
						bw.flush();
						bw.close();

					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}

		}
	}

	public static class Flush implements Runnable {

		private Set<Entry> s;
		private File outfile;

		private final static Object lock = new Object();

		public Flush(Set<Entry> s, File outfile) {
			this.s = s;
			this.outfile = outfile;
		}

		@Override
		public synchronized void run() {
			synchronized (lock) {
				while (true) {
					// System.out.println("looping");
					String out = "-----Headcount is up -----\n";

					try {
						Thread.sleep(interval);
					} catch (InterruptedException e1) {
						Thread.currentThread().interrupt();
					}

					// HashSet<Entry> r = new HashSet<Entry>();
					Watch w = new Watch();
					for (Entry e : s) {
						if (w.getTime() - e.getWatch().getTime() > interval * 3)
							s.remove(e);
					}
					// s.removeAll(r);
					for (Entry e : s) {
						out += e.getAddress() + " | last seen: "
								+ e.getLastSeen();
						out += "\n";
					}

					try {
						Files.write(outfile.toPath(),
								out.getBytes(StandardCharsets.UTF_8),
								StandardOpenOption.CREATE,
								StandardOpenOption.WRITE,
								StandardOpenOption.TRUNCATE_EXISTING);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					// System.out.println("wrote to file");
					// System.out.println(out);

				}
			}
		}
	}
}
