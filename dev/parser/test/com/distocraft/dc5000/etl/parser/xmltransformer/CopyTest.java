package com.distocraft.dc5000.etl.parser.xmltransformer;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author ejarsok
 *
 */

public class CopyTest {

  private static Field src;
  
  private static Field tgt;
  
  private static Field name;
  
  @BeforeClass
  public static void init() {
    try {
      /* initializing reflected fields */
      src = Copy.class.getDeclaredField("src");
      tgt = Copy.class.getDeclaredField("tgt");
      name = Copy.class.getDeclaredField("name");
      
      src.setAccessible(true);
      tgt.setAccessible(true);
      name.setAccessible(true);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
  }
  
  @Test
  public void testTransform1() {
    Copy c = new Copy();
    
    HashMap data = new HashMap();
    
    try {
      tgt.set(c, "tgt");
      
      /* Calling the tested method */
      c.transform(data, null);
      
      assertTrue(data.size() == 0);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTransform1() failed");
    }
  }
  
  @Test
  public void testTransform2() {
    Copy c = new Copy();
    
    HashMap data = new HashMap();
    data.put("src", "value");
    
    try {
      c.configure("name", "src", "tgt", null, null);
      
      /* Calling the tested method */
      c.transform(data, null);
      
      assertEquals("value", data.get("tgt"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTransform1() failed");
    }
  }

  @Test
  public void testConfigure() {
    Copy c = new Copy();

    try {
      /* Calling the tested method */
      c.configure("name", "src", "tgt", null, null);
      
      String expected = "name,src,tgt";
      String actual = name.get(c) + "," + src.get(c) + "," + tgt.get(c);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure() failed");
    }
  }

  @Test
  public void testGetSource() {
    Copy c = new Copy();
    
    try {
      src.set(c, "src");
      
      /* Calling the tested method & assert */
      assertEquals("src", c.getSource());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetSource() failed");
    }
  }

  @Test
  public void testGetTarget() {
    Copy c = new Copy();
    
    try {
      tgt.set(c, "tgt");
      
      /* Calling the tested method & assert */
      assertEquals("tgt", c.getTarget());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetTarget() failed");
    }
  }

  @Test
  public void testGetName() {
    Copy c = new Copy();
    
    try {
      name.set(c, "name");
      
      /* Calling the tested method & assert */
      assertEquals("name", c.getName());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetName() failed");
    }
  }

}
