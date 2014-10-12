package com.taskcommander;
import java.util.Date;

import com.google.gson.annotations.SerializedName;
//import com.taskcommander.Global.TaskType;

//@author A0112828H
/*
 * A basic task object.
 * Has a name. May also hold a Google API ID.
 * Related Google API: Tasks
 */

public abstract class Task {
	@SerializedName("taskType") // For use with GSON
	private TaskType _taskType;
	private String _name;
	private String _id;
	private boolean _done;
	private boolean _edited;
	private Date _updated;
	
	
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
	
	public boolean isEdited() {
		return _edited;
	}
	
	public Date getUpdated() {
		return _updated;
	}
	
	// Returns true if task has a google id and has not been edited since last sync
	public boolean isSynced() {
		return _id != null && !_edited; 
	}
	
	public void setType(TaskType taskType) {
		_taskType = taskType;
	}
	
	public void setName(String name) {
		_name = name;
	}
	
	public void setId(String id) {
		_id = id;
	}
	
	public void setDone(boolean done) {
		_done = done;
	}
	
	public void setEdited(boolean edited) {
		this._edited = edited;
	}
	
	public void setUpdated(Date updated) {
		_updated = updated;
	}

	public void markDone() {
		_done = true;
	}
	
	public void markOpen() {
		_done = false;
	}
	
	public void markSynced() {
		_edited = false;
	}

	// A0128620M
	@Override
	public boolean equals(Object otherObject) {
		if (otherObject == null) {
			return false;
		}
		if (!(otherObject instanceof Task)) {
			return false;
		}
		switch ( this.getType()) {
			case FLOATING:
				return ((FloatingTask) this).equals(otherObject);
			case DEADLINE:
				return ((DeadlineTask) this).equals(otherObject);
			case TIMED:
				return ((TimedTask) this).equals(otherObject);
			default:
				return false;
		}
	}
}
