package com.ericsson.eniq.etl.RedbackParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.helpers.DefaultHandler;

import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.distocraft.dc5000.etl.parser.Parser;
import com.distocraft.dc5000.etl.parser.SourceFile;

/**
 * 
 * Adapter implementation that reads redback format ASCII xml measurement data.<br>
 * <br>
 * Redback xml data format:<br>
 * epochtime:value;slot:value;port:value;key 1:value 1;...;key n:value n<br>
 * (N/A) values are converted to null values<br>
 * <br>
 * <table border="1" width="100%" cellpadding="3" cellspacing="0">
 * <tr bgcolor="#CCCCFF" class="TableHeasingColor">
 * <td colspan="4"><font size="+2"><b>Parameter Summary</b></font></td>
 * </tr>
 * <tr>
 * <td><b>Name</b></td>
 * <td><b>Key</b></td>
 * <td><b>Description</b></td>
 * </tr>
 * <tr>
 * <td>TagID pattern</td>
 * <td>tag_id</td>
 * <td>Defines the predefined vendor ID for measurement type or defines regexp pattern<br>
 * that is used to parse vendor ID from the name of sourcefile or from the 1st header line of sourcefile.</td>
 * </tr>
 * <tr>
 * <td>TagID mode</td>
 * <td>tag_id_mode</td>
 * <td>Defines the discovery method of the vendor identification (tag_id).<br>
 * 0 = vendor ID is predefined in parameter named tag_id<br>
 * 1 = vendor ID is parsed from name of sourcefile using regexp pattern defined in parameter named tag_id<br>
 * 2 = vendor ID is parsed from 1st header line of sourcefile using regexp pattern defined in parameter named tag_id</td>
 * </tr>
 * <tr>
 * <td>File Name</td><td>filename</td>
 * <td>contains the filename of the inputdatafile.</td>
 * <tr>
 * <td>DC_SUSPECTFLAG</td><td>DC_SUSPECTFLAG</td>
 * <td>EMPTY</td>
 * </tr>
 * <tr>
 * <td>Directory</td><td>DIRNAME</td>
 * <td>Conatins full path to the input data file.</td>
 * </tr>
 * <tr>
 * <td>TimeZone</td><td>JVM_TIMEZONE</td>
 * <td>contains the JVM timezone (example. +0200) </td>
 * </tr>
 * <tr>
 * <td>DateTime</td><td>DATETIME_ID</td>
 * <td>contains the measurement datetime (yyyyMMddHHmmss)</td>
 * </tr>
 * </table>
 * @author lmfakos
 * 
 */

public class RedbackParser extends DefaultHandler implements Parser {

	private static final String JVM_TIMEZONE = new SimpleDateFormat("Z").format(new Date());
	private String block = "";
	private int bufferSize = 10000;
	private String filename;
	private Logger log;
	private Main mainParserObject = null;
	private String setName;
	private String setType;
	private int status = 0;
	private String techPack;
	private String workerName = "";

	// The group keys of data being parsed.
	private String[] groupKeys = {};
	
	//The group keys of the different policies:
	private final String[] GLOBAL_POLICY_KEYS = {"epochtime"};  
	private final String[] PORT_POLICY_KEYS = {"epochtime", "slot", "port"};   
	private final String[] CHANNEL_POLICY_KEYS = {"epochtime", "slot", "port", "channel"};
	private final String[] DOT1Q_POLICY_KEYS = {"epochtime", "slot", "port", "vlan_id"}; 
	private final String[] ATM_POLICY_KEYS = {"epochtime", "slot", "port", "vpi", "vci"}; 
	private final String[] FRAMERELAY_POLICY_KEYS = {"epochtime", "slot", "port", "channel"}; 
	private final String[] SUBSCRIBER_POLICY_KEYS = {"epochtime", "user_name"}; 
	private final String[] CONTEXT_POLICY_KEYS = {"epochtime", "context_name"}; 
	private final String[] LINKGROUP_POLICY_KEYS = {"epochtime", "description"};
	private final String[] EPS_APN_POLICY_KEYS = {"epochtime", "SgiApnIndex"};
	private final String[] EPS_CONTROL_POLICY_KEYS = {"epochtime"};
	private final String[] EPS_TRAFFIC_POLICY_KEYS = {"epochtime"};

