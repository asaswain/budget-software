package budget_program;

import java.util.*;

import jodd.datetime.JDateTime;

/**
 * This class provides a text-based user-interface to let the user create and maintain a monthly ledger 
 * and a budget using the GeneralLedger class
 * 
 * @author Asa Swain
 */

// TODO: See GeneralLedger to do
//       add new move entries command which calls updateEntry with a new index for the order of the entry in the date

public class TestBudget {

	static final boolean prohibitBlankInput = false;
	static final boolean allowBlankInput = true;

	static GeneralLedger myGeneralLedger = new GeneralLedger();

	static Scanner myInputScanner = new Scanner(System.in);		

	/**
	 * This method controls the main UI of the budget_program class
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// load SQL database data into Java data structures
		myGeneralLedger.loadSQLData();

		boolean quit = false;

		do {
			System.out.println("");
			System.out.println("General Ledger Main Menu");
			System.out.println("");
			System.out.println("Add Data:");
			System.out.println("AM - add new monthly budget to the database");
			System.out.println("AC - add an account to the database");
			System.out.println("AE - add an entry to a specific month");
			System.out.println("DE - delete an entry from a specific month"); 
			System.out.println("UE - update an entry from a specific month"); 
			System.out.println("");
			System.out.println("Configure Budget:");
			System.out.println("CD - configure default budget account/amount data");
			System.out.println("CB - configure budget account/amount data for a specific month");
			System.out.println("");
			System.out.println("Printing:");
			System.out.println("PM - print out ledger and budget for a month");
			System.out.println("PA - print out the contents of the entire general ledger and budget info");
			System.out.println("PD - print out the default budget account/amount data");
			System.out.println("");
			System.out.println("Q - quit");
			System.out.println("");

			String menuChoice = myInputScanner.nextLine();

			// add a new month to the database
			if (menuChoice.toUpperCase().equals("AM")) {
				addMonthlyBudget();
			}

			// add a new account to the database
			if (menuChoice.toUpperCase().equals("AC")) {
				addAccount();
			}

			// add an entry to a month
			if (menuChoice.toUpperCase().equals("AE") ) {
				System.out.println("Enter type of entry 'S'ingle, 'R'epeating or 'I'nstallment:");
				String entryType = myInputScanner.nextLine();

				// single entries
				if (entryType.toUpperCase().equals("S")) {
					addSingleEntry();
				}

				//repeating entries
				if (entryType.toUpperCase().equals("R")) {
					addRepeatingEntry();
				}

				//installment entries
				if (entryType.toUpperCase().equals("I")) {
					System.out.println("Installment entries aren't functional yet");
				}
			}

			// delete an entry from a specific month
			if (menuChoice.toUpperCase().equals("DE")) {
				System.out.println("Enter type of entry 'S'ingle, 'R'epeating or 'I'nstallment:");
				String entryType = myInputScanner.nextLine();

				// single entries
				if (entryType.toUpperCase().equals("S")) {
					deleteSingleEntry();
				} 

				//repeating entries
				if (entryType.toUpperCase().equals("R")) {
					deleteRepeatingEntry();
				}

				//installment entries
				if (entryType.toUpperCase().equals("I")) {
					System.out.println("Installment entries aren't functional yet");
				}
			}


			// update an entry
			if (menuChoice.toUpperCase().equals("UE")) {

				System.out.println("Enter type of entry 'S'ingle, 'R'epeating or 'I'nstallment:");
				String entryType = myInputScanner.nextLine();

				// single entries
				if (entryType.toUpperCase().equals("S")) {
					updateSingleEntry();
				}

				//repeating entries
				if (entryType.toUpperCase().equals("R")) {
					updateRepeatingEntry();
				}

				//installment entries
				if (entryType.toUpperCase().equals("I")) {
					System.out.println("Installment entries aren't functional yet");
				}
			} 

			// configure default budget account/amount data
			if (menuChoice.toUpperCase().equals("CD")) {
				configureBudget("Default");
			}

			// configure a monthly budget account/amount data
			if (menuChoice.toUpperCase().equals("CB")) {
				configureBudget("Monthly");
			}

			// print the contents of a month
			if (menuChoice.toUpperCase().equals("PM")) {
				printMonthLedger();
			}

			// print the contents of the entire general ledger
			if (menuChoice.toUpperCase().equals("PA")) {
				printEntireLedger();
			}

			// print default budget
			if (menuChoice.toUpperCase().equals("PD")) {
				try {
					PrintLedger.printDefaultBudget(myGeneralLedger);
				} catch (IllegalArgumentException e) {
					System.out.println(e);	
				} 
			}

			// quit program
			if (menuChoice.toUpperCase().equals("Q")) {
				quit = true;
				myGeneralLedger.saveSQLData();
				System.out.println("Ending Program");
			}

			if (quit == false) {
				System.out.println("<hit return to continue>");	
				String junk = myInputScanner.nextLine();
			}

		} while (quit == false);
	}



	/**
	 * This allows the user to enter a new account to the ledger
	 */
	private static void addAccount() {
		System.out.println("Enter the account name or enter blank to abort:");	
		String inputName = myInputScanner.nextLine();
		if (inputName.equals("")) return;
		System.out.println("Enter the account description or enter blank to abort:");	
		String inputDesc = myInputScanner.nextLine();
		if (inputDesc.equals("")) return;

		char inputChoice;

		System.out.println("Enter 'Y' if the account is an expense or 'N' if the account is an income:");	
		boolean inputIsAnExpense;
		inputChoice = myInputScanner.nextLine().charAt(0);
		if (inputChoice == 'Y' | inputChoice == 'y') {
			inputIsAnExpense = true;
		} else {
			inputIsAnExpense = false;
		}

		System.out.println("Enter 'Y' if the account is displayed in the default monthly account listing");	
		boolean inputInDefaultAccountList;
		inputChoice = myInputScanner.nextLine().charAt(0);
		if (inputChoice == 'Y' | inputChoice == 'y') {
			inputInDefaultAccountList = true;
		} else {
			inputInDefaultAccountList = false;
		}

		System.out.println("Enter 'Y' if the account is included in the budget or 'N' if the account is not included in the budget:");	
		boolean inputIsInBudget;
		inputChoice = myInputScanner.nextLine().charAt(0);
		Double inputDefaultBudgetAmt = 0.0;
		if (inputChoice == 'Y' | inputChoice == 'y') {
			inputIsInBudget = true;
			if (inputInDefaultAccountList == true) {
				System.out.println("Enter default amount to budget for this account or enter blank to abort:");	
				inputDefaultBudgetAmt = inputDouble(allowBlankInput);
				if (inputDefaultBudgetAmt == null) return;
			}
		} else {
			inputIsInBudget = false;
		}

		try {
			myGeneralLedger.addAccount(inputName, inputDesc, inputIsAnExpense, inputIsInBudget, inputInDefaultAccountList, inputDefaultBudgetAmt);
		} catch (IllegalArgumentException e) {
			System.out.println(e);	
		}
	}

