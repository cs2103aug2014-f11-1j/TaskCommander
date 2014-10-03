package com.taskcommander;

/**
 * This class represents the Controller, which decides the execution depending on the command.
 * 
 * @author Andreas Christian Mayr
 */

public class Controller {
	
	/**
	 * Static variables
	 */
	
	// This list stores the lines of tasks temporary
	public static Data tasks;
	
	// This file stores the lines of tasks permanently  on the computer
	public static Storage file;
	
	public static Parser parser;
	
	public Controller(){
		tasks = new Data();
		file = new Storage();
		parser = new Parser();
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
			return Global.MESSAGE_NO_COMMAND;
		}

		Global.CommandType commandType= parser.determineCommandType(getFirstWord(userCommand));
		switch (commandType) {
		case HELP:
			if (isSingleWord(userCommand)) {
				return Global.MESSAGE_HELP;
			} else {
				return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
			}
		case ADD:
			return tasks.addTask(removeFirstWord(userCommand));
		case UPDATE:
			if (getNumberOfWords(userCommand) >= 3) {
				return tasks.updateTask(getNthWord(userCommand,1),removeFirstWord(removeFirstWord(userCommand)));
			} else {
				return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
			}
		case DISPLAY:
			if (isSingleWord(userCommand)) {
				return tasks.displayTasks();
			} else {
				return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
			}
		case DELETE:
			return tasks.deleteTask(removeFirstWord(userCommand));
		case CLEAR:
			if (isSingleWord(userCommand)) {
				return tasks.clearTasks();
			} else {
				return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
			}
		case SORT:
			return tasks.sort();
		case INVALID:
			return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
		case EXIT:
			System.exit(0);
		default:
			return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
		}
	}
	
	/**
	 * Read and write from storage to temporary data
	 */
	public void readFromStorage() {
		tasks.getStorage(file);
	}
	
	public void safeToStorage() {
		file.getData(tasks);
	}
	
	/**
	 * Checks if the given String is made up of only one word.
	 * Used to validate commands.
	 * 
	 * @param  userCommand 
	 * @return             Whether the given string is only one word.
	 */
	private static boolean isSingleWord(String userCommand) {
		return getNumberOfWords(userCommand) == 1;
	}
	
	private static int getNumberOfWords(String userCommand) {
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

	private static String removeFirstWord(String userCommand) {
		return userCommand.replace(getFirstWord(userCommand), "").trim();
	}

	private static String getFirstWord(String userCommand) {
		return userCommand.trim().split("\\s+")[0];
	}

}
	
	

