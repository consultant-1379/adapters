package com.distocraft.dc5000.etl.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import ssc.rockfactory.RockFactory;

/**
 * Created on Jan 18, 2005
 * 
 * Represents sourcefile for parser. Also act as holder for configuration
 * object.
 * 
 * @author lemminkainen
 */
public class SourceFile {

  private Logger log;

  private File file;

  private Properties conf;

  private boolean writeHeader;

  private RockFactory rf;

  private RockFactory reprf;

  private ParseSession psession;

  private ParserDebugger debugger;

  private List measurementFiles;

  private List measurementTypes;

  private InputStream fis = null;

  private int batchID = -1;

  private String parsingStatus = "INITIALIZED";

  private String errorMessage = "";

  private long parsingstarttime;

  private long parsingendtime;

  private String unzip = "none";

  private boolean error = false;

  private boolean suspected = false;

  private final List zipEntryList = new ArrayList();

  private ZipFile zipFile = null;

  private String errorMsg = "";

  private int memoryConsumptionMB = 0;
  
  private int totalRowCount = 0;
  
  private boolean totalRowCountCounted = false;
  
  SourceFile(File file, Properties conf, RockFactory rf, RockFactory reprf, ParseSession psession,
      ParserDebugger debugger, Logger log) {

    this(file, conf, rf, reprf, psession, debugger, "none", log);

  }
  
  SourceFile(File file, int memoryConsumptionMB, Properties conf, RockFactory rf, RockFactory reprf, ParseSession psession,
	      ParserDebugger debugger, String useZip, Logger log) {

	    this(file, conf, rf, reprf, psession, debugger, useZip, log);
	    this.memoryConsumptionMB = memoryConsumptionMB;
	    
  }

  SourceFile(File file, Properties conf, RockFactory rf, RockFactory reprf, ParseSession psession,
      ParserDebugger debugger, String useZip, Logger log) {

    this.file = file;
    this.conf = conf;
    this.rf = rf;
    this.reprf = reprf;
    this.psession = psession;
    this.debugger = debugger;
    this.unzip = useZip;
    this.log = log;

    measurementFiles = new ArrayList();
    measurementTypes = new ArrayList();

    try {
      String wt = getProperty("writeHeader", "false");
      if (wt.trim().equals("true")) {
        writeHeader = true;
      } else {
        writeHeader = false;
      }
    } catch (Exception e) {
      writeHeader = false;
    }

  }

  void addMeastype(final String type) {

    if (!measurementTypes.contains(type)) {
      measurementTypes.add(type);
    }
  }

  List getMeastypeList() {
    return measurementTypes;
  }

  /**
   * Session id is explicitly set to avoid reserving batch id for
   * 
   * @param batchID
   */
  void setBatchID(final int batchID) {
    this.batchID = batchID;
  }

  RockFactory getRockFactory() {
    return this.rf;
  }

  RockFactory getRepRockFactory() {
    return this.reprf;
  }

  ParseSession getParseSession() {
    return psession;
  }

  boolean getWriteHeader() {
    return writeHeader;
  }

  boolean getErrorFlag() {
    return error;
  }

  void setErrorFlag(final boolean f) {
    error = f;
  }

  boolean getSuspectedFlag() {
    return suspected;
  }

  void setSuspectedFlag(final boolean f) {
    suspected = f;
  }

  void setErrorMsg(final String e) {
    errorMsg = e;
  }

  String getErrorMsg() {
    return errorMsg;
  }

  /**
   * Returns lastModified of this sourceFile
   */
  long getLastModified() {
    return file.lastModified();
  }

  int getBatchID() {
    return batchID;
  }

  void delete() {
    log.fine("Deleting file");
    final boolean ok = file.delete();

    if (!ok) {
      log.warning("Could not delete file " + file.getName());
    }
  }

  public String getName() {
    return file.getName();
  }

  public String getDir() {
    return file.getParent();
  }

  public long getSize() {
    return file.length();
  }

  public int getRowCount() {
	int count = 0;
	
	if(!totalRowCountCounted) {
	    final Iterator i = measurementFiles.iterator();
	    while (i.hasNext()) {
	      final MeasurementFile mf = (MeasurementFile) i.next();
	      count += mf.getRowCount();
	    }
	} else {
		count = totalRowCount; 
	}
    
	return count;
  }

