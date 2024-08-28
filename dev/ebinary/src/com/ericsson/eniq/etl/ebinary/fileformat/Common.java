package com.ericsson.eniq.etl.ebinary.fileformat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a class holding static methods used by binary parsing
 * 
 * @author etogust
 * 
 */
public class Common {

  /**
   * Map to cache all the data definitions for blocks counters and entity
   * selector values
   */
  private static final Map<String, Properties> DATADEFTABLE = new HashMap<String, Properties>();

  private static final String[] SUPPORTEDTYPES = { "String", "HexString", "Integer", "Binary", "BinaryInt", "BCDNumber",
      "TBCDString" };

  /**
   * logger
   */
  public static Logger log = null;

  /**
   * This class gets never constructed. This only holds static methods used by
   * binary parsing
   * 
   */
  private Common() {
    // Cannot be constructed
  }

  /**
   * Gets the instructions for the system record header.
   * 
   * @return Properties
   */
  static public Properties getInstructions(String path, String propFileName) {
	  synchronized (DATADEFTABLE) {
	
		String fullName = path + File.separator + propFileName;
		Properties prop = DATADEFTABLE.get(fullName);
		if (prop != null)
		  return prop;
		
		try {
		  FileInputStream fis = new FileInputStream(fullName);
		  prop = new Properties();
		  prop.load(fis);
		  fis.close();
		  DATADEFTABLE.put(propFileName, prop);
		  return prop;
		} catch (Exception e) {
		
		  if (log != null)
		    log.severe("Could not load properties " + propFileName + " from folder " + path);
		
		}	
		return null;
	}
  }

  /**
   * 
   */
  @SuppressWarnings("unchecked")
  public static void putDataToMap(String path, Hashtable prop, Map<String, String> map, int[] byteArray,
      String keyPrefix) {
    if (keyPrefix == null)
      keyPrefix = "";

    Set keys = prop.keySet();
    String instructions = "";
    String valuekey = "";
    try {
      for (Object key : keys) {
        valuekey = (String) key;
        instructions = (String) prop.get(key);
        if (!needsMoreInstructions(instructions)) {
        	if(key.toString().length() > 1){//fix: keyPrefix is not working correctly for binary tables.
        		keyPrefix = "";
        	}
          String value = parseValue(instructions, byteArray);
          map.put(((String) keyPrefix + key).toUpperCase(), value);
        } else {
          keyPrefix = valuekey;
          Properties tableProps = getInstructions(path, instructions.split(",")[2].trim());
          int[] subByteArray = parseValueBytes(byteArray, instructions);
          putDataToMap(path, tableProps, map, subByteArray, keyPrefix);
        }
      }
    } catch (Exception ex) {
      String msg = "Error while tried to parse " + valuekey + " with instructions " + instructions + ". ";

      if (log != null) {
        log.warning(msg + ex.getMessage());
      }
      ex.printStackTrace();
    }

  }

  /**
   * This method lets us know whether instructions are enough or do we need to
   * load more props
   * 
   * @param instructionsString
   * @return
   */
  static private boolean needsMoreInstructions(String instructionsString) {
    String[] splittedInstructions = instructionsString.split(",");
    String dataType = splittedInstructions[2].trim();
    for (String dt : SUPPORTEDTYPES) {
      if (dt.equalsIgnoreCase(dataType))
        return false;
    }

    return true;
  }

