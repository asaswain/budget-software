package budget_program;

/** 
 * This class stores the type of entry, such as Food, Fun, Other Needs, or General Income.
 * @author Asa Swain
 */

/*
 * 05/09/2014 added isAnExpense variable because user will always enter positive numbers,
 *            and program needs to know if a category is an expense or an income
 */

public class Type {
	// name of the Income/Expense type - should be unique
	private String typeName;
	// description of the Income/Expense type
	private String typeDesc;
	// is this type an Expense or an Income
	private boolean isAnExpense;
	// is this type included in the budget?
	private boolean isIncludedInBudget;

	/**
	 * blank constructor
	 */
	public Type	(){
		typeName = "";
		typeDesc = "";
		isAnExpense = false;
		isIncludedInBudget = false;
	}

	// basic constructor
	public Type	(String name, String desc, boolean expense, boolean inBudget){
		typeName = name;
		typeDesc = desc;
		isAnExpense = expense;
		isIncludedInBudget = inBudget;
	}

	// get name of Type object
	public String getTypeName() {
		return typeName;
	}

	// set name of Type object
	public void setTypeName(String newName) {
		typeName = newName;
	}

	// get desc of Type object
	public String getTypeDesc() {
		return typeDesc;
	}

	// set desc of Type object
	public void setTypeDesc(String newDesc) {
		typeDesc = newDesc;
	}

	// get if this type amount is an expense or an income
	public boolean getIsAnExpense() {
		return isAnExpense;
	}

	// set if this type amount is an expense or an income
	public void setIsAnExpense(boolean newIsAnExpense) {
		isAnExpense = newIsAnExpense;
	}

	// get if this type is included in the monthly budget
	public boolean getIsIncludedInBudget() {
		return isIncludedInBudget;
	}

	// set if this type is included in the monthly budget
	public void setIsIncludedInBudget(boolean newIsIncludedInBudget) {
		isIncludedInBudget = newIsIncludedInBudget;
	}

}
