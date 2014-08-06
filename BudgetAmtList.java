package budget_program;

import java.util.*;

/**
 * This object stores a list of accounts and the associated budgeted amounts for a given month
 * @author Asa Swain
 */

public class BudgetAmtList {

	// a hashmap of how much money was budgeted for each account (aka type)
	private HashMap<Type,Double> budgetAccountList;

	/**
	 * blank constructor
	 */
	public BudgetAmtList() {
		budgetAccountList = new HashMap<Type,Double>();
	}

	/**
	 * This is a copy constructor
	 * 
	 * @param originalBudget original budget to copy from when creating new BudgetAmtList object
	 */
	public BudgetAmtList(BudgetAmtList originalBudget) {
		budgetAccountList = new HashMap<Type,Double>();
		this.budgetAccountList = originalBudget.budgetAccountList;
	}

	/**
	 * This adds a new account and an associated budgeted amount the budget list
	 * (first check to make sure the account hasn't already been added, and make sure
	 * user isn't trying to set a budget amount for an account type that can't be budgeted)
     * 
	 * @param newAccount  the new account to add to the budget
	 * @param newBudgetAmount  the new amount to add to the budget
	 * @throws error if the account already exists in the budget
	 * @throws error if the account type can't be budgeted  
	 */
	public void addAccount(Type newAccount, Double newBudgetAmount) {
		if (budgetAccountList.containsKey(newAccount) == true) {
			throw new IllegalArgumentException("Account " + newAccount.getTypeName() + " already exists in the budget for this month.");	
		} else {
			if (newAccount.getIsIncludedInBudget() == true) {
				budgetAccountList.put(newAccount,newBudgetAmount);
			} else {
				// accounts outside the budget have no amount, so load a placeholder zero value
				budgetAccountList.put(newAccount,(double)0);
				if (newBudgetAmount != 0) {
					throw new IllegalArgumentException("Account " + newAccount.getTypeName() + " isn't a budgeted account. Can't set a budget amount for this account.");
				}
			}
		}
	}

	/**
	 * This removes an account from the list of accounts in this montb's budget
	 * 
	 * @param deleteAccount The account to remove from the budget
	 */
	public void deleteAccount(Type deleteAccount) {
		budgetAccountList.remove(deleteAccount);
	}
	

	/**
	 * This updates the amount for an account in the budget
	 * 
	 * @param account  the account to update the budgeted amount for
	 * @param newBudgetAmount  the new budgeted amount
	 * @throws an exception if the account isn't a budgeted account 
	 */
	public void updateBudgetAmount(Type account, Double newBudgetAmount) {
		if (account.getIsIncludedInBudget() == true) {
			budgetAccountList.put(account,newBudgetAmount);
		} else {
			if (newBudgetAmount != 0) {
				throw new IllegalArgumentException("Account " + account + " isn't a budgeted account. Can't set a budget amount for this account.");
			}
		}
	}
	
	/** 
	 * This checks if an account is in this budget list of accounts
	 * 
	 * @param inputAccount  the account to look for
	 * @return true if the account is in the budget account list, else false
	 */
	public boolean isAccountInList(Type inputAccount) {
		return budgetAccountList.containsKey(inputAccount);
	}
	
	/**
	 * This returns a set of all the accounts in this month's budget
	 * 
	 * @return a set of all the accounts in this month's budget
	 */
	public Set<Type> getAccountList() {
		return budgetAccountList.keySet();
	}
	
	/**
	 * This returns the number of accounts in this month's budget
	 * 
	 * @return number of accounts in month's budget (integer)
	 */
	public int nbrAccounts() {
		return budgetAccountList.keySet().size();
	}

	/**
	 * This returns an amount for this account (aka type), unless the account is not used in the budget
	 * 
	 * @param account account to get an amount for
	 * @throws exception if the account isn't in the monthly budget
	 * @throws exception if the account isn't a budgeted account (according to Type object settings)
	 * @return amount for this account
	 */
	public double getBudgetAmount(Type account) {
		if (budgetAccountList.containsKey(account)) {
			if (account.getIsIncludedInBudget() == true) {
				return budgetAccountList.get(account);
			} else {
				throw new IllegalArgumentException("Account " + account + " isn't a budgeted account. Can't get a budget amount for this account.");
			}
		} else {
			throw new IllegalArgumentException("Account " + account + " isn't in this monthly budget.");
		}
	}
	
	/**
	 * This prints out a the budgeted amount for each account in the list for this month
	 */
	public void printBudget() {
		// extract data from DefaultBudget object - keySet returns a set of all the keys in the HashMap\
		for(Type setAccount : budgetAccountList.keySet()){
			String printAccount = setAccount.getTypeName();
			double printAmount = getBudgetAmount(setAccount);
			System.out.println("Account: " + printAccount + " Budgeted Amount: " + printAmount);
		}
	}
}