package com.taskcommander;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;


/**
 * This class represents the data component and stores the data temporary for manipulation reasons. 
 * At the beginning the content of the permanent storage is pulled to the temporary one. 
 * After each command the data will be pushed to the permanent storage.
 * 
 * @author A0128620M
 */

public class Data {

	/** 
	 * One array containing a list of task objects, one containing the state before
	 * the execution of the last command (needed for the undo-feature) and one containing
	 * all the deleted tasks.
	 */
	public ArrayList<Task> tasks;
	public ArrayList<Task> tasksHistory;
	public ArrayList<Task> deletedTasks;

	private Storage storage;

	/**
	 * Constructor: Creates all necessary objects, including the Storage and loads data from it.
	 */
	public Data() {
		tasks = new ArrayList<Task>();
		tasksHistory = new ArrayList<Task>();
		deletedTasks = new ArrayList<Task>();
		storage = new Storage();
		load();
	}

	/**
	 * Saves to and loads from storage.
	 * @author A0112828H
	 */
	public void save() {
		storage.writeToFile(tasks);
	}
	
	public void load() {
		tasks = storage.readFromFile(); 
	}	

	/**
	 * Adds a TimedTask, DeadlineTask or FloatingTask to the task ArrayList.
	 * 
	 * @param 	taskName     
	 * @return 	feedback for UI
	 */
	public Feedback addTimedTask(String taskName, Date startDate, Date endDate) {
		TimedTask timedTask = new TimedTask(taskName,startDate,endDate);
		tasks.add(timedTask);
		return new Feedback(true, Global.CommandType.ADD, timedTask);
	}
	
	public Feedback addDeadlineTask(String taskName, Date endDate) {
		DeadlineTask deadlineTask= new DeadlineTask(taskName,endDate);
		tasks.add(deadlineTask);
		return new Feedback(true, Global.CommandType.ADD, deadlineTask);
	}
	
	public Feedback addFloatingTask(String taskName) {
		FloatingTask floatingTask = new FloatingTask(taskName);
		tasks.add(floatingTask);
		return new Feedback(true, Global.CommandType.ADD, floatingTask);
	}

	/**
	 * Displays the tasks by forwarding all the needed information to the UI.
	 * 
	 * @param 	taskName     
	 * @return 	feedback for UI
	 */
	public Feedback displayTasks() {
		return new Feedback(true, Global.CommandType.DISPLAY, tasks);
	}
	
	
	
	/**
	 * Updates the task with the given index (as shown by 'display' command) and
	 * replaces the old task description by the new one.
	 * 
	 * @param index        index of the task to delete, as a string
	 * @param taskName     description of task
	 * @return             feedback for UI
	 */
	public Feedback updateTask(int index, String taskName) {	//TODO: implementation needs to be adjusted to different types of tasks
		/*
		if (tasks.isEmpty()) {
			return new Feedback(false, String.format(Global.MESSAGE_EMPTY));
		} 

		if (indexToUpdate > tasks.size() - Global.INDEX_OFFSET) {
			return String.format(Global.MESSAGE_NO_INDEX, index);
		} else {
			tasks.remove(indexToUpdate);
			tasks.add(indexToUpdate, new Task(taskName));

			return String.format(Global.MESSAGE_UPDATED, taskName);
		}
		*/
		return new Feedback(false,"out of order");
	}

	/**
	 * Deletes the task with the given index (as shown with 'display' command).
	 * Does not execute if there are no lines and if a wrong index is given.
	 * Eg: Index out of bounds or given a char instead of int.
	 * 
	 * @param index        Index of the task to delete, as a string. 
	 * @return             Feedback for user.
	 */
	public Feedback deleteTask(String index) {
		if (tasks.isEmpty()) {
			return new Feedback(false,String.format(Global.MESSAGE_EMPTY));
		} 

		int indexToRemove;
		try {
			indexToRemove = Integer.parseInt(index) - Global.INDEX_OFFSET; // Change the line number to an array index
		} catch (NumberFormatException e) {
			return new Feedback(false,String.format(Global.MESSAGE_INVALID_FORMAT, "delete " + index));
		} 

		if (indexToRemove > tasks.size() - Global.INDEX_OFFSET) {
			return new Feedback(false,String.format(Global.MESSAGE_NO_INDEX, index));
		} else {
			Task taskToRemove = tasks.get(indexToRemove);
			deletedTasks.add(taskToRemove);
			tasks.remove(indexToRemove);

			return new Feedback(false,String.format(Global.MESSAGE_DELETED, taskToRemove.getName()));
		}
	}
	
	/**@author A0109194A
	 * Deletes the task directly from the tasks list without the index
	 * Used to delete tasks when syncing
	 * 
	 * @param 			task
	 * @return 			boolean value on whether the delete was successful.
	 */
	public boolean deleteTask(Task task) {
		if (tasks.isEmpty()) {
			return false;
		} else {
			deletedTasks.add(task);
			tasks.remove(task);
			return true;
		}
	}
	
	/**
	 * Clears all tasks from memory.
	 * 
	 * @param userCommand 
	 * @return             Feedback for user.
	 */
	public Feedback clearTasks() {
		deletedTasks.addAll(tasks);
		tasks.clear();
		return new Feedback(true,Global.CommandType.CLEAR);
	}

	/**
	 * Sorts the tasks in memory in alphabetical order.
	 * @return   Feedback for user.
	 */
	public Feedback sort() {
		if (tasks.isEmpty()) {
			return new Feedback(false,String.format(Global.MESSAGE_EMPTY));
		} else {
			Collections.sort(tasks);
			return new Feedback(false,String.format(Global.MESSAGE_SORTED));
		}
	}	
	
	public ArrayList<Task> getAllTasks() {
		return tasks;
	}
	
	public ArrayList<Task> getDeletedTasks() {
		return deletedTasks;
	}
	
	public ArrayList<String> getAllIds() {
		ArrayList<String> idList = new ArrayList<String>();
		for (Task t : tasks) {
			idList.add(t.getId());
		}
		
		return idList;
	}
}
