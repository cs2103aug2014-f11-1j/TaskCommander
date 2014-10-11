package com.taskcommander;
import java.util.Date;

/**
 * A task with a date, the TimedTask and DeadlineTask inherit from;
 * needed to compare TimedTask and DeadlineTasks by their respective 
 * startDate and endDate.
 * 
 * @author A0128620M
 */

public abstract class DatedTask extends Task implements Comparable<DatedTask>{
	private Date _date;
	
	public DatedTask(String name, TaskType taskType, Date date) {
		super(name, taskType);
		if (date != null) {
			_date = date;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public Date getDate() {
		return _date;
	}
	
	public void setDate(Date date) {
		_date = date;
	}
	
	/*
	 * Compares the date of this task to the given task 
	 * in a chronological manner.
	 */
	@Override
	public int compareTo(DatedTask otherTask) {
		return (_date.compareTo(otherTask.getDate()));
	}

	public int compareTo(TimedTask otherTask) {
		return 0;
	}

	public int compareTo(DeadlineTask otherTask) {
		return 0;
	}
	
	@Override
	public boolean equals(Object otherObject) {
		if (otherObject == null) {
			return false;
		}
		if (!(otherObject instanceof DatedTask)) {
			return false;
		}
		switch ( this.getType()) {
			case DEADLINE:
				return ((DeadlineTask) this).equals(otherObject);
			case TIMED:
				return ((TimedTask) this).equals(otherObject);
			default:
				return false;
		}
	}
}
