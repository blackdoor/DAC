package blackdoor.cqbe.addressing;

import java.net.Inet6Address;

/**
 * 
 * @author Cj Buresch
 * @version 0.0.1 11/3/2014
 * Responsibilities - The structure for an address of a node in the server
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

	public Address(Inet6Address a) {
		// TODO Auto-generated constructor stub
		actual = a;
		// overlayAddress = Need the hashing stuff for this
	}

	/**
	 * @return integer that is great
	 */
	@Override
	public int compareTo(Address o) {
		// TODO Hamming distance stuff here. With the Overlay Address!!!
		// -1 return if lower
		// 0 if same distance
		// 1 if higher
		return 0;
	}

	/*
	 * GETTERS and SETTERS
	 */

	public void getIP() {
	}

	public void getOverlayAddress() {
	}
}