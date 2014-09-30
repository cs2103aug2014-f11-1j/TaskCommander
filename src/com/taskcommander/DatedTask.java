package com.taskcommander;
import com.google.api.client.util.DateTime;

/*
 * A task that has a name and a deadline.
 * May also hold a Google API ID.
 * Related Google API: Tasks
 * 
 * @author Michelle Tan
 */

public class DatedTask extends Task {
	private DateTime _endDate;

	/*
	 * Creates a new DatedTask with given name and end time.
	 * Throws IllegalArgumentException if null arguments are given.
	 */
	public DatedTask(String name, DateTime endTime){
		super(name);
		if (endTime != null) {
			_endDate = endTime;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public DateTime getEndDate() {
		return _endDate;
	}

	public void setEndDate(DateTime endDate) {
		_endDate = endDate;
	}
}
