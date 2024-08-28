/*
 * Created on 20.1.2005
 *
 */
package com.distocraft.dc5000.etl.raml;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.ericsson.eniq.common.ENIQEntityResolver;

/**
 * 
 * Reads RAML style xml file. <br>
 * <br>
 * There are three kinds of main tags in data: managedObject,list and item. <br>
 * Each tag contains datat that is handled differentyly. <br>
 * <br>
 * Data inside managedObject (tag is &lt;managedObject&gt;&lt;/managedObject&gt;) is
 * handelt so that every measurement-value (tag is &lt;p&gt;&lt;/p&gt;) is collected and stored (at the end of managedObject) to a single
 * datarow in outputfile. <br>
 * <br>
 * managedObject can contain lists (tag is &lt;list&gt;&lt;/list&gt;). <br>
 * <br>
 * Lists can contain values (tag is &lt;p&gt;&lt;/p&gt;),
 * each value pair inside a list is stored to its own
 * datarow in outputfile (measurement of the value is the lists name) also index information and key information from
 * managedObject is added to this datarow.<br> 
 * Object type of the new data row is created from old object type + separator + lista name<br>
 * separator is read from RAMLParser.listObjIdSeparator or is "_" by default.<br>
 * <br>
 * Lists can contain also items (tag is &lt;item&gt;&lt;/item&gt;). Each item inside a list <br>
 * is stored as its own datarow. Items contain measurement-value pairs (tag is &lt;p&gt;&lt;/p&gt;) <br> 
 * that are stored in the same datarow in outputfile. <br>
 * Object type of the new data row is created from old object type + separator + lista name<br>
 * separator is read from RAMLParser.listObjIdSeparator or is "_" by default.<br>
 * <br>
 * <br>
 * $id$ <br>
 * <br>
 * Copyright Distocraft 2005 <br>
 * 
 * 
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
 * <td>VendorID from</td>
 * <td>RAMLParser.readVendorIDFrom</td>
 * <td>Defines where the vendorID is retrieved from <b>data</b> (moid-tag) or from <b>filename</b>. RegExp is used to further define the actual vendorID. Vendor id is added to the outputdata as objectClass. See. VendorID Mask and objectClass</td>
 * <td>data</td>
 * </tr>
 * <tr>
 * <td>VendorID Mask</td>
 * <td>RAMLParser.vendorIDMask</td>
 * <td>Defines the RegExp mask that is used to extract the vendorID from either data or filename. See. VendorID from</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>DTD File</td>
 * <td>RAMLParser.dtdfile</td>
 * <td>Defines the DTD-file used when reading the XML inputfile.</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>RAMLParser.listObjIdSeparator</td>
 * <td>Defines the separator character when list name is added to the object name.</td>
 * <td>_</td>
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
 * <td>objectClass</td>
 * <td>contains the 'class' part of the 'managedObject' tag.</td>
 * </tr>
 * <tr>
 * <td>SN</td>
 * <td>EMPTY</td>
 * </tr>
 * <tr>
 * <td>MOID</td>
 * <td>contains the 'id' part of the 'managedObject' tag.</td>
 * </tr>
 * <tr>
 * <td>distName</td>
 * <td>contains the 'distName' part of the 'managedObject' tag.</td>
 * </tr>
 * <tr>
 * <td>version</td>
 * <td>contains the 'version' part of the 'managedObject' tag.</td>
 * </tr>
 * <tr>
 * <td>measType</td>
 * <td>contains the 'class' part of the 'managedObject' tag.</td>
 * </tr>
 * <tr>
 * <td>filename</td>
 * <td> contains the filename of the inputdatafile.</td>
 * </tr>
 * <tr>
 * <td>PERIOD_DURATION</td>
 * <td>EMPTY</td>
 * </tr>
 * <tr>
 * <td>DATETIME_ID</td>
 * <td>contains the 'dateTime' part of the 'log' tag.</td>
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
 * </tr>
 * </table>
 * <br><br> 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * @author Savinen
 *  
 */
