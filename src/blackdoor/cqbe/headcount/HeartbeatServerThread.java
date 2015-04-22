package blackdoor.cqbe.headcount;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.net.ServerThread;
import blackdoor.util.DBP;
import blackdoor.util.Misc;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nfischer3 on 4/21/15.
 */
public class HeartbeatServerThread implements ServerThread {

	public static void removeOld(Map<L3Address, Node> network, int numMin) {
		Date cutoff = new Date(new Date().getTime() - numMin);
		for (Map.Entry<L3Address, Node> e : network.entrySet()) {
			if (e.getValue().timestamp.before(cutoff))
				network.remove(e.getKey());
		}
	}

	public static JSONObject buildSHIT(Map<L3Address, Node> network) {
		Map<Address, Integer> stupid = new HashMap<>();
		JSONArray links = new JSONArray();
		JSONArray nodes = new JSONArray();
		JSONObject shit = new JSONObject();

		int i = 0;
		for (Map.Entry<L3Address, Node> e : network.entrySet()) {
			JSONObject node = new JSONObject();
			node.put("name", e.getKey());
			node.put("group", 1);
			nodes.put(node);

			stupid.put(e.getKey(), i++);
		}

		for (Map.Entry<L3Address, Node> e : network.entrySet()) {
			Node n = e.getValue();

			for (L3Address a : n.table.values()) {
				if (stupid.containsKey(a)) {
					JSONObject link = new JSONObject();
					link.put("source", stupid.get(e.getKey()));
					link.put("target", stupid.get(a));
					link.put(
							"value",
							Misc.getHammingDistance(e.getKey().getOverlayAddress(),
									a.getOverlayAddress()));
					links.put(link);
				}
			}
		}

		shit.put("links", links);
		shit.put("nodes", nodes);
		return shit;
	}

	Socket sock;
	Map<L3Address, Node> network;

	public HeartbeatServerThread(Socket sock, Map<L3Address, Node> network) {
		this.sock = sock;
		this.network = network;
	}

	@Override
	public Socket getSocket() {
		return sock;
	}

	@Override
	public void run() {
		try {
			BufferedReader bR = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			while (true) {
				JSONObject recv = new JSONObject(bR.readLine());
				Node n = new Node();
				n.timestamp =
						DatatypeConverter.parseDateTime(recv.getString("timestamp")).getTime();
				n.addr =
						L3Address.fromJSON(recv.getJSONArray("message").getJSONObject(0)
								.getJSONObject("source"));
				n.table =
						AddressTable.fromJSONArray(recv.getJSONArray("message").getJSONObject(0)
								.getJSONArray("table"));
				network.put(n.addr, n);
			}
		} catch (IOException e) {
			e.printStackTrace();
			DBP.printException(e);
		}
	}

	public static class HeartbeatServerThreadFactory implements ServerThreadBuilder {

		Map<L3Address, Node> network;

		public HeartbeatServerThreadFactory(Map<L3Address, Node> network) {
			this.network = network;
		}

		@Override
		public ServerThread build(Socket socket) {
			HeartbeatServerThread thread = new HeartbeatServerThread(socket, network);
			return thread;
		}
	}
}
