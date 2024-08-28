package com.distocraft.dc5000.etl.parser.xmltransformer;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author ejarsok
 *
 */

public class LimitedSizeStringCacheTest {

  private static Field keys;
  
  private static Field vals;
  
  @BeforeClass
  public static void init() {
    try {
      /* initializing reflected fields */
      keys = LimitedSizeStringCache.class.getDeclaredField("keys");
      vals = LimitedSizeStringCache.class.getDeclaredField("vals");
      
      keys.setAccessible(true);
      vals.setAccessible(true);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
  }
  
  @Test
  public void testGet1() {
    LimitedSizeStringCache lc = new LimitedSizeStringCache();
    
    /* Calling the tested method & assert */
    assertEquals(null, lc.get(null));
  }
  
  @Test
  public void testGet2() {
    LimitedSizeStringCache lc = new LimitedSizeStringCache();
    
    /* Calling the tested method & assert */
    assertEquals(null, lc.get("key"));
  }
  
  @Test
  public void testGet3() {
    LimitedSizeStringCache lc = new LimitedSizeStringCache();
    
    String[] sa = new String[12];
    sa[5] = "key";
    
    try {
      keys.set(lc, sa);
      vals.set(lc, sa);
      
      /* Calling the tested method & assert */
      assertEquals("key", lc.get("key"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGet3() failed");
    }
  }

  @Test
  public void testPut() {
    LimitedSizeStringCache lc = new LimitedSizeStringCache();
    
    /* Calling the tested method */
    lc.put("key", "value");
    
    try {
      String[] k = (String[]) keys.get(lc);
      Object[] v = (Object[]) vals.get(lc);
      
      String expected = "key,value";
      String actual = k[11] + "," + v[11];
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testPut() failed");
    }
  }

}
