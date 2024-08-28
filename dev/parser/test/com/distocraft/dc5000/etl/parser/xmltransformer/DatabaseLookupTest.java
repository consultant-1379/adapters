package com.distocraft.dc5000.etl.parser.xmltransformer;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.cache.DBLookupCache;

/**
 * 
 * @author ejarsok
 *
 */

public class DatabaseLookupTest {
  
  private static Statement stm;

  private static Field sql;
  
  private static Field src;
  
  private static Field name;
  
  private static Field tgt;
  
  private static RockFactory rockFact;
  
  @BeforeClass
  public static void init() {
    
    try {
      /* initializing reflected fields */
      sql = DatabaseLookup.class.getDeclaredField("sql");
      src = DatabaseLookup.class.getDeclaredField("src");
      name = DatabaseLookup.class.getDeclaredField("name");
      tgt = DatabaseLookup.class.getDeclaredField("tgt");
      
      sql.setAccessible(true);
      src.setAccessible(true);
      name.setAccessible(true);
      tgt.setAccessible(true);
      
      
      Class.forName("org.hsqldb.jdbcDriver");

      Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "");
      stm = c.createStatement();
      
      stm.execute("CREATE TABLE Meta_databases (USERNAME VARCHAR(31), VERSION_NUMBER VARCHAR(31), "
          + "TYPE_NAME VARCHAR(31), CONNECTION_ID VARCHAR(31), CONNECTION_NAME VARCHAR(31), "
          + "CONNECTION_STRING VARCHAR(31), PASSWORD VARCHAR(31), DESCRIPTION VARCHAR(31), DRIVER_NAME VARCHAR(31), "
          + "DB_LINK_NAME VARCHAR(31))");

      stm.executeUpdate("INSERT INTO Meta_databases VALUES('SA', '1', 'USER', '1', 'dwh', "
          + "'jdbc:hsqldb:mem:testdb', '', 'description', 'org.hsqldb.jdbcDriver', 'dblinkname')");

      
      rockFact = new RockFactory("jdbc:hsqldb:mem:testdb", "SA", "", "org.hsqldb.jdbcDriver", "con", true, -1);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
  }
  
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    
    /* Cleaning up after every test */
    stm.execute("DROP TABLE Meta_databases");
  }
  
  @Test
  public void testTransform() {
    DatabaseLookup dl = new DatabaseLookup();
    Properties props = new Properties();
    props.setProperty("sql", "select USERNAME, CONNECTION_NAME from Meta_databases");
    Logger log = Logger.getLogger("log");
    DBLookupCache.initialize(rockFact);
    
    HashMap data = new HashMap();
    data.put("src", "SA");
    
    try {
      dl.configure("name", "src", "tgt", props, log);
      
      /* Calling the tested method */
      dl.transform(data, log);
      
      assertEquals("dwh", data.get("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform() failed");
    }
  }

  @Test
  public void testConfigure() {
    DatabaseLookup dl = new DatabaseLookup();
    Properties props = new Properties();
    props.setProperty("sql", "select USERNAME, CONNECTION_NAME from Meta_databases");
    Logger log = Logger.getLogger("log");
    DBLookupCache.initialize(rockFact);
    
    try {
      /* Calling the tested method */
      dl.configure("name", "src", "tgt", props, log);
      
      String expected = "name,src,tgt,select USERNAME, CONNECTION_NAME from Meta_databases";
      String actual = (String) name.get(dl) + "," + src.get(dl) + "," + tgt.get(dl) + "," + sql.get(dl);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure() failed");
    }
  }

  @Test
  public void testGetSource() {
    DatabaseLookup dl = new DatabaseLookup();
    
    try {
      src.set(dl, "src");
      
      /* Calling the tested method & assert */
      assertEquals("src", dl.getSource());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetSource() failed");
    }
  }

  @Test
  public void testGetTarget() {
    DatabaseLookup dl = new DatabaseLookup();
    
    try {
      tgt.set(dl, "tgt");
      
      /* Calling the tested method & assert */
      assertEquals("tgt", dl.getTarget());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetTarget() failed");
    }
  }

  @Test
  public void testGetName() {
    DatabaseLookup dl = new DatabaseLookup();
    
    try {
      name.set(dl, "name");
      
      /* Calling the tested method & assert */
      assertEquals("name", dl.getName());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetName() failed");
    }
  }

}
