package com.ericsson.eniq.etl.ebinary.fileformat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Iterator;

import org.junit.Test;


public class SubDataBlockTest {

	@Test
	public void createBank(){
		SubDataBlock bank = new SubDataBlock();
		assertNotNull(bank);
	}
	
	@Test
	public void addKey_1(){
		SubDataBlock bank = new SubDataBlock();
		String key = "C0001";
		bank.addKey(key);
		bank.addKey("C0002");
		Row expectedRow = new Row();
		expectedRow.addKey("C0001");
		expectedRow.addKey("C0002");
		SubDataBlock expectedBank = new SubDataBlock();
		expectedBank.add(expectedRow);
		
		for(int i = 0; i < bank.size(); i++){
			System.out.println(i+" = "+bank.get(i).toString());
		}
		assertEquals(expectedBank, bank);
	}

	@Test
	public void addKey_2(){
		SubDataBlock bank = new SubDataBlock();
		String key = "C0001";
		bank.addKey(key);
		bank.addKey("C0002");
		bank.addKey("C0003_0");
		bank.addKey("C0004_0");
		
		
		Row expectedRow = new Row();
		expectedRow.addKey("C0001");
		expectedRow.addKey("C0002");
		expectedRow.addKey("C0003_0");
		expectedRow.addKey("C0004_0");
		SubDataBlock expectedBank = new SubDataBlock();
		expectedBank.add(expectedRow);
		
		for(int i = 0; i < bank.size(); i++){
			System.out.println(i+" = "+bank.get(i).toString());
		}
		assertEquals(expectedBank, bank);
	}

	@Test
	public void addKey_3(){
		SubDataBlock bank = new SubDataBlock();
		String key = "C0001";
		bank.addKey(key);
		bank.addKey("C0002");
		bank.addKey("C0003_0");
		bank.addKey("C0004_0");
		bank.addKey("C0004_1");
		
		
		Row expectedRow1 = new Row();
		expectedRow1.addKey("C0001");
		expectedRow1.addKey("C0002");
		expectedRow1.addKey("C0003_0");
		expectedRow1.addKey("C0004_0");
		Row expectedRow2 = new Row();
		expectedRow2.addKey("C0001");
		expectedRow2.addKey("C0002");
		expectedRow2.addKey("C0003_0");
		expectedRow2.addKey("C0004_1");
		SubDataBlock expectedBank = new SubDataBlock();
		expectedBank.add(expectedRow1);
		expectedBank.add(expectedRow2);
		
		for(int i = 0; i < bank.size(); i++){
			System.out.println(i+" = "+bank.get(i).toString());
		}
		assertEquals(expectedBank, bank);
	}

	@Test
	public void addKey_4(){
		SubDataBlock bank = new SubDataBlock();
		String key = "C0001";
		bank.addKey(key);
		bank.addKey("C0002");
		bank.addKey("C0003_0");
		bank.addKey("C0004_0");
		bank.addKey("C0003_1");		
		bank.addKey("C0004_1");
		bank.addKey("C0005");
		
		
		Row expectedRow1 = new Row();
		expectedRow1.addKey("C0001");
		expectedRow1.addKey("C0002");
		expectedRow1.addKey("C0003_0");
		expectedRow1.addKey("C0004_0");
		expectedRow1.addKey("C0005");
		Row expectedRow2 = new Row();
		expectedRow2.addKey("C0001");
		expectedRow2.addKey("C0002");
		expectedRow2.addKey("C0003_1");
		expectedRow2.addKey("C0004_1");
		expectedRow2.addKey("C0005");
		SubDataBlock expectedBank = new SubDataBlock();
		expectedBank.add(expectedRow1);
		expectedBank.add(expectedRow2);
		
		for(int i = 0; i < bank.size(); i++){
			System.out.println(i+" = "+bank.get(i).toString());
		}
		assertEquals(expectedBank, bank);
	}