  /**
   * The way to read parser source file.
   * 
   * @return inputStream to file represented by this object
   */
  public InputStream getFileInputStream() throws Exception {
    if (unzip.equalsIgnoreCase("zip")) {
      fis = unzip(file);
    } else if (unzip.equalsIgnoreCase("gzip")) {
      fis = gunzip(file);
    } else {
      fis = new FileInputStream(file);
    }
    return fis;

  }

  /*
   * 
   */
  public boolean hasNextFileInputStream() throws Exception {

    if (zipEntryList != null && zipEntryList.size() > 0) {
      return true;
    }

    return false;
  }

  /*
   * 
   */
  public InputStream getNextFileInputStream() throws Exception {

    if (zipEntryList != null && zipEntryList.size() > 0) {
      return zipFile.getInputStream((ZipEntry) zipEntryList.remove(0));
    }

    return null;

  }

  /**
   * This method will take a single file as its parameter and then return a
   * readable input stream to this file. Closing of the stream needs to be done
   * by the caller.
   * 
   * @param f
   *          the file to read.
   * @return an input stream for the given file. This will either be a regular
   *         FileInputStream or a GZipInputStream depending on if the file is
   *         compressed or not.
   * @throws Exception
   * @author ecarbjo
   */
  protected InputStream gunzip(final File f) throws Exception {
    try {
      // apparently checks GZIP validity checking is done automatically already
      // when the GZIP stream is initialized, with other words there is no need
      // to manually check the data.
      final GZIPInputStream gis = new GZIPInputStream(new FileInputStream(f));

      log.info("GZip file " + f.getName() + " is a valid GZip file. Returning decompressed data.");
      return gis;
    } catch (Exception e) {
      // We caught an exception. This indicates a problem with decompression.
      // Return the file input stream instead.
      log.info("GZip file " + f.getName() + " is not a valid GZip file. Retrying without decompression.");

      return new FileInputStream(f);
    }
  }

  private InputStream unzip(final File f) throws Exception {

    try {

      if (zipEntryList != null) {
        zipEntryList.clear();
      }
      zipFile = new ZipFile(f);
      final Enumeration ez = zipFile.entries();

      while (ez.hasMoreElements()) {
        final ZipEntry zipEntry = (ZipEntry) ez.nextElement();
        zipEntryList.add(zipEntry);
      }

    } catch (Exception e) {
      log.warning("Error while unzipping " + f.getName() + " " + e);
    }

    if (zipEntryList.size() == 0) {

      log.info("Zip file contains no entries, trying to use as normal data file.");
      return new FileInputStream(f);

    }

    log.info("Zip file (" + f.getName() + ") contains " + zipEntryList.size() + " entries");
    log.info("First entry is " + ((ZipEntry) zipEntryList.get(0)).getName());

    return zipFile.getInputStream((ZipEntry) zipEntryList.remove(0));
  }

  /**
   * Adds a measurementFile to list of measurementFiles.
   * 
   * @param mf
   *          MeasurementFile to add.
   */
  void addMeasurementFile(final MeasurementFile mf) {
    measurementFiles.add(mf);

    if (debugger != null) {
      debugger.newMeasurementFile(mf);

    }
  }

  /**
   * Checks that all measurementFiles and sourceFile are closed. If open
   * measurementFiles or sourceFile are found explicit close is performed.
   */
  void close() {

    // Close all measurementFiles
    while (measurementFiles.size() > 0) {
      final MeasurementFile mf = (MeasurementFile) measurementFiles.remove(0);
      try {
        if (mf.isOpen()) {
          mf.close();
          log.info("Found open measurementFile: " + mf);
        }
      } catch (Exception e) {
      }
    }

    // Close inputFile
    if (fis != null) {
      try {
        fis.close();
      } catch (Exception e) {
      }
    }

  }

  /**
   * Determines weather this SourceFile is old enough to be parsed
   * 
   * @return true if this file is old enough false otherwise
   */
  boolean isOldEnoughToBeParsed() throws Exception {
    try {

      final int timeDiff = Integer.parseInt(conf.getProperty("minFileAge"));

      if ((System.currentTimeMillis() - file.lastModified()) >= (timeDiff * 60000)) {
        return true;
      }

    } catch (Exception e) {
      log.info("File modification time comparison failed.");
    }

    return false;
  }

