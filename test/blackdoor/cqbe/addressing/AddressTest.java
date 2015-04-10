package blackdoor.cqbe.addressing;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import blackdoor.util.Misc;

public class AddressTest {
	
	byte[] array;
	Address a;

	@Before
	public void setUp() throws Exception {
		array = new byte[32];
		for(int i = 0; i < array.length; i++){
			array[i] = (byte) i;
		}
		a = new Address(array);
	}

	@Test
	public void testAddressByteArray() {
		assertArrayEquals(new Address(array).getShallowOverlayAddress(), Arrays.copyOf(array, Address.DEFAULT_ADDRESS_SIZE));
	}

	@Test
	public void testGetNullAddress() {
		System.out.println(Address.getNullAddress());
		assertTrue(Address.getNullAddress().getShallowOverlayAddress().length == Address.DEFAULT_ADDRESS_SIZE);
	}

	@Test
	public void testAddressString() throws AddressException {
		assertEquals(new Address(array), new Address(new Address(array).toString()));
	}

	@Test
	public void testSetOverlayAddress() {
		assertArrayEquals(new Address(array).getShallowOverlayAddress(), Arrays.copyOf(array, Address.DEFAULT_ADDRESS_SIZE));
	}

	@Test
	public void testOverlayAddressToString() {
		System.out.println(new Address(array));
	}
	
	@Test
	public void testComplementAddress() {
		System.out.println(a);
		System.out.println(a.getComplement());
	}

}
