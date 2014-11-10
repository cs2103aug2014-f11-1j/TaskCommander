package com.taskcommander;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//@author A0128620M
/**
 * Singleton class that determines and executes commands from user input, and
 * passes feedback to the UI. Acts as a facade class and delegates commands to
 * other classes. Delegates data manipulation commands to the Data component.
 * Delegates sync commands to the Google Integration component. Handles view
 * settings adjusted by display commands.
 */

public class Controller {
    private static Logger logger = Logger.getLogger(Controller.class.getName());
    
    // Tasks currently displayed by the UI needed by commands with index.
    private ArrayList<Task> displayedTasks;

    // Display settings, adjusted by display command.
    private String displaySettingsDescription;

    private boolean isDateRestricted;
    private Date startDateRestriction;
    private Date endDateRestriction;

    private  boolean isTaskTypeRestricted;
    private boolean areFloatingTasksDisplayed;
    private boolean areDeadlineTasksDisplayed;
    private boolean areTimedTasksDisplayed;

    private boolean isStatusRestricted;
    private boolean areDoneTasksDisplayed;
    private boolean areOpenTasksDisplayed;

    private boolean isSearchRestricted;
    private ArrayList<String> searchedWordsAndPhrases;

    // Singleton instance of Controller.
    private static Controller theOne;

    private Controller() {
        setDefaultDisplaySettings();
    }

    /**
     * Returns the only instance of Controller.
     * 
     * @return Controller instance
     */
    public static Controller getInstance() {
        if (theOne == null) {
            theOne = new Controller();
        }
        return theOne;
    }

