package com.ericsson.eniq.etl.asn1;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ericsson.junit.HelpClass;

/**
 * 
 * @author ejarsok
 * 
 */

public class ASN1RuleHandlerTest {

  private static Method peekRuleSet;

  private static Method popRuleSet;

  private static Method pushRuleSet;

  private static Method getRuleSet;

  private static Method handlePrimitive;
  
  private static Method handlePrimitive2;

  private static Method handleChoice;

  private static Method handleElement;

  private static Method handleSequence;
  
  private static Method singleLine;

  private static Method isSequence;

  private static Method isChoice;

  private static Method isPrimitive;

  private static Method createRuleSet;

  private static Method createRule;
  
  private static Method multiLine;
  
  private static Method createRules;

  private static Field ruleSetStack;

  private static Field ruleSetMap;

  // ASN1RuleSet
  private static Field rulePointer;

  @BeforeClass
  public static void init() {
    try {
      peekRuleSet = ASN1RuleHandler.class.getDeclaredMethod("peekRuleSet", new Class[] {});
      popRuleSet = ASN1RuleHandler.class.getDeclaredMethod("popRuleSet", new Class[] {});
      pushRuleSet = ASN1RuleHandler.class.getDeclaredMethod("pushRuleSet", new Class[] { ASN1RuleSet.class });
      getRuleSet = ASN1RuleHandler.class.getDeclaredMethod("getRuleSet", new Class[] { ASN1Rule.class });
      handlePrimitive = ASN1RuleHandler.class.getDeclaredMethod("handlePrimitive", new Class[] { ASN1RuleSet.class });
      handlePrimitive2 = ASN1RuleHandler.class.getDeclaredMethod("handlePrimitive2", new Class[] {ASN1RuleSet.class});
      handleChoice = ASN1RuleHandler.class.getDeclaredMethod("handleChoice",
          new Class[] { int.class, ASN1RuleSet.class });
      handleElement = ASN1RuleHandler.class.getDeclaredMethod("handleElement", new Class[] { ASN1RuleSet.class });
      handleSequence = ASN1RuleHandler.class.getDeclaredMethod("handleSequence", new Class[] { ASN1RuleSet.class });
      singleLine = ASN1RuleHandler.class.getDeclaredMethod("singleLine", new Class[] {String.class});
      isSequence = ASN1RuleHandler.class.getDeclaredMethod("isSequence", new Class[] { String.class });
      isChoice = ASN1RuleHandler.class.getDeclaredMethod("isChoice", new Class[] { String.class });
      isPrimitive = ASN1RuleHandler.class.getDeclaredMethod("isPrimitive", new Class[] { String.class });
      createRuleSet = ASN1RuleHandler.class.getDeclaredMethod("createRuleSet",
          new Class[] { String.class, String.class });
      createRule = ASN1RuleHandler.class.getDeclaredMethod("createRule", new Class[] { String.class, String.class });
      multiLine = ASN1RuleHandler.class.getDeclaredMethod("multiLine", new Class[] {String.class, BufferedReader.class});
      createRules = ASN1RuleHandler.class.getDeclaredMethod("createRules", new Class[] {BufferedReader.class});
      ruleSetStack = ASN1RuleHandler.class.getDeclaredField("ruleSetStack");
      ruleSetMap = ASN1RuleHandler.class.getDeclaredField("ruleSetMap");

      // ASN1RuleSet
      rulePointer = ASN1RuleSet.class.getDeclaredField("rulePointer");

      peekRuleSet.setAccessible(true);
      popRuleSet.setAccessible(true);
      pushRuleSet.setAccessible(true);
      getRuleSet.setAccessible(true);
      handlePrimitive.setAccessible(true);
      handlePrimitive2.setAccessible(true);
      handleChoice.setAccessible(true);
      handleElement.setAccessible(true);
      handleSequence.setAccessible(true);
      singleLine.setAccessible(true);
      isSequence.setAccessible(true);
      isChoice.setAccessible(true);
      isPrimitive.setAccessible(true);
      createRuleSet.setAccessible(true);
      createRule.setAccessible(true);
      multiLine.setAccessible(true);
      createRules.setAccessible(true);
      ruleSetStack.setAccessible(true);
      ruleSetMap.setAccessible(true);

      // ASN1RuleSet
      rulePointer.setAccessible(true);

    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
  }

  @Test
  public void TesPeekRuleSet1() {
    ASN1RuleHandler arh = new ASN1RuleHandler();

    try {
      // Test if ruleSetStack arraylist is null
      ASN1RuleSet ars = (ASN1RuleSet) peekRuleSet.invoke(arh, new Object[] {});

      assertNull(ars);

    } catch (Exception e) {
      e.printStackTrace();
      fail("TesPeekRuleSet1() failed");
    }
  }

  @Test
  public void TesPeekRuleSet2() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1RuleSet a = new ASN1RuleSet();
    ArrayList l = new ArrayList();
    l.add(a);

    try {
      ruleSetStack.set(arh, l);

      ASN1RuleSet ars = (ASN1RuleSet) peekRuleSet.invoke(arh, new Object[] {});

      assertEquals(a, ars);

    } catch (Exception e) {
      e.printStackTrace();
      fail("TesPeekRuleSet2() failed");
    }
  }

