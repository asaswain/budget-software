package budget_program;

/**
 * This class stores the account type for income and expenses, such as Food, Fun, Other Needs, or Payroll Income.
 * 
 * @author Asa Swain
 */

public class Account {
	// name of the Income/Expense type - should be unique
	private String accountName;
	// description of the Income/Expense type
	private String accountDesc;
	// is this type an Expense or an Income (entry amounts will always be positive,  
	// and program needs to know if a category is an expense or an income)
	private boolean isAnExpense;
	// is this type included in the budget?
	private boolean isIncludedInBudget;

	/**
	 * blank constructor
	 */
	public Account	(){
		accountName = "";
		accountDesc = "";
		isAnExpense = false;
		isIncludedInBudget = false;
	}

	/**
	 * This is a basic constructor
	 * 
	 * @param name - Account name
	 * @param desc - Account description
	 * @param expense - Is this account for expenses? (true/false)
	 * @param inBudget - Are the expense/income amounts in this account included in the budget? (true/false)
	 */
	public Account(String name, String desc, boolean expense, boolean inBudget){
		accountName = name;
		accountDesc = desc;
		isAnExpense = expense;
		isIncludedInBudget = inBudget;
	}

	/**
	 * This method gets the name of the account
	 * 
	 * @return account name
	 */
	public String getAccountName() {
		return accountName;
	}

	/**
	 * This method sets the name of the account
	 * 
	 * @param newName - new account name
	 */
	public void setAccountName(String newName) {
		accountName = newName;
	}

	/**
	 * This method gets the description of the account
	 * 
	 * @return account description
	 */
	public String getAccountDesc() {
		return accountDesc;
	}

	/**
	 * This method sets the description of the account
	 * 
	 * @param newDesc - new account description
	 */
	public void setAccountDesc(String newDesc) {
		accountDesc = newDesc;
	}

	/**
	 * This method gets if this account is for expense amounts or income amounts
	 * 
	 * @return boolean value if this account is for expense amounts or not
	 */
	public boolean getIsAnExpense() {
		return isAnExpense;
	}

	/**
	 * This method sets if this account is for expense amounts or income amounts
	 * 
	 * @param newIsAnExpense - boolean value if this account is for expense amounts or not
	 */
	public void setIsAnExpense(boolean newIsAnExpense) {
		isAnExpense = newIsAnExpense;
	}

	/**
	 * This method gets if this account is included in monthly budgets or not
	 * 
	 * @return boolean value if this account is included in monthly budgets or not
	 */
	public boolean getIsIncludedInBudget() {
		return isIncludedInBudget;
	}

	/**
	 * This method sets if this account is included in monthly budgets or not
	 * 
	 * @param newIsIncludedInBudget - boolean value if this account is included in monthly budgets or not
	 */
	// set if this type is included in the monthly budget
	public void setIsIncludedInBudget(boolean newIsIncludedInBudget) {
		isIncludedInBudget = newIsIncludedInBudget;
	}
}