	// This stores the PM data for one Redback PM file
	private Map<String,ArrayList<NameValuePair>> m_dataStoreMap = null;
	// edeamai: this stores the PM data of 1 ROP
	private Map<String,ArrayList<NameValuePair>> rop_dataStoreMap = null;
	
	// This will be set to policy name identified in PM filename.
	private String policy = "";


	/**
	 * 	Check that the group key ids are valid
	 * @param list
	 * @return
	 */
	private boolean checkGroupKeys(ArrayList<NameValuePair> list)
	{
		if(list.size() < groupKeys.length){
			return false;
		}
		boolean match = false;
		for(int i = 0; i < groupKeys.length; i++){
			String groupKey = list.get(i).m_name;
			for (int j = 0; j < groupKeys.length; j++){
				if(groupKey.equalsIgnoreCase(groupKeys[j])){
					match = true;
				}
				if(match==true)break;
			}
			if (match==false) return false;
			match = false;
		}
		return true;
	}

	/**
	 * 	Initialize parser
	 */
	@Override
	public void init(Main main, String techPack, String setType, String setName, String workerName) {

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

		log = Logger.getLogger("etl." + techPack + "." + setType + "."
				+ setName + ".parser.Redback" + logWorkerName);
	}

	/**
	 * Parser
	 */
	@Override
	public void parse(SourceFile sf, String techPack, String setType,
			String setName) throws Exception {

		this.filename = sf.getName();
		MeasurementFile mFile = null;
		block = "";
		BufferedReader br = null;
		int rowDelimLength = 1;
		final String colDelim = ";";
		final String rowDelim = "\n";
		final String fieldDelim = ":";
		
		
		String tag_id = sf.getProperty("tag_id", "(.+)");
		log.finest("vendorID: " + tag_id);

		int tag_id_mode = Integer.parseInt(sf.getProperty("tag_id_mode", "0"));
		log.finest("VendorID from: " + tag_id_mode);
		
		Pattern vendorPattern = null;
		
		try {
			if (tag_id_mode == 1) {
				vendorPattern = Pattern.compile(tag_id);
				final Matcher m = vendorPattern.matcher(filename);
				if (m.find()) {
					tag_id = m.group(1);
				}
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "Error while matching pattern " + tag_id
					+ " from filename " + filename + " for vendorId", e);
		}

		try {
			mFile = Main.createMeasurementFile(sf, tag_id, techPack, setType, setName, this.workerName, log);
			if (!mFile.isOpen()){
				log.severe("There was a problem preparing measurement file object - possibly due to invalid policy/schema name ("+tag_id+") extracted from source filename. " +
						"Regular expression used: "+vendorPattern.toString());
				throw new Exception("There was a problem preparing measurement file object");
			}
		}catch(Exception e){
			throw e;
		}
		
		
		setGroupKeys(tag_id);
		
		
			
		try {

			// Construct input reader for ascii file type
			final String charsetName = StaticProperties.getProperty("charsetName", null);
			
			InputStreamReader isr = null;
			if (charsetName == null) {
				isr = new InputStreamReader(sf.getFileInputStream());
			} else {
				log.log(Level.FINEST, "InputStreamReader charsetName: "	+ charsetName);
				isr = new InputStreamReader(sf.getFileInputStream(), charsetName);
			}
			log.log(Level.FINEST, "InputStreamReader Encoding: " + isr.getEncoding());
			br = new BufferedReader(isr);

			// Start parsing the input file
			log.fine("Parsing File: " + sf.getName());

			// Read header and check policy
			String header = readLine(rowDelim, br, rowDelimLength);
			if(header == null) {
				log.log(Level.WARNING, " Error reading header line from: " + this.filename);
			}
			else if(header.indexOf("policy_"+policy) == -1 ) {
				log.log(Level.WARNING, "Header (1st line of source file "+this.filename+") does not contain expected policy name.");

			}
			
			try {
				if (tag_id_mode == 2) {
					vendorPattern = Pattern.compile(tag_id);
					final Matcher m = vendorPattern.matcher(header);
					if (m.find()) {
						tag_id = m.group(1);
					}
				}
			} catch (Exception e) {

				log.log(Level.WARNING, "Error while matching pattern "
						+ tag_id + " from header " + header + " for vendorID", e);
			}

			String line;
			long lineNum = 0;
			Date epochdatetime = null;
			
			if(header.contains("epochtime")){
				line = header;
				log.warning("The 1st line of source file is not a header.");
			}else{
				line = readLine(rowDelim, br, rowDelimLength);
			}
			
			log.log(Level.FINE, "dataline: " + line);
			// While read line from file is not null
			while (line != null) {

				final String[] result = line.split(colDelim);
				ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();

				try {

					for (int i = 0; i < result.length; i++) {
						
						String field = result[i];
						log.log(Level.FINEST, "Field: " + field);

						//String[] items = field.split(fieldDelim);
						String key, value;
						if (field.contains("epochtime")){
							String[] items = field.split(fieldDelim);
							key = items[items.length - 2].trim();
							value = items[items.length - 1].trim();
						}
						else {
						key = field.substring(0, field.indexOf(fieldDelim)).trim();
						value = field.substring(field.indexOf(fieldDelim)+1).trim();
						}
						
						
						if(field.indexOf(fieldDelim) < 1) { 
							log.log(Level.WARNING, " Error while parsing data from: " + field);
							
						}
						else {
								
							list.add(new NameValuePair(key, value));
						}
					}
				} catch (Exception e) {

					log.log(Level.WARNING,
							"Error while parsing dataline, skipping(" + lineNum + "): " + line, e);
				}

				if(!checkGroupKeys(list)) {
					log.log(Level.WARNING, "Error in the group keys, skipping(" + lineNum	+ "): " + line);
				}
				else {
					// Add row into PM data store
					storeRow(list);
				}

				line = readLine(rowDelim, br, rowDelimLength);
				lineNum++;
			}
			
			Iterator<ArrayList<NameValuePair>>  ropRecords = rop_dataStoreMap.values().iterator();
			while(ropRecords.hasNext()){
				ArrayList<NameValuePair> record = ropRecords.next();
				StringBuffer keyBuff2 = new StringBuffer(groupKeys.length);
				for(NameValuePair a: record.subList(0, groupKeys.length)){
					keyBuff2.append(a.m_value);
				}
				String key2 = keyBuff2.toString();
				m_dataStoreMap.put(key2, record);
			}
			rop_dataStoreMap.clear();

			if (mFile.isOpen()) {
				Iterator<ArrayList<NameValuePair>>  iterator = m_dataStoreMap.values().iterator();
				String key_new   = "";
				String value_new = "";
				
				while (iterator.hasNext()) {
					ArrayList<NameValuePair> row = iterator.next();
					Iterator<NameValuePair> counters = row.iterator();
					while(counters.hasNext()){
						NameValuePair nvp = counters.next();
						key_new   = nvp.m_name;
						value_new = nvp.m_value;
						
						if(value_new != null) {
							if(value_new.equalsIgnoreCase("(N/A)")) {
								value_new = null;
							}
						}
						if(key_new.equalsIgnoreCase("epochtime")){
							epochdatetime = new Date(timeAdjust(value_new) * 1000);
						}
						mFile.addData(key_new, value_new);

						log.log(Level.FINEST, " data element: " + key_new + " = "
							+ value_new + " added to measurement file");
					}
					GregorianCalendar calTime = new GregorianCalendar();
					TimeZone tz = TimeZone.getTimeZone("UTC");
					calTime.setTimeZone(tz);
					calTime.setTime(epochdatetime);
					DecimalFormat f4 = new DecimalFormat("0000");
					DecimalFormat f2 = new DecimalFormat("00");
					String datetimeoutput = 
						f4.format(calTime.get(Calendar.YEAR)) +
						f2.format(calTime.get(Calendar.MONTH) + 1) +
						f2.format(calTime.get(Calendar.DAY_OF_MONTH)) +
						f2.format(calTime.get(Calendar.HOUR_OF_DAY)) +
						f2.format(calTime.get(Calendar.MINUTE)) +
						f2.format(calTime.get(Calendar.SECOND));
				
					mFile.addData("DATETIME_ID", datetimeoutput);
					mFile.addData("filename", sf.getName());
					mFile.addData("DC_SUSPECTFLAG", "");
					mFile.addData("DIRNAME", sf.getDir());
					mFile.addData("JVM_TIMEZONE", JVM_TIMEZONE);
					mFile.addData("vendorID", tag_id);
					mFile.saveData();
					
			    }

				//write file and clear data..
				m_dataStoreMap = null;
				mFile.close();
			}

		} catch (Exception e) {

			log.log(Level.WARNING, "General Failure", e);

		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					log.log(Level.WARNING, "Error closing Reader", e);
				}
			}

