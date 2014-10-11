package com.taskcommander;
import java.io.IOException;
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

/**
 * This class is used to connect to the Google API and
 * invoke the Calendar and Tasks services.
 * 
 * To use this class, the user has to provide the
 * details of their Google account and sign in.
 * This class can create, read, update or delete tasks
 * and calendar events for the given Google account.
 */
public class GoogleAPIConnector {

	private static final String MESSAGE_NO_ID = "Task has not been synced to Google API.";
	private static final String PRIMARY_CALENDAR_ID = "primary";
	private static LoginManager loginManager;

	//Global instances
	private static Calendar calendar;
	static final java.util.List<Calendar> addedCalendarsUsingBatch = Lists.newArrayList();
	private static Tasks tasks;
	private static DataStore<String> eventDataStore;
	private static DataStore<String> taskDataStore;
	private static DataStore<String> syncSettingsDataStore;
	
	/**
	 * Creates a new GoogleAPIHandler instance.
	 * Also creates a new LoginManager and attempts
	 * to get the Tasks and Calendar services.
	 * @throws IOException 
	 */
	public GoogleAPIConnector() {
		loginManager = new LoginManager();
		try {
			eventDataStore = LoginManager.getDataStoreFactory().getDataStore("EventStore");
			syncSettingsDataStore = LoginManager.getDataStoreFactory().getDataStore("SyncSettings");
			taskDataStore = LoginManager.getDataStoreFactory().getDataStore("TaskStore");
		} catch (IOException e) {
			System.out.println(Global.MESSAGE_EXCEPTION_IO);
		}
		getServices();
	}

	private void getServices() {
		tasks = loginManager.getTasksService();
		calendar = loginManager.getCalendarService();
	}
	
	public DataStore<String> getSyncSettingsDataStore() {
		return syncSettingsDataStore;
	}
	
	public DataStore<String> getTaskDataStore() {
		return taskDataStore;
	}
	
	public DataStore<String> getEventDataStore() {
		return eventDataStore;
	}

	/**
	 * @author A0109194A
	 * Returns all tasks.
	 * @return Feedback for user.
	 */
	public ArrayList<com.taskcommander.Task> getAllTasks() {
		ArrayList<com.taskcommander.Task> result = getAllFloatingTasks();
		result.addAll(getAllEvents());
		return result;
	}
	
	/**
	 * @author A0109194A
	 * Returns all Google Tasks
	 * @return List of Google Tasks
	 */
	public List<Task> getAllGoogleTasks() {
		try {
			Tasks.TasksOperations.List request = tasks.tasks().list(PRIMARY_CALENDAR_ID);
			List<Task> tasks = request.execute().getItems();
			return tasks;
		} catch (IOException e) {
			System.out.println(Global.MESSAGE_EXCEPTION_IO);
			return null;
		}
	}
	
	/**
	 * Gets all tasks from Tasks API.
	 * @return   Arraylist of TaskCommander Tasks.
	 */
	private ArrayList<com.taskcommander.Task> getAllFloatingTasks() {
		try {
			Tasks.TasksOperations.List request = tasks.tasks().list(PRIMARY_CALENDAR_ID);
			List<Task> tasks = request.execute().getItems();

			ArrayList<com.taskcommander.Task> taskList = new ArrayList<com.taskcommander.Task>();
			for (Task task : tasks) {
				taskList.add(toTask(task));
			}
			return taskList;
		} catch (IOException e) {
			System.out.println(Global.MESSAGE_EXCEPTION_IO);
			return null;
		}
	}

	public Tasks.TasksOperations.List getListTasksRequest() {
		try {
			Tasks.TasksOperations.List request = tasks.tasks().list(PRIMARY_CALENDAR_ID);
			return request;
		} catch (IOException e) {
			System.out.println(Global.MESSAGE_EXCEPTION_IO);
			return null;
		}
	}
	