	/**
	 * This allows the user to add a new monthly budget to the database
	 */

	private static void addMonthlyBudget() {
		System.out.println("Enter a date for which month to create a new budget for:");
		String inputDate = myInputScanner.nextLine();
		if (inputDate.equals("")) return;

		JDateTime tempDate = convertStringToDate(inputDate);

		int month = tempDate.getMonth();
		int year = tempDate.getYear();

		try {
			myGeneralLedger.addBudget(month, year);
		} catch (IllegalArgumentException e) {
			System.out.println(e);	
		} 
	}

	/**
	 * This allows the user to add a repeating entry to the ledger
	 */
	private static void addRepeatingEntry() {
		// enter start date for entry
		String checkForEntries = "";
		JDateTime startDate = chooseDate("Enter Start Date of Repeating Entries (or leave blank for all months):", allowBlankInput, checkForEntries);

		// enter end date for entry
		JDateTime endDate = chooseDate("Enter End Date of Repeating Entries (or leave blank for all months):", allowBlankInput, checkForEntries);

		// enter description
		System.out.println("Enter Entry Desc:");
		String inputDesc = myInputScanner.nextLine();

		// select account
		Account inputAcct = null;
		try {
			inputAcct = chooseAccount("", allowBlankInput);
			if (inputAcct == null) return;
		} catch (IllegalArgumentException e) {
			System.out.println("Error: Database has no accounts to choose from. Aborting input.");
			return;
		}
		// enter amount
		System.out.println("Enter Amount For Each Month:");
		Double inputAmt = inputDouble(allowBlankInput);
		if (inputAmt == null) return;

		myGeneralLedger.addRepeatingEntry(startDate, endDate, inputDesc, inputAcct, inputAmt);
	}

