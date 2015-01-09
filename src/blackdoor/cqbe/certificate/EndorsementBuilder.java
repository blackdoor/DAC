package blackdoor.cqbe.certificate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyException;
import java.security.PrivateKey;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.protobuf.ByteString;

import blackdoor.cqbe.certificate.CertificateProtos.Certificate;
import blackdoor.cqbe.certificate.CertificateProtos.Endorsement;
import blackdoor.cqbe.certificate.CertificateProtos.Extension;

/**
 * An endorsement builder. Given two certificates and some endorsement settings, an endorsement can be composed and signed.
 * @author nfischer3
 *
 */
public class EndorsementBuilder implements Builder{
	
	private Certificate issuer;
	private Certificate subject;
	private Endorsement endorsement;
	private blackdoor.cqbe.certificate.CertificateProtos.Endorsement.Builder builder;
	/**
	 * Create a new un-configured EndorsementBuilder object. Before this object is used the fields of the certificate will need to be set.
	 */
	public EndorsementBuilder() {
		// TODO Auto-generated constructor stub
		builder = Endorsement.newBuilder();
	}
	
	/**
	 *
	 * @param issuer
	 * @param subject
	 */
	public EndorsementBuilder(Certificate issuer, Certificate subject) {
		// TODO Auto-generated constructor stub
		setIssuer(issuer);
		setSubject(subject);
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
		builder.setIssuerUniqueId(issuer.getSubjectUniqueId());
		//builder.setIssuerSystem(issuer.getIssuer) where do i get this from??
		builder.setIssuerCommonName(issuer.getSubjectCommonName());
		//
		
	}
	
	public void setIssuer(File issuer) {
	CertificateBuilder certificate_builder = new CertificateBuilder(issuer);
	setIssuer(certificate_builder.getCertObject());
	}

	public Certificate getSubject() {
		return subject;
	}

	public void setSubject(Certificate subject) {
		builder.setSubjectUniqueId(subject.getSubjectUniqueId());
		builder.setSubjectCommonName(subject.getSubjectCommonName());
		//finish fields (protocol versions, timeframe, signatures...)
		builder.setSubjectKeyInfo(subject.getSubjectKeyInfo());
		
	}
	
	public void setSubject(File subject) {
	    CertificateBuilder certificate_builder = new CertificateBuilder(subject);
	    setSubject(certificate_builder.getCertObject());
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
	public void setEndorsement(File endorsement){
		try {
    	FileInputStream file = new FileInputStream(endorsement);
    	JSONObject endorsement_json = (JSONObject) new JSONTokener(file).nextValue();
    	setEndorsement(endorsement_json);
      } catch (FileNotFoundException e) {
	        System.out.println("File not found");
	        e.printStackTrace();
      }
}
	public void setEndorsement(JSONObject json) {
		String issuer_unique_id = json.optString("issuer_unique_id");
		builder.setIssuerUniqueId(issuer_unique_id);
		if (json.has("issuer_system")){
			String issuer = json.optString("issuer_system");
			builder.setIssuerSystem(issuer);
		}
		String subject_unique_id = json.optString("subject_unique_id");
		builder.setIssuerUniqueId(subject_unique_id);
		JSONObject subject_key_info = json.optJSONObject("subject_key_info");
		builder.setSubjectKeyInfo(CertificateBuilder.public_key_parse(subject_key_info));
		if (json.has("validity")){
			JSONObject validity = json.optJSONObject("validity");
			builder.setValidity(CertificateBuilder.time_frame_parse(validity));
		}
		if (json.has("revocation_info")) {
			JSONObject revocation_info = json.optJSONObject("revocation_info");
			builder.setRevocationInfo(CertificateBuilder.revocation_parse(revocation_info));
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
				Extension extension = CertificateBuilder.extension_parse(extension_json);
				builder.setEndorsementExtensions(i, extension);
			}	
		}
	}

	@Override
	public byte[] build(PrivateKey privateKey) throws BuilderException, KeyException {
		// TODO Auto-generated method stub
		return null;
	}
}
