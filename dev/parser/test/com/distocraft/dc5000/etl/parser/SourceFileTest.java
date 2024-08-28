package com.distocraft.dc5000.etl.parser;

import static org.junit.Assert.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.distocraft.dc5000.repository.cache.DFormat;
import com.distocraft.dc5000.repository.cache.DItem;
import com.distocraft.dc5000.repository.cache.DataFormatCache;
import ssc.rockfactory.RockFactory;

/**
 * Test class for SourceFile which represents the file to be parsed.
 * 
 * @author EJAAVAH
 * 
 */
public class SourceFileTest {

  private static SourceFile objUnderTest;

  private static File physicalSourceFile;

  private static File physicalZipFile;

  private static Field zipEntryList;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    /* Creating the physical source file for testing purposes */
    physicalSourceFile = new File(System.getProperty("user.dir"), "testSourceFile");
    physicalSourceFile.deleteOnExit();
    try {
      PrintWriter pw = new PrintWriter(new FileWriter(physicalSourceFile));
      pw.print("Test file for SourceFile test class");
      pw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    /* Creating zipfile */
    ZipOutputStream out = new ZipOutputStream(new FileOutputStream("testZipFile.zip"));
    FileInputStream in = new FileInputStream(physicalSourceFile);
    out.putNextEntry(new ZipEntry("testZippedFile"));
    byte[] buf = new byte[1024];
    int len;
    while ((len = in.read(buf)) > 0) {
      out.write(buf, 0, len);
    }
    out.closeEntry();
    in.close();
    out.close();
    physicalZipFile = new File(System.getProperty("user.dir"), "testZipFile.zip");
    physicalZipFile.deleteOnExit();

    /* Reflecting often used variables */
    zipEntryList = SourceFile.class.getDeclaredField("zipEntryList");
    zipEntryList.setAccessible(true);

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
    dataList1.add(new DItem("filename", 0, "header", null));
    dataList2.add(new DItem("header1", 1, "DATE_ID", null, "int", 1, 1, 1));
    dataList2.add(new DItem("header2", 2, "Key1", null, "int", 1, 1, 1));
    dataList2.add(new DItem("header3", 3, "Key2", null, "int", 1, 1, 1));
    dataList2.add(new DItem("header4", 4, "Key3", null, "int", 1, 1, 1));
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
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Cleaning up after tests */
    objUnderTest = null;
    System.gc();
    physicalZipFile.delete();
    physicalSourceFile.delete();
  }

  @Before
  public void setUp() throws Exception {

    /* Properties for sourcefile */
    Properties sfConf = new Properties();
    sfConf.setProperty("writeHeader", "false");
    sfConf.setProperty("interfaceName", "IFaceName");
    sfConf.setProperty("minFileAge", "1");

    /* Initializing rockfactory object */
    RockFactory rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);

    /* Initializing parse seesion object */
    ParseSession parseSession = new ParseSession(2, new Properties());

    /* Initializing parse debugger object */
    ParserDebugger parseDebugger = new ParserDebuggerCache();

