package com.distocraft.dc5000.etl.xml;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.distocraft.dc5000.etl.parser.Parser;
import com.distocraft.dc5000.etl.parser.SourceFile;

/**
 * Parser implementation to adapt generic wellformed XML data.<br>
 * <br>
 * Wellformed XML document is logically divided into three parts.<br>
 * Measurement part contains N measurement types which share the same datatime. Measurement type
 * contains N measurement columns which share same measurement type name. Measurement column is a
 * name-value pair.<br>
 * <br>
 * Different parts of data files are identified by configurable tags.<br>
 * <br>
 * <strong>Measurement (Header)</strong><br>
 * Attributes and leaf-elements found are stored and available for all measurement types. If header
 * is not defined all information is stored from beginning to start of measurement type.<br>
 * <strong>Measurement Type</strong><br>
 * Attributes and leaf elements found are stored and available for all measurement rows.<br>
 * <br>
 * <strong>Measurement Row</strong><br>
 * Attributes and leaf elements found are stored.<br>
 * <br>
 * Parameters:<br> - headerTag - name of tag indicating beginning of interpreted file. All
 * information before this tag is ignored. If this parameter is not defined all information is
 * stored. <br> - mtypeTag - name of tag inidicating beginning of a measurement type.<br> - rowTag -
 * name of tag indicating beginning of measurement row.<br> - vendorTagMode - Vendor tag behaviour
 * of this parser 0 = Vendor tag is specified in configuration using parameter vendorTag 1 = Vendor
 * tag is read from specified column (vendorTag is column name) 2 = Vendor tag is parsed from source
 * filename using RE pattern (pattern defined in parameter vendorTag)<br> - vendorTag - paramter
 * value for vendor tag resolving<br> - timeColumn - column name used to separate different time
 * periods from each other. If source file contains only one time period this paramter can be
 * omited.
 * <br><br>
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
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * </table>
 * <br><br> 
 * <table border="1" width="100%" cellpadding="3" cellspacing="0">
 * <tr bgcolor="#CCCCFF" class="TableHeasingColor">
 * <td colspan="2"><font size="+2"><b>Added DataColumns</b></font></td>
 * </tr>
 * <tr>
 * <td><b>Column name</b></td>
 * <td><b>Description</b></td>
 * </tr>
 * <tr>
 * <td>filename</td>
 * <td>contains the filename of the inputdatafile.</td>
 * </tr>
 * <tr>
 * <td>DC_SUSPECTFLAG</td>
 * <td>EMPTY</td>
 * </tr>
 * <tr>
 * <td>DIRNAME</td>
 * <td>Conatins full path to the inputdatafile.</td>
 * </tr>
 * <tr>
 * <td>JVM_TIMEZONE</td>
 * <td>contains the JVM timezone (example. +0200) </td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * </table>
 * <br><br> 
 * @author lemminkainen
 * 
 */
public class XMLParser extends DefaultHandler implements Parser {
  
  private static final String JVM_TIMEZONE = new SimpleDateFormat("Z").format(new Date());

  private static final int PREHEADER = 0;

  private static final int HEADER = 1;

  private static final int MTYPE = 2;

  private static final int DATA = 3;

  private static final int TAG_ID_FROM_CONFIG = 0;

  private static final int TAG_ID_FROM_COLUMN = 1;

  private static final int TAG_ID_FROM_FILENAME = 2;

  private Logger log;

  private SourceFile sourceFile;

  private UStack stack;

  private int parsemode = 0;

  private String headerTag;

  private String mtypeTag;

  private String rowTag;

  private int vendorTagMode;

  private String vendorTag;

  private String timeColumn;

  private HashMap mFiles;

  private Map headerMap;

  private Map mtypeMap;

  private Map rowMap;

  //***************** Worker stuff ****************************
  
