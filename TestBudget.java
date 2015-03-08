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
	
	/**
	 * This method controls the main UI of the budget_program class
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		GeneralLedger myGeneralLedger = new GeneralLedger();

		// load SQL database data into Java data structures
		myGeneralLedger.loadSQLData();

		Scanner stdInputScanner = new Scanner( System.in );		

		int abort = 0;
		do {
			System.out.println("");
			System.out.println("Main Menu");
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

			String menuChoice = stdInputScanner.nextLine();

			// add a new month to the database
			if (menuChoice.toUpperCase().equals("AM")) {
				System.out.println("Enter a date for which month to create a new budget for:");
				String inputDate = stdInputScanner.nextLine();
				JDateTime tempDate = convertStringToDate(inputDate);

				int month = tempDate.getMonth();
				int year = tempDate.getYear();

				try {
					myGeneralLedger.addBudget(month, year);
				} catch (IllegalArgumentException e) {
					System.out.println(e);	
				} 
			}

			// add a new account to the database
			if (menuChoice.toUpperCase().equals("AC")) {
				System.out.println("Enter the account name:");	
				String inputName = stdInputScanner.nextLine();
				System.out.println("Enter the account description:");	
				String inputDesc = stdInputScanner.nextLine();

				char inputChoice;

				System.out.println("Enter 'Y' if the account is an expense or 'N' if the account is an income:");	
				boolean inputIsAnExpense;
				inputChoice = stdInputScanner.nextLine().charAt(0);
				if (inputChoice == 'Y' | inputChoice == 'y') {
					inputIsAnExpense = true;
				} else {
					inputIsAnExpense = false;
				}

				System.out.println("Enter 'Y' if the account is displayed in the default monthly account listing");	
				boolean inputInDefaultAccountList;
				inputChoice = stdInputScanner.nextLine().charAt(0);
				if (inputChoice == 'Y' | inputChoice == 'y') {
					inputInDefaultAccountList = true;
				} else {
					inputInDefaultAccountList = false;
				}

				System.out.println("Enter 'Y' if the account is included in the budget or 'N' if the account is not included in the budget:");	
				boolean inputIsInBudget;
				inputChoice = stdInputScanner.nextLine().charAt(0);
				double inputDefaultBudgetAmt = 0;
				if (inputChoice == 'Y' | inputChoice == 'y') {
					inputIsInBudget = true;
					if (inputInDefaultAccountList == true) {
						System.out.println("Enter default amount to budget for this account:");	
						inputDefaultBudgetAmt = inputDouble(stdInputScanner,prohibitBlankInput);
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

			// add an entry to a month
			if (menuChoice.toUpperCase().equals("AE") ) {
				System.out.println("Enter type of entry 'S'ingle, 'R'epeating or 'I'nstallment:");
				String entryType = stdInputScanner.nextLine();

				// single entries
				if (entryType.toUpperCase().equals("S")) {

					// enter date for entry
					String checkForEntries = "";
					JDateTime entryDate = chooseDate(myGeneralLedger, stdInputScanner, "Enter Entry Date:", prohibitBlankInput, checkForEntries);

					// enter description
					System.out.println("Enter Entry Desc:");
					String inputDesc = stdInputScanner.nextLine();

					// select account
					Account inputAcct;
					try {
						inputAcct = chooseAccount(myGeneralLedger, stdInputScanner, "", prohibitBlankInput);
					} catch (IllegalArgumentException e) {
						System.out.println("Error: Database has no accounts to choose from. Aborting input.");
						return;
					}

					// enter amount
					System.out.println("Enter Entry Amount:");
					double inputAmt = inputDouble(stdInputScanner,prohibitBlankInput);

					try {
						myGeneralLedger.addSingleEntry(entryDate, inputDesc, inputAcct, inputAmt);
					} catch (IllegalArgumentException e) {
						System.out.println("Error: " + e);
					}
				}

				//repeating entries
				if (entryType.toUpperCase().equals("R")) {

					// enter start date for entry
					String checkForEntries = "";
					JDateTime startDate = chooseDate(myGeneralLedger, stdInputScanner, "Enter Start Date of Repeating Entries (or leave blank for all months):", allowBlankInput, checkForEntries);

					// enter end date for entry
					JDateTime endDate = chooseDate(myGeneralLedger, stdInputScanner, "Enter End Date of Repeating Entries (or leave blank for all months):", allowBlankInput, checkForEntries);

					// enter description
					System.out.println("Enter Entry Desc:");
					String inputDesc = stdInputScanner.nextLine();

					// select account
					Account inputAcct;
					try {
						inputAcct = chooseAccount(myGeneralLedger, stdInputScanner, "", prohibitBlankInput);
					} catch (IllegalArgumentException e) {
						System.out.println("Error: Database has no accounts to choose from. Aborting input.");
						return;
					}

					// enter amount
					System.out.println("Enter Amount For Each Month:");
					double inputAmt = inputDouble(stdInputScanner,prohibitBlankInput);

					myGeneralLedger.addRepeatingEntry(startDate, endDate, inputDesc, inputAcct, inputAmt);
				}

				//installment entries
				if (entryType.toUpperCase().equals("I")) {
					System.out.println("Installment entries aren't functional yet");
					/*String tmpDate;

					// enter start date for entry
					System.out.println("Enter Start Date of Installment Entries:");
					tmpDate = stdInputScanner.nextLine();

					JDateTime startDate = formatDate(tmpDate);

					// enter end date for entry
					System.out.println("Enter End Date of Installment Entries:");
					tmpDate = stdInputScanner.nextLine();

					JDateTime endDate = formatDate(tmpDate);

					// enter description
					System.out.println("Enter Entry Desc:");
					String inputDesc = stdInputScanner.nextLine();

					// select account
					if (myGeneralLedger.getAccountList().size() == 0) {
						System.out.println("Database has no accounts to choose from. Aborting input.");
						return;
					} 

					int inputNum = 0;
					int badCategory = 0;
					do {
						String categoryMsg = "Select account ";
						for (int i = 0; i < myGeneralLedger.getAccountList().size(); i++) {
							categoryMsg = categoryMsg + i + "-" + myGeneralLedger.getAccountList().get(i).getAccountName() + " ";
						}
						System.out.println(categoryMsg);
						inputNum = Integer.parseInt(stdInputScanner.nextLine());

						if (inputNum > myGeneralLedger.getAccountList().size()) {
							System.out.println("Invalid account number");	
							badCategory = 1;
						}
					} while (badCategory == 1);

					Type inputType = myGeneralLedger.getAccountList().get(inputNum);

					// enter amount
					System.out.println("Enter Total Amount:");
					double inputAmt = Double.parseDouble(stdInputScanner.nextLine());

					myGeneralLedger.addInstallmentEntry(startDate, endDate, inputDesc, inputType, inputAmt);*/
				}
			}

			// delete an entry from a specific month
			if (menuChoice.toUpperCase().equals("DE")) {
				System.out.println("Enter type of entry 'S'ingle, 'R'epeating or 'I'nstallment:");
				String entryType = stdInputScanner.nextLine();

				// single entries
				if (entryType.toUpperCase().equals("S")) {
					String checkForEntries = "S";
					JDateTime deleteDate = chooseDate(myGeneralLedger, stdInputScanner, "Enter Date To Remove Entry From (MM/DD/YYYY):", prohibitBlankInput, checkForEntries);
					System.out.println("Do you want to see all the entries for " + convertDatetoString(deleteDate) + "? (Y/N)");
					String printDateEntries = stdInputScanner.nextLine();

					if (printDateEntries.toUpperCase().equals("Y")) {
						PrintLedger.printSingleEntriesForDate(myGeneralLedger, deleteDate);
					} 

					System.out.println("Enter index of which entry for " + convertDatetoString(deleteDate) + " that you want to delete: (starting with 1)");
					int deleteIndex = inputNumericSelection(stdInputScanner,prohibitBlankInput);

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

				//repeating entries
				if (entryType.toUpperCase().equals("R")) {
					String checkForEntries = "R";
					JDateTime deleteDate = chooseDate(myGeneralLedger, stdInputScanner, "Enter Month To Remove Entry From (MM/DD/YYYY):", prohibitBlankInput, checkForEntries);
					System.out.println("Do you want to see all the repeating entries for this month? (Y/N)");
					String printDateEntries = stdInputScanner.nextLine();

					if (printDateEntries.toUpperCase().equals("Y")) {
						PrintLedger.printRepeatingEntries(myGeneralLedger, deleteDate);
					} 

					System.out.println("Enter index of which entry for this month that you want to delete: (starting with 1)");
					int deleteIndex = inputNumericSelection(stdInputScanner,false);

					int entryCnt = myGeneralLedger.getRepeatingEntryListForMonth(deleteDate).size();
					if ((deleteIndex > 0) && (deleteIndex <= entryCnt)) {
						try {
							// get description of repeating entry we want to delete, because that is how we index the array of repeating entries
							RepeatingEntry tmpRepeatEntry = myGeneralLedger.getRepeatingEntryListForMonth(deleteDate).get(deleteIndex);
							String deleteEntryDesc = tmpRepeatEntry.getDesc();
							myGeneralLedger.deleteRepeatingEntry(deleteEntryDesc);
						} catch (IllegalArgumentException e) {
							System.out.println(e);
						}
					} else {
						System.out.println("Cannot delete that entry, this month only has " + entryCnt + " repeating entries in it.");
					}
				}

				//installment entries
				if (entryType.toUpperCase().equals("I")) {
					System.out.println("Installment entries aren't functional yet");
				}
			}


			// update an entry from a specific month
			if (menuChoice.toUpperCase().equals("UE")) {

				// do we want to prompt user to print entries for a given date range or for a month?
				String checkForEntries = "S";
				JDateTime updateDate = chooseDate(myGeneralLedger, stdInputScanner, "Enter Date To Update Entry On (MM/DD/YYYY):", prohibitBlankInput, checkForEntries);
				System.out.println("Do you want to see all the entries for " + convertDatetoString(updateDate) + "? (Y/N)");
				String printDateEntries = stdInputScanner.nextLine();

				if (printDateEntries.toUpperCase().equals("Y")) {
					PrintLedger.printSingleEntriesForDate(myGeneralLedger, updateDate);
				} 

				System.out.println("Enter index of which entry for " + convertDatetoString(updateDate) + " that you want to update: (starting with 1)");
				int updateIndex = inputNumericSelection(stdInputScanner,false);

				SingleEntry tmpEntry = myGeneralLedger.getSingleEntryListForDate(updateDate).get(updateIndex-1);

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
				newDesc = stdInputScanner.nextLine();
				if (newDesc == "") {
					newDesc = oldDesc;
				}

				System.out.println("Old entry date is " + convertDatetoString(oldDate));
				System.out.println("Enter new date or leave blank to keep old date:");
				String tmpDate = stdInputScanner.nextLine();
				if (tmpDate.equals("")) {
					newDate = oldDate;
				} else {
					newDate = convertStringToDate(tmpDate);
				}

				System.out.println("Old account is " + oldAcct.getAccountName());
				try {
					newAcct = chooseAccount(myGeneralLedger, stdInputScanner, "Enter new account or leave blank to keep old account:", allowBlankInput);
				} catch (IllegalArgumentException e) {
					System.out.println("Error: Database has no accounts to choose from. Aborting input.");
					return;
				}
				if (newAcct == null) {
					newAcct = oldAcct;
				}

				System.out.println("Old amount is " + oldAmt);
				System.out.println("Enter new amount or leave blank to keep old amount:");
				String tmpAmt = stdInputScanner.nextLine();
				//
				newAmt = inputDouble(stdInputScanner,allowBlankInput);
				if (newAmt == null) {
					newAmt = oldAmt;
				}

				try {
					myGeneralLedger.updateSingleEntry(updateDate, updateIndex-1, newDate, newDesc, newAcct, newAmt);
				} catch (IllegalArgumentException e) {
					System.out.println(e);
				}
			} 

			// configure default budget account/amount data
			if (menuChoice.toUpperCase().equals("CD")) {
				System.out.println("Default budget account/amount options:");
				System.out.println("A - Add new account and amount to default budget");
				System.out.println("R - Remove account and amount from default budget");
				System.out.println("U - Update amount for an account on the default budget");

				char menuChoice2 = stdInputScanner.nextLine().charAt(0);

				if (menuChoice2 == 'A' || menuChoice2 == 'a') {
					// enter account
					Account inputType = chooseAccount(myGeneralLedger,stdInputScanner,"Select account to add to default budget ",prohibitBlankInput);

					// enter amount
					System.out.println("Enter default budget amount:");
					double inputAmt = inputDouble(stdInputScanner,prohibitBlankInput);

					myGeneralLedger.addDefaultBudgetAccount(inputType,inputAmt);
				}

				// remove account from account list
				if (menuChoice2 == 'R' || menuChoice2 == 'r') {
					// enter account
					Account inputType = chooseAccount(myGeneralLedger,stdInputScanner,"Select account to remove from default budget ",prohibitBlankInput);

					myGeneralLedger.removeDefaultBudgetAccount(inputType);
				}

				// update account in account list
				if (menuChoice2 == 'U' || menuChoice2 == 'u') {
					// enter account
					Account inputType = chooseAccount(myGeneralLedger,stdInputScanner,"Select account to update in default budget ",prohibitBlankInput);

					// enter amount
					System.out.println("Enter default budget amount:");
					double inputAmt = inputDouble(stdInputScanner,prohibitBlankInput);

					try {
						myGeneralLedger.updateDefaultBudgetAmount(inputType,inputAmt);
					} catch (IllegalArgumentException e) {
						System.out.println(e);	
					}
				}
			}

			// configure a monthly budget account/amount data
			if (menuChoice.toUpperCase().equals("CB")) {

				// enter date for entry
				System.out.println("Enter a date for which month to update:");
				String inputDate = stdInputScanner.nextLine();
				JDateTime tempDate = convertStringToDate(inputDate);

				int month = tempDate.getMonth();
				int year = tempDate.getYear();

				System.out.println(month + "-" + year + " monthly budget account/amount option:");
				System.out.println("A - Add new account and amount to monthly budget");
				System.out.println("R - Remove account and amount from monthly budget");
				System.out.println("U - Update amount for an account on the monthly budget");

				char menuChoice2 = stdInputScanner.nextLine().charAt(0);

				if (menuChoice2 == 'A' || menuChoice2 == 'a') {
					// enter account
					Account inputType = chooseAccount(myGeneralLedger,stdInputScanner,"Select account to add to monthly budget ",prohibitBlankInput);

					// enter amount
					System.out.println("Enter monthly budget amount:");
					double inputAmt = inputDouble(stdInputScanner,prohibitBlankInput);

					myGeneralLedger.addMonthlyBudgetAccount(month,year,inputType,inputAmt);
				}

				if (menuChoice2 == 'R' || menuChoice2 == 'r') {
					// enter account
					Account inputType = chooseAccount(myGeneralLedger,stdInputScanner,"Select account to remove from monthly budget ",prohibitBlankInput);

					myGeneralLedger.removeMonthlyBudgetAccount(month,year,inputType);
				}

				if (menuChoice2 == 'U' || menuChoice2 == 'u') {
					// enter account
					Account inputType = chooseAccount(myGeneralLedger,stdInputScanner,"Select account to update in monthly budget ",prohibitBlankInput);

					// enter amount
					System.out.println("Enter monthly budget amount:");
					double inputAmt = inputDouble(stdInputScanner,prohibitBlankInput);

					try {
						myGeneralLedger.updateMonthlyBudgetAmount(month,year,inputType,inputAmt);
					} catch (IllegalArgumentException e) {
						System.out.println(e);	
					}
				}
			}

			// print the contents of a month
			if (menuChoice.toUpperCase().equals("PM")) {
				System.out.println("Enter a date for which month to view:");
				String inputDate = stdInputScanner.nextLine();
				JDateTime tempDate = convertStringToDate(inputDate);

				int month = tempDate.getMonth();
				int year = tempDate.getYear();

				try {
					PrintLedger.printMonth(myGeneralLedger, month, year);
				} catch (IllegalArgumentException e) {
					System.out.println(e);	
				} 
			}

			// print the contents of the entire general ledger
			if (menuChoice.toUpperCase().equals("PA")) {
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
				abort = 1;
				myGeneralLedger.saveSQLData();
				System.out.println("Ending Program");
			}

			if (abort == 0) {
				System.out.println("<hit return to continue>");	
				String junk = stdInputScanner.nextLine();
			}

		} while (abort == 0);
	}

	/**
	 * This prompts the user to choose an account number from the list of all accounts in the general ledger
	 * 
	 * @param glData - general ledger object
	 * @param inputScanner - scanner object used to process user's input
	 * @param prompt - message display before user selects account
	 * @param allowBlankInput - boolean that controls if we allow blank input
	 * @exception - if glData has no account to choose from
	 * @return - Account object chosen by user
	 */
	private static Account chooseAccount(GeneralLedger glData, Scanner inputScanner, String prompt, boolean allowBlankInput) {
		if (glData.getAccountList().size() == 0) {
			throw new IllegalArgumentException("The general ledger has no accounts to select from.");
		} 
		if (prompt != "") {
			System.out.println(prompt);
		}

		int badInput;
		int inputNum = 0;

		do {
			badInput = 0;

			String categoryMsg = "Select account ";
			for (int i = 0; i < glData.getAccountList().size(); i++) {
				categoryMsg = categoryMsg + i + "-" + glData.getAccountList().get(i).getAccountName() + " ";
			}
			System.out.println(categoryMsg);

			inputNum = inputNumericSelection(inputScanner,allowBlankInput);
					
			if (inputNum != -1) {			
				if (inputNum > glData.getAccountList().size()) {
					System.out.println("Invalid account number.");	
					badInput = 1;
				}  
			}
		} while (badInput == 1);

		if (inputNum != -1) {
			return glData.getAccountList().get(inputNum);
		} else {
			return null;
		}
	}

	/**
	 * This prompts the user to enter a date
	 * 
	 * @param glData - general ledger object
	 * @param inputScanner - scanner object used to process user's input
	 * @param prompt - message to display before user enters a date
	 * @param allowBlankInput - boolean that controls if we allow blank input
	 * @param checkForEntries - String that controls if we make sure general ledger has 'S'ingle, 'R'epeating or 'I'nstallment entries for this date (or blank to skip validation)
	 * @return JDateTime object for date entered by user
	 */
	private static JDateTime chooseDate(GeneralLedger glData, Scanner inputScanner, String prompt, boolean allowBlankInput, String checkForEntries) {
		JDateTime newDate = null;

		int badDate;
		do {
			badDate = 0;

			System.out.println(prompt);
			String stringInput = inputScanner.nextLine();
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
					ArrayList<SingleEntry> tmpList = glData.getSingleEntryListForDate(newDate);
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
					ArrayList<RepeatingEntry> tmpList = glData.getRepeatingEntryListForMonth(newDate);
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
		return (inputDate.getMonth() + "/" + inputDate.getDay() + "/" + inputDate.getYear());
	}

	
	/**
	 * This inputs a double number from the user
	 * 
	 * @param inputScanner - scanner object used to process user's input
	 * @param allowBlankInput - if true then return null if input is blank
	 * @return a Double object or null
	 */
	private static Double inputDouble(Scanner inputScanner, boolean allowBlankInput) {
		int badInput = 0;
		Double inputNum = 0.0;
		do {
			badInput = 0;
			String stringInput = inputScanner.nextLine();
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
		} while (badInput == 0);

		return inputNum;
	}
	
	/**
	 * This inputs an Integer from the user
	 * 
	 * @param inputScanner - scanner object used to process user's input
	 * @param allowBlankInput - if true then return null if input is blank
	 * @return an Integer object or null
	 */
	private static Integer inputNumericSelection(Scanner inputScanner, boolean allowBlankInput) {
		int badInput = 0;
		Integer inputNum = 0;
		do {
			badInput = 0;
			String stringInput = inputScanner.nextLine();
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
		} while (badInput == 0);

		return inputNum;
	}

}