  @Test
  public void testPopRuleSet1() {
    ASN1RuleHandler arh = new ASN1RuleHandler();

    try {
      // Test if ruleSetStack arraylist is null
      ASN1RuleSet ars = (ASN1RuleSet) popRuleSet.invoke(arh, new Object[] {});

      assertNull(ars);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testPopRuleSet1() failed");
    }
  }

  @Test
  public void testPopRuleSet2() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1RuleSet a = new ASN1RuleSet();
    ArrayList l = new ArrayList();
    l.add(a);

    try {
      ruleSetStack.set(arh, l);

      ASN1RuleSet ars = (ASN1RuleSet) popRuleSet.invoke(arh, new Object[] {});

      assertEquals(a, ars);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testPopRuleSet2() failed");
    }
  }

  @Test
  public void testPushRuleSet() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1RuleSet a = new ASN1RuleSet();

    try {
      ArrayList l = (ArrayList) ruleSetStack.get(arh);
      pushRuleSet.invoke(arh, new Object[] { a });

      assertTrue(l.contains(a));

    } catch (Exception e) {
      e.printStackTrace();
      fail("testPushRuleSet() failed");
    }
  }

  @Test
  public void testGetRuleSet1() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1Rule ar = new ASN1Rule();
    ar.type = "rulez";
    HashMap hm = new HashMap();

    try {
      ruleSetMap.set(arh, hm);
      ASN1RuleSet ars = (ASN1RuleSet) getRuleSet.invoke(arh, new Object[] { ar });

      assertNull(ars);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetRuleSet1() failed");
    }
  }

  @Test
  public void testGetRuleSet2() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1RuleSet ars = new ASN1RuleSet();
    ars.name = "rulez";
    ASN1Rule ar = new ASN1Rule();
    ar.type = "rulez";
    HashMap hm = new HashMap();
    hm.put("keyrulez", ars);

    try {
      ASN1RuleSet ars2 = null;
      ruleSetMap.set(arh, hm);
      ars2 = (ASN1RuleSet) getRuleSet.invoke(arh, new Object[] { ar });

      assertNotNull(ars2);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetRuleSet2() failed");
    }
  }

  @Test
  public void testHandlePrimitive() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1Rule ar = new ASN1Rule();
    ASN1RuleSet ars = new ASN1RuleSet();
    ars.rules.add(ar);

    try {
      rulePointer.set(ars, 0);
      ASN1Rule asnr = (ASN1Rule) handlePrimitive.invoke(arh, new Object[] { ars });

      assertEquals(asnr, ar);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testHandlePrimitive() failed");
    }
  }

  @Test
  public void testHandlePrimitive21() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1RuleSet ars = new ASN1RuleSet();
    ASN1Rule ar = new ASN1Rule();
    ar.name = "ruleName";
    ar.type = "ruleType";
    ars.rules.add(ar);
    ars.nextRule();
    
    try {
      ASN1Rule ar2 = (ASN1Rule) handlePrimitive2.invoke(arh, new Object[] {ars});
      
      String expected = "ruleName,ruleType";
      String actual = ar2.name + "," + ar2.type;
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testHandlePrimitive2() failed");
    }
  }
  
  @Test
  public void testHandlePrimitive22() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1RuleSet ars = new ASN1RuleSet();
    ASN1Rule ar = new ASN1Rule();
    ar.name = "ruleName";
    ar.type = "ruleType";
    ars.rules.add(ar);
    ars.nextRule();
    
    ASN1RuleSet ars2 = new ASN1RuleSet();
    ASN1Rule ar2 = new ASN1Rule();
    ar2.name = "ruleName2";
    ar2.type = "ruleType2";
    ars2.rules.add(ar2);
    
    HashMap hm = new HashMap();
    hm.put("keyruleType", ars2);
    
    try {
      ruleSetMap.set(arh, hm);
      ASN1Rule ar3 = (ASN1Rule) handlePrimitive2.invoke(arh, new Object[] {ars});
      
      String expected = "ruleName2,ruleType2";
      String actual = ar3.name + "," + ar3.type;
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testHandlePrimitive2() failed");
    }
  }
  
  @Test
  public void testHandleChoice() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1Rule ar1 = new ASN1Rule();
    ar1.name = "ar1";
    ar1.type = "type1";
    ASN1Rule ar2 = new ASN1Rule();
    ar2.name = "ar2";
    ar2.type = "type2";
    ASN1RuleSet ars = new ASN1RuleSet();
    ars.rules.add(ar1);
    ars.rules.add(ar2);

    try {
      ASN1Rule result = (ASN1Rule) handleChoice.invoke(arh, new Object[] { 1, ars });

      String expected = "ar2,type2";
      String actual = result.name + "," + result.type;

      assertEquals(expected, actual);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testHandleChoice() failed");
    }
  }

  @Test
  public void testHandleElement1() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1Rule ar1 = new ASN1Rule();
    ar1.name = "ar1";
    ar1.type = "type1";
    ASN1RuleSet ars = new ASN1RuleSet();
    ars.rules.add(ar1);
    ars.nextRule();

    ASN1RuleSet ars2 = new ASN1RuleSet();
    ars2.type = "null";
    HashMap hm = new HashMap();
    hm.put("keytype1", ars2);

    try {
      ruleSetMap.set(arh, hm);
      String s = (String) handleElement.invoke(arh, new Object[] { ars });

      assertNull(s);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testHandleElement1() failed");
    }
  }

  @Test
  public void testHandleElement2() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1Rule ar1 = new ASN1Rule();
    ar1.name = "ar1";
    ar1.type = "type1";
    ASN1RuleSet ars = new ASN1RuleSet();
    ars.rules.add(ar1);
    ars.nextRule();

    ASN1RuleSet ars2 = new ASN1RuleSet();
    ars2.type = "SEQUENCE"; // SEQUENCE
    ars2.name = "ars2Name";
    HashMap hm = new HashMap();
    hm.put("keytype1", ars2);

    try {
      ruleSetMap.set(arh, hm);
      ArrayList l = (ArrayList) ruleSetStack.get(arh);
      String s = (String) handleElement.invoke(arh, new Object[] { ars });

      String expected = "ars2Name,true";
      String actual = s + "," + l.contains(ars2);

      assertEquals(expected, actual);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testHandleElement2() failed");
    }
  }

  @Test
  public void testHandleElement3() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1Rule ar1 = new ASN1Rule();
    ar1.name = "ar1";
    ar1.type = "type1";
    ASN1RuleSet ars = new ASN1RuleSet();
    ars.rules.add(ar1);
    ars.nextRule();

    ASN1RuleSet ars2 = new ASN1RuleSet();
    ars2.type = "PRIMITIVE"; // PRIMITIVE
    ars2.name = "ars2Name";
    HashMap hm = new HashMap();
    hm.put("keytype1", ars2);

    try {
      ruleSetMap.set(arh, hm);
      String s = (String) handleElement.invoke(arh, new Object[] { ars });

      assertEquals("ars2Name", s);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testHandleElement3() failed");
    }
  }

  @Test
  public void testHandleSequence1() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1Rule ar1 = new ASN1Rule();
    ar1.name = "ar1";
    ar1.type = "type1";
    ASN1RuleSet ars = new ASN1RuleSet();
    ars.rules.add(ar1);
    ars.nextRule();

    ASN1RuleSet ars2 = new ASN1RuleSet();
    ars2.type = "null";
    HashMap hm = new HashMap();
    hm.put("keytype1", ars2);

    try {
      ruleSetMap.set(arh, hm);
      String s = (String) handleSequence.invoke(arh, new Object[] { ars });

      assertNull(s);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testHandleSequence() failed");
    }
  }

  @Test
  public void testHandleSequence2() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1Rule ar1 = new ASN1Rule();
    ar1.name = "ar1";
    ar1.type = "type1";
    ASN1RuleSet ars = new ASN1RuleSet();
    ars.rules.add(ar1);
    ars.nextRule();

    ASN1RuleSet ars2 = new ASN1RuleSet();
    ars2.type = "SEQUENCE"; // SEQUENCE
    ars2.name = "ars2Name";
    HashMap hm = new HashMap();
    hm.put("keytype1", ars2);

    try {
      ruleSetMap.set(arh, hm);
      ArrayList l = (ArrayList) ruleSetStack.get(arh);
      String s = (String) handleSequence.invoke(arh, new Object[] { ars });

      String expected = "ars2Name,true";
      String actual = s + "," + l.contains(ars2);

      assertEquals(expected, actual);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testHandleElement2() failed");
    }
  }

  @Test
  public void testHandleSequence3() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1Rule ar1 = new ASN1Rule();
    ar1.name = "ar1";
    ar1.type = "type1";
    ASN1RuleSet ars = new ASN1RuleSet();
    ars.rules.add(ar1);
    ars.nextRule();

    ASN1RuleSet ars2 = new ASN1RuleSet();
    ars2.type = "PRIMITIVE"; // PRIMITIVE
    ars2.name = "ars2Name";
    HashMap hm = new HashMap();
    hm.put("keytype1", ars2);

    try {
      ruleSetMap.set(arh, hm);
      ArrayList l = (ArrayList) ruleSetStack.get(arh);
      String s = (String) handleSequence.invoke(arh, new Object[] { ars });

      String expected = "ars2Name,true";
      String actual = s + "," + l.contains(ars2);

      assertEquals(expected, actual);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testHandleElement2() failed");
    }
  }

  @Test
  public void testHandleSequence4() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1Rule ar1 = new ASN1Rule();
    ar1.name = "ar1";
    ar1.type = "type1";
    ASN1RuleSet ars = new ASN1RuleSet();
    ars.rules.add(ar1);
    ars.nextRule();

    ASN1RuleSet ars2 = new ASN1RuleSet();
    ars2.type = "CHOICE"; // CHOICE
    ars2.name = "ars2Name";
    HashMap hm = new HashMap();
    hm.put("keytype1", ars2);

    try {
      ruleSetMap.set(arh, hm);
      ArrayList l = (ArrayList) ruleSetStack.get(arh);
      String s = (String) handleSequence.invoke(arh, new Object[] { ars });

      String expected = "ars2Name,true";
      String actual = s + "," + l.contains(ars2);

      assertEquals(expected, actual);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testHandleElement2() failed");
    }
  }

  @Test
  public void testPrimitive1() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1RuleSet ars = new ASN1RuleSet();
    ars.type = "null";
    
    try {
      ArrayList al = (ArrayList) ruleSetStack.get(arh);
      al.add(ars);
      
      ASN1Rule ar = arh.primitive(0);
      
      assertNull(ar);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testPrimitive() failed");
    }
  }
  
  @Test
  public void testPrimitive2() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1Rule ar = new ASN1Rule();
    ar.name = "name";
    ar.type = "type";
    ASN1Rule ar2 = new ASN1Rule();
    ar2.name = "name2";
    ar2.type = "type2";
    ASN1RuleSet ars = new ASN1RuleSet();
    ars.type = "CHOICE";
    ars.rules.add(ar);
    ars.rules.add(ar2);
    
    ars.nextRule();
    
    try {
      ArrayList al = (ArrayList) ruleSetStack.get(arh);
      al.add(ars);
      
      ASN1Rule arr = arh.primitive(1);
      
      String expected = "name2,type2";
      String actual = arr.name + "," + arr.type;
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testPrimitive2() failed");
    }
  }
  
  @Test
  public void testPrimitive3() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1Rule ar = new ASN1Rule();
    ar.name = "name";
    ar.type = "type";
    ASN1RuleSet ars = new ASN1RuleSet();
    ars.type = "PRIMITIVE";
    ars.rules.add(ar);
    
    ars.nextRule();
    
    try {
      ArrayList al = (ArrayList) ruleSetStack.get(arh);
      al.add(ars);
      
      ASN1Rule arr = arh.primitive(0);
      
      String expected = "name,type";
      String actual = arr.name + "," + arr.type;
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testPrimitive3() failed");
    }
  }

  @Test
  public void testStartSequence1() {
    //initialize needed variables
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1RuleSet ars = new ASN1RuleSet();
    ars.name = "name";
    ars.callerRuleType = "NOT_MAIN";
    ArrayList al = new ArrayList();
    al.add(ars);
    
    try {
      //initialize ASN1RuleHandler Fields
      ruleSetStack.set(arh, al);
      
      String s = arh.startSequence();
            
      assertEquals("name", s);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStartSequence1() failed");
    }
  }
  
  @Test
  public void testStartSequence2() {
    //initialize needed variables
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1RuleSet ars = new ASN1RuleSet();
    ars.name = "name";
    ASN1Rule ar = new ASN1Rule();
    ar.internalType = "SEQUENCE";
    ar.type = "type";
    ars.rules.add(ar);
    
    ArrayList al = new ArrayList();
    al.add(ars);
    
    ASN1RuleSet ars2 = new ASN1RuleSet();
    ars2.type = "SEQUENCE";
    ars2.name = "name2";
    HashMap hm = new HashMap();
    hm.put("keytype", ars2);
    
    try {
      //initialize ASN1RuleHandler Fields
      ruleSetStack.set(arh, al);
      ruleSetMap.set(arh, hm);
      ars.nextRule();
      
      String s = arh.startSequence();
            
      assertEquals("name2", s);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStartSequence2() failed");
    }
  }
  
  @Test
  public void testStartSequence3() {
    //initialize needed variables
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1RuleSet ars = new ASN1RuleSet();
    ars.name = "name";
    ASN1Rule ar = new ASN1Rule();
    ar.internalType = "ELEMENT";
    ar.type = "type";
    ars.rules.add(ar);
    
    ArrayList al = new ArrayList();
    al.add(ars);
    
    ASN1RuleSet ars2 = new ASN1RuleSet();
    ars2.type = "SEQUENCE";
    ars2.name = "name2";
    HashMap hm = new HashMap();
    hm.put("keytype", ars2);
    
    try {
      //initialize ASN1RuleHandler Fields
      ruleSetStack.set(arh, al);
      ruleSetMap.set(arh, hm);
      ars.nextRule();
      
      String s = arh.startSequence();
            
      assertEquals("name2", s);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStartSequence3() failed");
    }
  }
  
  @Test
  public void testStartSequence4() {
    //initialize needed variables
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1RuleSet ars = new ASN1RuleSet();
    ASN1Rule ar = new ASN1Rule();
    ar.internalType = "PRIMITIVE";
    ar.name = "name";
    ars.rules.add(ar);
    
    ArrayList al = new ArrayList();
    al.add(ars);
    
    try {
      //initialize ASN1RuleHandler Fields
      ruleSetStack.set(arh, al);
      ars.nextRule();
      
      String s = arh.startSequence();
            
      assertEquals("name", s);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStartSequence4() failed");
    }
  }
  
  @Test
  public void testStartSequence5() {
    //initialize needed variables
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1RuleSet ars = new ASN1RuleSet();
    ASN1Rule ar = new ASN1Rule();
    ar.internalType = "NOT_EXIST";
    ars.rules.add(ar);
    
    ArrayList al = new ArrayList();
    al.add(ars);
    
    try {
      //initialize ASN1RuleHandler Fields
      ruleSetStack.set(arh, al);
      ars.nextRule();
      
      String s = arh.startSequence();
            
      assertEquals("", s);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStartSequence5() failed");
    }
  }

  @Test
  public void testEndSequence1() {
    //initialize needed variables
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1RuleSet ars = new ASN1RuleSet();
    ars.callerRuleType = "SEQUENCE";
    ASN1RuleSet ars2 = new ASN1RuleSet();
    ars2.callerRuleType = "SEQUENCE";
    ars2.count = 2;
    ars2.name = "name";
    
    ArrayList al = new ArrayList();
    al.add(ars);
    al.add(ars2);

    try {
      //initialize ASN1RuleHandler Fields
      ruleSetStack.set(arh, al);
      
      String s = arh.endSequence();
      
      assertEquals("name", s);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testEndSequence() failed");
    }
  }
  
  @Test
  public void testEndSequence2() {
    //initialize needed variables
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1RuleSet ars = new ASN1RuleSet();
    ars.callerRuleType = "ELEMENT";
    ars.name = "name";
    
    ArrayList al = new ArrayList();
    al.add(ars);

    try {
      //initialize ASN1RuleHandler Fields
      ruleSetStack.set(arh, al);
      
      String s = arh.endSequence();
      
      assertEquals("name", s);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testEndSequence() failed");
    }
  }
  
  @Test
  public void testEndSequence3() {
    //initialize needed variables
    ASN1RuleHandler arh = new ASN1RuleHandler();
    ASN1RuleSet ars = new ASN1RuleSet();
    ars.callerRuleType = "MAIN";
    
    ArrayList al = new ArrayList();
    al.add(ars);

    try {
      //initialize ASN1RuleHandler Fields
      ruleSetStack.set(arh, al);
      
      String s = arh.endSequence();
      
      assertEquals("", s);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testEndSequence() failed");
    }
  }

  @Test
  public void testSingleLine() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    
    try {
      ASN1RuleSet ars = (ASN1RuleSet) singleLine.invoke(arh, new Object[] { "name ::= INTEGER (0..10)" });
      ASN1Rule ar = (ASN1Rule) ars.rules.get(0);

      String expected = "name,PRIMITIVE,name,PRIMITIVE,INTEGER,,false,";
      String actual = ars.name + "," + ars.type + "," + ar.name + "," + ar.internalType + "," + ar.type + "," + ar.info
          + "," + ar.optional + "," + ar.defaultValue;

      assertEquals(expected, actual);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testSingleLine() failed");
    }
  }
  
  @Test
  public void testIsSequence1() {
    ASN1RuleHandler arh = new ASN1RuleHandler();

    try {
      boolean b = (Boolean) isSequence.invoke(arh, new Object[] { "--SEQUENCE--" });

      assertTrue(b);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsSequence1() failed");
    }
  }

  @Test
  public void testIsSequence2() {
    ASN1RuleHandler arh = new ASN1RuleHandler();

    try {
      boolean b = (Boolean) isSequence.invoke(arh, new Object[] { "--FOOBAR--" });

      assertFalse(b);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsSequence2() failed");
    }
  }

  @Test
  public void testIsChoice1() {
    ASN1RuleHandler arh = new ASN1RuleHandler();

    try {
      boolean b = (Boolean) isChoice.invoke(arh, new Object[] { "--CHOICE--" });

      assertTrue(b);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsChoice1() failed");
    }
  }

  @Test
  public void testIsChoice2() {
    ASN1RuleHandler arh = new ASN1RuleHandler();

    try {
      boolean b = (Boolean) isChoice.invoke(arh, new Object[] { "--Foobar--" });

      assertFalse(b);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsChoice2() failed");
    }
  }

  @Test
  public void testIsPrimitive1() {
    ASN1RuleHandler arh = new ASN1RuleHandler();

    try {
      boolean b = (Boolean) isPrimitive.invoke(arh, new Object[] { "--INTEGER--" });

      assertTrue(b);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsPrimitive1() failed");
    }
  }

  @Test
  public void testIsPrimitive2() {
    ASN1RuleHandler arh = new ASN1RuleHandler();

    try {
      boolean b = (Boolean) isPrimitive.invoke(arh, new Object[] { "--PrintableString--" });

      assertTrue(b);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsPrimitive2() failed");
    }
  }

  @Test
  public void testIsPrimitive3() {
    ASN1RuleHandler arh = new ASN1RuleHandler();

    try {
      boolean b = (Boolean) isPrimitive.invoke(arh, new Object[] { "--GraphicString--" });

      assertTrue(b);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsPrimitive3() failed");
    }
  }

  @Test
  public void testIsPrimitive4() {
    ASN1RuleHandler arh = new ASN1RuleHandler();

    try {
      boolean b = (Boolean) isPrimitive.invoke(arh, new Object[] { "--REAL--" });

      assertTrue(b);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsPrimitive4() failed");
    }
  }

  @Test
  public void testIsPrimitive5() {
    ASN1RuleHandler arh = new ASN1RuleHandler();

    try {
      boolean b = (Boolean) isPrimitive.invoke(arh, new Object[] { "--NULL--" });

      assertTrue(b);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsPrimitive5() failed");
    }
  }

  @Test
  public void testIsPrimitive6() {
    ASN1RuleHandler arh = new ASN1RuleHandler();

    try {
      boolean b = (Boolean) isPrimitive.invoke(arh, new Object[] { "--BOOLEAN--" });

      assertTrue(b);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsPrimitive6() failed");
    }
  }

  @Test
  public void testIsPrimitive7() {
    ASN1RuleHandler arh = new ASN1RuleHandler();

    try {
      boolean b = (Boolean) isPrimitive.invoke(arh, new Object[] { "--GeneralizedTime--" });

      assertTrue(b);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsPrimitive7() failed");
    }
  }

  @Test
  public void testIsPrimitive8() {
    ASN1RuleHandler arh = new ASN1RuleHandler();

    try {
      boolean b = (Boolean) isPrimitive.invoke(arh, new Object[] { "--UTF8String--" });

      assertTrue(b);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsPrimitive8() failed");
    }
  }

  @Test
  public void testIsPrimitive9() {
    ASN1RuleHandler arh = new ASN1RuleHandler();

    try {
      boolean b = (Boolean) isPrimitive.invoke(arh, new Object[] { "--FOOBAR--" });

      assertFalse(b);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsPrimitive9() failed");
    }
  }

  @Test
  public void testCreateRuleSet() {
    ASN1RuleHandler arh = new ASN1RuleHandler();

    try {
      ASN1RuleSet ars = (ASN1RuleSet) createRuleSet.invoke(arh, new Object[] { "name", "INTEGER (0..10)" });
      ASN1Rule ar = (ASN1Rule) ars.rules.get(0);

      String expected = "name,PRIMITIVE,name,PRIMITIVE,INTEGER,,false,";
      String actual = ars.name + "," + ars.type + "," + ar.name + "," + ar.internalType + "," + ar.type + "," + ar.info
          + "," + ar.optional + "," + ar.defaultValue;

      assertEquals(expected, actual);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testCreateRuleSet() failed");
    }
  }

  @Test
  public void testCreateRule1() {
    ASN1RuleHandler arh = new ASN1RuleHandler();

    try {
      ASN1Rule ar = (ASN1Rule) createRule.invoke(arh, new Object[] { "name", "INTEGER (0..10)" });

      String expected = "name,PRIMITIVE,INTEGER,,false,";
      String actual = ar.name + "," + ar.internalType + "," + ar.type + "," + ar.info + "," + ar.optional + ","
          + ar.defaultValue;

      assertEquals(expected, actual);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testCreateRule1() failed");
    }
  }

  @Test
  public void testCreateRule2() {
    ASN1RuleHandler arh = new ASN1RuleHandler();

    try {
      ASN1Rule ar = (ASN1Rule) createRule.invoke(arh, new Object[] { "name", "BOOLEAN DEFAULT true" });

      String expected = "name,PRIMITIVE,BOOLEAN,,true,true";
      String actual = ar.name + "," + ar.internalType + "," + ar.type + "," + ar.info + "," + ar.optional + ","
          + ar.defaultValue;

      assertEquals(expected, actual);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testCreateRule2() failed");
    }
  }

  @Test
  public void testCreateRule3() {
    ASN1RuleHandler arh = new ASN1RuleHandler();

    try {
      ASN1Rule ar = (ASN1Rule) createRule.invoke(arh, new Object[] { "name", "SEQUENCE OF data" });

      String expected = "name,SEQUENCE,data,,false,";
      String actual = ar.name + "," + ar.internalType + "," + ar.type + "," + ar.info + "," + ar.optional + ","
          + ar.defaultValue;

      assertEquals(expected, actual);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testCreateRule3() failed");
    }
  }

  @Test
  public void testCreateRule4() {
    ASN1RuleHandler arh = new ASN1RuleHandler();

    try {
      ASN1Rule ar = (ASN1Rule) createRule.invoke(arh, new Object[] { "name", "ELEMENT" });

      String expected = "name,ELEMENT,ELEMENT,,false,";
      String actual = ar.name + "," + ar.internalType + "," + ar.type + "," + ar.info + "," + ar.optional + ","
          + ar.defaultValue;

      assertEquals(expected, actual);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testCreateRule4() failed");
    }
  }
  
  @Test
  public void testMultiLine() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    String homeDir = System.getProperty("user.home");
    HelpClass hc = new HelpClass();
    
    String rules = "name ::= SEQUENCE {\n";
    File x = hc.createFile(homeDir, "asn1MultilineFile", "\tint1 INTEGER (0..10),\n\tbool BOOLEAN true\n}");
    x.deleteOnExit();
    
    try {
      BufferedReader br = new BufferedReader(new FileReader(x));
      ASN1RuleSet ars = (ASN1RuleSet) multiLine.invoke(arh, new Object[] {rules, br});
      br.close();
      
      ASN1Rule ar1 = (ASN1Rule) ars.rules.get(0);
      ASN1Rule ar2 = (ASN1Rule) ars.rules.get(1);
      
      String expected = "int1,INTEGER:bool,BOOLEAN true";
      String actual = ar1.name + "," + ar1.type + ":" + ar2.name + "," + ar2.type;
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testMultiLine() failed");
    }
  }

  @Test
  public void testCreateRules() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    
    String rules = "begin\nname ::= INTEGER (0..10)";
    BufferedReader br = new BufferedReader(new StringReader(rules));
    
    try {
      createRules.invoke(arh, new Object[] {br});
      
      HashMap hm = (HashMap) ruleSetMap.get(arh);
      ASN1RuleSet ars = (ASN1RuleSet) hm.get("keyname");
      ASN1Rule ar = (ASN1Rule) ars.rules.get(0);
      
      String expected = "name,INTEGER";
      String actual = ar.name + "," + ar.type;
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testCreateRules() failed");
    }
  }
  
  @Test
  public void testCreateRulesString() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    String rules = "begin\nname ::= INTEGER (0..10)";
    
    try {
      arh.createRules(rules);
      
      HashMap hm = (HashMap) ruleSetMap.get(arh);
      ASN1RuleSet ars = (ASN1RuleSet) hm.get("keyname");
      ASN1Rule ar = (ASN1Rule) ars.rules.get(0);
      
      String expected = "name,INTEGER";
      String actual = ar.name + "," + ar.type;
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testCreateRulesString() failed");
    }
  }

  @Test
  public void testCreateRulesFile() {
    ASN1RuleHandler arh = new ASN1RuleHandler();
    HelpClass hc = new HelpClass();
    String homeDir = System.getProperty("user.home");
    
    File x = hc.createFile(homeDir, "asn1File", "begin\nname ::= INTEGER (0..10)");
    x.deleteOnExit();
    
    try {
      arh.createRules(x);
      
      HashMap hm = (HashMap) ruleSetMap.get(arh);
      ASN1RuleSet ars = (ASN1RuleSet) hm.get("keyname");
      ASN1Rule ar = (ASN1Rule) ars.rules.get(0);
      
      String expected = "name,INTEGER";
      String actual = ar.name + "," + ar.type;
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testCreateRulesFile() failed");
    }
  }
}
