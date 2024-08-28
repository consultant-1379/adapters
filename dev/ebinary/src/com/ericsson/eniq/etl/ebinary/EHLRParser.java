package com.ericsson.eniq.etl.ebinary;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.distocraft.dc5000.etl.parser.Parser;
import com.distocraft.dc5000.etl.parser.SourceFile;
import com.ericsson.eniq.etl.ebinary.fileformat.Block;
import com.ericsson.eniq.etl.ebinary.fileformat.Common;
import com.ericsson.eniq.etl.ebinary.fileformat.DataBlock;
import com.ericsson.eniq.etl.ebinary.fileformat.Row;

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
 * <td>pathToProperties</td>
 * <td>pathToProperties</td>
 * <td>This points to the folder, where all the config files lie. Path should
 * be given without last file separator. The folder have to include all the
 * config files. All the config files mean all the
 * data definitions which are to be parsed.</td>
 * <td>[CONF_DIR]/efnfParserConf</td>
 * </tr>
 * 
 * <tr>
 * <td>DATETIME_ID</td>
 * <td>DATETIME_ID</td>
 * <td>This gives a definition on how the DATETIME_ID is set.</td>
 * <td>20&lt;A13&gt;-&lt;A15&gt;-&lt;A17&gt; &lt;A19&gt;:&lt;A21&gt;:00</td>
 * </tr>
 * <tr>
 * <td>HEADER_PROP</td>
 * <td>HEADER_PROP</td>
 * <td>Name of the properties file of the header data</td>
 * <td>headerBlock.prop</td>
 * </tr>
 * <tr>
 * <td>TRANSLATIONAL_PROP</td>
 * <td>TRANSLATIONAL_PROP</td>
 * <td>Name of the properties file of the translationdata</td>
 * <td>headerBlock.prop</td>
 * </tr>
 * </table> <br>
 * <br>
 * <table border="1" width="100%" cellpadding="3" cellspacing="0">
 * <tr bgcolor="#CCCCFF" class="TableHeasingColor">
 * <td colspan="2"><font size="+2"><b>Added DataColumns</b></font> <br>
 * These are the columns that are added to each row.</td>
 * </tr>
 * <tr>
 * <td><b>Column name</b></td>
 * <td><b>Description</b></td>
 * </tr>
 * <tr>
 * <td>DIRNAME</td>
 * <td>Contains full path to the inputdatafile.</td>
 * </tr>
 * <tr>
 * <td>fileName</td>
 * <td>Name of the parsed file.</td>
 * </tr>
 * <tr>
 * <td>JVM_TIMEZONE</td>
 * <td>Timezone.</td>
 * </tr>
 * </table> <br>
 * <br>
 * 
 * @author etogust <br>
 *         <br>
 * 
 */
public class EHLRParser implements Parser {

	/**
	 * Part of the file, the header block. Here comes some information to all
	 * datarows
	 */
	private Block headerBlock;
	private Block footerBlock;

	private Block testBlock = null;
	/**
	 * Logger for logging
	 */
	private Logger log;

	private Map<String, MeasurementFile> measFileMap;

	private Integer FIRST_BYTE = null;

	private DataBlock dataBlock;

	// ***************** parameters ****************************

	private String PATHTOPROPERTIES;

	private Properties HEADER_PROP = null;

	private Properties FOOTER_PROP = null;

	private Properties TAGID_PROP = null;

	private String DATETIME_ID = null;

	// ***************** Worker stuff ****************************

	private String techPack;

	private String setType;

	private String setName;

	private int status = 0;

	private Main mainParserObject = null;

	private String workerName = "";


	/**
	 * Parser init implementation.
	 */
	public void init(Main main, String techPack, String setType,
			String setName, String wn) {
		StringBuilder sb = new StringBuilder();
		this.mainParserObject = main;
		this.techPack = techPack;
		this.setType = setType;
		this.setName = setName;
		this.status = 1;
		this.workerName = wn;

		String logWorkerName = "";
		if (workerName.length() > 0) {
			logWorkerName = "." + workerName;
		}
		
		sb.append("etl.");
		sb.append(techPack);
		sb.append(".");
		sb.append(setType);
		sb.append(".");
		sb.append(setName);
		sb.append(".parser.EHLRPARSER");
		sb.append(logWorkerName);
		log = Logger.getLogger(sb.toString());
		Common.log = log;
	}

