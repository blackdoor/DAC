package blackdoor.cqbe.research;

import blackdoor.cqbe.research.Message.MessageHandler;
import blackdoor.util.DBP;

public class PathCountHandler implements MessageHandler {
	
	long pathCount;

	public PathCountHandler() {
		pathCount = 0;
	}

	@Override
	public void handle(Message m, Node reciever) {
		if(reciever.malicious)
			return;
		if(reciever.address.equals(m.destination)){
			pathCount ++;
			DBP.printdevln("PathCount: " + pathCount);
		}else{
			int remainingDistance = m.destination.getDistance(reciever.address);
			for(Node n : reciever.addressTable){
				if(n.address.getDistance(m.destination) < remainingDistance){
					handle(m, n);
				}
			}
		}
	}

}