	/** 
	 * This allows the user to enter a single entry to the ledger
	 */
	private static void addSingleEntry() {
		// enter date for entry
		String checkForEntries = "";
		JDateTime entryDate = chooseDate("Enter Entry Date:", allowBlankInput, checkForEntries);
		if (entryDate == null) return;

		// enter description
		System.out.println("Enter Entry Desc:");
		String inputDesc = myInputScanner.nextLine();

		// select account
		Account inputAcct = null;
		try {
			inputAcct = chooseAccount("", allowBlankInput);
			if (inputAcct == null) return;
		} catch (IllegalArgumentException e) {
			System.out.println("Error: Database has no accounts to choose from. Aborting input.");
			return;
		}

		// enter amount
		System.out.println("Enter Entry Amount:");
		Double inputAmt = inputDouble(allowBlankInput);
		if (inputAmt == null) return;

		try {
			myGeneralLedger.addSingleEntry(entryDate, inputDesc, inputAcct, inputAmt);
		} catch (IllegalArgumentException e) {
			System.out.println("Error: " + e);
		}
	}

	/**
	 * This prompts the user to choose an account number from the list of all accounts in the general ledger
	 * 
	 * @param prompt - message display before user selects account
	 * @param allowBlankInput - boolean that controls if we allow blank input
	 * @exception - if myGeneralLedger has no account to choose from
	 * @return - Account object chosen by user
	 */
	private static Account chooseAccount(String prompt, boolean allowBlankInput) {
		if (myGeneralLedger.getAccountList().size() == 0) {
			throw new IllegalArgumentException("The general ledger has no accounts to select from.");
		} 
		if (prompt != "") {
			System.out.println(prompt);
		}

		int badInput;
		Integer inputNum = 0;

		do {
			badInput = 0;

			String categoryMsg = "Select account ";
			for (int i = 0; i < myGeneralLedger.getAccountList().size(); i++) {
				categoryMsg = categoryMsg + i + "-" + myGeneralLedger.getAccountList().get(i).getAccountName() + " ";
			}
			System.out.println(categoryMsg);

			inputNum = inputNumericSelection(allowBlankInput);

			if (inputNum != null) {			
				if (inputNum > myGeneralLedger.getAccountList().size()) {
					System.out.println("Invalid account number.");	
					badInput = 1;
				}  
			}
		} while (badInput == 1);

		if (inputNum != null) {
			return myGeneralLedger.getAccountList().get(inputNum);
		} else {
			return null;
		}
	}

	/**
	 * This prompts the user to enter a date
	 * 
	 * @param prompt - message to display before user enters a date
	 * @param allowBlankInput - boolean that controls if we allow blank input
	 * @param checkForEntries - String that controls if we make sure general ledger has 'S'ingle, 'R'epeating or 'I'nstallment entries for this date (or blank to skip validation)
	 * @return JDateTime object for date entered by user
	 */
	private static JDateTime chooseDate(String prompt, boolean allowBlankInput, String checkForEntries) {
		JDateTime newDate = null;

		int badDate;
		do {
			badDate = 0;

			System.out.println(prompt);
			String stringInput = myInputScanner.nextLine();
			if (!stringInput.equals("")) {
				try {
					newDate = convertStringToDate(stringInput);
				} catch (IllegalArgumentException e) {
					System.out.println("Invalid date.");	
					badDate = 1;
				} catch (ArrayIndexOutOfBoundsException e){
					System.out.println("Invalid date.");	
					badDate = 1;
				}
			} else {
				if (allowBlankInput == true) {
					return null;
				} else {
					System.out.println("Can't enter a blank date.");	
					badDate = 1;
				}
			}

			if (badDate == 0) {
				if (checkForEntries.toUpperCase() == "S") {
					// check to make sure there are single entries on this date
					ArrayList<SingleEntry> tmpList = myGeneralLedger.getSingleEntryListForDate(newDate);
					if (tmpList != null) { 
						if (tmpList.size() == 0) {
							System.out.println("No entries found on this date. Enter a different date.");	
							badDate = 1;
						} 
					} else {
						System.out.println("No entries found on this date. Enter a different date.");
						badDate = 1;
					}
				}

				if (checkForEntries.toUpperCase() == "R") {
					// check to make sure there are repeating entries on this date
					ArrayList<RepeatingEntry> tmpList = myGeneralLedger.getRepeatingEntryList(newDate);
					if (tmpList != null) { 
						if (tmpList.size() == 0) {
							System.out.println("No repeating entries found on this month. Enter a different month.");	
							badDate = 1;
						} 
					} else {
						System.out.println("No repeating entries found on this month. Enter a different month");
						badDate = 1;
					}
				}
			}
		} while (badDate == 1);

		return newDate;
	}

