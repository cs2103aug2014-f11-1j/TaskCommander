package com.taskcommander;
import java.util.Date;
import java.util.List;

import com.taskcommander.Task.TaskType;

/**
 * This class represents the Controller, which decides the execution depending on the command.
 * 
 * @author Andreas Christian Mayr
 */

public class Controller {
	
	/**
	 * Constructor
	 */
	public Controller(){
		readFromStorage();
	}
	
	/**
	 * Additional feedback array for the display command, which only contains the desired types of tasks 
	 * within the desired time period in the right order. This array is set by the Data.display method and
	 * accessed by the UI.
	 * 
	 * The array will have the following format.
	 * 
	 * Day			Time				Name			|
	 * --------------------------------------------------				
	 * null			null				FloatingTask1   |
	 * null			null				FloatingTask2   |
	 * null			null				FloatingTask3   |
	 * ....			....				.............   |
	 * ..			..					............    |
	 * .			.					.......         |
	 * Sat Oct 04	10:00-11:00			TimedTask1      |
	 * Sat Oct 04	by 12:00			DeadlineTask1
	 * Sat Oct 04	12:00-13:00			TimedTask2
	 * Sat Oct 04	14:00-15:00			TimedTask3
	 * Sat Oct 04	by 18:00			DeadlineTask2
	 * ....			....				.................
	 * ..			..					............
	 * .			.					.......
	 * Sun Oct 04	10:00-11:00			TimedTask4
	 * Sun Oct 04	by 12:00			DeadlineTask3
	 * ....			....				.................
	 * ..			..					............
	 * .			.					.......
	 * 
	 */
	private String[][] displayedTasks;
	
	public String[][] getDisplayedTasks() {
		return displayedTasks;
	}

	public void setDisplayedTasks(String[][] displayedTasks) {
		this.displayedTasks = displayedTasks;
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
			if (isSingleWord(userCommand)) {
				return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
			}
			
			// TaskName
			String taskName = null;
			try {
				taskName = TaskCommander.parser.determineTaskName(userCommand);
			} catch (StringIndexOutOfBoundsException e) {
				return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
			}
			
			// TaskDateTime (3 cases depending on taskType)
			List<Date> taskDateTime = TaskCommander.parser.determineTaskDateTime(userCommand);
			if (taskDateTime == null) { 			// case 1: FloatingTask
				return TaskCommander.data.addTask(Global.TaskType.FLOATING, taskName, null, null);
			} else if (taskDateTime.size() ==1 ) { 	// case 2: DeadlineTask
				return TaskCommander.data.addTask(Global.TaskType.DEADLINE, taskName,null, taskDateTime.get(0));
			} else if (taskDateTime.size() == 2) { 	// case 3: TimedTask
				return TaskCommander.data.addTask(Global.TaskType.TIMED, taskName, taskDateTime.get(0), taskDateTime.get(1));
			} else {
				return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
			}
			
		case UPDATE:	// implementation needs to be adjusted to new parser
			
			/*
			if (getNumberOfWords(userCommand) >= 3) {
				return TaskCommander.data.updateTask(getNthWord(userCommand,1),removeFirstWord(removeFirstWord(userCommand)));
			} else {
				return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
			}
			*/
			
		case DISPLAY:
			String feedback;
			if (!isSingleWord(userCommand)) {
				return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
			} else {
				feedback = TaskCommander.data.displayTasks();
				// temporary output of the displayTasks Array in the console
				if (!(displayedTasks == null)) {
				for (int i = 0; i < displayedTasks.length; i++) {
					System.out.println(displayedTasks[i][0]+"\t"+displayedTasks[i][1]+"\t"+displayedTasks[i][1]);
				};
				}
				return feedback;
			}
			
		case DELETE:
			return TaskCommander.data.deleteTask(removeFirstWord(userCommand));
		case CLEAR:
			if (isSingleWord(userCommand)) {
				return TaskCommander.data.clearTasks();
			} else {
				return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
			}
		case SORT:
			return TaskCommander.data.sort();
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
		TaskCommander.data.readStorage(TaskCommander.storage);
	}
	
	public void safeToStorage() {
		TaskCommander.data.writeStorage(TaskCommander.storage);
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