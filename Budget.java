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
public class Budget {

	
	public BigDecimal beginingBal = new BigDecimal(0);
	
	public Boolean finalized = false;
	
	public String file;
	
	public String[] fileName;
	
	public LinkedList<LEntry> ledgerList = new LinkedList<LEntry>();
	
	public LinkedList<Ledger> ledgerStore = new LinkedList<Ledger>();
	
	public LinkedList<BEntry> budgetList = new LinkedList<BEntry>();
	
	public BigDecimal totalAcct, totalBudg, totalExp;
	
	public String[] categories;

	private String[] sanitizedResults;
	
	public static boolean createLedger = true;
	
	/**
	 * Loads a ledger.
	 * 
	 * @param fileName  Filename to load.  
	 * @param blink
	 */
	public Budget(String[] fileName) {
		new File("data").mkdir();
		
		this.fileName = fileName;
		
		file = "data/" + fileName[0] + "-" + fileName[1] + "-" + fileName[2] + ".txt";
		
		//Set up default budget items;
		this.categories = Data.getCategories();
		for(int i=0; i<categories.length; i++){
			budgetList.add(new BEntry(categories[i]));
		}
		
		//set up way to read ledger entries and store in budgetlist
		readLedgers();
		
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
							
							for(int i=0; i<budgetList.size(); i++){
								BEntry temp = budgetList.get(i);
								if(temp.getCategory().equals(lineArray[0])){
									temp.setBudgeted(lineArray[1]);
									temp.setRollover(lineArray[2]);
								}
							}
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
				for(int i=0; i<budgetList.size(); i++){
					BEntry temp = budgetList.get(i);
					pr.println(temp.category + "," + temp.budgeted + "," + temp.rollover);
				}
				
				pr.close();
			}catch (UnsupportedEncodingException | FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}

	//TODO read ledgers
	private void readLedgers() {
		String[] getAccts = Data.getAccts();
		String[] acctTitles = new String[getAccts.length - 1];
		for(int i=0; i<acctTitles.length; i++){
			acctTitles[i] = getAccts[i];
		}
		
		for(int i=0; i<acctTitles.length; i++){
			String[] tempFileName = fileName;
			tempFileName[2] = acctTitles[i];
			
			ledgerStore.add(new Ledger(tempFileName));
		}
		
		//Get each LedgerList
		for(int i=0; i<ledgerStore.size(); i++){
			//System.out.println(ledgerStore.get(i).beginingBal + " " + fileName[2]);
			LinkedList<LEntry> ledgerList = ledgerStore.get(i).ledgerList;
			
			//Get each entry from each ledgerList
			for(int j=0; j<ledgerList.size(); j++){
				LEntry entry = ledgerList.get(j);
				
				for(int k=0; k<budgetList.size(); k++){
					if(budgetList.get(k).getCategory().equals(entry.category)){
						budgetList.get(k).addExpense(entry.amount);
					}
				}
				
			}
			
		}
	}
	public void addEditItems(String resultsBudget, int index){

		
		
		String sanitizedResults = sanitizedResults(resultsBudget);
		
		
		if(index<budgetList.size()){
			budgetList.get(index).setBudgeted(sanitizedResults);
		}
		
		
		savefile();
	}
	/**
	 * Sanitize results to make sure they are valid.
	 * Remove all commas form use input.
	 * 
	 * @param resultsBudget
	 * @return
	 */
	private String sanitizedResults(String resultsBudget) {
		// TODO Auto-generated method stub
		String sanitizedResults;
		
		sanitizedResults = resultsBudget.replace(",", ";");
		
		try{
			float tempFloat = Float.parseFloat(sanitizedResults);
		}catch(Exception e){
			sanitizedResults = "0";
			JOptionPane.showMessageDialog(null, resultsBudget + " is not a number", "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		
		return sanitizedResults;
	}

	
	public String totalAvailable() {
		BigDecimal total = new BigDecimal("0");
		
		total = total.add(totalAcct);
		total = total.subtract(totalBudg);
		
		return total.toString();
	}

	public String totalBudg() {
		BigDecimal total = new BigDecimal("0");

		for(int i=0; i<budgetList.size(); i++){
			total = total.add(budgetList.get(i).getBudgetedDB());
		}
		
		totalBudg = total;
		
		return total.toString();
	}

	public String totalExp() {
		BigDecimal total = new BigDecimal("0");
		
		for(int i=0; i<budgetList.size(); i++){
			total = total.add(budgetList.get(i).getExpensed());
		}
		
		totalExp = total;
		
		return total.toString();
	}

	public String totalAcctBal() {
		BigDecimal total = new BigDecimal("0");
		
		for(int i=0; i<ledgerStore.size(); i++){
			total = total.add(ledgerStore.get(i).beginingBal);
		}
		
		totalAcct = total;
		
		return total.toString();
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
			
			
			for(int i=0; i<budgetList.size(); i++){
				pr.println(budgetList.get(i).toString());
			}
			
			pr.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
