package blackdoor.cqbe.certificate;

import java.io.File;
import java.security.KeyException;
import java.security.PrivateKey;

import blackdoor.cqbe.certificate.CertificateProtos.Certificate;

public class CertificateBuilder implements Builder {

	/**
	 * Create a new un-configured CertificateBuilder object. Before this object is used the fields of the certificate will need to be entered either by setting the certificate object, or by pointing to a file that has the certificate settings in it.
	 */
	public CertificateBuilder() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * Create a new Certificate Builder that is ready to build a self signed certificate based on the fields in certificateDescriptor.
	 * @param certificateDescriptor a JSON file that describes the field content for the certificate being built.
	 */
	public CertificateBuilder(File certificateDescriptor) {
		// TODO Auto-generated constructor stub
	}
	/**
	 * Create a new CertificateBuilder that is ready to sign and build a certificate based on certificate.
	 * @param certificate 
	 */
	public CertificateBuilder(Certificate certificate) {
		// TODO Auto-generated constructor stub
	}

	/**
	 * If one of the parameterized constructors was not used to build this object then configuration will need to be set with this method before build is called.
	 * @param certificate the certificate object to build from.
	 */
	public void setCertObject(Certificate certificate){
		
	}
	/**
	 * If one of the parameterized constructors was not used to build this object then configuration will need to be set with this method before build is called.
	 * @param certificateDescriptor a JSON file that describes the field content for the certificate to be built.
	 */
	public void setCertObject(File certificateDescriptor){
	}
	/**
	 * Retrieves the Certificate object that will be used to build the certificate when build is called.
	 * @return the Certificate object that will be used to build the certificate when build is called.
	 */
	public Certificate getCertObject(){
		return null;
	}

	
	/**
	 * Build and sign a certificate with privateKey. The certificate configuration needs to be set before this method is called.
	 * @param privateKey the private key to sign this certificate with, must match the key information on the certificate.
	 * @return TODO a file or array of bytes or something that represents the certificate.
	 * @throws KeyException thrown if privateKey does not match the public key in the certificate configuration
	 * @throws BuilderException
	 */
	@Override
	public Object build(PrivateKey privateKey) throws BuilderException, KeyException {
		// TODO Auto-generated method stub
		return null;
	}

}
