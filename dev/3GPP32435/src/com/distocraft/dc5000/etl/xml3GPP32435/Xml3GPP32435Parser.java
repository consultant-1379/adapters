/*
 * Created on 3.1.2008
 *
 */
package com.distocraft.dc5000.etl.xml3GPP32435;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.distocraft.dc5000.etl.parser.Parser;
import com.distocraft.dc5000.etl.parser.SourceFile;
import com.ericsson.eniq.common.ENIQEntityResolver;

/**
 * 3GPP TS 32.435 Parser <br>
 * <br>
 * Configuration: <br>
 * <br>
 * Database usage: Not directly <br>
 * <br>
 * <br>
 * Version supported: v 7.20 <br>
 * <br>
 * Copyright Ericsson 2008 <br>
 * <br>
 * $id$ <br>
 * 
 * <br>
 * <br>
 * <table border="1" width="100%" cellpadding="3" cellspacing="0">
 * <tr bgcolor="#CCCCFF" class="TableHeasingColor">
 * <td colspan="4"><font size="+2"><b>Parameter Summary</b></font></td>
 * </tr>
 * <tr>
 * <td><b>Name</b></td>
 * <td><b>Key</b></td>
 * <td><b>Description</b></td>
 * <td><b>Default</b></td>
 * </tr>
 * <tr>
 * <td>Vendor ID mask</td>
 * <td>3GPP32435Parser.vendorIDMask</td>
 * <td>Defines how to parse the vendorID</td>
 * <td>.+,(.+)=.+</td>
 * </tr>
 * <tr>
 * <td>Vendor ID from</td>
 * <td>3GPP32435Parser.readVendorIDFrom</td>
 * <td>Defines where to parse vendor ID (file/data supported)</td>
 * <td>data</td>
 * </tr>
 * <tr>
 * <td>Fill empty MOID</td>
 * <td>3GPP32435Parser.FillEmptyMOID</td>
 * <td>Defines whether empty moid is filled or not (true/ false)</td>
 * <td>true</td>
 * </tr>
 * <tr>
 * <td>Fill empty MOID style</td>
 * <td>3GPP32435Parser.FillEmptyMOIDStyle</td>
 * <td>Defines the style how moid is filled (static/inc supported)</td>
 * <td>inc</td>
 * </tr>
 * <tr>
 * <td>Fill empty MOID value</td>
 * <td>3GPP32435Parser.FillEmptyMOIDValue</td>
 * <td>Defines the value for the moid that is filled</td>
 * <td>0</td>
 * </tr>
 * </table> <br>
 * <br>
 * <table border="1" width="100%" cellpadding="3" cellspacing="0">
 * <tr bgcolor="#CCCCFF" class="TableHeasingColor">
 * <td colspan="2"><font size="+2"><b>Added DataColumns</b></font></td>
 * </tr>
 * <tr>
 * <td><b>Column name</b></td>
 * <td><b>Description</b></td>
 * </tr>
 * <tr>
 * <td>collectionBeginTime</td>
 * <td>contains the begin time of the whole collection</td>
 * </tr>
 * <tr>
 * <td>objectClass</td>
 * <td>contains the vendor id parsed from MOID</td>
 * </tr>
 * <tr>
 * <td>MOID</td>
 * <td>contains the measured object id</td>
 * </tr>
 * <tr>
 * <td>filename</td>
 * <td> contains the filename of the inputdatafile.</td>
 * </tr>
 * <tr>
 * <td>PERIOD_DURATION</td>
 * <td>contains the parsed duration of this measurement</td>
 * </tr>
 * <tr>
 * <td>DATETIME_ID</td>
 * <td>contains the counted starttime of this measurement</td>
 * </tr>
 * <tr>
 * <td>DC_SUSPECTFLAG</td>
 * <td>contains the suspected flag value</td>
 * </tr>
 * <tr>
 * <td>DIRNAME</td>
 * <td>Contains full path to the inputdatafile.</td>
 * </tr>
 * <tr>
 * <td>JVM_TIMEZONE</td>
 * <td>contains the JVM timezone (example. +0200) </td>
 * </tr>
 * <tr>
 * <td>vendorName</td>
 * <td>contains the vendor name</td>
 * </tr> 
 * <tr>
 * <td>fileFormatVersion</td>
 * <td>contains the version of file format</td>
 * </tr>
 * <tr>
 * <td>dnPrefix</td>
 * <td>contains the dn prefix</td>
 * </tr>
 * <tr>
 * <td>localDn</td>
 * <td>contains the local dn</td>
 * </tr>
 * <tr>
 * <td>managedElementLocalDn</td>
 * <td>contains the local dn of managedElement element</td>
 * </tr>
 * <tr>
 * <td>elementType</td>
 * <td>contains the element type</td>
 * </tr>
 * <tr>
 * <td>userLabel</td>
 * <td>contains the user label</td>
 * </tr>
 * <tr>
 * <td>swVersion</td>
 * <td>contains the software version</td>
 * </tr>
 * <tr>
 * <td>endTime</td>
 * <td>contains the granularity period end time</td>
 * </tr>
 * <tr>
 * <td>measInfoId</td>
 * <td>contains the measInfoId</td>
 * </tr>
 * <tr>
 * <td>jobId</td>
 * <td>contains the jobId</td>
 * </tr>
 * <tr>
 * <td>&lt;measType&gt; (amount varies based on measurement executed)</td>
 * <td>&lt;measValue&gt; (amount varies based on measurement executed)</td>
 * </tr>
 * </table> <br>
 * <br>
 * 
 * @author pylkkänen <br>
 *         <br>
 * 
 */
