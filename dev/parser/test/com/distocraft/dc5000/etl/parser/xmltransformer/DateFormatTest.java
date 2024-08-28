package com.distocraft.dc5000.etl.parser.xmltransformer;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
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

public class DateFormatTest {

  private static Field src;
  
  private static Field name;
  
  private static Field tgt;
  
  private static Field oldLocale;

  private static Field oldFormat;

  private static Field oldTimezone;

  private static Field oldFormatPar;

  private static Field oldLocalePar;

  private static Field oldTimezonePar;

  private static Field newLocale;

  private static Field newFormat;

  private static Field newTimezone;

  private static Field newFormatPar;

  private static Field newLocalePar;

  private static Field newTimezonePar;
  
  @BeforeClass
  public static void init() {
    try {
      /* initializing reflected fields */
      src = DateFormat.class.getDeclaredField("src");
      name = DateFormat.class.getDeclaredField("name");
      tgt = DateFormat.class.getDeclaredField("tgt");
      oldLocale = DateFormat.class.getDeclaredField("oldLocale");
      oldFormat = DateFormat.class.getDeclaredField("oldFormat");
      oldTimezone = DateFormat.class.getDeclaredField("oldTimezone");
      oldFormatPar = DateFormat.class.getDeclaredField("oldFormatPar");
      oldLocalePar = DateFormat.class.getDeclaredField("oldLocalePar");
      oldTimezonePar = DateFormat.class.getDeclaredField("oldTimezonePar");
      newLocale = DateFormat.class.getDeclaredField("newLocale");
      newFormat = DateFormat.class.getDeclaredField("newFormat");
      newTimezone = DateFormat.class.getDeclaredField("newTimezone");
      newFormatPar = DateFormat.class.getDeclaredField("newFormatPar");
      newLocalePar = DateFormat.class.getDeclaredField("newLocalePar");
      newTimezonePar = DateFormat.class.getDeclaredField("newTimezonePar");
      
      src.setAccessible(true);
      name.setAccessible(true);
      tgt.setAccessible(true);
      oldLocale.setAccessible(true);
      oldFormat.setAccessible(true);
      oldTimezone.setAccessible(true);
      oldFormatPar.setAccessible(true);
      oldLocalePar.setAccessible(true);
      oldTimezonePar.setAccessible(true);
      newLocale.setAccessible(true);
      newFormat.setAccessible(true);
      newTimezone.setAccessible(true);
      newFormatPar.setAccessible(true);
      newLocalePar.setAccessible(true);
      newTimezonePar.setAccessible(true);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
  }
  
  @Test
  public void testTransform1() {
    DateFormat df = new DateFormat();
    Logger log = Logger.getLogger("log");
    Properties props = new Properties();
    props.setProperty("oldformatfield", "off");
    
    HashMap data = new HashMap();
    data.put("src", "input");
    
    try {
      df.configure("name", "src", "tgt", props, log);
      
      /* Calling the tested method */
      df.transform(data, log);
      fail("Should not execute this line");
      
    } catch (Exception e) {
      // test passed
    }
  }
  
  @Test
  public void testTransform2() {
    DateFormat df = new DateFormat();
    Logger log = Logger.getLogger("log");
    Properties props = new Properties();
    props.setProperty("newformatfield", "off");
    
    HashMap data = new HashMap();
    data.put("src", "input");
    
    try {
      df.configure("name", "src", "tgt", props, log);
      
      /* Calling the tested method */
      df.transform(data, log);
      fail("Should not execute this line");
      
    } catch (Exception e) {
      // test passed
    }
  }
  
  @Test
  public void testTransform3() {
    DateFormat df = new DateFormat();
    Logger log = Logger.getLogger("log");
    Properties props = new Properties();
    props.setProperty("newtimezonefield", "nzf");
    props.setProperty("oldtimezonefield", "ozf");
    props.setProperty("oldformat", "yyyy/MM/dd");
    props.setProperty("newformat", "dd/MM/yyyy");
    props.setProperty("newlocalefield", "nlf");
    props.setProperty("oldlocalefield", "olf");
    
    HashMap data = new HashMap();
    data.put("src", "2008/09/09");
    data.put("nzf", "+0300");
    data.put("ozf", "+0200");
    data.put("nlf", "fi");
    data.put("olf", "no");
    
    try {
      df.configure("name", "src", "tgt", props, log);
      
      /* Calling the tested method */
      df.transform(data, log);
      
      assertEquals("09/09/2008", data.get("tgt"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTransform1() failed");
    }
  }

  @Test
  public void testTransform4() {
    final String JVM_TIMEZONE = (new SimpleDateFormat("Z")).format(new Date());
    DateFormat df = new DateFormat();
    Logger log = Logger.getLogger("log");
    Properties props = new Properties();
    props.setProperty("oldformat", "yyyy-MM-dd HH:mm");
    props.setProperty("newformat", "yyyy-MM-dd HH:mm:ss");
    props.setProperty("oldtimezonefield", "DC_TIMEZONE");
    props.setProperty("newtimezone", "+0000");
    
    HashMap data = new HashMap();
    data.put("DC_TIMEZONE", "+0200");
    data.put("src", "2011-01-24 16:30:00");
    
    try {
      df.configure("name", "src", "tgt", props, log);
      
      /* Calling the tested method */
      df.transform(data, log);
      
      assertEquals("2011-01-24 14:30:00", data.get("tgt"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTransform1() failed");
    }
  }

  @Test
  public void testConfigure() {
    DateFormat df = new DateFormat();
    Properties props = new Properties();
    props.setProperty("newformatfield", "nff");
    props.setProperty("oldformatfield", "off");
    props.setProperty("newlocalefield", "nlf");
    props.setProperty("oldlocalefield", "olf");
    props.setProperty("newtimezonefield", "nzf");
    props.setProperty("oldtimezonefield", "ozf");
    props.setProperty("oldformat", "of");
    props.setProperty("newformat", "nf");
    props.setProperty("oldlocale", "ol");
    props.setProperty("oldtimezone", "oz");
    props.setProperty("newlocale", "nl");
    props.setProperty("newtimezone", "nz");
        
    try {
      /* Calling the tested method */
      df.configure("name", "src", "tgt", props, null);
      
      String expected = "name,src,tgt,nff,off,nlf,olf,nzf,ozf,of,nf,ol,oz,nl,nz";
      String actual = name.get(df) + "," + src.get(df) + "," + tgt.get(df) + "," + newFormatPar.get(df) + "," + oldFormatPar.get(df)
                      + "," + newLocalePar.get(df) + "," + oldLocalePar.get(df) + "," + newTimezonePar.get(df) + "," +
                      oldTimezonePar.get(df) + "," + oldFormat.get(df) + "," + newFormat.get(df) + "," + oldLocale.get(df) + "," +
                      oldTimezone.get(df) + "," + newLocale.get(df) + "," + newTimezone.get(df);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure() failed");
    }
  }

  @Test
  public void testGetSource() {
    DateFormat df = new DateFormat();
    
    try {
      src.set(df, "src");
      
      /* Calling the tested method & assert */
      assertEquals("src", df.getSource());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetSource() failed");
    }
  }

  @Test
  public void testGetTarget() {
    DateFormat df = new DateFormat();
    
    try {
      tgt.set(df, "tgt");
      
      /* Calling the tested method & assert */
      assertEquals("tgt", df.getTarget());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetTarget() failed");
    }
  }

  @Test
  public void testGetName() {
    DateFormat df = new DateFormat();
    
    try {
      name.set(df, "name");
      
      /* Calling the tested method & assert */
      assertEquals("name", df.getName());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetName() failed");
    }
  }

}
