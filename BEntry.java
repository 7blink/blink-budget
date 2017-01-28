import java.math.BigDecimal;




public class BEntry {
	public String category;
	public BigDecimal budgeted= new BigDecimal("0");
	public BigDecimal rollover= new BigDecimal("0");
	public BigDecimal expensed= new BigDecimal("0");
	
	public BEntry(String category){
		this.category = category;
	}
	
	public String[] returnArray(){
		String[] tempArray = {category, budgeted.toString(), rollover.toString(), totalBudget().toString(), expensed.toString(), available().toString()};
		
		return tempArray;
	}
	
	public String[] returnSmallArray(){
		String[] tempArray = {category, budgeted.toString(),  expensed.toString(), available().toString()};
		
		return tempArray;
	}
		
	public BigDecimal totalBudget(){
			BigDecimal total = new BigDecimal("0");
			BigDecimal totalPlusBudgeted = total.add(budgeted);
			BigDecimal totalPlusRoll = totalPlusBudgeted.add(rollover);
			
			
			return totalPlusRoll;
	}
	
	public void addExpense(BigDecimal amount){
		expensed = expensed.add(amount);
	}
	
	public BigDecimal available(){
		BigDecimal available = new BigDecimal(totalBudget().toString());
		BigDecimal subExpense = available.add(expensed);
		
		return subExpense;
	}
	
	public String getAvailable(){
		return available().toString();
	}
	
	public String toString(){
		return category + "," + budgeted.toString() + "," + rollover.toString() + ",";
	}
	
	
	
	public String getCategory(){
		return category;
	}
	
	public String getBudgeted(){
		return budgeted.toString();
	}
	
	public void setBudgeted(String amount){
		this.budgeted = new BigDecimal(amount);
	}
	
	public BigDecimal getExpensed(){
		return expensed;
	}
	
	public String getRollover(){
		return rollover.toString();
	}
	
	public void setRollover(String amount){
		rollover = new BigDecimal(amount);
	}

	public BigDecimal getBudgetedDB() {
		return budgeted;
	}
	
}
