package com.distocraft.dc5000.etl.parser.xmltransformer;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
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

public class RoundTimeTest {

  private static Field src;
  
  private static Field tgt;
  
  private static Field name;
  
  private static Field format;

  private static Field minute;
  
  private static Field hour;
  
  @BeforeClass
  public static void init() {
    try {
      /*initializing reflected fields */
      src = RoundTime.class.getDeclaredField("src");
      tgt = RoundTime.class.getDeclaredField("tgt");
      name = RoundTime.class.getDeclaredField("name");
      format = RoundTime.class.getDeclaredField("format");
      minute = RoundTime.class.getDeclaredField("minute");
      hour = RoundTime.class.getDeclaredField("hour");
      
      src.setAccessible(true);
      tgt.setAccessible(true);
      name.setAccessible(true);
      format.setAccessible(true);
      minute.setAccessible(true);
      hour.setAccessible(true);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init failed");
    }
  }
  
  @Test
  public void testTransform1() {
    RoundTime rt = new RoundTime();
    Properties props = new Properties();
    props.setProperty("format", "MM/dd/yyyy");
    props.setProperty("minute", "m");
    props.setProperty("hour", "h");
    Logger clog = Logger.getLogger("clog");
    
    HashMap data = new HashMap();
    
    try {
      rt.configure("name", "src", "tgt", props, clog);
      
      /* Calling the tested method */
      rt.transform(data, clog);
      
      assertFalse("false expected", data.containsKey("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform() failed");
    }
  }
  
  @Test
  public void testTransform2() {
    RoundTime rt = new RoundTime();
    Properties props = new Properties();
    props.setProperty("format", "MM/dd/yyyy/hh/mm");
    props.setProperty("hour", "h");
    Logger clog = Logger.getLogger("clog");
    
    HashMap data = new HashMap();
    data.put("src", "10/03/2008/10/45");
    
    try {
      rt.configure("name", "src", "tgt", props, clog);
      
      /* Calling the tested method */
      rt.transform(data, clog);
      
      assertEquals("10/03/2008/11/00", data.get("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform() failed");
    }
  }
  
  @Test
  public void testTransform3() {
    RoundTime rt = new RoundTime();
    Properties props = new Properties();
    props.setProperty("format", "MM/dd/yyyy/hh/mm/ss");
    props.setProperty("minute", "m");
    Logger clog = Logger.getLogger("clog");
    
    HashMap data = new HashMap();
    data.put("src", "10/03/2008/10/30/45");
    
    try {
      rt.configure("name", "src", "tgt", props, clog);
      
      /* Calling the tested method */
      rt.transform(data, clog);
      
      assertEquals("10/03/2008/10/31/00", data.get("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform() failed");
    }
  }

  @Test
  public void testConfigure1() {
    RoundTime rt = new RoundTime();
    Properties props = new Properties();
    props.setProperty("minute", "m");
    props.setProperty("hour", "h");
    Logger clog = Logger.getLogger("clog");
    
    try {
      /* Calling the tested method */
      rt.configure("name", "src", "tgt", props, clog);
      fail("Should not execute this line, ConfigException expected");
      
    } catch (ConfigException e) {
      // Test passed
    }
  }
  
  @Test
  public void testConfigure2() {
    RoundTime rt = new RoundTime();
    Properties props = new Properties();
    props.setProperty("format", "F");
    Logger clog = Logger.getLogger("clog");
    
    try {
      /* Calling the tested method */
      rt.configure("name", "src", "tgt", props, clog);
      fail("Should not execute this line, ConfigException expected");
      
    } catch (ConfigException e) {
      // Test passed
    }
  }
  
  @Test
  public void testConfigure3() {
    RoundTime rt = new RoundTime();
    Properties props = new Properties();
    props.setProperty("minute", "m");
    props.setProperty("hour", "h");
    props.setProperty("format", "F");
    Logger clog = Logger.getLogger("clog");
    
    try {
      /* Calling the tested method */
      rt.configure("name", "src", "tgt", props, clog);

      String expected = "name,src,tgt,F,true,true";
      String actual = name.get(rt) + "," + src.get(rt) + "," + tgt.get(rt) + "," + format.get(rt) + "," + 
                      minute.get(rt) + "," + hour.get(rt);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure3() failed");
    }
  }

  @Test
  public void testGetSource() {
    RoundTime rt = new RoundTime();
    
    try {
      src.set(rt, "src");
      
      /* Calling the tested method & assert */
      assertEquals("src", rt.getSource());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetSource() failed");
    }
  }

  @Test
  public void testGetTarget() {
    RoundTime rt = new RoundTime();
    
    try {
      tgt.set(rt, "tgt");
      
      /* Calling the tested method & assert */
      assertEquals("tgt", rt.getTarget());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetTarget() failed");
    }
  }

  @Test
  public void testGetName() {
    RoundTime rt = new RoundTime();
    
    try {
      name.set(rt, "name");
      
      /* Calling the tested method & assert */
      assertEquals("name", rt.getName());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetTarget() failed");
    }
  }

}
