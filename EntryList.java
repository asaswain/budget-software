package budget_program;

import java.util.*;

import jodd.datetime.JDateTime;

/**
 * The EntryList class consists of: 
 * a TreeMap of single entries indexed by date. Each day contains an ArrayList of single entries.
 * a TreeMap of repeating entries indexed by the name of the repeating entry. 
 * 
 * @author Asa Swain
 */

public class EntryList {	
	// use date as key in HashMap to return an arraylist of entries for that date
	private TreeMap<JDateTime, ArrayList<SingleEntry>> singleEntryList;
	// index repeating entries based on their description
	private TreeMap<String,InstallmentEntry> installmentEntryList;
	private TreeMap<String,RepeatingEntry> repeatingEntryList;

	/**
	 * This is a blank constructor
	 */
	public EntryList() {
		singleEntryList = new TreeMap<JDateTime, ArrayList<SingleEntry>>();
		installmentEntryList = new TreeMap<String,InstallmentEntry>();
		repeatingEntryList = new TreeMap<String,RepeatingEntry>();
	}

	/**
	 * This is a constructor for a single entry
	 * 
	 * @param newEntry  the entry to add to the TreeMap
	 */
	public EntryList(SingleEntry newEntry) {
		singleEntryList = new TreeMap<JDateTime, ArrayList<SingleEntry>>();
		addSingleEntry(newEntry);
		installmentEntryList = new TreeMap<String,InstallmentEntry>();
		repeatingEntryList = new TreeMap<String,RepeatingEntry>();
	}

	/**
	 * This adds a single entry onto the list of entries for a date
	 * 
	 * @param newEntry  the new SingleEntry to add onto this day 
	 */
	public void addSingleEntry(SingleEntry newEntry) {
		ArrayList<SingleEntry> tmpList = new ArrayList<SingleEntry>();
		if (singleEntryList.containsKey(newEntry.getDate())) {
			tmpList = singleEntryList.get(newEntry.getDate());	
		} 
		tmpList.add(newEntry);
		singleEntryList.put(newEntry.getDate(), tmpList);
	}

	/**
	 * This adds a single entry into a specific location in the list of entries for a date
	 * 
	 * @param newEntry  the new SingleEntry to add to this day's list of entries
	 * @param targetIndex  location to insert the new SingleEntry in this day's list of entries
	 */
	public void addSingleEntry(SingleEntry newEntry, int targetIndex) {
		ArrayList<SingleEntry> tmpList = new ArrayList<SingleEntry>();
		if (singleEntryList.containsKey(newEntry.getDate())) {
			tmpList = singleEntryList.get(newEntry.getDate());	
		} 
		tmpList.add(targetIndex, newEntry);
		singleEntryList.put(newEntry.getDate(), tmpList);
	}

	/**
	 * This adds a installment entry onto the list
	 * 
	 * @param newEntry - the new InstallmentEntry to add
	 */
	public void addInstallmentEntry(InstallmentEntry newEntry) {
		installmentEntryList.put(newEntry.getDesc(), newEntry);
	}
	
	/**
	 * This adds a repeating entry onto the list
	 * 
	 * @param newEntry - the new RepeatingEntry to add
	 */
	public void addRepeatingEntry(RepeatingEntry newEntry) {
		repeatingEntryList.put(newEntry.getDesc(), newEntry);
	}

	/**
	 * This removes an entry from the list of single entries (making sure it's there first)
	 *
	 * @param targetDate - date of entry to delete
	 * @param targetIndex - index of entry to delete
	 * @exception IllegalArgumentException - if the list of entries for this date does not at least "targetIndex" number of items in it
	 */
	public void deleteSingleEntry(JDateTime targetDate, int targetIndex) {
		ArrayList<SingleEntry> tmpList = singleEntryList.get(targetDate);
		if ((tmpList != null) && (tmpList.size() >= targetIndex)) {
			tmpList.remove(targetIndex);
		} else {
			throw new IllegalArgumentException("The list of entries for this date: " + targetDate + " doesn't have the entry you are trying to delete");
		}
		singleEntryList.put(targetDate,tmpList);
	}

