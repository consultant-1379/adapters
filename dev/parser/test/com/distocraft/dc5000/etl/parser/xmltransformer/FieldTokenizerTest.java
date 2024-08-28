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

public class FieldTokenizerTest {

  private static Field src;
  
  private static Field name;
  
  private static Field tgt;
  
  private static Field delim;
  
  @BeforeClass
  public static void init() {
    try {
      /* initializing reflected fields */
      src = FieldTokenizer.class.getDeclaredField("src");
      name = FieldTokenizer.class.getDeclaredField("name");
      tgt = FieldTokenizer.class.getDeclaredField("tgt");
      delim = FieldTokenizer.class.getDeclaredField("delim");
      
      src.setAccessible(true);
      name.setAccessible(true);
      tgt.setAccessible(true);
      delim.setAccessible(true);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
  }
  
  @Test
  public void testTransform1() {
    FieldTokenizer ft = new FieldTokenizer();
    HashMap data = new HashMap();
    
    /* Calling the tested method */
    ft.transform(data, null);
    
    assertTrue(data.size() == 0);
    
  }

  @Test
  public void testTransform2() {
    FieldTokenizer ft = new FieldTokenizer();
    Properties props = new Properties();
    HashMap data = new HashMap();
    data.put("src", "token1,token2");
    
    ft.configure("name", "src", "tgt", props, null);
    
    /* Calling the tested method */
    ft.transform(data, null);
    
    String expected = "token1;token2";
    String actual = data.get("tgt.0") + ";" + data.get("tgt.1");
    
    assertEquals(expected, actual);
  }
  
  @Test
  public void testConfigure() {
    FieldTokenizer ft = new FieldTokenizer();
    Properties props = new Properties();
    props.setProperty("delim", ";");
    
    try {
      /* Calling the tested method */
      ft.configure("name", "src", "tgt", props, null);
      
      String expected = "name,src,tgt,;";
      String actual = name.get(ft) + "," + src.get(ft) + "," + tgt.get(ft) + "," + delim.get(ft);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure() failed");
    }
  }

  @Test
  public void testGetSource() {
    FieldTokenizer ft = new FieldTokenizer();
    
    try {
      src.set(ft, "src");
      
      /* Calling the tested method & assert */
      assertEquals("src", ft.getSource());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetSource() failed");
    }
  }

  @Test
  public void testGetTarget() {
    FieldTokenizer ft = new FieldTokenizer();
    
    try {
      tgt.set(ft, "tgt");
      
      /* Calling the tested method & assert */
      assertEquals("tgt", ft.getTarget());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetTarget() failed");
    }
  }

  @Test
  public void testGetName() {
    FieldTokenizer ft = new FieldTokenizer();
    
    try {
      name.set(ft, "name");
      
      /* Calling the tested method & assert */
      assertEquals("name", ft.getName());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetName() failed");
    }
  }

}
