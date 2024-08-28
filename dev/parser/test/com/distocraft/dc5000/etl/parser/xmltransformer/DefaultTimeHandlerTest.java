package com.distocraft.dc5000.etl.parser.xmltransformer;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * @author ejarsok
 *
 */

public class DefaultTimeHandlerTest {

  private static Field src;
  
  private static Field name;
  
  private static Field oldFormat;

  private static Field oldLocale;

  private static Field oldZone;

  private static Field newLocale;

  private static Field newZone;
  
  private static Field fcache;
  
  @BeforeClass
  public static void init() {
    try {
      /* initializing reflected fields */
      src = DefaultTimeHandler.class.getDeclaredField("src");
      name = DefaultTimeHandler.class.getDeclaredField("name");
      oldFormat = DefaultTimeHandler.class.getDeclaredField("oldFormat");
      oldLocale = DefaultTimeHandler.class.getDeclaredField("oldLocale");
      oldZone = DefaultTimeHandler.class.getDeclaredField("oldZone");
      newLocale = DefaultTimeHandler.class.getDeclaredField("newLocale");
      newZone = DefaultTimeHandler.class.getDeclaredField("newZone");
      fcache = DefaultTimeHandler.class.getDeclaredField("cache");
      
      src.setAccessible(true);
      name.setAccessible(true);
      oldFormat.setAccessible(true);
      oldLocale.setAccessible(true);
      oldZone.setAccessible(true);
      newLocale.setAccessible(true);
      newZone.setAccessible(true);
      fcache.setAccessible(true);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
  }
  
  @Test
  public void testTransform1() {
    DefaultTimeHandler dth = new DefaultTimeHandler();
    HashMap data = new HashMap();
    data.put("src", "09/09/2008");
    Properties props = new Properties();
    props.setProperty("oldformat", "dd/MM/yyyy");
    props.setProperty("oldlocale", "fi");
    props.setProperty("oldtimezone", "GMT+0300");
    props.setProperty("newlocale", "fi");
    props.setProperty("newtimezone", "GMT+0300");
    
    Logger log = Logger.getLogger("log");
    
    try {
      dth.configure("name", "src", "tgt", props, log);
      
      /* Calling the tested method */
      dth.transform(data, log);
      
      String expected = "2008 9 9 0 0 2008-09-09 2008-09-09 00:00:00";
      String actual = data.get("YEAR_ID") + " " + data.get("MONTH_ID") + " " + data.get("DAY_ID") + " " + data.get("HOUR_ID") + " " + 
                      data.get("MIN_ID") + " " + data.get("DATE_ID") + " " + data.get("DATETIME_ID");
      
      assertEquals(expected, actual);
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform1() failed");
    }
  }
  
  @Test
  public void testTransform2() {
    DefaultTimeHandler dth = new DefaultTimeHandler();
    LimitedSizeStringCache cache = new LimitedSizeStringCache();
    HashMap data = new HashMap();
    data.put("src", "08/08/2008");
    Properties props = new Properties();
    props.setProperty("oldformat", "dd/MM/yyyy");
    
    Logger log = Logger.getLogger("log");
    
    String[] forcache = new String[7];
    forcache[0] = "2008";
    forcache[1] = "8";
    forcache[2] = "8";
    forcache[3] = "0";
    forcache[4] = "0";
    forcache[5] = "2008-08-08";
    forcache[6] = "2008-08-08 00:00:00";
    
    cache.put("08/08/2008", forcache);
        
    try {
      /* inserting values in cache */
      fcache.set(dth, cache);
      
      dth.configure("name", "src", "tgt", props, log);
      
      /* Calling the tested method */
      dth.transform(data, log);
      
      String expected = "2008 8 8 0 0 2008-08-08 2008-08-08 00:00:00";
      String actual = data.get("YEAR_ID") + " " + data.get("MONTH_ID") + " " + data.get("DAY_ID") + " " + data.get("HOUR_ID") + " " + 
                      data.get("MIN_ID") + " " + data.get("DATE_ID") + " " + data.get("DATETIME_ID");
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTransform2() failed");
    }
  }

  @Test
  public void testConfigure1() {
    DefaultTimeHandler dth = new DefaultTimeHandler();
    Properties props = new Properties();
    
    try {
      /* Calling the tested method */
      dth.configure("name", "src", "tgt", props, null);
      fail("Should not execute this line, ConfigException expected. Parameter oldformat has to be defined");
      
    } catch (ConfigException e) {
      // test passed
    }
  }
  
  @Test
  public void testConfigure2() {
    DefaultTimeHandler dth = new DefaultTimeHandler();
    Properties props = new Properties();
    props.setProperty("oldformat", "of");
    props.setProperty("oldlocale", "ol");
    props.setProperty("oldtimezone", "otz");
    props.setProperty("newlocale", "nl");
    props.setProperty("newtimezone", "ntz");
        
    try {
      /* Calling the tested method */
      dth.configure("name", "src", "tgt", props, null);
      
      String expected = "name,src,of,ol,otz,nl,ntz";
      String actual = name.get(dth) + "," + src.get(dth) + "," + oldFormat.get(dth) + "," + oldLocale.get(dth) + "," + 
                      oldZone.get(dth) + "," + newLocale.get(dth) + "," + newZone.get(dth);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure2() failed");
    }
  }

  @Test
  public void testGetSource() {
    DefaultTimeHandler dth = new DefaultTimeHandler();
    
    try {
      src.set(dth, "src");
      
      /* Calling the tested method & assert */
      assertEquals("src", dth.getSource());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetSource() failed");
    }
  }

  @Test
  public void testGetTarget() {
    DefaultTimeHandler dth = new DefaultTimeHandler();
    
    try {
      /* Calling the tested method & assert */
      assertEquals("YEAR_ID,MONTH_ID,DAY_ID,HOUR_ID,MIN_ID,DATETIME_ID,DATE_ID", dth.getTarget());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetTarget() failed");
    }
  }

  @Test
  public void testGetName() {
    DefaultTimeHandler dth = new DefaultTimeHandler();
    
    try {
      name.set(dth, "name");
      
      /* Calling the tested method & assert */
      assertEquals("name", dth.getName());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetName() failed");
    }
  }
}
