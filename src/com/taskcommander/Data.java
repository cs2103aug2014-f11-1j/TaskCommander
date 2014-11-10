package com.taskcommander;
import java.util.Date;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.taskcommander.Global.CommandType;

/**
 * Singleton class that stores tasks temporarily. Contains all of the methods needed to manipulate 
 * the task objects within the temporary list, for internal use and for the Google Integration
 * component.
 * Upon initialization, the contents of the permanent storage will be pulled. After each 
 * command the data will be pushed to the permanent storage.
 */

public class Data {
    
    private static Logger logger = Logger.getLogger(Controller.class.getName());
    protected ArrayList<Task> tasks;

    // Singleton instance for Data
    private static Data theOne;

	//@author A0128620M
    /**
     * Returns the only instance of Data.
     * @return Data instance.
     */
    public static Data getInstance() {
        if (theOne == null) {
            theOne = new Data();
        }
        return theOne;
    }

    //@author A0109194A
    private ArrayList<Task> deletedTasks; // Used by the Google API.
    private Stack<ArrayList<Task>> clearedTasks;

    // Variables to store history of tasks for undo command.
    private Stack<Task> addedTasks;
    private Stack<Task> preupdatedTasks;
    private Stack<Task> updatedTasks;
    private Stack<Task> changedTypeTasks;
    private Stack<CommandType> operationHistory;
    private Stack<Global.CommandType> undoHistory;
    private Stack<Integer> doneTasks;
    private Stack<Integer> openTasks;

    protected Data() {
        tasks = new ArrayList<Task>();
        deletedTasks = new ArrayList<Task>();
        addedTasks = new Stack<Task>();
        preupdatedTasks = new Stack<Task>();
        updatedTasks = new Stack<Task>();
        clearedTasks = new Stack<ArrayList<Task>>();
        changedTypeTasks = new Stack<Task>();
        operationHistory = new Stack<Global.CommandType>();
        undoHistory = new Stack<Global.CommandType>();
        doneTasks = new Stack<Integer>();
        openTasks = new Stack<Integer>();

        loadFromPermanentStorage();
    }

    // Methods used internally:
    
    // @author A0128620M
    /**
     * Adds a Floating Task to the tasks list.
     * 
     * @param taskName
     * @return feedback for UI
     */
    public String addFloatingTask(String taskName) {
        FloatingTask floatingTask = new FloatingTask(taskName);
        tasks.add(floatingTask);
       
        saveToOperationHistory(Global.CommandType.ADD);
        addedTasks.push(floatingTask);
        saveToPermanentStorage();

        return String.format(Global.MESSAGE_ADDED,
            getTaskInDisplayFormat(floatingTask));
    }

    /**
     * Adds a Deadline Task to the tasks list.
     * 
     * @param taskName
     * @param endDate
     * @return feedback for UI
     */
    public String addDeadlineTask(String taskName, Date endDate) {
        DeadlineTask deadlineTask = new DeadlineTask(taskName, endDate);
        tasks.add(deadlineTask);
        
        saveToOperationHistory(Global.CommandType.ADD);
        addedTasks.push(deadlineTask);
        saveToPermanentStorage();

        return String.format(Global.MESSAGE_ADDED,
            getTaskInDisplayFormat(deadlineTask));
    }

    /**
     * Adds a Timed Task to the tasks list.
     * 
     * @param taskName
     * @param startDate
     * @param endDate
     * @return feedback for UI
     */
    public String addTimedTask(String taskName, Date startDate, Date endDate) {
        TimedTask timedTask = new TimedTask(taskName, startDate, endDate);
        tasks.add(timedTask);
        
        saveToOperationHistory(Global.CommandType.ADD);
        addedTasks.push(timedTask);
        saveToPermanentStorage();

        return String.format(Global.MESSAGE_ADDED,
            getTaskInDisplayFormat(timedTask));
    }
    
