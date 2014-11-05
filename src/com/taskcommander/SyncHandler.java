package com.taskcommander;

import com.google.api.services.calendar.model.Event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.taskcommander.Global.SyncState;
import com.taskcommander.GoogleAPIConnector;

//@author A0112828H
/**
 * Facade for the Google Integration component.
 * 
 * Handles synchronisation of data to and from Google services.
 * Notifies observers with a String message when sync status is updated.
 * 
 * To use this class, the user has to sign in and approve
 * the permissions for this application through the UI.
 */
public class SyncHandler extends Observable {

	private static final String STATUS_CANCELLED = "cancelled";
	private static GoogleAPIConnector con = null;
	private static final Logger logger = Logger.getLogger(SyncHandler.class.getName());

	private static final String MESSAGE_SYNC_PUSH = "Sending data to Google... %.2f%% completed.";
	private static final String MESSAGE_SYNC_PULL = "Getting data from Google... %.2f%% completed.";
	private static final String MESSAGE_SYNC_DONE = "Sync completed.";

	public int tasksTotal;
	public int tasksComplete;
	public SyncState syncState;

	public SyncHandler() {

	}

	/**
	 * Syncs the tasks in memory to Google. Checks if tasks have been edited 
	 * since previous sync and executes respective functions.
	 * @return   Feedback for user
	 */
	public String sync() {
		Global.syncing = true;
		TaskCommander.syncHandler.addObserver(TaskCommander.ui);
		if (con == null) {
			con = GoogleAPIConnector.getInstance();
		}

		Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread th, Throwable ex) {
				logger.log(Level.WARNING, Global.MESSAGE_SYNC_FAILED, ex);
				th.interrupt();
			}
		};
		Thread thread = new Thread() {
			long startTime = System.currentTimeMillis(); // gets current system time
			long endTime = 0;
			@Override
			public void run() {
				logger.log(Level.INFO, "Waiting for login...");
				while (!con.getServices()) {
					try {
						sleep(10);  // milliseconds
						endTime = System.currentTimeMillis();
					} catch (InterruptedException e) {
						logger.log(Level.WARNING, "Error while trying to sleep in SyncHandler", e);
					}
				}

					logger.log(Level.INFO, "Trying to sync...");
					push();
					try {
						pull();
						resetSyncState(SyncState.DONE);
					} catch (Exception e) {
						logger.log(Level.WARNING, Global.MESSAGE_SYNC_FAILED, e);
					}
			}
		};
		thread.setUncaughtExceptionHandler(h);
		thread.start();

		return Global.MESSAGE_SYNC_IN_PROGRESS;
	}

	private void push() {
		ArrayList<Task> tasks = TaskCommander.data.getAllTasks();
		ArrayList<Task> deletedTasks = TaskCommander.data.getDeletedTasks();
		Stack<Task> preupdatedTasks = TaskCommander.data.getPreupdatedTasks();
		Stack<ArrayList<Task>> clearedTasks = TaskCommander.data.getClearedTasks();
		logger.log(Level.INFO, "PUSH: Retrieved All Tasks");
		startSyncState(SyncState.PUSH, tasks.size() + deletedTasks.size() + 
				preupdatedTasks.size() + clearedTasks.size());

		//Handle Added Cases
		for (Task t : tasks) {
			if (!t.isSynced()) {
				if (t.getId() == null) {
					String result = con.addTask(t);
					if (result != null) {
						t.setId(result);
					}
				} else if (t.isEdited()) {
					con.updateTask(t);
				}
			}
			updateTasksComplete(tasksComplete+1);
		}
		logger.log(Level.INFO, "PUSH: Handled Added Cases");

		// Handle delete cases
		for (Task t : deletedTasks) {
			if (t.getId() != null) {
				con.deleteTask(t);
			}
		}

		// Delete tasks that were updated
		for (Task t: preupdatedTasks) {
			if (t.getId() != null) {
				con.deleteTask(t);
			}
		}


		// Delete tasks that were cleared
		for (ArrayList<Task> list : clearedTasks) {
			for (Task t : list) {
				if (t.getId() != null) {
					con.deleteTask(t);
				}
			}
		}

		logger.log(Level.INFO, "PUSH: Handled Deleted Cases");
		logger.log(Level.INFO, "PUSH: End Push");
	}

	//@author A0109194A
	private void pull() throws IOException {
		logger.log(Level.INFO, "PULL: Starting Pull");
		//Get all Tasks
		ArrayList<Task> tasksToSync = con.getAllTasks();
		ArrayList<Task> tasks = TaskCommander.data.getAllTasks();
		List<com.google.api.services.tasks.model.Task> googleTasks = con.getAllGoogleTasks();
		List<Event> googleEvents = con.getAllGoogleEvents();
		ArrayList<String> taskIds = TaskCommander.data.getAllIds();
		logger.log(Level.INFO, "PULL: Retrieved All Tasks");
		startSyncState(SyncState.PULL, tasksToSync.size() + tasks.size() + 
				googleTasks.size() + googleEvents.size());

		//Added case
		//For Tasks
		for (com.google.api.services.tasks.model.Task task : googleTasks) {
			if (!taskIds.contains(task.getId()) && task.getDeleted() != null && task.getTitle() != null) {
				TaskCommander.data.addTask(con.toTask(task));
			}
			updateTasksComplete(tasksComplete+1);
		}

		//For Events
		for (Event event: googleEvents) {
			if (!taskIds.contains(event.getId()) && !event.getStatus().equals(STATUS_CANCELLED)) {
				TaskCommander.data.addTask(con.toTask(event));
			}
			updateTasksComplete(tasksComplete+1);
		}

		logger.log(Level.INFO, "PULL: Handled Added Tasks");

		tasks = TaskCommander.data.getAllTasks();
		taskIds = TaskCommander.data.getAllIds();

		//Updated cases
		for (Task t: tasksToSync) {
			int index = taskIds.indexOf(t.getId());
			if (index == -1) {
				continue;
			}
			if (t.getUpdated() != tasks.get(index).getUpdated()) {
				switch(t.getType()) {
				case FLOATING:
					TaskCommander.data.updateToFloatingTask(index, (FloatingTask) t);
					break;
				case TIMED:
					TaskCommander.data.updateToTimedTask(index, (TimedTask) t);
					break;
				case DEADLINE:
					TaskCommander.data.updateToDeadlineTask(index, (DeadlineTask) t);
					break;
				}		
			}
			updateTasksComplete(tasksComplete+1);
		}
		logger.log(Level.INFO, "PULL: Handled Updated Cases");

		//Deleted case
		//For Tasks
		for (com.google.api.services.tasks.model.Task t : googleTasks) {
			if (t.getDeleted() == null) {
				int index = taskIds.indexOf(t.getId());
				if (index == -1) {
					continue;
				} else {
					TaskCommander.data.deleteFromGoogle(index);
				}
			}
			updateTasksComplete(tasksComplete+1);
		}

		logger.log(Level.INFO, "PULL: Handled Deleted Google Tasks");

		//Deleted Case For Events
		for (Event event : googleEvents) {
			tasks = TaskCommander.data.getAllTasks();
			taskIds = TaskCommander.data.getAllIds();

			if (event.getStatus().equals(STATUS_CANCELLED)) {
				int index = taskIds.indexOf(event.getId());
				if (index == -1) {
					continue;
				} else {
					TaskCommander.data.deleteFromGoogle(index);
				}
			}
			updateTasksComplete(tasksComplete+1);
		}
		logger.log(Level.INFO, "PULL: Handled Deleted Google Events");
		logger.log(Level.INFO, "PULL: End Pull");
	}

	//@author A0112828H
	// Methods for keeping track and notifying observers of sync state
	private void updateTasksComplete(int completed) {
		tasksComplete = completed;
		updateSyncMessage();
	}

	private void startSyncState(SyncState state, int total) {
		logger.log(Level.INFO, "Starting sync state " + state + " with " + total + " tasks.");
		syncState = state;
		tasksTotal = total;
		tasksComplete = 0;
		updateSyncMessage();
	}

	private void resetSyncState(SyncState state) {
		Global.syncing = false;
		syncState = state;
		tasksTotal = 0;
		tasksComplete = 0;
		updateSyncMessage();
	}

	private void updateSyncMessage() {
		String message = "";
		switch (syncState) {
		case DONE:
			message = MESSAGE_SYNC_DONE;
			break;
		case PUSH:
			message = String.format(MESSAGE_SYNC_PUSH, getTaskCompletion());
			break;
		case PULL:
			message = String.format(MESSAGE_SYNC_PULL, getTaskCompletion());
			break;
		}

		setChanged();
		notifyObservers(message);
	}

	/**
	 * Returns the percentage of task completion as a float.
	 */
	private float getTaskCompletion() {
		if (tasksTotal == 0) {
			return 0;
		} else {
			return (float) tasksComplete / (float) tasksTotal * (float) 100;
		}
	}
}
