package edu.teco.pavos.core.grid.polygon.math;

import java.util.ArrayList;

/**
 * Provides different mathematical methods to cope with
 * operations on ArrayLists in an abstract way.
 */
public final class ArrayListCalc {
	
	private ArrayListCalc() {
		
	}
	
	/**
	 * Sums all entries of multiple ArrayLists.
	 * All {@link ArrayList}s have to have the same dimensions.
	 * @param lists The {@link ArrayList}s of type {@link Double}
	 * @return values The resulting {@link ArrayList}
	 */
	@SafeVarargs
	public static ArrayList<Double> sumArrayArray(ArrayList<? extends Number>... lists) {
		if (!isSameType(lists)) throw new IllegalArgumentException();
		ArrayList<Double> result = new ArrayList<>();
		for (int i = 0; i < lists[0].size(); i++) {
			double value = 0;
			for (int j = 0; j < lists.length; j++) {
				value += lists[j].get(i).doubleValue();
			}
			result.add(value);
		}
		return result;
	}
	
	/**
	 * Multiplies all entries of a single Array by a factor.
	 * @param list The {@link ArrayList} of type {@link Double}
	 * @param factor {@link Number}
	 * @return values The resulting {@link ArrayList}
	 */
	public static ArrayList<Double> multiplyArrayValue(ArrayList<? extends Number> list, Number factor) {
		ArrayList<Double> result = new ArrayList<>();
		list.forEach(value -> result.add(value.doubleValue() * factor.doubleValue()));
		return result;
	}
	
	/**
	 * Divides all entries of a single Array by a factor.
	 * @param list The {@link ArrayList} of type {@link Double}
	 * @param factor {@link Number}
	 * @return values The resulting {@link ArrayList}
	 */
	public static ArrayList<Double> divideArrayValue(ArrayList<? extends Number> list, Double factor) {
		ArrayList<Double> result = new ArrayList<>();
		list.forEach(value -> result.add(value.doubleValue() / factor.doubleValue()));
		return result;
	}
	
	@SafeVarargs
	private static final boolean isSameType(ArrayList<? extends Number>... lists) {
		for (int i = 1; i < lists.length; i++) {
			if (!lists[i - 1].getClass().equals(lists[i].getClass())) {
				return false;
			}
		}
		return true;
	}
	
}
