package budget_program;

import java.util.*;

import jodd.datetime.JDateTime;

/**
 * This is a list of monthly ledger objects and an index to that list 
 * The index consists of JDateTime objects to make it easier to sort the months
 * @author Asa Swain
 */

public class MonthlyLedgerList {

	// hashmap of data for each month indexed by date
	private HashMap<JDateTime, MonthlyLedger> monthlyData;

	/**
	 * This is a blank constructor
	 */
	public MonthlyLedgerList() {
		monthlyData = new HashMap<JDateTime, MonthlyLedger>();
	}

	/**
	 * This returns the number of months in the general ledger
	 * 
	 * @return integer count of number of months in the general ledger
	 */
	public int monthCount() {
		return monthlyData.size();
	}

	/**
	 * The returns the number of dated entries in a specific month
	 * 
	 * @param month  the month to count entries in
	 * @param year  the year of the month to count entries in
	 * @return the number of dated entries in this month/year
	 */
	public int monthlyDatedEntryCount(int month, int year) {
		if (isMonthInLedger(month, year) == true) {
			JDateTime monthYearId = new JDateTime(year, month, 1);
			MonthlyLedger tempData = monthlyData.get(monthYearId);
			return tempData.getDatedEntryCount();
		} else {
			return 0;
		}
	}

