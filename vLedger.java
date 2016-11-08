import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


public class vLedger {
	

	Blink blink;
	String[] fileName;
	Ledger l;

	public vLedger(String[] fileName, Blink blink) {
		this.fileName = fileName;
		this.blink = blink;
		l = new Ledger(fileName);
	}
	

	/**
	 * Updates Center Panel
	 */
	public void updateCenterPane() {

		JPanel center = new JPanel(new BorderLayout());
		LinkedList<LEntry> ledgerList = l.getLedgerList();
		
		
		
		String[] columnNames = {"#", "Date", "Category", "Payee", "Memo", "Amount", "C"};
		
		Object[][] data = new Object[ledgerList.size()][7];
		

		for(int i=0; i<ledgerList.size(); i++){
			data[i][0] = (i+1) + "";
			String[] tempArray = ledgerList.get(i).returnArray();
			for (int j=0; j<tempArray.length; j++){
				data[i][(j+1)] = tempArray[j];
			}
		}
		
		DefaultTableModel model = new DefaultTableModel(data, columnNames);
		final JTable table = new JTable(model);
		


		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

		table.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);
		table.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		
		table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
		


		table.setEnabled(false);
		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.getColumnModel().getColumn(0).setPreferredWidth(35);
		table.getColumnModel().getColumn(1).setPreferredWidth(60);
		table.getColumnModel().getColumn(2).setPreferredWidth(195);
		table.getColumnModel().getColumn(3).setPreferredWidth(195);
		table.getColumnModel().getColumn(4).setPreferredWidth(70);
		table.getColumnModel().getColumn(5).setPreferredWidth(80);
		table.getColumnModel().getColumn(6).setPreferredWidth(20);
		
		JScrollPane scroll = new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(800,550));
		
		table.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent me) {
		        JTable table =(JTable) me.getSource();
		        Point p = me.getPoint();
		        int row = table.rowAtPoint(p);
		        if (me.getClickCount() == 2) {

		        	addEditItems(l.getLedgerList().get(row).returnArray(), row);
		        }
		    }
		});
		
		
		center.add(scroll, BorderLayout.CENTER);
		
		JButton addLine = new JButton("Add Entry");

		addLine.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				String[] tempArray = {"","","","","","y",};
				vLedger.this.addItems(tempArray);
			}
		});
		
		JButton editLine = new JButton("Edit Entry");

		editLine.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				vLedger.this.EditItems();
			}
		});
		
		JPanel panel = new JPanel(new BorderLayout());
		
		panel.add(addLine, BorderLayout.WEST);
		panel.add(editLine, BorderLayout.EAST);
		
		center.add(panel, BorderLayout.SOUTH);
		
		Blink.updateCenterPane(center);
	}
	

	/**
	 * Updates the right hand side panel.
	 */
	public void updateRightPane() {
		
		
		String line0 = " ";
		String line1 = "Starting Balance: $" + l.getBeginingBal();
		String line2 = " ";
		String line3 = "Cleared Expenses: $" + l.clearedItems();
		String line4 = "Bank Balance: $" + (l.getBankBal());
		String line5 = " ";
		String line6 = "Pending Transactions: $" + l.pendingItems();
		String line7 = "Available Balance: $" + (l.getAvailableBal());
		String line8 = " ";
		
		
		String[] rightText = {line0, line1, line2, line3, line4, line5, line6, line7, line8};
		
		JPanel right = new JPanel();
		
		JButton editStart = new JButton("Edit Starting Balance");
		editStart.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				vLedger.this.editStarting();
			}
		});
		right.add(editStart);
		
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		
		for(String text : rightText){
			JLabel label = new JLabel(text);
			right.add(label);
		}
		
		JButton finalizeAcct = new JButton("Finalize Account");
		finalizeAcct.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				vLedger.this.finalizeAcct();
			}
		});
		right.add(finalizeAcct);
		

		right.setPreferredSize(new Dimension(250,550));
		
		Blink.updateRightPane(right);
		
	}
	

	/**
	 * Edit starting balances.
	 */
	protected void editStarting() {
		String newStarting = JOptionPane.showInputDialog(l.getBeginingBal() + "\nPlease enter a new starting balance for the month");
		if(newStarting != null){
			try{
				newStarting = newStarting.replace(",", "");
				double tempDouble = Double.parseDouble(newStarting);
				
				BigDecimal tempBig = new BigDecimal(newStarting);
				l.updateStarting(tempBig);
	
				updateRightPane();
			}catch(NumberFormatException e){
				JOptionPane.showMessageDialog(null, "Error, not a number");
			}
		}
		
		updateRightPane();
	}
	

	/**
	 * Finalizes account and sets up next month's account.
	 */
	protected void finalizeAcct() {
		int result = JOptionPane.showConfirmDialog(null, "Are you sure you are finished with this month?\nThis will move the current Bank Balance to next month's starting balance and move any pending transactions.");
		if(result == JOptionPane.OK_OPTION){
			
			l.setFinalized(true);
			l.savefile();
			
			l.finalizeAcct();
		}
	}



	protected void addItems(String[] tempArray) {
		LinkedList<LEntry> ledgerList = l.getLedgerList();
		addEditItems(tempArray, ledgerList.size());
	}

	/**
	 * Edit an already existing item.
	 */
	protected void EditItems() {
		
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
				JOptionPane.showMessageDialog(null, "Error, not a valid number", " ", JOptionPane.WARNING_MESSAGE);
				EditItems();
				return;
			}
			int index = selection-1;
			if (index<l.getLedgerList().size() & index>-1){
				//String[] tempArray = ledgerList[index].returnArray();
				addEditItems(l.getLedgerList().get(index).returnArray(), index);
			}
			else{
				JOptionPane.showMessageDialog(null, "Error, not a valid number", " ", JOptionPane.WARNING_MESSAGE);
				EditItems();
			}
		}
	}
	
	


	/**
	 * Pop up menu to get user input.
	 * @param tempArray
	 * @param index
	 */
	protected void addEditItems(String[] tempArray, int index) {
		JTextField field1 = new JTextField(tempArray[0]);
		JTextField field2 = new JTextField(tempArray[2]);
		JTextField field3 = new JTextField(tempArray[3]);
		JTextField field4 = new JTextField(tempArray[4]);
		JTextField field5 = new JTextField(tempArray[5]);
		JPanel panel = new JPanel(new GridLayout(2,6));
		JLabel[] jlabels = new JLabel[6];
		String[] labels = {"Date","Category","Payee","Memo","Amount","Cleared"};
		
		for(int i=0; i<6; i++){
			jlabels[i] = new JLabel(labels[i]);
			panel.add(jlabels[i]);
		}
		
		String[] categories = Data.getCategories();
		
		JComboBox<String> dropdown = new JComboBox<String>(categories);
		
		panel.add(field1);
		panel.add(dropdown);
		panel.add(field2);
		panel.add(field3);
		panel.add(field4);
		panel.add(field5);
		
		int result = JOptionPane.showConfirmDialog(null, panel, "Enter Data", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if(result == JOptionPane.OK_OPTION){
			String[] resultsArray = {field1.getText(),dropdown.getSelectedItem()+ "", field2.getText(), field3.getText(), field4.getText(), field5.getText()};
			
			l.addEditItem(resultsArray, index);
		}
		
		updateCenterPane();
		updateRightPane();
	}
	
	

}
