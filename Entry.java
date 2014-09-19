package budget_program;

/**
 * This abstract class is the template for classes that store income and expenses in the general ledger 
 * 
 * @author Asa Swain
 */

public abstract class Entry {
	// type of income/expense
	protected Type entryType;
	// description of the income/expense
	protected String desc;
	
	/**
	 * Get the type for this entry
	 * @return entry type 
	 */
	public Type getType() {
		return entryType;
	}
	
	/** 
	 * Set the type for this entry
	 * @param newType - the new Type for this entry
	 */
	public void setType(Type newType) {
		entryType = newType;
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
	
	abstract double getAmount();
	abstract void setAmount(double newAmount);
	abstract void printEntry();
}
