package com.taskcommander;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;


/**
 * This class represents the Data component. Besides storing the data temporary, it also contains
 * all of the methods needed to manipulate the task objects within the ArrayList. At the beginning 
 * the content of the permanent storage is pulled to the temporary one. After each command the data 
 * will be pushed to the permanent storage.
 * 
 * @author A0128620M
 */

public class Data {

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
	 * Constructor
	 */
	public Data() {
		tasks = new ArrayList<Task>();
		tasksHistory = new ArrayList<Task>();
		deletedTasks = new ArrayList<Task>();
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
	 * 
	 * @param 	task     
	 * @return 	feedback for UI
	 */
	public Feedback addTask(Task task) {
		switch ( task.getType()) {
		case FLOATING:
			FloatingTask floatingTask = (FloatingTask) task;
			return addFloatingTask(floatingTask.getName());
		case DEADLINE:
			DeadlineTask deadlineTask = (DeadlineTask) task;
			return addDeadlineTask(deadlineTask.getName(), deadlineTask.getEndDate());
		default:
			TimedTask timedTask = (TimedTask) task;
			return addTimedTask(timedTask.getName(), timedTask.getStartDate(), timedTask.getEndDate());
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
	public Feedback addTimedTask(String taskName, Date startDate, Date endDate) {
		TimedTask timedTask = new TimedTask(taskName,startDate,endDate);
		saveToHistory();
		tasks.add(timedTask);
		save();
		return new Feedback(true, Global.CommandType.ADD, new TimedTask(timedTask));
	}
	
	/**
	 * This operation adds a DeadlineTask to the tasks ArrayList.
	 * 
	 * @param 	taskName     
	 * @param 	endDate       
	 * @return 	feedback for UI
	 */
	public Feedback addDeadlineTask(String taskName, Date endDate) {
		DeadlineTask deadlineTask= new DeadlineTask(taskName,endDate);
		saveToHistory();
		tasks.add(deadlineTask);
		save();
		return new Feedback(true, Global.CommandType.ADD, new DeadlineTask(deadlineTask));
	}
	
	/**
	 * This operation adds a FloatingTask to the tasks ArrayList.
	 * 
	 * @param 	taskName        
	 * @return 	feedback for UI
	 */
	public Feedback addFloatingTask(String taskName) {
		FloatingTask floatingTask = new FloatingTask(taskName);
		saveToHistory();
		tasks.add(floatingTask);
		save();
		return new Feedback(true, Global.CommandType.ADD, new FloatingTask(floatingTask));
	}

	/**
	 * This operation displays all tasks by forwarding all the needed information to the UI.
	 *  
	 * @return 	feedback for UI
	 */
	public Feedback displayTasks() {
		ArrayList<FloatingTask> displayedFloatingTasks = new ArrayList<FloatingTask>();
		ArrayList<DatedTask> displayedDatedTasks = new ArrayList<DatedTask>();
		ArrayList<Task> displayedTasks = new ArrayList<Task>();
		
		for(Task task: tasks) {
			if(task.getType() == Task.TaskType.FLOATING) {
				 displayedFloatingTasks.add(new FloatingTask((FloatingTask) task));	// TODO: use cloned task with "new FloatingTask((FloatingTask)" to  add a copy of the respective task, not the original
			} else if (task.getType() == Task.TaskType.DEADLINE) {
				displayedDatedTasks.add(new DeadlineTask((DeadlineTask) task));
			} else if (task.getType() == Task.TaskType.TIMED) {
				displayedDatedTasks.add(new TimedTask((TimedTask) task));
			}
		}

		Collections.sort(displayedFloatingTasks);
		displayedTasks.addAll(displayedFloatingTasks);
		Collections.sort(displayedDatedTasks);
		displayedTasks.addAll(displayedDatedTasks);
		
		return new Feedback(true, Global.CommandType.DISPLAY, displayedTasks);
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
	 * @return 	feedback for UI
	 */
	public Feedback displayTasks(boolean isDateTimeRestricted, Date startDate, Date endDate, boolean isTaskTypeRestricted, boolean shownFloatingTask, boolean shownDeadlineTask, boolean shownTimedTask, boolean isStatusRestricted, boolean status) {
		ArrayList<FloatingTask> displayedFloatingTasks = new ArrayList<FloatingTask>();
		ArrayList<DatedTask> displayedDatedTasks = new ArrayList<DatedTask>();
		ArrayList<Task> displayedTasks = new ArrayList<Task>();
		
		for(Task task: tasks) {
			// Step 1: Check Status
			if (!isStatusRestricted || (isStatusRestricted && status == task.isDone() )) {
				// Step 2: Check Type
				if(task.getType() == Task.TaskType.FLOATING && (!isTaskTypeRestricted || (isTaskTypeRestricted && shownFloatingTask))) {	
					// Step 3: Check DatePeriod
					if (!isDateTimeRestricted) {
						displayedFloatingTasks.add(new FloatingTask((FloatingTask) task));
					}
				} else if (task.getType() == Task.TaskType.DEADLINE && (!isTaskTypeRestricted || (isTaskTypeRestricted && shownDeadlineTask))) {
					DeadlineTask deadlineTask = (DeadlineTask) task;
					if (!isDateTimeRestricted || (isDateTimeRestricted && (deadlineTask.getEndDate().compareTo(endDate) < 0) || deadlineTask.getEndDate().compareTo(endDate) == 0) ) { //TODO: Refactor Date Comparison methods
						displayedDatedTasks.add(new DeadlineTask((DeadlineTask) task));
					}
				} else if (task.getType() == Task.TaskType.TIMED && (!isTaskTypeRestricted || (isTaskTypeRestricted && shownTimedTask))) {
					TimedTask timedTask = (TimedTask) task;
					if (!isDateTimeRestricted || (isDateTimeRestricted && (timedTask.getStartDate().compareTo(startDate) > 0 || timedTask.getStartDate().compareTo(startDate) == 0) && (timedTask.getEndDate().compareTo(endDate) < 0) || timedTask.getEndDate().compareTo(endDate) == 0) ){
						displayedDatedTasks.add(new TimedTask((TimedTask) task));
					}
				}		 
			}		 
		}
		
		Collections.sort(displayedFloatingTasks);
		displayedTasks.addAll(displayedFloatingTasks);
		Collections.sort(displayedDatedTasks);
		displayedTasks.addAll(displayedDatedTasks);
		
		for(Task task: displayedTasks) {
			System.out.print(task.getName());
			System.out.println(task.isDone());
		}
		
		return new Feedback(true, Global.CommandType.DISPLAY, displayedTasks);
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
	public Feedback updateToTimedTask(int index, String name, Date startDate, Date endDate) {
		if (tasks.isEmpty()) {
			return new Feedback(false, String.format(Global.MESSAGE_EMPTY));
		} 

		if (index > tasks.size() - Global.INDEX_OFFSET) {
			return new Feedback(false, String.format(Global.MESSAGE_NO_INDEX, index));
		}
		if  (tasks.get(index).getType() != Task.TaskType.TIMED) {
			TimedTask timedTask = new TimedTask(name,startDate,endDate);
			timedTask.setEdited(tasks.get(index).isEdited());
			timedTask.setDone(tasks.get(index).isDone());
			saveToHistory();
			deleteTask(index);
			tasks.add(index, timedTask);
			save();
			return new Feedback(true, Global.CommandType.UPDATE, new TimedTask(timedTask));
		} else {
			saveToHistory();
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
			return new Feedback(true, Global.CommandType.UPDATE, new TimedTask(timedTask));
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
	public Feedback updateToDeadlineTask(int index, String name, Date endDate) {
		if (tasks.isEmpty()) {
			return new Feedback(false, String.format(Global.MESSAGE_EMPTY));
		} 

		if (index > tasks.size() - Global.INDEX_OFFSET) {
			return new Feedback(false, String.format(Global.MESSAGE_NO_INDEX, index));
		}

		if  (tasks.get(index).getType() != Task.TaskType.DEADLINE) {
			DeadlineTask deadlineTask = new DeadlineTask(name,endDate);
			deadlineTask.setEdited(tasks.get(index).isEdited());
			deadlineTask.setDone(tasks.get(index).isDone());
			saveToHistory();
			deleteTask(index);
			tasks.add(index, deadlineTask);
			save();
			return new Feedback(true, Global.CommandType.UPDATE, new DeadlineTask(deadlineTask));
		} else {
			saveToHistory();
			DeadlineTask deadlineTask = (DeadlineTask) tasks.get(index);
			if (name != null) {
				deadlineTask.setName(name);
			}
			if (endDate != null) {
				deadlineTask.setEndDate(endDate);
			}
			deadlineTask.setEdited(true);
			save();
			return new Feedback(true, Global.CommandType.UPDATE, new DeadlineTask(deadlineTask)); 
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
	public Feedback updateToFloatingTask(int index, String name) {
		if (tasks.isEmpty()) {
			return new Feedback(false, String.format(Global.MESSAGE_EMPTY));
		} 

		if (index > tasks.size() - Global.INDEX_OFFSET || index < 0 ) {
			return new Feedback(false, String.format(Global.MESSAGE_NO_INDEX, index));
		}
		
		if  (tasks.get(index).getType() != Task.TaskType.FLOATING) {
			FloatingTask floatingTask = new FloatingTask(name);
			floatingTask.setEdited(tasks.get(index).isEdited());
			floatingTask.setDone(tasks.get(index).isDone());
			saveToHistory();
			deleteTask(index);
			tasks.add(index, floatingTask);
			save();
			return new Feedback(true, Global.CommandType.UPDATE, new FloatingTask(floatingTask));
		} else {
			saveToHistory();
			FloatingTask floatingTask = (FloatingTask) tasks.get(index);
			if (name != null) {
				floatingTask.setName(name);
			}
			floatingTask.setEdited(true);
			save();
			return new Feedback(true, Global.CommandType.UPDATE, new FloatingTask(floatingTask));
		}
	}

	/**
	 * This operation marks a task as done.
	 * 
	 * @param index        index of the done task 
	 * @return             feedback for UI
	 */
	public Feedback done(int index) {
		if (tasks.isEmpty()) {
			return new Feedback(false, String.format(Global.MESSAGE_EMPTY));
		} 

		if (index > tasks.size() - Global.INDEX_OFFSET || index < 0 ) {
			return new Feedback(false, String.format(Global.MESSAGE_NO_INDEX, index));
		}
		
		Task doneTask = tasks.get(index);
		if (doneTask.isDone()) {
			return new Feedback(false, String.format(Global.MESSAGE_ALREADY_DONE));
		} else {
			saveToHistory();
			doneTask.markDone();
			save();
			switch ( doneTask.getType()) {
			case FLOATING:
				return new Feedback(true, Global.CommandType.DONE, (FloatingTask) doneTask);
			case DEADLINE:
				return new Feedback(true, Global.CommandType.DONE, (DeadlineTask) doneTask);
			default:																				// TODO: find better solution than default
				return new Feedback(true, Global.CommandType.DONE,(TimedTask) doneTask);
			}
		}
	}
	
	/**
	 * This operation marks a task as undone.
	 * 
	 * @param index        index of the undone tasks   
	 * @return             feedback for UI
	 */
	public Feedback open(int index) {
		if (tasks.isEmpty()) {
			return new Feedback(false, String.format(Global.MESSAGE_EMPTY));
		} 

		if (index > tasks.size() - Global.INDEX_OFFSET || index < 0 ) {
			return new Feedback(false, String.format(Global.MESSAGE_NO_INDEX, index));
		}
		
		Task openTask = tasks.get(index);
		if (!openTask.isDone()) {
			return new Feedback(false, String.format(Global.MESSAGE_ALREADY_OPEN));
		} else {
			saveToHistory();
			openTask.markOpen();
			save();
			switch ( openTask.getType()) {
			case FLOATING:
				return new Feedback(true, Global.CommandType.OPEN, new FloatingTask((FloatingTask) openTask));
			case DEADLINE:
				return new Feedback(true, Global.CommandType.OPEN, new DeadlineTask((DeadlineTask) openTask));
			default:
				return new Feedback(true, Global.CommandType.OPEN, new TimedTask((TimedTask) openTask));
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
	public Feedback deleteTask(int index) {
		if (tasks.isEmpty()) {
			return new Feedback(false,String.format(Global.MESSAGE_EMPTY));
		} 

		if (index > tasks.size() - Global.INDEX_OFFSET || index < 0 ) {
			return new Feedback(false,String.format(Global.MESSAGE_NO_INDEX, index));
		} else {
			Task deletedTask = tasks.get(index);
			saveToHistory();
			deletedTasks.add(deletedTask);
			tasks.remove(index);
			save();
			switch ( deletedTask.getType()) {
			case FLOATING:
				return new Feedback(true, Global.CommandType.DELETE, new FloatingTask((FloatingTask) deletedTask));
			case DEADLINE:
				return new Feedback(true, Global.CommandType.DELETE, new DeadlineTask((DeadlineTask) deletedTask));
			default:
				return new Feedback(true, Global.CommandType.DELETE, new TimedTask((TimedTask) deletedTask));
			}
		}
	}
	
	/**@author A0109194A
	 * This operation deletes the task directly from the tasks list without the index.
	 * Used to delete tasks when syncing.
	 * 
	 * @param 			task
	 * @return 			boolean value on whether the delete was successful.
	 */
	public boolean deleteTask(Task task) {
		if (tasks.isEmpty()) {
			return false;
		} else {
			deletedTasks.add(task);
			tasks.remove(task);
			return true;
		}
	}
	
	/**
	 * This operation saves a backup to history tasks ArrayList.
	 */
	public void saveToHistory() {
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
	public Feedback clearTasks() {
		deletedTasks.addAll(tasks);
		tasks.clear();
		save();
		return new Feedback(true,Global.CommandType.CLEAR);
	}
	
	/**
	 * Returns index of the given task object within the tasks ArrayList.
	 * @return  index
	 */
	public int getIndexOf(Task task) {
		return tasks.indexOf(task);
	}

	
	public ArrayList<Task> getAllTasks() {
		return tasks;
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
}
