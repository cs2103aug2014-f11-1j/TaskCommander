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
		save();
		return new Feedback(true, Global.CommandType.ADD, new TimedTask(timedTask));
	}
	
	public Feedback addDeadlineTask(String taskName, Date endDate) {
		DeadlineTask deadlineTask= new DeadlineTask(taskName,endDate);
		tasks.add(deadlineTask);
		save();
		return new Feedback(true, Global.CommandType.ADD, new DeadlineTask(deadlineTask));
	}
	
	public Feedback addFloatingTask(String taskName) {
		FloatingTask floatingTask = new FloatingTask(taskName);
		tasks.add(floatingTask);
		save();
		return new Feedback(true, Global.CommandType.ADD, new FloatingTask(floatingTask));
	}

	/**
	 * Displays the tasks by forwarding all the needed information to the UI.
	 * 
	 * @param 	taskName     
	 * @return 	feedback for UI
	 */
	public Feedback displayTasks() {
		ArrayList<Task> displayedTasks = new ArrayList<Task>();
		
		ArrayList<FloatingTask> displayedFloatingTasks = new ArrayList<FloatingTask>();
		 for(Task task: tasks) {
			 if(task.getType() == Task.TaskType.FLOATING) {
				 displayedFloatingTasks.add((FloatingTask) task);	// TODO: use cloned task with "new FloatingTask((FloatingTask)" to  add a copy of the respective task, not the original
				 Collections.sort(displayedFloatingTasks);
			 }
		 }
		 displayedTasks.addAll(displayedFloatingTasks);
		 
		ArrayList<DatedTask> displayedDatedTasks = new ArrayList<DatedTask>();
		for (Task task : tasks) {
			if (task.getType() == Task.TaskType.DEADLINE) {
				displayedDatedTasks.add((DeadlineTask) task);
			} else if (task.getType() == Task.TaskType.TIMED) {
				displayedDatedTasks.add((TimedTask) task);
			}
			Collections.sort(displayedDatedTasks);
		}
		displayedTasks.addAll(displayedDatedTasks);
		
		return new Feedback(true, Global.CommandType.DISPLAY, displayedTasks);
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
			save();
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
			save();
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
			save();
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
			save();
			return new Feedback(true, Global.CommandType.UPDATE, deadlineTask); // New cloning approach: new DeadlineTask(deadlineTask) for less coupling, but then issues in update feature with equals-method
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
			save();
			return new Feedback(true, Global.CommandType.UPDATE, floatingTask);
		} else {
			FloatingTask floatingTask = (FloatingTask) tasks.get(index);
			if (name != null) {
				floatingTask.setName(name);
			}
			floatingTask.setEdited(true);
			save();
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
			save();
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
		save();
		return new Feedback(true,Global.CommandType.CLEAR);
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
