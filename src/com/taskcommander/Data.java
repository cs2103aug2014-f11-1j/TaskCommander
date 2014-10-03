package com.taskcommander;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class stores the data temporary.
 * 
 * @author Andreas Christian Mayr
 */


public class Data {

	/**
	 * Array containing a list of task objects
	 */
	public ArrayList<Task> tasks;
	
	/**
	 * Constructor
	 */
	public Data() {
		tasks = new ArrayList<Task>();
	}
	
	/**
	 * Reads the content of the file into the data array.
	 */
	public void readStorage(Storage storage){

		try {
			BufferedReader myBufferedReader = new BufferedReader(
					new FileReader(new File(Storage.getFileName())));
			String line;

			while ((line = myBufferedReader.readLine()) != null) {
				tasks.add(new Task(line));
			}

			myBufferedReader.close();

		} catch (IOException e) {
			System.err.println(Global.MESSAGE_FILE_COULD_NOT_BE_LOADED);
		}
	}
	
	/**
	 * Writes the content of the data array into the given storage.
	 */
	public void writeStorage(Storage storage ){

		try {
			BufferedWriter myBufferedWriter = new BufferedWriter(
					new FileWriter(new File(Storage.getFileName())));
			
			for(Task task: tasks) {
				myBufferedWriter.write(task.getName());
				myBufferedWriter.newLine();
			}
			
			myBufferedWriter.close();

		} catch (IOException e) {
			System.err.println(Global.MESSAGE_FILE_COULD_NOT_BE_WRITTEN);
		}
	}
	
	/**
	 * Adds a task with given name.
	 * 
	 * @param taskName     
	 * @return             Feedback for user.
	 */
	public String addTask(String taskName) {
		if (taskName == null) {
			return Global.MESSAGE_NO_LINE;
		}
		tasks.add(new Task(taskName));
		return String.format(Global.MESSAGE_ADDED, taskName);
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
	public String updateTask(String index, String taskName) {
		if (tasks.isEmpty()) {
			return String.format(Global.MESSAGE_EMPTY);
		} else if (index == null) {
			return Global.MESSAGE_NO_LINE;
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
	}

	/**
	 * Returns all tasks in this format:
	 * <index>. <task name> <line break> 
	 * 
	 * @return  String containing all task names.
	 */
	public String displayTasks() {
		if (tasks.isEmpty()) {
			return String.format(Global.MESSAGE_EMPTY);
		} else {
			String result = "";
			for (int i = 0; i < tasks.size(); i++) {
				result += (i + 1) + ". " + tasks.get(i).getName() + "\n";
			}
			return result;
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
	public String deleteTask(String index) {
		if (tasks.isEmpty()) {
			return String.format(Global.MESSAGE_EMPTY);
		} else if (index == null) {
			return Global.MESSAGE_NO_LINE;
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
}
