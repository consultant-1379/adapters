/*
 * Created on 20.1.2005
 *
 */
package com.distocraft.dc5000.etl.MDC;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
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
import com.distocraft.dc5000.etl.parser.MemoryRestrictedParser;
import com.distocraft.dc5000.etl.parser.SourceFile;
import com.distocraft.dc5000.repository.cache.DFormat;
import com.distocraft.dc5000.repository.cache.DItem;
import com.distocraft.dc5000.repository.cache.DataFormatCache;
import com.ericsson.eniq.common.ENIQEntityResolver;

/**
 * 
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
 * <td>VendorID from</td>
 * <td>MDCParser.readVendorIDFrom</td>
 * <td>Defines where the vendorID is retrieved from <b>data</b> (moid-tag) or
 * from <b>filename</b>. RegExp is used to further define the actual vendorID.
 * Vendor id is added to the outputdata as objectClass. See. VendorID Mask and
 * objectClass</td>
 * <td>data</td>
 * </tr>
 * <tr>
 * <td>VendorID Mask</td>
 * <td>MDCParser.vendorIDMask</td>
 * <td>Defines the RegExp mask that is used to extract the vendorID from either
 * data or filename. See. VendorID from</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>DTD File</td>
 * <td>MDCParser.dtdfile</td>
 * <td>Defines the DTD-file used when reading the XML inputfile.</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>Use Vector Data</td>
 * <td>MDCParser.UseVector</td>
 * <td>Is vector style data expanded to new rows or stored as it is.</td>
 * <td>false</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>MDCParser.UseMTS</td>
 * <td>Is MTS-tag added to MOID when storing datarows to the datamap.</td>
 * <td>true</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>MDCParser.FillEmptyMOID</td>
 * <td>Are empty moids replaced with running number.</td>
 * <td>true</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>MDCParser.RangeColumnName</td>
 * <td>Name of the added vector (range) index column to the outputfile. This
 * column contains the index of the expanded datarows.</td>
 * <td>DCVECTOR_INDEX</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>MDCParser.RangePostfix</td>
 * <td>Postfix that is added to the expanded (added) datarows.</td>
 * <td>_DCVECTOR</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>MDCParser.HashData</td>
 * <td>If true parser reads the data (entire file) in to memory  and then writes it to the file.<br> If false parser reads a chunk of data and writes the result in the file directly, no hashing.</td>
 * <td>false</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>MDCParser.RangeCounterTag</td>
 * <td>String that defines the vector style datacolumns in metadata (in
 * ProcessInstruction column). Datacolumns marked with this string are expectod
 * to contain vector styler data (comma delimited values) that are expanded
 * (each vectored value is moved to its own datarow) to output. See Use Vector
 * Data and KeyColumnTag</td>
 * <td>VECTOR</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>MDCParser.KeyColumnTag</td>
 * <td>String that defines the key datacolumns in metadata (in
 * ProcessInstruction column). Key columns are added to every expanded datarow.
 * See. Use Vector Data and RangeCounterTag</td>
 * <td>KEY</td>
 * </tr>
 * <tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>MDCParser.fillEmptyMoidStyle</td>
 * <td>If MDCParser.FillEmptyMOID is true this parameter tells what is done
 * with the value defined in MDCParser.fillEmptyMoidValue. parametar can get
 * values 'inc','dec' and 'static'. inc Increases the value (if numeric) by one.
 * dec decreases the value (if numeric) by one. if value is non numeric value is
 * added as it is. static inserts the value as it is in the moid tag. See
 * MDCParser.fillEmptyMoidValue and MDCParser.fillEmptyMoid.</td>
 * <td>inc</td>
 * </tr>
 * <tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>MDCParser.fillEmptyMoidValue</td>
 * <td>Defines the value that is used when filling empty moids. See
 * MDCParser.fillEmptyMoidStyle and MDCParser.fillEmptyMoid.</td>
 * <td>0</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
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
 * <td>filename</td>
 * <td> contains the filename of the inputdatafile.</td>
 * </tr>
 * <tr>
 * <td>SN</td>
 * <td>contains the data from SN tag.</td>
 * </tr>
 * <tr>
 * <td>MOID</td>
 * <td>contains the data from MOID tag.</td>
 * </tr>
 * <tr>
 * <td>PERIOD_DURATION</td>
 * <td>contains the data from GP tag.</td>
 * </tr>
 * <tr>
 * <td>DATETIME_ID</td>
 * <td>contains the data from CBT tag.</td>
 * </tr>
 * <tr>
 * <td>objectClass</td>
 * <td>contains the same data as in vendorID (see. readVendorIDFrom)</td>
 * </tr>
 * <tr>
 * <td>MTS</td>
 * <td>contains the MTS -tag.</td>
 * </tr>
 * <tr>
 * <td>neun</td>
 * <td>contains the neun -tag, network element user name.</td>
 * </tr>
 * <tr>
 * <td>nedn</td>
 * <td>contains the nedn -tag, network element distinguished name.</td>
 * </tr>
 * <tr>
 * <td>st</td>
 * <td>contains the st -tag.</td>
 * </tr>
 * <tr>
 * <td>vn</td>
 * <td>contains the vn -tag.</td>
 * </tr>
 * <tr>
 * <td>nesw</td>
 * <td>contains the nesw -tag, network element software version.</td>
 * </tr>
 * <tr>
 * <td>DC_SUSPECTFLAG</td>
 * <td>contains the sf -tag.</td>
 * </tr>
 * <tr>
 * <td>DIRNAME</td>
 * <td>Conatins full path to the inputdatafile.</td>
 * </tr>
 * <tr>
 * <td>JVM_TIMEZONE</td>
 * <td>contains the JVM timezone (example. +0200) </td>
 * </tr>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * </table> <br>
 * <br>
 * 
 * @author savinen <br>
 *         <br>
 * 
 */
public class MDCParser extends DefaultHandler implements MemoryRestrictedParser {

  // Virtual machine timezone unlikely changes during execution of JVM
  private static final String JVM_TIMEZONE = (new SimpleDateFormat("Z")).format(new Date());

  private String charValue;

  private String senderName;

  private String granularityPeriod;

  private String collectionBeginTime;

  private ArrayList measNameList;

  private int measIndex;

  private int measValueIndex;

  private String mts = "";

  private String st = "";

  private String vn = "";

  private String neun = "";

  private String nedn = "";

  private String nesw = "";

  private String vectorPostfix = null;
  


  private String uniqueVectorZeroIndex = null;

  private String uniqueVectorIndex = null;

  private boolean createOwnVectorFile = true;

  private ArrayList uniqueVectorCounterList=null;

  private SourceFile sourceFile;

  private Map measurement;

  private Map measurementMap;

  protected String fileBaseName;
  
  private boolean hashData = false;
  private String oldObjClass;
  private MeasurementFile measFile = null;
  private MeasurementFile vectorMeasFile = null;
  private Map uniqueVectorMeasFileMap = null;
  
  private Logger log;

  private String techPack;

  private String setType;

  private String setName;

  private String objectMask;

  private String readVendorIDFrom;

  private boolean UseMTS = true;

  private boolean fillEmptyMoid = true;

  private String fillEmptyMoidStyle = "";

  private String fillEmptyMoidValue = "";

  private final static int MOID = 1;

  private final static int OBJECTCLASS = 0;

  // RBS related
  private final static String ISRANGEDATA = "___RANGE___";

  private String rangeColunName = ""; // name of the range column

  private String rangePostfix = ""; // string that is added to the

  // rangeCounters

  private String rangeCounterTag;

