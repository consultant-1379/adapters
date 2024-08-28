package com.distocraft.dc5000.etl.binaryformatter;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 * @author ejarsok
 *
 */

public class CharFormatterTest {

	@Test
	public void testFormat1() {
		//EEIKBE: Base code was changed but test code was neglected. 
		//The expected result for this test should be verified.
		fail("Base code was changed but test code was neglected. The expected result for this test should be verified");
		CharFormatter cf = new CharFormatter();

		try {
			/* Calling the tested method */
			byte[] i = cf.doFormat("", 2);

			String expected = "32,32,0";
			String actual = i[0] + "," + i[1] + "," + i[2];

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
		CharFormatter cf = new CharFormatter();

		try {
			/* Calling the tested method */
			byte[] i = cf.doFormat(null, 2);

			String expected = "32,32,0";
			String actual = i[0] + "," + i[1] + "," + i[2];

			assertEquals(expected, actual);

		} catch (Exception e) {
			e.printStackTrace();
			fail("testFormat1() failed");
		}
	}

	@Test
	public void testFormat3() {
		//EEIKBE: Base code was changed but test code was neglected. 
		//The expected result for this test should be verified.
		fail("Base code was changed but test code was neglected. The expected result for this test should be verified");
		CharFormatter cf = new CharFormatter();

		try {
			/* Calling the tested method */
			byte[] i = cf.doFormat("text", 2);

			String expected = "116,101,0";
			String actual = i[0] + "," + i[1] + "," + i[2];

			assertEquals(expected, actual);

		} catch (Exception e) {
			e.printStackTrace();
			fail("testFormat1() failed");
		}
	}

	@Test
	public void testFormat4() {
		//EEIKBE: Base code was changed but test code was neglected. 
		//The expected result for this test should be verified.
		fail("Base code was changed but test code was neglected. The expected result for this test should be verified");
		CharFormatter cf = new CharFormatter();

		try {
			/* Calling the tested method */
			byte[] i = cf.doFormat("t", 2);

			String expected = "116,32,0";
			String actual = i[0] + "," + i[1] + "," + i[2];

			assertEquals(expected, actual);

		} catch (Exception e) {
			e.printStackTrace();
			fail("testFormat1() failed");
		}
	}
}