  /**
   * This method puts vector data to given map. Depending on instructions, table
   * can be splitted to counters for each column of the table or for each row of
   * the table.
   * 
   * @param prop
   * @param map
   * @param byteArray
   */
  static public void putTableDataToMap(Hashtable<String, String> prop, Map<String, String> map, int[] byteArray) {

    Set<String> keys = prop.keySet();
    String instructions = "";
    String valuekey = "";
    try {

      for (String key : keys) {
        instructions = prop.get(key);
        valuekey = key;
        String[] parts = instructions.split(",");
        int offSet = Integer.parseInt(parts[0].trim());
        int leng = Integer.parseInt(parts[1].trim());
        String byteOrder = (parts.length > 2) ? parts[2].trim() : "";
        String tableInstr = (parts.length > 3) ? parts[3].trim() : "";
        String chunksInstr = (parts.length > 4) ? parts[4].trim() : "";

        boolean splitOnX = tableInstr.contains("x") | tableInstr.contains("X");
        String[] tableSpecs = splitOnX ? tableInstr.split("[Xx]") : tableInstr.split("[Yy]");
        int xCnt = Integer.parseInt(tableSpecs[0].trim());
        int yCnt = Integer.parseInt(tableSpecs[1].trim());
        int counterCnt = splitOnX ? xCnt : yCnt;
        int valueCnt = splitOnX ? yCnt : xCnt;

        int os = offSet;
        int l = leng / (xCnt * yCnt);
        for (int i = 0; i < counterCnt; i++) {
          String counterName = key + "_" + (i + 1);
          os = splitOnX ? offSet + (i * l) : os;
          for (int j = 0; j < valueCnt; j++) {

            String inst = "" + os + "," + l + "," + byteOrder + ",," + chunksInstr;
            String value = parseValue(inst, byteArray);
            String existingValue = map.get(counterName);
            if (existingValue != null)
              value = existingValue + "," + value;

            map.put(counterName.toUpperCase(), value);
            os += splitOnX ? (l * xCnt) : l;
          }
        }

      }
    } catch (Exception e) {
      String msg = "Error while tried to parse value from table: " + valuekey + " with instructions " + instructions
          + ". ";

      if (log != null) {
        log.warning(msg + e.getMessage());
      }
      e.printStackTrace();
    }
  }

  static public int getDataIndex(String instructions){
	String[] indexString = instructions.split(",");
	int returnValue = 0;
	if(indexString.length > 3){
		returnValue = Integer.parseInt(indexString[3].trim());
	}
	return returnValue;
  }
  
  /**
   * Parses value from byteArray based on given instructions.
   * 
   * @param instructions
   * @param byteArray
   * @return
   */
  static public String parseValue(String instructions, int[] byteArray) throws Exception {
    String dataType = instructions.split(",")[2].trim();

    // Note that here we are parsing from original byteArray
    if (dataType.equalsIgnoreCase("Binary")) {
      return parseBinary(byteArray, instructions);
    }
    if (dataType.equalsIgnoreCase("BinaryInt")) {
      return parseBinaryInt(byteArray, instructions);
    }

    // Get the actual value bytes for this key
    int[] valArr = parseValueBytes(byteArray, instructions);

    if (dataType.equalsIgnoreCase("Integer"))
      return parseInteger(valArr);

    if (dataType.equalsIgnoreCase("String"))
      return parseString(valArr);

    if (dataType.equalsIgnoreCase("HexString"))
      return parseHexString(valArr);

    if (dataType.equalsIgnoreCase("BCDNumber"))
      return parseBCDNumber(valArr);

    if (dataType.equalsIgnoreCase("TBCDString"))
      return parseTBCDString(valArr);

    return "";

  }

  static public int[] parseValueBytes(int[] byteArray, String instructions) {
    String[] instParts = instructions.split(",");
    int offSet = Integer.parseInt(instParts[0].trim());
    int leng = 0;
    if (instParts[1].trim().equalsIgnoreCase("MAX"))
      leng = byteArray.length - offSet;
    else
      leng = Integer.parseInt(instParts[1].trim());

    // Get the actual value bytes for this key
    int[] valArr = new int[leng];
    for (int i = 0; i < leng; i++) {
      if (i + offSet >= byteArray.length) {
        log.info("Instructions exceed block length, cannot parse value.");
        return null;
      }
      valArr[i] = byteArray[i + offSet];
    }
    return valArr;
  }

