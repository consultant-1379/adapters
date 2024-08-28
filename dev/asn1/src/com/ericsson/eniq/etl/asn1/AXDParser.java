package com.ericsson.eniq.etl.asn1;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.distocraft.dc5000.etl.parser.Parser;
import com.distocraft.dc5000.etl.parser.SourceFile;

/**
 * 
 * This parser is a custom ASN.1 parser for Performance Data Report (PDR) files
 * from Ericsson AXD301 nodes. <br>
 * The parser is executed via "Generic" Parser action.<br>
 * <br>
 * 
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
 * <td>datetimeIDFormat</td>
 * <td>Defines the format for DATETIME_ID.</td>
 * <td>yyyyMMddHHmmss</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>bufferSize</td>
 * <td>Size of the buffer (in bytes) for the asn1Parser.</td>
 * <td>100000</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>getVendorTagFromFilename</td>
 * <td>Defines if the vendorTag is parsed from the file name. If set to false,
 * then the vendorTag is parsed from the PDR data 'measType' column.</td>
 * <td>"TRUE"</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>verbose</td>
 * <td>Defines if verbose logging is used.</td>
 * <td>"FALSE"</td>
 * </tr>
 * </table>
 * <br>
 * <br>
 * 
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
 * <td>Contains granularityPeriod from the data in seconds. The default value is
 * 900 (=15 minutes) is hard coded and is not calculated from the data.</td>
 * </tr>
 * <tr>
 * <td>DATETIME_ID</td>
 * <td>The time stamp in simple date format.</td>
 * </tr>
 * <td>version</td>
 * <td>Version, for example: "2".</td>
 * </tr>
 * <tr>
 * <td>sender</td>
 * <td>The IP Address of the node (in HEX), for example: "AC1FF802"
 * (=172.31.248.2).</td>
 * </tr>
 * <tr>
 * <td>nodeType</td>
 * <td>The type of the node, for example: "AXD301Backbone".</td>
 * </tr>
 * <tr>
 * <td>measType</td>
 * <td>The measurement type, for example: "CPU Statistics".</td>
 * </tr>
 * <td>timeStamp</td>
 * <td>The time stamp for the PDR in milliseconds, for example: "1218038760000".
 * </td>
 * </tr>
 * <tr>
 * <td>measurementName</td>
 * <td>The name of the measurement, for example:"PMR_11034_CPU Statistics_159.107.194.209_13.97.120.100.51.48.49.64.99.112.49.45.49.57"
 * .</td>
 * </tr>
 * <tr>
 * <td>instance</td>
 * <td>The instance, for example:
 * "13.97.120.100.51.48.49.64.99.112.49.45.49.57".</td>
 * </tr>
 * <tr>
 * <td>status</td>
 * <td>The status of the PDR. Possible values 1-5 (complete, noDataAvailable,
 * noSuchInstance, noSuchObject, other).</td>
 * </tr>
 * <tr>
 * <td>"counters values"</td>
 * <td>The counter values. One column per counter. The name of the column is the
 * counter OID collected from the header. Values are collected from each PDR,
 * for example: 1.3.6.1.4.1.193.19.3.2.2.1.3.1.2=1073741824,
 * 1.3.6.1.4.1.193.19.3.2.2.1.3.1.3=273195008,
 * 1.3.6.1.4.1.193.19.3.2.2.1.3.1.5=486000, 1.3.6.1.4.1.193.19.3.2.2.1.3.1.6=6.</td>
 * </tr>
 * <tr>
 * <td>fileName</td>
 * <td>The PDR file name, for example:
 * "V2_172.31.248.2_loadTable_1_37_AC1FF802".</td>
 * </tr>
 * <tr>
 * <td>vendorTag</td>
 * <td>Parsed from the index in PDR filename. The index consists of the
 * measurement type name and an integer separated by underscore. For example:
 * loadTable_1.</td>
 * </tr>
 * <tr>
 * <td>DC_SUSPECTFLAG</td>
 * <td>A suspectFlag from the data. This value is mapped from the status, i.e.
 * if the status is "1" (complete), then the suspect flag will be false and for
 * any other status value it will be true.</td>
 * </tr>
 * <tr>
 * <td>DIRNAME</td>
 * <td>The full path to the input data file.</td>
 * </tr>
 * <tr>
 * <td>JVM_TIMEZONE</td>
 * <td>The JVM time zone, for example "+0200".</td>
 * </tr>
 * </table>
 * <br>
 * <br>
 * 
 * @author eheitur
 */
