package edu.teco.pavos.core.web.util;

import java.util.ArrayList;

/**
 * Parses a {@link String} to an {@link ArrayList}.
 */
public final class ArrayListParser {
	
	private ArrayListParser() {
		
	}
	
	/**
	 * Parses a {@link String} of the form '[0.0,1.0,2.0]'
	 * to an {@link ArrayList} of type {@link Double}.
	 * @param arrayListAsString The {@link String} to parse from
	 * @return valueArray {@link ArrayList} of type {@link Double}
	 */
	public static ArrayList<Double> parse(String arrayListAsString) {
		String replaced = arrayListAsString.replaceAll("\\[", "")
				.replaceAll("\\]", "")
				.replaceAll(" ", "");
		String[] stringValues = replaced.split(",");
		ArrayList<Double> valueArray = new ArrayList<>();
		for (int i = 0; i < stringValues.length; i++) {
			Double entry = Double.parseDouble(stringValues[i]);
			valueArray.add(entry);
		}
		return valueArray;
	}
	
}
