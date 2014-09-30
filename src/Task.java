import java.util.List;
import java.util.Date;

import com.google.api.client.util.DateTime;

/*
 * A basic task object.
 * Has a name. May also hold a Google API ID.
 * Related Google API: Tasks
 * 
 * @author Michelle Tan
 */

public class Task {
	private TaskType _taskType;
	private String _name;
	private String _id;
	
	public enum TaskType {
		FLOATING, TIMED, DATED
	}
	
	public Task(String name){
		_name = name;
		_taskType = TaskType.FLOATING;
	}
	
	public TaskType getType() {
		return _taskType;
	}
	
	public String getName() {
		return _name;
	}
	
	public void setName(String name) {
		_name = name;
	}
	
	public void setID(String id) {
		_id = id;
	}
}
