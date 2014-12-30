package blackdoor.cqbe.certificate;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.KeyException;
import java.security.PrivateKey;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import blackdoor.cqbe.certificate.CertificateProtos.Certificate;
import blackdoor.cqbe.certificate.CertificateProtos.Extension;
import com.google.protobuf.ByteString;

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
		builder = Certificate.newBuilder();
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
	    //builder.setCertificateEndorsements(....)
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
	
	public CertificateProtos.TimeFrame time_frame_parse(JSONObject json){
		CertificateProtos.TimeFrame.Builder revocation_builder = CertificateProtos.TimeFrame.newBuilder();
		return revocation_builder.build();
	}
	
	public CertificateProtos.Revocation revocation_parse(JSONObject json){
		CertificateProtos.Revocation.Builder revocation_builder = CertificateProtos.Revocation.newBuilder();
 		int revocation_date = json.optInt("revocation_date");
 		revocation_builder.setRevocationReason(revocation_date);
 		int revocation_reason = json.optInt("revocation_reason");
		revocation_builder.setRevocationDate(revocation_reason);
		return revocation_builder.build();
	}
	
	public CertificateProtos.PublicKeyInfo public_key_parse(JSONObject json){
		CertificateProtos.PublicKeyInfo.Builder public_key_builder = CertificateProtos.PublicKeyInfo.newBuilder();
		String public_key = json.optString("public_key");
		ByteString pk_bytes = ByteString.copyFromUtf8(public_key);
		public_key_builder.setPublicKey(pk_bytes);
		String hash = json.optString("hash_algorithm");
		public_key_builder.setHashAlgorithm(hash);
		String signing_algorithm = json.optString("signing_algorithm");
		public_key_builder.setSigningAlgorithm(signing_algorithm);
		return public_key_builder.build();
	}
	
	
	public CertificateProtos.Endorsement endorsement_parse(JSONObject json){
		CertificateProtos.Endorsement.Builder endorsement_builder = CertificateProtos.Endorsement.newBuilder();
		String issuer_unique_id = json.optString("issuer_unique_id");
		endorsement_builder.setIssuerUniqueId(issuer_unique_id);
		if (json.has("issuer_system")){
			String issuer = json.optString("issuer_system");
			endorsement_builder.setIssuerSystem(issuer);
		}
		String subject_unique_id = json.optString("subject_unique_id");
		endorsement_builder.setIssuerUniqueId(subject_unique_id);
		JSONObject subject_key_info = json.optJSONObject("subject_key_info");
		builder.setSubjectKeyInfo(public_key_parse(subject_key_info));
		if (json.has("validity")){
			JSONObject validity = json.optJSONObject("validity");
			endorsement_builder.setValidity(time_frame_parse(validity));
		}
		if (json.has("revocation_info")) {
			JSONObject revocation_info = json.optJSONObject("revocation_info");
			builder.setRevocationInfo(revocation_parse(revocation_info));
			}
		if (json.has("protocol_version")) {
			builder.setProtocolVersion(json.optInt("protocol_version"));
		}
		if (json.has("subject_common_name")){	
			builder.setSubjectCommonName(json.optString("subject_common_name"));
		}
		if (json.has("issuer_common_name")){	
			builder.setSubjectCommonName(json.optString("issuer_common_name"));
		}
		if (json.has("Extensions")){
			JSONObject certificate_extensions = json.getJSONObject("certificate_extensions");
			JSONArray extensions = new JSONArray(certificate_extensions);
			for (int i = 0; i < extensions.length(); i++){
				JSONObject extension_json = extensions.getJSONObject(i);
				Extension extension = extension_parse(extension_json);
				builder.setCertificateExtensions(i, extension);
			}	
		}
		return endorsement_builder.build();
	}
	
	public CertificateProtos.Extension extension_parse(JSONObject json){
		CertificateProtos.Extension.Builder extension_builder = CertificateProtos.Extension.newBuilder();
		String name = json.optString("name");
 		extension_builder.setName(name);
		return extension_builder.build();
		//TODO - parse extensions?
	}
	
	
	public void setCertObject(File certificateDescriptor) throws IOException{
	//file --> JSON object
	    try {
	    	FileInputStream file = new FileInputStream(certificateDescriptor);
	    	JSONObject certificate = (JSONObject) new JSONTokener(file).nextValue();
			builder.setSubjectUniqueId(certificate.optString("subject_unique_id"));
			//subject key feilds
			JSONObject public_key_info = certificate.optJSONObject("subject_key_info");
			builder.setSubjectKeyInfo(public_key_parse(public_key_info));
			if (certificate.has("revocation_info")) {
				JSONObject revocation_info = certificate.optJSONObject("revocation_info");
				builder.setRevocationInfo(revocation_parse(revocation_info));
				}
	 		if (certificate.has("version")) {
	 			builder.setVersion(certificate.optInt("version"));
	 			}
			if (certificate.has("protocol_version")) {
				builder.setProtocolVersion(certificate.optInt("protocol_version"));
			}
			if (certificate.has("validity")){
				JSONObject validity = certificate.optJSONObject("validity");
				builder.setValidity(time_frame_parse(validity));
			}
			if (certificate.has("serial_number")){
				builder.setSerialNumber(certificate.optString("serial_number"));
			}
			if (certificate.has("subject_common_name")){	
				builder.setSubjectCommonName(certificate.optString("subject_common_name"));
			}
			if (certificate.has("Extensions")){
				JSONObject certificate_extensions = certificate.getJSONObject("certificate_extensions");
				JSONArray extensions = new JSONArray(certificate_extensions);
				for (int i = 0; i < extensions.length(); i++){
					JSONObject extension_json = extensions.getJSONObject(i);
					Extension extension = extension_parse(extension_json);
					builder.setCertificateExtensions(i, extension);
				}
				
			}
			String signature = certificate.optString("public_key");
			ByteString signature_bytes = ByteString.copyFromUtf8(signature);
			builder.setSignature(signature_bytes);
			
	      } catch (FileNotFoundException e) {
		        System.out.println("File not found");
		        e.printStackTrace();
	      }
	    
	}
	    
	/**
	 * Retrieves the Certificate object that will be used to build the certificate when build is called.
	 * @return the Certificate object that will be used to build the certificate when build is called.
	 */
	public Certificate getCertObject(){
		return builder.build();
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
		File Json = new File("C:\\Users\\annakocer\\git\\DAC\\src\\blackdoor\\cqbe\\certificate\\certTest.JSON");
		builder2.setCertObject(Json);
		Certificate cert = builder2.getCertObject();
		cert.getSubjectUniqueId();
		CertificateBuilder builder3 = new CertificateBuilder(cert);

	}
	}
