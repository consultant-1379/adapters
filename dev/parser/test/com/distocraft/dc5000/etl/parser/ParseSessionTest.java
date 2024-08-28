package com.distocraft.dc5000.etl.parser;

import static org.junit.Assert.*;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ssc.rockfactory.RockFactory;
import com.distocraft.dc5000.etl.parser.xmltransformer.DBTransformer;

/**
 * Test class for ParseSession.
 * 
 * @author EJAAVAH
 * 
 */
public class ParseSessionTest {

  private static ParseSession objUnderTest;

  private static Field transformerCache;

  private static Connection con = null;

  private static Statement stmt;

  private static RockFactory rockFactory;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    /* Reflecting fields used in testing */
    transformerCache = ParseSession.class.getDeclaredField("transformerCache");
    transformerCache.setAccessible(true);

    /* Creating connection to hsql database and create tables in there */
    try {
      Class.forName("org.hsqldb.jdbcDriver").newInstance();
      con = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    stmt = con.createStatement();
    stmt.execute("CREATE TABLE Tpactivation (TECHPACK_NAME VARCHAR(31), STATUS VARCHAR(31), "
        + "VERSIONID VARCHAR(31), TYPE VARCHAR(31))");
    stmt.execute("CREATE TABLE Transformer (TRANSFORMERID VARCHAR(31), VERSIONID VARCHAR(31), "
        + "DESCRIPTION VARCHAR(31))");
    stmt.execute("CREATE TABLE Transformation (TRANSFORMERID VARCHAR(31), ORDERNO BIGINT, TYPE VARCHAR(31), "
        + "SOURCE VARCHAR(31), TARGET VARCHAR(31), CONFIG VARCHAR(31), DESCRIPTION VARCHAR(31))");

    /* Initializing rockfactory */
    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Cleaning up after tests */
    stmt.execute("DROP TABLE Tpactivation");
    stmt.execute("DROP TABLE Transformer");
    stmt.execute("DROP TABLE Transformation");
    con = null;
    objUnderTest = null;
  }

  @Before
  public void setUp() throws Exception {

    /* Properties for ParseSession object */
    Properties psConf = new Properties();
    psConf.setProperty("testPropertyKey", "testPropertyValue");

    /* Initializing new parse session obect before each test */
    objUnderTest = new ParseSession(151, psConf);
  }

  @After
  public void tearDown() throws Exception {

    /* Cleaning up after each test */
    objUnderTest = null;
  }

  /**
   * Testing transformer getting.
   * 
   * @throws Exception
   */
  @Test
  public void testGetTransformer() throws Exception {

    /* Calling the tested method and asserting correct transformer is returned */
    assertEquals(DBTransformer.class, objUnderTest.getTransformer("tID", null, rockFactory).getClass());
  }

  /**
   * Testing transformer getting with null value.
   * 
   * @throws Exception
   */
  @Test
  public void testGetTransformerWithNullTID() throws Exception {

    /* Calling the tested method and asserting null is returned */
    assertEquals(null, objUnderTest.getTransformer(null, null, null));
  }

  /**
   * Testing transformer getting with null rockFactory object.
   * 
   * @throws Exception
   */
  @Test
  public void testGetTransformerWithNullRockfactory() throws Exception {

    /* Asserting that NullPointerException is thrown */
    try {
      objUnderTest.getTransformer("tID", null, null);
      fail("Test failed - NullPointerException expected as rockfactory object was null!");
    } catch (NullPointerException npe) {
      // Test Passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception thrown!\n" + e);
    }
  }

  /**
   * Testing property value retrieving by its key.
   * 
   * @throws Exception
   */
  @Test
  public void testGetParserParameter() throws Exception {

    /* Asserting that correct property is returned */
    assertEquals("testPropertyValue", objUnderTest.getParserParameter("testPropertyKey"));
  }

  /**
   * Testing property value retrieving with null key.
   * 
   * @throws Exception
   */
  @Test
  public void testGetParserParameterWithNullKey() throws Exception {

    /* Asserting that NullPointerException is thrown */
    try {
      objUnderTest.getParserParameter(null);
      fail("Test failed - NullPointerException expected as rockfactory object was null!");
    } catch (NullPointerException npe) {
      // Test Passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception thrown!\n" + e);
    }
  }
  
  /**
   * Testing getting session id.
   * 
   * @throws Exception
   */
  @Test
  public void testGetSessionID() throws Exception {

    assertEquals(151, objUnderTest.getSessionID());
  }
  
  /**
   * Testing clearing transformer cache.
   * 
   * @throws Exception
   */
  @Test
  public void testClear() throws Exception {
    
    /* Inserting test data to map for removal */
    HashMap testDataMap = new HashMap();
    testDataMap.put("dataTo", "beCleared");
    transformerCache.set(objUnderTest, testDataMap);
    
    /* Calling the tested method and asserting the map has been cleared */
    objUnderTest.clear();
    assertEquals("{}", transformerCache.get(objUnderTest).toString());
  }
}
