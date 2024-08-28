package com.ericsson.eniq.etl.ebinary.fileformat;


import java.util.ArrayList;


/**
 * <p>
 * <b>Copyright:</b> <i>Copyright (c) 2009</i> <br>
 * <b>Company:</b> <i>LM Ericsson Ltd.</i>
 * <p>
 * 
 * @author eeikbe - Kenneth Browne
 *
 */
public class Row extends ArrayList<String>{

	private int suffixCount = 0;
	private int suffixValue = -1;
	
	/**
	 * 
	 */
	private boolean active = true;
	
	
	private static final String SUFFIX_REG = "^[_0-9]*";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to add a key to the row. It also calculates 
	 * and stores the suffix count and suffix value.
	 * @param key - The Key to be stored in the row.
	 */
	public void addKey(String key) {
		suffixCount = extractSuffixCount(key);
		if(suffixCount > 0){
			suffixValue = convertSuffix(key);
		}
		this.add(key);
	}
	

	private int extractSuffixCount(String key){
		int answer = 0;
		if(key.contains("_")){
			answer = (key.split("_").length)-1;
		}else{
			answer = 0;
		}
		return answer;
	}

	/**
	 * 
	 * @param instructions
	 * @return
	 */
	public String getSubdataBlockId(String instructions){
		String[] indexString = instructions.split(",");
		String returnValue = "0";
		if(indexString.length > 3){
			returnValue = indexString[3].trim();
		}
		return returnValue;
	}

	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		
		for(int i = 0; i < this.size(); i++){
			buf.append(this.get(i));
			if(i < this.size()-1){
				buf.append(", ");
			}
		}
		buf.append("]");
		return buf.toString();
	}

	/**
	 * This method is given a key which will be inserted into the row. 
	 * It then calculates the insertion point and returns the list 
	 * (without the key added, this should be performed in another step).
	 * so that the new key can be added.<p>
	 * If the following is used as input:<br>
	 * <br>subdata = ["C0001", "C0002_0"]
	 * <br>key = "C0002_1"
	 * <br>Then the returned list is: ["C0001"]
	 * @param key - The key to be added to the row.
	 * @return
	 */
	public Row getInsertAfter(String key) {
		String keyNoSuffix = key.substring(0, 5);
		int position = 0;
		for(int i = this.size()-1; i > 0; i--){
			if(this.get(i).startsWith(keyNoSuffix)){
				position = i;
				break;
			}
		}
		//If the key was not found then need to return the original list.
		if(position == 0){
			return this;
		}
		Row answer = new Row();
		for(int i = 0; i < position; i++){
			answer.addKey(this.get(i));
		}
		return answer;
	}

	/**
	 * Returns the active state of the row.
	 * @return
	 */
	public boolean isActive() {
		return this.active;
	}

	/**
	 * Sets the active state of the row.
	 * @param active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * This method determines how the key should be added to the 
	 * KeyBank.
	 * <ol>
	 * 	    <li>If the key to be added has a larger suffix count than the 
	 * last key in the Row, then this key should be added to the Row.
	 *      <li>If the suffix count of the key to be added != suffix count of the 
	 * last key in the Row, then the Active Range of all the KeyRows should be 
	 * reset to (active) and the key added to all KeyRows in active range.
	 *      <li>If the suffix of the key to be added is greater than the suffix 
	 * value of the last key in the Row, then all KeyRows in active range should 
	 * be copied and pasted into the KeyBank, all "old" KeyRows should be set to 
	 * inactive . Key should be added to ALL KeyRows in active range.
	 *     <li>If the suffix value of the key to be added == suffix value of the 
	 * last key in the Row, then this key should be added to the Row.
	 * </ol>
	 * @param key - The key to be added.
	 * @return
	 * <ul>
	 *     <li>WRITE_KEY - Write the key to all Row's in active range.
	 *     <li>RESET_RANGE_WRITE_KEY - Reset the active range of all KeyRows to be Active, 
	 *     and write Key to row.
	 *     <li>COPY_RANGE_WRITE_KEY - Copy all KeyRows in active range (from start of row 
	 *     to insertion point), set old rows to be inactive. Add the Key to 
	 *     all rows in active range.
	 * </ul>
	 * 
	 */
	public BlockENum handleKey(String key) {
		int count = 0;
		if(key.contains("_")){
			count = (key.split("_").length)-1;
		}

		if(count > suffixCount || this.size() == 0){
			return BlockENum.WRITE_KEY;
		}
		if(count != suffixCount){	
			String nakedKey = key.split("_")[0];
			for(String s:this){
				if(s.startsWith(nakedKey)){
					return BlockENum.COPY_RANGE_WRITE_KEY;
				}
			}
			if(key.contains("_")){
				String parentOfKey = key.split("_")[1];
				String lastKeyInRow = this.get(this.size()-1);
				if(lastKeyInRow.contains("_")){
					String lastKeyInRowParent = lastKeyInRow.split("_")[1];
					if(lastKeyInRowParent.equals(parentOfKey)){
						return BlockENum.WRITE_KEY_IGNORE_ACTIVE;
					}
				}					
			}
			return BlockENum.RESET_RANGE_WRITE_KEY; 
		}

		int suffix = convertSuffix(key);

		if(suffix > suffixValue){
			return BlockENum.COPY_RANGE_WRITE_KEY;
		}
		if(suffix == suffixValue){
			return BlockENum.WRITE_KEY;
		}
		return BlockENum.RESET_RANGE_WRITE_KEY;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	private int convertSuffix(String key){
		String suffix = "";
		if(key.contains("_")){
			int index = key.indexOf("_");
			suffix = key.substring(index);
		}
		if(suffix.contains("_")){
			return Integer.parseInt(suffix.replaceAll("_", ""));
		}else{
			return -1;
		}
	}
	
	/**
	 * Return a copy of the Row...
	 * @return
	 */
	public Row copy(){
		Row kr = new Row();
		for(String s : this){
			kr.addKey(s);
		}
		return kr;
	}

	/**
	 * Add a key but only if it's not already in the Row.
	 * @param key
	 */
	public void addKeyWithCheck(String key) {
		String nakedKey = key.split("_")[0];
		for(String s:this){
			if(s.startsWith(nakedKey)){
				return;
			}
		}
		this.addKey(key);
		this.setActive(true);
	}
}
