package edu.teco.pavos.core.web.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

/**
 * Test {@link ArrayListParser}.
 */
public class ArrayListParserTest {
	
	/**
	 * Tests the parsing of an {@link ArrayList} of type {@link Double}.
	 */
	@Test
	public void testParse() {
		Double val1 = 17.4;
		Double val2 = 9.7;
		ArrayList<Double> list = new ArrayList<>();
		list.add(val1);
		list.add(val2);
		
		String listString = list.toString();
		System.out.println(listString);
		
		ArrayList<Double> parsedList = ArrayListParser.parse(listString);
		System.out.println(parsedList);
		assertEquals(val1, parsedList.get(0));
		assertEquals(val2, parsedList.get(1));
	}
	
}
