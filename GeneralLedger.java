package budget_program;

import java.util.*;
import java.sql.*;

import jodd.datetime.*;

/**
 * This class is the master General Ledger class, which contains the following objects:
 * 1. The list of accounts all income and expense entries are divided among
 * 2. A list of income and expense entries
 * 3. A list of monthly budgets
 * 4. A default budget used when creating new monthly budgets
 * 5. The ledger name 
 * 
 * @author Asa Swain
 */

// Note: to view SQL database go to localhost in web browser after starting Apache and mySQL in XAMPP
//       (Skype will cause Apache not to start unless you prevent it from using ports 80 and 443)

// 11/17/2014 MASTER TO DO LIST:
//
//   1. write a Add Repeating Entry Methods
//      a. Add code to TestBudget class to update repeating entries and test updateRepeatingEntry
//      
//   2. add a "getListOfMonths" method
//   ...
//   87. work on making all get methods in GeneralLedger class immutable
//   98. finish writing getTotalAmountDonatedToCharity method
//   99. in SQLDatabaseConnection check for failed connection and throw error if unable to make connection
//   

public class GeneralLedger {
	// list of income and expenses
	private EntryList entryData;
	// TODO: implement installment and repeating entries methods

	// list of budgets for each month
	private TreeMap<JDateTime,Budget> monthlyBudgetList;
	// list of all general ledger accounts
	private ArrayList<Account> accountList;
	// HashMap of default account list and amounts for each monthly budget
	private Budget defaultBudget;
	// name of general ledger object
	private String ledgerName;

	private SQLDatabaseConnection mySQLDatabase;

	/**
	 * This is a blank constructor
	 */
	public GeneralLedger() {
		entryData = new EntryList();
		monthlyBudgetList = new TreeMap<JDateTime,Budget>();
		accountList = new ArrayList<Account>();
		defaultBudget = new Budget();
		ledgerName = "";
		mySQLDatabase = new SQLDatabaseConnection();
	}

	/**
	 * This loads data from the SQL database
	 */
	public void loadSQLData() {
		mySQLDatabase.createSQLtable();
		mySQLDatabase.loadDatabasesFromSQL();
	}

	/**
	 * This saves data to SQL database
	 */
	public void saveSQLData() {
		// erase all old databases
		mySQLDatabase.eraseSQLDatabases();
		// write out contents of objects to databases
		mySQLDatabase.saveDatabasesToSQL();

		// (later try to only save changes)
	}

	/**
	 * This adds an account number (aka a Type object) to the General Leder master account list
	 * and also may add the account to the Default Budget
	 * 
	 * @param inputName - The account name
	 * @param inputDesc - The account description
	 * @param isAnExpense - If this account is an expense (or an income)
	 * @param isIncludedInBudget - If this account is included in the budget
	 * @param isInDefaultAcctList - If this account should be included in the Default Budget account list
	 * @param defaultBudgetAmount - If this account should be included in the Default Budget, this is the amount
	 * 								allocated for this amount
	 */
	public void addAccount(String inputName, String inputDesc, boolean isAnExpense, boolean isIncludedInBudget, boolean isInDefaultAcctList, double defaultBudgetAmount) {
		// TODO: make sure no other account in list has the same name, names should be unique
		Account inputAccount = new Account(inputName, inputDesc, isAnExpense, isIncludedInBudget);
		if (accountList.contains(inputAccount) == false) {
			accountList.add(inputAccount);
			// check if we should also add the account to the default budget account list
			if (isInDefaultAcctList == true) {
				if (defaultBudget.isAccountInList(inputAccount) == false) {
					defaultBudget.addAccount(inputAccount, defaultBudgetAmount);
				} else {
					// account already exists in default budget account list
				}
			}
		} else {
			throw new IllegalArgumentException("Account " + inputName + " already exists in the account list.");
		}
	}

	/**
	 * This removes an account from the list of all accounts 
	 * and remove it from the default budget account list (if present)
	 * 
	 * @param deleteAccount - the account to delete
	 */
	public void removeAccount(Account deleteAccount) {
		accountList.remove(deleteAccount);
		if (defaultBudget.isAccountInList(deleteAccount) == true) {
			defaultBudget.deleteAccount(deleteAccount);
		}
	}

	/** 
	 * This returns a list of all the accounts in the general ledger
	 * 
	 * @return ArrayList<Type> accountList object
	 */
	public ArrayList<Account> getAccountList() {
		ArrayList<Account> tmpList = new ArrayList<Account>(accountList);
		return tmpList;
	}

