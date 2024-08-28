package com.distocraft.dc5000.etl.parser;

import static org.junit.Assert.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import org.junit.BeforeClass;
import org.junit.Test;
import com.distocraft.dc5000.repository.cache.DItem;

/**
 * Tests for ParseDebuggerCache class.
 * 
 * @author EJAAVAH
 * 
 */
public class ParserDebuggerCacheTest {

  private static Field pdc;

  private static Field data;

  private static Field dataitems;

  private static Field transformation;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    pdc = ParserDebuggerCache.class.getDeclaredField("pdc");
    data = ParserDebuggerCache.class.getDeclaredField("data");
    dataitems = ParserDebuggerCache.class.getDeclaredField("dataitems");
    transformation = ParserDebuggerCache.class.getDeclaredField("transformation");
    pdc.setAccessible(true);
    data.setAccessible(true);
    dataitems.setAccessible(true);
    transformation.setAccessible(true);
  }

  /**
   * Testing initialize method which should create new objects for class
   * variables.
   * 
   * @throws Exception
   */
  @Test
  public void testInitialize() throws Exception {

    /* Calling tested method and check that variables have been initialized */
    ParserDebuggerCache.initialize();
    ParserDebuggerCache pdcForInitializeTest = (ParserDebuggerCache) pdc.get(ParserDebuggerCache.class);
    String actual = pdcForInitializeTest.getClass() + ", " + data.get(pdcForInitializeTest).getClass() + ", "
        + dataitems.get(pdcForInitializeTest).getClass();
    String expected = ParserDebuggerCache.class + ", " + HashMap.class + ", " + HashMap.class;
    assertEquals(expected, actual);
  }

  /**
   * Testing parserDebuggerCache object retrieving.
   * 
   * @throws Exception
   */
  @Test
  public void testGetCache() throws Exception {

    /* Setting object for pdc and asserting it is retrieved */
    pdc.set(ParserDebuggerCache.class, new ParserDebuggerCache());
    assertEquals(ParserDebuggerCache.class, ParserDebuggerCache.getCache().getClass());
  }

  /**
   * Testing transformation data adding to a map.
   * 
   * @throws Exception
   */
  @Test
  public void testAfterTransformer() throws Exception {

    /* Initializing tested method and transformations used in test */
    ParserDebuggerCache.initialize();
    ParserDebuggerCache pdcForAfterTransformerTest = (ParserDebuggerCache) pdc.get(ParserDebuggerCache.class);
    TransformationDebug testTransformationDebug = new TransformationDebug();
    transformation.set(pdcForAfterTransformerTest, testTransformationDebug);

    /* Map of transformations */
    HashMap transformationMap = new HashMap();
    transformationMap.put("existingData", new ArrayList());
    data.set(pdcForAfterTransformerTest, transformationMap);

    /* Calling the tested method with different inputs */
    pdcForAfterTransformerTest.afterTransformer("notYetExistingData", new HashMap());
    pdcForAfterTransformerTest.afterTransformer("existingData", new HashMap());

    /* Asserting that vectors from transformation objects has been added */
    HashMap actualMap = (HashMap) data.get(pdcForAfterTransformerTest);
    String actual = actualMap.get("notYetExistingData").getClass() + ", " + actualMap.get("existingData").getClass();
    String expected = Vector.class + ", " + ArrayList.class;
    assertEquals(expected, actual);
  }

  /**
   * Testing data item setting.
   * 
   * @throws Exception
   */
  @Test
  public void testSetDatatitems() throws Exception {

    /* Initializing tested method and dataitems used in test */
    ParserDebuggerCache.initialize();
    ParserDebuggerCache pdcForSetDatatitemsTest = (ParserDebuggerCache) pdc.get(ParserDebuggerCache.class);

    /* Creating list of test data */
    ArrayList testDataList = new ArrayList();
    testDataList.add(new DItem("ditem1", 1, "dataID", "pi"));
    testDataList.add(new DItem("ditem2", 1, "dataID", "pi"));

    /* Calling the tested method and asserting that dataitems are added */
    pdcForSetDatatitemsTest.setDatatitems("ditem2=[dataID]", testDataList);
    assertEquals("{ditem2=[dataID]=[dataID, dataID]}", dataitems.get(pdcForSetDatatitemsTest).toString());
  }

  /**
   * Testing transformer ID setting.
   * 
   * @throws Exception
   */
  @Test
  public void testSetAndGetTransformerId() throws Exception {

    /* Setting and getting transformer id */
    ParserDebuggerCache pdcForSetAndGetTransformerIdTest = (ParserDebuggerCache) pdc.get(ParserDebuggerCache.class);
    pdcForSetAndGetTransformerIdTest.setTransformerId("testTransformerID");
    assertEquals("testTransformerID", pdcForSetAndGetTransformerIdTest.getTransformerId());
  }
}
