package com.ericsson.eniq.etl.ct;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
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

public class CTParserTest {

  private static Field mainParserObject;

  private static Field techPack;

  private static Field setType;

  private static Field setName;

  private static Field status;

  private static Field workerName;
  
  private static Field key;
  
  private static Field seqKey;
  
  private static Field seqCount;
  
  private static Field seqContainer;
  
  private static Field oldObjectClass;
  
  private static Field measData;
  
  private static Field readVendorIDFrom;
  
  private static Field objectMask;
  
  private static Field fdn;
  
  private static Field objectClass;

  private static Field sourceFile;
  
  private static Field charValue;
  
  private static Field measFile;
  
  private static Field multiLine;
  
  private static Constructor sourceFileC;
  
  private static Constructor ParseSessionC;
  
  //Main
  private static Field fileList;

  private static Field psession;
  
  @BeforeClass
  public static void init() {
    try {
      /* Reflecting fields and methods from the tested class */
      mainParserObject = CTParser.class.getDeclaredField("mainParserObject");
      techPack = CTParser.class.getDeclaredField("techPack");
      setType = CTParser.class.getDeclaredField("setType");
      setName = CTParser.class.getDeclaredField("setName");
      status = CTParser.class.getDeclaredField("status");
      workerName = CTParser.class.getDeclaredField("workerName");
      key = CTParser.class.getDeclaredField("key");
      seqKey = CTParser.class.getDeclaredField("seqKey");
      seqCount = CTParser.class.getDeclaredField("seqCount");
      seqContainer = CTParser.class.getDeclaredField("seqContainer"); 
      oldObjectClass = CTParser.class.getDeclaredField("oldObjectClass");
      measData = CTParser.class.getDeclaredField("measData");
      readVendorIDFrom = CTParser.class.getDeclaredField("readVendorIDFrom");
      objectMask = CTParser.class.getDeclaredField("objectMask");
      fdn = CTParser.class.getDeclaredField("fdn");
      objectClass = CTParser.class.getDeclaredField("objectClass");
      sourceFile = CTParser.class.getDeclaredField("sourceFile");
      charValue = CTParser.class.getDeclaredField("charValue");
      measFile = CTParser.class.getDeclaredField("measFile");
      multiLine = CTParser.class.getDeclaredField("multiLine");
      
      sourceFileC = SourceFile.class.getDeclaredConstructor(new Class[] { File.class, Properties.class,
          RockFactory.class, RockFactory.class, ParseSession.class, ParserDebugger.class, Logger.class });
      ParseSessionC = ParseSession.class.getDeclaredConstructor(new Class[] {long.class, Properties.class});
      
      
      mainParserObject.setAccessible(true);
      techPack.setAccessible(true);
      setType.setAccessible(true);
      setName.setAccessible(true);
      status.setAccessible(true);
      workerName.setAccessible(true);
      key.setAccessible(true);
      seqKey.setAccessible(true);
      seqCount.setAccessible(true);
      seqContainer.setAccessible(true);
      oldObjectClass.setAccessible(true);
      measData.setAccessible(true);
      readVendorIDFrom.setAccessible(true);
      objectMask.setAccessible(true);
      fdn.setAccessible(true);
      objectClass.setAccessible(true);
      sourceFile.setAccessible(true);
      charValue.setAccessible(true);
      measFile.setAccessible(true);
      multiLine.setAccessible(true);
      
      sourceFileC.setAccessible(true);
      ParseSessionC.setAccessible(true);
      
      // Main
      fileList = Main.class.getDeclaredField("fileList");
      psession = Main.class.getDeclaredField("psession");
      
      fileList.setAccessible(true);
      psession.setAccessible(true);
      
      
      Field dMap = DataFormatCache.class.getDeclaredField("map");
      dMap.setAccessible(true);
      DataFormatCache.initialize(null);

      ArrayList<DItem> al = new ArrayList<DItem>();
      DItem di1 = new DItem("key1", "first");
      DItem di2 = new DItem("key2", "second");
      al.add(di1);
      al.add(di2);
      
      ArrayList<DItem> al2 = new ArrayList<DItem>();
      DItem di3 = new DItem("seqKey", "third");
      al2.add(di3);

      DFormat df = new DFormat("tfid", "foldName", al);
      DFormat df2 = new DFormat("tfid", "foldName", al2);
      DataFormatCache dfc = new DataFormatCache();
      HashMap<String,DFormat> hm = (HashMap) dMap.get(dfc);
      hm.put("if:tagID", df);
      hm.put("if:tagID2", df2);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
  }
  
  @Test
  public void testInit() {
    CTParser cp = new CTParser();
    // initialize CTParser variables
    cp.init(null, "techPack", "setType", "setName", "workerName");

    try {

      String expected = "null,techPack,setType,setName,1,workerName";
      String actual = mainParserObject.get(cp) + "," + techPack.get(cp) + "," + setType.get(cp) + ","
          + setName.get(cp) + "," + status.get(cp) + "," + workerName.get(cp);

      assertEquals(expected, actual);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testInit() failed");
    }
  }

  @Test
  public void testStatus() {
    CTParser cp = new CTParser();
    
    assertEquals(0, cp.status());
  }

  @Test
  public void testRun() {
    CTParser cp = new CTParser();
    String homeDir = System.getProperty("user.home");
    Properties sprop = new Properties();
    sprop.setProperty("SessionHandling.storageFile", homeDir + File.separator + "storageFile");
    sprop.setProperty("SessionHandling.log.types", "AdapterLog");
    sprop.setProperty("SessionHandling.log.AdapterLog.class", "com.distocraft.dc5000.common.AdapterLog");
    sprop.setProperty("SessionHandling.log.ADAPTER.inputTableDir", homeDir);
    Properties prop = new Properties();
    prop.setProperty("baseDir", homeDir);
    prop.setProperty("interfaceName", "if");
    prop.setProperty("vendorIDMask", "f.+(tagID2)");
    
    /* Creating file to be parsed */
    HelpClass hc = new HelpClass();
    ArrayList<File> al = new ArrayList<File>();
    File x = hc.createFile(homeDir, "csexportFile.xml",
        "<?xml version=\"1.0\"?>\n<mo fdn=\"fffftagID2\">\n\t<attr name=\"seqKey\">seqValue</attr>\n</mo>");
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
      
      cp.init(main, "tp", "st", "sn", "wn");
      
      /* Calling the tested method */
      cp.run();
      
      File tp = new File(homeDir + File.separator + "out\\tp");
      File i = new File(homeDir + File.separator + "out\\tp\\foldName_wn_null");

      String actual = hc.readFileToString(i);
      String expected = "seqValue\t";

      MeasurementFileImpl mf = (MeasurementFileImpl) measFile.get(cp);
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
  public void testParse() {
    CTParser cp = new CTParser();
    cp.init(null, "techpack", "st", "sn", "worker");
    String homeDir = System.getProperty("user.home");
    Properties prop = new Properties();
    prop.setProperty("baseDir", homeDir);
    prop.setProperty("interfaceName", "if");
    prop.setProperty("vendorIDMask", "f.+(tagID2)");
    
    /* Creating file to be parsed */
    HelpClass hc = new HelpClass();
    File x = hc.createFile(homeDir, "ctFile.xml",
        "<?xml version=\"1.0\"?>\n<mo fdn=\"fffftagID2\">\n\t<attr name=\"seqKey\">seqvalue</attr>\n</mo>");
    x.deleteOnExit();
    File out = new File(homeDir, "out");
    out.mkdir();

    /* Initializing transformer cache */
    TransformerCache tc = new TransformerCache();
    
    try {
      MeasurementFileImpl.setTestMode(true);
      SourceFile sf = (SourceFile) sourceFileC.newInstance(new Object[] { x, prop, null, null, null, null, null });

      /* Calling the tested method */
      cp.parse(sf, "techPack", "setType", "setName");
      
      File tp = new File(homeDir + File.separator + "out\\techpack");
      File i = new File(homeDir + File.separator + "out\\techpack\\foldName_worker_null");

      String actual = hc.readFileToString(i);
      String expected = "seqvalue\t";

      MeasurementFileImpl mf = (MeasurementFileImpl) measFile.get(cp);
      mf.close(); // Should this be in endElement method

      i.delete();
      tp.delete();
      out.delete();

      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testParse() failed");
    }
  }
  
  @Test
  public void testStartDocument() {
    CTParser cp = new CTParser();
    cp.init(null, null, null, null, "worker");
    
    try {
      oldObjectClass.set(cp, "something");
      String actual = (String) oldObjectClass.get(cp);
      
      /* Calling the tested method */
      cp.startDocument();
      
      actual += "," + oldObjectClass.get(cp);
      String expected = "something,null";
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStartDocument() failed");
    }
  }

  @Test
  public void testEndDocument() {
    CTParser cp = new CTParser();
    cp.init(null, null, null, null, "worker");
    
    Properties prop = new Properties();
    
    HashMap<String, String> hm = new HashMap<String, String>();
    hm.put("key", "value");
    
    try {
      SourceFile sfile = (SourceFile) sourceFileC
          .newInstance(new Object[] { null, prop, null, null, null, null, null });
      sourceFile.set(cp, sfile);
      
      measData.set(cp, hm);
      
      /* Calling the tested method */
      cp.endDocument();
      
      String expected = "true,null";
      String actual = hm.isEmpty() + "," + sourceFile.get(cp);
            
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testEndDocument() failed");
    }
  }

  @Test
  public void testStartElementStringStringStringAttributes1() {
    CTParser cp = new CTParser();
    cp.init(null, null, null, null, "worker");

    AttributesImpl atts = new AttributesImpl();
    atts.addAttribute("uri", "fdn", "qName", "type", "filename");
    
    try {
      readVendorIDFrom.set(cp, "data");
      objectMask.set(cp, "f.+(name)");
      
      /* Calling the tested method */
      cp.startElement(null, null, "mo", atts);
      
      String expected = "filename,name";
      String actual = fdn.get(cp) + "," + objectClass.get(cp);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStartElementStringStringStringAttributes1() failed");
    }
  }
  
  @Test
  public void testStartElementStringStringStringAttributes2() {
    CTParser cp = new CTParser();
    cp.init(null, null, null, null, "worker");

    AttributesImpl atts = new AttributesImpl();
    
    try {
      readVendorIDFrom.set(cp, "notEqual");
      objectMask.set(cp, "f.+(name)");
      
      /* Calling the tested method */
      cp.startElement(null, null, "mo", atts);
      
      fail("Should't execute this line, SAXException expected");
      
    } catch (Exception e) {
      // Test passed
    }
  }
  
  @Test
  public void testStartElementStringStringStringAttributes3() {
    CTParser cp = new CTParser();

    AttributesImpl atts = new AttributesImpl();
    atts.addAttribute("uri", "name", "qName", "type", "KEY");
    
    try {
      /* Calling the tested method */
      cp.startElement(null, null, "attr", atts);
      
      String expected = "KEY";
      String actual = (String) key.get(cp);

      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStartElementStringStringStringAttributes3() failed");
    }
  }
  
  @Test
  public void testStartElementStringStringStringAttributes4() {
    CTParser cp = new CTParser();

    AttributesImpl atts = new AttributesImpl();
    atts.addAttribute("uri", "count", "qName", "type", "10");
    
    try {
      key.set(cp, "key");
      
      /* Calling the tested method */
      cp.startElement(null, null, "seq", atts);
      
      String expected = "key,10";
      String actual = seqKey.get(cp) + "," + seqCount.get(cp);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testStartElementStringStringStringAttributes4() failed");
    }
  }

  @Test
  public void testEndElementStringStringString1() {
    CTParser cp = new CTParser();
    
    try {
      charValue.set(cp, "value");
      seqCount.set(cp, 10);
      seqContainer.set(cp, new ArrayList());
      
      /* Calling the tested method */
      cp.endElement(null, null, "item");
      
      ArrayList al = (ArrayList) seqContainer.get(cp);
      
      assertTrue(al.contains("value"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testEndElementStringStringString1() failed");
    }
  }
  
  @Test
  public void testEndElementStringStringString2() {
    CTParser cp = new CTParser();
    cp.init(null, null, null, null, "worker");
    
    try {
      key.set(cp, "key");
      charValue.set(cp, "value");
      measData.set(cp, new HashMap());
      
      /* Calling the tested method */
      cp.endElement(null, null, "attr");
      
      HashMap hm = (HashMap) measData.get(cp);
      
      assertEquals("value", hm.get("key"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testEndElementStringStringString2() failed");
    }
  }
  
  @Test
  public void testEndElementStringStringString3() {
    CTParser cp = new CTParser();
    cp.init(null, "techPack", "setType", "setName", "workerName");
    HelpClass hc = new HelpClass();
    String homeDir = System.getProperty("user.home");
    
    Properties prop = new Properties();
    prop.setProperty("baseDir", homeDir);
    prop.setProperty("interfaceName", "if");
    
    File x = hc.createFile(homeDir, "ctFile", "<?xml version=\"1.0\"?>\n<ct>\n</ct>");
    x.deleteOnExit();
    File out = new File(homeDir, "out");
    out.mkdir();
    
    HashMap<String,String> hm = new HashMap<String,String>();
    hm.put("key1", "value1");
    hm.put("key2", "value2");
    
    TransformerCache tc = new TransformerCache();
    
    try {
      MeasurementFileImpl.setTestMode(true);
      
      SourceFile sfile = (SourceFile) sourceFileC
          .newInstance(new Object[] { x, prop, null, null, null, null, null });
      sourceFile.set(cp, sfile);
      objectClass.set(cp, "tagID");
      measData.set(cp, hm);
      
      /* Calling the tested method */
      cp.endElement(null, null, "mo");
      
      File tp = new File(homeDir + File.separator + "out\\techPack");
      File i = new File(homeDir + File.separator + "out\\techPack\\foldName_workerName_null");

      String actual = hc.readFileToString(i);
      String expected = "value1\tvalue2\t";

      MeasurementFileImpl mf = (MeasurementFileImpl) measFile.get(cp);
      mf.close(); // Should this be in endElement method

      i.delete();
      tp.delete();
      out.delete();

      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testEndElementStringStringString3() failed");
    }
  }
  
  @Test
  public void testEndElementStringStringString4() {
    CTParser cp = new CTParser();
    cp.init(null, "techPack", "setType", "setName", "workerName");
    
    HelpClass hc = new HelpClass();
    String homeDir = System.getProperty("user.home");
    
    Properties prop = new Properties();
    prop.setProperty("baseDir", homeDir);
    prop.setProperty("interfaceName", "if");
    
    File x = hc.createFile(homeDir, "ctFile", "<?xml version=\"1.0\"?>\n<ct>\n</ct>");
    x.deleteOnExit();
    File out = new File(homeDir, "out");
    out.mkdir();
    
    ArrayList<String> al = new ArrayList<String>();
    al.add("value1");
    al.add("value2");
    
    TransformerCache tc = new TransformerCache();
    
    try {
      MeasurementFileImpl.setTestMode(true);
      
      SourceFile sfile = (SourceFile) sourceFileC
          .newInstance(new Object[] { x, prop, null, null, null, null, null });
      sourceFile.set(cp, sfile);
      objectClass.set(cp, "tagID2");
      seqKey.set(cp, "seqKey");
      measData.set(cp, new HashMap());
      seqContainer.set(cp, al);
      
      /* Calling the tested method */
      cp.endElement(null, null, "mo");
      
      File tp = new File(homeDir + File.separator + "out\\techPack");
      File i = new File(homeDir + File.separator + "out\\techPack\\foldName_workerName_null");

      String actual = hc.readFileToString(i);
      String expected = "value1,value2\t";

      MeasurementFileImpl mf = (MeasurementFileImpl) measFile.get(cp);
      mf.close(); // Should this be in endElement method

      i.delete();
      tp.delete();
      out.delete();

      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testEndElementStringStringString4() failed");
    }
  }
  
  @Test
  public void testEndElementStringStringString5() {
    CTParser cp = new CTParser();
    cp.init(null, "techPack", "setType", "setName", "workerName");
    
    HelpClass hc = new HelpClass();
    String homeDir = System.getProperty("user.home");
    
    Properties prop = new Properties();
    prop.setProperty("baseDir", homeDir);
    prop.setProperty("interfaceName", "if");
    
    File x = hc.createFile(homeDir, "ctFile", "<?xml version=\"1.0\"?>\n<ct>\n</ct>");
    x.deleteOnExit();
    File out = new File(homeDir, "out");
    out.mkdir();
    
    ArrayList<String> al = new ArrayList<String>();
    al.add("value1");
    al.add("value2");
    
    TransformerCache tc = new TransformerCache();
    
    try {
      MeasurementFileImpl.setTestMode(true);
      
      SourceFile sfile = (SourceFile) sourceFileC
          .newInstance(new Object[] { x, prop, null, null, null, null, null });
      sourceFile.set(cp, sfile);
      objectClass.set(cp, "tagID2");
      seqKey.set(cp, "seqKey");
      multiLine.set(cp, true);
      measData.set(cp, new HashMap());
      seqContainer.set(cp, al);
      
      /* Calling the tested method */
      cp.endElement(null, null, "mo");
      
      File tp = new File(homeDir + File.separator + "out\\techPack");
      File i = new File(homeDir + File.separator + "out\\techPack\\foldName_workerName_null");

      ArrayList list = hc.readFileToArray(i);
      String actual = "";
      for(int j = 0; j < list.size(); j++)
        actual += list.get(j) + "\n";
      String expected = "value1\t\nvalue2\t\n";

      MeasurementFileImpl mf = (MeasurementFileImpl) measFile.get(cp);
      mf.close(); // Should this be in endElement method

      i.delete();
      tp.delete();
      out.delete();

      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testEndElementStringStringString4() failed");
    }
  }
  
  @Test
  public void testCharacters() {
    CTParser cp = new CTParser();
    char[] ch = new char[] { 'f', 'a', 'l', 's', 'e', ',', 't', 'r', 'u', 'e' };

    try {
      charValue.set(cp, "");

      /* Calling the tested method */
      cp.characters(ch, 6, 4);

      String s = (String) charValue.get(cp);

      assertEquals("true", s);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testCharacters() failed");
    }
  }

  @Test
  public void testParseFileName1() {
    CTParser cp = new CTParser();
    cp.init(null, "techPack", "setType", "setName", "workerName");

    /* Calling the tested method */
    String s = cp.parseFileName("filename", "f.+(name)");

    assertEquals("name", s);
  }
  
  @Test
  public void testParseFileName2() {
    CTParser cp = new CTParser();
    cp.init(null, "techPack", "setType", "setName", "workerName");

    /* Calling the tested method */
    String s = cp.parseFileName("abcdefg", "f.+(name)");

    assertEquals("", s);
  }
  
  @AfterClass
  public static void clean() {
    File i = new File(System.getProperty("user.home"), "storageFile");
    i.deleteOnExit();
  }
}
