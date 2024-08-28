package com.distocraft.dc5000.etl.parser;

import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.distocraft.dc5000.etl.engine.common.Share;
import com.distocraft.dc5000.repository.cache.DFormat;
import com.distocraft.dc5000.repository.cache.DItem;
import com.distocraft.dc5000.repository.cache.DataFormatCache;
import ssc.rockfactory.RockFactory;

/**
 * Tests for the measurement file class. Measurement data parsed out from source
 * files is handled and written to an physical file.
 * 
 * @author EJAAVAH
 * 
 */
public class MeasurementFileImplTest {

  private static MeasurementFileImpl objUnderTest;

  private static SourceFile testSourceFile;

  private static File outDir;

  private static File actualSourceFile;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    /* Creating the physical output folder */
    outDir = new File(System.getProperty("user.dir") + "/outDirForMFileTest");
    outDir.mkdir();
    outDir.deleteOnExit();

    /* Creating the physical source file */
    actualSourceFile = new File(System.getProperty("user.dir"), "actualSourceFile");
    actualSourceFile.deleteOnExit();
    try {
      PrintWriter pw = new PrintWriter(new FileWriter(actualSourceFile));
      pw.print("Source file - This represents the physical file which will be parsed");
      pw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    /* Initializing transformer cache */
    TransformerCache tfc = new TransformerCache();

    /* Creating new instance of DataFormatCache */
    Constructor dfcConstructor = DataFormatCache.class.getDeclaredConstructor(new Class[] {});
    dfcConstructor.setAccessible(true);
    DataFormatCache dfcInstance = (DataFormatCache) dfcConstructor.newInstance(new Object[] {});

    /* Initializing lists of interface names and add it to dfc instance */
    HashSet IFaceNames = new HashSet();
    HashMap tagMap = new HashMap();
    DFormat dfObj1 = new DFormat("ifname", "tid", "dfid", "fname1", "trID");
    DFormat dfObj2 = new DFormat("ifname", "tid", "dfid", "fname2", "trID");
    DFormat dfObj3 = new DFormat("ifname", "tid", "dfid", "fname3", "trID");
    List dataList1 = new ArrayList();
    List dataList2 = new ArrayList();
    dataList1.add(new DItem("filename", 0, "header", null, "varchar", 10, 0));
    dataList2.add(new DItem("header1", 1, "DATE_ID", null, "varchar", 10, 0 ));
    dataList2.add(new DItem("header2", 2, "Key1", null, "varchar", 10, 0));
    dataList2.add(new DItem("header3", 3, "Key2", null, "varchar", 10, 0));
    dataList2.add(new DItem("header4", 4, "Key3", null, "varchar", 10, 0));
    dfObj1.setItems(dataList1);
    dfObj2.setItems(dataList2);
    dfObj3.setItems(new ArrayList());
    IFaceNames.add("IFaceName");
    tagMap.put("IFaceName_null", dfObj1);
    tagMap.put("IFaceName_tagID", dfObj2);
    tagMap.put("IFaceName_woMetaData", dfObj3);
    Field if_names = dfcInstance.getClass().getDeclaredField("if_names");
    Field it_map = dfcInstance.getClass().getDeclaredField("it_map");
    if_names.setAccessible(true);
    it_map.setAccessible(true);
    if_names.set(dfcInstance, IFaceNames);
    it_map.set(dfcInstance, tagMap);

    /* Reflecting the dfc field and initialize it with our own instance */
    Field dfc = DataFormatCache.class.getDeclaredField("dfc");
    dfc.setAccessible(true);
    dfc.set(DataFormatCache.class, dfcInstance);

    /* Testmode on */
    MeasurementFileImpl.setTestMode(true);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Cleaning up after all tests */
    objUnderTest = null;
  }

