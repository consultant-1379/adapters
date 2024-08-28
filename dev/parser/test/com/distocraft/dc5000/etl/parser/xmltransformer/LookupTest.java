package com.distocraft.dc5000.etl.parser.xmltransformer;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Pattern;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author ejarsok
 *
 */

public class LookupTest {

  private static Field src;
  
  private static Field name;
  
  private static Field tgt;
  
  private static Field ipatt;

  private static Field epatt;

  private static Field patt;
  
  @BeforeClass
  public static void init() {
    try {
      /* initializing reflected fields */
      src = Lookup.class.getDeclaredField("src");
      name = Lookup.class.getDeclaredField("name");
      tgt = Lookup.class.getDeclaredField("tgt");
      ipatt = Lookup.class.getDeclaredField("ipatt");
      epatt = Lookup.class.getDeclaredField("epatt");
      patt = Lookup.class.getDeclaredField("patt");
      
      src.setAccessible(true);
      name.setAccessible(true);
      tgt.setAccessible(true);
      ipatt.setAccessible(true);
      epatt.setAccessible(true);
      patt.setAccessible(true);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init failed");
    }
  }
  
  @Test
  public void testTransform1() {
    Lookup l = new Lookup();
    HashMap data = new HashMap();
    
    /* Calling the tested method */
    l.transform(data, null);
    
    assertTrue(data.size() == 0);
  }
  
  @Test
  public void testTransform2() {
    Lookup l = new Lookup();
    Properties props = new Properties();
    props.setProperty("ipattern", "a*b");
    HashMap data = new HashMap();
    data.put("src", "aaaab");
    
    try {
      l.configure("name", "src", "tgt", props, null);
      
      /* Calling the tested method */
      l.transform(data, null);
      
      assertEquals("aaaab", data.get("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform2() failed");
    }
  }
  
  @Test
  public void testTransform3() {
    Lookup l = new Lookup();
    Properties props = new Properties();
    props.setProperty("epattern", "a*b");
    HashMap data = new HashMap();
    data.put("src", "cccd");
    
    try {
      l.configure("name", "src", "tgt", props, null);
      
      /* Calling the tested method */
      l.transform(data, null);
      
      assertEquals("cccd", data.get("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform3() failed");
    }
  }
  
  @Test
  public void testTransform4() {
    Lookup l = new Lookup();
    Properties props = new Properties();
    props.setProperty("epattern", "a*b");
    HashMap data = new HashMap();
    data.put("src", "aaabcd");
    
    try {
      l.configure("name", "src", "tgt", props, null);
      
      /* Calling the tested method */
      l.transform(data, null);
      
      assertEquals("cd", data.get("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform4() failed");
    }
  }
  
  @Test
  public void testTransform5() {
    Lookup l = new Lookup();
    Properties props = new Properties();
    props.setProperty("pattern", "a*b(ab)");
    HashMap data = new HashMap();
    data.put("src", "aaaabab");
    
    try {
      l.configure("name", "src", "tgt", props, null);
      
      /* Calling the tested method */
      l.transform(data, null);
      
      assertEquals("ab", data.get("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform5() failed");
    }
  }

  @Test
  public void testConfigure1() {
    Lookup l = new Lookup();
    Properties props = new Properties();
    props.setProperty("ipattern", "IP");
    props.setProperty("epattern", "EP");
    props.setProperty("pattern", "P");
    
    try {
      /* Calling the tested method */
      l.configure("name", "srcKey", "tgtKey", props, null);
      
      String expected = "name,srcKey,tgtKey,IP,EP,P";
      String actual = name.get(l) + "," + src.get(l) + "," + tgt.get(l) + "," + ipatt.get(l) + "," + epatt.get(l) + "," + patt.get(l);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure1() failed");
    }
  }
  
  @Test
  public void testConfigure2() {
    Lookup l = new Lookup();
    Properties props = new Properties();
    
    try {
      /* Calling the tested method */
      l.configure("name", "srcKey", "tgtKey", props, null);
      fail("Should not execute this line, ConfigException expected. One of these parameters must be defined: ipattern, epattern or pattern");
      
    } catch (Exception e) {
      // test passed
    }
  }

  @Test
  public void testGetSource() {
    Lookup l = new Lookup();
    
    try {
      src.set(l, "src");
      
      /* Calling the tested method & assert */
      assertEquals("src", l.getSource());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetSource() failed");
    }
  }

  @Test
  public void testGetTarget() {
    Lookup l = new Lookup();
    
    try {
      tgt.set(l, "tgt");
      
      /* Calling the tested method & assert */
      assertEquals("tgt", l.getTarget());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetTarget() failed");
    }
  }

  @Test
  public void testGetName() {
    Lookup l = new Lookup();
    
    try {
      name.set(l, "name");
      
      /* Calling the tested method & assert */
      assertEquals("name", l.getName());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetName() failed");
    }
  }

}
