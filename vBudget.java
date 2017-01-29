import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;


public class vBudget {
	
	Blink blink;
	String[] fileName;
	Budget b;

	public vBudget(String[] fileName, Blink blink) {

		this.fileName = fileName;
		this.blink = blink;
		b = new Budget(fileName);
	}


	/**
	 * Updates Center Panel
	 */
	public void updateCenterPane() {

		JPanel center = new JPanel(new BorderLayout());
		
		String[] columnNames = {" ", "Category", "Budgeted", "Rollover", "Total", "Spent", "Available"};
		//String[] columnNames = {"#", "Category", "Budgeted", "Spent", "Available"};
		
		Object[][] data = new Object[b.budgetList.size() + Data.getMainCategories().length][8];
		//Object[][] data = new Object[b.budgetList.size()][5];
		
		int rowCount = 0;
		
		for(int k=0; k<Data.getMainCategories().length; k++){
			data[rowCount][0] = Data.getMainCategories()[k];
			rowCount++;
			
			
			for(int i=0; i<b.budgetList.size(); i++){
				if(Data.getMainCategories()[k].equals(b.budgetList.get(i).lookupMainCategory())){
					data[rowCount][0] =  "";
					String[] tempArray = b.budgetList.get(i).returnArray();
					//String[] tempArray = b.budgetList.get(i).returnSmallArray();
					for (int j=0; j<tempArray.length; j++){
						data[rowCount][(j+1)] = tempArray[j];
					}
					rowCount++;
				}
			}
		}
		
		DefaultTableModel model = new DefaultTableModel(data, columnNames);
		JTable table = new JTable(model);
		
		table.setEnabled(false);
		
		
		table.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent me) {
		        JTable table =(JTable) me.getSource();
		        Point p = me.getPoint();
		        int row = table.rowAtPoint(p);
		        if (me.getClickCount() == 2) {


			        String category = table.getModel().getValueAt(row, 1).toString();
			        for(int i=0; i<b.getBudgetList().size(); i++){
			        	if(b.getBudgetList().get(i).getCategory().equals(category))
			        		addEditItems(b.getBudgetList().get(i).getBudgeted().toString(), i);
			        }

		        }
		    }
		});
		
		
		
		JScrollPane scroll = new JScrollPane(table);
		
		
		center.add(scroll, BorderLayout.CENTER);
		
		JButton addLine = new JButton("Edit Budget Entry");

		addLine.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				vBudget.this.editItems();
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
			if (index<b.budgetList.size()){
				//String[] tempArray = ledgerList[index].returnArray();
				addEditItems(b.budgetList.get(index).getBudgeted().toString(), index);
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
			b.addEditItems(resultsBudget, index);
			

			updateCenterPane();
			updateRightPane();
		}
	}
	

	/**
	 * Updates the right hand side panel.
	 */
	public void updateRightPane() {
		
		
		String line0 = " ";
		String line1 = "Total Acct Balances: $" + b.totalAcctBal();
		String line2 = " ";
		String line3 = "Total Expenses: $" + b.totalExp();
		String line4 = "Total Budgeted: $" + b.totalBudg();
		String line5 = " ";
		String line6 = " ";
		String line7 = "Available to Budget: $" + b.totalAvailable();
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
		
		JButton finalizeAcct = new JButton("Finalize Account");
		finalizeAcct.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				vBudget.this.finalizeAcct();
			}
		});
		right.add(finalizeAcct);
		
		
		Blink.updateRightPane(right);
		
	}

	//TODO remake finalized.
	/**
	 * Finalizes account and sets up next month's account.
	 */
	protected void finalizeAcct() {
		int result = JOptionPane.showConfirmDialog(null, "Are you sure you are finished with this month?\nThis will move the current Bank Balance to next month's starting balance and move any pending transactions.");
		if(result == JOptionPane.OK_OPTION){
			
			b.finalizeAcct();
			
		}
	}


}
