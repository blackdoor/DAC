package blackdoor.auth;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

import blackdoor.auth.User.UserRight;
import blackdoor.crypto.Hash;
@Deprecated //See the Portunes project for more features and an SQL user database
public final class UserDB implements Serializable, Map<String, User>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5916108480866658192L;
	private static final String saveLocation = "";
	private static final String saveName = "UserDB.object";
	private ConcurrentSkipListMap<String, User> users;
	private static final String originPassword = "pass";
	/**
	 * create a DB with one user, named origin who has all rights
	 * origin user should be removed after a new user with ADD rights is created
	 */
	public UserDB() {
		users = new ConcurrentSkipListMap<String, User>();
		User origin = new User("origin", Hash.getSHA1(originPassword.getBytes()));
		origin.addUserRight(UserRight.ADD);
		origin.addUserRight(UserRight.CHANGE);
		origin.addUserRight(UserRight.REMOVE);
		put(origin);
	}
	/**
	 * create a DB from the file specified
	 * @param userDBFile
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public UserDB(String userDBFile) throws IOException, ClassNotFoundException{
		InputStream file = new FileInputStream( userDBFile );
    	InputStream buffer = new BufferedInputStream( file );
		ObjectInput input = new ObjectInputStream( buffer );
		try {
			ConcurrentSkipListMap<String, User> readObject = (ConcurrentSkipListMap<String, User>) input.readObject();
			users = readObject;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw e;
		}
		finally{
			input.close();
		}
	}
	/**
	 * saves the DB in the default save location
	 */
	public void saveDB(){
		try{
		OutputStream file = new FileOutputStream( saveLocation + saveName );
    	OutputStream buffer = new BufferedOutputStream( file );
		ObjectOutput output = new ObjectOutputStream( buffer );
		try{
			output.writeObject(users);
		}
		finally{
			output.close();
		}}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * saves the DB in the given save location
	 * @throws IOException 
	 */
	public void saveDB(String saveLocation) throws IOException{
		OutputStream file = new FileOutputStream( saveLocation );
    	OutputStream buffer = new BufferedOutputStream( file );
		ObjectOutput output = new ObjectOutputStream( buffer );
		try{
			output.writeObject(users);
		}
		finally{
			output.close();
		}
	}
	@Override
	public int size() {
		return users.size();
	}

	@Override
	public boolean isEmpty() {
		return users.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return users.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return users.containsKey(value);
	}

	@Override
	public User get(Object key) {
		return users.get(key);
	}


	public User remove(String key) {

		return users.remove(key);
	}


	@Override
	public void clear() {

		users.clear();
	}

	@Override
	public Set keySet() {
		return users.keySet();
	}

	@Override
	public Collection values() {
		return users.values();
	}

	@Override
	public Set entrySet() {
		return users.entrySet();
	}
	
	/**
	 * adds a user to the database
	 * @param newUser the new user to add to the database
	 * @return the previous value associated with key, or null if there was no mapping for key. (A null return can also indicate that the map previously associated null with key, if the implementation supports null values.)
	 */
	public User put(User newUser){
		return users.put(newUser.getUserName(), newUser);
	}
	@Deprecated
	/**
	 * use put(newUser) instead
	 */
	public User put(String key, User value) {
		// TODO Auto-generated method stub
		return users.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends User> m) {
		users.putAll(m);
	}
	@Override
	public User remove(Object key) {
		return users.remove(key);
	}

}
