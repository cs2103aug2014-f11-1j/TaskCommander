package com.taskcommander;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.api.client.auth.oauth2.DataStoreCredentialRefreshListener;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Lists;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.TasksScopes;
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
public class GoogleAPIHandler {

	private static final String MESSAGE_NO_ID = "Task has not been synced to Google API.";
	private static final String PRIMARY_CALENDAR_ID = "primary";
	private static LoginManager loginManager;

	//Global instances
	private static Calendar calendar;
	static final java.util.List<Calendar> addedCalendarsUsingBatch = Lists.newArrayList();
	private static Tasks tasks;

	/**
	 * Creates a new GoogleAPIHandler instance.
	 * Also creates a new LoginManager and attempts
	 * to get the Tasks and Calendar services.
	 */
	public GoogleAPIHandler() {
		loginManager = new LoginManager();
		getServices();
	}

	private void getServices() {
		tasks = loginManager.getTasksService();
		calendar = loginManager.getCalendarService();
	}

	/**
	 * Returns all tasks.
	 * @return       Feedback for user.
	 */
	public ArrayList<com.taskcommander.Task> getAllTasks() {
		ArrayList<com.taskcommander.Task> result = getAllFloatingTasks();
		result.addAll(getAllEvents());
		return result;
	}

	/**
	 * Gets all tasks from Tasks API.
	 * @return   Arraylist of TaskCommander Tasks.
	 */
	private ArrayList<com.taskcommander.Task> getAllFloatingTasks() {
		try {
			Tasks.TasksOperations.List request = tasks.tasks().list("@default");
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

	//@author Sean Saito
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
			for (Event event : events){
				taskList.add(toTask(event));
			}
			return taskList;
		} catch (IOException e) {
			System.out.println(Global.MESSAGE_EXCEPTION_IO);
			return null;
		}
	}

	//@author A0112828H
	/**
	 * Adds a task to the Tasks API, given a FloatingTask object.
	 * Returns true if successful.
	 * 
	 * @param task   Custom FloatingTask object
	 * @return       Success of action
	 */
	public boolean addTask(FloatingTask task) {
		if (task == null) {
			System.out.println(Global.MESSAGE_ARGUMENTS_NULL);
		} else {
			Task taskToAdd = new Task();
			taskToAdd.setTitle(task.getName());
			try {
				Tasks.TasksOperations.Insert request = tasks.tasks().insert("@default", taskToAdd);
				Task result = request.execute();
				return result != null;
			} catch (IOException e) {
				System.out.println(Global.MESSAGE_EXCEPTION_IO);
			}
		}
		return false;
	}

	// @author Sean Saito A0109194A
	/**
	 * Adds an Event to the Calendar API, given a DeadlineTask object.
	 * Returns true if successful.
	 * 
	 * @param task   Custom DeadlineTask object
	 * @return       Success of action
	 */
	public boolean addTask(DeadlineTask task) {
		if (task == null) {
			System.out.println(Global.MESSAGE_ARGUMENTS_NULL);
		} else {
			Event event = new Event();
			event.setSummary(task.getName());
			event.setEnd(new EventDateTime().setDateTime(toDateTime(task.getEndDate())));
			
			try {
				Event createdEvent = calendar.events().insert(PRIMARY_CALENDAR_ID, event).execute();
				return createdEvent != null;
			} catch (IOException e) {
				System.out.println(Global.MESSAGE_EXCEPTION_IO);
			}
		}
		return false;
	}
	
	//@author Sean Saito A0109194A
	/**
	 * Adds an Event to the Calendar API, given a TimedTask object.
	 * Returns true if successful. 
	 * 
	 * @param task   Custom TimedTask object
	 * @return	     Success of action
	 */
	public boolean addTask(TimedTask task) {
		if (task == null){
			System.out.println(Global.MESSAGE_ARGUMENTS_NULL);
		} else {
			Event event = new Event();
			event.setSummary(task.getName());
			event.setStart(new EventDateTime().setDateTime(toDateTime(task.getStartDate())));			
			event.setEnd(new EventDateTime().setDateTime(toDateTime(task.getEndDate())));		

			try {
				Event createdEvent = calendar.events().insert(PRIMARY_CALENDAR_ID, event).execute();
				return createdEvent != null;
			} catch (IOException e) {
				System.out.println(Global.MESSAGE_EXCEPTION_IO);
			}
		}
		return false;
	}
	
