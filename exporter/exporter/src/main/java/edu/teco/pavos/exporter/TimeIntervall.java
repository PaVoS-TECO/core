package edu.teco.pavos.exporter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * 
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
