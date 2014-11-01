package blackdoor.cqbe.certificate;

/**
 * 
 * A factory for certificates or endorsements. 
 * @author nfischer3
 *
 */
public interface Builder {
	
	/**
	 * Based on the settings for this builder, build an object.
	 * @return a built endorsement or certificate.
	 * @throws Exception if not all settings for this builder are present or valid.
	 */
	public Object Build() throws BuilderException;
	
}
