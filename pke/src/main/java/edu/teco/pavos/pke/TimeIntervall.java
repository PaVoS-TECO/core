package edu.teco.pavos.pke;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Defines a time interval to be used for filtering the data
 * @author Jean Baumgarten
 */
public class TimeIntervall {
	
	private DateTime start;
	private DateTime end;

	/**
	 * Default constructor
	 * @param interval of time
	 */
	public TimeIntervall(String interval) {
		
		String[] dates = interval.split(",");
		
		if (dates.length == 2) {
			
			DateTimeFormatter parser = ISODateTimeFormat.dateTimeNoMillis();
			this.start = parser.parseDateTime(dates[0]);
			this.end = parser.parseDateTime(dates[1]);
			
		} else {
			
			this.start = new DateTime();
			this.end = new DateTime();
			
		}
		
	}
	
	/**
	 * Checks if the given time is inside the interval
	 * @param time to check
	 * @return true if is inside
	 */
	public boolean isInside(DateTime time) {
		
		return (time.isAfter(this.start) && time.isBefore(this.end));
		
	}
	
	/**
	 * Gives the staring time of the interval
	 * @return a joda date time
	 */
	public DateTime getStartDate() {
		
		return this.start;
		
	}
	
	/**
	 * Gives the ending time of the interval
	 * @return a joda date time
	 */
	public DateTime getEndDate() {
		
		return this.end;
		
	}

}
