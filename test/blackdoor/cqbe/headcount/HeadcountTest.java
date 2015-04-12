package blackdoor.cqbe.headcount;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import blackdoor.cqbe.addressing.L3Address;
import blackdoor.net.SocketIOWrapper;

public class HeadcountTest {

	@Before
	public void setUp() throws Exception {
		Headcount.main(new String[]{"12345"});
	}

	@Test
	public void test() throws IOException, InterruptedException {
		Thread.sleep(500);
		for(int i = 0; i < 4; i++){
			L3Address addr = new L3Address(InetAddress.getLocalHost(), i);
			SocketIOWrapper io = new SocketIOWrapper(new Socket(InetAddress.getLocalHost(), 12345));
			io.write(addr.toJSONString());
		}
		while(true);
	}

}