    /**
     * Parses the command from the user and executes it if valid. Afterwards a
     * feedback String is returned.
     * 
     * @param userCommand
     * @return feedback for UI
     */
    public String executeCommand(String userCommand) {
        if (userCommand == null | userCommand == "") {
            return String.format(Global.MESSAGE_NO_COMMAND);
        }

        Global.CommandType commandType = TaskCommander.parser
            .determineCommandType(userCommand);

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
	
    /**
     * Returns the tasks which are supposed to be displayed by the UI according
     * to the current display settings.
     * 
     * @return tasks to be displayed
     */
    public ArrayList<Task> getDisplayedTasks() {
        if (noDisplayRestrictions()) {
            displayedTasks = TaskCommander.data.getCopiedTasks();
        } else {
            displayedTasks = TaskCommander.data.getCopiedTasks(
                isDateRestricted, startDateRestriction, endDateRestriction,
                isTaskTypeRestricted, areFloatingTasksDisplayed,
                areDeadlineTasksDisplayed, areTimedTasksDisplayed,
                isStatusRestricted, areDoneTasksDisplayed,
                areOpenTasksDisplayed, isSearchRestricted,
                searchedWordsAndPhrases);
        }
        return displayedTasks;
    }

    /**
     * Returns the current display settings consolidated as a String.
     * 
     * @return description of display settings
     */
    public String getDisplaySettingsDescription() {
        return displaySettingsDescription;
    }
	
    // Checks the add command from the user and forwards it to Data.
    String addTask(String userCommand) {
        if (hasNoParamters(userCommand)) {
            return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
        }

        String taskName = TaskCommander.parser.determineTaskName(userCommand);
        List<Date> taskDateTime = TaskCommander.parser
            .determineTaskDateTime(userCommand);

        if (taskName == null || isTaskDateTimeInvalid(taskDateTime)) {
            return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
        } else {
            return addTaskToData(taskName, taskDateTime);
        }
    }

    // Checks the update command from the user and forwards it to Data.
    private String updateTask(String userCommand) {
        if (hasNoParamters(userCommand)) {
            return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
        }

        int indexDisplayedTasks = TaskCommander.parser
            .determineIndex(userCommand) - Global.INDEX_OFFSET;
        if (isIndexDisplayedTasksInvalid(indexDisplayedTasks)) {
            return String.format(Global.MESSAGE_NO_INDEX, indexDisplayedTasks
                + Global.INDEX_OFFSET);
        }

        String newTaskName = TaskCommander.parser
            .determineTaskName(userCommand);

        List<Date> newTaskDateTime = TaskCommander.parser
            .determineTaskDateTime(userCommand);
        if (isTaskDateTimeInvalid(newTaskDateTime)) {
            return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
        }

        boolean removeExistingDate = TaskCommander.parser.containsParameter(
            userCommand, Global.PARAMETER_FLOATING);
        if (noChangesToTaskGiven(newTaskName, newTaskDateTime, removeExistingDate)) {
            return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
        }

        Task relatedTask = displayedTasks.get(indexDisplayedTasks);
        return updateTaskInData(relatedTask, newTaskName, newTaskDateTime,
            removeExistingDate);
    }

    // Checks the done command from the user and forwards it to Data.
    private String doneTask(String userCommand) {
        if (hasNoParamters(userCommand)) {
            return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
        }

        int indexDisplayedTasks = TaskCommander.parser
            .determineIndex(userCommand) - Global.INDEX_OFFSET;
        if (isIndexDisplayedTasksInvalid(indexDisplayedTasks)) {
            return String.format(Global.MESSAGE_NO_INDEX, indexDisplayedTasks
                + Global.INDEX_OFFSET);
        }

        Task relatedTask = displayedTasks.get(indexDisplayedTasks);
        return TaskCommander.data.done(TaskCommander.data
            .getIndexOf(relatedTask));
    }


    // Checks the open command from the user and forwards it to Data.
    private String openTask(String userCommand) {
        if (hasNoParamters(userCommand)) {
            return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
        }

        int indexDisplayedTasks = TaskCommander.parser
            .determineIndex(userCommand) - Global.INDEX_OFFSET;
        if (isIndexDisplayedTasksInvalid(indexDisplayedTasks)) {
            return String.format(Global.MESSAGE_NO_INDEX, indexDisplayedTasks
                + Global.INDEX_OFFSET);
        }

        Task relatedTask = displayedTasks.get(indexDisplayedTasks);
        return TaskCommander.data.open(TaskCommander.data
            .getIndexOf(relatedTask));
    }

    // Checks the delete command from the user and forwards it to the Data.
    private String deleteTask(String userCommand) {
        if (hasNoParamters(userCommand)) {
            return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
        }

        int indexDisplayedTasks = TaskCommander.parser
            .determineIndex(userCommand) - Global.INDEX_OFFSET;
        if (isIndexDisplayedTasksInvalid(indexDisplayedTasks)) {
            return Global.MESSAGE_NO_INDEX;
        }

        Task relatedTask = displayedTasks
            .get(indexDisplayedTasks);
        return TaskCommander.data.deleteTask(TaskCommander.data
            .getIndexOf(relatedTask));
    }

    // Checks the display command from the user and adjusts the settings.
    private String displayTasks(String userCommand) {
        List<Date> taskDateTimes = TaskCommander.parser
            .determineTaskDateTime(userCommand);
        if (isTaskDateTimeInvalid(taskDateTimes)) {
            return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
        }

        updateDisplayRestrictions(userCommand, taskDateTimes);
        setDisplaySettingsDescription();

        return Global.MESSAGE_DISPLAYED;
    }

    // Checks the search command from the user and forwards it to the Data.
    private String searchForTasks(String userCommand) {
        if (hasNoParamters(userCommand)) {
            return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
        }
        resetDisplayRestrictions();
        
        searchedWordsAndPhrases = TaskCommander.parser.determineSearchedWords(userCommand);
        if (searchedWordsAndPhrases == null) {
            return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
        }
        
        isSearchRestricted = true;
        setDisplaySettingsDescription();

        return String.format(Global.MESSAGE_SEARCHED, searchedWordsAndPhrases);
    }

    // Checks the clear command from the user and forwards it to the Data.
    private String clearTasks(String userCommand) {
        if (hasParamters(userCommand)) {
            return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
        }
        return TaskCommander.data.clearTasks();
    }

    // Checks the clear command from the user and forwards it to the Data.
    private String syncTasks(String userCommand) {
        if (hasParamters(userCommand)) {
            return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
        }
        if (TaskCommander.syncHandler == null) {
            TaskCommander.getSyncHandler();
        }
        return TaskCommander.syncHandler.sync();
    }

    // Checks the undo command from the user and forwards it to the Data.
    private String undoTask(String userCommand) {
        if (hasParamters(userCommand)) {
            return String.format(Global.MESSAGE_INVALID_FORMAT, userCommand);
        }
        return TaskCommander.data.undo();
    }

    // Processes the addition of the task to the respective method in Data.
    private String addTaskToData(String taskName, List<Date> taskDateTime) {
        if (taskDateTime == null) {
            return TaskCommander.data.addFloatingTask(taskName);
        } else if (taskDateTime.size() == 1) {
            return TaskCommander.data.addDeadlineTask(taskName,
                taskDateTime.get(0));
        } else {
            assert (taskDateTime.size() >= 2);
            Date startDate = taskDateTime.get(0);
            Date endDate = taskDateTime.get(1);
            return TaskCommander.data
                .addTimedTask(taskName, startDate, endDate);
        }
    }

    // Processes the update of the task to the respective method in Data.
    private String updateTaskInData(Task relatedTask, String newTaskName,
        List<Date> newTaskDateTime, boolean removeExistingDate) {
        Task.TaskType newTaskType;
        Date newStartDate = null;
        Date newEndDate = null;

        if (newTaskDateTime != null) {
            if (newTaskDateTime.size() == 1) {
                newTaskType = Task.TaskType.DEADLINE;
                newEndDate = newTaskDateTime.get(0);
            } else {
                assert (newTaskDateTime.size() >= 2);
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
            return TaskCommander.data.updateToFloatingTask(indexOfRelatedTask,
                newTaskName);
        case DEADLINE:
            return TaskCommander.data.updateToDeadlineTask(indexOfRelatedTask,
                newTaskName, newEndDate);
        default:
            return TaskCommander.data.updateToTimedTask(indexOfRelatedTask,
                newTaskName, newStartDate, newEndDate);
        }
    }
    
    // Checks if given list is valid, i.e. contains either one or two DateTimes.
    private boolean isTaskDateTimeInvalid(List<Date> taskDateTimes) {
        if (taskDateTimes != null && taskDateTimes.size() > 2) {
            return true;
        } else {
            return false;
        }
    }
	
    // Checks if given index for displayed tasks list is valid.
    private boolean isIndexDisplayedTasksInvalid(int indexDisplayedTasks) {
        if (indexDisplayedTasks > displayedTasks.size() - Global.INDEX_OFFSET
            || indexDisplayedTasks < 0) {
            return true;
        } else {
            return false;
        }
    }

    // Checks if userCommand has no parameters, i.e. only contains commandType.
    private boolean hasNoParamters(String userCommand) {
        return getNumberOfWords(userCommand) == 1;
    }

    // Checks if userCommand has parameters, i.e. not only contains commandType.
    private boolean hasParamters(String userCommand) {
        return getNumberOfWords(userCommand) > 1;
    }

    // Sets default display settings, that is, overdue and upcoming open tasks
    // of the next week.
    private void setDefaultDisplaySettings() {
        resetDisplayRestrictions();

        isDateRestricted = true;
        startDateRestriction = null;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        calendar.set(Calendar.MILLISECOND, 99);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR, 11);
        endDateRestriction = calendar.getTime();
        
        System.out.println(Global.dayFormat.format(endDateRestriction) + " "
        + Global.timeFormat.format(endDateRestriction));

        isStatusRestricted = true;
        areDoneTasksDisplayed = false;
        areOpenTasksDisplayed = true;

        setDisplaySettingsDescription();
    }	

    // Sets date, task type, status restrictions and resets search restrictions
    // of display settings.
    private void updateDisplayRestrictions(String userCommand,
        List<Date> taskDateTimes) {
        setDateRestrictionOfDisplaySettings(taskDateTimes);
        setTaskTypeRestrictionsOfDisplaySettings(userCommand);
        setStatusRestrictionOfDisplaySettings(userCommand);
        resetSearchRestrictionOfDisplaySettings();
    }

    // Resets date, task type and status restrictions of display settings.
    private void resetDisplayRestrictions() {
        resetDateRestrictionOfDisplaySettings();
        resetTaskTypeRestrictionOfDisplaySettings();
        resetStatusRestrictionOfDisplaySettings();
        resetSearchRestrictionOfDisplaySettings();
    }

    // Sets the date restrictions of the display settings.
    private void setDateRestrictionOfDisplaySettings(List<Date> taskDateTimes) {
        if (taskDateTimes != null) {
            if (taskDateTimes.size() == 1) {
                isDateRestricted = true;
                startDateRestriction = null;
                endDateRestriction = taskDateTimes.get(0);
            } else {
                assert (taskDateTimes.size() >= 2);
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

    // Sets the task type restrictions of the display settings.
    private void setTaskTypeRestrictionsOfDisplaySettings(String userCommand) {
        areFloatingTasksDisplayed = TaskCommander.parser.containsParameter(
            userCommand, Global.PARAMETER_FLOATING);
        areDeadlineTasksDisplayed = TaskCommander.parser.containsParameter(
            userCommand, Global.PARAMETER_DEADLINE);
        areTimedTasksDisplayed = TaskCommander.parser.containsParameter(
            userCommand, Global.PARAMETER_TIMED);
        
        if (noTypeRestrictionGiven()) {
            resetTaskTypeRestrictionOfDisplaySettings();
        } else {
            isTaskTypeRestricted = true;
        }
    }

    // Sets the status restrictions of the display settings.
    private void setStatusRestrictionOfDisplaySettings(String userCommand) {
        areDoneTasksDisplayed = TaskCommander.parser.containsParameter(
            userCommand, Global.PARAMETER_DONE);
        areOpenTasksDisplayed = TaskCommander.parser.containsParameter(
            userCommand, Global.PARAMETER_OPEN);
        
        if (noStatusRestrictionGiven()) {
            resetStatusRestrictionOfDisplaySettings();
        } else {
            isStatusRestricted = true;
        }
    }

    // Sets the description of display settings by consolidating it to a string.
    private void setDisplaySettingsDescription() {
        if (noDisplayRestrictions()) {
            displaySettingsDescription = Global.DESCRIPTION_ALL;
        } else {
            displaySettingsDescription = "";

            if (isDateRestricted) {
                displaySettingsDescription += getDateRestrictionDescriptionOfDisplaySettings();
            }

            if (isTaskTypeRestricted) {
                displaySettingsDescription += addSeperatingSpace(displaySettingsDescription);
                displaySettingsDescription += getTypeRestrictionDescriptionOfDisplaySettings();
            }

            if (isStatusRestricted) {
                displaySettingsDescription += addSeperatingSpace(displaySettingsDescription);
                displaySettingsDescription += getStatusRestrictionDescriptionOfDisplaySettings();
            }
            if (isSearchRestricted) {
                assert (isDateRestricted == false
                    && isTaskTypeRestricted == false && isStatusRestricted == false);
                displaySettingsDescription += getSearchRestrictionDescriptionOfDislplaySettings();
            }
        }
        logger.log(Level.INFO, "Display settings set to: "
            + displaySettingsDescription);
    }

    private String getDateRestrictionDescriptionOfDisplaySettings() {
        if (startDateRestriction == null) {
            return Global.DESCRIPTION_DEADLINE
                + Global.dayFormat.format(endDateRestriction) + " "
                + Global.timeFormat.format(endDateRestriction);
        } else {
            return Global.DESCRIPTION_TIMED
                + Global.dayFormat.format(startDateRestriction) + " "
                + Global.timeFormat.format(startDateRestriction) + " - "
                + Global.dayFormat.format(endDateRestriction) + " "
                + Global.timeFormat.format(endDateRestriction);
        }
    }

    private String getTypeRestrictionDescriptionOfDisplaySettings() {
        String typeRestrictionDescription = Global.DESCRIPTION_TYPE;
        if (areFloatingTasksDisplayed) {
            typeRestrictionDescription += Global.PARAMETER_FLOATING;
        }
        if (areDeadlineTasksDisplayed && !areFloatingTasksDisplayed) {
            typeRestrictionDescription += Global.PARAMETER_DEADLINE;
        } else if (areDeadlineTasksDisplayed) {
            typeRestrictionDescription += ", " + Global.PARAMETER_DEADLINE;
        }
        if (areTimedTasksDisplayed && !areFloatingTasksDisplayed
            && !areDeadlineTasksDisplayed) {
            typeRestrictionDescription += Global.PARAMETER_TIMED;
        } else if (areTimedTasksDisplayed) {
            typeRestrictionDescription += ", " + Global.PARAMETER_TIMED;
        }
        return typeRestrictionDescription;
    }

    private String getStatusRestrictionDescriptionOfDisplaySettings() {
        if (areDoneTasksDisplayed) {
            return Global.DESCRIPTION_STATUS + Global.PARAMETER_DONE;
        } else {
            return Global.DESCRIPTION_STATUS + Global.PARAMETER_OPEN;
        }
    }

    private String getSearchRestrictionDescriptionOfDislplaySettings() {
        String searchRestrictionDescription = "";

        for (String searchedWordOrPhrase : searchedWordsAndPhrases) {
            if (searchRestrictionDescription.equals("")) {
                searchRestrictionDescription = Global.DESCRIPTION_SEARCH + "\""
                    + searchedWordOrPhrase + "\"";
            } else {
                searchRestrictionDescription += ", " + "\""
                    + searchedWordOrPhrase + "\"";
            }
        }
       
        return searchRestrictionDescription;
    }

    private String addSeperatingSpace(String displaySettingsDescription) {
        if (displaySettingsDescription.equals("")) {
            return "";
        } else {
            return " ";
        }
    }

    private void resetSearchRestrictionOfDisplaySettings() {
        isSearchRestricted = false;
        searchedWordsAndPhrases = null;
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

    private boolean noDisplayRestrictions() {
        return !isDateRestricted && !isTaskTypeRestricted
            && !isStatusRestricted && !isSearchRestricted;
    }

    private boolean noChangesToTaskGiven(String newTaskName,
        List<Date> newTaskDateTime, boolean removeExistingDate) {
        return (newTaskDateTime == null) && !removeExistingDate
            && (newTaskName == null);
    }

    private boolean noTypeRestrictionGiven() {
        return (!areFloatingTasksDisplayed && !areDeadlineTasksDisplayed && !areTimedTasksDisplayed)
            || (areFloatingTasksDisplayed && areDeadlineTasksDisplayed && areTimedTasksDisplayed);
    }

    private boolean noStatusRestrictionGiven() {
        return (!areDoneTasksDisplayed && !areOpenTasksDisplayed)
            || (areDoneTasksDisplayed && areOpenTasksDisplayed);
    }

    private int getNumberOfWords(String userCommand) {
        String[] allWords = userCommand.trim().split("\\s+");
        return allWords.length;
    }
}