    /**
     * Updates a FloatingTask with the given index and replaces the old
     * taskName, startDate or endDate respectively and changes the taskType if
     * needed. If a given date or name parameter equals null, the old value
     * remains.
     * 
     * @param index
     * @param taskName
     * @return feedback for UI
     */
    public String updateToFloatingTask(int index, String name) {
        if (tasks.isEmpty()) {
            return String.format(Global.MESSAGE_EMPTY);
        }
        if (isIndexInvalid(index)) {
            return String.format(Global.MESSAGE_NO_INDEX, index);
        }

        Task relatedTask = tasks.get(index);
        FloatingTask floatingTask;

        if (relatedTask.getType() != Task.TaskType.FLOATING) {
            if (name == null) {
                name = relatedTask.getName();
            }
            
            floatingTask = new FloatingTask(name);
            floatingTask.setEdited(true);
            floatingTask.setDone(relatedTask.isDone());
            
            changedTypeTasks.add(relatedTask);
            tasks.remove(index);
            tasks.add(index, floatingTask);
        } else {
        	floatingTask = new FloatingTask(relatedTask.getName());
            if (name != null) {
                floatingTask.setName(name);
            }
            floatingTask.setEdited(true);
            tasks.remove(index);
            tasks.add(index, floatingTask);
        }
        tasks.remove(index);
        tasks.add(index, floatingTask);
        
        processUpdateHistory(relatedTask, floatingTask);
        saveToPermanentStorage();
        
        return String.format(Global.MESSAGE_UPDATED,
            getTaskInDisplayFormat(floatingTask));
    }

    /**
     * Updates a DeadlineTask with the given index and replaces the old
     * taskName, startDate or endDate respectively and changes the taskType if
     * needed. If a given date or name parameter equals null, the old value
     * remains.
     * 
     * @param index
     * @param taskName
     * @param endDate
     * @return feedback for UI
     */
    public String updateToDeadlineTask(int index, String name, Date endDate) {
        if (tasks.isEmpty()) {
            return String.format(Global.MESSAGE_EMPTY);
        }
        if (isIndexInvalid(index)) {
            return String.format(Global.MESSAGE_NO_INDEX, index);
        }

        Task relatedTask = tasks.get(index);
        DeadlineTask deadlineTask;

        if (relatedTask.getType() != Task.TaskType.DEADLINE) {
            if (name == null) {
                name = relatedTask.getName();
            }
            if (endDate == null) {
                return Global.MESSAGE_ARGUMENTS_INVALID;
            }
            
            deadlineTask = new DeadlineTask(name, endDate);
            deadlineTask.setEdited(true);
            deadlineTask.setDone(relatedTask.isDone());
           
            changedTypeTasks.add(relatedTask);
        } else {
            deadlineTask = new DeadlineTask(relatedTask.getName(), 
            		((DeadlineTask) relatedTask).getEndDate());
            
            if (name != null) {
                deadlineTask.setName(name);
            }
            if (endDate != null) {
                deadlineTask.setEndDate(endDate);
            }
            deadlineTask.setEdited(true);
        }
        	tasks.remove(index);
        	tasks.add(index, deadlineTask);

        processUpdateHistory(relatedTask, deadlineTask);
        saveToPermanentStorage();

        return String.format(Global.MESSAGE_UPDATED,
            getTaskInDisplayFormat(deadlineTask));
    }

    /**
     * Updates a TimedTask with the given index and replaces the old taskName,
     * startDate or endDate respectively and changes the taskType if needed. If
     * a given date or name parameter equals null, the old value remains.
     * 
     * @param index
     * @param taskName
     * @param startDate
     * @param endDate
     * @return feedback for UI
     */
    public String updateToTimedTask(int index, String name, Date startDate,
        Date endDate) {
        if (tasks.isEmpty()) {
            return String.format(Global.MESSAGE_EMPTY);
        }
        if (isIndexInvalid(index)) {
            return String.format(Global.MESSAGE_NO_INDEX, index);
        }

        Task relatedTask = tasks.get(index);
        TimedTask timedTask;
        if (!relatedTask.getType().equals(Task.TaskType.TIMED)) {
            if (name == null) {
                name = relatedTask.getName();
            }
            if (startDate == null || endDate == null) {
                return Global.MESSAGE_ARGUMENTS_INVALID;
            }
            
            timedTask = new TimedTask(name, startDate, endDate);
            timedTask.setEdited(true);
            timedTask.setDone(relatedTask.isDone());

            changedTypeTasks.add(relatedTask);
        } else {
            timedTask = (TimedTask) relatedTask;
            timedTask = new TimedTask(relatedTask.getName(),
            		((TimedTask) relatedTask).getStartDate(),
            		((TimedTask) relatedTask).getEndDate());
            
            if (name != null) {
                timedTask.setName(name);
            }
            if (startDate != null) {
                timedTask.setStartDate(startDate);
            }
            if (endDate != null) {
                timedTask.setEndDate(endDate);
            }
            timedTask.setEdited(true);
        }
        tasks.remove(index);
        tasks.add(index, timedTask);

        processUpdateHistory(relatedTask, timedTask);
        saveToPermanentStorage();

        return String.format(Global.MESSAGE_UPDATED,
            getTaskInDisplayFormat(timedTask));
    }

