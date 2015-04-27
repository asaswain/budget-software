package budget_program;

//import java.util.*;
import java.math.BigDecimal;
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
	private BigDecimal amount;

	private final BigDecimal NEGATIVE = new BigDecimal("-1");
	
	/**
	 * This is a blank constructor
	 */
	public SingleEntry(){
		this.date = new JDateTime(); // current date and time
		this.entryAccount = new Account();
		this.desc = "";
		this.amount = new BigDecimal("0");
		this.amount = this.amount.setScale(2, BigDecimal.ROUND_CEILING);
	}

	/**
	 * This is a constructor using data for a single entry  - with the date passed in a JDateTime object
	 * 
	 * @param newDate - a JDateTime object with the date of the entry
	 * @param newAccount - an account object with the account the entry should be applied to
	 * @param newDesc - the description of the entry
	 * @param newAmount - the BigDecimal amount of the entry
	 */
	public SingleEntry(JDateTime newDate, Account newAccount, String newDesc, BigDecimal newAmount) {
		this.date = newDate;
		this.entryAccount = newAccount;
		this.desc = newDesc;
		this.amount = newAmount;
		// if this is for an expense account, then store amount as a negative number
		if (newAccount.getIsAnExpense() == true) {
			this.amount = this.amount.multiply(NEGATIVE);
		}
	}

	/**
	 * This is a constructor using data for a single entry (with date as separate integers)
	 *
	 * @param newDay - an integer with the day of the entry date
	 * @param newMonth - an integer with the month of the entry date
	 * @param newYear - an integer with the year of the entry date
	 * @param newAccount - an account object with the account the entry should be applied to 
	 * @param newDesc - the description of the entry 
	 * @param newAmount - the BigDecimal amount of the entry
	 */
	public SingleEntry(int newDay, int newMonth, int newYear, Account newAccount, String newDesc, BigDecimal newAmount) {
		this.date = new JDateTime(newYear,newMonth,newDay);
		this.entryAccount = newAccount;
		this.desc = newDesc;
		this.amount = newAmount;
		// if this is for an expense account, then store amount as a negative number
		if (newAccount.getIsAnExpense() == true) { 
			this.amount = this.amount.multiply(NEGATIVE);
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
	 * @return the amount of the entry as a BigDecimal
	 */
	public BigDecimal getMonthlyAmount() {
		return amount;
	}

	/**
	 * This sets the amount of the entry (either expense or income depending on the Account)
	 * 
	 * @param the amount of the entry as a BigDecimal
	 */
	public void setTotalAmount(BigDecimal newAmount) {
		amount = newAmount;
	}

	/**
	 * This compares two SingleEntry objects by comparing their dates
	 */
	public int compareTo(SingleEntry other) {
		return date.compareTo(other.date);
	}
}
