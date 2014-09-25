package budget_program;

import java.util.*;

import jodd.datetime.JDateTime;

/**
 * This class provides a text-based user-interface to let the user create and maintain a monthly ledger 
 * and a budget using the GeneralLedger class
 * 
 * @author Asa Swain
 */

// TODO: print month method disabled

public class TestBudget {

	/**
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
				System.out.println("Enter the month number to create new budget for:");					
				int month = Integer.parseInt(stdInputScanner.nextLine());
				System.out.println("Enter the year to create new budget for:");	
				int year = Integer.parseInt(stdInputScanner.nextLine());

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
						inputDefaultBudgetAmt = Double.parseDouble(stdInputScanner.nextLine());
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
					System.out.println("Enter Entry Date:");
					String inputDate = stdInputScanner.nextLine();

					JDateTime entryDate = formatDate(inputDate);

					//System.out.println("You typed: " + entryDate.getMonth() + '-' + entryDate.getDay() + '-' + entryDate.getYear());

					// enter description
					System.out.println("Enter Entry Desc:");
					String inputDesc = stdInputScanner.nextLine();

					// select account
					if (myGeneralLedger.getAccountList().size() == 0) {
						System.out.println("Database has no accounts to choose from. Aborting input.");
						return;
					} 

					int inputNum = 0;
					int badCategory;
					do {
						String categoryMsg = "Select account ";
						for (int i = 0; i < myGeneralLedger.getAccountList().size(); i++) {
							categoryMsg = categoryMsg + i + "-" + myGeneralLedger.getAccountList().get(i).getTypeName() + " ";
						}
						System.out.println(categoryMsg);
						inputNum = Integer.parseInt(stdInputScanner.nextLine());

						if (inputNum > myGeneralLedger.getAccountList().size()) {
							System.out.println("Invalid account number");	
							badCategory = 1;
						} else {
							badCategory = 0;
						}
					} while (badCategory == 1);

					Account inputType = myGeneralLedger.getAccountList().get(inputNum);

					// enter amount
					System.out.println("Enter Entry Amount:");
					double inputAmt = Double.parseDouble(stdInputScanner.nextLine());

					try {
						myGeneralLedger.addSingleEntry(entryDate, inputDesc, inputType, inputAmt);
					} catch (IllegalArgumentException e) {
						System.out.println("Error: " + e);
					}
				}

				//repeating entries
				if (entryType.toUpperCase().equals("R")) {
					System.out.println("Repeating entries aren't functional yet");
					/*String tmpDate;

					// enter start date for entry
					System.out.println("Enter Start Date of Repeating Entries:");
					tmpDate = stdInputScanner.nextLine();

					JDateTime startDate = formatDate(tmpDate);

					// enter end date for entry
					System.out.println("Enter End Date of Repeating Entries:");
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
							categoryMsg = categoryMsg + i + "-" + myGeneralLedger.getAccountList().get(i).getTypeName() + " ";
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
					System.out.println("Enter Amount For Each Month:");
					double inputAmt = Double.parseDouble(stdInputScanner.nextLine());

					myGeneralLedger.addRepeatingEntry(startDate, endDate, inputDesc, inputType, inputAmt);*/
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
							categoryMsg = categoryMsg + i + "-" + myGeneralLedger.getAccountList().get(i).getTypeName() + " ";
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

				// enter date for entry
				System.out.println("Enter Date of Month To Remove Entry From (MM/DD/YYYY):");
				String inputDate = stdInputScanner.nextLine();

				JDateTime monthDate = formatDate(inputDate);

				System.out.println("Do you want me to print all the entries? (Y/N)");
				String printMonthEntries = stdInputScanner.nextLine();
				if (printMonthEntries.toUpperCase().equals("Y")) {
					// print monthly income/expenses without budget info
					boolean printBudget = false;
					myGeneralLedger.printAllEntries();
				}

				JDateTime deleteDate = null;
				int badDate;
				do {
					System.out.println("Enter Date To Remove Entry From (MM/DD/YYYY):");
					inputDate = stdInputScanner.nextLine();
					deleteDate = formatDate(inputDate);

					// check to make sure there are any entries on this date
					if (myGeneralLedger.getDailyLedgerSingleEntryCount(deleteDate) > 0) {
						badDate = 0;
					} else {
						badDate = 1;
					}
				} while (badDate == 1); 

