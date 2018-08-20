package server.core.model;

import java.util.Objects;

import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class TimeInterval implements TimeValue {

	private Interval interval;

	private TimeInterval() {
	}

	private TimeInterval(Interval interval) {
		assert (interval != null);
		this.interval = interval;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 67 * hash + Objects.hashCode(this.interval);
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
		final TimeInterval other = (TimeInterval) obj;
		if (!Objects.equals(this.interval, other.interval)) {
			return false;
		}
		return true;
	}

	public static TimeInterval create(long start, long end) {
		return create(start, end, null);
	}

	public static TimeInterval create(long start, long end, DateTimeZone timeZone) {
		if (timeZone == null) {
			return new TimeInterval(new Interval(start, end));
		}
		return new TimeInterval(new Interval(start, end, timeZone));
	}

	public static TimeInterval parse(String value) {
		return new TimeInterval(Interval.parse(value));
	}

	public Interval getInterval() {
		return interval;
	}

	@Override
	public String asISO8601() {
		DateTimeFormatter printer = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC);
		printer = printer.withChronology(interval.getChronology());
		StringBuffer buf = new StringBuffer(48);
		printer.printTo(buf, interval.getStartMillis());
		buf.append('/');
		printer.printTo(buf, interval.getEndMillis());
		return buf.toString();
	}

	@Override
	public String toString() {
		return asISO8601();
	}

}
