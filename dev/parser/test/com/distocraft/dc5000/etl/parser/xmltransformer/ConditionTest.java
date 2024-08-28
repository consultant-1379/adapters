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

public class ConditionTest {

  private static Field src;
  
  private static Field name;
  
  private static Field tgt;
  
  private static Field factor;
  
  private static Field result1;
  
  private static Field result2;
    
  private static Field factorfield;
  
  private static Field result1field;
  
  private static Field result2field;
  
  @BeforeClass
  public static void init() {
    try {
      /* initializing reflected fields */
      src = Condition.class.getDeclaredField("src");
      name = Condition.class.getDeclaredField("name");
      tgt = Condition.class.getDeclaredField("tgt");
      factor = Condition.class.getDeclaredField("factor");
      result1 = Condition.class.getDeclaredField("result1");
      result2 = Condition.class.getDeclaredField("result2");
      factorfield = Condition.class.getDeclaredField("factorfield");
      result1field = Condition.class.getDeclaredField("result1field");
      result2field = Condition.class.getDeclaredField("result2field");
      
      src.setAccessible(true);
      name.setAccessible(true);
      tgt.setAccessible(true);
      factor.setAccessible(true);
      result1.setAccessible(true);
      result2.setAccessible(true);
      factorfield.setAccessible(true);
      result1field.setAccessible(true);
      result2field.setAccessible(true);
      
    } catch(Exception e) {
      fail("init failed");
    }
  }
  
  @Test
  public void testTransform1() {
    Condition c = new Condition();
    Properties props = new Properties();
    props.setProperty("result1field", "R1F");
    
    HashMap data = new HashMap();
    data.put("R1F", "R1Fvalue");
    
    c.configure("name", "src", "tgt", props, null);
    
    /* Calling the tested method */
    c.transform(data, null);
    
    assertEquals("R1Fvalue", data.get("tgt"));
  }
  
  @Test
  public void testTransform2() {
    Condition c = new Condition();
    Properties props = new Properties();
    props.setProperty("result1", "R1");
    
    HashMap data = new HashMap();
    
    c.configure("name", "src", "tgt", props, null);
    
    /* Calling the tested method */
    c.transform(data, null);
    
    assertEquals("R1", data.get("tgt"));
  }
  
  @Test
  public void testTransform3() {
    Condition c = new Condition();
    Properties props = new Properties();
    props.setProperty("result2field", "R2F");
    
    HashMap data = new HashMap();
    data.put("src", "value");
    data.put("R2F", "R2Fvalue");
    
    c.configure("name", "src", "tgt", props, null);
    
    /* Calling the tested method */
    c.transform(data, null);
    
    assertEquals("R2Fvalue", data.get("tgt"));
  }
  
  @Test
  public void testTransform4() {
    Condition c = new Condition();
    Properties props = new Properties();
    props.setProperty("result2", "R2");
    
    HashMap data = new HashMap();
    data.put("src", "value");
    
    c.configure("name", "src", "tgt", props, null);
    
    /* Calling the tested method */
    c.transform(data, null);
    
    assertEquals("R2", data.get("tgt"));
  }

  @Test
  public void testConfigure() {
    Condition c = new Condition();
    Properties props = new Properties();
    props.setProperty("factor", "F");
    props.setProperty("result1", "R1");
    props.setProperty("result2", "R2");
    props.setProperty("factorfield", "FF");
    props.setProperty("result1field", "R1F");
    props.setProperty("result2field", "R2F");
    
    try {
      /* Calling the tested method */
      c.configure("name", "src", "tgt", props, null);
      
      String expected = "name,src,tgt,F,R1,R2,FF,R1F,R2F";
      String actual = name.get(c) + "," + src.get(c) + "," + tgt.get(c) + "," + factor.get(c) + "," + result1.get(c) + "," + 
                      result2.get(c) + "," + factorfield.get(c) + "," + result1field.get(c) + "," + result2field.get(c);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure() failed");
    }
  }

  @Test
  public void testGetSource() {
    Condition c = new Condition();
    
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
    Condition c = new Condition();
    
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
    Condition c = new Condition();
    
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
