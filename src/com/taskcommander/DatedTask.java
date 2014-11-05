package com.taskcommander;
import java.util.Date;

//@author A0128620M
/**
 * A task with a date. Compares DatedTasks by date in chronological order.
 */
public abstract class DatedTask extends Task implements Comparable<DatedTask>{
	private Date _date;

	/**
	 * Creates a new DatedTask with given name, task type and date.
	 * Throws IllegalArgumentException if null arguments are given.
	 */
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
	
	/**
	 * Compares the date of this task to the given task 
	 * in chronological order.
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
