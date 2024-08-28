package com.ericsson.eniq.etl.ebinary.fileformat;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * <b>Copyright:</b> <i>Copyright (c) 2009</i> <br>
 * <b>Company:</b> <i>LM Ericsson Ltd.</i>
 * <p>
 * This Class is responsible for creating rows of keys based on the key added.
 * Here is a description of the algorithm used to expand the rows:<p>
 * KeyBank contains a list of KeyRows. Each Row represents a row of data to be written to the database.
 * Each Row has a status called "active" this is used to determine if the row should be considered when 
 * adding a new key. <br>
 * The first action is to read the key. If the suffix count (_0_0 = 2, _0 = 1) of the key increases then 
 * add the key to all rows in the current range.
 * <br>If the suffix value increases (_0_0 -> _0_1) then this 
 * should be put into a new Row. So for all Row's in active range determine the insertion point of the new 
 * key, copy the Row to that point, paste this into the KeyBank, add the new key, set the copied 
 * Row to be inactive (active = false). Refer to the example shown below:
 * <br>
 * <ol>
 *    <li>KeyBank = [["C0001", "C0002_0", "C0003_0"]*].
 *    <li>Add key, "C0003_1".
 *    <li>Determine the insertion point. key should be inserted in place of the "C0003_0".
 *    <li>New Row created = ["C0001", "C0002_0"].
 *    <li>Add the new key, ["C0001", "C0002_0", "C0003_1"].
 *    <li>Add new Row to KeyBank and set copied Row to inactive, 
 *    [["C0001", "C0002_0", "C0003_0"], ["C0001", "C0002_0", "C0003_1"]*]
 *</ol>
 *    (*, signifies KeyRows in active range)
 * If, for example, the key "C0003_1_0" was now added then the first Row in active range is queried to 
 * see if it should be added. It should be added (the suffix count increases), so the result now = 
 * [["C0001", "C0002_0", "C0003_0"], ["C0001", "C0002_0", "C0003_1", "C0003_1_0"]*].
 * <br>
 * If the suffix count decreases (_0_0 -> _0), then all rows are set to be in active range and the key is 
 * added to all rows in active range. 
 * i.e.
 * <ol>
 *    <li>KeyBank = [["C0001", "C0002_0", "C0003_0"], ["C0001", "C0002_0", "C0003_1", "C0003_1_0"]*].
 *    <li>Add key, "C0004".
 *    <li>Suffix count has decreased...
 *    <li>Make all in Active Range.
 *    <li>KeyBank = [["C0001", "C0002_0", "C0003_0"]*, ["C0001", "C0002_0", "C0003_1", "C0003_1_0"]*]
 *    <li>Add the new key...
 *    <li>KeyBank = [["C0001", "C0002_0", "C0003_0", "C0004"]*, ["C0001", "C0002_0", "C0003_1", "C0003_1_0", "C0004"]*]
 * </ol>
 * @author eeikbe - Kenneth Browne
 *
 */
public class SubDataBlock extends ArrayList<Row> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id = "0";
	
	/**
	 * This method adds a key to the SubDataBlock. It performs a 
	 * number of checks to determine where the new key is inserted.
	 * <p>
	 * There are 3 different ways to handle the writing of a key:
	 * <ol>
	 *    <li>Add the key to ALL KeyRows in the current range.
	 *    <li>Reset ALL keyRows to be in active range, then add the key to ALL KeyRows.
	 *    <li>From the first row in the current active range determine where the key 
	 *    must be inserted and copy that Row, add it to the KeyBank, then 
	 *    add the key to the new Row and set the copied Row to inactive. 
	 * </ol>
	 * @param key - The key to add to the KeyBank
	 */
	public void addKey(String key){
		//first time make sure there is a Row.
		if(this.size() == 0){
			this.add(new Row());
		}
		Row firstRow = getFirstKeyRowInRange();

		switch(firstRow.handleKey(key)){
		case WRITE_KEY:
			addKeyToAllRowsInRange(key);
			break;
		case WRITE_KEY_IGNORE_ACTIVE:
			addKeyToAllRows(key);
			break;
		case RESET_RANGE_WRITE_KEY:
			//Reset all rows to be active and add key to all rows
			for(Row row : this){
				row.setActive(true);
				row.addKey(key);
			}
			break;
		case COPY_RANGE_WRITE_KEY:
			//Copy all rows in active range and add them to the bank. 
			//set the old rows inactive and set new rows active.
			List<Row> tempRows = new ArrayList<Row>();
			for(Row row : this){
				if(row.isActive()){
				tempRows.add(row.getInsertAfter(key));//copy new row...
				row.setActive(false);    //de-activate old row
				}
			}
			//copy into row bank...
			for(Row row : tempRows){
				this.add(row);
			}
			tempRows = null;
			addKeyToAllRowsInRange(key);
			break;
		}
	}

	
	private void addKeyToAllRows(String key) {
		for(Row row : this){
			row.addKeyWithCheck(key);
		}		
	}


	/**
	 * This method adds the key to all Rows in active range.
	 * @param key - the key to add.
	 */
	private void addKeyToAllRowsInRange(String key) {
		for(Row row : this){
			if(row.isActive()){
				row.addKey(key);
			}
		}
	}
	
	/**
	 * This method gets the first Row in active range from 
	 * the SubDataBlock.
	 * @return The first Row in range.
	 */
	private Row getFirstKeyRowInRange(){
		for(Row row : this){
			if(row.isActive()){
				return row;
			}
		}
		return null;
	}


	/**
	 * Get the id of the SubDataBlock.
	 * @return
	 */
	public String getId() {
		return this.id;
	}


	/**
	 * Add a Key into the SubDataBlock
	 * @param key
	 * @param id
	 */
	public void addKey(String key, String id) {
		this.id = id;
		this.addKey(key);
	}


	/**
	 * Set the Id of the Subdata Block
	 * @param blockId
	 */
	public void setId(String blockId) {
		this.id = blockId;
	}
	
	/**
	 * Create a copy of the SubDataBlock. Note this is not 
	 * a copy of the reference, it's a new object.
	 * @return
	 */
	public SubDataBlock copy(){
		SubDataBlock rb = new SubDataBlock();
		for(Row row : this){
			rb.add(row.copy());
		}
		return rb;
	}
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		
		for(int i = 0; i < this.size(); i++){
			buf.append(this.get(i));
			if(i < this.size()-1){
				buf.append(", \n  ");
			}
		}
		buf.append("]");
		return buf.toString();
	}


//	/**
//	 * Set all rows active. 
//	 * @param key 
//	 */
//	public void setAllRowsActive(String key) {
//		for(Row row : this){
//			row.setActive(key);
//		}
//	}
}
