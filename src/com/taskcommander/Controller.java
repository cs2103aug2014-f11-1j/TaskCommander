package com.taskcommander;
import java.util.ArrayList;
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
			return new Feedback(false,Global.MESSAGE_NO_COMMAND, TaskCommander.data.getAllTasks());
		}

		Global.CommandType commandType = TaskCommander.parser.determineCommandType(userCommand);
		
		switch (commandType) {
		
			case ADD:
				
				// taskName
				String taskName = TaskCommander.parser.determineTaskName(userCommand);
				if (taskName == null) {
					return new Feedback(false,String.format(Global.MESSAGE_INVALID_FORMAT, userCommand), TaskCommander.data.getAllTasks());
				}
				
				// taskDateTime (3 cases depending on taskType)
				List<Date> taskDateTime = TaskCommander.parser.determineTaskDateTime(userCommand, false);
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
					return new Feedback(false,String.format(Global.MESSAGE_INVALID_FORMAT, userCommand), TaskCommander.data.getAllTasks());
				}
				
			case UPDATE: case DONE: case OPEN: case DELETE:
				
				// Index in recent display of tasks
				int indexDisplayedTasks = TaskCommander.parser.determineIndex(userCommand);
				ArrayList<Task> displayedTasks = recentDisplayFeedback.getCommandRelatedTasks();
				if (indexDisplayedTasks > displayedTasks.size() - Global.INDEX_OFFSET || indexDisplayedTasks < 0) {
					return new Feedback(false, String.format(Global.MESSAGE_NO_INDEX, indexDisplayedTasks + Global.INDEX_OFFSET), TaskCommander.data.getAllTasks());
				}
				
				// Task to be updated
				Task displayedTask = displayedTasks.get(indexDisplayedTasks);
				
				// Index in tasks list of Data
				int indexTasks = TaskCommander.data.getIndexOf(displayedTask);
				
				if (commandType == Global.CommandType.UPDATE) {

						// New taskName, if stated
						String oldTaskName = displayedTask.getName();
						String newTaskName = TaskCommander.parser.determineTaskName(userCommand);
						if (newTaskName == null) {
							newTaskName = oldTaskName;
						}
						
						// New taskDateTime and taskType, if stated
						List<Date> newTaskDateTime = TaskCommander.parser.determineTaskDateTime(userCommand, true);	// returns null if no date found in given String
						Task.TaskType oldTaskType = displayedTask.getType();
						Task.TaskType newTaskType = oldTaskType;
						Date newStartDate = null;
						Date newEndDate = null;
						if ((newTaskDateTime == null) && (TaskCommander.parser.containsParameter(userCommand, "none"))) {	// "none" is a keyword used by the user to indicate, that he wants to change a DatedTask to a FloatingTask
							newTaskType = Task.TaskType.FLOATING;
						} else if (newTaskDateTime != null) {
							if (newTaskDateTime.size() == 1) {
								newTaskType = Task.TaskType.DEADLINE;
								newEndDate = newTaskDateTime.get(0);
							} else if (newTaskDateTime.size() == 2) {
								newTaskType = Task.TaskType.TIMED;
								newStartDate = newTaskDateTime.get(0);
								newEndDate = newTaskDateTime.get(1);
							} else {
								return new Feedback(false, String.format(Global.MESSAGE_INVALID_FORMAT, userCommand), TaskCommander.data.getAllTasks());
							}
						}
						
						// No changes at all, that is, no new DateTime, Name, or "none" given
						if ((newTaskDateTime == null) && (newTaskName == oldTaskName) && (oldTaskType == newTaskType)) {	// Invalid Format when input: update 1 none for a floatingTask
							return new Feedback(false,String.format(Global.MESSAGE_INVALID_FORMAT, userCommand), TaskCommander.data.getAllTasks());
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
						
				} else if (commandType == Global.CommandType.DONE) {
					
					return TaskCommander.data.done(indexTasks);
					
				} else if (commandType == Global.CommandType.OPEN) {
					
					return TaskCommander.data.open(indexTasks);
				
				} else if (commandType == Global.CommandType.DELETE) {
					
					return TaskCommander.data.deleteTask(indexTasks);
					
				}
				
			case DISPLAY:
				
				// DateTime period to be displayed
				boolean isDatePeriodRestricted = false;
				Date startDate = null;
				Date endDate = null;
				List<Date> DatePeriod = TaskCommander.parser.determineTaskDateTime(userCommand, false);	// returns null if no date found in given String
		
				if (DatePeriod != null) { // DatePeriod given
					if (DatePeriod.size() == 2) {
						isDatePeriodRestricted = true;
						startDate = DatePeriod.get(0);
						endDate = DatePeriod.get(1);
					} else if (DatePeriod.size() == 1) {
						isDatePeriodRestricted = true;
						startDate = new Date(); // current DateTime
						endDate = DatePeriod.get(0);
					} else { // no DateTime period
						return new Feedback(false,String.format(Global.MESSAGE_INVALID_FORMAT, userCommand), TaskCommander.data.getAllTasks());
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
				
			case CLEAR:
				if (isSingleWord(userCommand)) {
					return TaskCommander.data.clearTasks();
				} else {
					return new Feedback(false,String.format(Global.MESSAGE_INVALID_FORMAT, userCommand), TaskCommander.data.getAllTasks());
				}
				
			case HELP:
				if (isSingleWord(userCommand)) {
					return new Feedback(false,Global.MESSAGE_HELP, TaskCommander.data.getAllTasks());
				} else {
					return new Feedback(false,String.format(Global.MESSAGE_INVALID_FORMAT, userCommand), TaskCommander.data.getAllTasks());
				}
				
			case SYNC: 
			/* TODO
				if (TaskCommander.syncHandler == null) {
					TaskCommander.getSyncHandler();
				}
				return TaskCommander.syncHandler.sync();
			*/
				return new Feedback(false,"out of order", TaskCommander.data.getAllTasks());
				
			case UNDO: 
				
				return new Feedback(false,"to be implemented", TaskCommander.data.getAllTasks());
				
			case INVALID:
				return new Feedback(false,String.format(Global.MESSAGE_INVALID_FORMAT, userCommand), TaskCommander.data.getAllTasks());
				
			case EXIT:
				System.exit(0);
				
			default:
				return new Feedback(false,String.format(Global.MESSAGE_INVALID_FORMAT, userCommand), TaskCommander.data.getAllTasks());
		}
	}
	
	/**
	 * Auxiliary methods
	 */
	private static boolean isSingleWord(String userCommand) {
		return getNumberOfWords(userCommand) == 1;
	}
	
	private static int getNumberOfWords(String userCommand) {
		String[] allWords = userCommand.trim().split("\\s+");
		return allWords.length;
	}
}