    /**
     * Marks a task as done.
     * 
     * @param index
     * @return feedback for UI
     */
    public String done(int index) {
        if (tasks.isEmpty()) {
            return String.format(Global.MESSAGE_EMPTY);
        }
        if (isIndexInvalid(index)) {
            return String.format(Global.MESSAGE_NO_INDEX, index);
        }

        Task doneTask = tasks.get(index);
        if (doneTask.isDone()) {
            return String.format(Global.MESSAGE_ALREADY_DONE);
        } else {
            doneTask.markDone();
            saveToPermanentStorage();
            saveToOperationHistory(Global.CommandType.DONE);
            doneTasks.add(index);

            switch (doneTask.getType()) {
              case FLOATING:
                  FloatingTask floatingTask = (FloatingTask) doneTask;
                  return String.format(Global.MESSAGE_DONE,
                      getTaskInDisplayFormat(floatingTask));
              case DEADLINE:
                  DeadlineTask deadlineTask = (DeadlineTask) doneTask;
                  return String.format(Global.MESSAGE_DONE,
                      getTaskInDisplayFormat(deadlineTask));
              default:
                  TimedTask timedTask = (TimedTask) doneTask;
                  return String.format(Global.MESSAGE_DONE,
                      getTaskInDisplayFormat(timedTask));
            }
        }
    }

    /**
     * Marks a task as open.
     * 
     * @param index
     * @return feedback for UI
     */
    public String open(int index) {
        if (tasks.isEmpty()) {
            return String.format(Global.MESSAGE_EMPTY);
        }
        if (isIndexInvalid(index)) {
            return String.format(Global.MESSAGE_NO_INDEX, index);
        }

        Task openTask = tasks.get(index);
        if (!openTask.isDone()) {
            return String.format(Global.MESSAGE_ALREADY_OPEN);
        } else {
            openTask.markOpen();
            saveToPermanentStorage();
            saveToOperationHistory(Global.CommandType.OPEN);
            openTasks.add(index);
            
            switch (openTask.getType()) {
              case FLOATING:
                  FloatingTask floatingTask = (FloatingTask) openTask;
                  return String.format(Global.MESSAGE_OPEN,
                      getTaskInDisplayFormat(floatingTask));
              case DEADLINE:
                  DeadlineTask deadlineTask = (DeadlineTask) openTask;
                  return String.format(Global.MESSAGE_OPEN,
                      getTaskInDisplayFormat(deadlineTask));
              default:
                  TimedTask timedTask = (TimedTask) openTask;
                  return String.format(Global.MESSAGE_OPEN,
                      getTaskInDisplayFormat(timedTask));
            }
        }
    }
	
    /**
     * Deletes the task with the given index. Does not execute if there are no
     * lines and if a wrong index is given.
     * 
     * @param index
     * @return feedback for UI
     */
    public String deleteTask(int index) {
        if (tasks.isEmpty()) {
            return String.format(Global.MESSAGE_EMPTY);
        }
        if (isIndexInvalid(index)) {
            return String.format(Global.MESSAGE_NO_INDEX, index);
        }

        Task deletedTask = tasks.get(index);
        saveToOperationHistory(Global.CommandType.DELETE);
        deletedTasks.add(deletedTask);
        deletedTask.setEdited(true);
        tasks.remove(index);
        saveToPermanentStorage();
        
        switch (deletedTask.getType()) { // TODO: Extract to method
          case FLOATING:
              FloatingTask floatingTask = (FloatingTask) deletedTask;
              return String.format(Global.MESSAGE_DELETED,
                  getTaskInDisplayFormat(floatingTask));
          case DEADLINE:
              DeadlineTask deadlineTask = (DeadlineTask) deletedTask;
              return String.format(Global.MESSAGE_DELETED,
                  getTaskInDisplayFormat(deadlineTask));
          default:
              TimedTask timedTask = (TimedTask) deletedTask;
  
              return String.format(Global.MESSAGE_DELETED,
                  getTaskInDisplayFormat(timedTask));
        }
    }
	
    //@author A0109194A
    /**
     * Clears all tasks from memory.
     * 
     * @param userCommand
     * @return feedback for UI
     */
    public String clearTasks() {
        if (tasks.isEmpty()) {
            return String.format(Global.MESSAGE_EMPTY);
        }

        ArrayList<Task> cleared = new ArrayList<Task>();
        cleared.addAll(tasks);
        clearedTasks.push(cleared);
        tasks.clear();
        saveToOperationHistory(Global.CommandType.CLEAR);
        saveToPermanentStorage();

        return String.format(Global.MESSAGE_CLEARED);
    }

