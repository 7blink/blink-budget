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

	public static Blink blink;
	
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
	public Budget(String[] fileName, Blink blink) {
		new File("data").mkdir();
		
		this.fileName = fileName;
		
		file = "data/" + fileName[0] + "-" + fileName[1] + "-" + fileName[2] + ".txt";
		
		//Set up default budget items;
		this.categories = Data.getCategories();
		for(int i=0; i<categories.length; i++){
			budgetList.add(new BEntry(categories[i]));
		}
		
		//TODO
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

	/**
	 * Updates Center Panel
	 */
	public void updateCenterPane() {

		JPanel center = new JPanel(new BorderLayout());
		
		//TODO use expanded columns.
		//String[] columnNames = {"#", "Category", "Budgeted", "Rollover", "Total", "Spent", "Available"};
		String[] columnNames = {"#", "Category", "Budgeted", "Spent", "Available"};
		
		//Object[][] data = new Object[budgetList.size()][7];
		Object[][] data = new Object[budgetList.size()][5];
		

		for(int i=0; i<budgetList.size(); i++){
			data[i][0] = (i+1) + "";
			//String[] tempArray = budgetList.get(i).returnArray();
			String[] tempArray = budgetList.get(i).returnSmallArray();
			for (int j=0; j<tempArray.length; j++){
				data[i][(j+1)] = tempArray[j];
			}
		}
		
		DefaultTableModel model = new DefaultTableModel(data, columnNames);
		JTable table = new JTable(model);
		
		JScrollPane scroll = new JScrollPane(table);
		
		
		center.add(scroll, BorderLayout.CENTER);
		
		JButton addLine = new JButton("Edit Budget Entry");

		addLine.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				Budget.this.editItems();
			}
		});
		
		//TODO create add category button
		/*
		JButton editLine = new JButton("Edit Entry");

		editLine.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Clicked editline button");
				
				//Ledger.this.EditItems();
			}
		});
		*/
		JPanel panel = new JPanel(new BorderLayout());
		
		panel.add(addLine, BorderLayout.WEST);
		//panel.add(editLine, BorderLayout.EAST);
		
		center.add(panel, BorderLayout.SOUTH);
		
		Blink.updateCenterPane(center);
	}


	/**
	 * Edit an already existing item.
	 */
	protected void editItems() {
		
		JPanel panel = new JPanel(new GridLayout(2,1));
		JTextField field = new JTextField("");
		JLabel label = new JLabel("Enter Row Number to edit");
		
		panel.add(label);
		panel.add(field);
		
		int selection = 1;
		
		int result = JOptionPane.showConfirmDialog(null, panel, "Enter Data", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if(result == JOptionPane.OK_OPTION){
			try{
				selection = Integer.parseInt(field.getText());
			}catch(NumberFormatException e){
				return;
			}
			int index = selection-1;
			if (index<budgetList.size()){
				//String[] tempArray = ledgerList[index].returnArray();
				addEditItems(budgetList.get(index).getBudgeted().toString(), index);
			}
		}
	}

	/**
	 * Pop up menu to get user input.
	 * @param tempArray
	 * @param index
	 */
	protected void addEditItems(String tempValue, int index) {
		JTextField field1 = new JTextField(tempValue);
		JPanel panel = new JPanel(new GridLayout(2,1));
		
		String labelsString = "Budgeted Amount";
		
		JLabel label = new JLabel(labelsString);
		panel.add(label);
		
		String[] categories = Data.getCategories();
		
		//TODO, change Row Selection to drowndown.
		JComboBox<String> dropdown = new JComboBox<String>(categories);
		
		panel.add(field1);
		
		int result = JOptionPane.showConfirmDialog(null, panel, "Enter Data", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if(result == JOptionPane.OK_OPTION){
			String resultsBudget = field1.getText();
			
			String sanitizedResults = sanitizedResults(resultsBudget);
			
			
			if(index<budgetList.size()){
				budgetList.get(index).setBudgeted(sanitizedResults);
			}
			
			
			updateCenterPane();
			updateRightPane();
			savefile();
		}
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

//TODO Update right panel
	/**
	 * Updates the right hand side panel.
	 */
	public void updateRightPane() {
		
		
		String line0 = " ";
		String line1 = "Total Acct Balances: $" + totalAcctBal();
		String line2 = " ";
		String line3 = "Total Expenses: $" + totalExp();
		String line4 = "Total Budgeted: $" + totalBudg();
		String line5 = " ";
		String line6 = " ";
		String line7 = "Available to Budget: $" + totalAvailable();
		String line8 = " ";
		
		
		String[] rightText = {line0, line1, line2, line3, line4, line5, line6, line7, line8};
		
		JPanel right = new JPanel();
		
		/*
		JButton editStart = new JButton("Edit Starting Balance");
		editStart.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Ledger.this.editStarting();
			}
		});
		right.add(editStart);
		*/
		
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		
		for(String text : rightText){
			JLabel label = new JLabel(text);
			right.add(label);
		}
		
		//TODO finalize Button
		/*
		JButton finalizeAcct = new JButton("Finalize Account");
		finalizeAcct.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Ledger.this.finalizeAcct();
			}
		});
		right.add(finalizeAcct);
		*/
		
		Blink.updateRightPane(right);
		
	}
	
	
	private String totalAvailable() {
		BigDecimal total = new BigDecimal("0");
		
		total = total.add(totalAcct);
		total = total.subtract(totalBudg);
		
		return total.toString();
	}

	private String totalBudg() {
		BigDecimal total = new BigDecimal("0");

		for(int i=0; i<budgetList.size(); i++){
			total = total.add(budgetList.get(i).getBudgetedDB());
		}
		
		totalBudg = total;
		
		return total.toString();
	}

	private String totalExp() {
		BigDecimal total = new BigDecimal("0");
		
		for(int i=0; i<budgetList.size(); i++){
			total = total.add(budgetList.get(i).getExpensed());
		}
		
		totalExp = total;
		
		return total.toString();
	}

	private String totalAcctBal() {
		BigDecimal total = new BigDecimal("0");
		
		for(int i=0; i<ledgerStore.size(); i++){
			total = total.add(ledgerStore.get(i).beginingBal);
		}
		
		totalAcct = total;
		
		return total.toString();
	}

	//TODO remake finalized.
	/**
	 * Finalizes account and sets up next month's account.
	 */
	protected void finalizeAcct() {
		int result = JOptionPane.showConfirmDialog(null, "Are you sure you are finished with this month?\nThis will move the current Bank Balance to next month's starting balance and move any pending transactions.");
		if(result == JOptionPane.OK_OPTION){
			
			finalized = true;
			savefile();
			
			String[] tempFileName = Data.nextFileName(fileName);
			Ledger tempLedger = new Ledger(tempFileName);
			//tempLedger.updateStarting(beginingBal.add(clearedItems()));
			
			for(int i=0; i<ledgerList.size(); i++){
				if(!ledgerList.get(i).cleared){
					tempLedger.ledgerList.add(ledgerList.get(i));
					tempLedger.savefile();
				}
			}
		}
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
