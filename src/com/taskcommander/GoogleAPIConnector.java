package com.taskcommander;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.api.client.util.DateTime;
import com.google.api.client.util.Lists;
import com.google.api.client.util.store.DataStore;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.taskcommander.LoginManager;

/**
 * This class is used to connect to the Google API and
 * invoke the Calendar and Tasks services.
 * 
 * Requires permissions and login token from user.
 * This class can create, read, update or delete tasks
 * and calendar events for the given Google account.
 */
public class GoogleAPIConnector {
	private static final String MESSAGE_ERROR_GETTING_SERVICES = "Error getting services.";
	private static final String MESSAGE_ERROR_OPERATION = "Error performing %1$s operation.";
	private static final String MESSAGE_SERVICES_NULL = "Services null, getting services.";
	private static final String MESSAGE_NULL_TASK = "Null task given.";
	private static final String MESSAGE_NO_ID = "Task has not been synced to Google API.";
	
	private static final String OPERATION_GET = "get";
	private static final String OPERATION_ADD = "add";
	private static final String OPERATION_UPDATE = "update";
	private static final String OPERATION_DELETE = "delete";
	
	private static final String PRIMARY_CALENDAR_ID = "primary";
	private static final String PRIMARY_TASKS_ID = "@default";
	
	private static GoogleAPIConnector instance;
	private static LoginManager loginManager;

	//Global instances
	private static Calendar calendar;
	static final java.util.List<Calendar> addedCalendarsUsingBatch = Lists.newArrayList();
	private static Tasks tasks;
	private static DataStore<String> eventDataStore;
	private static DataStore<String> taskDataStore;
	private static final Logger logger = Logger.getLogger(GoogleAPIConnector.class.getName());

	/**
	 * This method returns a GoogleAPIConnector
	 * It is to be called by SyncHandler.
	 * @return GoogleAPIConnector object
	 */
	public static GoogleAPIConnector getInstance() {
		if (instance == null) {
			instance = new GoogleAPIConnector();
		}
		return instance;
	}

	/**
	 * Creates a new GoogleAPIHandler instance.
	 * Also creates a new LoginManager and attempts
	 * to get the Tasks and Calendar services.
	 * @throws IOException 
	 */
	private GoogleAPIConnector() {
		loginManager = LoginManager.getInstance();
		try {
			eventDataStore = LoginManager.getDataStoreFactory().getDataStore("EventStore");
			taskDataStore = LoginManager.getDataStoreFactory().getDataStore("TaskStore");
		} catch (IOException e) {
			logger.log(Level.SEVERE, MESSAGE_ERROR_GETTING_SERVICES, e);
		}
	}

	public boolean getServices() {
		tasks = loginManager.getTasksService();
		calendar = loginManager.getCalendarService();
		
		if (tasks != null && calendar != null) {
			return true;
		} else {
			return false;
		}
	}

	public DataStore<String> getTaskDataStore() {
		return taskDataStore;
	}

	public DataStore<String> getEventDataStore() {
		return eventDataStore;
	}

	//@author A0112828H
	/**
	 * Returns all tasks.
	 * @return Feedback for user.
	 */
	public ArrayList<com.taskcommander.Task> getAllTasks() {
		if (tasks == null || calendar == null) {
			logger.log(Level.INFO, MESSAGE_SERVICES_NULL);
			getServices();
			return null;
		} else {
			ArrayList<com.taskcommander.Task> result = getAllFloatingTasks();
			if (result != null) {
				result.addAll(getAllEvents());
			}
			return result;
		}
	}

