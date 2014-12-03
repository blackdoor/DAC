package blackdoor.cqbe.test;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.rpc.RPCBuilder;
import blackdoor.cqbe.rpc.RPCException.RequiredParametersNotSetException;

public class RPCTest {

	public static void main(String[] args) throws RequiredParametersNotSetException, UnknownHostException {
		InetAddress a = InetAddress.getByName("::FFFF:192.168.1.1");
		byte[] aBytes = new byte[16];
		System.arraycopy(a.getAddress(), 0, aBytes, 12, a.getAddress().length);
		aBytes[10] = (byte) 0xFF;
		aBytes[11] = (byte) 0xFF;
		InetAddress b = InetAddress.getByAddress(aBytes);
		System.out.println(b);
		
		RPCBuilder builder = new RPCBuilder();
		builder.setSourceIP(b);
		builder.setSourcePort(1234);
		builder.setDestinationO(new L3Address(a, 1234));
		builder.setIndex(1);
		System.out.println(builder.buildGET());
		System.out.println(builder.buildLOOKUP());
		
	}

}
