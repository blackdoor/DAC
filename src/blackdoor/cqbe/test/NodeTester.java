package blackdoor.cqbe.test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.node.Node.NodeBuilder;
import blackdoor.cqbe.node.NodeException.RequiredParametersNotSetException;

public class NodeTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length >1 && args[0].equals("adam")) {
			NodeBuilder adamNode = new NodeBuilder();
			adamNode.setAdam(true);
			adamNode.setPort(1778);
			try {
				adamNode.buildNode();
			} catch (RequiredParametersNotSetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			NodeBuilder node = new NodeBuilder();
			InetAddress addr;
			try {
				addr = InetAddress.getByName("127.0.0.1");
				L3Address bootstrapNode = new L3Address(addr, 1778);
				node.setPort(1778);
				node.setBootstrapNode(bootstrapNode);
				node.buildNode();
			} catch (UnknownHostException | RequiredParametersNotSetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
