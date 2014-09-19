package budget_program;

import jodd.datetime.JDateTime;

/**
 * This entry object stores the data for an expense/income that repeats for a series of months
 * starting with the month of startDate and ending at the month of endDate
 * 
 * @author Asa Swain
 *
 * <afs> 07/20/2014 this class is unfinished and currently not used
 */
public class InstallmentEntry extends Entry implements Comparable<InstallmentEntry>{
    // start date of the income/expense - day of the month doesn't matter
	private JDateTime startDate;
    // end date of the income/expense - day of the month doesn't matter
	private JDateTime endDate;
    // dollar amount of the income/expense
	private Double totalAmount;

	/**
	 * This is a blank constructor
	 */
	public InstallmentEntry(){
		startDate = new JDateTime(); // current date and time
		endDate = new JDateTime(); // current date and time
		entryType = new Type();
		desc = "";
		totalAmount = 0.00;
	}

	public InstallmentEntry(int startDay, int startMonth, int startYear, int endDay, int endMonth, int endYear, Type newType, String newDesc, double newAmount) {
		startDate = new JDateTime(startYear,startMonth,startDay);
		endDate = new JDateTime(endYear,endMonth,endDay);
		entryType = newType;
		desc = newDesc;
		totalAmount = newAmount;
	}

	public JDateTime getStartDate() {
		return startDate;
	}

	//	use overloading depending on what type of parameter is passed to set the date
	public void setStartDate(JDateTime newDate) {
		startDate = newDate;
		if (startDate.isAfter(endDate)) {
			throw new IllegalArgumentException("Start date can't be after end date");
		}
	}
	public void setStartDate(int newDay, int newMonth, int newYear) {
		startDate = new JDateTime(newYear,newMonth,newDay);
		if (startDate.isAfter(endDate)) {
			throw new IllegalArgumentException("Start date can't be after end date");
		}
	}

	public JDateTime getEndDate() {
		return endDate;
	}
//	<afs> use overloading depending on what type of parameter is passed to set the date
	public void setEndDate(JDateTime newDate) {
		endDate = newDate;
		if (endDate.isBefore(startDate)) {
			throw new IllegalArgumentException("End date can't be before start date");
		}
	}
	public void setEndDate(int newDay, int newMonth, int newYear) {
		endDate = new JDateTime(newYear,newMonth,newDay);
		if (endDate.isBefore(startDate)) {
			throw new IllegalArgumentException("End date can't be before start date");
		}
	}

	public double getAmount() {
		// return the amount for a single months which equals totalAmount / number of months
		int startMonth = startDate.getMonth();
		int startYear = startDate.getYear();
		int endMonth = endDate.getMonth();
		int endYear = endDate.getMonth();
		final int monthsInAYear = 12;
		int numberOfMonths = (endMonth - startMonth) + ((endYear - startYear) * monthsInAYear);
		// I assume the end date isn't before the start date
		assert numberOfMonths >= 0;
		double installmentAmount;
		if (numberOfMonths > 0 && totalAmount != 0) {
			installmentAmount = totalAmount/numberOfMonths;
		} else {
			installmentAmount = 0;
		}

		return installmentAmount;
	}
	public void setAmount(double newAmount) {
		totalAmount = newAmount;
	}

	public void printEntry() {
	}

//	compare two repeating entries by alpha sorting their description text
	public int compareTo(InstallmentEntry other) {
		return desc.compareTo(other.desc);
	}
}