    //@author A0109194A
    /**
     * This operation undoes the latest command. It supports Add, Delete,
     * Update, and Clear commands.
     * 
     * @return feedback for UI
     */
    public String undo() {
        if (operationHistory.empty()) {
            return Global.MESSAGE_UNDO_EMPTY;
        }
        Global.CommandType type = operationHistory.pop();
        Global.CommandType undoCommand = null;
        switch (type) {
        case ADD:
            undoCommand = Global.CommandType.DELETE;
            undoAdd();
            break;
        case DELETE:
            undoCommand = Global.CommandType.ADD;
            undoDelete();
            break;
        case UPDATE:
            undoCommand = Global.CommandType.UPDATE;
            undoUpdate();
            break;
        case CLEAR:
            undoCommand = Global.CommandType.UNCLEAR;
            undoClear();
            break;
        case DONE:
        	undoCommand = Global.CommandType.OPEN;
        	undoDone();
        	break;
        case OPEN:
        	undoCommand = Global.CommandType.DONE;
        	undoOpen();
        	break;
        default:
            undo(); // Calls undo again to look for one of the six commands
                    // above
        }
        saveToUndoHistory(undoCommand);
        saveToPermanentStorage();
        return String.format(Global.MESSAGE_UNDONE, type);
    }
    
    //@author A0109194A
    /**
     * Undoes the add command.
     * 
     * @return Success of undo.
     */
    private boolean undoAdd() {
        Task toDelete = addedTasks.pop();
        switch (toDelete.getType()) {
        case TIMED:
            tasks.remove((TimedTask) toDelete);
            return true;
        case DEADLINE:
            tasks.remove((DeadlineTask) toDelete);
            return true;
        case FLOATING:
            tasks.remove((FloatingTask) toDelete);
            return true;
        }
        return false;
    }
    
    //@author A0109194A
    /**
     * Undoes the delete command.
     * 
     * @return Success of undo.
     */
    private boolean undoDelete() {
        Task toAdd = deletedTasks.get(deletedTasks.size() - 1);
        deletedTasks.remove(deletedTasks.size() - 1);
        switch (toAdd.getType()) {
        case TIMED:
            tasks.add((TimedTask) toAdd);
            return true;
        case DEADLINE:
            tasks.add((DeadlineTask) toAdd);
            return true;
        case FLOATING:
            tasks.add((FloatingTask) toAdd);
            return true;
        }
        return false;
    }

    //@author A0109194A
    /**
     * Undoes the update command.
     * 
     * @return Success of undo.
     */
    private boolean undoUpdate() {
        Task updated = updatedTasks.pop();
        Task beforeUpdate = preupdatedTasks.pop();
        if (updated.getType() != beforeUpdate.getType()) {
            changedTypeTasks.pop();
        }

        int index = 0;
        switch (updated.getType()) {
        case TIMED:
            index = tasks.indexOf((TimedTask) updated);
            break;
        case DEADLINE:
            index = tasks.indexOf((DeadlineTask) updated);
            break;
        case FLOATING:
            index = tasks.indexOf((FloatingTask) updated);
            break;
        }

        tasks.remove(index);
        switch (beforeUpdate.getType()) {
        case TIMED:
            tasks.add(index, (TimedTask) beforeUpdate);
            return true;
        case DEADLINE:
            tasks.add(index, (DeadlineTask) beforeUpdate);
            return true;
        case FLOATING:
            tasks.add(index, (FloatingTask) beforeUpdate);
            return true;
        }
        return false;
    }
    
    //@author A0109194A
    /**
     * Undoes the Clear command.
     * 
     * @return Success of undo.
     */
    private boolean undoClear() {
        ArrayList<Task> toRestore = clearedTasks.pop();
        tasks.addAll(toRestore);
        return true;
    }
    
    //@author A0109194A
    /**
     * Undoes the Done command.
     * @return	Success of undo
     */
    private boolean undoDone() {
    	int index = doneTasks.pop();
    	if (index != -1) {
    		Task task = tasks.get(index);
    		task.markOpen();    
    		return true;
    	} else {
    		return false;
    	}
    }
    
    //@author A0109194A
    /**
     * Undoes the Open command.
     * @return	Success of undo.
     */
    private boolean undoOpen() {
    	int index = openTasks.pop();
    	if (index != -1) {
    		Task task = tasks.get(index);
    		task.markDone();
    		return true;
    	} else {
    		return false;
    	}
    }
    
	// Methods used by Google Integration:
    
