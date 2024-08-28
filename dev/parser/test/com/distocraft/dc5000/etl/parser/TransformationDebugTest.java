package com.distocraft.dc5000.etl.parser;

import static org.junit.Assert.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Vector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for transformation debugger.
 * 
 * @author EJAAVAH
 * 
 */
public class TransformationDebugTest {

  private static TransformationDebug objUnderTest;

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Clean up after tests */
    objUnderTest = null;
  }

  @Before
  public void setUp() throws Exception {

    /* Initializing tested object before each test */
    objUnderTest = new TransformationDebug();
  }

  @After
  public void tearDown() throws Exception {

    /* Clean up after each test */
    objUnderTest = null;
  }

  /**
   * Testing if data from given map is added to a vector.
   * 
   * @throws Exception
   */
  @Test
  public void testBefore() throws Exception {

    /* Reflecting first field to test flagging */
    Field first = objUnderTest.getClass().getDeclaredField("first");
    first.setAccessible(true);

    /* Creating a map of data */
    HashMap mapOfData = new HashMap();
    mapOfData.put("key1", "value1");
    mapOfData.put("key2", "value2");

    /* Calling tested method and asserting maps are put into vector */
    objUnderTest.before(mapOfData);
    String actual = first.get(objUnderTest) + ", " + objUnderTest.getData().toString();
    String expected = false + ", [{key2=value2, key1=value1}]";
    assertEquals(expected, actual);
  }

  /**
   * Testing if data from given map is added to a vector.
   * 
   * @throws Exception
   */
  @Test
  public void testAfter() throws Exception {

    /* Creating a map of data */
    HashMap mapOfData = new HashMap();
    mapOfData.put("key1", "value1");
    mapOfData.put("key2", "value2");

    /* Calling tested method and asserting maps are put into vector */
    objUnderTest.after(mapOfData);
    assertEquals("[{key2=value2, key1=value1}]", objUnderTest.getData().toString());
  }

  /**
   * Testing set and get methods.
   * 
   * @throws Exception
   */
  @Test
  public void testSetAndGetMethods() throws Exception {

    /* Setting values through set methods */
    objUnderTest.setName("testName");
    objUnderTest.setTransformerId("testTransformerID");

    /* Asserting get methods retrieve correct data */
    assertEquals("testName, testTransformerID", objUnderTest.getName() + ", " + objUnderTest.getTransformerId());
  }
}
