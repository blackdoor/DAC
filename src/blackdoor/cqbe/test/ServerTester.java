package blackdoor.cqbe.test;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.json.JSONObject;

import blackdoor.cqbe.addressing.Address;
import blackdoor.cqbe.addressing.L3Address;
import blackdoor.cqbe.rpc.PingRpc;
import blackdoor.cqbe.rpc.RPCBuilder;
import blackdoor.cqbe.rpc.RPCException;
import blackdoor.cqbe.rpc.Rpc;
import blackdoor.cqbe.rpc.Rpc.Method;
import blackdoor.util.DBP;

public class ServerTester {

	private String hostname;
	private int port;
	private String desthost;
	private int destport;
	private Socket csock = null;

	public static void main(String[] args) {
		for (int i = 0; i++ < 100;) {
			ServerTester ts = new ServerTester();
			ts.sendJSON(ts.getActualJSONRPC());
			// ts.closeOut();
			String res = ts.read();
			System.out.println(i + " " + res);
		}
	}

	public ServerTester() {
		this.port = 1778;
		hostname = "localhost";
		grabSocket();
	}

	public void grabSocket() {
		try {
			csock = new Socket(hostname, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeOut() {
		try {
			csock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendRpc(Rpc jo) {
		String input = jo.toString();
		PrintWriter out;
		try {
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					csock.getOutputStream())));
			out.print(input);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendJSON(JSONObject jo) {
		String input = jo.toString();
		PrintWriter out;
		try {
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					csock.getOutputStream())));
			out.print(input);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 */
	private String read() {
		byte[] buffer = new byte[1024];
		int count = 0;
		try {
			count = csock.getInputStream().read(buffer);
		} catch (IOException e) {
			DBP.printerror("Problem reading from InputStream...");
			DBP.printException(e);
			return null;
		}
		if (count > 0)
			return new String(buffer).substring(0, count);
		else
			return null;
	}

	public Rpc getActualRPC() {
		RPCBuilder builder = new RPCBuilder();
		builder.setDestinationO(Address.getFullAddress());
		builder.setSource(new L3Address(InetAddress.getLoopbackAddress(), port));
		builder.setIndex(2);
		PingRpc pingrpc = builder.buildPingObject();
		return pingrpc;
	}

	public JSONObject getActualJSONRPC() {
		RPCBuilder builder = new RPCBuilder();
		JSONObject obj = null;
		try {

			builder.setSourceIP(InetAddress.getByName(hostname));
			builder.setSourcePort(port);

			builder.setDestinationO(new L3Address(InetAddress
					.getByName(desthost), destport));
			builder.setIndex(1);
			obj = builder.buildLOOKUP();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RPCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

	public JSONObject getStubRPC() {
		JSONObject rpc = new JSONObject();
		rpc.put("jsonrpc", "2.0");
		rpc.put("method", "TESTER");
		rpc.put("id", 99999999);
		return rpc;
	}
}
