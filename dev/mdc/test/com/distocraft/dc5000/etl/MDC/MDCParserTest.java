/**
 * 
 */
package com.distocraft.dc5000.etl.MDC;


import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author edeamai
 *
 */
public class MDCParserTest {
	
	MDCParser objectUnderTest;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		objectUnderTest = new MDCParser();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testDecompressVector101() throws Exception {
		List input = makeArrayList("1,0,1");
		List result = objectUnderTest.decompressVector(input);
		List expected = makeArrayList("1");
		assertTrue(result.equals(expected));
		
		//String result = objectUnderTest.decompressVector("1,0,1");
		//assertTrue(result.equals("1"));
	}

	@Test
	public void testDecompressVector20112() throws Exception {
		List input = makeArrayList("2,0,1,1,2");
		List result = objectUnderTest.decompressVector(input);
		List expected = makeArrayList("1,2");
		assertTrue(result.equals(expected));
		//String result = objectUnderTest.decompressVector("2,0,1,1,2");
		//assertTrue(result.equals("1,2"));
	}
	
	@Test
	public void testDecompressVector123() throws Exception {
		List input = makeArrayList("1,2,3");
		List result = objectUnderTest.decompressVector(input);
		List expected = makeArrayList("0,0,3");
		assertTrue(result.equals(expected));
		//String result = objectUnderTest.decompressVector("1,2,3");
		//assertTrue(result.equals("0,0,3"));
	}
	
	@Test
	public void testDecompressVector20123() throws Exception {
		List input = makeArrayList("2,0,1,2,3");
		List result = objectUnderTest.decompressVector(input);
		List expected = makeArrayList("1,0,3");
		assertTrue(result.equals(expected));
		//String result = objectUnderTest.decompressVector("2,0,1,2,3");
		//assertTrue(result.equals("1,0,3"));
	}
	
	@Test
	public void testDecompressVector22301() throws Exception {
		List input = makeArrayList("2,2,3,0,1");
		List result = objectUnderTest.decompressVector(input);
		List expected = makeArrayList("1,0,3");
		assertTrue(result.equals(expected));
		//String result = objectUnderTest.decompressVector("2,2,3,0,1");
		//assertTrue(result.equals("1,0,3"));
	}
	
	@Test
	public void testDecompressVector20120() throws Exception {
		List input = makeArrayList("2,0,1,2,0");
		List result = objectUnderTest.decompressVector(input);
		List expected = makeArrayList("1,0,0");
		assertTrue(result.equals(expected));
		//String result = objectUnderTest.decompressVector("2,0,1,2,0");
		//assertTrue(result.equals("1,0,0"));
	}
	
	@Test
	public void testDecompressVectorEmptyString() throws Exception {
		List input = new ArrayList();input.add("");
		List result = objectUnderTest.decompressVector(input);
		assertTrue(result.equals(input));
	}
	
	@Test
	public void testDecompressVector20empty20() throws Exception {
		List input = new ArrayList();
		input.add("2");input.add("0");input.add("");input.add("2");input.add("0");
		List result = objectUnderTest.decompressVector(input);
		List expected = new ArrayList();
		expected.add("");expected.add("0");expected.add("0");
		assertTrue(result.equals(expected));
	}
	
	
	
	
	//EMPTY DATA:
	
	@Test
	public void testDecompressVector0() throws Exception {
		List input = makeArrayList("0");
		List result = objectUnderTest.decompressVector(input);
		List expected = makeArrayList("0");
		assertTrue(result.equals(expected));
	}
	
	@Test
	public void testDecompressVector100() throws Exception {
		List input = makeArrayList("1,0,0");
		List result = objectUnderTest.decompressVector(input);
		List expected = makeArrayList("0");
		assertTrue(result.equals(expected));
	}
	
	@Test
	public void testDecompressVector110() throws Exception {
		List input = makeArrayList("1,1,0");
		List result = objectUnderTest.decompressVector(input);
		List expected = makeArrayList("0,0");
		assertTrue(result.equals(expected));
	}
	
	@Test
	public void testDecompressVector130() throws Exception {
		List input = makeArrayList("1,3,0");
		List result = objectUnderTest.decompressVector(input);
		List expected = makeArrayList("0,0,0,0");
		assertTrue(result.equals(expected));
	}
	