public class AXDParser implements ASN1, Parser {

	/**
	 * Verbose indicator used for detailed logging.
	 */
	boolean verbose = false;

	/**
	 * Defines where the vendorTag is parsed from. True: from the filename
	 * (index part), false: from measType tag inside the PDR file (truncated to
	 * 50 characters).
	 */
	boolean getVendorTagFromFileName = true;

	/**
	 * The source file to be parsed.
	 */
	SourceFile sf;

	/**
	 * The measurement file for storing the parsed output.
	 */
	private MeasurementFile MeasurementFile;

	/**
	 * The logger object.
	 */
	private Logger log;

	/**
	 * A string buffer used in the (verbose) logging.
	 */
	StringBuffer verboseStuffer;

	/**
	 * Virtual machine time zone unlikely changes during execution of JVM
	 */
	private static final String JVM_TIMEZONE = (new SimpleDateFormat("Z"))
			.format(new Date());

	/**
	 * The default format for the dateTimeId as a string
	 */
	private String defaultDateTimeIDFormat = "yyyyMMddHHmmss";

	/**
	 * The date format for the DATETIME_ID.
	 */
	private SimpleDateFormat dateFormat = null;

	/**
	 * The technology package. Initialized in init().
	 */
	private String techPack;

	/**
	 * The set type. Initialized in init().
	 */
	private String setType;

	/**
	 * The set name. Initialized in init().
	 */
	private String setName;

	/**
	 * The status of the parser.
	 */
	private int parserStatus = 0;

	/**
	 * The technology package. Initialized in init().
	 */
	private Main mainParserObject = null;

	/**
	 * The name of the worker. Initialized in init().
	 */
	private String workerName = "";

	/**
	 * Performance Data Report: Status indicator values
	 */
	public final static String[] STATUS_VALUES = { "complete",
			"noDataAvailable", "noSuchInstance", "noSuchObject", "other" };

	/**
	 * Version, for example: "2"
	 */
	private String version;

	/**
	 * The IP Address of the node (in HEX), for example: "AC1FF802".
	 */
	private String sender;

	/**
	 * The node type, for example: "AXD301Backbone";
	 */
	private String nodeType;

	/**
	 * The measurement type, for example: "CPU Statistics".
	 */
	private String measType;

	/**
	 * The object identifiers, for example:
	 * "06 0F 2B 06 01 04 01 81 41 13 03 02 02 01 03 01 02",
	 * "06 0F 2B 06 01 04 01 81 41 13 03 02 02 01 03 01 03",
	 * "06 0F 2B 06 01 04 01 81 41 13 03 02 02 01 03 01 05",
	 * "06 0F 2B 06 01 04 01 81 41 13 03 02 02 01 03 01 06"
	 */
	private ArrayList<String> counterList;

	/**
	 * The time stamp for the PDR, for example: 1218038760000.
	 */

	private int timeStamp = 0;

	/**
	 * The granularity period between the PDRs of the same type. Will be stored
	 * as the PERIOD_DURATION (default: 900 seconds).
	 */
	private int granularityPeriod = 900;

	/**
	 * The name of the measurement, for example:"PMR_11034_CPU Statistics_159.107.194.209_13.97.120.100.51.48.49.64.99.112.49.45.49.57"
	 * .
	 */
	private String measurementName;

	/**
	 * The instance, for example:
	 * "13.97.120.100.51.48.49.64.99.112.49.45.49.57".
	 */
	private String instance;

	/**
	 * The status of the PDR. Values 1-5 correspond to the values (complete,
	 * noDataAvailable, noSuchInstance, noSuchObject, other).
	 */
	private int status;

	/**
	 * The value list from the PDR, for example: 1073741824, 273195008, 486000,
	 * 6.
	 */
	private ArrayList<Integer> valueList;