public class Xml3GPP32435Parser extends DefaultHandler implements Parser {

  // Virtual machine timezone unlikely changes during execution of JVM
  private static final String JVM_TIMEZONE = (new SimpleDateFormat("Z")).format(new Date());
  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

  /**
   * 3GPP 7.20
   */
  private String fileFormatVersion;
  private String vendorName;
  private String dnPrefix;
  private String fsLocalDN;
  private String elementType;
  private String meLocalDN;
  private String userLabel;
  private String swVersion;
  private String collectionBeginTime;
  //private String collectionEndTime; //received so late, that migth not be used
  private String granularityPeriodDuration;
  private String granularityPeriodEndTime;
  private String repPeriodDuration;
  private String jobId;
  private String measInfoId;
  private HashMap measNameMap;
  private HashMap clusterMap;

  // Since special handling of #-mark causes parsing to fail in some cases,
  // let's keep the "original" counterrnames in the map also without special #-handling
  private HashMap<String, String> origMeasNameMap;
  
  private String suspectFlag = "";
  private String measIndex;
  private String measValueIndex;
  private String measObjLdn;

  private String charValue;

  private SourceFile sourceFile;
  
  private String objectClass;
  
  private String objectMask;
  
  private String matchObjectMaskAgainst;

  private String readVendorIDFrom;

  private boolean fillEmptyMoid = true;

  private String fillEmptyMoidStyle = "";

  private String fillEmptyMoidValue = "";

  //private Map measurement;

  private String oldObjClass;

  private MeasurementFile measFile = null;

  private Logger log;

  private String techPack;

  private String setType;

  private String setName;

  private int status = 0;

  private Main mainParserObject = null;

  private String workerName = "";

  final private List errorList = new ArrayList();


  /**
   * 
   */
  public void init(final Main main, final String techPack, final String setType, final String setName,
      final String workerName) {
    this.mainParserObject = main;
    this.techPack = techPack;
    this.setType = setType;
    this.setName = setName;
    this.status = 1;
    this.workerName = workerName;

    String logWorkerName = "";
    if (workerName.length() > 0) {
      logWorkerName = "." + workerName;
    }

    log = Logger.getLogger("etl." + techPack + "." + setType + "." + setName + ".parser.xml3GPP32435Parser" + logWorkerName);
  }

  public int status() {
    return status;
  }

  public List errors() {
    return errorList;
  }

  public void run() {

    try {

      this.status = 2;
      SourceFile sf = null;

      while ((sf = mainParserObject.nextSourceFile()) != null) {

        try {

          mainParserObject.preParse(sf);
          parse(sf, techPack, setType, setName);
          mainParserObject.postParse(sf);
        } catch (Exception e) {
          mainParserObject.errorParse(e, sf);
        } finally {
          mainParserObject.finallyParse(sf);
        }
      }
    } catch (Exception e) {
      // Exception catched at top level. No good.
      log.log(Level.WARNING, "Worker parser failed to exception", e);
      errorList.add(e);
    } finally {
      this.status = 3;
    }
  }

