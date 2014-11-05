package com.taskcommander;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
//@author A0128620M
/**
 * Singleton class that determines and executes commands from user input, and passes feedback to the UI.
 * Acts as a facade class and delegates commands to other classes.
 * 
 * Delegates data manipulation commands to the Data component. 
 * Delegates sync commands to the Google Integration component.
 * Handles view settings adjusted by display commands.
 */
public class Controller {

	// Constructor, Variables and Logger
	private static Logger logger = Logger.getLogger(Controller.class.getName());	//TODO add logs

	// Contains all tasks recently displayed by the UI. Needed by the update, delete, done and open commands.
	private ArrayList<Task> displayedTasks;	

	// Variables for display settings, adjusted by display command
	String displaySettingsDescription;

	boolean isDateRestricted;
	Date startDateRestriction;
	Date endDateRestriction;

	boolean isTaskTypeRestricted;
	boolean areFloatingTasksDisplayed;
	boolean areDeadlineTasksDisplayed;
	boolean areTimedTasksDisplayed;

	boolean isStatusRestricted;
	boolean areDoneTasksDisplayed;
	boolean areOpenTasksDisplayed;

	boolean isSearchRestricted;
	ArrayList<String> searchedWordsAndPhrases;

	// Singleton instance for Controller
	private static Controller theOne;

	private Controller(){
		setDefaultDisplaySettings();
	}

	/**
	 * Returns the only instance of Controller.
	 * @return  Controller instance.
	 */
	public static Controller getInstance(){
		if (theOne == null) {    
			theOne = new Controller();
		}
		return theOne;
	}

