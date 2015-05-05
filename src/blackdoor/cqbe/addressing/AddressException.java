package blackdoor.cqbe.addressing;

/**
 * Custom Exceptions to help express errors in Address related functions of a
 * node.
 * <p>
 * 
 * @author Cj Buresch
 * @version v1.0.0 - May 4, 2015
 */
public class AddressException extends Exception {

	public AddressException() {
	}

	public AddressException(String e) {
		super(e);
	}

	public static class MissingLayer3Exception extends AddressException {

	}
}
