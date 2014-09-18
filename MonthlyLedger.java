package budget_program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import jodd.datetime.JDateTime;
import budget_program.SingleEntry;

// 09/16/2014 MASTER TO DO LIST: 
//
//   finish writing Javadoc for methods
//   implement installment and repeating entries methods

/**
 * This is a ledger with all the financial information for a single month, including:
 *
 * - a list of dated income and expense entries
 * - a list of repeating and installment entries
 * - a budget of amounts for each type of expense
 * - a month and year for this ledger
 * 
 * @author Asa Swain
 */

public class MonthlyLedger {
	// one-time expenses
	private DatedList datedEntries;
	// expenses that occur each month
	//private EntryList repeatingEntries; 
	// A list of budgeted amounts for each category
	private BudgetAmtList myBudget;
	// current month of ledger
	private int month;
	// current year of ledger
	private int year;

	/**
	 * This builds a ledger for a specific month and year
	 * 
	 * @param newMonth - the month to build the ledger for
	 * @param newYear - the year to build the ledger for
	 */
	public MonthlyLedger(int newMonth, int newYear) {
		datedEntries = new DatedList();
		//repeatingEntries = new EntryList();
		myBudget = new BudgetAmtList();
		month = newMonth;
		year = newYear;
	}

	/**
	 * This builds a ledger for a specific month and year with a default budget
	 * 
	 * @param newMonth - the month to build the ledger for
	 * @param newYear - the year to build the ledger for
	 * @param defaultBudget - tje default budget to use in this Monthly Ledger
	 */
	public MonthlyLedger(int newMonth, int newYear, BudgetAmtList defaultBudget) {
		datedEntries = new DatedList();
		//repeatingEntries = new EntryList();
		myBudget = new BudgetAmtList(defaultBudget);
		month = newMonth;
		year = newYear;
	}

	/**
	 * This method returns the number of single entries in all the days in this month
	 * 
	 * @return an integer of the number of single entries in this month
	 */
	public int getMonthSingleEntryCount() {
		return datedEntries.size();
	}
	
	/**
	 * This method returns the number of single entries in a date
	 *
	 * @param searchDate - the day we are searching for single entries in
	 * @return an integer of the number of single entries in this day
	 */
	public int getDaySingleEntryCount(JDateTime searchDate) {
		return datedEntries.size();
	}
	