				System.out.println("Do you want me to print all the entries for " + deleteDate + "? (Y/N)");
				String printDateEntries = stdInputScanner.nextLine();

				if (printDateEntries.toUpperCase().equals("Y")) {
					myGeneralLedger.printDaysEntries(deleteDate);
				} 

				System.out.println("Enter index of which entry for " + deleteDate + " that you want to delete: (starting with 1)");
				String inputIndex = stdInputScanner.nextLine();
				int deleteIndex = Integer.parseInt(inputIndex);

				try {
					myGeneralLedger.deleteSingleEntry(deleteDate, deleteIndex-1);
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
					int inputNum = 0;
					int badCategory = 0;
					do {
						String categoryMsg = "Select account to add to budget ";
						for (int i = 0; i < myGeneralLedger.getAccountList().size(); i++) {
							categoryMsg = categoryMsg + i + "-" + myGeneralLedger.getAccountList().get(i).getTypeName() + " ";
						}
						System.out.println(categoryMsg);
						inputNum = Integer.parseInt(stdInputScanner.nextLine());

						if (inputNum > myGeneralLedger.getAccountList().size()) {
							System.out.println("Invalid category number");	
							badCategory = 1;
						}
					} while (badCategory == 1);

					Account inputType = myGeneralLedger.getAccountList().get(inputNum);

					// enter amount
					System.out.println("Enter default budget amount:");
					double inputAmt = Double.parseDouble(stdInputScanner.nextLine());

					myGeneralLedger.addDefaultBudgetAcct(inputType,inputAmt);
				}

				// remove account from account list
				if (menuChoice2 == 'R' || menuChoice2 == 'r') {
					// enter account
					int inputNum = 0;
					int badCategory = 0;
					do {
						String categoryMsg = "Select account to remove ";
						for (int i = 0; i < myGeneralLedger.getAccountList().size(); i++) {
							categoryMsg = categoryMsg + i + "-" + myGeneralLedger.getAccountList().get(i).getTypeName() + " ";
						}
						System.out.println(categoryMsg);
						inputNum = Integer.parseInt(stdInputScanner.nextLine());

						if (inputNum > myGeneralLedger.getAccountList().size()) {
							System.out.println("Invalid category number");	
							badCategory = 1;
						}
					} while (badCategory == 1);

					Account inputType = myGeneralLedger.getAccountList().get(inputNum);

					myGeneralLedger.removeDefaultBudgetAcct(inputType);
				}

