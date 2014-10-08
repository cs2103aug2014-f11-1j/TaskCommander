package com.taskcommander;
import java.util.Date;
import java.util.List;

/**
 * This class represents the Controller component, which calls the respective execution method
 * depending on the user command.
 * 
 * @author A0128620M
 */

public class Controller {
	
	/**
	 * Constructor
	 */
	public Controller(){
	}

	/**
	 * Parses command from user and executes it if valid. Returns feedback to UI.
	 * 
	 * @param  userCommand  command given by user
	 * @return              feedback for to the UI
	 */
	public Feedback executeCommand(String userCommand) {	
		if (userCommand == null) {
			return new Feedback(false,Global.MESSAGE_NO_COMMAND);
		}

		Global.CommandType commandType= TaskCommander.parser.determineCommandType(userCommand);
		
		String restOfUserCommand = removeFirstWord(userCommand);
		
		switch (commandType) {
			case ADD:
				
				String taskName;
				try {
					taskName = TaskCommander.parser.determineTaskName(restOfUserCommand);
				} catch (StringIndexOutOfBoundsException e) {
					return new Feedback(false,String.format(Global.MESSAGE_INVALID_FORMAT, userCommand));
				}
				
				restOfUserCommand = removeTaskName(restOfUserCommand, taskName);
				
				// taskDateTime (3 cases depending on taskType)
				List<Date> taskDateTime = TaskCommander.parser.determineTaskDateTime(restOfUserCommand);
				// case 1: FloatingTask
				if (taskDateTime == null) { 			
					return TaskCommander.data.addFloatingTask(taskName);
				// case 2: DeadlineTask
				} else if (taskDateTime.size() == 1 ) { 	
					return TaskCommander.data.addDeadlineTask(taskName, taskDateTime.get(0));
				// case 3: TimedTask
				} else if (taskDateTime.size() == 2) { 
					return TaskCommander.data.addTimedTask(taskName, taskDateTime.get(0), taskDateTime.get(1));
				} else {
					return new Feedback(false,String.format(Global.MESSAGE_INVALID_FORMAT, userCommand));
				}
				
			case UPDATE:	//TODO: implementation needs to be adjusted to different types of tasks
				
				/*
				String newtaskName = "";
				try {
					newtaskName = TaskCommander.parser.determineTaskName(restOfUserCommand);
				} catch (StringIndexOutOfBoundsException e) {
				}
				
				if (!newtaskName.equals("")) {
					restOfUserCommand = removeTaskName(restOfUserCommand, newtaskName);
				}
				
				// new taskDateTime (3 cases depending on taskType)
				List<Date> newTaskDateTime = TaskCommander.parser.determineTaskDateTime(restOfUserCommand);
				// case 1: FloatingTask
				if (taskDateTime == null) { 			
					return TaskCommander.data.updateToFloatingTask(taskName);
				// case 2: DeadlineTask
				} else if (taskDateTime.size() == 1 ) { 	
					return TaskCommander.data.addDeadlineTask(taskName, taskDateTime.get(0));
				// case 3: TimedTask
				} else if (taskDateTime.size() == 2) { 
					return TaskCommander.data.addTimedTask(taskName, taskDateTime.get(0), taskDateTime.get(1));
				} else {
					return new Feedback(false,String.format(Global.MESSAGE_INVALID_FORMAT, userCommand));
				}
				*/
				
				/*
				if (getNumberOfWords(userCommand) >= 3) {
					return TaskCommander.data.updateTask(getNthWord(userCommand,1),removeFirstWord(removeFirstWord(userCommand)));
				} else {
					return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
				}
				*/
				
			case DISPLAY:
				if (isSingleWord(userCommand)) {
					return TaskCommander.data.displayTasks();
				} else {
					return new Feedback(false,String.format(Global.MESSAGE_INVALID_FORMAT, userCommand));
				}
				
			case DELETE:
				return TaskCommander.data.deleteTask(removeFirstWord(userCommand));
				
			case CLEAR:
				if (isSingleWord(userCommand)) {
					return TaskCommander.data.clearTasks();
				} else {
					return new Feedback(false,String.format(Global.MESSAGE_INVALID_FORMAT, userCommand));
				}
				
			case HELP:
				if (isSingleWord(userCommand)) {
					return new Feedback(false,Global.MESSAGE_HELP);
				} else {
					return new Feedback(false,String.format(Global.MESSAGE_INVALID_FORMAT, userCommand));
				}
			case SORT:
				return TaskCommander.data.sort();
				
			case SYNC:
			/*
				if (TaskCommander.syncHandler == null) {
					TaskCommander.getSyncHandler();
				}
				return TaskCommander.syncHandler.sync();
			*/
				return new Feedback(false,"out of order");
				
			case INVALID:
				return new Feedback(false,String.format(Global.MESSAGE_INVALID_FORMAT, userCommand));
				
			case EXIT:
				System.exit(0);
				
			default:
				return new Feedback(false,String.format(Global.MESSAGE_INVALID_FORMAT, userCommand));
		}
	}
	
	/**
	 * Helper methods
	 */
	private static boolean isSingleWord(String userCommand) {
		return getNumberOfWords(userCommand) == 1;
	}
	
	private static int getNumberOfWords(String userCommand) {
		String[] allWords = userCommand.trim().split("\\s+");
		return allWords.length;
	}
	
	/* not used at the moment
	private static String getNthWord(String userCommand, int position) {
		String[] allWords = userCommand.trim().split("\\s+");
		if (position > allWords.length-1) {
			return "";	// otherwise there would be a java.lang.ArrayIndexOutOfBoundsException
		} 
		String nthWord = userCommand.trim().split("\\s+")[position];
		return nthWord;
	}
	*/

	private static String removeFirstWord(String userCommand) {
		return userCommand.replace(getFirstWord(userCommand), "").trim();
	}
	
	private static String removeTaskName(String userCommand,String taskName) {
		return userCommand.replace("\""+taskName+"\"", "").trim();
	}

	private static String getFirstWord(String userCommand) {
		return userCommand.trim().split("\\s+")[0];
	}

}
