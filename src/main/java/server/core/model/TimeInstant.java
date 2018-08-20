package server.core.model;

import java.util.Objects;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class TimeInstant implements TimeValue {

	private DateTime dateTime;

	public TimeInstant(DateTime dateTime) {
		this.dateTime = dateTime;
	}

	public static TimeInstant now() {
		return new TimeInstant(DateTime.now());
	}

	public static TimeInstant now(DateTimeZone timeZone) {
		return new TimeInstant(DateTime.now(timeZone));
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + Objects.hashCode(this.dateTime);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (getClass() != obj.getClass()) {
			return false;
		}
		final TimeInstant other = (TimeInstant) obj;
		boolean tdtNull = this.dateTime == null;
		boolean odtNull = other.dateTime == null;
		if (tdtNull && odtNull) {
			return true;
		} else if (tdtNull | odtNull) {
			return false;
		} else if (!this.dateTime.isEqual(other.dateTime)) {
			return false;
		}
		return true;
	}

	public static TimeInstant parse(String value) {
		return parse(value, null);
	}
	
	public static TimeInstant parse(String value, DateTimeFormatter dtf) {
		if (dtf == null) {
			return new TimeInstant(DateTime.parse(value));
		}
		return new TimeInstant(DateTime.parse(value, dtf));
	}

	public static TimeInstant create(Long value) {
		return create(value, null);
	}

	public static TimeInstant create(Long value, DateTimeZone timeZone) {
		if (timeZone == null) {
			return new TimeInstant(new DateTime(value));
		}
		return new TimeInstant(new DateTime(value, timeZone));
	}

	public DateTime getDateTime() {
		return dateTime;
	}

	@Override
	public String asISO8601() {
		if (dateTime == null) {
			return null;
		}
		return ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC).print(dateTime);
	}

	@Override
	public String toString() {
		return asISO8601();
	}
}
