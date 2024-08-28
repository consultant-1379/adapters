package com.distocraft.dc5000.etl.parser;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.distocraft.dc5000.etl.binaryformatter.BinFormatter;
import com.distocraft.dc5000.etl.engine.common.Share;
import com.distocraft.dc5000.repository.cache.DFormat;
import com.distocraft.dc5000.repository.cache.DItem;
import com.distocraft.dc5000.repository.cache.DataFormatCache;

/**
 * MeasurementFile of parser. Takes parsed data in by addData methods and writes
 * default form output file. <br>
 * <br>
 * Configuration: none <br>
 * <br>
 * Database usage: Metadata <br>
 * <br>
 * $id$ <br>
 * <br>
 * Copyright Distocraft 2005 <br>
 * 
 * @author lemminkainen
 */
public class MeasurementFileImpl implements MeasurementFile {

  public static boolean moveNagged = false;

  public static final int IQ_TEXT = 0;

  public static final int IQ_BIN = 1;

  public final int outputFormat;

  private SourceFile sf;

  private String techpack;

  private Logger log;

  private final boolean ignoreMetaData;

  private final String coldelimiter;

  private final String rowdelimiter;

  private final boolean delimitedHeader;

  private Map<String, String> data;

  private int rowcount = 0;

  private static DataFormatCache dfCache = null;

  private DFormat dataformat = null;
  private Transformer transformerCache = null;

  private PrintWriter writer = null;

  private BufferedOutputStream bwriter = null; //For writing to measurement file in binary format mode.
  private int rowByteSize; //The size of a row in bytes when written to a binary format measurement file.
  private FileOutputStream fileOutputStream = null; 

  private boolean metadataFound = false;

  private String outputFileName = null;

  private String typeName = null;

  private HashSet<String> nullSet = null;

  private ParserDebugger debugger;

  private boolean debug = false;
  
  private boolean isTransformed = false;

  private static boolean testMode = false;

  private String tagID = null;

  // is measFile ready
  private boolean ready = false;

  private String workerName = null;

  private String rowstatus = "ROWSTATUS";

  private String suspected = "SUSPECTED";

  private Charset charsetEncoding = null;

  private String nullValue = null;

  private long counterVolume = 0L;
  
  private String datetimeID = "";


  
  /**
   * 
   * constructor
   * 
   * @param sfile
   * @param tagID
   * @param techPack
   * @param set_type
   * @param set_name
   * @throws Exception
   */
  MeasurementFileImpl(SourceFile sfile, String tagID, String techPack, String set_type, String set_name, Logger log)
      throws Exception {

    this(sfile, tagID, techPack, set_type, set_name, "", log);

  }

  /**
   * 
   * constructor
   * 
   * @param sfile
   * @param tagID
   * @param techPack
   * @param set_type
   * @param set_name
   * @throws Exception
   */
  MeasurementFileImpl(SourceFile sfile, String tagID, String techPack, String set_type, String set_name,
      String workerName, Logger log) throws Exception {

    this.sf = sfile;

    this.techpack = techPack;

    this.workerName = workerName;

    this.log = log;

    //Find out the format of the output from parser (e.g. ASCII or binary).
    final String outputFormatTemp = sf.getProperty("MDCParser.outputFormat", "0");
    if(outputFormatTemp.equals("")){
    	outputFormat = 0;
    }else{
    	outputFormat = Integer.parseInt(outputFormatTemp);
    }

    ignoreMetaData = (sf.getProperty("ignoreMetaData", "false").equals("true"));

    coldelimiter = sf.getProperty("outputColDelimiter", "\t");

    rowdelimiter = sf.getProperty("outputRowDelimiter", "\n");

    delimitedHeader = (sf.getProperty("delimitedHeader", "false").equals("true"));

    nullValue = sf.getProperty("nullValue", null);

    data = new HashMap<String, String>();

    if (dfCache == null) {
      dfCache = DataFormatCache.getCache();
    }

    String interfaceName = sf.getProperty("interfaceName");
    rowstatus = sf.getProperty("rowstatus", "ROWSTATUS");
    suspected = sf.getProperty("suspected", "SUSPECTED");

    boolean b = "TRUE".equalsIgnoreCase(sf.getProperty("useDebugger", "false"));

    if (b) {
      this.debugger = ParserDebuggerCache.getCache();
    }

    debug = "true".equalsIgnoreCase(sf.getProperty("debug", ""));

    if (interfaceName == null || interfaceName.equalsIgnoreCase("")) {
      log.log(Level.WARNING, "Invalid interface name: " + interfaceName);
      return;
    }

    dataformat = dfCache.getFormatWithTagID(interfaceName, tagID);

    if (dataformat == null) {
      log.fine("DataFormat not found for (tag: " + tagID + ") -> writing nothing.");
      return;
    }

    transformerCache = TransformerCache.getCache().getTransformer(dataformat.getTransformerID());  // FROM PREV
    
    log.fine("Opening measurementFile TagID \"" + tagID + "\" Interface Name \"" + interfaceName + "\"");

    this.tagID = tagID;
    writer = null;
    bwriter = null;  //FROM BIN
    rowByteSize = Main.getRowByteSize(dataformat);

    String nullStrings = sf.getProperty("nullStrings", null);
    if (nullStrings != null) {
      nullSet = new HashSet<String>();
      StringTokenizer st = new StringTokenizer(nullStrings, ",");
      while (st.hasMoreTokens()) {
        nullSet.add(st.nextToken());
      }
    }

    this.charsetEncoding = getOutputCharsetEncoding();
    
    ready = true;

  }
  