	/**
	 * This removes an entry from the list of single entries (making sure it's there first)
	 *
	 * @param delEntry - the SingleEntry to delete
	 * @exception IllegalArgumentException - if the list of entries for this date does not contain the delEntry object
	 */
	public void deleteSingleEntry(SingleEntry delEntry) {
		ArrayList<SingleEntry> tmpList = singleEntryList.get(delEntry.getDate());
		if ((tmpList != null) && (tmpList.contains(delEntry))) {
			tmpList.remove(delEntry);
		} else {
			throw new IllegalArgumentException("The list of entries for this date: " + delEntry.getDate() + " doesn't have the entry you are trying to delete");
		}
		singleEntryList.put(delEntry.getDate(),tmpList);
	}
	
	/**
	 * This removes an entry from the list of installment entries (making sure it's there first)
	 *
	 * @param targetDesc - the description of the InstallmentEntry to delete
	 * @exception IllegalArgumentException - if the list of installment entries for this month does not contain an item with the targetDesc description
	 */
	public void deleteInstallmentEntry(String targetDesc) {
		if ((installmentEntryList != null) && (installmentEntryList.containsKey(targetDesc))) {
			installmentEntryList.remove(targetDesc);
		} else {
			throw new IllegalArgumentException("The list of entries for this month doesn't have the repeating entry you are trying to delete");
		}
	}

	/**
	 * This removes an entry from the list of installment entries (making sure it's there first)
	 *
	 * @param delEntry - the InstallmentEntry to delete
	 * @exception IllegalArgumentException - if the list of entries for this month does not contain the delEntry object
	 */
	public void deleteInstallmentEntry(InstallmentEntry delEntry) {
		if ((installmentEntryList != null) && (installmentEntryList.containsKey(delEntry.getDesc()))) {
			installmentEntryList.remove(delEntry);
		} else {
			throw new IllegalArgumentException("The list of entries for this month doesn't have the entry you are trying to delete");
		}
	}

	/**
	 * This removes an entry from the list of repeating entries (making sure it's there first)
	 *
	 * @param targetDesc - the description of the RepeatingEntry to delete
	 * @exception IllegalArgumentException - if the list of repeating entries for this month does not contain an item with the targetDesc description
	 */
	public void deleteRepeatingEntry(String targetDesc) {
		if ((repeatingEntryList != null) && (repeatingEntryList.containsKey(targetDesc))) {
			repeatingEntryList.remove(targetDesc);
		} else {
			throw new IllegalArgumentException("The list of entries for this month doesn't have the repeating entry you are trying to delete");
		}
	}

	/**
	 * This removes an entry from the list of repeating entries (making sure it's there first)
	 *
	 * @param delEntry - the RepeatingEntry to delete
	 * @exception IllegalArgumentException - if the list of entries for this month does not contain the delEntry object
	 */
	public void deleteRepeatingEntry(RepeatingEntry delEntry) {
		if ((repeatingEntryList != null) && (repeatingEntryList.containsKey(delEntry.getDesc()))) {
			repeatingEntryList.remove(delEntry);
		} else {
			throw new IllegalArgumentException("The list of entries for this month doesn't have the entry you are trying to delete");
		}
	}

