package blackdoor.cqbe.research;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import blackdoor.cqbe.research.Message.MessageHandler;
import blackdoor.util.DBP;

public class Network {
	
	int dimension;
	List<Node> nodes;

	public Network(int dimension) {
		this.dimension = dimension;
		nodes = new ArrayList<Node>();
		populateNodes();
		DBP.printdemoln("Nodes Created");
		buildEdges();
		DBP.printdemoln("Edges Built");
	}
	
	public void runSimulation(Message m, Node sender, MessageHandler handler){
		handler.handle(m, sender);
	}
	
	public void populateNodes(){
		for(long i = 0; i < Math.pow(2, dimension); i++){
			nodes.add(new Node(new Address(i)));
		}
	}
	
	public void addAttackers(int a){
		HashSet<Integer> s = new HashSet<Integer>();
		Random r = new Random();
		for(int n = 0; n < a; n++){
			int i = r.nextInt((int) Math.pow(2, dimension));
			for(; s.contains(i); 
					i = r.nextInt((int) Math.pow(2, dimension)));
			nodes.get(i).malicious = true;
			DBP.printdevln("Attacker added:" + nodes.get(i).address);
		}
	}
	
	public void addAttackers(int a, int src, int dest){
		HashSet<Integer> s = new HashSet<Integer>();
		s.add(src);
		s.add(dest);
		Random r = new Random();
		for(int n = 0; n < a; n++){
			int i = r.nextInt((int) Math.pow(2, dimension));
			for(; s.contains(i); 
					i = r.nextInt((int) Math.pow(2, dimension)));
			nodes.get(i).malicious = true;
			s.add(i);
			DBP.printdevln("Attacker added:" + nodes.get(i).address);
		}
	}
	
	public void buildEdges(){
		for(Node n : nodes){
			long mask = 0x01;
			int index;
			for(int i = 0; i < dimension; i++){
				index = (int) (n.address.address ^ mask);
				n.addressTable.add(nodes.get(index));
				mask <<= 1;
			}
		}
	}
	
	public String toString(){
		String ret = "";
		for(Node n : nodes){
			ret += n;
		}
		return ret;
	}

}