  private String uniqueVectorTag;
  
  private String cmVectorTag;
  
  private String compressedVectorTag;
  private Set compressedVectorCounters;

  private String vectorColumn;

  private String keyColumnTag;

  private boolean rbs = false;

  private boolean removeRanged = false;

  // Latest SGSN parameters

  private String sgsnEmptyMOIDVendorID;
  
  private boolean forceVendorPatterns;

  private Map sgsnVendorIDFormulas;

  private String interfacename;

  final static private String delimiter = ",";

  private int status = 0;

  private Main mainParserObject = null;

  private String suspectFlag = "";

  private String workerName = "";
  
  private int rangedCounters = 0;

  private int normalCounters = 0;

  private int memoryConsumptionMB = 0;
  
  private String counterValueRange = null;
  
  final private List errorList = new ArrayList();

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

    this.memoryConsumptionMB = mainParserObject.getNextSFMemConsumptionMB();
    
    log = Logger.getLogger("etl." + techPack + "." + setType + "." + setName + ".parser.MDC" + logWorkerName);
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
          this.memoryConsumptionMB = sf.getMemoryConsumptionMB();

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
      this.memoryConsumptionMB = 0;
      this.status = 3;
    }
  }

  /**
   * 
   */
  public void parse(final SourceFile sf, final String techPack, final String setType, final String setName)
      throws Exception {

    this.sourceFile = sf;

    measurementMap = new HashMap();
    uniqueVectorMeasFileMap = new HashMap();
    
    final long before = System.currentTimeMillis();

    final XMLReader xmlReader = new org.apache.xerces.parsers.SAXParser();
    xmlReader.setContentHandler(this);
    xmlReader.setErrorHandler(this);

    if (sf.getProperty("MDCParser.InputBufferSize", null) != null) {
      xmlReader.setProperty("http://apache.org/xml/properties/input-buffer-size", new Integer(sf
          .getProperty("MDCParser.InputBufferSize")));
    }

    objectMask = sf.getProperty("MDCParser.vendorIDMask", ".+,(.+)=.+");
    readVendorIDFrom = sf.getProperty("MDCParser.readVendorIDFrom", "data");
    UseMTS = "true".equalsIgnoreCase(sf.getProperty("MDCParser.UseMTS", "true"));
    fillEmptyMoid = "true".equalsIgnoreCase(sf.getProperty("MDCParser.FillEmptyMOID", "true"));
    fillEmptyMoidStyle = sf.getProperty("MDCParser.FillEmptyMOIDStyle", "inc");
    fillEmptyMoidValue = sf.getProperty("MDCParser.FillEmptyMOIDValue", "0");
    hashData = "true".equalsIgnoreCase(sf.getProperty("MDCParser.HashData", "false"));
    rbs = "true".equalsIgnoreCase(sf.getProperty("MDCParser.UseVector", "false"));
    rangePostfix = sf.getProperty("MDCParser.RangePostfix", "_DCVECTOR");
    rangeColunName = sf.getProperty("MDCParser.RangeColumnName", "DCVECTOR_INDEX");

    rangeCounterTag = sf.getProperty("MDCParser.RangeCounterTag", "VECTOR");

    uniqueVectorTag = sf.getProperty("MDCParser.UniqueVectorTag", "UNIQUEVECTOR");
      
    cmVectorTag = sf.getProperty("MDCParser.cmVectorTag", "CMVECTOR");
    
    compressedVectorTag = sf.getProperty("MDCParser.compressedVectorTag", "COMPRESSEDVECTOR");
    compressedVectorCounters = new HashSet();

    uniqueVectorZeroIndex = sf.getProperty("MDCParser.uniqueVectorZeroIndexName", "INDEXZERO");

    uniqueVectorIndex = sf.getProperty("MDCParser.uniqueVectorIndexName", "UNIQUEVECTOR_INDEX");

    vectorColumn = sf.getProperty("MDCParser.vectorColumn", "vectorColumn");

    keyColumnTag = sf.getProperty("MDCParser.KeyColumnTag", "KEY");

    createOwnVectorFile = "true".equalsIgnoreCase(sf.getProperty("MDCParser.createOwnVectorFile", "false"));

    final String uniqueVectorCounterListStr = sf.getProperty("MDCParser.uniqueVectorCounterList", "pmRes");
    //
    //below line is temporary unless common module jar is embeded
    counterValueRange = "19";
   
    final String[] tmp = uniqueVectorCounterListStr.split(",");

    uniqueVectorCounterList = new ArrayList();
    
    for (int i = 0; i < tmp.length; i++) {
      uniqueVectorCounterList.add(tmp[i]);
    }

    vectorPostfix = sf.getProperty("MDCParser.VectorPostfix", "_V");

    removeRanged = "true".equalsIgnoreCase(sf.getProperty("MDCParser.RemoveRangeData", "true"));

    interfacename = sf.getProperty("interfaceName", "");

    sgsnEmptyMOIDVendorID = sf.getProperty("MDCParser.emptyMOIDVendorID", "");

    forceVendorPatterns = "TRUE".equalsIgnoreCase(sf.getProperty("MDCParser.forceVendorPatterns", "false"));
    
    sgsnVendorIDFormulas = new HashMap();
    
    //Get the list of vendor IDs and 
    final String vpats = sf.getProperty("MDCParser.VendorPatterns", null);
    if (vpats != null) {
      final String[] ids = vpats.split(",");
      for (int i = 0; i < ids.length; i++) {
        final String form = sf.getProperty("MDCParser.VendorIDPattern." + ids[i]);
        sgsnVendorIDFormulas.put(ids[i], form);
      }
      log.finest("Configured " + sgsnVendorIDFormulas.size() + " vendorID formulas");
    }

    xmlReader.setEntityResolver(new ENIQEntityResolver(log.getName()));

    log.finest("Initializations before parse took " + (System.currentTimeMillis() - before) + " ms");

    xmlReader.parse(new InputSource(sf.getFileInputStream()));

    // close old meas file
    if (measFile != null) {
      measFile.close();
    }
    
    // close old meas file
    if (vectorMeasFile != null) {
      vectorMeasFile.close();
    }
    
    oldObjClass = null;
    
    final Iterator iter = uniqueVectorMeasFileMap.keySet().iterator();
    while (iter.hasNext()) {
      final String uKey = (String) iter.next();
      if (uniqueVectorMeasFileMap.get(uKey)!=null){
        ((MeasurementFile)uniqueVectorMeasFileMap.get(uKey)).close();
      }
    }    
  }

  public List strToList(final String str) {

    final ArrayList list = new ArrayList();

    if (str != null) {

      // list all triggers
      final StringTokenizer triggerTokens = new StringTokenizer(((String) str), ",");
      while (triggerTokens.hasMoreTokens()) {
        list.add(triggerTokens.nextToken());
      }
    }

    return list;
  }

  /**
   * Event handlers
   */
  public void startDocument() {

  }

  public void endDocument() throws SAXException {

  }

  public void startElement(final String uri, final String name, final String qName, final Attributes atts)
      throws SAXException {

    charValue = "";

    if (qName.equals("mi")) { // measInfo

      measNameList = new ArrayList();

      try {

        if (!fillEmptyMoidStyle.equalsIgnoreCase("static")) {
          measValueIndex = Integer.parseInt(fillEmptyMoidValue);
        }
      } catch (Exception e) {
        // if we cannot convert the fillEmptyMoidValue to integer insert
        // it in the moid as it is.
        fillEmptyMoidStyle = "static";
      }

    } else if (qName.equals("mv")) { // measValues

      measIndex = 0; // Index of measInfo value
      rangedCounters = 0;
      normalCounters = 0;

      if (fillEmptyMoidStyle.equalsIgnoreCase("inc")) {
        measValueIndex++;
      }

      if (fillEmptyMoidStyle.equalsIgnoreCase("dec")) {
        measValueIndex--;
      }

      this.suspectFlag = "";

    } else if (qName.equals("md")) { // measData

    }
  }

  private Map getUniqueVectorsFromProcessInstructions(final String interfacename, final String objectClass,
      final String prefix, final String key) {

    final HashMap result = new HashMap();

    try {

      final DataFormatCache dfc = DataFormatCache.getCache();
      final DFormat df = dfc.getFormatWithTagID(interfacename, objectClass);

      if (df == null) {
        return result;
      }
      final List dItemList = df.getDitems();

      final Iterator iter = dItemList.iterator();
      while (iter.hasNext()) {

        final DItem di = (DItem) iter.next();
        if (di.getProcessInstruction() != null) {
          final StringTokenizer token = new StringTokenizer(di.getProcessInstruction(), ",");
          while (token.hasMoreElements()) {
            final String t = (String) token.nextElement();

            if (t.startsWith(prefix)) {

              if (!result.containsKey(key)) {
                result.put(key, new ArrayList());
              }
              ((ArrayList) result.get(key)).add(di.getDataID());
            }
          }
        }
      }

    } catch (Exception e) {
      log.warning("Error while retrieving UniqueVectors from ProcessInstructions ");
    }

    return result;

  }

  private Set getDataIDFromProcessInstructions(final String interfacename, final String objectClass, final String key) {

    final Set result = new HashSet();

    try {

      final DataFormatCache dfc = DataFormatCache.getCache();
      final DFormat df = dfc.getFormatWithTagID(interfacename, objectClass);

      if (df == null) {
        return result;
      }
      final List dItemList = df.getDitems();

      final Iterator iter = dItemList.iterator();
      while (iter.hasNext()) {

        final DItem di = (DItem) iter.next();
        if (di.getProcessInstruction() != null) {
          final StringTokenizer token = new StringTokenizer(di.getProcessInstruction(), ",");
          while (token.hasMoreElements()) {
            final String t = (String) token.nextElement();

            if (t.equalsIgnoreCase(key)) {
              result.add(di.getDataID());
            }
          }
        }
      }

    } catch (Exception e) {
      log.warning("Error while retrieving DataIDs from ProcessInstructions");

    }

    return result;

  }
  
  private void handleTAGmoidNoHash() throws SAXException{

    try {
    
    // TypeClassID is determined from the moid
    // of the first mv of the md

    HashMap uniqueVectorMeasFileMap = new HashMap();
    
    String objectClass = "";

    // where to read objectClass (moid)
    if ("file".equalsIgnoreCase(readVendorIDFrom)) {

      // if moid is empty and empty moids are filled.
      if (fillEmptyMoid && charValue.length() <= 0) {
        if (fillEmptyMoidStyle.equalsIgnoreCase("static")) {
          charValue = fillEmptyMoidValue;
        } else {
          charValue = measValueIndex + "";
        }
      }

      // read vendor id from file
      objectClass = parseFileName(sourceFile.getName(), objectMask);

    } else if ("data".equalsIgnoreCase(readVendorIDFrom)) {

      // if moid is empty and empty moids are filled.
      if (fillEmptyMoid && charValue.length() <= 0) {
        if (fillEmptyMoidStyle.equalsIgnoreCase("static")) {
          charValue = fillEmptyMoidValue;
        } else {
          charValue = measValueIndex + "";
        }
      }

      // read vendor id from data
      objectClass = parseFileName(charValue, objectMask);

    } else if ("sgsn".equalsIgnoreCase(readVendorIDFrom)) {

      if (charValue.length() <= 0) {
        objectClass = sgsnEmptyMOIDVendorID;
      }

      // lets do this if charValue contains something or forceVendorPatterns is true..
      if (forceVendorPatterns || charValue.length() > 0){
        
    	//Get the node version from file name
        final String sgsnRevisionID = parseFileName(sourceFile.getName(), objectMask);

        //Find out what version we are to treat it as.
        String formula = (String) sgsnVendorIDFormulas.get(sgsnRevisionID);

        if (formula == null) {
          formula = (String) sgsnVendorIDFormulas.get("default");
          log.fine("No VendorID Pattern defined for \"" + sgsnRevisionID + "\". Going to use default: " + formula);
          if (formula == null) {
        	  throw new SAXException("No VendorID Pattern defined for \"" + sgsnRevisionID + "\"");
          }
        }

        for (int i = 0; i < 5 && i < measNameList.size(); i++) {
          if (formula.indexOf("c" + i) >= 0) {
            formula = formula.replaceAll("c" + i, (String) measNameList.get(i));
          }
        }

        objectClass = formula;
      }

    } else {
      log.warning("Value of parameter VendorID From \"" + readVendorIDFrom + "\" is not valid");
      throw new SAXException("readVendorIDFrom property" + readVendorIDFrom + " is not defined or not valid");
    }

    
    // UNIQUEVECTOR counters
    final Map uniqueVectorCounters = new HashMap();

    // we search unique vector counters also from objectClass+vectorPostfix
    if (createOwnVectorFile) {

      final Iterator iter = uniqueVectorCounterList.iterator();

      while (iter.hasNext()) {

        final String key = (String) iter.next();

        uniqueVectorCounters.putAll(getUniqueVectorsFromProcessInstructions(interfacename, objectClass + vectorPostfix
            + key, uniqueVectorTag, key));
      }
    }

    // VECTOR counters
    final Set rangeCounters = getDataIDFromProcessInstructions(interfacename, objectClass, rangeCounterTag);

    // CMVECTOR counters
    final Set cmVectorCounters = getDataIDFromProcessInstructions(interfacename, objectClass, cmVectorTag);

    // we search instructions also from objectClass+vectorPostfix
    if (createOwnVectorFile) {
      rangeCounters
          .addAll(getDataIDFromProcessInstructions(interfacename, objectClass + vectorPostfix, rangeCounterTag));
      
      cmVectorCounters
      .addAll(getDataIDFromProcessInstructions(interfacename, objectClass + vectorPostfix, cmVectorTag));
    }
    final Set keyColumns = getDataIDFromProcessInstructions(interfacename, objectClass, keyColumnTag);

    // we search instructions also from objectClass+vectorPostfix
    if (createOwnVectorFile) {
      keyColumns.addAll(getDataIDFromProcessInstructions(interfacename, objectClass + vectorPostfix, keyColumnTag));
    }
    
    // new measurement started
    measurement = new HashMap();
    
    measurement.put("SN", senderName);
    measurement.put("MOID", charValue);
    measurement.put("MTS", mts);
    measurement.put("nesw", nesw);
    measurement.put("nedn", nedn);
    measurement.put("neun", neun);
    measurement.put("st", st);
    measurement.put("vn", vn);
    measurement.put("PERIOD_DURATION", granularityPeriod);
    measurement.put("DATETIME_ID", collectionBeginTime);
    measurement.put("objectClass", objectClass);
    measurement.put("filename", sourceFile.getName());
    measurement.put("rangeCounters", rangeCounters);
    measurement.put("cmVectorCounters", cmVectorCounters);
    measurement.put("uniqueCounters", uniqueVectorCounters);
    measurement.put("keyColumns", keyColumns);
    measurement.put("JVM_TIMEZONE", JVM_TIMEZONE);
    measurement.put("DC_SUSPECTFLAG", suspectFlag);
    measurement.put("DIRNAME", sourceFile.getDir());

    
    
   } catch (Exception e){
     log.log(Level.WARNING, "Error closing measurement file", e);
     throw new SAXException("Error closing measurement file: " + e.getMessage(), e);
   }
    
  }


  
  /**
   * @throws SAXException
   */
  private void handleTAGmoid() throws SAXException {

    // TypeClassID is determined from the moid
    // of the first mv of the md

    String objectClass = "";

    // where to read objectClass (moid)
    if ("file".equalsIgnoreCase(readVendorIDFrom)) {

      // if moid is empty and empty moids are filled.
      if (fillEmptyMoid && charValue.length() <= 0) {
        if (fillEmptyMoidStyle.equalsIgnoreCase("static")) {
          charValue = fillEmptyMoidValue;
        } else {
          charValue = measValueIndex + "";
        }
      }

      // read vendor id from file
      objectClass = parseFileName(sourceFile.getName(), objectMask);

    } else if ("data".equalsIgnoreCase(readVendorIDFrom)) {

      // if moid is empty and empty moids are filled.
      if (fillEmptyMoid && charValue.length() <= 0) {
        if (fillEmptyMoidStyle.equalsIgnoreCase("static")) {
          charValue = fillEmptyMoidValue;
        } else {
          charValue = measValueIndex + "";
        }
      }

      // read vendor id from data
      objectClass = parseFileName(charValue, objectMask);

    } else if ("sgsn".equalsIgnoreCase(readVendorIDFrom)) {

      if (charValue.length() <= 0) {
        objectClass = sgsnEmptyMOIDVendorID;
      } else {
    	//Get the node version from file name
        final String sgsnRevisionID = parseFileName(sourceFile.getName(), objectMask);
        
        //Find out what version we are to treat it as.
        String formula = (String) sgsnVendorIDFormulas.get(sgsnRevisionID);

        if (formula == null) {
            formula = (String) sgsnVendorIDFormulas.get("default");
            log.fine("No VendorID Pattern defined for \"" + sgsnRevisionID + "\". Going to use default: " + formula);
            if (formula == null) {
          	  throw new SAXException("No VendorID Pattern defined for \"" + sgsnRevisionID + "\"");
            }
        }

        for (int i = 0; i < 5 && i < measNameList.size(); i++) {
          if (formula.indexOf("c" + i) >= 0) {
            formula = formula.replaceAll("c" + i, (String) measNameList.get(i));
          }
        }

        objectClass = formula;
      }

    } else {
      log.warning("Value of parameter VendorID From \"" + readVendorIDFrom + "\" is not valid");
      throw new SAXException("readVendorIDFrom property" + readVendorIDFrom + " is not defined or not valid");
    }

    // UNIQUEVECTOR counters
    final Map uniqueVectorCounters = new HashMap();

    // we search unique vector counters also from objectClass+vectorPostfix
    if (createOwnVectorFile) {

      final Iterator iter = uniqueVectorCounterList.iterator();

      while (iter.hasNext()) {

        final String key = (String) iter.next();

        uniqueVectorCounters.putAll(getUniqueVectorsFromProcessInstructions(interfacename, objectClass + vectorPostfix
            + key, uniqueVectorTag, key));
      }
    }

    // VECTOR counters
    final Set rangeCounters = getDataIDFromProcessInstructions(interfacename, objectClass, rangeCounterTag);
    
    // COMPRESSEDVECTOR counters
    compressedVectorCounters.addAll(getDataIDFromProcessInstructions(interfacename, objectClass, compressedVectorTag));
    rangeCounters.addAll(compressedVectorCounters);
    
    // CMVECTOR counters
    final Set cmVectorCounters = getDataIDFromProcessInstructions(interfacename, objectClass, cmVectorTag);

    // we search instructions also from objectClass+vectorPostfix
    if (createOwnVectorFile) {
      rangeCounters
          .addAll(getDataIDFromProcessInstructions(interfacename, objectClass + vectorPostfix, rangeCounterTag));
   
      compressedVectorCounters.addAll(getDataIDFromProcessInstructions(interfacename, objectClass + vectorPostfix, compressedVectorTag));
      rangeCounters.addAll(compressedVectorCounters);

      cmVectorCounters
      .addAll(getDataIDFromProcessInstructions(interfacename, objectClass + vectorPostfix, cmVectorTag));
    }
    final Set keyColumns = getDataIDFromProcessInstructions(interfacename, objectClass, keyColumnTag);

    // we search instructions also from objectClass+vectorPostfix
    if (createOwnVectorFile) {
      keyColumns.addAll(getDataIDFromProcessInstructions(interfacename, objectClass + vectorPostfix, keyColumnTag));
    }
    // check if map contains a moid (charValue)
    if (measurementMap != null && measurementMap.containsKey(charValue + mts)) {

      // measurement is already in map
      measurement = (Map) measurementMap.get(charValue + mts);

    } else {

      // new measurement
      measurement = new HashMap();
    }

    measurement.put("SN", senderName);
    measurement.put("MOID", charValue);
    measurement.put("MTS", mts);
    measurement.put("nesw", nesw);
    measurement.put("nedn", nedn);
    measurement.put("neun", neun);
    measurement.put("st", st);
    measurement.put("vn", vn);
    measurement.put("PERIOD_DURATION", granularityPeriod);
    measurement.put("DATETIME_ID", collectionBeginTime);
    measurement.put("objectClass", objectClass);
    measurement.put("filename", sourceFile.getName());
    measurement.put("rangeCounters", rangeCounters);
    measurement.put("cmVectorCounters", cmVectorCounters);
    measurement.put("uniqueCounters", uniqueVectorCounters);
    measurement.put("keyColumns", keyColumns);
    measurement.put("JVM_TIMEZONE", JVM_TIMEZONE);
    measurement.put("DC_SUSPECTFLAG", suspectFlag);
    measurement.put("DIRNAME", sourceFile.getDir());
  }

  final private Map getKeyCounters(final Map datarow) {

    final Set keyColumns = ((Set) datarow.get("keyColumns"));

    final HashMap keyMap = new HashMap();

    // create map that contains all keys to be added to every new datarow
    final Iterator keyIter = keyColumns.iterator();
    // loop all key columns
    while (keyIter.hasNext()) {
      final String key = (String) keyIter.next();
      // add key columns from original datarow.
      keyMap.put(key, datarow.get(key));
    }

    return keyMap;
  }

  final private void handleRBS(final String objectClass, final Map datarow, final Map keyMap,
      final MeasurementFile measFile, final boolean newVectorFile) throws Exception {

    final Map tmpMap = new HashMap();
    int max = 0;

    // get VECTOR counters
    final Set rangeCounters = ((Set) datarow.get("rangeCounters"));

    // loop all range (vector) counters in this datarow
    Iterator iter = rangeCounters.iterator();
    while (iter.hasNext()) {

    	final String key = (String) iter.next();
      
    	if (datarow.containsKey(key)) {
    	  
	    	String tmp = (String) datarow.get(key);
	
	    	//BREAK-OUT THE RANGE (VECTOR) INTO AN ArrayList:
	    	final StringTokenizer tokens = new StringTokenizer(tmp, delimiter, true);
	    	List <String> bins = new ArrayList(tokens.countTokens()+1); //We do not know capacity needed, so make it bigger then needed (and reduce later).
	    	boolean prevTokenWasDelim = true;
	    	String currentToken; 
	   		while (tokens.hasMoreTokens()){
	   			currentToken = tokens.nextToken();
	   			if(!currentToken.equalsIgnoreCase(delimiter)){
	   				bins.add(currentToken); //It's not a delimiter so add it.
	   				prevTokenWasDelim = false;
	   			}else if(!prevTokenWasDelim){
	   				prevTokenWasDelim = true; //It's a delimiter so we don't add anything
	   			}else{
	   				bins.add(null); //It's a delimiter AND SO WAS THE LAST ONE. This represents empty input - so we add null.
	   			}
	   		}
	   		if(prevTokenWasDelim){ //This accounts for empty bin at end of vector.
	   			bins.add(null);
	   		}
	   		((ArrayList)bins).trimToSize();  //Set the capacity of the ArrayList to it's size.
	   		
	   		//DECOMPRESS THE VECTOR IF REQUIRED:
	    	if(compressedVectorCounters.contains(key)){
	    		bins = decompressVector(bins);
	    		if(null==bins){
	    			log.info("Vector "+key+" could not be decompressed.");
	    			datarow.put(key, null);
	    			continue;
	    		}
	    	}
	    	
	    	//COLLECT VECTOR IN A HashMap
	   		tmpMap.put(key, bins);
	   		
	   		//IS IT THE LONGEST VECTOR SO FAR?
	   		if(bins.size()>max){
	   			max = bins.size();
	   		}

	    	// IF IT'S REQUIRED, INSERT THE 1ST VALUE OF VECTOR (THE ZERO INDEX) INTO datarow
	        if (removeRanged && !tmpMap.isEmpty()) {
	          datarow.put(key, bins.get(0));
	        }

    	}
    }
    
    // get CMVECTOR counters
    final Set cmVectorCounters = ((Set) datarow.get("cmVectorCounters"));

    // loop all CMVECTOR counters in this datarow
    Iterator cmIter = cmVectorCounters.iterator();
    while (cmIter.hasNext()) {

      final String key = (String) cmIter.next();

      if (datarow.containsKey(key)) {

        String tmp = (String) datarow.get(key);

        // musta add one delim to the end to make it work...
        tmp += delimiter;
        int i = 0;
        boolean prewWasDelim = true;
        final StringTokenizer token = new StringTokenizer(tmp, delimiter, true);

        while (token.hasMoreTokens()) {

          final String tmptoken = token.nextToken();
          String value = null;

          if (prewWasDelim || (tmptoken.equalsIgnoreCase(delimiter) && prewWasDelim)) {

            if (!tmptoken.equalsIgnoreCase(delimiter)) {
              value = tmptoken;
            }
            if (tmpMap.containsKey(key)) {

              ((List) tmpMap.get(key)).add(value);

            } else {
              final List list = new ArrayList();
              list.add(value);
              tmpMap.put(key, list);
            }

            i++;

          }

          prewWasDelim = false;
          if (tmptoken.equalsIgnoreCase(delimiter)) {
            prewWasDelim = true;
          }
        }

        // get the number of rows to add..
        if (max < i) {
          max = i;
        }
        
        if(!tmpMap.isEmpty()) {
          
          // put the value from the first (zero) index into the original datarow because CMVECTORs are on hand
          datarow.put(key, ((List) tmpMap.get(key)).get(0));
        
          // set the value from the first (zero) index to NULL for the CMVECTOR 
          ((List) tmpMap.get(key)).set(0, null);
        
        }
       
      }

    }

    // if we are going to create new vector data file add the original 0 row to
    // the measfile.
    int start = 1;
    if (newVectorFile) {
      start = 0;
    }
    // loop all new rows to be added to the measFile
    for (int index = start; index < max; index++) {

      final Map result = new HashMap();
      iter = tmpMap.keySet().iterator();

      // loop all range counters (columns)
      while (iter.hasNext()) {

        final String key = (String) iter.next();
        final List list = (List) tmpMap.get(key);

        if (index < list.size()) {

          // range index
          result.put(key + rangePostfix, "" + index);

          // counter range value
          result.put(key, list.get(index));
        }

        // counter range index
        result.put(rangeColunName, "" + index);

      }

      // add key rows

      result.put("PERIOD_DURATION", datarow.get("PERIOD_DURATION"));
      result.put("MTS", datarow.get("MTS"));
      result.put("nesw", datarow.get("nesw"));
      result.put("nedn", datarow.get("nedn"));
      result.put("neun", datarow.get("neun"));
      result.put("st", datarow.get("st"));
      result.put("vn", datarow.get("vn"));
      result.put("DC_SUSPECTFLAG", datarow.get("DC_SUSPECTFLAG"));
      result.put("MOID", datarow.get("MOID"));

      result.put("SN", senderName);
      // result.put("MTS", mts);
      // result.put("nesw", nesw);
      // result.put("nedn", nedn);
      // result.put("neun", neun);
      // result.put("PERIOD_DURATION", granularityPeriod);
      result.put("DATETIME_ID", collectionBeginTime);
      result.put("objectClass", objectClass);
      result.put("filename", sourceFile.getName());
      // result.put("DC_SUSPECTFLAG", suspectFlag);
      result.put("DIRNAME", sourceFile.getDir());
      result.put("JVM_TIMEZONE", JVM_TIMEZONE);

      //EEIKBE (HK80868): Put all the rest of the key's in, but don't overwrite newly added values.
      Iterator iterator = keyMap.entrySet().iterator();
      while (iterator.hasNext()) {
          Map.Entry kvPair = (Map.Entry)iterator.next();
          if(result.containsKey(kvPair.getKey())){
        	  //Skip, don't overwrite this key!!
          }else{
        	  result.put(kvPair.getKey(), kvPair.getValue());
          }
      }
//      result.putAll(keyMap);
      //TR (HK80868) FINISH
      
      measFile.setData(result);

      measFile.saveData();

    }

    if (!newVectorFile) {
      // add "zero" indexs to the counter range in datarow

      iter = tmpMap.keySet().iterator();
      // loop all range counters (columns) and s
      while (iter.hasNext()) {
        final String key = (String) iter.next();
        // range index
        datarow.put(key + rangePostfix, "0");
      }

      datarow.put(rangeColunName, "0");

      measFile.setData(datarow);
      measFile.saveData();
    }

  }

 /* private void  handleUniquedVector(final String objectClass, final Map datarow, final Map keyCounters,
      final Map vectorMeasFileMap) throws Exception {

    // create unique vector files
    final Map uniqueCounterMap = (Map) datarow.get("uniqueCounters");
    final Iterator iter = uniqueCounterMap.keySet().iterator();

    while (iter.hasNext()) {

      final String uKey = (String) iter.next();
      final List conterList = (List) uniqueCounterMap.get(uKey);
      final MeasurementFile meas = (MeasurementFile) vectorMeasFileMap.get(uKey);

      for (int i = 0; i < conterList.size(); i++) {

        final String counterName = (String) conterList.get(i);
        final String counterValue = (String) datarow.get(counterName);

        if (counterValue == null || counterValue.length() == 0) {
          continue;
        }

        final StringTokenizer token = new StringTokenizer(counterValue, delimiter);
        int index = 0;
        String zeroIndex = "";

        while (token.hasMoreTokens()) {

          final String tmptoken = token.nextToken();

          final Map result = new HashMap();

          if (index == 0) {
            zeroIndex = tmptoken;
            // data
            result.put(counterName, "");

          } else {

            // data
            result.put(counterName, tmptoken);

          }

          // indexZero
          result.put(uniqueVectorZeroIndex, zeroIndex);

          // Running index
          result.put(uniqueVectorIndex, "" + index);

          // add key rows

          if (vectorColumn.length() > 0) {
            result.put(vectorColumn, counterName);
          }

          result.put("PERIOD_DURATION", datarow.get("PERIOD_DURATION"));
          result.put("MTS", datarow.get("MTS"));
          result.put("nesw", datarow.get("nesw"));
          result.put("nedn", datarow.get("nedn"));
          result.put("neun", datarow.get("neun"));
          result.put("st", datarow.get("st"));
          result.put("vn", datarow.get("vn"));
          result.put("DC_SUSPECTFLAG", datarow.get("DC_SUSPECTFLAG"));
          result.put("MOID", datarow.get("MOID"));

          result.put("SN", senderName);
          // result.put("MTS", mts);
          // result.put("nesw", nesw);
          // result.put("nedn", nedn);
          // result.put("neun", neun);
          // result.put("PERIOD_DURATION", granularityPeriod);
          result.put("DATETIME_ID", collectionBeginTime);
          result.put("objectClass", objectClass + uKey);
          result.put("filename", sourceFile.getName());
          // result.put("DC_SUSPECTFLAG", suspectFlag);

          result.put("DIRNAME", sourceFile.getDir());
          result.put("JVM_TIMEZONE", JVM_TIMEZONE);

          // keys
          result.putAll(keyCounters);

          // add data
          meas.setData(result);

          // save file
          meas.saveData();


          
          index++;

        }

      }

      //meas.close();
      
    }
  }*/
  
  private void  handleUniquedVector(final String objectClass, final Map datarow, final Map keyCounters,
	      final Map vectorMeasFileMap) throws Exception {

	    // create unique vector files
	    final Map uniqueCounterMap = (Map) datarow.get("uniqueCounters");
	    final Iterator iter = uniqueCounterMap.keySet().iterator();
	    int counterValueRangeCount = Integer.valueOf(counterValueRange);
	    final Map counterNameValueMap = new HashMap();
	    // while loop is used once only. It is in existing code so did not touch
	    // But can be removed by doing some more testing with data
	    while (iter.hasNext()) {

	      final String uKey = (String) iter.next();
	      final List counterList = (List) uniqueCounterMap.get(uKey);
	      final MeasurementFile meas = (MeasurementFile) vectorMeasFileMap.get(uKey);

	      for (int i = 0; i < counterList.size(); i++) {

	        final String counterName = (String) counterList.get(i);
	        final String counterValue = (String) datarow.get(counterName);

	        if (counterValue == null || counterValue.length() == 0) {
	          continue;
	        }

	        final StringTokenizer token = new StringTokenizer(counterValue, delimiter);


	        final String counterValues[] = new String[19];
	        int count = 0;
	        while (token.hasMoreTokens()) {

	          final String tmptoken = token.nextToken();
	          counterValues[count] = tmptoken;
	          count++;
	          
	        }
	        
	        counterNameValueMap.put(counterName, counterValues);
	      }
	      // Now we put pmres name and values in counterNameValueMap
	      // which wil be used in following code	      
	      int index = 0;
	      // loop will start from 1 as 0th value is handled in seperete way.
	      // 0th value is not required to save seperately as row. Oth value of pmRes
	      //will be handled together with rest of the counters  
	      // loop will conitue till the length of pmRes value which is defined in property
    	  for(int j=1; j < counterValueRangeCount; j++){
	          final Map result = new HashMap();
	          String zeroIndex = "";
    		  for(int k = 0; k < counterList.size(); k++){
    		        
    		        String counterTempName = (String) counterList.get(k);
    		        // For pmres 0th value, we are making pmres name like pmRes1_0, pmRes2_0
    		        String counterTempNameForZeroIndex = counterTempName + "_0";
    		        
    			  String[] counterTempValue = (String[]) counterNameValueMap.get(counterTempName);

    			  if(counterTempValue == null)
    		        	continue;
    		          
    		          if (index == 0) {
    		            zeroIndex = counterTempValue[index];
    		            // data
    		            result.put(counterTempNameForZeroIndex, zeroIndex);
    		            result.put(counterTempName, counterTempValue[j]);

    		          } else {

    		            // data
    		        	  result.put(counterTempNameForZeroIndex, counterTempValue[0]);
    		            result.put(counterTempName, counterTempValue[j]);

    		          }

    		          // indexZero
    		          result.put(uniqueVectorZeroIndex, "");

    		          // Running index
    		          //For pmRes changes we don't require to fill vector column
    		          if (vectorColumn.length() > 0) {
    			            result.put(vectorColumn, "");
    			          }
    		  }
    		  if(index == 0){
    			  // used trick to handle 0th value together with rest of pmRes counters.
    			  // Because we don't need to handle 0th value seperately 
    			  index = index+1;
    			  result.put(uniqueVectorIndex, "" + index);
    		  }
    		  else
    			  // Value of index will be put in normal way
    			  result.put(uniqueVectorIndex, "" + index);
    	          // add key rows
	          result.put("PERIOD_DURATION", datarow.get("PERIOD_DURATION"));
	          result.put("MTS", datarow.get("MTS"));
	          result.put("nesw", datarow.get("nesw"));
	          result.put("nedn", datarow.get("nedn"));
	          result.put("neun", datarow.get("neun"));
	          result.put("st", datarow.get("st"));
	          result.put("vn", datarow.get("vn"));
	          result.put("DC_SUSPECTFLAG", datarow.get("DC_SUSPECTFLAG"));
	          result.put("MOID", datarow.get("MOID"));

	          result.put("SN", senderName);
	          // result.put("MTS", mts);
	          // result.put("nesw", nesw);
	          // result.put("nedn", nedn);
	          // result.put("neun", neun);
	          // result.put("PERIOD_DURATION", granularityPeriod);
	          result.put("DATETIME_ID", collectionBeginTime);
	          result.put("objectClass", objectClass + uKey);
	          result.put("filename", sourceFile.getName());
	          // result.put("DC_SUSPECTFLAG", suspectFlag);

	          result.put("DIRNAME", sourceFile.getDir());
	          result.put("JVM_TIMEZONE", JVM_TIMEZONE);

	          // keys
	          result.putAll(keyCounters);

	          // add data
	          meas.setData(result);

	          // save file
	          meas.saveData();
 
	          index++;
    		  
    	  }
    	  
      }
	      //}

	      //meas.close();
	      
//	    }
	  }
  

	  
	  /**
	   * This method decompresses (decodes) a compressed vector. 
	   * 
	   * @input	A compressed vector as a List: first entry in list indicates number of indexes, and following entries are alternatly an index and a value. 
	   * @return	A decompressed (decoded) version of the input as a List, containing the values in their corect position.
	   */
	  public List decompressVector(List input){
		  
		  if(null==input){
			  return null;
		  }
		  
		  int inputSize = input.size();
		  
		  if (input.isEmpty() || (input.get(0).equals("")&&inputSize==1) ){
			  return input;  //Return input List if it is empty 
		  }
		  
		  final int expectedNumOfPairs;
		  try{
			  expectedNumOfPairs = Integer.parseInt((String)input.get(0)); //Find how many index value pairs there are in List.
		  }catch(Exception e){
			  return null;
		  }
		  
		  if(expectedNumOfPairs > 1024){ //Make sure it's not too big
			  return null;
		  }
		  if(expectedNumOfPairs==0 && inputSize==1){
			  return input;  //return input if it just has zero
		  }
		  if (inputSize % 2 == 0){ //Make sure it has odd size
			  return null;
		  }
		  if(inputSize!=(expectedNumOfPairs*2)+1){ //Check for correct num of name-value pairs. This also catches negative expectedNumOfPairs
			  return null; 
		  }

		  //This FOR loop does 3 things: sanity checks the indecies, finds the highest one and puts them in their own ArrayList, 
		  int index;
		  int highestIndex = -1;
		  List  <Integer> indecies = new ArrayList(expectedNumOfPairs);
		  for (int i=1;i<inputSize;i=i+2){ //Takes the indecies from input List and put the in their own list
			  try{
				  index = Integer.parseInt((String)input.get(i));
			  }catch(Exception e){
				  return null;
			  }
			  if(index<0){
				  return null;
			  }
			  indecies.add(index);
			  if(index > highestIndex){
				  highestIndex = index;
			  }
		  }
		  if(highestIndex>1024){ //Make sure highest index is not too big
			  return null;
		  }
		  
		  //Instanciate ArrayList to be returned (with required capacity) and fill it with zeros
		  List <String> result = new ArrayList(highestIndex+1);
		  for(int i=0;i<=highestIndex;i++){
			  result.add("0");
		  }
		  
		  //Add the values from input into the correct position in the returned ArrayList (according to their corresponding index)
		  for(int i=0;i<indecies.size();i++){
			  result.set(indecies.get(i), (String)input.get(i*2+2));
		  }
		  
		  return result;
	  }
  

  /**
   * @throws SAXException
   */
  private void handleTAGmdc() throws SAXException {
    try {

      String oldObjClass = "";
      MeasurementFile measFile = null;
      MeasurementFile vectorMeasFile = null;
      final Map uniqueVectorMeasFileMap = new HashMap();

      // all of the datarows of this file have been collected to the
      // measurementMap

      final List keyList = sortMOIDs();

      // loop all the measurement keys (moid) from sorted list
      for (int index = 0; index < keyList.size(); index++) {

        final List list = (List) keyList.get(index);

        final String key = (String) list.get(MOID);
        final Map datarow = (Map) measurementMap.get(key);

        final String objClass = (String) datarow.get("objectClass");

        log.log(Level.FINEST, "KEY:" + key + " ObjectClass:" + objClass);

        // change file when object class changes
        if (!oldObjClass.equals(objClass)) {

          // close old meas file
          if (measFile != null) {
            measFile.close();
          }

          // create new measurementFile
          measFile = Main.createMeasurementFile(sourceFile, objClass, techPack, setType, setName, workerName, log);

          // if this is vector style create new objclass
          if (createOwnVectorFile && datarow.containsKey(ISRANGEDATA)) {

            // create new measurementFile
            vectorMeasFile = Main.createMeasurementFile(sourceFile, objClass + vectorPostfix, techPack, setType,
                setName, workerName, log);

          }

          // does data row contain unique vectors
          if (datarow.get("uniqueCounters") != null && !((Map) datarow.get("uniqueCounters")).isEmpty()) {

            final Map uniqueCounterMap = (Map) datarow.get("uniqueCounters");
            final Iterator iter = uniqueCounterMap.keySet().iterator();

            while (iter.hasNext()) {

              final String uKey = (String) iter.next();

              // create new measurementFile for unique vectors
              uniqueVectorMeasFileMap.put(uKey, Main.createMeasurementFile(sourceFile, objClass + vectorPostfix + uKey,
                  techPack, setType, setName, workerName, log));

            }

          }

          oldObjClass = objClass;
        }

        // if datarow contains ranged data or unique counters
        if (datarow.containsKey(ISRANGEDATA)
            || (datarow.get("uniqueCounters") != null && !((Map) datarow.get("uniqueCounters")).isEmpty())) {

          final Map keyCounters = getKeyCounters(datarow);

          if (createOwnVectorFile) {

            // create own vector file
            removeRanged = false;
            if (vectorMeasFile != null) {
            	log.finest("VectorMeasFile being closed for ObjectClass : " + objClass);
                vectorMeasFile.close();
               }

            vectorMeasFile = Main.createMeasurementFile(sourceFile, objClass + vectorPostfix, techPack, setType,setName, workerName, log); 
            handleRBS(objClass + vectorPostfix, datarow, keyCounters, vectorMeasFile, true);

            if (datarow.get("uniqueCounters") != null && !((Map) datarow.get("uniqueCounters")).isEmpty()) {
              handleUniquedVector(objClass + vectorPostfix, datarow, keyCounters, uniqueVectorMeasFileMap);
            }
            
            Integer hashDataNormalCounters = (Integer)datarow.get("__hashDataNormalCounters");
            
            if(null == hashDataNormalCounters){
            	hashDataNormalCounters = 0;
            }
            
            if (hashDataNormalCounters>0){
              // add datarow to normal data
              measFile.setData(datarow);
              measFile.saveData();
            } else {
              this.log.finest("No normal counters found for datarow.");
            }

          } else {
            handleRBS(objClass, datarow, keyCounters, measFile, false);
          }
        } else {

          if (measFile != null) {
            // if we are reading vector data there should be
            // rangeColunName in datarow.
            if (rbs) {
              datarow.put(rangeColunName, "0");
            }
            measFile.setData(datarow);
            measFile.saveData();
          }

        }

      }

      // close final meas file
      if (measFile != null) {
        measFile.close();
      }

      // close final meas file
      if (vectorMeasFile != null) {
        vectorMeasFile.close();
      }

    } catch (Exception e) {
      log.log(Level.WARNING, "Error closing measurement file", e);
      throw new SAXException("Error closing measurement file: " + e.getMessage(), e);
    }
  }

  /**
   * @return
   */
  private List sortMOIDs() {
    final Iterator iter = measurementMap.keySet().iterator();
    this.log.finest("measurementMap has " + measurementMap.keySet().size() + " keys");
    final List keyList = new ArrayList();

    // loop all the measurement
    while (iter.hasNext()) {
      // put keys to a list for sorting..

      final String key = (String) iter.next();
      //this.log.finest("Iterating at key " + key);
      //this.log.finest("Value for the key is " + measurementMap.get(key));
      final List list = new ArrayList();
      final Map map = (Map) measurementMap.get(key);

      //this.log.finest("map.get(\"objectClass\") returned " + map.get("objectClass"));

      list.add(OBJECTCLASS, (String) map.get("objectClass"));
      list.add(MOID, key);

      keyList.add(list);
    }

    // comparartor for the sort
    final class keyComparator implements java.util.Comparator {

      public int compare(Object o1, Object o2) {

        final String s1 = (String) ((List) o1).get(0);
        final String s2 = (String) ((List) o2).get(0);

        return s1.compareTo(s2);
      }
    }

    this.log.finest("keyList contains " + keyList.size() + " keys.");

    // sort keys
    Collections.sort(keyList, new keyComparator());

    log.log(Level.FINEST, "found " + keyList.size() + " moids from file");
    return keyList;
  }

  public void endElement(final String uri, final String name, final String qName) throws SAXException {

    if (qName.equals("mts")) // measurement end time
    {
      mts = charValue;
    } else if (qName.equals("nesw")) // network element software version
    {
      nesw = charValue;

    } else if (qName.equals("nedn")) // network element distinguished name
    {
      nedn = charValue;

    } else if (qName.equals("neun")) // network element user name
    {
      neun = charValue;

    } else if (qName.equals("st")) // network element user name
    {
      st = charValue;

    } else if (qName.equals("vn")) // network element user name
    {
      vn = charValue;

    } else if (qName.equals("sn")) // senderName
    {
      senderName = charValue;
    } else if (qName.equals("sf")) // suspectFlag
    {
      this.suspectFlag = charValue;

    } else if (qName.equals("mdc")) {

      if (hashData){
        handleTAGmdc();
      }

      return;

    } else if (qName.equals("cbt")) { // collectionBeginTime

      collectionBeginTime = charValue;

    } else if (qName.equals("gp")) { // granularityPeriod

      granularityPeriod = charValue;

    } else if (qName.equals("mt")) { // measTypes

      measNameList.add((Object) charValue);
    } else if (qName.equals("mv")) { // measValues

      try {

        measurement.put("PERIOD_DURATION", granularityPeriod);
        measurement.put("DATETIME_ID", collectionBeginTime);
        measurement.put("MTS", mts);
        measurement.put("nesw", nesw);
        measurement.put("nedn", nedn);
        measurement.put("neun", neun);
        measurement.put("st", st);
        measurement.put("vn", vn);
        measurement.put("DC_SUSPECTFLAG", suspectFlag);
        
        if ("sgsn".equalsIgnoreCase(readVendorIDFrom)) {
          if (measNameList.size() > 0) {
            measurement.put("FIRST_COUNTER_NAME", measNameList.get(0));
          }
        }

        Integer hashDataNormalCounters = (Integer)measurement.get("__hashDataNormalCounters");
        
        if(null == hashDataNormalCounters){
        	hashDataNormalCounters = 0;
        }
        
        hashDataNormalCounters += normalCounters;
        measurement.put("__hashDataNormalCounters", hashDataNormalCounters);

        if (UseMTS) {

          measurementMap.put(measurement.get("MOID") + mts, measurement);

        } else {

          measurementMap.put(measurement.get("MOID"), measurement);
        }

        
        /*
        if ("sgsn".equalsIgnoreCase(readVendorIDFrom)) {
          if (measNameList.size() > 0) {
            measurementMap.put("FIRST_COUNTER_NAME", measNameList.get(0));
          }
        }
        */
        if (!hashData){
          

          
          final String objClass = (String)measurement.get("objectClass");

          log.log(Level.FINEST, " ObjectClass:" + objClass);
       
          // change file when object class changes
          
          if (oldObjClass == null || !oldObjClass.equals(objClass)) {

           
            // close old meas file
            if (measFile != null) {
              measFile.close();
            }

            // create new measurementFile
            measFile = Main.createMeasurementFile(sourceFile, objClass, techPack, setType, setName, workerName, log);

            // if this is vector style create new objclass
            if (createOwnVectorFile && measurement.containsKey(ISRANGEDATA)) {

              // close old meas file
              if (vectorMeasFile != null) {
                vectorMeasFile.close();
              }
              
              // create new measurementFile
              vectorMeasFile = Main.createMeasurementFile(sourceFile, objClass + vectorPostfix, techPack, setType,
                  setName, workerName, log);

            }

            // does data row contain unique vectors
            if (measurement.get("uniqueCounters") != null && !((Map) measurement.get("uniqueCounters")).isEmpty()) {

              final Map uniqueCounterMap = (Map) measurement.get("uniqueCounters");
              final Iterator iter = uniqueCounterMap.keySet().iterator();

              while (iter.hasNext()) {

                final String uKey = (String) iter.next();

                if (uniqueVectorMeasFileMap.containsKey(uKey)){
                  if (uniqueVectorMeasFileMap.get(uKey)!=null){
                    ((MeasurementFile)uniqueVectorMeasFileMap.get(uKey)).close();
                  }
                }
                
                // create new measurementFile for unique vectors
                uniqueVectorMeasFileMap.put(uKey, Main.createMeasurementFile(sourceFile, objClass + vectorPostfix + uKey,
                    techPack, setType, setName, workerName, log));

              }

            }

            oldObjClass = objClass;
          }

          // if datarow contains ranged data or unique counters
          if (measurement.containsKey(ISRANGEDATA)
              || (measurement.get("uniqueCounters") != null && !((Map) measurement.get("uniqueCounters")).isEmpty())) {

            final Map keyCounters = getKeyCounters(measurement);

            if (createOwnVectorFile) {

              // create own vector file
              removeRanged = false;
              handleRBS(objClass + vectorPostfix, measurement, keyCounters, vectorMeasFile, true);

              if (measurement.get("uniqueCounters") != null && !((Map) measurement.get("uniqueCounters")).isEmpty()) {
                handleUniquedVector(objClass + vectorPostfix, measurement, keyCounters, uniqueVectorMeasFileMap);
              }
              
              if (normalCounters>0){
                // add datarow to normal data
                measFile.setData(measurement);
                measFile.saveData();
              } else {
                this.log.finest("No normal counters found for datarow.");
              }


            } else {
              handleRBS(objClass, measurement, keyCounters, measFile, false);
            }
          } else {

            if (measFile != null) {
              // if we are reading vector data there should be
              // rangeColunName in datarow.
              if (rbs) {
                measurement.put(rangeColunName, "0");
              }
              measFile.setData(measurement);
              measFile.saveData();
            }

          }  

          
          //measFile.saveData();
        }
        
      } catch (Exception e) {
        log.log(Level.FINEST, "Error saving measurement data", e);
        throw new SAXException("Error saving measurement data: " + e.getMessage(), e);
      }

    } else if (qName.equals("moid")) { // measObjInstId

      if (hashData){
        handleTAGmoid();
      } else {
        handleTAGmoidNoHash();
      }

    } else if (qName.equals("r")) { // measResults

      // To avoid exceptional crash if there is more r-tags than mt-tags
      if (measIndex < measNameList.size()) {

        boolean containsCmVectors = ((Set) measurement.get("cmVectorCounters")).contains((String) measNameList.get(measIndex));
        // mark counter that contains range data.
        if (!measurement.containsKey(ISRANGEDATA) && rbs
            && (((Set) measurement.get("rangeCounters")).contains((String) measNameList.get(measIndex)) 
            || containsCmVectors )) {
          measurement.put(ISRANGEDATA, "");
          rangedCounters++;
         
          // because of CMVECTOR handling both rangedCounters and normalCounters are added
          if(containsCmVectors){
            normalCounters++;
          }
          
        } else {
          normalCounters++;
        }
        measurement.put((String) measNameList.get(measIndex), charValue);

        measIndex++;

      } else {
        log.warning("Data contains an r-element without meastype name. (More r-tags than mt-tags)");
      }

    }
  }

  /**
   * Extracts a substring from given string based on given regExp
   * 
   */
  public String parseFileName(final String str, final String regExp) {

    final Pattern pattern = Pattern.compile(regExp);
    final Matcher matcher = pattern.matcher(str);

    if (matcher.matches()) {
      final String result = matcher.group(1);
      log.finest(" regExp (" + regExp + ") found from " + str + "  :" + result);
      return result;
    } else {
      log.warning("String " + str + " doesn't match defined regExp " + regExp);
    }

    return "";

  }

  public void characters(final char ch[], final int start, final int length) {
    final StringBuffer charBuffer = new StringBuffer(length);
    for (int i = start; i < start + length; i++) {
      // If no control char
      if (ch[i] != '\\' && ch[i] != '\n' && ch[i] != '\r' && ch[i] != '\t') {
        charBuffer.append(ch[i]);
      }
    }
    charValue += charBuffer;
  }

  public int memoryConsumptionMB() {
	  return memoryConsumptionMB;
  }
  
  public void setMemoryConsumptionMB(int memoryConsumptionMB) {
	  this.memoryConsumptionMB = memoryConsumptionMB;
  }

}
