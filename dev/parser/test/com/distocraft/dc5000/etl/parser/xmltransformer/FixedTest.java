package com.distocraft.dc5000.etl.parser.xmltransformer;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author ejarsok
 *
 */

public class FixedTest {

  private static Field fixedStr;
  
  private static Field name;
  
  private static Field tgt;
  
  @BeforeClass
  public static void init() {
    try {
      /* initializing reflected fields */
      fixedStr = Fixed.class.getDeclaredField("fixedStr");
      name = Fixed.class.getDeclaredField("name");
      tgt = Fixed.class.getDeclaredField("tgt");
      
      fixedStr.setAccessible(true);
      name.setAccessible(true);
      tgt.setAccessible(true);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
  }
  
  @Test
  public void testTransform() {
    Fixed f = new Fixed();
    Properties props = new Properties();
    props.setProperty("value", "VALUE");
    HashMap data = new HashMap();
    
    try {
      f.configure("name", "src", "tgt", props, null);
      
      /* Calling the tested method */
      f.transform(data, null);
      
      assertEquals("VALUE", data.get("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform() failed");
    }
  }

  @Test
  public void testConfigure1() {
    Fixed f = new Fixed();
    Properties props = new Properties();
    props.setProperty("value", "VALUE");
    
    try {
      /* Calling the tested method */
      f.configure("name", "src", "tgt", props, null);
      
      String expected = "name,tgt,VALUE";
      String actual = name.get(f) + "," + tgt.get(f) + "," + fixedStr.get(f);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure1() failed");
    }    
  }
  
  @Test
  public void testConfigure2() {
    Fixed f = new Fixed();
    Properties props = new Properties();
    
    try {
      /* Calling the tested method */
      f.configure("name", "src", "tgt", props, null);
      fail("Should not execute this line, ConfigException expected. Parameter value has to be defined");
      
    } catch (Exception e) {
      // test passed
    }
  }

  @Test
  public void testGetSource() {
    Fixed f = new Fixed();
    
    try {
      /* Calling the tested method & assert */
      assertNull(f.getSource());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetSource() failed");
    }
  }

  @Test
  public void testGetTarget() {
    Fixed f = new Fixed();
    
    try {
      tgt.set(f, "tgt");
      
      /* Calling the tested method & assert */
      assertEquals("tgt", f.getTarget());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetTarget() failed");
    }
  }

  @Test
  public void testGetName() {
    Fixed f = new Fixed();
    
    try {
      name.set(f, "name");
      
      /* Calling the tested method & assert */
      assertEquals("name", f.getName());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetTarget() failed");
    }
  }

}
