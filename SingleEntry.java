package budget_program;

//import java.util.*;
import jodd.datetime.*;

/**
 * This entry object stores the data for a single expense or income, with the associated date and amount
 * @author Asa Swain
 */

public class SingleEntry extends Entry implements Comparable<SingleEntry>{
	// date of the income/expense
	private JDateTime date;
	// dollar amount of the income/expense
	private Double amount;

	/**
	 * blank constructor
	 */
	public SingleEntry(){
		date = new JDateTime(); // current date and time
		entryType = new Type();
		desc = "";
		amount = 0.00;
	}

	/**
	 * constructor using data for a single entry (with date as a JTimeDate object)
	 */
	public SingleEntry(JDateTime newDate, Type newType, String newDesc, double newAmount) {
		date = newDate;
		entryType = newType;
		desc = newDesc;
		amount = newAmount;
		// if expense type store amount as a negative number
		if (newType.getIsAnExpense() == true) {
			amount = amount * -1;
		}
	}

	/**
	 * constructor using data for a single entry (with date as separate integers)
	 */
	public SingleEntry(int newDay, int newMonth, int newYear, Type newType, String newDesc, double newAmount) {
		date = new JDateTime(newYear,newMonth,newDay);
		entryType = newType;
		desc = newDesc;
		amount = newAmount;
		// if expense type store amount as a negative number
		if (newType.getIsAnExpense() == true) {
			amount = amount * -1;
		}
	}

	/**
	 * get date for this entry
	 * @return date 
	 */
	public JDateTime getDate() {
		return date;
	}
	
	// Note: use overloading depending on what type of parameter is passed to set the date
	
	// set the date for this entry using a JDateTime object
	public void setDate(JDateTime newDate) {
		date = newDate;
	}
	// set the date for this entry using integers
	public void setDate(int newDay, int newMonth, int newYear) {
		date = new JDateTime(newYear,newMonth,newDay);
	}
	
	// get the amount of expense or income
	public double getAmount() {
		return amount;
	}
	
	// set the amount of expense or income
	public void setAmount(double newAmount) {
		amount = newAmount;
	}
	
	/** 
	 * Print the contents of a single entry
	 */
	public void printEntry() {
		System.out.println("Date: " + date.toString("MM/DD/YYYY") + " Type: " + entryType.getTypeName() + " Desc: " + desc + " Amount: " + amount);
	}
	
	// compare two single entries by checking their dates
	public int compareTo(SingleEntry other) {
	     return date.compareTo(other.date);
	}
}