    //@author A0109194A
    /**
     * Adds a Task to the tasks list by forwarding the task's attributes to the
     * respective add method. It is usually called by the SyncHandler class.
     * 
     * @param task
     * @return Feedback for UI
     */
    public String addTask(Task task) {
        logger.log(Level.INFO, "Called addTask(Task task)");
        if (task.getId() != null) {
            switch (task.getType()) {
              case FLOATING:
                  FloatingTask floatingTask = (FloatingTask) task;
                  return addFloatingTask(floatingTask);
              case DEADLINE:
                  DeadlineTask deadlineTask = (DeadlineTask) task;
                  return addDeadlineTask(deadlineTask);
              default:
                  TimedTask timedTask = (TimedTask) task;
                  return addTimedTask(timedTask);
              }
        } else {
            return Global.MESSAGE_NULL_ID;
        }
    }
    
    //@author A0109194A
    /**
     * Adds a Floating Task to the tasks list.
     * 
     * @param floatingTask
     * @return Feedback for UI
     */
    public String addFloatingTask(FloatingTask floatingTask) {
        floatingTask.setEdited(false);
        tasks.add(floatingTask);
        saveToPermanentStorage();
        return String.format(Global.MESSAGE_ADDED,
            getTaskInDisplayFormat(floatingTask));
    }
    
    //@author A0109194A
    /**
     * Adds a Deadline Task to the tasks list.
     * 
     * @param deadlineTask
     * @return Feedback for UI
     */
    public String addDeadlineTask(DeadlineTask deadlineTask) {
        deadlineTask.setEdited(false);
        tasks.add(deadlineTask);
        saveToPermanentStorage();
        return String.format(Global.MESSAGE_ADDED,
            getTaskInDisplayFormat(deadlineTask));
    }
    
    //@author A0109194A
    /**
     * Adds a Timed Task to the tasks list.
     * 
     * @param timedTask
     * @return Feedback for UI
     */
    public String addTimedTask(TimedTask timedTask) {
        timedTask.setEdited(false);
        tasks.add(timedTask);
        saveToPermanentStorage();
        return String.format(Global.MESSAGE_ADDED,
            getTaskInDisplayFormat(timedTask));
    }
    
    //@author A0109194A
    /**
     * Updates a task with a FloatingTask object as a parameter. It is usually
     * called by the SyncHandler.
     * 
     * @param index
     * @param task
     * @return Feedback for user
     */
    public String updateToFloatingTask(int index, FloatingTask task) {
        FloatingTask floatingTask;
        if (tasks.get(index).getType() != Task.TaskType.FLOATING) {
            floatingTask = new FloatingTask(task.getName());
            floatingTask.setEdited(false);
            floatingTask.setDone(tasks.get(index).isDone());
            floatingTask.setId(task.getId());
            tasks.remove(index);
            tasks.add(index, floatingTask);
        } else {
            floatingTask = (FloatingTask) tasks.get(index);
            if (task.getName() != null) {
                floatingTask.setName(task.getName());
            }
            floatingTask.setEdited(false);
        }
        floatingTask.setUpdated(task.getUpdated());
        saveToPermanentStorage();
        return String.format(Global.MESSAGE_UPDATED,
            getTaskInDisplayFormat(floatingTask));
    }

    //@author A0109194A
    /**
     * Updates a task with a DeadlineTask object as a parameter. It is usually
     * called by the SyncHandler.
     * 
     * @param index
     * @param task
     * @return Feedback for user
     */
    public String updateToDeadlineTask(int index, DeadlineTask task) {
        DeadlineTask deadlineTask;
        if (tasks.get(index).getType() != Task.TaskType.DEADLINE) {
            deadlineTask = new DeadlineTask(task.getName(), task.getEndDate());
            deadlineTask.setEdited(false);
            deadlineTask.setDone(tasks.get(index).isDone());
            deadlineTask.setUpdated(task.getUpdated());
            deadlineTask.setId(task.getId());
            tasks.remove(index);
            tasks.add(index, deadlineTask);
            saveToPermanentStorage();
            return String.format(Global.MESSAGE_UPDATED,
                getTaskInDisplayFormat(deadlineTask));
        } else {
            deadlineTask = (DeadlineTask) tasks.get(index);
            if (task.getName() != null) {
                deadlineTask.setName(task.getName());
            }
            if (task.getEndDate() != null) {
                deadlineTask.setEndDate(task.getEndDate());
            }
            deadlineTask.setEdited(false);
        }
        deadlineTask.setUpdated(task.getUpdated());
        saveToPermanentStorage();
        return String.format(Global.MESSAGE_UPDATED,
            getTaskInDisplayFormat(deadlineTask));
    }
    
