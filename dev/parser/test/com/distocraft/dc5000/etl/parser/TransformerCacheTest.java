package com.distocraft.dc5000.etl.parser;

import static org.junit.Assert.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.distocraft.dc5000.etl.parser.xmltransformer.DBTransformer;
import com.distocraft.dc5000.etl.parser.xmltransformer.Transformation;
import ssc.rockfactory.RockFactory;

/**
 * Testing the TransformerCache. Transformers with different transformations can
 * be loaded to map (memory) from database and used by get method.
 * 
 * @author EJAAVAH
 * 
 */
public class TransformerCacheTest {

  private static TransformerCache objUnderTest;

  private static Statement stmt;

  private static Connection con = null;

  private static Field transformers;

  private static RockFactory rockFactory;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    /* Creating connection to hsql database and create tables in there */
    try {
      Class.forName("org.hsqldb.jdbcDriver").newInstance();
      con = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    stmt = con.createStatement();
    stmt.execute("CREATE TABLE Tpactivation (TECHPACK_NAME VARCHAR(31), STATUS VARCHAR(31), "
        + "VERSIONID VARCHAR(31), TYPE VARCHAR(31), MODIFIED VARCHAR(31))");
    stmt.execute("CREATE TABLE Transformer (TRANSFORMERID VARCHAR(31), VERSIONID VARCHAR(31), "
        + "DESCRIPTION VARCHAR(31), TYPE VARCHAR(31))");
    stmt.execute("CREATE TABLE Transformation (TRANSFORMERID VARCHAR(31), ORDERNO BIGINT, TYPE VARCHAR(31), "
        + "SOURCE VARCHAR(31), TARGET VARCHAR(31), CONFIG VARCHAR(31), DESCRIPTION VARCHAR(31))");
    stmt.executeUpdate("INSERT INTO Tpactivation VALUES('tpForTransformerCacheTest', 'ACTIVE', 'versionid', 'type', '10')");
    stmt
        .executeUpdate("INSERT INTO Transformer VALUES('tpForTransformerCacheTest:transformerid1', 'versionid', 'description', 'type')");
    stmt
        .executeUpdate("INSERT INTO Transformer VALUES('tpForTransformerCacheTest:transformerid2', 'versionid', 'description', 'type')");

    /* Reflecting map including all transformers */
    transformers = TransformerCache.class.getDeclaredField("transformers");
    transformers.setAccessible(true);

    /* Initializing rockfactory */
    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Cleaning up after test */
    stmt.execute("DROP TABLE Tpactivation");
    stmt.execute("DROP TABLE Transformer");
    stmt.execute("DROP TABLE Transformation");
    con = null;
    objUnderTest = null;
  }

  @Before
  public void setUp() throws Exception {

    /* Initializing tested object before each test */
    objUnderTest = new TransformerCache();
  }

  @After
  public void tearDown() throws Exception {

    /* Cleaning up after each test */
    stmt.executeUpdate("DELETE FROM Transformation");
    objUnderTest = null;
  }

  /**
   * Testing transformer fetching from map of transformers with key given as
   * parameter.
   * 
   * @throws Exception
   */
  @Test
  public void testGetTransformer() throws Exception {

    /* Map of transformers */
    HashMap mapOfTransformers = new HashMap();
    mapOfTransformers.put("testTransformer", new TransformerMOC());
    transformers.set(objUnderTest, mapOfTransformers);

    /* Invoking the tested method */
    Transformer actualTransformer = objUnderTest.getTransformer("testTransformer");
    Transformer actualNullTransformer = objUnderTest.getTransformer("notFound");
    String actual = actualTransformer.getClass() + ", " + actualNullTransformer;
    assertEquals("class com.distocraft.dc5000.etl.parser.TransformerCacheTest$TransformerMOC, " + null, actual);
  }

