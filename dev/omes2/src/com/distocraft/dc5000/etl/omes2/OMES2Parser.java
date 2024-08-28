package com.distocraft.dc5000.etl.omes2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.distocraft.dc5000.etl.parser.Parser;
import com.distocraft.dc5000.etl.parser.SourceFile;
import com.ericsson.eniq.common.ENIQEntityResolver;

/**
 * OMES2 Parser <br>
 * <br>
 * Configuration: <br>
 * <br>
 * Database usage: Not directly <br>
 * <br>
 * Copyright Distocraft 2005 <br>
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
 * <td>DN(X)</td>
 * <td>First DN -tag value in datafile is added to the datamap with 'DN' as the
 * key . Following DN -tag values are added with 'DN(X)' (X is 2 for the second
 * and 3 for the third DN value) as the key. </td>
 * </tr>
 * <tr>
 * <td>objectClass</td>
 * <td>contains the measurement type from localMoid -tag.</td>
 * </tr>
 * <tr>
 * <td>localMoid</td>
 * <td>contains the measurement type from localMoid -tag.</td>
 * </tr>
 * <tr>
 * <td>filename</td>
 * <td> contains the filename of the inputdatafile.</td>
 * </tr>
 * <tr>
 * <td>PERIOD_DURATION</td>
 * <td>contains the data from PMSetup/interval tag.</td>
 * </tr>
 * <tr>
 * <td>DATETIME_ID</td>
 * <td>contains the data from PMSetup/startTime tag.</td>
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
 * </table> <br>
 * <br>
 * 
 * @author lemminkainen
 * @author melkko
 * @author savinen
 * 
 * 
 * <br>
 * Added data elements: - Data elements that parsesr adds to the outputdata.
 * <br>
 * <br>
 * localMoid - contains the data from 'localMoid' tag. <br>
 * 
 */
public class OMES2Parser extends DefaultHandler implements Parser {

  private static final String JVM_TIMEZONE = new SimpleDateFormat("Z").format(new Date());

  private Logger log;

  private SourceFile sourceFile;

  private MeasurementFile measurementFile;

  private String charValue;

  private String oldVendorTag = "";

  private String counterName = "";

  private String targetName = "";

  private HashMap elementMap;

  int dnCount = 0;

  private AttributesImpl setupAtts;

  private AttributesImpl targetAtts;

  // ***************** Worker stuff ****************************

  private String techPack;

  private String setType;

  private String setName;

  private int status = 0;

  private Main mainParserObject = null;

  private String workerName = "";

  /**
   * Initializing the parser variables.
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
    log = Logger.getLogger(techPack + "." + setType + "." + setName + ".parser.OMES2Parser" + logWorkerName);
  }

  /**
   * Returns parse status (1 = initialized, 2 = running, 3 = parsed).
   */
  public int status() {
    return status;
  }

  /**
   * Run method which oversees the parse process from beginning to the end.
   */
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

  /**
   * Parses Nokia OMeS xml files to destination directory
   * 
   * @param sourceFileName
   *          file to pase
   * @param configFileSource
   *          configfile
   * @param fileDestDir
   *          destination directory
   */
  public void parse(final SourceFile sourceFile, final String techPack, final String setType, final String setName)
      throws Exception {
    oldVendorTag = "";
    this.sourceFile = sourceFile;
    elementMap = new HashMap();

    final XMLReader xmlReader = new org.apache.xerces.parsers.SAXParser();
    xmlReader.setContentHandler(this);
    xmlReader.setErrorHandler(this);

    xmlReader.setEntityResolver(new ENIQEntityResolver(log.getName()));
    xmlReader.parse(new InputSource(sourceFile.getFileInputStream()));

  }

  // ************* Event handlers *****************

  /**
   * Event handler called in the beginning of parse.
   */
  public void startDocument() throws SAXException {

  }

  /**
   * Event handler called at the end of parse. Closing the output (measurement)
   * file.
   */
  public void endDocument() throws SAXException {
    try {
      this.measurementFile.close();
    } catch (Exception e) {
      log.log(Level.FINEST, "Error closing measurement file", e);
      throw new SAXException("Error closing measurement file");
    }
  }

  /**
   * This method is called before every element in the document. Depending on
   * the qName parameter of the element, header, measurement type data or
   * counter will be parsed.
   * 
   * @param uri
   *          = the Namespace URI.
   * @param name
   *          = the local name.
   * @param qName
   *          = the qualified (prefixed) name.
   * @param atts
   *          = Elements attributes.
   */
  public void startElement(final String uri, final String name, final String qName, final Attributes atts)
      throws SAXException {

    charValue = "";
    counterName = "";

    if (qName.equals("OMeS")) { // Root-tag

    } else if (qName.equals("PMSetup")) {

      elementMap.put("DATETIME_ID", atts.getValue("startTime"));
      elementMap.put("PERIOD_DURATION", atts.getValue("interval"));
      elementMap.put("filename", sourceFile.getName());

      elementMap.put("JVM_TIMEZONE", JVM_TIMEZONE);
      elementMap.put("DC_SUSPECTFLAG", "");
      elementMap.put("DIRNAME", sourceFile.getDir());

    } else if (qName.equals("PMMOResult")) {

      this.dnCount = 1;

    } else if (qName.equals("MO")) {

    } else if (qName.equals("DN")) {

    } else if (atts.getLength() > 0) {

      targetName = qName;

      targetAtts = new AttributesImpl(atts);
      final String curVendorID = targetAtts.getValue("measurementType");
      final String vendorTag = curVendorID.substring(curVendorID.indexOf(":") + 1);

      if (!vendorTag.equals(oldVendorTag)) {

        try {

          if (measurementFile != null) {
            this.measurementFile.close();
          }

        } catch (Exception e) {
          log.log(Level.WARNING, "Error closing MeasurementFile", e);
        }

        oldVendorTag = vendorTag;

        try {
          this.measurementFile = Main.createMeasurementFile(sourceFile, vendorTag, techPack, setType, setName,
              this.workerName, log);
        } catch (Exception e) {
          log.log(Level.FINEST, "Could not create MeasurementFile", e);
          throw new SAXException("Could not create MeasurementFile", e);
        }

      }
    } else {
      counterName = qName;
    }
  }

