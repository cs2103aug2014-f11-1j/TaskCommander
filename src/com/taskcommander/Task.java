package com.taskcommander;
import com.google.gson.annotations.SerializedName;
//import com.taskcommander.Global.TaskType;

//@author A0112828H
/*
 * A basic task object.
 * Has a name. May also hold a Google API ID.
 * Related Google API: Tasks
 */

public class Task {
	@SerializedName("taskType") // For use with GSON
	private TaskType _taskType;
	private String _name;
	private String _id;
	private boolean _done;
	private boolean _edited;
	
	
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
			_edited = true;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void setType(TaskType taskType) {
		_taskType = taskType;
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
	
	public void setDone(boolean done) {
		_done = done;
	}
	
	// Returns true if task has a google id and has not been edited since last sync
	public boolean isSynced() {
		return _id != null && !_edited; 
	}

	public void setName(String name) {
		_name = name;
	}

	public void setId(String id) {
		_id = id;
	}
	
	public boolean getDone() {
		return _done;
	}
	
	public void markDone() {
		_done = true;
	}
	
	public void markUndone() {
		_done = false;
	}
	
	public void setSynced() {
		_edited = false;
	}
	
	public boolean getEdited() {
		return _edited;
	}
	
	public void setEdited(boolean edited) {
		this._edited = edited;
	}

	public int compareTo(TimedTask otherTask) {
		// TODO Auto-generated method stub
		return 0;
	}

}
