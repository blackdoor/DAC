package blackdoor.cqbe.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.AddressException;
import blackdoor.cqbe.addressing.CASFileAddress;
import blackdoor.crypto.Hash;

public class CASFileAddressTest {

	@Test
	public void testCASFileAddressPathByteArray() throws IOException, AddressException {
		byte[] value = new byte[16];
		File directory = new File(".");
		CASFileAddress fileAddress = new CASFileAddress(directory.toPath(), value);
		assertEquals("fail - generated overlay address is wrong", fileAddress.overlayAddressToString(), new Address(Hash.getSHA256(value)).overlayAddressToString());
		CASFileAddress generatedAddress = new CASFileAddress(fileAddress.getFile());
		assertTrue("fail - CAS address constructed from file is not the same as the one constructed from blob", generatedAddress.equals(fileAddress));
	}

}