	/**
	 * Parses the command from the user and executes it if valid. Afterwards a 
	 * feedback String is returned.
	 * @param  userCommand  
	 * @return              Feedback to the UI
	 */
	public String executeCommand(String userCommand) {	
		if (userCommand == null | userCommand == "") {
			return String.format(Global.MESSAGE_NO_COMMAND);
		}

		Global.CommandType commandType = TaskCommander.parser.determineCommandType(userCommand);

		switch (commandType) {
		case ADD:
			return addTask(userCommand);
		case UPDATE: 
			return updateTask(userCommand);
		case DONE: 
			return doneTask(userCommand);
		case OPEN: 
			return openTask(userCommand);
		case DELETE:
			return deleteTask(userCommand);
		case DISPLAY:
			return displayTasks(userCommand);
		case SEARCH:	
			return searchForTasks(userCommand);
		case CLEAR:
			return clearTasks(userCommand);
		case SYNC: 
			return syncTasks(userCommand);			
		case UNDO: 
			return undoTask(userCommand);
		case EXIT:
			System.exit(0);
		default:
			return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);		
		}
	}

	// Command methods
	private String addTask(String userCommand) {
		String taskName = TaskCommander.parser.determineTaskName(userCommand);
		List<Date> taskDateTime = TaskCommander.parser.determineTaskDateTime(userCommand);

		if (isSingleWord(userCommand) || taskName == null || isTaskDateTimeInvalid(taskDateTime)) {
			return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
		} else {
			return addTaskToData(taskName, taskDateTime);
		}
	}

	private String updateTask(String userCommand) {
		int indexOfRelatedTaskInDisplayedTasks = TaskCommander.parser.determineIndex(userCommand) - Global.INDEX_OFFSET;
		if (isIndexDisplayedTasksInvalid(indexOfRelatedTaskInDisplayedTasks)) {
			return String.format(Global.MESSAGE_NO_INDEX, indexOfRelatedTaskInDisplayedTasks + Global.INDEX_OFFSET);
		}

		Task relatedTask = displayedTasks.get(indexOfRelatedTaskInDisplayedTasks);
		String newTaskName = TaskCommander.parser.determineTaskName(userCommand);
		List<Date> newTaskDateTime = TaskCommander.parser.determineTaskDateTime(userCommand);

		if (isSingleWord(userCommand) || isTaskDateTimeInvalid(newTaskDateTime)) {
			return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
		}

		// "none" is a keyword used by the user to indicate, that he wants to change a DatedTask to a FloatingTask
		boolean removeExistingDate = TaskCommander.parser.containsParameter(userCommand, "none");
		if ((newTaskDateTime == null) && !removeExistingDate && (newTaskName == null)) {	// no changes at all
			//TODO: @Andy: If there are no changes to be made, you can simply do nothing and return a success message
			// because it's not quite an invalid command format, and the user did nothing wrong.
			return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
		}

		return updateTaskInData(relatedTask, newTaskName, newTaskDateTime, removeExistingDate);
	}

	private String doneTask(String userCommand) {
		if (isSingleWord(userCommand)) {
			return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
		}

		int indexOfRelatedTaskInDisplayedTasks = TaskCommander.parser.determineIndex(userCommand) - Global.INDEX_OFFSET;
		if (isIndexDisplayedTasksInvalid(indexOfRelatedTaskInDisplayedTasks)) {
			return String.format(Global.MESSAGE_NO_INDEX, indexOfRelatedTaskInDisplayedTasks + Global.INDEX_OFFSET);
		}

		Task relatedTask = displayedTasks.get(indexOfRelatedTaskInDisplayedTasks);
		return TaskCommander.data.done(TaskCommander.data.getIndexOf(relatedTask));
	}

	private String openTask(String userCommand) {
		if (isSingleWord(userCommand)) {
			return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
		}

		int indexOfRelatedTaskInDisplayedTasks = TaskCommander.parser.determineIndex(userCommand) - Global.INDEX_OFFSET;
		if (isIndexDisplayedTasksInvalid(indexOfRelatedTaskInDisplayedTasks)) {
			return String.format(Global.MESSAGE_NO_INDEX, indexOfRelatedTaskInDisplayedTasks + Global.INDEX_OFFSET);
		}

		Task relatedTask = displayedTasks.get(indexOfRelatedTaskInDisplayedTasks);
		return TaskCommander.data.open(TaskCommander.data.getIndexOf(relatedTask));
	}

	private String deleteTask(String userCommand) {
		if (isSingleWord(userCommand)) {
			return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
		}

		int indexOfRelatedTaskInDisplayedTasks = TaskCommander.parser.determineIndex(userCommand) - Global.INDEX_OFFSET;
		System.out.println("Index is:");
		System.out.println(indexOfRelatedTaskInDisplayedTasks);
		if (isIndexDisplayedTasksInvalid(indexOfRelatedTaskInDisplayedTasks)) {
			return Global.MESSAGE_NO_INDEX;
		}

		Task relatedTask = displayedTasks.get(indexOfRelatedTaskInDisplayedTasks);
		return TaskCommander.data.deleteTask(TaskCommander.data.getIndexOf(relatedTask));
	}

	private String displayTasks(String userCommand) {
		List<Date> taskDateTimes = TaskCommander.parser.determineTaskDateTime(userCommand);
		if (isTaskDateTimeInvalid(taskDateTimes)) {
			return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
		}

		updateDisplayRestrictions(userCommand, taskDateTimes);
		setDisplaySettingsDescription();

		return String.format(Global.MESSAGE_DISPLAYED, displaySettingsDescription);
	}

	private String searchForTasks(String userCommand) {
		searchedWordsAndPhrases = TaskCommander.parser.determineSearchedWords(userCommand);	
		if (searchedWordsAndPhrases == null) {
			return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
		}
		isSearchRestricted = true;

		resetDisplayRestrictions();

		return String.format(Global.MESSAGE_SEARCHED, displaySettingsDescription);
	}


	private String clearTasks(String userCommand) {
		if (isSingleWord(userCommand)) {
			return TaskCommander.data.clearTasks();
		} else {
			return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
		}
	}

	private String syncTasks(String userCommand) {
		if (isSingleWord(userCommand)) {
			if (TaskCommander.syncHandler == null) {
				TaskCommander.getSyncHandler();
			}
			return TaskCommander.syncHandler.sync();
		} else {
			return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
		}	
	}

	private String undoTask(String userCommand) {
		if (isSingleWord(userCommand)) {
			return TaskCommander.data.undo();
		} else {
			return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
		}
	}

	// For displayed tasks
	/**
	 * Returns the tasks which are supposed to be displayed by the UI according to the current display settings.
	 * @return      ArrayList with tasks to be displayed
	 */
	public ArrayList<Task> getDisplayedTasks() {
		if (!isDateRestricted && !isTaskTypeRestricted && !isStatusRestricted  && !isSearchRestricted) {
			displayedTasks = TaskCommander.data.getCopiedTasks();
		} else {
			displayedTasks = TaskCommander.data.getCopiedTasks(isDateRestricted, startDateRestriction, endDateRestriction, 
					isTaskTypeRestricted, areFloatingTasksDisplayed, areDeadlineTasksDisplayed, areTimedTasksDisplayed, 
					isStatusRestricted, areDoneTasksDisplayed, areOpenTasksDisplayed, isSearchRestricted, searchedWordsAndPhrases);
		}
		return displayedTasks;
	}

	public String getDisplaySettingsDescription() {
		return displaySettingsDescription;
	}

	// Helper methods
	/**
	 * Checks whether the given list is valid, that is, contains either one or two DateTimes.
	 * @param  taskDateTimes  	List containing DateTimes
	 * @return              	If list is valid
	 */
	private boolean isTaskDateTimeInvalid(List<Date> taskDateTimes) {
		if (taskDateTimes != null && taskDateTimes.size() > 2) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Processes the addition of the task to the respective method in Data.
	 * @param  taskDateTimes  	List containing DateTimes
	 * @param  taskName  		Task name
	 * @return              	Feedback to the UI
	 */
	private String addTaskToData(String taskName, List<Date> taskDateTime) {
		if (taskDateTime == null) { 			
			return TaskCommander.data.addFloatingTask(taskName);
		} else if (taskDateTime.size() == 1 ) { 	
			return TaskCommander.data.addDeadlineTask(taskName, taskDateTime.get(0));
		} else { 
			assert (taskDateTime.size() <= 2);	//TODO
			Date startDate = taskDateTime.get(0);
			Date endDate = taskDateTime.get(1);
			return TaskCommander.data.addTimedTask(taskName, startDate, endDate);
		}
	}

	/**
	 * Checks whether the given index is valid in respect to the recently displayed tasks list.
	 * @param  indexDisplayedTasks  	Index of related task in displayedTasks list
	 * @return              			If valid
	 */
	private boolean isIndexDisplayedTasksInvalid(int indexDisplayedTasks) {
		if (indexDisplayedTasks > displayedTasks.size() - Global.INDEX_OFFSET || indexDisplayedTasks < 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Processes the update of the task to the respective method in Data.
	 * @param  TODO
	 */
	private String updateTaskInData(Task relatedTask, String newTaskName, List<Date> newTaskDateTime, boolean removeExistingDate) {
		Task.TaskType newTaskType;
		Date newStartDate = null;
		Date newEndDate = null;

		if (newTaskDateTime != null) {
			if (newTaskDateTime.size() == 1) {
				newTaskType = Task.TaskType.DEADLINE;
				newEndDate = newTaskDateTime.get(0);
			} else {
				assert (newTaskDateTime.size() <= 2); //TODO: @Andy: is this assert used properly? 
				newTaskType = Task.TaskType.TIMED;
				newStartDate = newTaskDateTime.get(0);
				newEndDate = newTaskDateTime.get(1);
			}
		} else if (removeExistingDate) {	
			newTaskType = Task.TaskType.FLOATING;
		} else {
			Task.TaskType oldTaskType = relatedTask.getType();
			newTaskType = oldTaskType;
		}

		int indexOfRelatedTask = TaskCommander.data.getIndexOf(relatedTask);
		switch (newTaskType) {
		case FLOATING:
			return TaskCommander.data.updateToFloatingTask(indexOfRelatedTask, newTaskName);
		case DEADLINE:
			return TaskCommander.data.updateToDeadlineTask(indexOfRelatedTask, newTaskName, newEndDate);
		default:
			return TaskCommander.data.updateToTimedTask(indexOfRelatedTask, newTaskName, newStartDate, newEndDate);
		}
	}

	// Sets the default values of the display settings (upcoming open tasks within the next week). 
	private void setDefaultDisplaySettings() {
		displaySettingsDescription = "Period: one week from now | Status: open";

		isDateRestricted = true;
		Calendar calendar = Calendar.getInstance();
		startDateRestriction = calendar.getTime();
		calendar.add(Calendar.WEEK_OF_YEAR, 1);
		endDateRestriction = calendar.getTime();

		resetTaskTypeRestrictionOfDisplaySettings();

		isStatusRestricted = true;
		areDoneTasksDisplayed = false; 
		areOpenTasksDisplayed = true;

		resetSearchRestrictionOfDisplaySettings();
	}	

	/**
	 * Sets date, task type and status restrictions, and resets search restrictions
	 * for display settings.
	 * @param userCommand
	 * @param taskDateTimes
	 */
	private void updateDisplayRestrictions(String userCommand, List<Date> taskDateTimes) {
		setDateRestrictionOfDisplaySettings(taskDateTimes);
		setTaskTypeRestrictionsOfDisplaySettings(userCommand);
		setStatusRestrictionOfDisplaySettings(userCommand);
		resetSearchRestrictionOfDisplaySettings();
	}

	/**
	 * Resets date, task type and status restrictions for display settings.
	 */
	private void resetDisplayRestrictions() {
		resetDateRestrictionOfDisplaySettings();
		resetTaskTypeRestrictionOfDisplaySettings();
		resetStatusRestrictionOfDisplaySettings();
	}

	/**
	 * Sets the date restrictions of the display settings.
	 * @param  taskDateTimes  	list containing DateTimes
	 */
	private void setDateRestrictionOfDisplaySettings(List<Date> taskDateTimes) {
		if (taskDateTimes != null) {
			if (taskDateTimes.size() == 1) {
				isDateRestricted = true;
				startDateRestriction = null; 
				endDateRestriction = taskDateTimes.get(0);
			} else if (taskDateTimes.size() == 2) {
				isDateRestricted = true;
				startDateRestriction = taskDateTimes.get(0);
				endDateRestriction = taskDateTimes.get(1);
			}
		} else {
			resetDateRestrictionOfDisplaySettings();
			startDateRestriction = null;
			endDateRestriction = null;
		}
	}

	/**
	 * Sets the task type restrictions of the display settings.
	 * @param  userCommand 
	 */
	private void setTaskTypeRestrictionsOfDisplaySettings(String userCommand) {
		areFloatingTasksDisplayed = TaskCommander.parser.containsParameter(userCommand, "none");
		areDeadlineTasksDisplayed = TaskCommander.parser.containsParameter(userCommand, "deadline");
		areTimedTasksDisplayed = TaskCommander.parser.containsParameter(userCommand, "timed");
		if ((!areFloatingTasksDisplayed && !areDeadlineTasksDisplayed && !areTimedTasksDisplayed) || (areFloatingTasksDisplayed && areDeadlineTasksDisplayed && areTimedTasksDisplayed)) {
			resetTaskTypeRestrictionOfDisplaySettings();
		} else {
			isTaskTypeRestricted = true;
		}
	}

	/**
	 * Sets the status restrictions of the display settings.
	 * @param  userCommand
	 */
	private void setStatusRestrictionOfDisplaySettings(String userCommand) {
		areDoneTasksDisplayed = TaskCommander.parser.containsParameter(userCommand, "done");
		areOpenTasksDisplayed = TaskCommander.parser.containsParameter(userCommand, "open");
		if ((!areDoneTasksDisplayed && !areOpenTasksDisplayed) || (areDoneTasksDisplayed && areOpenTasksDisplayed) ) {
			resetStatusRestrictionOfDisplaySettings();
		} else {
			isStatusRestricted = true;
		}
	}

	// Resets the search restrictions of the display settings.
	private void resetSearchRestrictionOfDisplaySettings() {
		isSearchRestricted = false;
		searchedWordsAndPhrases = null;
	}


	// Sets the description of the display settings.
	private void setDisplaySettingsDescription() {
		if (!isDateRestricted && !isTaskTypeRestricted && !isStatusRestricted && !isSearchRestricted) {	// no display restriction
			displaySettingsDescription = "All";
		} else {
			displaySettingsDescription = "";
			if (isDateRestricted) {
				if (startDateRestriction == null) {
					displaySettingsDescription += "Date: by "+ Global.dayFormat.format(endDateRestriction)+ " " + Global.timeFormat.format(endDateRestriction);
				} else {
					displaySettingsDescription += "Date: "+ Global.dayFormat.format(startDateRestriction)+ " " +
							Global.timeFormat.format(startDateRestriction)+ " - "+ Global.dayFormat.format(endDateRestriction)+ " " + Global.timeFormat.format(endDateRestriction);
				}
			}
			if (isTaskTypeRestricted) {
				if (!displaySettingsDescription.equals("")) {
					displaySettingsDescription += " ";
				}
				displaySettingsDescription += "Type: ";
				if (areFloatingTasksDisplayed) {
					displaySettingsDescription += "none";
				}
				if (areDeadlineTasksDisplayed && !areFloatingTasksDisplayed) {
					displaySettingsDescription += "deadline";
				} else if (areDeadlineTasksDisplayed) {
					displaySettingsDescription += ", deadline";
				}
				if (areTimedTasksDisplayed && !areFloatingTasksDisplayed && !areDeadlineTasksDisplayed) {
					displaySettingsDescription += "timed";
				} else if (areTimedTasksDisplayed) {
					displaySettingsDescription += ", timed";
				}
			}
			if (isStatusRestricted) {
				if (!displaySettingsDescription.equals("")) {
					displaySettingsDescription += " ";
				}
				displaySettingsDescription += "Status: ";
				if (areDoneTasksDisplayed) {
					displaySettingsDescription += "done";
				} else {
					displaySettingsDescription += "open";
				}
			}
			if (isSearchRestricted) {
				if (!displaySettingsDescription.equals("")) {
					displaySettingsDescription += " ";
				}
				for(String searchedWordOrPhrase : searchedWordsAndPhrases) {
					if (displaySettingsDescription.equals("")) {
						displaySettingsDescription = "Words/Phrases: "+"\""+searchedWordOrPhrase+"\"";
					} else {
						displaySettingsDescription += ", "+"\""+searchedWordOrPhrase+"\"";
					}
				}
			}
		}
	logger.log(Level.INFO, displaySettingsDescription);	
	}
	

	private void resetStatusRestrictionOfDisplaySettings() {
		isStatusRestricted = false;
	}

	private void resetTaskTypeRestrictionOfDisplaySettings() {
		isTaskTypeRestricted = false;
	}

	private void resetDateRestrictionOfDisplaySettings() {
		isDateRestricted = false;
	}

	/**
	 * Returns the number of words of given String.
	 * @param  userCommand 
	 */
	private int getNumberOfWords(String userCommand) {
		String[] allWords = userCommand.trim().split("\\s+");
		return allWords.length;
	}

	/**
	 * Returns true if given string is a single word.
	 * @param userCommand
	 * @return             If given string is a single word.
	 */
	private boolean isSingleWord(String userCommand) {
		return getNumberOfWords(userCommand) == 1;
	}
}
