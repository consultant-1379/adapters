package com.distocraft.dc5000.etl.parser.xmltransformer;

import static org.junit.Assert.*;

import java.util.Properties;
import java.util.logging.Logger;

import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * @author ejarsok
 *
 */

public class TransformationFactoryTest {

  @Test
  public void testGetTransformation1() {
    
    Properties conf = new Properties();
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation(null, "src", "tgt", conf, null, log);
    
    assertNull(t);
  }
  
  @Test
  public void testGetTransformation2() {
    
    Properties conf = new Properties();
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation("lookup", null, "tgt", conf, null, log);
    
    assertNull(t);
  }
  
  @Test
  public void testGetTransformation3() {
    
    Properties conf = new Properties();
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation("lookup", "src", null, conf, null, log);
    
    assertNull(t);
  }
  
  @Test
  public void testGetTransformation4() {
    
    Properties conf = new Properties();
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation("notTransformer", "src", "tgt", conf, null, log);
    
    assertNull(t);
  }
  
  @Test
  public void testGetTransformation5() {
    
    Properties conf = new Properties();
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation("lookup", "src", "tgt", conf, null, log);
    
    assertEquals("class com.distocraft.dc5000.etl.parser.xmltransformer.Lookup", t.getClass().toString());
  }
  
  @Test // this print nullPointerException
  public void testGetTransformation6() {
    
    Properties conf = new Properties();
    conf.setProperty("sql", "SQL");
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation("databaselookup", "src", "tgt", conf, null, log);
    
    assertEquals("class com.distocraft.dc5000.etl.parser.xmltransformer.DatabaseLookup", t.getClass().toString());
  }
  
  @Test
  public void testGetTransformation7() {
    
    Properties conf = new Properties();
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation("bitmaplookup", "src", "tgt", conf, null, log);
    
    assertEquals("class com.distocraft.dc5000.etl.parser.xmltransformer.BitmapLookup", t.getClass().toString());
  }
  
  @Test
  public void testGetTransformation8() {
    
    Properties conf = new Properties();
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation("condition", "src", "tgt", conf, null, log);
    
    assertEquals("class com.distocraft.dc5000.etl.parser.xmltransformer.Condition", t.getClass().toString());
  }
  
  @Test
  public void testGetTransformation9() {
    
    Properties conf = new Properties();
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation("radixConverter", "src", "tgt", conf, null, log);
    
    assertEquals("class com.distocraft.dc5000.etl.parser.xmltransformer.RadixConverter", t.getClass().toString());
  }
  
  @Test
  public void testGetTransformation10() {
    
    Properties conf = new Properties();
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation("dateformat", "src", "tgt", conf, null, log);
    
    assertEquals("class com.distocraft.dc5000.etl.parser.xmltransformer.DateFormat", t.getClass().toString());
  }
  
  @Test
  public void testGetTransformation11() {
    
    Properties conf = new Properties();
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation("defaulttimehandler", "src", "tgt", conf, null, log);
    
    assertEquals("class com.distocraft.dc5000.etl.parser.xmltransformer.DefaultTimeHandler", t.getClass().toString());
  }
  
  @Test
  public void testGetTransformation12() {
    
    Properties conf = new Properties();
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation("fixed", "src", "tgt", conf, null, log);
    
    assertEquals("class com.distocraft.dc5000.etl.parser.xmltransformer.Fixed", t.getClass().toString());
  }
  
  @Test
  public void testGetTransformation13() {
    
    Properties conf = new Properties();
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation("postappender", "src", "tgt", conf, null, log);
    
    assertEquals("class com.distocraft.dc5000.etl.parser.xmltransformer.PostAppender", t.getClass().toString());
  }
  
  @Test
  public void testGetTransformation14() {
    
    Properties conf = new Properties();
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation("preappender", "src", "tgt", conf, null, log);
    
    assertEquals("class com.distocraft.dc5000.etl.parser.xmltransformer.PreAppender", t.getClass().toString());
  }
  
  @Test
  public void testGetTransformation15() {
    
    Properties conf = new Properties();
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation("reducedate", "src", "tgt", conf, null, log);
    
    assertEquals("class com.distocraft.dc5000.etl.parser.xmltransformer.ReduceDate", t.getClass().toString());
  }
  
  @Test
  public void testGetTransformation16() {
    
    Properties conf = new Properties();
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation("roundtime", "src", "tgt", conf, null, log);
    
    assertEquals("class com.distocraft.dc5000.etl.parser.xmltransformer.RoundTime", t.getClass().toString());
  }
  
  @Test
  public void testGetTransformation17() {
    
    Properties conf = new Properties();
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation("switch", "src", "tgt", conf, null, log);
    
    assertEquals("class com.distocraft.dc5000.etl.parser.xmltransformer.Switch", t.getClass().toString());
  }
  
  @Test
  public void testGetTransformation18() {
    
    Properties conf = new Properties();
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation("copy", "src", "tgt", conf, null, log);
    
    assertEquals("class com.distocraft.dc5000.etl.parser.xmltransformer.Copy", t.getClass().toString());
  }
  
  @Test
  public void testGetTransformation19() {
    
    Properties conf = new Properties();
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation("propertytokenizer", "src", "tgt", conf, null, log);
    
    assertEquals("class com.distocraft.dc5000.etl.parser.xmltransformer.PropertyTokenizer", t.getClass().toString());
  }
  
  @Test
  public void testGetTransformation20() {
    
    Properties conf = new Properties();
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation("fieldtokenizer", "src", "tgt", conf, null, log);
    
    assertEquals("class com.distocraft.dc5000.etl.parser.xmltransformer.FieldTokenizer", t.getClass().toString());
  }
  
  @Test
  public void testGetTransformation21() {
    
    Properties conf = new Properties();
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation("calculation", "src", "tgt", conf, null, log);
    
    assertEquals("class com.distocraft.dc5000.etl.parser.xmltransformer.Calculation", t.getClass().toString());
  }
  
  @Test
  public void testGetTransformation22() {
    
    Properties conf = new Properties();
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation("currenttime", "src", "tgt", conf, null, log);
    
    assertEquals("class com.distocraft.dc5000.etl.parser.xmltransformer.CurrentTime", t.getClass().toString());
  }
  
  @Ignore // AlarmTransformation class not exists
  public void testGetTransformation23() {
    
    Properties conf = new Properties();
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation("alarm", "src", "tgt", conf, null, log);
    
    assertEquals("class com.distocraft.dc5000.etl.parser.xmltransformer.AlarmTransformation", t.getClass().toString());
  }
  
  @Test
  public void testGetTransformation24() {
    
    Properties conf = new Properties();
    Logger log = Logger.getLogger("log");
    Transformation t = TransformationFactory.getTransformation("dstparameters", "src", "tgt", conf, null, log);
    
    assertEquals("class com.distocraft.dc5000.etl.parser.xmltransformer.DSTParameters", t.getClass().toString());
  }
}
