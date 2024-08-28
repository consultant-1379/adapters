package com.distocraft.dc5000.etl.parser;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ssc.rockfactory.RockFactory;
import com.distocraft.dc5000.common.AdapterLog;
import com.distocraft.dc5000.common.StaticProperties;

/**
 * Test class for StandaloneMain.
 * 
 * @author EJAAVAH
 * 
 */
public class StandaloneMainTest {

  private static StandaloneMain objUnderTest;

  private static Connection con = null;

  private static Statement stmt;

  private static Field urlDwh;

  private static Field userNameDwh;

  private static Field passwordDwh;

  private static Field dbDriverNameDwh;

  private static Field urlRep;

  private static Field userNameRep;

  private static Field passwordRep;

  private static Field dbDriverNameRep;

  private static Field source;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    /* Reflecting fields used in testing */
    urlDwh = StandaloneMain.class.getDeclaredField("urlDwh");
    userNameDwh = StandaloneMain.class.getDeclaredField("userNameDwh");
    passwordDwh = StandaloneMain.class.getDeclaredField("passwordDwh");
    dbDriverNameDwh = StandaloneMain.class.getDeclaredField("dbDriverNameDwh");
    urlRep = StandaloneMain.class.getDeclaredField("urlRep");
    userNameRep = StandaloneMain.class.getDeclaredField("userNameRep");
    passwordRep = StandaloneMain.class.getDeclaredField("passwordRep");
    dbDriverNameRep = StandaloneMain.class.getDeclaredField("dbDriverNameRep");
    source = StandaloneMain.class.getDeclaredField("source");
    urlDwh.setAccessible(true);
    userNameDwh.setAccessible(true);
    passwordDwh.setAccessible(true);
    dbDriverNameDwh.setAccessible(true);
    urlRep.setAccessible(true);
    userNameRep.setAccessible(true);
    passwordRep.setAccessible(true);
    dbDriverNameRep.setAccessible(true);
    source.setAccessible(true);

