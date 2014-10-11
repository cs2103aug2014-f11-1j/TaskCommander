package com.taskcommander;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

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
		Calendar.Events.List request = con.getListEventRequest();
		String syncToken = null;
		
		try {
			syncToken = con.getSyncSettingsDataStore().get(SYNC_TOKEN_KEY);
		} catch (IOException e) {
			System.out.println(Global.MESSAGE_EXCEPTION_IO);
		}
		
		if (syncToken == null) {
			System.out.println("Performing full sync.");
		} else {
			System.out.println("Performing incremental sync.");
			request.setSyncToken(syncToken);
		}
		
		//Retrieve the events, one page at a time.
		String pageToken = null;
		com.google.api.services.calendar.model.Events events = null;
		do {
			request.setTimeMin(new DateTime(System.currentTimeMillis())); 
			request.setPageToken(pageToken);
			
			try {
				events = request.execute();
			} catch (IOException e) {
				System.out.println(Global.MESSAGE_INVALID_SYNC_TOKEN);
				con.getSyncSettingsDataStore().delete(SYNC_TOKEN_KEY);
				con.getEventDataStore().clear();
				pull();
			}
			
			List<Event> items = events.getItems();
			if (items.size() == 0) {
				System.out.println("No new events to sync.");
			} else {
				for (Event event : items) {
					syncEvent(event);
				}
			}
			
			
		} while (pageToken != null);
		
		/**Store the sync token from the last request to be used during the
		next execution**/
		con.getSyncSettingsDataStore().set(SYNC_TOKEN_KEY, events.getNextSyncToken());
		
		
		
		//Handle Added Cases
		ArrayList<com.taskcommander.Task> toSync = con.getAllTasks();
		ArrayList<String> idList = TaskCommander.data.getAllIds();
		for (Task task : toSync) {
			if (!containsId(task.getId(), idList)) {
				if (task.getType() == TaskType.DEADLINE) {
					TaskCommander.data.addDeadlineTask(task.getName(),
							((DeadlineTask) task).getEndDate());
				} else if (task.getType() == TaskType.FLOATING) {
					TaskCommander.data.addFloatingTask(task.getName());
				} else {
					TaskCommander.data.addTimedTask(task.getName(), 
							((TimedTask) task).getStartDate(), ((TimedTask) task).getEndDate());
				}
			}
		}
		
		//Deleted Cases
		ArrayList<String> googleIdList = con.getAllIds();
		for (com.taskcommander.Task task : TaskCommander.data.tasks){
			if (!containsId(task.getId(), googleIdList)) {
				TaskCommander.data.deleteTask(task);
			}
		}
		
	}
	
	private void syncEvent(Event event) throws IOException {
		if ("cancelled".equals(event.getStatus()) && con.getEventDataStore().containsKey(event.getId())) {
			con.getEventDataStore().delete(event.getId());
			TaskCommander.data.deleteTask(con.toTask(event));
		} else {
			con.getEventDataStore().set(event.getId(), event.toString());
		}
	}

	private boolean containsId(String id, ArrayList<String> idList) {
		if (idList.contains(id)) {
			return true;
		} else {
			return false;
		}
	}
}
