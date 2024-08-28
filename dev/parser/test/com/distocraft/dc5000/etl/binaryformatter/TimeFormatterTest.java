package com.distocraft.dc5000.etl.binaryformatter;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 * @author ejarsok
 *
 */

public class TimeFormatterTest {

	@Test
	public void testFormat1() {
		//EEIKBE: Base code was changed but test code was neglected. 
		//The expected result for this test should be verified.
		fail("Base code was changed but test code was neglected. The expected result for this test should be verified");
		TimeFormatter tf = new TimeFormatter();

		try {
			/* Calling the tested method */
			byte[] i = tf.doFormat("", 0);

			String expected = "0,0,0,0,0,0,0,0,1";
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
		TimeFormatter tf = new TimeFormatter();

		try {
			/* Calling the tested method */
			byte[] i = tf.doFormat(null, 0);

			String expected = "0,0,0,0,0,0,0,0,1";
			String actual = i[0] + "," + i[1] + "," + i[2] + "," + i[3] + "," + i[4] + "," + i[5] + "," + i[6] + "," + i[7] + "," + i[8];

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
		TimeFormatter tf = new TimeFormatter();

		try {
			/* Calling the tested method */
			byte[] i = tf.doFormat("10:20:30", 0);

			String expected = "0,0,0,8,171,20,183,128,0";
			String actual = i[0] + "," + i[1] + "," + i[2] + "," + i[3] + "," + i[4] + "," + i[5] + "," + i[6] + "," + i[7] + "," + i[8];

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
		TimeFormatter tf = new TimeFormatter();

		try {
			/* Calling the tested method */
			tf.doFormat("text", 0);
			fail("Should not execute this line");

		} catch (Exception e) {
			// test passed
		}
	}
}
