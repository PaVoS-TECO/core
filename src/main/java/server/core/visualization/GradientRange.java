package server.core.visualization;

public class GradientRange {
	
	public final String NAME;
	public final double VALUE_START;
	public final double VALUE_END;
	
	public GradientRange(String name, double valueStart, double valueEnd) {
		this.NAME = name;
		this.VALUE_START = valueStart;
		this.VALUE_END = valueEnd;
	}
	
	@Override
	public String toString() {
		return VALUE_START + "," + VALUE_END;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!o.getClass().equals(this.getClass())) return false;
		GradientRange oRange = (GradientRange) o;
		if (!oRange.NAME.equals(this.NAME)) return false;
		return true;
	}
	
}
