package server.transfer.sender.util;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

/**
 *  Utility time class.
 */
public final class TimeUtil {
	
	private TimeUtil() {
		
	}
	
	/**
	 * Removes the milliseconds from a {@link String} created from {@link LocalDateTime} (joda-time).
	 * @param localDateTime {@link String}
	 * @return resultLocalDateTime {@link String}
	 */
	public static String removeMillis(String localDateTime) {
		return getUTCDateTimeString(LocalDateTime.parse(localDateTime, DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
	}
	
	/**
	 * Returns the {@link LocalDateTime} in UTC time.
	 * Parses from {@link String}.
	 * @param localDateTimeString The {@link String} to parse.
	 * @return localDateTime {@link LocalDateTime}
	 */
	public static LocalDateTime getUTCDateTime(String localDateTimeString) {
		return LocalDateTime.parse(localDateTimeString, DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
	}
	
	/**
	 * Returns the String of the current {@link LocalDateTime} in UTC time.
	 * @return localDateTimeString {@link String}
	 */
	public static String getUTCDateTimeNowString() {
		return getUTCDateTimeNow().toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
	}
	
	/**
	 * Returns the {@link LocalDateTime} in UTC time.
	 * @return localDateTime {@link LocalDateTime}
	 */
	public static LocalDateTime getUTCDateTimeNow() {
		return LocalDateTime.now(DateTimeZone.UTC);
	}
	
	/**
	 * Returns the {@link LocalDateTime} in UTC time.
	 * @param ldt The {@link LocalDateTime} to be converted.
	 * @return localDateTime {@link String}
	 */
	public static String getUTCDateTimeString(LocalDateTime ldt) {
		return ldt.toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
	}
	
}
