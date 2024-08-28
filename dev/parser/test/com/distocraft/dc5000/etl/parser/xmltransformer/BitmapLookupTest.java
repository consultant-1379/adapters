package com.distocraft.dc5000.etl.parser.xmltransformer;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import ssc.rockfactory.RockFactory;

/**
 * 
 * @author ejarsok
 *
 */

public class BitmapLookupTest {

  private static Field src;

  private static Field tgt;
  
  private static Field name;
  
  private static Field lookupMap;
  
  private static RockFactory rockFact;
  
  @BeforeClass
  public static void init() {
    Statement stm;
    
    try {
      /* initializing reflected fields */
      src = BitmapLookup.class.getDeclaredField("src");
      tgt = BitmapLookup.class.getDeclaredField("tgt");
      name = BitmapLookup.class.getDeclaredField("name");
      lookupMap = BitmapLookup.class.getDeclaredField("lookupMap");
      
      src.setAccessible(true);
      tgt.setAccessible(true);
      name.setAccessible(true);
      lookupMap.setAccessible(true);
      
      
      Class.forName("org.hsqldb.jdbcDriver");

      Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "");
      stm = c.createStatement();

      stm.execute("CREATE TABLE blookup (SERVICE_NAME VARCHAR(20), SERVICE_NUMBER VARCHAR(20))");

      stm.executeUpdate("INSERT INTO blookup VALUES('blookup1', '1')");
      stm.executeUpdate("INSERT INTO blookup VALUES('blookup2', '2')");

      
      rockFact = new RockFactory("jdbc:hsqldb:mem:testdb", "SA", "", "org.hsqldb.jdbcDriver", "con", true, -1);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
  }
  
  @Test
  public void testTransform() {
    BitmapLookup bl = new BitmapLookup(rockFact);
    Properties props = new Properties();
    props.setProperty("tablename", "blookup");
    Logger log = Logger.getLogger("log");
    HashMap data = new HashMap();
    data.put("src", "3");
    
    try {
      bl.configure("name", "src", "tgt", props, log);
      
      /* Calling the tested method */
      bl.transform(data, null);
      
      String expected = "blookup1,blookup2";
      String actual = (String) data.get("tgt");
      
      assertEquals(expected, actual);
      
    } catch (ConfigException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testConfigure1() {
    BitmapLookup bl = new BitmapLookup(null);
    Properties props = new Properties();
    Logger log = Logger.getLogger("log");
    
    try {
      /* Calling the tested method */
      bl.configure("name", "src", "tgt", props, log);
      fail("Should not execute this line, ConfigException failed. Parameter tablename has to be defined");
      
    } catch (ConfigException e) {
      // test passed
    }
  }
  
  @Test
  public void testConfigure2() {
    BitmapLookup bl = new BitmapLookup(rockFact);
    Properties props = new Properties();
    props.setProperty("tablename", "blookup");
    Logger log = Logger.getLogger("log");
    
    try {
      /* Calling the tested method */
      bl.configure("name", "src", "tgt", props, log);
      
      LinkedHashMap lhm = (LinkedHashMap) lookupMap.get(bl);
      
      String expected = "blookup1,blookup2";
      String actual = lhm.get("1") + "," +lhm.get("2");
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure2() failed");
    }
  }

  @Test
  public void testGetSource() {
    BitmapLookup bl = new BitmapLookup(null);
    
    try {
      src.set(bl, "src");
      
      /* Calling the tested method & assert */
      assertEquals("src", bl.getSource());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetSource() failed");
    }
  }

  @Test
  public void testGetTarget() {
    BitmapLookup bl = new BitmapLookup(null);
    
    try {
      tgt.set(bl, "tgt");
      
      /* Calling the tested method & assert */
      assertEquals("tgt", bl.getTarget());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetTarget() failed");
    }
  }

  @Test
  public void testGetName() {
    BitmapLookup bl = new BitmapLookup(null);
    
    try {
      name.set(bl, "name");
      
      /* Calling the tested method & assert */
      assertEquals("name", bl.getName());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetName() failed");
    }
  }

}