	/**
	 * Parser status implementation
	 */
	public int status() {
		return status;
	}

	/**
	 * Runnable run implementation
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
					log.log (Level.INFO, "EHLRParser End of File :" + formatEndOfRecord() + ".");
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
	 * Implements parser parse
	 */
	public void parse(final SourceFile sf, final String techPack,
			final String setType, final String setName) throws Exception {

		StringBuilder sBuild = new StringBuilder();
		File dir = new File(System.getProperty("CONF_DIR"));

		sBuild.append(dir.getAbsolutePath());
		sBuild.append(File.separator);
		sBuild.append("ehlrParserConf");
		String path = sBuild.toString();
		
		PATHTOPROPERTIES = sf.getProperty("pathToProperties", path);
		DATETIME_ID = sf.getProperty("DATETIME_ID",
				"20<A0013>-<A0015>-<A0017> <A0019>:<A0021>:00");

		HEADER_PROP = Common.getInstructions(PATHTOPROPERTIES,
				"headerBlock.prop");
		FOOTER_PROP = Common.getInstructions(PATHTOPROPERTIES, "footerBlock.prop");
		TAGID_PROP = Common.getInstructions(PATHTOPROPERTIES,
				"recordTypes.prop");

		InputStream is = sf.getFileInputStream();
		try {
//			fileSizeInBytes = sf.getSize();
			readHeader(is);
			readSubscriberData(sf, is);
		} catch (Exception ex) {
			log.log(Level.WARNING, "Could not parse file.", ex);
			ex.printStackTrace();
		}

	}

	/**
	 * Read the file into sequense of records
	 * 
	 * @param bis
	 */
	private void readSubscriberData(SourceFile sf, InputStream is)
			throws Exception {

		int testNum = 0;
		int subscriberId = 0;

		try {
			dataBlock = new DataBlock();

			int firstOfRecord = is.read();
			//readBytes++;

			while (isValidBLockIdentifier(firstOfRecord)) {
				testNum++;

				MeasurementFile mFile = getMeasurementFile(sf, firstOfRecord);
				Block nextBlock = null;
				if(firstOfRecord == 51){
					log.log (Level.INFO, "End of File Found.");
					readFooter(firstOfRecord, is);
					break; //Stop the parsing once the end of file is found. 
				}else if (firstOfRecord != 50) {
					nextBlock = readSSData(firstOfRecord, is);
				}else {
					subscriberId++;
//					subscriberContent.clear();
					nextBlock = readSubsData(firstOfRecord, is);
				}
				
				SaveData(mFile, sf, nextBlock);
				dataBlock.clear();
				if (FIRST_BYTE != null)
					firstOfRecord = FIRST_BYTE;
				else {
					firstOfRecord = is.read();
//					readBytes++;
				}
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "Error while parsing translation data.("
					+ testNum + ")", e);
		}
		try {
			closeAllMeasurementFiles();
			cleanMyCache();
		} catch (Exception e) {
			log.log(Level.WARNING, "Error closing MeasurementFile", e);
		}

	}

	private boolean isValidBLockIdentifier(int firstOfRecord) {
		StringBuilder sType = new StringBuilder();
		sType.append(firstOfRecord);
		String tagId = (String) TAGID_PROP.get(sType.toString());

		if (tagId == null)
			return false;

		return true;
	}

	/**
	 * Gets measurementFile for the given tagId and sourceFile.
	 * 
	 * @param sf
	 * @param record_type
	 * @return
	 * @throws Exception
	 */
	private MeasurementFile getMeasurementFile(SourceFile sf, int record_type)
			throws Exception {
		if (measFileMap == null)
			measFileMap = new HashMap<String, MeasurementFile>();

		StringBuilder sType = new StringBuilder();
		sType.append(record_type);

		String tagId = (String) TAGID_PROP.get(sType.toString());
		if (tagId == null) {
			log
					.log(Level.SEVERE,
							"Unknown tagID, reading corrupted file or wrong configurations");
			throw new Exception("Cannot create measurementFile");
		}
		MeasurementFile mFile = measFileMap.get(tagId);
		if (mFile != null) {
			return mFile;
		}
		mFile = Main.createMeasurementFile(sf, tagId, techPack, setType,
				setName, this.workerName, this.log);
		measFileMap.put(tagId, mFile);
		return mFile;
	}

