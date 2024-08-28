package com.distocraft.dc5000.etl.parser;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.parser.xmltransformer.DBTransformer;


public class TransformerFactoryTest {
  
  private static RockFactory rockFact;
  
  private static Statement stm;
  
  @BeforeClass
  public static void init() {
    try {
      Class.forName("org.hsqldb.jdbcDriver");
      
      Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "");
      stm = c.createStatement();

      stm.execute("CREATE TABLE Transformation (TRANSFORMERID VARCHAR(30), ORDERNO VARCHAR(30), TYPE VARCHAR(30), SOURCE VARCHAR(30)" +
      		",TARGET VARCHAR(30), CONFIG VARCHAR(30), DESCRIPTION VARCHAR(30))");

      stm.executeUpdate("INSERT INTO Transformation VALUES('1', '2', 'type', 'source', 'target', 'config', 'description')");
      
      
      rockFact = new RockFactory("jdbc:hsqldb:mem:testdb", "SA", "", "org.hsqldb.jdbcDriver", "con", true, -1);
            
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Cleaning up after test */
    stm.execute("DROP TABLE Transformation");
  }
  
  @Test
  public void testCreate1() {
    try {
      DBTransformer dbt = new DBTransformer("tID:tID", null, rockFact);
      dbt = (DBTransformer) TransformerFactory.create(null, null, null);
      
      assertNull(dbt);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testCreate1() failed");
    }
  }

  @Test
  public void testCreate2() {
    try {
      DBTransformer dbt = null;
      dbt = (DBTransformer) TransformerFactory.create("tID:tID", null, rockFact);
      
      assertNotNull(dbt);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testCreate2() failed");
    }
  }
}
