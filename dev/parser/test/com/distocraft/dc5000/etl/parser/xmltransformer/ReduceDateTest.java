package com.distocraft.dc5000.etl.parser.xmltransformer;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
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

public class ReduceDateTest {

  private static Field src;
  
  private static Field tgt;
  
  private static Field name;
  
  private static Field format;

  private static Field seconds;
  
  private static Field secondsVar;
  
  @BeforeClass
  public static void init() {
    try {
      /*initializing reflected methods*/
      src = ReduceDate.class.getDeclaredField("src");
      tgt = ReduceDate.class.getDeclaredField("tgt");
      name = ReduceDate.class.getDeclaredField("name");
      format = ReduceDate.class.getDeclaredField("format");
      seconds = ReduceDate.class.getDeclaredField("seconds");
      secondsVar = ReduceDate.class.getDeclaredField("secondsVar");
      
      src.setAccessible(true);
      tgt.setAccessible(true);
      name.setAccessible(true);
      format.setAccessible(true);
      seconds.setAccessible(true);
      secondsVar.setAccessible(true);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
  }
  
  @Test
  public void testTransform1() {
    ReduceDate rd = new ReduceDate();
    Properties props = new Properties();
    props.setProperty("secondsfield", "sf");
    props.setProperty("seconds", "10");
    props.setProperty("format", "F");
    Logger clog = Logger.getLogger("clog");
    
    HashMap data = new HashMap();
    
    try {
      rd.configure("name", "src", "tgt", props, clog);

      /* Calling the tested method & assert */
      rd.transform(data, clog);
      
      assertFalse("false expected", data.containsKey("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform1() failed");
    }
  }
  
  @Test
  public void testTransform2() {
    ReduceDate rd = new ReduceDate();
    Properties props = new Properties();
    props.setProperty("secondsfield", "sf");
    props.setProperty("seconds", "10");
    props.setProperty("format", "MM/dd/yyyy");
    Logger clog = Logger.getLogger("clog");
    
    HashMap data = new HashMap();
    data.put("src", "10/03/2008");
    data.put("sf", "600");
    
    try {
      rd.configure("name", "src", "tgt", props, clog);

      /* Calling the tested method & assert */
      rd.transform(data, clog);
      
      assertEquals("10/02/2008", data.get("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform2() failed");
    }
  }
  
  @Test
  public void testTransform3() {
    ReduceDate rd = new ReduceDate();
    Properties props = new Properties();
    props.setProperty("seconds", "600");
    props.setProperty("format", "MM/dd/yyyy");
    Logger clog = Logger.getLogger("clog");
    
    HashMap data = new HashMap();
    data.put("src", "10/03/2008");
    
    try {
      rd.configure("name", "src", "tgt", props, clog);

      /* Calling the tested method & assert */
      rd.transform(data, clog);
      
      assertEquals("10/02/2008", data.get("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform3() failed");
    }
  }

  @Test
  public void testConfigure1() {
    ReduceDate rd = new ReduceDate();
    Properties props = new Properties();
    props.setProperty("secondsfield", "sf");
    props.setProperty("seconds", "s");
    Logger clog = Logger.getLogger("clog");
    
    try {
      /* Calling the tested method & assert */
      rd.configure("name", "src", "tgt", props, clog);
      fail("Should not execute this line, ConfigException expected");
      
    } catch (ConfigException e) {
      // test passed
    }
  }
  
  @Test
  public void testConfigure2() {
    ReduceDate rd = new ReduceDate();
    Properties props = new Properties();
    props.setProperty("format", "F");
    Logger clog = Logger.getLogger("clog");
    
    try {
      /* Calling the tested method & assert */
      rd.configure("name", "src", "tgt", props, clog);
      fail("Should not execute this line, ConfigException expected");
      
    } catch (ConfigException e) {
      // test passed
    }
  }
  
  @Test
  public void testConfigure3() {
    ReduceDate rd = new ReduceDate();
    Properties props = new Properties();
    props.setProperty("format", "F");
    props.setProperty("secondsfield", "sf");
    props.setProperty("seconds", "10");
    Logger clog = Logger.getLogger("clog");
    
    try {
      /* Calling the tested method & assert */
      rd.configure("name", "src", "tgt", props, clog);

      String expected = "name,src,tgt,F,10,sf";
      String actual= name.get(rd) + "," + src.get(rd) + "," + tgt.get(rd) + "," + format.get(rd) + "," + seconds.get(rd) +
                    "," + secondsVar.get(rd);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure3() failed");
    }
  }
  
  @Test
  public void testConfigure4() {
    ReduceDate rd = new ReduceDate();
    Properties props = new Properties();
    props.setProperty("format", "F");
    props.setProperty("secondsfield", "sf");
    props.setProperty("seconds", "number");
    Logger clog = Logger.getLogger("clog");
    
    try {
      /* Calling the tested method */
      rd.configure("name", "src", "tgt", props, clog);

      String expected = "name,src,tgt,F,10,sf";
      String actual= name.get(rd) + "," + src.get(rd) + "," + tgt.get(rd) + "," + format.get(rd) + "," + seconds.get(rd) +
                    "," + secondsVar.get(rd);
      
      assertEquals(expected, actual);
      fail("Should not execute this line, ConfigException expected");
      
    } catch (Exception e) {
      // test passed
    }
  }
  
  @Test
  public void testConfigure5() {
	    ReduceDate rd = new ReduceDate();
	    Properties props = new Properties();
	    props.setProperty("format", "F");
	    props.setProperty("secondsfield", "sf");
	    Logger clog = Logger.getLogger("clog");
	    
	    try {
	      /* Calling the tested method & assert */
	      rd.configure("name", "src", "tgt", props, clog);

	      String expected = "name,src,tgt,F,sf";
	      String actual= name.get(rd) + "," + src.get(rd) + "," + tgt.get(rd) + "," + format.get(rd) +
	                    "," + secondsVar.get(rd);
	      
	      assertEquals(expected, actual);
	      
	    } catch (Exception e) {
	      e.printStackTrace();
	      fail("testConfigure5() failed");
	    }
	  }

  @Test
  public void testGetSource() {
    ReduceDate rd = new ReduceDate();
    
    try {
      src.set(rd, "src");
      
      /* Calling the tested method & assert */
      assertEquals("src", rd.getSource());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetSource() failed");
    }
  }

  @Test
  public void testGetTarget() {
    ReduceDate rd = new ReduceDate();
    
    try {
      tgt.set(rd, "tgt");
      
      /* Calling the tested method & assert */
      assertEquals("tgt", rd.getTarget());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetTarget() failed");
    }
  }

  @Test
  public void testGetName() {
    ReduceDate rd = new ReduceDate();
    
    try {
      name.set(rd, "name");
      
      /* Calling the tested method & assert */
      assertEquals("name", rd.getName());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetName() failed");
    }
  }

}
