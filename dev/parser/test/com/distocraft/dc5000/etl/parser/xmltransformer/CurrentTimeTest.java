package com.distocraft.dc5000.etl.parser.xmltransformer;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author ejarsok
 *
 */

public class CurrentTimeTest {

  private static Field tgt;
  private static Field name;
  private static Field format;
  
  @BeforeClass
  public static void init() {
    try {
      /* initializing reflected fields */
      tgt = CurrentTime.class.getDeclaredField("tgt");
      name = CurrentTime.class.getDeclaredField("name");
      format = CurrentTime.class.getDeclaredField("format");
      
      tgt.setAccessible(true);
      name.setAccessible(true);
      format.setAccessible(true);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
  }
  
  @Test
  public void testTransform() {
    CurrentTime ct = new CurrentTime();
    Properties props = new Properties();
    props.setProperty("format", "MM/dd/yyyy");
    HashMap data = new HashMap();
    SimpleDateFormat sf = new SimpleDateFormat("MM/dd/yyyy");
    
    try {
      ct.configure("name", "src", "tgt", props, null);

      /* Calling the tested method */
      ct.transform(data, null);
      
      assertEquals(sf.format(new Date()), data.get("tgt"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure2() failed");
    }
  }

  @Test
  public void testConfigure1() {
    CurrentTime ct = new CurrentTime();
    Properties props = new Properties();
    
    try {
      /* Calling the tested method */
      ct.configure("name", "src", "tgt", props, null);
      fail("Should not execute this line, ConfigException expected. Parameter format has to be defined");
      
    } catch (ConfigException e) {
      // test passed
    }
  }

  @Test
  public void testConfigure2() {
    CurrentTime ct = new CurrentTime();
    Properties props = new Properties();
    props.setProperty("format", "MM/dd/yyyy");

    try {
      /* Calling the tested method */
      ct.configure("name", "src", "tgt", props, null);

      String expected = "name,tgt";
      String actual = name.get(ct) + "," + tgt.get(ct);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure2() failed");
    }
  }
  
  @Test
  public void testConfigure3() {
    CurrentTime ct = new CurrentTime();
    Properties props = new Properties();
    props.setProperty("format", "MM/dd/yyyy");
    props.setProperty("locale", "fi");
    props.setProperty("timezone", "GMT+0300");

    try {
      /* Calling the tested method */
      ct.configure("name", "src", "tgt", props, null);

      SimpleDateFormat sdf = (SimpleDateFormat) format.get(ct);
      
      String expected = "name,tgt,GMT+03:00";
      String actual = name.get(ct) + "," + tgt.get(ct) + "," + sdf.getTimeZone().getID();
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure3() failed");
    }
  }
  
  @Test
  public void testGetSource() {
    CurrentTime ct = new CurrentTime();
    
    try {
      /* Calling the tested method & assert */
      assertNull(ct.getSource());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetSource() failed");
    }
  }

  @Test
  public void testGetTarget() {
    CurrentTime ct = new CurrentTime();
    
    try {
      tgt.set(ct, "tgt");
      
      /* Calling the tested method & assert */
      assertEquals("tgt", ct.getTarget());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetTarget() failed");
    }
  }

  @Test
  public void testGetName() {
    CurrentTime ct = new CurrentTime();
    
    try {
      name.set(ct, "name");
      
      /* Calling the tested method & assert */
      assertEquals("name", ct.getName());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetName() failed");
    }
  }

}
