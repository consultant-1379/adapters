package com.ericsson.eniq.etl.asn1;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.Logger;

import junit.framework.JUnit4TestAdapter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.parser.MeasurementFileImpl;
import com.distocraft.dc5000.etl.parser.ParseSession;
import com.distocraft.dc5000.etl.parser.ParserDebugger;
import com.distocraft.dc5000.etl.parser.SourceFile;
import com.distocraft.dc5000.etl.parser.TransformerCache;
import com.distocraft.dc5000.repository.cache.DFormat;
import com.distocraft.dc5000.repository.cache.DItem;
import com.distocraft.dc5000.repository.cache.DataFormatCache;
import com.ericsson.junit.HelpClass;

/**
 * 
 * @author ejarsok
 * 
 */

public class ASN1ParserTest {

  private static Field verbose;

  private static Field sf;

  private static Field seqNameList;

  private static Field seqName;

  private static Field verboseStuffer;

  private static Field vendorTag;

  private static Field asn1;

  private static Field measFileFooter;

  private static Field fileFormatVersion;

  private static Field senderName;

  private static Field senderType;

  private static Field vendorName;

  private static Field collectionBeginTime;
  
  private static Field nEDistinguishedName;
  
  private static Field nEUserName;
  
  private static Field counterList;
  
  private static Field measStartTime;
  
  private static Field granularityPeriod;
  
  private static Field dateFormatLen;
  
  private static Field measObjInstId;
  
  private static Field suspectFlag;
  
  private static Field objectMask;
  
  private static Field addObjectMask;
  
  private static Field addVendorTag;
  
  private static Field addVendorIDSet;
  
  private static Field MeasurementFile;

  private static Constructor MeasurementFileImplC;
  
  // init
  private static Field mainParserObject;

  private static Field techPack;

  private static Field setType;

  private static Field setName;

  private static Field status;

  private static Field workerName;

  private static Constructor sourceFileC;

