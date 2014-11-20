package blackdoor.cqbe.certificate;

/**
 * Validator interface to be implemented for both certificates and endorsements.
 * Validators consider a group of predicates (certificates and or endorsements) and draw a conclusion as to whether they are valid together.
 * @author nfischer3
 *
 */
public interface Validator {
	
	/**
	 * Determines if the predicates for this Validator are valid.
	 * This function must call all the other methods in the class that begin with isValid.
	 * @return true if all other methods in the function that start with isValid return true.
	 */
	public boolean isValid();
	
	/**
	 * Checks to see if the signatures on predicates are valid for the public keys given by the predicates.
	 * @return true if all predicates signatures are valid for their public keys.
	 */
	public boolean isValidSignature();
	
}
