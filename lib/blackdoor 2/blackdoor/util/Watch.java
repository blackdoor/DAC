package blackdoor.util;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/** a class which uses the Calendar api in a way that is accessable using Date style functions.
 * 
 * @author Nathan Fischer
 * @version 1.0.00
 * @since 2013-03-10
 * 
 */
public class Watch implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Calendar stupid; // im a terrible person
	
	/**
	 * 
	 */
	public Watch() {
		// TODO Auto-generated constructor stub
		stupid = Calendar.getInstance();
	}
	
	/**	instanciate this with the properties of other
	 * @param other 
	 */
	public Watch(Watch other){
		stupid = (Calendar) other.stupid.clone();
	}

	/**
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public Watch(TimeZone arg0, Locale arg1) {
		// TODO Auto-generated constructor stub
		stupid = Calendar.getInstance(arg0, arg1);
	}

	/**
	 * 
	 * @param year
	 * @param month
	 * @param date
	 */
	public Watch(int year, int month, int date) {
		stupid = Calendar.getInstance();
		stupid.set(year, month, date);
	}

	/**
	 * 
	 * @param year
	 * @param month
	 * @param date
	 * @param hrs
	 * @param min
	 */
	public Watch(int year, int month, int date, int hrs, int min) {
		stupid = Calendar.getInstance();
		stupid.set(year, month, date, hrs, min);
	}

	/**
	 * 
	 * @param year
	 * @param month
	 * @param date
	 * @param hrs
	 * @param min
	 * @param sec
	 */
	public Watch(int year, int month, int date, int hrs, int min, int sec) {
		stupid = Calendar.getInstance();
		stupid.set(year, month, date, hrs, min, sec);
	}

	/**
	 * 
	 * @param date a point in time that is time milliseconds after January 1, 1970 00:00:00 GMT.
	 */
	public Watch(long date) {
		stupid = Calendar.getInstance();
		stupid.setTimeInMillis(date);
	}
	
	/**
	 * 
	 * @return the calendar representation of the current instance
	 */
	public Calendar getCalendar(){
		return stupid;
	}
	
	/**
	 * 
	 * @return current time in milliseconds
	 */
	public static long getNow(){
		return Calendar.getInstance().getTimeInMillis();
	}
	
	/**
	 * @return a copy of the current instance
	 */
	public Object clone(){
		return new Watch(this);
	}
	

	/**
	 * @return the day
	 */
	public int getDay() {
		return stupid.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * @return the hours
	 */
	public int getHours() {
		return stupid.get(Calendar.HOUR_OF_DAY);
	}
	
	/**
	 * @param hours
	 *            the hours to set
	 */
	public void setHours(int hours) {
		stupid.set(Calendar.HOUR_OF_DAY, hours);
	}

	/**
	 * @return the minutes
	 */
	public int getMinutes() {
		return stupid.get(Calendar.MINUTE);
	}

	/**
	 * @param minutes
	 *            the minutes to set
	 */
	public void setMinutes(int minutes) {
		stupid.set(Calendar.MINUTE, minutes);
	}

	/**
	 * @return the month
	 */
	public int getMonth() {
		return stupid.get(Calendar.MONTH);
	}

	/**
	 * @param month
	 *            the month to set
	 */
	public void setMonth(int month) {
		stupid.set(Calendar.MONTH, month);
	}

	/**
	 * @return the seconds
	 */
	public int getSeconds() {
		return stupid.get(Calendar.SECOND);
	}

	/**
	 * @param seconds
	 *            the seconds to set
	 */
	public void setSeconds(int seconds) {
		stupid.set(Calendar.SECOND, seconds);
	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return stupid.get(Calendar.YEAR);
	}

	/**
	 * @param year
	 *            the year to set
	 */
	public void setYear(int year) {
		stupid.set(Calendar.YEAR, year);
	}

	/**
	 * @return a point in time that is time milliseconds after January 1, 1970 00:00:00 GMT.
	 */
	public long getTime() {
		return stupid.getTimeInMillis();
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(long time) {
		stupid.setTimeInMillis(time);
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(int date) {
		stupid.set(Calendar.DAY_OF_MONTH, date);
	}

	/**
	 * 
	 * @return the day of the month
	 */
	public int getDate() {
		return stupid.get(Calendar.DAY_OF_MONTH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stupid == null) ? 0 : stupid.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Watch other = (Watch) obj;
		if (stupid == null) {
			if (other.stupid != null)
				return false;
		} else if (!stupid.equals(other.stupid))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS zzzz");
		return df.format(stupid.getTime());
	}
	public static class StopWatch{
		private long startTime;
		/**
		 * 
		 * @param start if true start the watch when the class is created
		 */
		public StopWatch(boolean start){
			if(start){
				startTime = System.nanoTime();
			}
			else startTime = 0;
		}
		
		/**
		 * mark the current time. Future calls to check$ will return the elapsed time between that call and mark
		 */
		public void mark(){
			startTime = System.nanoTime();
		}
		/**
		 * 
		 * @return the number of nanoseconds since the time was marked or the stopwatch was created
		 */
		public long checkNS(){
			return System.nanoTime() - startTime;
		}
		/**
		 * 
		 * @return the number of seconds since the time was marked or the stopwatch was created
		 */
		public double checkS(){
			double time = System.nanoTime() - startTime;
			return (double)time / 1000000000.0 ;
		}
		/**
		 * 
		 * @return the number of milliseconds since the time was marked or the stopwatch was created
		 */
		public double checkMS(){
			double time = System.nanoTime() - startTime;
			return (double)time / 1000000.0 ;
		}
	}

}