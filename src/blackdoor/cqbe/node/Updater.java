package blackdoor.cqbe.node;

public class Updater {

	
	public Updater() {
		
	}

	/**
	 * Timer function that tells Updater when to check for updates
	 */
	public void updateTimer(){
		
	}
	
	/**
	 * Checks to see if any updates are needed
	 * @return Yes if update needed, no otherwise
	 */
	public Boolean checkForUpdates(){
		return null;
	}
	
	/**
	 * Checks for needed updates then calls appropriate helper functions to update the node
	 */
	public void update(){
		
	}
	
	/**
	 * Checks for a certificate signing request
	 * Calls updateStorage if needed
	 */
	public void checkForCert(){
		
	}
	
	/**
	 * Updates storage in the node if needed 
	 * @param something to be stored
	 */
	public void updateStorage(String something){
		
	}
	
	/**
	 * Pings neighbors, returning list of neighbors that did not respond to ping
	 * @return List of neighbors that did not respond.
	 */
	public String[] pingNeighbors(){
		return null;
	}
	
	/**
	 * Similar to pingNeighbors, but pings the members of the optional complement table
	 * @return List of nonresponsive complements 
	 */
	public String[] pingComplement(){
		return null;
	}
	
	/**
	 * Updates list of neighbors if it is detected that there has been an issue.
	 */
	public void updateNeighbors(){
		
	}
	
	/**
	 * Updates the list of complements if needed
	 */
	public void updateComplement(){
		
	}
}
