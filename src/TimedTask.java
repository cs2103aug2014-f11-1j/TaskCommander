import com.google.api.client.util.DateTime;

/*
 * A task that has a start and an end date.
 * Has a name. May also hold a Google API ID.
 * Related Google API: Calendar
 * 
 * @author Michelle Tan
 */

public class TimedTask extends Task {
	private DateTime _startDate;
	private DateTime _endDate;
	
	public TimedTask(String name, DateTime startTime, DateTime endTime){
		super(name);
		_startDate = startTime;
		_endDate = endTime;
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
