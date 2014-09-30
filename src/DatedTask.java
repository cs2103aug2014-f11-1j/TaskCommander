import com.google.api.client.util.DateTime;

/*
 * A task that has a deadline.
 * Has a name. May also hold a Google API ID.
 * Related Google API: Tasks
 * 
 * @author Michelle Tan
 */

public class DatedTask extends Task {
	private DateTime _endDate;
	
	public DatedTask(String name, DateTime endTime){
		super(name);
		_endDate = endTime;
	}
	
	public DateTime getEndDate() {
		return _endDate;
	}
	
	public void setEndDate(DateTime endDate) {
		_endDate = endDate;
	}
}