  /**
   * 
   */
  public void parse(final SourceFile sf, final String techPack, final String setType, final String setName)
  throws Exception {
    this.measFile = null;
    final long start = System.currentTimeMillis();
    this.sourceFile = sf;
    objectMask = sf.getProperty("x3GPPParser.vendorIDMask", ".+,(.+)=.+");
    matchObjectMaskAgainst = sf.getProperty("x3GPPParser.matchVendorIDMaskAgainst", "subset");
    readVendorIDFrom = sf.getProperty("3GPP32435Parser.readVendorIDFrom", null);
    if (null==readVendorIDFrom || readVendorIDFrom==""){
    	readVendorIDFrom = sf.getProperty("x3GPPParser.readVendorIDFrom", "data");
    }
    fillEmptyMoid = "true".equalsIgnoreCase(sf.getProperty("x3GPPParser.FillEmptyMOID", "true"));
    fillEmptyMoidStyle = sf.getProperty("x3GPPParser.FillEmptyMOIDStyle", "inc");
    fillEmptyMoidValue = sf.getProperty("x3GPPParser.FillEmptyMOIDValue", "0");
    
    SAXParserFactory spf = SAXParserFactory.newInstance();
    //spf.setValidating(validate);

    SAXParser parser = spf.newSAXParser();
    final XMLReader xmlReader = parser.getXMLReader();
    xmlReader.setContentHandler(this);
    xmlReader.setErrorHandler(this);

    xmlReader.setEntityResolver(new ENIQEntityResolver(log.getName()));
    final long middle = System.currentTimeMillis();
    xmlReader.parse(new InputSource(sourceFile.getFileInputStream()));
    final long end = System.currentTimeMillis();
    log.log(Level.FINER, "Data parsed. Parser initialization took "+ (middle-start)+ " ms, parsing "+(end-middle)+ " ms. Total: "+(end-start)+ " ms.");
    oldObjClass = null;
  }

  /**
   * 
   */
  public void parse(final FileInputStream fis) throws Exception {

    final long start = System.currentTimeMillis();
    SAXParserFactory spf = SAXParserFactory.newInstance();
    //spf.setValidating(validate);
    final SAXParser parser = spf.newSAXParser();
    final XMLReader xmlReader = parser.getXMLReader();
    xmlReader.setContentHandler(this);
    xmlReader.setErrorHandler(this);
    final long middle = System.currentTimeMillis();
    xmlReader.parse(new InputSource(fis));
    final long end = System.currentTimeMillis();
    log.log(Level.FINEST, "Data parsed. Parser initialization took "+ (middle-start)+ " ms, parsing "+(end-middle)+ " ms. Total: "+(end-start)+ " ms.");
  }

  public HashMap strToMap(final String str) {

    final HashMap hm = new HashMap();
    int index = 0;
    if (str != null) {

      // list all triggers
      final StringTokenizer triggerTokens = new StringTokenizer(((String) str), " ");
      while (triggerTokens.hasMoreTokens()) {
        hm.put(""+index,triggerTokens.nextToken());
      }
    }

    return hm;
  }
  
  private String printAttributes(Attributes atts) {
	  String line = "";
	  int i;
	  if(atts == null) {
		  return "";
	  }
	  for(i = 0; i < atts.getLength(); ++i) {
		  line += "[" + atts.getType(i) + "," + atts.getValue(i) + "]";		  
	  }
	  return line;
  }

  /**
   * Event handlers
   */
  public void startDocument() {
	//log.log(Level.FINEST, "Start of xml document");
  }

  public void endDocument() throws SAXException {
	//log.log(Level.FINEST, "End of xml document");
    // close last meas file
    if (measFile != null) {
      try {
        measFile.close();
      } catch (Exception e) {
        log.log(Level.FINEST, "Worker parser failed to exception", e);
        throw new SAXException("Error closing measurement file");
      }
    }
  }

