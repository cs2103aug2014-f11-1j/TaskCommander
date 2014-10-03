package com.taskcommander;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * This class represents the Controller, which decides the execution depending on the command.
 * 
 * @author Andreas Christian Mayr
 */

public class Controller {
	
	public Controller(){
		readFromStorage();
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

		Global.CommandType commandType= TaskCommander.parser.determineCommandType(userCommand);
		
		switch (commandType) {
		case HELP:
			if (isSingleWord(userCommand)) {
				return Global.MESSAGE_HELP;
			} else {
				return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
			}
		case ADD:
			String taskName = null;
			try {
				taskName = TaskCommander.parser.determineTaskName(userCommand);
			} catch (StringIndexOutOfBoundsException e) {
				return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
			}
			
			List<Date> dateTime = TaskCommander.parser.determineTaskDateTime(userCommand);

			if (dateTime == null) {
				return TaskCommander.tasks.addTask("\""+taskName+"\"");
			} else if (dateTime.size() ==1 ) {
				return TaskCommander.tasks.addTask(dateTime.get(0).toString()+" "+"\""+taskName+"\"");
			} else if (dateTime.size() == 2) {
				return TaskCommander.tasks.addTask(dateTime.get(0).toString()+" "+dateTime.get(1).toString()+" "+"\""+taskName+"\"");
			} else {
				return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
			}

		case UPDATE:
			if (getNumberOfWords(userCommand) >= 3) {
				return TaskCommander.tasks.updateTask(getNthWord(userCommand,1),removeFirstWord(removeFirstWord(userCommand)));
			} else {
				return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
			}
		case DISPLAY:
			if (isSingleWord(userCommand)) {
				return TaskCommander.tasks.displayTasks();
			} else {
				return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
			}
		case DELETE:
			return TaskCommander.tasks.deleteTask(removeFirstWord(userCommand));
		case CLEAR:
			if (isSingleWord(userCommand)) {
				return TaskCommander.tasks.clearTasks();
			} else {
				return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
			}
		case SORT:
			return TaskCommander.tasks.sort();
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
		TaskCommander.tasks.getStorage(TaskCommander.file);
	}
	
	public void safeToStorage() {
		TaskCommander.file.getData(TaskCommander.tasks);
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
	
	

