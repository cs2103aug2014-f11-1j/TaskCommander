package com.taskcommander;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class represents the Controller component. After receiving the user's
 * input from the UI, the Controller determines the exact type of command and its 
 * related parameters by means of the Parser. Then the respective method
 * of the Data component is called to execute the command and the regained feedback
 * is returned to the UI. By hiding all other internal components like Parser, Data, Storage and so on,
 * the Controller acts as a Facade class (see Facade pattern).
 * 
 * @author A0128620M
 */

public class Controller {
	
	// Logging
	private static Logger logger = Logger.getLogger("Controller");
	
	/**
	 * This variable is initialized with the one and only instance of the Controller class 
	 * (see also getInstance() below)
	 */
	private static Controller theOne;
	
	/**
	 * This operation which returns either a new instance of the Controller or an existing one, if any.
	 * Therefore, it ensures that there will be only one instance of the Controller (see Singleton pattern)
	 */
	public static Controller getInstance(){
		if (theOne == null) {    
			theOne = new Controller();
		}
		return theOne;
	}
	
	/**
	 * Constructor, which sets the default values for the display restriction
	 * so that the user gets an overview of the open tasks of the next week when starting the application.
	 */
	private Controller(){
		
		// Set default display settings
		displayRestriction = "Period: one week from now Status: open";
	
		isDatePeriodRestricted = true;
		Calendar calendar = Calendar.getInstance();
		startDate = calendar.getTime();
		calendar.add(Calendar.WEEK_OF_YEAR, 1);
		endDate = calendar.getTime();
	
		isTaskTypeRestricted = false;
		shownFloatingTask = true;
		shownDeadlineTask = true;
		shownTimedTask = true;
	
		isStatusRestricted = true;
		done = false; // false = open, true = done
		
		isSearchedWordsRestricted = false;
		searchedWords = null;
	}
	
	/**
	 * This list contains all tasks which were recently displayed by the UI. Memorizing the 
	 * tasks which have been displayed recently by the UI is needed by the update, delete, done and open methods.
	 */
	private ArrayList<Task> displayedTasks;	
	
	/**
	 * This variables represent the memorized display settings, the user has been set with his last display comment.
	 */
	String displayRestriction;
	boolean isDatePeriodRestricted;
	Date startDate;
	Date endDate;
	
	boolean isTaskTypeRestricted;
	boolean shownFloatingTask;
	boolean shownDeadlineTask;
	boolean shownTimedTask;
	
	boolean isStatusRestricted;
	boolean done; // false = open, true = done
	