  public void startElement(final String uri, final String name, final String qName, final Attributes atts)
  throws SAXException {

    charValue = "";
    //log.log(Level.FINEST, "startElement(" + uri + "," + name + "," + qName + "," + printAttributes(atts) + ")");

    if (qName.equals("fileHeader")) {
      this.fileFormatVersion = atts.getValue("fileFormatVersion");
      this.vendorName = atts.getValue("vendorName");
      this.dnPrefix = atts.getValue("dnPrefix");
    } else if (qName.equals("fileSender")) {
      this.fsLocalDN = atts.getValue("localDn");
      this.elementType = atts.getValue("elementType");
    } else if (qName.equals("measCollec")) {
      if (atts.getValue("beginTime") != null) {
        // header
        collectionBeginTime = atts.getValue("beginTime");
      } else if (atts.getValue("endTime") != null) {
        // footer
        //collectionEndTime = atts.getValue("endTime");
      }
    } else if (qName.equals("measData")) {
      measNameMap = new HashMap();
      clusterMap = new HashMap();
      
      origMeasNameMap = new HashMap<String, String>();
      
    } else if (qName.equals("managedElement")) {
      this.meLocalDN = atts.getValue("localDn");
      this.userLabel = atts.getValue("userLabel");
      this.swVersion = atts.getValue("swVersion");
    } else if (qName.equals("measInfo")) {
      this.measInfoId = atts.getValue("measInfoId");
    } else if (qName.equals("job")) {
      this.jobId = atts.getValue("jobId");
    } else if (qName.equals("granPeriod")) {
      granularityPeriodDuration = getSeconds(atts.getValue("duration"));
      granularityPeriodEndTime = atts.getValue("endTime");
    } else if (qName.equals("repPeriod")) {
      repPeriodDuration = getSeconds(atts.getValue("duration"));
    } else if (qName.equals("measTypes")) {
    } else if (qName.equals("measType")) {
      measIndex = atts.getValue("p");
      //log.log(Level.FINEST, "meastype p:"+measIndex);
    } else if (qName.equals("measValue")) {
      //this.measurement = new HashMap();
      this.suspectFlag = "";
      this.measObjLdn = atts.getValue("measObjLdn");
      handleTAGmoid(measObjLdn);
      try {
        if (sourceFile != null) {
          if (oldObjClass == null || !oldObjClass.equals(objectClass)) {
            // close meas file
            if (measFile != null) {
              measFile.close();
            }
            // create new measurementFile
            measFile = Main.createMeasurementFile(sourceFile, objectClass, techPack, setType, setName, workerName, log);
            oldObjClass = objectClass;
          }
        }
      } catch (Exception e) {
        log.log(Level.FINEST, "Error opening measurement data", e);
        e.printStackTrace();
        throw new SAXException("Error opening measurement data: " + e.getMessage(), e);
      }
    } else if (qName.equals("measResults")) {
    } else if (qName.equals("r")) {
      this.measValueIndex = atts.getValue("p");
      //log.log(Level.FINEST, "r p:"+measValueIndex);
    } else if (qName.equals("suspect")) {
    } else if (qName.equals("fileFooter")) {
    }
  }

  private void handleTAGmoid(String value) {
    //  TypeClassID is determined from the moid
    // of the first mv of the md

    this.objectClass = "";

    // where to read objectClass (moid)
    if ("file".equalsIgnoreCase(readVendorIDFrom)) {
      // read vendor id from file
      objectClass = parseFileName(sourceFile.getName(), objectMask);

    } else if ("data".equalsIgnoreCase(readVendorIDFrom) || "measObjLdn".equalsIgnoreCase(readVendorIDFrom)) {

      // if moid is empty and empty moids are filled.
      if (fillEmptyMoid && value.length() <= 0) {
        if (fillEmptyMoidStyle.equalsIgnoreCase("static")) {
          value = fillEmptyMoidValue;
        } else {
          value = measValueIndex + "";
        }
      }

      // read vendor id from data
      objectClass = parseFileName(value, objectMask);
      
    } else if ("measInfoId".equalsIgnoreCase(readVendorIDFrom)){
      objectClass = this.measInfoId;
    }
  }


  /**
   * Rips PT and S values off from the value.
   * 
   * @param value Contains the duration value
   * @return the duration in seconds
   */
  private String getSeconds(String value) {
    String result = null;
    if (value != null) {
       result = value.substring(2,value.indexOf('S'));
    }
    return result;
  }

  private String nameField = "";
  private String clusterField = "";