  private Charset getOutputCharsetEncoding() {
    final Share share = Share.instance();
    Charset cs = null;
    String dwhdbCharsetEncoding = null;

    try {
      dwhdbCharsetEncoding = (String) share.get("dwhdb_charset_encoding");
      if (null != dwhdbCharsetEncoding) {
        cs = Charset.forName(dwhdbCharsetEncoding);
      }
    } catch (Exception e) {
      log.log(Level.WARNING, "Cannot instantiate charset object out of the database charset setting: "
          + dwhdbCharsetEncoding, e);
    }

    return cs;
  }

  public void setDebugger(final ParserDebugger debugger) {
    this.debugger = debugger;
  }

  public void addData(final String name, final String value) {
    data.put(name, value);
  }

  public void addData(final Map map) {
    data.putAll(map);
  }

  public void setData(final Map map) {
    data = map;
  }
  
  public DFormat getDataformat(){
	return dataformat;
  }
  

  public void saveData() throws Exception {

    log.finest("saveData");

    if (testMode) {
      log.finest("TestMode active, setting fixed SESSION_ID and BATCH_ID");
      data.put("SESSION_ID", "111111111");
      if (debug) {
          log.finest("session id saved");
      }
      data.put("BATCH_ID", "55555");
      if (debug) {
          log.finest("batch id saved");
      }
    } else {
      if (sf != null) {
        if (sf.getParseSession() != null) {
          data.put("SESSION_ID", String.valueOf(sf.getParseSession().getSessionID()));
        } else {
          log.warning("Invalid ParseSession (== null)");
          return;
        }
        data.put("BATCH_ID", String.valueOf(sf.getBatchID()));
      } else {
    	log.warning("Invalid SourceFile (== null)");
        return;
      }
    }

    // No dataformat found. Skipping the write...
    if (dataformat == null) {
      return;
    }

    transformerCache = TransformerCache.getCache().getTransformer(dataformat.getTransformerID());
    
    if (debugger != null) {
    	if (transformerCache != null){
    		transformerCache.addDebugger(debugger);
    	}
      debugger.setDatatitems(dataformat.getTransformerID(), dataformat.getDitems());
    }

    if (debug) {

      log.finest("Datavector before transformation:");

      final Iterator<String> iter = data.keySet().iterator();

      while (iter.hasNext()) {
        final String key = iter.next();
        Object value = data.get(key);
        log.finest("  Key: '" + key + "' Value: '" + value + "'");
      }

      log.finest("End of datavector");

    }

    if (debugger != null) {
      debugger.beforeTransformer(dataformat.getTransformerID(), data);
    }
    
    if (transformerCache != null && !isTransformed()) {
    	transformerCache.transform(data, log);
    }

    String rStatus = (String) data.get(rowstatus);
    if (rStatus != null && rStatus.equalsIgnoreCase(suspected)) {
      sf.setSuspectedFlag(true);
    }

    if (debug) {

      log.finest("Datavector after transformation:");

      final Iterator<String> iter = data.keySet().iterator();

      while (iter.hasNext()) {
        final String key = iter.next();
        Object value = data.get(key);
        log.finest("  Key: '" + key + "' Value: '" + value + "'");
      }

      log.finest("End of datavector");

    }

    if (debugger != null) {
      debugger.afterTransformer(dataformat.getTransformerID(), data);
    }

    if (writer == null && bwriter == null) {

      final String firstDateID = (String) data.get("DATE_ID");

      if (firstDateID == null) {
        if (log.isLoggable(Level.FINEST)) {
          final Set<String> keys = data.keySet();
          final Iterator<String> i = keys.iterator();
          final StringBuffer sb = new StringBuffer("FirstRow: DATE_ID was not found. Available keys: ");
          while (i.hasNext()) {
            sb.append(i.next());
            if (i.hasNext()) {
              sb.append(",");
            }
          }
          log.finest(sb.toString());
        }
      } else {
        log.finest("FirstRow: DATE_ID was set " + firstDateID);
      }
      
      // get datetime_id from the first row for counter volume information
      final String firstDatetimeID = (String) data.get("DATETIME_ID");
      
      if (null == firstDatetimeID) {
    	  datetimeID = "";
    	  log.finest("FirstRow: Did not found DATETIME_ID");
      } else {
    	  datetimeID = firstDatetimeID;
    	  log.finest("FirstRow: DATETIME_ID = " + firstDatetimeID);
      }

      // Ignore metadata = We create metadata based on the first dataline
      if (ignoreMetaData) {

        final List<String> keys = new ArrayList<String>(data.keySet());

        final List<DItem> ditems = new ArrayList<DItem>();

        Collections.sort(keys);

        final Iterator<String> i = keys.iterator();
        int ix = 1;
        while (i.hasNext()) {
          final String counter = (String) i.next();
          final DItem ditem = new DItem(counter, ix++, counter, "");
          ditems.add(ditem);
        }
        dataformat.setItems(ditems);
      }

      openMeasurementFile(tagID, firstDateID);

    }

    LinkedHashMap<String, String> lsm = null;

    if (debugger != null) {
      lsm = new LinkedHashMap<String, String>();
    }
    
    final Iterator<DItem> iterator = dataformat.getDItems();

    while (iterator.hasNext()) {
      final DItem mData = iterator.next();

      String value = (String) data.get(mData.getDataID());
      
      if (nullValue != null && value == null) {
        value = nullValue;
      } 

      if (nullSet != null && nullSet.contains(value)) {
        value = null;
      } 
      
      // calculate not null counters
      if (null != value && 1 == mData.getIsCounter()) {
    	  counterVolume++;
      }

      if (outputFormat == IQ_TEXT) {
        if (value != null) {
          writer.write(value);
        }

        writer.write(coldelimiter);

      } else if (outputFormat == IQ_BIN) {

        Map<String, BinFormatter> formatters = BinFormatter.getClasses();

        BinFormatter bf = formatters.get(mData.getDataType());

        if (bf == null) {
          throw new Exception("Datatype " + mData.getDataType() + " of data item " + mData.getDataName() + " (of "
              + dataformat.getTagID() + ") is not supported in binary output mode.");
        }

        int [] sizeDetails = {mData.getDataSize(),mData.getDataScale()};
        byte[] binaryValue = null;
        try{ //Convert to binary form
        	binaryValue = bf.doFormat(value, sizeDetails);
        }catch(NumberFormatException e){ //value was supposed to be a number but has turned out not to be.
        	
        	log.warning("Could not convert data item "+mData.getDataName()+" (of "+dataformat.getTagID()+") to binary form. Value: "+value 
                    + "\nThe entire datarow will be skipped and not written to measurement file!" );
        	
        	data.clear(); 
        	//Ensure the data in buffer (bwriter) will not be written to file, and provide new buffer for next call to this method.
        	bwriter = new BufferedOutputStream(fileOutputStream, rowByteSize); 
        	return;
        }

        /*for (int i = 0; i < bindata.length; i++) {
          bwriter.write(bindata[i]);
        }*/
        
        bwriter.write(binaryValue);

      }

      if (lsm != null) {
        lsm.put(mData.getDataID(), value);
      }

    } // foreach data value

    if (outputFormat == IQ_TEXT) {  //FROM BIN
      writer.write(rowdelimiter);

      writer.flush();

      if (writer.checkError()) {
        log.warning("Error in printwriter while writing: " + this.outputFileName);
      }
    } else if (outputFormat == IQ_BIN) {
      bwriter.flush();
    }

    rowcount++;

    if (debugger != null) {
      debugger.result(lsm);
    }

    data.clear();

  }

