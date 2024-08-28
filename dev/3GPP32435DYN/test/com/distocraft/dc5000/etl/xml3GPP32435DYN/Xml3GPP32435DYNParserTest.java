package com.distocraft.dc5000.etl.xml3GPP32435;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.common.SessionHandler;
import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.engine.common.EngineCom;
import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.MeasurementFileImpl;
import com.distocraft.dc5000.etl.parser.ParseSession;
import com.distocraft.dc5000.etl.parser.ParserDebugger;
import com.distocraft.dc5000.etl.parser.SourceFile;
import com.distocraft.dc5000.etl.parser.TransformerCache;
import com.distocraft.dc5000.repository.cache.DFormat;
import com.distocraft.dc5000.repository.cache.DItem;
import com.distocraft.dc5000.repository.cache.DataFormatCache;
import com.ericsson.junit.HelpClass;
import com.sun.xml.internal.bind.util.AttributesImpl;

/**
 * 
 * @author ejarsok
 *
 */

public class Xml3GPP32435ParserTest {
  
  private static Method getSeconds;
  
  private static Method handleTAGmoid;
  
  private static Method calculateBegintime;

  private static Field mainParserObject;

  private static Field techPack;

  private static Field setType;

  private static Field setName;

  private static Field status;

  private static Field workerName;
  
  private static Field charValue;
  
  private static Field errorList;
  
  private static Field fileFormatVersion;
  
  private static Field vendorName;
  
  private static Field dnPrefix;
  
  private static Field fsLocalDN;
  
  private static Field elementType;
  
  private static Field collectionBeginTime;
  
  private static Field measNameMap;
  
  private static Field meLocalDN;
  
  private static Field userLabel;
  
  private static Field swVersion;
  
  private static Field measInfoId;
  
  private static Field jobId;
  
  private static Field granularityPeriodDuration;
  
  private static Field granularityPeriodEndTime;
  
  private static Field repPeriodDuration;
  
  private static Field measIndex;
  
  private static Field measValueIndex;
  
  private static Field objectClass;
  
  private static Field readVendorIDFrom;
  
  private static Field fillEmptyMoidStyle;
  
  private static Field fillEmptyMoidValue;
  
  private static Field objectMask;
  
  private static Field sourceFile;
  
  private static Field measObjLdn;
  
  private static Field oldObjClass;
  
  private static Field suspectFlag;
  
  private static Field measFile;
  
  private static Constructor sourceFileC;
  
  private static Constructor MeasurementFileImplC;
  
  private static Constructor ParseSessionC;
  
  //MeasurementFileImpl
  private static Field data;
  
  //Main
  private static Field fileList;