  private String extractCounterName(String counterName) {
	  int index1 = counterName.indexOf("#");
	  int index2 = counterName.indexOf(".", index1);
	  // System.out.print(counterName + " index1 = " + index1 + " index2 = " + index2 + " ");
	  if(index1 >= 0) {
		  if(index2 > index1) { // Format NAME#cluster.NAME -> NAME.NAME and Cluster
			  nameField = counterName.substring(0, index1) + counterName.substring(index2, counterName.length());
			  clusterField = counterName.substring(index1 + 1, index2);
		  }
		  else { // Format NAME#Cluster -> NAME and Cluster
			  nameField = counterName.substring(0, index1);
			  clusterField = counterName.substring(index1 + 1, counterName.length());
		  }
	  }
	  else { // Format NAME -> NAME
		  nameField = counterName;
		  clusterField = "";
	  }
	  return nameField;
  }

  public void endElement(final String uri, final String name, final String qName) throws SAXException {
    // log.log(Level.FINEST, "endElement(" + uri + "," + name + "," + qName + "," + charValue + ")");

    if (qName.equals("fileHeader")) {
    } else if (qName.equals("fileSender")) {
    } else if (qName.equals("measCollec")) {
    } else if (qName.equals("measData")) {
    } else if (qName.equals("managedElement")) {
    } else if (qName.equals("measInfo")) {
    } else if (qName.equals("job")) {
    } else if (qName.equals("granPeriod")) {
    } else if (qName.equals("repPeriod")) {
    } else if (qName.equals("measTypes")) {
      measNameMap = strToMap(charValue);
    } else if (qName.equals("measType")) {
    	
      measNameMap.put(measIndex,(Object) extractCounterName(charValue));
      clusterMap. put(measIndex,(Object) clusterField);
      
      origMeasNameMap.put(measIndex, charValue);
      
      
      // log.log(Level.FINEST, "ADD " + measIndex + ":" + nameField + " clusterId: " + clusterField);
    } else if (qName.equals("measValue")) {
      try {
        // change file when object class changes
        if (measFile == null) {
          System.err.println("Measurement file null");
          log.log(Level.FINEST, "PERIOD_DURATION: "+ granularityPeriodDuration);
          log.log(Level.FINEST, "repPeriodDuration: " + repPeriodDuration);
          //DATETIME_ID calculated from end time
          String begin = calculateBegintime();
          if (begin != null) {
            log.log(Level.FINEST, "DATETIME_ID: "+ begin);
          }
          log.log(Level.FINEST, "collectionBeginTime: "+ collectionBeginTime);
          log.log(Level.FINEST, "DC_SUSPECTFLAG: "+ suspectFlag);
          log.log(Level.FINEST, "filename: "+ (sourceFile == null ? "dummyfile" : sourceFile.getName()));
          log.log(Level.FINEST, "JVM_TIMEZONE: "+ JVM_TIMEZONE);
          log.log(Level.FINEST, "DIRNAME: "+ (sourceFile == null ? "dummydir" : sourceFile.getDir()));
          log.log(Level.FINEST, "measInfoId: "+ measInfoId);
          log.log(Level.FINEST, "MOID: "+ measObjLdn);
          log.log(Level.FINEST, "objectClass: "+ objectClass);
          log.log(Level.FINEST, "vendorName: "+vendorName);
          log.log(Level.FINEST, "fileFormatVersion: "+fileFormatVersion);
          log.log(Level.FINEST, "dnPrefix: "+ dnPrefix);
          log.log(Level.FINEST, "localDn: "+ fsLocalDN);
          log.log(Level.FINEST, "managedElementLocalDn: "+ meLocalDN);
          log.log(Level.FINEST, "elementType: "+elementType);
          log.log(Level.FINEST, "userLabel: "+userLabel);
          log.log(Level.FINEST, "swVersion: "+swVersion);
          //collectionEndTime received so late, that migth not be used
          log.log(Level.FINEST, "endTime: "+ granularityPeriodEndTime);
          log.log(Level.FINEST, "jobId: "+ jobId);
        } else {
          measFile.addData("PERIOD_DURATION", granularityPeriodDuration);
          log.log(Level.FINEST, "PERIOD_DURATION: "+ granularityPeriodDuration);
          measFile.addData("repPeriodDuration", repPeriodDuration);
          log.log(Level.FINEST, "repPeriodDuration: "+ repPeriodDuration);
          //DATETIME_ID calculated from end time
          String begin = calculateBegintime();
          if (begin != null) {
            measFile.addData("DATETIME_ID", begin);
            log.log(Level.FINEST, "DATETIME_ID: "+ begin);
          }
          measFile.addData("collectionBeginTime", collectionBeginTime);
          log.log(Level.FINEST, "collectionBeginTime: "+ collectionBeginTime);
          measFile.addData("DC_SUSPECTFLAG", suspectFlag);
          log.log(Level.FINEST, "DC_SUSPECTFLAG: "+ suspectFlag);
          measFile.addData("filename", (sourceFile == null ? "dummyfile" : sourceFile.getName()));
          log.log(Level.FINEST, "filename: "+ (sourceFile == null ? "dummyfile" : sourceFile.getName()));
          measFile.addData("JVM_TIMEZONE", JVM_TIMEZONE);
          log.log(Level.FINEST, "JVM_TIMEZONE: "+ JVM_TIMEZONE);
          measFile.addData("DIRNAME", (sourceFile == null ? "dummydir" : sourceFile.getDir()));
          log.log(Level.FINEST, "DIRNAME: "+ (sourceFile == null ? "dummydir" : sourceFile.getDir()));
          measFile.addData("measInfoId", measInfoId);
          log.log(Level.FINEST, "measInfoId: "+ measInfoId);
          measFile.addData("MOID", measObjLdn);
          log.log(Level.FINEST, "MOID: "+ measObjLdn);
          measFile.addData("objectClass", objectClass);
          log.log(Level.FINEST, "objectClass: "+ objectClass);
          measFile.addData("vendorName",vendorName);
          log.log(Level.FINEST, "vendorName: "+ vendorName);
          measFile.addData("fileFormatVersion",fileFormatVersion);
          log.log(Level.FINEST, "fileFormatVersion: "+ fileFormatVersion);
          measFile.addData("dnPrefix", dnPrefix);
          log.log(Level.FINEST, "dnPrefix: "+ dnPrefix);
          measFile.addData("localDn", fsLocalDN);
          log.log(Level.FINEST, "localDn: "+ fsLocalDN);
          measFile.addData("managedElementLocalDn", meLocalDN);
          log.log(Level.FINEST, "managedElementLocalDn: "+ meLocalDN);
          measFile.addData("elementType",elementType);
          log.log(Level.FINEST, "elementType: "+ elementType);
          measFile.addData("userLabel",userLabel);
          log.log(Level.FINEST, "userLabel: "+ userLabel);
          measFile.addData("swVersion",swVersion);
          log.log(Level.FINEST, "swVersion: "+ swVersion);
          //collectionEndTime received so late, that migth not be used
          measFile.addData("endTime", granularityPeriodEndTime);
          log.log(Level.FINEST, "endTime: "+ granularityPeriodEndTime);
          measFile.addData("jobId", jobId);
          log.log(Level.FINEST, "jobId: "+ jobId);
          
          measFile.saveData();
        }


      } catch (Exception e) {
        log.log(Level.FINEST, "Error saving measurement data", e);
        e.printStackTrace();
        throw new SAXException("Error saving measurement data: " + e.getMessage(), e);
      }
    } else if (qName.equals("measResults")) {
      final Map measValues = strToMap(charValue);
      if (measValues.keySet().size() == measNameMap.keySet().size()) {
        Iterator it = measValues.keySet().iterator();
        while (it.hasNext()) {
          String s = (String)it.next();
          String origValue = (String)measValues.get(s);
          if (origValue != null && origValue.equalsIgnoreCase("NIL")) {
            origValue = null;
          }
          if (measFile == null) {
            System.out.println((String)measNameMap.get(s)+ ": "+ origValue);
          } else {
            measFile.addData((String)measNameMap.get(s), origValue);
            log.log(Level.FINEST, (String)measNameMap.get(s)+": "+ origValue);
          }
        }
      } else {
        log.warning("Data contains one or more r-tags than mt-tags");
      }
    } else if (qName.equals("r")) {
      if (measNameMap.get(measValueIndex) != null) {
        String origValue = charValue;
        if (origValue != null && origValue.equalsIgnoreCase("NIL")) {
          origValue = null;
        }
        if (measFile == null) {
          System.out.println((String)measNameMap.get(measValueIndex)+ ": "+ origValue + " clusterId: " + (String)clusterMap.get(measValueIndex));
        } else {
          measFile.addData((String)measNameMap.get(measValueIndex), origValue);
          measFile.addData("clusterId", (String)clusterMap.get(measValueIndex));
          
          measFile.addData(origMeasNameMap.get(measValueIndex), origValue);
          
          log.log(Level.FINEST, (String)measNameMap.get(measValueIndex)+": "+ origValue);
          log.log(Level.FINEST, origMeasNameMap.get(measValueIndex)+": "+ origValue);
        } 
      } else {
        log.warning("Data contains one or more r-tags than mt-tags");
      }
    } else if (qName.equals("suspect")) {
      this.suspectFlag = charValue;
    } else if (qName.equals("fileFooter")) {
    }
  }

