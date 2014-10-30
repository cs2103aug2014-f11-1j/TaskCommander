package com.taskcommander;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents the Controller component. After receiving the user's input from the UI, 
 * the Controller determines the exact type of command and its related parameters by means of the Parser. 
 * Then the Controller processes the command to the executing methods in the Data or Google component. 
 * The regained feedback is returned to the UI. In addition, the Controller handles the view settings, 
 * which can be adjusted by the display command.
 * By hiding all other internal components like Parser, Data, Storage and so on, the Controller acts as 
 * a Facade class (Facade pattern).
 * 
 * @author A0128620M
 */

public class Controller {
	
	/* ========================= Constructor, Variables and Logger ================================== */
	
	/**
	 * Logger and related logging messages
	 */
	private static Logger logger = Logger.getLogger(Controller.class.getName());	//TODO add logs
	
	/**
	 * This list contains all tasks which were recently displayed by the UI. Memorizing the 
	 * tasks which have been displayed recently is needed by the update, delete, done and open commands.
	 */
	private ArrayList<Task> displayedTasks;	
	
	/**
	 * This variables represent the display settings, which are set to default values at the beginning of the 
	 * application, an can be adjusted afterwards using the display command.
	 */
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
	
	/**
	 * This variable is initialized with the one and only instance of the Parser class.
	 */
	private static Controller theOne;
	
	/**
	 * Private Constructor, which is only called by the getInstance() method.
	 */
	private Controller(){
		setDefaultDisplaySettings();
	}
	
	/* ============================================ API ============================================= */
	
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
				if (getNumberOfWords(userCommand) < 2) {
					return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
				}
				
				String taskName = TaskCommander.parser.determineTaskName(userCommand);
				if (taskName == null) {
					return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
				}
				
				List<Date> taskDateTime = TaskCommander.parser.determineTaskDateTime(userCommand);
				if (isTaskDateTimeInvalid(taskDateTime)) {
					return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
				}
				
				return addTaskToData(taskName, taskDateTime);
				
			case UPDATE: case DONE: case OPEN: case DELETE:
				if (getNumberOfWords(userCommand) < 2) {
					return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
				}
				
				int indexOfRelatedTaskInDisplayedTasks = TaskCommander.parser.determineIndex(userCommand) - Global.INDEX_OFFSET;
				if (isIndexDisplayedTasksInvalid(indexOfRelatedTaskInDisplayedTasks)) {
					return String.format(Global.MESSAGE_NO_INDEX, indexOfRelatedTaskInDisplayedTasks + Global.INDEX_OFFSET);
				}
				
				Task relatedTask = displayedTasks.get(indexOfRelatedTaskInDisplayedTasks);
				int indexOfRelatedTaskInData = TaskCommander.data.getIndexOf(relatedTask);
				
				if (commandType == Global.CommandType.UPDATE) {
					String newTaskName = TaskCommander.parser.determineTaskName(userCommand);
					
					List<Date> newTaskDateTime = TaskCommander.parser.determineTaskDateTime(userCommand);
					if (isTaskDateTimeInvalid(newTaskDateTime)) {
						return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
					}
					
					boolean existingDateTimeIsToBeRemoved = TaskCommander.parser.containsParameter(userCommand, "none");
					if ((newTaskDateTime == null) && !existingDateTimeIsToBeRemoved && (newTaskName == null)) {		// no changes at all
						return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
					}
					
					return updateTaskInData(relatedTask, newTaskName, newTaskDateTime, existingDateTimeIsToBeRemoved);
						
				} else if (commandType == Global.CommandType.DONE) {
					return TaskCommander.data.done(indexOfRelatedTaskInData);
					
				} else if (commandType == Global.CommandType.OPEN) {
					return TaskCommander.data.open(indexOfRelatedTaskInData);
				
				} else if (commandType == Global.CommandType.DELETE) {
					return TaskCommander.data.deleteTask(indexOfRelatedTaskInData);
				}
				
			case DISPLAY:

				List<Date> taskDateTimes = TaskCommander.parser.determineTaskDateTime(userCommand);
				if (isTaskDateTimeInvalid(taskDateTimes)) {
					return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
				}
				setDateRestrictionOfDisplaySettings(taskDateTimes);
				
				setTaskTypeRestrictionsOfDisplaySettings(userCommand);
				setStatusRestrictionOfDisplaySettings(userCommand);
				resetSearchRestrictionOfDisplaySettings();
				setDisplaySettingsDescription();
				
				return String.format(Global.MESSAGE_DISPLAYED, displaySettingsDescription);
			
			case SEARCH:	
				
				searchedWordsAndPhrases = TaskCommander.parser.determineSearchedWords(userCommand);	
				if (searchedWordsAndPhrases == null) {
					return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
				}
				isSearchRestricted = true;
				
