import java.awt.*;
import java.awt.Desktop.Action;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;


public class GUI extends JFrame implements ActionListener {

	public JPanel top, center, right;
	public JMenuBar menubar;
	public JMenuItem about;
	public JComboBox yrList, monList, acctList; 
	public Blink blink;
	public JTextArea text;
	public JScrollPane scroll;
	
	public JButton load, add;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public GUI(String title){
		super(title);
		Container window = getContentPane();
		window.setLayout(new BoxLayout(window, BoxLayout.Y_AXIS));
		
		blink = new Blink(this);
		
		//Create the menu bar
		menubar = new JMenuBar();
		about = new JMenuItem("About");
		about.getAccessibleContext().setAccessibleDescription("The only menu");
		menubar.add(about);
		about.addActionListener(this);
		this.setJMenuBar(menubar);
		
		
		//Create top JPanel
		JPanel top = new JPanel();
		top.setMaximumSize(new Dimension(1800, 100));
		
		top.setLayout(new FlowLayout());
		
		String[] years = Data.getYears();
		yrList = new JComboBox(years);
		yrList.setSelectedIndex(0);
		

		String[] months = Data.getMonths();
		monList = new JComboBox(months);
		monList.setSelectedIndex(0);
		
		String[] accts = Data.getAccts();
		acctList = new JComboBox(accts);
		acctList.setSelectedItem(0);
		acctList.setEditable(true);
		
		
		top.add(yrList);
		top.add(monList);
		top.add(acctList);
		
		load = new JButton("View");
		load.addActionListener(this);
		top.add(load);
		
		//TODO add/remove accounts
		/*
		add = new JButton("add/remove accounts");
		add.addActionListener(this);
		top.add(add);
		*/
		
		
		window.add(top);
		
		center = new JPanel();
		
		right = new JPanel();
		
		JPanel lower = new JPanel();
		lower.setLayout(new BoxLayout(lower, BoxLayout.X_AXIS));
		
		lower.add(center, BorderLayout.CENTER);
		lower.add(right, BorderLayout.EAST);
		
		window.add(lower);
		
		
		
	}
	
	/**
	 * Launches the GUI object to start program.
	 * Gui should be an object to allow easy access in other objects.
	 */
	public static void main(String[] args) {
		GUI gui = new GUI("Budget");
		
		
		//Set size and location
		Toolkit tk = Toolkit.getDefaultToolkit();
		int x = (int) tk.getScreenSize().getWidth();
		int y = (int) tk.getScreenSize().getHeight();
		
		gui.setSize(1100, 700);
		gui.setLocationRelativeTo(null);
		gui.setVisible(true);
		
		gui.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		

	}

	/**
	 * ActionEvents
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == about){
			about();
		}
		else if(e.getSource() == load){
			load();
		}
		else if(e.getSource() == add){
			addAcct();
		}
	}

	/**
	 * Add Button Onclick.
	 */
	private void addAcct() {
		System.out.println("Add Acct Clicked.");
		JOptionPane.showMessageDialog(null, "Feature Not Available yet.");
	}

	/**
	 * Load Button onclick.
	 */
	private void load() {
		
		String[] fileName = {(String) yrList.getSelectedItem(), (String) monList.getSelectedItem(), (String) acctList.getSelectedItem()};
		
		Data.lastViewed(fileName);
		
		blink.viewLedger(fileName);
	}

	/*
	 * About Window pop-up.
	 */
	private void about() {
		JOptionPane.showMessageDialog(null, "Created by: 7 Blink");
		//blink.updateAccts();
	}

}
