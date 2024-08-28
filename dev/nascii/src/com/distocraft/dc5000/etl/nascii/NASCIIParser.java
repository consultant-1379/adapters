package com.distocraft.dc5000.etl.nascii;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.distocraft.dc5000.etl.parser.Parser;
import com.distocraft.dc5000.etl.parser.SourceFile;

/**
 *
 * NASCII Parser <br>
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
 * <td>timestampPattern</td>
 * <td>RegExp that defines the timestamp part in filename. See. DATETIME_ID</td>
 * <td>.+?,.+?,(\\d{12}).*</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>elemIDPattern</td>
 * <td>RegExp that defines the elemID part in filename. See. ELEMENT_ID</td>
 * <td>BSC(.+?),.*</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>vendorTagPattern</td>
 * <td>RegExp that defines the vendorTag part in filename. See. objectClass</td>
 * <td>.+?,(.+?),.*</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>keylessParameter</td>
 * <td>Parameter that has no key identified. See. BSCU_ID</td>
 * <td>BSCU_ID</td>
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
 * <td>ELEMENT_ID</td>
 * <td>Contains the elementID part of a outputfilename. elemIDPattern is used to define this part.</td>
 * </tr>
 * <tr>
 * <td>DATETIME_ID</td>
 * <td>Contains the elementID part of a outputfilename. timestampPattern is used to define this part.</td>
 * </tr>
 * <tr>
 * <td>objectClass</td>
 * <td>Contains the vendorTag part of a outputfilename. vendorTagPattern is used to define this part.</td>
 * </tr>
 * <tr>
 * <td>BSCU_ID</td>
 * <td>Contains the node identifier of a outputfilename. keylessParameter is used to define this part.</td>
 * </tr>
 * <tr>
 * <td>filename</td>
 * <td>Contains the outputfilename.</td>
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
 * </table>
 * <br><br> 
 * @author savinen
 * <br>
 * <br> 
 * 
 * 
 * 
 */
public class NASCIIParser implements Parser {

  public static final String DELIMITER = ",";
  // end of measurement
  public static final String EOM = ";";
  private Logger log;
  private String timestampPattern = ".+?,.+?,(\\d{12}).*";
  private String elementIDPattern = "BSC(.+?),.*";
  private String vendorTagPattern = ".+?,(.+?),.*";
  
  //***************** Worker stuff ****************************
  
  private String techPack;
  private String setType;
  private String setName;
  private int status = 0; 
  private Main mainParserObject = null;
  private String suspectFlag = "";
  private String workerName = "";
  private String unknownParameter = "BSCU_ID";


  public void init(Main main,String techPack, String setType, String setName,String workerName){
    this.mainParserObject = main;
    this.techPack = techPack;
    this.setType = setType;
    this.setName = setName;
    this.status = 1;
    this.workerName = workerName;
    String logWorkerName = "";
    if (workerName.length() > 0) logWorkerName = "."+workerName;

    log = Logger.getLogger("etl." + techPack + "." + setType + "." + setName + ".parser.NASCII"+logWorkerName);

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
   * Just-hang-around-constructor
   */
  public NASCIIParser() {}

  
  /**
   * Parse one SourceFile
   * 
   * @see com.distocraft.dc5000.etl.parser.Parser#parse(com.distocraft.dc5000.etl.parser.SourceFile,
   *      java.lang.String, java.lang.String, java.lang.String)
   */
  public void parse(SourceFile sf, String techPack, String setType, String setName) throws Exception {
    

    BufferedReader br = null;
    MeasurementFile mFile = null;
    
    unknownParameter = sf.getProperty("keylessParameter", unknownParameter);
    timestampPattern = sf.getProperty("timestampPattern", timestampPattern);
    elementIDPattern = sf.getProperty("elemIDPattern", elementIDPattern);
    vendorTagPattern = sf.getProperty("vendorTagPattern", vendorTagPattern);

    
    try {

      br = new BufferedReader(new InputStreamReader(sf.getFileInputStream()));

      String line;
      
      // first row is header      
      line = br.readLine();

      String elementID = getPattern(line,elementIDPattern);
      String vendorTag = getPattern(line,vendorTagPattern);
      String datetimeID = getPattern(line,timestampPattern);
      
      
      // create new measFile
      mFile = Main.createMeasurementFile(sf, vendorTag, techPack, setType, setName,this.workerName, this.log);

      while ((line = br.readLine()) != null)
      {
        try {
	        if (line.indexOf(EOM)!=-1){
	          
	          // last row of a measurement
	          
	          // remove the end of measurement marker (EOM)
	          line = line.replaceAll(EOM,",");
	          readData(line.trim(),mFile); 
	          
	          mFile.addData("ELEMENT_ID",elementID);
            mFile.addData("DATETIME_ID",datetimeID);
            mFile.addData("objectClass",vendorTag);
            mFile.addData("filename",sf.getName());
			  
            SimpleDateFormat sdf = new SimpleDateFormat("Z");        
            mFile.addData("JVM_TIMEZONE",sdf.format(new Date()));
            mFile.addData("DC_SUSPECTFLAG", suspectFlag);
            mFile.addData("DIRNAME", sf.getDir());             

            
	          mFile.saveData();
	          
	        }else{
	          
	          readData(line,mFile); 
	          
	        }
	         
        } catch (Exception e) {
        	log.log(Level.WARNING, "Error while processing line: "+line, e);
        }
        
      }
      
      mFile.close();
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
   * Reads datalines.
   * 
   * @param line
   *          A line of data
   * @param mFile
   *          MeasurementFile
   * @throws Exception
   *           in case of failure
   */
  private void readData(String line,MeasurementFile mFile) throws Exception {
  	String value = "";
  	String key = "";
  	boolean keyLessParameter = false;
	
	try{
		
		key = line.substring(0,line.indexOf(DELIMITER));

	}catch (Exception e){
		log.log(Level.FINEST, "Could not read key value from file, null key returned");
	}
	
	try{
		
		value = line.substring(line.indexOf(DELIMITER)+1,line.lastIndexOf(DELIMITER));

	}catch (Exception e){
		log.log(Level.FINEST, "Could not read value from file, null value returned");
		keyLessParameter = true;
	}
	if (keyLessParameter) {
	  mFile.addData(unknownParameter,key);
	} else {
	  mFile.addData(key,value);
	}
    
  }


  /**
   * 
   * returns rexExp patter defined part oa a string.
   *
   * 
   * @param string
   * @param pattern
   * @return
   */ 
  private String getPattern(String string,String pattern)
  {
    
    try
    {  
    
      String result="";

      Pattern timeStampPattern = Pattern.compile(pattern);
      Matcher m = timeStampPattern.matcher(string);
      if (m.find())
        result = m.group(1);

      return result;
      
    }catch (Exception e)
    {
      
    }
    
    return null;
    
  }
   
}