	/**
	 * The handler object for the AXD parser.
	 */
	private AXDHandler axdHandler;

	/**
	 * A stack for storing the sequence names
	 */
	private ArrayList<String> seqNameList;

	private String seqName;

	/**
	 * The date time Id
	 */
	private String datetime_id;

	/**
	 * The vendor tag.
	 */
	private String vendorTag;

	/**
	 * The suspect flag. This value is derived from the status field of the PDR.
	 */
	private String suspectFlag = "false";

	/**
	 * @see com.distocraft.dc5000.etl.parser.Parser#init(com.distocraft.dc5000.etl.parser.Main,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	public void init(final Main main, final String techPack,
			final String setType, final String setName, final String workerName) {

		this.mainParserObject = main;
		this.techPack = techPack;
		this.setType = setType;
		this.setName = setName;
		this.parserStatus = 1;
		this.workerName = workerName;

		String logWorkerName = "";
		if (workerName.length() > 0) {
			logWorkerName = "." + workerName;
		}

		log = Logger.getLogger("etl." + techPack + "." + setType + "."
				+ setName + ".parser.ASN1" + logWorkerName);

	}

	/**
	 * @see com.ericsson.eniq.etl.axd.ASN1#seqStart(int, int, java.lang.String)
	 */
	public void seqStart(final int tagID, final int length, final String name)
			throws Exception {

		// Push the sequence name to the stack
		seqNameList.add(0, name);
		seqName = name;

		// Handle the sequence based on the name
		if (seqName.equalsIgnoreCase("Header")) {
			// Sequence: File header

			// Create a new counter list
			counterList = new ArrayList<String>();

		} else if (seqName.equalsIgnoreCase("Pdr")) {
			// Sequence: a PDR

			// Create a new value list
			valueList = new ArrayList<Integer>();

			// Initialize the suspect flag
			suspectFlag = "false";

			// Create a new measurementFile
			if (MeasurementFile == null) {

				if (MeasurementFile != null) {
					MeasurementFile.close();
				}

				this.log.finest("Using vendorTag '" + vendorTag + "'");

				MeasurementFile = Main.createMeasurementFile(sf, vendorTag,
						techPack, setType, setName, workerName, log);
			}

		} else if (seqName.equalsIgnoreCase("CounterData")) {
			// Sequence: Counters in the file header

		} else if (seqName.equalsIgnoreCase("ValueData")) {
			// Sequence: Values list in a PDR
		}

		// Logging
		if (verbose) {
			log.fine(verboseStuffer + "Sequence[" + name + "]{ ");
			verboseStuffer.append(" ");
		}

	}

