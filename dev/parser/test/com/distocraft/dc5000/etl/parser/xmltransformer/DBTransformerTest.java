package com.distocraft.dc5000.etl.parser.xmltransformer;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.distocraft.dc5000.etl.parser.ParserDebugger;
import com.distocraft.dc5000.etl.parser.ParserDebuggerCache;

import ssc.rockfactory.RockFactory;


public class DBTransformerTest {

  private static Field transformations;
  
  private static Field parserDebugger;
  
  private static RockFactory rockFact;
  
  private static Statement stm;
  
  @BeforeClass
  public static void init() {
    try {
      transformations = DBTransformer.class.getDeclaredField("transformations");
      parserDebugger = DBTransformer.class.getDeclaredField("parserDebugger");
      
      transformations.setAccessible(true);
      parserDebugger.setAccessible(true);
      
      
      Class.forName("org.hsqldb.jdbcDriver");
      
      Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "");
      
      stm = c.createStatement();

      stm.execute("CREATE TABLE Transformation (TRANSFORMERID VARCHAR(20), ORDERNO BIGINT,"
          + "TYPE VARCHAR(20), SOURCE VARCHAR(20), TARGET VARCHAR(20),CONFIG VARCHAR(20), DESCRIPTION VARCHAR(20))");

      stm.executeUpdate("INSERT INTO Transformation VALUES('id1:id2:id3:id4', 5, 'copy', 'src5', 'tgt', '', 'description5')");
      stm.executeUpdate("INSERT INTO Transformation VALUES('id1:id2:ALL:id4', 4, 'copy', 'src4', 'tgt', '', 'description4ALL')");
      stm.executeUpdate("INSERT INTO Transformation VALUES('id1:id2:id3:id4', 1, 'copy', 'src1', 'tgt', '', 'description1')");
      stm.executeUpdate("INSERT INTO Transformation VALUES('id1:id2:ALL:id4', 2, 'copy', 'src2', 'tgt', '', 'description2ALL')");
      stm.executeUpdate("INSERT INTO Transformation VALUES('id1:id2:id3:id4', 3, 'copy', 'src3', 'tgt', '', 'description3')");      
      
      rockFact = new RockFactory("jdbc:hsqldb:mem:testdb", "SA", "", "org.hsqldb.jdbcDriver", "con", true, -1);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init failed");
    }
  }
  
  @Test
  public void testDBTransformer() {
    try {
      DBTransformer dbt = new DBTransformer("id1:id2:id3:id4", rockFact, rockFact);
      
      ArrayList list = (ArrayList) transformations.get(dbt);
      
      Transformation t = (Transformation) list.get(0);
      
      assertEquals("class com.distocraft.dc5000.etl.parser.xmltransformer.Copy", t.getClass().toString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testDBTransformer() failed");
    }
  }
  
  @Test
  /**
   * Test for HL22908: TechPack IDE changes the order of the transformations 
   * Verify that for each MeasurementType, the transformations are ordered by ORDERNO column.
   */
  public void testDBTransformerByOrderNo() {
    try {
      DBTransformer dbt = new DBTransformer("id1:id2:id3:id4", rockFact, rockFact);
      
      ArrayList list = (ArrayList) transformations.get(dbt);
      String result = ""; // Concatenate sources together to check order.
      for (Object o : list) {
    	  Transformation t = (Transformation) o;
    	  result += t.getSource();
      }
      
      assertEquals("src1src2src3src4src5", result);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testDBTransformer() failed");
    }
  }

  @Test
  public void testTransform() {
    Logger log = Logger.getLogger("log");
    HashMap data = new HashMap();
    data.put("src1", "value");
    
    try {
      DBTransformer dbt = new DBTransformer("id1:id2:id3:id4", rockFact, rockFact);
      
      dbt.transform(data, log);
      
      assertEquals("value", data.get("tgt"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTransform() failed");
    }
  }

  @Test
  public void testAddDebugger() {
    try {
      DBTransformer dbt = new DBTransformer("id1:id2:id3:id4", rockFact, rockFact);

      ParserDebuggerCache pd = new ParserDebuggerCache();
      
      dbt.addDebugger(pd);
      
      ParserDebugger obj = (ParserDebugger) parserDebugger.get(dbt);
      
      assertNotNull(obj);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testAddDebugger() failed");
    }
  }

  @Test
  public void testGetTransformations() {
    try {
      DBTransformer dbt = new DBTransformer("id1:id2:id3:id4", rockFact, rockFact);
      
      ArrayList list = (ArrayList) dbt.getTransformations();
      
      Transformation t = (Transformation) list.get(0);
      
      assertEquals("class com.distocraft.dc5000.etl.parser.xmltransformer.Copy", t.getClass().toString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetTransformations() failed");
    }
  }

  @AfterClass
  public static void clean() {
    try {
      stm.execute("DROP TABLE Transformation");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
