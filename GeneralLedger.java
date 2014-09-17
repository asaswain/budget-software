package budget_program;

import java.util.*;
import java.sql.*;

import jodd.datetime.*;

/**
 * This class is the master General Ledger class, which contains the list of accounts 
 * the income/expense entries for each month, the budget for each month and the name of the ledger
 * @author Asa Swain
 */

// 09/16/2014 MASTER TO DO LIST:
//
//   1. finish working on deleteSingleEntry Method
//   2. write a Update Single Entry Method
//   3. write a Add Repeating Entry Method
//   add a "getListOfMonths" method
//   finish writing getTotalAmountDonatedToCharity method
//   In SQLDatabaseConnection check for failed connection and throw error if unable to make connection
//   

//05/09/2014 TODO: add a "getListOfMonths" method?

public class GeneralLedger {
	// list of data for each month
	private MonthlyLedgerList monthlyData;
	// list of all general ledger accounts
	private ArrayList<Type> accountList;
	// HashMap of default account list and amounts for each monthly budget
	private BudgetAmtList defaultBudget;
	// name of general ledger object
	private String ledgerName;

	private SQLDatabaseConnection mySQLDatabase;

	/**
	 * This is a blank constructor
	 */
	public GeneralLedger() {
		monthlyData = new MonthlyLedgerList();
		accountList = new ArrayList<Type>();
		defaultBudget = new BudgetAmtList();
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
		Type inputAccount = new Type(inputName, inputDesc, isAnExpense, isIncludedInBudget);
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
	public void removeAccount(Type deleteAccount) {
		accountList.remove(deleteAccount);
		if (defaultBudget.isAccountInList(deleteAccount) == true) {
			defaultBudget.deleteAccount(deleteAccount);
		}
	}

	/**
	 * // Adds a new month to the ledger (an error is thrown if the month already exists in the ledger)
	 * 
	 * @param month - month to add to ledger
	 * @param year - year of month to add to ledger
	 */
	public void addMonth(int month, int year) {
		try {
			monthlyData.addMonthToLedger(month,year,defaultBudget);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * This adds a Single Entry to a month
	 * 
	 * @param inputDate - date for single entry
	 * @param inputDesc - description of single entry
	 * @param inputType - account number for single entry
	 * @param inputAmt - amount for single entry
	 */
	public void addSingleEntry(JDateTime inputDate, String inputDesc, Type inputType, double inputAmt) {
		// create new SingleEntry
		SingleEntry inputEntry = new SingleEntry(inputDate, inputType, inputDesc, inputAmt);
		monthlyData.addSingleEntryToLedger(inputDate.getMonth(), inputDate.getYear(), inputEntry);
	}
	
	/**
	 * This deletes a single entry from a month
	 * 
	 * @param entryDate - the date of the entry to delete
	 * @param entryIndex - the index of the entry to delete on that date
	 * @exception - if there is an error from the deleteSingleEntryFromLedger method
	 */
	public void deleteSingleEntry(JDateTime targetDate, int targetIndex) {
		try {
			monthlyData.deleteSingleEntryFromLedger(targetDate, targetIndex);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/** 
	 * This returns a list of all the accounts in the general ledger
	 * 
	 * @return ArrayList<Type> accountList object
	 */
	public ArrayList<Type> getAccountList() {
		return accountList;
	}

	/**
	 * Print all entries for a given month
	 * 
	 * @param month - month to print entries for
	 * @param year - year to print entries for
	 */
	public void printMonth(int month, int year) {
		try {
			monthlyData.printMonthlyLedger(month, year);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		} 
	}

	/**
	 * Print all entries for all months in the ledger
	 */
	public void printAll() {
		try {
			monthlyData.printEntireLedger();
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		} 
	}

	/**
	 * Print default budget info
	 */
	public void printDefaultBudget() {
		try {
			System.out.println("Default Monthly Budget Info:");
			defaultBudget.printBudget();
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		} 
	}

	/**
	 * Add an account (and an associated amount) to the default budget
	 * 
	 * @param newAccount - account to add to the default budget
	 * @param newBudgetAmount - amount to budget for that account
	 */
	public void addDefaultBudgetAcct(Type newAccount, double newBudgetAmount){
		defaultBudget.addAccount(newAccount, newBudgetAmount);
	}

	/** 
	 * Remove an account from the default budget
	 * 
	 * @param deleteAccount - the account to remove
	 */
	public void removeDefaultBudgetAcct(Type deleteAccount){
		defaultBudget.deleteAccount(deleteAccount);
	}

	/**
	 * Update an amount for an existing account in the default budget
	 * 
	 * @param updateAccount - account in the default budget to update
	 * @param newAmount - new amount to budget for that account
	 * 
	 * @exception IllegalArgumentException if the account updateAccount is not in the default budget
	 */
	public void updateDefaultBudgetAmount(Type updateAccount, double newAmount) {
		if (defaultBudget.isAccountInList(updateAccount) == true) {
			defaultBudget.updateBudgetAmount(updateAccount, newAmount);
		} else {
			throw new IllegalArgumentException("Account " + updateAccount + " isn't in default budget.");
		}
	}

	/**
	 * Add an account (and an associated amount) to a monthly budget
	 * 
	 * @param month - month of budget to update
	 * @param year - year of budget to update
	 * @param newAccount - account to add to the default budget
	 * @param newBudgetAmount - amount to budget for that account
	 */
	public void addMonthlyBudgetAcct(int month, int year, Type newAccount, double newBudgetAmount){
		monthlyData.addMonthlyBudgetAcct(month, year, newAccount, newBudgetAmount);
	}

	/**
	 * Remove an account for a monthly budget
	 * 
	 * @param month - month - month of budget to update
	 * @param year - year of budget to update
	 * @param deleteAccount - the account to remove
	 */
	public void removeMonthlyBudgetAcct(int month, int year, Type deleteAccount){
		monthlyData.removeMonthlyBudgetAcct(month, year, deleteAccount);
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
	public void updateMonthlyBudgetAmount(int month, int year, Type updateAccount, double newAmount) {
		if (defaultBudget.isAccountInList(updateAccount) == true) {
			monthlyData.updateMonthlyBudgetAmount(month, year, updateAccount, newAmount);
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
	private Type getAccount(String accountName) {
		for (int i = 1; i < accountList.size(); i++) {
			Type tempAccount = accountList.get(i);
			if (tempAccount.getTypeName().equals(accountName)) {
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

		final int NBR_SQL_DATABASES = 4;
		
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
				SQLTablesExist[i] = true;
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
			executeSQLCommand("LoadMonthlyLedger");
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
			mySQLDatabase.executeSQLCommand("SaveMonthlyLedger");
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

			for (int i = 0; i < statementCnt; i++) {
				Statement statement = null;
				try {
					if (commandType == "ReadTables") {
						if (commandName.length() > 14 && commandName.substring(0, 14) == "verifySQLTables"){
														
							// verify that each of the SQL tables exist
							DatabaseMetaData md = connection.getMetaData();
							ResultSet rs = md.getTables(null, null, "%", null);
							
							while (rs.next()) {
								if (rs.getString(3) == "account_List")   { SQLTablesExist[0] = true; }
								if (rs.getString(3) == "general_ledger") { SQLTablesExist[1] = true; }
								if (rs.getString(3) == "monthly_budget") { SQLTablesExist[2] = true; }
								if (rs.getString(3) == "default_budget") { SQLTablesExist[3] = true; }
							}	
						}
					} else {
						statement = connection.createStatement();

						// get SQL command to execute
						String command = statementList.get(i);

						if (command != "") {
							//System.out.println("SQL command = " + command);
							if (commandType == "ExecuteUpdate" ) {
								statement.executeUpdate(command);
							} else { 
								ResultSet resultSet = statement.executeQuery(command);

								while(resultSet.next()){	
									// parse contents of resultSet to handle data returned from SQL database
									parseSQLResults(commandName, resultSet);
								}
							}
						}
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
				connection = DriverManager.getConnection("jdbc:mysql://localhost:3308/javabudget", user, password);
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

			if (commandName == "LoadAccountList") {
				statementList.add("SELECT * FROM `account_list`");
			}
			if (commandName == "LoadMonthlyLedger") {
				statementList.add("SELECT * FROM `general_ledger`");
			}
			if (commandName == "LoadMonthlyBudgets") {
				statementList.add("SELECT * FROM `monthly_budget`");
			}
			if (commandName == "LoadDefaultBudget") {
				statementList.add("SELECT * FROM `default_budget`");
			}

			if (commandName == "EraseDatabase") {
				statementList.add("TRUNCATE `account_list`");
				statementList.add("TRUNCATE `general_ledger`");
				statementList.add("TRUNCATE `monthly_budget`");
				statementList.add("TRUNCATE `default_budget`");
			}

			if (commandName == "SaveAccountList") {
				// number of statements = number of accounts
				for (Type tmpAccount : accountList){
					// extract data from accountList object		
					String tmpName = tmpAccount.getTypeName();
					tmpName = tmpName.replace("'", "''"); // replace single quotes with '' to make it compatible with SQL database
					String tmpDesc = tmpAccount.getTypeDesc();
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
			if (commandName == "SaveMonthlyLedger") {
				// number of statements = number of single entries in ledger so make a giant list of all the SingleEntry data from all months in the general ledger
				ArrayList<SingleEntry> rawEntryList = new ArrayList<SingleEntry>();
				rawEntryList = monthlyData.getRawSingleEntryList();
				for (SingleEntry tmpEntry : rawEntryList){
					// extract data from accountList object
					JDateTime entryDate = tmpEntry.getDate();
					int day = entryDate.getDay();
					int month = entryDate.getMonth();
					int year = entryDate.getYear();
					String tmpDate = (year  + "-" + month + "-" + day);
					String tmpDesc = tmpEntry.getDesc();
					tmpDesc = tmpDesc.replace("'", "''"); // replace single quotes with '' to make it compatible with SQL database
					String tmpAccount = tmpEntry.getType().getTypeName();
					tmpAccount = tmpAccount.replace("'", "''"); // replace single quotes with '' to make it compatible with SQL database
					Double tmpAmount = tmpEntry.getAmount();
					String tmpStatement = "INSERT INTO general_ledger (DATE, DESCRIPTION, ACCOUNT, AMOUNT) ";
					tmpStatement += "VALUES ('"+tmpDate+"', '"+tmpDesc+"', '"+tmpAccount+"', '"+tmpAmount+"')";
					statementList.add(tmpStatement);
				}
			}

			if (commandName == "SaveDefaultBudget") {
				// extract account list from the DefaultBudget object - keySet returns a set of all the keys in the HashMap
				for(Type setAccount : defaultBudget.getAccountList()){
					String tmpAccount = setAccount.getTypeName();
					tmpAccount = tmpAccount.replace("'", "''"); // replace single quotes with '' to make it compatible with SQL database
					double tmpAmount = defaultBudget.getBudgetAmount(setAccount);

					String tmpStatement = "INSERT INTO default_budget (ACCOUNT, AMOUNT) ";
					tmpStatement += "VALUES ('"+tmpAccount+"', '"+tmpAmount+"')";
					statementList.add(tmpStatement);
				}
			}

			if (commandName == "SaveMonthlyBudgets"){
				// extract monthly budget dates from the monthlyData object - keySet returns a set of all the keys in the HashMap
				for(JDateTime setDate : monthlyData.getMonthlyLedgerListDateList()){
					int tmpMonth = setDate.getMonth();
					int tmpYear = setDate.getYear();
					String tmpMonthYear = tmpMonth + "-" + tmpYear;
					BudgetAmtList tmpBudget = monthlyData.getBudgetInfo(setDate);
					// extract account list from the tmpBudget object - keySet returns a set of all the keys in the HashMap
					for (Type setAccount : tmpBudget.getAccountList()){
						String tmpAccount = setAccount.getTypeName();
						tmpAccount = tmpAccount.replace("'", "''"); // replace single quotes with '' to make it compatible with SQL database
						double tmpAmount = tmpBudget.getBudgetAmount(setAccount);

						String tmpStatement = "INSERT INTO monthly_budget (MONTHYEAR, MONTH, YEAR, ACCOUNT, AMOUNT) ";
						tmpStatement += "VALUES ('"+tmpMonthYear+"','"+tmpMonth+"','"+tmpYear+"','"+tmpAccount+"', '"+tmpAmount+"')";
						statementList.add(tmpStatement);
					}
				}
			}

			if(commandName.length() > 14 && commandName.substring(0,14) == "CreateSQLTable"){
				String tableNbr = commandName.substring(14, 15);
				
				String tmpStatement = "";
				if (tableNbr == "1") {
					tmpStatement = "CREATE TABLE account_list (";
					tmpStatement += " name VARCHAR(25) DEFAULT NULL, ";
					tmpStatement += " description VARCHAR(100) DEFAULT NULL,";
					tmpStatement += " is_an_expense VARCHAR(1) DEFAULT NULL,";
					tmpStatement += " is_in_budget VARCHAR(1) DEFAULT NULL";
					tmpStatement += " PRIMARY KEY (name)";
					tmpStatement += ");";
				}
				
				if (tableNbr == "2") {
					tmpStatement = "CREATE TABLE general_ledger (";
					tmpStatement += " date date DEFAULT NULL, ";
					tmpStatement += " description VARCHAR(100) DEFAULT NULL,";
					tmpStatement += " account VARCHAR(25) DEFAULT NULL,";
					tmpStatement += " amount float DEFAULT NULL,";
					tmpStatement += " PRIMARY KEY (date)";
					tmpStatement += ");";
				}
				
				if (tableNbr == "3") {
					tmpStatement = "CREATE TABLE monthly_budget (";
					tmpStatement += " monthyear VARCHAR(100) NOT NULL, ";
					tmpStatement += " month INT(10) NOT NULL,";
					tmpStatement += " year INT(10) NOT NULL,";
					tmpStatement += " account VARCHAR(25) DEFAULT NULL, ";
					tmpStatement += " amount float DEFAULT NULL,";
					tmpStatement += " PRIMARY KEY (monthyear)";
					tmpStatement += ");";
				}
				
				if (tableNbr == "4") {
					tmpStatement = "CREATE TABLE default_budget (";
					tmpStatement += " account VARCHAR(25) DEFAULT NULL, ";
					tmpStatement += " amount float DEFAULT NULL,";
					tmpStatement += ");";
				}	
				if (tmpStatement != "") {
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
			if ((commandName == "VerifySQLTables")) {
				commandType = "ReadTables";
			}

			if ((commandName == "EraseDatabase") || (commandName == "SaveAccountList") 
					|| (commandName == "SaveDefaultBudget") || (commandName == "SaveMonthlyLedger") 
					|| (commandName == "SaveMonthlyBudgets")  || (commandName.length() > 14 && commandName.substring(0,14) == "CreateSQLTable")) {
				commandType = "ExecuteUpdate";
			} else {
				// commandName == "LoadAccountList" or "LoadMonthlyLedger" or "LoadMonthlyBudgets" or "LoadDefaultBudget" {
				commandType = "ExecuteQuery";	
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
				if (commandName == "LoadAccountList") {
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
				if (commandName == "LoadMonthlyBudgets") {
					//String monthYear = resultSet.getString("MONTHYEAR");
					String stringMonth = resultSet.getString("MONTH");
					int budgetMonth = Integer.parseInt(stringMonth);
					String stringYear = resultSet.getString("YEAR");
					int budgetYear = Integer.parseInt(stringYear);
					String stringAccount = resultSet.getString("ACCOUNT");
					stringAccount = stringAccount.replace("''", "'"); // replace single quotes with '' to make it compatible with SQL database
					// get the Type object for this account name
					Type budgetAccount = getAccount(stringAccount);
					String stringAmount = resultSet.getString("AMOUNT");
					double budgetAmount = Double.parseDouble(stringAmount);
					addMonthlyBudgetAcct(budgetMonth, budgetYear, budgetAccount, budgetAmount);
				}
				if (commandName == "LoadDefaultBudget") {
					String stringAccount = resultSet.getString("ACCOUNT");
					stringAccount = stringAccount.replace("''", "'"); // replace single quotes with '' to make it compatible with SQL database
					// get the Type object for this account name
					Type budgetAccount = getAccount(stringAccount);
					String stringAmount = resultSet.getString("AMOUNT");
					double budgetAmount = Double.parseDouble(stringAmount);
					addDefaultBudgetAcct(budgetAccount, budgetAmount);
				}
				if (commandName == "LoadMonthlyLedger") {
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
					Type entryAccount = getAccount(stringAccount);
					String stringAmount  = resultSet.getString("AMOUNT");
					double entryAmount = Double.parseDouble(stringAmount);
					// if this is an expense, multiple amount by -1 before entering into database
					if (entryAccount.getIsAnExpense()) entryAmount = entryAmount * -1;
					addSingleEntry(entryDate, entryDesc, entryAccount, entryAmount);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
