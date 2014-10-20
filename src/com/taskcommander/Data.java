package com.taskcommander;
import java.util.Date;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.taskcommander.Global.CommandType;


/**
 * This class represents the Data component. Besides storing the data temporary, it also contains
 * all of the methods needed to manipulate the task objects within the ArrayList. At the beginning 
 * the content of the permanent storage is pulled to the temporary one. After each command the data 
 * will be pushed to the permanent storage.
 * 
 * @author A0128620M, A0109194A
 */

public class Data {

	// Logging
	private static Logger logger = Logger.getLogger("Data");
	
	/**
	 * This variable is initialized with the one and only instance of the Data class 
	 * (see also getInstance() below)
	 */
	private static Data theOne;
	
	/**
	 * This operation which returns either a new instance of the Data or an existing one, if any.
	 * Therefore, it ensures that there will be only one instance of the Controller (see Singleton pattern)
	 */
	public static Data getInstance(){
		if (theOne == null) {    
			theOne = new Data();
		}
		return theOne;
	}
	
	/** 
	 * This Array contains all available task objects.
	 */
	public ArrayList<Task> tasks;

	/**
	 * This Array contains the state of the tasks ArrayList before the last execution of a user command.
	 */
	public ArrayList<Task> tasksHistory;
	
	/**
	 * This Array contains all the deleted tasks, needed by the GoogleAPI.
	 */
	public ArrayList<Task> deletedTasks;
	
	/**
	 * This Stack contains the history of all operations.
	 */
	public Stack<CommandType> operationHistory;
	
	/**
	 * This Stack contains the history of all operations.
	 */
	public Stack<Global.CommandType> undoHistory;
	
	
	/**
	 * Constructor
	 */
	private Data() {
		tasks = new ArrayList<Task>();
		tasksHistory = new ArrayList<Task>();
		deletedTasks = new ArrayList<Task>();
		operationHistory = new Stack<Global.CommandType>();
		undoHistory = new Stack<Global.CommandType>();
		
		load();
	}

	/**
	 * This operation saves the temporary tasks ArrayList to the permanent storage.
	 * @author A0112828H
	 */
	public void save() {
		TaskCommander.storage.writeToFile(tasks);
	}
	
	/**
	 * This operation loads the content from the permanent storage to the tasks ArrayList.
	 * @author A0112828H
	 */
	public void load() {
		tasks = TaskCommander.storage.readFromFile(); 
	}	

