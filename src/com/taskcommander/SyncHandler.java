package com.taskcommander;

import java.util.ArrayList;

import com.taskcommander.Task.TaskType;

//@author A0112828H and A0109194A
public class SyncHandler {

	private static GoogleAPIConnector con = null;

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
		pull();
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
	
	private void pull(){
		//Handle Added Cases
		ArrayList<com.taskcommander.Task> toSync = con.getAllTasks();
		ArrayList<String> idList = TaskCommander.data.getAllIds();
		for (Task task : toSync) {
			if (!containsId(task.getId(), idList)) {
				if (task.getType() == TaskType.DEADLINE) {
					TaskCommander.data.addTask(task.getType(), task.getName(),
							null, ((DeadlineTask) task).getEndDate());
				} else if (task.getType() == TaskType.FLOATING) {
					TaskCommander.data.addTask(task.getType(), task.getName(), 
							null, null);
				} else {
					TaskCommander.data.addTask(task.getType(), task.getName(), 
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
	
	private boolean containsId(String id, ArrayList<String> idList) {
		if (idList.contains(id)) {
			return true;
		} else {
			return false;
		}
	}
}