	/**
	 * @see com.ericsson.eniq.etl.axd.ASN1#seqEnd(int, java.lang.String)
	 */
	public void seqEnd(final int tagID, final String name) throws Exception {

		// Sanity check: the name list must not be empty
		if (seqNameList.isEmpty()) {
			throw new Exception("ASN1 Error: Unexpected sequence end after ["
					+ seqName + "].");
		}

		// Pop sequence name from the stack
		final String thisSeqName = (String) seqNameList.remove(0);

		// If the sequence is a Performance Data Report, then add the
		// current PDR data to the measurement file.
		if (thisSeqName.equalsIgnoreCase("Pdr")) {

			if (MeasurementFile != null) {

				// The period duration
				MeasurementFile.addData("PERIOD_DURATION", ""
						+ granularityPeriod);

				// Time stamp
				MeasurementFile.addData("DATETIME_ID", datetime_id);

				// File header data
				MeasurementFile.addData("version", "" + version);
				MeasurementFile.addData("sender", sender);
				MeasurementFile.addData("nodeType", nodeType);
				MeasurementFile.addData("measType", measType);

				// File PDR Data
				MeasurementFile.addData("timeStamp", "" + timeStamp);
				MeasurementFile.addData("measurementName", measurementName);
				MeasurementFile.addData("instance", instance);
				MeasurementFile.addData("status", "" + status);

				// Add the counter OID + value.
				// Note: In case there are more values in the value list than
				// there are OIDs, then only the values that have an OID will be
				// stored. In case the value is empty, then -1 will be stored.
				// The final value for -1 will be null later on.
				if (valueList.size() > counterList.size()) {
					log.log(Level.WARNING,
							"Number of counter OIDs and values do not match! The number of OIDs: "
									+ counterList.size()
									+ " < the number of values: "
									+ valueList.size()
									+ ". Extra values will be ignored.");
				}
				String oid = "";
				int value = 0;
				for (int i = 0; i < counterList.size(); i++) {
					oid = counterList.get(i);
					if (i > valueList.size() - 1)
						value = -1;
					else
						value = valueList.get(i);
					// String valueStr = (value == -1) ? "null" : "" + value;
					String valueStr = (value == -1) ? null : "" + value;

					// Add the OID + value
					MeasurementFile.addData("" + oid, valueStr);
				}

				// Other data
				MeasurementFile.addData("fileName", sf.getName());
				MeasurementFile.addData("vendorTag", vendorTag);
				MeasurementFile.addData("DC_SUSPECTFLAG", suspectFlag);
				MeasurementFile.addData("DIRNAME", sf.getDir());
				MeasurementFile.addData("JVM_TIMEZONE", JVM_TIMEZONE);

				// // DEBUG:
				// log.finest("seqEnd(): Data: " + datetime_id + ", " + version
				// + ", " + sender + ", " + nodeType + ", " + measType
				// + ", " + counterList.toString() + ", " + timeStamp
				// + ", " + measurementName + ", " + instance + ", "
				// + status + ", " + valueList.toString() + ", "
				// + sf.getName() + ", " + vendorTag + ", " + suspectFlag
				// + ", " + sf.getDir() + ", " + JVM_TIMEZONE);

				MeasurementFile.saveData();
			}
		}

		// Logging
		if (verbose) {
			verboseStuffer.delete(0, 1);
			log.fine(verboseStuffer + "}" + name + "");
		}
	}

	/**
	 * @see com.ericsson.eniq.etl.axd.ASN1#primitive(int, java.lang.String,
	 *      byte[], com.ericsson.eniq.etl.axd.ASN1Rule)
	 */
	public void primitive(final int tagID, final String type,
			final byte[] data, final ASN1Rule rule) throws Exception {

		// Handle the primitive based on the current sequence
		if (seqName.equalsIgnoreCase("Header")) {
			// Sequence: File header

			if (rule.name.equalsIgnoreCase("version")) {
				version = "" + axdHandler.doInt(data);
			} else if (rule.name.equalsIgnoreCase("sender")) {
				sender = axdHandler.doString(rule, data);
			} else if (rule.name.equalsIgnoreCase("nodeType")) {
				nodeType = axdHandler.doString(rule, data);
			} else if (rule.name.equalsIgnoreCase("measType")) {
				measType = axdHandler.doString(rule, data);

				// Get the vendorTag from the measType in case defined by the
				// property. The measType value will be truncated to 50
				// characters and any commas will be replaced by underscore to
				// avoid ENIQ treating the vendorTag as two different comma
				// separated tags.
				if (!getVendorTagFromFileName) {
					vendorTag = measType.replace(',', '_').trim();
					if (measType.length() > 50) {
						vendorTag = vendorTag.substring(0, 50).trim();
					}
				}
			}

		} else if (seqName.equalsIgnoreCase("CounterData")) {
			// Sequence: Counters in the file header

			// Add the counter to the list
			counterList.add(axdHandler.doOid(data));

		} else if (seqName.equalsIgnoreCase("Pdr")) {
			// Sequence: Performance Data Report

			if (rule.name.equalsIgnoreCase("timeStamp")) {
				// Set the time stamp (epoch) and datetimeId.
				timeStamp = axdHandler.doInt(data);
				// Get the matching date for the epoch time stamp
				Date tmpDate = new Date(
						((Integer) timeStamp).longValue() * 1000);
				// Format and store the date time id.
				datetime_id = dateFormat.format(tmpDate);

				// Granularity period calculation commented out, since the
				// default 900 seconds will be set automatically.
				//
				// // Calculate the granularity period if there is a difference
				// in
				// // the time stamps between two PDRs.
				// if ((oldTimeStamp > 0) && ((timeStamp - oldTimeStamp) > 0)) {
				// // There is a time difference between the old and current
				// // time stamp. The granularity period (in seconds) can be
				// // (re)calculated.
				// granularityPeriod = timeStamp - oldTimeStamp;
				// }
				//
				// // Set the old time stamp for calculating the granularity
				// period
				// // in the next PDR.
				// oldTimeStamp = timeStamp;

			} else if (rule.name.equalsIgnoreCase("measurementName")) {
				measurementName = axdHandler.doString(rule, data);

			} else if (rule.name.equalsIgnoreCase("instance")) {
				instance = axdHandler.doString(rule, data);

			} else if (rule.name.equalsIgnoreCase("status")) {

				// Set the status value
				status = axdHandler.doInt(data);

				// Set the suspect flag value based on the status. If the status
				// is 1 (complete) then suspect flag is false, for any other
				// status value the flag will be true.
				if (status == 1) {
					suspectFlag = "false";
				} else
					suspectFlag = "true";
			}

		} else if (seqName.equalsIgnoreCase("ValueData")) {
			// Sequence: Value list in a performance data report

			// If the value is not empty, then add it to the list.
			if (data.length > 0) {
				valueList.add(axdHandler.doInt(data));
			}
		}

		// Logging
		if (verbose) {
			log.fine(verboseStuffer + "primitive[(" + rule.name + ") "
					+ rule.type + "] " + axdHandler.doData(rule, data) + " ");
		}

	}
	
