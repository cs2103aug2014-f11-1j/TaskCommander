package com.taskcommander;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.tasks.Tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.taskcommander.Task.TaskType;

//@author A0112828H and A0109194A
public class SyncHandler {

	private static GoogleAPIConnector con = null;
	private static final Logger logger = Logger.getLogger(SyncHandler.class.getName());

	
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
			System.out.println(Global.MESSAGE_FAILED_PULL);
		}
		return Global.MESSAGE_SYNC_SUCCESS;
	}
	
	private void push() {
		ArrayList<Task> tasks = TaskCommander.data.getAllTasks();
		logger.log(Level.INFO, "PUSH: Retrieved All Tasks");
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
		logger.log(Level.INFO, "PUSH: Handled Added Cases");

		// Handle delete cases
		ArrayList<Task> deletedTasks = TaskCommander.data.getDeletedTasks();
		for (Task t : deletedTasks) {
			if (t.getId() != null) {
				con.deleteTask(t);
			}
		}
		logger.log(Level.INFO, "PUSH: Handled Deleted Cases");
	}
	
	private void pull() throws IOException {
		//Get all Tasks
		ArrayList<Task> tasksToSync = con.getAllTasks();
		logger.log(Level.INFO, "PULL: Retrieved All Tasks");
		
		//Added case
		ArrayList<String> taskIds = TaskCommander.data.getAllIds();
		for (Task t: tasksToSync) {
			if (!taskIds.contains(t.getId())) {
				TaskCommander.data.addTask(t);
			}
		}
		logger.log(Level.INFO, "PULL: Handled Added Tasks");
		
		//Deleted case
		//For Tasks
		List<com.google.api.services.tasks.model.Task> googleTasks = con.getAllGoogleTasks();
		for (com.google.api.services.tasks.model.Task task : googleTasks) {
			if (task.getDeleted()) {
				TaskCommander.data.deleteTask(con.toTask(task));
			}
		}
		logger.log(Level.INFO, "PULL: Handled Deleted Google Tasks");
		
		
		//Deleted Case For Events
		List<Event> googleEvents = con.getAllGoogleEvents();
		for (Event event : googleEvents) {
			if ("cancelled".equals(event.getStatus())) {
				TaskCommander.data.deleteTask(con.toTask(event));
			}
		}
		logger.log(Level.INFO, "PULL: Handled Deleted Google Events");
		
		//Updated cases
		ArrayList<Task> tasks = TaskCommander.data.getAllTasks();
		taskIds = TaskCommander.data.getAllIds(); 
		for (Task t: tasksToSync) {
			int index = taskIds.indexOf(t.getId());
			if (t.getUpdated() != tasks.get(index).getUpdated()) {
				switch(t.getType()) {
				case FLOATING:
					TaskCommander.data.updateToFloatingTask(index, (FloatingTask) t);
				case TIMED:
					TaskCommander.data.updateToTimedTask(index, (TimedTask) t);
				case DEADLINE:
					TaskCommander.data.updateToDeadlineTask(index, (DeadlineTask) t);
				}		
			}
		}
	}
}
