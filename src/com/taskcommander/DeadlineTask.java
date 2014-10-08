package com.taskcommander;
import java.util.Date;

import com.google.api.client.util.DateTime;
import com.taskcommander.Task.TaskType;

/*
 * A task that has a name and a deadline.
 * May also hold a Google API ID.
 * Related Google API: Tasks
 * 
 * @author Michelle Tan, ANDREAS, Sean Saito
 */

public class DeadlineTask extends Task {
	private Date _endDate;	// Remark by Andi: Format yyyy-mm-ddTHH:MM:ss

	/*
	 * Creates a new DatedTask with given name and end time.
	 * Throws IllegalArgumentException if null arguments are given.
	 */
	public DeadlineTask(String name, Date endTime){
		super(name,TaskType.DEADLINE);
		if (endTime != null) {
			_endDate = endTime;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public Date getEndDate() {
		return _endDate;
	}

	public void setEndDate(Date endDate) {
		_endDate = endDate;
	}
	
	/*
	 * Compares the deadline of this task to the given task in a 
	 * chronological manner.
	 * Edit by Sean Saito
	 */
	@Override
	public int compareTo(DeadlineTask otherTask) {
		return (_endDate.compareTo(otherTask.getEndDate()));
	}
}
