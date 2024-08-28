package com.distocraft.dc5000.etl.csexport;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
 * <td>readVendorIDFrom</td>
 * <td>Defines where the vendorID is retrieved from <b>data</b> (moid-tag) or from <b>filename</b>. RegExp is used to further define the actual vendorID. Vendor id is added to the outputdata as objectClass. See. VendorID Mask and objectClass</td>
 * <td>data</td>
 * </tr>
 * <tr>
 * <td>VendorID Mask</td>
 * <td>vendorIDMask</td>
 * <td>Defines the RegExp mask that is used to extract the vendorID from either data or filename. See. VendorID from</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>singlerow</td>
 * <td>A list of MOs for which a single row is generated even if there is a seq (sequence) tag. The items in the seq tag will be concatinated (separated by semicolon space) into one value for that key.</td>
 * <td>empty string</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>extractHeaderRows</td>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>headerVendorID</td>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>headerVendorID</td>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>headerTag</td>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>numberOfHeaderRows</td>
 * <td></td>
 * <td></td>
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
 * <td> contains the filename of the inputdatafile.</td>
 * </tr>
 * <td>objectClass</td>
 * <td>contains the same data as in vendorID (see. readVendorIDFrom)</td>
 * </tr>
 * <tr>
 * <td>FDN</td>
 * <td>contains the FDN -tag.</td>
 * </tr>
 * <tr>
 * <td>DC_SUSPECTFLAG</td>
 * <td>EMPTY</td>
 * </tr>
 * <tr>
 * <td>DIRNAME</td>
 * <td>Conatins full path to the nputdatafile.</td>
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
 * 
 * @author savinen
 * <br>
 * <br> 
 *   
 */
public class CSExportParser extends DefaultHandler implements Parser {

  private Logger log;

  private SourceFile sourceFile;

  private FileReader dtdReader;

  private String objectMask;

  private String readVendorIDFrom;

  private String charValue;

  private String fdn;
  
  private List fdnList = null;
  
  private String objectClass;
  
  private String oldObjectClass;

  private String key;
  
  private Set<String> seqKeys = new HashSet<String>(); //For a given MO, these are the attributes that have a sequence in them 
  
  private String seqKey; //The seq attibute (key) being processed currently.
  
  private int seqCount = 0;

  private ArrayList seqContainer = null;
  
  private Map structContainer = null;
  
  private int highestSeqCount = 1;

  private MeasurementFile measFile = null;

  private Map measData;
  
  private String singleRow = "";

  private Map headerData = null;

  private Map cloneData = null;

  private boolean extractHeader = false;

  private String headerVendorID = null;

  private String headerTag = "";

  private int numberOfHeaderRows = 2;

  private String cloneTypeID = "";
  
  private int headerCount = 0;

  // ***************** Worker stuff ****************************

  private String techPack;

  private String setType;

  private String setName;

  private int status = 0;

  private Main mainParserObject = null;

  private String suspectFlag = "";

  private String workerName = "";



  public void init(Main main, String techPack, String setType, String setName, String workerName) {
    this.mainParserObject = main;
    this.techPack = techPack;
    this.setType = setType;
    this.setName = setName;
    this.status = 1;
    this.workerName = workerName;
    
    String logWorkerName = "";
    if (workerName.length() > 0)
      logWorkerName = "." + workerName;
    
    log = Logger.getLogger("etl." + techPack + "." + setType + "." + setName + ".parser.XML" + logWorkerName);
  }