    //@author A0109194A
    /**
     * Updates a task with a TimedTask object as a parameter. It is usually
     * called by the SyncHandler.
     * 
     * @param index
     * @param task
     * @return Feedback for user
     */
    public String updateToTimedTask(int index, TimedTask task) {
        Task relatedTask = tasks.get(index);
        TimedTask timedTask;
        if (relatedTask.getType() != Task.TaskType.TIMED) {
            timedTask = new TimedTask(task.getName(), task.getStartDate(),
                task.getEndDate());
            timedTask.setEdited(false);
            timedTask.setDone(relatedTask.isDone());
            timedTask.setId(task.getId());
            tasks.remove(index);
            tasks.add(index, timedTask);
        } else {
            timedTask = (TimedTask) tasks.get(index);
            if (task.getName() != null) {
                timedTask.setName(task.getName());
            }
            if (task.getStartDate() != null) {
                timedTask.setStartDate(task.getStartDate());
            }
            if (task.getEndDate() != null) {
                timedTask.setEndDate(task.getEndDate());
            }
            timedTask.setEdited(false);
        }
        timedTask.setUpdated(task.getUpdated());
        saveToPermanentStorage();
        return String.format(Global.MESSAGE_UPDATED,
            getTaskInDisplayFormat(timedTask));
    }
    
    //@author A0109194A
    /**
     * Deletes the task directly from the tasks list without the index. Used to
     * delete tasks when syncing. Checking for invalid index is unnecessary,
     * since this is called internally, independent of user input.
     * 
     * @param task
     * @return If the delete was successful.
     */
    public boolean deleteFromGoogle(int index) {
        if (tasks.isEmpty()) {
            return false;
        } else {
            tasks.remove(index);
            saveToPermanentStorage();
            return true;
        }
    }
	
    // Getter methods for tasks:

    //@author A0128620M
    /**
     * Returns a sorted list consisting of copies of all tasks of the tasks
     * list.
     * 
     * @return sorted copy of tasks list.
     */
    public ArrayList<Task> getCopiedTasks() {
        ArrayList<FloatingTask> floatingTasks = new ArrayList<FloatingTask>();
        ArrayList<DatedTask> datedTasks = new ArrayList<DatedTask>();
        ArrayList<Task> allTasks = new ArrayList<Task>();

        for (Task task : tasks) {
            if (task.getType().equals(Task.TaskType.FLOATING)) {
                floatingTasks.add(new FloatingTask((FloatingTask) task));
            } else if (task.getType().equals(Task.TaskType.DEADLINE)) {
                datedTasks.add(new DeadlineTask((DeadlineTask) task));
            } else if (task.getType().equals(Task.TaskType.TIMED)) {
                datedTasks.add(new TimedTask((TimedTask) task));
            }
        }

        Collections.sort(floatingTasks);
        allTasks.addAll(floatingTasks);
        Collections.sort(datedTasks);
        allTasks.addAll(datedTasks);

        return allTasks;
    }

    /**
     * Returns a sorted list consisting of copies of those tasks of the tasks
     * list which satisfy the given DateTime, TaskType, Status and Search
     * restrictions.
     * 
     * @param isDateRestricted
     * @param startDate
     * @param endDate
     * @param isTaskTypeRestricted
     * @param areFloatingTasksDisplayed
     * @param areDeadlineTasksDisplayed
     * @param areTimedTasksDisplayed
     * @param isStatusRestricted
     * @param areDoneTasksDisplayed
     * @param areOpenTasksDisplayed
     * @param isSearchRestricted
     * @param searchedWords
     * @return sorted copy of tasks list satisfying given restrictions.
     */
    public ArrayList<Task> getCopiedTasks(boolean isDateRestricted,
        Date startDate, Date endDate, boolean isTaskTypeRestricted,
        boolean areFloatingTasksDisplayed, boolean areDeadlineTasksDisplayed,
        boolean areTimedTasksDisplayed, boolean isStatusRestricted,
        boolean areDoneTasksDisplayed, boolean areOpenTasksDisplayed,
        boolean isSearchedWordRestricted, ArrayList<String> searchedWords) {
        ArrayList<FloatingTask> floatingTasks = new ArrayList<FloatingTask>();
        ArrayList<DatedTask> datedTasks = new ArrayList<DatedTask>();
        ArrayList<Task> concernedTasks = new ArrayList<Task>();
        boolean containsSearchedWords = false;

        for (Task task : tasks) {
            if (isSearchedWordRestricted) {
                containsSearchedWords = checkStringForWords(searchedWords,
                    task.getName());
            }

            if (matchesSearchAndStatusRestrictions(isStatusRestricted,
                areDoneTasksDisplayed, isSearchedWordRestricted,
                containsSearchedWords, task)) {

                switch (task.getType()) {
                  case FLOATING:
                      FloatingTask floatingTask = (FloatingTask) task;
                      if (matchesTypeAndDateRestrictionsFloating(
                          isDateRestricted, isTaskTypeRestricted,
                          areFloatingTasksDisplayed)) {
                          floatingTasks.add(floatingTask);
  
                      }
                      break;
                      
                  case DEADLINE:
                      DeadlineTask deadlineTask = (DeadlineTask) task;
                      if (matchesTypeAndDateRestrictionsDeadline(
                          isDateRestricted, startDate, endDate,
                          isTaskTypeRestricted, areDeadlineTasksDisplayed,
                          deadlineTask)) {
                          datedTasks.add(deadlineTask);
                      }
                      break;
                      
                  default:
                      TimedTask timedTask = (TimedTask) task;
                      if (matchesTypeAndDateRestrictionsTimed(isDateRestricted,
                          startDate, endDate, isTaskTypeRestricted,
                          areTimedTasksDisplayed, timedTask)) {
                          datedTasks.add(timedTask);
  
                      }
                      break;
                  }
            }
        }
        Collections.sort(floatingTasks);
        concernedTasks.addAll(floatingTasks);
        Collections.sort(datedTasks);
        concernedTasks.addAll(datedTasks);

        return concernedTasks;
    }
    
