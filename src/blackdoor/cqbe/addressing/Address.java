package blackdoor.cqbe.addressing;

import blackdoor.cqbe.settings.Config;
import blackdoor.util.Misc;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.StringTokenizer;

/**
 * * This class is the superclass of all addresses that might be encountered in
 * an overlay network. That is, all addresses have an overlay address.
 * <p>
 * By default this class looks to use the size that is specified in the
 * configuration file "default.config".
 * <p>
 * 
 * @author Nathaniel Fischer
 * @version v1.0.0 - May 4, 2015
 */
public class Address implements Serializable {

	/**
	 * The size of an overlay address, in bytes.
	 */
	public static final int DEFAULT_ADDRESS_SIZE = (int) Config.getReadOnly(
			"default_address_size", "default.config");

	private static byte[] getNullOverlay() {
		return new byte[DEFAULT_ADDRESS_SIZE];
	}

	private static byte[] getFullOverlay() {
		byte[] b = new byte[DEFAULT_ADDRESS_SIZE];
		Arrays.fill(b, (byte) 0xFF);
		return b;
	}

	/**
	 * Returns true if address is definitely not the overlay address of a node
	 * <p>
	 * 
	 * @param address
	 * @return true if address is definitely not the overlay address of a node
	 */
	public static boolean isNonNodeAddress(Address address) {
		if (address.equals(getFullAddress())
				|| address.equals(getNullAddress()))
			return true;
		return false;
	}

	private byte[] overlayAddress;

	protected Address() {
		this.overlayAddress = getNullOverlay();
	}

	/**
	 * Constructs an Address object based on an overlay address.
	 * <p>
	 * 
	 * @param overlayAddress
	 *            a byte array representing an overlay address
	 * @throws AddressException
	 *             if overlayAddress is not the correct size (of the same length
	 *             as ADDRESS_SIZE)
	 */
	public Address(byte[] overlayAddress) {
		/*
		 * if (overlayAddress.length != ADDRESS_SIZE){ throw new
		 * AddressException("overlay address is " + overlayAddress.length +
		 * " bytes and needs to be " + ADDRESS_SIZE + " bytes."); }
		 * this.overlayAddress = overlayAddress;
		 */
		setOverlayAddress(overlayAddress);
	}

	/**
	 * Constructs an Address object with an overlay made up of all 0 bits.
	 * Equivalent to calling Address(new byte[ADDRESS_SIZE])
	 * <p>
	 * 
	 * @return
	 */
	public static Address getNullAddress() {
		return new Address(getNullOverlay());
	}

	/**
	 * Constructs an Address object with an overlay made up of all 1 bits.
	 * <p>
	 * 
	 * @return
	 */
	public static Address getFullAddress() {
		return new Address(getFullOverlay());
	}

	/**
	 * Construct an Address object from a String which contains a formatted
	 * address representation (':' separated hex).
	 * <p>
	 * 
	 * @throws AddressException
	 *             thrown if the address parameter is not in a valid format
	 */
	public Address(String address) throws AddressException {
		// Address this = new Address();
		this.overlayAddress = getNullOverlay();
		StringTokenizer tk = new StringTokenizer(address, ":");
		if (tk.countTokens() != Address.DEFAULT_ADDRESS_SIZE)
			throw new AddressException(
					"String representation of Address is improperly formatted. Wrong number of elements");
		try {
			for (int i = 0; i < Address.DEFAULT_ADDRESS_SIZE; i++) {
				this.overlayAddress[i] = Integer.decode("0x" + tk.nextToken())
						.byteValue();
			}
			// return a;
		} catch (NumberFormatException e) {
			throw new AddressException(
					"String representation of Address is improperly formatted. Invalid hex format.");
		}
	}

	/**
	 * Returns an AddressComparator which has this Address as its reference.
	 * <p>
	 * 
	 * @return an AddressComparator which has this Address as its reference.
	 */
	public AddressComparator getComparator() {
		return new AddressComparator(this);
	}

	/**
	 * Returns the overlay address of this Address object as a byte[]
	 * <p>
	 * 
	 * @return a deep copy of the overlay address of this Address object
	 */
	public byte[] getOverlayAddress() {
		return Arrays.copyOf(overlayAddress, overlayAddress.length);
	}

	/**
	 * Similar to getOverlayAddress, but returns the actual byte array instead
	 * of a deep copy. Changes to the return will affect the address.
	 * <p>
	 * 
	 * @return actual byte array for the overlay address instead of a deep copy.
	 */
	protected byte[] getShallowOverlayAddress() {
		return overlayAddress;
	}

