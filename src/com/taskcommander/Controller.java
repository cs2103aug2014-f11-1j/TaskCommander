package com.taskcommander;
import java.util.ArrayList;
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
	 * This ArrayList contains all tasks which were recently displayed by the UI. It equals to the 
	 * ArrayList which was returned to the UI within the Feedback object in respond to the latest 
	 * display command. Memorizing the tasks which have been displayed recently by the UI is needed 
	 * by the update and delete feature.
	 */
	private ArrayList<Task> tasksRecentlyDisplayed;

	/**
	 * Parses command from user and executes it if valid. Returns feedback to UI.
	 * 
	 * @param  userCommand  command given by user
	 * @return              feedback for to the UI
	 */
	public Feedback executeCommand(String userCommand) {	
		if (userCommand == null | userCommand == "") {
			return new Feedback(false,Global.MESSAGE_NO_COMMAND);
		}

		String commandTypeString = getFirstWord(userCommand);
		Global.CommandType commandType= TaskCommander.parser.determineCommandType(commandTypeString);
		String residualUserCommand = removeFirstWord(userCommand);
		
		String indexTasksRecentlyDisplayedString;
		int indexTasksRecentlyDisplayed;
		
		switch (commandType) {
			case ADD:
				
				// taskName
				String taskName = TaskCommander.parser.determineTaskName(residualUserCommand);
				if (taskName == null) {
					return new Feedback(false,String.format(Global.MESSAGE_INVALID_FORMAT, userCommand));
				}
				
				residualUserCommand = removeTaskName(residualUserCommand, taskName);
				
				// taskDateTime (3 cases depending on taskType)
				List<Date> taskDateTime = TaskCommander.parser.determineTaskDateTime(residualUserCommand);
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
				
			case UPDATE:
				
				// Index in ArrayList tasksRecentlyDisplayed
				indexTasksRecentlyDisplayedString = getFirstWord(residualUserCommand);
				try {
					indexTasksRecentlyDisplayed = Integer.parseInt(indexTasksRecentlyDisplayedString) - Global.INDEX_OFFSET; // Change the line number to an array index
				} catch (NumberFormatException e) {
					return new Feedback(false, String.format(Global.MESSAGE_INVALID_FORMAT, userCommand));
				} 
				if (indexTasksRecentlyDisplayed > tasksRecentlyDisplayed.size() - Global.INDEX_OFFSET || indexTasksRecentlyDisplayed < 0) {
					return new Feedback(false, String.format(Global.MESSAGE_NO_INDEX, indexTasksRecentlyDisplayed + Global.INDEX_OFFSET));
				}
				residualUserCommand = removeFirstWord(residualUserCommand);
				
				// Task to be updated
				Task oldTask = tasksRecentlyDisplayed.get(indexTasksRecentlyDisplayed);
				String oldTaskName = oldTask.getName();
				Task.TaskType oldTaskType = oldTask.getType();
				
				// Index in ArrayList tasks of the Data class
				int indexTasks = TaskCommander.data.getIndexOf(oldTask);
				
				// New taskName, if stated
				String newTaskName = null;
				newTaskName = TaskCommander.parser.determineTaskName(residualUserCommand);

				if (newTaskName != null) {
					residualUserCommand = removeTaskName(residualUserCommand, newTaskName);
				} else {
					newTaskName = oldTaskName;
				}
				
				// New taskDateTime and taskType, if stated
				List<Date> newTaskDateTime = TaskCommander.parser.determineTaskDateTime(residualUserCommand);	// returns null if no date found in given String
				if (newTaskDateTime != null) {	// more than two DateTimes given
					if (newTaskDateTime.size() > 2) {
						return new Feedback(false, String.format(Global.MESSAGE_NO_INDEX, indexTasksRecentlyDisplayed));
					}
				}

				Task.TaskType newTaskType = oldTaskType;
				Date newStartDate = null;
				Date newEndDate = null;
				if ((newTaskDateTime == null) && (residualUserCommand.contains("none"))) {	// "none" is a keyword used by the user to indicate, that he wants to change a DatedTask to a FloatingTask
					newTaskType = Task.TaskType.FLOATING;
				} else if (newTaskDateTime != null) {
					if (newTaskDateTime.size() == 1) {
						newTaskType = Task.TaskType.DEADLINE;
						newEndDate = newTaskDateTime.get(0);
					} else if (newTaskDateTime.size() == 2) {
						newTaskType = Task.TaskType.TIMED;
						newStartDate = newTaskDateTime.get(0);
						newEndDate = newTaskDateTime.get(1);
					}
				}
				
				// No changes at all, that is, no new DateTime, Name, or "none" given
				if ((newTaskDateTime == null) && (newTaskName == oldTaskName) && (oldTaskType == newTaskType)) {
					return new Feedback(false,String.format(Global.MESSAGE_INVALID_FORMAT, userCommand));
				}
				
				// Update including change of taskType if necessary
				switch (newTaskType) {
					case FLOATING:
						return TaskCommander.data.updateToFloatingTask(indexTasks, newTaskName);
					case DEADLINE:
						return TaskCommander.data.updateToDeadlineTask(indexTasks, newTaskName, newEndDate);
					case TIMED:
						return TaskCommander.data.updateToTimedTask(indexTasks, newTaskName, newStartDate, newEndDate);
					}
				
			case DISPLAY:
				if (isSingleWord(userCommand)) {
					Feedback feedback = TaskCommander.data.displayTasks();
					tasksRecentlyDisplayed = feedback.getCommandRelatedTasks();
					return feedback;
				} else {
					return new Feedback(false,String.format(Global.MESSAGE_INVALID_FORMAT, userCommand));
				}
				
			case DELETE:
				
				// Index in ArrayList tasksRecentlyDisplayed
				indexTasksRecentlyDisplayedString = getFirstWord(residualUserCommand);
				try {
					indexTasksRecentlyDisplayed = Integer.parseInt(indexTasksRecentlyDisplayedString) - Global.INDEX_OFFSET; // Change the line number to an array index
				} catch (NumberFormatException e) {
					return new Feedback(false, String.format(Global.MESSAGE_INVALID_FORMAT, userCommand));
				} 
				if (indexTasksRecentlyDisplayed > tasksRecentlyDisplayed.size() - Global.INDEX_OFFSET || indexTasksRecentlyDisplayed < 0) {
					return new Feedback(false, String.format(Global.MESSAGE_NO_INDEX, indexTasksRecentlyDisplayed + Global.INDEX_OFFSET));
				}
				
				return TaskCommander.data.deleteTask(indexTasksRecentlyDisplayed);
				
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
	
	/* not used
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
