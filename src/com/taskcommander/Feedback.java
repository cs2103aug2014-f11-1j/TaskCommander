package com.taskcommander;
import java.util.ArrayList;

/**
 * This class is used as a return type to the UI. It provides the UI with all the needed information 
 * depending on the type of command.
 * 
 * @author A0128620M
 */

public class Feedback {
	private boolean _wasSuccesfullyExecuted;
	private String _errorMessage;
	private Global.CommandType _commandType;
	private Task _commandRelatedTask;
	private ArrayList<Task> _commandRelatedTasks;
	
	// Feedback of add, update, delete command
	public Feedback(boolean wasSuccesfullyExecuted, Global.CommandType commandType, Task commandRelatedTask) { // TODO: handle wrong input parameter
		_wasSuccesfullyExecuted = wasSuccesfullyExecuted;
		_commandType = commandType;
		_commandRelatedTask = commandRelatedTask;
	}
	
	// Feedback of display command
	public Feedback(boolean wasSuccesfullyExecuted, Global.CommandType commandType, ArrayList<Task> commandRelatedTasks) { // TODO: handle wrong input parameter
		_wasSuccesfullyExecuted = wasSuccesfullyExecuted;
		_commandType = commandType;
		_commandRelatedTasks = commandRelatedTasks;
	}
	
	// Feedback of clear command
	public Feedback(boolean wasSuccesfullyExecuted, Global.CommandType commandType) { // TODO: handle wrong input parameter
		_wasSuccesfullyExecuted = wasSuccesfullyExecuted;
		_commandType = commandType;
	}
	
	// Feedback in case of an error
	public Feedback(boolean wasSuccesfullyExecuted, String errorMessage ) { // TODO: handle wrong input parameter
		_wasSuccesfullyExecuted = wasSuccesfullyExecuted;
		_errorMessage = errorMessage;
	}
	
	public boolean wasSuccesfullyExecuted() {
		return _wasSuccesfullyExecuted;
	}	
	
		public String getErrorMessage() {
		return _errorMessage;
	}
	
	public Global.CommandType getCommandType() {
		return _commandType;
	}	
	
	public Task getCommandRelatedTask() {
		return _commandRelatedTask;
	}	
	
	public ArrayList<Task> getCommandRelatedTasks() {
		return _commandRelatedTasks;
	}
	
}
