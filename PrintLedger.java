package budget_program;

import java.util.ArrayList;
import jodd.datetime.JDateTime;

//TODO: finish printMonthlyBudget and printDefaultBudget methods

public class PrintLedger {

	/**
	 * This prints all the single entries on a specific date
	 * 
	 * @param ledgerData - the ledgerData to print entries from
	 * @param searchDate - the date to print all the entries for
	 */
	public static void printEntriesForDate(GeneralLedger ledgerData, JDateTime searchDate) {		
		ArrayList<SingleEntry> entryList = ledgerData.getDailyLedgerSingleEntryList(searchDate);
		if (entryList != null) {
			for (SingleEntry printEntry : entryList){
				System.out.println("Date: " + printEntry.getDate().toString("MM/DD/YYYY") + " Type: " + printEntry.getAccount().getAccountName() + " Desc: " + printEntry.getDesc() + " Amount: " + printEntry.getAmount());
			}
		}
	}
	
	/**
	 * This prints all single entries in a given date range
	 *
	 * @param ledgerData - the ledgerData to print entries from
	 * @param startDate - start date to print entries for
	 * @param endDate - last date to print entries for
	 */
	public static void printDateRangeEntries(GeneralLedger ledgerData, JDateTime startDate, JDateTime endDate) {
		JDateTime tmp = startDate;
		JDateTime cutoffDate = endDate;
		cutoffDate.add(0,0,1);
		do {
			try {
				printEntriesForDate(ledgerData, tmp);
				tmp.add(0, 0, 1);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(e);
			}
		} while (tmp.isBefore(cutoffDate) == true);
	}

	/**
	 * This prints all entries and the budget for a given month
	 * 
	 * @param ledgerData - the ledgerData to print entries from
	 * @param month - month to print entries for
	 * @param year - year to print entries for
	 */
	public static void printMonth(GeneralLedger ledgerData, int month, int year) {
		// print entries for this month
		System.out.println("Entries for " + month + "-" + year);
		JDateTime startDate = new JDateTime(year,month,1);
		int lastDayInMonth = startDate.getMonthLength();
		JDateTime endDate = new JDateTime(year,month,lastDayInMonth);
		try {
			printDateRangeEntries(ledgerData, startDate, endDate);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		} 
		// print budget for this month
		System.out.println("");
		printMonthlyBudget(ledgerData, month, year);
	}

	/**
	 * Print all entries for all months in the ledger
	 * 
	 * @param ledgerData - the ledgerData to print entries from
	 */
	public static void printAllEntries(GeneralLedger ledgerData) {
		ArrayList<JDateTime> dateList = ledgerData.getSingleEntryDates();
		if (dateList != null) {
			try {
				for (JDateTime tmpDate : dateList){
					printEntriesForDate(ledgerData, tmpDate);
				}
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	/**
	 * Print monthly budget info
	 * 
	 * @param ledgerData - the ledgerData to print entries from
	 * @param month - month to print entries for
	 * @param year - year to print entries for
	 */
	public static void printMonthlyBudget(GeneralLedger ledgerData, int month, int year) {
		ArrayList<Account> accountList = ledgerData.getMonthlyBudgetAccounts(month, year);
		if (accountList != null) {
			try {
				System.out.println("Monthly Budget Info for: " + month + "/" + year);
				// print account name and budgeted amount for each account in the monthly budget
				for(Account tmpAccount : accountList) {
					System.out.println("Account: " + tmpAccount.getAccountName() + " Budgeted Amount: " + ledgerData.getMonthlyBudgetAmount(month, year, tmpAccount));
				}
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(e);
			}
		} else {
			System.out.println("No budget found for: " + month + "/" + year);
		}
	}
	
	/**
	 * Print default budget info
	 * 
	 * @param ledgerData - the ledgerData to print entries from
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
