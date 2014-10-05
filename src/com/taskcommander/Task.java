package com.taskcommander;
import java.util.List;
import java.util.Date;

import com.google.api.client.util.DateTime;
import com.google.gson.annotations.SerializedName;
//import com.taskcommander.Global.TaskType;

//@author A0112828H
/*
 * A basic task object.
 * Has a name. May also hold a Google API ID.
 * Related Google API: Tasks
 */

public class Task implements Comparable<Task> {
	@SerializedName("taskType") // For use with GSON
	private TaskType _taskType;
	private String _name;
	private String _id;
	private boolean _done;
	
	// Possible task types
	public enum TaskType {
		@SerializedName("0") FLOATING("FloatingTask"), 
		@SerializedName("1") TIMED("TimedTask"), 
		@SerializedName("2") DEADLINE("DeadlineTask");
		
		String name;
	
	    private TaskType(String s) {
	        name = s;
	    }
	    
	    public String toString(){
	        return name;
	     }
	}

	/*
	 * Creates a new Task with given name.
	 * Throws IllegalArgumentException if name is not given.
	 */
	public Task(String name, TaskType taskType){
		if (name != null) {
			_name = name;
			_taskType = taskType;
			_done = false;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void setType(TaskType taskTye) {
		_taskType = taskTye;
	}
	
	public TaskType getType() {
		return _taskType;
	}

	public String getName() {
		return _name;
	}
	
	public String getId() {
		return _id;
	}
	
	public boolean isDone() {
		return _done;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setId(String id) {
		_id = id;
	}
	
	public void markDone() {
		_done = true;
	}
	
	public void markUndone() {
		_done = false;
	}

	//@author Sean Saito
	@Override
	public int compareTo(Task arg0) {
		// TODO @Sean
		return 0;
	}
}