	/**
	 * Tries to close all the measurementFiles and logs warnings for those which
	 * fail
	 */
	private void closeAllMeasurementFiles() {
		if (measFileMap == null)
			return;

		MeasurementFile mFile;
		Set<String> keys = measFileMap.keySet();
		for (String key : keys) {
			mFile = measFileMap.get(key);
			try {
				mFile.close();
			} catch (Exception e) {
				log.log(Level.WARNING,
						"Error while trying to close measurementFile for tagId: "
								+ key + ".", e);
			}
		}
	}

	/**
	 * 
	 * @param firstByte
	 * @param is
	 * @throws Exception
	 */
	private Block readSubsData(int firstByte, InputStream is) throws Exception {

		// Need to create a Row Header. Is this the best place to do this?
		Properties blockProps = getBlockProperties(firstByte);
		// read bytes to arraylist, instead of normal array, because the size is
		// unknown at this point
		List<Integer> dynamicContent = new ArrayList<Integer>();
		List<Integer> dynamicBytes = new ArrayList<Integer>();
		dynamicContent.add(firstByte);

		Properties subsDataPropsRegenerated = new Properties();
		Set<Object> keys = blockProps.keySet();
		Object[] keyArr = keys.toArray();
		Arrays.sort(keyArr);

		for (Object key : keyArr) {
			String instructions = blockProps.getProperty(key.toString());
			// Just normal simple instructions
			if (dynamicBytes.size() == 0
					&& Common
							.isSimpleInstructions(key.toString(), instructions)) {
				String newInstr = instructions.replaceAll("DynStart",
						new Integer(dynamicContent.size()).toString());
				Common.readBytesToArray(dynamicContent, newInstr, is);
				subsDataPropsRegenerated.setProperty(key.toString(), newInstr);
//				readBytes += Integer.parseInt(newInstr.split(",")[1].trim());
				dataBlock.addKey((String) key, instructions);
				continue;
			}

			// multiply by N type of instructions
			if (key.toString().contains("*")) {
				Properties multiProps = new Properties();
				multiProps.load(new StringReader(instructions));
				String multiplierKey = multiProps.getProperty("Multiplier")
						.trim();

				int[] byteArr = new int[dynamicContent.size()];
				for (int i = 0; i < byteArr.length; i++)
					byteArr[i] = dynamicContent.get(i);
				String multiplier = Common.parseValue(subsDataPropsRegenerated
						.getProperty(multiplierKey), byteArr);
				int multiplierNum = Integer.parseInt(multiplier);

				Set<Object> multiKeys = multiProps.keySet();
				Object[] multiKeyArr = multiKeys.toArray();
				Arrays.sort(multiKeyArr);

				for (int j = 0; j < multiplierNum; j++) {
					for (Object multiKey : multiKeyArr) {
						if (multiKey.toString().equalsIgnoreCase("multiplier"))
							continue;
						StringBuilder sBuild = new StringBuilder(30);
						String multiInstr = multiProps.getProperty(multiKey
								.toString());
						String newInstr = multiInstr.replaceAll("DynStart",
								new Integer(dynamicContent.size()).toString());
						Common.readBytesToArray(dynamicContent, newInstr, is);
						sBuild.append(multiKey.toString());
						sBuild.append("_");
						sBuild.append(j);
						subsDataPropsRegenerated.setProperty(sBuild.toString(),
								newInstr);
//						readBytes += Integer.parseInt(newInstr.split(",")[1].trim());
						dataBlock.addKey(sBuild.toString(), newInstr);
					}
				}
				continue;
			}

			// Special handling for SUDs
			if (key.toString().equalsIgnoreCase("B0032")) {
				int SUDByte = is.read();
//				readBytes++;
				while (SUDByte != 255 && SUDByte != 254) {
					dynamicBytes.add(SUDByte);
					dynamicBytes.add(is.read());
					dynamicBytes.add(is.read());
					SUDByte = is.read();
//					readBytes += 3;
					if (SUDByte == -1)
						throw new Exception(
								"SubsData related error, error while handling data for SUDs");
				}
				dynamicBytes.add(SUDByte);
			}
			if (key.toString().equalsIgnoreCase("B0033")) {
				if (dynamicBytes.size() == 1)
					continue;
				int[] sudArray = new int[dynamicBytes.size()];
				for (int i = 0; i < sudArray.length; i++)
					sudArray[i] = dynamicBytes.get(i);

				int numberOfSUDs = (dynamicBytes.size() - 1) / 3;
				for (int i = 0; i < numberOfSUDs; i++) {
					int offset = i * 3;
					StringBuilder sBuild = new StringBuilder(20);
					sBuild.append(offset);
					sBuild.append(",");
					sBuild.append("1,Integer");
					String SUD = Common.parseValue(sBuild.toString(), sudArray);
					// String SUD = Common.parseValue(offset + "," +
					// "1,Integer", sudArray);
					String newKey = "SUD" + SUD;
					String newInstr = dynamicContent.size() + ",2,Integer,0";
					subsDataPropsRegenerated.setProperty(newKey, newInstr);
//					readBytes += Integer.parseInt(newInstr.split(",")[1].trim());
					dataBlock.addKey(newKey, newInstr);
					dynamicContent.add(dynamicBytes.get(offset + 1));
					dynamicContent.add(dynamicBytes.get(offset + 2));
				}
			}
			if (key.toString().equalsIgnoreCase("B0035")) {
				int nextByte = dynamicBytes.get(dynamicBytes.size() - 1);
				if (nextByte == 254) {
					String newInstr = dynamicContent.size() + ", 1, Integer,0";
					dynamicContent.add(nextByte);
					subsDataPropsRegenerated.setProperty(key.toString(),
							newInstr);
//					readBytes += Integer.parseInt(newInstr.split(",")[1].trim());
					dataBlock.addKey(key.toString(), newInstr);
					dynamicBytes.clear();
				} else if (nextByte == 255) {
					String newKey = "B0044";
					String newInstr = dynamicContent.size() + ", 1, Integer,0";
					dynamicContent.add(nextByte);
					subsDataPropsRegenerated.setProperty(newKey, newInstr);
//					readBytes += Integer.parseInt(newInstr.split(",")[1].trim());
					dataBlock.addKey(newKey, newInstr);
					break;
				} else
					throw new Exception(
							"SubsData related error, error while reading subsdata. Data is not formed as spesified");
			}
		}

		int[] byteArr = new int[dynamicContent.size()];
		for (int i = 0; i < byteArr.length; i++){
			byteArr[i] = dynamicContent.get(i);
		}
		testBlock = new Block(PATHTOPROPERTIES, subsDataPropsRegenerated,
				byteArr);
		return testBlock;

	}