	boolean isSearchedWordsRestricted;
	String[] searchedWords;
	
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
			return String.format(Global.ERROR_MESSAGE_NO_COMMAND);
		}

		Global.CommandType commandType = TaskCommander.parser.determineCommandType(userCommand);
		
		switch (commandType) {
		
			case ADD:
				if (getNumberOfWords(userCommand) < 2) {
					logger.log(Level.WARNING, "Not enough parameters for add command");
					return String.format(Global.ERROR_MESSAGE_INVALID_FORMAT, userCommand);
				}
				
				// taskName
				String taskName = TaskCommander.parser.determineTaskName(userCommand);
				if (taskName == null) {
					return String.format(Global.ERROR_MESSAGE_INVALID_FORMAT, userCommand);
				}
				
				// taskDateTime
				List<Date> taskDateTime = TaskCommander.parser.determineTaskDateTime(userCommand, false);
				
				// Adding to data component depending on taskType
				// case 1: FloatingTask
				if (taskDateTime == null) { 			
					logger.log(Level.INFO, "Task to be added is FloatingTask");
					return TaskCommander.data.addFloatingTask(taskName);
				// case 2: DeadlineTask
				} else if (taskDateTime.size() == 1 ) { 	
					logger.log(Level.INFO, "Task to be added is DeadlineTask with EndDate "+taskDateTime.get(0));
					return TaskCommander.data.addDeadlineTask(taskName, taskDateTime.get(0));
				// case 3: TimedTask
				} else if (taskDateTime.size() == 2) { 
					Date startDate = taskDateTime.get(0);
					Date endDate = taskDateTime.get(1);
					logger.log(Level.INFO, "Task to be added is TimedTask with StartDate "+taskDateTime.get(0)+" and Enddate "+taskDateTime.get(1));
					if ( endDate.compareTo(startDate) < 0 ) {
						Calendar c = Calendar.getInstance(); 
						c.setTime(endDate); 
						c.add(Calendar.DATE, 1);
						endDate = c.getTime();
					}
					logger.log(Level.INFO, "Task which is really added as a TimedTask has a StartDate "+startDate+" and Enddate "+endDate);
					return TaskCommander.data.addTimedTask(taskName, startDate, endDate);
				// Invalid format, when no command parameter given
				} else {
					return String.format(Global.ERROR_MESSAGE_INVALID_FORMAT, userCommand);
				}
				
			case UPDATE: case DONE: case OPEN: case DELETE:
				
				if (getNumberOfWords(userCommand) < 2) {
					return String.format(Global.ERROR_MESSAGE_INVALID_FORMAT, userCommand);
				}
				
				// Index in DisplayedTasks
				int indexDisplayedTasks = TaskCommander.parser.determineIndex(userCommand);
				System.out.println(userCommand+indexDisplayedTasks+displayedTasks.size());
				if (indexDisplayedTasks > displayedTasks.size() - Global.INDEX_OFFSET || indexDisplayedTasks < 0) {
					return String.format(Global.ERROR_MESSAGE_NO_INDEX, indexDisplayedTasks + Global.INDEX_OFFSET);
				}
				
				// Task to be updated
				Task displayedTask = displayedTasks.get(indexDisplayedTasks);
				
				// Index in tasks list of Data component
				int indexDataTasks = TaskCommander.data.getIndexOf(displayedTask);
				
				if (commandType == Global.CommandType.UPDATE) {

						// New taskName, if stated
						String newTaskName = TaskCommander.parser.determineTaskName(userCommand);
						
						
						// New taskDateTime and taskType, if stated
						List<Date> newTaskDateTime = TaskCommander.parser.determineTaskDateTime(userCommand, true);	// returns null if no date found in given String
						
						// Invalid command format when no parameter like a new Name, DateTime or "none" given
						if ((newTaskDateTime == null) && !(TaskCommander.parser.containsParameter(userCommand, "none")) && (newTaskName == null)) {
							return String.format(Global.ERROR_MESSAGE_INVALID_FORMAT, userCommand);
						}
						
						// Updating of respective task in Data component including change of taskType if necessary
						String oldTaskName = displayedTask.getName();
						if (newTaskName == null) {
							newTaskName = oldTaskName;
						}
						Task.TaskType newTaskType;
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
								if ( newEndDate.compareTo(newStartDate) < 0 ) {
									Calendar c = Calendar.getInstance(); 
									c.setTime(newEndDate); 
									c.add(Calendar.DATE, 1);
									newEndDate = c.getTime();
								}
							} else {
								return String.format(Global.ERROR_MESSAGE_INVALID_FORMAT, userCommand);
							}
						} else {
							Task.TaskType oldTaskType = displayedTask.getType();
							newTaskType = oldTaskType;
						}
						switch (newTaskType) {
							case FLOATING:
								return TaskCommander.data.updateToFloatingTask(indexDataTasks, newTaskName);
							case DEADLINE:
								return TaskCommander.data.updateToDeadlineTask(indexDataTasks, newTaskName, newEndDate);
							case TIMED:
								return TaskCommander.data.updateToTimedTask(indexDataTasks, newTaskName, newStartDate, newEndDate);
						}
						
				} else if (commandType == Global.CommandType.DONE) {
					
					return TaskCommander.data.done(indexDataTasks);
					
				} else if (commandType == Global.CommandType.OPEN) {
					
					return TaskCommander.data.open(indexDataTasks);
				
				} else if (commandType == Global.CommandType.DELETE) {
					
					return TaskCommander.data.deleteTask(indexDataTasks);
					
				}
				
			case DISPLAY:
				
				// DatePeriod Restriction
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
						return String.format(Global.ERROR_MESSAGE_INVALID_FORMAT, userCommand);
					}
				} else {
					isDatePeriodRestricted = false;
					startDate = null;
					endDate = null;
				}

				// TaskType Restriction
				shownFloatingTask = TaskCommander.parser.containsParameter(userCommand, "none");
				shownDeadlineTask = TaskCommander.parser.containsParameter(userCommand, "deadline");
				shownTimedTask = TaskCommander.parser.containsParameter(userCommand, "timed");
				if ((!shownFloatingTask && !shownDeadlineTask && !shownTimedTask) || (shownFloatingTask && shownDeadlineTask && shownTimedTask)) {
					isTaskTypeRestricted = false;
				} else {
					isTaskTypeRestricted = true;
				}
				
				// Status Restriction
				isStatusRestricted = false;
				done = false; // false = open, true = done
				boolean shownDone = TaskCommander.parser.containsParameter(userCommand, "done");
				boolean shownOpen = TaskCommander.parser.containsParameter(userCommand, "open");
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
				
				// Reset SearchedWord Restrictions
				isSearchedWordsRestricted = false;
				searchedWords = null;
				
				// Case 1: No restrictions of display
				if (!isDatePeriodRestricted && !isTaskTypeRestricted && !isStatusRestricted) {
					displayRestriction = "all";
					return String.format(Global.MESSAGE_DISPLAYED, displayRestriction);
					
				// Case 2: With restrictions of display
				} else {
					displayRestriction = "";
					if (isDatePeriodRestricted) {
						displayRestriction += "Period: ["+ Global.dayFormat.format(startDate)+ " "+ Global.timeFormat.format(startDate)+ "-"+ Global.timeFormat.format(endDate) + "]  ";
					}
					if (isTaskTypeRestricted) {
						displayRestriction += "Type: ";
						if (shownFloatingTask) {
							displayRestriction += "none";
						}
						if (shownDeadlineTask && !shownFloatingTask) {
							displayRestriction += "deadline";
						} else if (shownDeadlineTask) {
							displayRestriction += ", deadline";
						}
						if (shownTimedTask && !shownFloatingTask && !shownDeadlineTask) {
							displayRestriction += "timed ";
						} else if (shownTimedTask) {
							displayRestriction += ", timed";
						}
						displayRestriction += " ";
					}
					if (isStatusRestricted) {
						displayRestriction += "Status: ";
						if (done) {
							displayRestriction += "done ";
						} else {
							displayRestriction += "open ";
						}
					}
					return String.format(Global.MESSAGE_DISPLAYED, displayRestriction);
			}
				
			case SEARCH:	
				
				// SearchedWords
				searchedWords = TaskCommander.parser.determineSearchedWords(userCommand);	
				
				isDatePeriodRestricted = false;
				isTaskTypeRestricted = false;
				isStatusRestricted = false;
				isSearchedWordsRestricted = true;
				
				displayRestriction = "";
				for(String searchedWord : searchedWords) {
					if (displayRestriction.equals("")) {
						displayRestriction = "Tasks containing the words "+searchedWord;
					} else {
						displayRestriction += ", "+searchedWord;
					}
				}
				return String.format(Global.MESSAGE_DISPLAYED, displayRestriction);

			case CLEAR:
				if (getNumberOfWords(userCommand) == 1) {
					return TaskCommander.data.clearTasks();
				} else {
					return String.format(Global.ERROR_MESSAGE_INVALID_FORMAT, userCommand);
				}
				
			case HELP:
				if (getNumberOfWords(userCommand) == 1) {
					return String.format(Global.MESSAGE_HELP);
				} else {
					return String.format(Global.ERROR_MESSAGE_INVALID_FORMAT, userCommand);
				}
				
			case SYNC: 
				if (getNumberOfWords(userCommand) == 1) {
					if (TaskCommander.syncHandler == null) {
					TaskCommander.getSyncHandler();
					}
					return TaskCommander.syncHandler.sync();
				} else {
					return String.format(Global.ERROR_MESSAGE_INVALID_FORMAT, userCommand);
				}				
				
			case UNDO: 
				
				return String.format("to  implemented yet");
				
			case EXIT:
				System.exit(0);
				
			default: //Invalid
				return String.format(Global.ERROR_MESSAGE_INVALID_FORMAT, userCommand);		
		}
	}
	
	/**
	 * Returns the tasks to be displayed in the UI's table.
	 */
	public ArrayList<Task> getDisplayedTasks() {
		
		// Case 1: No display restrictions
		if (!isDatePeriodRestricted && !isTaskTypeRestricted && !isStatusRestricted  && !isSearchedWordsRestricted) {
			displayedTasks = TaskCommander.data.getCopiedTasks();
		
		// Case 2: With display restrictions
		} else {
			displayedTasks = TaskCommander.data.getCopiedTasks(isDatePeriodRestricted, startDate, endDate, isTaskTypeRestricted, shownFloatingTask, shownDeadlineTask, shownTimedTask, isStatusRestricted, done, isSearchedWordsRestricted, searchedWords);
		}

		return displayedTasks;
	}
	
	// Auxiliary methods:
	
	/**
	 * This operation returns the number of words the given String consists of.
	 */
	private int getNumberOfWords(String userCommand) {
		String[] allWords = userCommand.trim().split("\\s+");
		return allWords.length;
	}
	
}