    /* Creating connection to hsql database and create tables in there */
    try {
      Class.forName("org.hsqldb.jdbcDriver").newInstance();
      con = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    stmt = con.createStatement();
    stmt.execute("CREATE TABLE Meta_databases (USERNAME VARCHAR(31), VERSION_NUMBER VARCHAR(31), "
        + "TYPE_NAME VARCHAR(31), CONNECTION_ID BIGINT, CONNECTION_NAME VARCHAR(31), "
        + "CONNECTION_STRING VARCHAR(31), PASSWORD VARCHAR(31), DESCRIPTION VARCHAR(31), DRIVER_NAME VARCHAR(31), "
        + "DB_LINK_NAME VARCHAR(31))");
    stmt.execute("CREATE TABLE Tpactivation (TECHPACK_NAME VARCHAR(31), STATUS VARCHAR(31), "
        + "VERSIONID VARCHAR(31), TYPE VARCHAR(31))");
    stmt.execute("CREATE TABLE Transformer (TRANSFORMERID VARCHAR(31), VERSIONID VARCHAR(31), "
        + "DESCRIPTION VARCHAR(31))");
    stmt.execute("CREATE TABLE Transformation (TRANSFORMERID VARCHAR(31), ORDERNO BIGINT, TYPE VARCHAR(31), "
        + "SOURCE VARCHAR(31), TARGET VARCHAR(31), CONFIG VARCHAR(31), DESCRIPTION VARCHAR(31))");
    stmt.execute("CREATE TABLE Datainterface (INTERFACENAME VARCHAR(31), STATUS BIGINT, INTERFACETYPE VARCHAR(31), "
        + "DESCRIPTION VARCHAR(31), DATAFORMATTYPE VARCHAR(31), INTERFACEVERSION VARCHAR(31), LOCKEDBY VARCHAR(31), "
        + "LOCKDATE TIMESTAMP, PRODUCTNUMBER VARCHAR(31))");
    stmt.execute("CREATE TABLE Interfacemeasurement (TAGID VARCHAR(31), DATAFORMATID VARCHAR(31), "
        + "INTERFACENAME VARCHAR(31), TRANSFORMERID VARCHAR(31), STATUS BIGINT, MODIFTIME TIMESTAMP, "
        + "DESCRIPTION VARCHAR(31), TECHPACKVERSION TIMESTAMP, INTERFACEVERSION VARCHAR(31))");
    stmt.execute("CREATE TABLE Dataformat (DATAFORMATID VARCHAR(31), TYPEID VARCHAR(31), VERSIONID VARCHAR(31), "
        + "OBJECTTYPE VARCHAR(31), FOLDERNAME VARCHAR(31), DATAFORMATTYPE VARCHAR(31))");
    stmt.executeUpdate("INSERT INTO Meta_databases VALUES('sa', '1', 'USER', 1, 'dwh', "
        + "'jdbc:hsqldb:mem:testdb', '', 'description', 'org.hsqldb.jdbcDriver', 'dblinkname')");
    stmt.executeUpdate("INSERT INTO Meta_databases VALUES('sa', '1', 'USER', 1, 'dwhrep', "
        + "'jdbc:hsqldb:mem:testdb', '', 'description', 'org.hsqldb.jdbcDriver', 'dblinkname')");

    /* Creating ETLC property file */
    File ETLCConfFile = new File(System.getProperty("user.dir"), "ETLCServer.properties");
    ETLCConfFile.deleteOnExit();
    try {
      PrintWriter pw = new PrintWriter(new FileWriter(ETLCConfFile));
      pw.print("ENGINE_DB_URL=jdbc:hsqldb:mem:testdb\n");
      pw.print("ENGINE_DB_USERNAME=sa\n");
      pw.print("ENGINE_DB_PASSWORD=\n");
      pw.print("ENGINE_DB_DRIVERNAME=org.hsqldb.jdbcDriver\n");
      pw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    /* Creating generic property file */
    File genericProperties = new File(System.getProperty("user.dir"), "generic.properties");
    genericProperties.deleteOnExit();
    try {
      PrintWriter pw = new PrintWriter(new FileWriter(genericProperties));
      pw.print("source.parserType=unittestparser\n");
      pw.print("source.interfaceName=IFaceName\n");
      pw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    /* Creating static property file */
    File sp = new File(System.getProperty("user.dir"), "static.properties");
    sp.deleteOnExit();
    try {
      PrintWriter pw = new PrintWriter(new FileWriter(sp));
      pw.print("SessionHandling.storageFile=storage.txt\n");
      pw.print("SessionHandling.log.types=ADAPTER\n");
      pw.print("SessionHandling.log.ADAPTER.class=" + AdapterLog.class.getName() + "\n");
      pw.print("SessionHandling.log.ADAPTER.inputTableDir=inputTableDir\n");
      pw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    /* Setting the system property for ETLC property file */
    System.setProperty("dc5000.config.directory", System.getProperty("user.dir"));
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Cleaning up after every test */
    con = null;
    objUnderTest = null;
    stmt.execute("DROP TABLE Tpactivation");
    stmt.execute("DROP TABLE Transformer");
    stmt.execute("DROP TABLE Transformation");
    stmt.execute("DROP TABLE Meta_databases");
    stmt.execute("DROP TABLE Datainterface");
    stmt.execute("DROP TABLE Interfacemeasurement");
    stmt.execute("DROP TABLE Dataformat");
    System.gc();
    File storageFile = new File(System.getProperty("user.dir"), "storage.txt");
    File inputTableDir = new File(System.getProperty("user.dir") + "/inputTableDir");
    storageFile.deleteOnExit();
    inputTableDir.deleteOnExit();
  }

  @After
  public void tearDown() throws Exception {

    /* Cleaning up after every test */
    objUnderTest = null;
  }

  /**
   * Testing generic constructor.
   * 
   * @throws Exception
   */
  @Test
  public void testGenericConstructor() throws Exception {

    /* Asserting initializing does necessary things and does not throw exception */
    try {
      objUnderTest = new StandaloneMain();
      String actual = urlDwh.get(objUnderTest) + ", " + userNameDwh.get(objUnderTest) + ", "
          + passwordDwh.get(objUnderTest) + ", " + dbDriverNameDwh.get(objUnderTest) + ", " + urlRep.get(objUnderTest)
          + ", " + userNameRep.get(objUnderTest) + ", " + passwordRep.get(objUnderTest) + ", "
          + dbDriverNameRep.get(objUnderTest) + ", " + source.get(objUnderTest);
      String expected = "jdbc:hsqldb:mem:testdb, sa, , org.hsqldb.jdbcDriver, jdbc:hsqldb:mem:testdb, sa, , org.hsqldb.jdbcDriver, ";
      assertEquals(expected, actual);
    } catch (Exception e) {
      fail("Test faled - Exception was thrown during initialization:\n" + e);
    }
  }

  /**
   * Testing constructor with source.
   * 
   * @throws Exception
   */
  @Test
  public void testConstructorWithSource() throws Exception {

    /* Asserting initializing does necessary things and does not throw exception */
    try {
      objUnderTest = new StandaloneMain("testSource");
      String actual = urlDwh.get(objUnderTest) + ", " + userNameDwh.get(objUnderTest) + ", "
          + passwordDwh.get(objUnderTest) + ", " + dbDriverNameDwh.get(objUnderTest) + ", " + urlRep.get(objUnderTest)
          + ", " + userNameRep.get(objUnderTest) + ", " + passwordRep.get(objUnderTest) + ", "
          + dbDriverNameRep.get(objUnderTest) + ", " + source.get(objUnderTest);
      String expected = "jdbc:hsqldb:mem:testdb, sa, , org.hsqldb.jdbcDriver, jdbc:hsqldb:mem:testdb, sa, , "
          + "org.hsqldb.jdbcDriver, testSource";
      assertEquals(expected, actual);
    } catch (Exception e) {
      fail("Test faled - Exception was thrown during initialization:\n" + e);
    }
  }

  /**
   * Testing constructor with defined rockfactories.
   * 
   * @throws Exception
   */
  @Test
  public void testConstructorWithRockFactories() throws Exception {

    /* Asserting initializing does necessary things and does not throw exception */
    try {

      /* Initializing Static Properties in order to initialize SessionHandler */
      new StaticProperties().reload();

      /* Initializing rockfactory object */
      RockFactory rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con",
          true);

      objUnderTest = new StandaloneMain("testSource", rockFactory, rockFactory, rockFactory);
      String actual = urlDwh.get(objUnderTest) + ", " + userNameDwh.get(objUnderTest) + ", "
          + passwordDwh.get(objUnderTest) + ", " + dbDriverNameDwh.get(objUnderTest) + ", " + urlRep.get(objUnderTest)
          + ", " + userNameRep.get(objUnderTest) + ", " + passwordRep.get(objUnderTest) + ", "
          + dbDriverNameRep.get(objUnderTest) + ", " + source.get(objUnderTest);
      String expected = "jdbc:hsqldb:mem:testdb, sa, , org.hsqldb.jdbcDriver, jdbc:hsqldb:mem:testdb, sa, , "
          + "org.hsqldb.jdbcDriver, testSource";
      assertEquals(expected, actual);
    } catch (Exception e) {
      fail("Test faled - Exception was thrown during initialization:\n" + e);
    }
  }

  /**
   * Testing running main class.
   * 
   * @throws Exception
   */
  @Test
  public void testRunInterface() throws Exception {

    /* Setting connection variables */
    objUnderTest = new StandaloneMain(true);
    urlDwh.set(objUnderTest, "jdbc:hsqldb:mem:testdb");
    userNameDwh.set(objUnderTest, "sa");
    passwordDwh.set(objUnderTest, "");
    dbDriverNameDwh.set(objUnderTest, "org.hsqldb.jdbcDriver");
    urlRep.set(objUnderTest, "jdbc:hsqldb:mem:testdb");
    userNameRep.set(objUnderTest, "sa");
    passwordRep.set(objUnderTest, "");
    dbDriverNameRep.set(objUnderTest, "org.hsqldb.jdbcDriver");

    /* Calling the tested method and checking no exceptions was thrown */
    try {
      objUnderTest.runInterface("source");
    } catch (Exception e) {
      fail("Test failed - Exception thrown while running main: \n" + e);
    }
  }
}
