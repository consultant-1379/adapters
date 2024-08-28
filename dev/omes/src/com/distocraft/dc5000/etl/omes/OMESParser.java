package com.distocraft.dc5000.etl.omes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * OMES Parser <br>
 * <br>
 * Configuration: <br>
 * <br>
 * Database usage: Not directly <br>
 * <br>
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$ <br>
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
 * <td>&nbsp;</td>
 * <td>PMSetup-tags attributes are added to the datamap as key-value pairs.</td>
 * </tr>
 * </tr>
 * <tr>
 * <td>objectClass</td>
 * <td>contains the measurement type.</td>
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
 * @author savinen <br>
 *         <br>
 */
public class OMESParser extends DefaultHandler implements Parser {
  
  private static final String JVM_TIMEZONE = new SimpleDateFormat("Z").format(new Date());

  private Logger log;

  private SourceFile sf;

  private String charValue;

  private Map pmSetupData = null;

  private Map measTypeData = null;

  private MeasurementFile mFile;

  private String currentMeasType;

  private String targetName = "";

  private String counterName = "";

  // ***************** Worker stuff ****************************

  private String techPack;

  private String setType;

  private String setName;

  private int status = 0;

  private Main mainParserObject = null;

  private String workerName = "";

  public void init(final Main main, final String techPack, final String setType, final String setName, final String workerName) {
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
   
    log = Logger.getLogger("etl." + techPack + "." + setType + "." + setName + ".parser.STFIOP"+logWorkerName);
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

  /**
   * Parse one SourceFile
   * 
   * @see com.distocraft.dc5000.etl.parser.Parser#parse(com.distocraft.dc5000.etl.parser.SourceFile,
   *      java.lang.String, java.lang.String, java.lang.String)
   */
  public void parse(final SourceFile sf, final String techPack, final String setType, final String setName) throws Exception {
    this.sf = sf;

    final XMLReader xmlReader = new org.apache.xerces.parsers.SAXParser();
    xmlReader.setContentHandler(this);
    xmlReader.setErrorHandler(this);

    log.fine("Start parsing: " + sf.getName());

    xmlReader.setEntityResolver(new ENIQEntityResolver(log.getName()));
    xmlReader.parse(new InputSource(sf.getFileInputStream()));

    log.fine("Parse finished");
  }

  /**
   * SAX event handlers
   */
  public void startDocument() {
  }

  public void endDocument() {
    try {
      if (mFile != null) {
        mFile.close();
      }
    } catch (Exception e) {
      log.log(Level.WARNING, "Error closing measurementFile", e);
    }
  }

  public void startElement(final String uri, final String name, final String qName, final Attributes atts) {

    charValue = "";

    if (qName.equals("OMeS")) { // root-tag OMeS

      return;

    } else if (qName.equals("PMSetup")) { // PMSetup-tag

      pmSetupData = new HashMap(); // Attributes on PMSetup

      for (int i = 0; i < atts.getLength(); i++) {
        pmSetupData.put(atts.getQName(i), atts.getValue(i));
      }

      log.finer("PMSetupData read. " + pmSetupData.size() + " attributes discovered.");

    } else if (atts.getLength() > 0) { // a measurementType-tag

      targetName = qName;

      final String measType = atts.getValue("measurementType");

      log.finest("Found measurementType: " + measType);

      // Found new measurementType or encountered first measurementType
      if (currentMeasType == null || !currentMeasType.equals(measType)) {

        if (mFile != null) {
          try {
            if (mFile != null) {
              mFile.close();
            }
          } catch (Exception e) {
            log.log(Level.WARNING, "Error closing measurementFile", e);
          }
        }

        try {
          mFile = Main.createMeasurementFile(sf, measType, techPack, setType, setName, this.workerName, log);
        } catch (Exception e) {
          log.log(Level.WARNING, "Unable to create MeasurementFile", e);
        }

        mFile.addData("objectClass", measType);
        mFile.addData("DATETIME_ID", atts.getValue("startTime"));
        mFile.addData("PERIOD_DURATION", atts.getValue("interval"));

        mFile.addData("JVM_TIMEZONE", JVM_TIMEZONE);
        mFile.addData("DC_SUSPECTFLAG", "");
        mFile.addData("DIRNAME", sf.getDir());

        currentMeasType = measType;
      }

      measTypeData = new HashMap(); // Attributes on measurementType-tag

      for (int i = 0; i < atts.getLength(); i++) {
        measTypeData.put(atts.getQName(i), atts.getValue(i));
      }

    } else { // a counter-tag

      counterName = qName;

    }

  }

  public void endElement(final String uri, final String name, final String qName) throws SAXException {

    if (qName.equals("OMeS")) { // root-tag

      return;

    } else if (qName.equals("PMSetup")) { // PMSetup-tag

      return;

    } else if (qName.equals(targetName)) { // MeasurementType-tag

      try {

        mFile.addData(pmSetupData);
        mFile.addData(measTypeData);
        mFile.addData("filename", sf.getName());

        mFile.addData("JVM_TIMEZONE", JVM_TIMEZONE);
        mFile.addData("DC_SUSPECTFLAG", "");
        mFile.addData("DIRNAME", sf.getDir());

        mFile.saveData();

        counterName = "";
        charValue = "";

      } catch (Exception e) {
        throw new SAXException(e);
      }

    } else { // a counter-tag

      if (charValue.length() > 0 && mFile != null) {
        mFile.addData(counterName, charValue);
      }
    }
  }

  public void characters(final char ch[], final int start, final int length) {
    for (int i = start; i < start + length; i++) {
      // If no control char
      if (ch[i] != '\\' && ch[i] != '\n' && ch[i] != '\r' && ch[i] != '\t') {
        charValue += ch[i];
      }
    }
  }
}
