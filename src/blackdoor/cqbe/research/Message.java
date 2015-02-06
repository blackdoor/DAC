package blackdoor.cqbe.research;

public class Message {
	Address destination;
	
	public Message(Address destination) {
		this.destination = destination;
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Message [destination=" + destination + "]";
	}



	public static interface MessageHandler{
		public void handle(Message m, Node reciever);
	}
}
