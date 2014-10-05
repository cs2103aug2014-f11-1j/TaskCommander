package com.taskcommander;
import com.google.api.client.util.DateTime;
import com.taskcommander.Global.TaskType;

/*
 * A floating task that has no deadline. 
 * Has a name. May also hold a Google API ID.
 * Related Google API: Tasks
 * 
 * @author Michelle Tan, ANDREAS, Sean Saito
 */

public class FloatingTask extends Task {
	public FloatingTask(String name){
		super(name, TaskType.FLOATING);
	}
	
	public FloatingTask(String name, String googleID) {
		super(name, TaskType.FLOATING);
		this.setID(googleID);
	}
	
	/*
	 * Compares the name of this task to the given task, using
	 * alphabetical order of the names.
	 * Edit by Sean Saito
	 */
	@Override
	public int compareTo(Task otherTask) {
		return (_name.compareTo(otherTask.getName()));
	}
}
