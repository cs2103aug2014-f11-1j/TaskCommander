package com.taskcommander;

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
		this.setId(googleID);
	}
}
