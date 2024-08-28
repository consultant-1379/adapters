package com.distocraft.dc5000.etl.stfiop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.distocraft.dc5000.etl.parser.Parser;
import com.distocraft.dc5000.etl.parser.SourceFile;

/**
 * Parser for STFIOP data files<br>
 * <br>
 * STFIOP data contains two main blocks:  GENERAL_DATA_RECORD and OBJECT_TYPE_RECORD_BASE<br>
 * <br>
 * Datafile contains number of GENERAL_DATA_RECORDs.<br>
 * GENERAL_DATA_RECORD contains number of  OBJECT_TYPE_RECORDs.<br>
 * OBJECT_TYPE_RECORD contains number of OBJECT_RECORDs<br>
 * OBJECT_RECORD contains the actual meastypes and counters.<br>
 *<br>
 *      GENERAL_DATA_RECORD (78 Characters) <br>
 *<br>
 *<br>
 *      from 4 + 4 Characters <br>
 *      recordNumberOfNextFile<br>
 *<br>
 *      from 8 + 24 Characters <br>
 *      ExhangeIdentity <br>
 *<br>
 *      from 40 + 4 Characters <br>
 *      CollectionInterval <br>
 *      PERIOD_DURATION <br>
 *<br>
 *      from 50 + 10 Characters <br>
 *      StartTime <br>
 *      DATETIME_ID <br>
 *<br>
 *      from 60 + 4 Characters <br>
 *      StopTime <br>
 *<br>
 *      OBJECT_TYPE_RECORD (26 Characters) <br>
 *<br>
 *<br>
 *      from 0 + 10 Characters <br>
 *      RECORDTYPE <br>
 *<br>
 *    OBJECT_RECORD (16 Characters)
 *<br>
 *      from 0 + 16 Characters <br>
 *      VendorTag <br>
 *      CELL_ID <br>
 *      CELL_ID_NEIGHBOUR <br>
 *    measObjInstId <br>
 *<br>

 * 
 * <br>
 * Configuration: <br>
 * <br>
 * Database usage: not directly<br>
 * <br>
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$<br>
 * 
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
 * <td>contains the filename of the inputdatafile.</td>
 * </tr>
 * <tr>
 * <td>PERIOD_DURATION</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>DATETIME_ID</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>objectClass</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>ExhangeIdentity</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>CollectionInterval</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>StartTime</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>StopTime</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>RECORDTYPE</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>VendorTag</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>CELL_ID</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>CELL_ID_NEIGHBOUR</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>measObjInstId</td>
 * <td>&nbsp;</td>
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
 * <tr>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * </table>
 * <br><br> 
 * 
 * @author lemminkainen
 * 
 */

public class STFIOPParser implements Parser {

  private static final String JVM_TIMEZONE = new SimpleDateFormat("Z").format(new Date());
  private static final int GENERAL_DATA_RECORD_SIZE = 78;
  private static final int OBJECT_TYPE_RECORD_BASE_SIZE = 26;
  private static final int OBJECT_COUNTER_SIZE = 16;
  private static final int OBJECT_RECORD_BASE_SIZE = 16;
  private static final int OBJECT_RECORD_SIZE = 10;
  private static final int BUFFER_BLOCK = 2048;

  private Logger log;

  private SourceFile sf;
  private BufferedReader br;
  private int filePosition;

  private Map generalDataValues;
  private int recordNumberOfNextFile = 1;
  
  private Map objectTypeValues;
  private List measNameList;
  private int numberOfCounters;

  //***************** Worker stuff ****************************
  
  private final List errorList = new ArrayList();
  private String techPack;
  private String setType;
  private String setName;
  private int status = 0; 
  private Main mainParserObject = null;
  private final static String suspectFlag = "";
  private String workerName = "";

  
  public void init(final Main main, final String techPack, final String setType, final String setName, final String workerName){
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
  
  public int status(){
    return status;
  }
  
  public List errors(){
    return errorList;
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
      errorList.add(e);
    } finally {     
      this.status = 3;
    }
  }
  
  
  
  //***************** Worker stuff ****************************
  
  