  private String calculateBegintime() {
    String result = null;
    try {
      String granPeriodETime = granularityPeriodEndTime;
      if (granPeriodETime.matches(".+\\+\\d\\d(:)\\d\\d") || granPeriodETime.matches(".+\\-\\d\\d(:)\\d\\d")) {
        granPeriodETime = granularityPeriodEndTime.substring(0,granularityPeriodEndTime.lastIndexOf(":")) 
        + granularityPeriodEndTime.substring(granularityPeriodEndTime.lastIndexOf(":") + 1);
      }
      granPeriodETime = granPeriodETime.replaceAll("[.]\\d{3}","");
      Date end = simpleDateFormat.parse(granPeriodETime);
      Calendar cal = Calendar.getInstance();
      cal.setTime(end);
      int period = Integer.parseInt(granularityPeriodDuration);
      cal.add(Calendar.SECOND, - period);
      result = simpleDateFormat.format(cal.getTime());
    } catch (ParseException e) {
      log.log(Level.WARNING, "Worker parser failed to exception", e);
    } catch (NumberFormatException e) {
      log.log(Level.WARNING, "Worker parser failed to exception", e);
    } catch (NullPointerException e) {
      log.log(Level.WARNING, "Worker parser failed to exception", e);
    }
    return result;
  }