  @BeforeClass
  public static void init() {
    try {
      verbose = ASN1Parser.class.getDeclaredField("verbose");
      sf = ASN1Parser.class.getDeclaredField("sf");
      seqNameList = ASN1Parser.class.getDeclaredField("seqNameList");
      seqName = ASN1Parser.class.getDeclaredField("seqName");
      verboseStuffer = ASN1Parser.class.getDeclaredField("verboseStuffer");
      vendorTag = ASN1Parser.class.getDeclaredField("vendorTag");
      asn1 = ASN1Parser.class.getDeclaredField("asn1");
      measFileFooter = ASN1Parser.class.getDeclaredField("measFileFooter");
      fileFormatVersion = ASN1Parser.class.getDeclaredField("fileFormatVersion");
      senderName = ASN1Parser.class.getDeclaredField("senderName");
      senderType = ASN1Parser.class.getDeclaredField("senderType");
      vendorName = ASN1Parser.class.getDeclaredField("vendorName");
      collectionBeginTime = ASN1Parser.class.getDeclaredField("collectionBeginTime");
      nEDistinguishedName = ASN1Parser.class.getDeclaredField("nEDistinguishedName");
      nEUserName = ASN1Parser.class.getDeclaredField("nEUserName");
      counterList = ASN1Parser.class.getDeclaredField("counterList");
      measStartTime = ASN1Parser.class.getDeclaredField("measStartTime");
      granularityPeriod = ASN1Parser.class.getDeclaredField("granularityPeriod");
      dateFormatLen = ASN1Parser.class.getDeclaredField("dateFormatLen");
      measObjInstId = ASN1Parser.class.getDeclaredField("measObjInstId");
      suspectFlag = ASN1Parser.class.getDeclaredField("suspectFlag");
      objectMask = ASN1Parser.class.getDeclaredField("objectMask");
      addObjectMask = ASN1Parser.class.getDeclaredField("addObjectMask");
      addVendorTag = ASN1Parser.class.getDeclaredField("addVendorTag");
      addVendorIDSet = ASN1Parser.class.getDeclaredField("addVendorIDSet");
      MeasurementFile = ASN1Parser.class.getDeclaredField("MeasurementFile");
      sourceFileC = SourceFile.class.getDeclaredConstructor(new Class[] { File.class, Properties.class, RockFactory.class, RockFactory.class, ParseSession.class, ParserDebugger.class,
          Logger.class });
      /*sourceFileC = SourceFile.class.getDeclaredConstructor(new Class[] { File.class, Properties.class, String.class,
          String.class, String.class, RockFactory.class, RockFactory.class, ParseSession.class, ParserDebugger.class,
          Logger.class });*/
      MeasurementFileImplC = MeasurementFileImpl.class.getDeclaredConstructor(new Class[] { SourceFile.class,
          String.class, String.class, String.class, String.class, Logger.class });
      
      mainParserObject = ASN1Parser.class.getDeclaredField("mainParserObject");
      techPack = ASN1Parser.class.getDeclaredField("techPack");
      setType = ASN1Parser.class.getDeclaredField("setType");
      setName = ASN1Parser.class.getDeclaredField("setName");
      status = ASN1Parser.class.getDeclaredField("status");
      workerName = ASN1Parser.class.getDeclaredField("workerName");

      verbose.setAccessible(true);
      sf.setAccessible(true);
      seqNameList.setAccessible(true);
      seqName.setAccessible(true);
      verboseStuffer.setAccessible(true);
      vendorTag.setAccessible(true);
      asn1.setAccessible(true);
      measFileFooter.setAccessible(true);
      fileFormatVersion.setAccessible(true);
      senderName.setAccessible(true);
      senderType.setAccessible(true);
      vendorName.setAccessible(true);
      collectionBeginTime.setAccessible(true);
      nEDistinguishedName.setAccessible(true);
      nEUserName.setAccessible(true);
      counterList.setAccessible(true);
      measStartTime.setAccessible(true);
      granularityPeriod.setAccessible(true);
      dateFormatLen.setAccessible(true);
      measObjInstId.setAccessible(true);
      suspectFlag.setAccessible(true);
      objectMask.setAccessible(true);
      addObjectMask.setAccessible(true);
      addVendorTag.setAccessible(true);
      addVendorIDSet.setAccessible(true);
      MeasurementFile.setAccessible(true);
      sourceFileC.setAccessible(true);
      MeasurementFileImplC.setAccessible(true);

      mainParserObject.setAccessible(true);
      techPack.setAccessible(true);
      setType.setAccessible(true);
      setName.setAccessible(true);
      status.setAccessible(true);
      workerName.setAccessible(true);

      // MeasurementFileImpl saveData method need these
      Field dMap = DataFormatCache.class.getDeclaredField("map");
      dMap.setAccessible(true);
      DataFormatCache.initialize(null);

      ArrayList<DItem> al = new ArrayList();
      DItem di1 = new DItem("JVM_TIMEZONE", "first"); 
      DItem di2 = new DItem("Filename", "second");
      DItem di3 = new DItem("DIRNAME", "third");
      DItem di4 = new DItem("DC_SUSPECTFLAG", "fourth");
      al.add(di1);
      al.add(di2);
      al.add(di3);
      al.add(di4);
      
      ArrayList<DItem> al2 = new ArrayList();
      DItem di21 = new DItem("senderName", "first");
      DItem di22 = new DItem("measObjInstId", "second");
      DItem di23 = new DItem("MeasObjInstId", "third");
      DItem di24 = new DItem("measFileFooter", "fourth");
      al2.add(di21);
      al2.add(di22);
      al2.add(di23);
      al2.add(di24);
      
      DFormat df = new DFormat(null, null, null);
      DFormat df2 = new DFormat("tfid", "foldName", al);
      DFormat df3 = new DFormat("tfid", "foldName", al2);

      DataFormatCache dfc = DataFormatCache.getCache();
      HashMap hm = (HashMap) dMap.get(dfc);
      hm.put("if:tagID", df);
      hm.put("if2:tagID2", df2);
      hm.put("if2:CLSMS.SANTH2B", df2); // samplefile vendorTag CLSMS.SANTH2B
      hm.put("if2:CLSMS.AMPSANA", df3); // samplefile vendorTag CLSMS.AMPSANA

    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
  }

  @Ignore
  public void testInit() {
    ASN1Parser ap = new ASN1Parser();
    ap.init(null, "tp", "st", "sn", "wn");

    try {
      String expected = "null,tp,st,sn,wn,1";
      String actual = (String) mainParserObject.get(ap) + "," + techPack.get(ap) + "," + setType.get(ap) + ","
          + setName.get(ap) + "," + workerName.get(ap) + "," + status.get(ap);

      assertEquals(expected, actual);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testInit() failed");
    }
  }

  @Ignore
  public void testChoice1() {
    ASN1Parser ap = new ASN1Parser();
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.type = "INTEGER";
    byte[] b = new byte[] { 9, 9 };

    try {
      asn1.set(ap, ah);
      
      /* Calling the tested method */
      String s = ap.choice(ar, b);

      assertEquals("2313", s);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testChoice() failed");
    }
  }

  @Ignore
  public void testChoice2() {
    ASN1Parser ap = new ASN1Parser();
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.type = "REAL";
    byte[] b = new byte[] { 0, 0, 1 };

    try {
      asn1.set(ap, ah);
      
      /* Calling the tested method */
      String s = ap.choice(ar, b);

      assertEquals("1.0", s);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testChoice2() failed");
    }
  }

  @Ignore
  public void testChoice3() {
    ASN1Parser ap = new ASN1Parser();
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.type = "NULL";
    byte[] b = new byte[] { 0, 0, 1 };

    try {
      asn1.set(ap, ah);
      
      /* Calling the tested method */
      String s = ap.choice(ar, b);

      assertEquals("NULL", s);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testChoice3() failed");
    }
  }

  @Ignore
  public void testChoice4() {
    ASN1Parser ap = new ASN1Parser();
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.type = "GraphicString";
    byte[] b = "foobar".getBytes();

    try {
      asn1.set(ap, ah);
      
      /* Calling the tested method */
      String s = ap.choice(ar, b);

      assertEquals("foobar", s);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testChoice4() failed");
    }
  }

  @Ignore
  public void testChoice5() {
    ASN1Parser ap = new ASN1Parser();
    ap.init(null, null, null, null, "workerName");
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.type = "xxxx";
    byte[] b = "foobar".getBytes();

    try {
      asn1.set(ap, ah);
      
      /* Calling the tested method */
      String s = ap.choice(ar, b);

      assertEquals("N/A", s);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testChoice5() failed");
    }
  }

  @Ignore
  public void testSeqStart1() {
    ASN1Parser ap = new ASN1Parser();
    ap.init(null, null, null, null, "workerName");
    ArrayList l = new ArrayList();
    StringBuffer sb = new StringBuffer();

    try {
      verbose.set(ap, true);
      seqNameList.set(ap, l);
      verboseStuffer.set(ap, sb);
      
      /* Calling the tested method */
      ap.seqStart(0, 0, "MeasType");

      String expected = "true, ";
      String actual = l.contains("MeasType") + "," + sb.toString();
      assertEquals(expected, actual);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testSeqStart() failed");
    }
  }

  @Ignore
  public void testSeqStart2() {
    ASN1Parser ap = new ASN1Parser();
    ap.init(null, "tp", "st", "sn", "wn");
    ArrayList l = new ArrayList();
    StringBuffer sb = new StringBuffer();
    Properties prop = new Properties();
    prop.setProperty("interfaceName", "if");

    TransformerCache tc = new TransformerCache();

    try {
      SourceFile sfile = (SourceFile) sourceFileC.newInstance(new Object[] { null, prop, null, null,
          null, null, null });
      sf.set(ap, sfile);
      vendorTag.set(ap, "tagID");
      seqNameList.set(ap, l);
      verboseStuffer.set(ap, sb);
      suspectFlag.set(ap, true);
      
      /* Calling the tested method */
      ap.seqStart(0, 0, "MeasResult");
      
      String actual = "";
      String expected = "false";
      actual += suspectFlag.get(ap);
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testSeqStart2() failed");
    }
  }

  @Ignore
  public void testSeqEnd1() {
    ASN1Parser ap = new ASN1Parser();
    
    try {
      ap.seqEnd(0, "");
      fail("Should't execute this line, exception expected");
      
    } catch (Exception e) {
      //Test passed
    }
  }
  
  @Ignore
  public void testSeqEnd2() {
    ASN1Parser ap = new ASN1Parser();
    String homeDir = System.getProperty("user.home");
    
    //initialize needed variables
    Properties prop = new Properties();
    prop.setProperty("interfaceName", "if2");
    prop.setProperty("debug", "true");
    prop.setProperty("baseDir", homeDir);
    
    ArrayList l = new ArrayList();
    l.add("MeasValue");
    l.add("MeasValue");
    Logger log = Logger.getLogger("Log");
    
    HelpClass hc = new HelpClass();
    File x = hc.createFile(homeDir, "sfile", "Meaningles");
    File out = new File(homeDir, "out");
    out.mkdir();
    
    try {
      
      MeasurementFileImpl.setTestMode(true);
      SourceFile sfile = (SourceFile) sourceFileC.newInstance(new Object[] { x, prop, null, null,
          null, null, null });
      MeasurementFileImpl mf = (MeasurementFileImpl) MeasurementFileImplC.newInstance(new Object[] { sfile, "tagID2", "tp",
          null, null, log });
      
      //initialize ASN1Parser fields
      MeasurementFile.set(ap, mf);
      sf.set(ap, sfile);
      seqNameList.set(ap, l);
      
      //run seqEnd method to save data into file
      ap.seqEnd(0, "");
      mf.close();
      
      File tp = new File(homeDir + File.separator + "out\\tp");
      File i = new File(homeDir + File.separator + "out\\tp\\foldName__null");
      
      String expected = "+0300\tsfile\tC:\\Users\\ejarsok\tfalse\t";
      String actual = hc.readFileToString(i); //read data from file
      
      i.delete();
      tp.delete();
      out.delete();
      x.delete();
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testSeqEnd2() failed");
    }
  }

  @Ignore
  public void testPrimitive1() {
    ASN1Parser ap = new ASN1Parser();
    ap.init(null, null, null, null, "workerName");
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.name = "TimeStamp";
    ar.type = "UTF8String";
    byte[] b = "text".getBytes();

    try {
      verbose.set(ap, true);
      asn1.set(ap, ah);
      seqName.set(ap, "MeasDataCollection");
      
      /* Calling the tested method */
      ap.primitive(0, null, b, ar);
      
      String s = (String) measFileFooter.get(ap);

      assertEquals("text", s);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testPrimitive1() failed");
    }
  }

  @Ignore
  public void testPrimitive2() {
    ASN1Parser ap = new ASN1Parser();
    ap.init(null, null, null, null, "workerName");
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.type = "UTF8String";

    byte[] bi = new byte[] { 9, 9 };
    byte[][] bs = new byte[][] {"text1".getBytes(),"text2".getBytes(),"text3".getBytes(),"text4".getBytes()};

    try {
      asn1.set(ap, ah);
      String[] stable = new String[] { "fileFormatVersion", "senderName", "senderType", "vendorName",
          "collectionBeginTime" };
      seqName.set(ap, "MeasFileHeader");

      // Calling primite method 5 times
      for (int i = 0; i < stable.length; i++) {
        if (stable[i] == "fileFormatVersion") {
          ar.name = stable[i];
          ap.primitive(0, null, bi, ar); // asn1.doInt(data);
        } else {
          ar.name = stable[i];
          ap.primitive(0, null, bs[i-1], ar); // asn1.doString(rule, data);
        }
      }

      String expected = "2313,text1,text2,text3,text4";
      String s = (String) fileFormatVersion.get(ap) + "," + senderName.get(ap) + "," + senderType.get(ap) + ","
          + vendorName.get(ap) + "," + collectionBeginTime.get(ap);

      assertEquals(expected, s);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testPrimitive2() failed");
    }
  }
  
  @Ignore
  public void testPrimitive3() {
    ASN1Parser ap = new ASN1Parser();
    ap.init(null, null, null, null, "workerName");
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.type = "UTF8String";

    byte[][] bs = new byte[][] {"text1".getBytes(),"text2".getBytes()};

    try {
      asn1.set(ap, ah);
      String[] stable = new String[] { "nEDistinguishedName", "nEUserName"};
      seqName.set(ap, "NEId");

      // Calling primite method 2 times
      for (int i = 0; i < stable.length; i++) {
          ar.name = stable[i];
          ap.primitive(0, null, bs[i], ar); // asn1.doString(rule, data);
        
      }

      String expected = "text1,text2";
      String s = (String) nEDistinguishedName.get(ap) + "," + nEUserName.get(ap);

      assertEquals(expected, s);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testPrimitive3() failed");
    }
  }
  
  @Ignore
  public void testPrimitive4() {
    ASN1Parser ap = new ASN1Parser();
    ap.init(null, null, null, null, "workerName");
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.type = "UTF8String";

    String date = "200809181200";
    byte[] bi = new byte[] { 9, 9 };
    byte[] bs = date.getBytes();

    try {
      dateFormatLen.set(ap, date.length());
      asn1.set(ap, ah);
      String[] stable = new String[] { "TimeStamp", "granularityPeriod"};
      seqName.set(ap, "MeasInfo");

      // Calling primite method 2 times
      for (int i = 0; i < stable.length; i++) {
        if(stable[i] == "TimeStamp") {
          ar.name = stable[i];
          ap.primitive(0, null, bs, ar); // asn1.doString(rule, data);
        } else {
          ar.name = stable[i];
          ap.primitive(0, null, bi, ar);
        }
        
      }

      String expected = "200809181200,2313";
      String s = (String) measStartTime.get(ap) + "," + granularityPeriod.get(ap);

      assertEquals(expected, s);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testPrimitive3() failed");
    }
  }
  
  @Ignore
  public void testPrimitive5() {
    ASN1Parser ap = new ASN1Parser();
    ap.init(null, null, null, null, "workerName");
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.name = "TimeStamp";
    ar.type = "UTF8String";
    ArrayList al = new ArrayList();
    byte[] b = "text".getBytes();

    try {
      asn1.set(ap, ah);
      seqName.set(ap, "MeasType");
      counterList.set(ap, al);
      
      /* Calling the tested method */
      ap.primitive(0, null, b, ar);

      assertTrue(al.contains("text"));

    } catch (Exception e) {
      e.printStackTrace();
      fail("testPrimitive5() failed");
    }
  }

  @Ignore
  public void testPrimitive6() {
    ASN1Parser ap = new ASN1Parser();
    ap.init(null, null, null, null, "workerName");
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.type = "UTF8String";

    byte[] bi = new byte[] {1};
    byte[] bs = "filename".getBytes();

    try {
      objectMask.set(ap, "f.+(name)");
      addObjectMask.set(ap, "f.+(name)");
      asn1.set(ap, ah);
      String[] stable = new String[] { "measObjInstId", "suspectFlag"};
      seqName.set(ap, "MeasValue");

      // Calling primite method 2 times
      for (int i = 0; i < stable.length; i++) {
        if (stable[i] == "suspectFlag") {
          ar.name = stable[i];
          ap.primitive(0, null, bi, ar); // asn1.doBoolean(data);
        } else {
          ar.name = stable[i];
          ap.primitive(0, null, bs, ar); // asn1.doString(rule, data);
        }
      }

      String expected = "filename,true,name,name";
      String s = (String) measObjInstId.get(ap) + "," + suspectFlag.get(ap) + "," + vendorTag.get(ap) + "," + addVendorTag.get(ap);

      assertEquals(expected, s);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testPrimitive6() failed");
    }
  }
  
  @Ignore
  public void testPrimitive7() {
    ASN1Parser ap = new ASN1Parser();
    ap.init(null, null, null, null, "workerName");
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.type = "GraphicString";

    Properties prop = new Properties();
    
    HashSet hs = new HashSet();
    ArrayList l = new ArrayList();
    l.add("key");
    
    byte[] bs = "text".getBytes();

    Logger log = Logger.getLogger("Log");
    
    try {
      counterList.set(ap, l);
      addVendorIDSet.set(ap, hs);
      asn1.set(ap, ah);
      seqName.set(ap, "MeasResult");
      
      SourceFile sfile = (SourceFile) sourceFileC.newInstance(new Object[] { null, prop, null, null,
          null, null, null });
      MeasurementFileImpl mf = (MeasurementFileImpl) MeasurementFileImplC.newInstance(new Object[] { sfile, "tagID2", "tp",
          null, null, log });

      MeasurementFile.set(ap, mf);
      
      /* Calling the tested method */
      ap.primitive(0, null, bs, ar); // asn1.doString(rule, data);

      
      Field data = MeasurementFileImpl.class.getDeclaredField("data");
      data.setAccessible(true);
      
      HashMap hm = (HashMap) data.get(mf);
      
      String expected = "text";
      String s = (String) hm.get("key");

      assertEquals(expected, s);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testPrimitive7() failed");
    }
  }
  
  @Ignore
  public void testPrimitive8() {
    ASN1Parser ap = new ASN1Parser();
    ap.init(null, null, null, null, "workerName");
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.type = "GraphicString";

    Properties prop = new Properties();
    
    HashSet hs = new HashSet();
    hs.add("#ALL#");
    ArrayList l = new ArrayList();
    l.add("key");
    
    byte[] bs = "text".getBytes();

    Logger log = Logger.getLogger("Log");
    
    try {
      //initialize ASN1Parser fields
      counterList.set(ap, l);
      addVendorIDSet.set(ap, hs);
      asn1.set(ap, ah);
      seqName.set(ap, "MeasResult");
      addVendorTag.set(ap, "tag");
      
      SourceFile sfile = (SourceFile) sourceFileC.newInstance(new Object[] { null, prop, null, null,
          null, null, null });
      MeasurementFileImpl mf = (MeasurementFileImpl) MeasurementFileImplC.newInstance(new Object[] { sfile, "tagID2", "tp",
          null, null, log });

      MeasurementFile.set(ap, mf);
      
      /* Calling the tested method */
      ap.primitive(0, null, bs, ar); // asn1.doString(rule, data);

      
      Field data = MeasurementFileImpl.class.getDeclaredField("data");
      data.setAccessible(true);
      
      HashMap hm = (HashMap) data.get(mf);
      
      String expected = "text";
      String s = (String) hm.get("tag_key");

      assertEquals(expected, s);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testPrimitive7() failed");
    }
  }
  
  @Ignore
  public void testPrimitive9() {
    ASN1Parser ap = new ASN1Parser();
    ap.init(null, null, null, null, "workerName");
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.type = "GraphicString";

    Properties prop = new Properties();
    
    HashSet hs = new HashSet();
    hs.add("tag");
    ArrayList l = new ArrayList();
    l.add("key");
    
    byte[] bs = "text".getBytes();

    Logger log = Logger.getLogger("Log");
    
    try {
      //initialize ASN1Parser fields
      counterList.set(ap, l);
      addVendorIDSet.set(ap, hs);
      asn1.set(ap, ah);
      seqName.set(ap, "MeasResult");
      addVendorTag.set(ap, "tag");
      vendorTag.set(ap, "tag");
      
      SourceFile sfile = (SourceFile) sourceFileC.newInstance(new Object[] { null, prop, null, null,
          null, null, null });
      MeasurementFileImpl mf = (MeasurementFileImpl) MeasurementFileImplC.newInstance(new Object[] { sfile, "tagID2", "tp",
          null, null, log });

      MeasurementFile.set(ap, mf);
      
      /* Calling the tested method */
      ap.primitive(0, null, bs, ar); // asn1.doString(rule, data);

      
      Field data = MeasurementFileImpl.class.getDeclaredField("data");
      data.setAccessible(true);
      
      HashMap hm = (HashMap) data.get(mf);
      
      String expected = "text";
      String s = (String) hm.get("tag_key");

      assertEquals(expected, s);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testPrimitive7() failed");
    }
  }
  
  @Ignore
  public void testStatus() {
    ASN1Parser ap = new ASN1Parser();

    assertEquals(0, ap.status());
  }

  @Ignore
  public void testRun() {
    fail("Not yet implemented");
    //TODO
  }

  @Ignore
  public void testParseFileName() {
    ASN1Parser ap = new ASN1Parser();
    ap.init(null, "techPack", "setType", "setName", "workerName");
    
    /* Calling the tested method */
    String s = ap.parseFileName("filename", "f.+(name)");

    assertEquals("name", s);
  }

  @Ignore
  public void testParseFileName2() {
    ASN1Parser ap = new ASN1Parser();
    ap.init(null, "techPack", "setType", "setName", "workerName");
    
    /* Calling the tested method */
    String s = ap.parseFileName("abcdefg", "f.+(name)");

    assertEquals("", s);
  }

  @Test
  public void testParse() {
    //fail("Not yet implemented");
    //TODO
    
    ASN1Parser ap = new ASN1Parser();
    ap.init(null, "techPack", null, null, "worker");
    String homeDir = System.getProperty("user.home");
    Properties prop = new Properties();
    prop.setProperty("baseDir", homeDir); // where out folder is created
    prop.setProperty("interfaceName", "if2");
    
    File x = new File(homeDir, "A20070615.2300-20070616.0000_SAMPLE_12");
    File out = new File(homeDir, "out"); // folder where output folder and file is created
    out.mkdir();
    
    /* Initializing transformer cache */
    TransformerCache tc = new TransformerCache();
    
    try {
      MeasurementFileImpl.setTestMode(true);
      SourceFile sfile = (SourceFile) sourceFileC.newInstance(new Object[] { x, prop, null, null, null,
        null, null });
      
      /* Calling the tested method */
      ap.parse(sfile, "techPack", "setType", "setName");
      
      //Deleting files and folders
      File tp = new File(homeDir + File.separator + "out\\techPack");
      File i = new File(homeDir + File.separator + "out\\techPack\\foldName_worker_null");
      
      i.delete();
      tp.delete();
      out.delete();
      
    } catch(Exception e) {
      e.printStackTrace();
      fail("testParse() failed");
    }
  }

  @AfterClass
  public static void clean() {

  }

  /*
   * public static junit.framework.Test suite() { return new
   * JUnit4TestAdapter(ASN1ParserTest.class); }
   */
}