  @Before
  public void setUp() throws Exception {

    /* Source file properties */
    Properties sfConf = new Properties();
    sfConf.setProperty("interfaceName", "IFaceName");
    sfConf.setProperty("outDir", outDir.getPath());

    /* Reflecting SourceFile class constructor and creating an instance of it */
    Constructor sourceFileConstructor = SourceFile.class.getDeclaredConstructor(new Class[] { File.class,
        Properties.class, RockFactory.class, RockFactory.class, ParseSession.class, ParserDebugger.class,
        String.class, Logger.class });
    sourceFileConstructor.setAccessible(true);
    testSourceFile = (SourceFile) sourceFileConstructor.newInstance(actualSourceFile, sfConf, null, null,
        new ParseSession(101, null), null, "", Logger.getLogger("sfLogger"));

    /* Initializing tested class before each test */
    objUnderTest = new MeasurementFileImpl(testSourceFile, "tagID", "techPack", "set_type", "set_name", "workerName",
        Logger.getLogger("mfLogger"));
  }

  @After
  public void tearDown() throws Exception {

    /* Cleaning up after all tests */
    testSourceFile = null;
    objUnderTest = null;
  }

  /**
   * Testing getting the character encoding from share object.
   * 
   * @throws Exception
   */
  @Test
  public void testGetOutputCharsetEncoding() throws Exception {

    /* Reflecting the tested method */
    Method getOutputCharsetEncoding = objUnderTest.getClass().getDeclaredMethod("getOutputCharsetEncoding",
        new Class[] {});
    getOutputCharsetEncoding.setAccessible(true);

    /* Adding the character encoding to share objects map */
    Share share = Share.instance();
    share.add("dwhdb_charset_encoding", "UTF-8");

    /* Asserting that charset encoding from share object is returned */
    Charset expectedCSE = Charset.forName("UTF-8");
    assertEquals(expectedCSE, getOutputCharsetEncoding.invoke(objUnderTest, null));
  }

  /**
   * Testing data saving. Data is written to a physical output file.
   * 
   * @throws Exception
   */
  @Test
  public void testSaveData() throws Exception {

    /* Adding map of data to be written to the physical output file */
    HashMap dataMap = new HashMap();
    dataMap.put("DATE_ID", "20021001");
    dataMap.put("Key1", "Value1");
    dataMap.put("Key2", "Value2");
    dataMap.put("Key3", "Value3");
    objUnderTest.addData(dataMap);

    /* Filepath to the outputfile */
    File outputFilePath = new File(outDir + "/techPack");
    //File outputFile = new File(outputFilePath, "fname2_workerName_20021001");
    File outputFile = new File(outputFilePath, "fname2_workerName");

    /* Invoking the tested method */
    objUnderTest.saveData();

    /* Asserting that data has bee written to the measurement file */
    BufferedReader br = new BufferedReader(new FileReader(outputFile));
    String actual = br.readLine();
    br.close();
    objUnderTest.close();
    assertEquals("20021001\tValue1\tValue2\tValue3\t", actual);

    /* Cleaning up after test */
    br.close();
    objUnderTest.close();
    outputFile.delete();
    outputFilePath.delete();
  }

  /**
   * Testing data saving using debug, metadata creating and other exceptional
   * cases.
   * 
   * @throws Exception
   */
  @Test
  public void testSaveDataWithExceptionalInput() throws Exception {

    /* Initializing tested class with different tag id */
    objUnderTest = new MeasurementFileImpl(testSourceFile, "woMetaData", "techPack", "set_type", "set_name",
        "workerName", Logger.getLogger("mfLogger"));

    /* Test, debug and metadata mode on */
    MeasurementFileImpl.setTestMode(true);
    Field debug = objUnderTest.getClass().getDeclaredField("debug");
    Field ignoreMetaData = objUnderTest.getClass().getDeclaredField("ignoreMetaData");
    debug.setAccessible(true);
    ignoreMetaData.setAccessible(true);
    debug.set(objUnderTest, true);
    ignoreMetaData.set(objUnderTest, true);

    /* Adding map of data to be written to the physical output file */
    HashMap dataMap = new HashMap();
    dataMap.put("Key1", "Value1");
    dataMap.put("Key2", "Value2");
    dataMap.put("Key3", "Value3");
    objUnderTest.addData(dataMap);

    /* Filepath to the outputfile */
    File outputFilePath = new File(outDir + "/techPack");
    //File outputFile = new File(outputFilePath, "fname3_workerName_null");
    File outputFile = new File(outputFilePath, "fname3_workerName");
    outputFilePath.deleteOnExit();
    outputFile.deleteOnExit();

    /* Invoking the tested method */
    objUnderTest.saveData();

    /* Asserting that data has bee written to the measurement file */
    BufferedReader br = new BufferedReader(new FileReader(outputFile));
    String line = br.readLine();
    String actual = "";
    while (line != null) {
      actual += line;
      line = br.readLine();
    }
    br.close();
    objUnderTest.close();
    assertEquals("55555\tValue1\tValue2\tValue3\t111111111\t", actual);

    /* Cleaning up after test */
    objUnderTest.close();
  }

