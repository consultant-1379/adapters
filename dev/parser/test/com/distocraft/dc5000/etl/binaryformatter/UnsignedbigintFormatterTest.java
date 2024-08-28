package com.distocraft.dc5000.etl.binaryformatter;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 * @author ejarsok
 *
 */

public class UnsignedbigintFormatterTest {

	@Test
	public void testFormat1() {
		//EEIKBE: Base code was changed but test code was neglected. 
		//The expected result for this test should be verified.
		fail("Base code was changed but test code was neglected. The expected result for this test should be verified");
		UnsignedbigintFormatter ubif = new UnsignedbigintFormatter();

		try {
			/* Calling the tested method */
			byte[] i = ubif.doFormat("100", 0);

			String expected = "0,0,0,0,0,0,0,100,0";
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
		UnsignedbigintFormatter ubif = new UnsignedbigintFormatter();

		try {
			/* Calling the tested method */
			ubif.doFormat("text", 0);
			fail("Should not execute this line");

		} catch (Exception e) {
			// test passed
		}
	}
}
