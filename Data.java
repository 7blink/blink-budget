
public class Data {
	
	public static String[] months = {"Jan", "Feb", "Mar", "April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};

	public Data() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns an array of categories.
	 * @return
	 */
	public static String[] getCategories(){
		//TODO read from file to get categories
		String[] tempCategories = {"Income Current", "Income Next", "Groceries", "Rent", "Phone", "Car Insurance", "Health Insurance", "Spending Money", "Gas", "Savings"};
		
		return tempCategories;
	}

	/**
	 * Creates the next FileName for a ledger or budget.
	 * @param fileName
	 * @return
	 */
	public static String[] nextFileName(String[] fileName) {
		Integer year = Integer.parseInt(fileName[0]);
		Integer monthNum = 0;
		for(int i=0; i<months.length; i++){
			if(fileName[1].equals(months[i])){
				if(i<11)
					monthNum = i+1;
				else{
					monthNum = 0;
					year = year + 1;
				}
			}
		}
		String [] results = {(year + ""), months[monthNum], fileName[2] };
		
		return results;
	}
	

	/**
	 * Gets the previous month's filename.
	 * @param fileName
	 * @return
	 */
	public static String[] prevFileName(String[] fileName) {
		Integer year = Integer.parseInt(fileName[0]);
		Integer monthNum = 0;
		for(int i=0; i<months.length; i++){
			if(fileName[1].equals(months[i])){
				if(i>0)
					monthNum = i-1;
				else{
					monthNum = 11;
					year = year - 1;
				}
			}
		}
		String [] results = {(year + ""), months[monthNum], fileName[2] };
		
		return results;
	}

	public static void lastViewed(String[] fileName) {
		//TODO Create function to store last viewed acct.
	}

	public static String[] getYears() {
		//TODO create years function
		String[] years = {"2016", "2017"};
		return years;
	}

	public static String[] getAccts() {
		String[] accts = {"Checking", "Savings", "Budget"};
		return accts;
	}
	
	public static int numDecimals() {
		return 2;
	}

	public static String[] getMonths() {
		return months;
	}
}