  /**
   * Parses binary values from bytes
   */
  static public String parseBinary(int[] byteArr, String instructions) {
    String[] instParts = instructions.split(",");
    int offSet = Integer.parseInt(instParts[0].trim());
    int leng = Integer.parseInt(instParts[1].trim());

    StringBuilder sb = new StringBuilder();
    StringBuilder tmp = new StringBuilder();
    for (int i = 0; i < byteArr.length; i++) {
      tmp.append(Integer.toBinaryString(byteArr[i]));
      while (tmp.length() < 8)
        tmp.insert(0, "0");
      sb.append(tmp);
      tmp = new StringBuilder();
    }

    String arrAsStr = sb.toString();
    return arrAsStr.substring(offSet, offSet + leng);
  }

  private static String parseBinaryInt(int[] byteArray, String instructions) {
    String bits = parseBinary(byteArray, instructions);
    int retVal = Integer.parseInt(bits, 2);
    return new Integer(retVal).toString();
  }

  static public String XXXparseValue(String instructions, int[] byteArray) throws Exception {
    String[] instParts = instructions.split(",");
    int offSet = Integer.parseInt(instParts[0].trim());
    int leng = Integer.parseInt(instParts[1].trim());
    String byteOrder = (instParts.length > 2) ? instParts[2].trim() : "";
    String chunksInstr = (instParts.length > 4) ? instParts[4].trim() : "";

    if (!chunksInstr.equals("")) {
      String[] chunkNameList = chunksInstr.split(";");
      StringBuilder retVal = new StringBuilder("");
      int os = offSet;
      int l = leng / chunkNameList.length;
      for (int i = 0; i < chunkNameList.length; i++) {
        if (i != 0)
          retVal.append(";");
        retVal.append(chunkNameList[i] + "=");
        String ci = os + "," + l + "," + byteOrder;
        retVal.append(parseValue(ci, byteArray));
        os += l;
      }
      return retVal.toString();
    }

    int[] valArr = new int[leng];
    for (int i = 0; i < leng; i++) {
      if (i + offSet >= byteArray.length) {
        if (log != null)
          log.info("Instructions exceed block length, cannot parse value.");
        return "";
      }
      valArr[i] = byteArray[i + offSet];
    }

    if (leng == 1)
      return Integer.toString(valArr[0]);

    if (leng == 2) {
      return parse2ByteValue(valArr, byteOrder);
    }

    if (leng == 4) {
      return parse4ByteValue(valArr, byteOrder);
    }

    if (leng == 6) {
      return parse6ByteValue(valArr, byteOrder);
    }

    if (leng == 16) {
      return parse16ByteValue(valArr);
    }

    StringBuilder sb = new StringBuilder();
    for (int val : valArr)
      sb.append(Integer.toHexString(val));

    return sb.toString();
  }

  /**
   * Parses value from 2 bytes array. Order can be given as parameter to tell
   * which byte is LSB and which one is MSB. (Most Significant Byte, Least
   * Significant Byte)
   * 
   * @param byteArray
   * @param byteOrder
   * @return
   */
  static String parse2ByteValue(int[] byteArray, String byteOrder) {
    if (byteOrder.trim().equals(""))
      byteOrder = "LSB;MSB";

    String[] bytes = byteOrder.split(";");
    List<String> byteList = new ArrayList<String>();
    for (String str : bytes)
      byteList.add(str.trim());

    int LSBIndex = byteList.indexOf("LSB");
    int MSBIndex = byteList.indexOf("MSB");

    int LSB = byteArray[LSBIndex];
    int MSB = byteArray[MSBIndex];

    long retVal = MSB;
    retVal = retVal << 8;
    retVal |= LSB;
    return Long.toString(retVal);
  }

  /**
   * Parses String value from given byteArray
   * 
   * @param byteArray
   * @return
   */
  static String parseString(int[] byteArray) {
    StringBuilder sb = new StringBuilder();
    for (int oneByte : byteArray)
      sb.append((char) (oneByte));

    return sb.toString();
  }

