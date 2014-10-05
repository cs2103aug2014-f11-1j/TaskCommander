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


	/**
	 * Adds a task given a FloatingTask object.
	 * Returns the task name if successful.
	 * 
	 * @param task   Custom FloatingTask object
	 * @return       Feedback for user.
	 */
	public String addTask(FloatingTask task) {
		if (task == null) {
			return Global.MESSAGE_ARGUMENTS_NULL;
		} else {
			Task taskToAdd = new Task();
			taskToAdd.setTitle(task.getName());
			try {
				Tasks.TasksOperations.Insert request = tasks.tasks().insert("@default", taskToAdd);
				Task result = request.execute();
				return result.getTitle();
			} catch (IOException e) {
				return Global.MESSAGE_EXCEPTION_IO;
			}
		}
	}

	// @author Sean Saito
	/**
	 * Adds a task given a DatedTask object.
	 * Returns the task name if successful.
	 * 
	 * @param task   Custom DatedTask object
	 * @return       Feedback for user.
	 */
	public String addTask(DeadlineTask task) {
		//TODO @Sean
		return "";
	}

	/**
	 * Adds an Event to the primary calendar given a TimedTask object.
	 * Returns the name of the task if successful. 
	 * 
	 * @param task   Custom TimedTask object
	 * @return	Title of the event
	 */
	public String addEvent(TimedTask task) {
		if (task == null){
			return Global.MESSAGE_ARGUMENTS_NULL;
		} else {
			Event event = new Event();
			event.setSummary(task.getName());
			event.setStart(new EventDateTime().setDateTime(toDateTime(task.getStartDate())));			
			event.setEnd(new EventDateTime().setDateTime(toDateTime(task.getEndDate())));		

			try {
				Event createdEvent = calendar.events().insert(PRIMARY_CALENDAR_ID, event).execute();
				return createdEvent.getSummary();
			} catch (IOException e) {
				return Global.MESSAGE_EXCEPTION_IO;
			}
		}
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

}
