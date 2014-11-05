package com.taskcommander;


/**
 * A floating task that has no deadline. 
 * Has a name. May also hold a Google API ID.
 * Related Google API: Tasks
 */
public class FloatingTask extends Task implements Comparable<FloatingTask>{
	/**
	 * Creates a new FloatingTask.
	 */
	public FloatingTask(String name){
		super(name, TaskType.FLOATING);
	}
	
	/**
	 * Creates a new FloatingTask with given name and a Google ID.
	 */
	public FloatingTask(String name, String googleID) {
		super(name, TaskType.FLOATING);
		this.setId(googleID);
	}
	
	//@author A0128620M, A0109194A
	/**
	 * Creates a new FloatingTask from a given FloatingTask.
	 * Used for cloning.
	 */
	public FloatingTask(FloatingTask otherFloatingTask) {
		super(otherFloatingTask.getName(), TaskType.FLOATING);
		this.setId(otherFloatingTask.getId());
		this.setDone(otherFloatingTask.isDone());
		this.setEdited(otherFloatingTask.isEdited());
	}
	
	// A0128620M
	/**
	 * Compares the name of this task to the given task 
	 * in alphabetical order.
	 */
	@Override
	public int compareTo(FloatingTask otherTask) {
		return (getName().compareTo(otherTask.getName()));
	}
	
	@Override
	public boolean equals(Object otherObject) {
		if (otherObject == null) {
			return false;
		}
		if (!(otherObject instanceof FloatingTask)) {
			return false;
		}
		FloatingTask otherFloatingTask = (FloatingTask) otherObject;
		if (this.getType().equals(otherFloatingTask.getType()) && 
			this.getName().equals(otherFloatingTask.getName()) && 
			this.getId() == otherFloatingTask.getId()) {	
			return true;
		} else {
			return false;
		}
	}
}
