package budget_program;

import java.util.*;

/**
* The EntryList class is a list of entries 
* eventually it is supposed to be used for installment and repeating entries
* 
* <afs> 07/20/2014 this class currently isn't being used
*/
public class EntryList {

	// a list of income/expense entries
	private ArrayList<Entry> entryList;
	
	// blank constructor
	public EntryList() {
		entryList = new ArrayList<Entry>();
	}

	// eventually I'll need to insert this into a sorted list instead of just adding to the end
	public void addEntry(Entry newEntry) {
		entryList.add(newEntry);
	}
	
	public void deleteEntry(Entry delEntry) {
		entryList.remove(delEntry);
	}
	
	public void updateEntry(Entry oldEntry, Entry newEntry) {
		int loc = entryList.indexOf(oldEntry);
		if (loc != -1) {
			entryList.set(loc, newEntry);
		} else  {
			throw new IllegalArgumentException("List does not contain entry you are trying to update");
		}
	}
	
	public Entry getEntry(int loc) {
		return entryList.get(loc);
	}
	
	public int size() {
		return entryList.size();
	}
	
	public void printListItem(int index) {
		Entry temp = entryList.get(index);
		temp.printEntry();
	}
	
	//show data for a specific month
	//show date for a date range
	
}
