package com.ericsson.eniq.etl.asn1;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import junit.framework.JUnit4TestAdapter;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author ejarsok
 *
 */

public class ASN1RuleSetTest {

  private static Field rulePointer;
  
  @BeforeClass
  public static void init() {
    try {
      rulePointer = ASN1RuleSet.class.getDeclaredField("rulePointer");
      
      rulePointer.setAccessible(true);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
  }
  
  @Test
  public void testReset() {
    ASN1RuleSet ars = new ASN1RuleSet();
    try {
      rulePointer.set(ars, 20);
      ars.reset();
      
      assertEquals(-1, rulePointer.get(ars));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStatus2()");
    }
  }

  @Test
  public void testSetAndGetCallerRuleType() {
    ASN1RuleSet ars = new ASN1RuleSet();
    
    ars.setCallerRuleType("foobar");
    assertEquals("foobar", ars.getCallerRuleType());
  }

  @Test
  public void testStatus1() {
    ASN1RuleSet ars = new ASN1RuleSet();
    
    assertEquals(0, ars.status());
  }
  
  @Test
  public void testStatus2() {
    ASN1RuleSet ars = new ASN1RuleSet();
    try {
      rulePointer.set(ars, 1);
      
      assertEquals(1, ars.status());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStatus2()");
    }
  }
  
  @Test
  public void testStatus3() {
    ASN1RuleSet ars = new ASN1RuleSet();
    
    try {
      rulePointer.set(ars, -10);
      
      assertEquals(-1, ars.status());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStatus2()");
    }
  }

  @Test
  public void testRule1() {
    ASN1RuleSet ars = new ASN1RuleSet();
    ASN1Rule asn = new ASN1Rule();
    ASN1Rule rasn = null;
    ars.rules.add(asn);
    
    try {
      rulePointer.set(ars, 0);
      
      rasn = ars.rule();
      
      assertEquals(asn, rasn);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testRule() failed");
    }
  }
  
  @Test
  public void testRule2() {
    ASN1RuleSet ars = new ASN1RuleSet();
    
    try {
      assertEquals(null, ars.rule());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testRule() failed");
    }
  }
  
  @Test
  public void testRule3() {
    ASN1RuleSet ars = new ASN1RuleSet();
    
    try {
      rulePointer.set(ars, 10);
      
      assertEquals(null, ars.rule());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testRule() failed");
    }
  }

  @Test
  public void testHasNextRule() {
    ASN1RuleSet ars = new ASN1RuleSet();
    
    Boolean b = ars.hasNextRule();
    
    assertFalse("false expected", b);
  }
  
  @Test
  public void testHasNextRule2() {
    ASN1RuleSet ars = new ASN1RuleSet();
    ASN1Rule asn = new ASN1Rule();
    ars.rules.add(asn);
    
    Boolean b = ars.hasNextRule();
    
    assertTrue("true expected", b);
  }

  @Test
  public void testNextRule() {
    ASN1RuleSet ars = new ASN1RuleSet();
        
    ars.nextRule();
    
    try {
      Integer i = (Integer) rulePointer.get(ars);
      assertEquals(0, i.intValue());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testNextRule() failed");
    }
  }
  
  @Test
  public void testNextRule2() {
    ASN1RuleSet ars = new ASN1RuleSet();
    ASN1Rule asn = new ASN1Rule();
    ars.rules.add(asn);
    
    try {
      rulePointer.set(ars, 10);
      
      ars.nextRule();
      
      Integer i = (Integer) rulePointer.get(ars);
      assertEquals(0, i.intValue());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testNextRule() failed");
    }
  }

  @Test
  public void testToString() {
    ASN1RuleSet ars = new ASN1RuleSet();
    ASN1Rule asn = new ASN1Rule();
    
    ars.name = "name";
    ars.typeName = "typeName";
    ars.type = "type";
    
    asn.name = "Name";
    asn.internalType = "iType";
    asn.type = "Type";
    asn.defaultValue = "Default";
    ars.rules.add(asn);
    
    String expected = "name typeName (type) \n   Name Type (iType)";
    String actual = ars.toString();
    
    assertEquals(expected, actual);
    //System.out.println(ars.toString());
  }

  /*public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(ASN1RuleSetTest.class);
  }*/
}
