package server.core.grid.polygon.math;

/**
 * A generic tuple class with 3 values.
 * 
 * @param <A>
 * @param <B>
 * @param <C>
 */
public class Tuple3D<A, B, C> {
	
	private A valueA;
	private B valueB;
	private C valueC;
	
	/**
	 * Creates a simple tuple.
	 * @param valueA
	 * @param valueB
	 * @param valueC
	 */
	public Tuple3D(A valueA, B valueB, C valueC) {
		this.valueA = valueA;
		this.valueB = valueB;
		this.valueC = valueC;
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
	
	/**
	 * Returns the third value.
	 * @return value
	 */
	public C getThirdValue() {
		return this.valueC;
	}
	
}
