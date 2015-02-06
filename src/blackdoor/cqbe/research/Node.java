package blackdoor.cqbe.research;

import java.util.HashSet;
import java.util.Set;

public class Node {
	
	public Set<Node> addressTable;
	public boolean malicious;
	public Address address;

	public Node(Address a) {
		this.address = a;
		malicious = false;
		addressTable = new HashSet<Node>();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String ret = "Node [address=" + address +", malicious="
				+ malicious + '\n';
		for(Node n : addressTable){
			ret += "\t" + n.address + "\n";
		}
		return ret;
	}
	
	
	
}
