package blackdoor.cqbe.certificate;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.KeyException;
import java.security.PrivateKey;
import java.util.Scanner;

import org.json.JSONObject;

import com.google.protobuf.ByteString;

import blackdoor.cqbe.certificate.CertificateProtos.Certificate;

public class CertificateBuilder implements Builder {

	private blackdoor.cqbe.certificate.CertificateProtos.Certificate.Builder builder;
	/**
	 * Create a new un-configured CertificateBuilder object. Before this object is used the fields of the certificate will need to be entered either by setting the certificate object, or by pointing to a file that has the certificate settings in it.
	 */
	public CertificateBuilder() {
		// TODO Auto-generated constructor stub
		builder = Certificate.newBuilder();
	}
	/**
	 * Create a new Certificate Builder that is ready to build a self signed certificate based on the fields in certificateDescriptor.
	 * @param certificateDescriptor a JSON file that describes the field content for the certificate being built.
	 */
	public CertificateBuilder(File certificateDescriptor) {
		CertificateBuilder certificate = new CertificateBuilder();
		try {
			certificate.setCertObject(certificateDescriptor);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Create a new CertificateBuilder that is ready to sign and build a certificate based on certificate.
	 * @param certificate 
	 */
	public CertificateBuilder(Certificate certificate) {
		setCertObject(certificate);
	}

	/**
	 * If one of the parameterized constructors was not used to build this object then configuration will need to be set with this method before build is called.
	 * @param certificate the certificate object to build from.
	 */
	public void setCertObject(Certificate certificate){
		builder.setSubjectUniqueId(certificate.getSubjectUniqueId());
		builder.setSubjectKeyInfo(certificate.getSubjectKeyInfo());
		if (certificate.hasRevocationInfo()) {
			builder.setRevocationInfo(certificate.getRevocationInfo());
		}
		if (certificate.hasVersion()) {
		builder.setVersion(certificate.getVersion());
		}
		if (certificate.hasProtocolVersion()) {
		builder.setVersion(certificate.getProtocolVersion());
		}
	    //builder.setCertificateExtensions(....)
        if (certificate.hasValidity()){
        	builder.setValidity(certificate.getValidity());
        }
        if (certificate.hasSerialNumber()){
        	builder.setSerialNumber(certificate.getSerialNumber());
        }
		if (certificate.hasSubjectCommonName()) {
			builder.setSubjectCommonName(certificate.getSubjectCommonName());
		}
        builder.setSignature(certificate.getSignature());
        //builder.setCertificateExtensions(index, value);
	}

	/**
	 * If one of the parameterized constructors was not used to build this object then configuration will need to be set with this method before build is called.
	 * @param certificateDescriptor a JSON file that describes the field content for the certificate to be built.
	 */
	
	//converts a string to byte array
	public ByteString stringToByte(String string){
		ByteBuffer bb = ByteBuffer.wrap(string.getBytes());
		return ByteString.copyFrom(bb);
	}
	
	public void setCertObject(File certificateDescriptor) throws IOException{
		//file --> JSON object
		    try {
		        JSONObject certificate = new JSONObject(builder.mergeFrom(new FileInputStream(certificateDescriptor)));
		//String content = new Scanner(certificateDescriptor).useDelimiter("//Z").next();
		//JSONObject certificate = new JSONObject(content);
		//builder.setSubjectUniqueId(certificate.optString("subject_unique_id");
		//builder.setSubjectKeyInfo(certificate.optString("subject_key_info"));
		//if (certificate.has("revocation_info") {
		//builder.setRevocationInfo(certificate.opt("revocation_info"));
		//}
		if (certificate.has("version")) {
		builder.setVersion(certificate.optInt("version"));
		}
		if (certificate.has("protocol_version")) {
		builder.setProtocolVersion(certificate.optInt("protocol_version"));
		}
	    //builder.setCertificateExtensions(i, certificate.getCertificateExtensionsList());
        //if (certificate.has("validity"){
        	//builder.setValidity(certificate.get("validity");
        //}
        if (certificate.has("serial_number")){
        	builder.setSerialNumber(certificate.optString("serial_number"));
        }
		if (certificate.has("subject_common_name")){	
			builder.setSubjectCommonName(certificate.optString("subject_common_name"));
		}
		      } catch (FileNotFoundException e) {
			        System.out.println("File not found");
			      }
	}

       /** builder.setSignature(ByteString(certificate.optString("signature")));
        //builder.setCertificateExtensions(index, value);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();/**
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
	 tstr* @throws BuilderException
	 */
	@Override
	public byte[] build(PrivateKey privateKey) throws BuilderException, KeyException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String[] args) throws Exception{
		CertificateBuilder builder2 = new CertificateBuilder();
		System.out.println("building");

	}
	}
