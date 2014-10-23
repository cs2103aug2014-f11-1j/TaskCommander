package com.taskcommander;

import com.google.api.services.calendar.model.Event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
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

	private static GoogleAPIConnector con = null;
	private static final Logger logger = Logger.getLogger(SyncHandler.class.getName());

	private static final String MESSAGE_SYNC_PUSH = "Sending data to Google... %1$s/%2$s completed.";
	private static final String MESSAGE_SYNC_PULL = "Getting data from Google... %1$s/%2$s completed.";
	private static final String MESSAGE_SYNC_DONE = "Sync completed.";

	public int tasksTotal;
	public int tasksComplete;
	public SyncState syncState;

	public SyncHandler() {

	}

	/**
	 * Syncs the tasks in memory to Google. Checks if
	 * tasks have been edited since previous sync and
	 * executes respective function.
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
			@Override
			public void run() {
				while (con.getAllTasks() == null) {
					try {
						sleep(10);  // milliseconds
					} catch (InterruptedException e) {
						logger.log(Level.WARNING, "Error while trying to sleep in SyncHandler", e);
					}
				}

				push();
				try {
					pull();
				} catch (Exception e) {
					logger.log(Level.WARNING, Global.MESSAGE_SYNC_FAILED, e);
				}
				resetSyncState();
			}
		};
		thread.setUncaughtExceptionHandler(h);
		thread.start();

		return Global.MESSAGE_SYNC_IN_PROGRESS;
	}

	private void push() {
		ArrayList<Task> tasks = TaskCommander.data.getAllTasks();
		logger.log(Level.INFO, "PUSH: Retrieved All Tasks");
		startSyncState(SyncState.PUSH, tasks.size());
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
			updateTasksComplete(tasksComplete+1);
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

	//@author A0109194A
	private void pull() throws IOException {
		//Get all Tasks
		ArrayList<Task> tasksToSync = con.getAllTasks();
		logger.log(Level.INFO, "PULL: Retrieved All Tasks");
		startSyncState(SyncState.PULL, tasksToSync.size());
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
			if (task.getDeleted() != null) {
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
					break;
				case TIMED:
					TaskCommander.data.updateToTimedTask(index, (TimedTask) t);
					break;
				case DEADLINE:
					TaskCommander.data.updateToDeadlineTask(index, (DeadlineTask) t);
					break;
				}		
			}
		}
		logger.log(Level.INFO, "PULL: Handled Updated Cases");
	}

	//@author A0112828H
	private void updateTasksComplete(int completed) {
		tasksComplete = completed;
		updateSyncMessage();
	}

	private void startSyncState(SyncState state, int total) {
		syncState = state;
		tasksTotal = total;
		tasksComplete = 0;
		updateSyncMessage();
	}

	private void resetSyncState() {
		Global.syncing = false;
		syncState = SyncState.DONE;
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
			message = String.format(MESSAGE_SYNC_PUSH, tasksComplete, tasksTotal);
			break;
		case PULL:
			message = String.format(MESSAGE_SYNC_PULL, tasksComplete, tasksTotal);
			break;
		}

		setChanged();
		notifyObservers(message);
	}
}