	//@author A0112828H
	/**
	 * Gets a task from the Tasks API, given a FloatingTask object.
	 * The given task must have a Google ID.
	 * Returns the name of the task if successful. 
	 * 
	 * @param task   Custom FloatingTask object
	 * @return	     Success of action
	 */
	public Task getTask(FloatingTask task) {
		if (task == null) {
			System.out.println(Global.MESSAGE_ARGUMENTS_NULL);
		} else if (task.getId() == null) {
			System.out.println(MESSAGE_NO_ID);
		} else {
			try {
				Task check = tasks.tasks().get("@default", task.getId()).execute();
				return check;
			} catch (IOException e) {
				System.out.println(Global.MESSAGE_EXCEPTION_IO);
			}
		}
		return null;
	}
	
	//@author Michelle
	/**
	 * Gets an event from the Calendar API, given a DeadlineTask object.
	 * The given task must have a Google ID.
	 * Returns the name of the task if successful. 
	 * 
	 * @param task   Custom DeadlineTask object
	 * @return	     Success of action
	 */
	public String getTask(DeadlineTask task) {
		//TODO @Michelle
		return null;
	}
	
	/**
	 * Gets an event from the Calendar API, given a TimedTask object.
	 * The given task must have a Google ID.
	 * Returns the name of the task if successful. 
	 * 
	 * @param task   Custom TimedTask object
	 * @return	     Success of action
	 */
	public Task getTask(TimedTask task) {
		if (task == null) {
			System.out.println(Global.MESSAGE_ARGUMENTS_NULL);
		} else if (task.getId() == null) {
			System.out.println(MESSAGE_NO_ID);
		} else {
			try {
				Event check = calendar.events().get("@default", task.getId()).execute();
				return check;
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
	 * Returns the name of the task if successful. 
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
	
	//@author Sean Saito
	/**
	 * Deletes an event from the Calendar API, given a DeadlineTask object.
	 * The given task must have a Google ID.
	 * Returns the name of the task if successful. 
	 * 
	 * @param task   Custom DeadlineTask object
	 * @return	     Success of action
	 */
	public String deleteTask(DeadlineTask task) {
		//TODO @Sean
		return null;
	}
	
	/**@author Sean Saito A0109194A
	 * Deletes an event from the Calendar API, given a TimedTask object.
	 * The given task must have a Google ID.
	 * Returns the name of the task if successful. 
	 * 
	 * @param task   Custom TimedTask object
	 * @return	     Success of action
	 */
	public String deleteTask(TimedTask task) {
		if (task == null) {
			System.out.println(Global.MESSAGE_ARGUMENTS_NULL);
		} else if (task.getId() == null) {
			System.out.println(MESSAGE_NO_ID);
		} else {
			try {
				Calendar.CalendarOperations.Delete request = calendar.events().delete("@default", task.getId());
				request.execute();
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
	 * Returns the name of the task if successful. 
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

	//@author Michelle
	/**
	 * Updates an event from the Calendar API, given a DeadlineTask object.
	 * The given task must have a Google ID.
	 * Returns the name of the task if successful. 
	 * 
	 * @param task   Custom DeadlineTask object
	 * @return	     Success of action
	 */
	public String updateTask(DeadlineTask task) {
		//TODO @Michelle
		return null;
	}
	
	/**@author SeanSaito
	 * Updates an event from the Calendar API, given a TimedTask object.
	 * The given task must have a Google ID.
	 * Returns the name of the task if successful. 
	 * 
	 * @param task   Custom TimedTask object
	 * @return	     Success of action
	 */
	public String updateTask(TimedTask task) {
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
	
	// Changes a Date to a DateTime object.
	private Date toDate(DateTime dateTime) {
		return new Date(dateTime.getValue());
	}

	// Changes a Google Task to a TaskCommander Task.
	private com.taskcommander.Task toTask(Task task) {
		return new FloatingTask(task.getTitle(), task.getId());
	}

	// Changes a Google Calendar Event to a TaskCommander Task.
	private com.taskcommander.Task toTask(Event event) {
		if (event.containsKey("start")) {
			return new TimedTask(event.getSummary(), 
					toDate(event.getStart().getDateTime()), 
					toDate(event.getEnd().getDateTime()));
		} else {
			return new DeadlineTask(event.getSummary(), 
					toDate(event.getEnd().getDateTime()));
		}
	}
	
	private Task toGoogleTask(FloatingTask task) {
		Task newTask = new Task();
		newTask.setTitle(task.getName());
		setStatusFromTask(newTask, task);
		return newTask;
	}
	
	private Event toGoogleTask(DeadlineTask task) {
		Event newEvent = new Event();
		newEvent.setSummary(task.getName());			
		newEvent.setEnd(new EventDateTime().setDateTime(toDateTime(task.getEndDate())));		
		return newEvent;
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

}
