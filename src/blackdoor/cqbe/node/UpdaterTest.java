package blackdoor.cqbe.node;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;

import org.junit.Test;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressTable;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.node.Node.NodeBuilder;
import blackdoor.cqbe.node.NodeException.RequiredParametersNotSetException;

public class UpdaterTest {
	
	
	@Test
	public void testUpdateTimerVolatility() throws InterruptedException, NodeException {
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


	@SuppressWarnings("static-access")
	@Test
	public void testUpdateStrikeList() throws NodeException{
		NodeBuilder bob = new NodeBuilder();
		bob.setAdam(true);
		bob.setPort(1234);
		Node n = null;
		try {
			n = bob.buildNode();
			n.getAddressTable().add(new L3Address(InetAddress.getByName("192.168.1.1"), 8888));
			n.getAddressTable().add(new L3Address(InetAddress.getByName("192.168.1.2"), 8888));
			n.getAddressTable().add(new L3Address(InetAddress.getByName("192.168.1.3"), 8888));
			n.getAddressTable().add(new L3Address(InetAddress.getByName("192.168.1.4"), 8888));
			
			Thread.sleep(1000);
		
		} catch (RequiredParametersNotSetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
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
			firstStrike = n.getUpdater().getFS();
			secondStrike = n.getUpdater().getSS();
			System.out.println("FS Size is: " + firstStrike.size());
			System.out.println("SS Size is: " + secondStrike.size());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	/*
	@SuppressWarnings("static-access")
	public void testUpdateStorage() throws NodeException{
		NodeBuilder bob = new NodeBuilder();
		bob.setAdam(true);
		bob.setPort(1234);
		Node n = null;
		try{
			n = bob.buildNode();
			Thread.sleep(1000);
		} catch (RequiredParametersNotSetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			n.getAddressTable().add(new L3Address(InetAddress.getByName("192.168.1.1"), 8888));
			n.getAddressTable().add(new L3Address(InetAddress.getByName("192.168.1.2"), 8888));
			n.getAddressTable().add(new L3Address(InetAddress.getByName("192.168.1.3"), 8888));
			n.getAddressTable().add(new L3Address(InetAddress.getByName("192.168.1.4"), 8888));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(true){
			try {
				Thread.sleep(61000);
				System.out.println("FS-" + n.getUpdater().getFS());
				System.out.println("SS-" + n.getUpdater().getSS());
				for(L3Address a : n.getAddressTable().values()){
					System.out.println(a);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	*/
}