  /**
   * Parse one SourceFile
   * 
   * @see com.distocraft.dc5000.etl.parser.Parser#parse(com.distocraft.dc5000.etl.parser.SourceFile,
   *      java.lang.String, java.lang.String, java.lang.String)
   */
  public void parse(final SourceFile sf, final String techPack, final String setType, final String setName) throws Exception {
    
    this.sf = sf;
    this.techPack = techPack;
    this.setType = setType;
    this.setName = setName;
    recordNumberOfNextFile = 1;
    MeasurementFile mFile = null;
    
    try {

      generalDataValues = new HashMap();
      br = new BufferedReader(new InputStreamReader(sf.getFileInputStream()));

      log.fine("Start parse...");

	  filePosition = 0;
      
      while(recordNumberOfNextFile > 0) {

        final int numberOfObjectTypes = readGeneralDataRecord();

        for (int i = 0; i < numberOfObjectTypes; i++) {
         
          final int numberOfObjectRecords = readObjectTypeRecord();        
        
          final String vendorTag = (String)objectTypeValues.get("RECORDTYPE");          
          log.finer("VendorTag: " + vendorTag);
          log.finer("number Of Object Records: " + numberOfObjectRecords);
          mFile = Main.createMeasurementFile(sf, vendorTag, techPack, setType, setName,this.workerName,log);
          
          for (int ii = 0; ii < numberOfObjectRecords; ii++) {

            readObjectRecord(mFile);

          }
         
          mFile.close();
        }

      } //while(recordNumberOfNextFile > 0)

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
   * Reads General data record from STFIOP file. Discovered variables are placed
   * on generalDataValues Map.
   * 
   * @return Number of object types
   */
  private int readGeneralDataRecord() {

    log.finest("Reading general record...");

    int numberOfObjectTypes = 0;
    this.recordNumberOfNextFile = 0;
    String buff = null;
    
    try {

      buff = readBlock(GENERAL_DATA_RECORD_SIZE, BUFFER_BLOCK);

      this.recordNumberOfNextFile = parseInt(buff.substring(4, 4 + 4).trim());

      final String ExhangeIdentity = buff.substring(8, 8 + 24).trim();
      generalDataValues.put("ExhangeIdentity", ExhangeIdentity);
      log.finer("  ExhangeIdentity: " + ExhangeIdentity);

      final String CollectionInterval = buff.substring(40, 40 + 4).trim();
      generalDataValues.put("CollectionInterval", CollectionInterval);
      generalDataValues.put("PERIOD_DURATION", CollectionInterval);
      log.finer("  CollectionInterval: " + CollectionInterval);

      final String StartTime = buff.substring(50, 50 + 10).trim();
      generalDataValues.put("StartTime", StartTime);
      log.finer("  StartTime: " + StartTime);

      final String StopTime = buff.substring(60, 60 + 4).trim();
      generalDataValues.put("StopTime", StopTime);
      log.finer("  StopTime: " + StopTime);

      numberOfObjectTypes = parseInt(buff.substring(70, 70 + 4).trim());
      log.finer("  numberOfObjectTypes: " + numberOfObjectTypes);

      log.finest("General record read.");

    } catch (Exception e) {
      log.log(Level.WARNING, "Invalid General Data Record", e);
      log.info("invalib block: "+buff);
    }

    return numberOfObjectTypes;

  }

  /**
   * Reads object type record from STFIOP file. Discovered variables are placed
   * on objectTypeValues Map.
   * 
   * @return Number of object records
   */
  private int readObjectTypeRecord() {

    log.finest("Reading objecTypeRecord...");

    if (objectTypeValues != null){
      objectTypeValues.clear();
    }
    objectTypeValues = new HashMap();

    if (measNameList != null) {
      measNameList.clear();
    }
    measNameList = new ArrayList();

    int numberOfObjectRecords = 0;

    final StringBuffer buff = new StringBuffer();

    try {

       buff.append(readBlock(OBJECT_TYPE_RECORD_BASE_SIZE, BUFFER_BLOCK));

      if (!buff.toString().trim().equals("")) {

        final String classID = buff.substring(0, 10).trim();
        log.finest("  id: " + classID);
        objectTypeValues.put("RECORDTYPE", classID);

        // NOT USED:
        // int faultCode = -1;
        // faultCode = parseInt(buff.substring(14, 14 +
        // 4).toString().trim());
        // log.finest("faultCode: " + faultCode);

        numberOfObjectRecords = parseInt(buff.substring(18, 18 + 6).toString().trim());
        log.finer("  numberOfObjectRecords: " + numberOfObjectRecords);

        this.numberOfCounters = parseInt(buff.substring(24, 24 + 2).toString().trim());
        log.finer("  numberOfCounters: " + numberOfCounters);

        if (numberOfCounters < 0) {
          numberOfCounters = 0; 
        }
                
        if (numberOfCounters > 0) {

          buff.delete(0, buff.length());
          buff.append(readBlock(numberOfCounters * OBJECT_COUNTER_SIZE, BUFFER_BLOCK));

          // if counter name list is not empty, close it.
          if (!measNameList.isEmpty()) {
            measNameList.clear();
          }

          // read the names of the counters..
          for (int i = 0; i < numberOfCounters; i++) {
            final String measurement = buff.substring((i * OBJECT_COUNTER_SIZE), (OBJECT_COUNTER_SIZE * (i + 1))).toString()
                .trim();
            log.finest("  measurement: " + measurement);
            measNameList.add(measurement);
          }
        }
      }
      
      log.finest("ObjectTypeRecord read.");
      
    } catch (Exception e) {
      log.log(Level.WARNING, "Invalid Object Type Record", e);
      log.info("invalib block: "+buff.toString());
    }

    return numberOfObjectRecords;
  }

  /**
   * Reads object record from STFIOP file.
   */
  private void readObjectRecord(final MeasurementFile mFile) {

    log.finest("Reading Object Record...");
    final StringBuffer buff = new StringBuffer("");

    try {


      buff.append(readBlock(OBJECT_RECORD_BASE_SIZE + (OBJECT_RECORD_SIZE * this.numberOfCounters), BUFFER_BLOCK));

      String objectID = "";

      if (!buff.toString().trim().equals("")) {


        mFile.addData(generalDataValues);
        mFile.addData(objectTypeValues);

        objectID = buff.substring(0, 16).trim();

        log.finest("  objectID: " + objectID);
      
        mFile.addData("Filename", sf.getName());
        mFile.addData("CELL_ID", objectID);
        mFile.addData("CELL_ID_NEIGHBOUR", objectID);
        mFile.addData("measObjInstId", objectID);
        mFile.addData("DATETIME_ID", (String) generalDataValues.get("StartTime"));
        mFile.addData("JVM_TIMEZONE",JVM_TIMEZONE);
        mFile.addData("DC_SUSPECTFLAG", suspectFlag);
        mFile.addData("DIRNAME", sf.getDir());             


        for (int i = 0; i < this.numberOfCounters; i++) {

          final String key = (String) measNameList.get(i);
          final String val = buff.substring(16 + (i * OBJECT_RECORD_SIZE), 16 + ((i + 1) * OBJECT_RECORD_SIZE)).toString()
              .trim();

          log.finest("  " + key + ": " + val);

          mFile.addData(key, val);

        }

        mFile.saveData();

      }
      
      log.finest("Object Record read.");

    } catch (Exception e) {
      log.log(Level.WARNING, "Invalid Object Record", e);
      log.info("invalib block: "+buff.toString());
    }

  }

  /**
   * Parses integer from string
   * 
   * @param str
   *          String containing an integer
   * @return parsed integer
   */
  private int parseInt(final String str) {
    int val = 0;
    try {
      val = Integer.parseInt(str);
    } catch (Exception nfe) {}

    return val;
  }

  /**
   * Reads number of characters from bufferreader.
   * 
   * @param number
   *          of character to read
   * @param size
   *          of the block
   * @return String of characters
   */

  private String readBlock(final int size, final int blockSize) {

    String block = "";
    log.log(Level.FINEST, "Reading "+size+" characters.");
    log.log(Level.FINEST, "Blocksize "+blockSize);
    try {
      

    
    if (size > 0) {

          do {

         int realSize = 0;

          try {

            if (br.ready()) {

              char[] ch;
              ch = new char[size];
              final int pos = filePosition;

              final int left = (((pos / blockSize) + 1) * blockSize - pos);
              
              if (left < size) {

                char[] tmp;
                tmp = new char[size];
                realSize = br.read(tmp, 0, left);
                filePosition += realSize;

              }

              realSize = br.read(ch, 0, size);
              filePosition += realSize;

              block = (new String(ch));

            }else{
              
              break;
            }

          } catch (IOException e) {
            log.log(Level.INFO, "Error reading data", e);
          }
     
               
      } while (block.trim().equals(""));
        } 
    } catch (NullPointerException e) {

      log.log(Level.FINE, "NPE reading data", e);

      block = "";

    }

    return block;

  }  
}