	/**
	 * This is used to configure the default budget or the budget for a specific month
	 * @param budgetType - either "Default" for the default budget or "Monthly" for a monthly budget
	 */
	private static void configureBudget(String budgetType) {
		Integer month = null;
		Integer year = null;

		if (budgetType.equals("Monthly")) {
			// enter date for entry
			System.out.println("Enter a date for which monthly budget to update:");
			String inputDate = myInputScanner.nextLine();
			if (inputDate.equals("")) return;
			JDateTime tempDate = convertStringToDate(inputDate);

			month = tempDate.getMonth();
			year = tempDate.getYear();
		}

		if (budgetType.equals("Monthly")) {
			System.out.println(month + "-" + year + " monthly budget account/amount option:");
		} else {
			System.out.println("Default budget account/amount options:");
		}

		System.out.println("A - Add new account and amount to " + budgetType + " budget");
		System.out.println("R - Remove account and amount from " + budgetType + " budget");
		System.out.println("U - Update amount for an account on the " + budgetType + " budget"); 

		char menuChoice = myInputScanner.nextLine().charAt(0);

		if (menuChoice == 'A' || menuChoice == 'a') {
			// enter account
			Account inputType = chooseAccount("Select account to add to " + budgetType + " budget ",allowBlankInput);
			if (inputType == null) return;

			// enter amount
			System.out.println("Enter " + budgetType + " budget amount:");
			Double inputAmt = inputDouble(allowBlankInput);
			if (inputAmt == null) return;

			if (budgetType.equals("Monthly")) {
				myGeneralLedger.addMonthlyBudgetAccount(month,year,inputType,inputAmt);
			} else {
				myGeneralLedger.addDefaultBudgetAccount(inputType,inputAmt);
			}
		}

		if (menuChoice == 'R' || menuChoice == 'r') {
			// enter account
			Account inputType = chooseAccount("Select account to remove from " + budgetType + " budget ",allowBlankInput);
			if (inputType == null) return;

			if (budgetType.equals("Monthly")) {
				myGeneralLedger.removeMonthlyBudgetAccount(month,year,inputType);
			} else {
				myGeneralLedger.removeDefaultBudgetAccount(inputType);
			}
		}

		if (menuChoice == 'U' || menuChoice == 'u') {
			// enter account
			Account inputType = chooseAccount("Select account to update in " + budgetType + " budget ",allowBlankInput);
			if (inputType == null) return;

			// enter amount
			System.out.println("Enter " + budgetType + " budget amount:");
			Double inputAmt = inputDouble(allowBlankInput);
			if (inputAmt == null) return;

			try {
				if (budgetType.equals("Monthly")) {
					myGeneralLedger.updateMonthlyBudgetAmount(month,year,inputType,inputAmt);
				} else {
					myGeneralLedger.updateDefaultBudgetAmount(inputType,inputAmt);
				}
			} catch (IllegalArgumentException e) {
				System.out.println(e);	
			}
		}
	}

	/**
	 * This converts a String input into a JDateTime object
	 * 
	 * @param inputDate - String containing the date text
	 * @return - JDateTime object containing the date
	 */
	private static JDateTime convertStringToDate(String inputDate) {
		// replace periods with slashes
		if (inputDate.contains(".") == true) {
			inputDate = inputDate.replace(".","/");
		}
		//System.out.println("Date:" + inputDate);
		String parts[] = inputDate.split("/");
		// convert year from 2 digits to 4 digits
		if (parts[2].length() == 2) { 
			parts[2] = "20" + parts[2]; 
		}
		int month = Integer.parseInt(parts[0]);
		int day = Integer.parseInt(parts[1]);
		int year = Integer.parseInt(parts[2]);

		return new JDateTime(year, month, day);
	}