    /**
     * Returns the index of the given task object within the tasks ArrayList.
     * 
     * @return index
     */
    public int getIndexOf(Task task) {
        return tasks.indexOf(task);
    }
    
    //@author A0112828H
    /**
     * Returns all tasks.
     * 
     * @return tasks list.
     */
    public ArrayList<Task> getAllTasks() {
        return tasks;
    }
    
    // Helper methods:
    
    //@author A0128620M
    // Checks if given string contains any strings from the given ArrayList.
    private boolean checkStringForWords(ArrayList<String> searchedWords,
        String taskName) {
        boolean containsSearchedWords;
        containsSearchedWords = true;
        
        for (String searchedWord : searchedWords) {
            if (!taskName.contains(searchedWord)) {
                logger.log(Level.INFO, "Doesn't contain the word");
                containsSearchedWords = false;
                break;
            }
        }
        return containsSearchedWords;
    }
	
    // Checks if the type and Date of the timed task matches the restrictions.
    private boolean matchesTypeAndDateRestrictionsTimed(
        boolean isDateRestricted, Date startDate, Date endDate,
        boolean isTaskTypeRestricted, boolean areTimedTasksDisplayed,
        TimedTask timedTask) {
        return (!isTaskTypeRestricted || areTimedTasksDisplayed)
            && checkDateRestrictionForTimedTask(
                isDateRestricted, startDate, endDate, timedTask);
    }

    // Checks if the type and Date of the deadline task matches the restrictions.
    private boolean matchesTypeAndDateRestrictionsDeadline(
        boolean isDateRestricted, Date startDate, Date endDate,
        boolean isTaskTypeRestricted, boolean areDeadlineTasksDisplayed,
        DeadlineTask deadlineTask) {
        return (!isTaskTypeRestricted || areDeadlineTasksDisplayed)
            && checkDateRestrictionForDeadlineTask(
                isDateRestricted, startDate, endDate,
                deadlineTask);
    }

    // Checks if the type and Date of the floating task matches the restrictions.
    private boolean matchesTypeAndDateRestrictionsFloating(boolean isDateRestricted,
        boolean isTaskTypeRestricted, boolean areFloatingTasksDisplayed) {
        return (!isTaskTypeRestricted || areFloatingTasksDisplayed) && checkDateRestrictionForFloatingTask(isDateRestricted);
    }

    // Checks if the searchedWords and Type matches to the given task.
    private boolean matchesSearchAndStatusRestrictions(
        boolean isStatusRestricted, boolean areDoneTasksDisplayed,
        boolean isSearchedWordRestricted, boolean containsSearchedWords,
        Task task) {
        return (!isSearchedWordRestricted || containsSearchedWords)
            && checkStatusRestricted(isStatusRestricted, areDoneTasksDisplayed,
                task);
    }

    // Checks if the status of the given task matches the status parameters.
    private boolean checkStatusRestricted(boolean isStatusRestricted,
        boolean areDoneTasksDisplayed, Task task) {
        return !isStatusRestricted
            || (isStatusRestricted && areDoneTasksDisplayed == task.isDone());
    }

