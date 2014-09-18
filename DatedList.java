package budget_program;

import java.util.*;

import jodd.datetime.JDateTime;

/**
* The DatedList class consists of a HashMap of days indexed by date (for a single month)
* and each day contains an ArrayList of single entries
* @author Asa Swain
*/

public class DatedList {	
	// use date as key in HashMap to return an arraylist of entries for that date
	private HashMap<JDateTime, DaysEntries> listDatedEntries;

	/**
	 * This is a blank constructor
	 */
	public DatedList() {
		listDatedEntries = new HashMap<JDateTime, DaysEntries>();
	}
	
	/**
	 * This is a constructor for a single entry
	 * 
	 * @param newEntry  the entry to add to the HashMap
	 */
	public DatedList(SingleEntry newEntry) {
		listDatedEntries = new HashMap<JDateTime, DaysEntries>();
		addEntry(newEntry);
	}
	
	/**
	 * This adds a single entry onto the list of entries for this month
	 * 
	 * @param newEntry  the new SingleEntry to add onto this day 
	 */
	public void addEntry(SingleEntry newEntry) {
		DaysEntries tempDaysEntries;
		if (listDatedEntries.containsKey(newEntry.getDate())) {
			tempDaysEntries = listDatedEntries.get(newEntry.getDate());
		} else {
			tempDaysEntries = new DaysEntries(newEntry.getDate());
		}
		try {
		tempDaysEntries.addEntry(newEntry);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
		listDatedEntries.put(newEntry.getDate(), tempDaysEntries);
	}
	
	/**
	 * This removes an entry from the list of entries for this month (making sure it's there first)
	 *
	 * @param targetDate - date of entry to delete
	 * @param targetIndex - index of entry to delete
	 * @exception IllegalArgumentException - if exception occurs in deleteEntry command
	 */
	public void deleteEntry(JDateTime targetDate, int targetIndex) {
		DaysEntries tempDaysEntries = listDatedEntries.get(targetDate);
		try {
			tempDaysEntries.deleteEntry(targetIndex);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
		listDatedEntries.put(targetDate,tempDaysEntries);
	}
	
	/**
	 * This removes the old SingleEntry and adds the new SingleEntry from the list of entries for this month
	 *
	 * @param oldEntry  the old SingleEntry to remove
	 * @param newEntry  the new SingleEntry to add
	 * @exception IllegalArgumentException - if the oldEntry does not exist on this day
	 */
	public void updateEntry(SingleEntry oldEntry, SingleEntry newEntry) {
		// delete old entry
		try {
			updateEntry(oldEntry, newEntry);
		} catch (IllegalArgumentException e){
			throw new IllegalArgumentException(e);
		}
		// add new entry
		addEntry(newEntry);
	}
	
	/**
	 * This checks if a single entry is in the list of entries for this month
	 * 
	 * @param testEntry - the entry we are searching for
	 * @return true if this entry is in the month then return true, else return false
	 */
	public boolean isEntryInTheList(SingleEntry testEntry) {
		DaysEntries tempDaysEntries = listDatedEntries.get(testEntry.getDate());	
		return tempDaysEntries.isInDaysEntries(testEntry);
	}
	
	/**
	 * This returns the nth SingleEntry on a specific date
	 * 
	 * @param date  the date to get the entry from
	 * @param loc  the index of the entry to get on that date
	 * @return a SingleEntry object
	 */
	public SingleEntry getEntry(JDateTime date, int loc) {
		DaysEntries targetDaysEntries = listDatedEntries.get(date);
		return targetDaysEntries.getEntry(loc);
	}
	
	/**
	 * This returns the number of SingleEntries in all the days in the month
	 * 
	 * @return the number of entries in this month
	 */
	public int size() {
		int totalSize = 0;
		//keySet returns a set of all the keys in the HashMap
		for(JDateTime tmpDate : listDatedEntries.keySet()){
			totalSize = totalSize + listDatedEntries.get(tmpDate).size();
		}
		return totalSize;
	}

	/**
	 * This returns the number of SingleEntries in a specific date
	 * 
	 * @param the date we are looking for 
	 * @return the number of entries in that date
	 */
	public int getNumberOfEntriesInDate(JDateTime searchDate) {
		return listDatedEntries.get(searchDate).size();
	}
	
	/**
	 * This prints all the single entries in the entire month
	 */
	public void printAllEntries() {
		//keySet returns a set of all the keys in the HashMap
		for(JDateTime tmpDate : listDatedEntries.keySet()){
			printDaysEntries(tmpDate);
		}
	}
	
	/**
	 * This prints all the single entries on a specific date
	 * 
	 * @param date  the date to print all the entries for
	 */
	public void printDaysEntries(JDateTime date) {
		DaysEntries targetDaysEntries = listDatedEntries.get(date);
		
		int listSize = targetDaysEntries.size();
		for (int i = 0; i < listSize; i++) {
			targetDaysEntries.printListItem(i); 
		}
	}
	
	/**
	 * This returns a list of all the SingleEntries for the entire month
	 * @return an ArrayList of all the SingleEntries for the entire month
	 */
	public ArrayList<SingleEntry> getRawSingleEntryList() {
		ArrayList<SingleEntry> masterList = new ArrayList<SingleEntry>();

		//keySet returns a set of all the keys in the HashMap
		for(JDateTime tmpDate : listDatedEntries.keySet()){
			ArrayList<SingleEntry> tmpList = listDatedEntries.get(tmpDate).getRawSingleEntryList();
			masterList.addAll(tmpList);
		}
		
/*		(old code)
		// get a list of SingleEntruies for each month in monthlyData
		Set<JDateTime> listDays = listDatedEntries.keySet();
		Iterator<JDateTime> iterator = listDays.iterator();
		while(iterator.hasNext()) {
			JDateTime setDate = iterator.next();
			ArrayList<SingleEntry> tmpList = listDatedEntries.get(setDate).getRawSingleEntryList();
			masterList.addAll(tmpList);
		}*/

		return masterList;
	};
	
	/** 
	 * This private class stores an ArrayList of single entries for one date
	 */
	private class DaysEntries implements Comparable<DaysEntries> {
		// a list of income/expense entries
		private ArrayList<SingleEntry> datedEntryList;
		// the date for this list of entries (all entries on this list should have this date)
		private JDateTime date;

		/**
		 * This is a blank constructor, if no date is passed in, use today's date
		 */
		public DaysEntries() {
			date = new JDateTime(); // current date and time
			datedEntryList = new ArrayList<SingleEntry>();
		}
		
		/**
		 * This is a constructor to create a list of entries for a specific date
		 * 
		 * @param date  the date for this list of entries
		 */
		public DaysEntries(JDateTime date) {
			this.date = date; 
			datedEntryList = new ArrayList<SingleEntry>();
		}

		/**
		 * This adds a new SingleEntry onto end of list of entries for this day
		 * 
		 * @param newEntry  the entry to add onto this day's list
		 * @throws IllegalArgumentException  if the date of the SingleEntry you are adding to this DaysEntries object
		 * doesn't match the date of this DaysEntries object
		 */
		public void addEntry(SingleEntry newEntry) {
			if (newEntry.getDate().equals(date)) {
				datedEntryList.add(newEntry);
			} else {
				System.out.println("database date = " + date);
				System.out.println("entry date = " + newEntry.getDate());
				throw new IllegalArgumentException("Date doesn't match date for this day of entries: " + date);
			}
		}

		/**
		 * This deletes a single entry from this list
		 * 
		 * @param delEntry  the entry to remove from this day's list
		 * @throws IllegalArgumentException  if the list for this date does not contain the delEntry object 
		 */
		public void deleteEntry(SingleEntry delEntry) {
			if (datedEntryList.contains(delEntry)) {
			   datedEntryList.remove(delEntry);
			} else {
				throw new IllegalArgumentException("The list of entries for this date: " + date + " doesn't have the entry you are trying to delete");
			}
		}
		
		/**
		 * This deletes the Nth entry from this list
		 * 
		 * @param index  the index of the entry to remove from this day's list
		 * @throws IllegalArgumentException  if the list for this date does not at least "index" number of items in it
		 */
		public void deleteEntry(int index) {
			if (datedEntryList.size() > index) {
			datedEntryList.remove(index);
			} else {
				throw new IllegalArgumentException("You are trying to delete entry number " + index + " from the list of entries date: " + date + " but this date only has " + datedEntryList.size() +" entries.");
			}
		}

		/**
		 * This replaces an old version of a SingleEntry with a new version
		 * 
		 * @param oldEntry  the old SingleEntry object to remove
		 * @param newEntry  the new SingleEntry object to add
		 * @throws IllegalArgumentException  if the list for this date does not contain the oldEntry object
		 */
		public void updateEntry(SingleEntry oldEntry, SingleEntry newEntry) {
			int loc = datedEntryList.indexOf(oldEntry);
			if (loc != -1) {
				datedEntryList.set(loc, newEntry);
			} else  {
				throw new IllegalArgumentException("Date " + date + "does not contain entry you are trying to update");
			}
		}
		
		/**
		 * This replaces the Nth entry in the list with a new version
		 * 
		 * @param index  the index of the SingleEntry object to overwrite
		 * @param newEntry  the new SingleEntry object to add
		 * @throws IllegalArgumentException  if the list for this date does not at least "index" number of items in it
		 */
		public void updateEntry(int index, SingleEntry newEntry) {
			if (datedEntryList.size() > index) {
				datedEntryList.set(index, newEntry);
			} else  {
				throw new IllegalArgumentException("Cannot update item number " + index + " in list of entries for date: " + date + ". It only contains " + datedEntryList.size() + "items.");
			}
		}

		/**
		 * This returns the Nth entry in the list
		 * @param index  the index of the SingleEntry object to return
		 * @returns one entry from this day
		 * @throws  IllegalArgumentException  if the list for this date does not at least "index" number of items in it
		 */
		
		public SingleEntry getEntry(int index) {
			if (datedEntryList.size() > index) {
				return datedEntryList.get(index);
			} else  {
				throw new IllegalArgumentException("Cannot get item number " + index + " in list of entries for date: " + date + ". It only contains " + datedEntryList.size() + "items.");
			}
		}

		/**
		 * This returns the date for this object
		 * 
		 * @returns date of this list of entries
		 */
		public JDateTime getDate() {
			return date;
		}

		/**
		 * This returns the number of entries in this object
		 * 
		 * @returns the number of entries in this day
		 */
		public int size() {
			return datedEntryList.size();
		}

		/**
		 * This prints the Nth entry in the list
		 * 
		 * @param index  the index of the SingleEntry object to print
		 * @throws IllegalArgumentException if the list for this date does not at least "index" number of items in it
		 */
		public void printListItem(int index) {
			if (datedEntryList.size() >= index) {
				datedEntryList.get(index).printEntry();
			} else  {
				throw new IllegalArgumentException("Cannot print item number " + index + " in list of entries for date: " + date + ". It only contains " + datedEntryList.size() + "items.");
			}
		}
		
		/**
		 * This checks if this day contains a specific entry
		 * 
		 * @param newEntry  the new SingleEntry object to search for
		 * @returns true/false (if this day contains the entry)
		 */
		public boolean isInDaysEntries(SingleEntry testEntry) {
			return datedEntryList.contains(testEntry);
		}
		
		/**
		 * This returns an ArrayList of all the SingleEntries in this day
		 * 
		 * @returns a list of all the SingleEntries in this day
		 */
		public ArrayList<SingleEntry> getRawSingleEntryList() {
			return datedEntryList;
		}
		
		//to do: show data for a specific month
		//to do: show date for a date range

		/**
		 * This compare two entries and returns which is greater
		 * 
		 * @param  other objects to compare this object to
		 * @returns Returns a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object
		 */
		public int compareTo(DaysEntries other ) {
			return date.compareTo(other.getDate());	
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((date == null) ? 0 : date.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof DaysEntries))
				return false;
			final DaysEntries other = (DaysEntries) obj;
			if (date == null) {
				if (other.date != null)
					return false;
			} else if (!date.equals(other.date))
				return false;
			return true;
		}
	}
	
}
