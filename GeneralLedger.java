package budget_program;

import java.util.*;
import java.sql.*;
import jodd.datetime.*;

/**
 * This class is the master General Ledger class, which contains the list of accounts 
 * the income/expense entries for each month, the budget for each month and the name of the ledger
 * @author Asa Swain
 */

//05/09/2014 To do: add a "getListOfMonths" method?

public class GeneralLedger {
	// list of data for each month
	private MonthlyLedgerList monthlyData;
	// list of all general ledger accounts
	private ArrayList<Type> accountList;
	// HashMap of default account list and amounts for each monthly budget
	private BudgetAmtList defaultBudget;
	// name of general ledger object
	private String ledgerName;

	/**
	 * This is a blank constructor
	 */
	public GeneralLedger() {
		monthlyData = new MonthlyLedgerList();
		accountList = new ArrayList<Type>();
		defaultBudget = new BudgetAmtList();
		ledgerName = "";
	}

	/**
	 * This loads data from the SQL database
	 */
	public void loadSQLData() {
		loadAccountListFromSQL();
		loadMonthlyLedgersFromSQL();
		loadMonthlyBudgetsFromSQL();
		loadDefaultBudgetFromSQL();
	}
	
	/**
	 * This loads single entries from the SQL database into the monthlyData object
	 */
	public void loadMonthlyLedgersFromSQL() {
		executeSQLCommand("LoadSingleEntries");
	}
	
	/**
	 * This loads a list of accounts from the SQL database into the accountList object
	 */
	public void loadAccountListFromSQL() {
		executeSQLCommand("LoadAccounts");
	}
	
	/**
	 * This loads the monthly budget from the SQL database into the monthlyData object
	 */
	public void loadMonthlyBudgetsFromSQL() {
		executeSQLCommand("LoadMonthlyBudgets");
	}
	
	/**
	 * This loads the default budget from the SQL database into the defaultBudget object
	 */
	public void loadDefaultBudgetFromSQL() {
		executeSQLCommand("LoadDefaultBudget");
	}

