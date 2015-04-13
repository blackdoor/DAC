package blackdoor.cqbe.node;

import java.io.File;
import java.net.InetAddress;

import blackdoor.cqbe.addressing.CASFileAddress;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.node.Node;
import blackdoor.cqbe.node.Node.NodeBuilder;
import blackdoor.cqbe.storage.StorageController;
import blackdoor.util.DBP;

public class NodeTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DBP.VERBOSE = true;
		if (args.length >1 && args[0].equals("adam")) {
			NodeBuilder adamNode = new NodeBuilder();
			adamNode.setAdam(true);
			adamNode.setPort(1778);
			try {
				adamNode.buildNode();
			} catch (Exception e) {
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
				StorageController c = Node.getStorageController();
				CASFileAddress lolfile = new CASFileAddress(new File(c.getDomain().toString() +"/lol_file.txt"));
				c.put(lolfile);
				System.out.println(c);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}