	/**
	 * Adds a new month to the ledger (an error is thrown if the month already exists in the ledger)
	 * 
	 * @param month - month to add to ledger
	 * @param year - year of month to add to ledger
	 */
	public void addBudget(int month, int year) {
		try {
			JDateTime monthYearId = new JDateTime(year, month, 1);
			Budget newBudget = defaultBudget;
			monthlyBudgetList.put(monthYearId, newBudget);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
	}

	//	/**
	//	 * This returns true if a ledger for this month and year has been created, else returns false
	//	 * 
	//	 * @param month - month we are searching for in ledger
	//	 * @param year - year we are searching for in ledger
	//	 * @return - true if a ledger for this month and year has been created, else returns false
	//	 */
	//	public boolean isMonthInLedger(int month, int year) {
	//		return ledgerData.isMonthInLedger(month, year);
	//	}

	/**
	 * This adds a Single Entry to a month
	 * 
	 * @param inputDate - date for single entry
	 * @param inputDesc - description of single entry
	 * @param inputAcct - account number for single entry
	 * @param inputAmt - amount for single entry
	 */
	public void addSingleEntry(JDateTime inputDate, String inputDesc, Account inputAcct, double inputAmt) {
		SingleEntry inputEntry = new SingleEntry(inputDate, inputAcct, inputDesc, inputAmt);
		try {
			entryData.addSingleEntry(inputEntry);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * This deletes a single entry from a month
	 * 
	 * @param targetDate - the date of the entry to delete
	 * @param targetIndex - the index of the entry to delete on that date
	 * @exception - if there is an error from the deleteSingleEntry method
	 */
	public void deleteSingleEntry(JDateTime targetDate, int targetIndex) {
		try {
			entryData.deleteSingleEntry(targetDate, targetIndex);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * This updates a single entry in a month
	 * 
	 * @param targetDate - the date of the entry to update
	 * @param targetIndex - the index of the entry to update on that date
	 * @param inputDate - the new date
	 * @param inputDesc - the new description
	 * @param inputAcct - the new type
	 * @param inputAmt - the new amount
	 * @exception - if there is an error from the updateEntry method
	 */
	public void updateSingleEntry(JDateTime targetDate, int targetIndex, JDateTime inputDate, String inputDesc, Account inputAcct, double inputAmt) {
		try {
			SingleEntry inputEntry = new SingleEntry(inputDate, inputAcct, inputDesc, inputAmt);
			entryData.updateSingleEntry(targetDate, targetIndex, inputEntry);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * This adds a new repeating entry
	 * 
	 * @param startDate - month to start repeating entry on
	 * @param endDate - month to end repeating entry on
	 * @param inputDesc - description of repeating entry
	 * @param inputAcct - account number for repeating entry
	 * @param inputAmt - amount for repeating entry
	 */
	public void addRepeatingEntry(JDateTime startDate, JDateTime endDate, String inputDesc, Account inputAcct, double inputAmt) {
		RepeatingEntry inputEntry = new RepeatingEntry(startDate, endDate, inputAcct, inputDesc, inputAmt);
		try {
			entryData.addRepeatingEntry(inputEntry);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * This deletes a repeating entry from a month
	 * 
	 * @param targetDate - the date of the entry to delete
	 * @param targetIndex - the index of the entry to delete on that date
	 * @exception - if there is an error from the deleteSingleEntry method
	 */
	public void deleteRepeatingEntry(JDateTime targetDate, int targetIndex) {
		try {
			entryData.deleteRepeatingEntry(targetDate, targetIndex);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * This updates a single entry in a month
	 * 
	 * @param targetDesc - the desc of the repeating entry to update
	 * @param inputStartDate - the new month start date
	 * @param inputEndDate - the new month end date
	 * @param inputDesc - the new description
	 * @param inputAcct - the new type
	 * @param inputAmt - the new amount
	 * 
	 * @exception - if the targetDate isn't at the beginning of the month
	 * @exception - if the inputStartDate isn't at the beginning of the month
	 * @exception - if the inputEndDate isn't at the beginning of the month
	 * @exception - if there is an error from the updateEntry method
	 */
	public void updateRepeatingEntry(String targetDesc, JDateTime inputStartDate, JDateTime inputEndDate, String inputDesc, Account inputAcct, double inputAmt) {

		if (inputStartDate.getDay() != 1) {
			throw new IllegalArgumentException("Start Date must be at beginning of the month.");
		}
		if (inputStartDate.getDay() != 1) {
			throw new IllegalArgumentException("End Date must be at beginning of the month.");
		}

		try {
			RepeatingEntry inputEntry = new RepeatingEntry(inputStartDate, inputEndDate, inputAcct, inputDesc, inputAmt);
			entryData.updateRepeatingEntry(targetDesc, inputEntry);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Get a list of dates for all the single entries in this ledger
	 * 
	 * @return - an ArrayList object of all the dates that have single entries in this ledger
	 */
	public ArrayList<JDateTime> getSingleEntryDates() {
		ArrayList<JDateTime> dateList = new ArrayList<JDateTime>(entryData.getSingleEntryDateList());
		return dateList;
	}

	/**
	 * Get an ArrayList object of Single Entries for target date
	 * 
	 * @param targetDate - the date of the entry to search for
	 * @return - an ArrayList object of Single Entries
	 */
	public ArrayList<SingleEntry> getSingleEntryListForDate(JDateTime targetDate) {
		return entryData.getSingleEntryList(targetDate);
	}

	/**
	 * Get an ArrayList object of all the Repeating Entries for the target month
	 * 
	 * @param targetMonth - the month that we want to display all the repeating entries for (if null then display all repeating entries)
	 * @return - an ArrayList object of all the Repeating Entries found for this month
	 */
	public ArrayList<RepeatingEntry> getRepeatingEntryListForMonth(JDateTime targetMonth) {
		return entryData.getRepeatingEntryList(targetMonth);
	}

	/**
	 * Get a list of default budget accounts
	 * 
	 * @return - an ArrayList object of all the accounts in the default budget
	 */
	public ArrayList<Account> getDefaultBudgetAccounts() {
		ArrayList<Account> accountList = new ArrayList<Account>(defaultBudget.getAccountList());
		return accountList;
	}

	/**
	 * Get an amount from default budget for an account
	 * 
	 * @param searchAcct - the account you want to get the budgeted amount for
	 * 
	 * @return - the amount budgeted for this account in the default budget
	 */
	public double getDefaultBudgetAmount(Account searchAcct) {
		try {
			return defaultBudget.getBudgetAmount(searchAcct);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Get a list of monthly budget accounts
	 * 
	 * @param month - month to read list of budget accounts from
	 * @param year - year of month to read list of budget accounts from
	 * 
	 * @return - a ArrayList object of all the accounts in the monthly budget
	 */
	public ArrayList<Account> getMonthlyBudgetAccounts(int month, int year) {
		JDateTime monthYearId = new JDateTime(year, month, 1);
		Budget monthlyBudget = monthlyBudgetList.get(monthYearId);
		if (monthlyBudgetList.containsKey(monthYearId)) {
			ArrayList<Account> accountList = new ArrayList<Account>(monthlyBudget.getAccountList());
			return accountList;
		} else {
			return null;
		}
	}

	/**
	 * Get an amount from a monthly budget for an account
	 * 
	 * @param month - month of budget to read amount from
	 * @param year - year of month of budget to read amount from
	 * @param searchAcct - the account you want to get the budgeted amount for
	 * 
	 * @return - the amount budgeted for this account in a monthly budget
	 */
	public double getMonthlyBudgetAmount(int month, int year, Account searchAcct) {
		JDateTime monthYearId = new JDateTime(year, month, 1);
		if (monthlyBudgetList.containsKey(monthYearId)) {
			try {
				return monthlyBudgetList.get(monthYearId).getBudgetAmount(searchAcct);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(e);
			}
		} else {
			throw new IllegalArgumentException("There is no budget for " + month + "/" + year + ".");
		}
	}

	/**
	 * Add an account (and an associated amount) to the default budget
	 * 
	 * @param newAccount - account to add to the default budget
	 * @param newBudgetAmount - amount to budget for that account
	 */
	public void addDefaultBudgetAccount(Account newAccount, double newBudgetAmount){
		try {
			defaultBudget.addAccount(newAccount, newBudgetAmount);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/** 
	 * Remove an account from the default budget
	 * 
	 * @param deleteAccount - the account to remove
	 */
	public void removeDefaultBudgetAccount(Account deleteAccount){
		try {
			defaultBudget.deleteAccount(deleteAccount);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Update an amount for an existing account in the default budget
	 * 
	 * @param updateAccount - account in the default budget to update
	 * @param newAmount - new amount to budget for that account
	 * 
	 * @exception IllegalArgumentException if the account updateAccount is not in the default budget
	 */
	public void updateDefaultBudgetAmount(Account updateAccount, double newAmount) {
		if (defaultBudget.isAccountInList(updateAccount) == true) {
			try {
				defaultBudget.updateBudgetAmount(updateAccount, newAmount);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(e);
			}
		} else {
			throw new IllegalArgumentException("Account " + updateAccount + " isn't in default budget.");
		}
	}

	/**
	 * Add an account (and an associated amount) to the list of monthly budgets
	 * 
	 * @param month - month of budget to update
	 * @param year - year of budget to update
	 * @param newAccount - account to add to the default budget
	 * @param newBudgetAmount - amount to budget for that account
	 */
	public void addMonthlyBudgetAccount(int month, int year, Account newAccount, double newBudgetAmount) {
		JDateTime monthYearId = new JDateTime(year, month, 1);
		Budget tmpBudget = new Budget();
		if (monthlyBudgetList.containsKey(monthYearId)) {
			tmpBudget = monthlyBudgetList.get(monthYearId);
		} 
		tmpBudget.addAccount(newAccount, newBudgetAmount);
		monthlyBudgetList.put(monthYearId, tmpBudget);
	}

	/**
	 * Remove an account for the list of monthly budgets
	 * 
	 * @param month - month - month of budget to update
	 * @param year - year of budget to update
	 * @param deleteAccount - the account to remove
	 */
	public void removeMonthlyBudgetAccount(int month, int year, Account deleteAccount) {
		JDateTime monthYearId = new JDateTime(year, month, 1);
		Budget tmpList = monthlyBudgetList.get(monthYearId);
		if (tmpList.isAccountInList(deleteAccount)) {
			tmpList.deleteAccount(deleteAccount);
		} else {
			throw new IllegalArgumentException("The list of entries for this month doesn't have the account you are trying to delete");
		}
		monthlyBudgetList.put(monthYearId,tmpList);
	}

	/**
	 * Update an amount for an existing account in a monthly budget
	 * 
	 * @param month - month - month of budget to update
	 * @param year - year of budget to update
	 * @param updateAccount - account in the default budget to update
	 * @param newAmount - new amount to budget for that account
	 * 
	 * @exception IllegalArgumentException if the account updateAccount is not in the default budget
	 */
	public void updateMonthlyBudgetAmount(int month, int year, Account updateAccount, double newAmount) {
		JDateTime monthYearId = new JDateTime(year, month, 1);
		Budget tmpList = monthlyBudgetList.get(monthYearId);
		if (tmpList.isAccountInList(updateAccount) == true) {
			tmpList.updateBudgetAmount(updateAccount, newAmount);
		} else {
			throw new IllegalArgumentException("Account " + updateAccount + " isn't in this month's budget.");
		}
	}

	/**
	 * 	Get an account object from the list of accounts by searching using the account name
	 * @param accountName - name of account to search for
	 * 
	 * @return account object or null if not found
	 */
	private Account getAccount(String accountName) {
		for (int i = 0; i < accountList.size(); i++) {
			Account tempAccount = accountList.get(i);
			//System.out.println(tempAccount.getAccountName());
			if (tempAccount.getAccountName().equals(accountName)) {
				return tempAccount;	
			}
		}
		return null;
	}

	// TODO: finish writing getTotalAmountDonatedToCharity method
	//public double getTotalAmountDonatedToCharity(JDateTime startDate, JDateTime endDate) {
	//	...
	//}

	/**
	 * This class handles the connection with the SQL database (creating SQL tables, loading data, saving data)
	 * 
	 * @author Asa Swain
	 *
	 */
	private class SQLDatabaseConnection {
		// SQL database connection object 
		private Connection connection;

		// variable to keep track of it SQL Tables exist
		private boolean SQLTablesExist[];

		final int NBR_SQL_DATABASES = 5;

		/**
		 * This is a blank constructor
		 */
		public SQLDatabaseConnection() {
			// initialize connection with SQL database
			initSQLConnection();
			//TODO check for failed connection and throw error if unable to make connection
			//if (connection == null) return;

			SQLTablesExist = new boolean[NBR_SQL_DATABASES];
			for (int i = 0; i < NBR_SQL_DATABASES; i++) {
				SQLTablesExist[i] = false;
			}
		}

		/**
		 * This verifies that the SQL table exists and creates an SQL table from scratch if none exists before
		 */
		public void createSQLtable() {
			executeSQLCommand("VerifySQLTables");
			for (int i = 0; i < NBR_SQL_DATABASES; i++) {
				if (SQLTablesExist[i] == false) {
					executeSQLCommand("CreateSQLTable" + i);
					SQLTablesExist[i] = true;
				}
			}
		}

		/**
		 * This loads a list of accounts from the SQL database into the accountList object
		 * This loads single entries from the SQL database into the monthlyData object
		 * This loads the monthly budget from the SQL database into the monthlyData object
		 * This loads the default budget from the SQL database into the defaultBudget object
		 */
		public void loadDatabasesFromSQL() {
			// be sure to load accounts before loading monthly ledger
			executeSQLCommand("LoadAccountList"); 
			executeSQLCommand("LoadSingleEntry");
			executeSQLCommand("LoadRepeatEntry");
			executeSQLCommand("LoadMonthlyBudgets");
			executeSQLCommand("LoadDefaultBudget");
		}

		/**
		 * This saves a list of accounts from the accountList object into the SQL database,
		 * saves single entries from the monthlyData object into the SQL database,
		 * saves the monthly budget from the monthlyData object into the SQL database,
		 * and saves the default budget from the defaultBudget object into the SQL database
		 */
		public void saveDatabasesToSQL() {
			mySQLDatabase.executeSQLCommand("SaveAccountList");
			mySQLDatabase.executeSQLCommand("SaveSingleEntries");
			mySQLDatabase.executeSQLCommand("SaveRepeatEntries");
			mySQLDatabase.executeSQLCommand("SaveMonthlyBudgets");
			mySQLDatabase.executeSQLCommand("SaveDefaultBudget");
		}

		/**
		 * This erases the contents of all the SQL databases
		 */
		public void eraseSQLDatabases() {
			executeSQLCommand("EraseDatabase");
		}

		/**
		 * Send commands to SQL database to load data, save date, or create tables 
		 * 
		 * @param commandName - SQL command to execute
		 * 
		 * @exception SQLException if an error occurs when interacting with the SQL database
		 */
		private void executeSQLCommand(String commandName) {
			// get list of SQL statements based on commandName
			ArrayList<String> statementList = new ArrayList<String>();
			// build list of SQL statements for this command
			statementList = getSQLStatementList(commandName);
			int statementCnt = statementList.size();
			String commandType = getSQLCommandType(commandName);

			if (commandType.equals("ReadTables")) {
				if (commandName.length() > 14 && commandName.substring(0,14).equals("VerifySQLTable")){
					try {
						// verify that each of the SQL tables exist
						DatabaseMetaData md = connection.getMetaData();
						ResultSet rs = md.getTables(null, null, "%", null);

						while (rs.next()) {
							System.out.println(rs.getString(3));
							if (rs.getString(3).equals("account_List"))   { SQLTablesExist[0] = true; }
							if (rs.getString(3).equals("general_ledger")) { SQLTablesExist[1] = true; }
							if (rs.getString(3).equals("monthly_budget")) { SQLTablesExist[2] = true; }
							if (rs.getString(3).equals("default_budget")) { SQLTablesExist[3] = true; }
							if (rs.getString(3).equals("repeat_entry"))   { SQLTablesExist[4] = true; }
						}	
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				for (int i = 0; i < statementCnt; i++) {
					Statement statement = null;
					try {

						statement = connection.createStatement();

						// get SQL command to execute
						String command = statementList.get(i);

						if (command != "") {
							//System.out.println("SQL command = " + command);
							if (commandType.equals("ExecuteUpdate")) {
								statement.executeUpdate(command);
							} else { 
								ResultSet resultSet = statement.executeQuery(command);

								while(resultSet.next()){	
									// parse contents of resultSet to handle data returned from SQL database
									parseSQLResults(commandName, resultSet);
								}
							}
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		/**
		 * This initializes the connection with the SQL database
		 * 
		 * @exception SQLException if an error occurs when interacting with the SQL database
		 */
		private void initSQLConnection(){
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// We can set a custom username and password in phpMyadmin (in localhost for XAMPP) 
			// but for now just use default host login with no password

			connection = null;

			String user = "root";
			String password = "";
			try {
				connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javabudget", user, password);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/**
		 * This generates a list of SQL commands to pass to the SQL database based on the commandName
		 * 
		 * @parameter commandName - the type of command we are trying to execute
		 */
		private ArrayList<String> getSQLStatementList(String commandName) {
			ArrayList<String> statementList = new ArrayList<String>();

			if (commandName.equals("LoadAccountList")) {
				statementList.add("SELECT * FROM `account_list`");
			}
			if (commandName.equals("LoadSingleEntry")) {
				statementList.add("SELECT * FROM `general_ledger`");
			}
			if (commandName.equals("LoadRepeatEntry")) {
				statementList.add("SELECT * FROM `repeat_entry`");
			}

			if (commandName.equals("LoadMonthlyBudgets")) {
				statementList.add("SELECT * FROM `monthly_budget`");
			}
			if (commandName.equals("LoadDefaultBudget")) {
				statementList.add("SELECT * FROM `default_budget`");
			}

			if (commandName.equals("EraseDatabase")) {
				statementList.add("TRUNCATE `account_list`");
				statementList.add("TRUNCATE `general_ledger`");
				statementList.add("TRUNCATE `repeat_entry`");
				statementList.add("TRUNCATE `monthly_budget`");
				statementList.add("TRUNCATE `default_budget`");
			}

			if (commandName.equals("SaveAccountList")) {
				// number of statements = number of accounts
				for (Account tmpAccount : accountList){
					// extract data from accountList object		
					String tmpName = tmpAccount.getAccountName();
					tmpName = tmpName.replace("'", "''"); // replace single quotes with '' to make it compatible with SQL database
					String tmpDesc = tmpAccount.getAccountDesc();
					tmpDesc = tmpDesc.replace("'", "''"); // replace single quotes with '' to make it compatible with SQL database
					char tmpIsAnExpense;
					if (tmpAccount.getIsAnExpense() == true) {
						tmpIsAnExpense = 'Y';
					} else {
						tmpIsAnExpense = 'N';
					}
					char tmpIsInBudget;
					if (tmpAccount.getIsIncludedInBudget() == true) {
						tmpIsInBudget = 'Y';
					} else {
						tmpIsInBudget = 'N';
					}
					String tmpStatement = "INSERT INTO account_list (NAME, DESCRIPTION, IS_AN_EXPENSE, IS_IN_BUDGET) ";
					tmpStatement += "VALUES ('"+tmpName+"', '"+tmpDesc+"', '"+tmpIsAnExpense+"', '"+tmpIsInBudget+"')";
					statementList.add(tmpStatement);
				}
			}

			// no list required, just need a dummy list so we can count the number of iterations
			if (commandName.equals("SaveSingleEntries")) {
				// number of statements = number of single entries in ledger so make a giant list of all the SingleEntry data from all months in the general ledger
				ArrayList<SingleEntry> rawEntryList = entryData.getSingleEntryList();
				for (SingleEntry tmpEntry : rawEntryList){
					// extract data from SingleEntry object
					JDateTime entryDate = tmpEntry.getDate();
					int day = entryDate.getDay();
					int month = entryDate.getMonth();
					int year = entryDate.getYear();
					String tmpDate = (year  + "-" + month + "-" + day);
					String tmpDesc = tmpEntry.getDesc();
					tmpDesc = tmpDesc.replace("'", "''"); // replace single quotes with '' to make it compatible with SQL database
					String tmpAccount = tmpEntry.getAccount().getAccountName();
					tmpAccount = tmpAccount.replace("'", "''"); // replace single quotes with '' to make it compatible with SQL database
					Double tmpAmount = tmpEntry.getAmount();
					// intentionally ignore AUTONUM column which auto-generates an ID for this row
					String tmpStatement = "INSERT INTO general_ledger (DATE, DESCRIPTION, ACCOUNT, AMOUNT) ";
					tmpStatement += "VALUES ('"+tmpDate+"', '"+tmpDesc+"', '"+tmpAccount+"', '"+tmpAmount+"')";
					statementList.add(tmpStatement);
				}
			}

			// no list required, just need a dummy list so we can count the number of iterations
			if (commandName.equals("SaveRepeatEntries")) {
				// number of statements = number of single entries in ledger so make a giant list of all the SingleEntry data from all months in the general ledger
				ArrayList<RepeatingEntry> rawEntryList = entryData.getRepeatingEntryList();
				for (RepeatingEntry tmpEntry : rawEntryList){
					// extract data from RepeatingEntry object
					JDateTime startDate = tmpEntry.getStartDate();
					int day = startDate.getDay();
					int month = startDate.getMonth();
					int year = startDate.getYear();
					String tmpStartDate = (year  + "-" + month + "-" + day);
					JDateTime endDate = tmpEntry.getEndDate();
					day = endDate.getDay();
					month = endDate.getMonth();
					year = endDate.getYear();
					String tmpEndDate = (year  + "-" + month + "-" + day);
					String tmpDesc = tmpEntry.getDesc();
					tmpDesc = tmpDesc.replace("'", "''"); // replace single quotes with '' to make it compatible with SQL database
					String tmpAccount = tmpEntry.getAccount().getAccountName();
					tmpAccount = tmpAccount.replace("'", "''"); // replace single quotes with '' to make it compatible with SQL database
					Double tmpAmount = tmpEntry.getAmount();
					// intentionally ignore AUTONUM column which auto-generates an ID for this row
					String tmpStatement = "INSERT INTO repeat_entry (STARTDATE, ENDDATE, DESCRIPTION, ACCOUNT, AMOUNT) ";
					tmpStatement += "VALUES ('"+tmpStartDate+"', '"+tmpEndDate+"', '"+tmpDesc+"', '"+tmpAccount+"', '"+tmpAmount+"')";
					statementList.add(tmpStatement);
				}
			}

			if (commandName.equals("SaveDefaultBudget")) {
				// extract account list from the DefaultBudget object - keySet returns a set of all the keys in the HashMap
				for(Account setAccount : defaultBudget.getAccountList()){
					String tmpAccount = setAccount.getAccountName();
					tmpAccount = tmpAccount.replace("'", "''"); // replace single quotes with '' to make it compatible with SQL database
					double tmpAmount = defaultBudget.getBudgetAmount(setAccount);

					String tmpStatement = "INSERT INTO default_budget (ACCOUNT, AMOUNT) ";
					tmpStatement += "VALUES ('"+tmpAccount+"', '"+tmpAmount+"')";
					statementList.add(tmpStatement);
				}
			}

			if (commandName.equals("SaveMonthlyBudgets")){
				// extract monthly budget dates from the monthlyData object - keySet returns a set of all the keys in the HashMap
				for(JDateTime tmpDate : monthlyBudgetList.keySet()){
					int tmpMonth = tmpDate.getMonth();
					int tmpYear = tmpDate.getYear();
					Budget tmpBudget = monthlyBudgetList.get(tmpDate);
					// extract account list from the tmpBudget object - keySet returns a set of all the keys in the HashMap
					for (Account setAccount : tmpBudget.getAccountList()){
						String tmpAccount = setAccount.getAccountName();
						tmpAccount = tmpAccount.replace("'", "''"); // replace single quotes with '' to make it compatible with SQL database
						double tmpAmount = tmpBudget.getBudgetAmount(setAccount);
						// intentionally ignore AUTONUM column which auto-generates an ID for this row
						String tmpStatement = "INSERT INTO monthly_budget (MONTH, YEAR, ACCOUNT, AMOUNT) ";
						tmpStatement += "VALUES ('"+tmpMonth+"','"+tmpYear+"','"+tmpAccount+"', '"+tmpAmount+"')";
						statementList.add(tmpStatement);
					}
				}
			}

			if(commandName.length() > 14 && commandName.substring(0,14).equals("CreateSQLTable")){
				String tableNbr = commandName.substring(14, 15);

				String tmpStatement = "";
				if (tableNbr.equals("0")) {
					tmpStatement = "CREATE TABLE account_list (";
					tmpStatement += " name VARCHAR(25) DEFAULT NULL, ";
					tmpStatement += " description VARCHAR(100) DEFAULT NULL,";
					tmpStatement += " is_an_expense VARCHAR(1) DEFAULT NULL,";
					tmpStatement += " is_in_budget VARCHAR(1) DEFAULT NULL,";
					tmpStatement += " PRIMARY KEY (name)";
					tmpStatement += ");";
				}

				if (tableNbr.equals("1")) {
					tmpStatement = "CREATE TABLE general_ledger (";
					tmpStatement += " autonum INT(100) NOT NULL AUTO_INCREMENT,";
					tmpStatement += " date date DEFAULT NULL, ";
					tmpStatement += " description VARCHAR(100) DEFAULT NULL,";
					tmpStatement += " account VARCHAR(25) DEFAULT NULL,";
					tmpStatement += " amount float DEFAULT NULL,";
					tmpStatement += " PRIMARY KEY (autonum)";
					tmpStatement += ");";
				}

				if (tableNbr.equals("2")) {
					tmpStatement = "CREATE TABLE monthly_budget (";
					tmpStatement += " autonum INT(100) NOT NULL AUTO_INCREMENT, ";
					tmpStatement += " month INT(10) NOT NULL,";
					tmpStatement += " year INT(10) NOT NULL,";
					tmpStatement += " account VARCHAR(25) DEFAULT NULL, ";
					tmpStatement += " amount float DEFAULT NULL,";
					tmpStatement += " PRIMARY KEY (autonum)";
					tmpStatement += ");";
				}

				if (tableNbr.equals("3")) {
					tmpStatement = "CREATE TABLE default_budget (";
					tmpStatement += " account VARCHAR(25) DEFAULT NULL, ";
					tmpStatement += " amount float DEFAULT NULL,";
					tmpStatement += " PRIMARY KEY (account)";
					tmpStatement += ");";
				}	

				if (tableNbr.equals("4")) {
					tmpStatement = "CREATE TABLE repeat_entry (";
					tmpStatement += " autonum INT(100) NOT NULL AUTO_INCREMENT, ";
					tmpStatement += " description VARCHAR(100) DEFAULT NULL,";
					tmpStatement += " startdate date DEFAULT NULL, ";
					tmpStatement += " enddate date DEFAULT NULL, ";
					tmpStatement += " account VARCHAR(25) DEFAULT NULL,";
					tmpStatement += " amount float DEFAULT NULL,";
					tmpStatement += " PRIMARY KEY (autonum)";
					tmpStatement += ");";
				}

				if (!tmpStatement.equals("")) {
					statementList.add(tmpStatement);	
				}
			}

			return statementList;
		}

		/**
		 * This gets the command type based on the type of the command we are sending to the SQL Database
		 * 
		 * @param commandName - the type of command we are executing
		 * @return a string containing the type of command (ReadTables/ExecuteUpdate/ExecuteQuery)
		 */
		private String getSQLCommandType(String commandName) {
			String commandType = "";
			if ((commandName.equals("VerifySQLTables"))) {
				commandType = "ReadTables";
			} else {
				if ((commandName.equals("EraseDatabase")) || (commandName.equals("SaveAccountList") )
						|| (commandName.equals("SaveDefaultBudget")) || (commandName.equals("SaveSingleEntries")) || (commandName.equals("SaveRepeatEntries")) 
						|| (commandName.equals("SaveMonthlyBudgets"))  || (commandName.length() > 14 && commandName.substring(0,14).equals("CreateSQLTable"))) {
					commandType = "ExecuteUpdate";
				} else {
					// commandName == "LoadAccountList" or "LoadRepeatEntry" or "LoadMonthlyBudgets" or "LoadDefaultBudget" {
					commandType = "ExecuteQuery";	
				}
			}
			return commandType;
		}

		/**
		 * Parse the results of the command we sent to the SQL Database
		 * 
		 * @param commandName - the type of command we are executing
		 * @param resultSet - the results from the SQL database 
		 * 
		 * @exception SQLException if an error occurs when interacting with the SQL database
		 */
		private void parseSQLResults(String commandName, ResultSet resultSet) {
			try {
				if (commandName.equals("LoadAccountList")) {
					String accountName = resultSet.getString("NAME");		
					accountName = accountName.replace("''", "'"); // replace single quotes with '' to make it compatible with SQL database
					String accountDesc = resultSet.getString("DESCRIPTION");	
					accountDesc = accountDesc.replace("''", "'"); // replace single quotes with '' to make it compatible with SQL database
					String tmpIsAnExpense = resultSet.getString("IS_AN_EXPENSE");	
					boolean isAnExpense = (tmpIsAnExpense.equals("Y"));
					String tmpIsInBudget  = resultSet.getString("IS_IN_BUDGET");	
					boolean isInBudget = (tmpIsInBudget.equals("Y"));
					addAccount(accountName, accountDesc, isAnExpense, isInBudget, false, 0);
				}
				if (commandName.equals("LoadMonthlyBudgets")) {
					// ignore AUTONUM column at beginning of MONTHLY_BUDGET table
					String stringMonth = resultSet.getString("MONTH");
					int budgetMonth = Integer.parseInt(stringMonth);
					String stringYear = resultSet.getString("YEAR");
					int budgetYear = Integer.parseInt(stringYear);
					String stringAccount = resultSet.getString("ACCOUNT");
					stringAccount = stringAccount.replace("''", "'"); // replace single quotes with '' to make it compatible with SQL database
					// get the Type object for this account name
					Account budgetAccount = getAccount(stringAccount);
					String stringAmount = resultSet.getString("AMOUNT");
					double budgetAmount = Double.parseDouble(stringAmount);
					addMonthlyBudgetAccount(budgetMonth, budgetYear, budgetAccount, budgetAmount);
				}
				if (commandName.equals("LoadDefaultBudget")) {
					String stringAccount = resultSet.getString("ACCOUNT");
					stringAccount = stringAccount.replace("''", "'"); // replace single quotes with '' to make it compatible with SQL database
					// get the Type object for this account name
					Account budgetAccount = getAccount(stringAccount);
					String stringAmount = resultSet.getString("AMOUNT");
					double budgetAmount = Double.parseDouble(stringAmount);
					addDefaultBudgetAccount(budgetAccount, budgetAmount);
				}
				if (commandName.equals("LoadSingleEntry")) {
					// ignore AUTONUM column at beginning of GENERAL_LEDGER table
					String stringDate = resultSet.getString("DATE");
					// convert date from YYYY-MM-DD string into a JDateTime object
					String parts[] = stringDate.split("-");
					int year = Integer.parseInt(parts[0]);
					int month = Integer.parseInt(parts[1]);
					int day = Integer.parseInt(parts[2]);
					JDateTime entryDate = new JDateTime(year, month, day);
					String entryDesc = resultSet.getString("DESCRIPTION");	
					entryDesc = entryDesc.replace("''", "'"); // replace single quotes with '' to make it compatible with SQL database
					String stringAccount = resultSet.getString("ACCOUNT");	
					stringAccount = stringAccount.replace("''", "'"); // replace single quotes with '' to make it compatible with SQL database
					Account entryAccount = getAccount(stringAccount);
					String stringAmount  = resultSet.getString("AMOUNT");
					double entryAmount = Double.parseDouble(stringAmount);
					// if this is an expense, multiple amount by -1 before entering into database
					if (entryAccount.getIsAnExpense()) entryAmount = entryAmount * -1;
					addSingleEntry(entryDate, entryDesc, entryAccount, entryAmount);
				}
				if (commandName.equals("LoadRepeatEntry")) {
					// ignore AUTONUM column at beginning of REPEAT_ENTRY table
					String stringDate = resultSet.getString("STARTDATE");
					// convert date from YYYY-MM-DD string into a JDateTime object
					String parts[] = stringDate.split("-");
					int year = Integer.parseInt(parts[0]);
					int month = Integer.parseInt(parts[1]);
					int day = Integer.parseInt(parts[2]);
					JDateTime startDate = new JDateTime(year, month, day);
					stringDate = resultSet.getString("ENDDATE");
					// convert date from YYYY-MM-DD string into a JDateTime object
					parts = stringDate.split("-");
					year = Integer.parseInt(parts[0]);
					month = Integer.parseInt(parts[1]);
					day = Integer.parseInt(parts[2]);
					JDateTime endDate = new JDateTime(year, month, day);
					String entryDesc = resultSet.getString("DESCRIPTION");	
					entryDesc = entryDesc.replace("''", "'"); // replace single quotes with '' to make it compatible with SQL database
					String stringAccount = resultSet.getString("ACCOUNT");	
					stringAccount = stringAccount.replace("''", "'"); // replace single quotes with '' to make it compatible with SQL database
					Account entryAccount = getAccount(stringAccount);
					String stringAmount  = resultSet.getString("AMOUNT");
					double entryAmount = Double.parseDouble(stringAmount);
					// if this is an expense, multiple amount by -1 before entering into database
					if (entryAccount.getIsAnExpense()) entryAmount = entryAmount * -1;
					addRepeatingEntry(startDate, endDate, entryDesc, entryAccount, entryAmount);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
