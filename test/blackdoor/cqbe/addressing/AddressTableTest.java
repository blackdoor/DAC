package blackdoor.cqbe.addressing;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

public class AddressTableTest {
	AddressTable table;
	Address ref = Address.getNullAddress();

	@Before
	public void setUp() throws Exception {
		table = new AddressTable(ref);
		table.add(new L3Address(InetAddress.getByName("192.168.1.101"), 1234));
	}

	@Test
	public void testGetReferenceAddress() {
		assertEquals(table.getReferenceAddress(), ref);
	}
	
	@Test
	public void testTrim() throws UnknownHostException, InterruptedException{
		for(int i = 0; i < 50; i++){
			table.add(new L3Address(InetAddress.getByName("192.168.1.101"), i));
			//System.out.println(table);
		}
		table.setMaxSize(10);
		table.trim();
		assertEquals(table.size(), 10);
	}
	
	@Test
	public void testBigTable() throws Exception{
		AddressTable table = new AddressTable();
		for(int i = 1; i < AddressTable.DEFAULT_MAX_SIZE * 1.5; i++){
			table.add(new L3Address(InetAddress.getByName("192.168.1.101"), i));
		}
		assertTrue(table.size() > AddressTable.DEFAULT_MAX_SIZE);
	}

	@Test
	public void testFromJSONArray() {
		assertEquals(table, AddressTable.fromJSONArray(table.toJSONArray()));
	}

	@Test
	public void testToJSONString() {
		System.out.println(table.toJSONString());
	}

	@Test
	public void testAdd() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetAddress() throws UnknownHostException {
		L3Address addr = new L3Address(InetAddress.getByName("192.168.1.101"), 1234);
		assertTrue(table.get((Address) addr).equals(addr));
	}

	@Test
	public void testRemoveL3Address() throws UnknownHostException {
		table.add(new L3Address(InetAddress.getByName("192.168.1.101"), 11));
		assertTrue(table.contains(new L3Address(InetAddress.getByName("192.168.1.101"), 11)));
		table.remove(new L3Address(InetAddress.getByName("192.168.1.101"), 11));
		assertFalse(table.contains(new L3Address(InetAddress.getByName("192.168.1.101"), 11)));
	}

	@Test
	public void testContainsKey() throws UnknownHostException {
		table.add(new L3Address(InetAddress.getByName("192.168.1.101"), 11));
		assertTrue(table.contains(new L3Address(InetAddress.getByName("192.168.1.101"), 11)));
	}

	@Test
	public void testToString() {
		System.out.println(table);
	}

}