  /**
   * This method is called at the end of every element. Writes the parsed data
   * (header, measurement type, counters, additional information) after each
   * measurement row.
   * 
   * @param uri
   *          = the Namespace URI.
   * @param name
   *          = the local name.
   * @param qName
   *          = the qualified (prefixed) name.
   */
  public void endElement(final String uri, final String name, final String qName) throws SAXException {

    log.log(Level.FINEST, "Ending element " + qName);

    if (qName.equals(targetName)) {

      try {
        addAttributesToDataSet(setupAtts);
        addAttributesToDataSet(targetAtts);
        addElementsToDataSet(elementMap);

        log.log(Level.FINEST, "Saving parsing results");

        measurementFile.saveData();
        counterName = "";
        charValue = "";
      } catch (Exception e) {
        log.log(Level.INFO, "Error saving measurement data", e);
      }

    } else if ((counterName.length() > 0) && (charValue.length() > 0) && (measurementFile != null)) {

      if (charValue.indexOf("e") > 0) {
        charValue = extractExponentFormat(charValue);
      }

      log.log(Level.FINEST, "Adding data " + counterName + " with value " + charValue);

      measurementFile.addData(counterName, charValue);

    } else if (qName.equals("OMeS") || qName.equals("PMSetup")) {

      return;

    } else if (qName.equals("DN")) {

      String key = "";

      if (this.dnCount == 1) {
        key = "DN";
      } else {
        key = "DN(" + this.dnCount + ")";
      }

      this.dnCount++;
      elementMap.put(key, charValue);
      return;

    } else if (qName.equals("localMoid")) {

      elementMap.put("localMoid", charValue);
      elementMap.put("objectClass", charValue);
      return;

    }
    int i = 0;
  }

  /**
   * The Parser will call this method to report each chunk of character data.
   * Each data chunk is trimmed and put into data map (header, measurement type
   * or data map).
   * 
   * @param ch
   *          = The characters from the XML document.
   * @param start
   *          = The start position in the array.
   * @param lenght
   *          = The number of characters to read from the array.
   */
  public void characters(final char ch[], final int start, final int length) {
    for (int i = start; i < start + length; i++) {
      // If no control char
      if (ch[i] != '\\' && ch[i] != '\n' && ch[i] != '\r' && ch[i] != '\t') {
        charValue += ch[i];
      }
    }
  }

  /**
   * Adding data from a map to be added to the output (measurement) file.
   * 
   * @param elemMap
   *          = Map of data to be added to teh measurement file.
   */
  private void addElementsToDataSet(final HashMap elemMap) {
    final Iterator it = elemMap.entrySet().iterator();

    while (it.hasNext()) {
      final java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
      log.log(Level.FINEST, "adding key: " + (String) entry.getKey() + "  Value: " + (String) entry.getValue());
      measurementFile.addData((String) entry.getKey(), (String) entry.getValue());
    }
  }

  /**
   * Adds PMSetup's and target's attributes to dataSet
   */
  private void addAttributesToDataSet(final AttributesImpl atts) {
    if (atts != null) {
      for (int i = 0; i < atts.getLength(); i++) {
        measurementFile.addData(atts.getQName(i), atts.getValue(i));
      }
    }
  }

  /**
   * Transforming a number from exponent format to an actual number. For example
   * 0.5e2 is transformed to 50.
   * 
   * @param old
   *          = the value in exponent format.
   * @return given value in integer format.
   */
  private String extractExponentFormat(final String old) {

    String mantissa = old.substring(0, old.indexOf("e"));
    String exp = old.substring(old.indexOf("e") + 1);
    String sign = "";

    if (exp.startsWith("+")) {
      exp = exp.substring(1);
    }

    int iexp = Integer.parseInt(exp);

    if (mantissa.startsWith("+")) {
      mantissa = mantissa.substring(1);
    } else if (mantissa.startsWith("-")) {
      sign = mantissa.substring(0, 1);
      mantissa = mantissa.substring(1);
    }

    int pix = mantissa.indexOf(".");

    final StringBuffer sbm = new StringBuffer();

    if (pix > 0) {

      sbm.append(mantissa.substring(0, pix));
      sbm.append(mantissa.substring(pix + 1));

    } else {
      pix = mantissa.length();
    }

    for (; iexp > 0; iexp--) {
      pix++;
      if (pix > sbm.length()) {
        sbm.append("0");
      }
    }

    for (; iexp < 0; iexp++) {
      if (pix - 1 <= 1) {
        sbm.insert(0, "0");
      }

    }

    if (pix < sbm.length() - 1) {
      sbm.insert(pix, ".");
    }

    sbm.insert(0, sign);

    return sbm.toString();
  }

}
