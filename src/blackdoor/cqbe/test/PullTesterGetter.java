package blackdoor.cqbe.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.List;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.node.Node.NodeBuilder;
import blackdoor.cqbe.output_logic.Router;

public class PullTesterGetter {

	public static void main(String[] args) throws Exception, IOException {
		InetAddress address;

		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(
				whatismyip.openStream()));
		address = InetAddress.getByName(in.readLine());

		
		L3Address neighbor = new L3Address(address, 1778);
		NodeBuilder node = new NodeBuilder();
		node.setPort(1779);
		node.setBootstrapNode(neighbor);
		node.buildNode();
		List<Address> keys = Router.getIndex(neighbor , 3);
		System.out.print(keys);

	}

}