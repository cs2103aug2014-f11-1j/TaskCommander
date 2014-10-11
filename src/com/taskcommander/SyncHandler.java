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
		String syncToken = null;
		
		try {
			syncToken = con.getSyncSettingsDataStore().get(SYNC_TOKEN_KEY);
		} catch (IOException e) {
			System.out.println(Global.MESSAGE_EXCEPTION_IO);
		}
		
		if (syncToken == null) {
			System.out.println(Global.MESSAGE_FULL_SYNC);
		} else {
			System.out.println();
			eventRequest.setSyncToken(syncToken);
		}
		
		//Retrieve the events, one page at a time.
		String pageToken = null;
		Events events = null;
		do {
			eventRequest.setTimeMin(new DateTime(System.currentTimeMillis())); 
			eventRequest.setPageToken(pageToken);
			
			try {
				events = eventRequest.execute();
			} catch (IOException e) {
				System.out.println(Global.MESSAGE_INVALID_SYNC_TOKEN);
				con.getSyncSettingsDataStore().delete(SYNC_TOKEN_KEY);
				con.getEventDataStore().clear();
				pull();
			}
			
			List<Event> items = events.getItems();
			if (items.size() == 0) {
				System.out.println(Global.MESSAGE_NO_NEW_SYNC);
			} else {
				for (Event event : items) {
					syncEvent(event);
				}
			}
		} while (pageToken != null);
		
		/**Store the sync token from the last request to be used during the
		next execution**/
		con.getSyncSettingsDataStore().set(SYNC_TOKEN_KEY, events.getNextSyncToken());
		
		System.out.println(Global.MESSAGE_COMPLETED_SYNC);		
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