  private String techPack;
  private String setType;
  private String setName;
  private int status = 0; 
  private Main mainParserObject = null;
  private static final String suspectFlag = "";
  private String workerName = "";

  
  public void init(final Main main, final String techPack, final String setType, final String setName, final String workerName){
    this.mainParserObject = main;
    this.techPack = techPack;
    this.setType = setType;
    this.setName = setName;
    this.status = 1;
    this.workerName = workerName;
    
    String logWorkerName = "";
    if (workerName.length() > 0) {
      logWorkerName = "."+workerName;
    }

    log = Logger.getLogger("etl." + techPack + "." + setType + "." + setName + ".parser.XML"+logWorkerName);

  }
  
  
  public int status(){
    return status;
  }
  
  public void run(){      
    
    try {
    
      this.status = 2;
      SourceFile sf = null;
         
      while((sf = mainParserObject.nextSourceFile())!=null){
        
        try{     
          mainParserObject.preParse(sf);
          parse(sf,  techPack,  setType,  setName);
          mainParserObject.postParse(sf);          
        } catch (Exception e){
          mainParserObject.errorParse(e,sf);
        } finally {
          mainParserObject.finallyParse(sf);          
        }        
      }     
    } catch (Exception e){  
      // Exception catched at top level. No good.
      log.log(Level.WARNING, "Worker parser failed to exception", e);
    } finally {     
      this.status = 3;
    }
  }
  
  //***************** Worker stuff ****************************
  
  
  public void parse(final SourceFile sf, final String techPack, final String setType, final String setName) throws Exception {
    this.techPack = techPack;
    this.setType = setType;
    this.setName = setName;
    
    this.sourceFile = sf;

    log.finest("Reading configuration...");

    headerTag = sf.getProperty("headerTag", null);
    if (headerTag == null) {
      parsemode = HEADER;
    }

    mtypeTag = sf.getProperty("mtypeTag");
    rowTag = sf.getProperty("rowTag");

    if (mtypeTag == null || rowTag == null) {
      log.fine("mtypeTag \"" + mtypeTag + "\" rowTag \"" + rowTag + "\"");
      throw new Exception("mtypeTag and dataTag parameters must be defined");
    }

    try {
      vendorTagMode = Integer.parseInt(sf.getProperty("vendorTagMode", null));
      vendorTag = sf.getProperty("vendorTag", null);

      if (vendorTagMode < 0 || vendorTagMode > 2 || vendorTag == null || vendorTag.length() <= 0){
        throw new Exception();
      }

    } catch (Exception e) {
      throw new Exception("VendorTag configuration is invalid. Check vendorTagMode and vendorTag");
    }

    timeColumn = sf.getProperty("timeColumn", null);

    mFiles = new HashMap(10);

    headerMap = new HashMap();
    mtypeMap = new HashMap();
    rowMap = new HashMap();
    stack = new UStack();
    

    if (vendorTagMode == TAG_ID_FROM_CONFIG) {
      headerMap.put("vendorTag", vendorTag);
    } else if (vendorTagMode == TAG_ID_FROM_FILENAME) {
      final Pattern pat = Pattern.compile(vendorTag);
      final Matcher m = pat.matcher(sf.getName());
      if (m.matches()){
        headerMap.put("vendorTag", m.group(1));
      } else {
        log.warning("SourceFile name does not match to vendorID pattern");
      }
    }

    log.finest("Creating SAXParser...");

    final XMLReader xmlReader = new org.apache.xerces.parsers.SAXParser();
    xmlReader.setContentHandler(this);
    xmlReader.setErrorHandler(this);

    log.fine("Staring to parse...");

    xmlReader.parse(new InputSource(sf.getFileInputStream()));

    log.fine("Parse finished.");
  }

  /**
   * Event handlers
   */
  public void startDocument() {
    log.finest("Start document");
  }

  public void endDocument() throws SAXException {
    log.finest("End document");
    
    final Iterator i = mFiles.entrySet().iterator();
    
    while(i.hasNext()) {
      try {
      final MeasurementFile mf = (MeasurementFile)i.next();
      mf.close();
      } catch(Exception e) {
        log.log(Level.FINE,"Error closing a measurement file",e);
      }
    }
    
    headerMap.clear();
    mtypeMap.clear();
    rowMap.clear();
    sourceFile = null;
    
    
  }

