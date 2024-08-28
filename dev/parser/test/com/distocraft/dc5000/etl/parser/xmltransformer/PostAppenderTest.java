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

public class PostAppenderTest {

  private static Field src;
  
  private static Field name;
  
  private static Field tgt;

  private static Field appendField;

  private static Field appendFixed;
  
  @BeforeClass
  public static void init() {
    try {
      /* initializing reflected fields */
      src = PostAppender.class.getDeclaredField("src");
      name = PostAppender.class.getDeclaredField("name");
      tgt = PostAppender.class.getDeclaredField("tgt");
      appendField = PostAppender.class.getDeclaredField("appendField");
      appendFixed = PostAppender.class.getDeclaredField("appendFixed");
      
      src.setAccessible(true);
      name.setAccessible(true);
      tgt.setAccessible(true);
      appendField.setAccessible(true);
      appendFixed.setAccessible(true);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init failed");
    }
  }
  
  @Test
  public void testTransform1() {
    PostAppender pa = new PostAppender();
    HashMap data = new HashMap();
    
    /* Calling the tested method */
    pa.transform(data, null);
    
    assertTrue(data.size() == 0);
  }
  
  @Test
  public void testTransform2() {
    PostAppender pa = new PostAppender();
    Properties props = new Properties();
    props.setProperty("fixed", "FIXED");
    HashMap data = new HashMap();
    data.put("src", "value");
    
    try {
      pa.configure("name", "src", "tgt", props, null);
      
      /* Calling the tested method */
      pa.transform(data, null);
      
      assertEquals("valueFIXED", data.get("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform2() failed");
    }
  }
  
  @Test
  public void testTransform3() {
    PostAppender pa = new PostAppender();
    Properties props = new Properties();
    props.setProperty("field", "FIELD");
    HashMap data = new HashMap();
    data.put("src", "value");
    data.put("FIELD", "F");
    
    try {
      pa.configure("name", "src", "tgt", props, null);
      
      /* Calling the tested method */
      pa.transform(data, null);
      
      assertEquals("valueF", data.get("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform3() failed");
    }
  }
  
  @Test
  public void testTransform4() {
    PostAppender pa = new PostAppender();
    Properties props = new Properties();
    props.setProperty("field", "FIELD");
    HashMap data = new HashMap();
    data.put("src", "value");
    
    try {
      pa.configure("name", "src", "tgt", props, null);
      
      /* Calling the tested method */
      pa.transform(data, null);
      
      assertEquals("value", data.get("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform4() failed");
    }
  }

  @Test
  public void testConfigure1() {
    PostAppender pa = new PostAppender();
    
    Properties props = new Properties();
    props.setProperty("field", "FIELD");
    props.setProperty("fixed", "FIXED");
    
    try {
      /* Calling the tested method */
      pa.configure("name", "src", "tgt", props, null);
      
      String expected = "name,src,tgt,FIELD,FIXED";
      String actual = name.get(pa) + "," + src.get(pa) + "," + tgt.get(pa) + "," + appendField.get(pa) + "," + appendFixed.get(pa);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure1() failed");
    }
  }
  
  @Test
  public void testConfigure2() {
    PostAppender pa = new PostAppender();
    
    Properties props = new Properties();
    
    try {
      /* Calling the tested method */
      pa.configure("name", "src", "tgt", props, null);
      fail("Should not execute this line, ConfigException expected. Parameter field or fixed has to be defined");
      
    } catch (Exception e) {
      // test passed
    }
  }

  @Test
  public void testGetSource() {
    PostAppender pa = new PostAppender();
    
    try {
      src.set(pa, "src");
      
      /* Calling the tested method & assert */
      assertEquals("src", pa.getSource());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetSource() failed");
    }
  }

  @Test
  public void testGetTarget() {
    PostAppender pa = new PostAppender();
    
    try {
      tgt.set(pa, "tgt");
      
      /* Calling the tested method & assert */
      assertEquals("tgt", pa.getTarget());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetTarget() failed");
    }
  }

  @Test
  public void testGetName() {
    PostAppender pa = new PostAppender();
    
    try {
      name.set(pa, "name");
      
      /* Calling the tested method & assert */
      assertEquals("name", pa.getName());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetName() failed");
    }
  }

}
