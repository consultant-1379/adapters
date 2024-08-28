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

public class CalculationTest {

  private static Field src;
  
  private static Field name;
  
  private static Field tgt;
  
  private static Field fixedInt;
  
  private static Field fixedFloat;

  private static Field operandName;

  private static Field operation;
  
  
  
  @BeforeClass
  public static void init() {
    try {
      /* initializing reflected fields */
      src = Calculation.class.getDeclaredField("src");
      name  = Calculation.class.getDeclaredField("name");
      tgt  = Calculation.class.getDeclaredField("tgt");
      fixedInt = Calculation.class.getDeclaredField("fixedInt");
      fixedFloat = Calculation.class.getDeclaredField("fixedFloat");
      operandName = Calculation.class.getDeclaredField("operandName");
      operation = Calculation.class.getDeclaredField("operation");
      
      src.setAccessible(true);
      name.setAccessible(true);
      tgt.setAccessible(true);
      fixedInt.setAccessible(true);
      fixedFloat.setAccessible(true);
      operandName.setAccessible(true);
      operation.setAccessible(true);
      
    } catch(Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
  }
  
  @Test
  public void testTransform1() {
    Calculation c = new Calculation();
    
    Properties props = new Properties();
    props.setProperty("formula", "/10");
    
    HashMap data = new HashMap();
    data.put("src", "50");
    
    Logger log = Logger.getLogger("log");
    
    try {
      c.configure("name", "src", "tgt", props, null);
      
      c.transform(data, log);
      
      assertEquals("5", data.get("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform1() failed");
    }
  }
  
  @Test
  public void testTransform2() {
    Calculation c = new Calculation();
    
    Properties props = new Properties();
    props.setProperty("formula", "*10");
    
    HashMap data = new HashMap();
    data.put("src", "50");
    
    Logger log = Logger.getLogger("log");
    
    try {
      c.configure("name", "src", "tgt", props, null);
      
      c.transform(data, log);
      
      assertEquals("500", data.get("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform2() failed");
    }
  }
  
  @Test
  public void testTransform3() {
    Calculation c = new Calculation();
    
    Properties props = new Properties();
    props.setProperty("formula", "+10");
    
    HashMap data = new HashMap();
    data.put("src", "50");
    
    Logger log = Logger.getLogger("log");
    
    try {
      c.configure("name", "src", "tgt", props, null);
      
      c.transform(data, log);
      
      assertEquals("60", data.get("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform3() failed");
    }
  }
  
  @Test
  public void testTransform4() {
    Calculation c = new Calculation();
    
    Properties props = new Properties();
    props.setProperty("formula", "-10");
    
    HashMap data = new HashMap();
    data.put("src", "50");
    
    Logger log = Logger.getLogger("log");
    
    try {
      c.configure("name", "src", "tgt", props, null);
      
      c.transform(data, log);
      
      assertEquals("40", data.get("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform4() failed");
    }
  }
  
  @Test
  public void testTransform5() {
    Calculation c = new Calculation();
    
    Properties props = new Properties();
    props.setProperty("varformula", "number/");
    
    HashMap data = new HashMap();
    data.put("src", "10");
    data.put("number", "50");
    
    Logger log = Logger.getLogger("log");
    
    try {
      c.configure("name", "src", "tgt", props, null);
      
      c.transform(data, log);
      
      assertEquals("5", data.get("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform5() failed");
    }
  }
  
  @Test
  public void testTransform6() {
    Calculation c = new Calculation();
    
    Properties props = new Properties();
    props.setProperty("varformula", "number*");
    
    HashMap data = new HashMap();
    data.put("src", "10");
    data.put("number", "50");
    
    Logger log = Logger.getLogger("log");
    
    try {
      c.configure("name", "src", "tgt", props, null);
      
      c.transform(data, log);
      
      assertEquals("500", data.get("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform6() failed");
    }
  }
  
  @Test
  public void testTransform7() {
    Calculation c = new Calculation();
    
    Properties props = new Properties();
    props.setProperty("varformula", "number+");
    
    HashMap data = new HashMap();
    data.put("src", "10");
    data.put("number", "50");
    
    Logger log = Logger.getLogger("log");
    
    try {
      c.configure("name", "src", "tgt", props, null);
      
      c.transform(data, log);
      
      assertEquals("60", data.get("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform7() failed");
    }
  }
  
  @Test
  public void testTransform8() {
    Calculation c = new Calculation();
    
    Properties props = new Properties();
    props.setProperty("varformula", "number-");
    
    HashMap data = new HashMap();
    data.put("src", "10");
    data.put("number", "50");
    
    Logger log = Logger.getLogger("log");
    
    try {
      c.configure("name", "src", "tgt", props, null);
      
      c.transform(data, log);
      
      assertEquals("40", data.get("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform8() failed");
    }
  }
  
  
  /**
   * This tests calulation of PMRes Quantity Transformation. NB: usefloat must be false
   * for it to work.
   * @author edeamai
   */
  @Test
  public void testTransform9() {
    Calculation c = new Calculation();
    
    Properties props = new Properties();
    props.setProperty("formula", "pmresquantity");
    props.setProperty("usefloat", "false");
    
    HashMap data = new HashMap();
    data.put("src", "3200");
    
    Logger log = Logger.getLogger("log");
    
    try {
      c.configure("name", "src", "tgt", props, null);
      
      c.transform(data, log);
      assertEquals("128", data.get("tgt"));
      
      data.put("src", "12600");
      c.transform(data, log);
      assertEquals("56", data.get("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform10() failed");
    }
  }
  
  
  /**
   * This tests calulation of PMRes Quantity Transformation. NB: usefloat must be false
   * for it to work.
   * @author edeamai
   */
  @Test
  public void testTransform10() {
    Calculation c = new Calculation();
    
    Properties props = new Properties();
    props.setProperty("formula", "pmresquantity");
    props.setProperty("usefloat", "false");
    
    HashMap data = new HashMap();
    data.put("src", "6401");
    
    Logger log = Logger.getLogger("log");
    
    try {
      c.configure("name", "src", "tgt", props, null);
      
      c.transform(data, log);
      assertEquals("1", data.get("tgt"));
      
      data.put("src", "6146");
      c.transform(data, log);
      assertEquals("2", data.get("tgt"));
      
      data.put("src", "5891");
      c.transform(data, log);
      assertEquals("3", data.get("tgt"));
      
      data.put("src", "5636");
      c.transform(data, log);
      assertEquals("4", data.get("tgt"));
      
      data.put("src", "5381");
      c.transform(data, log);
      assertEquals("5", data.get("tgt"));
      
      data.put("src", "5120");
      c.transform(data, log);
      assertEquals("0", data.get("tgt"));
      
    } catch (ConfigException e) {
      e.printStackTrace();
      fail("testTransform10() failed");
    }
  }
  
  
  
  

  @Test
  public void testConfigure1() {
    Calculation c = new Calculation();
    Properties props = new Properties();
    props.setProperty("formula", "/10");
    
    try {
      /* Calling the tested method */
      c.configure("name", "src", "tgt", props, null);
      
      String expected = "name,src,tgt,11,10";
      String actual = name.get(c) + "," + src.get(c) + "," + tgt.get(c) + "," + operation.get(c) + "," + fixedInt.get(c);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure1() failed");
    }
  }
  
  @Test
  public void testConfigure2() {
    Calculation c = new Calculation();
    Properties props = new Properties();
    props.setProperty("formula", "10*");
    
    try {
      /* Calling the tested method */
      c.configure("name", "src", "tgt", props, null);
      
      String expected = "name,src,tgt,22,10";
      String actual = name.get(c) + "," + src.get(c) + "," + tgt.get(c) + "," + operation.get(c) + "," + fixedInt.get(c);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure1() failed");
    }
  }
  
  @Test
  public void testConfigure3() {
    Calculation c = new Calculation();
    Properties props = new Properties();
    props.setProperty("varformula", "+10");
    
    try {
      /* Calling the tested method */
      c.configure("name", "src", "tgt", props, null);
      
      String expected = "name,src,tgt,13,10";
      String actual = name.get(c) + "," + src.get(c) + "," + tgt.get(c) + "," + operation.get(c) + "," + operandName.get(c);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure1() failed");
    }
  }
  
  @Test
  public void testConfigure4() {
    Calculation c = new Calculation();
    Properties props = new Properties();
    props.setProperty("varformula", "10-");
    
    try {
      /* Calling the tested method */
      c.configure("name", "src", "tgt", props, null);
      
      String expected = "name,src,tgt,24,10";
      String actual = name.get(c) + "," + src.get(c) + "," + tgt.get(c) + "," + operation.get(c) + "," + operandName.get(c);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure1() failed");
    }
  }
  
  @Test
  public void testConfigure5() {
    Calculation c = new Calculation();
    Properties props = new Properties();
    props.setProperty("formula", "s10");
    
    try {
      /* Calling the tested method */
      c.configure("name", "src", "tgt", props, null);
      fail("Should not execute this line");
      
    } catch (Exception e) {
      // test passed
    }
  }
  
  @Test
  public void testConfigure6() {
    Calculation c = new Calculation();
    Properties props = new Properties();
    props.setProperty("formula", "+text");
    
    try {
      /* Calling the tested method */
      c.configure("name", "src", "tgt", props, null);
      fail("Should not execute this line");
      
    } catch (Exception e) {
      // test passed
    }
  }

  @Test
  public void testConfigure7() {
    Calculation c = new Calculation();
    Properties props = new Properties();
    props.setProperty("varformula", "s10");
    
    try {
      /* Calling the tested method */
      c.configure("name", "src", "tgt", props, null);
      fail("Should not execute this line");
      
    } catch (Exception e) {
      // test passed
    }
  }
  
  @Test
  public void testConfigure8() {
    Calculation c = new Calculation();
    Properties props = new Properties();
    
    try {
      /* Calling the tested method */
      c.configure("name", "src", "tgt", props, null);
      fail("Should not execute this line");
      
    } catch (Exception e) {
      // test passed
    }
  }
  
  @Test
  public void testConfigure9() {
    Calculation c = new Calculation();
    Properties props = new Properties();
    props.setProperty("formula", "/10");
    props.setProperty("usefloat", "true");
    
    try {
      /* Calling the tested method */
      c.configure("name", "src", "tgt", props, null);
      
      String expected = "name,src,tgt,11,10.0";
      String actual = name.get(c) + "," + src.get(c) + "," + tgt.get(c) + "," + operation.get(c) + "," + fixedFloat.get(c);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure1() failed");
    }
  }
  
  @Test
  public void testConfigure10() {
    Calculation c = new Calculation();
    Properties props = new Properties();
    props.setProperty("formula", "10*");
    props.setProperty("usefloat", "true");
    
    try {
      /* Calling the tested method */
      c.configure("name", "src", "tgt", props, null);
      
      String expected = "name,src,tgt,22,10.0";
      String actual = name.get(c) + "," + src.get(c) + "," + tgt.get(c) + "," + operation.get(c) + "," + fixedFloat.get(c);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConfigure1() failed");
    }
  }
  
  
  
  @Test
  public void testGetSource() {
    Calculation c = new Calculation();
    
    try {
      src.set(c, "src");
      
      /* Calling the tested method & assert */
      assertEquals("src", c.getSource());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetSource() failed");
    }
  }

  @Test
  public void testGetTarget() {
    Calculation c = new Calculation();
    
    try {
      tgt.set(c, "tgt");
      
      /* Calling the tested method & assert */
      assertEquals("tgt", c.getTarget());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetTarget() failed");
    }
  }

  @Test
  public void testGetName() {
    Calculation c = new Calculation();
    
    try {
      name.set(c, "name");
      
      /* Calling the tested method & assert */
      assertEquals("name", c.getName());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetName() failed");
    }
  }

}
