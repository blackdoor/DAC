package blackdoor.cqbe.addressing;

/**
 * 
 * @author Cj Buresch
 * @version v0.0.1 - Nov 5, 2014
 */
public class AddressException extends Exception {

	public AddressException(){}

	public AddressException(String e){
		super(e);
	}

	/**
	 * Missing Layer 3 Address.
	 * <p>
	 * 
	 * @author Cj Buresch
	 * @version v0.0.1 - Nov 5, 2014
	 */
	public static class MissingLayer3Exception extends AddressException {

	}
}
