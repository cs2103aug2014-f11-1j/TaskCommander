package com.taskcommander;
import java.util.Date;

/*
 * A task that has a name, a start date and an end date.
 * May also hold a Google API ID.
 * Related Google API: Calendar
 * 
 * @author Michelle Tan, ANDREAS, Sean Saito
 */

public class TimedTask extends DatedTask {
	private Date _startDate;
	private Date _endDate;
	
	/*
	 * Creates a new TimedTask with given name, start time and end time.
	 * Throws IllegalArgumentException if null arguments are given.
	 */
	public TimedTask(String name, Date startTime, Date endTime){
		super(name, TaskType.TIMED, startTime );
		if (startTime != null && endTime !=null) {
			_startDate = startTime;
			_endDate = endTime;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	// Constructor for Cloning
	public TimedTask(TimedTask otherTimedTask){
		super(otherTimedTask.getName(), TaskType.TIMED, otherTimedTask.getStartDate() );
			_startDate = otherTimedTask.getStartDate();
			_endDate = otherTimedTask.getEndDate();
			this.setId(otherTimedTask.getId());
			this.setDone(otherTimedTask.isDone());
			this.setEdited(otherTimedTask.isEdited());
	}
	
	public Date getStartDate() {
		return _startDate;
	}
	
	public Date getEndDate() {
		return _endDate;
	}
	
	public void setStartDate(Date startDate) {
		_startDate = startDate;
	}
	
	public void setEndDate(Date endDate) {
		_endDate = endDate;
	}
	

	@Override
	public int compareTo(TimedTask otherTask) {
		return (_startDate.compareTo(otherTask.getStartDate()));
	}
	
	@Override
	public boolean equals(Object otherObject) {
		if (otherObject == null) {
			return false;
		}
		if (!(otherObject instanceof TimedTask)) {
			return false;
		}
		TimedTask otherTimedTask = (TimedTask) otherObject;

		if (this.getType().equals(otherTimedTask.getType()) && this.getName().equals(otherTimedTask.getName()) && this.getId() == otherTimedTask.getId() && this.getStartDate().equals(otherTimedTask.getStartDate()) && this.getEndDate().equals(otherTimedTask.getEndDate())) {
			return true;
		} else {
			return false;
		}
	}
}
