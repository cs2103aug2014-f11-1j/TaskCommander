package com.taskcommander;

import com.google.api.services.calendar.model.Event;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
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
	private static final String MESSAGE_SYNC_FAILED = "Sync failed, please try again.";

	public int tasksTotal;
	public int tasksComplete;
	public SyncState syncState;

	public SyncHandler() {

	}

	/**
	 * Syncs the tasks in memory to Google in a separate thread. 
	 * Attempts to login if not already logged in.
	 * Notifies UI when sync state changes.
	 * @return   Feedback for user
	 */
	public String sync() {
		Global.syncing = true;
		TaskCommander.syncHandler.addObserver(TaskCommander.ui);
		if (con == null) {
			con = GoogleAPIConnector.getInstance();
		}

		Thread.UncaughtExceptionHandler h = createExceptionHandler();
		Thread thread = new SyncThread();
		thread.setUncaughtExceptionHandler(h);
		thread.start();

		return Global.MESSAGE_SYNC_IN_PROGRESS;
	}

	/**
	 * Attempts to get services from GoogleAPIConnector and syncs data to Google.
	 * If services not available, waits for GoogleAPIConnector to login.
	 */
	class SyncThread extends Thread {
		@Override
		public void run() {
			logger.log(Level.INFO, "Waiting for login...");
			while (!con.getServices()) {
				try {
					sleep(10);  // milliseconds
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
	}
	
	/**
	 * Returns an UncaughtExceptionHandler that interrupts the thread
	 * and resets sync state when exceptions are caught.
	 */
	private UncaughtExceptionHandler createExceptionHandler() {
		return new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread th, Throwable ex) {
				logger.log(Level.WARNING, Global.MESSAGE_SYNC_FAILED, ex);
				th.interrupt();
				resetSyncState(SyncState.FAILED);
			}
		};
	}

	/**
	 * Pushes tasks that have been changed since last sync to Google services.
	 * @param tasks
	 */
	private void push() {
		ArrayList<Task> tasks = TaskCommander.data.getAllTasks();
		ArrayList<Task> deletedTasks = TaskCommander.data.getDeletedTasks();
		Stack<Task> preupdatedTasks = TaskCommander.data.getPreupdatedTasks();
		Stack<ArrayList<Task>> clearedTasks = TaskCommander.data.getClearedTasks();
		logger.log(Level.INFO, "PUSH: Retrieved All Tasks");
		startSyncState(SyncState.PUSH, tasks.size() + deletedTasks.size() + 
				preupdatedTasks.size() + clearedTasks.size());

		pushUnsyncedTasks(tasks);
		pushDeletedTasks(deletedTasks);
		pushPreupdatedTasks(preupdatedTasks);
		pushClearedTasks(clearedTasks);

		logger.log(Level.INFO, "PUSH: End Push");
	}

	/**
	 * Pushes tasks that have been changed since last sync to Google services.
	 * Does not include tasks that have changed types.
	 * @param tasks
	 */
	private void pushUnsyncedTasks(ArrayList<Task> tasks) {
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
	}

	/**
	 * Pushes tasks that have been deleted since last sync to Google services.
	 * @param deletedTasks
	 */
	private void pushDeletedTasks(ArrayList<Task> deletedTasks) {
		// Handle delete cases
		for (Task t : deletedTasks) {
			if (t.getId() != null) {
				con.deleteTask(t);
			}
		}
		logger.log(Level.INFO, "PUSH: Handled Deleted Cases");
	}

	/**
	 * Pushes deletions of tasks that have changed task type 
	 * since last sync to Google services.
	 * @param preupdatedTasks
	 */
	private void pushPreupdatedTasks(Stack<Task> preupdatedTasks) {
		// Delete tasks that were updated to a different type
		for (Task t: preupdatedTasks) {
			if (t.getId() != null) {
				con.deleteTask(t);
			}
		}
		logger.log(Level.INFO, "PUSH: Handled Preupdated Cases");
	}

	/**
	 * Pushes tasks that have been cleared since last sync to Google services.
	 * @param clearedTasks
	 */
	private void pushClearedTasks(Stack<ArrayList<Task>> clearedTasks) {
		// Delete tasks that were cleared
		for (ArrayList<Task> list : clearedTasks) {
			pushDeletedTasks(list);
		}
		logger.log(Level.INFO, "PUSH: Handled Cleared Cases");
	}

	//@author A0109194A
	/**
	 * Pulls tasks that have been changed since last sync to Google services.
	 * @param tasks
	 */
	private void pull() throws IOException {
		logger.log(Level.INFO, "PULL: Starting Pull");
		//Get all Tasks
		ArrayList<Task> tasksToSync = con.getAllTasks();
		ArrayList<Task> tasks = TaskCommander.data.getAllTasks();
		List<com.google.api.services.tasks.model.Task> googleTasks = con.getAllGoogleTasks(true);
		List<Event> googleEvents = con.getAllGoogleEvents(true);
		ArrayList<String> taskIds = TaskCommander.data.getAllIds();
		logger.log(Level.INFO, "PULL: Retrieved All Tasks");

		startSyncState(SyncState.PULL, getTotalTasks(tasksToSync, tasks, googleTasks, googleEvents));

		pullAddedCases(googleTasks, googleEvents, taskIds);
		pullUpdatedCases(tasksToSync);
		pullDeletedCases(googleTasks, googleEvents);

		logger.log(Level.INFO, "PULL: End Pull");
	}

	/**
	 * Pulls new tasks from Google services that have been
	 * added since last sync, and adds them locally.
	 * @param googleTasks
	 * @param googleEvents
	 * @param taskIds
	 */
	private void pullAddedCases(List<com.google.api.services.tasks.model.Task> googleTasks,
			List<Event> googleEvents, ArrayList<String> taskIds) {
		pullAddedCasesForTasks(googleTasks, taskIds);
		pullAddedCasesForCalendar(googleEvents, taskIds);
		logger.log(Level.INFO, "PULL: Handled Added Tasks");
	}

	/**
	 * Pulls new tasks from Google Calendar service that have been
	 * added since last sync, and adds them locally.
	 * @param googleEvents
	 * @param taskIds
	 */
	private void pullAddedCasesForCalendar(List<Event> googleEvents,
			ArrayList<String> taskIds) {
		if (googleEvents != null) {
			//For Events
			for (Event event: googleEvents) {
				if (!taskIds.contains(event.getId()) && !event.getStatus().equals(STATUS_CANCELLED)) {
					TaskCommander.data.addTask(con.toTask(event));
				}
				updateTasksComplete(tasksComplete+1);
			}
		}
	}

	/**
	 * Pulls new tasks from Google Tasks service that have been
	 * added since last sync, and adds them locally.
	 * @param googleTasks
	 * @param taskIds
	 */
	private void pullAddedCasesForTasks(
			List<com.google.api.services.tasks.model.Task> googleTasks,
			ArrayList<String> taskIds) {
		if (googleTasks != null) {
			for (com.google.api.services.tasks.model.Task task : googleTasks) {
				if (!taskIds.contains(task.getId()) && task.getDeleted() == null) {
					TaskCommander.data.addTask(con.toTask(task));
				}
				updateTasksComplete(tasksComplete+1);
			}
		}
	}

	/**
	 * Pulls tasks from Google services that have been
	 * updated since last sync, and updates them locally.
	 * @param tasksToSync
	 */
	private void pullUpdatedCases(ArrayList<Task> tasksToSync) {
		ArrayList<Task> tasks;
		ArrayList<String> taskIds;
		if (tasksToSync != null) {
			//Updated cases
			tasks = TaskCommander.data.getAllTasks();
			taskIds = TaskCommander.data.getAllIds();
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
		}
		logger.log(Level.INFO, "PULL: Handled Updated Cases");
	}

	/**
	 * Pulls tasks from Google services that have been
	 * deleted since last sync, and deletes them locally.
	 * @param googleTasks
	 * @param googleEvents
	 */
	private void pullDeletedCases(List<com.google.api.services.tasks.model.Task> googleTasks,
			List<Event> googleEvents) {
		pullDeletedCasesForTasks(googleTasks);
		pullDeletedCasesForCalendar(googleEvents);
	}

	/**
	 * Pulls tasks from Google Tasks service that have been
	 * deleted since last sync, and deletes them locally.
	 * @param googleTasks
	 */
	private void pullDeletedCasesForTasks(
			List<com.google.api.services.tasks.model.Task> googleTasks) {
		ArrayList<String> taskIds;
		if (googleTasks != null) {
			taskIds = TaskCommander.data.getAllIds();
			for (com.google.api.services.tasks.model.Task t : googleTasks) {
				if (t.getDeleted() != null) {
					int index = taskIds.indexOf(t.getId());
					if (index == -1) {
						continue;
					} else {
						TaskCommander.data.deleteFromGoogle(index);
					}
				}
				updateTasksComplete(tasksComplete+1);
			}
		}
		logger.log(Level.INFO, "PULL: Handled Deleted Google Tasks");
	}

	/**
	 * Pulls tasks from Google Calendar service that have been
	 * deleted since last sync, and deletes them locally.
	 * @param googleEvents
	 */
	private void pullDeletedCasesForCalendar(List<Event> googleEvents) {
		ArrayList<String> taskIds;
		if (googleEvents != null) {
			taskIds = TaskCommander.data.getAllIds();
			//Deleted Case For Events
			for (Event event : googleEvents) {
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
		}
		logger.log(Level.INFO, "PULL: Handled Deleted Google Events");
	}

	/** 
	 * Gets total number of tasks from Google services to update locally 
	 * @param tasksToSync
	 * @param tasks
	 * @param googleTasks
	 * @param googleEvents
	 * @return               Total number of tasks from Google services to update
	 */
	private int getTotalTasks(ArrayList<Task> tasksToSync,
			ArrayList<Task> tasks,
			List<com.google.api.services.tasks.model.Task> googleTasks,
			List<Event> googleEvents) {
		int total = 0;
		if (tasksToSync != null) {
			total += tasksToSync.size();
		}

		if (tasks != null) {
			total += tasks.size();
		}

		if (googleTasks != null) {
			total += googleTasks.size();
		}

		if (googleEvents != null) {
			total += googleEvents.size();
		}
		return total;

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
		case FAILED:
			message = MESSAGE_SYNC_FAILED;
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