	/**
	 * This method converts a JDateTime object into a String object
	 * 
	 * @param inputDate - JDateTime object containing the date
	 * @return - String object containing the date text
	 */
	private static String convertDatetoString(JDateTime inputDate) {
		if (inputDate != null) {
			return (inputDate.getMonth() + "/" + inputDate.getDay() + "/" + inputDate.getYear());
		} else {
			return null;
		}
	}

	/** 
	 * This allows the user to delete a repeating entry from the ledger
	 */
	private static void deleteRepeatingEntry() {
		String checkForEntries = "R";
		JDateTime deleteMonth = chooseDate("Enter Month To Display Repeating Entries For or leave blank for all months (MM/DD/YYYY):", allowBlankInput, checkForEntries);

		if (deleteMonth == null) {
			System.out.println("List of all the repeating entries");
			PrintLedger.printRepeatingEntries(myGeneralLedger);
		} else {
			System.out.println("List of all the repeating entries for month" + convertDatetoString(deleteMonth));
			PrintLedger.printRepeatingEntries(myGeneralLedger,deleteMonth);
		}

		System.out.println("Enter index of which entry for " + convertDatetoString(deleteMonth) + " that you want to delete: (starting with 1)");
		Integer deleteIndex = inputNumericSelection(allowBlankInput);
		if (deleteIndex == null) return;

		int entryCnt = myGeneralLedger.getRepeatingEntryList(deleteMonth).size();
		if ((deleteIndex > 0) && (deleteIndex <= entryCnt)) {
			try {
				// get description of repeating entry we want to delete, because that is how we index the array of repeating entries
				RepeatingEntry tmpRepeatEntry = null;
				if (deleteMonth != null) {
					tmpRepeatEntry = myGeneralLedger.getRepeatingEntryList(deleteMonth).get(deleteIndex-1);
				} else {
					tmpRepeatEntry = myGeneralLedger.getRepeatingEntryList().get(deleteIndex-1);
				}
				String deleteEntryDesc = tmpRepeatEntry.getDesc();
				myGeneralLedger.deleteRepeatingEntry(deleteEntryDesc);
			} catch (IllegalArgumentException e) {
				System.out.println(e);
			}
		} else {
			System.out.println("Cannot delete that entry, this month only has " + entryCnt + " repeating entries in it.");
		}
	}

	/**
	 * This allows the user to delete a single entry from the ledger
	 */
	private static void deleteSingleEntry() {
		String checkForEntries = "S";
		JDateTime deleteDate = chooseDate("Enter Date To Remove Entry From (MM/DD/YYYY):", allowBlankInput, checkForEntries);
		if (deleteDate == null) return;
		System.out.println("Do you want to see all the entries for " + convertDatetoString(deleteDate) + "? (Y/N)");
		String printDateEntries = myInputScanner.nextLine();

		if (printDateEntries.toUpperCase().equals("Y")) {
			PrintLedger.printSingleEntriesForDate(myGeneralLedger, deleteDate);
		} 

		System.out.println("Enter index of which entry for " + convertDatetoString(deleteDate) + " that you want to delete: (starting with 1)");
		Integer deleteIndex = inputNumericSelection(allowBlankInput);
		if (deleteIndex == null) return;

		int entryCnt = myGeneralLedger.getSingleEntryListForDate(deleteDate).size();
		if ((deleteIndex > 0) && (deleteIndex <= entryCnt)) {
			try {
				myGeneralLedger.deleteSingleEntry(deleteDate, deleteIndex-1);
			} catch (IllegalArgumentException e) {
				System.out.println(e);
			}
		} else {
			System.out.println("Cannot delete that entry" + convertDatetoString(deleteDate) + " only has " + entryCnt + " entries in it.");
		}
	}

	/**
	 * This inputs a double number from the user
	 * 
	 * @param allowBlankInput - if true then return null if input is blank
	 * @return a Double object or null
	 */
	private static Double inputDouble(boolean allowBlankInput) {
		int badInput = 0;
		Double inputNum = 0.0;
		do {
			badInput = 0;
			String stringInput = myInputScanner.nextLine();
			if (!stringInput.equals("")) {
				try { 
					inputNum = Double.parseDouble(stringInput); 
				} catch(NumberFormatException e) { 
					System.out.println("Input is not a number.");	
					badInput = 1;
				}
			} else {
				if (allowBlankInput == true) {
					inputNum = null;
				} else {
					System.out.println("Can't enter a blank number.");	
					badInput = 1;
				}
			}
		} while (badInput == 1);

		return inputNum;
	}
	
