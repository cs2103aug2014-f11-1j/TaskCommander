package com.taskcommander;
import com.google.api.client.util.DateTime;

/*
 * A task that has a name, a start date and an end date.
 * May also hold a Google API ID.
 * Related Google API: Calendar
 * 
 * @author Michelle Tan
 */

public class TimedTask extends Task {
	private DateTime _startDate;
	private DateTime _endDate;
	
	/*
	 * Creates a new TimedTask with given name, start time and end time.
	 * Throws IllegalArgumentException if null arguments are given.
	 */
	public TimedTask(String name, DateTime startTime, DateTime endTime){
		super(name);
		if (startTime != null && endTime !=null) {
		_startDate = startTime;
		_endDate = endTime;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public DateTime getStartDate() {
		return _startDate;
	}
	
	public DateTime getEndDate() {
		return _endDate;
	}
	
	public void setStartDate(DateTime startDate) {
		_startDate = startDate;
	}
	
	public void setEndDate(DateTime endDate) {
		_endDate = endDate;
	}
}
