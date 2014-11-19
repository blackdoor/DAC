package blackdoor.cqbe.addressing;

import java.net.Inet6Address;

import blackdoor.cqbe.addressing.AddressException.*;

/**
 * 
 * @author Cj Buresch
 * @version v0.0.1 - Nov 3, 2014
 */
public class Address implements Comparable<Address> {

	class OverlayAddress implements Comparable<OverlayAddress> {

		private String address;

		public OverlayAddress(Inet6Address a) {
			// address = HASH needs to be done here
		}

		@Override
		public int compareTo(OverlayAddress o) {
			// TODO Hamming distance stuff here.
			// -1 return if lower
			// 0 if same distance
			// 1 if higher
			return 0;
		}
	}

	private OverlayAddress overlayAddress = null;
	private Inet6Address actual = null;
	// EXPERIMENTAL!!
	private String last_connected; // maybe make this a cal object?

	/**
	 * Initialize Address with subject unique identifier;
	 * <p>
	 * 
	 * @param a
	 */
	public Address(String a) {
		// TODO
	}

	/**
	 * Initialize Address with IP and PORT.
	 * <p>
	 * Formatting ----------?
	 * 
	 * @param ip
	 * @param port
	 */
	public Address(String ip, String port) {
		// TODO
	}

	public Address(Inet6Address a) {
		// TODO Auto-generated constructor stub
		actual = a;
		// overlayAddress = Need the hashing stuff for this
	}

	/**
	 * @return
	 */
	@Override
	public int compareTo(Address o) {
		// TODO Hamming distance stuff here. With the Overlay Address!!!
		// -1 return if lower
		// 0 if same distance
		// 1 if higher
		return 0;
	}

	/**
	 * Returns true if address has a Layer3 Address.
	 * <p>
	 * 
	 * @return boolean
	 */
	public boolean hasLayer3() {
		return actual == null;
	}

	/*
	 * GETTERS and SETTERS
	 */

	/**
	 * Returns a Layer3 address.
	 * <p>
	 * If the address has not been initialized, it will throw an exception.
	 * 
	 * @throws MissingLayer3Exception
	 */
	public Inet6Address getLayer3() throws MissingLayer3Exception {
		if (actual != null)
			return actual;
		else
			throw new MissingLayer3Exception();
	}

	public String getOverlay() {
		return null;
	}
}