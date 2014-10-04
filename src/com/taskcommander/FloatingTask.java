package com.taskcommander;
import com.google.api.client.util.DateTime;
import com.taskcommander.Global.TaskType;

/*
 * A floating task that has no deadline. 
 * Has a name. May also hold a Google API ID.
 * Related Google API: Tasks
 * 
 * @author Michelle Tan, ANDREAS 
 */

public class FloatingTask extends Task {
	public FloatingTask(String name){
		super(name, TaskType.FLOATING);
	}
}