  public void startElement(final String uri, final String name, final String qName, final Attributes atts) throws SAXException {
    stack.push(qName);
    //System.out.println(uri+":"+name+":"+qName);
    if (headerTag != null && qName.equals(headerTag)) {
      parsemode = HEADER;
    } else if (qName.equals(mtypeTag)) {
      parsemode = MTYPE;
    } else if (qName.equals(rowTag)) {
      parsemode = DATA;
    }

    if (parsemode == PREHEADER) {
      return;
    } else if (parsemode == HEADER) {
      if (atts != null) {
        final String address = stack.getStack();
        for (int i = 0; i < atts.getLength(); i++) {
          headerMap.put(address + "/@" + atts.getLocalName(i), atts.getValue(i));
        }
      }
    } else if (parsemode == MTYPE) {
      if (atts != null) {
        final String address = stack.getStack();
        for (int i = 0; i < atts.getLength(); i++) {
          mtypeMap.put(address + "/@" + atts.getLocalName(i), atts.getValue(i));
        }
      }
    } else if (parsemode == DATA) {
      if (atts != null) {
        final String address = stack.getStack();
        for (int i = 0; i < atts.getLength(); i++) {
          rowMap.put(address + "/@" + atts.getLocalName(i), atts.getValue(i));
        }
      }
    }

  }

  public void endElement(final String uri, final String name, final String qName) throws SAXException {
    final String last = stack.pop();

    if (!last.equals(qName)) {
      log.info("XML structure violation occured. End tag: \"" + qName + "\", expected \"" + last + "\"");
    }

    if (qName.equals(mtypeTag)) { // End of measurement type
      parsemode = HEADER;
    } else if (qName.equals(rowTag)) { // End of measurement row
      parsemode = MTYPE;

      rowMap.putAll(headerMap);
      rowMap.putAll(mtypeMap);

      try {

        final MeasurementFile mf = getMFile();
        
        mf.addData("Filename", sourceFile.getName());     
        mf.addData("JVM_TIMEZONE",JVM_TIMEZONE);
        mf.addData("DC_SUSPECTFLAG", suspectFlag);
        mf.addData("DIRNAME", sourceFile.getDir());             


        if (mf != null) {
          mf.addData(rowMap);
          mf.saveData();
        }

      } catch (Exception e) {
        log.log(Level.FINE, "Error in writing measurement file", e);
      }

      rowMap.clear();

    }
  }

  public void characters(final char ch[], final int start, final int length) {

    final StringBuffer chars = new StringBuffer();

    for (int i = start; i < start + length; i++) {
      // If no control char
      if (ch[i] != '\\' && ch[i] != '\n' && ch[i] != '\r' && ch[i] != '\t') {
        chars.append(ch[i]);
      }
    }

    final String charValue = chars.toString().trim();

    if (charValue.length() > 0) { // Found Tag with content

      if (parsemode == PREHEADER) {
        return;
      } else if (parsemode == HEADER) {
        headerMap.put(stack.getStack(), charValue);
      } else if (parsemode == MTYPE) {
        mtypeMap.put(stack.getStack(), charValue);
      } else if (parsemode == DATA) {
        rowMap.put(stack.getStack(), charValue);
      }

    }

  }

  private MeasurementFile getMFile() {

    try {

      String tag = null;
      if (vendorTagMode == TAG_ID_FROM_COLUMN){
        tag = (String) rowMap.get(vendorTag);
      } else {
        tag = (String) rowMap.get("vendorTag");
      }

      if (tag == null) {
        log.fine("A row without vendorTag found.");
        return null;
      }

      String time = "";
      if (timeColumn != null) {
        time = (String) rowMap.get(timeColumn);
      }

      MeasurementFile msf = (MeasurementFile) mFiles.get(tag + "_" + time);

      if (msf == null) {
        msf = Main.createMeasurementFile(sourceFile, tag, techPack, setType, setName,this.workerName, log);
        mFiles.put(tag + "_" + time, msf);
      }

      return msf;

    } catch (Exception e) {
      log.log(Level.WARNING, "MeasurementFile failed", e);
      return null;
    }
  }
}