	@Test
	public void addKey_5(){
		System.out.println("addkey_5 - 4 Rows");
		SubDataBlock bank = new SubDataBlock();
		String key = "C0001";
		bank.addKey(key);
		bank.addKey("C0002");
		bank.addKey("C0003_0");
		bank.addKey("C0004_0");
		bank.addKey("C0003_1");		
		bank.addKey("C0004_1");
		bank.addKey("C0005");
		bank.addKey("C0006_0");
		bank.addKey("C0006_1");
		
		
		Row expectedRow1 = new Row();
		expectedRow1.addKey("C0001");
		expectedRow1.addKey("C0002");
		expectedRow1.addKey("C0003_0");
		expectedRow1.addKey("C0004_0");
		expectedRow1.addKey("C0005");
		expectedRow1.addKey("C0006_0");
		Row expectedRow2 = new Row();
		expectedRow2.addKey("C0001");
		expectedRow2.addKey("C0002");
		expectedRow2.addKey("C0003_1");
		expectedRow2.addKey("C0004_1");
		expectedRow2.addKey("C0005");
		expectedRow2.addKey("C0006_0");
		Row expectedRow3 = new Row();
		expectedRow3.addKey("C0001");
		expectedRow3.addKey("C0002");
		expectedRow3.addKey("C0003_0");
		expectedRow3.addKey("C0004_0");
		expectedRow3.addKey("C0005");
		expectedRow3.addKey("C0006_1");
		Row expectedRow4 = new Row();
		expectedRow4.addKey("C0001");
		expectedRow4.addKey("C0002");
		expectedRow4.addKey("C0003_1");
		expectedRow4.addKey("C0004_1");
		expectedRow4.addKey("C0005");
		expectedRow4.addKey("C0006_1");
		SubDataBlock expectedBank = new SubDataBlock();
		expectedBank.add(expectedRow1);
		expectedBank.add(expectedRow2);
		expectedBank.add(expectedRow3);
		expectedBank.add(expectedRow4);
		
		for(int i = 0; i < bank.size(); i++){
			System.out.println(i+" = "+bank.get(i).toString());
		}
		assertEquals(expectedBank, bank);
	}

	@Test
	public void addKey_6(){
		System.out.println("addkey_6 - 4 Rows");
		SubDataBlock bank = new SubDataBlock();
		String key = "C0001";
		bank.addKey(key);
		bank.addKey("C0002");
		bank.addKey("C0003_0");
		bank.addKey("C0004_0");
		bank.addKey("C0003_1");		
		bank.addKey("C0004_1");
		bank.addKey("C0005");
		bank.addKey("C0006_0");
		bank.addKey("C0006_1");
		bank.addKey("C0007_1_0");
		bank.addKey("C0008_0");
		
		
		Row expectedRow1 = new Row();
		expectedRow1.addKey("C0001");
		expectedRow1.addKey("C0002");
		expectedRow1.addKey("C0003_0");
		expectedRow1.addKey("C0004_0");
		expectedRow1.addKey("C0005");
		expectedRow1.addKey("C0006_0");
		expectedRow1.addKey("C0008_0");
		Row expectedRow2 = new Row();
		expectedRow2.addKey("C0001");
		expectedRow2.addKey("C0002");
		expectedRow2.addKey("C0003_1");
		expectedRow2.addKey("C0004_1");
		expectedRow2.addKey("C0005");
		expectedRow2.addKey("C0006_0");
		expectedRow2.addKey("C0008_0");
		Row expectedRow3 = new Row();
		expectedRow3.addKey("C0001");
		expectedRow3.addKey("C0002");
		expectedRow3.addKey("C0003_0");
		expectedRow3.addKey("C0004_0");
		expectedRow3.addKey("C0005");
		expectedRow3.addKey("C0006_1");
		expectedRow3.addKey("C0007_1_0");
		expectedRow3.addKey("C0008_0");
		Row expectedRow4 = new Row();
		expectedRow4.addKey("C0001");
		expectedRow4.addKey("C0002");
		expectedRow4.addKey("C0003_1");
		expectedRow4.addKey("C0004_1");
		expectedRow4.addKey("C0005");
		expectedRow4.addKey("C0006_1");
		expectedRow4.addKey("C0007_1_0");
		expectedRow4.addKey("C0008_0");
		SubDataBlock expectedBank = new SubDataBlock();
		expectedBank.add(expectedRow1);
		expectedBank.add(expectedRow2);
		expectedBank.add(expectedRow3);
		expectedBank.add(expectedRow4);
		
		for(int i = 0; i < bank.size(); i++){
			System.out.println(i+" = "+bank.get(i).toString());
		}
		assertEquals(expectedBank, bank);
	}

	@Test
	public void addKey_7(){
		System.out.println("addkey_7 - 19 Rows");
		SubDataBlock bank = new SubDataBlock();
		bank.addKey("C0001");
		bank.addKey("C0002");
		bank.addKey("C0003");
		bank.addKey("C0004_0");
		bank.addKey("C0005_0");
		bank.addKey("C0006_0");
		bank.addKey("C0006_1");
		bank.addKey("C0006_2");
		bank.addKey("C0007_0");
		bank.addKey("C0008_0");
		bank.addKey("C0009_0");
		bank.addKey("C0010_0_0");
		bank.addKey("C0011_0_0");
		bank.addKey("C0010_0_1");
		bank.addKey("C0011_0_1");
		bank.addKey("C0012_0");
		bank.addKey("C0012_1");
		bank.addKey("C0012_2");
		bank.addKey("C0013_0");

		
		for(int i = 0; i < bank.size(); i++){
			System.out.println(i+" = "+bank.get(i).toString());
		}
		assertEquals(18, bank.size());
		assertTrue(isUnique(bank));
	}

