package com.taskcommander;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;

/**
 * In general the application “Task Commander” is a uncomplicated, command-line based “todo” list app for PC. 
 * In doing so, it represents a scaled-down version of a “Siri for keyboards” which manages command related 
 * “todo” tasks. Although these tasks are not truly formulated in natural language, the command format is still 
 * flexible yet comfortable to use.

Examples of commands...

 * @author Group F11-1J
 */

public class TaskCommander {
	private static final String MESSAGE_FILE_NOT_GIVEN = "No file given. Please enter a file name.";
	private static final String MESSAGE_FILE_NOT_FOUND = "File not found. Please enter a valid file name.";
	private static final String MESSAGE_WELCOME = "Welcome to TextBuddy. %1$s is ready for use. " + 
			"Type 'help' to see the list of commands.";
	private static final String MESSAGE_ADDED = "Added to %1$s: \"%2$s\"";
	private static final String MESSAGE_UPDATED = "Updated %1$s: \"%2$s\"";
	private static final String MESSAGE_DELETED = "Deleted from %1$s: \"%2$s\"";
	private static final String MESSAGE_CLEARED = "All content deleted from %1$s";
	private static final String MESSAGE_INVALID_FORMAT = "Invalid command format: %1$s. " + 
			"Type 'help' to see the list of commands.";
	private static final String MESSAGE_NO_COMMAND = "No command given.";
	private static final String MESSAGE_NO_LINE = "No line given.";
	private static final String MESSAGE_NO_INDEX = "Index %1$s does not exist. Please type a valid index.";
	private static final String MESSAGE_EMPTY = "%1$s is empty";
	private static final String MESSAGE_HELP = "Commands: add <string>, display, delete <index of string>, clear, sort, exit.";
	private static final String MESSAGE_LINE_FOUND = "Found \"%1$s\".";
	private static final String MESSAGE_LINE_NOT_FOUND = "The line \"%1$s\" does not exist.";
	private static final String MESSAGE_SORTED = "%1$s has been sorted.";

	private static final int INDEX_OFFSET = 1; // Difference between the array index and actual line number

	// Possible command types
	public enum CommandType {
		HELP,
		ADD, 
		UPDATE,
		DISPLAY, 
		DELETE,
		CLEAR,
		SORT,
		INVALID,
		EXIT
	};

	// This string stores the name of the file being used
	private static String _fileName = "tasks.txt";

	// This arraylist stores the lines of tasks
	private static ArrayList<Task> tasks = new ArrayList<Task>();

	/*
	 * This variable is declared for the whole class (instead of declaring it
	 * inside the readUserCommand() method to facilitate automated testing using
	 * the I/O redirection technique. If not, only the first line of the input
	 * text file will be processed.
	 */
	private static Scanner scanner = new Scanner(System.in);

	public TaskCommander() {
		readFromFile();
	}

	/**
	 * Waits for user commands, executes them and shows feedback.
	 */
	public void run() {
		while (true) {
			System.out.print("Enter command: ");
			String userCommand = scanner.nextLine();
			String feedback = executeCommand(userCommand);
			showToUser(feedback);
			writeToFile();
		}
	}

	/**
	 * Checks if input is valid and returns to caller, exits with error message if not.
	 * Input is valid when argument given is not empty, is a String,
	 * and the file name is valid.
	 * 
	 * @param  fileName    Input given by user.
	 * @return             True if input valid.
	 */
	public boolean isValidFileName(String fileName) {
		if (fileName == null || fileName.equals("")) { 
			showToUser(MESSAGE_FILE_NOT_GIVEN);
			return false;
		}

		File givenFile = new File(fileName);
		if (!givenFile.exists() || givenFile.isDirectory()) {
			showToUser(MESSAGE_FILE_NOT_FOUND);
			return false;
		}

		return true;
	}