  /**
   * Tries to move this SourceFile to target directory. If file.renameTo fails
   * move is tried via copying and deleting.
   * 
   * @param tgtDir
   *          Target directory category.
   * @throws Exception
   *           in case of failure
   */
  public void move(final File tgtDir) throws Exception {

    final File tgt = new File(tgtDir, file.getName());

    final boolean success = file.renameTo(tgt);

    if (success) {
      return;
    }

    final InputStream in = getFileInputStream();
    final OutputStream out = new FileOutputStream(tgt);

    final byte[] buf = new byte[1024];
    int len;
    while ((len = in.read(buf)) > 0) {
      out.write(buf, 0, len);
    }
    in.close();
    out.close();

    delete();

    log.finest("File successfully moved via copy & delete");

  }

  /**
   * Returns SessionLog entry for this sourcefile
   */
  public Map getSessionLog() {

    final HashMap lentry = new HashMap();

    lentry.put("sessionID", String.valueOf(getParseSession().getSessionID()));
    lentry.put("batchID", String.valueOf(batchID));
    lentry.put("fileName", getName());

    lentry.put("source", conf.getProperty("interfaceName"));

    lentry.put("sessionStartTime", String.valueOf(parsingstarttime));
    lentry.put("sessionEndTime", String.valueOf(parsingendtime));

    lentry.put("srcLastModified", String.valueOf(getLastModified()));
    lentry.put("srcFileSize", String.valueOf(getSize()));

    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    lentry.put("dateID", sdf.format(new Date(parsingstarttime)));

    lentry.put("status", parsingStatus);

    if (errorMessage != null) {
      lentry.put("errorMessage", errorMessage);
    }

    // get counter volume information per measurement type and rop starttime
    final HashMap counterVolumes = getCounterVolumeInfo();
    
    // set counter volume information to session information
    lentry.put("counterVolumes", counterVolumes);
    
    return lentry;

  }

  private HashMap getCounterVolumeInfo() {
	  final HashMap counterVolumes = new HashMap();
	  
	  final Iterator i = measurementFiles.iterator();
	  int totalRowCount = 0;
	  
	  while (i.hasNext()) {
	    final MeasurementFile mf = (MeasurementFile) i.next();
	    
	    final HashMap counterVolumeInfo = new HashMap();
	    
	    final int rowCount = mf.getRowCount();
	    final long counterVolume = mf.getCounterVolume();
	    final String datetimeID = mf.getDatetimeID();
	    final String typeName = mf.getTypename();
	    
	    counterVolumeInfo.put("rowCount", String.valueOf(rowCount));
	    counterVolumeInfo.put("counterVolume", String.valueOf(counterVolume));
	    counterVolumeInfo.put("ropStarttime", datetimeID);
	    counterVolumeInfo.put("typeName", typeName);
	    
	    // store counter volume information by measurement type and rop starttime as key
	    counterVolumes.put(typeName + "_" + datetimeID, counterVolumeInfo);
	    
	    totalRowCount += rowCount;
	  }
	  
	  this.totalRowCount = totalRowCount;
	  this.totalRowCountCounted = true;
	  
	  return counterVolumes;
  }
  
  /**
   * Gets property value from attached properties object.
   * 
   * @param name
   *          Property name.
   * @return Property value.
   * @throws Exception
   *           is thrown if property is undefined.
   */
  public String getProperty(final String name) throws Exception {
    return conf.getProperty(name);
  }

  /**
   * Gets property value from attached properties object. If property is not
   * defined defaultValue is returned.
   * 
   * @param name
   *          Property name
   * @param defaultValue
   *          Default value
   * @return Property value or defaultValue if property is not defined.
   */
  public String getProperty(final String name, final String defaultValue) {
    return conf.getProperty(name, defaultValue);
  }

  /**
   * 
   * return the size of the file in bytes.
   * 
   * @return
   */
  public long fileSize() {
    return file.length();
  }

  public String getParsingStatus() {
    return parsingStatus;
  }

  public void setParsingStatus(final String parsingStatus) {
    this.parsingStatus = parsingStatus;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(final String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public long getParsingendtime() {
    return parsingendtime;
  }

  public void setParsingendtime(final long parsingendtime) {
    this.parsingendtime = parsingendtime;
  }

  public long getParsingstarttime() {
    return parsingstarttime;
  }

  public void setParsingstarttime(final long parsingstarttime) {
    this.parsingstarttime = parsingstarttime;
  }
  
  public int getMemoryConsumptionMB() {
	  return this.memoryConsumptionMB;
  }
  
  public void setMemoryConsumptionMB(int memoryConsumptionMB) {
	  this.memoryConsumptionMB = memoryConsumptionMB;
  }

}
