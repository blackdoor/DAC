package blackdoor.cqbe.headcount;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;

import blackdoor.net.ServerThread;
import blackdoor.net.SocketIOWrapper;
import blackdoor.util.Watch;

public class HeadcountServerThread implements ServerThread {

	private Socket s;
	Set<Entry> set;

	public HeadcountServerThread(Set<Entry> set, Socket sock) {
		this.s = sock;
		this.set = set;
	}

	@Override
	public void run() {
		SocketIOWrapper io;
		try {
			io = new SocketIOWrapper(s);
			String str = io.read();
			Ping p = new Ping(str);
			Entry e = new Entry();
			e.setAddress(p.getL3());
			e.setLastSeen(new Watch());
			set.remove(e);
			set.add(e);
		} catch (UnknownHostException | JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
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
			return new HeadcountServerThread(set, sock);
		}

	}

}