  /**
   * Testing measurement file closing.
   * 
   * @throws Exception
   */
  @Test
  public void testClose() throws Exception {

    /* Adding data to fields which should be cleared on closing */
    Field writer = objUnderTest.getClass().getDeclaredField("writer");
    Field data = objUnderTest.getClass().getDeclaredField("data");
    writer.setAccessible(true);
    data.setAccessible(true);
    OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(new File("testFile")));
    PrintWriter pw = new PrintWriter(new BufferedWriter(osw));
    writer.set(objUnderTest, pw);
    HashMap dataMap = new HashMap();
    dataMap.put("key", "value");
    objUnderTest.addData(dataMap);

    /* Invoking the tested method and assert that fields are cleared */
    objUnderTest.close();
    HashMap actualMap = (HashMap) data.get(objUnderTest);
    OutputStreamWriter actualWriter = (OutputStreamWriter) writer.get(objUnderTest);
    assertEquals(null + ", " + null, actualMap + ", " + actualWriter);

    /* Closing testFile */
    File testFileRemoval = new File(System.getProperty("user.dir"), "testFile");
    testFileRemoval.deleteOnExit();
  }

  /**
   * Testing measurement file opening.
   * 
   * @throws Exception
   */
  @Test
  public void testOpenMeasurementFile() throws Exception {

    /* Reflecting tested method */
    Method openMeasurementFile = objUnderTest.getClass().getDeclaredMethod("openMeasurementFile",
        new Class[] { String.class, String.class });
    openMeasurementFile.setAccessible(true);
    
    Field writer = objUnderTest.getClass().getDeclaredField("writer");
    writer.setAccessible(true);

    /* Setting header writing on */
    Field delimitedHeader = objUnderTest.getClass().getDeclaredField("delimitedHeader");
    Field writeHeader = testSourceFile.getClass().getDeclaredField("writeHeader");
    delimitedHeader.setAccessible(true);
    writeHeader.setAccessible(true);
    delimitedHeader.set(objUnderTest, true);
    writeHeader.set(testSourceFile, true);

    /* Filepath to the outputfile */
    File outputFilePath = new File(outDir + "/techPack");
    //File outputFile = new File(outputFilePath, "fname2_workerName_20081009");
    File outputFile = new File(outputFilePath, "fname2_workerName");
    outputFilePath.deleteOnExit();
    outputFile.deleteOnExit();

    /*
     * Invoking the tested method and asserting that writer is created and
     * header is written
     */
    openMeasurementFile.invoke(objUnderTest, new Object[] { "tagID", "20081009" });
    PrintWriter actualpw = (PrintWriter) writer.get(objUnderTest);
    String actual = actualpw.getClass() + ", ";
    actualpw.close();
    BufferedReader br = new BufferedReader(new FileReader(outputFile));
    String line = br.readLine();
    while (line != null) {
      actual += line;
      line = br.readLine();
    }
    br.close();
    assertEquals("class java.io.PrintWriter, header1\theader2\theader3\theader4\t", actual);
  }

  /**
   * Testing measurement file opening.
   * 
   * @throws Exception
   */
  @Test
  public void testDetermineOutputFileName() throws Exception {

    /* Reflecting tested method */
    Method determineOutputFileName = objUnderTest.getClass().getDeclaredMethod("determineOutputFileName",
        new Class[] { String.class, String.class });
    determineOutputFileName.setAccessible(true);

    /* Asserting that correct filename is returned */
    File outputFile = new File(outDir + "/techPack", "typeName");
    String actualFileName = (String) determineOutputFileName.invoke(objUnderTest, new Object[] { "tagID", "typeName" });
    assertEquals(outputFile.getAbsolutePath(), actualFileName);
  }
}
