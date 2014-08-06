package budget_program;

import jodd.datetime.JDateTime;

/**
 * This entry object stores the data for an expense/income that repeats for a series of months
 * starting with the month of startDate and ending at the month of endDate
 * 
 * <afs> 07/20/2014 this class is unfinished and currently not used
 */
public class RepeatingEntry extends Entry implements Comparable<RepeatingEntry>{
	// start date of the income/expense - day of the month doesn't matter
	private JDateTime startDate;
	// end date of the income/expense - day of the month doesn't matter
	private JDateTime endDate;
	// dollar amount of the income/expense
	private Double monthlyAmount;

	/**
	 * This is a blank constructor
	 */
	public RepeatingEntry(){
		startDate = new JDateTime(); // current date and time
		endDate = new JDateTime(); // current date and time
		entryType = new Type();
		desc = "";
		monthlyAmount = 0.00;
	}

	// basic constructor
	public RepeatingEntry(int startDay, int startMonth, int startYear, int endDay, int endMonth, int endYear, Type newType, String newDesc, double newAmount) {
		startDate = new JDateTime(startYear,startMonth,startDay);
		endDate = new JDateTime(endYear,endMonth,endDay);
		entryType = newType;
		desc = newDesc;
		monthlyAmount = newAmount;
	}

	// get start date of repeating expense/income
	public JDateTime getStartDate() {
		return startDate;
	}
	// use overloading depending on what type of parameter is passed to set the date
	public void setStartDate(JDateTime newDate) {
		startDate = newDate;
	}
	public void setStartDate(int newDay, int newMonth, int newYear) {
		startDate = new JDateTime(newYear,newMonth,newDay);
	}
	
	public JDateTime getEndDate() {
		return endDate;
	}
	// use overloading depending on what type of parameter is passed to set the date
	public void setEndDate(JDateTime newDate) {
		endDate = newDate;
	}
	public void setEndDate(int newDay, int newMonth, int newYear) {
		endDate = new JDateTime(newYear,newMonth,newDay);
	}
	
	public double getAmount() {
		return monthlyAmount;
	}
	public void setAmount(double newAmount) {
		monthlyAmount = newAmount;
	}
	
	public void printEntry() {
	}
	
	// compare two repeating entries by alpha sorting their description text
	public int compareTo(RepeatingEntry other) {
	     return desc.compareTo(other.desc);
	}
}
