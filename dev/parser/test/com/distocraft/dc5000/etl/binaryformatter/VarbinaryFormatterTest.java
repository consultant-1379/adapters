package com.distocraft.dc5000.etl.binaryformatter;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 * @author ejarsok
 *
 */

public class VarbinaryFormatterTest {

	@Test
	public void testFormat1() {
		//EEIKBE: Base code was changed but test code was neglected. 
		//The expected result for this test should be verified.
		fail("Base code was changed but test code was neglected. The expected result for this test should be verified");
		VarbinaryFormatter vf = new VarbinaryFormatter();

		try {
			/* Calling the tested method */
			byte[] i = vf.doFormat("", 2);

			String expected = "0,0,1";
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
		VarbinaryFormatter vf = new VarbinaryFormatter();

		try {
			/* Calling the tested method */
			byte[] i = vf.doFormat(null, 2);

			String expected = "0,0,1";
			String actual = i[0] + "," + i[1] + "," + i[2];

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
		VarbinaryFormatter vf = new VarbinaryFormatter();

		try {
			/* Calling the tested method */
			byte[] i = vf.doFormat("text", 2);

			String expected = "116,101,0";
			String actual = i[0] + "," + i[1] + "," + i[2];

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
		VarbinaryFormatter vf = new VarbinaryFormatter();

		try {
			/* Calling the tested method */
			byte[] i = vf.doFormat("t", 2);

			String expected = "116,0,0";
			String actual = i[0] + "," + i[1] + "," + i[2];

			assertEquals(expected, actual);

		} catch (Exception e) {
			e.printStackTrace();
			fail("testFormat3() failed");
		} 
	}
}
