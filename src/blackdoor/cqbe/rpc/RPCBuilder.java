package blackdoor.cqbe.rpc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.settings.Config;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * 
 * @author Cj Buresch
 * @version v0.0.1 - Nov 3, 2014
 */
public class RPCBuilder {

	public RPCBuilder() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Initializies a GET RPC JSON request to be sent.
	 * <p>
	 * 
	 * @parem ?
	 * @parem ??
	 * @parem ???
	 * @return JSON Object with relevant information.
	 */
	public void buildGET() {
	}

	/**
	 * Initializies a PUT RPC JSON request to be sent.
	 * <p>
	 * @param file 
	 * 
	 * @parem ?
	 * @parem ??
	 * @parem ???
	 * @return JSON Object.
	 */
	public JSONObject buildPUT(File file) {
    return null;
	}
	
	/**
	 * Initializies a LOOKUP RPC JSON request to be sent.
	 * <p>
	 * 
	 * @parem ?
	 * @parem ??
	 * @parem ???
	 * @return JSON Object with relevant information.
	 */
	public JSONObject buildLOOKUP() {
		String ipAddress = Config.getAddress();
		String port = Config.getPort();
		Address overlayAddress = new Address(ipAddress,port);
		JSONObject lookupObject = new JSONObject();
		lookupObject.put("call_name", "LOOKUP");
		lookupObject.put("source_overlay_address", overlayAddress.getOverlay());
		lookupObject.put("source_ip_address", ipAddress);
		lookupObject.put("source_port", port);
		//Should buildLOOKUP be passed the destination overlay address or is that done elsewhere?
		
		return lookupObject;
	}

	/**
	 * Initializies a PING RPC JSON request to be sent.
	 * <p>
	 * 
	 * @parem ?
	 * @parem ??
	 * @parem ???
	 * @return JSON Object with relevant information.
	 */
	public void buildPING() {
	}
	
	/**
	 * Initiliazes a SHUTDOWN RPC JSON request to be sent
	 *
	 * @param port - Port to be shutdown
	 */
	public JSONObject buildSHUTDOWN(int portToShutdown) {
		String ipAddress = Config.getAddress();
		String port = Integer.toString(portToShutdown);
		Address overlayAddress = new Address(ipAddress,port);
		JSONObject shutdownObject = new JSONObject();
		shutdownObject.put("call_name", "LOOKUP");
		shutdownObject.put("source_overlay_address", overlayAddress.getOverlay());
		shutdownObject.put("source_ip_address", ipAddress);
		shutdownObject.put("source_port", port);
		//Is there a destination overlayAddress?
		
		return shutdownObject;
	}
	public JSONObject buildGETendorsement(String subjectUID, String issuer) {
	    return null;
	}

  public static byte[] getBinary(JSONObject enorceJSON) {
    // TODO Auto-generated method stub
    return null;
  }

  public JSONObject buildGETendorsementList(String subjectUID) {
    // TODO Auto-generated method stub
    return null;
  }

  public JSONObject buildGETcertificate(String subjectUID) {
    // TODO Auto-generated method stub
    return null;
  }
}
