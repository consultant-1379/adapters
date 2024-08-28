package com.distocraft.dc5000.etl.binaryformatter;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author ejarsok
 *
 */

public class NumDecFormatterTest {

  private static Method specialFormat;
  
  @BeforeClass
  public static void init() {
    try {
      /* initializing reflected method */
      specialFormat = NumDecFormatter.class.getDeclaredMethod("specialFormat", new Class[] {String.class, int.class, int.class});
      
      specialFormat.setAccessible(true);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
  }
  
  @Test
  public void testFormat1() {
		//EEIKBE: Base code was changed but test code was neglected. 
		//The expected result for this test should be verified.
		fail("Base code was changed but test code was neglected. The expected result for this test should be verified");
    NumDecFormatter ndf = new NumDecFormatter();
    
    try {
      /* Calling the tested method */
      ndf.doFormat("", 0);
      fail("Should not execute this line");
      
    } catch (Exception e) {
      // test passed
    }
  }
  
  @Test
  public void testFormat2() {
		//EEIKBE: Base code was changed but test code was neglected. 
		//The expected result for this test should be verified.
		fail("Base code was changed but test code was neglected. The expected result for this test should be verified");
    NumDecFormatter ndf = new NumDecFormatter();
    
    try {
      /* Calling the tested method */
      byte[] i = ndf.doFormat("", 2, 0);

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
    NumDecFormatter ndf = new NumDecFormatter();
    
    try {
      /* Calling the tested method */
      byte[] i = ndf.doFormat(null, 7, 0);

      String expected = "0,0,0,0,1";
      String actual = i[0] + "," + i[1] + "," + i[2] + "," + i[3] + "," + i[4];
      
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
    NumDecFormatter ndf = new NumDecFormatter();
    
    try {
      /* Calling the tested method */
      byte[] i = ndf.doFormat("1,2,3", 15, 5);

      String expected = "0,0,0,0,0,1,212,192,0";
      String actual = i[0] + "," + i[1] + "," + i[2] + "," + i[3] + "," + i[4] + "," + i[5] + "," + i[6] + "," + i[7] + "," + i[8];
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testFormat4() failed");
    }
  }
  
  @Test
  public void testFormat5() {
		//EEIKBE: Base code was changed but test code was neglected. 
		//The expected result for this test should be verified.
		fail("Base code was changed but test code was neglected. The expected result for this test should be verified");
    NumDecFormatter ndf = new NumDecFormatter();
    
    try {
      /* Calling the tested method */
      byte[] i = ndf.doFormat("-1,2,3", 15, 5);

      String expected = "255,255,255,255,255,254,43,64,0";
      String actual = i[0] + "," + i[1] + "," + i[2] + "," + i[3] + "," + i[4] + "," + i[5] + "," + i[6] + "," + i[7] + "," + i[8];
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testFormat5() failed");
    }
  }
  
  @Test
  public void testFormat6() {
		//EEIKBE: Base code was changed but test code was neglected. 
		//The expected result for this test should be verified.
		fail("Base code was changed but test code was neglected. The expected result for this test should be verified");
    NumDecFormatter ndf = new NumDecFormatter();
    
    try {
      /* Calling the tested method */
      byte[] i = ndf.doFormat("1,2,3", 25, 5);

      String expected = "1,2,79,0,7,208,0,1,0";
      String actual = i[0] + "," + i[1] + "," + i[2] + "," + i[3] + "," + i[4] + "," + i[5] + "," + i[6] + "," + i[7] + "," + i[8];
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testFormat4() failed");
    }
  }

  @Test
  public void testSpecialFormatStringIntInt() {
		//EEIKBE: Base code was changed but test code was neglected. 
		//The expected result for this test should be verified.
		fail("Base code was changed but test code was neglected. The expected result for this test should be verified");
    NumDecFormatter ndf = new NumDecFormatter();
    
    try {
      /* Calling the tested method */
      int[] i = (int[]) specialFormat.invoke(ndf, new Object[] {"0000000000000000000120000", 25, 5});
      
      String expected = "1,2,79,0,7,208,0,1,0";
      String actual = i[0] + "," + i[1] + "," + i[2] + "," + i[3] + "," + i[4] + "," + i[5] + "," + i[6] + "," + i[7] + "," + i[8];
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testSpecialFormatStringIntInt() failed");
    }
  }
}
