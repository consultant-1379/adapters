package com.ericsson.eniq.etl.ct;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.ericsson.eniq.common.*;

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
 * <td>Defines where the vendorID is retrieved from <b>data</b> (moid-tag) or from <b>filename</b>. RegExp is used to
 * further define the actual vendorID. Vendor id is added to the outputdata as objectClass. See. VendorID Mask and
 * objectClass</td>
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
 * <td>multiline</td>
 * <td>are multiple sequences (seq) handled as separate lines (multiline = true) or single line with items comma delimited (multiline = false).</td>
 * <td>false</td>
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
 * <td>fdn</td>
 * <td>contains the data from FDN tag.</td>
 * </tr>
 * <tr>
 * <td>seq</td>
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
public class CTParser extends DefaultHandler implements Parser {

  // Virtual machine timezone unlikely changes during execution of JVM
  private static final String JVM_TIMEZONE = (new SimpleDateFormat("Z")).format(new Date());

  private Logger log;

  private SourceFile sourceFile;

  private String charValue = "";

  private String fdn;

  private String objectMask;

  private String objectClass;

  private String oldObjectClass;

  private String readVendorIDFrom;

  private String key;

  private String seqKey = "";

  private int seqCount = 0;

  private ArrayList seqContainer = null;

  private MeasurementFile measFile = null;

  private Map measData;

  private boolean multiLine = false;

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
    multiLine = "TRUE".equalsIgnoreCase(sf.getProperty("multiline", "false"));

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
    oldObjectClass = null;
  }

  public void endDocument() throws SAXException {

    log.finest("End document");
    measData.clear();
    sourceFile = null;

    // reset seq also
    seqKey = "";
    seqCount = 0;
    seqContainer = null;

  }

  public void startElement(String uri, String name, String qName, Attributes atts) throws SAXException {

    charValue = new String();

    if (qName.equals("mo")) {

      // read object class from data
      for (int i = 0; i < atts.getLength(); i++) {
        if (atts.getLocalName(i).equalsIgnoreCase("fdn")) {
          fdn = atts.getValue(i);
          log.log(Level.FINEST, "fdn: " + fdn);
        }
      }

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
    }
    if (qName.equals("attr")) {

      for (int i = 0; i < atts.getLength(); i++) {
        if (atts.getLocalName(i).equalsIgnoreCase("name"))
          key = atts.getValue(i);
      }
    }
    if (qName.equals("seq")) {

      seqKey = key;

      for (int i = 0; i < atts.getLength(); i++) {
        if (atts.getLocalName(i).equalsIgnoreCase("count")) {
          seqCount = Integer.parseInt(atts.getValue(i));
        }
      }

      seqContainer = new ArrayList();

    }
  }

  public void endElement(String uri, String name, String qName) throws SAXException {

    if (qName.equals("item")) {

      if (seqContainer.size() < seqCount) {
        seqContainer.add(charValue);
      }
      
    
    } else if (qName.equals("seq")) {

    } else if (qName.equals("attr")) {

      log.log(Level.FINEST, "Key: " + key + " Value: " + charValue);
      measData.put(key, charValue);

    } else if (qName.equals("mo")) {

      if (objectClass != null) {

        try {

          if (!objectClass.equals(oldObjectClass)) {

            log.log(Level.FINEST, "New objectClass found: " + objectClass);

            oldObjectClass = objectClass;
            // close old meas file
            if (measFile != null) {
              measFile.close();
            }

            measFile = Main.createMeasurementFile(sourceFile, objectClass, techPack, setType, setName, this.workerName, this.log);

          } else {
            log.log(Level.FINEST, "Old objectClass, no need to create new measFile " + oldObjectClass);
          }

          // no sequenses just add once
          if (seqContainer == null || seqContainer.size() == 0) {

            measFile.addData("Filename", sourceFile.getName());
            measFile.addData("DC_SUSPECTFLAG", suspectFlag);
            measFile.addData("DIRNAME", sourceFile.getDir());
            measFile.addData("objectClass", objectClass);
            measFile.addData("fdn", fdn);
            measFile.addData("JVM_TIMEZONE", JVM_TIMEZONE);
            measFile.addData(measData);
            measFile.saveData();

          } else {

            if (!multiLine) {

              // there is sequnce but we want only one datarow.
              StringBuffer tmp = new StringBuffer();
              for (int i = 0; i < seqContainer.size(); i++) {
                if (i > 0){
                  tmp.append(",");
                }
                tmp.append((String) seqContainer.get(i));
              }

              measFile.addData(measData);
              measFile.addData(seqKey, tmp.toString());
              measFile.addData("Filename", sourceFile.getName());
              measFile.addData("DC_SUSPECTFLAG", suspectFlag);
              measFile.addData("DIRNAME", sourceFile.getDir());
              measFile.addData("objectClass", objectClass);
              measFile.addData("fdn", fdn);
              measFile.addData("JVM_TIMEZONE", JVM_TIMEZONE);
              measFile.saveData();

            } else {

              // there is sequence and we want multiple datarows -> clone data.
              for (int i = 0; i < seqContainer.size(); i++) {

                measFile.addData(measData);
                measFile.addData(seqKey, (String) seqContainer.get(i));
                measFile.addData("Filename", sourceFile.getName());
                measFile.addData("DC_SUSPECTFLAG", suspectFlag);
                measFile.addData("DIRNAME", sourceFile.getDir());
                measFile.addData("objectClass", objectClass);
                measFile.addData("fdn", fdn);
                measFile.addData("JVM_TIMEZONE", JVM_TIMEZONE);
                measFile.saveData();
              }
            }
          }

          measData.clear();

        } catch (Exception e) {
          log.log(Level.FINE, "Error in writing measurement file", e);
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