  public static void main(String[] args) {
    int argnum = 0;
    Xml3GPP32435Parser np = null;
    FileInputStream fis = null;
    while (argnum < args.length) {
      if (args[argnum].equals("-sf")) {
        final String s = args[argnum+1];
        final File f = new File(s);
        try {
          fis = new FileInputStream(f);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      argnum++;
    }
    if (fis == null) {
      final File f = new File("C:\\tmp\\koetus.xml");
      try {
        fis = new FileInputStream(f);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    try {
      np = new Xml3GPP32435Parser();
      np.log = Logger.getLogger("etl.tp.st.sn.parser.NewParser.wn");
      
      /* Add logging handler
      np.log = Logger.getLogger("MyLog");
      FileHandler fh = new FileHandler("c:\\tmp\\log.txt", true);
      np.log.addHandler(fh);
      SimpleFormatter formatter = new SimpleFormatter();
      fh.setFormatter(formatter);
      np.log.setLevel(Level.ALL);
      np.log.log(Level.INFO, "Logger started");
      */

      np.parse(fis);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
    }
  }

  /**
   * Extracts a substring from given string based on given regExp
   * 
   */
  public String parseFileName(final String str, final String regExp) {

    final Pattern pattern = Pattern.compile(regExp);
    final Matcher matcher = pattern.matcher(str);
    String result = "";

    if(matchObjectMaskAgainst.equalsIgnoreCase("whole")){
    	//Find a match between regExp and whole of str, and return group 1 as result
	    if (matcher.matches()) {
		      result = matcher.group(1);
		      log.finest(" regExp (" + regExp + ") found from " + str + "  :" + result);
		} else {
		      log.warning("String " + str + " doesn't match defined regExp " + regExp);
		}
    } else {
    	//Find a match between regExp and return group 1 as result.
    	if (matcher.find()){
    		result = matcher.group(1);
    		log.finest(" regExp (" + regExp + ") found from subset of " + str + "  :" + result);
    	} else {
	      log.warning("No subset of String " + str + " matchs defined regExp " + regExp);
	    }
	}

    return result;

  }

  public void characters(char[] ch, int start, int length) throws SAXException {
    for (int i = start; i < start + length; i++) {
      // If no control char
      if (ch[i] != '\\' && ch[i] != '\n' && ch[i] != '\r' && ch[i] != '\t') {
        charValue += ch[i];
      }
    }
  }
}
