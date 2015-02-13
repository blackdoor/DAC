package blackdoor.cqbe.node;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.node.Node.NodeBuilder;
import blackdoor.cqbe.node.NodeException.RequiredParametersNotSetException;

public class UpdaterTest {
	
	
	@Test
	public void testUpdateTimerVolatility() throws RequiredParametersNotSetException, InterruptedException {
		NodeBuilder bob = new NodeBuilder();
		bob.setAdam(true);
		bob.setPort(1234);
		Node n = bob.buildNode();
		Thread.sleep(20);

		
		assertTrue("Timer should initially be started",n.getUpdater().getTimer());
		n.getUpdater().stopUpdater();
		assertFalse("Timer should now be stopped",n.getUpdater().getTimer());
		n.getUpdater().startUpdater();
		assertTrue("Timer should be restarted",n.getUpdater().getTimer());
	}
	
	@Test
	public void testUpdateTimerFire(){
		NodeBuilder bob = new NodeBuilder();
		bob.setAdam(true);
		bob.setPort(1234);
		try {
			Node n = bob.buildNode();
			Thread.sleep(1);
		} catch (RequiredParametersNotSetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	@Test
	public void testUpdateStrikeList(){
		NodeBuilder bob = new NodeBuilder();
		bob.setAdam(true);
		bob.setPort(1234);
		Node n = null;
		try {
			n = bob.buildNode();
			Thread.sleep(10000);
		
		} catch (RequiredParametersNotSetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashSet<Address> firstStrike = new HashSet<Address>();
		HashSet<Address> secondStrike = new HashSet<Address>();
		firstStrike = n.getUpdater().getFS();
		secondStrike = n.getUpdater().getSS();
		System.out.println("FS Size is: " + firstStrike.size());
		System.out.println(firstStrike);
		try {
			Thread.sleep(61000);
			System.out.println("FS Size is: " + firstStrike.size());
			System.out.println("SS Size is: " + secondStrike.size());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
