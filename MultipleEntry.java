package budget_program;

import jodd.datetime.JDateTime;

/**
 * This abstract class is the template for classes that store income and expenses 
 * in the general ledger across multiple dates 
 * 
 * @author Asa Swain
 */

public abstract class MultipleEntry extends Entry {
	// start date of the income/expense - day of the month doesn't matter
	protected JDateTime startDate;
	// end date of the income/expense - day of the month doesn't matter
	protected JDateTime endDate;

	/**
	 * This gets the start date for this entry
	 * 
	 * @return JDateTime object with the start date 
	 */
	public JDateTime getStartDate() {
		return startDate;
	}

	// use overloading depending on what type of parameter is passed to set the start date

	/**
	 * This sets the start date for this entry using a JDateTime object
	 * 
	 * @param newDate - the new start date for this entry
	 */
	public void setStartDate(JDateTime newDate) {
		startDate = newDate;
		if (startDate.isAfter(endDate)) {
			throw new IllegalArgumentException("Start date can't be after end date");
		}
	}

	/**
	 * This sets the start date for this entry using integers
	 * 
	 * @param newDay - an integer with the day of the start date
	 * @param newMonth - an integer with the month of the start date
	 * @param newYear - an integer with the year of the start date
	 */
	public void setStartDate(int newDay, int newMonth, int newYear) {
		startDate = new JDateTime(newYear,newMonth,newDay);
		if (startDate.isAfter(endDate)) {
			throw new IllegalArgumentException("Start date can't be after end date");
		}
	}

	/**
	 * This gets the end date for this entry
	 * 
	 * @return JDateTime object with the end date 
	 */
	public JDateTime getEndDate() {
		return endDate;
	}

	// use overloading depending on what type of parameter is passed to set the end date

	/**
	 * This sets the end date for this entry using a JDateTime object
	 * 
	 * @param newDate - the new end date for this entry
	 */
	public void setEndDate(JDateTime newDate) {
		endDate = newDate;
		if (endDate.isBefore(startDate)) {
			throw new IllegalArgumentException("End date can't be before start date");
		}
	}

	/**
	 * This sets the end date for this entry using integers
	 * 
	 * @param newDay - an integer with the day of the end date
	 * @param newMonth - an integer with the month of the end date
	 * @param newYear - an integer with the year of the end date
	 */
	public void setEndDate(int newDay, int newMonth, int newYear) {
		endDate = new JDateTime(newYear,newMonth,newDay);
		if (endDate.isBefore(startDate)) {
			throw new IllegalArgumentException("End date can't be before start date");
		}
	}
}


