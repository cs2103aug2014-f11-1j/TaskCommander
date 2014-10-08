package com.taskcommander;
import java.util.ArrayList;

/**
 * This class is used to return the commandType and the related tasks to the UI.
 * The UI then can display the command's result in a specific format.
 * 
 * E.g. when a task is added, the feedback object will contain the _commandType ADD and
 * _commandRelatedTasks.get(0) will be the added task.
 * In additioon, when the tasks should be displayed, the feedback object will consist of a _commandType DISPLAY
 * and _commandRelatedTasks array which contains all the related tasks.
 * 
 * @author Andreas Christian Mayr
 */


public class FeedbackToUI {
	private Global.CommandType _commandType;
	private ArrayList<Task> _commandRelatedTasks;

	public FeedbackToUI(Global.CommandType commandType, ArrayList<Task> commandRelatedTasks) {
		if (commandType != null && commandRelatedTasks != null) {
			_commandType = commandType;
			_commandRelatedTasks = commandRelatedTasks;
		} else {
			throw new IllegalArgumentException();
		}
	}
}
