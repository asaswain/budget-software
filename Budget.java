package budget_program;

import java.util.*;

/**
 * This class stores a Budget which consists of a hashmap containing a list of accounts and the budgeted amounts for each account
 * Each MonthlyLedger has its own BudgetAmtList object.
 * 
 * @author Asa Swain
 */

public class Budget {

	// a hashmap of how much money was budgeted for each account (aka type)
	private HashMap<Account,Double> budgetAccountList;

	/**
	 * blank constructor
	 */
	public Budget() {
		budgetAccountList = new HashMap<Account,Double>();
	}

	/**
	 * This is a copy constructor
	 * 
	 * @param originalBudget original budget to copy from when creating new BudgetAmtList object
	 */
	public Budget(Budget originalBudget) {
		budgetAccountList = new HashMap<Account,Double>();
		this.budgetAccountList = originalBudget.budgetAccountList;
	}

	/**
	 * This adds a new account and an associated budgeted amount the budget list
	 * (first check to make sure the account hasn't already been added, and make sure
	 * user isn't trying to set a budget amount for an account type that can't be budgeted)
     * 
	 * @param newAccount  the new account to add to the budget
	 * @param newBudgetAmount  the new amount to add to the budget
	 * @exception if the account already exists in the budget
	 * @exception if the account type can't be budgeted  
	 */
	public void addAccount(Account newAccount, Double newBudgetAmount) {
		if (budgetAccountList.containsKey(newAccount) == true) {
			throw new IllegalArgumentException("Account " + newAccount.getAccountName() + " already exists in the budget for this month.");	
		} else {
			if (newAccount.getIsIncludedInBudget() == true) {
				budgetAccountList.put(newAccount,newBudgetAmount);
			} else {
				// accounts outside the budget have no amount, so load a placeholder zero value
				budgetAccountList.put(newAccount,(double)0);
				if (newBudgetAmount != 0) {
					throw new IllegalArgumentException("Account " + newAccount.getAccountName() + " isn't a budgeted account. Can't set a budget amount for this account.");
				}
			}
		}
	}

	/**
	 * This removes an account from the list of accounts in this montb's budget
	 * 
	 * @param deleteAccount The account to remove from the budget
	 */
	public void deleteAccount(Account deleteAccount) {
		budgetAccountList.remove(deleteAccount);
	}
	

	/**
	 * This updates the amount for an account in the budget
	 * 
	 * @param account  the account to update the budgeted amount for
	 * @param newBudgetAmount  the new budgeted amount
	 * @exception if the account isn't a budgeted account 
	 */
	public void updateBudgetAmount(Account account, Double newBudgetAmount) {
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
	public boolean isAccountInList(Account inputAccount) {
		return budgetAccountList.containsKey(inputAccount);
	}
	
	/**
	 * This returns a set of all the accounts in this month's budget
	 * 
	 * @return a set of all the accounts in this month's budget
	 */
	public Set<Account> getAccountList() {
		return budgetAccountList.keySet();
	}
	
	/**
	 * This returns an amount for this account (aka type), unless the account is not used in the budget
	 * 
	 * @param account account to get an amount for
	 * @exception if the account isn't in the monthly budget
	 * @exception if the account isn't a budgeted account (according to Type object settings)
	 * @return amount for this account
	 */
	public double getBudgetAmount(Account account) {
		if (budgetAccountList.containsKey(account)) {
			if (account.getIsIncludedInBudget() == true) {
				return budgetAccountList.get(account);
			} else {
				throw new IllegalArgumentException("Account " + account + " isn't a budgeted account. Can't get a budget amount for this account.");
			}
		} else {
			throw new IllegalArgumentException("Account " + account + " isn't in this budget.");
		}
	}
}