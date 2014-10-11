package com.taskcommander;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * This class represents the Controller component. After receiving the user's
 * input from the UI, the Controller determines the exact type of command and its 
 * related parameters by means of the Parser. Then the respective method
 * of the Data component is called to execute the command and the regained feedback
 * is returned to the UI.
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
	 * This feedback object of the latest display command contains all tasks which were 
	 * recently displayed by the UI. Memorizing the tasks which have been displayed recently 
	 * by the UI is needed by the update, delete, done and open methods.
	 */
	private Feedback recentDisplayFeedback;	
	
	/**
	 * This feedback object represent the feedback of the latest add/update/done/open/delete command.
	 * Memorizing the commands which have been executed recently is needed by the undo method.
	 * 
	 * TODO
	 */
	private Feedback recentAddUpdateMarkDeleteClearFeedback;

	/**
	 * This operation parses the command from the user and executes it if valid. Afterwards a 
	 * feedback is returned.
	 * 
	 * @param  userCommand  command given by user
	 * @return              feedback to the UI
	 */
	public Feedback executeCommand(String userCommand) {	
		if (userCommand == null | userCommand == "") {
			return new Feedback(false,Global.MESSAGE_NO_COMMAND);
		}

		Global.CommandType commandType= TaskCommander.parser.determineCommandType(userCommand);
		String residualUserCommand = removeFirstWord(userCommand);
		
		String indexTasksRecentlyDisplayedString;
		int indexTasksRecentlyDisplayed;
		int indexTasks;
		
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
				if (indexTasksRecentlyDisplayed > recentDisplayFeedback.getCommandRelatedTasks().size() - Global.INDEX_OFFSET || indexTasksRecentlyDisplayed < 0) {
					return new Feedback(false, String.format(Global.MESSAGE_NO_INDEX, indexTasksRecentlyDisplayed + Global.INDEX_OFFSET));
				}
				residualUserCommand = removeFirstWord(residualUserCommand);
				
				// Task to be updated
				Task oldTask = recentDisplayFeedback.getCommandRelatedTasks().get(indexTasksRecentlyDisplayed);
				String oldTaskName = oldTask.getName();
				Task.TaskType oldTaskType = oldTask.getType();
				
				// Index in ArrayList tasks of the Data class
				indexTasks = TaskCommander.data.getIndexOf(oldTask);
				
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
				
			case DONE:
				
				// Index in ArrayList tasksRecentlyDisplayed
				indexTasksRecentlyDisplayedString = getFirstWord(residualUserCommand);
				try {
					indexTasksRecentlyDisplayed = Integer.parseInt(indexTasksRecentlyDisplayedString) - Global.INDEX_OFFSET; // Change the line number to an array index
				} catch (NumberFormatException e) {
					return new Feedback(false, String.format(Global.MESSAGE_INVALID_FORMAT, userCommand));
				} 
				if (indexTasksRecentlyDisplayed > recentDisplayFeedback.getCommandRelatedTasks().size() - Global.INDEX_OFFSET || indexTasksRecentlyDisplayed < 0) {
					return new Feedback(false, String.format(Global.MESSAGE_NO_INDEX, indexTasksRecentlyDisplayed + Global.INDEX_OFFSET));
				}
				residualUserCommand = removeFirstWord(residualUserCommand);
				
				// Task to be marked as done
				Task doneTask = recentDisplayFeedback.getCommandRelatedTasks().get(indexTasksRecentlyDisplayed);
				
				// Index in ArrayList tasks of the Data class
				indexTasks = TaskCommander.data.getIndexOf(doneTask);
				
				return TaskCommander.data.done(indexTasks);
				
			case OPEN:
				
				// Index in ArrayList tasksRecentlyDisplayed
				indexTasksRecentlyDisplayedString = getFirstWord(residualUserCommand);
				try {
					indexTasksRecentlyDisplayed = Integer.parseInt(indexTasksRecentlyDisplayedString) - Global.INDEX_OFFSET; // Change the line number to an array index
				} catch (NumberFormatException e) {
					return new Feedback(false, String.format(Global.MESSAGE_INVALID_FORMAT, userCommand));
				} 
				if (indexTasksRecentlyDisplayed > recentDisplayFeedback.getCommandRelatedTasks().size() - Global.INDEX_OFFSET || indexTasksRecentlyDisplayed < 0) {
					return new Feedback(false, String.format(Global.MESSAGE_NO_INDEX, indexTasksRecentlyDisplayed + Global.INDEX_OFFSET));
				}
				residualUserCommand = removeFirstWord(residualUserCommand);
				
				// Task to be marked as done
				Task undoneTask = recentDisplayFeedback.getCommandRelatedTasks().get(indexTasksRecentlyDisplayed);
				
				// Index in ArrayList tasks of the Data class
				indexTasks = TaskCommander.data.getIndexOf(undoneTask);
				
				return TaskCommander.data.open(indexTasks);
				
				
			case DISPLAY:
				
				// DateTime period to be displayed
				boolean isDatePeriodRestricted = false;
				Date startDate = null;
				Date endDate = null;
				List<Date> DatePeriod = TaskCommander.parser.determineTaskDateTime(residualUserCommand);	// returns null if no date found in given String
				if (DatePeriod != null) { // DatePeriod given
					isDatePeriodRestricted = true;
					if (DatePeriod.size() == 2) {
						startDate = DatePeriod.get(0);
						endDate = DatePeriod.get(1);
					} else if (DatePeriod.size() == 1) {
						startDate = new Date(); // current DateTime
						endDate = DatePeriod.get(0);
					} else { // no DateTime period
						return new Feedback(false,String.format(Global.MESSAGE_INVALID_FORMAT, userCommand));
					}
				}

				// TaskType to be displayed
				boolean isTaskTypeRestricted;
				boolean shownFloatingTask = Arrays.asList(userCommand.split(" ")).contains("none");
				boolean shownDeadlineTask = Arrays.asList(userCommand.split(" ")).contains("deadline");
				boolean shownTimedTask = Arrays.asList(userCommand.split(" ")).contains("timed");
				if ((!shownFloatingTask && !shownDeadlineTask && !shownTimedTask) || (shownFloatingTask && shownDeadlineTask && shownTimedTask)) {
					isTaskTypeRestricted = false;
				} else {
					isTaskTypeRestricted = true;
				}
				
				// Status to be displayed
				boolean isStatusRestricted = false;
				boolean done = false; // false = open, true = done
				boolean shownDone = Arrays.asList(userCommand.split(" ")).contains("done");
				boolean shownOpen = Arrays.asList(userCommand.split(" ")).contains("open");
				if ((!shownDone && !shownOpen) || (shownDone && shownOpen) ) {
					isStatusRestricted = false;
				} else {
					isStatusRestricted = true;
					if(shownDone) {
						done = true;
					} else {
						done = false;
					}
				}
				
				// Case 1: No restrictions of display
				if (!isDatePeriodRestricted && !isTaskTypeRestricted && !isStatusRestricted) {
					recentDisplayFeedback = TaskCommander.data.displayTasks();
					return recentDisplayFeedback;
				// Case 2: With restrictions of display
				} else {
					recentDisplayFeedback = TaskCommander.data.displayTasks(isDatePeriodRestricted, startDate, endDate, isTaskTypeRestricted, shownFloatingTask, shownDeadlineTask, shownTimedTask, isStatusRestricted, done);
					return recentDisplayFeedback;
				}
				
			case DELETE:
				
				// Index in ArrayList tasksRecentlyDisplayed
				indexTasksRecentlyDisplayedString = getFirstWord(residualUserCommand);
				try {
					indexTasksRecentlyDisplayed = Integer.parseInt(indexTasksRecentlyDisplayedString) - Global.INDEX_OFFSET; // Change the line number to an array index
				} catch (NumberFormatException e) {
					return new Feedback(false, String.format(Global.MESSAGE_INVALID_FORMAT, userCommand));
				} 
				if (indexTasksRecentlyDisplayed > recentDisplayFeedback.getCommandRelatedTasks().size() - Global.INDEX_OFFSET || indexTasksRecentlyDisplayed < 0) {
					return new Feedback(false, String.format(Global.MESSAGE_NO_INDEX, indexTasksRecentlyDisplayed + Global.INDEX_OFFSET));
				}
				
				// Task to be deleted
				Task deletedTask = recentDisplayFeedback.getCommandRelatedTasks().get(indexTasksRecentlyDisplayed);
				
				// Index in ArrayList tasks of the Data class
				indexTasks = TaskCommander.data.getIndexOf(deletedTask);
				
				return TaskCommander.data.deleteTask(indexTasks);
				
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
				
			case SYNC: 
			/* TODO
				if (TaskCommander.syncHandler == null) {
					TaskCommander.getSyncHandler();
				}
				return TaskCommander.syncHandler.sync();
			*/
				return new Feedback(false,"out of order");
				
			case UNDO: 
				
				
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
	
	/* TODO not used anymore
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
