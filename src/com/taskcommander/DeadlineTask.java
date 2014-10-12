package com.taskcommander;
import java.util.Date;

/*
 * A task that has a name and a deadline.
 * May also hold a Google API ID.
 * Related Google API: Tasks
 * 
 * @author Michelle Tan, ANDREAS, Sean Saito
 */

public class DeadlineTask extends DatedTask {
	private Date _endDate;	// Remark by Andi: Format yyyy-mm-ddTHH:MM:ss

	/*
	 * Creates a new DatedTask with given name and end time.
	 * Throws IllegalArgumentException if null arguments are given.
	 */
	public DeadlineTask(String name, Date endTime){
		super(name,TaskType.DEADLINE, endTime);
		if (endTime != null) {
			_endDate = endTime;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	// Constructor for Cloning
	// A0128620M
	public DeadlineTask(DeadlineTask otherDeadlineTask){
		super(otherDeadlineTask.getName(),TaskType.DEADLINE, otherDeadlineTask.getEndDate());
		_endDate = otherDeadlineTask.getEndDate();
		this.setId(otherDeadlineTask.getId());
		this.setDone(otherDeadlineTask.getDone());
		this.setEdited(otherDeadlineTask.getEdited());
	}

	public Date getEndDate() {
		return _endDate;
	}

	public void setEndDate(Date endDate) {
		_endDate = endDate;
	}
	
	@Override
	// A0128620M
	public int compareTo(DeadlineTask otherTask) {
		return (_endDate.compareTo(otherTask.getEndDate()));
	}
	
	@Override
	// A0128620M
	public boolean equals(Object otherObject) {
		if (otherObject == null) {
			return false;
		}
		if (!(otherObject instanceof DeadlineTask)) {
			return false;
		}
		DeadlineTask otherDeadlineTask = (DeadlineTask) otherObject;

		if (this.getType().equals(otherDeadlineTask.getType()) && this.getName().equals(otherDeadlineTask.getName()) && this.getId() == otherDeadlineTask.getId() && this.getEndDate().equals(otherDeadlineTask.getEndDate())) {
			return true;
		} else {
			return false;
		}
	}
}
