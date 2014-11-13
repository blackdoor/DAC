/**
 * 
 */
package blackdoor.cqbe.certificate;

import java.io.File;

import blackdoor.cqbe.certificate.CertificateProtos.Certificate;

/**
 * @author nfischer3
 *
 */
public class CertificateValidator implements Validator {

	private Certificate certificate;
	/**
	 * If true, certificates will be evaluated as false if considerationDate is outside the validity period on the certificate.
	 */
	public boolean checkValidityPeriod = false;
	private Object considerationDate;
	/**
	 * 
	 */
	public CertificateValidator() {
		// TODO Auto-generated constructor stub
	}
	
	public CertificateValidator(Certificate certificate){
		
	}
	
	public CertificateValidator(File certificate){
		
	}
	
	

	public Certificate getCertificate() {
		return certificate;
	}

	public void setCertificate(Certificate certificate) {
		this.certificate = certificate;
	}

	/* (non-Javadoc)
	 * @see blackdoor.cqbe.certificate.Validator#isValid()
	 */
	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return isValidSignature() && 
				isValidValidityPeriod();
	}

	/* (non-Javadoc)
	 * @see blackdoor.cqbe.certificate.Validator#isValidSignature()
	 */
	@Override
	/**
	 * @return true if the signature for the certificate matches the private key information for the certificate.
	 */
	public boolean isValidSignature() {
		// TODO Auto-generated method stub
		return true;
	}
	/**
	 * 
	 * @return true if checkValidityPeriod is false or if considerationDate is inside the certificate's validity period. 
	 */
	public boolean isValidValidityPeriod(){
		if(checkValidityPeriod){
			return true;//TODO logic
		}
		else
			return true;
	}
	/**
	 * TODO determine if we need to handle messages in the past
	 * @return true if (checkValidityPeriod is false and certificate has not been revoked) or if (checkValidityPeriod is true and considerationDate is inside the certificate's validity period)
	 */
	public boolean isValidRevocationState(){
		return true;
	}

}
