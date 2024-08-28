package com.distocraft.dc5000.etl.binaryformatter;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 * @author ejarsok
 *
 */

public class BitFormatterTest {

	@Test
	public void testFormat1() {
		//EEIKBE: Base code was changed but test code was neglected. 
		//The expected result for this test should be verified.
		fail("Base code was changed but test code was neglected. The expected result for this test should be verified");
		BitFormatter bif = new BitFormatter();
		try {
			/* Calling the tested method */
			byte[] i = bif.doFormat("", 0);

			String expected = "0,0";
			String actual = i[0] + "," + i[1];

			assertEquals(expected, actual);

		} catch (Exception e) {
			e.printStackTrace();
			fail("testFormat1() failed");
		}
	}

	@Test
	public void testFormat2() {
		//EEIKBE: Base code was changed but test code was neglected. 
		//The expected result for this test should be verified.
		fail("Base code was changed but test code was neglected. The expected result for this test should be verified");
		BitFormatter bif = new BitFormatter();
		try {
			/* Calling the tested method */
			byte[] i = bif.doFormat(null, 0);

			String expected = "0,0";
			String actual = i[0] + "," + i[1];

			assertEquals(expected, actual);

		} catch (Exception e) {
			e.printStackTrace();
			fail("testFormat2() failed");
		}
	}

	@Test
	public void testFormat3() {
		//EEIKBE: Base code was changed but test code was neglected. 
		//The expected result for this test should be verified.
		fail("Base code was changed but test code was neglected. The expected result for this test should be verified");
		BitFormatter bif = new BitFormatter();
		try {
			/* Calling the tested method */
			byte[] i = bif.doFormat("10", 0);

			String expected = "10,0";
			String actual = i[0] + "," + i[1];

			assertEquals(expected, actual);

		} catch (Exception e) {
			e.printStackTrace();
			fail("testFormat3() failed");
		}
	}

	@Test
	public void testFormat4() {
		//EEIKBE: Base code was changed but test code was neglected. 
		//The expected result for this test should be verified.
		fail("Base code was changed but test code was neglected. The expected result for this test should be verified");
		BitFormatter bif = new BitFormatter();
		try {
			/* Calling the tested method */
			bif.doFormat("text", 0);
			fail("Should not execute this line");

		} catch (Exception e) {
			// test passed
		}
	}
}
