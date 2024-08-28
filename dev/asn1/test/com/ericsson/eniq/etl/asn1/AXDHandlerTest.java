package com.ericsson.eniq.etl.asn1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

/**
 * This class contains misc set of unit tests for the AXD parser.
 * 
 * @author eheitur
 *
 */
public class AXDHandlerTest {

	/**
	 * Testing Object Identifier conversion from bytes to a dotted syntax OID
	 * string.
	 */
	@Test
	public void testDoOid() {
		AXDHandler ah = new AXDHandler();

		// Using OID hex: 2B 06 01 04 01 81 41 0E 01 02 02 01 02 01 03
		byte[] b = HexBin.decode("2B0601040181411303020201030103");

		try {
			String str = ah.doOid(b);
			assertEquals("1.3.6.1.4.1.193.19.3.2.2.1.3.1.3", str);

		} catch (Exception e) {
			e.printStackTrace();
			fail("testDoOid() failed.");
		}

	}

	/**
	 * Testing octet string to string conversion
	 */
	@Test
	public void testDoString_with_OctectString() {
		AXDHandler ah = new AXDHandler();

		// Using IP Address hex: AC1FF802
		// byte[] b = HexBin.decode("AC1FF802");
		byte[] b = new byte[] { (byte) 172, (byte) 31, (byte) 248, (byte) 2 };
		try {
			ASN1Rule rule = new ASN1Rule();
			rule.type = "OCTET STRING";
			String str = ah.doString(rule, b);
			assertEquals("AC1FF802", str);

		} catch (Exception e) {
			e.printStackTrace();
			fail("testDoString_with_OctectString() failed.");
		}

	}

	/**
	 * Testing date time id from time stamp
	 */
	@Test
	public void testDateTimeIdConversion() {
		
		String defaultDateTimeIDFormat = "yyyyMMddHHmmss";
		SimpleDateFormat dateFormat = null;
		dateFormat = new SimpleDateFormat(defaultDateTimeIDFormat);
		
		int timeStamp = 1218038430;
		System.out.println("timestamp: " + timeStamp + ", in millis: " + ((Integer)timeStamp).longValue() * 1000);


		// Using int it will fail!!!
		//Date tmpDate = new Date(timeStamp * 1000);
		Date tmpDate = new Date(((Integer)timeStamp).longValue() * 1000);
		
		System.out.println("date: " + tmpDate);
		// Format and store the date time id.
		String datetime_id = dateFormat.format(tmpDate);

		assertEquals("20080806190030", datetime_id);

	}

	/**
	 * Testing date time id from time stamp
	 */
	@Test
	public void testVendorTagConversion() {
		
		String measType = "abcdefghijklmnopqrstuvwxyz_abcdefghijklmnopqrstuvwxyz";
		String vendorTag = measType.substring(0, 50);
		assertEquals("abcdefghijklmnopqrstuvwxyz_abcdefghijklmnopqrstuvw", vendorTag);

		measType = "abcdefghijklmnopqrstuvwxyz_abcdefghijklmnopqrstu";
		vendorTag = measType; 
		assertEquals("abcdefghijklmnopqrstuvwxyz_abcdefghijklmnopqrstu", vendorTag);

		measType = "1111111111_222222222_333333333_444444444_555555555_666666666";
    vendorTag = measType.substring(0, 50);
    assertEquals("1111111111_222222222_333333333_444444444_555555555", vendorTag);
	
    measType = "123456789     ";
    vendorTag = measType.substring(0, 10);
    System.out.println("'" + vendorTag + "'");
    assertEquals("123456789 ", vendorTag);

	}

	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(AXDHandlerTest.class);
	}
}
