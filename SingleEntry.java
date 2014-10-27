package budget_program;

//import java.util.*;
import jodd.datetime.*;

/**
 * This class contains an entry object which stores the data for a single expense or income, 
 * with the associated date and amount.
 * 
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
		entryAccount = new Account();
		desc = "";
		amount = 0.00;
	}

	/**
	 * This is a constructor using data for a single entry  - with the date passed in a JDateTime object
	 * 
	 * @param newDate - a JDateTime object with the date of the entry
	 * @param newType - a Type object with the account the entry should be applied to
	 * @param newDesc - the description of the entry
	 * @param newAmount - the amount of the entry
	 */
	public SingleEntry(JDateTime newDate, Account newType, String newDesc, double newAmount) {
		date = newDate;
		entryAccount = newType;
		desc = newDesc;
		amount = newAmount;
		// if expense type store amount as a negative number
		if (newType.getIsAnExpense() == true) {
			amount = amount * -1;
		}
	}

	/**
	 * This is a constructor using data for a single entry (with date as separate integers)
	 *
	 * @param newDay - an integer with the day of the entry date
	 * @param newMonth - an integer with the month of the entry date
	 * @param newYear - an integer with the year of the entry date
	 * @param newType - a Type object with the account the entry should be applied to 
	 * @param newDesc - the description of the entry 
	 * @param newAmount - the amount of the entry
	 */
	public SingleEntry(int newDay, int newMonth, int newYear, Account newType, String newDesc, double newAmount) {
		date = new JDateTime(newYear,newMonth,newDay);
		entryAccount = newType;
		desc = newDesc;
		amount = newAmount;
		// if expense type store amount as a negative number
		if (newType.getIsAnExpense() == true) {
			amount = amount * -1;
		}
	}

	/**
	 * This gets the date for this entry
	 * 
	 * @return JDateTime object with the entry date 
	 */
	public JDateTime getDate() {
		return date;
	}

	// Note: use overloading depending on what type of parameter is passed to set the date

	/**
	 * This sets the date for this entry using a JDateTime object
	 * 
	 * @param newDate - the new date for this entry
	 */
	public void setDate(JDateTime newDate) {
		date = newDate;
	}

	/**
	 * This sets the date for this entry using integers
	 * 
	 * @param newDay - an integer with the day of the entry date
	 * @param newMonth - an integer with the month of the entry date
	 * @param newYear - an integer with the year of the entry date
	 */
	public void setDate(int newDay, int newMonth, int newYear) {
		date = new JDateTime(newYear,newMonth,newDay);
	}

	/**
	 * This gets the amount of the entry (either expense or income depending on the Account)
	 * 
	 * @return the amount of the entry as a double
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * This sets the amount of the entry (either expense or income depending on the Account)
	 * 
	 * @param the amount of the entry as a double
	 */
	public void setAmount(double newAmount) {
		amount = newAmount;
	}

	/**
	 * This compare two SingleEntry objects by comparing their dates
	 */
	public int compareTo(SingleEntry other) {
		return date.compareTo(other.date);
	}
}