  public int status() {
    return status;
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
    } finally {
      this.status = 3;
    }
  }

  // ***************** Worker stuff ****************************

  public void parse(SourceFile sf, String techPack, String setType, String setName) throws Exception {

    measData = new HashMap();

    this.sourceFile = sf;

    log.finest("Reading configuration...");

    XMLReader xmlReader = new org.apache.xerces.parsers.SAXParser();
    xmlReader.setContentHandler(this);
    xmlReader.setErrorHandler(this);

    objectMask = sf.getProperty("vendorIDMask", ".+,(.+)=.+");
    readVendorIDFrom = sf.getProperty("readVendorIDFrom", "data");
    
    singleRow = sf.getProperty("singlerow", "");

    extractHeader = "true".equalsIgnoreCase(sf.getProperty("extractHeaderRows", "true"));
    headerVendorID = sf.getProperty("headerVendorID");
    headerTag = sf.getProperty("headerTag", "mo");
    numberOfHeaderRows = Integer.parseInt(sf.getProperty("numberOfHeaderRows", "2"));
    cloneTypeID = sf.getProperty("cloneTypeID", "");
    
    log.fine("Staring to parse...");

    xmlReader.setEntityResolver(new ENIQEntityResolver(log.getName()));

    xmlReader.parse(new InputSource(sf.getFileInputStream()));

    log.fine("Parse finished.");
  }

  /**
   * Event handlers
   */
  public void startDocument() {
    log.finest("Start document");
    headerCount = 0;
    fdnList = new ArrayList();
    headerData = new HashMap();
    cloneData = new HashMap();
    oldObjectClass = "";
  }

  public void endDocument() throws SAXException {

    log.finest("End document");
    measData.clear();
    cloneData.clear();
    sourceFile = null;
    
    seqKeys.clear();
    seqContainer = null;

  }

  public void startElement(String uri, String name, String qName, Attributes atts) throws SAXException {

    charValue = new String();
    
    // read object class from data
    for (int i = 0; i < atts.getLength(); i++) {
      if (atts.getLocalName(i).equalsIgnoreCase("fdn")) {
        fdn = atts.getValue(i);
        log.log(Level.FINEST, "fdn: " + fdn);
      }
    }
    
    if (qName.equals(headerTag) && extractHeader && numberOfHeaderRows > headerCount) {

      fdnList.add(fdn);
      headerCount++;
      // first header row

    } else if (qName.equals("mo")) {

           
      // where to read objectClass (moid)
      if (readVendorIDFrom.equalsIgnoreCase("file")) {

        // read vendor id from file
        objectClass = parseFileName(sourceFile.getName(), objectMask);

      } else if (readVendorIDFrom.equalsIgnoreCase("data")) {

        // read vendor id from file
        objectClass = parseFileName(fdn, objectMask);

      } else {

        // error
        log.warning("readVendorIDFrom property" + readVendorIDFrom + " is not defined");
        throw new SAXException("readVendorIDFrom property" + readVendorIDFrom + " is not defined");
      }

    } else if (qName.equals("attr")) {
      for (int i = 0; i < atts.getLength(); i++) {
        if (atts.getLocalName(i).equalsIgnoreCase("name"))
          key = atts.getValue(i);
      }
      
    } else if (qName.equals("seq")) {
    	seqKeys.add(key);
    	seqKey = key;
	    for (int i = 0; i < atts.getLength(); i++) {
	      if (atts.getLocalName(i).equalsIgnoreCase("count")) {
	        seqCount = Integer.parseInt(atts.getValue(i));
	      }
	    }
	    if (seqCount > highestSeqCount){
	    	highestSeqCount = seqCount;
	    }
	    seqContainer = new ArrayList();

    } else if (qName.equals("item")) {

    	
    }else if (qName.equals("struct")) {
    	if (null!=seqContainer){
    		structContainer = new HashMap();
    	}

    }

  }

  public void endElement(String uri, String name, String qName) throws SAXException {

    if (qName.equals(headerTag) && extractHeader && numberOfHeaderRows == headerCount) {
      extractHeader = false;

      try {

        measFile = Main.createMeasurementFile(sourceFile, headerVendorID, techPack, setType, setName, this.workerName, this.log);
        measFile.addData("Filename", sourceFile.getName());
        measFile.addData("DC_SUSPECTFLAG", suspectFlag);
        measFile.addData("DIRNAME", sourceFile.getDir());
        SimpleDateFormat sdf = new SimpleDateFormat("Z");        
        measFile.addData("JVM_TIMEZONE",sdf.format(new Date()));
        measFile.addData("objectClass", headerVendorID);
        
        // add all fdn(s) from header rows.
        for (int i = 0 ; i < fdnList.size() ; i++){
          
          String value = (String)fdnList.get(i);
          String key = "fdn";
          if (i > 0) key += i;
          measFile.addData(key, value);
         
        }
        
               
        // clone the map
        Iterator iter = measData.keySet().iterator();
        while(iter.hasNext()){
          
          String key = (String)iter.next();
          headerData.put(key,measData.get(key));         
        }
               
        measFile.addData(measData);
        measFile.saveData();
        measData.clear();

      } catch (Exception e) {
        log.log(Level.FINE, "Error in writing measurement file", e);
      }

    } else if (qName.equals("attr")) {
    	if (null==seqContainer){
    		//We're not in a sequence (and we may or may not be under a struct)
    		log.log(Level.FINEST, "Key: " + key + " Value: " + charValue);
		    measData.put(key, charValue);
    	} else if (seqContainer.size()==seqCount){
     		//We have finished a sequence
    		measData.put(seqKey, seqContainer);
     		seqContainer = null;
     	} else {
    		//We're in a sequence
     		log.log(Level.FINEST, "Key: " + key + " Value: " + charValue + " of attribute "+seqKey+" being put in struct HashMap for sequence index: "+seqContainer.size()+".");
     		structContainer.put(key, charValue);
     		structContainer.put(key+"_"+seqKey, charValue); //This is to provide distinction when there is more than one seq
    	}

    } else if (qName.equals("item")) {
	    if (null==structContainer){
	    	//This was an item with no struct under it
		    seqContainer.add(charValue);
	    } else {
	    	seqContainer.add(structContainer);
	    	structContainer = null;
	    }
	
    } else if (qName.equals("seq")) {
    	

	
    } else if (qName.equals("struct")) {
    	

	
    } else if (qName.equals("mo")) {
      if (objectClass != null) {
        try {
          // if this is a clonable type, 
          if (this.cloneTypeID.length() > 0 && this.objectClass.equals(cloneTypeID)){     
            // clear clone data map
            cloneData.clear();
        
            // inser this tags data to cloneData map, this map is added to every other datatarow from this point forward.
            Iterator iter = measData.keySet().iterator();
            while(iter.hasNext()){
              String key = (String)iter.next();
              cloneData.put(key,measData.get(key));         
            }
          }	
        	
          if (!objectClass.equals(oldObjectClass)) {
            log.log(Level.FINEST, "New objectClass found: " + objectClass);
            oldObjectClass = objectClass;
            // close old meas file
            if (measFile != null) {
              measFile.close();
            }
            measFile = Main.createMeasurementFile(sourceFile, objectClass, techPack, setType, setName, this.workerName, this.log);

          } else {
            log.log(Level.FINEST, "Old objectClass, no need to cretae new measFile " + oldObjectClass);
          }
          
          
          log.fine("Writting "+objectClass);
          for (int i=0; i<highestSeqCount; i++){
            //Processing one row (one index of sequence). MO's without sequence are also handled (iterate once).
            measFile.addData(headerData);
	        measFile.addData(cloneData);
	        measFile.addData("Filename", sourceFile.getName());
	        measFile.addData("DC_SUSPECTFLAG", suspectFlag);
	        measFile.addData("DIRNAME", sourceFile.getDir());
	        measFile.addData("objectClass", objectClass);
	        measFile.addData("fdn", fdn);
	        SimpleDateFormat sdf = new SimpleDateFormat("Z");
	        measFile.addData("JVM_TIMEZONE",sdf.format(new Date()));
	        measFile.addData(measData);
	        
	        final Iterator<String> seqsKeysIter = seqKeys.iterator();
	        while (seqsKeysIter.hasNext()) { //For each attribute with seq tag (for each seqKey), 
	          //..get the item for current sequence index.
	          seqKey = (String) seqsKeysIter.next();
	          seqContainer = (ArrayList) measData.get(seqKey);
	          if (i < seqContainer.size()) { //Check if we have come to the end of items for this seqKey
	            if (seqContainer.get(i) instanceof String){
	              //The item could be a string value,
	              measFile.addData(seqKey, (String) seqContainer.get(i));
	              log.finest("Sequence key "+seqKey+" has String item");
		        } else if (seqContainer.get(i) instanceof HashMap) {
		          //..or could be a mapping of keys and values (from a struct tag).
		          measFile.addData( (HashMap) seqContainer.get(i) );
		          measFile.addData(seqKey, ""); //Remove the ArrayList from measFile.
		          log.finest("Sequence key "+seqKey+" has HashMap (struct) item");
		        }
	          }
	        }
	        
	        if (seqKeys.size()>0){//We only need a value for SQUENCE_INDEX when there's a seqKey.
	          measFile.addData("SEQUENCE_INDEX", Integer.toString(i + 1));
	          log.finest("Writting SEQUENCE_INDEX "+Integer.toString(i + 1));
	        } else {
	          measFile.addData("SEQUENCE_INDEX", "");
	          log.finest("There is no sequence for this objectClass");
	        }
	        
	        measFile.saveData();
	        
	        if (singleRow.contains(objectClass)){
	        	break; //Only 1 row to be written for this MO.
	        }
          }
          
          
          measData.clear();
          seqContainer = null;
          seqKeys.clear();
          highestSeqCount = 1;
          
          } catch (Exception e) {
            log.log(Level.INFO, "Error in writing measurement file", e);
            seqContainer = null;
            seqKeys.clear();
            highestSeqCount = 1;
          }

      }

    }
  }

  public void characters(char ch[], int start, int length) {
    StringBuffer charBuffer = new StringBuffer(length);
    for (int i = start; i < start + length; i++) {
      // If no control char
      if (ch[i] != '\\' && ch[i] != '\n' && ch[i] != '\r' && ch[i] != '\t') {
        charBuffer.append(ch[i]);
      }
    }
    charValue += charBuffer;
  }

  /**
   * Extracts a substring from given string based on given regExp
   * 
   */
  public String parseFileName(String str, String regExp) {

    Pattern pattern = Pattern.compile(regExp);
    Matcher matcher = pattern.matcher(str);

    if (matcher.matches()) {
      String result = matcher.group(1);
      log.finest(" regExp (" + regExp + ") found from " + str + "  :" + result);
      return result;
    } else {
      log.warning("String " + str + " doesn't match defined regExp " + regExp);
    }

    return "";

  }
}