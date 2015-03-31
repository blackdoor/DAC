package blackdoor.cqbe;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import blackdoor.net.SocketIOWrapper;


public class TestOutput {

	public static void main(String[] args) throws Exception{
		//testIO_server();
		testIO();

	}
	
	public static void testIO() throws IOException{
		Socket s = new Socket(InetAddress.getLoopbackAddress(), 1234);
		SocketIOWrapper w = new blackdoor.net.SocketIOWrapper(s);
		w.write("this is the test string\n--");
	}
	
	public static void testIO_server() throws IOException{
		ServerSocket serverSocket = new ServerSocket(1234);
        Socket clientSocket = serverSocket.accept();
        SocketIOWrapper w = new SocketIOWrapper(clientSocket);
        System.out.println("reading ");
        System.out.println(w.read() + ">");
	}

}
