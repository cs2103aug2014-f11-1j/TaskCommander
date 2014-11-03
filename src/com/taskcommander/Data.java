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
 * 
 * Upon initialisation, the contents of the permanent storage will be pulled. After each 
 * command the data will be pushed to the permanent storage.
 */

public class Data {
	//@author A0128620M
	// Constructor, Variables and Logger
	private static Logger logger = Logger.getLogger(Controller.class.getName());	//TODO add logs

	private ArrayList<Task> tasks; // Contains all available task objects.

	//@author A0109194A
	private ArrayList<Task> deletedTasks; // Used by the Google API.
	private Stack<ArrayList<Task>> clearedTasks;

	// Variables to store history of tasks for undo command.
	private Stack<Task> addedTasks;
	private Stack<Task> preupdatedTasks;
	private Stack<Task> updatedTasks;
	private Stack<CommandType> operationHistory;
	private Stack<Global.CommandType> undoHistory;

	//@author A0128620M
	// Singleton instance for Data
	private static Data theOne;

	//@author A0128620M, A0109194A
	private Data() {
		tasks = new ArrayList<Task>();
		deletedTasks = new ArrayList<Task>();
		addedTasks = new Stack<Task>();
		preupdatedTasks = new Stack<Task>();
		updatedTasks = new Stack<Task>();
		clearedTasks = new Stack<ArrayList<Task>>();
		operationHistory = new Stack<Global.CommandType>();
		undoHistory = new Stack<Global.CommandType>();

		loadFromPermanentStorage();
	}

	//@author A0128620M
	/**
	 * Returns the only instance of Data.
	 * @return  Data instance.
	 */
	public static Data getInstance(){
		if (theOne == null) {    
			theOne = new Data();
		}
		return theOne;
	}

	//@author A0128620M, A0109194A
	// CRUD methods used internally.
	// Add methods
	/**
	 * Adds a Floating Task to the tasks list.
	 * 
	 * @param 	taskName        
	 * @return 	           Feedback for UI
	 */
	public String addFloatingTask(String taskName) {
		FloatingTask floatingTask = new FloatingTask(taskName);

		saveToOperationHistory(Global.CommandType.ADD);
		tasks.add(floatingTask);
		addedTasks.push(floatingTask);

		saveToPermanentStorage();

		return String.format(Global.MESSAGE_ADDED,getTaskInDisplayFormat(floatingTask));
	}

	/**
	 * Adds a Deadline Task to the tasks list.
	 * 
	 * @param 	taskName     
	 * @param 	endDate    
	 * @return 	           Feedback for UI
	 */
	public String addDeadlineTask(String taskName, Date endDate) {
		DeadlineTask deadlineTask = new DeadlineTask(taskName, endDate);

		saveToOperationHistory(Global.CommandType.ADD);
		tasks.add(deadlineTask);
		addedTasks.push(deadlineTask);

		saveToPermanentStorage();

		return String.format(Global.MESSAGE_ADDED, getTaskInDisplayFormat(deadlineTask));
	}

	/**
	 * Adds a Timed Task to the tasks list.
	 * 
	 * @param 	taskName  
	 * @param 	startDate    
	 * @param 	endDate   
	 * @return 	           Feedback for UI
	 */
	public String addTimedTask(String taskName, Date startDate, Date endDate) {
		TimedTask timedTask = new TimedTask(taskName,startDate,endDate);

		saveToOperationHistory(Global.CommandType.ADD);
		tasks.add(timedTask);
		addedTasks.push(timedTask);

		saveToPermanentStorage();

		return String.format(Global.MESSAGE_ADDED, getTaskInDisplayFormat(timedTask));
	}

