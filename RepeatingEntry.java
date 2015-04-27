package budget_program;

import java.math.BigDecimal;
import jodd.datetime.JDateTime;

/**
 * This entry object stores the data for an expense/income that repeats for a series of months
 * starting with the month of startDate and ending at the month of endDate
 * 
 * @author Asa Swain
 */
public class RepeatingEntry extends MultipleEntry implements Comparable<RepeatingEntry>{
	// dollar amount of the income/expense
	private BigDecimal monthlyAmount;

	private final BigDecimal NEGATIVE = new BigDecimal("-1");
	
	// used to store type of MultipleEntry this is
	private static final String TYPE = new String("Repeating");
	
	/**
	 * This is a blank constructor
	 */
	public RepeatingEntry(){
		startDate = new JDateTime(); // current date and time
		endDate = new JDateTime(); // current date and time
		entryAccount = new Account();
		desc = "";
		monthlyAmount = new BigDecimal("0");
		monthlyAmount = monthlyAmount.setScale(2, BigDecimal.ROUND_CEILING);
	}

	/**
	 * This is a constructor using data for a repeating entry (with date as separate integers)
	 * 
	 * @param startDay - an integer with the day of the start date
	 * @param startMonth - an integer with the month of the start date
	 * @param startYear - an integer with the year of the start date
	 * @param endDay - an integer with the day of the end date
	 * @param endMonth - an integer with the month of the end date
	 * @param endYear - an integer with the year of the end date
	 * @param newAccount - an account object with the account the entry should be applied to
	 * @param newDesc - the description of the entry
	 * @param newAmount - the amount of the entry for each month
	 */
	public RepeatingEntry(int startDay, int startMonth, int startYear, int endDay, int endMonth, int endYear, Account newAccount, String newDesc, BigDecimal newAmount) {
		this.startDate = new JDateTime(startYear,startMonth,startDay);
		this.endDate = new JDateTime(endYear,endMonth,endDay);
		this.entryAccount = newAccount;
		this.desc = newDesc;
		this.monthlyAmount = newAmount;
		// if this is for an expense account, then store amount as a negative number
		if (entryAccount.getIsAnExpense() == true) { 			
			this.monthlyAmount = this.monthlyAmount.multiply(NEGATIVE);
		}		
	}
	
	/**
	 * This is a constructor using data for a repeating entry - with the date passed in a JDateTime object
	 * 
	 * @param startDate - a JDateTime object with the start date
	 * @param endDate - a JDateTime object with the end date
	 * @param newAccount - an account object with the account the entry should be applied to
	 * @param newDesc - the description of the entry
	 * @param newAmount - the amount of the entry for each month
	 */
	public RepeatingEntry(JDateTime startDate, JDateTime endDate, Account newAccount, String newDesc, BigDecimal newAmount) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.entryAccount = newAccount;
		this.desc = newDesc;
		this.monthlyAmount = newAmount;
		// if this is for an expense account, then store amount as a negative number
		if (entryAccount.getIsAnExpense() == true) {
			this.monthlyAmount = this.monthlyAmount.multiply(NEGATIVE);
		}
	}
	
	/**
	 * This gets the amount of the entry (either expense or income depending on the Account)
	 * 
	 * @return the amount of the entry as a BigDecimal
	 */
	public BigDecimal getMonthlyAmount() {
		return monthlyAmount;
	}
	
	/**
	 * This sets the amount of the entry (either expense or income depending on the Account)
	 * 
	 * @param the amount of the entry as a BigDecimal
	 */
	public void setTotalAmount(BigDecimal newAmount) {
		monthlyAmount = newAmount;
	}
	
	/**
	 * This compares two RepeatingEntry objects by comparing their description text
	 */
	public int compareTo(RepeatingEntry other) {
	     return desc.compareTo(other.desc);
	}
	
	public String getEntryType() {
		return TYPE;
	}
}