  private static Field psession;
  
  
  @BeforeClass
  public static void init() {
    
    try {
      getSeconds = Xml3GPP32435Parser.class.getDeclaredMethod("getSeconds", new Class[] {String.class});
      handleTAGmoid = Xml3GPP32435Parser.class.getDeclaredMethod("handleTAGmoid", new Class[] {String.class});
      calculateBegintime = Xml3GPP32435Parser.class.getDeclaredMethod("calculateBegintime", new Class[] {});
      mainParserObject = Xml3GPP32435Parser.class.getDeclaredField("mainParserObject");
      techPack = Xml3GPP32435Parser.class.getDeclaredField("techPack");
      setType = Xml3GPP32435Parser.class.getDeclaredField("setType");
      setName = Xml3GPP32435Parser.class.getDeclaredField("setName");
      status = Xml3GPP32435Parser.class.getDeclaredField("status");
      workerName = Xml3GPP32435Parser.class.getDeclaredField("workerName");
      charValue = Xml3GPP32435Parser.class.getDeclaredField("charValue");
      errorList = Xml3GPP32435Parser.class.getDeclaredField("errorList");
      fileFormatVersion = Xml3GPP32435Parser.class.getDeclaredField("fileFormatVersion");
      vendorName = Xml3GPP32435Parser.class.getDeclaredField("vendorName");
      dnPrefix = Xml3GPP32435Parser.class.getDeclaredField("dnPrefix");
      fsLocalDN = Xml3GPP32435Parser.class.getDeclaredField("fsLocalDN");
      elementType = Xml3GPP32435Parser.class.getDeclaredField("elementType");
      collectionBeginTime = Xml3GPP32435Parser.class.getDeclaredField("collectionBeginTime");
      measNameMap = Xml3GPP32435Parser.class.getDeclaredField("measNameMap");
      meLocalDN = Xml3GPP32435Parser.class.getDeclaredField("meLocalDN");
      userLabel = Xml3GPP32435Parser.class.getDeclaredField("userLabel");
      swVersion = Xml3GPP32435Parser.class.getDeclaredField("swVersion");
      measInfoId = Xml3GPP32435Parser.class.getDeclaredField("measInfoId");
      jobId = Xml3GPP32435Parser.class.getDeclaredField("jobId");
      granularityPeriodDuration = Xml3GPP32435Parser.class.getDeclaredField("granularityPeriodDuration");
      granularityPeriodEndTime = Xml3GPP32435Parser.class.getDeclaredField("granularityPeriodEndTime");
      repPeriodDuration = Xml3GPP32435Parser.class.getDeclaredField("repPeriodDuration");
      measIndex = Xml3GPP32435Parser.class.getDeclaredField("measIndex");
      measValueIndex = Xml3GPP32435Parser.class.getDeclaredField("measValueIndex");
      objectClass = Xml3GPP32435Parser.class.getDeclaredField("objectClass");
      readVendorIDFrom = Xml3GPP32435Parser.class.getDeclaredField("readVendorIDFrom");
      fillEmptyMoidStyle = Xml3GPP32435Parser.class.getDeclaredField("fillEmptyMoidStyle");
      fillEmptyMoidValue = Xml3GPP32435Parser.class.getDeclaredField("fillEmptyMoidValue");
      objectMask = Xml3GPP32435Parser.class.getDeclaredField("objectMask");
      sourceFile = Xml3GPP32435Parser.class.getDeclaredField("sourceFile");
      measObjLdn = Xml3GPP32435Parser.class.getDeclaredField("measObjLdn");
      oldObjClass = Xml3GPP32435Parser.class.getDeclaredField("oldObjClass");
      suspectFlag = Xml3GPP32435Parser.class.getDeclaredField("suspectFlag");
      measFile = Xml3GPP32435Parser.class.getDeclaredField("measFile");
            
      sourceFileC = SourceFile.class.getDeclaredConstructor(new Class[] { File.class, Properties.class,
          RockFactory.class, RockFactory.class, ParseSession.class, ParserDebugger.class, Logger.class });
      MeasurementFileImplC = MeasurementFileImpl.class.getDeclaredConstructor(new Class[] { SourceFile.class,
          String.class, String.class, String.class, String.class, Logger.class });
      ParseSessionC = ParseSession.class.getDeclaredConstructor(new Class[] {long.class, Properties.class});
      
      getSeconds.setAccessible(true);
      handleTAGmoid.setAccessible(true);
      calculateBegintime.setAccessible(true);
      mainParserObject.setAccessible(true);
      techPack.setAccessible(true);
      setType.setAccessible(true);
      setName.setAccessible(true);
      status.setAccessible(true);
      workerName.setAccessible(true);
      charValue.setAccessible(true);
      errorList.setAccessible(true);
      fileFormatVersion.setAccessible(true);
      vendorName.setAccessible(true);
      dnPrefix.setAccessible(true);
      fsLocalDN.setAccessible(true);
      elementType.setAccessible(true);
      collectionBeginTime.setAccessible(true);
      measNameMap.setAccessible(true);
      meLocalDN.setAccessible(true);
      userLabel.setAccessible(true);
      swVersion.setAccessible(true);
      measInfoId.setAccessible(true);
      jobId.setAccessible(true);
      granularityPeriodDuration.setAccessible(true);
      granularityPeriodEndTime.setAccessible(true);
      repPeriodDuration.setAccessible(true);
      measIndex.setAccessible(true);
      measValueIndex.setAccessible(true);
      objectClass.setAccessible(true);
      readVendorIDFrom.setAccessible(true);
      fillEmptyMoidStyle.setAccessible(true);
      fillEmptyMoidValue.setAccessible(true);
      objectMask.setAccessible(true);
      sourceFile.setAccessible(true);
      measObjLdn.setAccessible(true);
      oldObjClass.setAccessible(true);
      suspectFlag.setAccessible(true);
      measFile.setAccessible(true);
      
      sourceFileC.setAccessible(true);
      MeasurementFileImplC.setAccessible(true);
      ParseSessionC.setAccessible(true);
      
      //MeasurementFileImpl
      data = MeasurementFileImpl.class.getDeclaredField("data");
      
      data.setAccessible(true);
      
      // Main
      fileList = Main.class.getDeclaredField("fileList");
      psession = Main.class.getDeclaredField("psession");
      
      fileList.setAccessible(true);
      psession.setAccessible(true);
      
      
      Field dMap = DataFormatCache.class.getDeclaredField("map");
      dMap.setAccessible(true);
      DataFormatCache.initialize(null);
      
      ArrayList<DItem> al = new ArrayList();
      DItem di1 = new DItem("DATETIME_ID", 1, "first",  "pi");
      DItem di2 = new DItem("filename",    2, "second", "pi");
      DItem di3 = new DItem("DIRNAME",     3, "third",  "pi");
      al.add(di1);
      al.add(di2);
      al.add(di3);
      
      ArrayList<DItem> al2 = new ArrayList();
      DItem di21 = new DItem("value", 1, "first", "pi");
      al2.add(di21);
      

      DFormat df = new DFormat("tfid", "foldName", al);
      DFormat df2 = new DFormat("tfid", "foldName", al2);
      DataFormatCache dfc = new DataFormatCache();
      HashMap hm = (HashMap) dMap.get(dfc);
      hm.put("if:tagID", df);
      hm.put("if2:tagID2", df2);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
    
  }
  
  @Test
  public void testInit() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    xml3gpp.init(null, "techPack", "setType", "setName", "workerName");

    try {

      String expected = "null,techPack,setType,setName,1,workerName";
      String actual = mainParserObject.get(xml3gpp) + "," + techPack.get(xml3gpp) + "," + setType.get(xml3gpp) + ","
          + setName.get(xml3gpp) + "," + status.get(xml3gpp) + "," + workerName.get(xml3gpp);

      assertEquals(expected, actual);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testInit() failed");
    }
  }