	// add an account number (aka Type object) from the list of all accounts
	public void addAccount(String inputName, String inputDesc, boolean isAnExpense, boolean isIncludedInBudget, boolean isInDefaultAcctList, double defaultBudgetAmount) {
		// to do: make sure no other account in list has the same name, names should be unique
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

	// remove an account number from the list of all accounts and remove from the default budget accunt list
	public void removeAccount(Type deleteAccount) {
		accountList.remove(deleteAccount);
		if (defaultBudget.isAccountInList(deleteAccount) == true) {
			defaultBudget.deleteAccount(deleteAccount);
		}
	}

	// check if it already exists and then add a new month to the ledger
	public void addMonth(int month, int year) {
		try {
			monthlyData.addMonthToLedger(month,year,defaultBudget);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
	}

	// add a Single Entry to a month
	public void addSingleEntry(JDateTime inputDate, String inputDesc, Type inputType, double inputAmt) {
		// create new SingleEntry
		SingleEntry inputEntry = new SingleEntry(inputDate, inputType, inputDesc, inputAmt);
		monthlyData.addSingleEntryToLedger(inputDate.getMonth(), inputDate.getYear(), inputEntry);
	}

	/** This returns a list of all the accounts in the general ledger
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

	// add account to the default budget
	public void addDefaultBudgetAcct(Type newAccount, double newBudgetAmount){
		defaultBudget.addAccount(newAccount, newBudgetAmount);
	}

	// remove account from the default budget
	public void removeDefaultBudgetAcct(Type deleteAccount){
		defaultBudget.deleteAccount(deleteAccount);
	}

	// update amount on the default budget
	public void updateDefaultBudgetAmount(Type updateAccount, double newAmount) {
		if (defaultBudget.isAccountInList(updateAccount) == true) {
			defaultBudget.updateBudgetAmount(updateAccount, newAmount);
		} else {
			throw new IllegalArgumentException("Account " + updateAccount + " isn't in default budget.");
		}
	}

	// add account to a monthly budget
	public void addMonthlyBudgetAcct(int month, int year, Type newAccount, double newBudgetAmount){
		monthlyData.addMonthlyBudgetAcct(month, year, newAccount, newBudgetAmount);
	}

	// remove account from a monthly budget
	public void removeMonthlyBudgetAcct(int month, int year, Type deleteAccount){
		monthlyData.removeMonthlyBudgetAcct(month, year, deleteAccount);
	}

	// update amount on a monthly budget account
	public void updateMonthlyBudgetAmount(int month, int year, Type updateAccount, double newAmount) {
		if (defaultBudget.isAccountInList(updateAccount) == true) {
			monthlyData.updateMonthlyBudgetAmount(month, year, updateAccount, newAmount);
		} else {
			throw new IllegalArgumentException("Account " + updateAccount + " isn't in this month's budget.");
		}
	}

	/**
	 * This saves data to SQL database
	 */
	public void saveSQLData() {
		// erase all 3 old databases
		executeSQLCommand("EraseDatabase");
		// write out contents of objects to 3 databases
		executeSQLCommand("SaveAccountList");
		executeSQLCommand("SaveMonthlyLedger");
		executeSQLCommand("SaveDefaultBudget");
		executeSQLCommand("SaveMonthlyBudgets");
		
		// (later try to only save changes)
	}
	
	// load different types of data from SQL database (accounts, monthly ledger, budget info)
	public void executeSQLCommand(String loadType) {

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// We can set a custom username and password in phpMyadmin (in localhost for XAMPP) 
		// but for now just use default host login with no password

		Connection connection = null;

		String user = "root";
		String password = "";
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3308/javabudget", user, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		int statementCnt = 1;
		ArrayList<String> statementList = new ArrayList<String>();
		if (loadType == "EraseDatabase") statementCnt = 4;
		if (loadType == "SaveAccountList") statementCnt = accountList.size();
		ArrayList<SingleEntry> rawEntryList = new ArrayList<SingleEntry>();
		if (loadType == "SaveMonthlyLedger") {
			// make a giant list of all the SingleEntry data from all months in the general ledger
			rawEntryList = monthlyData.getRawSingleEntryList();
			statementCnt = rawEntryList.size();
		}
		if (loadType == "SaveDefaultBudget") {
			// extract account list from the DefaultBudget object - keySet returns a set of all the keys in the HashMap
			for(Type setAccount : defaultBudget.getAccountList()){
				String tmpAccount = setAccount.getTypeName();
				tmpAccount = tmpAccount.replace("'", "''"); // replace single quotes with '' to make it compatible with SQL database
				double tmpAmount = defaultBudget.getBudgetAmount(setAccount);
				
				String tmpStatement = "INSERT INTO default_budget (ACCOUNT, AMOUNT) ";
				tmpStatement += "VALUES ('"+tmpAccount+"', '"+tmpAmount+"')";
				statementList.add(tmpStatement);
			}
			//statementCnt = defaultBudget.nbrAccounts();
			statementCnt = statementList.size();
		}
		
		if (loadType == "SaveMonthlyBudgets"){

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
			//statementCnt = monthlyData.monthCount();
			statementCnt = statementList.size();
		}
		
		for (int i = 0; i < statementCnt; i++) {

			Statement statement = null;
			try {
				statement = connection.createStatement();

				String command = "";
				String commandType = "";
				if (loadType == "LoadAccounts") {
					command = "SELECT * FROM `account_list`";
					commandType = "Query";
				}
				if (loadType == "LoadSingleEntries") {
					command = "SELECT * FROM `general_ledger`";
					commandType = "Query";
				}
				if (loadType == "LoadMonthlyBudgets") {
					command = "SELECT * FROM `monthly_budget`";
					commandType = "Query";
				}
				if (loadType == "LoadDefaultBudget") {
					command = "SELECT * FROM `default_budget`";
					commandType = "Query";
				}
				if (loadType == "EraseDatabase") {
					if (i == 0) command = "TRUNCATE `account_list`";
					if (i == 1) command = "TRUNCATE `general_ledger`";
					if (i == 2) command = "TRUNCATE `monthly_budget`";
					if (i == 3) command = "TRUNCATE `default_budget`";
					commandType = "Update";
				}
				if (loadType == "SaveAccountList") {
					// extract data from accountList object
					Type tmpAccount = accountList.get(i);
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
					command = "INSERT INTO account_list (NAME, DESCRIPTION, IS_AN_EXPENSE, IS_IN_BUDGET) ";
					command += "VALUES ('"+tmpName+"', '"+tmpDesc+"', '"+tmpIsAnExpense+"', '"+tmpIsInBudget+"')";
					commandType = "Update";
				}
				if (loadType == "SaveMonthlyLedger") {
					// extract data from accountList object
					SingleEntry tmpEntry = rawEntryList.get(i);
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
					command = "INSERT INTO general_ledger (DATE, DESCRIPTION, ACCOUNT, AMOUNT) ";
					command += "VALUES ('"+tmpDate+"', '"+tmpDesc+"', '"+tmpAccount+"', '"+tmpAmount+"')";
					commandType = "Update";
				}
				
				if ((loadType == "SaveDefaultBudget") || (loadType == "SaveMonthlyBudgets")) {
					command = statementList.get(i);
					commandType = "Update";
				}

				if (command != "") {
					System.out.println("SQL command = " + command);
					if (commandType == "Update" ) {
						statement.executeUpdate(command);
					} else { 
						ResultSet resultSet = statement.executeQuery(command);
						
						while(resultSet.next()){	
							if (loadType == "LoadAccounts") {
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
							if (loadType == "LoadMonthlyBudgets") {
								String monthYear = resultSet.getString("MONTHYEAR");
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
							if (loadType == "LoadDefaultBudget") {
								String stringAccount = resultSet.getString("ACCOUNT");
								stringAccount = stringAccount.replace("''", "'"); // replace single quotes with '' to make it compatible with SQL database
								// get the Type object for this account name
								Type budgetAccount = getAccount(stringAccount);
								String stringAmount = resultSet.getString("AMOUNT");
								double budgetAmount = Double.parseDouble(stringAmount);
								addDefaultBudgetAcct(budgetAccount, budgetAmount);
							}
							if (loadType == "LoadSingleEntries") {
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
						}
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*	public saveBudgetToSQL
	public viewYear
	public editMonth
	 */

	// get an account from the accountList by searching using the account name
	private Type getAccount(String accountName) {
		for (int i = 1; i < accountList.size(); i++) {
			Type tempAccount = accountList.get(i);
			if (tempAccount.getTypeName().equals(accountName)) {
				return tempAccount;	
			}
		}
		return null;
	}

	public double getTotalAmountDonatedToCharity(JDateTime startDate, JDateTime endDate) {
		return 0;
	}
}