  /**
   * Parses String value from given byteArray, where each charachter is stored
   * in 4 bits (halfbyte)
   * 
   * @param byteArray
   * @return
   */
  static String parseHexString(int[] byteArr) {
    StringBuilder sb = new StringBuilder();
    StringBuilder tmp = new StringBuilder();
    for (int i = 0; i < byteArr.length; i++) {
      tmp.append(Integer.toHexString(byteArr[i]));
      while (tmp.length() < 2)
        tmp.insert(0, "0");
      sb.append(tmp);
      tmp = new StringBuilder();
    }
    return sb.toString();
  }

  /**
   * Parses String value from given byteArray, where each charachter is stored
   * in 4 bits (halfbyte) and is filled with f values
   * 
   * @param byteArray
   * @return
   */
  static String parseBCDNumber(int[] byteArr) {
    String hexString = parseHexString(byteArr);
    return hexString.replaceAll("[a-z]", "");
  }

  private static String parseTBCDString(int[] byteArr) {
    String hexString = parseHexString(byteArr);
    return hexString.replaceAll("f", "");
  }

  /**
   * parses Integer value from the given bytes
   * 
   * @param byteArray
   * @return
   */
  static String parseInteger(int[] byteArray) {
    long retVal = 0;

    for (int oneByte : byteArray) {
      retVal <<= 8;
      retVal |= oneByte;
    }
    return Long.toString(retVal);
  }

  /**
   * Parses value from 4 bytes array. Order can be given as parameter to tell
   * which byte is MSW_LSB, MSW_MSB, LSW_LSB and LSW_MSB. (Most Significant
   * Words's, Least Significant Byte etc.)
   * 
   * @param valArr
   * @param byteOrder
   * @return
   */
  static String parse4ByteValue(int[] valArr, String byteOrder) {
    if (byteOrder.trim().equals(""))
      byteOrder = "MSW_LSB;MSW_MSB;LSW_LSB;LSW_MSB";

    String[] bytes = byteOrder.split(";");
    List<String> byteList = new ArrayList<String>();
    for (String str : bytes)
      byteList.add(str.trim());

    int MSW_LSB_index = byteList.indexOf("MSW_LSB");
    int MSW_MSB_index = byteList.indexOf("MSW_MSB");
    int LSW_LSB_index = byteList.indexOf("LSW_LSB");
    int LSW_MSB_index = byteList.indexOf("LSW_MSB");

    int MSW_LSB = valArr[MSW_LSB_index];
    int MSW_MSB = valArr[MSW_MSB_index];
    int LSW_LSB = valArr[LSW_LSB_index];
    int LSW_MSB = valArr[LSW_MSB_index];

    long MSW = MSW_MSB;
    MSW = MSW << 8;
    MSW |= MSW_LSB;

    int LSW = LSW_MSB;
    LSW = LSW << 8;
    LSW |= LSW_LSB;

    long retVal = MSW << 16;
    retVal |= LSW;

    return Long.toString(retVal);
  }

