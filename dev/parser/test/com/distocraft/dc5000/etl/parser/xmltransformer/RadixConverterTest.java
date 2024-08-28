package com.distocraft.dc5000.etl.parser.xmltransformer;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author ejarsok
 *
 */

public class RadixConverterTest {

  private static Field src;
  
  private static Field tgt;
  
  private static Field name;

  private static Field fromRadix;
  
  private static Field toRadix;
  
  private static Field log;
  
  private static Method convert;
  
  @BeforeClass
  public static void init() {
    try {
      /* initializing reflected fields and methods */
      src = RadixConverter.class.getDeclaredField("src");
      tgt = RadixConverter.class.getDeclaredField("tgt");
      name = RadixConverter.class.getDeclaredField("name");
      fromRadix = RadixConverter.class.getDeclaredField("fromRadix");
      toRadix = RadixConverter.class.getDeclaredField("toRadix");
      log = RadixConverter.class.getDeclaredField("log");
      convert = RadixConverter.class.getDeclaredMethod("convert", new Class[] {String.class});
      
      src.setAccessible(true);
      tgt.setAccessible(true);
      name.setAccessible(true);
      fromRadix.setAccessible(true);
      toRadix.setAccessible(true);
      log.setAccessible(true);
      convert.setAccessible(true);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init failed");
    }
  }
  
  @Test
  public void testTransform1() {
    RadixConverter rc = new RadixConverter();
    Logger clog = Logger.getLogger("clog");
    HashMap data = new HashMap();
    
    try {
      tgt.set(rc, "tgt");
      
      /* Calling the tested method */
      rc.transform(data, clog);
      
      assertFalse("false expected", data.containsKey("tgt"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTransform1() failed");
    }
  }
  
  @Test
  public void testTransform2() {
    RadixConverter rc = new RadixConverter();
    Properties props = new Properties();
    props.setProperty("fromRadix", "10");
    props.setProperty("toRadix", "2");
    Logger clog = Logger.getLogger("clog");
    HashMap data = new HashMap();
    data.put("src", "2");
    
    rc.configure("name", "src", "tgt", props, clog);
    
    /* Calling the tested method */
    rc.transform(data, clog);
    
    assertEquals("10", data.get("tgt"));
  }
  
  @Test
  public void testConvert1() {
    RadixConverter rc = new RadixConverter();
    
    try {
      log.set(rc, Logger.getLogger("clog"));
      fromRadix.set(rc, 2);
      toRadix.set(rc, 16);
      
      /* Calling the tested method */
      String s = (String) convert.invoke(rc, new Object[] {"11"});
      
      assertEquals("3", s);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConvert() failed");
    }
  }
  
  @Test
  public void testConvert2() {
    RadixConverter rc = new RadixConverter();
    
    try {
      log.set(rc, Logger.getLogger("clog")); // this print NumberFormatException
      fromRadix.set(rc, 2);
      toRadix.set(rc, 16);
      
      /* Calling the tested method */
      String s = (String) convert.invoke(rc, new Object[] {"number"});
      
      assertEquals("", s);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConvert() failed");
    }
  }

  @Test
  public void testConfigure() {
    RadixConverter rc = new RadixConverter();
    Properties props = new Properties();
    
    /* Calling the tested method */
    rc.configure("name", "src", "tgt", props, null);
    
    try {
      String expected = "name,src,tgt,10,16";
      String actual = name.get(rc) + "," + src.get(rc) + "," + tgt.get(rc) + "," + fromRadix.get(rc) + "," + toRadix.get(rc);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure() failed");
    }
  }

  @Test
  public void testGetSource() {
    RadixConverter rc = new RadixConverter();
    
    try {
      src.set(rc, "src");
      
      /* Calling the tested method & assert */
      assertEquals("src", rc.getSource());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetSource() failed");
    }
  }

  @Test
  public void testGetTarget() {
    RadixConverter rc = new RadixConverter();
    
    try {
      tgt.set(rc, "tgt");
      
      /* Calling the tested method & assert */
      assertEquals("tgt", rc.getTarget());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetSource() failed");
    }
  }

  @Test
  public void testGetName() {
    RadixConverter rc = new RadixConverter();
    
    try {
      name.set(rc, "name");
      
      /* Calling the tested method & assert */
      assertEquals("name", rc.getName());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetSource() failed");
    }
  }

}
