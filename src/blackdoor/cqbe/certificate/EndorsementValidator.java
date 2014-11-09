/**
 * 
 */
package blackdoor.cqbe.certificate;

import blackdoor.cqbe.certificate.CertificateProtos.Certificate;
import blackdoor.cqbe.certificate.CertificateProtos.Endorsement;

/**
 * @author nfischer3
 *
 */
public class EndorsementValidator implements Validator {

	private Certificate issuer;
	private Certificate subject;
	private Endorsement endorsement;
	/**
	 * 
	 */
	public EndorsementValidator() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * @return the issuer
	 */
	public Certificate getIssuer() {
		return issuer;
	}


	/**
	 * @param issuer the issuer to set
	 */
	public void setIssuer(Certificate issuer) {
		this.issuer = issuer;
	}


	/**
	 * @return the subject
	 */
	public Certificate getSubject() {
		return subject;
	}


	/**
	 * @param subject the subject to set
	 */
	public void setSubject(Certificate subject) {
		this.subject = subject;
	}


	/**
	 * @return the endorsement
	 */
	public Endorsement getEndorsement() {
		return endorsement;
	}


	/**
	 * @param endorsement the endorsement to set
	 */
	public void setEndorsement(Endorsement endorsement) {
		this.endorsement = endorsement;
	}


	/* (non-Javadoc)
	 * @see blackdoor.cqbe.certificate.Validator#isValid()
	 */
	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see blackdoor.cqbe.certificate.Validator#isValidSignature()
	 */
	@Override
	public boolean isValidSignature() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isValidUniqueIdentifiers(){
		return true;
	}
	public boolean isValidSubjectKeyInfo(){
		return true;
	}
	/**
	 * 
	 * @return true if checkValidityPeriod is false or if considerationDate is inside the endorsment's validity period. 
	 */
	public boolean isValidValidityPeriod(){
		return true;
	}
	/**
	 * TODO determine if we need to handle messages in the past
	 * @return true if (checkValidityPeriod is false and certificate has not been revoked) or if (checkValidityPeriod is true and considerationDate is inside the certificate's validity period)
	 */
	public boolean isValidRevocationState(){
		return true;
	}
	
	public boolean isValidCommonNames(){
		return true;
	}

}