	/**
	 * This removes the old SingleEntry and adds the new SingleEntry from the list of entries
	 *
	 * @param oldEntry - the old SingleEntry to remove
	 * @param newEntry - the new SingleEntry to add
	 * @exception IllegalArgumentException - if the oldEntry does not exist on this day
	 */
	public void updateSingleEntry(SingleEntry oldEntry, SingleEntry newEntry) {
		//TODO: this breaks the sort order of the entries
		try {
			deleteSingleEntry(oldEntry);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
		addSingleEntry(newEntry);
	}

	/**
	 * This updates an entry from the list of entries (making sure it's there first)
	 *
	 * @param targetDate - date of entry to update
	 * @param targetIndex - index of entry to update
	 * @param newEntry - the new SingleEntry data to use in the update
	 * @exception IllegalArgumentException - if the list of entries for this date does not at least have "targetIndex" number of items in it
	 */
	public void updateSingleEntry(JDateTime targetDate, int targetIndex, SingleEntry newEntry) {
		try {
			deleteSingleEntry(targetDate,targetIndex);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
		// if both entries are on the same date, then insert new entry into the same location in the list as the old entry, otherwise insert onto end of list
		if (newEntry.getDate().equals(targetDate)) {
			addSingleEntry(newEntry,targetIndex);
		} else {
			addSingleEntry(newEntry);
		}
	}
	
	/**
	 * This replaces the old Installment Entry object with a new one in the TreeMap
	 *
	 * @param oldDesc - the description of the old InstallmentEntry replace
	 * @param newEntry - the new InstallmentEntry data to use in the update
	 * @exception IllegalArgumentException - if a InstallmentEntry object with the oldDesc does not exist in the TreeMap
	 */
	public void updateInstallmentEntry(String oldDesc, InstallmentEntry newEntry) {
		if (installmentEntryList.containsKey(oldDesc)) {
			try {
				installmentEntryList.replace(oldDesc, newEntry);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(e);
			}
		} else {
			throw new IllegalArgumentException("The list of entries doesn't have the entry you are trying to update");
		}
	}

	/**
	 * This replaces the old Repeating Entry object with a new one in the TreeMap
	 *
	 * @param oldDesc - the description of the old RepeatingEntry replace
	 * @param newEntry - the new RepeatingEntry data to use in the update
	 * @exception IllegalArgumentException - if a RepeatingEntry object with the oldDesc does not exist in the TreeMap
	 */
	public void updateRepeatingEntry(String oldDesc, RepeatingEntry newEntry) {
		if (repeatingEntryList.containsKey(oldDesc)) {
			try {
				repeatingEntryList.replace(oldDesc, newEntry);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(e);
			}
		} else {
			throw new IllegalArgumentException("The list of entries doesn't have the entry you are trying to update");
		}
	}

	/**
	 * This checks if a single entry is in the list of entries
	 * 
	 * @param testEntry - the entry we are searching for
	 * @return true if this entry is in the month then return true, else return false
	 */
	public boolean isSingleEntryInTheList(SingleEntry testEntry) {
		ArrayList<SingleEntry> tmpList = singleEntryList.get(testEntry.getDate());	
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
		for(JDateTime tmpDate : singleEntryList.keySet()){
			// add all the items in the list for this date
			ArrayList<SingleEntry> tmpList = singleEntryList.get(tmpDate);
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
		return singleEntryList.get(searchDate);
	};

	/**
	 * This returns the nth SingleEntry on a specific date
	 * 
	 * @param searchDate  the date to get the entry from
	 * @param loc  the index of the entry to get on that date
	 * @return a SingleEntry object
	 * @exception IllegalArgumentException - if the ledger doesn't have loc entries for the searchDate
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
	 * This returns a list of installment entries for a target month or for all months
	 * 
	 * @param targetMonth - this is the month that we want to return all the installment entries for (if blank return entries for all months)
	 * @return - an ArrayList of installment entries
	 */
	public ArrayList<InstallmentEntry> getInstallmentEntryList(JDateTime targetMonth) {
		ArrayList<InstallmentEntry> returnList = new ArrayList<InstallmentEntry>();
		for (String tmpDesc : installmentEntryList.keySet()){
				InstallmentEntry tmpInstallmentEntry = installmentEntryList.get(tmpDesc);
				if (targetMonth != null) {
					if ((targetMonth.isAfterDate(tmpInstallmentEntry.getStartDate()) || targetMonth.equals(tmpInstallmentEntry.getStartDate()))  && 
							(targetMonth.isBeforeDate(tmpInstallmentEntry.getEndDate()) || targetMonth.equals(tmpInstallmentEntry.getEndDate()))) {
						returnList.add(tmpInstallmentEntry);
					} 
				} else {
					returnList.add(tmpInstallmentEntry);
				}
		}
		return returnList;
	}
	
	/**
	 * This returns a list of repeating entries for a target month or for all months
	 * 
	 * @param targetMonth - this is the month that we want to return all the repeating entries for (if blank return entries for all months)
	 * @return - an ArrayList of repeating entries
	 */
	public ArrayList<RepeatingEntry> getRepeatingEntryList(JDateTime targetMonth) {
		ArrayList<RepeatingEntry> returnList = new ArrayList<RepeatingEntry>();
		for (String tmpDesc : repeatingEntryList.keySet()){
				RepeatingEntry tmpRepeatingEntry = repeatingEntryList.get(tmpDesc);
				if (targetMonth != null) {
					if ((targetMonth.isAfterDate(tmpRepeatingEntry.getStartDate()) || targetMonth.equals(tmpRepeatingEntry.getStartDate()))  && 
							(targetMonth.isBeforeDate(tmpRepeatingEntry.getEndDate()) || targetMonth.equals(tmpRepeatingEntry.getEndDate()))) {
						returnList.add(tmpRepeatingEntry);
					} 
				} else {
					returnList.add(tmpRepeatingEntry);
				}
		}
		return returnList;
	}
	
//	/**
//	 * This returns a list of installment entries for a target month or for all months
//	 * 
//	 * @param targetMonth - this is the month that we want to return all the installment entries for (if blank return entries for all months)
//	 * @return - an ArrayList of installment entries
//	 /*
//	public ArrayList<InstallmentEntry> getInstallmentEntryList(JDateTime targetMonth) {
//		ArrayList<InstallmentEntry> returnList = new ArrayList<InstallmentEntry>();
//		try {
//			ArrayList<MultipleEntry> installmentEntryList = getMultipleEntryList(targetMonth, "installment");			
//			for (MultipleEntry tmpInstallmentEntry : installmentEntryList) {
//				returnList.add((InstallmentEntry) tmpInstallmentEntry);
//			}
//			return returnList;	
//		} catch (IllegalArgumentException e){
//			throw new IllegalArgumentException(e);
//		}
//	}
//	
//	*//**
//	 * This returns a list of repeating entries for a target month or for all months
//	 * 
//	 * @param targetMonth - this is the month that we want to return all the repeating entries for (if blank return entries for all months)
//	 * @return - an ArrayList of repeating entries
//	 */
//	public ArrayList<RepeatingEntry> getRepeatingEntryList(JDateTime targetMonth) {
//		ArrayList<RepeatingEntry> returnList = new ArrayList<RepeatingEntry>();
//		try {
//			ArrayList<MultipleEntry> installmentEntryList = getMultipleEntryList(targetMonth, "repeating");			
//			for (MultipleEntry tmpRepeatingEntry : installmentEntryList) {
//				returnList.add((RepeatingEntry) tmpRepeatingEntry);
//			}
//			return returnList;	
//		} catch (IllegalArgumentException e){
//			throw new IllegalArgumentException(e);
//		}
//	}
//
//	/**
//	 * This returns a list of multiple entries for a target month or for all months
//	 * 
//	 * @param targetMonth - this is the month that we want to return all the multiple entries for (if blank return entries for all months)
//	 * @param entryType - the type of MultpleEntry to select, either "installment" or "repeating"
//	 * @return - an ArrayList of multiple entries
//	 */
//	private ArrayList<MultipleEntry> getMultipleEntryList(JDateTime targetMonth, String entryType) {
//		if ((entryType == null) || (!entryType.equals("installment") && !entryType.equals("repeating"))) {
//			throw new IllegalArgumentException("Type of entry to get is invalid");
//		}	
//		ArrayList<MultipleEntry> returnList = new ArrayList<MultipleEntry>();
//		for (String tmpDesc : multipleEntryList.keySet()){
//			if (multipleEntryList.get(tmpDesc).getEntryType().equals(entryType)) {
//				MultipleEntry tmpMultipleEntry = (MultipleEntry) multipleEntryList.get(tmpDesc);
//				if (targetMonth != null) {
//					if ((targetMonth.isAfterDate(tmpMultipleEntry.getStartDate()) || targetMonth.equals(tmpMultipleEntry.getStartDate()))  && 
//							(targetMonth.isBeforeDate(tmpMultipleEntry.getEndDate()) || targetMonth.equals(tmpMultipleEntry.getEndDate()))) {
//						returnList.add(tmpMultipleEntry);
//					} 
//				} else {
//					returnList.add(tmpMultipleEntry);
//				}
//			}
//		}
//		return returnList;
//	}

	/**
	 * This returns the number of SingleEntries in a specific date
	 * 
	 * @param the date we are looking for 
	 * @return the number of entries in that date or zero if no entries have been entered for that date
	 */
	public int getNumberOfSingleEntriesInDate(JDateTime searchDate) {
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
	public ArrayList<JDateTime> getSingleEntryDateList() {
		return new ArrayList<JDateTime>(singleEntryList.keySet());
	};

	/**
	 * This returns the number of SingleEntries in the entire datedEntryList
	 * 
	 * @return the number of entries in this month
	 */
	public int singleEntrySize() {
		int totalSize = 0;
		//keySet returns a set of all the keys in the TreeMap
		for(JDateTime tmpDate : singleEntryList.keySet()){
			totalSize = totalSize + singleEntryList.get(tmpDate).size();
		}
		return totalSize;
	}
}
