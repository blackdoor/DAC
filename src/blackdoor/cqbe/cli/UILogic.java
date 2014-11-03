package blackdoor.cqbe.cli;

public class UILogic {

	public UILogic() {
		// TODO Auto-generated constructor stub
	}

	/**
 	* creates the dart network and sets up the listener and address table
 	*
 	* @param  port the port number to use
 	*/
	public create(port) {
	}

	/**
 	* joins the dart network and sets up the listener and address table
 	*
 	* @param  ip address  the address for the node you know
 	* @param  port the port number of the node you know
 	*/
	public join(ip, port) {
	}

	/**
 	* Collects signatures and information from reliable sources outside the DART 
 	* network on a certificate.
 	*
 	* @param  certid  the the cert to agree on 
 	*/
	public agree(certid) {
	}

	/**
 	* Used to sign any arbitrary file stored locally, or to sign a 
 	* certificate in the network, with the user’s stored private key.
 	*
 	* @param  certid or file  the the cert to sign 
 	* @param  keystore  the private-key to sighn with
 	*/
	public sign(certid or file, keystore) {
	}

	/**
 	* The user can choose to revoke any certificate they created.
 	*
 	* @param  certid  the the cert to agree on 
 	* @param  keystore  the private-key to sighn with
 	*/
	public revoke(certid, keystore) {
	}

	/**
 	* Used to see the specifics of the node’s operations. 
 	* The number of connections in the address table or the storage used for this node’s connections. 
 	*
 	* @param  flags  what to get the status of
 	*/
	public status(flags) {
	}

	/**
 	* Finds a specific certificate in the network, and saves it to the location. 
 	*
 	* @param  certid  the cert to retrieve
 	* @param  location  a location on the local drive
 	*/
	public retrieve(certid, location) {
	}

	/**
 	* verify a certificate based on the options provided
 	*
 	* @param  certid  the the cert to verify
 	* @param  options  the verifying options
 	*/
	public verify(certid, options) {
	}

	/**
 	* Insert certfile(s) into the network
 	*
 	* @param  cert  the cert to insert
 	* @param  options  the insert options
 	*/
	public insert(cert, options) {
	}

	/**
 	* Used to exit the network and to cease all DART node functions.
 	*
 	*/
	public leave() {
	}
}
