package blackdoor.cqbe.node;

public class NodeException extends Exception {

	public static class RequiredParametersNotSetException extends NodeException{
		
	}
	
	public static class CantGetAddress extends NodeException{
		
	}
}