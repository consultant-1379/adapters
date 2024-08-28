package com.distocraft.dc5000.etl.binaryformatter;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 * @author ejarsok
 *
 */

public class BigintFormatterTest {

  @Test
  public void testFormat1() {
	  //EEIKBE: Base code was changed but test code was neglected. 
	  //The expected result for this test should be verified.
	  fail("Base code was changed but test code was neglected. The expected result for this test should be verified");
    BigintFormatter bif = new BigintFormatter();
    
    try {
      /* Calling the tested method */
      byte[] i = bif.doFormat("10000000", 0);
      
      String expected = "-128,-106,-104,0,0,0,0,0,0";
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
    BigintFormatter bif = new BigintFormatter();
    
    try {
      /* Calling the tested method */
      byte[] i = bif.doFormat("-10000000", 0);
      
      String expected = "-128,105,103,-1,-1,-1,-1,-1,0";
      String actual = i[0] + "," + i[1] + "," + i[2] + "," + i[3] + "," + i[4] + "," + i[5] + "," + i[6] + "," + i[7] + "," + i[8];
      
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
    BigintFormatter bif = new BigintFormatter();
    
    try {
      /* Calling the tested method */
      bif.doFormat("text", 0);
      fail("Should not execute this line");
      
    } catch (Exception e) {
      // test passed
    }
  }
}
