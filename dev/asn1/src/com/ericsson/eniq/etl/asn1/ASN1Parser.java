package com.ericsson.eniq.etl.asn1;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.distocraft.dc5000.etl.parser.Parser;
import com.distocraft.dc5000.etl.parser.ParserMeasurementCache;
import com.distocraft.dc5000.etl.parser.SourceFile;

/**
 * 
 * 
 * <br>
 * <br>
 * ASN1 Parser is executed via "Generic" Parser action <br>
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
 * <td>objectMask</td>
 * <td>Defines the RegExp pattern that is used to retrieve the vendorTag from
 * MeasObjInstID.</td>
 * <td>(.*)</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>nullValue</td>
 * <td>Defines the string that is put to the outputfile when null is read from
 * data.</td>
 * <td>NULL</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>addVendorIDTo</td>
 * <td>Contains a list of comma delimited vendorTags where the vendorTag is
 * added to the counter name<br>
 * EX. if addVendorIDTo contains meastype A (addVendorIDTo=A) then conter names
 * (a,b,c) of A are changed to A_a, A_b and A_c.</td>
 * <td>NULL</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>datetimeIDFormat</td>
 * <td>Defines the format for DATETIME_ID</td>
 * <td>yyyyMMddHHmm</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>trimVendorTag</td>
 * <td>is the vendorTag trimmed.</td>
 * <td>true</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>trimCounterName</td>
 * <td>Are the counter names trimmed.</td>
 * <td>true</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>bufferSize</td>
 * <td>Size of the buffer (in bytes) for the asn1Parser.</td>
 * <td>100000</td>
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
 * <td>PERIOD_DURATION</td>
 * <td>Contains granularityPeriod from data.</td>
 * </tr>
 * <tr>
 * <td>DATETIME_ID</td>
 * <td>Contains data start time (measTimeStamp - granularityPeriod)</td>
 * </tr>
 * <tr>
 * <td>measTimeStamp</td>
 * <td>Contains measTimeStamp from data.</td>
 * </tr>
 * <tr>
 * <td>senderName</td>
 * <td>Contains senderName from data.</td>
 * </tr>
 * <tr>
 * <td>MeasObjInstId</td>
 * <td>Contains measObjInstId from data.</td>
 * </tr>
 * <tr>
 * <td>nEUserName</td>
 * <td>Contains nEUserName from data.</td>
 * </tr>
 * <tr>
 * <td>nEDistinguishedName</td>
 * <td>Contains nEDistinguishedName from data.</td>
 * </tr>
 * 
 * <tr>
 * <td>measFileFooter</td>
 * <td>Contains measFileFooter from data.</td>
 * </tr>
 * <tr>
 * <td>fileFormatVersion</td>
 * <td>Contains fileFormatVersion from data.</td>
 * </tr>
 * <tr>
 * <td>vendorName</td>
 * <td>Contains vendorName from data.</td>
 * </tr>
 * <tr>
 * <td>senderType</td>
 * <td>Contains senderType from data.</td>
 * </tr>
 * 
 * <tr>
 * <td>collectionBeginTime</td>
 * <td>Contains collectionBeginTime from data.</td>
 * </tr>
 * <tr>
 * <td>Filename</td>
 * <td>Contains the source files filename.</td>
 * </tr>
 * </tr>
 * <tr>
 * <td>vendorTag</td>
 * <td>Parsed from MeasObjInstId. See. objectMask</td>
 * </tr>
 * <tr>
 * <td>DC_SUSPECTFLAG</td>
 * <td>Contains suspectFlag from data.</td>
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
 * @author savinen
 * 
 * 
 * 
 */
public class ASN1Parser implements ASN1, Parser {

  boolean verbose = false;

  // Virtual machine timezone unlikely changes during execution of JVM
  private static final String JVM_TIMEZONE = (new SimpleDateFormat("Z")).format(new Date());

  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");

  StringBuffer verboseStuffer;

  SourceFile sf;

  HashMap map = new HashMap();

  private MeasurementFile MeasurementFile;

  protected String fileBaseName;

  private Logger log;

  private String nullValue = "NULL";

  // ***************** Cache stuff ****************************

  private ParserMeasurementCache measCache;

  private boolean cache = false;

  private int cacheSize;

  private String keyConf;

  // ***************** Worker stuff ****************************

