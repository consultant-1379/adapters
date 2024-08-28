package com.distocraft.dc5000.etl.parser.xmltransformer;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.Properties;
import java.util.TimeZone;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * @author ejarsok
 *
 */

public class DSTParametersTest {

  private static Field tgt;
  
  private static Field tz;
  
  private static Field name;
  
  @BeforeClass
  public static void init() {
    try {
      /* initializing reflected fields */
      tgt = DSTParameters.class.getDeclaredField("tgt");
      tz = DSTParameters.class.getDeclaredField("tz");
      name = DSTParameters.class.getDeclaredField("name");
      
      tgt.setAccessible(true);
      tz.setAccessible(true);
      name.setAccessible(true);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
  }
  
  @Test
  public void testConfigure() {
    DSTParameters dst = new DSTParameters();
    Properties props = new Properties();
    props.setProperty("timezone", "GMT+0300");
    
    try {
      dst.configure("name", "srcKey", "tgtKey", props, null);
      
      /* Calling the tested method */
      TimeZone t = (TimeZone) tz.get(dst);
      
      String expected = "name,tgtKey,GMT+03:00";
      String actual = name.get(dst) + "," + tgt.get(dst) + "," + t.getID();
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure() failed");
    }
  }

  @Ignore
  public void testTransform() {
    fail("Not yet implemented");
    // TODO
  }

  @Test
  public void testGetSource() {
    DSTParameters dst = new DSTParameters();
    
    try {
      /* Calling the tested method & assert */
      assertNull(dst.getSource());
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetSource() failed");
    }
  }

  @Test
  public void testGetTarget() {
    DSTParameters dst = new DSTParameters();
    
    try {
      tgt.set(dst, "tgt");
      
      /* Calling the tested method & assert */
      assertEquals("tgt", dst.getTarget());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetTarget() failed");
    }
  }

  @Test
  public void testGetName() {
    DSTParameters dst = new DSTParameters();
    
    try {
      name.set(dst, "name");
      
      /* Calling the tested method & assert */
      assertEquals("name", dst.getName());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetTarget() failed");
    }
  }

}