    // Checks if the date is restricted for FloatingTasks.
    private boolean checkDateRestrictionForFloatingTask(boolean isDateRestricted) {
        return !isDateRestricted;
    }

    // Checks if the deadline task matches the date restrictions.
    private boolean checkDateRestrictionForDeadlineTask(
        boolean isDateRestricted, Date startDate, Date endDate,
        DeadlineTask deadlineTask) {
        return !isDateRestricted
            || (isDateRestricted
                && (startDate == null || deadlineTask.getEndDate().compareTo(
                    startDate) >= 0) && (deadlineTask.getEndDate().compareTo(
                endDate) <= 0));
    }

    // Checks if the timed task matches the date restrictions.
    private boolean checkDateRestrictionForTimedTask(boolean isDateRestricted,
        Date startDate, Date endDate, TimedTask timedTask) {
        return !isDateRestricted
            || (isDateRestricted
                && (startDate == null || timedTask.getStartDate().compareTo(
                    startDate) >= 0) && (timedTask.getEndDate().compareTo(
                endDate) <= 0));
    }
	
	//@author A0109194A
    // Adds the given task to the arrays for keeping track of updated tasks,
    private void processUpdateHistory(Task relatedTask, Task task) {
        saveToOperationHistory(Global.CommandType.UPDATE);
        preupdatedTasks.push(relatedTask);
        updatedTasks.push(task);
    }
    
    private void saveToOperationHistory(Global.CommandType type) {
        operationHistory.push(type);
    }

    private void saveToUndoHistory(Global.CommandType type) {
        undoHistory.push(type);
    }

    public void clearOperationHistory() {
        operationHistory.clear();
    }

    public boolean contains(Task task) {
        return tasks.contains(task);
    }

    public Stack<Task> getAddedTasks() {
        return addedTasks;
    }

    public ArrayList<Task> getDeletedTasks() {
        return deletedTasks;
    }

    public Stack<Task> getPreupdatedTasks() {
        return preupdatedTasks;
    }

    public Stack<Task> getUpdatedTasks() {
        return updatedTasks;
    }

    public Stack<Task> getChangedTypeTasks() {
        return changedTypeTasks;
    }

    public Stack<ArrayList<Task>> getClearedTasks() {
        return clearedTasks;
    }

    public Stack<Global.CommandType> getOperationsHistory() {
        return operationHistory;
    }

    public ArrayList<String> getAllIds() {
        ArrayList<String> idList = new ArrayList<String>();
        for (Task t : tasks) {
            idList.add(t.getId());
        }
        return idList;
    }
    
    // Saves the temporary tasks ArrayList to the permanent storage.
    public void saveToPermanentStorage() {
        TaskCommander.storage.writeToFile(tasks);
    }

    // Loads the content from the permanent storage to the tasks ArrayList.
    public void loadFromPermanentStorage() {
        tasks = TaskCommander.storage.readFromFile();
    }

    //@author A0128620M 
    // Returns the given floating task displayed as a formatted String.
    private String getTaskInDisplayFormat(FloatingTask floatingTask) {
        return "\"" + floatingTask.getName() + "\"";
    }

    // Returns the given deadline task displayed as a formatted String.
    private String getTaskInDisplayFormat(DeadlineTask deadlineTask) {
        return "[by " + Global.dayFormat.format(deadlineTask.getEndDate())
            + " " + Global.timeFormat.format(deadlineTask.getEndDate()) + "]"
            + " \"" + deadlineTask.getName() + "\"";
    }

    // Returns the given timed task displayed as a formatted String.
    private String getTaskInDisplayFormat(TimedTask timedTask) {
        if (Global.dayFormat.format(timedTask.getStartDate()).equals(
            Global.dayFormat.format(timedTask.getEndDate()))) {
            return "[" + Global.dayFormat.format(timedTask.getStartDate())
                + " " + Global.timeFormat.format(timedTask.getStartDate())
                + "-" + Global.timeFormat.format(timedTask.getEndDate()) + "]"
                + " \"" + timedTask.getName() + "\"";
        } else {
            return "[" + Global.dayFormat.format(timedTask.getStartDate())
                + " " + Global.timeFormat.format(timedTask.getStartDate())
                + "-" + Global.dayFormat.format(timedTask.getEndDate()) + " "
                + Global.timeFormat.format(timedTask.getEndDate()) + "]"
                + " \"" + timedTask.getName() + "\"";
        }
    }

    private boolean isIndexInvalid(int index) {
        return index > tasks.size() - Global.INDEX_OFFSET || index < 0;
    }
}