				// update account in account list
				if (menuChoice2 == 'U' || menuChoice2 == 'u') {
					// enter account
					int inputNum = 0;
					int badCategory = 0;
					do {
						String categoryMsg = "Select account to add to budget ";
						for (int i = 0; i < myGeneralLedger.getAccountList().size(); i++) {
							categoryMsg = categoryMsg + i + "-" + myGeneralLedger.getAccountList().get(i).getTypeName() + " ";
						}
						System.out.println(categoryMsg);
						inputNum = Integer.parseInt(stdInputScanner.nextLine());

						if (inputNum > myGeneralLedger.getAccountList().size()) {
							System.out.println("Invalid category number");	
							badCategory = 1;
						}
					} while (badCategory == 1);

					Account inputType = myGeneralLedger.getAccountList().get(inputNum);

					// enter amount
					System.out.println("Enter default budget amount:");
					double inputAmt = Double.parseDouble(stdInputScanner.nextLine());

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

				JDateTime tempDate = formatDate(inputDate);

				int month = tempDate.getMonth();
				int year = tempDate.getYear();

				System.out.println(month + "-" + year + " monthly budget account/amount option:");
				System.out.println("A - Add new account and amount to monthly budget");
				System.out.println("R - Remove account and amount from monthly budget");
				System.out.println("U - Update amount for an account on the monthly budget");

				char menuChoice2 = stdInputScanner.nextLine().charAt(0);

				if (menuChoice2 == 'A' || menuChoice2 == 'a') {
					// enter account
					int inputNum = 0;
					int badCategory = 0;
					do {
						String categoryMsg = "Select account to add to monthly budget ";
						for (int i = 0; i < myGeneralLedger.getAccountList().size(); i++) {
							categoryMsg = categoryMsg + i + "-" + myGeneralLedger.getAccountList().get(i).getTypeName() + " ";
						}
						System.out.println(categoryMsg);
						inputNum = Integer.parseInt(stdInputScanner.nextLine());

						if (inputNum > myGeneralLedger.getAccountList().size()) {
							System.out.println("Invalid category number");	
							badCategory = 1;
						}
					} while (badCategory == 1);

					Account inputType = myGeneralLedger.getAccountList().get(inputNum);

					// enter amount
					System.out.println("Enter monthly budget amount:");
					double inputAmt = Double.parseDouble(stdInputScanner.nextLine());

					myGeneralLedger.addMonthlyBudgetAcct(month,year,inputType,inputAmt);
				}

				if (menuChoice2 == 'R' || menuChoice2 == 'r') {
					// enter account
					int inputNum = 0;
					int badCategory = 0;
					do {
						String categoryMsg = "Select account to remove from monthly budget";
						for (int i = 0; i < myGeneralLedger.getAccountList().size(); i++) {
							categoryMsg = categoryMsg + i + "-" + myGeneralLedger.getAccountList().get(i).getTypeName() + " ";
						}
						System.out.println(categoryMsg);
						inputNum = Integer.parseInt(stdInputScanner.nextLine());

						if (inputNum > myGeneralLedger.getAccountList().size()) {
							System.out.println("Invalid category number");	
							badCategory = 1;
						}
					} while (badCategory == 1);

					Account inputType = myGeneralLedger.getAccountList().get(inputNum);

					myGeneralLedger.removeMonthlyBudgetAcct(month,year,inputType);
				}

				if (menuChoice2 == 'U' || menuChoice2 == 'u') {
					// enter account
					int inputNum = 0;
					int badCategory = 0;
					do {
						String categoryMsg = "Select account to add to monthly budget";
						for (int i = 0; i < myGeneralLedger.getAccountList().size(); i++) {
							categoryMsg = categoryMsg + i + "-" + myGeneralLedger.getAccountList().get(i).getTypeName() + " ";
						}
						System.out.println(categoryMsg);
						inputNum = Integer.parseInt(stdInputScanner.nextLine());

						if (inputNum > myGeneralLedger.getAccountList().size()) {
							System.out.println("Invalid category number");	
							badCategory = 1;
						}
					} while (badCategory == 1);

					Account inputType = myGeneralLedger.getAccountList().get(inputNum);

					// enter amount
					System.out.println("Enter monthly budget amount:");
					double inputAmt = Double.parseDouble(stdInputScanner.nextLine());

					try {
						myGeneralLedger.updateMonthlyBudgetAmount(month,year,inputType,inputAmt);
					} catch (IllegalArgumentException e) {
						System.out.println(e);	
					}
				}
			}

			// print the contents of a month
			if (menuChoice.toUpperCase().equals("PM")) {
				System.out.println("Enter the month number to display:");					
				int month = Integer.parseInt(stdInputScanner.nextLine());
				System.out.println("Enter the year to display:");	
				int year = Integer.parseInt(stdInputScanner.nextLine());

//				try {
//					boolean printBudget = true;
//					myGeneralLedger.printMonth(month, year, printBudget);
//				} catch (IllegalArgumentException e) {
//					System.out.println(e);	
//				} 
			}

			// print the contents of the entire general ledger
			if (menuChoice.toUpperCase().equals("PA")) {
				try {
					boolean printBudget = true;
					myGeneralLedger.printAllEntries();
				} catch (IllegalArgumentException e) {
					System.out.println(e);	
				} 
			}

			// print the contents of the entire general ledger
			if (menuChoice.toUpperCase().equals("PD")) {
				try {
					myGeneralLedger.printDefaultBudget();
				} catch (IllegalArgumentException e) {
					System.out.println(e);	
				} 
			}

			// quiot program
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

	private static JDateTime formatDate(String inputDate) {
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
}