	private Block readSSData(int firstByte, InputStream is) throws Exception {
		Properties blockProps = getBlockProperties(firstByte);
		// read bytes to arraylist, instead of normal array, because the size is
		// unknown at this point
		List<Integer> dynamicContent = new ArrayList<Integer>();

		// TODO: Catch the null pointer exception here, blockProps == null if
		// the property file is missing
		List<Integer> dynamicBytes = new ArrayList<Integer>();
		dynamicBytes.add(firstByte);

		Properties propsRegenerated = new Properties();
		Set<Object> keys = blockProps.keySet();
		Object[] keyArr = keys.toArray();
		Arrays.sort(keyArr);

		for (Object key : keyArr) {
			String instructions = blockProps.getProperty(key.toString());
			handleInstructions(key.toString(), instructions, dynamicContent,
					dynamicBytes, is, propsRegenerated);
		}

		int[] byteArr = new int[dynamicContent.size()];

		for (int i = 0; i < byteArr.length; i++) {
			byteArr[i] = dynamicContent.get(i);
		}

		FIRST_BYTE = (dynamicBytes.size() >= 1 ? dynamicBytes.get(0) : null);
		return new Block(PATHTOPROPERTIES, propsRegenerated, byteArr);
	}

	/**
	 * 
	 * @param sf
	 *            TODO
	 * @param key
	 * @param instructions
	 * @param dynamicContent
	 * @param dynamicBytes
	 * @param is
	 * @param propsRegenerated
	 * @param forward
	 *            TODO
	 * @throws Exception
	 */
	private void handleInstructions(String key, String instructions,
			List<Integer> dynamicContent, List<Integer> dynamicBytes,
			InputStream is, Properties propsRegenerated) throws Exception {
		int j = 2;
		// Just normal simple instructions
		if (Common.isSimpleInstructions(key.toString(), instructions)) {
			String newInstr = instructions.replaceAll("DynStart", new Integer(
					dynamicContent.size()).toString());
			dataBlock.addKey(key, instructions);
//			readBytes += Integer.parseInt(instructions.split(",")[1].trim());
			Common.readBytesToArray(dynamicContent, newInstr, is, dynamicBytes);
			propsRegenerated.setProperty(key, newInstr);
			return;
		} else if (key.toString().contains("+")) {// Multiply as long as
													// condition is valid
			if (Common.canBeSkipped(key, dynamicContent, propsRegenerated,
					PATHTOPROPERTIES)) {
				return;
			}
			handleDynamicMultiplier(key, instructions, dynamicContent,
					dynamicBytes, is, propsRegenerated);
			return;
		}

		// Simply multiply by N type of instructions
		if (key.toString().contains("*")) {
			handleMultiplier(key, instructions, dynamicContent, dynamicBytes,
					is, propsRegenerated);
			return;
		}
		int i = 0;
	}