public class RAMLParser extends DefaultHandler implements Parser {

	private String charValue;

	private String senderName;

	private String granularityPeriod;

	private String dateTime;

	private ArrayList rowModeStack;

	private ArrayList activeMeasStack;

	private int measIndex;

	private int measValueIndex;

	private String objectClass;

	private FileReader dtdReader;

	private SourceFile sourceFile;

	private MeasurementFile measFile = null;

	protected String fileBaseName;

	private Logger log;

	private String pName;

	private int iIndex = 0;

	private int pIndex = 0;

	private int lIndex = 0;
	
	private String objectMask;

	private String readVendorIDFrom;

	private Map openMeasMap;

	private final static boolean USESUBLISTS = true;

	private final static int MOID = 1;

	private final static int OBJECTCLASS = 0;

  //***************** Worker stuff ****************************
  
  private String techPack;
  private String setType;
  private String setName;
  private int status = 0; 
  private Main mainParserObject = null;
  private String suspectFlag = "";
  private String workerName = "";
  private String listObjIdSeparator = "_";

  
  public void init(Main main,String techPack, String setType, String setName,String workerName){
    this.mainParserObject = main;
    this.techPack = techPack;
    this.setType = setType;
    this.setName = setName;
    this.status = 1;
    this.workerName = workerName;
    
    String logWorkerName = "";
    if (workerName.length() > 0) logWorkerName = "."+workerName;

    log = Logger.getLogger("etl." + techPack + "." + setType + "."
        + setName + ".parser.RAML"+logWorkerName);

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
  
  
	/**
	 * Constructor
	 */
	public RAMLParser() {
		super();
	}

	/**
	 * 
	 * parses file (SourceFile sf)
	 * 
	 * 
	 */
	public void parse(SourceFile sf, String techPack, String setType,
			String setName) throws Exception {
    

		this.sourceFile = sf;

		openMeasMap = new HashMap();

		rowModeStack = new ArrayList();
		activeMeasStack = new ArrayList();

		XMLReader xmlReader = new org.apache.xerces.parsers.SAXParser();
		xmlReader.setContentHandler(this);
		xmlReader.setErrorHandler(this);

    listObjIdSeparator = sf.getProperty("RAMLParser.listObjIdSeparator","_");
		objectMask = sf.getProperty("RAMLParser.vendorIDMask", ".+,(.+)=.+");
		readVendorIDFrom = sf
				.getProperty("RAMLParser.readVendorIDFrom", "data");

    xmlReader.setEntityResolver(new ENIQEntityResolver(log.getName()));
		xmlReader.parse(new InputSource(sf.getFileInputStream()));

	}

	/**
	 * Event handlers
	 */
	public void startDocument() {
	  log.finer("startDocument");
	}

	/**
	 *  
	 */
	public void endDocument() throws SAXException {
	  log.finer("endDocument");
	}

	/**
	 *  
	 */
	public void startElement(String uri, String name, String qName,
			Attributes atts) throws SAXException {
    
    log.finer("startElement "+name);
    
		charValue = new String();

		if (qName.equals("header")) {

		} else if (qName.equals("log")) {

			try {
				// get datatime
				this.dateTime = atts.getValue("dateTime");

			} catch (Exception e) {
				log.log(Level.WARNING, "Error saving measurement data", e);
				throw new SAXException("Error saving measurement data: "
						+ e.getMessage(), e);
			}

		} else if (qName.equals("managedObject")) {

			try {

				String measType = atts.getValue("class");
				String ID = atts.getValue("id");
				String distName = atts.getValue("distName");
				String version = atts.getValue("version");

				rowModeStack.add("managedObject");

				iIndex = 0;
				pIndex = 0;
				lIndex = 0;
				
				pName = "";

				Map activeMeasurement = new HashMap();

				activeMeasurement.put("SN", senderName);
				activeMeasurement.put("MOID", ID);
				activeMeasurement.put("PERIOD_DURATION", granularityPeriod);
				activeMeasurement.put("DATETIME_ID", this.dateTime);
				activeMeasurement.put("objectClass", measType);
				activeMeasurement.put("distName", distName);
				activeMeasurement.put("version", version);
        activeMeasurement.put("filename", sourceFile.getName());
        
        SimpleDateFormat sdf = new SimpleDateFormat("Z");        
        activeMeasurement.put("JVM_TIMEZONE",sdf.format(new Date()));
        activeMeasurement.put("DC_SUSPECTFLAG", suspectFlag);
        activeMeasurement.put("DIRNAME", sourceFile.getDir()); 
        
				activeMeasStack.add(activeMeasurement);

			} catch (Exception e) {
				log.log(Level.WARNING, "Error creating managedObject: "
						+ atts.getValue("class"), e);
				throw new SAXException("Error creating managedObject: "
						+ atts.getValue("class"));
			}

		} else if (qName.equals("p")) {

			try {

				pName = atts.getValue("name");

			} catch (Exception e) {
				log.log(Level.FINEST, "Error reading value", e);
				throw new SAXException("Error reading value");
			}

		} else if (qName.equals("item")) {

			if (USESUBLISTS) {
				try {

					rowModeStack.add("item");

					// get the top of the stack (list)
					Map objMeasurement = (Map) activeMeasStack
							.get(activeMeasStack.size() - 1);

					String sn = (String) objMeasurement.get("SN");
					String moid = (String) objMeasurement.get("MOID");
					String granularityPeriod = (String) objMeasurement
							.get("PERIOD_DURATION");
					String DATETIME_ID = (String) objMeasurement
							.get("DATETIME_ID");
					String objectClass = (String) objMeasurement
							.get("objectClass");
					String distName = (String) objMeasurement.get("distName");
					String version = (String) objMeasurement.get("version");
					String measType = (String) objMeasurement.get("measType");

					Map activeMeasurement = new HashMap();
					activeMeasurement.put("SN", sn);
					activeMeasurement.put("MOID", moid);
					activeMeasurement.put("PERIOD_DURATION", granularityPeriod);
					activeMeasurement.put("DATETIME_ID", DATETIME_ID);
					activeMeasurement.put("objectClass", objectClass);
					activeMeasurement.put("distName", distName);
					activeMeasurement.put("version", version);
					activeMeasurement.put("measType", measType);
          activeMeasurement.put("filename", sourceFile.getName());
          
          SimpleDateFormat sdf = new SimpleDateFormat("Z");        
          activeMeasurement.put("JVM_TIMEZONE",sdf.format(new Date()));
          activeMeasurement.put("DC_SUSPECTFLAG", suspectFlag);
          activeMeasurement.put("DIRNAME", sourceFile.getDir()); 

          
					activeMeasStack.add(activeMeasurement);

				} catch (Exception e) {
					log.log(Level.FINEST, "Error initialising sublist", e);
					throw new SAXException("Error initialising sublist");
				}

			}

		} else if (qName.equals("list")) {

			if (USESUBLISTS) {
				try {

					rowModeStack.add("list");

					iIndex = 0;
					pIndex = 0;
					lIndex = 0;
					
					// get the top of the stack (managedObject)
					Map objMeasurement = (Map) activeMeasStack
							.get(activeMeasStack.size() - 1);

					String sn = (String) objMeasurement.get("SN");
					String moid = (String) objMeasurement.get("MOID");
					String granularityPeriod = (String) objMeasurement
							.get("PERIOD_DURATION");
					String DATETIME_ID = (String) objMeasurement
							.get("DATETIME_ID");
					String objectClass = (String) objMeasurement
							.get("objectClass");
					String distName = (String) objMeasurement.get("distName");
					String version = (String) objMeasurement.get("version");

					Map activeMeasurement = new HashMap();

					activeMeasurement.put("SN", sn);
					activeMeasurement.put("MOID", moid);
					activeMeasurement.put("PERIOD_DURATION", granularityPeriod);
					activeMeasurement.put("DATETIME_ID", DATETIME_ID);
					activeMeasurement.put("objectClass", objectClass + listObjIdSeparator
							+ atts.getValue("name"));
					activeMeasurement.put("distName", distName);
					activeMeasurement.put("version", version);
					activeMeasurement.put("measType", atts.getValue("name"));
          activeMeasurement.put("filename", sourceFile.getName());
          
          SimpleDateFormat sdf = new SimpleDateFormat("Z");        
          activeMeasurement.put("JVM_TIMEZONE",sdf.format(new Date()));
          activeMeasurement.put("DC_SUSPECTFLAG", suspectFlag);
          activeMeasurement.put("DIRNAME", sourceFile.getDir()); 

          
					activeMeasStack.add(activeMeasurement);

				} catch (Exception e) {
					log.log(Level.WARNING, "Error initialising sublist", e);
					throw new SAXException("Error initialising sublist");
				}

			}

		}
	}

	
	public void endElement(String uri, String name, String qName)
			throws SAXException {
    
    log.finer("endElement "+name);

		if (qName.equals("managedObject")) {

			try {

				// pop the stack (remove managedObject)
				Map activeMeasurement = (Map) activeMeasStack
						.remove(activeMeasStack.size() - 1);

				// add row..
				writeRow(activeMeasurement);

				//pop the mode stack.
				rowModeStack.remove(rowModeStack.size() - 1);

			} catch (Exception e) {
				log.log(Level.WARNING, "Error closing managedObject", e);
				throw new SAXException("Error closing managedObject: "
						+ e.getMessage(), e);

			}

		} else if (qName.equals("p")) {
			try {

				// no value, no need to put it in..
				if (charValue != null || !charValue.equalsIgnoreCase("")) {

					// if this is list we are iterating..
					if (((String) rowModeStack.get(rowModeStack.size() - 1))
							.equalsIgnoreCase("list")) {

						// get the top of the stack (item)
						Map objMeasurement = (Map) activeMeasStack
								.get(activeMeasStack.size() - 1);

						String sn = (String) objMeasurement.get("SN");
						String moid = (String) objMeasurement.get("MOID");
						String granularityPeriod = (String) objMeasurement
								.get("PERIOD_DURATION");
						String DATETIME_ID = (String) objMeasurement
								.get("DATETIME_ID");
						String objectClass = (String) objMeasurement
								.get("objectClass");
						String distName = (String) objMeasurement
								.get("distName");
						String version = (String) objMeasurement.get("version");
						String measType = (String) objMeasurement
								.get("measType");

						Map activeMeasurement = new HashMap();
						activeMeasurement.put(measType, charValue);
						activeMeasurement.put("ItemId", Integer
								.toString(lIndex));
						lIndex++;

						activeMeasurement.put("SN", sn);
						activeMeasurement.put("MOID", moid);
						activeMeasurement.put("PERIOD_DURATION",
								granularityPeriod);
						activeMeasurement.put("DATETIME_ID", DATETIME_ID);
						activeMeasurement.put("objectClass", objectClass);
						activeMeasurement.put("distName", distName);
						activeMeasurement.put("version", version);
						activeMeasurement.put("measType", measType);
            activeMeasurement.put("filename", sourceFile.getName());
            
            SimpleDateFormat sdf = new SimpleDateFormat("Z");        
            activeMeasurement.put("JVM_TIMEZONE",sdf.format(new Date()));
            activeMeasurement.put("DC_SUSPECTFLAG", suspectFlag);
            activeMeasurement.put("DIRNAME", sourceFile.getDir()); 

            
						writeRow(activeMeasurement);


					} else if (((String) rowModeStack.get(rowModeStack.size() - 1))
							.equalsIgnoreCase("item")) {
						
						// Item						
						Map activeMeasurement = (Map) activeMeasStack
							.get(activeMeasStack.size() - 1);
						activeMeasurement.put(this.pName, charValue);
						activeMeasurement.put("ItemId", Integer
								.toString(iIndex));	
						pIndex++;
						

					} else {
					
						// managedObject
						Map activeMeasurement = (Map) activeMeasStack
								.get(activeMeasStack.size() - 1);
						activeMeasurement.put(this.pName, charValue);
						activeMeasurement.put("ItemId", Integer
								.toString(pIndex));
						pIndex++;
					}

				}

			} catch (Exception e) {
				log.log(Level.WARNING, "Error saving measurement value: "
						+ charValue, e);
				throw new SAXException("Error saving measurement value: "
						+ charValue + " " + e.getMessage(), e);

			}

			return;

		} else if (qName.equals("list")) {

			if (USESUBLISTS) {
				try {

					// pop the stack (remove item)
					// list contains no relevant information so row is not
					// writen
					activeMeasStack.remove(activeMeasStack.size() - 1);

					//pop the mode stack.
					rowModeStack.remove(rowModeStack.size() - 1);

				} catch (Exception e) {
					log.log(Level.WARNING, "Error closing sublist", e);
					throw new SAXException("Error closing sublist: "
							+ e.getMessage(), e);

				}

			}

		} else if (qName.equals("item")) {

			if (USESUBLISTS) {
				try {

					// pop the stack (remove item)
					Map activeMeasurement = (Map) activeMeasStack
							.remove(activeMeasStack.size() - 1);

					// if item contains no data rows (<p>), no need to write datarow
					if (activeMeasurement.containsKey("ItemId")) {

						// write row measurement
						writeRow(activeMeasurement);
					}

					iIndex++;
					
					//pop the mode stack.
					rowModeStack.remove(rowModeStack.size() - 1);

				} catch (Exception e) {
					log.log(Level.WARNING, "Error closing sublist", e);
					throw new SAXException("Error closing sublist: "
							+ e.getMessage(), e);

				}
			}

		} else if (qName.equals("cmData")) {

			try {

				// close all open measurement files
				Iterator iter = this.openMeasMap.keySet().iterator();
				while (iter.hasNext()) {

					String key = (String) iter.next();
					MeasurementFile tmpMeas = (MeasurementFile) openMeasMap
							.get(key);
					tmpMeas.close();

				}

			} catch (Exception e) {
				log.log(Level.WARNING, "Error closing measurement file", e);
				throw new SAXException("Error closing measurement file", e);
			}

			return;

		}

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
			log.finest(" regExp (" + regExp + ") found from " + str + "  :"
					+ result);
			return result;
		} else {
			log.warning("String " + str + " doesn't match defined regExp "
					+ regExp);
		}

		return "";

	}

	public void characters(char ch[], int start, int length) {
		for (int i = start; i < start + length; i++) {
			//If no control char
			if (ch[i] != '\\' && ch[i] != '\n' && ch[i] != '\r'
					&& ch[i] != '\t') {
				charValue += ch[i];
			}
		}
	}

	private void writeRow(Map measurement) throws Exception {
    
		String obj = (String) measurement.get("objectClass");
		if (!openMeasMap.containsKey(obj)) {

      log.finer("writeRow: creating new");
      
			// create new
			measFile = Main.createMeasurementFile(sourceFile,
					(String) measurement.get("objectClass"), techPack, setType,
					setName,this.workerName, this.log);
			openMeasMap.put(obj, measFile);

		} else {
      
      log.finer("writeRow: already open");

			// get old
			measFile = (MeasurementFile) openMeasMap.get(obj);

		}

    if(log.isLoggable(Level.FINEST)) {
      Iterator i = measurement.keySet().iterator();
      while(i.hasNext()) {
        String key = (String)i.next();
        log.finest("  \""+key+"\" = \""+measurement.get(key)+"\"");
      }
    }
    
		measFile.setData(measurement);
		measFile.saveData();
    
    log.finer("saved successfully");

	}

}