	@Test
	public void addKey_8(){
		//This tests real data taken from the LCS Data block.
		System.out.println("addkey_8 - 32 Rows");
		SubDataBlock bank = new SubDataBlock();
		String[] key = {"C0691", "C0692_0", "C0693_0", "C0695_0", "C0696_0_0", "C0697_0_0", 
				"C0699_0_0", "C0700_0_0_0", "C0708_0_0_0", "C0709_0_0_0", "C0700_0_0_1", "C0708_0_0_1", 
				"C0709_0_0_1", "C0700_0_0_2", "C0708_0_0_2", "C0709_0_0_2", "C0700_0_0_3", "C0708_0_0_3", 
				"C0709_0_0_3", "C0710_0", "C0711_0", "C0713_0", "C0714_0_0", "C0715_0_0", 
				"C0717_0_0", "C0718_0_0_0", "C0727_0_0_0", "C0718_0_0_1", "C0727_0_0_1", "C0718_0_0_2", 
				"C0727_0_0_2", "C0718_0_0_3", "C0727_0_0_3", "C0731_0", "C0732_0", "C0733_0", 
				"C0734_0_0", "C0735_0_0", "C0736_0_0", "C0734_0_1", "C0735_0_1", "C0736_0_1", 
				"C0741_0", "C0742_0", "C0743_0", "C0744_0", "C0745_0", "C0746_0"};

		long startTime = Calendar.getInstance().getTimeInMillis();
		for(int i = 0; i < key.length; i++){
			bank.addKey(key[i]);
		}
		long stopTime = Calendar.getInstance().getTimeInMillis();

		long duration = stopTime - startTime;
		System.out.println("The time taken to process LCS for one subscriber (42 keys) = "+duration+"ms");
		for(int i = 0; i < bank.size(); i++){
			System.out.println(i+" = "+bank.get(i).toString());
		}
		assertEquals(32, bank.size());
		assertTrue(isUnique(bank));
	}

//	@Test
//	public void addKey_9(){
//		
//		SubDataBlock bank = new SubDataBlock();
//		String[] key = {"C0001", "C0002_0", "C0003_0", "C0004_0", "C0005_0", "C0006_0", "C0007_0A", "C0007_0B", "C0008_0A", "C0008_0B", "C0008_0C",
//				"C0002_1", "C0003_1", "C0004_1", "C0005_1", "C0006_1", "C0007_1A", "C0007_1B", "C0008_1A", "C0008_1B", "C0008_1C"};
//
//		for(int i = 0; i < key.length; i++){
//			bank.addKey(key[i]);
//		}
//		assertEquals(2, bank.size());
//		assertTrue(isUnique(bank));
//	}
		
//	@Test
//	public void getIdFromRowBank(){
//		SubDataBlock bank = new SubDataBlock();
//		String id = bank.getId();
//		assertEquals("0", id);
//	}

	@Test
	public void addKey_10(){
		System.out.println("addkey_7 - 19 Rows");
		SubDataBlock bank = new SubDataBlock();
		bank.addKey("C0001");
		bank.addKey("C0002");
		bank.addKey("C0003");
		bank.addKey("C0004_0");
		bank.addKey("C0005_0");
		bank.addKey("C0004_1");
		bank.addKey("C0005_1");
		bank.addKey("C0004_2");
		bank.addKey("C0005_2");
		bank.addKey("C0004_3");
		bank.addKey("C0005_3");
		bank.addKey("C0004_4");
		bank.addKey("C0005_4");
		bank.addKey("C0004_5");
		bank.addKey("C0005_5");
		bank.addKey("C0004_6");
		bank.addKey("C0005_6");
		bank.addKey("C0004_7");
		bank.addKey("C0005_7");
		bank.addKey("C0004_8");
		bank.addKey("C0005_8");
		bank.addKey("C0004_9");
		bank.addKey("C0005_9");
		bank.addKey("C0004_10");
		bank.addKey("C0005_10");
		bank.addKey("C0004_11");
		bank.addKey("C0005_11");

		
		for(int i = 0; i < bank.size(); i++){
			System.out.println(i+" = "+bank.get(i).toString());
		}
		assertEquals(12, bank.size());

	}

	/**
	 * This is a test to check that each row is unique. True if unique, 
	 * otherwise False is returned.
	 * @param bank
	 * @return
	 */
	private boolean isUnique(SubDataBlock bank){
		//Compare each row to every other row to ensure that all rows in bank are unique.
		int rowNum = 1;
		Iterator<Row> remianingRows = null;
		while(bank.size() > 0){
			Row row = bank.get(0);
			bank.remove(row);
			remianingRows = bank.iterator();
			int iterNum = 1;
			while(remianingRows.hasNext()){
				Row next = remianingRows.next();
				if(row == next){
					return false;
				}
				iterNum++;
			}
			rowNum++;
		}
		return true;
	}
	
}