	/**
	 * This inputs an Integer from the user
	 * 
	 * @param allowBlankInput - if true then return null if input is blank
	 * @return an Integer object or null
	 */
	private static Integer inputNumericSelection(boolean allowBlankInput) {
		int badInput = 0;
		Integer inputNum = 0;
		do {
			badInput = 0;
			String stringInput = myInputScanner.nextLine();
			if (!stringInput.equals("")) {
				try { 
					inputNum = Integer.parseInt(stringInput); 
					if (inputNum < 0) {
						System.out.println("Input must be zero or greater.");	
						badInput = 1;
					}
				} catch(NumberFormatException e) { 
					System.out.println("Input is not a number.");	
					badInput = 1;
				}
			} else {
				if (allowBlankInput == true) {
					inputNum = null;
				} else {
					System.out.println("Can't enter a blank number.");	
					badInput = 1;
				}
			}
		} while (badInput == 1);

		return inputNum;
	}

	/**
	 * Allows user to print entire ledger 
	 */
	private static void printEntireLedger() {
		try {
			System.out.println("Single Entries");	
			PrintLedger.printSingleEntries(myGeneralLedger);
			System.out.println("");	
			System.out.println("Repeating Entries:");	
			PrintLedger.printRepeatingEntries(myGeneralLedger);
		} catch (IllegalArgumentException e) {
			System.out.println(e);	
		} 
	}

	/**
	 * Allows users to print ledger for a specific month and year
	 */
	private static void printMonthLedger() {
		System.out.println("Enter a date for which month to view:");
		String inputDate = myInputScanner.nextLine();
		if (inputDate.equals("")) return;
		JDateTime tempDate = convertStringToDate(inputDate);

		int month = tempDate.getMonth();
		int year = tempDate.getYear();

		try {
			PrintLedger.printMonth(myGeneralLedger, month, year);
		} catch (IllegalArgumentException e) {
			System.out.println(e);	
		} 
	}

	/** 
	 * This allows the user to update a repeating entry in the ledger
	 */
	private static void updateRepeatingEntry() {
		System.out.println("Do you want to see all the repeating entries for a specific month or see all repeating entries?");
		String checkForEntries = "R";
		JDateTime updateMonth = chooseDate("Enter Date of month to see repeating entries for, or leave blank for all repeating entires (MM/DD/YYYY):", allowBlankInput, checkForEntries);

		if (updateMonth == null) {
			System.out.println("List of all the repeating entries");
			PrintLedger.printRepeatingEntries(myGeneralLedger);
		} else {
			System.out.println("List of all the repeating entries for month" + convertDatetoString(updateMonth));
			PrintLedger.printRepeatingEntries(myGeneralLedger,updateMonth);
		}

		System.out.println("Enter index of which entry for " + convertDatetoString(updateMonth) + " that you want to update: (starting with 1)");
		Integer updateIndex = inputNumericSelection(allowBlankInput);
		if (updateIndex == null) return;

		RepeatingEntry tmpEntry;
		if (updateMonth != null) {
			tmpEntry = myGeneralLedger.getRepeatingEntryList(updateMonth).get(updateIndex-1);			
		} else {
			tmpEntry = myGeneralLedger.getRepeatingEntryList().get(updateIndex-1);
		}

		String oldDesc = tmpEntry.getDesc();
		JDateTime oldStartDate = tmpEntry.getStartDate();
		JDateTime oldEndDate = tmpEntry.getEndDate();
		Account oldAcct = tmpEntry.getAccount();
		double oldAmt = tmpEntry.getAmount();

		String newDesc;
		JDateTime newStartDate;
		JDateTime newEndDate;
		Account newAcct;
		Double newAmt;

		System.out.println("Old description is " + oldDesc);
		System.out.println("Enter new description or leave blank to keep old description:");
		newDesc = myInputScanner.nextLine();
		if (newDesc == "") {
			newDesc = oldDesc;
		}

		System.out.println("Old start date of repeating entry is " + convertDatetoString(oldStartDate));
		System.out.println("Enter new date or leave blank to keep old date:");
		String tmpDate = myInputScanner.nextLine();
		if (tmpDate.equals("")) {
			newStartDate = oldStartDate;
		} else {
			newStartDate = convertStringToDate(tmpDate);
		}

		System.out.println("Old end date of repeating entry is " + convertDatetoString(oldEndDate));
		System.out.println("Enter new date or leave blank to keep old date:");
		tmpDate = myInputScanner.nextLine();
		if (tmpDate.equals("")) {
			newEndDate = oldEndDate;
		} else {
			newEndDate = convertStringToDate(tmpDate);
		}

		System.out.println("Old account is " + oldAcct.getAccountName());
		try {
			newAcct = chooseAccount("Enter new account or leave blank to keep old account:", allowBlankInput);
		} catch (IllegalArgumentException e) {
			System.out.println("Error: Database has no accounts to choose from. Aborting input.");
			return;
		}
		if (newAcct == null) {
			newAcct = oldAcct;
		}

		System.out.println("Old amount is " + oldAmt);
		System.out.println("Enter new amount or leave blank to keep old amount:");
		newAmt = inputDouble(allowBlankInput);
		if (newAmt == null) {
			newAmt = oldAmt;
		}

		try {
			myGeneralLedger.updateRepeatingEntry(oldDesc, newStartDate, newEndDate, newDesc, newAcct, newAmt);
		} catch (IllegalArgumentException e) {
			System.out.println(e);
		}
	}

