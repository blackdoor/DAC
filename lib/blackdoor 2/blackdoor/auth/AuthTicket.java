/**
 * 
 */
package blackdoor.auth;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import blackdoor.crypto.HistoricSHE;
import blackdoor.util.Misc;
import blackdoor.util.Watch;

/**
 * @author nfischer3
 *
 */
public class AuthTicket implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 504276381469119124L;
	private String userName;
	private Watch issueTime;//CAUTION:: tickets opened in different time zones than they were created in will show the different time zone but not compensate for the offset
	private long duration; //in minutes
	private byte serviceID;
	private InetAddress userIP;
	
	public static final int serialSize = 4 + 8 + 4;
	
	/**
	 * Create a new AuthTicket with the issue time set to the current time.
	 * @param userName
	 * @param duration The validity period for this ticket in minutes.
	 * @param userIP
	 * @param serviceID
	 */
	public AuthTicket(String userName, long duration,
			InetAddress userIP, byte serviceID) {
		super();
		this.userName = userName;
		this.issueTime = new Watch();
		this.duration = duration;
		this.userIP = userIP;
		this.serviceID = serviceID;
	}
	/**
	 * Create a new AuthTicket.
	 * @param userName
	 * @param issueTime The time this ticket was issued. The ticket will expire duration minutes from issueTime. CAUTION:: tickets opened in different time zones than they were created in will show the different time zone but not compensate for the offset!!
	 * @param duration The validity period for this ticket in minutes.
	 * @param userIP
	 * @param serviceID
	 */
	public AuthTicket(String userName, Watch issueTime, long duration,
			InetAddress userIP, byte serviceID) {
		super();
		this.userName = userName;
		this.issueTime = issueTime;
		this.duration = duration;
		this.userIP = userIP;
		this.serviceID = serviceID;
	}
	
	/**
	 * Open an AuthTicket using the given key
	 * @param key
	 * @param ticket
	 */
	public AuthTicket(byte[] key, byte[] ticket){
		//byte b = -1;
		ticket = decrypt(key, ticket);
		//System.out.println(Misc.bytesToHex(key));
		//System.out.println("plaintextticket " + Misc.bytesToHex(ticket));
		int index = 0;
		int issueHours = ticket[index++];// = (byte) issueTime.getHours();
		int issueMinutes = ticket[index++];// = (byte) issueTime.getMinutes();
		Watch issueTime = new Watch();
		
		issueTime.setHours(issueHours);
		issueTime.setMinutes(issueMinutes);
		//System.out.println(issueHours + ":" + issueMinutes);
		this.issueTime = issueTime;
		//put the ticket validity duration in the array
		byte[] duration = new byte[8];
		for(int i = 0; i <duration.length; i++){
			duration[i] = ticket[index++];// = duration[i];
		}
		this.duration = ByteBuffer.wrap(duration).getLong();
		serviceID =ticket[index++];// = serviceID;
		byte[] address = new byte[4];
		for(int i = 0; i < address.length; i++){
			address[i] = ticket[index++];// = userIP.getAddress()[i];
		}
		try {
			userIP = InetAddress.getByAddress(address);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.userName = new String(Arrays.copyOfRange(ticket, index, ticket.length-1), StandardCharsets.US_ASCII);
	}
	
	/**
	 * gets this in the form (byte)<issueHour>:(byte)<issueMinute>:(double)duration:(byte)serviceID:(byte[4])userIP:String userName encrypted with key
	 * @param key
	 * @return
	 */
	public byte[] generate(byte[] key){
		HistoricSHE cipher = new HistoricSHE();
		byte[] IV = cipher.init(key);
		System.out.println("IV: " + Misc.bytesToHex(IV));
		return new HistoricSHE.EncryptionResult(IV, cipher.doFinal(getPlainTicketText())).simpleSerial();
	}
	
	private byte[] decrypt(byte[] key, byte[] ticket){
		HistoricSHE cipher = new HistoricSHE();
		HistoricSHE.EncryptionResult result = new HistoricSHE.EncryptionResult(ticket);
		cipher.init(result.getIv(), key);
		return cipher.doFinal(result.getText());
		
		
		
		
//		SHE cipher = new SHE();
//		SHE.EncryptionResult ticketWrapper = new SHE.EncryptionResult(ticket);
//		System.out.println(ticketWrapper);
//		cipher.init(ticketWrapper.getIv(), key);
//		byte[] out = cipher.doFinal(ticket);
//		System.out.println("out:"+Misc.bytesToHex(out));
//		return out;
	}
	
	private byte[] getPlainTicketText(){
		int index = 0;
		byte[] userName = this.userName.getBytes(StandardCharsets.US_ASCII);
		byte[] ticket = new byte[serialSize + userName.length];
		byte[] duration = ByteBuffer.allocate(8).putLong(this.duration).array();
		ticket[index++] = (byte) issueTime.getHours();
		ticket[index++] = (byte) issueTime.getMinutes();
		//put the ticket validity duration in the array
		for(int i = 0; i <duration.length; i++){
			ticket[index++] = duration[i];
		}
		ticket[index++] = serviceID;
		for(int i = 0; i < userIP.getAddress().length; i++){
			ticket[index++] = userIP.getAddress()[i];
		}
		for(int i = 0; i < userName.length; i++){
			ticket[index++] = userName[i];
		}
		System.out.println(Misc.bytesToHex(ticket));
		return ticket;
	}

	@Override
	public String toString() {
		return "AuthTicket [userName=" + userName + ", issueTime=" + issueTime
				+ ", duration=" + duration + ", serviceID=" + serviceID
				+ ", userIP=" + userIP + "]";
	}

}