	// Update methods
	/**
	 * Updates a FloatingTask with the given index and replaces the old taskName, 
	 * startDate or endDate respectively and changes the taskType if needed.
	 * If a given date or name parameter equals null, the old value remains.
	 *  
	 * @param index        Index of the task to delete, as a string
	 * @param taskName     Description of task    
	 * @return 	           Feedback for UI
	 */
	public String updateToFloatingTask(int index, String name) {
		assert index < tasks.size();  //TODO: @Andy: instead of asserting this, maybe you should do a check and handle errors
		if (tasks.isEmpty()) {
			return String.format(Global.MESSAGE_EMPTY);
		} 

		Task relatedTask = tasks.get(index);
		FloatingTask floatingTask;

		if  (relatedTask.getType() != Task.TaskType.FLOATING) {
			if (name == null) {
				name = relatedTask.getName();
			}
			floatingTask = new FloatingTask(name);
			// floatingTask.setEdited(relatedTask.isEdited());  TODO: @Sean: do we need that?
			floatingTask.setDone(relatedTask.isDone());
			deletedTasks.add(relatedTask);
			tasks.remove(index);
			tasks.add(index, floatingTask);
		} else {
			floatingTask = (FloatingTask) relatedTask;
			if (name != null) {
				floatingTask.setName(name);
			}
			floatingTask.setEdited(true);
		}
		processUpdateHistory(floatingTask);
		saveToPermanentStorage();
		return String.format(Global.MESSAGE_UPDATED, getTaskInDisplayFormat(floatingTask));
	}

	/**
	 * Updates a DeadlineTask with the given index and replaces the old taskName, 
	 * startDate or endDate respectively and changes the taskType if needed.
	 * If a given date or name parameter equals null, the old value remains.
	 * 
	 * @param index        Index of the task to delete, as a string
	 * @param taskName     Description of task   
	 * @param endDate      
	 * @return 	           Feedback for UI
	 */
	public String updateToDeadlineTask(int index, String name, Date endDate) {
		assert index < tasks.size();
		if (tasks.isEmpty()) {
			return String.format(Global.MESSAGE_EMPTY);
		} 

		Task relatedTask = tasks.get(index);
		DeadlineTask deadlineTask;
		if  (relatedTask.getType() != Task.TaskType.DEADLINE) {
			assert endDate != null;
			if (name == null) {
				name = relatedTask.getName();
			}
			deadlineTask = new DeadlineTask(name,endDate);
			// deadlineTask.setEdited(relatedTask.isEdited());  TODO: @Sean: do we need that?
			deadlineTask.setDone(relatedTask.isDone());

			deletedTasks.add(relatedTask);
			tasks.remove(index);
			tasks.add(index, deadlineTask);
		} else {
			deadlineTask = (DeadlineTask) relatedTask;
			if (name != null) {
				deadlineTask.setName(name);
			}
			if (endDate != null) {
				deadlineTask.setEndDate(endDate);
			}
			deadlineTask.setEdited(true);
		}
		processUpdateHistory(deadlineTask);
		saveToPermanentStorage();
		return String.format(Global.MESSAGE_UPDATED, getTaskInDisplayFormat(deadlineTask));
	}

