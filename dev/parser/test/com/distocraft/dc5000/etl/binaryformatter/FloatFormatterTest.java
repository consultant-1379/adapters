package com.distocraft.dc5000.etl.binaryformatter;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 * @author ejarsok
 *
 */

public class FloatFormatterTest {

	@Test
	public void testFormat1() {
		//EEIKBE: Base code was changed but test code was neglected. 
		//The expected result for this test should be verified.
		fail("Base code was changed but test code was neglected. The expected result for this test should be verified");
		FloatFormatter ff = new FloatFormatter();

		try {
			/* Calling the tested method */
			byte[] i = ff.doFormat("8", 0);

			String expected = "64,32,0,0,0,0,0,0,0";
			String actual = i[0] + "," + i[1] + "," + i[2] + "," + i[3] + "," + i[4] + "," + i[5] + "," + i[6] + "," + i[7] + "," + i[8];

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
		FloatFormatter ff = new FloatFormatter();

		try {
			/* Calling the tested method */
			ff.doFormat("text", 0);
			fail("Should not execute this line");

		} catch (Exception e) {
			// test passed
		}
	}
}
