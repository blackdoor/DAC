package blackdoor.cqbe.research;

import java.util.ArrayList;
import java.util.List;

import blackdoor.util.DBP;

public class Experiment {

	public static void main(String[] args) {
		DBP.DEMO = true;
		//DBP.DEV = true;
		List<Integer> results = new ArrayList();
		int sum = 0;
		for(int i = 0; i<30; i++){
			sum += pathCountExperiment();
		}
		System.out.println(sum/30);
		//pathCountExperiment();
	}
	
	public static int pathCountExperiment(){
		Network net = new Network(4);
		int dest = 0b1111;
		net.addAttackers(5, 0, dest);
		//System.out.println(net);
		PathCountHandler h = new PathCountHandler();
		Message m = new Message(new Address(dest));
		net.runSimulation(m, net.nodes.get(0), h);
		System.out.println("Paths:" + h.pathCount);
		return (int) h.pathCount;
	}

}