	/**
	 * Sets this address with a byte array
	 * <p>
	 * http://csrc.nist.gov/publications/fips/fips180-4/fips-180-4.pdf
	 * <p>
	 * 
	 * @param address
	 *            the output of a CSHF digest
	 */
	protected void setOverlayAddress(byte[] address) {
		if (address.length < DEFAULT_ADDRESS_SIZE)
			throw new RuntimeException(
					"Can't set overlay address, given bytes are shorter than address size.");
		overlayAddress = Arrays.copyOf(address, DEFAULT_ADDRESS_SIZE);
	}

	/**
	 * A pretty-printed representation of the overlay address for this Address
	 * object. Each byte is separated by a colon
	 * <p>
	 * 
	 * @return a pretty-printed representation of the overlay address for this
	 *         Address object
	 */
	public String overlayAddressToString() {
		return Misc.getHexBytes(overlayAddress, ":");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Arrays.hashCode(overlayAddress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Address))
			return false;
		Address other = (Address) obj;
		if (!Arrays.equals(getOverlayAddress(), other.getOverlayAddress()))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return overlayAddressToString();
	}

	/**
	 * @author Nathaniel Fischer
	 * @version v1.0.0 - Nov 13, 2014
	 */
	public enum NaturalByteArrayComparator implements Comparator<byte[]> {
		INSTANCE;

		@Override
		public int compare(byte[] arg0, byte[] arg1) {
			return new BigInteger(arg0).compareTo(new BigInteger(arg1));
		}

	}

	/**
	 * Compares Overlay Addresses for ordering lists and figuring out which
	 * addresses to return in responses.
	 * <p>
	 * 
	 * @author Nathaniel Fischer
	 * @version v1.0.0 - Nov 13, 2014
	 *
	 */
	public static class OverlayComparator implements Comparator<byte[]> {
		private byte[] ref;

		/**
		 * 
		 * @param refrence
		 */
		public OverlayComparator(byte[] refrence) {
			this.ref = refrence;

		}

		public byte[] getReferenceAddress() {
			return ref;
		}

		/**
		 * Compare two overlay addresses based on the hamming distance between
		 * them.
		 * <p>
		 * Hamming distnace is defined as the count of bit difference between
		 * two addresses.
		 * <p>
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(byte[] arg0, byte[] arg1) {
			int ref0Distance = Misc.getHammingDistance(arg0, ref);
			int ref1Distance = Misc.getHammingDistance(arg1, ref);
			if (ref1Distance == ref0Distance) {
				BigInteger num1 = new BigInteger(1, arg1);
				BigInteger num0 = new BigInteger(1, arg0);
				return num0.compareTo(num1);
			}
			return ref0Distance - ref1Distance;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			OverlayComparator that = (OverlayComparator) o;

			if (!Arrays.equals(ref, that.ref))
				return false;

			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return Arrays.hashCode(ref);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "OverlayComparator{" + "ref=" + Misc.getHexBytes(ref, ":")
					+ '}';
		}
	}

	/**
	 * Returns the complement address value of this address.
	 * <p>
	 * 
	 * @return address
	 */
	public Address getComplement() {
		BitSet a = new BitSet();
		a = BitSet.valueOf(this.getOverlayAddress());
		a.xor(BitSet.valueOf(Address.getFullOverlay()));
		return new Address(a.toByteArray());
	}

	/**
	 * A comparator for Addresses. Distance between Addresses is defined as the
	 * Hamming weight of the first address XOR'd with the second. Since two
	 * addresses can only have a distance between them a reference point is
	 * needed to determine which is greater than the other.
	 * <p>
	 * 
	 * @author Nathaniel Fischer
	 * @version v1.0.0 - May 4, 2015
	 *
	 */
	public static class AddressComparator implements Comparator<Address> {

		private OverlayComparator comp;

		public AddressComparator(Address refrence) {
			this.comp = new OverlayComparator(
					refrence.getShallowOverlayAddress());
		}

		/**
		 * @return
		 */
		public Address getRefrenceAddress() {
			return new Address(comp.getReferenceAddress());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			AddressComparator that = (AddressComparator) o;

			if (!comp.equals(that.comp))
				return false;

			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return comp.hashCode();
		}

		/**
		 * Returns negative if arg0 is closer to reference than arg1 positive if
		 * o1 is farther from reference than o2 0 if they are equal distant
		 * <p>
		 * 
		 * @return negative if o1 is closer to reference than o2, 0 if they are
		 *         equal distant, positive if o1 is farther from reference than
		 *         o2
		 */
		public int compare(Address o1, Address o2) {
			return comp.compare(o1.getShallowOverlayAddress(),
					o2.getShallowOverlayAddress());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "AddressComparator{" + "comp=" + comp + '}';
		}
	}
}
