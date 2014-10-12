package com.taskcommander;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.tasks.Tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.taskcommander.Task.TaskType;

//@author A0112828H and A0109194A
public class SyncHandler {

	private static GoogleAPIConnector con = null;
	
	// The key in the sync settings datastore that holds the current sync token.
	private static final String SYNC_TOKEN_KEY = "syncToken";

	public SyncHandler() {

	}

	/**
	 * Syncs the tasks in memory to Google. Checks if
	 * tasks have been edited since previous sync and
	 * executes respective function.
	 * @return   Feedback for user
	 */
	public String sync() {
		if (con == null) {
			con = new GoogleAPIConnector();
		}
		push();
		try {
			pull();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Global.MESSAGE_SYNC_SUCCESS;
	}
	
	private void push() {
		ArrayList<Task> tasks = TaskCommander.data.getAllTasks();
		for (Task t : tasks) {
			if (!t.isSynced()) {
				if (t.getId() == null) {
					String result = con.addTask(t);
					if (result != null) {
						t.setId(result);
					}
				} else {
					con.updateTask(t);
				}
			}
		}

		// Handle delete cases
		ArrayList<Task> deletedTasks = TaskCommander.data.getDeletedTasks();
		for (Task t : deletedTasks) {
			if (t.getId() != null) {
				con.deleteTask(t);
			}
		}
	}
	
	private void pull() throws IOException{
		//Construct the Calendar.Events.List request, but don't execute yet		
		Calendar.Events.List eventRequest = con.getListEventRequest();
				
	}
	
	private void syncEvent(Event event) throws IOException {
		if ("cancelled".equals(event.getStatus()) && con.getEventDataStore().containsKey(event.getId())) {
			con.getEventDataStore().delete(event.getId());
			TaskCommander.data.deleteTask(con.toTask(event));
		} else {
			con.getEventDataStore().set(event.getId(), event.toString());
			TaskCommander.data.addTask(con.toTask(event));
		}
	}
}
