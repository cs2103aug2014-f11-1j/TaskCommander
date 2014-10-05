package com.taskcommander;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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

	//Global instances
	private static Calendar calendar;
	static final java.util.List<Calendar> addedCalendarsUsingBatch = Lists.newArrayList();
	private static Tasks taskService;

	
	/**
	 * Prints out all tasks.
	 * @return       Feedback for user.
	 */
	public String getAllTasks() {
		try {
			Tasks.TasksOperations.List request = taskService.tasks().list("@default");
			List<Task> tasks = request.execute().getItems();

			String result = "";
			for (Task task : tasks) {
				result += task.getTitle() + "\n";
			}
			return result;
		} catch (IOException e) {
			return MESSAGE_EXCEPTION_IO;
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
			return MESSAGE_ARGUMENTS_NULL;
		} else {
			Task taskToAdd = new Task();
			taskToAdd.setTitle(task.getName());
			try {
				Tasks.TasksOperations.Insert request = taskService.tasks().insert("@default", taskToAdd);
				Task result = request.execute();
				return result.getTitle();
			} catch (IOException e) {
				return MESSAGE_EXCEPTION_IO;
			}
		}
	}

	/**
	 * Adds a task given a DatedTask object.
	 * Returns the task name if successful.
	 * 
	 * @param task   Custom DatedTask object
	 * @return       Feedback for user.
	 */
	public String addTask(DeadlineTask task) {
		if (task == null) {
			return MESSAGE_ARGUMENTS_NULL;
		} else {
			Task taskToAdd = new Task();
			taskToAdd.setTitle(task.getName());
			taskToAdd.setDue(toDateTime(task.getEndDate()));	
			try {
				Tasks.TasksOperations.Insert request = taskService.tasks().insert("@default", taskToAdd);
				Task result = request.execute();
				return result.getTitle();
			} catch (IOException e) {
				return MESSAGE_EXCEPTION_IO;
			}
		}
	}

	// @author Sean Saito
	/**
	 * Adds an Event to the primary calendar given a TimedTask object.
	 * Returns the name of the task if successful. 
	 * 
	 * @param task   Custom TimedTask object
	 * @return	Title of the event
	 */
	public String addEvent(TimedTask task) {
		if (task == null){
			return MESSAGE_ARGUMENTS_NULL;
		} else {
			Event event = new Event();
			event.setSummary(task.getName());
			event.setStart(new EventDateTime().setDateTime(toDateTime(task.getStartDate())));			
			event.setEnd(new EventDateTime().setDateTime(toDateTime(task.getEndDate())));		

			try {
				Event createdEvent = calendar.events().insert(PRIMARY_CALENDAR_ID, event).execute();
				return createdEvent.getSummary();
			} catch (IOException e) {
				return MESSAGE_EXCEPTION_IO;
			}
		}
	}

	/**
	 * Returns a list of all events starting from current system time.
	 * @return List of all events
	 */
	public String getAllEvents(){
		try {
			// Gets events from current time onwards
			List<Event> events = calendar.events().list(PRIMARY_CALENDAR_ID)
					.setTimeMin(new DateTime(System.currentTimeMillis())) 
					.execute().getItems();

			String result = "";
			for (Event event : events){
				result += event.getSummary() + "\n";
			}
			return result;
		} catch (IOException e){
			return MESSAGE_EXCEPTION_IO;
		}
	}

	//@author A0112828H
	// Changes a Date to a DateTime object.
	private DateTime toDateTime(Date date) {
		return new DateTime(date);
	}

}
