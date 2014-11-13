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
 * @author Nathaniel Fischer
 * @version v1.0.0 - Nov 13, 2014
 */
@SuppressWarnings("serial")
public class Address implements Serializable{
	
	private byte[] overlayAddress;
	private Inet6Address l3Address = null;
	private int port = -1;

	/**
	 * Initialize Address with subject unique identifier;
	 * <p>
	 * 
	 * @param a
	 */
	public Address(String a) {
		overlayAddress = Hash.getSHA256(a.getBytes(Charset.availableCharsets().get("UTF-8")), true);
	}

	/**
	 * Initialize Address with IP and PORT.
	 * <p>
	 * Formatting ----------?
	 * 
	 * @param ip
	 * @param port
	 */
	public Address(byte[] overlayAddress) {
		this.overlayAddress = overlayAddress;
	}

	public Address(Inet6Address a, int port) {
		setLayer3(a, port);
	}

	/**
	 * Returns true if address has a Layer3 Address.
	 * <p>
	 * 
	 * @return boolean
	 */
	public boolean hasLayer3() {
		return l3Address != null;
	}

	/*
	 * GETTERS and SETTERS
	 */

	public void setLayer3(Inet6Address a, int port){
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
	 * Returns a Layer3 address.
	 * <p>
	 * If the address has not been initialized, it will throw an exception.
	 * 
	 * @throws MissingLayer3Exception
	 */
	public Inet6Address getLayer3Address() throws MissingLayer3Exception {
		if (l3Address != null)
			return l3Address;
		else
			throw new MissingLayer3Exception();
	}
	
	public int getPort() throws MissingLayer3Exception {
		if (l3Address != null)
			return port;
		else
			throw new MissingLayer3Exception();
	}

	public byte[] getOverlayAddress() {
		return overlayAddress;
	}
	
	public String overlayAddressToString(){
		return Misc.bytesToHex(overlayAddress);
	}
	
	public String l3ToString() throws MissingLayer3Exception{
		if (l3Address != null)
			return l3Address.getHostAddress() + ":" + port;
		else
			throw new MissingLayer3Exception();
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
		Address other = (Address) obj;
		if (!Arrays.equals(overlayAddress, other.overlayAddress))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Address [overlayAddress=" + Arrays.toString(overlayAddress)
				+ ", l3Address=" + l3Address + ", port=" + port + "]";
	}
	
	public static class OverlayComparator implements Comparator<byte[]>{
		//TODO kill this bastard thing. reverse the roles of OverlayComparator and AddressComparator
		private AddressComparator comp;
		
		public OverlayComparator(Address refrence){
			this.comp = new AddressComparator(refrence);
			
		}

		@Override
		public int compare(byte[] arg0, byte[] arg1) {
			return comp.compare(new Address(arg0), new Address(arg1));
		}
	}
	
	public static class AddressComparator implements Comparator<Address>{
		
		private Address reference;
		
		public AddressComparator(Address refrence){
			this.reference = refrence;
		}
		
		public Address getRefrenceAddress(){
			return reference;
		}

		/**
		 * returns negative if arg0 is closer to reference than arg1
		 * positive if arg0 is farther from reference than arg1
		 * 0 if they are equal distant
		 */
		@Override
		public int compare(Address arg0, Address arg1) {
			int ref0Distance = Misc.getHammingDistance(arg0.getOverlayAddress(), reference.getOverlayAddress());
			int ref1Distance = Misc.getHammingDistance(arg1.getOverlayAddress(), reference.getOverlayAddress());
			return ref0Distance - ref1Distance;
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
			AddressComparator other = (AddressComparator) obj;
			if (reference == null) {
				if (other.reference != null)
					return false;
			} else if (!reference.equals(other.reference))
				return false;
			return true;
		}

		
	}
	
	
}