	/**
	 * 
	 * @param sf
	 *            TODO
	 * @param instructions
	 * @param dynamicContent
	 * @param dynamicBytes
	 * @param is
	 * @param propsRegenerated
	 * @param forward
	 *            TODO
	 * @throws Exception
	 */
	private void handleMultiplier(String key, String instructions,
			List<Integer> dynamicContent, List<Integer> dynamicBytes,
			InputStream is, Properties propsRegenerated) throws Exception {
		Properties multiProps = Common.getInstructions(PATHTOPROPERTIES,
				instructions);
		String multiplierKey = multiProps.getProperty("Multiplier").trim();

		int[] byteArr = new int[dynamicContent.size()];
		for (int i = 0; i < byteArr.length; i++) {
			byteArr[i] = dynamicContent.get(i);
		}
		String multiplier = "0";
		String parentSuffix = "";

		try {
			// TODO: There is a problem here! need to pass the suffix into the
			// method.
			if (key.contains("_")) {
				parentSuffix = key.substring(key.indexOf("_"));
				multiplierKey = multiplierKey + parentSuffix;
			}
			if(multiplierKey.contains("+")){
				int multiply = 0;
				String[] keyParts = multiplierKey.split("\\+");
				for(String s:keyParts){
					multiply += Integer.parseInt(Common.parseValue(propsRegenerated
							.getProperty(s), byteArr));
				}
				multiplier = Integer.toString(multiply);
			}
			else{
				multiplier = Common.parseValue(propsRegenerated
						.getProperty(multiplierKey), byteArr);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not parse multiplier for: " + key
					+ ".");

		}
		int multiplierNum = Integer.parseInt(multiplier);

		Set<Object> multiKeys = multiProps.keySet();
		Object[] multiKeyArr = multiKeys.toArray();
		Arrays.sort(multiKeyArr);

		for (int j = 0; j < multiplierNum; j++) {
			for (Object multiKey : multiKeyArr) {
				if (multiKey.toString().equalsIgnoreCase("multiplier"))
					continue;

				String multiInstr = multiProps.getProperty(multiKey.toString());
				StringBuilder sBuild = new StringBuilder(11);
				sBuild.append(multiKey.toString());
				sBuild.append(parentSuffix);
				sBuild.append("_");
				sBuild.append(j);
				handleInstructions(sBuild.toString(), multiInstr,
						dynamicContent, dynamicBytes, is, propsRegenerated);
			}
		}
	}

