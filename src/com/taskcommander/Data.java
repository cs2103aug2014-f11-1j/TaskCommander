package com.taskcommander;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import com.taskcommander.Task.TaskType;

/**
 * This class stores temporary data to be manipulated in
 * the Logic component. The data will be pulled from the
 * permanent storage.
 */

//@author Andreas Christian Mayr
public class Data {

	public ArrayList<Task> tasks; // Array containing a list of task objects

	/*
	 * Array containing the history of the commands used since program start.
	 * This array is needed for the undo-feature.
	 */
	public ArrayList<Task> tasksHistory;
	
	public ArrayList<Task> deletedTasks;

	private Storage storage;

	/**
	 * Returns a Data object.
	 * Creates a new Storage and loads data from it.
	 */
	public Data() {
		tasks = new ArrayList<Task>();
		tasksHistory = new ArrayList<Task>();
		deletedTasks = new ArrayList<Task>();
		storage = new Storage();
		load();
	}

	//@author A0112828H
	public void save() {
		storage.writeToFile(tasks);
	}

	public void load() {
		tasks = storage.readFromFile(); 
	}	

	//@author Andreas Christian Mayr
	/**
	 * Adds a task with given name.
	 * 
	 * @param taskName     
	 * @return             Feedback for user.
	 */
	public String addTask(TaskType taskType, String taskName, Date startDate, Date endDate) {
		if (taskName == null || taskName == " ") {
			return Global.MESSAGE_NO_TASK;
		}
		SimpleDateFormat dayFormat = new SimpleDateFormat("EEE MMM d ''yy");
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");

		switch (taskType) {
		case DEADLINE:
			DeadlineTask deadlineTask;
			deadlineTask= new DeadlineTask(taskName,endDate);
			System.out.println(tasks);
			tasks.add(deadlineTask);
			return String.format(Global.MESSAGE_ADDED,"[by "+dayFormat.format(endDate)+" "+timeFormat.format(endDate)+"]"+" \""+taskName+"\"");
		case TIMED:
			TimedTask timedTask;
			timedTask = new TimedTask(taskName,startDate,endDate);
			tasks.add(timedTask);
			return String.format(Global.MESSAGE_ADDED,"["+dayFormat.format(endDate)+" "+timeFormat.format(startDate)+"-"+timeFormat.format(endDate)+"]"+" \""+taskName+"\"");				
		case FLOATING:
			FloatingTask floatingTask;
			floatingTask = new FloatingTask(taskName);
			tasks.add(floatingTask);
			return String.format(Global.MESSAGE_ADDED,"\""+taskName+"\"");
		default:
			return null;
		}
	}

	/**
	 * Updates the task with the given index (as shown with 'display' command) and
	 * replaces the old task description by the new one.
	 * Does not execute if there are no lines and if a wrong index is given.
	 * Eg: Index out of bounds or given a char instead of int.
	 * 
	 * @param index        Index of the task to delete, as a string. 
	 * @param taskName     Description of task. 
	 * @return             Feedback for user.
	 */
	public String updateTask(String index, String taskName) {	// implementation needs to be adjusted to new parser
		/*
		if (tasks.isEmpty()) {
			return String.format(Global.MESSAGE_EMPTY);
		} else if (index == null) {
			return Global.MESSAGE_NO_TASK;
		}

		int indexToUpdate;
		try {
			indexToUpdate = Integer.parseInt(index) - Global.INDEX_OFFSET; // Change the line number to an array index
		} catch (NumberFormatException e) {
			return String.format(Global.MESSAGE_INVALID_FORMAT, "update " + index + taskName);
		} 

		if (indexToUpdate > tasks.size() - Global.INDEX_OFFSET) {
			return String.format(Global.MESSAGE_NO_INDEX, index);
		} else {
			tasks.remove(indexToUpdate);
			tasks.add(indexToUpdate, new Task(taskName));

			return String.format(Global.MESSAGE_UPDATED, taskName);
		}
		 */
		return "out of order";
	}

	/**
	 * Creates an array which only contains the desired types of tasks 
	 * within the desired time period in the right order.
	 * See also description for Controller.displayedTasks
	 * 
	 * @return  Internal Message, will be hidden by the UI
	 */
	public String displayTasks() {
		if (tasks.isEmpty()) {
			return String.format(Global.MESSAGE_EMPTY);
		}

		String[][] result = new String[tasks.size()][3]; // first [] represents line, second [] represents row of the array
		SimpleDateFormat dayFormat = new SimpleDateFormat("EEE MMM d ''yy");
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");

		for (int i = 0; i < tasks.size(); i++) {

			TaskType taskType = tasks.get(i).getType();
			if (taskType == TaskType.TIMED) {
				TimedTask timedTask = (TimedTask) tasks.get(i);
				result[i][0] = dayFormat.format(timedTask.getStartDate());
				result[i][1] = timeFormat.format(timedTask.getStartDate()) + "-"
						+ timeFormat.format(timedTask.getEndDate());
				result[i][2] = tasks.get(i).getName();

			} else if (taskType == TaskType.DEADLINE) {
				DeadlineTask deadlineTask = (DeadlineTask) tasks.get(i);
				result[i][0] = dayFormat.format(deadlineTask.getEndDate());
				result[i][1] = timeFormat.format(deadlineTask.getEndDate());
				result[i][2] = deadlineTask.getName();

			} else {
				FloatingTask floatingTask = (FloatingTask) tasks.get(i);
				result[i][0] = null;
				result[i][1] = null;
				result[i][2] = floatingTask.getName();
			}

			TaskCommander.controller.setDisplayedTasks(result);
		}
		return "Internal Message: String Array for the UI was created, see also console output";

	}

	/**
	 * Deletes the task with the given index (as shown with 'display' command).
	 * Does not execute if there are no lines and if a wrong index is given.
	 * Eg: Index out of bounds or given a char instead of int.
	 * 
	 * @param index        Index of the task to delete, as a string. 
	 * @return             Feedback for user.
	 */
	public String deleteTask(String index) {
		if (tasks.isEmpty()) {
			return String.format(Global.MESSAGE_EMPTY);
		} else if (index == null) {
			return Global.MESSAGE_NO_TASK;
		}

		int indexToRemove;
		try {
			indexToRemove = Integer.parseInt(index) - Global.INDEX_OFFSET; // Change the line number to an array index
		} catch (NumberFormatException e) {
			return String.format(Global.MESSAGE_INVALID_FORMAT, "delete " + index);
		} 

		if (indexToRemove > tasks.size() - Global.INDEX_OFFSET) {
			return String.format(Global.MESSAGE_NO_INDEX, index);
		} else {
			Task taskToRemove = tasks.get(indexToRemove);
			deletedTasks.add(taskToRemove);
			tasks.remove(indexToRemove);

			return String.format(Global.MESSAGE_DELETED, taskToRemove.getName());
		}
	}

	/**
	 * Clears all tasks from memory.
	 * 
	 * @param userCommand 
	 * @return             Feedback for user.
	 */
	public String clearTasks() {
		deletedTasks.addAll(tasks);
		tasks.clear();
		return String.format(Global.MESSAGE_CLEARED);
	}

	/**
	 * Sorts the tasks in memory in alphabetical order.
	 * @return   Feedback for user.
	 */
	public String sort() {
		if (tasks.isEmpty()) {
			return String.format(Global.MESSAGE_EMPTY);
		} else {
			Collections.sort(tasks);
			return String.format(Global.MESSAGE_SORTED);
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
