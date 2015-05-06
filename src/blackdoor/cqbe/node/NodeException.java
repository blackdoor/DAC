package blackdoor.cqbe.node;

/**
 * Custom Exceptions for The Node Singleton.
 * <p>
 * 
 * @author Yuryi Kravtsov
 * @version v1.0.0 - May 4, 2015
 */
public class NodeException extends Exception {

	public static class RequiredParametersNotSetException extends NodeException {

	}

	public static class CantGetAddress extends NodeException {

	}
}