	/**
	 * 
	 * @param sf
	 *            TODO
	 * @param instructions
	 * @param dynamicContent
	 * @param dynamicBytes
	 * @param is
	 * @param propsRegenerated
	 * @param forward
	 *            TODO
	 * @throws Exception
	 */
	private void handleDynamicMultiplier(String key, String instructions,
			List<Integer> dynamicContent, List<Integer> dynamicBytes,
			InputStream is, Properties propsRegenerated) throws Exception {

		// TODO:: handle suffix from given key
		Properties multiProps = Common.getInstructions(PATHTOPROPERTIES,
				instructions);
		String condition;
		Integer condInt = null;
		boolean doOnce = false;

		condition = multiProps.getProperty("Condition");
		if (condition == null) {
			// NB: if condition is null (i.e. no condition in properties file)
			// it indicates this set of keys is only to be parsed once.
			condInt = -1;
			doOnce = true;
		} else {
			condition.trim();
			condInt = new Integer(condition);
		}

		int nextByte= 0;
		if (dynamicBytes.size() >= 1) {
			nextByte = dynamicBytes.get(0);
		} else {
			nextByte = is.read();
//			readBytes++;
		}

		if (doOnce == false) {
			if (nextByte != condInt) {
				if (dynamicBytes.size() == 0)
					dynamicBytes.add(nextByte);
				return;
			}
		}

		int leng = Common.parseLeng(multiProps);
		Set<Object> multiKeys = multiProps.keySet();
		Object[] multiKeyArr = multiKeys.toArray();
		Arrays.sort(multiKeyArr);

		String parentSuffix = "";
		if (key.contains("_")) {
			parentSuffix = key.substring(key.indexOf("_"));
		}

		int suffix = 0;

		// execute while loop (parse key set) if condition is met or if key set
		// is to be parsed once
		while ((nextByte == condInt) || (doOnce == true)) {
			if (dynamicBytes.size() == 0) {
				dynamicBytes.add(nextByte);
			}
			while (dynamicBytes.size() < leng) {
				dynamicBytes.add(is.read());
//				readBytes++;
			}

			for (Object multiKey : multiKeyArr) {
				if (multiKey.toString().equalsIgnoreCase("Condition"))
					continue;
				StringBuilder sBuild = new StringBuilder(11);
				sBuild.append(multiKey.toString());
				sBuild.append(parentSuffix);
				sBuild.append("_");
				sBuild.append(suffix);

				String subInstr = multiProps.getProperty(multiKey.toString());
				handleInstructions(sBuild.toString(), subInstr, dynamicContent,
						dynamicBytes, is, propsRegenerated); // KEN1 Comment

			}
			suffix++;
			nextByte = (dynamicBytes.size() >= 1) ? dynamicBytes.get(0) : is
					.read();

			if (doOnce == true) {
				break; // break from this while loop as this key set is only to
						// be parsed once.
			}
		}
		if (dynamicBytes.size() == 0)
			dynamicBytes.add(nextByte);
	}

	/**
	 * 
	 * @param firstByte
	 * @return
	 */
	private Properties getBlockProperties(int firstByte) {
		Properties props = null;
		try {
			StringBuilder sType = new StringBuilder();
			sType.append(firstByte);
			sType.append("_prop");

			String propFile = (String) TAGID_PROP.get(sType.toString());
			props = Common.getInstructions(PATHTOPROPERTIES, propFile);
			if (props == null) {
				log.log(Level.SEVERE,
						"Could not find properties for blockType " + firstByte
								+ ".");
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not find properties for blockType "
					+ firstByte + ".", e);
		}
		return props;
	}

	/**
	 * TODO: Exception handling
	 * 
	 * @param is
	 * @throws Exception
	 */
	private void readHeader(InputStream is) throws Exception {
		try {
			int leng = Common.parseLeng(HEADER_PROP);
			int[] content = new int[leng];
			for (int i = 0; i < leng; i++) {
				content[i] = is.read();
//				readBytes++;
			}
			headerBlock = new Block(PATHTOPROPERTIES, HEADER_PROP, content);
		} catch (Exception e) {
			log.warning("Error while parsing header data");
		}
	}

	private void readFooter(int firstOfRecord, InputStream is) throws Exception {
		try {
			int leng = Common.parseLeng(FOOTER_PROP);
			int[] content = new int[leng];
			content[0] = firstOfRecord;
			for (int i = 1; i < leng; i++) {
				content[i] = is.read();
//				readBytes++;
			}
			footerBlock = new Block(PATHTOPROPERTIES, FOOTER_PROP, content);
		} catch (Exception e) {
			log.warning("Error while parsing footer data");
		}	
		FIRST_BYTE = null;
	}
	/**
	 * Cleans all the data which were cached
	 * 
	 */
	private void cleanMyCache() {
		headerBlock = null;
		DATETIME_ID = null;
	}