	/** 
	 * This checks if we have created a ledger for a given month and year
	 *
	 * @param month  the month to check
	 * @param year  the year of the month to check
	 * @return true if a ledger or budget has been created for this month/year, else false
	 */
	public boolean isMonthInLedger(int month, int year) {
		JDateTime monthYearId = new JDateTime(year, month, 1);
		if (monthlyData.get(monthYearId) == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * This adds a ledger for a given month and year to the list and set the default budget amount for each account
	 *
	 * @param month  the month to add a ledger for
	 * @param year  the year of the month to add a ledger for
	 * @param defaultBudget  the default budget to add for this new ledger item
	 */
	public void addMonthToLedger(int month, int year, BudgetAmtList defaultBudget) {
		if (isMonthInLedger(month, year) == true) {
			throw new IllegalArgumentException(month + "-" + year + "already exists in the budget ledger.");
		} else {
			JDateTime monthYearId = new JDateTime(year, month, 1);
			MonthlyLedger newLedger = new MonthlyLedger(month, year, defaultBudget);
			monthlyData.put(monthYearId, newLedger);	
		}
	}

	/**
	 * This adds/updates a single entry to the ledger for a given month and year
	 * 
	 * @param month  the month of the ledger to add the entry to
	 * @param year  the year of the month of the ledger to add the entry to
	 * @param inputEntry  the new entry to add to the ledger
	 */
	public void addSingleEntryToLedger(int month, int year, SingleEntry inputEntry) {
		// if we don't have a ledger for this month, create one on the fly with a blank Budget
		if (isMonthInLedger(month, year) == false) {
			BudgetAmtList emptyBudget = new BudgetAmtList();
			addMonthToLedger(month, year, emptyBudget);
		} 
		JDateTime monthYearId = new JDateTime(year, month, 1);
		MonthlyLedger tempData = monthlyData.get(monthYearId);
		tempData.addSingleEntry(inputEntry);
		monthlyData.put(monthYearId, tempData);	
	}

	/**
	 * This prints out all the entries for all months in the list
	 * 
	 * @param month  month to print entries for
	 * @param year  year to print entries for
	 */
	public void printMonthlyLedger(int month, int year) {
		if (isMonthInLedger(month, year) == false) {
			throw new IllegalArgumentException(month + "-" + year + "doesn't exist in the budget ledger.");
		} else {
			try {
				JDateTime monthYearId = new JDateTime(year, month, 1);
				MonthlyLedger singleMonth = monthlyData.get(monthYearId);
				singleMonth.printAllEntries();
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	/**
	 * This prints out all the entries for all months in the list
	 */
	public void printEntireLedger() {
		try {
			//keySet returns a set of all the keys in the HashMap
			for(JDateTime tmpDate : monthlyData.keySet()){
				MonthlyLedger singleMonth = monthlyData.get(tmpDate);
				singleMonth.printAllEntries();
			}
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * This adds an account and associated amount to the list of accounts for a single month's budget
	 * 
	 * @param month  month of budget to update
	 * @param year  year of budget to update
	 * @param newAccount  new account number to add to this month's budget
	 * @param newBudgetAmount  new amount to add to this month's budget
	 */
	public void addMonthlyBudgetAcct(int month, int year, Type newAccount, double newBudgetAmount) {
		if (isMonthInLedger(month, year) == false) {
			throw new IllegalArgumentException(month + "-" + year + "doesn't exist in the budget ledger.");
		} else {
			try {
				JDateTime monthYearId = new JDateTime(year, month, 1);
				MonthlyLedger singleMonth = monthlyData.get(monthYearId);
				singleMonth.addBudgetAcct(newAccount, newBudgetAmount);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	/**
	 * This removes an account from the list of accounts (and associated amounts) for a single month's budget
	 *
	 * @param month  month of budget to update
	 * @param year  year of budget to update
	 * @param deleteAccount  account to delete from this month's budget
	 */
	public void removeMonthlyBudgetAcct(int month, int year, Type deleteAccount) {
		if (isMonthInLedger(month, year) == false) {
			throw new IllegalArgumentException(month + "-" + year + "doesn't exist in the budget ledger.");
		} else {
			try {
				JDateTime monthYearId = new JDateTime(year, month, 1);
				MonthlyLedger singleMonth = monthlyData.get(monthYearId);
				singleMonth.removeBudgetAcct(deleteAccount);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	/**
	 * This updates an amount for an account in a single month's budget
	 *
	 * @param month  month of budget to update
	 * @param year  year of budget to update
	 * @param updateAccount  account to update amount for
	 * @param newAmount  new amount to write to this month's budget
	 */
	public void updateMonthlyBudgetAmount(int month, int year, Type updateAccount, double newAmount) {
		if (isMonthInLedger(month, year) == false) {
			throw new IllegalArgumentException(month + "-" + year + "doesn't exist in the budget ledger.");
		} else {
			try {
				JDateTime monthYearId = new JDateTime(year, month, 1);
				MonthlyLedger singleMonth = monthlyData.get(monthYearId);
				singleMonth.updateBudgetAmount(updateAccount, newAmount);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	/**
	 * This returns a list of SingleEntries for every month in monthlyData object
	 * 
	 * @return an ArrayList of SingleEntries for every month in monthlyData object
	 */
	public ArrayList<SingleEntry> getRawSingleEntryList() {
		ArrayList<SingleEntry> masterList = new ArrayList<SingleEntry>();

		//keySet returns a set of all the keys in the HashMap
		for(JDateTime tmpDate : monthlyData.keySet()){
			ArrayList<SingleEntry> tmpList = monthlyData.get(tmpDate).getRawSingleEntryList();
			masterList.addAll(tmpList);
		}

		/*		(old code)
  		Set<JDateTime> listMonths = monthlyData.keySet();
		Iterator<JDateTime> iterator = listMonths.iterator();
		while(iterator.hasNext()) {
			JDateTime setDate = iterator.next();
			ArrayList<SingleEntry> tmpList = monthlyData.get(setDate).getRawSingleEntryList();
			masterList.addAll(tmpList);
		}*/

		return masterList;
	};

	/**
	 * This returns a list of dates for all the months in the MonthlyLedgerList object
	 * 
	 * @return a set of JDateTime dates for all months in the MonthlyLedgerList
	 */
	public Set<JDateTime> getMonthlyLedgerListDateList() {
		return monthlyData.keySet();
	}

	/**
	 * This returns the monthly budget (budgeted amount for each account) for a specific month and year
	 * 
	 * @param inputDate  The month and year to return the budget info for
	 * @return the BudgetAmtList object for a specific month and year
	 */
	public BudgetAmtList getBudgetInfo(JDateTime inputDate) {
		return monthlyData.get(inputDate).getBudgetInfo();
	}

	/* I commented out this method because we shouldn't let other classes see the MonthlyLedger object implementation
	 *
	// get the MonthlyLedger object for a given month and year from the list
	public MonthlyLedger getMonthlyLedger(int month, int year) {
		if (isMonthInLedger(month, year) == false) {
			throw new IllegalArgumentException(month + "-" + year + "doesn't exist in the budget ledger.");
		} else {
			JDateTime monthYearId = new JDateTime(year, month, 1);
			return monthlyData.get(monthYearId);
		}
	}
	 *
	 */

	/*
	 * // eventually I'll need to insert this into a list instead of just adding to the end
	public void addEntry(Entry newEntry) {
		entryList.add(newEntry);
	}

	public void deleteEntry(Entry delEntry) {
		entryList.remove(delEntry);
	}

	public void updateEntry(Entry oldEntry, Entry newEntry) {
		int loc = entryList.indexOf(oldEntry);
		entryList.set(loc, newEntry);
	}

	public Entry getEntry(int loc) {
		return entryList.get(loc);
	}

	public int size() {
		return entryList.size();
	}

	 */
}
