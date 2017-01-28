import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.LinkedList;

import java.awt.*;
import java.awt.Desktop.Action;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Class to create the Ledger View.
 *
 */
public class Ledger {
	
	public BigDecimal beginingBal = new BigDecimal(0);
	
	public Boolean finalized = false;
	
	public String file;
	
	public String[] fileName;
	
	public LinkedList<LEntry> ledgerList = new LinkedList<LEntry>();

	private String[] sanitizedResults;
	
	public static boolean createLedger = true;
	
	/**
	 * Loads a ledger.
	 * 
	 * @param fileName  Filename to load.  
	 * @param blink
	 */
	public Ledger(String[] fileName) {
		new File("data").mkdir();
		
		this.fileName = fileName;
		
		file = "data/" + fileName[0] + "-" + fileName[1] + "-" + fileName[2] + ".txt";
		
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			try {
				if(br.ready())
					try {
						String firstLine = br.readLine();
						beginingBal = new BigDecimal(firstLine);
						
						String secondline = br.readLine();
						if(secondline.equals("y"))
							finalized = true;
						
						while(br.ready()){
							String line = br.readLine();
							String lineArray[] = line.split(",");
							addLine(lineArray);
						}
					} catch (IOException e) {
						System.out.println("error reading");
					}
			} catch (IOException e) {
				System.out.println("Error");
			}
			
			
			
		} catch (FileNotFoundException e) {
			try {
				PrintWriter pr = new PrintWriter(file, "UTF-8");
				pr.println("0");
				pr.println("n");
				pr.close();
			}catch (UnsupportedEncodingException | FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}



	/**
	 * Sanitize results to make sure they are valid.
	 * Remove all commas form use input.
	 * 
	 * @param resultsArray
	 * @return
	 */
	public String[] sanitizedResults(String[] resultsArray) {
		// TODO Auto-generated method stub
		String[] sanitizedResults = new String[resultsArray.length];
		
		for(int i=0; i<resultsArray.length; i++)
			sanitizedResults[i] = resultsArray[i].replace(",", ";");
		sanitizedResults[4] = sanitizedResults[4].replaceAll(";", "");
		
		try{
			float tempFloat = Float.parseFloat(sanitizedResults[4]);
		}catch(Exception e){
			sanitizedResults[4] = "0";
			JOptionPane.showMessageDialog(null, resultsArray[4] + " is not a number", "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		return sanitizedResults;
	}

	/**
	 * Add a line to the Ledger.
	 * @param lineArray
	 */
	public void addLine(String[] lineArray) {
		ledgerList.add(new LEntry(lineArray));
		
	}

	/**
	 * Updated starting balance from another ledger.  For Finalizing account.
	 * @param tempStarting
	 */
	public void updateStarting(BigDecimal tempStarting){

		beginingBal = tempStarting;
		
		savefile();
	}
	
	/**
	 * Add pending items.  For right panel calculations
	 * @return
	 */
	public BigDecimal pendingItems() {
		BigDecimal tempPending = new BigDecimal(0);
		
		for(int i=0; i<ledgerList.size(); i++){
			if(!ledgerList.get(i).cleared)
				tempPending = tempPending.add(ledgerList.get(i).amount);
		}
		return tempPending;
	}

	/**
	 * Add cleared items.  For right panel calculations.
	 * @return
	 */
	public BigDecimal clearedItems() {
		BigDecimal tempTotal = new BigDecimal(0);
		
		for(int i=0; i<ledgerList.size(); i++){
			if(ledgerList.get(i).cleared)
				tempTotal = tempTotal.add(ledgerList.get(i).amount);
		}
		return tempTotal;
	}

	/**
	 * Saves the data in the CSV format.
	 */
	public void savefile(){
		PrintWriter pr;
		try {
			pr = new PrintWriter(file, "UTF-8");
			pr.println(beginingBal);
			if(finalized)
				pr.println("y");
			else
				pr.println("n");
			
			
			for(int i=0; i<ledgerList.size(); i++){
				pr.println(ledgerList.get(i).toString());
			}
			
			pr.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public LinkedList<LEntry> getLedgerList() {
		return ledgerList;
	}



	public BigDecimal getBeginingBal() {
		return beginingBal;
	}



	public BigDecimal getBankBal() {
		BigDecimal clearedItems = clearedItems();
		return beginingBal.add(clearedItems);
	}



	public BigDecimal getAvailableBal() {

		BigDecimal clearedItems = clearedItems();
		BigDecimal pendingItems = pendingItems();
		return beginingBal.add(clearedItems.add(pendingItems));
	}



	public void setFinalized(boolean b) {
		finalized = b;
	}
	
	public Boolean getFinalized(){
		return finalized;
	}



	public void finalizeAcct() {

		String[] tempFileName = Data.nextFileName(fileName);
		Ledger tempLedger = new Ledger(tempFileName);
		tempLedger.updateStarting(getBankBal());
		
		for(int i=0; i<ledgerList.size(); i++){
			if(!ledgerList.get(i).cleared){
				tempLedger.ledgerList.add(ledgerList.get(i));
				tempLedger.savefile();
			}
		}
	}



	public void addEditItem(String[] resultsArray, int index) {

		String[] sanitizedResults = sanitizedResults(resultsArray);
		
		if(index<ledgerList.size()){
			ledgerList.remove(index);
		}
		
		if(index>0)
			ledgerList.add(index, new LEntry(sanitizedResults));
		
		savefile();
	}

}
