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
	 * This list contains all tasks which were recently displayed by the UI. Memorizing the 
	 * tasks which have been displayed recently by the UI is needed by the update, delete, done and open methods.
	 */
	private ArrayList<Task> displayedTasks;	
	
	/**
	 * This list contains the state of the tasks before the latest add/update/done/open/delete command.
	 * Memorizing the commands which have been executed recently is needed by the undo method.
	 * 
	 * TODO
	 */
	// private ArrayList<Task> recentAddUpdateMarkDeleteClearFeedback;

	/**
	 * This operation parses the command from the user and executes it if valid. Afterwards a 
	 * feedback String is returned.
	 * 
	 * @param  userCommand  command given by user
	 * @return              feedback to the UI
	 */
	public String executeCommand(String userCommand) {	
		if (userCommand == null | userCommand == "") {
			return String.format(Global.MESSAGE_NO_COMMAND);
		}

		Global.CommandType commandType = TaskCommander.parser.determineCommandType(userCommand);
		
		switch (commandType) {
		
			case ADD:
				
				// taskName
				String taskName = TaskCommander.parser.determineTaskName(userCommand);
				if (taskName == null) {
					return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
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
					return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
				}
				
			case UPDATE: case DONE: case OPEN: case DELETE:
				
				// Index in recent display of tasks
				int indexDisplayedTasks = TaskCommander.parser.determineIndex(userCommand);
				if (indexDisplayedTasks > displayedTasks.size() - Global.INDEX_OFFSET || indexDisplayedTasks < 0) {
					return String.format(Global.MESSAGE_NO_INDEX, indexDisplayedTasks + Global.INDEX_OFFSET);
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
								return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
							}
						}
						
						// No changes at all, that is, no new DateTime, Name, or "none" given
						if ((newTaskDateTime == null) && (newTaskName == oldTaskName) && (oldTaskType == newTaskType)) {	// Invalid Format when input: update 1 none for a floatingTask
							return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
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
						return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
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
					displayedTasks = TaskCommander.data.getCopiedTasks();
					return String.format(Global.MESSAGE_DISPLAYED, "all");
					
				// Case 2: With restrictions of display
				} else {
					displayedTasks = TaskCommander.data.getCopiedTasks(isDatePeriodRestricted, startDate, endDate, isTaskTypeRestricted, shownFloatingTask, shownDeadlineTask, shownTimedTask, isStatusRestricted, done);
					String displayRestriction = "tasks ";
					if (isDatePeriodRestricted) {
						displayRestriction = "within ["+ Global.dayFormat.format(startDate)+ " "+ Global.timeFormat.format(startDate)+ "-"+ Global.timeFormat.format(endDate) + "] ";
					}
					if (isTaskTypeRestricted) {
						displayRestriction = "of the type ";
						if (shownFloatingTask) {
							displayRestriction += "none ";
						}
						if (shownDeadlineTask) {
							displayRestriction += "timed ";
						}
						if (shownTimedTask) {
							displayRestriction += "deadline ";
						}
					}
					if (isStatusRestricted) {
						displayRestriction = "of the status ";
						if (done) {
							displayRestriction += "done ";
						} else {
							displayRestriction += "open ";
						}
					}
					return String.format(Global.MESSAGE_DISPLAYED, displayRestriction);
			}
				
			case CLEAR:
				if (isSingleWord(userCommand)) {
					return TaskCommander.data.clearTasks();
				} else {
					return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
				}
				
			case HELP:
				if (isSingleWord(userCommand)) {
					return String.format(Global.MESSAGE_HELP);
				} else {
					return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
				}
				
			case SYNC: 
			/* TODO
				if (TaskCommander.syncHandler == null) {
					TaskCommander.getSyncHandler();
				}
				return TaskCommander.syncHandler.sync();
			*/
				return String.format("out of order");
				
			case UNDO: 
				
				return String.format("to be implemented");
				
			case INVALID:
				return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
				
			case EXIT:
				System.exit(0);
				
			default:
				return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
		}
	}
	
	
	/**
	 * Returns the tasks to be displayed in the UI's table.
	 */
	public ArrayList<Task> getDisplayedTasks() {
		executeCommand("display");
		return displayedTasks;
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
