package com.ericsson.eniq.etl.ebinary.fileformat;

import java.util.ArrayList;
import java.util.List;

public class DataBlock extends ArrayList<SubDataBlock> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param key
	 * @param instructions
	 */
	public void addKey(String key, String instructions) {
		String[] id = getId(instructions);
		for(String blockId: id){
			if(this.size() == 0){
				//This is the first key added to the rows.
				SubDataBlock rb = new SubDataBlock();
				rb.addKey(key, blockId);
				this.add(rb);
			}
			else if(blockId.equals("0")){
				//This is a common key to be added to ALL rows...
				for(SubDataBlock subDataBlock : this){
					subDataBlock.addKey(key);
				}
			}else{
				//check if there is a datablock for this key...
				boolean found = false;
				for(SubDataBlock subDataBlock : this){
					if(subDataBlock.getId().equals(blockId)){
						found = true;
						subDataBlock.addKey(key);
						break;
					}
				}
				if(!found){
					//Need to create a new SubDataBlock for this key,
					//but need to build it using previously added common keys.
					SubDataBlock rb1 = null;
					for(SubDataBlock subDataBlock : this){
						if(subDataBlock.getId().equals("0")){
							rb1 = subDataBlock.copy();
							rb1.setId(blockId);
							break;
						}
					}
					if(rb1 == null){
						rb1 = new SubDataBlock();
					}
					rb1.addKey(key, blockId);
					this.add(rb1);
				}
			}
		}
	}

	/**
	 * 
	 * @param instructions
	 * @return
	 */
	public String[] getId(String instructions){
		String[] indexString = instructions.split(",");
		String returnValue = "0";
		if(indexString.length > 3){
			returnValue = indexString[3].trim();
		}
		return returnValue.split("[+]");
	}

	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		
		for(int i = 0; i < this.size(); i++){
			buf.append(this.get(i));
			if(i < this.size()-1){
				buf.append(", \n ");
			}
		}
		buf.append("]");
		return buf.toString();
	}

	/**
	 * 
	 * @return
	 */
	public List<Row> flatten() {
		List<Row> row = new ArrayList<Row>();
		if(this.size() > 1){
			for(int i = 0; i < this.size(); i++){
				if(this.get(i).getId().equals("0")){
					this.remove(i);
				}
			}			
		}
		for(SubDataBlock rb: this){
			for(Row kr: rb){
				row.add(kr);
			}
		}
		return row;
	}

	public boolean duplicateCheck() {
		List<Row> rows = this.flatten();
		for(int i = 0; i < rows.size(); i++){
			for(int j = i+1; j < rows.size(); j++){
				if(rows.get(i).equals(rows.get(j))){
					return true;
				}
			}
		}
		return false;
	}

}