	/**
	 * Populates the memory with tasks read from saved file.
	 * Creates a new file if it doesn't exist.
	 */
	public void readFromFile() {
		File file = new File(_fileName);

	    try {
			if (!file.isFile() && !file.createNewFile())
			{
			    throw new IOException("Error creating new file: " + file.getAbsolutePath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				tasks.add(new Task(line));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Writes the saved lines into the given text file. 
	 */
	public void writeToFile() {
		PrintWriter pr = null;
		try {
			pr = new PrintWriter(_fileName);
			if (tasks.isEmpty()) {
				// Do nothing, file will be cleared of text.
			} else {
				Iterator<Task> it = tasks.iterator();
				while (it.hasNext()) {
					pr.write(it.next().getName());
				}
				pr.flush();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			pr.close();
		}
	}

	/**
	 * Parses command from user and executes it if valid. Writes to file after each command.
	 * Returns feedback to show to user.
	 * 
	 * @param  userCommand  Command given by user.
	 * @return              Feedback to show to user.
	 */
	public String executeCommand(String userCommand) {	// made static for testing reasons
		if (userCommand == null) {
			return MESSAGE_NO_COMMAND;
		}

		CommandType commandType = determineCommandType(getFirstWord(userCommand));
		switch (commandType) {
		case HELP:
			if (isSingleWord(userCommand)) {
				return MESSAGE_HELP;
			} else {
				return String.format(MESSAGE_INVALID_FORMAT, userCommand);
			}
		case ADD:
			return addTask(removeFirstWord(userCommand));
		case UPDATE:
			if (getNumberOfWords(userCommand) >= 3) {
				return updateTask(getNthWord(userCommand,1),removeFirstWord(removeFirstWord(userCommand)));
			} else {
				return String.format(MESSAGE_INVALID_FORMAT, userCommand);
			}
		case DISPLAY:
			if (isSingleWord(userCommand)) {
				return displayTasks();
			} else {
				return String.format(MESSAGE_INVALID_FORMAT, userCommand);
			}
		case DELETE:
			return deleteTask(removeFirstWord(userCommand));
		case CLEAR:
			if (isSingleWord(userCommand)) {
				return clearTasks();
			} else {
				return String.format(MESSAGE_INVALID_FORMAT, userCommand);
			}
		case SORT:
			return sort();
		case INVALID:
			return String.format(MESSAGE_INVALID_FORMAT, userCommand);
		case EXIT:
			System.exit(0);
		default:
			return String.format(MESSAGE_INVALID_FORMAT, userCommand);
		}
	}

	/**
	 * This operation determines which of the supported command types the user
	 * wants to perform.
	 * 
	 * @param commandTypeString  First word of the user command.
	 */
	public CommandType determineCommandType(String commandTypeString) {
		if (commandTypeString == null) {
			throw new Error("command type string cannot be null!");
		}

		if (commandTypeString.equalsIgnoreCase("help")) {
			return CommandType.HELP;
		} else if (commandTypeString.equalsIgnoreCase("add")) {
			return CommandType.ADD;
		} else if (commandTypeString.equalsIgnoreCase("update")) {
			return CommandType.UPDATE;
		} else if (commandTypeString.equalsIgnoreCase("display")) {
			return CommandType.DISPLAY;
		} else if (commandTypeString.equalsIgnoreCase("delete")) {
			return CommandType.DELETE;
		} else if (commandTypeString.equalsIgnoreCase("clear")) {
			return CommandType.CLEAR;
		} else if (commandTypeString.equalsIgnoreCase("exit")) {
			return CommandType.EXIT;
		} else {
			return CommandType.INVALID;
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
			return MESSAGE_NO_LINE;
		}
		tasks.add(new Task(taskName));
		return String.format(MESSAGE_ADDED, _fileName, taskName);
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
			return String.format(MESSAGE_EMPTY, _fileName);
		} else if (index == null) {
			return MESSAGE_NO_LINE;
		}

		int indexToUpdate;
		try {
			indexToUpdate = Integer.parseInt(index) - INDEX_OFFSET; // Change the line number to an array index
		} catch (NumberFormatException e) {
			return String.format(MESSAGE_INVALID_FORMAT, "update " + index + taskName);
		} 

		if (indexToUpdate > tasks.size() - INDEX_OFFSET) {
			return String.format(MESSAGE_NO_INDEX, index);
		} else {
			tasks.remove(indexToUpdate);
			tasks.add(indexToUpdate, new Task(taskName));

			return String.format(MESSAGE_UPDATED, _fileName, taskName);
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
			return String.format(MESSAGE_EMPTY, _fileName);
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
			return String.format(MESSAGE_EMPTY, _fileName);
		} else if (index == null) {
			return MESSAGE_NO_LINE;
		}

		int indexToRemove;
		try {
			indexToRemove = Integer.parseInt(index) - INDEX_OFFSET; // Change the line number to an array index
		} catch (NumberFormatException e) {
			return String.format(MESSAGE_INVALID_FORMAT, "delete " + index);
		} 

		if (indexToRemove > tasks.size() - INDEX_OFFSET) {
			return String.format(MESSAGE_NO_INDEX, index);
		} else {
			Task taskToRemove = tasks.get(indexToRemove);

			tasks.remove(indexToRemove);

			return String.format(MESSAGE_DELETED, _fileName, taskToRemove.getName());
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
		return String.format(MESSAGE_CLEARED, _fileName);
	}

	/**
	 * Sorts the tasks in memory in alphabetical order.
	 * @return   Feedback for user.
	 */
	public String sort() {
		if (tasks.isEmpty()) {
			return String.format(MESSAGE_EMPTY, _fileName);
		} else {
			Collections.sort(tasks);
			return String.format(MESSAGE_SORTED, _fileName);
		}
	}

	private void showToUser(String s) {
		System.out.println(s);
	}
	
	/**
	 * Checks if the given String is made up of only one word.
	 * Used to validate commands.
	 * 
	 * @param  userCommand 
	 * @return             Whether the given string is only one word.
	 */
	private boolean isSingleWord(String userCommand) {
		return getNumberOfWords(userCommand) == 1;
	}
	
	private int getNumberOfWords(String userCommand) {
		String[] allWords = userCommand.trim().split("\\s+");
		return allWords.length;
	}
	
	private static String getNthWord(String userCommand, int position) {
		String[] allWords = userCommand.trim().split("\\s+");
		if (position > allWords.length-1) {
			return "";	// otherwise there would be a java.lang.ArrayIndexOutOfBoundsException
		} 
		String nthWord = userCommand.trim().split("\\s+")[position];
		return nthWord;
	}

	private String removeFirstWord(String userCommand) {
		return userCommand.replace(getFirstWord(userCommand), "").trim();
	}

	private String getFirstWord(String userCommand) {
		return userCommand.trim().split("\\s+")[0];
	}

}