	/**
	 * @see com.ericsson.eniq.etl.asn1#eof()
	 */
	public void eof() throws Exception {
		return;
	}
	
	/**
	 * @see com.distocraft.dc5000.etl.parser.Parser#status()
	 */
	public int status() {
		return parserStatus;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		try {

			this.parserStatus = 2;
			SourceFile sf = null;

			// Parse all available source files one at a time.
			while ((sf = mainParserObject.nextSourceFile()) != null) {

				// Execute the parse actions: preParse, parse, postParse. Also
				// errors are handled with errorParse and finallyParse.
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
			// Exception caught at top level. No good.
			log.log(Level.WARNING, "Worker parser failed to exception", e);
		} finally {
			this.parserStatus = 3;
		}
	}

	// ***************** Worker stuff ****************************

	/**
	 * 
	 * @see com.distocraft.dc5000.etl.parser.Parser#parse(com.distocraft.dc5000.etl.parser.SourceFile,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	public void parse(final SourceFile sf, final String techPack,
			final String setType, final String setName) throws Exception {

		// Initialization
		verboseStuffer = new StringBuffer();
		seqNameList = new ArrayList<String>();
		MeasurementFile = null;
		this.sf = sf;

		// Get all the other properties form the source file
		verbose = "TRUE".equalsIgnoreCase(sf.getProperty("verbose", "false"));
		getVendorTagFromFileName = "TRUE".equalsIgnoreCase(sf.getProperty(
				"getVendorTagFromFilename", "true"));
		final int bufferSize = Integer.parseInt(sf.getProperty("bufferSize",
				"100000"));
		final String dateFormatStr = sf.getProperty("datetimeIDFormat",
				defaultDateTimeIDFormat);
		dateFormat = new SimpleDateFormat(dateFormatStr);

		// Get the vendorTag from the file name if this is defined by the
		// property
		if (getVendorTagFromFileName) {
			// Get the vendorTag from the file name. It is the <Index>
			// consisting of
			// <measurementName>_<number>.
			String[] fnParts = sf.getName().split("_");
			vendorTag = fnParts[2] + "_" + fnParts[3];
		}

		// Since the node will always report the time stamps in UTC time, the
		// time zone of the date format will be set to UTC, instead of the local
		// time zone of the JVM.
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		// Get the parser handler and start parsing
		axdHandler = new AXDHandler(this);
		axdHandler.init(sf.getFileInputStream());
		axdHandler.setBuffer(bufferSize);

		// Note: The rules are set in the handler, because they are changed
		// between parsing the header and the pdrs.
		// axdHandler.setRules(rules);

		axdHandler.parse();
	}
}