  /**
   * Parses value from 6 bytes array. Order can be given as parameter to tell
   * which byte is MSW_LSB;MSW_MSB;MW_LSB;MW_MSB;LSW_LSB;LSW_MSB. (Most
   * Significant Words's, Least Significant Byte etc. MW means the meaningly
   * middle word here.)
   * 
   * @param valArr
   * @param byteOrder
   * @return
   */
  static String parse6ByteValue(int[] valArr, String byteOrder) {
    if (byteOrder.trim().equals(""))
      byteOrder = "MSW_LSB;MSW_MSB;MW_LSB;MW_MSB;LSW_LSB;LSW_MSB";

    String[] bytes = byteOrder.split(";");
    List<String> byteList = new ArrayList<String>();
    for (String str : bytes)
      byteList.add(str.trim());

    int MSW_LSB_index = byteList.indexOf("MSW_LSB");
    int MSW_MSB_index = byteList.indexOf("MSW_MSB");
    int MW_LSB_index = byteList.indexOf("MW_LSB");
    int MW_MSB_index = byteList.indexOf("MW_MSB");
    int LSW_LSB_index = byteList.indexOf("LSW_LSB");
    int LSW_MSB_index = byteList.indexOf("LSW_MSB");

    int MSW_LSB = valArr[MSW_LSB_index];
    int MSW_MSB = valArr[MSW_MSB_index];

    int MW_LSB = valArr[MW_LSB_index];
    int MW_MSB = valArr[MW_MSB_index];

    int LSW_LSB = valArr[LSW_LSB_index];
    int LSW_MSB = valArr[LSW_MSB_index];

    long MSW = MSW_MSB;
    MSW = MSW << 8;
    MSW |= MSW_LSB;

    long MW = MW_MSB;
    MW = MW << 8;
    MW |= MW_LSB;

    int LSW = LSW_MSB;
    LSW = LSW << 8;
    LSW |= LSW_LSB;

    long retVal = MSW << 16;
    retVal |= MW;
    retVal = retVal << 16;
    retVal |= LSW;

    return Long.toString(retVal);
  }

  /**
   * Parses date->16 byte value is date, described in 16 chars.
   * 
   * @param valArr
   * @return
   */
  static String parse16ByteValue(int[] valArr) {
    StringBuilder time = new StringBuilder();
    for (int i : valArr)
      time.append((char) i);

    return time.toString();
  }

  /**
   * Parses the max leng from properties, which are format key = <offset>,<leng>
   * 
   * @param prop
   * @return
   */
  @SuppressWarnings("unchecked")
  public static int parseLeng(Properties prop) {
    if (prop == null)
      return 0;

    Set keys = prop.keySet();
    int leng = 0;
    for (Object key : keys) {
      if (key.toString().contains("+"))
        continue;
      String instructions = prop.getProperty((String) key);
      String[] instArr = instructions.split(",");
      if (instArr.length < 2)
        continue;
      leng += Integer.parseInt(instArr[1].trim());
    }

    return leng;
  }

  /**
   * Decides whether the given instructions are simple in format
   * "key = offset, leng, type"
   * 
   * @param instructions
   * @return
   */
  public static boolean isSimpleInstructions(String key, String instructions) {

    if (key.contains("*"))
      return false;

    if (key.contains("+"))
      return false;

    String[] parts = instructions.split(",");
    try {
      Integer.parseInt(parts[1].trim());
    } catch (Exception e) {
      return false;
    }

    return true;
  }

  /**
   * 
   * @param dynamicContent
   * @param instructions
   * @param is
   * @throws IOException
   */
  public static void readBytesToArray(List<Integer> dynamicContent, String instructions, InputStream is)
      throws IOException {
    String[] parts = instructions.split(",");
    int leng = Integer.parseInt(parts[1].trim());
    int offset = Integer.parseInt(parts[0].trim());
    if (offset + leng <= dynamicContent.size())
      return;

    for (int i = 0; i < leng; i++)
      dynamicContent.add(is.read());
  }

  /**
   * 
   * @param dynamicContent
   * @param newInstr
   * @param dynamicBytes
   */
  public static void readBytesToArray(List<Integer> dynamicContent, String newInstr, List<Integer> dynamicBytes) {
    String[] parts = newInstr.split(",");
    int leng = Integer.parseInt(parts[1].trim());
    int offset = Integer.parseInt(parts[0].trim());
    if (offset + leng <= dynamicContent.size())
      return;

    for (int i = 0; i < leng; i++)
      dynamicContent.add(dynamicBytes.remove(i));

  }

