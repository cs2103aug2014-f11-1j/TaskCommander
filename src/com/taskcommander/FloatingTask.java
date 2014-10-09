package com.taskcommander;

/*
 * A floating task that has no deadline. 
 * Has a name. May also hold a Google API ID.
 * Related Google API: Tasks
 * 
 * @author Michelle Tan, ANDREAS, Sean Saito
 */

public class FloatingTask extends Task implements Comparable<FloatingTask>{
	public FloatingTask(String name){
		super(name, TaskType.FLOATING);
	}
	
	public FloatingTask(String name, String googleID) {
		super(name, TaskType.FLOATING);
		this.setId(googleID);
	}
	
	// Constructor for Cloning
	public FloatingTask(FloatingTask otherFloatingTask) {
		super(otherFloatingTask.getName(), TaskType.FLOATING);
	}
	
	/*
	 * Compares the name of this task to the given task 
	 * in a alphabetically manner.
	 */
	@Override
	public int compareTo(FloatingTask otherTask) {
		return (getName().compareTo(otherTask.getName()));
	}
}