  @Test
  public void testStatus() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    
    assertEquals(0, xml3gpp.status());
  }

  @Test
  public void testErrors() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    
    ArrayList al = new ArrayList();
    al.add("text");
    
    try {
      errorList.set(xml3gpp, al);
      
      /* Calling the tested method */
      ArrayList list = (ArrayList) xml3gpp.errors();
      
      assertTrue(list.contains("text"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testErrors() failed");
    }
  }

  @Test
  public void testRun() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    String homeDir = System.getProperty("user.home");
    Properties sprop = new Properties();
    sprop.setProperty("SessionHandling.storageFile", homeDir + File.separator + "storageFile");
    sprop.setProperty("SessionHandling.log.types", "AdapterLog");
    sprop.setProperty("SessionHandling.log.AdapterLog.class", "com.distocraft.dc5000.common.AdapterLog");
    sprop.setProperty("SessionHandling.log.ADAPTER.inputTableDir", homeDir);
    Properties prop = new Properties();
    prop.setProperty("baseDir", homeDir);
    prop.setProperty("interfaceName", "if2");
    prop.setProperty("x3GPPParser.vendorIDMask", "f.+(tagID2)");
    
    /* Creating file to be parsed */
    HelpClass hc = new HelpClass();
    ArrayList<File> al = new ArrayList<File>();
    File x = hc.createFile(homeDir, "3GPPfile.xml", "<measValue measObjLdn=\"ffftagID2\">\n\t<measData>\n\t\t<measType p=\"key\">value</measType>\n" +
                    "\t\t<r p=\"key\">rvalue</r>\n\t</measData>\n</measValue>");
    al.add(x);
    x.deleteOnExit();
    File out = new File(homeDir, "out");
    out.mkdir();
    
    /* Initializing transformer cache */
    TransformerCache tc = new TransformerCache();
    
    try {
      MeasurementFileImpl.setTestMode(true);
      ParseSession ps = (ParseSession) ParseSessionC.newInstance(new Object[] {1L, null});
      Main main = new Main(prop, "x", "x", "x", null, null, new EngineCom());
      fileList.set(main, al);
      psession.set(main, ps);
      
      /* Initializing Static Properties in order to initialize SessionHandler */
      StaticProperties.giveProperties(sprop);
      
      /* Initializing SessionHandler */
      SessionHandler.init();
      
      xml3gpp.init(main, "tp", "st", "sn", "wn");
      
      /* Calling the tested method */
      xml3gpp.run();
      
      File tp = new File(homeDir + File.separator + "out\\tp");
      File i = new File(homeDir + File.separator + "out\\tp\\foldName_wn_null");

      String actual = hc.readFileToString(i);
      String expected = "rvalue\t";

      MeasurementFileImpl mf = (MeasurementFileImpl) measFile.get(xml3gpp);
      mf.close(); // Should this be in endElement method

      i.delete();
      tp.delete();
      out.delete();

      assertEquals(expected, actual);
      
    } catch(Exception e) {
      e.printStackTrace();
      fail("testRun() failed");
    }
  }

  @Test
  public void testParseSourceFileStringStringString() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    xml3gpp.init(null, "techpack", "st", "sn", "worker");
    
    String homeDir = System.getProperty("user.home");
    
    Properties prop = new Properties();
    prop.setProperty("interfaceName", "if2");
    prop.setProperty("baseDir", homeDir);
    prop.setProperty("x3GPPParser.vendorIDMask", "f.+(tagID2)");
    
    HelpClass hc = new HelpClass();
    File x = hc.createFile(homeDir, "3GPPfile.xml", "<measValue measObjLdn=\"ffftagID2\">\n\t<measData>\n\t\t<measType p=\"key\">value</measType>\n" +
    		"\t\t<r p=\"key\">rvalue</r>\n\t</measData>\n</measValue>");
    x.deleteOnExit();
    File out = new File(homeDir, "out");
    out.mkdir();
    
    /* Initializing transformer cache */
    TransformerCache tc = new TransformerCache();
    
    try {
      MeasurementFileImpl.setTestMode(true);
      SourceFile sf = (SourceFile) sourceFileC.newInstance(new Object[] { x, prop, null, null, null, null, null });
      
      xml3gpp.parse(sf, "techPack", "setType", "setName");
      
      File tp = new File(homeDir + File.separator + "out\\techpack");
      File i = new File(homeDir + File.separator + "out\\techpack\\foldName_worker_null");

      String actual = hc.readFileToString(i);
      String expected = "rvalue\t";

      MeasurementFileImpl mf = (MeasurementFileImpl) measFile.get(xml3gpp);
      mf.close(); // Should this be in endElement method

      i.delete();
      tp.delete();
      out.delete();

      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testParseSourceFileStringStringString() failed");
    }
  }

  @Test
  public void testStrToMap() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    
    /* Calling the tested method */
    HashMap hm = xml3gpp.strToMap("this is string"); // ??? index++; ???
    
    assertEquals("string", hm.get("0"));
  }
  
  @Test
  public void testStartElementStringStringStringAttributes1() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    
    AttributesImpl atts = new AttributesImpl();
    atts.addAttribute("uri", "fileFormatVersion", "fileFormatVersion", "type", "ffv");
    atts.addAttribute("uri", "vendorName", "vendorName", "type", "VName");
    atts.addAttribute("uri", "dnPrefix", "dnPrefix", "type", "dnp");
    
    try {
      /* Calling the tested method */
      xml3gpp.startElement(null, null, "fileHeader", atts);
      
      String actual = "";
      String expected = "ffv,VName,dnp";
      actual += fileFormatVersion.get(xml3gpp) + "," + vendorName.get(xml3gpp) + "," + dnPrefix.get(xml3gpp);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStartElementStringStringStringAttributes1() failed");
    }
  }
  
  @Test
  public void testStartElementStringStringStringAttributes2() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    
    AttributesImpl atts = new AttributesImpl();
    atts.addAttribute("uri", "localDn", "localDn", "type", "ldn");
    atts.addAttribute("uri", "elementType", "elementType", "type", "etype");
    
    try {
      /* Calling the tested method */
      xml3gpp.startElement(null, null, "fileSender", atts);
      
      String actual = "";
      String expected = "ldn,etype";
      actual += fsLocalDN.get(xml3gpp) + "," + elementType.get(xml3gpp);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStartElementStringStringStringAttributes2() failed");
    }
  }
  
  @Test
  public void testStartElementStringStringStringAttributes3() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    
    AttributesImpl atts = new AttributesImpl();
    atts.addAttribute("uri", "beginTime", "beginTime", "type", "btime");
    
    try {
      /* Calling the tested method */
      xml3gpp.startElement(null, null, "measCollec", atts);
      
      String actual = "";
      String expected = "btime";
      actual += collectionBeginTime.get(xml3gpp);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStartElementStringStringStringAttributes3() failed");
    }
  }
  
  @Test
  public void testStartElementStringStringStringAttributes4() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    
    AttributesImpl atts = new AttributesImpl();
    atts.addAttribute("uri", "beginTime", "beginTime", "type", "btime");
    
    try {
      HashMap hm = null;
      
      /* Calling the tested method */
      xml3gpp.startElement(null, null, "measData", atts);
      
      hm = (HashMap) measNameMap.get(xml3gpp);
      
      assertNotNull(hm);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStartElementStringStringStringAttributes4() failed");
    }
  }
  
  @Test
  public void testStartElementStringStringStringAttributes5() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    
    AttributesImpl atts = new AttributesImpl();
    atts.addAttribute("uri", "localDn", "localDn", "type", "ldn");
    atts.addAttribute("uri", "userLabel", "userLabel", "type", "ulabel");
    atts.addAttribute("uri", "swVersion", "swVersion", "type", "swv");
    
    try {
      /* Calling the tested method */
      xml3gpp.startElement(null, null, "managedElement", atts);
      
      String actual = "";
      String expected = "ldn,ulabel,swv";
      actual += meLocalDN.get(xml3gpp) + "," + userLabel.get(xml3gpp) + "," + swVersion.get(xml3gpp);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStartElementStringStringStringAttributes5() failed");
    }
  }
  
  @Test
  public void testStartElementStringStringStringAttributes6() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    
    AttributesImpl atts = new AttributesImpl();
    atts.addAttribute("uri", "measInfoId", "measInfoId", "type", "mii");
    
    try {
      /* Calling the tested method */
      xml3gpp.startElement(null, null, "measInfo", atts);
      
      String actual = "";
      String expected = "mii";
      actual += measInfoId.get(xml3gpp);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStartElementStringStringStringAttributes6() failed");
    }
  }
  
  @Test
  public void testStartElementStringStringStringAttributes7() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    
    AttributesImpl atts = new AttributesImpl();
    atts.addAttribute("uri", "jobId", "jobId", "type", "jid");
    
    try {
      /* Calling the tested method */
      xml3gpp.startElement(null, null, "job", atts);
      
      String actual = "";
      String expected = "jid";
      actual += jobId.get(xml3gpp);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStartElementStringStringStringAttributes7() failed");
    }
  }
  
  @Test
  public void testStartElementStringStringStringAttributes8() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    
    AttributesImpl atts = new AttributesImpl();
    atts.addAttribute("uri", "duration", "duration", "type", "xx200S");
    atts.addAttribute("uri", "endTime", "endTime", "type", "etime");
    
    try {
      /* Calling the tested method */
      xml3gpp.startElement(null, null, "granPeriod", atts);
      
      String actual = "";
      String expected = "200,etime";
      actual += granularityPeriodDuration.get(xml3gpp) + "," + granularityPeriodEndTime.get(xml3gpp);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStartElementStringStringStringAttributes8() failed");
    }
  }
  
  @Test
  public void testStartElementStringStringStringAttributes9() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    
    AttributesImpl atts = new AttributesImpl();
    atts.addAttribute("uri", "duration", "duration", "type", "xx300S");
    
    try {
      /* Calling the tested method */
      xml3gpp.startElement(null, null, "repPeriod", atts);
      
      String actual = "";
      String expected = "300";
      actual += repPeriodDuration.get(xml3gpp);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStartElementStringStringStringAttributes9() failed");
    }
  }
  
  @Test
  public void testStartElementStringStringStringAttributes10() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    
    AttributesImpl atts = new AttributesImpl();
    atts.addAttribute("uri", "p", "p", "type", "P");
    
    try {
      /* Calling the tested method */
      xml3gpp.startElement(null, null, "measType", atts);
      
      String actual = "";
      String expected = "P";
      actual += measIndex.get(xml3gpp);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStartElementStringStringStringAttributes10() failed");
    }
  }
  
  @Test
  public void testStartElementStringStringStringAttributes11() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    xml3gpp.init(null, null, null, null, "workerName");
    Properties prop = new Properties();
    
    AttributesImpl atts = new AttributesImpl();
    atts.addAttribute("uri", "measObjLdn", "measObjLdn", "type", "filename");
    
    try {
      SourceFile sf = (SourceFile) sourceFileC.newInstance(new Object[] { null, prop, null, null, null, null, null });
      sourceFile.set(xml3gpp, sf);
      objectMask.set(xml3gpp, "f.+(name)");
      readVendorIDFrom.set(xml3gpp, "data");
      
      /* Calling the tested method */
      xml3gpp.startElement(null, null, "measValue", atts);
      
      String actual = "";
      String expected = "filename,name";
      actual += measObjLdn.get(xml3gpp) + "," + oldObjClass.get(xml3gpp);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStartElementStringStringStringAttributes11() failed");
    }
  }
  
  @Test
  public void testStartElementStringStringStringAttributes12() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    
    AttributesImpl atts = new AttributesImpl();
    atts.addAttribute("uri", "p", "p", "type", "P");
    
    try {
      /* Calling the tested method */
      xml3gpp.startElement(null, null, "r", atts);
      
      String actual = "";
      String expected = "P";
      actual += measValueIndex.get(xml3gpp);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStartElementStringStringStringAttributes12() failed");
    }
  }

  @Test
  public void testHandleTAGmoid() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    xml3gpp.init(null, null, null, null, "workerName");
    
    try {
      readVendorIDFrom.set(xml3gpp, "data");
      fillEmptyMoidStyle.set(xml3gpp, "static");
      fillEmptyMoidValue.set(xml3gpp, "filename");
      objectMask.set(xml3gpp, "f.+(name)");
      
      /* Calling the tested method */
      handleTAGmoid.invoke(xml3gpp, new Object[] {""});
      
      assertEquals("name", objectClass.get(xml3gpp));
            
    } catch (Exception e) {
      e.printStackTrace();
      fail("testHandleTAGmoid() failed");
    }
  }
  
  @Test
  public void testGetSeconds() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    
    try {
      /* Calling the tested method */
      String s = (String) getSeconds.invoke(xml3gpp, new Object[] {"xx100S"});
      
      assertEquals("100", s);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetSeconds() failed");
    }
  }
  
  @Test
  public void testEndElementStringStringString1() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    
    try {
      charValue.set(xml3gpp, "this is text");
      
      xml3gpp.endElement(null, null, "measTypes");
      
      /* Calling the tested method */
      HashMap hm = (HashMap) measNameMap.get(xml3gpp);
      
      assertEquals("text", hm.get("0"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testEndElementStringStringString1() failed");
    }
  }
  
  @Test
  public void testEndElementStringStringString2() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    
    try {
      charValue.set(xml3gpp, "Type");
      measIndex.set(xml3gpp, "key");
      measNameMap.set(xml3gpp, new HashMap());
      
      /* Calling the tested method */
      xml3gpp.endElement(null, null, "measType");
      
      HashMap hm = (HashMap) measNameMap.get(xml3gpp);
      
      assertEquals("Type", hm.get("key"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testEndElementStringStringString2()");
    }
  }
  
  @Test
  public void testEndElementStringStringString3() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    xml3gpp.init(null, null, null, null, "workerName");
    
    String homeDir = System.getProperty("user.home");
    
    Properties prop = new Properties();
    prop.setProperty("interfaceName", "if");
    prop.setProperty("baseDir", homeDir);
    prop.setProperty("debug", "true");
    
    HelpClass hc = new HelpClass();
    
    Logger log = Logger.getLogger("log");
    
    File out = new File(homeDir, "out");
    out.mkdir();
    
    TransformerCache tc = new TransformerCache();
    
    try {
      MeasurementFileImpl.setTestMode(true);
      
      granularityPeriodEndTime.set(xml3gpp, "2008-09-29T11:10:15+0300");
      granularityPeriodDuration.set(xml3gpp, "10");
      SourceFile sf = (SourceFile) sourceFileC.newInstance(new Object[] { null, prop, null, null,
          null, null, null });
      MeasurementFileImpl mf = (MeasurementFileImpl) MeasurementFileImplC.newInstance(new Object[] { sf, "tagID", "tp",
          null, null, log });
      
      measFile.set(xml3gpp, mf);
      
      /* Calling the tested method */
      xml3gpp.endElement(null, null, "measValue");

      File tp = new File(homeDir + File.separator + "out\\tp");
      File i = new File(homeDir + File.separator + "out\\tp\\foldName__null");

      String actual = hc.readFileToString(i);
      String expected = "2008-09-29T11:10:05+0300\tdummyfile\tdummydir\t";

      mf.close(); // Should this be in endElement method

      i.delete();
      tp.delete();
      out.delete();

      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testEndElementStringStringString3()");
    }
  }
    
  @Test
  public void testEndElementStringStringString4() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    xml3gpp.init(null, null, null, null, "workerName");
    
    Properties prop = new Properties();
    prop.setProperty("interfacename", "if");
    
    HashMap hm = new HashMap();
    hm.put("0", "value");
    
    Logger log = Logger.getLogger("log");
    
    try {
      charValue.set(xml3gpp, "Type");
      measNameMap.set(xml3gpp, hm);
      SourceFile sf = (SourceFile) sourceFileC.newInstance(new Object[] { null, prop, null, null,
          null, null, null });
      MeasurementFileImpl mf = (MeasurementFileImpl) MeasurementFileImplC.newInstance(new Object[] { sf, "tagID", null,
          null, null, log });
      
      measFile.set(xml3gpp, mf);
      
      /* Calling the tested method */
      xml3gpp.endElement(null, null, "measResults");
      
      HashMap map = (HashMap) data.get(mf);
      
      assertEquals("Type", map.get("value"));

      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testEndElementStringStringString4()");
    }
  }
  
  @Test
  public void testEndElementStringStringString5() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    xml3gpp.init(null, null, null, null, "workerName");
    
    Properties prop = new Properties();
    prop.setProperty("interfacename", "if");
    
    HashMap hm = new HashMap();
    hm.put("key", "value");
    
    Logger log = Logger.getLogger("log");
    
    try {
      charValue.set(xml3gpp, "Type");
      measValueIndex.set(xml3gpp, "key");
      measNameMap.set(xml3gpp, hm);
      SourceFile sf = (SourceFile) sourceFileC.newInstance(new Object[] { null, prop, null, null,
          null, null, null });
      MeasurementFileImpl mf = (MeasurementFileImpl) MeasurementFileImplC.newInstance(new Object[] { sf, "tagID", null,
          null, null, log });
      
      measFile.set(xml3gpp, mf);
      
      /* Calling the tested method */
      xml3gpp.endElement(null, null, "r");
     
      HashMap map = (HashMap) data.get(mf);
      
      assertEquals("Type", map.get("value"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testEndElementStringStringString5()");
    }
  }
  
  @Test
  public void testEndElementStringStringString6() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    
    try {
      charValue.set(xml3gpp, "flag");
      
      /* Calling the tested method */
      xml3gpp.endElement(null, null, "suspect");
      
      assertEquals("flag", suspectFlag.get(xml3gpp));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testEndElementStringStringString6() failed");
    }
  }

  @Test
  public void voidCalculateBegintime() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    
    try {
      granularityPeriodEndTime.set(xml3gpp, "2008-09-29T11:10:15+0300");
      granularityPeriodDuration.set(xml3gpp, "10");
      
      /* Calling the tested method */
      String s = (String) calculateBegintime.invoke(xml3gpp, new Object[] {});
      
      assertEquals("2008-09-29T11:10:05+0300", s);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("voidCalculateBegintime() failed");
    }
  }
  
  @Test
  public void testParseFileName1() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    xml3gpp.init(null, null, null, null, "workerName");

    /* Calling the tested method */
    String s = xml3gpp.parseFileName("filename", "f.+(name)");

    assertEquals("name", s);
  }

  @Test
  public void testParseFileName2() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    xml3gpp.init(null, null, null, null, "workerName");

    /* Calling the tested method */
    String s = xml3gpp.parseFileName("abcdefg", "f.+(name)");

    assertEquals("", s);
  }
  
  @Test
  public void testCharacters() {
    Xml3GPP32435Parser xml3gpp = new Xml3GPP32435Parser();
    char[] ch = new char[] { 'f', 'a', 'l', 's', 'e', ',', 't', 'r', 'u', 'e' };

    try {
      charValue.set(xml3gpp, "");

      /* Calling the tested method */
      xml3gpp.characters(ch, 6, 4);

      String s = (String) charValue.get(xml3gpp);

      assertEquals("true", s);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testCharacters() failed");
    }
  }

  @AfterClass
  public static void clean() {
    File i = new File(System.getProperty("user.home"), "storageFile");
    i.deleteOnExit();
  }
}
