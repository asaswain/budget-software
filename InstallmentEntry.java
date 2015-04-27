package budget_program;

import java.math.BigDecimal;

import jodd.datetime.JDateTime;

/**
 * This entry object stores the data for an expense/income that repeats for a series of months
 * starting with the month of startDate and ending at the month of endDate
 * 
 * @author Asa Swain
 *
 */
public class InstallmentEntry extends MultipleEntry implements Comparable<InstallmentEntry>{
	// dollar amount of the income/expense
	private BigDecimal totalAmount;

	private final BigDecimal NEGATIVE = new BigDecimal("-1");

	// used to store type of MultipleEntry this is
	private static final String TYPE = new String("Installment");
	
	/**
	 * This is a blank constructor
	 */
	public InstallmentEntry(){
		startDate = new JDateTime(); // current date and time
		endDate = new JDateTime(); // current date and time
		entryAccount = new Account();
		desc = "";
		totalAmount = new BigDecimal("0");
		totalAmount = totalAmount.setScale(2, BigDecimal.ROUND_CEILING);
	}

	/**
	 * This is a constructor using data for an installment entry (with date as separate integers)
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
	public InstallmentEntry(int startDay, int startMonth, int startYear, int endDay, int endMonth, int endYear, Account newType, String newDesc, BigDecimal newAmount) {
		this.startDate = new JDateTime(startYear,startMonth,startDay);
		this.endDate = new JDateTime(endYear,endMonth,endDay);
		this.entryAccount = newType;
		this.desc = newDesc;
		this.totalAmount = newAmount;
		// if this is for an expense account, then store amount as a negative number
		if (entryAccount.getIsAnExpense() == true) {
			this.totalAmount = this.totalAmount.multiply(NEGATIVE);
		}
	}

	/**
	 * This is a constructor using data for a installment entry - with the date passed in a JDateTime object
	 * 
	 * @param startDate - a JDateTime object with the start date
	 * @param endDate - a JDateTime object with the end date
	 * @param newAccount - an account object with the account the entry should be applied to
	 * @param newDesc - the description of the entry
	 * @param newAmount - the amount of the entry for each month
	 */
	public InstallmentEntry(JDateTime startDate, JDateTime endDate, Account newAccount, String newDesc, BigDecimal newAmount) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.entryAccount = newAccount;
		this.desc = newDesc;
		this.totalAmount = newAmount;
		// if this is for an expense account, then store amount as a negative number
		if (entryAccount.getIsAnExpense() == true) {
			this.totalAmount = this.totalAmount.multiply(NEGATIVE);
		}
	}

	public BigDecimal getMonthlyAmount() {
		// return the amount for a single months which equals totalAmount / number of months
		int startMonth = startDate.getMonth();
		int startYear = startDate.getYear();
		int endMonth = endDate.getMonth();
		int endYear = endDate.getYear();
		final int monthsInAYear = 12;
		int numberOfMonths = (endMonth - startMonth) + ((endYear - startYear) * monthsInAYear);
		// I assume the end date isn't before the start date
		assert numberOfMonths >= 0;
		BigDecimal installmentAmount;
		if (numberOfMonths > 0 && totalAmount.compareTo(BigDecimal.ZERO) != 0) {
			installmentAmount = totalAmount.divide(new BigDecimal(numberOfMonths));
		} else {
			installmentAmount = new BigDecimal("0");
			installmentAmount = totalAmount.setScale(2, BigDecimal.ROUND_CEILING);
		}

		return installmentAmount;
	}
	public void setTotalAmount(BigDecimal newAmount) {
		totalAmount = newAmount;
	}

	public void printEntry() {
	}

	//	compare two repeating entries by alpha sorting their description text
	public int compareTo(InstallmentEntry other) {
		return desc.compareTo(other.desc);
	}

	public String getEntryType() {
		return TYPE;
	}

}