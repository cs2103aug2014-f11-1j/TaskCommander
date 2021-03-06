package com.taskcommander;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Lists;
import com.google.api.client.util.store.DataStore;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.taskcommander.LoginManager;

//@author A0112828H
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
	private static final String MESSAGE_NOT_FOUND = "Task was not found.";

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
		if (isLoggedIn()) {
			return true;
		} else {
			tasks = loginManager.getTasksService();
			calendar = loginManager.getCalendarService();
			return false;
		}
	}

	// Checks if the services are not null.
	public boolean isLoggedIn() {
		return (tasks != null && calendar != null);
	}

	public DataStore<String> getTaskDataStore() {
		return taskDataStore;
	}

	public DataStore<String> getEventDataStore() {
		return eventDataStore;
	}

	/**
	 * Returns all tasks.
	 * @return All tasks.
	 */
	public ArrayList<com.taskcommander.Task> getAllTasks() {
		if (!isLoggedIn()) {
			logger.log(Level.INFO, MESSAGE_SERVICES_NULL);
			getServices();
			return null;
		} else {
			ArrayList<com.taskcommander.Task> result = getAllFloatingTasks();
			if (result == null) {
				return getAllEvents();
			}
			result.addAll(getAllEvents());
			return result;
		}
	}

	//@author A0109194A
	/**
	 * Returns all Google Tasks
	 * @return List of Google Tasks
	 */
	public List<Task> getAllGoogleTasks(boolean showDeleted) {
		try {
			if (showDeleted) {
				Tasks.TasksOperations.List request = tasks.tasks().list(PRIMARY_TASKS_ID)
						.setShowDeleted(showDeleted);
				List<Task> tasks = request.execute().getItems();
				if (tasks != null) {
					return tasks;					
				}
			} else {			
				Tasks.TasksOperations.List request = tasks.tasks().list(PRIMARY_TASKS_ID);
				List<Task> tasks = request.execute().getItems();
				if (tasks != null) {
					return tasks;					
				}
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_GET), e);
		}
		return null;
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

			if (tasks != null) {
				ArrayList<com.taskcommander.Task> taskList = new ArrayList<com.taskcommander.Task>();
				for (Task task : tasks) {
					if (task.getTitle() != null && !task.getTitle().equals("")) {
						taskList.add(toTask(task));
					}
				}
				return taskList;
			} else {
				logger.log(Level.SEVERE, "Task list was null");
				return null;
			}

		}  catch (GoogleJsonResponseException e) {
			if (e.getMessage().contains("401 Unauthorized")) {
				// If not logged in, login attempt handled outside of this class
			} else {
				logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_GET), e);
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_GET), e);
		}
		return null;
	}

	//@author A0109194A
	/**
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
	 * Gets all events from Calendar API from the primary calendar,
	 * starting from current system time.
	 * @return   Arraylist of TaskCommander Tasks.
	 */
	private ArrayList<com.taskcommander.Task> getAllEvents() {
		try {
			// Gets events from current time onwards
			List<Event> events = calendar.events().list(PRIMARY_CALENDAR_ID)
					.setTimeMin(new DateTime(System.currentTimeMillis())) 
					.execute().getItems();
			if (events != null) {
				ArrayList<com.taskcommander.Task> taskList = new ArrayList<com.taskcommander.Task>();
				for (Event event : events) {
					com.taskcommander.Task task = toTask(event);
					taskList.add(task);
				}
				return taskList;
			} else {
				return null;
			}
		}  catch (GoogleJsonResponseException e) {
			if (e.getMessage().contains("401 Unauthorized")) {
				// If not logged in, login attempt handled outside of this class
			} else {
				logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_GET), e);
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_GET), e);
		}
		return null;
	}

	//@author A0109194A
	/**
	 * Returns all Events
	 * @return List of Events
	 */
	public List<Event> getAllGoogleEvents(boolean showDeleted) {
		try {
			if (showDeleted) {
				List<Event> events = calendar.events().list(PRIMARY_CALENDAR_ID)
						.setTimeMin(new DateTime(System.currentTimeMillis()))
						.setShowDeleted(showDeleted)
						.execute().getItems();
				if (events != null) {
					return events;
				}
			} else {
				List<Event> events = calendar.events().list(PRIMARY_CALENDAR_ID)
						.setTimeMin(new DateTime(System.currentTimeMillis()))
						.execute().getItems();
				if (events != null) {
					return events;
				}
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_GET), e);
		}
		return null;
	}

	/**
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

	public ArrayList<String> getAllIds() {
		ArrayList<com.taskcommander.Task> tasks = getAllTasks();
		ArrayList<String> idList = new ArrayList<String>();
		for (com.taskcommander.Task t : tasks) {
			idList.add(t.getId());
		}
		return idList;
	}

	//@author A0112828H
	public String addTask(com.taskcommander.Task task) {
		if (task != null) {
			switch (task.getType()) {
			case FLOATING:
				return addTask((FloatingTask) task);
			case TIMED:
				return addTask((TimedTask) task);
			case DEADLINE:
				return addTask((DeadlineTask) task);
			}
		}
		return null;
	}

	public com.taskcommander.Task getTask(com.taskcommander.Task task) {
		if (task != null) {
			switch (task.getType()) {
			case FLOATING:
				return getTask((FloatingTask) task);
			case TIMED:
				return getTask((TimedTask) task);
			case DEADLINE:
				return getTask((DeadlineTask) task);
			}
		}
		return null;
	}

	public boolean updateTask(com.taskcommander.Task task) {
		if (task != null) {
			switch (task.getType()) {
			case FLOATING:
				return updateTask((FloatingTask) task);
			case TIMED:
				return updateTask((TimedTask) task);
			case DEADLINE:
				return updateTask((DeadlineTask) task);
			}
		}
		return false;
	}

	public boolean deleteTask(com.taskcommander.Task task) {
		if (task != null) {
			switch (task.getType()) {
			case FLOATING:
				return deleteTask((FloatingTask) task);
			case TIMED:
				return deleteTask((TimedTask) task);
			case DEADLINE:
				return deleteTask((DeadlineTask) task);
			}
		}
		return false;
	}

	/**
	 * Adds a task to the Tasks API, given a FloatingTask object.
	 * Returns the Google ID if successful.
	 * 
	 * @param task   Custom FloatingTask object
	 * @return       Google ID of task
	 */
	private String addTask(FloatingTask task) {
		assert(task != null);
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
		return null;
	}

	//@author A0109194A
	/**
	 * Adds a Task to the Task API, given a DeadlineTask object.
	 * Returns the Google ID if successful.
	 * 
	 * @param task   Custom DeadlineTask object
	 * @return       Google ID of task
	 */
	private String addTask(DeadlineTask task) {
		assert(task != null);
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
		return null;
	}

	/**
	 * Adds an Event to the Calendar API, given a TimedTask object.
	 * Returns the Google ID if successful.
	 * 
	 * @param task   Custom TimedTask object
	 * @return       Google ID of task
	 */
	private String addTask(TimedTask task) {
		assert(task != null);
		Event event = new Event();
		event.setSummary(task.getName());
		event.setStart(new EventDateTime().setDateTime(toDateTime(task.getStartDate())));			
		event.setEnd(new EventDateTime().setDateTime(toDateTime(task.getEndDate())));		
		return addEventToCalendar(task, event);
	}

	//@author A0112828H
	/**
	 * Adds the given TimedTask as an event to the primary calendar.
	 * @param task
	 * @param event
	 * @return            Google ID of task
	 */
	private String addEventToCalendar(TimedTask task, Event event) {
		try {
			Event createdEvent = calendar.events().insert(PRIMARY_CALENDAR_ID, event).execute();
			if (createdEvent != null) {
				task.setUpdated(createdEvent.getUpdated());
				return createdEvent.getId();
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_ADD), e);
		} catch (NullPointerException e) {
			logger.log(Level.SEVERE, MESSAGE_NULL_TASK, e);
		}
		return null;
	}

	/**
	 * Gets a task from the Tasks API, given a FloatingTask object.
	 * The given task must have a Google ID.
	 * Returns the TaskCommander Task if successful. 
	 * 
	 * @param task   Custom FloatingTask object
	 * @return	     TaskCommander Task object
	 */
	private com.taskcommander.Task getTask(FloatingTask task) {
		assert(task != null);
		if (task.getId() == null) {
			logger.log(Level.WARNING, MESSAGE_NO_ID);
		} else {
			try {
				Task check = tasks.tasks().get(PRIMARY_TASKS_ID, task.getId()).execute();
				if (check != null) {
					if (check.getDeleted() == null || !check.getDeleted()) {
						return toTask(check);
					}
				}
			} catch (IOException e) {
				logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_GET), e);
			} catch (NullPointerException e) {
				logger.log(Level.SEVERE, "", e);
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
		assert(task != null);
		if (task.getId() == null) {
			logger.log(Level.WARNING, MESSAGE_NO_ID);
		} else {
			try {
				Task check = tasks.tasks().get(PRIMARY_TASKS_ID, task.getId()).execute();
				if (check != null) {
					if (check.getDeleted() == null || !check.getDeleted()) {
						return toTask(check);
					}
				}
			} catch (IOException e) {
				logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_GET), e);
			} catch (NullPointerException e) {
				logger.log(Level.SEVERE, "", e);
			}
		}
		return null;
	}

	//@author A0109194A
	/**
	 * Gets an event from the Calendar API, given a TimedTask object.
	 * The given task must have a Google ID.
	 * Returns the name of the task if successful. 
	 * 
	 * @param task   Custom TimedTask object
	 * @return	     TaskCommander Task object
	 */
	private com.taskcommander.Task getTask(TimedTask task) {
		assert(task != null);
		if (task.getId() == null) {
			logger.log(Level.WARNING, MESSAGE_NO_ID);
		} else {
			return getEventFromCalendar(task);

		}
		return null;
	}

	/**
	 * Gets an event from the primary calendar, given a TimedTask.
	 * @param task
	 * @return            TaskCommander task
	 */
	private com.taskcommander.Task getEventFromCalendar(TimedTask task) {
		try {
			Event check = calendar.events().get(PRIMARY_CALENDAR_ID, task.getId()).execute();
			if (check != null && check.getStatus().equals("confirmed")) {
				return toTask(check);
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_GET), e);
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
		assert(task != null);
		if (task.getId() == null) {
			logger.log(Level.WARNING, MESSAGE_NO_ID);
		} else {
			logger.log(Level.INFO, "Trying to delete task");
			try {
				if (getTask(task) != null) {
					Tasks.TasksOperations.Delete request = tasks.tasks().delete(PRIMARY_TASKS_ID, task.getId());
					request.execute();
					return (getTask(task) == null);
				}
			} catch (IOException e) {
				if (e.getMessage().contains("404 Not Found") || e.getMessage().contains("410 Gone")) {
					logger.log(Level.INFO, MESSAGE_NOT_FOUND);
					return true;
				} else {
					logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_DELETE), e);
				}
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
		assert(task != null);
		if (task.getId() == null) {
			logger.log(Level.WARNING, MESSAGE_NO_ID);
		} else {
			try {
				Tasks.TasksOperations.Delete request = tasks.tasks().delete(PRIMARY_TASKS_ID, task.getId());
				request.execute();
				return (getTask(task) == null);
			} catch (IOException e) {
				if (e.getMessage().contains("404 Not Found")) {
					return true;
				} else {
					logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_DELETE), e);
				}
			}
		}
		return false;
	}

	//@author A0109194A
	/**
	 * Deletes an event from the Calendar API, given a TimedTask object.
	 * The given task must have a Google ID.
	 * Returns true if successful. 
	 * 
	 * @param task   Custom TimedTask object
	 * @return	     Success of action
	 */
	private boolean deleteTask(TimedTask task) {
		assert(task != null);
		if (task.getId() == null) {
			logger.log(Level.WARNING, MESSAGE_NO_ID);
		} else {
			return deleteEventFromCalendar(task);
		}
		return false;
	}

	//@author A0112828H
	/**
	 * Deletes an event from the primary calendar, given a TimedTask.
	 * @param task
	 * @return            TaskCommander task
	 */
	private boolean deleteEventFromCalendar(TimedTask task) {
		try {
			calendar.events().delete(PRIMARY_CALENDAR_ID, task.getId()).execute();
			return (getTask(task) == null);
		} catch (IOException e) {
			if (e.getMessage().contains("404 Not Found")) {
				return true;
			} else {
				logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_DELETE), e);
			}
		}
		return false;
	}

	/**
	 * Updates a task from the Tasks API, given a FloatingTask object.
	 * The given task must have a Google ID.
	 * Returns true if successful.  
	 * 
	 * @param task   Custom FloatingTask object
	 * @return	     Success of action
	 */
	private boolean updateTask(FloatingTask task) {
		assert(task != null);
		if (task.getId() == null) {
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
		assert(task != null);
		if (task.getId() == null) {
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

	//@author A0109194A
	/**
	 * Updates an event from the Calendar API, given a TimedTask object.
	 * The given task must have a Google ID.
	 * Returns true if successful.  
	 * 
	 * @param task   Custom TimedTask object
	 * @return	     Success of action
	 */
	private boolean updateTask(TimedTask task) {
		assert(task != null);
		if (task.getId() == null) {
			logger.log(Level.WARNING, MESSAGE_NO_ID);
		} else {
			return updateEventToCalendar(task);
		}
		return false;
	}

	//@author A0112828H
	/**
	 * Updates an event in the primary calendar, given a TimedTask.
	 * @param task
	 * @return       Success of action
	 */
	private boolean updateEventToCalendar(TimedTask task) {
		logger.log(Level.INFO, "Trying to update task...");
		try {
			Event result = calendar.events().update(PRIMARY_CALENDAR_ID, task.getId(), toGoogleTask(task)).execute();
			return result != null;
		} catch (IOException e) {
			logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_UPDATE), e);
		}
		return false;
	}

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
		assert(task != null);
		if (task.isEmpty()) {
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
		assert(event != null);
		if (event.isEmpty()) {
			return null;
		}

		TimedTask timedTask;
		if (event.getStart().getDateTime() != null && event.getEnd().getDateTime() != null) {
			timedTask = new TimedTask(event.getSummary(),
					toDate(event.getStart().getDateTime()),
					toDate(event.getEnd().getDateTime()));

		} else {
			//Handle all-day event cases
			timedTask = new TimedTask(event.getSummary(), 
					toDate(event.getStart().getDate()), 
					toDate(event.getEnd().getDate()));
		}
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


	//@author A0112828H-unused
	// Methods for done calendar
	/**
	 * Gets all events from Calendar API from the done calendar,
	 * starting from current system time.
	 * @return   Arraylist of TaskCommander Tasks.
	 */
	/*
	private ArrayList<com.taskcommander.Task> getAllDoneEvents() {
		try {
			// Gets all done events
			List<Event> events = calendar.events().list(doneCalendarId)
					.execute().getItems();
			if (events != null) {
				ArrayList<com.taskcommander.Task> taskList = new ArrayList<com.taskcommander.Task>();
				for (Event event : events) {
					com.taskcommander.Task task = toTask(event);
					task.setDone(true);
					taskList.add(task);
				}
				logger.log(Level.INFO, taskList.toString());
				return taskList;
			} else {
				return null;
			}
		}  catch (GoogleJsonResponseException e) {
			if (e.getMessage().contains("401 Unauthorized")) {
				// If not logged in, login attempt handled outside of this class
			} else if (e.getMessage().contains("404 Not Found")) {
				logger.log(Level.WARNING, DONE_CALENDAR_NULL);
			} else {
				logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_GET), e);
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_OPERATION, OPERATION_GET), e);
		}
		return null;
	}

	private boolean hasDoneCalendar() {
		logger.log(Level.INFO, "Checking for Done calendar.");
		try {
			String calId = getIdForDoneCalendar();
			if (calId == null) {
				return false;
			} else {
				return !(calendar.calendarList().get(calId).execute().isEmpty());
			}
		} catch (IOException e) {
			if (e.getMessage().contains("404 Not Found")) {
				logger.log(Level.INFO, DONE_CALENDAR_NULL);
			} else {
				logger.log(Level.WARNING, "Unable to check for Done calendar.", e);
			}
		}
		return false;
	}

	private boolean createDoneCalendar() {
		logger.log(Level.INFO, "Trying to create Done calendar.");
		if (!hasDoneCalendar()) {
			try {
				com.google.api.services.calendar.model.Calendar cal = 
						new com.google.api.services.calendar.model.Calendar();
				cal.setSummary(DONE_CALENDAR_SUMMARY);
				com.google.api.services.calendar.model.Calendar createdCal = 
						calendar.calendars().insert(cal).execute();
				doneCalendarId = createdCal.getId();
				logger.log(Level.INFO, "Done calendar created with ID: " + doneCalendarId);

				CalendarListEntry calendarListEntry = new CalendarListEntry();
				calendarListEntry.setId(doneCalendarId);
				CalendarListEntry newEntry = calendar.calendarList().insert(calendarListEntry).execute();
				return !newEntry.isEmpty();
			} catch (IOException e) {
				logger.log(Level.WARNING, "Unable to create Done calendar.", e);
				return false;
			}
		} else {
			doneCalendarId = getIdForDoneCalendar();
			if (doneCalendarId != null) {
				logger.log(Level.INFO, "Done calendar exists.");
				return true;
			} else {
				return false;
			}
		}
	}

	private String getIdForDoneCalendar() {
		logger.log(Level.INFO, "Trying to find the Done calendar and get the ID...");
		try {
			CalendarList newEntry = calendar.calendarList().list().execute();
			String result = null;
			for (CalendarListEntry c : newEntry.getItems()) {
				if (c.getSummary().equals(DONE_CALENDAR_SUMMARY)) {
					result = c.getId();
				}
			}
			return result;
		} catch (IOException e) {
			logger.log(Level.WARNING, "Unable to get ID for Done calendar.", e);
		}
		return null;
	}
	 */
}
