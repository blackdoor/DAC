package blackdoor.cqbe.headcount;

import java.io.IOException;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import blackdoor.net.ServerThread;
import blackdoor.net.SocketIOWrapper;
import blackdoor.util.Watch;

public class HeadcountServerThread implements ServerThread {

	Socket s;
	Set<Entry> set;

	@Override
	public void run() {
		try {
			SocketIOWrapper io = new SocketIOWrapper(s);
			Ping p = new Ping(io.read());
			Entry e = new Entry();
			e.setAddress(p.getL3());
			e.setLastSeen(new Watch());
			set.remove(e);
			set.add(e);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Socket getSocket() {
		return s;
	}

	public static class HeadcountServerThreadBuilder implements
			ServerThreadBuilder {

		Set<Entry> set;

		public HeadcountServerThreadBuilder(Set<Entry> set) {
			this.set = set;
		}

		@Override
		public ServerThread build(Socket sock) {
			HeadcountServerThread x = new HeadcountServerThread();
			x.s = sock;
			x.set = set;
			return x;
		}

	}

}
