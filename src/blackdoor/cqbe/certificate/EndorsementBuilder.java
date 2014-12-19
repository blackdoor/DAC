package blackdoor.cqbe.certificate;

import java.io.File;
import java.security.KeyException;
import java.security.PrivateKey;

import blackdoor.cqbe.certificate.CertificateProtos.Certificate;
import blackdoor.cqbe.certificate.CertificateProtos.Endorsement;

/**
 * An endorsement builder. Given two certificates and some endorsement settings, an endorsement can be composed and signed.
 * @author nfischer3
 *
 */
public class EndorsementBuilder implements Builder{
	
	private Certificate issuer;
	private Certificate subject;
	private Endorsement endorsement;

	/**
	 * Create a new un-configured EndorsementBuilder object. Before this object is used the fields of the certificate will need to be set.
	 */
	public EndorsementBuilder() {
		// TODO Auto-generated constructor stub
		
	}
	
	/**
	 *
	 * @param issuer
	 * @param subject
	 */
	public EndorsementBuilder(Certificate issuer, Certificate subject) {
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 * @param issuer a binary certificate for the issuer
	 * @param subject a binary certificate for the subject
	 */
	public EndorsementBuilder(File issuer, File subject) {
		// TODO Auto-generated constructor stub
	}

	public Certificate getIssuer() {
		return issuer;
	}

	public void setIssuer(Certificate issuer) {
		this.issuer = issuer;
	}
	
	public void setIssuer(File issuer) {
	}

	public Certificate getSubject() {
		return subject;
	}

	public void setSubject(Certificate subject) {
		this.subject = subject;
	}
	
	public void setSubject(File subject) {
	}
	
	public Endorsement getEndorsement() {
		return endorsement;
	}
	/**
	 * 
	 * @param endorsement
	 */
	public void setEndorsement(Endorsement endorsement) {
		this.endorsement = endorsement;
	}
	/**
	 * 
	 * @param endorsement a JSON file with the fields of the endorsement described.
	 */
	public void setEndorsement(File endorsement) {
	}

	@Override
	public byte[] build(PrivateKey privateKey) throws BuilderException, KeyException {
		// TODO Auto-generated method stub
		return null;
	}
}