  private String techPack;

  private String setType;

  private String setName;

  private int status = 0;

  private Main mainParserObject = null;

  private HashSet addVendorIDSet;

  private String workerName = "";

  private String objectMask;

  private String addObjectMask;

  private boolean trimVendorTag = true;

  private boolean trimCounterName = true;

  private int granularityPeriodMultiplier = 1000;

  private final static String ALLTAG = "#ALL#";

  private final static String RULES = "BEGIN\n" + "MeasDataCollection::=SEQUENCE {\n"
      + "measFileHeader MeasFileHeader,\n" + "measData SEQUENCE OF MeasData,\n" + "measFileFooter MeasFileFooter}\n"
      + "MeasFileHeader::=SEQUENCE{\n" + "fileFormatVersion INTEGER,\n" + "senderName UTF8String (SIZE(0..400)),\n"
      + "senderType SenderType,\n" + "vendorName GraphicString (SIZE (0..32)),\n" + "collectionBeginTime TimeStamp}\n"
      + "SenderType::=GraphicString (SIZE (0..8))\n" + "TimeStamp::=GeneralizedTime\n" + "MeasData::=SEQUENCE {\n"
      + "nEId NEId,\n" + "measInfo SEQUENCE OF MeasInfo}\n" + "NEId::=SEQUENCE{\n"
      + "nEUserName GraphicString (SIZE(0..64)),\n" + "nEDistinguishedName GraphicString (SIZE (0..400))}\n"
      + "MeasInfo::=SEQUENCE {\n" + "measStartTime TimeStamp,\n" + "granularityPeriod INTEGER,\n"
      + "measTypes SEQUENCE OF MeasType,\n" + "measValues SEQUENCE OF MeasValue}\n"
      + "MeasType::=GraphicString (SIZE (1..32))\n" + "MeasValue::=SEQUENCE {\n" + "measObjInstId MeasObjInstId,\n"
      + "measResults SEQUENCE OF MeasResult,\n" + "suspectFlag BOOLEAN DEFAULT FALSE}\n"
      + "MeasObjInstId::=GraphicString (SIZE (1..64))\n" + "MeasResult::=CHOICE {\n"
      + "iValue INTEGER (0..4294967295),\n" + "rValue REAL,\n" + "noValue NULL,\n"
      + "sValue GraphicString (SIZE (0..128))} }\n" + "MeasFileFooter::=TimeStamp\n" + "END";

  private ASN1Handler asn1;

  private ArrayList seqNameList;

  private String seqName;

  private String senderName;

  private int dateFormatLen;

  private String nEUserName;

  private String nEDistinguishedName;

  private String measStartTime;

  private String datetime_id;

  private int granularityPeriod;

  private String vendorTag;

  private String addVendorTag;

  private String measObjInstId;

  private String oldVendorTag = "";

  private String suspectFlag = "false";

  private String measFileFooter;

  private String fileFormatVersion;

  private String senderType;

  private String vendorName;

  private String collectionBeginTime;

  private ArrayList counterList;

  private int counterIndex = 0;

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

