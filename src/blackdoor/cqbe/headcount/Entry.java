package blackdoor.cqbe.headcount;

import blackdoor.cqbe.addressing.L3Address;
import blackdoor.util.Watch;

public class Entry implements Comparable<Entry>{
	Watch lastSeen;
	L3Address address;
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
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
		Entry other = (Entry) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		return true;
	}



	@Override
	public int compareTo(Entry o) {
		return this.lastSeen.getCalendar().compareTo(o.lastSeen.getCalendar());
	}
	
	
}