	/**
	 * This method adds a single entry to the single entry list for this month
	 * 
	 * @param newEntry - the new entry to add
	 * @exception - entry month does not match general ledger month
	 * @exception - entry year does not match general ledger year
	 */
	public void addSingleEntry(SingleEntry newEntry) {
		// make sure entry date is valid before adding entry
		if (newEntry.getDate().getMonth() != month) {
			throw new IllegalArgumentException("Entry month does not match general ledger month.");
		}
		if (newEntry.getDate().getYear() != year) {
			throw new IllegalArgumentException("Entry year does not match general ledger year.");
		} 
		try {
			datedEntries.addEntry(newEntry);		
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * This deletes a single entry from the dated expense list
	 * 
	 * @param targetDate - the date of the entry to delete
	 * @param targetIndex - the index of the entry to delete on that date
	 * @exception - if there is an error from the deleteEntry method
	 */
	public void deleteSingleEntry(JDateTime targetDate, int targetIndex) {
		try {
			datedEntries.deleteEntry(targetDate, targetIndex);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * This updates a single entry on the dated expense list
	 * 
	 * @param newEntry - the new entry to add
	 * @param oldEntry - the old entry to remove
	 * @exception - old entry month does not match general ledger month
	 * @exception - old entry year does not match general ledger year
	 * @exception - new entry month does not match general ledger month
	 * @exception - new entry year does not match general ledger year
	 * 
	 */
	public void updateSingleEntry(SingleEntry newEntry, SingleEntry oldEntry)  {
		// make sure old entry date is valid before updating entry
		if (oldEntry.getDate().getMonth() != month) {
			throw new IllegalArgumentException("Old entry month does not match general ledger month");
		}
		if (oldEntry.getDate().getYear() != year) {
			throw new IllegalArgumentException("Old entry year does not match general ledger year.");
		}	
		// make sure new entry date is valid before updating entry
		if (newEntry.getDate().getMonth() != month) {
			throw new IllegalArgumentException("New entry month does not match general ledger month");
		}
		if (newEntry.getDate().getYear() != year) {
			throw new IllegalArgumentException("New entry year does not match general ledger year.");
		}
		datedEntries.updateEntry(oldEntry, newEntry);
	}

	/**
	 * 	This gets a single entry object from the dated expense list
	 * 
	 * @param entryDate - the date of the entry I am looking for
	 * @param index - the index in that date of the entry I am looking for
	 * @return a SingleEntry object
	 */
	public SingleEntry getSingleEntry(JDateTime entryDate, int index)  {
		return datedEntries.getEntry(entryDate, index);
	}

	/**
	 * This returns true if this single entry is in the general ledger for this month else returns false
	 * 
	 * @param testEntry - the single entry I am looking for
	 * @return a true/false value for if the single entry is in the general ledger for this month
	 */
	public boolean isSingleEntryInMonth(SingleEntry testEntry) {
		return datedEntries.isEntryInTheList(testEntry);
	}

	/**
	 * This prints a list of all income/expenses for a given month to the screen (and may print the budget for the month)
	 * 
	 * @param printBudget - if true then also print budget info for this month
	 */
	public void printAllEntries(boolean printBudget) {
		System.out.println("Ledger for " + month + "-" + year +" (displaying " + datedEntries.size() + " entries)");
		datedEntries.printAllEntries();
		System.out.println("");
		if (printBudget) {
			System.out.println("Budget for " + month + "-" + year);
			myBudget.printBudget();
			System.out.println("");
		}
	};
	
	/**
	 * This prints a list of all single entries for a given date to the screen
	 * 
	 * @param searchDate - the date to print single entries for
	 */
	public void printDaysEntries(JDateTime searchDate) {
		System.out.println("Ledger for " + searchDate + " (displaying " + datedEntries.getNumberOfEntriesInDate(searchDate) + " entries)");
		datedEntries.printDaysEntries(searchDate);
		System.out.println("");
	};

	/**
	 * This adds an account and associated amount to the list of accounts in this month's budget
	 * @param newAccount - account to add to budget
	 * @param newBudgetAmount - amount to budget for that account
	 */
	public void addBudgetAcct(Type newAccount, double newBudgetAmount){
		myBudget.addAccount(newAccount, newBudgetAmount);
	}

	/**
	 * This removes an account from the list of categories in the budget 
	 * (TODO: this should only be allowed if no entries in the month for this account)
	 * @param deleteAccount - account to delete from budget
	 */
	public void removeBudgetAcct(Type deleteAccount){
		myBudget.deleteAccount(deleteAccount);
	}

	/**
	 * This updates the budgeted amount for a specific account
	 * 
	 * @param updateAccount - account to update budgeted amount for
	 * @param newAmount - new budgeted amount
	 */
	public void updateBudgetAmount(Type updateAccount, double newAmount) {
		myBudget.updateBudgetAmount(updateAccount, newAmount);
	}

	/**
	 * This checks if an account is in the budget for this month
	 * 
	 * @param inputAccount - the account we are looking for
	 * @return true if account is in the budget, else false
	 */
	public boolean isAccountInBudget(Type inputAccount) {
		return myBudget.isAccountInList(inputAccount);
	}

	/**
	 * This returns a raw ArrayList of single entry objects for all the entries in this month
	 * 
	 * @return an ArrayList of single entry objects
	 */
	public ArrayList<SingleEntry> getRawSingleEntryList() {
		return datedEntries.getRawSingleEntryList();
	};

	/**
	 * This gets the budget accounts and amounts for this month
	 * 
	 * @return the budget info for this month
	 */
	public BudgetAmtList getBudgetInfo() {
		return myBudget;
	};


	//public getMoneyLeftInCategory(category)
	//public getTotalBudget
	//public getTotalMoneyLeftInBudget
	//public getTotalIncome
	//public getTotalExpense
	//public getActualMoneySaved
	//public getProjectedSavings 
}
