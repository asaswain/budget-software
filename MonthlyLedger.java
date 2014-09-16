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
	 * This method returns the number of Single Entries in all the days in this month
	 * 
	 * @return an integer of the number of single entries in this month
	 */
	public int getSingleEntryCount() {
		return datedEntries.size();
	}

	// add a single entry to the dated expense list
	public void addSingleEntry(SingleEntry newEntry) {
		// make sure entry date is valid before adding entry
		if (newEntry.getDate().getMonth() != month) {
			throw new IllegalArgumentException("Entry month does not match general ledger month.");
		}
		if (newEntry.getDate().getYear() != year) {
			throw new IllegalArgumentException("Entry year does not match general ledger year.");
		} 
		datedEntries.addEntry(newEntry);		
	}

	// delete a single entry from the dated expense list
	public void deleteSingleEntry(SingleEntry delEntry) {
		datedEntries.deleteEntry(delEntry);
	}

	// update a single entry on the dated expense list
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

	// get data for a single entry on the dated expense list
	public Entry getSingleEntry(JDateTime date, int loc)  {
		return datedEntries.getEntry(date, loc);
	}

	// return if this single entry is in the general ledger for this month
	public boolean isSingleEntryInMonth(SingleEntry testEntry) {
		return datedEntries.isEntryInTheList(testEntry);
	}

	// print all income/expenses for a given month
	public void printAllEntries() {
		System.out.println("Ledger for " + month + "-" + year +" (displaying " + datedEntries.size() + " entries)");
		datedEntries.printAllItems();
		System.out.println("");
		System.out.println("Budget for " + month + "-" + year);
		myBudget.printBudget();
		System.out.println("");
	};

	// add an account and associated amount to the list of accounts in this month's budget
	public void addBudgetAcct(Type newAccount, double newBudgetAmount){
		myBudget.addAccount(newAccount, newBudgetAmount);
	}

	// remove an account from the list of categories in the budget (only allowed if no entries in the month for this account)
	public void removeBudgetAcct(Type deleteAccount){
		myBudget.deleteAccount(deleteAccount);
	}

	// update the budgeted amount for a specific account
	public void updateBudgetAmount(Type updateAccount, double newAmount) {
		myBudget.updateBudgetAmount(updateAccount, newAmount);
	}

	// check if an account is in the budget for this month
	public boolean isAccountInBudget(Type inputAccount) {
		return myBudget.isAccountInList(inputAccount);
	}

	// get a list of singleEntries from all the days in the month
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

	//(make sure expense date is within the month)

	//public getMoneyLeftInCategory(category)
	//public getTotalBudget
	//public getTotalMoneyLeftInBudget
	//public getTotalIncome
	//public getTotalExpense
	//public getActualMoneySaved
	//public getProjectedSavings 
}