  /**
   * Testing revalidating of transformers. Query is made to be able in order to
   * get all active transformers. All found transformers are put into map for
   * later use.
   * 
   * @throws Exception
   */
  @Test
  public void testRevalidate() throws Exception {

    /* Creating transformations for the transformers to database */
    stmt.executeUpdate("INSERT INTO Transformation VALUES('tpForTransformerCacheTest:transformerid1', 1, "
        + "'calculation', 'source', 'target', 'config', 'description')");
    stmt.executeUpdate("INSERT INTO Transformation VALUES('tpForTransformerCacheTest:transformerid1', 2, "
        + "'roundtime', 'source', 'target', 'config', 'description')");
    stmt.executeUpdate("INSERT INTO Transformation VALUES('tpForTransformerCacheTest:transformerid2', 3, "
        + "'switch', 'source', 'target', 'config', 'description')");

    /* Calling the tested method */
    objUnderTest.revalidate(rockFactory, null);

    /*
     * Asserting that the transformers with correct tranformations have been put
     * into the map
     */
    try {
      HashMap actualMap = (HashMap) transformers.get(objUnderTest);
      DBTransformer transformer1 = (DBTransformer) actualMap.get("tpForTransformerCacheTest:transformerid1");
      DBTransformer transformer2 = (DBTransformer) actualMap.get("tpForTransformerCacheTest:transformerid2");
      List listOfTransformations1 = transformer1.getTransformations();
      List listOfTransformations2 = transformer2.getTransformations();
      Transformation actualTransformation1 = (Transformation) listOfTransformations1.get(0);
      Transformation actualTransformation2 = (Transformation) listOfTransformations1.get(1);
      Transformation actualTransformation3 = (Transformation) listOfTransformations2.get(0);
      String actual = actualTransformation1.getName() + ", " + actualTransformation2.getName() + ", "
          + actualTransformation3.getName();
      assertEquals("calculation, roundtime, switch", actual);
    } catch (Exception e) {
      fail("Test failed - Transformers with correct transformations were not created!\n" + e);
    }
  }

  /**
   * Testing transformer revalidating. Creating new transformer object with
   * tranformations according to the database data of given techpack.
   * 
   * @throws Exception
   */
  @Test
  public void testRevalidateTransformer() throws Exception {

    /* Creating transformation for the transformer to database */
    stmt.executeUpdate("INSERT INTO Transformation VALUES('tpForTransformerCacheTest:transformerid1', 1, "
        + "'currenttime', 'source', 'target', 'config', 'description')");

    /*
     * Reflecting & invoking the tested method and asserting correct transformer
     * with correct transformations is returned
     */
    Method revalidateTransformer = objUnderTest.getClass().getDeclaredMethod("revalidateTransformer",
        new Class[] { String.class, RockFactory.class, RockFactory.class });
    revalidateTransformer.setAccessible(true);
    try {
      Transformer actualTransformer = (Transformer) revalidateTransformer.invoke(objUnderTest, new Object[] {
          "tpForTransformerCacheTest:transformerid1", null, rockFactory });
      List listOfTransformations = actualTransformer.getTransformations();
      Transformation actualTransformation = (Transformation) listOfTransformations.get(0);
      assertEquals("currenttime", actualTransformation.getName());
    } catch (Exception e) {
      fail("Test failed - Transformers with correct transformations were not created!\n" + e);
    }
  }

  /**
   * Testing updating of a certain transformer. This transformer is removed from
   * the transformer map and query to database is made to find an updated
   * version of this transformation.
   * 
   * @throws Exception
   */
  @Test
  public void testUpdateTransformer() throws Exception {

    /* Creating transformation for the transformer to database */
    stmt.executeUpdate("INSERT INTO Transformation VALUES('tpForTransformerCacheTest:transformerid1', 1, "
        + "'preappender', 'source', 'target', 'config', 'description')");

    /*
     * Adding transformers with transformations in to the transformer map which
     * will then be updated to database
     */
    Transformer testTransformer = new DBTransformer("tpForTransformerCacheTest:transformerid1", null, rockFactory);
    HashMap testTransformers = new HashMap();
    testTransformers.put("transformerid1:", testTransformer);
    transformers.set(objUnderTest, testTransformers);

    /* Update transformations for our test transformer */
    stmt.executeUpdate("DELETE FROM Transformation");
    stmt.executeUpdate("INSERT INTO Transformation VALUES('tpForTransformerCacheTest:transformerid1', 2, "
        + "'propertytokenizer', 'source', 'target', 'config', 'description')");
    
    /* Calling the tested method */
    objUnderTest.updateTransformer("tpForTransformerCacheTest", rockFactory, null);

    /* Asserting that the transformer has been updated with new transformation */
    try {
      HashMap actualTransformerMap = (HashMap) transformers.get(objUnderTest);
      Transformer actualTransformer = (Transformer) actualTransformerMap
          .get("tpForTransformerCacheTest:transformerid1");
      List listOfTransformations = actualTransformer.getTransformations();
      Transformation actualTransformation = (Transformation) listOfTransformations.get(0);
      assertEquals("propertytokenizer", actualTransformation.getName());
    } catch (Exception e) {
      fail("Test failed - Transformer was not updated!\n" + e);
    }
  }

  /**
   * This is a test transformer for TransformeCacheTest. It is a mock
   * implementing transformer interface and has no functionality.
   * 
   * @author EJAAVAH
   * 
   */
  class TransformerMOC implements Transformer {

    public TransformerMOC() {
    }

    public void addDebugger(ParserDebugger parserDebugger) {
    }

    public List getTransformations() {
      return null;
    }

    public void transform(Map data, Logger clog) throws Exception {
    }

  }
}
