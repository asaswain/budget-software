package budget_program;

import java.util.*;

import jodd.datetime.JDateTime;

/**
* The DatedList class consists of a TreeMap of days indexed by date and each day contains an ArrayList of single entries.
* 
* @author Asa Swain
*/

public class SingleEntryList {	
	// use date as key in HashMap to return an arraylist of entries for that date
	private TreeMap<JDateTime, ArrayList<SingleEntry>> datedEntryList;

	/**
	 * This is a blank constructor
	 */
	public SingleEntryList() {
		datedEntryList = new TreeMap<JDateTime, ArrayList<SingleEntry>>();
	}
	
	/**
	 * This is a constructor for a single entry
	 * 
	 * @param newEntry  the entry to add to the TreeMap
	 */
	public SingleEntryList(SingleEntry newEntry) {
		datedEntryList = new TreeMap<JDateTime, ArrayList<SingleEntry>>();
		addEntry(newEntry);
	}
	
	/**
	 * This adds a single entry onto the list of entries for a date
	 * 
	 * @param newEntry  the new SingleEntry to add onto this day 
	 */
	public void addEntry(SingleEntry newEntry) {
		ArrayList<SingleEntry> tmpList = new ArrayList<SingleEntry>();
		if (datedEntryList.containsKey(newEntry.getDate())) {
			tmpList = datedEntryList.get(newEntry.getDate());	
		} 
		tmpList.add(newEntry);
		datedEntryList.put(newEntry.getDate(), tmpList);
	}
	
	/**
	 * This adds a single entry into a specific location in the list of entries for a date
	 * 
	 * @param newEntry  the new SingleEntry to add to this day's list of entries
	 * @param targetIndex  location to insert the new SingleEntry in this day's list of entries
	 */
	public void addEntry(SingleEntry newEntry, int targetIndex) {
		ArrayList<SingleEntry> tmpList = new ArrayList<SingleEntry>();
		if (datedEntryList.containsKey(newEntry.getDate())) {
			tmpList = datedEntryList.get(newEntry.getDate());	
		} 
		tmpList.add(targetIndex, newEntry);
		datedEntryList.put(newEntry.getDate(), tmpList);
	}
	
	/**
	 * This removes an entry from the list of entries (making sure it's there first)
	 *
	 * @param targetDate - date of entry to delete
	 * @param targetIndex - index of entry to delete
	 * @exception IllegalArgumentException - if the list of entries for this date does not at least "targetIndex" number of items in it
	 */
	public void deleteEntry(JDateTime targetDate, int targetIndex) {
		ArrayList<SingleEntry> tmpList = datedEntryList.get(targetDate);
		if (tmpList.size() >= targetIndex) {
			tmpList.remove(targetIndex);
		} else {
			throw new IllegalArgumentException("The list of entries for this date: " + targetDate + " doesn't have the entry you are trying to delete");
		}
		datedEntryList.put(targetDate,tmpList);
	}
	
	/**
	 * This removes an entry from the list of entries (making sure it's there first)
	 *
	 * @param delEntry - the SingleEntry to delete
	 * @exception IllegalArgumentException - if the list of entries for this date does not contain the delEntry object
	 */
	public void deleteEntry(SingleEntry delEntry) {
		ArrayList<SingleEntry> tmpList = datedEntryList.get(delEntry.getDate());
		if (tmpList.contains(delEntry)) {
			tmpList.remove(delEntry);
			} else {
				throw new IllegalArgumentException("The list of entries for this date: " + delEntry.getDate() + " doesn't have the entry you are trying to delete");
			}
		datedEntryList.put(delEntry.getDate(),tmpList);
	}
	
