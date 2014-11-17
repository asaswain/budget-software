package budget_program;

import java.util.ArrayList;
import jodd.datetime.JDateTime;

/**
 * This class prints lists of entries and budgets for the generalLedger object to the screen
 * 
 * @author Asa Swain
 */

public class PrintLedger {

	/**
	 * This prints all the single entries on a specific date
	 * 
	 * @param ledgerData - the GeneralLedger object to print entries from
	 * @param printDate - the date to print all the entries for
	 */
	public static void printSingleEntriesForDate(GeneralLedger ledgerData, JDateTime printDate) {		
		ArrayList<SingleEntry> entryList = ledgerData.getSingleEntryListForDate(printDate);
		if (entryList != null) {
			for (SingleEntry printEntry : entryList){
				System.out.println("Date: " + printEntry.getDate().toString("MM/DD/YYYY") + " Desc: " + printEntry.getDesc() + " Type: " + printEntry.getAccount().getAccountName() + " Amount: " + printEntry.getAmount());
			}
		}
	}

	/**
	 * This prints all the single entries in a given date range
	 *
	 * @param ledgerData - the GeneralLedger object to print entries from
	 * @param startDate - start date to print entries for
	 * @param endDate - last date to print entries for
	 */
	public static void printSingleEntries(GeneralLedger ledgerData, JDateTime startDate, JDateTime endDate) {
		JDateTime tmp = startDate;
		JDateTime cutoffDate = endDate;
		cutoffDate.add(0,0,1);
		do {
			printSingleEntriesForDate(ledgerData, tmp);
			tmp.add(0, 0, 1);
		} while (tmp.isBefore(cutoffDate) == true);
	}

	/**
	 * This prints all the single entries for all dates in the ledger
	 * 
	 * @param ledgerData - the GeneralLedger object to print entries from
	 */
	public static void printSingleEntries(GeneralLedger ledgerData) {
		ArrayList<JDateTime> dateList = ledgerData.getSingleEntryDates();
		if (dateList != null) {
			for (JDateTime tmpDate : dateList){
				printSingleEntriesForDate(ledgerData, tmpDate);
			}
		}
	}
	
	/**
	 * This prints all the repeating entries on a specific month
	 * 
	 * @param ledgerData - the GeneralLedger object to print entries from
	 * @param printMonth - the month to print all the repeating entries for, if null then print all repeating entries
	 */
	public static void printRepeatingEntries(GeneralLedger ledgerData, JDateTime printMonth) {		
		ArrayList<RepeatingEntry> entryList = ledgerData.getRepeatingEntryListForMonth(printMonth);
		if (entryList != null) {
			for (RepeatingEntry printEntry : entryList){
				System.out.println("Desc: " + printEntry.getDesc() + " Type: " + printEntry.getAccount().getAccountName() + " Amount: " + printEntry.getAmount());
			}
		}
	}
	
	/**
	 * This prints all the repeating entries across all months
	 * 
	 * @param ledgerData - the GeneralLedger object to print entries from
	 */
	public static void printRepeatingEntries(GeneralLedger ledgerData) {		
		ArrayList<RepeatingEntry> entryList = ledgerData.getRepeatingEntryListForMonth(null);
		if (entryList != null) {
			for (RepeatingEntry printEntry : entryList){
				System.out.println("Desc: " + printEntry.getDesc() + " Type: " + printEntry.getAccount().getAccountName() + " Amount: " + printEntry.getAmount());
			}
		}
	}

	/**
	 * This prints all the single entries and the amount budgeted for each account for a given month
	 * 
	 * @param ledgerData - the GeneralLedger object to print entries from
	 * @param printMonth - month to print entries for
	 * @param printYear - year to print entries for
	 * 
	 * @exception if we encountered an error trying to print the budget for this month and year
	 */
	public static void printMonth(GeneralLedger ledgerData, int printMonth, int printYear) {
		// print entries for this month
		System.out.println("Entries for " + printMonth + "/" + printYear);
		JDateTime startDate = new JDateTime(printYear,printMonth,1);
		int lastDayInMonth = startDate.getMonthLength();
		JDateTime endDate = new JDateTime(printYear,printMonth,lastDayInMonth);
		printSingleEntries(ledgerData, startDate, endDate);

		// print repeating entries for this month
		System.out.println("");
		System.out.println("Repeating Entries for " + printMonth + "/" + printYear);
		printRepeatingEntries(ledgerData, startDate);
		
		// print budget for this month
		try {
			System.out.println("");
			printMonthlyBudget(ledgerData, printMonth, printYear);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * This prints the amount budgeted for each account for a given month
	 * 
	 * @param ledgerData - the GeneralLedger object to print budget from
	 * @param printMonth - month to print entries for
	 * @param printYear - year to print entries for
	 * 
	 * @exception if we encountered an error trying to print the budget for this month and year
	 */
	public static void printMonthlyBudget(GeneralLedger ledgerData, int printMonth, int printYear) {
		ArrayList<Account> accountList = ledgerData.getMonthlyBudgetAccounts(printMonth, printYear);
		if (accountList != null) {
			try {
				System.out.println("Monthly Budget Info for: " + printMonth + "/" + printYear);
				// print account name and budgeted amount for each account in the monthly budget
				for(Account tmpAccount : accountList) {
					System.out.println("Account: " + tmpAccount.getAccountName() + " Budgeted Amount: " + ledgerData.getMonthlyBudgetAmount(printMonth, printYear, tmpAccount));
				}
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(e);
			}
		} else {
			System.out.println("No budget found for: " + printMonth + "/" + printYear);
		}
	}

	/**
	 * This prints the amounts budgeted for each account in the default budget
	 * 
	 * @param ledgerData - the GeneralLedger object to print budget from
	 * 
	 * @exception if we encountered an error trying to print the default budget
	 */
	public static void printDefaultBudget(GeneralLedger ledgerData) {
		ArrayList<Account> accountList = ledgerData.getDefaultBudgetAccounts();
		if (accountList != null) {
			try {
				System.out.println("Default Monthly Budget Info:");
				// print account name and budgeted amount for each account in the default budget		
				for(Account tmpAccount : accountList) {
					System.out.println("Account: " + tmpAccount.getAccountName() + " Budgeted Amount: " + ledgerData.getDefaultBudgetAmount(tmpAccount));
				}	
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(e);
			} 
		} else {
			System.out.println("No default budget found.");
		}
	}
}
