import java.awt.BorderLayout;

import javax.swing.*;

/**
 * This class will switch out the Ledger and Budget panels.
 *
 */
public class Blink {

	public static GUI gui;
	
	public vLedger a;
	public vBudget b;
	
	public Blink(){
		
	}
	
	public Blink(GUI gui) {
		this.gui = gui;
	}
	
	/**
	 * Function to update the accounts on the drop down menu.
	 */
	@SuppressWarnings("unchecked")
	public void updateAccts(){
		gui.acctList.removeAllItems();

		gui.acctList.addItem("Other Accts");
		
		
	}

	/**
	 * Decide which view to load.
	 * @param fileName	Filename to load.
	 */
	public void viewLedger(String[] fileName) {
		
		
		if(fileName[2].equals("Budget")){
			b = new vBudget(fileName, this);
			
			b.updateRightPane();
			b.updateCenterPane();
		}
		else{
			a = new vLedger(fileName, this);
			
			a.updateRightPane();
			a.updateCenterPane();
		}
		
	}
	
	/**
	 * Update right panel
	 * @param right		Panel to load.
	 */
	public static void updateRightPane(JPanel right){
		gui.right.removeAll();
		
		
		gui.right.add(right, BorderLayout.EAST);
		gui.setVisible(true);
	}
	
	/**
	 * Update Center Panel
	 * @param center	Panel to load.
	 */
	public static void updateCenterPane(JPanel center){
		gui.center.removeAll();
		
		gui.center.add(center, BorderLayout.CENTER);
		gui.setVisible(true);
	}

}
