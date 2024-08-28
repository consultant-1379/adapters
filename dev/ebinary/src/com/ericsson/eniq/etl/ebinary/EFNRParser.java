package com.ericsson.eniq.etl.ebinary;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.distocraft.dc5000.etl.parser.Parser;
import com.distocraft.dc5000.etl.parser.SourceFile;
import com.ericsson.eniq.etl.ebinary.fileformat.Block;
import com.ericsson.eniq.etl.ebinary.fileformat.Common;

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
public class EFNRParser implements Parser {

  private static final int RECORD_TYPE_TRANSLATIONAL = '2';

  /**
   * Part of the file, the header block. Here comes some information to all
   * datarows
   */
  private Block headerBlock;

  /**
   * Logger for logging
   */
  private Logger log;

  // ***************** parameters ****************************

  private String PATHTOPROPERTIES;

  private Properties HEADER_PROP = null;

  private Properties TRANSLATIONAL_PROP = null;

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
  public void init(Main main, String techPack, String setType, String setName, String wn) {
    this.mainParserObject = main;
    this.techPack = techPack;
    this.setType = setType;
    this.setName = setName;
    this.status = 1;
    this.workerName = wn;

    String logWorkerName = "";
    if (workerName.length() > 0)
      logWorkerName = "." + workerName;

    log = Logger.getLogger("etl." + techPack + "." + setType + "." + setName + ".parser.EFNRPARSER" + logWorkerName);
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
  public void parse(final SourceFile sf, final String techPack, final String setType, final String setName)
      throws Exception {

    File dir = new File(System.getProperty("CONF_DIR"));
    String path = dir.getAbsolutePath() + File.separator + "efnrParserConf";
    PATHTOPROPERTIES = sf.getProperty("pathToProperties", path);
    DATETIME_ID = sf.getProperty("DATETIME_ID", "20<A13>-<A15>-<A17> <A19>:<A21>:00");
    HEADER_PROP = Common.getInstructions(PATHTOPROPERTIES, "headerBlock.prop");
    TRANSLATIONAL_PROP = Common.getInstructions(PATHTOPROPERTIES, "translationData.prop");
    InputStream is = sf.getFileInputStream();
    try {

      readHeader(is);

      readTranslationalData(sf, is);

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
  private void readTranslationalData(SourceFile sf, InputStream is) throws Exception {

    MeasurementFile mFile = Main.createMeasurementFile(sf, "TRANSLATIONDATA", techPack, setType, setName,
        this.workerName, this.log);

    try {
      int firstOfRecord = is.read();
      while (firstOfRecord == RECORD_TYPE_TRANSLATIONAL) {
        int[] firstThree = new int[3];
        firstThree[0] = firstOfRecord;
        firstThree[1] = is.read();
        firstThree[2] = is.read();
        int leng = firstThree[2] + firstThree.length;
        int[] content = new int[leng];

        for (int i = 0; i < content.length; i++) {
          if (i < firstThree.length)
            content[i] = firstThree[i];
          else
            content[i] = is.read();
        }
        Block translationalBlock = new Block(PATHTOPROPERTIES, TRANSLATIONAL_PROP, content);
        SaveData(mFile, sf, translationalBlock);
        firstOfRecord = is.read();
      }
    } catch (Exception e) {
      log.log(Level.WARNING, "Error while parsing translation data", e);
    }
    try {
      mFile.close();
      cleanMyCache();
    } catch (Exception e) {
      log.log(Level.WARNING, "Error closing MeasurementFile", e);
    }

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
      for (int i = 0; i < leng; i++)
        content[i] = is.read();

      headerBlock = new Block(PATHTOPROPERTIES, HEADER_PROP, content);
    } catch (Exception e) {
      log.warning("Error while parsing header data");
    }
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
        String value = headerValues.get(key);
        datetimeID = datetimeID.replaceAll("<" + key + ">", value);
      }

    } catch (Exception e) {
      log.info("Error while trying to format DATETIME_ID.");
      datetimeID = null;
    }
    return datetimeID;
  }

  /**
   * Saves the data from cached temp map.
   * 
   * @param sf
   */
  private void SaveData(MeasurementFile mFile, SourceFile sf, Block translationalData) {

    // These are the "added columns", which will be added to each row
    Map<String, String> addedColumns = new HashMap<String, String>();
    addedColumns.put("filename", sf.getName());
    SimpleDateFormat sdf = new SimpleDateFormat("Z");
    addedColumns.put("JVM_TIMEZONE", sdf.format(new Date()));
    addedColumns.put("DIRNAME", sf.getDir());
    addedColumns.put("DATETIME_ID", formatDateTimeID());

    log.fine("Added columns are: " + addedColumns);

    try {

      mFile.addData(addedColumns);

      Map<String, String> headerValues = headerBlock.getSimpleValues();
      mFile.addData(headerValues);

      Map<String, String> translationalValues = translationalData.getSimpleValues();
      mFile.addData(translationalValues);

      mFile.saveData();

    } catch (Exception e) {
      log.log(Level.WARNING, "Worker parser failed to exception while trying to save data ", e);
    }
  }
}