			if (mFile != null) {
				try {
					mFile.close();
				} catch (Exception e) {
					log.log(Level.WARNING, "Error closing MeasurementFile", e);
				}
			}
		}
	}

	/**
	 * This method checks the input parameter for a valid policy name, and sets the groupKeys class object to 
	 * the value of the corresponding hardcoded list of group keys. If no valid policy name is found and exception 
	 * is thrown, and parsing of file will not continue.
	 * @param tag_id String that should have a valid policy/schema name
	 * @throws Exception
	 */
	private void setGroupKeys(String tag_id) throws Exception {
		String partPolicyName = "PM_policy_";
		
		// Check the policy and set groupKeys accordingly
		if(tag_id.indexOf(partPolicyName+"global") >= 0) {
			groupKeys = this.GLOBAL_POLICY_KEYS;
		}
		else if(tag_id.indexOf(partPolicyName+"port") >= 0) {
			groupKeys = this.PORT_POLICY_KEYS;
		}
		else if(tag_id.indexOf(partPolicyName+"channel") >= 0) {
			groupKeys = this.CHANNEL_POLICY_KEYS;
		}
		else if(tag_id.indexOf(partPolicyName+"dot1q") >= 0) {
			groupKeys = this.DOT1Q_POLICY_KEYS;
		}
		else if(tag_id.indexOf(partPolicyName+"atm") >= 0) {
			groupKeys = this.ATM_POLICY_KEYS;
		}
		else if(tag_id.indexOf(partPolicyName+"fr") >= 0) {
			groupKeys = this.FRAMERELAY_POLICY_KEYS;
		}
		else if(tag_id.indexOf(partPolicyName+"sub") >= 0) {
			groupKeys = this.SUBSCRIBER_POLICY_KEYS;
		}
		else if(tag_id.indexOf(partPolicyName+"context") >= 0) {
			groupKeys = this.CONTEXT_POLICY_KEYS;
		}
		else if(tag_id.indexOf(partPolicyName+"linkgroup") >= 0) {
			groupKeys = this.LINKGROUP_POLICY_KEYS;
		}
		else if(tag_id.indexOf(partPolicyName+"eps_apn") >= 0) {
			groupKeys = this.EPS_APN_POLICY_KEYS;
		}
		else if(tag_id.indexOf(partPolicyName+"eps_ctrl") >= 0) {
			groupKeys = this.EPS_CONTROL_POLICY_KEYS;
		}
		else if(tag_id.indexOf(partPolicyName+"eps_traf") >= 0) {
			groupKeys = this.EPS_TRAFFIC_POLICY_KEYS;
		}
		else {
			//Parsing of this file cannot proceed without knowing what policy (schema) the file has. Exception thrown.
			log.severe("No valid Policy name ("+tag_id+")found in source filename " + this.filename + ". Parsing of this file will not proceed.");
			throw new Exception("No valid Policy name found in filename.");
		}
	}

	/**
	 * read characters from reader until eof or delimiter is encountered.
	 * 
	 * @param delimiter row delimiter
	 * @param br reader
	 * @param rowDelimLength row delim length
	 * @return read text line
	 * @throws Exception
	 */
	private String readLine(final String delimiter, BufferedReader br, int rowDelimLength) throws Exception {

		final char[] tmp = new char[bufferSize];

		while (true) {
			
			final String[] result = this.block.split(delimiter);
			
			// delimiter found
			if (result.length > 1) {

				// remove discovered token + deliminator from block
				block = block.substring(result[0].length() + rowDelimLength);

				log.log(Level.FINEST, "result: " + result[0]);

				// return found block
				return result[0];

			} else {

				// delimiter not found, read next block
				final int count = br.read(tmp, 0, bufferSize);

				// if end of file return whole block
				if (count == -1) {
					return null;
				}

				this.block += (new String(tmp));
			}
		}
	}

	
	/**
	 * Parser thread 
	 */
	@Override
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
			// Exception at top level
			log.log(Level.WARNING, "Worker parser failed to exception", e);
		} finally {
			this.status = 3;
		}
	}
	
	/**
	 * @return status of the parser
	 */
	@Override
	public int status()	{

		return status;
	}

	public long timeAdjust(String value) {
		long epochTime = Long.decode(value); //get epochtime as a double
		double divider = 900; //The num of sec in a ROP
		
		double multi = epochTime / divider; //The total num of ROPs since 1970 (or whenever it was)
		 
		long multiRound = (long)Math.floor(multi); //Round down to nearest integer (in other words round down the number of ROP).
		long adjusted = multiRound * 900; //Convert back  from num of ROPs to epochtime.
		return adjusted;
	}

	/**
	 * Repeated calls to this method collects parsed data into records in rop_dataStoreMap (rop_dataStoreMap is initialised if null), and ultimately copies the data to m_dataStoreMap. 
	 * rop_dataStoreMap is for collecting data from current ROP being processed, and m_dataStoreMap is for collecting multiple ROPs of data. If data submitted to this method is found to 
	 * belong to the next ROP (this is checked by examining epochtime value) this indicates that collection of current ROP has finished and next ROP has started, and so all records in 
	 * rop_dataStoreMap get transfered to m_dataStoreMap, and rop_dataStoreMap is set to null.
	 * 
	 * Before adding data to rop_dataStoreMap, this method checks it to see if it belongs to a record already in it (it does this check by way of unique key). If yes, then it adds the data 
	 * to the existing record (existing entry) in rop_dataStoreMap. If no, then it adds it to rop_dataStoreMap as a new record (new entry). 
	 * 
	 * The unique key mentioned above consists of the values of the group keys of policy (excluding epochtime key).
	 * @param list	This is an ArrayList of NameValuePair objects containing data parsed from a line read from a redback PM file. It might consist of a partial record of data or full record 
	 * (a record corresponds to 1 row in the table to which the data will ultimately be loaded).
	 */
	private void storeRow(ArrayList<NameValuePair> list) {
		//Make a key made up of a concatenation of all group keys (excluding epochtime).
		StringBuffer keyBuff = new StringBuffer(groupKeys.length-1);
		for(NameValuePair a: list.subList(1, groupKeys.length)){
			keyBuff.append(a.m_value);
		}
		String key = keyBuff.toString();
		
		if(m_dataStoreMap == null){
			m_dataStoreMap = new TreeMap<String, ArrayList<NameValuePair>>();
		}
		
		if(rop_dataStoreMap == null){
			rop_dataStoreMap = new TreeMap<String, ArrayList<NameValuePair>>();
			rop_dataStoreMap.put(key, list); //Add the first data of this rop to the rop store.
		}else{
			long foundEpochtime = Integer.parseInt(list.get(0).m_value);
			
			if(rop_dataStoreMap.containsKey(key)){
				//The group key values in this data (apart form epochtime) match an already found record in current ROP
				long storedEpochtime = Integer.parseInt(rop_dataStoreMap.get(key).get(0).m_value);
				if (foundEpochtime < storedEpochtime+900 && foundEpochtime > storedEpochtime-900){
					//This data belongs to current ROP. Adding it to existing entry in rop datastore.
					rop_dataStoreMap.get(key).addAll(list.subList(groupKeys.length, list.size()));
				}else{
					//We have come to the 1st line of data of the next ROP!!
					//Copy all records in rop_dataStoreMap to m_dataStoreMap.
					Iterator<ArrayList<NameValuePair>>  ropRecords = rop_dataStoreMap.values().iterator();
					while(ropRecords.hasNext()){
						ArrayList<NameValuePair> record = ropRecords.next();
						StringBuffer keyBuff2 = new StringBuffer(groupKeys.length);
						for(NameValuePair a: record.subList(0, groupKeys.length)){
							keyBuff2.append(a.m_value);
						}
						String key2 = keyBuff2.toString();
						m_dataStoreMap.put(key2, record);
					}
					//Clear the rop datastore (it will now be used for a new ROP) and add the new data to it.
					rop_dataStoreMap.clear();
					rop_dataStoreMap.put(key, list);
				}
			}else{
				//This data is a new found record in current ROP, add it as a new entry in rop datastore.
				rop_dataStoreMap.put(key, list);
			}
		}
	}	
}