package server.transfer.sender.util;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * 
 */
public final class TimeUtil {
	
	private TimeUtil() {
		
	}
	
	/**
	 * Returns the String of the current {@link LocalDateTime} in UTC time
	 * @return localDateTimeString {@link String}
	 */
	public static String getUTCDateTimeString() {
		return getUTCDateTime().toString();
	}
	
	/**
	 * Returns the {@link LocalDateTime} in UTC time
	 * @return localDateTime {@link LocalDateTime}
	 */
	public static LocalDateTime getUTCDateTime() {
		return LocalDateTime.now(Clock.systemUTC());
	}
	
}
