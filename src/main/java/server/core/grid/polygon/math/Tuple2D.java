package server.core.grid.polygon.math;

/**
 * A generic tuple class with 2 values.
 * 
 * @param <A>
 * @param <B>
 */
public class Tuple2D<A, B> {
	
	private A valueA;
	private B valueB;
	
	/**
	 * Creates a simple tuple.
	 * @param valueA
	 * @param valueB
	 * @param valueC
	 */
	public Tuple2D(A valueA, B valueB) {
		this.valueA = valueA;
		this.valueB = valueB;
	}
	
	/**
	 * Returns the first value.
	 * @return value
	 */
	public A getFirstValue() {
		return this.valueA;
	}
	
	/**
	 * Returns the second value.
	 * @return value
	 */
	public B getSecondValue() {
		return this.valueB;
	}
	
}