    /* Initializing tested object before each test */
    objUnderTest = new SourceFile(physicalSourceFile, sfConf, rockFactory, rockFactory, parseSession, parseDebugger,
        Logger.getLogger("sfLog"));
  }

  @After
  public void tearDown() throws Exception {

    /* Cleaning up after each test */
    objUnderTest = null;
  }

  /**
   * Testing measurement type adding to a list and retrieving it.
   * 
   * @throws Exception
   */
  @Test
  public void testAddAndGetMeasType() throws Exception {

    /* Adding few measurement types to the list */
    objUnderTest.addMeastype("testMeasType1");
    objUnderTest.addMeastype("testMeasType1");
    objUnderTest.addMeastype("testMeasType2");

    /* Asserting that the values are added and that there is no doubles */
    assertEquals("[testMeasType1, testMeasType2]", objUnderTest.getMeastypeList().toString());
  }

  /**
   * Testing get and set methods to set and retrieve various variables from the
   * source file object.
   * 
   * @throws Exception
   */
  @Test
  public void testSetAndGetMethods() throws Exception {

    /* Setting few variables */
    objUnderTest.setBatchID(333);
    objUnderTest.setErrorFlag(true);
    objUnderTest.setSuspectedFlag(true);
    objUnderTest.setErrorMsg("testErrorMessage");
    objUnderTest.setParsingStatus("ParsingStatus");
    objUnderTest.setErrorMessage("anotherTestErrorMessage");
    objUnderTest.setParsingstarttime(5L);
    objUnderTest.setParsingendtime(8L);

    /* Asserting that get methods return what they should */
    String actual = objUnderTest.getRockFactory().getClass() + ", " + objUnderTest.getRepRockFactory().getClass()
        + ", " + objUnderTest.getParseSession().getClass() + ", " + objUnderTest.getWriteHeader() + ", "
        + objUnderTest.getErrorFlag() + ", " + objUnderTest.getSuspectedFlag() + ", " + objUnderTest.getErrorMsg()
        + ", " + objUnderTest.getLastModified() + ", " + objUnderTest.getBatchID() + ", " + objUnderTest.getName()
        + ", " + objUnderTest.getDir() + ", " + objUnderTest.getSize() + ", "
        + objUnderTest.getProperty("interfaceName") + ", " + objUnderTest.getProperty("notFoundProperty", "default")
        + ", " + objUnderTest.fileSize() + ", " + objUnderTest.getParsingStatus() + ", "
        + objUnderTest.getErrorMessage() + ", " + objUnderTest.getParsingstarttime() + ", "
        + objUnderTest.getParsingendtime();
    String expected = RockFactory.class + ", " + RockFactory.class + ", " + ParseSession.class + ", " + false + ", "
        + true + ", " + true + ", testErrorMessage, " + physicalSourceFile.lastModified() + ", 333, testSourceFile, "
        + physicalSourceFile.getParent() + ", " + physicalSourceFile.length()
        + ", IFaceName, default, 35, ParsingStatus, anotherTestErrorMessage, 5, 8";
    assertEquals(expected, actual);
  }

  /**
   * Testing file deletion.
   * 
   * @throws Exception
   */
  @Test
  public void testDelete() throws Exception {

    /* Calling the tested method and asserting file is deleted */
    objUnderTest.delete();
    assertEquals(false, physicalSourceFile.exists());

    /* Recreating the physical source file */
    physicalSourceFile = new File(System.getProperty("user.dir"), "testSourceFile");
    physicalSourceFile.deleteOnExit();
    try {
      PrintWriter pw = new PrintWriter(new FileWriter(physicalSourceFile));
      pw.print("Test file for SourceFile test class");
      pw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Testing Adding measurement files to a list and retrieving the row count
   * based on that list.
   * 
   * @throws Exception
   */
  @Test
  public void testAddMeasurementFilesAndGetRowCount() throws Exception {

    /* Creating few test measurement files and modifying their row count */
    MeasurementFileImpl testMeasFile1 = new MeasurementFileImpl(objUnderTest, "tagID", "techPack1", "set_type1",
        "set_name1", "workerName1", Logger.getLogger("mfLogger"));
    MeasurementFileImpl testMeasFile2 = new MeasurementFileImpl(objUnderTest, "tagID", "techPack2", "set_type2",
        "set_name2", "workerName2", Logger.getLogger("mfLogger"));
    Field rowcount = MeasurementFileImpl.class.getDeclaredField("rowcount");
    rowcount.setAccessible(true);
    rowcount.set(testMeasFile1, 5);
    rowcount.set(testMeasFile2, 3);

    /* Adding the measurement file to the measurement file list */
    objUnderTest.addMeasurementFile(testMeasFile1);
    objUnderTest.addMeasurementFile(testMeasFile2);

    /* Asserting that the measurement file has been added */
    assertEquals(8, objUnderTest.getRowCount());
  }

  /**
   * Testing FileInputStream opening.
   * 
   * @throws Exception
   */
  @Test
  public void testGetFileInputStream() throws Exception {

    /* checking stream opening with regular file */
    FileInputStream regularFis = (FileInputStream) objUnderTest.getFileInputStream();

    /* checking stream opening with zipped file */
    Field unzip = objUnderTest.getClass().getDeclaredField("unzip");
    unzip.setAccessible(true);
    unzip.set(objUnderTest, "zip");
    FileInputStream zippedFis = (FileInputStream) objUnderTest.getFileInputStream();

    /* Asserting that streams are returned */
    assertEquals(true + ", " + true, regularFis.getFD().valid() + ", " + zippedFis.getFD().valid());
  }

  /**
   * Testing checking of the next input stream. If there are more items in the
   * list true is returned, otherwise false.
   * 
   * @throws Exception
   */
  @Test
  public void testHasNextFileInputStream() throws Exception {

    /* Checking with empty list */
    String actual = objUnderTest.hasNextFileInputStream() + ", ";

    /* Adding items to the list and making the check again */
    ArrayList testList = new ArrayList();
    testList.add("nextValue");
    zipEntryList.set(objUnderTest, testList);
    actual += objUnderTest.hasNextFileInputStream() + "";

    /* Asserting that correct values are returned */
    assertEquals(false + ", " + true, actual);
  }

  /**
   * Testing initializing inputstream for zip files.
   * 
   * @throws Exception
   */
  @Test
  public void testGetNextFileInputStream() throws Exception {

    /* Calling the tested method with empty list of zip files */
    InputStream nullInputstream = objUnderTest.getNextFileInputStream();

    /* Creating zipentry and add it to zipfile list */
    ZipFile testZipFile = new ZipFile(physicalZipFile);
    ZipEntry testZipEntry = testZipFile.getEntry("testZippedFile");
    ArrayList zipFileList = new ArrayList();
    zipFileList.add(testZipEntry);
    zipEntryList.set(objUnderTest, zipFileList);

    /* Reflecting the zipfile variable from sourcefile */
    Field zipFile = objUnderTest.getClass().getDeclaredField("zipFile");
    zipFile.setAccessible(true);
    zipFile.set(objUnderTest, testZipFile);

    /* Calling the tested method again, inputstream should be returned */
    InputStream zipInputStream = objUnderTest.getNextFileInputStream();

    /* Asserting that the */
    assertEquals(null + ", class java.util.zip.ZipFile$1", nullInputstream + ", "
        + zipInputStream.getClass().toString());
  }

  /**
   * Testing unzipping of files. Retrieves entries of zipped files and opens up
   * inputstream for them.
   * 
   * @throws Exception
   */
  @Test
  public void testUnzip() throws Exception {

    /* Reflecting tested method */
    Method unzip = objUnderTest.getClass().getDeclaredMethod("unzip", new Class[] { File.class });
    unzip.setAccessible(true);

    /* Invoking the tested method and asserting inputstream is returned */
    InputStream actualInputStream = (InputStream) unzip.invoke(objUnderTest, new Object[] { physicalZipFile });
    assertEquals("class java.util.zip.ZipFile$1", actualInputStream.getClass().toString());
  }

  /**
   * Testing source and measurement file closing. Inputstream is also closed.
   * 
   * @throws Exception
   */
  @Test
  public void testClose() throws Exception {

    /* Creating measurement file to be closed */
    MeasurementFileImpl testMeasFile = new MeasurementFileImpl(objUnderTest, "tagID", "techPack", "set_type",
        "set_name", "workerName", Logger.getLogger("mfLogger"));
    objUnderTest.addMeasurementFile(testMeasFile);
    Field measurementFiles = objUnderTest.getClass().getDeclaredField("measurementFiles");
    measurementFiles.setAccessible(true);

    /*
     * Calling the tested method first time which should remove measurement file
     * from the list and close it
     */
    File closeTestFile = new File("closeTestfile");
    closeTestFile.deleteOnExit();
    OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(closeTestFile, true));
    PrintWriter pw = new PrintWriter(new BufferedWriter(osw));
    Field writer = testMeasFile.getClass().getDeclaredField("writer");
    writer.setAccessible(true);
    writer.set(testMeasFile, pw);
    objUnderTest.close();
    ArrayList mfList = (ArrayList) measurementFiles.get(objUnderTest);
    String actual = testMeasFile.isOpen() + ", " + mfList.size();

    /* Opening inputstream which the close method should close */
    Field fis = objUnderTest.getClass().getDeclaredField("fis");
    fis.setAccessible(true);
    FileInputStream testfis = new FileInputStream(physicalSourceFile);
    fis.set(objUnderTest, testfis);

    /* Calling the tested method and assert everything is closed */
    objUnderTest.close();
    actual += ", " + testfis.getFD().valid();
    assertEquals(false + ", 0, " + false, actual);
  }

  /**
   * Testing if file is old enough to be parsed. True is returned, if the file
   * is old enough to be parsed, otherwise false.
   * 
   * @throws Exception
   */
  @Test
  public void testIsOldEnoughToBeParsed() throws Exception {

    /* Reflecting the file field to modify lastModified value */
    Field file = objUnderTest.getClass().getDeclaredField("file");
    file.setAccessible(true);
    File ageCheckTestFile = new File(System.getProperty("user.dir"), "ageCheckTestFile");
    ageCheckTestFile.deleteOnExit();
    PrintWriter pw = new PrintWriter(new FileWriter(ageCheckTestFile));
    pw.print("Test file for SourceFile test class");
    pw.close();

    /* Setting test files last modified value to be younger than needed */
    ageCheckTestFile.setLastModified(System.currentTimeMillis() + 800000);
    file.set(objUnderTest, ageCheckTestFile);
    String actual = objUnderTest.isOldEnoughToBeParsed() + ", ";

    /* Setting test files last modified value to be old enough */
    ageCheckTestFile.setLastModified(System.currentTimeMillis() - 800000);
    file.set(objUnderTest, ageCheckTestFile);
    actual += objUnderTest.isOldEnoughToBeParsed();

    /* Asserting correct booleans are returned */
    assertEquals(false + ", " + true, actual);
  }

  /**
   * Testing file moving. Physical source file is moved to target directory.
   * 
   * @throws Exception
   */
  @Test
  public void testMove() throws Exception {

    /* Creating target directory */
    File targetDirectoryForMoveTest = new File(System.getProperty("user.dir"), "targetDirectoryForMoveTest");
    targetDirectoryForMoveTest.mkdir();
    targetDirectoryForMoveTest.deleteOnExit();

    /* Calling the tested object and asserting the file is moved */
    objUnderTest.move(targetDirectoryForMoveTest);
    File closeTestFile = new File(targetDirectoryForMoveTest, "testSourceFile");
    closeTestFile.deleteOnExit();
    assertEquals(true, closeTestFile.exists());
  }

  /**
   * Testing session log retrieving. Session data is added to a map which is
   * returned.
   * 
   * @throws Exception
   */
  @Test
  public void testGetSessionLog() throws Exception {

    /* Asserting map of session data is returned */
    HashMap sDataMap = (HashMap) objUnderTest.getSessionLog();
    String actual = sDataMap.get("sessionID") + ", " + sDataMap.get("batchID") + ", " + sDataMap.get("fileName") + ", "
        + sDataMap.get("source") + ", " + sDataMap.get("status");
    String expected = "2, -1, testSourceFile, IFaceName, INITIALIZED";
    assertEquals(expected, actual);
  }
}
