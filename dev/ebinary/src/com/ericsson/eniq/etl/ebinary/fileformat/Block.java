package com.ericsson.eniq.etl.ebinary.fileformat;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Class representing block. Block is part of file, which holds counter values.
 * Counter names are described at Alcatel-Lucent GSM BSC Counters Block Layout
 * document. BlockInstructions (property files) are made based on that document.
 * 
 * @author etogust
 * 
 */
public class Block {

  
  private Properties instructions;  
  
  private String path;
  
  /**
   * Content bytes
   */
  private int[] blockContent = null;

  private Map<String, String> simpleValues = null;


  /**
   * Constructs the block and sets the header, entityselector and content
   * nicely.
   * 
   * @param Properties
   * @param content
   */
  public Block(String path, Properties prop, int[] content) throws Exception {
    setPath (path);
    setInstructions(prop);
    setBlockContent(content);
    parseBlockContent ();
  }

  /**
   * This method initialises the block by extracting all the possible data from
   * actual content. Simple and table data
   * 
   * @param arr
   */
  private void parseBlockContent() {
    if (Common.log != null)
      Common.log.fine("Starting to parse content of block");
    
    simpleValues = new HashMap<String, String>();
//    vectorValues = new HashMap<String, String>();

    Properties prop = getInstructions();
    if (prop == null){
      Common.log.severe("Could not parse content of block because instructions on how to parse it is missing.");
      return;
    }
    
    Common.putDataToMap(getPath (), prop, simpleValues, getBlockContent(),"");
//    Common.putTableDataToMap(tableDataDef, vectorCounterValues, getBlockContent());
  }


  private void setBlockContent(int[] blockContent) {
    this.blockContent = blockContent;
  }

  private int[] getBlockContent() {
    return this.blockContent;
  }

  private void setInstructions(Properties instructions) {
    this.instructions = instructions;
  }

  private Properties getInstructions() {
    return this.instructions;
  }
  
  public Map<String, String> getSimpleValues() throws Exception {
    return this.simpleValues;
  }

  /**
   * @return the path
   */
  private String getPath() {
    return path;
  }

  
  /**
   * @param path the path to set
   */
  private void setPath(String path) {
    this.path = path;
  }
  
}
