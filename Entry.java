package budget_program;

import java.math.BigDecimal;

/**
 * This abstract class is the template for classes that store income and expenses in the general ledger 
 * 
 * @author Asa Swain
 */

public abstract class Entry {
	// type of income/expense
	protected Account entryAccount;
	// description of the income/expense
	protected String desc;
	
	/**
	 * Get the type for this entry
	 * @return entry type 
	 */
	public Account getAccount() {
		return entryAccount;
	}
	
	/** 
	 * Set the type for this entry
	 * @param newType - the new Type for this entry
	 */
	public void setAccount(Account newType) {
		entryAccount = newType;
	}
	
	/**
	 * Get the description of this entry
	 * @return entry description
	 */
	public String getDesc() {
		return desc;
	}
	
	/**
	 * Set the description of this entry
	 * @param newDesc - the new description for this entry
	 */
	public void setDesc(String newDesc) {
		desc = newDesc;
	}
	
	abstract BigDecimal getMonthlyAmount();
	abstract void setTotalAmount(BigDecimal newAmount);
}