  public int getRowCount() {
    return rowcount;
  }

  public long getCounterVolume() {
	  return counterVolume;
  }
  
  public String getDatetimeID() {
	  return datetimeID;
  }
  
  public String getTypename() {
	  return typeName;
  }
  
  public boolean metadataFound() {
    return metadataFound;
  }

  public boolean isOpen() {
    return ready;
  }

  public void close() throws Exception {

    if (data != null) {
      if (data.size() > 0 && metadataFound) {
        log.info("MeasurementFile contains data when closing outputfile");
      }
      data.clear();
    }
    
    if (writer != null) { // output file was open
      writer.close();
      ready = false;
      log.finer("File closed " + outputFileName);
    }
    
    if (bwriter != null) {
      bwriter.close();
      ready = false;
      log.finer("File closed " + outputFileName);
    }

    writer = null;
    sf = null;
    data = null;
    nullSet = null;
    debugger = null;
  }

  public String toString() {
    return "MeasurementFile " + outputFileName + " type " + typeName;
  }

  /**
   * Opens writer to output file and writes header line
   */
  private void openMeasurementFile(final String tagID, final String dateID) throws Exception {

	  String filename = null;
	  
	  // fixed filename for testing
	  if (!testMode) {
		  filename = determineOutputFileName(tagID, dataformat.getFolderName()) + "_" + workerName + "_"
		  + dateID;
	  }else{
		  filename = determineOutputFileName(tagID, dataformat.getFolderName()) + "_" + workerName;	    	
	  }

    sf.addMeastype(dataformat.getFolderName());
    
    final File outFile = new File(filename);

    if (outputFormat == IQ_BIN) {
    	
      fileOutputStream = new FileOutputStream(outFile, true);
      bwriter = new BufferedOutputStream(fileOutputStream, rowByteSize);
      
    } else if (outputFormat == IQ_TEXT) {

      OutputStreamWriter osw = null;

      if (null == this.charsetEncoding) {
        osw = new OutputStreamWriter(new FileOutputStream(outFile, true));
      } else {
        osw = new OutputStreamWriter(new FileOutputStream(outFile, true), this.charsetEncoding);
      }

      writer = new PrintWriter(new BufferedWriter(osw));

      // Write header line to file?
      if (sf.getWriteHeader()) {

        if (delimitedHeader) {
          writer.write(rowdelimiter);
        }

        final Iterator<DItem> iterator = dataformat.getDItems();

        while (iterator.hasNext()) {

          final DItem data = iterator.next();

          writer.write(data.getDataName());
          writer.write(coldelimiter);

        }

        writer.write(rowdelimiter);

        if (delimitedHeader) {
          writer.write(rowdelimiter);
        }

      }

    } // else no such outputformat

  }