    log = Logger.getLogger("etl." + techPack + "." + setType + "." + setName + ".parser.ASN1" + logWorkerName);

  }

  public String choice(final ASN1Rule rule, final byte[] data) throws Exception {

    if (rule.type.equalsIgnoreCase("INTEGER")) {
      return new BigInteger(data) + "";
    } else if (rule.type.equalsIgnoreCase("REAL")) {
      return asn1.doReal(data) + "";
    } else if (rule.type.equalsIgnoreCase("NULL")) {
      return nullValue;
    } else if (rule.type.equalsIgnoreCase("GraphicString")) {
      return asn1.doString(rule, data);
    }
    log.warning("Unknown data type " + rule.type + " at " + rule);
    return "N/A";
  }

  public void seqStart(final int tagID, final int length, final String name) throws Exception {

    // push sequence name to stack
    seqNameList.add(0, name);
    seqName = name;

    if (seqName.equalsIgnoreCase("MeasType")) {
      counterList = new ArrayList();
    } else if (seqName.equalsIgnoreCase("MeasResult")) {

      if (cache) {
        if (measCache == null)
          measCache = new ParserMeasurementCache(log, cacheSize);
        measCache.setTP(techPack);
        measCache.setSetType(setType);
        measCache.setSetName(setName);
        measCache.setWorkerName(workerName);
        measCache.setSourceFile(sf);

      } else {
        // create new measurementFile
        if (MeasurementFile == null || !oldVendorTag.equalsIgnoreCase(vendorTag)) {

          if (MeasurementFile != null) {
            MeasurementFile.close();
          }

          this.log.finest("Using vendorTag = " + vendorTag);

          MeasurementFile = Main.createMeasurementFile(sf, vendorTag, techPack, setType, setName, workerName, log);
        }
      }
      oldVendorTag = vendorTag;
      counterIndex = 0;
      suspectFlag = "false";

    }

    if (verbose) {
      log.fine(verboseStuffer + "Sequence[" + name + "]{ ");
      verboseStuffer.append(" ");
    }

  }

  public void seqEnd(final int tagID, final String name) throws Exception {

    // pop sequence name to stack

    if (seqNameList.isEmpty()) {
      throw new Exception("ASN1 Error: Unexpected sequence end after [" + seqName + "].");
    }

    final String thisSeqName = (String) seqNameList.remove(0);

    if (!seqNameList.isEmpty()) {
      seqName = (String) seqNameList.get(0);
    }

    // if this seq is measValue and the next seq is also measvalue this is valid
    // measurement,
    // if the next value is != measvalue this is the last seq of measurement and
    // no need to create dataline.
    if (thisSeqName.equalsIgnoreCase("MeasValue") && seqName.equalsIgnoreCase("MeasValue")) {

      if (cache) {

        measCache.put("PERIOD_DURATION", "" + granularityPeriod);
        measCache.put("DATETIME_ID", datetime_id);
        measCache.put("measStartTime", measStartTime);
        measCache.put("senderName", senderName);
        measCache.put("measObjInstId", measObjInstId);
        measCache.put("MeasObjInstId", measObjInstId);
        measCache.put("measFileFooter", measFileFooter);
        measCache.put("fileFormatVersion", fileFormatVersion);
        measCache.put("senderType", senderType);
        measCache.put("vendorName", vendorName);
        measCache.put("collectionBeginTime", collectionBeginTime);
        measCache.put("nEUserName", nEUserName);
        measCache.put("nEDistinguishedName", nEDistinguishedName);
        measCache.put("Filename", sf.getName());
        measCache.put("vendorTag", vendorTag);
        measCache.put("DC_SUSPECTFLAG", suspectFlag);
        measCache.put("DIRNAME", sf.getDir());
        measCache.put("JVM_TIMEZONE", JVM_TIMEZONE);

        measCache.endOfRow(vendorTag, techPack);

      } else {
        if (MeasurementFile != null) {

          MeasurementFile.addData("PERIOD_DURATION", "" + granularityPeriod);

          MeasurementFile.addData("DATETIME_ID", datetime_id);
          MeasurementFile.addData("measStartTime", measStartTime);
          MeasurementFile.addData("senderName", senderName);
          MeasurementFile.addData("measObjInstId", measObjInstId);
          MeasurementFile.addData("MeasObjInstId", measObjInstId);
          MeasurementFile.addData("measFileFooter", measFileFooter);
          MeasurementFile.addData("fileFormatVersion", fileFormatVersion);
          MeasurementFile.addData("senderType", senderType);
          MeasurementFile.addData("vendorName", vendorName);
          MeasurementFile.addData("collectionBeginTime", collectionBeginTime);
          MeasurementFile.addData("nEUserName", nEUserName);
          MeasurementFile.addData("nEDistinguishedName", nEDistinguishedName);
          MeasurementFile.addData("Filename", sf.getName());
          MeasurementFile.addData("vendorTag", vendorTag);
          MeasurementFile.addData("DC_SUSPECTFLAG", suspectFlag);
          MeasurementFile.addData("DIRNAME", sf.getDir());
          MeasurementFile.addData("JVM_TIMEZONE", JVM_TIMEZONE);

          MeasurementFile.saveData();
        }

        // MeasurementFile = null;
        counterIndex = 0;
      }
    }

    if (verbose) {
      verboseStuffer.delete(0, 1);
      log.fine(verboseStuffer + "}" + name + "");
    }

  }

  public void primitive(final int tagID, final String type, final byte[] data, final ASN1Rule rule) throws Exception {

    if (seqName.equalsIgnoreCase("MeasDataCollection")) {

      if (rule.name.equalsIgnoreCase("TimeStamp")) {
        measFileFooter = asn1.doString(rule, data);

      }

    } else if (seqName.equalsIgnoreCase("MeasFileHeader")) {

      // handle header
      if (rule.name.equalsIgnoreCase("fileFormatVersion")) {
        fileFormatVersion = "" + new BigInteger(data);

      } else if (rule.name.equalsIgnoreCase("senderName")) {
        senderName = asn1.doString(rule, data);

      } else if (rule.name.equalsIgnoreCase("senderType")) {
        senderType = asn1.doString(rule, data);

      } else if (rule.name.equalsIgnoreCase("vendorName")) {
        vendorName = asn1.doString(rule, data);

      } else if (rule.name.equalsIgnoreCase("collectionBeginTime")) {
        collectionBeginTime = asn1.doString(rule, data);

      }

    } else if (seqName.equalsIgnoreCase("MeasData")) {

    } else if (seqName.equalsIgnoreCase("NEId")) {

      if (rule.name.equalsIgnoreCase("nEDistinguishedName")) {
        nEDistinguishedName = asn1.doString(rule, data);
      } else if (rule.name.equalsIgnoreCase("nEUserName")) {
        nEUserName = asn1.doString(rule, data);
      }

    } else if (seqName.equalsIgnoreCase("MeasInfo")) {

      if (rule.name.equalsIgnoreCase("TimeStamp")) {
        measStartTime = asn1.doString(rule, data);
      } else if (rule.name.equalsIgnoreCase("granularityPeriod")) {
        granularityPeriod = new BigInteger(data).intValue();
        if (granularityPeriod > 0) {
          // The measStartTime must be fixed. measStartTime is actually the
          // endtime, so granularityPeriod must be reduced from the
          // measStartTime.
          // granularityPeriod is in seconds. Convert it to milliseconds.
          // put the calculated starttime to DATETIME_ID so original string in
          // measStartTime is unchanged.

          final long granPeriodMillis = granularityPeriod * granularityPeriodMultiplier;
          final String oldMeasStartTime = measStartTime;
          try {

            long measStartTimeTs = dateFormat.parse(measStartTime.substring(0, dateFormatLen)).getTime();

            long measStartTimeTsTmp = (measStartTimeTs - granPeriodMillis);

            final Date measStartDate = new Date(measStartTimeTsTmp);

            datetime_id = dateFormat.format(measStartDate) + measStartTime.substring(dateFormatLen);

            this.log.finest("Fixed DATETIME_ID from " + oldMeasStartTime + " to " + datetime_id
                + " because granularityPeriod has value " + granularityPeriod);

          } catch (Exception e) {
            log.log(Level.WARNING, "Failed to fix old DATETIME_ID " + oldMeasStartTime
                + " with new value reduced by granularityPeriod.", e);
          }
        }
      }

    } else if (seqName.equalsIgnoreCase("MeasType")) {

      counterList.add(asn1.doString(rule, data));

    } else if (seqName.equalsIgnoreCase("MeasValue")) {

      if (rule.name.equalsIgnoreCase("measObjInstId")) {

        measObjInstId = asn1.doString(rule, data);

        if (trimVendorTag) {
          vendorTag = parseFileName(measObjInstId, objectMask).trim();
          addVendorTag = parseFileName(measObjInstId, addObjectMask).trim();
        } else {
          vendorTag = parseFileName(measObjInstId, objectMask);
          addVendorTag = parseFileName(measObjInstId, addObjectMask);
        }

      } else if (rule.name.equalsIgnoreCase("suspectFlag")) {
        suspectFlag = "" + asn1.doBoolean(data);
      }

    } else if (seqName.equalsIgnoreCase("MeasResult")) {

      if (addVendorIDSet.isEmpty()) {

        if (cache) {
          measCache.put((String) counterList.get(counterIndex), choice(rule, data));
        } else {
          MeasurementFile.addData((String) counterList.get(counterIndex), choice(rule, data));
          this.log.finest("Adding data " + (String) counterList.get(counterIndex) + " = " + choice(rule, data));
        }

      } else if (addVendorIDSet.contains(ALLTAG)) {

        if (cache) {
          measCache.put(addVendorTag + "_" + counterList.get(counterIndex), choice(rule, data));
        } else {
          MeasurementFile.addData(addVendorTag + "_" + counterList.get(counterIndex), choice(rule, data));
          this.log.finest("Adding data " + addVendorTag + "_" + counterList.get(counterIndex) + " = "
              + choice(rule, data));
        }

      } else {

        String cname = "";

        if (trimCounterName) {

          cname = ((String) counterList.get(counterIndex)).trim();

        } else {

          cname = ((String) counterList.get(counterIndex));
        }

        if (addVendorIDSet.contains(vendorTag)) {
          if (cache) {
            measCache.put(addVendorTag + "_" + cname, choice(rule, data));
          } else {
            MeasurementFile.addData(addVendorTag + "_" + cname, choice(rule, data));
            this.log.finest("Adding data " + addVendorTag + "_" + cname + " = " + choice(rule, data));
          }
        } else {
          if (cache) {
            measCache.put(cname, choice(rule, data));
          } else {
            MeasurementFile.addData(cname, choice(rule, data));
            this.log.finest("Adding data " + cname + " = " + choice(rule, data));
          }

        }
      }

      counterIndex++;
    }

    if (verbose) {
      log.fine(verboseStuffer + "primitive[(" + rule.name + ") " + rule.type + "] " + asn1.doData(rule, data) + " ");
    }

  }

  /**
   * End of file reached. If cache is used, now it is time to save and clear it.
   * 
   * @throws Exception
   */
  public void eof() throws Exception {
    if (cache)
      measCache.saveData();
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

  // ***************** Worker stuff ****************************

  /**
   * 
   * @see com.distocraft.dc5000.etl.parser.Parser#parse(com.distocraft.dc5000.etl.parser.SourceFile,
   *      java.lang.String, java.lang.String, java.lang.String)
   */
  public void parse(final SourceFile sf, final String techPack, final String setType, final String setName)
      throws Exception {

    verboseStuffer = new StringBuffer();
    seqNameList = new ArrayList();
    MeasurementFile = null;

    this.sf = sf;

    final String rules = sf.getProperty("rules", RULES);

    objectMask = sf.getProperty("vendorIDMask", "(.*)");
    addObjectMask = sf.getProperty("addVendorIDMask", objectMask);

    // cache props
    cache = sf.getProperty("cache", "false").equalsIgnoreCase("true");
    cacheSize = Integer.parseInt(sf.getProperty("cacheSize", "5000"));
    keyConf = sf.getProperty("keyConf", "OSS_ID.BSC.CELL_NAME.CELL_NAME_NEIGHBOUR.ADJACENT_TYPE.RA_TYPE");

    verbose = "TRUE".equalsIgnoreCase(sf.getProperty("verbose", "false"));
    trimVendorTag = "TRUE".equalsIgnoreCase(sf.getProperty("trimVendorTag", "true"));
    trimCounterName = "TRUE".equalsIgnoreCase(sf.getProperty("trimCounterName", "true"));
    final int bufferSize = Integer.parseInt(sf.getProperty("bufferSize", "100000"));
    granularityPeriodMultiplier = Integer.parseInt(sf.getProperty("granularityPeriodMultiplier", "1000"));
    final String dateFormatStr = sf.getProperty("datetimeIDFormat", "yyyyMMddHHmm");
    dateFormat = new SimpleDateFormat(dateFormatStr);
    dateFormatLen = dateFormatStr.length();

    nullValue = sf.getProperty("nullValue", "NULL");
    final String addVendorIDTmp = sf.getProperty("addVendorIDTo", "");

    final String[] addVendorIDs = addVendorIDTmp.split(",");

    addVendorIDSet = new HashSet();

    if (addVendorIDTmp.equalsIgnoreCase("all")) {
      addVendorIDSet.add(ALLTAG);
    } else {
      for (int i = 0; i < addVendorIDs.length; i++) {
        addVendorIDSet.add(addVendorIDs[i]);
      }
    }
    log.fine("objectMask: " + objectMask);
    log.fine("rules: " + rules);

    asn1 = new ASN1Handler(this);
    asn1.init(sf.getFileInputStream());
    asn1.setBuffer(bufferSize);
    asn1.setRules(rules);
    asn1.parse();
  }
}
