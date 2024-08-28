/**
 * 
 */
package com.distocraft.dc5000.etl.parser;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * @author ecarbjo
 * 
 */
public class SourceFileGunzipTest {

  private static File testGzFile;

  private static File testFile;

  // change this if you change the placement of the testfile.
  private static final File testFileReal = new File("c:/tmp/test.xml.gz");

  private static Properties conf = new Properties();

  private static final String testStr = "This is a simple test string that I will write to a file to test gzip";

  @BeforeClass
  public static void setupBeforeClass() throws Exception {
    testGzFile = new File(System.getProperty("user.dir"), "test.gz");
    testGzFile.deleteOnExit();

    testFile = new File(System.getProperty("user.dir"), "test.txt");
    testFile.deleteOnExit();

    try {
      GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(testGzFile));
      PrintWriter pw = new PrintWriter(gos);
      pw.write(testStr);
      pw.close();

      pw = new PrintWriter(testFile);
      pw.write(testStr);
      pw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    conf.setProperty("writeHeader", "false");
    conf.setProperty("interfaceName", "IFaceName");
    conf.setProperty("minFileAge", "1");
    conf.setProperty("useZip", "gzip");
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for
   * {@link com.distocraft.dc5000.etl.parser.SourceFile#gunzip(java.io.File)}.
   */
  @Test
  public void testGunzip() {
    SourceFile sf = new SourceFile(testGzFile, conf, null, null, null, null, "gzip", Logger.getAnonymousLogger());
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(sf.getFileInputStream()));
      String in = br.readLine();

      assertEquals(in, testStr);
    } catch (Exception e) {
      fail("Caught an exception: " + e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.distocraft.dc5000.etl.parser.SourceFile#gunzip(java.io.File)}
   * for a regular file.
   */
  @Test
  public void testGunzipRegularFile() {
    SourceFile sf = new SourceFile(testFile, conf, null, null, null, null, "gzip", Logger.getAnonymousLogger());
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(sf.getFileInputStream()));
      String in = br.readLine();

      assertEquals(in, testStr);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Caught an exception: " + e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.distocraft.dc5000.etl.parser.SourceFile#gunzip(java.io.File)}
   * This method tests an actual gzipped xml file meant for ENIQ.
   */
  @Test
  public void testGunzipRealFile() {
    if (testFileReal.exists()) {
      SourceFile sf = new SourceFile(testFileReal, conf, null, null, null, null, "gzip", Logger.getAnonymousLogger());
      try {
        // now try to read the XML from the stream. This will automatically test
        // the well-formedness of the XML
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);

        SAXParser parser = factory.newSAXParser();

        XMLReader reader = parser.getXMLReader();
        reader.parse(new InputSource(sf.getFileInputStream()));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        System.out.println("Caught a FileNotFoundException, this was probably caused by missing DTD files and hence it will not fail the test. It is worth checking out though.");
      } catch (Exception e) {
        e.printStackTrace();
        fail("Caught an exception: " + e.getMessage());
      }
    } else {
      System.out.println("Test \"testGunzipRealFile\" could not be run because file " + testFileReal.getAbsolutePath()
          + " doesn't exist.");
    }
  }
}
