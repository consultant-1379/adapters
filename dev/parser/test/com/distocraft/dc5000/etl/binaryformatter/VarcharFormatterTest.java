/**
 * 
 */
package com.distocraft.dc5000.etl.binaryformatter;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author edeamai
 *
 */
public class VarcharFormatterTest {
	
	VarcharFormatter varCharFormater;

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
		varCharFormater = new VarcharFormatter();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public final void testDoFormat() {
		String value = "Here is text string value";
		int dataSize = 30; //Max possible bytes of input data
		byte[] result = varCharFormater.doFormat(value, dataSize);
		String resultAsString  = "";
		for(int i=0; i<result.length; i++){
			resultAsString = resultAsString + result[i] + ",";
		}
		//31 bytes expected (1 extra byte added as the null-byte)
		String expected = "72,101,114,101,32,105,115,32,116,101,120,116,32,115,116," +
						  "114,105,110,103,32,118,97,108,117,101,32,32,32,32,32,0,";
		assertEquals(expected, resultAsString);
	}
	
	@Test
	public final void testDoFormat_EmptyString() {
		String value = "";
		int dataSize = 12; //Max possible bytes of input data
		byte[] result = varCharFormater.doFormat(value, dataSize);
		String resultAsString  = "";
		for(int i=0; i<result.length; i++){
			resultAsString = resultAsString + result[i] + ",";
		}
		//13 bytes expected (1 extra byte added as the null-byte)
		String expected = "32,32,32,32,32,32,32,32,32,32,32,32,0,";  
		assertEquals(expected, resultAsString);
	}
	
	@Test
	public final void testDoFormat_NULL() {
		String value = null;
		int dataSize = 12; //Max possible bytes of input data
		byte[] result = varCharFormater.doFormat(value, dataSize);
		String resultAsString  = "";
		for(int i=0; i<result.length; i++){
			resultAsString = resultAsString + result[i] + ",";
		}
		//13 bytes expected (1 extra byte added as the null-byte)
		String expected = "32,32,32,32,32,32,32,32,32,32,32,32,0,";
		assertEquals(expected, resultAsString);
	}
	/*
	@Test
	public final void testDoFormat_ValueTooLongForDataSize() {
		String value = "Here is text string value that is too long";
		int dataSize = 30; //Max possible bytes of input data
		byte[] result = varCharFormater.doFormat(value, dataSize);
		String resultAsString  = "";
		for(int i=0; i<result.length; i++){
			resultAsString = resultAsString + result[i] + ",";
		}
		//Truncated data expected. 31 bytes expected (1 extra byte added as the null-byte). 
		String expected = "72,101,114,101,32,105,115,32,116,101,120,116,32,115,116," +
						  "114,105,110,103,32,118,97,108,117,101,32,116,104,97,116,0,";
		assertEquals(expected, resultAsString);
	}
	*/

}