	/**
	 * This allows the user to update a single entry in the ledger
	 */
	private static void updateSingleEntry() {
		// TODO: prompt user to print entries for a given date range or for a month instead of just for a single date
		String checkForEntries = "S";
		JDateTime updateDate = chooseDate("Enter Date To Update Entry On (MM/DD/YYYY):", allowBlankInput, checkForEntries);
		if (updateDate == null) return;
		System.out.println("Do you want to see all the entries for " + convertDatetoString(updateDate) + "? (Y/N)");
		String printDateEntries = myInputScanner.nextLine();

		if (printDateEntries.toUpperCase().equals("Y")) {
			PrintLedger.printSingleEntriesForDate(myGeneralLedger, updateDate);
		} 

		SingleEntry tmpEntry = null;
		Integer updateIndex;
		int badInput = 0;
		do {
			badInput = 0;
			System.out.println("Enter index of which entry for " + convertDatetoString(updateDate) + " that you want to update: (starting with 1)");
			updateIndex = inputNumericSelection(allowBlankInput);
			if (updateIndex == null) return;
			if (myGeneralLedger.getSingleEntryListForDate(updateDate).contains(updateIndex-1)) {
				tmpEntry = myGeneralLedger.getSingleEntryListForDate(updateDate).get(updateIndex-1);
			} else {
				System.out.println("There are only " + myGeneralLedger.getSingleEntryListForDate(updateDate).size() + " entries for date " + convertDatetoString(updateDate));	
			}
		} while (badInput == 1);

		String oldDesc = tmpEntry.getDesc();
		JDateTime oldDate = tmpEntry.getDate();
		Account oldAcct = tmpEntry.getAccount();
		double oldAmt = tmpEntry.getAmount();

		String newDesc;
		JDateTime newDate;
		Account newAcct;
		Double newAmt;

		System.out.println("Old description is " + oldDesc);
		System.out.println("Enter new description or leave blank to keep old description:");
		newDesc = myInputScanner.nextLine();
		if (newDesc == "") {
			newDesc = oldDesc;
		}

		System.out.println("Old entry date is " + convertDatetoString(oldDate));
		System.out.println("Enter new date or leave blank to keep old date:");
		String tmpDate = myInputScanner.nextLine();
		if (tmpDate.equals("")) {
			newDate = oldDate;
		} else {
			newDate = convertStringToDate(tmpDate);
		}

		System.out.println("Old account is " + oldAcct.getAccountName());
		try {
			newAcct = chooseAccount("Enter new account or leave blank to keep old account:", allowBlankInput);
		} catch (IllegalArgumentException e) {
			System.out.println("Error: Database has no accounts to choose from. Aborting input.");
			return;
		}
		if (newAcct == null) {
			newAcct = oldAcct;
		}

		System.out.println("Old amount is " + oldAmt);
		System.out.println("Enter new amount or leave blank to keep old amount:");
		newAmt = inputDouble(allowBlankInput);
		if (newAmt == null) {
			newAmt = oldAmt;
		}

		try {
			myGeneralLedger.updateSingleEntry(updateDate, updateIndex-1, newDate, newDesc, newAcct, newAmt);
		} catch (IllegalArgumentException e) {
			System.out.println(e);
		}
	}

}
