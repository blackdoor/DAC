package blackdoor.cqbe.addressing;

import java.io.Serializable;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import blackdoor.crypto.Hash;

/**
 *
 * This class is an Address derived from a network layer address and port.
 *
 * @author Nathaniel Fischer
 * @version v1.0.0 - Nov 13, 2014
 */
@SuppressWarnings("serial")
public class L3Address extends Address implements Serializable{

	private InetAddress l3Address = null;
	private int port = -1;
	private static L3Address nonNodeAddress = null;
	protected static final boolean LOOPBACK_IS_NON_NODE = true; 
	
	/**
	 * Returns true if address is definitely not the overlay address of a node
	 * @param address
	 * @return true if address is definitely not the overlay address of a node
	 */
	public static boolean isNonNodeAddress(L3Address address){
		if(Address.isNonNodeAddress(address) ||
				(isLoopbackAddress(address) && LOOPBACK_IS_NON_NODE) ||
				address.equals(getNonNodeAddress()))
			return true;
		return false;
	}
	
	public static boolean isLoopbackAddress(L3Address address){
		return address.getLayer3Address().isLoopbackAddress();
	}
	
	/**
	 * Returns an address that other nodes will recognize is not the address of a node
	 * @return an address that other nodes will recognize is not the address of a node
	 */
	public static L3Address getNonNodeAddress(){
		if(nonNodeAddress != null)
			return nonNodeAddress;
		byte[] b = new byte[16];
		Arrays.fill(b, (byte)0x0);
		try {
			nonNodeAddress = new L3Address(InetAddress.getByAddress(b),0);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return nonNodeAddress;
	}

	/**
	 * Constructs a L3Address from the given address and port.
	 * @param a the IPv6 Address to use to create this Address
	 * @param port the port to use to create this Address
	 */
	public L3Address(InetAddress a, int port) {
		super();
		setLayer3(a, port);
	}
	
	/**
	 * Parses a JSONObject into an L3Address object. The JSONObject should contain an "IP" field, and a "port" field.
	 * @param js
	 * @return an L3Address object based on js
	 * @throws UnknownHostException
	 * @throws JSONException
	 */
	public static L3Address fromJSON(JSONObject js) throws UnknownHostException, JSONException{
		InetAddress l3Address = InetAddress.getByName(js.getString("IP"));
		int port = js.getInt("port");
		return new L3Address(l3Address, port);
	}
	
	/**
	 * 
	 * @return a JSONObject representing this L3Address
	 */
	public JSONObject toJSON(){
		JSONObject ret = new JSONObject();
		ret.put("IP", l3Address.getHostAddress());
		ret.put("port", port);
		return ret;
	}
	
	/**
	 * 
	 * @return a JSON string representing this L3Address
	 */
	public String toJSONString(){
		return toJSON().toString();
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

	protected void setLayer3(InetAddress a, int port){
		l3Address = a;
		this.port = port;
		byte [] portBytes = BigInteger.valueOf(port).toByteArray();
		byte[] l3Bytes = a.getAddress().length == 4 ? v426(a.getAddress()) : a.getAddress();
		byte[] overlay = new byte[l3Bytes.length + portBytes.length];
		System.arraycopy(l3Bytes, 0, overlay, 0, l3Bytes.length);
		System.arraycopy(portBytes, 0, overlay, l3Bytes.length, portBytes.length);

		setOverlayAddress(Hash.getSHA256(overlay, false));

	}

	/**
	 * Returns the InetAddress for this L3Address.
	 * @return the InetAddress for this L3Address
	 */
	public InetAddress getLayer3Address(){
		return l3Address;
	}

	/**
	 *
	 * @return this L3Address's port
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
		return l3Address.getHostAddress() + ":" + port;
	}
	
	public int hashCode() {
		return super.hashCode();
	};

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "L3Address [l3Address = " + l3ToString() + ", overlayAddress=" + overlayAddressToString() + "]";
	}
}