package com.ericsson.eniq.etl.parser.unittests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.XMLReader;

import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.distocraft.dc5000.etl.parser.Parser;
import com.distocraft.dc5000.etl.parser.SourceFile;

public class UnittestParser implements Parser {

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
  
  private List errorList = new ArrayList();
  private String techPack;
  private String setType;
  private String setName;
  private int status = 0; 
  private Main mainParserObject = null;
  private String suspectFlag = "";
  private String workerName = "";

  
  public void init(Main main,String techPack, String setType, String setName,String workerName){
    this.mainParserObject = main;
    this.techPack = techPack;
    this.setType = setType;
    this.setName = setName;
    this.status = 1;
    this.workerName = workerName;
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
   * Waistrel constructor. Does nothing.
   */
  public UnittestParser() {
  }

  /**
   * Parse one SourceFile
   * 
   * @see com.distocraft.dc5000.etl.parser.Parser#parse(com.distocraft.dc5000.etl.parser.SourceFile,
   *      java.lang.String, java.lang.String, java.lang.String)
   */
  public void parse(SourceFile sf, String techPack, String setType, String setName) throws Exception {
    this.techPack = techPack;
    this.setType = setType;
    this.setName = setName;

    String logWorkerName = "";
    if (workerName.length() > 0)
      logWorkerName = "." + workerName;

    log = Logger.getLogger("etl." + techPack + "." + setType + "." + setName + ".parser.XML" + logWorkerName);

    
    String vendorID = sf.getProperty("vendorID", null);
    String measListStr = sf.getProperty("measList", null);
    int rowCount = Integer.parseInt(sf.getProperty("rowCount", "1"));
    String datatime = sf.getProperty("datatime", "2007-01-01 00:00:000");
    boolean useCurrTime = "TRUE".equalsIgnoreCase(sf.getProperty("useCurrTime", "false"));
    String dateFormat = sf.getProperty("dateFormat", "yyyy-MM-dd hh:mm:sss");
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    
    
    log.fine("vendorID: "+vendorID);
    log.fine("measListStr: "+measListStr);
    log.fine("rowCount: "+rowCount);  
    log.fine("datatime: "+datatime);
    log.fine("useCurrTime: "+useCurrTime);
    log.fine("dateFormat: "+dateFormat);
    
    String[] measListStrs = measListStr.split(",");
    ArrayList measList = new ArrayList();
    for (int i = 0 ; i < measListStrs.length ; i++){
      measList.add(measListStrs[i]);
    }
   
    MeasurementFile measFile = Main.createMeasurementFile(sf, vendorID, techPack, setType, setName, this.workerName, log);

    
    for (int i = 0 ; i < rowCount ; i++){
      
      HashMap measData = new HashMap();
      
      log.fine("new row");
      
      Iterator iter = measList.iterator();
      while (iter.hasNext()){
        String meas = (String)iter.next();
        measData.put(meas, meas+i);
        log.fine("coll: "+meas+"/"+meas+i);
      }
      
      
      if (!useCurrTime){
        measFile.addData("DATE_ID", datatime);
      } else {
        measFile.addData("DATE_ID", sdf.format(new Date()));
      }
      
      measFile.addData("Filename", sf.getName());
      measFile.addData("DC_SUSPECTFLAG", suspectFlag);
      measFile.addData("DIRNAME", sf.getDir());
      measFile.addData("objectClass", vendorID);
      SimpleDateFormat sdft = new SimpleDateFormat("Z");
      measFile.addData("JVM_TIMEZONE", sdft.format(new Date()));
      measFile.addData(measData);
      measFile.saveData();
      
    }
    

  }

}
