package edu.teco.pavos.transfer.sender.util;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

/**
 *  Utility time class.
 */
public final class TimeUtil {
	private static final String TIME_REGEX = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}Z";
	private static final String TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	
	private TimeUtil() {
		
	}
	
	/**
	 * Removes the milliseconds from a {@link String} created from {@link LocalDateTime} (joda-time).
	 * @param localDateTime {@link String}
	 * @return resultLocalDateTime {@link String}
	 */
	public static String removeMillis(String localDateTime) {
		return getUTCDateTimeString(LocalDateTime.parse(
				localDateTime, DateTimeFormat.forPattern(TIME_PATTERN)));
	}
	
	/**
	 * Returns the currently used TimeFormat as a Regex {@link String}.
	 * @return regex {@link String}
	 */
	public static String getDateTimeRegex() {
		return TIME_REGEX;
	}
	
	/**
	 * Returns the {@link LocalDateTime} in UTC time.
	 * Parses from {@link String}.
	 * @param localDateTimeString The {@link String} to parse.
	 * @return localDateTime {@link LocalDateTime}
	 */
	public static LocalDateTime getUTCDateTime(String localDateTimeString) {
		return LocalDateTime.parse(localDateTimeString, DateTimeFormat.forPattern(TIME_PATTERN));
	}
	
	/**
	 * Returns the String of the current {@link LocalDateTime} in UTC time.
	 * @return localDateTimeString {@link String}
	 */
	public static String getUTCDateTimeNowString() {
		return getUTCDateTimeNow().toString(DateTimeFormat.forPattern(TIME_PATTERN));
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
		return ldt.toString(DateTimeFormat.forPattern(TIME_PATTERN));
	}
	
}