	/**
	 * Formats the DATETIME_ID according
	 * 
	 * @return
	 */
	String formatDateTimeID() {
		String datetimeID = DATETIME_ID;
		try {
			Map<String, String> headerValues = headerBlock.getSimpleValues();
			String[] parts = DATETIME_ID.split(">");

			for (String part : parts) {
				if (part.indexOf('<') == -1)
					continue;
				String key = part.substring(part.indexOf('<') + 1);
				StringBuilder sBuild = new StringBuilder(20);
				sBuild.append("<");
				sBuild.append(key);
				sBuild.append(">");
				String value = headerValues.get(key);
				datetimeID = datetimeID.replaceAll(sBuild.toString(), value);
			}

		} catch (Exception e) {
			log.info("Error while trying to format DATETIME_ID.");
			datetimeID = null;
		}
		return datetimeID;
	}

	String formatEndOfRecord(){
		StringBuilder sBuild = new StringBuilder(20);
		try {
			Map<String, String> footerValues = footerBlock.getSimpleValues();
			sBuild.append("Date (dd/mm/yy : hh:mm): ");
			sBuild.append(footerValues.get("A0037")); //Day
			sBuild.append("/");
			sBuild.append(footerValues.get("A0035")); //Month
			sBuild.append("/");
			sBuild.append(footerValues.get("A0033")); //Year
			sBuild.append(" : ");
			sBuild.append(footerValues.get("A0039")); //Hour
			sBuild.append(":");
			sBuild.append(footerValues.get("A0041")); //Hour
			
			if(footerValues.get("A0043").equals("48")){
				sBuild.append(" - Normal End");
			}else{
				sBuild.append(" - Interrupted by operator");
			}
		} catch (Exception e) {
			log.info("Error while trying to format End of Record.");
		}
		return sBuild.toString();
	}
	/**
	 * Saves the data from cached temp map.
	 * 
	 * @param sf
	 */
	private void SaveData(MeasurementFile mFile, SourceFile sf,
			Block translationalData) {

		// These are the "added columns", which will be added to each row
		Map<String, String> addedColumns = new HashMap<String, String>();
		addedColumns.put("filename", sf.getName());
		SimpleDateFormat sdf = new SimpleDateFormat("Z");
		addedColumns.put("JVM_TIMEZONE", sdf.format(new Date()));
		addedColumns.put("DIRNAME", sf.getDir());
		addedColumns.put("DATETIME_ID", formatDateTimeID());

		log.fine("Added columns are: " + addedColumns);
		//Check for duplicates in the rows...
//		boolean duplicate = dataBlock.duplicateCheck();
//		if(duplicate){
//			log.info("Duplicate detected in this dataBlock: "+dataBlock.toString());
//		}
		
		for (Row row : dataBlock.flatten()) {
			try {
				mFile.addData(addedColumns);

				Map<String, String> headerValues = headerBlock
						.getSimpleValues();
				mFile.addData(headerValues);

				if (testBlock != null) {
					Map<String, String> rowHeaderValues = testBlock
							.getSimpleValues();
					mFile.addData(rowHeaderValues);
				}

				String key = "";
				String value = "";
				Map<String, String> translationalValues = translationalData
						.getSimpleValues();
				Map<String, String> translationalValues2 = new HashMap<String, String>();
				final Iterator<String> iter = translationalValues.keySet()
						.iterator();

				while (iter.hasNext()) {
					final String translationalKey = (String) iter.next();
					for (int i = 0; i < row.size(); i++) {
						if (translationalKey.contains(row.get(i))) {
							// need to remove all suffix values, but keep the A,
							// B, C that might be there from the Binary
							// conversion.
							value = translationalValues.get(translationalKey);
							if (translationalKey.contains("_")) {
								int index = translationalKey.indexOf("_");
								key = translationalKey.substring(0, index);
							} else {
								key = translationalKey;
							}

							// key = translationalKey.substring(0,5);
							if (translationalKey.endsWith("A")) {
								key = key + "A";
							} else if (translationalKey.endsWith("B")) {
								key = key + "B";
							} else if (translationalKey.endsWith("C")) {
								key = key + "C";
							} else {
								row.remove(i);
							}
							break;
						}
					}
					translationalValues2.put(key, value);
				}
				mFile.addData(translationalValues2);				
				mFile.saveData();

			} catch (Exception e) {
				log
						.log(
								Level.WARNING,
								"Worker parser failed to exception while trying to save data ",
								e);
			}
		}
	}
}
