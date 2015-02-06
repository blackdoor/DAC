package blackdoor.cqbe.research;

public class Address {
	
	public long address;

	public Address(long address) {
		this.address = address;
	}
	
	public int getDistance(Address a){
		long xorResult = a.address ^ this.address;
		return Long.bitCount(xorResult);
	}
	
	public String toString(){
		return Long.toBinaryString(address);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (address ^ (address >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Address other = (Address) obj;
		if (address != other.address)
			return false;
		return true;
	}
	
	

}