  /**
   * 
   * @param dynamicContent
   * @param newInstr
   * @param is
   * @param dynamicBytes
   * @throws IOException
   */
  public static void readBytesToArray(List<Integer> dynamicContent, String newInstr, InputStream is,
      List<Integer> dynamicBytes) throws IOException {
    String[] parts = newInstr.split(",");
    int leng = Integer.parseInt(parts[1].trim());
    int offset = Integer.parseInt(parts[0].trim());
    if (offset + leng <= dynamicContent.size()){
        return;   	
    }
    int i = 0;

    for (; i < leng; i++) {
      if (dynamicBytes.size() == 0)
        break;
//      System.out.println("VALUE = "+dynamicBytes.get(0));
      dynamicContent.add(dynamicBytes.remove(0));
    }
    int x = 0;
    for (; i < leng; i++){
    	x = is.read();
      dynamicContent.add(x);
//      System.out.println("VALUE = "+x);
//      
    }

  }

  /**
   * Condition whether this key should be parsed or not exists at the key
   * 
   * @param key
   * @param dynamicContent
   * @param propsRegenerated
   * @param path
   * @return
   * @throws Exception
   */
  public static boolean canBeSkipped(String key, List<Integer> dynamicContent, Properties propsRegenerated, String path)
      throws Exception {
	  
    if (key.contains("+_"))
      return false;

    String suffix = "";
    if (key.contains("_")) {
    	suffix = key.substring(key.indexOf("_"));
    	key = key.replaceAll(suffix, "");
    }

    String[] parts = key.split("\\+");
    if (parts.length == 1)
      return false;

    String condition = parts[1];

    if (condition.contains("OR")) {
      String[] conditions = condition.split("OR");
      for (String cond : conditions) {
        if (isTrue(suffix, cond, dynamicContent, propsRegenerated, path))
          return false;
      }
    }

    if (isTrue(suffix, condition, dynamicContent, propsRegenerated, path))
      return false;

    return true;
  }

  /**
   * 
   * @param cond
   * @param condition
   * @param dynamicContent
   * @param propsRegenerated
   * @param path
   * @return
   */
  private static boolean isTrue(String suffix, String cond, List<Integer> dynamicContent, Properties propsRegenerated,
      String path) {
    if (cond.startsWith("."))
      cond = cond.substring(1);
    String[] conditionParts = cond.split("\\.");
    String key = conditionParts[0] + suffix;
    String operator = conditionParts[1];
    String value = conditionParts[2];

    int[] byteArr = new int[dynamicContent.size()];
    for (int i = 0; i < byteArr.length; i++)
      byteArr[i] = dynamicContent.get(i);

    String parsedValue = null;
    String instructions = propsRegenerated.getProperty(key);

    // This means that the given condition is kind of key, which needs more
    // instructions (== one byte divided in to many keys) or if it is not, then
    // something is going terribly wrong
    if (instructions == null) {
      StringBuilder origKey = new StringBuilder(conditionParts[0]);
      // This is the part of key which makes it part of another key
      StringBuilder sb = new StringBuilder();

      while (instructions == null && origKey.length() > 0) {
        sb.insert(0, origKey.charAt(origKey.length() - 1));
        origKey.deleteCharAt(origKey.length() - 1);
        key = origKey.toString() + suffix;
        instructions = propsRegenerated.getProperty(key);
      }
      HashMap<String, String> tempValues = new HashMap<String, String>();
      Common.putDataToMap(path, propsRegenerated, tempValues, byteArr, "");
      parsedValue = tempValues.get((key + sb).toUpperCase());
    }

    try {
      if (parsedValue == null)
        parsedValue = Common.parseValue(instructions, byteArr);
    } catch (Exception e) {
      log.log(Level.SEVERE, "Could not handle logic whether key can be skipped or not.", e);
      return false;
    }

    if (operator.equalsIgnoreCase("EQ"))
      return parsedValue.equalsIgnoreCase(value);

    if (operator.equalsIgnoreCase("NE"))
      return !parsedValue.equalsIgnoreCase(value);

    if (operator.equalsIgnoreCase("GT")){
      Long lParsedVal = Long.parseLong(parsedValue); 
      Long lVal = Long.parseLong(value); 
      return lParsedVal > lVal;
    }

    return false;
  }

}