	@Test
	public void testDecompressVector20010() throws Exception {
		List input = makeArrayList("2,0,0,1,0");
		List result = objectUnderTest.decompressVector(input);
		List expected = makeArrayList("0,0");
		assertTrue(result.equals(expected));
	}
	
	@Test
	public void testDecompressVector20020() throws Exception {
		List input = makeArrayList("2,0,0,2,0");
		List result = objectUnderTest.decompressVector(input);
		List expected = makeArrayList("0,0,0");
		assertTrue(result.equals(expected));
	}
	
	
	
	//BAD KEY:
	
	@Test
	public void testDecompressVectorKeyHasBadChar() throws Exception {
		List input = makeArrayList("'-1,0,1");
		List result = objectUnderTest.decompressVector(input);
		assertTrue(null==result);
	}
	
	@Test
	public void testDecompressVectorKeyNegative() throws Exception {
		List input = makeArrayList("-1,0,1");
		List result = objectUnderTest.decompressVector(input);
		assertTrue(null==result);
	}
	
	@Test
	public void testDecompressVectorKeyNotWholeNum() throws Exception {
		List input = makeArrayList("1.5,0,1");
		List result = objectUnderTest.decompressVector(input);
		assertTrue(null==result);
	}
	
	@Test
	public void testDecompressVector201() throws Exception {
		List input = makeArrayList("2,0,1");
		List result = objectUnderTest.decompressVector(input);
		assertTrue(null==result);
	}
	
	@Test
	public void testDecompressVector10123() throws Exception {
		List input = makeArrayList("1,0,1,2,3");
		List result = objectUnderTest.decompressVector(input);
		assertTrue(null==result);	
	}
	
	@Test
	public void testDecompressVector201238() throws Exception {
		List input = makeArrayList("2,0,1,2,3,8");
		List result = objectUnderTest.decompressVector(input);
		assertTrue(null==result);	
	}
	
	@Test
	public void testDecompressVector01235() throws Exception {
		List input = makeArrayList("0,1,2,3,5");
		List result = objectUnderTest.decompressVector(input);
		assertTrue(null==result);	
	}
	
	
	
	
	
	//BAD INDEX:
	
	@Test
	public void testDecompressVector11dot52() throws Exception {
		List input = makeArrayList("1,1.5,2");
		List result = objectUnderTest.decompressVector(input);
		assertTrue(null==result);
	}
	
	@Test
	public void testDecompressVector1minus12() throws Exception {
		List input = makeArrayList("1,-1,2");
		List result = objectUnderTest.decompressVector(input);
		assertTrue(null==result);	
	}
	
	@Test
	public void testDecompressVector210minus12() throws Exception {
		List input = makeArrayList("2,1,0,-1,2");
		List result = objectUnderTest.decompressVector(input);
		assertTrue(null==result);	
	}
	
	@Test
	public void testDecompressVector1minus1dot51() throws Exception {
		List input = makeArrayList("1,-1.5,1");
		List result = objectUnderTest.decompressVector(input);
		assertTrue(null==result);	
	}
	
	
	
//BAD DATA:
	
	@Test
	public void testDecompressVector1() throws Exception {
		List input = makeArrayList("1");
		List result = objectUnderTest.decompressVector(input);
		assertTrue(null==result);
	}
	
	@Test
	public void testDecompressVector12() throws Exception {
		List input = makeArrayList("1,2");
		List result = objectUnderTest.decompressVector(input);
		assertTrue(null==result);	
	}
	
	@Test
	public void testDecompressVector1234() throws Exception {
		List input = makeArrayList("1,2,3,4");
		List result = objectUnderTest.decompressVector(input);
		assertTrue(null==result);	
	}

	
	
	
	/**
	 * @param input	expected to be a String of comma separated entities
	 * @return and ArrayList object whose elements are the entities listed in input - entities returned are of type String - in same order as in input.
	 */
	private List makeArrayList(String input) {
		StringTokenizer bins = new StringTokenizer(input,",");
		List inputArrayList = new ArrayList();
		while(bins.hasMoreElements()){
			inputArrayList.add(bins.nextToken());
		}
		return inputArrayList;
	}

}