	/**
	 * This removes the old SingleEntry and adds the new SingleEntry from the list of entries
	 *
	 * @param oldEntry  the old SingleEntry to remove
	 * @param newEntry  the new SingleEntry to add
	 * @exception IllegalArgumentException - if the oldEntry does not exist on this day
	 */
	public void updateEntry(SingleEntry oldEntry, SingleEntry newEntry) {
		//TODO: this breaks the sort order of the entries
		try {
			deleteEntry(oldEntry);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
		addEntry(newEntry);
	}
	
	/**
	 * This updates an entry from the list of entries (making sure it's there first)
	 *
	 * @param targetDate - date of entry to update
	 * @param targetIndex - index of entry to update
	 * @param newEntry - the new SingleEntry data to use in the update
	 * @exception IllegalArgumentException - if the list of entries for this date does not at least "targetIndex" number of items in it
	 */
	public void updateEntry(JDateTime targetDate, int targetIndex, SingleEntry newEntry) {
		try {
			deleteEntry(targetDate,targetIndex);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
		// if both entries are on the same date, then insert new entry into the same location in the list as the old entry, otherwise insert onto end of list
		if (newEntry.getDate().equals(targetDate)) {
			addEntry(newEntry,targetIndex);
		} else {
			addEntry(newEntry);
		}
	}
	
	/**
	 * This checks if a single entry is in the list of entries
	 * 
	 * @param testEntry - the entry we are searching for
	 * @return true if this entry is in the month then return true, else return false
	 */
	public boolean isEntryInTheList(SingleEntry testEntry) {
		ArrayList<SingleEntry> tmpList = datedEntryList.get(testEntry.getDate());	
		return tmpList.contains(testEntry);
	}
	
	/**
	 * This returns a list of all the SingleEntries for the entire list
	 * 
	 * @return an ArrayList of all the SingleEntries for the entire list
	 */
	public ArrayList<SingleEntry> getSingleEntryList() {
		ArrayList<SingleEntry> masterList = new ArrayList<SingleEntry>();

		//keySet returns a set of all the keys in the TreeMap
		for(JDateTime tmpDate : datedEntryList.keySet()){
			ArrayList<SingleEntry> tmpList = datedEntryList.get(tmpDate);
			masterList.addAll(tmpList);
		}

		return masterList;
	};
	
	/**
	 * This returns a list of all the SingleEntries for a specific date
	 * 
	 * @return an ArrayList of all the SingleEntries for a specific date
	 */
	public ArrayList<SingleEntry> getSingleEntryList(JDateTime searchDate) {
		return datedEntryList.get(searchDate);
	};
	
	/**
	 * This returns the nth SingleEntry on a specific date
	 * 
	 * @param searchDate  the date to get the entry from
	 * @param loc  the index of the entry to get on that date
	 * @return a SingleEntry object
	 */
	public SingleEntry getSingleEntry(JDateTime searchDate, int loc) {
		ArrayList<SingleEntry> tmpList = getSingleEntryList(searchDate);	
		try {
		return tmpList.get(loc);
	} catch (IndexOutOfBoundsException e) {
		throw new IllegalArgumentException("Ledger doesn't have " + loc + " entries for this date");
	}
	}
	
	/**
	 * This returns the number of SingleEntries in a specific date
	 * 
	 * @param the date we are looking for 
	 * @return the number of entries in that date or zero if no entries have been entered for that date
	 */
	public int getNumberOfEntriesInDate(JDateTime searchDate) {
		ArrayList<SingleEntry> tmpList = getSingleEntryList(searchDate);
		if (tmpList != null) {
			return tmpList.size();
		} else {
			return 0;
		}
	}
	
	/**
	 * This returns a list of all dates in the datedEntryList
	 * 
	 * @return an ArrayList of all the dates that have entries in them
	 */
	public ArrayList<JDateTime> getDateList() {
		return new ArrayList(datedEntryList.keySet());
	};
	
	/**
	 * This returns the number of SingleEntries in the entire datedEntryList
	 * 
	 * @return the number of entries in this month
	 */
	public int size() {
		int totalSize = 0;
		//keySet returns a set of all the keys in the TreeMap
		for(JDateTime tmpDate : datedEntryList.keySet()){
			totalSize = totalSize + datedEntryList.get(tmpDate).size();
		}
		return totalSize;
	}
}
