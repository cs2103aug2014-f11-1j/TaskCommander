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

	/**
	 * Array contains the state of the tasks ArrayList before the last execution of a user command.
	 */
	public ArrayList<Task> tasksHistory;
	
	/**
	 * Array contains all the deleted tasks, needed by the GoogleAPI.
	 */
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
	 * @param 	startDate    
	 * @param 	endDate       
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
	 * Updates a TimedTask, DeadlineTask or FloatingTask with the given index 
	 * and replaces the old taskName, startDate or endDate respectively and 
	 * changes the taskType if needed.
	 * If a given date or name parameter equals null, the old value remains.
	 * 
	 * @param index        index of the task to delete, as a string
	 * @param taskName     description of task
	 * @param startDate    
	 * @param endDate      
	 * @return             feedback for UI
	 */
	public Feedback updateToTimedTask(int index, String name, Date startDate, Date endDate) {
		if (tasks.isEmpty()) {
			return new Feedback(false, String.format(Global.MESSAGE_EMPTY));
		} 

		if (index > tasks.size() - Global.INDEX_OFFSET) {
			return new Feedback(false, String.format(Global.MESSAGE_NO_INDEX, index));
		}
		if  (tasks.get(index).getType() != Task.TaskType.TIMED) {
			TimedTask timedTask = new TimedTask(name,startDate,endDate);
			timedTask.setEdited(tasks.get(index).getEdited());
			timedTask.setDone(tasks.get(index).isDone());
			deleteTask(index);
			tasks.add(index, timedTask);
			return new Feedback(true, Global.CommandType.UPDATE, timedTask);
		} else {
			TimedTask timedTask = (TimedTask) tasks.get(index);
			if (name != null) {
				timedTask.setName(name);
			}
			if (startDate != null) {
				timedTask.setStartDate(startDate);
			}
			if (endDate != null) {
				timedTask.setEndDate(endDate);
			}
			timedTask.setEdited(true);
			return new Feedback(true, Global.CommandType.UPDATE, timedTask);
		}
		
	}
	
	public Feedback updateToDeadlineTask(int index, String name, Date endDate) {
		if (tasks.isEmpty()) {
			return new Feedback(false, String.format(Global.MESSAGE_EMPTY));
		} 

		if (index > tasks.size() - Global.INDEX_OFFSET) {
			return new Feedback(false, String.format(Global.MESSAGE_NO_INDEX, index));
		}

		if  (tasks.get(index).getType() != Task.TaskType.DEADLINE) {
			DeadlineTask deadlineTask = new DeadlineTask(name,endDate);
			deadlineTask.setEdited(tasks.get(index).getEdited());
			deadlineTask.setDone(tasks.get(index).isDone());
			deleteTask(index);
			tasks.add(index, deadlineTask);
			return new Feedback(true, Global.CommandType.UPDATE, deadlineTask);
		} else {
			DeadlineTask deadlineTask = (DeadlineTask) tasks.get(index);
			if (name != null) {
				deadlineTask.setName(name);
			}
			if (endDate != null) {
				deadlineTask.setEndDate(endDate);
			}
			deadlineTask.setEdited(true);
			return new Feedback(true, Global.CommandType.UPDATE, deadlineTask);
		}
	}
	
	public Feedback updateToFloatingTask(int index, String name) {
		if (tasks.isEmpty()) {
			return new Feedback(false, String.format(Global.MESSAGE_EMPTY));
		} 

		if (index > tasks.size() - Global.INDEX_OFFSET || index < 0 ) {
			return new Feedback(false, String.format(Global.MESSAGE_NO_INDEX, index));
		}
		
		if  (tasks.get(index).getType() != Task.TaskType.FLOATING) {
			FloatingTask floatingTask = new FloatingTask(name);
			floatingTask.setEdited(tasks.get(index).getEdited());
			floatingTask.setDone(tasks.get(index).isDone());
			deleteTask(index);
			tasks.add(index, floatingTask);
			return new Feedback(true, Global.CommandType.UPDATE, floatingTask);
		} else {
			FloatingTask floatingTask = (FloatingTask) tasks.get(index);
			if (name != null) {
				floatingTask.setName(name);
			}
			floatingTask.setEdited(true);
			return new Feedback(true, Global.CommandType.UPDATE, floatingTask);
		}
	}

	/**
	 * Deletes the task with the given index (as shown with 'display' command).
	 * Does not execute if there are no lines and if a wrong index is given.
	 * Eg: Index out of bounds or given a char instead of int.
	 * 
	 * @param index        Index of the task to delete, as a string. 
	 * @return             Feedback for user.
	 */
	public Feedback deleteTask(int index) {
		if (tasks.isEmpty()) {
			return new Feedback(false,String.format(Global.MESSAGE_EMPTY));
		} 

		if (index > tasks.size() - Global.INDEX_OFFSET || index < 0 ) {
			return new Feedback(false,String.format(Global.MESSAGE_NO_INDEX, index));
		} else {
			Task deletedTask = tasks.get(index);
			deletedTasks.add(deletedTask);
			tasks.remove(index);

			return new Feedback(true, Global.CommandType.DELETE, deletedTask);
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
	/*
		if (tasks.isEmpty()) {
			return new Feedback(false,String.format(Global.MESSAGE_EMPTY));
		} else {
			Collections.sort(tasks);
			return new Feedback(false,String.format(Global.MESSAGE_SORTED));
		}
	*/
		return new Feedback(false,"Out of order");
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
	
	/**
	 * Returns index of the given task object within the tasks ArrayList.
	 * @return  index
	 */
	public int getIndexOf(Task task) {
		return tasks.indexOf(task);
	}
}