  /**
   * Determines the directory + filename for this measurementFile
   * 
   * @param tagid
   * @param typename
   * @param typeid
   * @param vendorid
   * @return
   * @throws Exception
   */
  private String determineOutputFileName(final String tagid, final String typeName) throws Exception {

    // generate typename
    this.typeName = typeName;

    String destDir = sf.getProperty("outDir", sf.getProperty("baseDir") + File.separator + "out");

    if (!destDir.endsWith(File.separator)) {
      destDir += File.separator;
    }

    destDir = Main.resolveDirVariable(destDir);

    destDir += techpack;

    final File ddir = new File(destDir);
    if (!ddir.exists()) {
      ddir.mkdir();
    }

    String fileName = destDir + File.separator + typeName;

    // fixed filename for testing
    if (!testMode) {
      final SimpleDateFormat loaderTimestamp = new SimpleDateFormat("yyyyMMddHH");
      fileName += "_" + loaderTimestamp.format(new Date());
    }

    this.outputFileName = fileName;

    return fileName;
  }
  
  
  /**
   * @param isTransformed the isTransformed to set
   */
  public void setTransformed(boolean isTransformed) {
    this.isTransformed = isTransformed;
  }

  /**
   * @return the isTransformed
   */
  public boolean isTransformed() {
    return isTransformed;
  }
  

  /**
   * In Test Mode return always same sessionID
   * 
   * @param testMode
   *          The testMode to set.
   */
  public static void setTestMode(final boolean testMode) {
    MeasurementFileImpl.testMode = testMode;
  }
}