	/**
	 * @author A0109194A
	 * Returns all Google Tasks
	 * @return List of Google Tasks
	 */
	public List<Task> getAllGoogleTasks() {
		try {
			Tasks.TasksOperations.List request = tasks.tasks().list(PRIMARY_TASKS_ID);
			List<Task> tasks = request.execute().getItems();
			return tasks;
		} catch (IOException e) {
			logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_GET), e);
			return null;
		}
	}

	//@author A0112828H
	/**
	 * Gets all tasks from Tasks API.
	 * @return   Arraylist of TaskCommander Tasks.
	 */
	private ArrayList<com.taskcommander.Task> getAllFloatingTasks() {
		try {
			Tasks.TasksOperations.List request = tasks.tasks().list(PRIMARY_TASKS_ID);
			List<Task> tasks = request.execute().getItems();

			ArrayList<com.taskcommander.Task> taskList = new ArrayList<com.taskcommander.Task>();
			for (Task task : tasks) {
				taskList.add(toTask(task));
			}
			return taskList;
		} catch (IOException e) {
			logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_GET), e);
			return null;
		}
	}

	/**
	 * @author A0109194A
	 * Returns a List object which holds a request that would be sent to Google
	 * @return	List object
	 */
	public Tasks.TasksOperations.List getListTasksRequest() {
		try {
			Tasks.TasksOperations.List request = tasks.tasks().list(PRIMARY_TASKS_ID);
			return request;
		} catch (IOException e) {
			logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_GET), e);
			return null;
		}
	}

	/**
	 * @author A0109194A
	 * Gets all events from Calendar API starting from
	 * current system time.
	 * @return   Arraylist of TaskCommander Tasks.
	 */
	private ArrayList<com.taskcommander.Task> getAllEvents() {
		try {
			// Gets events from current time onwards
			List<Event> events = calendar.events().list(PRIMARY_CALENDAR_ID)
					.setTimeMin(new DateTime(System.currentTimeMillis())) 
					.execute().getItems();

			ArrayList<com.taskcommander.Task> taskList = new ArrayList<com.taskcommander.Task>();
			for (Event event : events) {
				taskList.add(toTask(event));
			}
			return taskList;
		} catch (IOException e) {
			logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_GET), e);
			return null;
		}
	}

	/**
	 * @author A0109194A
	 * Returns all Events
	 * @return List of Events
	 */
	public List<Event> getAllGoogleEvents() {
		try {
			List<Event> events = calendar.events().list(PRIMARY_CALENDAR_ID)
					.setTimeMin(new DateTime(System.currentTimeMillis()))
					.execute().getItems();
			return events;
		} catch (IOException e) {
			logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_GET), e);
			return null;
		}
	}

	/**
	 * @author A0109194A
	 * Returns a request for listing all the events from Google Calendar
	 * @return A Google Calendar request
	 */
	public Calendar.Events.List getListEventRequest() {
		try {
			Calendar.Events.List request = calendar.events().list(PRIMARY_CALENDAR_ID);
			return request;
		} catch (IOException e) {
			logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_GET), e);
			return null;
		}
	}

	//@author A0112828H
	/**
	 * Adds a task to the Tasks API, given a FloatingTask object.
	 * Returns the Google ID if successful.
	 * 
	 * @param task   Custom FloatingTask object
	 * @return       Google ID of task
	 */
	private String addTask(FloatingTask task) {
		if (task == null) {
			logger.log(Level.WARNING, MESSAGE_NULL_TASK);
		} else {
			Task taskToAdd = new Task();
			taskToAdd.setTitle(task.getName());
			try {
				Tasks.TasksOperations.Insert request = tasks.tasks().insert(PRIMARY_TASKS_ID, taskToAdd);
				Task result = request.execute();
				if (result != null) {
					task.setUpdated(result.getUpdated());
					return result.getId();
				}
			} catch (IOException e) {
				logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_ADD), e);
			}
		}
		return null;
	}

	/**@author A0109194A
	 * Adds a Task to the Task API, given a DeadlineTask object.
	 * Returns the Google ID if successful.
	 * 
	 * @param task   Custom DeadlineTask object
	 * @return       Google ID of task
	 */
	private String addTask(DeadlineTask task) {
		if (task == null) {
			logger.log(Level.WARNING, MESSAGE_NULL_TASK);
		} else {
			Task taskToAdd = new Task();
			taskToAdd.setTitle(task.getName());
			taskToAdd.setDue(toDateTime(task.getEndDate()));
			try {
				Tasks.TasksOperations.Insert request = tasks.tasks().insert(PRIMARY_TASKS_ID, taskToAdd);
				Task result = request.execute();
				if (result != null) {
					task.setUpdated(result.getUpdated());
					return result.getId();
				}
			} catch (IOException e) {
				logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_ADD), e);
			}
		}
		return null;
	}

	//@author A0109194A
	/**
	 * Adds an Event to the Calendar API, given a TimedTask object.
	 * Returns the Google ID if successful.
	 * 
	 * @param task   Custom TimedTask object
	 * @return       Google ID of task
	 */
	private String addTask(TimedTask task) {
		if (task == null){
			logger.log(Level.WARNING, MESSAGE_NULL_TASK);
		} else {
			Event event = new Event();
			event.setSummary(task.getName());
			event.setStart(new EventDateTime().setDateTime(toDateTime(task.getStartDate())));			
			event.setEnd(new EventDateTime().setDateTime(toDateTime(task.getEndDate())));		

			try {
				Event createdEvent = calendar.events().insert(PRIMARY_CALENDAR_ID, event).execute();
				if (createdEvent != null) {
					task.setUpdated(createdEvent.getUpdated());
					return createdEvent.getId();
				}
			} catch (IOException e) {
				logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_ADD), e);
			}
		}
		return null;
	}

	//@author A0112828H
	/**
	 * Gets a task from the Tasks API, given a FloatingTask object.
	 * The given task must have a Google ID.
	 * Returns the TaskCommander Task if successful. 
	 * 
	 * @param task   Custom FloatingTask object
	 * @return	     TaskCommander Task object
	 */
	private com.taskcommander.Task getTask(FloatingTask task) {
		if (task == null) {
			logger.log(Level.WARNING, MESSAGE_NULL_TASK);
		} else if (task.getId() == null) {
			logger.log(Level.WARNING, MESSAGE_NO_ID);
		} else {
			try {
				Task check = tasks.tasks().get(PRIMARY_TASKS_ID, task.getId()).execute();
				return toTask(check);
			} catch (IOException e) {
				logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_GET), e);
			}
		}
		return null;
	}

	/**
	 * Gets a task from the Tasks API, given a DeadlineTask object.
	 * The given task must have a Google ID.
	 * Returns the TaskCommander Task if successful. 
	 * 
	 * @param task   Custom DeadlineTask object
	 * @return	     TaskCommander Task object
	 */
	private com.taskcommander.Task getTask(DeadlineTask task) {
		if (task == null) {
			logger.log(Level.WARNING, MESSAGE_NULL_TASK);
		} else if (task.getId() == null) {
			logger.log(Level.WARNING, "Task has no ID");
		} else {
			try {
				Task check = tasks.tasks().get(PRIMARY_TASKS_ID, task.getId()).execute();
				return toTask(check);
			} catch (IOException e) {
				logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_GET), e);
			}
		}
		return null;
	}

	/**
	 * Gets an event from the Calendar API, given a TimedTask object.
	 * The given task must have a Google ID.
	 * Returns the name of the task if successful. 
	 * 
	 * @param task   Custom TimedTask object
	 * @return	     TaskCommander Task object
	 */
	private com.taskcommander.Task getTask(TimedTask task) {
		if (task == null) {
			logger.log(Level.WARNING, MESSAGE_NULL_TASK);
		} else if (task.getId() == null) {
			logger.log(Level.WARNING, MESSAGE_NO_ID);
		} else {
			try {
				Event check = calendar.events().get(PRIMARY_CALENDAR_ID, task.getId()).execute();
				return toTask(check);
			} catch (IOException e) {
				logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_GET), e);
			}
		}
		return null;
	}

	//@author A0112828H
	/**
	 * Deletes a task from the Tasks API, given a FloatingTask object.
	 * The given task must have a Google ID.
	 * Returns true if successful. 
	 * 
	 * @param task   Custom FloatingTask object
	 * @return	     Success of action
	 */
	private boolean deleteTask(FloatingTask task) {
		if (task == null) {
			logger.log(Level.WARNING, MESSAGE_NULL_TASK);
		} else if (task.getId() == null) {
			logger.log(Level.WARNING, MESSAGE_NO_ID);
		} else {
			try {
				Tasks.TasksOperations.Delete request = tasks.tasks().delete(PRIMARY_TASKS_ID, task.getId());
				request.execute();
				Task check = tasks.tasks().get(PRIMARY_TASKS_ID, task.getId()).execute();
				return check == null;
			} catch (IOException e) {
				logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_DELETE), e);
			} catch (NullPointerException e) {
				logger.log(Level.SEVERE, MESSAGE_NULL_TASK, e);
				return true;
			}
		}
		return false;
	}

	/**
	 * Deletes an event from the Tasks API, given a DeadlineTask object.
	 * The given task must have a Google ID.
	 * Returns true if successful. 
	 * 
	 * @param task   Custom DeadlineTask object
	 * @return	     Success of action
	 */
	private boolean deleteTask(DeadlineTask task) {
		if (task == null) {
			System.out.println(Global.MESSAGE_ARGUMENTS_NULL);
			logger.log(Level.WARNING, MESSAGE_NULL_TASK);
		} else if (task.getId() == null) {
			logger.log(Level.WARNING, MESSAGE_NO_ID);
		} else {
			try {
				Tasks.TasksOperations.Delete request = tasks.tasks().delete(PRIMARY_TASKS_ID, task.getId());
				request.execute();
				Task check = tasks.tasks().get(PRIMARY_TASKS_ID, task.getId()).execute();
				return check == null;
			} catch (IOException e) {
				logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_DELETE), e);
			} catch (NullPointerException e) {
				logger.log(Level.SEVERE, MESSAGE_NULL_TASK, e);
				return true;
			}
		}
		return false;
	}

	/**@author A0109194A
	 * Deletes an event from the Calendar API, given a TimedTask object.
	 * The given task must have a Google ID.
	 * Returns true if successful. 
	 * 
	 * @param task   Custom TimedTask object
	 * @return	     Success of action
	 */
	private boolean deleteTask(TimedTask task) {
		if (task == null) {
			System.out.println(Global.MESSAGE_ARGUMENTS_NULL);
			logger.log(Level.WARNING, MESSAGE_NULL_TASK);
		} else if (task.getId() == null) {
			logger.log(Level.WARNING, MESSAGE_NO_ID);
		} else {
			try {
				calendar.events().delete(PRIMARY_CALENDAR_ID, "eventId").execute();
				Event check = calendar.events().get(PRIMARY_TASKS_ID, task.getId()).execute();
				return check == null;
			} catch (IOException e) {
				logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_DELETE), e);
			}
		}
		return false;
	}

	//@author A0112828H
	/**
	 * Updates a task from the Tasks API, given a FloatingTask object.
	 * The given task must have a Google ID.
	 * Returns true if successful.  
	 * 
	 * @param task   Custom FloatingTask object
	 * @return	     Success of action
	 */
	private boolean updateTask(FloatingTask task) {
		if (task == null) {
			logger.log(Level.WARNING, MESSAGE_NULL_TASK);
		} else if (task.getId() == null) {
			logger.log(Level.WARNING, MESSAGE_NO_ID);
		} else {
			try {
				Task result = tasks.tasks().update(PRIMARY_TASKS_ID, task.getId(), toGoogleTask(task)).execute();
				return result != null;
			} catch (IOException e) {
				logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_UPDATE), e);
			}
		}
		return false;
	}

	//@author A0112828H
	/**
	 * Updates a task from the Tasks API, given a DeadlineTask object.
	 * The given task must have a Google ID.
	 * Returns true if successful.  
	 * 
	 * @param task   Custom DeadlineTask object
	 * @return	     Success of action
	 */
	private boolean updateTask(DeadlineTask task) {
		if (task == null) {
			System.out.println(Global.MESSAGE_ARGUMENTS_NULL);
			logger.log(Level.WARNING, MESSAGE_NULL_TASK);
		} else if (task.getId() == null) {
			logger.log(Level.WARNING, MESSAGE_NO_ID);
		} else {
			try {
				Task result = tasks.tasks().update(PRIMARY_TASKS_ID, task.getId(), toGoogleTask(task)).execute();
				task.setUpdated(result.getUpdated());
				return result != null;
			} catch (IOException e) {
				logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_UPDATE), e);
			}
		}
		return false;
	}

	/**@author A0109194A
	 * Updates an event from the Calendar API, given a TimedTask object.
	 * The given task must have a Google ID.
	 * Returns true if successful.  
	 * 
	 * @param task   Custom TimedTask object
	 * @return	     Success of action
	 */
	private boolean updateTask(TimedTask task) {
		if (task == null) {
			logger.log(Level.WARNING, MESSAGE_NULL_TASK);
		} else if (task.getId() == null) {
			logger.log(Level.WARNING, MESSAGE_NO_ID);
		} else {
			try {
				Event result = calendar.events().update(PRIMARY_CALENDAR_ID, task.getId(), toGoogleTask(task)).execute();
				return result != null;
			} catch (IOException e) {
				logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_UPDATE), e);
			}
		}
		return false;
	}

	//@author A0112828H
	// Changes a Date to a DateTime object.
	private DateTime toDateTime(Date date) {
		return new DateTime(date);
	}

	// Changes a DateTime to a Date object.
	private Date toDate(DateTime dateTime) {
		return new Date(dateTime.getValue());
	}

	//@author A0109194A
	// Changes a Google Task to a TaskCommander Task.
	public com.taskcommander.Task toTask(Task task) {
		if (task == null) {
			return null;
		}
		if (task.containsKey("due")) {
			DeadlineTask deadlineTask = new DeadlineTask(task.getTitle(), toDate(task.getDue()));
			deadlineTask.setId(task.getId());
			deadlineTask.setUpdated(task.getUpdated());
			return deadlineTask;
		} else {
			FloatingTask floatingTask = new FloatingTask(task.getTitle(), task.getId());
			floatingTask.setUpdated(task.getUpdated());
			return floatingTask;
		}
	}

	//@author A0109194A
	// Changes a Google Calendar Event to a TaskCommander Task.
	public com.taskcommander.Task toTask(Event event) {
		if (event == null) {
			return null;
		}
		TimedTask timedTask = new TimedTask(event.getSummary(),
				toDate(event.getStart().getDateTime()),
				toDate(event.getEnd().getDateTime()));
		timedTask.setId(event.getId());
		timedTask.setUpdated(event.getUpdated());
		return timedTask;
	}

	//@author A0112828H
	//The following operations turn a task into a Google Task or Event
	private Task toGoogleTask(FloatingTask task) {
		Task newTask = new Task();
		newTask.setId(task.getId());
		newTask.setTitle(task.getName());
		setStatusFromTask(newTask, task);
		return newTask;
	}

	private Task toGoogleTask(DeadlineTask task) {
		Task newTask = new Task();
		newTask.setId(task.getId());
		newTask.setTitle(task.getName());
		newTask.setDue(toDateTime(task.getEndDate()));
		setStatusFromTask(newTask, task);
		return newTask;
	}

	private Event toGoogleTask(TimedTask task) {
		Event newEvent = new Event();
		newEvent.setId(task.getId());
		newEvent.setSummary(task.getName());
		newEvent.setStart(new EventDateTime().setDateTime(toDateTime(task.getStartDate())));			
		newEvent.setEnd(new EventDateTime().setDateTime(toDateTime(task.getEndDate())));		
		return newEvent;
	}

	private void setStatusFromTask(Task newTask, com.taskcommander.Task task) {
		if (task.isDone()) {
			newTask.setStatus("completed");
		} else {
			newTask.setStatus("needsAction");
		}
	}

	public String addTask(com.taskcommander.Task task) {
		switch (task.getType()) {
		case FLOATING:
			return addTask((FloatingTask) task);
		case TIMED:
			return addTask((TimedTask) task);
		case DEADLINE:
			return addTask((DeadlineTask) task);
		}
		return null;
	}

	public com.taskcommander.Task getTask(com.taskcommander.Task task) {
		switch (task.getType()) {
		case FLOATING:
			return getTask((FloatingTask) task);
		case TIMED:
			return getTask((TimedTask) task);
		case DEADLINE:
			return getTask((DeadlineTask) task);
		}
		return null;
	}

	public boolean updateTask(com.taskcommander.Task task) {
		switch (task.getType()) {
		case FLOATING:
			return updateTask((FloatingTask) task);
		case TIMED:
			return updateTask((TimedTask) task);
		case DEADLINE:
			return updateTask((DeadlineTask) task);
		}
		return false;
	}

	public boolean deleteTask(com.taskcommander.Task task) {
		switch (task.getType()) {
		case FLOATING:
			return deleteTask((FloatingTask) task);
		case TIMED:
			return deleteTask((TimedTask) task);
		case DEADLINE:
			return deleteTask((DeadlineTask) task);
		}
		return false;
	}

	public ArrayList<String> getAllIds() {
		ArrayList<com.taskcommander.Task> tasks = getAllTasks();
		ArrayList<String> idList = new ArrayList<String>();
		for (com.taskcommander.Task t : tasks) {
			idList.add(t.getId());
		}
		return idList;
	}

}
