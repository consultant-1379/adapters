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

public class SwitchTest {

  private static Field src;
  
  private static Field tgt;
  
  private static Field name;
  
  private static Field newValue;
  
  private static Field oldValue;
  
  @BeforeClass
  public static void init() {
    try {
      /* initializing reflected fields */
      src = Switch.class.getDeclaredField("src");
      tgt = Switch.class.getDeclaredField("tgt");
      name = Switch.class.getDeclaredField("name");
      newValue = Switch.class.getDeclaredField("newValue");
      oldValue = Switch.class.getDeclaredField("oldValue");
      
      src.setAccessible(true);
      tgt.setAccessible(true);
      name.setAccessible(true);
      newValue.setAccessible(true);
      oldValue.setAccessible(true);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
  }

  @Test
  public void testTransform1() {
    Switch sw = new Switch();
    Properties props = new Properties();
    props.setProperty("new", "newString");
    props.setProperty("old", "oldString");
    Logger clog = Logger.getLogger("clog");
    HashMap data = new HashMap();
    data.put("src", "oldString");
    
    try {
      sw.configure("name", "src", "tgt", props, clog);
      
      /* Calling the tested method */
      sw.transform(data, clog);
      
      assertEquals("newString", data.get("tgt"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("void testTransform() failed");
    }
  }
  
  @Test
  public void testTransform2() {
    Switch sw = new Switch();
    Properties props = new Properties();
    props.setProperty("new", "newString");
    props.setProperty("old", "oldString");
    Logger clog = Logger.getLogger("clog");
    HashMap data = new HashMap();
    
    try {
      sw.configure("name", "src", "tgt", props, clog);
      
      /* Calling the tested method */
      sw.transform(data, clog);
      
      assertFalse("false expected", data.containsKey("tgt"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("void testTransform() failed");
    }
  }

  @Test
  public void testConfigure1() {
    Switch sw = new Switch();
    Properties props = new Properties();
    props.setProperty("new", "newString");
    Logger clog = Logger.getLogger("clog");
    
    try {
      /* Calling the tested method */
      sw.configure("name", "src", "tgt", props, clog);
      fail("Should not execute this line, ConfigException expected");
      
    } catch (ConfigException e) {
      // Test passed
    }
  }
  
  @Test
  public void testConfigure2() {
    Switch sw = new Switch();
    Properties props = new Properties();
    props.setProperty("old", "oldString");
    Logger clog = Logger.getLogger("clog");
    
    try {
      /* Calling the tested method */
      sw.configure("name", "src", "tgt", props, clog);
      fail("Should not execute this line, ConfigException expected");
      
    } catch (ConfigException e) {
      // Test passed
    }
  }
  
  @Test
  public void testConfigure3() {
    Switch sw = new Switch();
    Properties props = new Properties();
    props.setProperty("new", "newString");
    props.setProperty("old", "oldString");
    Logger clog = Logger.getLogger("clog");
    
    try {
      /* Calling the tested method */
      sw.configure("name", "src", "tgt", props, clog);
      
      String expected = "name,src,tgt,newString,oldString";
      String actual = name.get(sw) + "," + src.get(sw) + "," + tgt.get(sw) + "," + newValue.get(sw) + "," + oldValue.get(sw);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure3() failed");
    }
  }

  @Test
  public void testGetSource() {
    Switch sw = new Switch();
    
    try {
      src.set(sw, "src");
      
      /* Calling the tested method & assert */
      assertEquals("src", sw.getSource());
            
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetSource() failed");
    }
  }

  @Test
  public void testGetTarget() {
    Switch sw = new Switch();
    
    try {
      tgt.set(sw, "tgt");
      
      /* Calling the tested method & assert */
      assertEquals("tgt", sw.getTarget());
            
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetTarget() failed");
    }
  }

  @Test
  public void testGetName() {
    Switch sw = new Switch();
    
    try {
      name.set(sw, "name");
      
      /* Calling the tested method & assert */
      assertEquals("name", sw.getName());
            
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetName() failed");
    }
  }

}
