package blackdoor.cqbe.addressing;

import java.io.Serializable;
import java.math.BigInteger;
import java.net.Inet6Address;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;

import blackdoor.cqbe.addressing.AddressException.MissingLayer3Exception;
import blackdoor.crypto.Hash;
import blackdoor.util.Misc;

/**
 *
 * This class is an Address derived from a network layer address and port.
 *
 * @author Nathaniel Fischer
 * @version v1.0.0 - Nov 13, 2014
 */
public class L3Address extends Address implements Serializable{

	private Inet6Address l3Address = null;
	private int port = -1;

	/**
	 * Constructs a L3Address from the given address and port.
	 * @param a the IPv6 Address to use to create this Address
	 * @param port the port to use to create this Address
	 */
	public L3Address(Inet6Address a, int port) {
		super();
		setLayer3(a, port);
	}

	/**
	 * Convert the bytes of an IPv4 address to an "IPv4-mapped IPv6 address" according to RFC2373
	 * @param v4 32 bits representing an IPv4 address
	 * @return 16 bytes representing an IPv4-mapped IPv6 address for v4
	 */
	public static byte[] v426(byte[] v4){
		if(v4.length != 4){
			throw new RuntimeException("v4 must be 4 bytes that represent an IPv4 address.");
		}
		byte[] v6 = new byte[16];
		System.arraycopy(v4, 0, v6, 12, 4);
		System.arraycopy(new byte[]{(byte) 0xff, (byte) 0xff}, 0, v6, 10, 2);
		return v6;
	}

	protected void setLayer3(Inet6Address a, int port){
		l3Address = a;
		this.port = port;
		byte [] portBytes = BigInteger.valueOf(port).toByteArray();
		byte[] l3Bytes = a.getAddress();
		byte[] overlay = new byte[l3Bytes.length + portBytes.length];
		System.arraycopy(l3Bytes, 0, overlay, 0, l3Bytes.length);
		System.arraycopy(portBytes, 0, overlay, l3Bytes.length, portBytes.length);
		overlayAddress = Hash.getSHA256(overlay, true);
	}

	/**
	 * Returns the InetAddress for this L3Address.
	 * @return the InetAddress for this L3Address
	 */
	public Inet6Address getLayer3Address(){
		return l3Address;
	}

	/**
	 *
	 * @return
	 */
	public int getPort(){
		return port;
	}

	/**
	 * Returns a pretty printed string of the ip and port.
	 * The ip is printed as defined by java.net.InetAddress.toString()
	 * @return the ip and port in the format ip : port
	 */
	public String l3ToString(){
		return l3Address.getHostAddress() + " : " + port;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		L3Address other = (L3Address) obj;
		if (!Arrays.equals(overlayAddress, other.overlayAddress))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "L3Address [overlayAddress=" + overlayAddressToString()
				+ ", l3Address : port=" + l3ToString() + "]";
	}
}