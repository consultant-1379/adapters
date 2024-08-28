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

public class PropertyTokenizerTest {

  private static Field src;
  private static Field tgt;
  private static Field tgts;
  private static Field name;
  private static Field delim;
  
  @BeforeClass
  public static void init() {
    try {
      /* initializing reflected fields */
      src = PropertyTokenizer.class.getDeclaredField("src");
      tgt = PropertyTokenizer.class.getDeclaredField("tgt");
      tgts = PropertyTokenizer.class.getDeclaredField("tgts");
      name = PropertyTokenizer.class.getDeclaredField("name");
      delim = PropertyTokenizer.class.getDeclaredField("delim");
      
      src.setAccessible(true);
      tgt.setAccessible(true);
      tgts.setAccessible(true);
      name.setAccessible(true);
      delim.setAccessible(true);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init failed");
    }
  }
  
  @Test
  public void testTransform1() {
    PropertyTokenizer pt = new PropertyTokenizer();
    
    HashMap data = new HashMap();
    
    /* Calling the tested method */
    pt.transform(data, null);
    
    assertTrue(data.size() == 0);
    
  }
  
  @Test
  public void testTransform2() {
    PropertyTokenizer pt = new PropertyTokenizer();
    Properties props = new Properties();
    
    HashMap data = new HashMap();
    data.put("src", "key1=1,key2=2");
    
    try {
      pt.configure("name", "src", "tgt", props, null);
      
      /* Calling the tested method */
      pt.transform(data, null);
      
      String expected = "tgt.key1,tgt.key2,;1;2";
      String actual = tgts.get(pt) + ";" + data.get("tgt.key1") + ";" + data.get("tgt.key2");
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTransform2() failed");
    }
  }

  @Test
  public void testConfigure() {
    PropertyTokenizer pt = new PropertyTokenizer();
    Properties props = new Properties();

    try {
      /* Calling the tested method */
      pt.configure("name", "src", "tgt", props, null);
      
      String expected = "name,src,tgt,,";
      String actual = name.get(pt) + "," + src.get(pt) + "," + tgt.get(pt) + "," + delim.get(pt);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure() failed");
    }  
  }

  @Test
  public void testGetSource() {
    PropertyTokenizer pt = new PropertyTokenizer();
    
    try {
      src.set(pt, "src");
      
      /* Calling the tested method & assert */
      assertEquals("src", pt.getSource());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetSource() failed");
    }
  }

  @Test
  public void testGetTarget() {
    PropertyTokenizer pt = new PropertyTokenizer();
    
    try {
      tgts.set(pt, "tgts");
      
      /* Calling the tested method & assert */
      assertEquals("tgts", pt.getTarget());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetTarget() failed");
    }
  }

  @Test
  public void testGetName() {
    PropertyTokenizer pt = new PropertyTokenizer();
    
    try {
      name.set(pt, "name");
      
      /* Calling the tested method & assert */
      assertEquals("name", pt.getName());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetName() failed");
    }
  }

}
