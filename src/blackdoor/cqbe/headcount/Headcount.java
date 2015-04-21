package blackdoor.cqbe.headcount;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import blackdoor.cqbe.headcount.HeadcountServerThread.HeadcountServerThreadBuilder;
import blackdoor.net.Server;
import blackdoor.util.Watch;

public class Headcount {

	// final static
	final static long interval = 5000;

	public static void main(String[] args) {
		File outfile = new File(args[1]);
		int port = Integer.parseInt(args[2]);
		// System.out.println("Listening on port " + port);
		Set<Entry> entries = Collections.synchronizedSet(new HashSet<Entry>());
		HeadcountServerThreadBuilder b = new HeadcountServerThreadBuilder(
				entries);
		Server s = new Server(b, port);
		Thread flush = new Thread(new Flush(entries, outfile));
		Thread serverThread = new Thread(s);
		flush.start();
		serverThread.start();

	}

	static class Flush implements Runnable {

		Set<Entry> s;
		File outfile;

		public Flush(Set<Entry> s, File outfile) {
			this.s = s;
			this.outfile = outfile;
		}

		@Override
		public void run() {
			int count = 0;
			while (true) {
				// System.out.println("looping");
				String out = "";
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
				}

				HashSet<Entry> r = new HashSet<Entry>();
				for (Entry e : s) {
					Watch w = new Watch();
					if (w.getTime() - e.lastSeen.getTime() > interval)
						r.add(e);
				}
				s.removeAll(r);
				for (Entry e : s) {
					out += e.address + " | last seen: " + e.lastSeen;
					out += "\n";
				}

				try {
					Files.write(outfile.toPath(),
							out.getBytes(StandardCharsets.UTF_8),
							StandardOpenOption.CREATE,
							StandardOpenOption.WRITE,
							StandardOpenOption.TRUNCATE_EXISTING);
					// System.out.println("wrote to file");
					// System.out.println(out);
				} catch (IOException e1) {
					// // TODO Auto-generated catch block
					// e1.printStackTrace();
				}
			}
		}

	}

}