	/**
	 * This operation adds a Task to the tasks ArrayList.
	 * It is usually called by the SyncHandler class.
	 * 
	 * @param 	task     
	 * @return 	feedback for UI
	 */
	public String addTask(Task task) {
		logger.log(Level.INFO, "Called addTask(Task task)");
		assert task.getId() != null;
		switch ( task.getType()) {
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
	}
	
	/**
	 * This operation adds a TimedTask to the tasks ArrayList.
	 * 
	 * @param 	taskName  
	 * @param 	startDate    
	 * @param 	endDate       
	 * @return 	feedback for UI
	 */
	public String addTimedTask(String taskName, Date startDate, Date endDate) {
		TimedTask timedTask = new TimedTask(taskName,startDate,endDate);
		saveToTaskHistory();
		tasks.add(timedTask);
		save();
		return String.format(Global.MESSAGE_ADDED,"["+ Global.dayFormat.format(timedTask.getStartDate())+ " "+ Global.timeFormat.format(timedTask.getStartDate())+ "-"+ Global.timeFormat.format(timedTask.getEndDate()) + "]"+ " \"" + timedTask.getName() + "\"");
	}
	
	/**
	 * This operation adds a TimedTask, and sets an ID to it
	 * 
	 * @param 	taskName
	 * @param 	startDate
	 * @param 	endDate
	 * @param 	id
	 * @return 	feedback for UI
	 */
	public String addTimedTask(String taskName, Date startDate, Date endDate, String googleID) {
		TimedTask timedTask = new TimedTask(taskName,startDate,endDate, googleID);
		saveToTaskHistory();
		tasks.add(timedTask);
		save();
		return String.format(Global.MESSAGE_ADDED,"["+ Global.dayFormat.format(timedTask.getStartDate())+ " "+ Global.timeFormat.format(timedTask.getStartDate())+ "-"+ Global.timeFormat.format(timedTask.getEndDate()) + "]"+ " \"" + timedTask.getName() + "\"");
	}
	
	/**
	 * This operation adds a DeadlineTask to the tasks ArrayList.
	 * 
	 * @param 	taskName     
	 * @param 	endDate       
	 * @return 	feedback for UI
	 */
	public String addDeadlineTask(String taskName, Date endDate) {
		DeadlineTask deadlineTask= new DeadlineTask(taskName,endDate);
		saveToTaskHistory();
		tasks.add(deadlineTask);
		save();
		return String.format(Global.MESSAGE_ADDED,"[by "+ Global.dayFormat.format(deadlineTask.getEndDate())+ " "+ Global.timeFormat.format(deadlineTask.getEndDate()) + "]"+ " \"" + deadlineTask.getName() + "\"");
	}
	
	/**
	 * This operation adds a DeadlineTask and sets a GoogleID to it.
	 * 
	 * @param taskName
	 * @param endDate
	 * @param googleID
	 * @return
	 */
	public String addDeadlineTask(String taskName, Date endDate, String googleID) {
		DeadlineTask deadlineTask= new DeadlineTask(taskName, endDate, googleID);
		saveToTaskHistory();
		tasks.add(deadlineTask);
		save();
		return String.format(Global.MESSAGE_ADDED,"[by "+ Global.dayFormat.format(deadlineTask.getEndDate())+ " "+ Global.timeFormat.format(deadlineTask.getEndDate()) + "]"+ " \"" + deadlineTask.getName() + "\"");
	}
	
	/**
	 * This operation adds a FloatingTask to the tasks ArrayList.
	 * 
	 * @param 	taskName        
	 * @return 	feedback for UI
	 */
	public String addFloatingTask(String taskName) {
		FloatingTask floatingTask = new FloatingTask(taskName);
		saveToTaskHistory();
		tasks.add(floatingTask);
		save();
		return String.format(Global.MESSAGE_ADDED,"\"" + floatingTask.getName() + "\"");
	}
	
	/**
	 * This operation adds a FloatingTask and sets a GoogleID to it.
	 * 
	 * @param taskName
	 * @param googleID
	 * @return
	 */
	public String addFloatingTask(String taskName, String googleID) {
		FloatingTask floatingTask = new FloatingTask(taskName, googleID);
		saveToTaskHistory();
		tasks.add(floatingTask);
		save();
		return String.format(Global.MESSAGE_ADDED,"\"" + floatingTask.getName() + "\"");
	}

	/**
	 * This operation displays all tasks by forwarding all the needed information to the UI.
	 *  
	 * @return 	ArrayList<Task>
	 */
	public ArrayList<Task> getCopiedTasks() {

		ArrayList<FloatingTask> floatingTasks = new ArrayList<FloatingTask>();
		ArrayList<DatedTask> datedTasks = new ArrayList<DatedTask>();
		ArrayList<Task> allTasks = new ArrayList<Task>();
		
		for(Task task: tasks) {
			if(task.getType() == Task.TaskType.FLOATING) {
				floatingTasks.add(new FloatingTask((FloatingTask) task));	// TODO: use cloned task with "new FloatingTask((FloatingTask)" to  add a copy of the respective task, not the original
			} else if (task.getType() == Task.TaskType.DEADLINE) {
				datedTasks.add(new DeadlineTask((DeadlineTask) task));
			} else if (task.getType() == Task.TaskType.TIMED) {
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
	 * This operation displays the tasks of the given DatePeriod, taskType and status.
	 * 
	 * @param isDateTimeRestricted
	 * @param startDate
	 * @param endDate
	 * @param isTaskTypeRestricted
	 * @param shownFloatingTask
	 * @param shownDeadlineTask
	 * @param shownTimedTask
	 * @param isStatusRestricted
	 * @param done
	 * @return 	ArrayList<Task>
	 */
	public ArrayList<Task> getCopiedTasks(boolean isDateTimeRestricted, Date startDate, Date endDate, boolean isTaskTypeRestricted, boolean shownFloatingTask, boolean shownDeadlineTask, boolean shownTimedTask, boolean isStatusRestricted, boolean status, boolean isSearchedWordRestricted, String[] searchedWords) {
		ArrayList<FloatingTask> floatingTasks = new ArrayList<FloatingTask>();
		ArrayList<DatedTask> datedTasks = new ArrayList<DatedTask>();
		ArrayList<Task> concernedTasks = new ArrayList<Task>();
		boolean containsSearchedWords = false;
		
		for(Task task: tasks) {

			// Step 1: Check SearchedWords
			if (isSearchedWordRestricted) {
				containsSearchedWords = true;
				for(String searchedWord : searchedWords) {
					if (!task.getName().contains(searchedWord)) {
						logger.log(Level.INFO, "Doesn't contain the word");
						containsSearchedWords = false;
						break;
					}
				} 
			}
			
			if (!isSearchedWordRestricted || containsSearchedWords) {	

				// Step 2: Check Status
				if (!isStatusRestricted || (isStatusRestricted && status == task.isDone() )) {
					// Step 3: Check Type
					if(task.getType() == Task.TaskType.FLOATING && (!isTaskTypeRestricted || (isTaskTypeRestricted && shownFloatingTask))) {	
						// Step 4: Check DatePeriod
						if (!isDateTimeRestricted) {
							floatingTasks.add(new FloatingTask((FloatingTask) task));
						}
					} else if (task.getType() == Task.TaskType.DEADLINE && (!isTaskTypeRestricted || (isTaskTypeRestricted && shownDeadlineTask))) {
						DeadlineTask deadlineTask = (DeadlineTask) task;
						if (!isDateTimeRestricted || (isDateTimeRestricted && (deadlineTask.getEndDate().compareTo(endDate) < 0) || deadlineTask.getEndDate().compareTo(endDate) == 0) ) { //TODO: Refactor Date Comparison methods
							datedTasks.add(new DeadlineTask((DeadlineTask) task));
						}
					} else if (task.getType() == Task.TaskType.TIMED && (!isTaskTypeRestricted || (isTaskTypeRestricted && shownTimedTask))) {
						TimedTask timedTask = (TimedTask) task;
						if (!isDateTimeRestricted || (isDateTimeRestricted && (timedTask.getStartDate().compareTo(startDate) > 0 || timedTask.getStartDate().compareTo(startDate) == 0) && (timedTask.getEndDate().compareTo(endDate) < 0) || timedTask.getEndDate().compareTo(endDate) == 0) ){
							datedTasks.add(new TimedTask((TimedTask) task));
						}
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
	
	/**
	 * This operation updates a TimedTask with the given index and replaces the old taskName, 
	 * startDate or endDate respectively and changes the taskType if needed.
	 * If a given date or name parameter equals null, the old value remains.
	 * 
	 * @param index        index of the task to delete, as a string
	 * @param taskName     description of task
	 * @param startDate    
	 * @param endDate      
	 * @return             feedback for UI
	 */
	public String updateToTimedTask(int index, String name, Date startDate, Date endDate) {
		if (tasks.isEmpty()) {
			return String.format(Global.MESSAGE_EMPTY);
		} 

		if (index > tasks.size() - Global.INDEX_OFFSET) {
			return String.format(Global.ERROR_MESSAGE_NO_INDEX, index);
		}
		if  (tasks.get(index).getType() != Task.TaskType.TIMED) {
			TimedTask timedTask = new TimedTask(name,startDate,endDate);
			timedTask.setEdited(tasks.get(index).isEdited());
			timedTask.setDone(tasks.get(index).isDone());
			saveToTaskHistory();
			deleteTask(index);
			tasks.add(index, timedTask);
			save();
			return String.format(Global.MESSAGE_UPDATED,"["+ Global.dayFormat.format(timedTask.getStartDate())+ " "+ Global.timeFormat.format(timedTask.getStartDate())+ "-"+ Global.timeFormat.format(timedTask.getEndDate()) + "]"+ " \"" + timedTask.getName() + "\"");
		} else {
			saveToTaskHistory();
			TimedTask timedTask = (TimedTask) tasks.get(index);
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
			save();
			return String.format(Global.MESSAGE_UPDATED,"["+ Global.dayFormat.format(timedTask.getStartDate())+ " "+ Global.timeFormat.format(timedTask.getStartDate())+ "-"+ Global.timeFormat.format(timedTask.getEndDate()) + "]"+ " \"" + timedTask.getName() + "\"");
		}
		
	}
	
	
	/**
	 * This operation updates a task with a TimedTask object as a parameter
	 * It is usually called by the SyncHandler
	 * 
	 * @param 		index
	 * @param 		task
	 * @return		Feedback for user
	 */
	public String updateToTimedTask(int index, TimedTask task) {
		if (tasks.get(index).getType() != Task.TaskType.TIMED) {
			TimedTask timedTask = new TimedTask(task.getName(), task.getStartDate(), task.getEndDate());
			timedTask.setEdited(tasks.get(index).isEdited());
			timedTask.setDone(tasks.get(index).isDone());
			timedTask.setUpdated(task.getUpdated());
			saveToTaskHistory();
			deleteTask(index);
			tasks.add(index, timedTask);
			save();
			return String.format(Global.MESSAGE_UPDATED,"["+ Global.dayFormat.format(timedTask.getStartDate())+ " "+ Global.timeFormat.format(timedTask.getStartDate())+ "-"+ Global.timeFormat.format(timedTask.getEndDate()) + "]"+ " \"" + timedTask.getName() + "\"");
		} else {
			saveToTaskHistory();
			TimedTask timedTask = (TimedTask) tasks.get(index);
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
			timedTask.setUpdated(task.getUpdated());
			save();
			return String.format(Global.MESSAGE_UPDATED,"["+ Global.dayFormat.format(timedTask.getStartDate())+ " "+ Global.timeFormat.format(timedTask.getStartDate())+ "-"+ Global.timeFormat.format(timedTask.getEndDate()) + "]"+ " \"" + timedTask.getName() + "\"");
		}
		
	}
	
	/**
	 * This operation updates a DeadlineTask with the given index and replaces the old taskName, 
	 * startDate or endDate respectively and changes the taskType if needed.
	 * If a given date or name parameter equals null, the old value remains.
	 * 
	 * @param index        index of the task to delete, as a string
	 * @param taskName     description of task   
	 * @param endDate      
	 * @return             feedback for UI
	 */
	public String updateToDeadlineTask(int index, String name, Date endDate) {
		if (tasks.isEmpty()) {
			return String.format(Global.MESSAGE_EMPTY);
		} 

		if (index > tasks.size() - Global.INDEX_OFFSET) {
			return String.format(Global.ERROR_MESSAGE_NO_INDEX, index);
		}

		if  (tasks.get(index).getType() != Task.TaskType.DEADLINE) {
			DeadlineTask deadlineTask = new DeadlineTask(name,endDate);
			deadlineTask.setEdited(tasks.get(index).isEdited());
			deadlineTask.setDone(tasks.get(index).isDone());
			saveToTaskHistory();
			deleteTask(index);
			tasks.add(index, deadlineTask);
			save();
			return String.format(Global.MESSAGE_UPDATED,"[by "+ Global.dayFormat.format(deadlineTask.getEndDate())+ " "+ Global.timeFormat.format(deadlineTask.getEndDate()) + "]"+ " \"" + deadlineTask.getName() + "\"");
		} else {
			saveToTaskHistory();
			DeadlineTask deadlineTask = (DeadlineTask) tasks.get(index);
			if (name != null) {
				deadlineTask.setName(name);
			}
			if (endDate != null) {
				deadlineTask.setEndDate(endDate);
			}
			deadlineTask.setEdited(true);
			save();
			return String.format(Global.MESSAGE_UPDATED,"[by "+ Global.dayFormat.format(deadlineTask.getEndDate())+ " "+ Global.timeFormat.format(deadlineTask.getEndDate()) + "]"+ " \"" + deadlineTask.getName() + "\"");
			}
	}
	
	/**
	 * This operation updates a task with a DeadlineTask object as a parameter
	 * It is usually called by the SyncHandler
	 * 
	 * @param 		index
	 * @param 		task
	 * @return		Feedback for user
	 */
	public String updateToDeadlineTask(int index, DeadlineTask task) {
		if  (tasks.get(index).getType() != Task.TaskType.DEADLINE) {
			DeadlineTask deadlineTask = new DeadlineTask(task.getName(),task.getEndDate());
			deadlineTask.setEdited(tasks.get(index).isEdited());
			deadlineTask.setDone(tasks.get(index).isDone());
			deadlineTask.setUpdated(task.getUpdated());
			saveToTaskHistory();
			deleteTask(index);
			tasks.add(index, deadlineTask);
			save();
			return String.format(Global.MESSAGE_UPDATED,"[by "+ Global.dayFormat.format(deadlineTask.getEndDate())+ " "+ Global.timeFormat.format(deadlineTask.getEndDate()) + "]"+ " \"" + deadlineTask.getName() + "\"");
		} else {
			saveToTaskHistory();
			DeadlineTask deadlineTask = (DeadlineTask) tasks.get(index);
			if (task.getName() != null) {
				deadlineTask.setName(task.getName());
			}
			if (task.getEndDate() != null) {
				deadlineTask.setEndDate(task.getEndDate());
			}
			deadlineTask.setEdited(true);
			deadlineTask.setUpdated(task.getUpdated());
			save();
			return String.format(Global.MESSAGE_UPDATED,"[by "+ Global.dayFormat.format(deadlineTask.getEndDate())+ " "+ Global.timeFormat.format(deadlineTask.getEndDate()) + "]"+ " \"" + deadlineTask.getName() + "\"");
			}
	}
	
	/**
	 * This operation updates a FloatingTask with the given index and replaces the old taskName, 
	 * startDate or endDate respectively and changes the taskType if needed.
	 * If a given date or name parameter equals null, the old value remains.
	 * 
	 * @param index        index of the task to delete, as a string
	 * @param taskName     description of task    
	 * @return             feedback for UI
	 */
	public String updateToFloatingTask(int index, String name) {
		if (tasks.isEmpty()) {
			return String.format(Global.MESSAGE_EMPTY);
		} 

		if (index > tasks.size() - Global.INDEX_OFFSET || index < 0 ) {
			return String.format(Global.ERROR_MESSAGE_NO_INDEX, index);
		}
		
		if  (tasks.get(index).getType() != Task.TaskType.FLOATING) {
			FloatingTask floatingTask = new FloatingTask(name);
			floatingTask.setEdited(tasks.get(index).isEdited());
			floatingTask.setDone(tasks.get(index).isDone());
			saveToTaskHistory();
			deleteTask(index);
			tasks.add(index, floatingTask);
			save();
			return String.format(Global.MESSAGE_UPDATED,"\"" + floatingTask.getName() + "\"");
		} else {
			saveToTaskHistory();
			FloatingTask floatingTask = (FloatingTask) tasks.get(index);
			if (name != null) {
				floatingTask.setName(name);
			}
			floatingTask.setEdited(true);
			save();
			return String.format(Global.MESSAGE_UPDATED,"\"" + floatingTask.getName() + "\"");
			}
	}
	
	/**
	 * This operation updates a task with a FloatingTask object as a parameter
	 * It is usually called by the SyncHandler
	 * 
	 * @param 		index
	 * @param 		task
	 * @return		Feedback for user
	 */
	public String updateToFloatingTask(int index, FloatingTask task) {
		if  (tasks.get(index).getType() != Task.TaskType.FLOATING) {
			FloatingTask floatingTask = new FloatingTask(task.getName());
			floatingTask.setEdited(tasks.get(index).isEdited());
			floatingTask.setDone(tasks.get(index).isDone());
			floatingTask.setUpdated(task.getUpdated());
			saveToTaskHistory();
			deleteTask(index);
			tasks.add(index, floatingTask);
			save();
			return String.format(Global.MESSAGE_UPDATED,"\"" + floatingTask.getName() + "\"");
		} else {
			saveToTaskHistory();
			FloatingTask floatingTask = (FloatingTask) tasks.get(index);
			if (task.getName() != null) {
				floatingTask.setName(task.getName());
			}
			floatingTask.setEdited(true);
			floatingTask.setUpdated(task.getUpdated());
			save();
			return String.format(Global.MESSAGE_UPDATED,"\"" + floatingTask.getName() + "\"");
		}
	}

	/**
	 * This operation marks a task as done.
	 * 
	 * @param index        index of the done task 
	 * @return             feedback for UI
	 */
	public String done(int index) {
		if (tasks.isEmpty()) {
			return String.format(Global.MESSAGE_EMPTY);
		} 

		if (index > tasks.size() - Global.INDEX_OFFSET || index < 0 ) {
			return String.format(Global.ERROR_MESSAGE_NO_INDEX, index);
		}
		
		Task doneTask = tasks.get(index);
		if (doneTask.isDone()) {
			return String.format(Global.MESSAGE_ALREADY_DONE);
		} else {
			saveToTaskHistory();
			doneTask.markDone();
			save();
			switch ( doneTask.getType()) {
			case FLOATING:
				FloatingTask floatingTask = (FloatingTask) doneTask;
				return String.format(Global.MESSAGE_DONE,"\"" + floatingTask.getName() + "\"");
			case DEADLINE:
				DeadlineTask deadlineTask = (DeadlineTask) doneTask;
				return String.format(Global.MESSAGE_DONE,"[by "+ Global.dayFormat.format(deadlineTask.getEndDate())+ " "+ Global.timeFormat.format(deadlineTask.getEndDate()) + "]"+ " \"" + deadlineTask.getName() + "\"");
			default:
				TimedTask timedTask = (TimedTask) doneTask;// TODO: find better solution than default
				return String.format(Global.MESSAGE_DONE,"["+ Global.dayFormat.format(timedTask.getStartDate())+ " "+ Global.timeFormat.format(timedTask.getStartDate())+ "-"+ Global.timeFormat.format(timedTask.getEndDate()) + "]"+ " \"" + timedTask.getName() + "\"");
			}
		}
	}
	
	/**
	 * This operation marks a task as undone.
	 * 
	 * @param index        index of the undone tasks   
	 * @return             feedback for UI
	 */
	public String open(int index) {
		if (tasks.isEmpty()) {
			return String.format(Global.MESSAGE_EMPTY);
		} 

		if (index > tasks.size() - Global.INDEX_OFFSET || index < 0 ) {
			return String.format(Global.ERROR_MESSAGE_NO_INDEX, index);
		}
		
		Task openTask = tasks.get(index);
		if (!openTask.isDone()) {
			return String.format(Global.MESSAGE_ALREADY_OPEN);
		} else {
			saveToTaskHistory();
			openTask.markOpen();
			save();
			switch ( openTask.getType()) {
			case FLOATING:
				FloatingTask floatingTask = (FloatingTask) openTask;
				return String.format(Global.MESSAGE_OPEN,"\"" + floatingTask.getName() + "\"");
			case DEADLINE:
				DeadlineTask deadlineTask = (DeadlineTask) openTask;
				return String.format(Global.MESSAGE_OPEN,"[by "+ Global.dayFormat.format(deadlineTask.getEndDate())+ " "+ Global.timeFormat.format(deadlineTask.getEndDate()) + "]"+ " \"" + deadlineTask.getName() + "\"");
			default:
				TimedTask timedTask = (TimedTask) openTask;// TODO: find better solution than default
				return String.format(Global.MESSAGE_OPEN,"["+ Global.dayFormat.format(timedTask.getStartDate())+ " "+ Global.timeFormat.format(timedTask.getStartDate())+ "-"+ Global.timeFormat.format(timedTask.getEndDate()) + "]"+ " \"" + timedTask.getName() + "\"");
			}
		}
	}
	
	/**
	 * This operation deletes the task with the given index (as shown with 'display' command).
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
			return String.format(Global.ERROR_MESSAGE_NO_INDEX, index);
		} else {
			Task deletedTask = tasks.get(index);
			saveToTaskHistory();
			deletedTasks.add(deletedTask);
			tasks.remove(index);
			save();
			switch ( deletedTask.getType()) {
			case FLOATING:
				FloatingTask floatingTask = (FloatingTask) deletedTask;
				return String.format(Global.MESSAGE_DELETED,"\"" + floatingTask.getName() + "\"");
			case DEADLINE:
				DeadlineTask deadlineTask = (DeadlineTask) deletedTask;
				return String.format(Global.MESSAGE_DELETED,"[by "+ Global.dayFormat.format(deadlineTask.getEndDate())+ " "+ Global.timeFormat.format(deadlineTask.getEndDate()) + "]"+ " \"" + deadlineTask.getName() + "\"");
			default:
				TimedTask timedTask = (TimedTask) deletedTask;// TODO: find better solution than default
				return String.format(Global.MESSAGE_DELETED,"["+ Global.dayFormat.format(timedTask.getStartDate())+ " "+ Global.timeFormat.format(timedTask.getStartDate())+ "-"+ Global.timeFormat.format(timedTask.getEndDate()) + "]"+ " \"" + timedTask.getName() + "\"");
			}
		}
	}
	
	/**@author A0109194A
	 * This operation deletes the task directly from the tasks list without the index.
	 * Used to delete tasks when syncing.
	 * 
	 * @param 	task
	 * @return 	boolean value on whether the delete was successful.
	 */
	public boolean deleteTask(Task task) {
		if (tasks.isEmpty()) {
			return false;
		} else {
			deletedTasks.add(task);
			tasks.remove(task);
			saveToTaskHistory();
			return true;
		}
	}
	
	/**
	 * This operation saves a backup to history tasks ArrayList.
	 */
	public void saveToTaskHistory() {
		tasksHistory.clear();
		for(Task task: tasks) {
			switch ( task.getType()) {
			case FLOATING:
				tasksHistory.add( new FloatingTask((FloatingTask) task));
				break;
			case DEADLINE:
				tasksHistory.add( new DeadlineTask((DeadlineTask) task));
				break;
			default:
				tasksHistory.add(new TimedTask((TimedTask) task));
			}
		}
	}	
	
	public void saveToOperationHistory(Global.CommandType type) {
		operationHistory.push(type);
	}
	
	/**
	 * This operation restores from history tasks ArrayList.
	 */
	public void restoresFromHistory() {
		tasks.clear();
		for(Task task: tasksHistory) {
			switch ( task.getType()) {
			case FLOATING:
				tasks.add( new FloatingTask((FloatingTask) task));
				break;
			case DEADLINE:
				tasks.add( new DeadlineTask((DeadlineTask) task));
				break;
			default:
				tasks.add(new TimedTask((TimedTask) task));
			}
		}
	}	
	
	/**
	 * This operation clears all tasks from memory.
	 * 
	 * @param userCommand 
	 * @return             Feedback for user.
	 */
	public String clearTasks() {
		deletedTasks.addAll(tasks);
		tasks.clear();
		save();
		return String.format(Global.MESSAGE_CLEARED);
	}
	
	/**
	 * Returns index of the given task object within the tasks ArrayList.
	 * @return  index
	 */
	public int getIndexOf(Task task) {
		return tasks.indexOf(task);
	}

	
	public ArrayList<Task> getDeletedTasks() {
		return deletedTasks;
	}
	
	public ArrayList<String> getAllIds() {
		ArrayList<String> idList = new ArrayList<String>();
		for (Task t : tasks) {
			idList.add(t.getId());
		}
		return idList;
	}
	
	/**
	 * Returns the tasks.
	 */
	public ArrayList<Task> getAllTasks() {
		return tasks;
	}
	
}