	//@author A0109194A
	/**
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
			System.out.println(Global.MESSAGE_EXCEPTION_IO);
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
			System.out.println(Global.MESSAGE_EXCEPTION_IO);
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
			System.out.println(Global.MESSAGE_EXCEPTION_IO);
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
	public String addTask(FloatingTask task) {
		if (task == null) {
			System.out.println(Global.MESSAGE_ARGUMENTS_NULL);
		} else {
			Task taskToAdd = new Task();
			taskToAdd.setTitle(task.getName());
			try {
				Tasks.TasksOperations.Insert request = tasks.tasks().insert("@default", taskToAdd);
				Task result = request.execute();
				if (result != null) {
					return result.getId();
				}
			} catch (IOException e) {
				System.out.println(Global.MESSAGE_EXCEPTION_IO);
			}
		}
		return null;
	}

	/**
	 * Adds a Task to the Task API, given a DeadlineTask object.
	 * Returns the Google ID if successful.
	 * 
	 * @param task   Custom DeadlineTask object
	 * @return       Google ID of task
	 */
	public String addTask(DeadlineTask task) {
		if (task == null) {
			System.out.println(Global.MESSAGE_ARGUMENTS_NULL);
		} else {
			Task taskToAdd = new Task();
			taskToAdd.setTitle(task.getName());
			taskToAdd.setDue(toDateTime(task.getEndDate()));
			try {
				Tasks.TasksOperations.Insert request = tasks.tasks().insert("@default", taskToAdd);
				Task result = request.execute();
				if (result != null) {
					return result.getId();
				}
			} catch (IOException e) {
				System.out.println(Global.MESSAGE_EXCEPTION_IO);
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
	public String addTask(TimedTask task) {
		if (task == null){
			System.out.println(Global.MESSAGE_ARGUMENTS_NULL);
		} else {
			Event event = new Event();
			event.setSummary(task.getName());
			event.setStart(new EventDateTime().setDateTime(toDateTime(task.getStartDate())));			
			event.setEnd(new EventDateTime().setDateTime(toDateTime(task.getEndDate())));		

			try {
				Event createdEvent = calendar.events().insert(PRIMARY_CALENDAR_ID, event).execute();
				if (createdEvent != null) {
					return createdEvent.getId();
				}
			} catch (IOException e) {
				System.out.println(Global.MESSAGE_EXCEPTION_IO);
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
	public com.taskcommander.Task getTask(FloatingTask task) {
		if (task == null) {
			System.out.println(Global.MESSAGE_ARGUMENTS_NULL);
		} else if (task.getId() == null) {
			System.out.println(MESSAGE_NO_ID);
		} else {
			try {
				Task check = tasks.tasks().get("@default", task.getId()).execute();
				return toTask(check);
			} catch (IOException e) {
				System.out.println(Global.MESSAGE_EXCEPTION_IO);
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
	public com.taskcommander.Task getTask(DeadlineTask task) {
		if (task == null) {
			System.out.println(Global.MESSAGE_ARGUMENTS_NULL);
		} else if (task.getId() == null) {
			System.out.println(MESSAGE_NO_ID);
		} else {
			try {
				Task check = tasks.tasks().get("@default", task.getId()).execute();
				return toTask(check);
			} catch (IOException e) {
				System.out.println(Global.MESSAGE_EXCEPTION_IO);
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
	public com.taskcommander.Task getTask(TimedTask task) {
		if (task == null) {
			System.out.println(Global.MESSAGE_ARGUMENTS_NULL);
		} else if (task.getId() == null) {
			System.out.println(MESSAGE_NO_ID);
		} else {
			try {
				Event check = calendar.events().get("@default", task.getId()).execute();
				return toTask(check);
			} catch (IOException e) {
				System.out.println(Global.MESSAGE_EXCEPTION_IO);
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
	public boolean deleteTask(FloatingTask task) {
		if (task == null) {
			System.out.println(Global.MESSAGE_ARGUMENTS_NULL);
		} else if (task.getId() == null) {
			System.out.println(MESSAGE_NO_ID);
		} else {
			try {
				Tasks.TasksOperations.Delete request = tasks.tasks().delete("@default", task.getId());
				request.execute();
				Task check = tasks.tasks().get("@default", task.getId()).execute();
				return check == null;
			} catch (IOException e) {
				System.out.println(Global.MESSAGE_EXCEPTION_IO);
			}
		}
		return false;
	}
	
	/**
	 * Deletes an event from the Calendar API, given a DeadlineTask object.
	 * The given task must have a Google ID.
	 * Returns true if successful. 
	 * 
	 * @param task   Custom DeadlineTask object
	 * @return	     Success of action
	 */
	public boolean deleteTask(DeadlineTask task) {
		if (task == null) {
			System.out.println(Global.MESSAGE_ARGUMENTS_NULL);
		} else if (task.getId() == null) {
			System.out.println(MESSAGE_NO_ID);
		} else {
			try {
				Tasks.TasksOperations.Delete request = tasks.tasks().delete("@default", task.getId());
				request.execute();
				Task check = tasks.tasks().get("@default", task.getId()).execute();
				return check == null;
			} catch (IOException e) {
				System.out.println(Global.MESSAGE_EXCEPTION_IO);
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
	public boolean deleteTask(TimedTask task) {
		if (task == null) {
			System.out.println(Global.MESSAGE_ARGUMENTS_NULL);
		} else if (task.getId() == null) {
			System.out.println(MESSAGE_NO_ID);
		} else {
			try {
				calendar.events().delete(PRIMARY_CALENDAR_ID, "eventId").execute();
				Event check = calendar.events().get("@default", task.getId()).execute();
				return check == null;
			} catch (IOException e) {
				System.out.println(Global.MESSAGE_EXCEPTION_IO);
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
	public boolean updateTask(FloatingTask task) {
		if (task == null) {
			System.out.println(Global.MESSAGE_ARGUMENTS_NULL);
		} else if (task.getId() == null) {
			System.out.println(MESSAGE_NO_ID);
		} else {
			try {
				Task result = tasks.tasks().update("@default", task.getId(), toGoogleTask(task)).execute();
				return result != null;
			} catch (IOException e) {
				System.out.println(Global.MESSAGE_EXCEPTION_IO);
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
	public boolean updateTask(DeadlineTask task) {
		if (task == null) {
			System.out.println(Global.MESSAGE_ARGUMENTS_NULL);
		} else if (task.getId() == null) {
			System.out.println(MESSAGE_NO_ID);
		} else {
			try {
				Task result = tasks.tasks().update("@default", task.getId(), toGoogleTask(task)).execute();
				return result != null;
			} catch (IOException e) {
				System.out.println(Global.MESSAGE_EXCEPTION_IO);
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
	public boolean updateTask(TimedTask task) {
		if (task == null) {
			System.out.println(Global.MESSAGE_ARGUMENTS_NULL);
		} else if (task.getId() == null) {
			System.out.println(MESSAGE_NO_ID);
		} else {
			try {
				Event result = calendar.events().update("@default", task.getId(), toGoogleTask(task)).execute();
				return result != null;
			} catch (IOException e) {
				System.out.println(Global.MESSAGE_EXCEPTION_IO);
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

	// Changes a Google Task to a TaskCommander Task.
	public com.taskcommander.Task toTask(Task task) {
		if (task == null) {
			return null;
		}
		if (task.containsKey("due")) {
			DeadlineTask deadlineTask = new DeadlineTask(task.getTitle(), toDate(task.getDue()));
			deadlineTask.setId(task.getId());
			return deadlineTask;
		} else {
			return new FloatingTask(task.getTitle(), task.getId());	
		}
	}

	// Changes a Google Calendar Event to a TaskCommander Task.
	public com.taskcommander.Task toTask(Event event) {
		if (event == null) {
			return null;
		}
		TimedTask timedTask = new TimedTask(event.getSummary(),
				toDate(event.getStart().getDateTime()),
				toDate(event.getEnd().getDateTime()));
		timedTask.setId(event.getId());
		return timedTask;
	}
				
	private Task toGoogleTask(FloatingTask task) {
		Task newTask = new Task();
		newTask.setTitle(task.getName());
		setStatusFromTask(newTask, task);
		return newTask;
	}
	
	private Task toGoogleTask(DeadlineTask task) {
		Task newTask = new Task();
		newTask.setTitle(task.getName());
		newTask.setDue(toDateTime(task.getEndDate()));
		setStatusFromTask(newTask, task);
		return newTask;
	}
	
	private Event toGoogleTask(TimedTask task) {
		Event newEvent = new Event();
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
