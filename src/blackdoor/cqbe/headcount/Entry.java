package blackdoor.cqbe.headcount;

import blackdoor.cqbe.addressing.L3Address;
import blackdoor.util.Watch;

public class Entry implements Comparable<Entry> {
	private Watch lastSeen = null;
	private L3Address address = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getAddress() == null) ? 0 : getAddress().hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		if (getAddress() == null) {
			if (other.getAddress() != null)
				return false;
		} else if (!getAddress().equals(other.getAddress()))
			return false;
		return true;
	}

	@Override
	public int compareTo(Entry o) {
		return this.getWatch().getCalendar()
				.compareTo(o.getWatch().getCalendar());
	}

	public String getAddress() {
		return address.getLayer3Address().toString() + ":" + address.getPort()
				+ " " + address.overlayAddressToString();
	}

	public void setAddress(L3Address address) {
		this.address = address;
	}

	public Watch getWatch() {
		return lastSeen;
	}

	public String getLastSeen() {
		return String.format("%2d:%2d:%2d", 24 - (lastSeen.getHours() - 7),
				lastSeen.getMinutes(), lastSeen.getSeconds());
	}

	public void setLastSeen(Watch lastSeen) {
		this.lastSeen = lastSeen;
	}

}