				resetDateRestrictionOfDisplaySettings();
				resetTaskTypeRestrictionOfDisplaySettings();
				resetStatusRestrictionOfDisplaySettings();
				
				return String.format(Global.MESSAGE_SEARCHED, displaySettingsDescription);

			case CLEAR:
				if (getNumberOfWords(userCommand) == 1) {
					return TaskCommander.data.clearTasks();
				} else {
					return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
				}
				
			case HELP:
				if (getNumberOfWords(userCommand) == 1) {
					return String.format(Global.MESSAGE_HELP);		//TODO @Michelle: add new Help Tab here
				} else {
					return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
				}
				
			case SYNC: 
				if (getNumberOfWords(userCommand) == 1) {
					if (TaskCommander.syncHandler == null) {		//TODO @Michelle: is this if statement necessary?
						TaskCommander.getSyncHandler();
						}
					return TaskCommander.syncHandler.sync();
				} else {
					return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
				}				
				
			case UNDO: 
				if (getNumberOfWords(userCommand) == 1) {
					return TaskCommander.data.undo();
				} else {
					return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
				}
				
			case EXIT:
				System.exit(0);
				
			default:
				return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);		
		}
	}

	/**
	 * Returns the tasks which are supposed to be displayed by the UI according to the current display settings.
	 * 
	 * @return              ArrayList with tasks to be displayed
	 */
	public ArrayList<Task> getDisplayedTasks() {
		if (!isDateRestricted && !isTaskTypeRestricted && !isStatusRestricted  && !isSearchRestricted) {
			displayedTasks = TaskCommander.data.getCopiedTasks();
		} else {
			displayedTasks = TaskCommander.data.getCopiedTasks(isDateRestricted, startDateRestriction, endDateRestriction, isTaskTypeRestricted, areFloatingTasksDisplayed, areDeadlineTasksDisplayed, areTimedTasksDisplayed, isStatusRestricted, areDoneTasksDisplayed, areOpenTasksDisplayed, isSearchRestricted, searchedWordsAndPhrases);
		}

		return displayedTasks;
	}
	
	public String getDisplaySettingsDescription() {
		return displaySettingsDescription;
	}
	
	/* ================================ Specific auxiliary methods =================================== */
	
	/**
	 * This operation checks whether the given list is valid, that is, contains either one or two DateTimes.
	 * 
	 * @param  taskDateTimes  	list containing DateTimes
	 * @return              	true if invalid, false if invalid
	 */
	private boolean isTaskDateTimeInvalid(List<Date> taskDateTimes) {
		if (taskDateTimes != null && taskDateTimes.size() > 2) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * This operation processes the addition of the task to the respective method in Data.
	 * 
	 * @param  taskDateTimes  	list containing DateTimes
	 * @param  taskName  		name of the task
	 * @return              	feedback to the UI
	 */
	private String addTaskToData(String taskName, List<Date> taskDateTime) {
		if (taskDateTime == null) { 			
			return TaskCommander.data.addFloatingTask(taskName);
		} else if (taskDateTime.size() == 1 ) { 	
			return TaskCommander.data.addDeadlineTask(taskName, taskDateTime.get(0));
		} else { 
			assert taskDateTime.size() <= 2;	//TODO
			Date startDate = taskDateTime.get(0);
			Date endDate = taskDateTime.get(1);
			return TaskCommander.data.addTimedTask(taskName, startDate, endDate);
		}
	}
	
	/**
	 * This operation checks whether the given index is valid in respect to the recently displayed tasks list.
	 * 
 	 * @param  indexDisplayedTasks  	index of relating task in displayedTasks list
	 * @return              			true if invalid, false if invalid
	 * 
	 */
	private boolean isIndexDisplayedTasksInvalid(int indexDisplayedTasks) {
		if (indexDisplayedTasks > displayedTasks.size() - Global.INDEX_OFFSET || indexDisplayedTasks < 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * This operation processes the update of the task to the respective method in Data.
	 * 
	 * @param  TODO
	 */
	private String updateTaskInData(Task relatedTask, String newTaskName, List<Date> newTaskDateTime, boolean existingDateTimeIsToBeRemoved) {
		int indexOfRelatedTaskInData = TaskCommander.data.getIndexOf(relatedTask);
		
		String oldTaskName = relatedTask.getName();
		if (newTaskName == null) {
			newTaskName = oldTaskName;
		}
		Task.TaskType newTaskType;
		Date newStartDate = null;
		Date newEndDate = null;
		
		if ((newTaskDateTime == null) && existingDateTimeIsToBeRemoved) {	// "none" is a keyword used by the user to indicate, that he wants to change a DatedTask to a FloatingTask
			newTaskType = Task.TaskType.FLOATING;
		} else if (newTaskDateTime != null) {
			if (newTaskDateTime.size() == 1) {
				newTaskType = Task.TaskType.DEADLINE;
				newEndDate = newTaskDateTime.get(0);
			} else {
				assert newTaskDateTime.size() <= 2;
				newTaskType = Task.TaskType.TIMED;
				newStartDate = newTaskDateTime.get(0);
				newEndDate = newTaskDateTime.get(1);
				if ( newEndDate.compareTo(newStartDate) < 0 ) {				// if recognized endDate would be before the startDate; that's the case when the endDate is not on the same day as the startDate.
					Calendar c = Calendar.getInstance(); 
					c.setTime(newEndDate); 
					c.add(Calendar.DATE, 1);
					newEndDate = c.getTime();
				}
			}
		} else {
			Task.TaskType oldTaskType = relatedTask.getType();
			newTaskType = oldTaskType;
		}
		
		switch (newTaskType) {
			case FLOATING:
				return TaskCommander.data.updateToFloatingTask(indexOfRelatedTaskInData, newTaskName);
			case DEADLINE:
				return TaskCommander.data.updateToDeadlineTask(indexOfRelatedTaskInData, newTaskName, newEndDate);
			default:
				return TaskCommander.data.updateToTimedTask(indexOfRelatedTaskInData, newTaskName, newStartDate, newEndDate);
		}
	}
	
	/**
	 * This operation sets the default values of the display settings (upcoming open tasks within the next week). 
	*/
	private void setDefaultDisplaySettings() {
		displaySettingsDescription = "Period: one week from now Status: open";
	
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
	 * This operation sets the date restrictions of the display settings.
	 * 
	 * @param  taskDateTimes  	list containing DateTimes
	 */
	private void setDateRestrictionOfDisplaySettings(List<Date> taskDateTimes) {
		if (taskDateTimes != null) {
			if (taskDateTimes.size() == 1) {
				isDateRestricted = true;
				startDateRestriction = new Date(Long.MIN_VALUE); 	// returns minimum DateTime
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
	 * This operation sets the task type restrictions of the display settings.
	 * 
	 * @param  userCommand  command given by user
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
	 * This operation sets the status restrictions of the display settings.
	 * 
	 * @param  userCommand  command given by user
	 */
	private void setStatusRestrictionOfDisplaySettings(String userCommand) {
		areDoneTasksDisplayed = TaskCommander.parser.containsParameter(userCommand, "done");
		areOpenTasksDisplayed = TaskCommander.parser.containsParameter(userCommand, "open");
		if ((!areDoneTasksDisplayed && !areDoneTasksDisplayed) || (areDoneTasksDisplayed && areOpenTasksDisplayed) ) {
			resetStatusRestrictionOfDisplaySettings();
		} else {
			isStatusRestricted = true;
		}
	}
	
	/**
	 * This operation resets the search restrictions of the display settings.
	 */
	private void resetSearchRestrictionOfDisplaySettings() {
		isSearchRestricted = false;
		searchedWordsAndPhrases = null;
	}
	
	/**
	 * This operation sets the description of the display settings.
	 */
	private void setDisplaySettingsDescription() {
		if (!isDateRestricted && !isTaskTypeRestricted && !isStatusRestricted && !isSearchRestricted) {	// no display restriction
			displaySettingsDescription = "all";
		} else {
			displaySettingsDescription = "";
			if (isDateRestricted) {
				displaySettingsDescription += "Date: ["+ Global.dayFormat.format(startDateRestriction)+ " "+ Global.timeFormat.format(startDateRestriction)+ "-"+ Global.timeFormat.format(endDateRestriction) + "]  ";
			}
			if (isTaskTypeRestricted) {
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
					displaySettingsDescription += "timed ";
				} else if (areTimedTasksDisplayed) {
					displaySettingsDescription += ", timed";
				}
				displaySettingsDescription += " ";
			}
			if (isStatusRestricted) {
				displaySettingsDescription += "Status: ";
				if (areDoneTasksDisplayed) {
					displaySettingsDescription += "done ";
				} else {
					displaySettingsDescription += "open ";
				}
			}
			if (isSearchRestricted) {
				for(String searchedWordOrPhrase : searchedWordsAndPhrases) {
					if (displaySettingsDescription.equals("")) {
						displaySettingsDescription = "Words/Phases: "+"\""+searchedWordOrPhrase+"\"";
					} else {
						displaySettingsDescription += ", "+"\""+searchedWordOrPhrase+"\"";
					}
				}
			}
		}
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
	
	/* ================================ General auxiliary methods =================================== */
	
	/**
	 * This operation returns the number of words the given String consists of.
	 * 
	 * @param  userCommand  command given by user
	 */
	private int getNumberOfWords(String userCommand) {
		String[] allWords = userCommand.trim().split("\\s+");
		return allWords.length;
	}
}
