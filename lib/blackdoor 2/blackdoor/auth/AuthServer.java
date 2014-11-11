/**
 * 
 */
package blackdoor.auth;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

import blackdoor.auth.AuthRequest.Operation;
import blackdoor.crypto.Hash;

/**
 * @author kAG0
 * 
 */
@Deprecated //See the Portunes project for more features and an SQL user database
public class AuthServer {
	private int port = 1234;
	private ServerSocket serverSocket;
	private boolean running = true;
	private AuthManager authManager;
	public static final String greeting = "hello server";

	/**
	 * @param args
	 *            valid args are -port port#, -db userDB-file-location
	 */
	public static void main(String[] args) {
		AuthServer server = new AuthServer();

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-port")) {
				server.setPort(Integer.parseInt(args[++i]));
			} else if (args[i].equalsIgnoreCase("-db")) {
				server.createManager(args[++i]);
			} else if(args[i].equalsIgnoreCase("-help")){
				System.out.println(
"	-db <database>	set database for the server to reference.");
				System.out.println(
"	-port <port number>	");
				System.out.println(
"					set local port for the server to listen on.");
			} else
				System.err.println("invalid argument:" + args[i]);
		}

		server.start();

	}

	AuthServer() {
	}
	/**
	 * creates a new authentication manager with new database and default settings
	 */
	public void createManager() {
		authManager = new AuthManager();
	}
	/**
	 * creates a new authentication manager with database from specified file
	 * @param DBFile
	 */
	public void createManager(String DBFile) {
		try {
			authManager = new AuthManager(DBFile);
		} catch (IOException e) {
			System.err.println("specified file is corrupted or not a UserDB");
			e.printStackTrace();
			System.out.println("server will now quit.");
			System.exit(-1);
		}
	}
	/**
	 * start the server
	 * will listen on port 1234 if a port has not been specified already
	 */
	public void start() {
		if (authManager == null) {
			authManager = new AuthManager();
		}
		CLI cli = new CLI(this);
		cli.start();
		listen(port);
		acceptConnections();
	}
	
	/**
	 * save userDB to a file
	 * @param location location to save file
	 */
	public void save(String location) {
		try {
			authManager.save(location);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.err.println("save location not valid, database not saved");
		}
	}

	private void acceptConnections() {
		Socket socket = null;
		while (running) {
			try {
				socket = serverSocket.accept();
				System.out.println("connection accepted from "
						+ socket.getRemoteSocketAddress());
			} catch (IOException e) {
				System.err.println("Unable to accept connection on port "
						+ port);
				e.printStackTrace();
			}
			AuthConnectionHandler handler = new AuthConnectionHandler(socket,
					authManager);
			handler.start();
		}
	}

	private void listen(int port) {
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("listenting on port: " + port + ".");
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + port + ".");
			// e.printStackTrace();
			System.err.println("Server will now quit.");
			System.exit(-1);
		}
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * @param running
	 *            the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}
	/**
	 * a command line interface for the server, supports save and exit commands
	 * @author kAG0
	 *
	 */
	class CLI extends Thread {
		AuthServer server;

		CLI(AuthServer server) {
			this.server = server;
		}

		public void run() {
			Scanner keyboard = new Scanner(System.in);
			String input;
			System.out.println("exit to quit, save to save");
			while (true) {
				input = keyboard.nextLine();
				if (input.equalsIgnoreCase("exit")) {
					server.setRunning(false);
					keyboard.close();
					System.exit(1);
				} else if (input.equalsIgnoreCase("save")) {
					System.out.println("enter location to save DB");
					String location = keyboard.nextLine();
					System.out.println(location);
					server.save(location);
				} else
					System.out.println("Invalid Command");
			}
		}
	}
	
	/**
	 * a multi-threaded connection handler for incoming connections
	 * @author kAG0
	 *
	 */
	class AuthConnectionHandler extends Thread {
		private Socket socket;
		private AuthManager manager;
		// private OutputStream outputBuffer;
		private ObjectOutput outputObject;
		private InputStream inputBuffer;
		private ObjectInput inputObject;

		AuthConnectionHandler(Socket socket, AuthManager manager) {
			this.socket = socket;
			this.manager = manager;
		}

		public void run() {
			//System.out.println("herp derp, I should reply");
			openSocketInput();
			openSocketOutput();
			if(recieveGreeting()){
				try {
					byte[] challenge = sendChallenge();
					AuthRequest request = recieveRequest();
					boolean operationCompleted = false;
					if(request == null) throw new IOException("Request not recieved.");
					switch (request.getOperation()) {
					case ADD:
						operationCompleted = manager.addUser(request.getUserName(),
								request.getPasswordHash(), request.getRights(),
								request.getAuthUserName(),
								request.getAuthPasswordHash(), challenge);
						break;
					case CHANGENAME:
						operationCompleted = manager.changeUserName(
								request.getUserName(), request.getNewUserName(),
								request.getAuthUserName(),
								request.getAuthPasswordHash(), challenge);
						break;
					case CHANGEPASSWORD:
						operationCompleted = manager.changePassword(
								request.getUserName(), request.getPasswordHash(), challenge,
								request.getNewPasswordHash());
						break;
					case CHECK:
						operationCompleted = manager.checkUser(request.getUserName(),
								request.getPasswordHash(), challenge);
						break;
					case REMOVE:
						operationCompleted = manager.removeUser(request.getUserName(),
								request.getAuthUserName(),
								request.getAuthPasswordHash(), challenge);
						break;
					}
					AuthReply reply = new AuthReply(operationCompleted,
							request.getID(), request.getOperation());
					// openSocketOutput();
					sendReply(reply);
					closeSocket();
				} catch (IOException e) {
					System.err.print(e);
					//e.printStackTrace();
				}
			} else{
				try {
					closeSocket();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		private boolean recieveGreeting(){
			try {
				String greeting = (String) inputObject.readObject();
				return greeting.equalsIgnoreCase(AuthServer.greeting);
			} catch (ClassNotFoundException | IOException e) {
				System.err.println("Probelm encountered with greeting.");
				return false;
			}
			
		}
		
		private byte[] sendChallenge() throws IOException{
			byte[] challenge = Hash.getSHA1(UUID.randomUUID().toString().getBytes());
			outputObject.writeObject(challenge);
			return challenge;
		}
		
		private void openSocketInput() {
			try {
				System.out.println(socket.getRemoteSocketAddress());
				// inputObject = new ObjectInputStream(socket.getInputStream());
				inputBuffer = new BufferedInputStream(socket.getInputStream());
				inputObject = new ObjectInputStream(inputBuffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void openSocketOutput() {
			try {
				outputObject = new ObjectOutputStream(socket.getOutputStream());
				// outputBuffer = new
				// BufferedOutputStream(socket.getOutputStream());
				// outputObject = new ObjectOutputStream(outputBuffer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private AuthRequest recieveRequest() {
			AuthRequest request = null;
			try{
				request = (AuthRequest) inputObject.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch(SocketException e2){
				System.err.println(e2);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return request;
		}

		private void sendReply(AuthReply reply) {
			try {
				outputObject.writeObject(reply);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private void closeSocket() throws IOException {
			System.out.println("attempting to close conneciton from "
					+ socket.getRemoteSocketAddress() + " on "
					+ socket.getLocalPort());
			try {
				inputObject.close();
				inputBuffer.close();
				outputObject.close();
				// outputBuffer.close();
				socket.close();
			} catch (NullPointerException e) {
				System.err
						.println("Couldn't close connections. Was the connection reset?");
			}
		}
	}
}
