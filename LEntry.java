import java.math.BigDecimal;

/**
 * Object to hold an entry.
 *
 */
public class LEntry {
	
	public String date;
	public String category;
	public String payee;
	public String memo;
	public BigDecimal amount;
	public boolean cleared;
	
	public LEntry(String[] lineArray){
		update(lineArray);
	}

	public void update(String[] lineArray) {
		date = lineArray[0];
		category = lineArray[1];
		payee = lineArray[2];
		memo = lineArray[3];
		amount = new BigDecimal(lineArray[4]).setScale(Data.numDecimals(), BigDecimal.ROUND_HALF_UP);
		
		if(lineArray[5].equals("n"))
			cleared = false;
		else
			cleared = true;
		
		System.out.println(toString());
	}
	
	public String[] returnArray(){
		String tempAmount = amount + "";
		String tempCleared;
		
		if(cleared)
			tempCleared = "y";
		else
			tempCleared = "n";
		
		String[] tempArray = {date,category,payee,memo,tempAmount,tempCleared};
		return tempArray;
	}

	public String toString(){
		String tempString = "";
		String[] tempArray = returnArray();
		for(int i=0; i<tempArray.length; i++)
			tempString = tempString + "" + tempArray[i] + ",";
		
		return tempString;
	}
}