	/**
	 * Updates a TimedTask with the given index and replaces the old taskName, 
	 * startDate or endDate respectively and changes the taskType if needed.
	 * If a given date or name parameter equals null, the old value remains.
	 * 
	 * @param index        Index of the task to delete, as a string
	 * @param taskName     Description of task
	 * @param startDate    
	 * @param endDate      
	 * @return 	           Feedback for UI
	 */
	public String updateToTimedTask(int index, String name, Date startDate, Date endDate) {
		assert index < tasks.size();
		if (tasks.isEmpty()) {
			return String.format(Global.MESSAGE_EMPTY);
		} 

		Task relatedTask = tasks.get(index);
		TimedTask timedTask;
		if  (!relatedTask.getType().equals(Task.TaskType.TIMED)) {
			assert startDate != null;
			assert endDate != null;
			if (name == null) {
				name = relatedTask.getName();
			}
			timedTask = new TimedTask(name,startDate,endDate);
			// timedTask.setEdited(tasks.get(index).isEdited());  TODO: @Sean: do we need that?
			timedTask.setDone(relatedTask.isDone());

			deletedTasks.add(relatedTask);
			tasks.remove(index);
			tasks.add(index, timedTask);
		} else {
			timedTask = (TimedTask) relatedTask;

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
		processUpdateHistory(timedTask);
		saveToPermanentStorage();
		return String.format(Global.MESSAGE_ADDED, getTaskInDisplayFormat(timedTask));

	}

	// Helper method for update
	/**
	 * Adds the given task to the arrays for keeping track of updated tasks,
	 * and adds the update command to history.
	 * @param task
	 */
	private void processUpdateHistory(Task task) {
		saveToOperationHistory(Global.CommandType.UPDATE);
		preupdatedTasks.push(task);
		updatedTasks.push(task);
	}

	//@author A0128620M
	//Done, open, delete, clear methods
	/**
	 * Marks a task as done.
	 * 
	 * @param index        Index of the done task 
	 * @return 	           Feedback for UI
	 */
	public String done(int index) {
		if (tasks.isEmpty()) {
			return String.format(Global.MESSAGE_EMPTY);
		} 

		if (index > tasks.size() - Global.INDEX_OFFSET || index < 0 ) {
			return String.format(Global.MESSAGE_NO_INDEX, index);
		}

		Task doneTask = tasks.get(index);
		if (doneTask.isDone()) {
			return String.format(Global.MESSAGE_ALREADY_DONE);
		} else {
			doneTask.markDone();
			saveToPermanentStorage();
			switch (doneTask.getType()) {
			case FLOATING:
				FloatingTask floatingTask = (FloatingTask) doneTask;
				return String.format(Global.MESSAGE_DONE, getTaskInDisplayFormat(floatingTask));
			case DEADLINE:
				DeadlineTask deadlineTask = (DeadlineTask) doneTask;
				return String.format(Global.MESSAGE_DONE, getTaskInDisplayFormat(deadlineTask));
			default:
				TimedTask timedTask = (TimedTask) doneTask;// TODO: find better solution than default
				return String.format(Global.MESSAGE_DONE, getTaskInDisplayFormat(timedTask));
			}
		}
	}

	/**
	 * Marks a task as open.
	 * 
	 * @param index        Index of the open tasks   
	 * @return             Feedback for UI
	 */
	public String open(int index) {
		if (tasks.isEmpty()) {
			return String.format(Global.MESSAGE_EMPTY);
		} 

		if (index > tasks.size() - Global.INDEX_OFFSET || index < 0 ) {
			return String.format(Global.MESSAGE_NO_INDEX, index);
		}

		Task openTask = tasks.get(index);
		if (!openTask.isDone()) {
			return String.format(Global.MESSAGE_ALREADY_OPEN);
		} else {
			openTask.markOpen();
			saveToPermanentStorage();
			switch (openTask.getType()) {
			case FLOATING:
				FloatingTask floatingTask = (FloatingTask) openTask;
				return String.format(Global.MESSAGE_OPEN, getTaskInDisplayFormat(floatingTask));
			case DEADLINE:
				DeadlineTask deadlineTask = (DeadlineTask) openTask;
				return String.format(Global.MESSAGE_OPEN, getTaskInDisplayFormat(deadlineTask));
			default:
				TimedTask timedTask = (TimedTask) openTask;// TODO: find better solution than default
				return String.format(Global.MESSAGE_OPEN, getTaskInDisplayFormat(timedTask));
			}
		}
	}

	//@author A0128620M, A0109194A
	/**
	 * Deletes the task with the given index (as shown with 'display' command).
	 * Does not execute if there are no lines and if a wrong index is given.
	 * 
	 * @param index        Index of the task to delete, as a string. 
	 * @return             Feedback for user.

	 */
	public String deleteTask(int index) {
		if (tasks.isEmpty()) {
			return String.format(Global.MESSAGE_EMPTY);
		} 

		if (index > tasks.size() - Global.INDEX_OFFSET || index < 0 ) {
			return String.format(Global.MESSAGE_NO_INDEX, index);
		} else {
			Task deletedTask = tasks.get(index);
			saveToOperationHistory(Global.CommandType.DELETE);
			deletedTasks.add(deletedTask);
			tasks.remove(index);
			saveToPermanentStorage();
			switch (deletedTask.getType()) {		// TODO: Extract to method
			case FLOATING:
				FloatingTask floatingTask = (FloatingTask) deletedTask;
				return String.format(Global.MESSAGE_DELETED, getTaskInDisplayFormat(floatingTask));
			case DEADLINE:
				DeadlineTask deadlineTask = (DeadlineTask) deletedTask;
				return String.format(Global.MESSAGE_DELETED, getTaskInDisplayFormat(deadlineTask));
			default:
				TimedTask timedTask = (TimedTask) deletedTask;// TODO: find better solution than default
				return String.format(Global.MESSAGE_DELETED, getTaskInDisplayFormat(timedTask));		
			}
		}
	}

	/**
	 * Clears all tasks from memory.
	 * 
	 * @param userCommand 
	 * @return             Feedback for user.
	 */
	public String clearTasks() {
		ArrayList<Task> cleared = new ArrayList<Task>();
		cleared.addAll(tasks);
		deletedTasks.addAll(tasks);
		clearedTasks.push(cleared);
		tasks.clear();
		saveToOperationHistory(Global.CommandType.CLEAR);
		saveToPermanentStorage();
		return String.format(Global.MESSAGE_CLEARED);
	}

	//@author A0109194A
	// CRUD methods used by Google Integration component
	// Add methods
	/**
	 * Adds a Task to the tasks list by forwarding the task's attributes to the respective
	 * add method. It is usually called by the SyncHandler class.
	 * @param 	task     
	 * @return 	           Feedback for UI
	 */
	public String addTask(Task task) {
		logger.log(Level.INFO, "Called addTask(Task task)");
<<<<<<< HEAD
		if (task.getId() != null) {		
			switch (task.getType()) {
			case FLOATING:
				FloatingTask floatingTask = (FloatingTask) task;
				return addFloatingTask(floatingTask.getName(), task.getId());
			case DEADLINE:
				DeadlineTask deadlineTask = (DeadlineTask) task;
				return addDeadlineTask(deadlineTask.getName(), deadlineTask.getEndDate(), task.getId());
			default:
				TimedTask timedTask = (TimedTask) task;
				return addTimedTask(timedTask.getName(), timedTask.getStartDate(), timedTask.getEndDate(), task.getId());
			}	
		} else {
			return Global.MESSAGE_NULL_ID;
=======
		assert (task.getId() != null); //TODO: @Sean: consider changing this to an if-else, otherwise an AssertionError could be thrown
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
>>>>>>> a1fd3d9999160399e8c5918eb68f54b6b03158fb
		}
	}

	/**
	 * Adds a Floating Task to the tasks list.
	 * @param floatingTask
	 * @return 	           Feedback for UI
	 */
	public String addFloatingTask(FloatingTask floatingTask) {
		tasks.add(floatingTask);
		saveToPermanentStorage();
		return String.format(Global.MESSAGE_ADDED, getTaskInDisplayFormat(floatingTask));
	}

	/**
	 * Adds a Deadline Task to the tasks list.
	 * @param deadlineTask
	 * @return 	           Feedback for UI
	 */
	public String addDeadlineTask(DeadlineTask deadlineTask) {
		tasks.add(deadlineTask);
		saveToPermanentStorage();
		return String.format(Global.MESSAGE_ADDED, getTaskInDisplayFormat(deadlineTask));
	}

	/**
	 * Adds a Timed Task to the tasks list.
	 * @param 	timedTask
	 * @return 	           Feedback for UI
	 */
	public String addTimedTask(TimedTask timedTask) {	
		tasks.add(timedTask);
		saveToPermanentStorage();
		return getTaskInDisplayFormat(timedTask);
	}

	// Update methods
	/**
	 * Updates a task with a FloatingTask object as a parameter.
	 * It is usually called by the SyncHandler.
	 * @param 		index
	 * @param 		task
	 * @return		Feedback for user
	 */
	public String updateToFloatingTask(int index, FloatingTask task) {
		FloatingTask floatingTask;
		if  (tasks.get(index).getType() != Task.TaskType.FLOATING) {
			Task toChange = tasks.get(index);
			floatingTask = new FloatingTask(task.getName());
			floatingTask.setEdited(toChange.isEdited());
			floatingTask.setDone(toChange.isDone());
			deletedTasks.add(toChange);
			tasks.remove(index);
			tasks.add(index, floatingTask);
		} else {
			floatingTask = (FloatingTask) tasks.get(index);
			preupdatedTasks.push(floatingTask);
			if (task.getName() != null) {
				floatingTask.setName(task.getName());
			}
			floatingTask.setEdited(true);
		}
		floatingTask.setUpdated(task.getUpdated());
		saveToPermanentStorage();
		return String.format(Global.MESSAGE_UPDATED,getTaskInDisplayFormat(floatingTask));
	}


	/**
	 * Updates a task with a DeadlineTask object as a parameter.
	 * It is usually called by the SyncHandler.
	 * 
	 * @param 		index
	 * @param 		task
	 * @return		Feedback for user
	 */
	public String updateToDeadlineTask(int index, DeadlineTask task) {
		DeadlineTask deadlineTask;
		if  (tasks.get(index).getType() != Task.TaskType.DEADLINE) {
			Task toChange = tasks.get(index);
			deadlineTask = new DeadlineTask(task.getName(),task.getEndDate());
			deadlineTask.setEdited(tasks.get(index).isEdited());
			deadlineTask.setDone(tasks.get(index).isDone());
			deadlineTask.setUpdated(task.getUpdated());
			deletedTasks.add(toChange);
			tasks.remove(index);
			tasks.add(index, deadlineTask);
			saveToPermanentStorage();
			return String.format(Global.MESSAGE_UPDATED,getTaskInDisplayFormat(deadlineTask));
		} else {
			deadlineTask = (DeadlineTask) tasks.get(index);
			preupdatedTasks.push(deadlineTask);
			if (task.getName() != null) {
				deadlineTask.setName(task.getName());
			}
			if (task.getEndDate() != null) {
				deadlineTask.setEndDate(task.getEndDate());
			}
			deadlineTask.setEdited(true);
		}
		deadlineTask.setUpdated(task.getUpdated());
		saveToPermanentStorage();
		return String.format(Global.MESSAGE_UPDATED,getTaskInDisplayFormat(deadlineTask));
	}

	/**
	 * Updates a task with a TimedTask object as a parameter.
	 * It is usually called by the SyncHandler.
	 * 
	 * @param 		index
	 * @param 		task
	 * @return		Feedback for user
	 */
	public String updateToTimedTask(int index, TimedTask task) {
		Task relatedTask = tasks.get(index);
		TimedTask timedTask;
		if (relatedTask.getType() != Task.TaskType.TIMED) {
			timedTask = new TimedTask(task.getName(), task.getStartDate(), task.getEndDate());
			timedTask.setEdited(relatedTask.isEdited());
			timedTask.setDone(relatedTask.isDone());
			deletedTasks.add(relatedTask);
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
			timedTask.setEdited(true);
		}
		timedTask.setUpdated(task.getUpdated());
		saveToPermanentStorage();
		return String.format(Global.MESSAGE_UPDATED, getTaskInDisplayFormat(timedTask));
	}

	// Delete method
	/**
	 * Deletes the task directly from the tasks list without the index.
	 * Used to delete tasks when syncing.
	 * 
	 * @param 	task
	 * @return 	       If the delete was successful.
	 */
	public boolean deleteTask(Task task) {
		if (tasks.isEmpty()) {
			return false;
		} else {
			deletedTasks.add(task);
			tasks.remove(task);
			saveToPermanentStorage();
			return true;
		}
	}

	// Undo method
	/**
	 * This operation undoes the latest command.
	 * It supports Add, Delete, Update, and Clear commands.
	 * @return Feedback for UI
	 */
	public String undo() {
		if (operationHistory.empty()) {
			return Global.MESSAGE_UNDO_EMPTY;
		}
		Global.CommandType type = operationHistory.pop();
		Global.CommandType undoCommand = null;
		switch(type) {
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
		default:
			undo(); //Calls undo again to look for one of the four commands above
		}
		saveToUndoHistory(undoCommand);
		saveToPermanentStorage();
		return String.format(Global.MESSAGE_UNDONE, type);
	}

	/**
	 * Undoes the add command.
	 * @return           Success of undo.
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

	/**
	 * Undoes the delete command
	 * @return           Success of undo.
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

	/**
	 * Undoes the update command
	 * @return           Success of undo.
	 */
	private boolean undoUpdate() {
		Task updated = updatedTasks.pop();
		Task beforeUpdate = preupdatedTasks.pop();
		if (updated.getType() != beforeUpdate.getType()) {
			deletedTasks.remove(deletedTasks.size() - 1);
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

	/**
	 * Undoes the Clear command
	 * @return           Success of undo.
	 */
	private boolean undoClear() {
		ArrayList<Task> toRestore = clearedTasks.pop();
		tasks.addAll(toRestore);
		return true;
	}

	public void saveToOperationHistory(Global.CommandType type) {
		operationHistory.push(type);
	}

	public void saveToUndoHistory(Global.CommandType type) {
		undoHistory.push(type);
	}

	//@author A0128620M
	// Getter methods
	/**
	 * Returns a sorted list consisting of copies of all tasks of the tasks list.
	 * @return 	  Copy of tasks list.
	 */
	public ArrayList<Task> getCopiedTasks() {
		ArrayList<FloatingTask> floatingTasks = new ArrayList<FloatingTask>();
		ArrayList<DatedTask> datedTasks = new ArrayList<DatedTask>();
		ArrayList<Task> allTasks = new ArrayList<Task>();

		for(Task task: tasks) {
			if(task.getType().equals(Task.TaskType.FLOATING)) {
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
	 * Returns a sorted list consisting of copies of those tasks of the tasks list 
	 * which satisfy the given DateTime, TaskType, Status and Search restrictions.
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
	 * @return 	                        Sorted copy of tasks list satisfying given restrictions.
	 */
	public ArrayList<Task> getCopiedTasks(boolean isDateRestricted, Date startDate, Date endDate, 
			boolean isTaskTypeRestricted, boolean areFloatingTasksDisplayed, 
			boolean areDeadlineTasksDisplayed, boolean areTimedTasksDisplayed, 
			boolean isStatusRestricted, boolean areDoneTasksDisplayed, 
			boolean areOpenTasksDisplayed, boolean isSearchedWordRestricted, 
			ArrayList<String> searchedWords) {
		ArrayList<FloatingTask> floatingTasks = new ArrayList<FloatingTask>();
		ArrayList<DatedTask> datedTasks = new ArrayList<DatedTask>();
		ArrayList<Task> concernedTasks = new ArrayList<Task>();
		boolean containsSearchedWords = false;

		for (Task task: tasks) {
			// Step 1: Check SearchedWords
			if (isSearchedWordRestricted) {
				containsSearchedWords = checkStringForWords(searchedWords, task.getName()); 
			}

			if (!isSearchedWordRestricted || containsSearchedWords) {	
				// Step 2: Check Status
				if (checkStatusRestricted(isStatusRestricted, areDoneTasksDisplayed, task)) {
					// Step 3: Check Type
					if (isTaskTypeRestricted) {
						if(task.getType() == Task.TaskType.FLOATING && areFloatingTasksDisplayed) {
							// Step 4: Check DatePeriod
							if (checkDateRestrictionForFloatingTask(isDateRestricted)) { 	
								floatingTasks.add(new FloatingTask((FloatingTask) task));
							}
						} else if (task.getType() == Task.TaskType.DEADLINE && areDeadlineTasksDisplayed) { 	
							DeadlineTask deadlineTask = (DeadlineTask) task;
							if (checkDateRestrictionForDeadlineTask(isDateRestricted, startDate, endDate, deadlineTask)) { 
								datedTasks.add(new DeadlineTask((DeadlineTask) task));
							}
						} else if (task.getType() == Task.TaskType.TIMED && areTimedTasksDisplayed) {
							TimedTask timedTask = (TimedTask) task;
							if (checkDateRestrictionForTimedTask(isDateRestricted, startDate, endDate, timedTask)) {
								datedTasks.add(new TimedTask((TimedTask) task));
							}
						}		
					} else {
						//TODO: Task type not restricted 
						//TODO: Add all tasks?
					}
				}	
			}
		}
		Collections.sort(floatingTasks);
		concernedTasks.addAll(floatingTasks);
		Collections.sort(datedTasks);
		concernedTasks.addAll(datedTasks);

		return concernedTasks;
	}


	// Helper methods for getCopiedTasks
	/**
	 * Checks if given string contains any strings from the given ArrayList.
	 * @param searchedWords
	 * @param string
	 * @return
	 */
	private boolean checkStringForWords(ArrayList<String> searchedWords, String s) {
		boolean containsSearchedWords;
		containsSearchedWords = true;
		for(String searchedWord : searchedWords) {
			if (!s.contains(searchedWord)) {
				logger.log(Level.INFO, "Doesn't contain the word");
				containsSearchedWords = false;
				break;
			}
		}
		return containsSearchedWords;
	}

	/**
	 * Checks if the status of the given task matches the status parameters.
	 * @param isStatusRestricted
	 * @param areDoneTasksDisplayed
	 * @param task
	 * @return
	 */
	private boolean checkStatusRestricted(boolean isStatusRestricted, boolean areDoneTasksDisplayed, Task task) {
		return !isStatusRestricted ||
				(isStatusRestricted && areDoneTasksDisplayed == task.isDone() );
	}

	// Checks if the date is restricted for FloatingTasks.
	private boolean checkDateRestrictionForFloatingTask(boolean isDateRestricted) {
		return !isDateRestricted;
	}

	/**
	 * Checks if the date is restricted for DeadlineTasks, and if the DeadlineTask
	 * matches the parameters given.
	 * @param isDateRestricted
	 * @param startDate
	 * @param endDate
	 * @param deadlineTask
	 * @return
	 */
	private boolean checkDateRestrictionForDeadlineTask(boolean isDateRestricted, Date startDate, 
			Date endDate, DeadlineTask deadlineTask) {
		return !isDateRestricted || (isDateRestricted && 
				(startDate == null || deadlineTask.getEndDate().compareTo(startDate) >= 0) && 
				(deadlineTask.getEndDate().compareTo(endDate) <= 0) );
	}

	/**
	 * Checks if the date is restricted for TimedTasks, and if the TimedTask
	 * matches the parameters given.
	 * @param isDateRestricted
	 * @param startDate
	 * @param endDate
	 * @param timedTask
	 * @return
	 */
	private boolean checkDateRestrictionForTimedTask(boolean isDateRestricted, Date startDate, 
			Date endDate, TimedTask timedTask) {
		return !isDateRestricted || (isDateRestricted && 
				(startDate == null || timedTask.getStartDate().compareTo(startDate) >= 0) && 
				(timedTask.getEndDate().compareTo(endDate) <= 0) );
	}

	/**
	 * Returns the index of the given task object within the tasks ArrayList.
	 * @return  index
	 */
	public int getIndexOf(Task task) {
		return tasks.indexOf(task);
	}

	/**
	 * Checks if the tasks ArrayList contains the given task.
	 * @return  index
	 */
	public boolean contains(Task task) {
		return tasks.contains(task);
	}

	public ArrayList<Task> getDeletedTasks() {
		return deletedTasks;
	}

	public Stack<Task> getPreupdatedTasks() {
		return preupdatedTasks;
	}

	public ArrayList<String> getAllIds() {
		ArrayList<String> idList = new ArrayList<String>();
		for (Task t : tasks) {
			idList.add(t.getId());
		}
		return idList;
	}

	/**
	 * Returns all tasks.
	 * @return        Tasks list.
	 */
	public ArrayList<Task> getAllTasks() {		//TODO: @Sean still encessary?
		return tasks;
	}

	// Display formatting methods
	/**
	 * Returns the given task displayed as a formatted String.
	 * @param floatingTask
	 * @return               Task displayed as a formatted String
	 */
	private String getTaskInDisplayFormat(FloatingTask floatingTask) {
		return "\"" + floatingTask.getName() + "\"";
	}

	/**
	 * Returns the given task displayed as a formatted String.
	 * @param deadlineTask
	 * @return               Task displayed as a formatted String
	 */
	private String getTaskInDisplayFormat(DeadlineTask deadlineTask) {
		return "[by "+ Global.dayFormat.format(deadlineTask.getEndDate())+ " "+ Global.timeFormat.format(deadlineTask.getEndDate()) + "]"+ " \"" + deadlineTask.getName() + "\"";
	}

	/**
	 * Returns the given task displayed as a formatted String.
	 * @param timedTask
	 * @return               Task displayed as a formatted String
	 */
	private String getTaskInDisplayFormat(TimedTask timedTask) {
		if(Global.dayFormat.format(timedTask.getStartDate()).equals(Global.dayFormat.format(timedTask.getEndDate()))) {
			return "["+ Global.dayFormat.format(timedTask.getStartDate())+ " "+ 
					Global.timeFormat.format(timedTask.getStartDate())+ "-"+ 
					Global.timeFormat.format(timedTask.getEndDate())+ "]"+ 
					" \"" + timedTask.getName() + "\""; 
		} else {
			return "["+ Global.dayFormat.format(timedTask.getStartDate())+ " "+ 
					Global.timeFormat.format(timedTask.getStartDate())+ "-"+ 
					Global.dayFormat.format(timedTask.getEndDate()) +" "+ 
					Global.timeFormat.format(timedTask.getEndDate())+ "]"+ 
					" \"" + timedTask.getName() + "\"";
		}
	}

	//@author A0112828H
	// Saves the temporary tasks ArrayList to the permanent storage.
	public void saveToPermanentStorage() {
		TaskCommander.storage.writeToFile(tasks);
	}

	// Loads the content from the permanent storage to the tasks ArrayList.
	public void loadFromPermanentStorage() {
		tasks = TaskCommander.storage.readFromFile(); 
	}	
}