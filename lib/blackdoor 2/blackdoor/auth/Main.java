package blackdoor.auth;

import java.io.IOException;

//import org.apache.commons.codec.binary.Hex;

import blackdoor.auth.User.UserRight;
@Deprecated //See the Portunes project for more features and an SQL user database
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//User user = new User("jim", Hash.getSHA1("hi".getBytes()));
		//System.out.println(Hash.getSHA1("hi".getBytes()).length);
		//user.getSaltyHash(Hash.getSHA1("hi".getBytes()));
		
		
		AuthClient client = new AuthClient("localhost", 1234);
		System.out.println("	origin is a user " + client.checkUser("origin", "pass"));
		UserRight[] rights = {};
		//System.out.println("	origin has removed jim " + client.removeUser("jim", "origin", "pass"));
		System.out.println("	origin has added jim " + client.addUser("jim", "password", rights, "origin", "pass"));
		//System.out.println("	jim removed himself " + client.removeUser("jim", "jim", "password"));
		System.out.println("	jim is a user " +client.checkUser("jim", "password"));
		
	}

}
