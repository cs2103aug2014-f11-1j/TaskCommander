package com.taskcommander;
import java.util.ArrayList;

/**
 * This class is used as a return type of the Logic to the UI. If the command was executed successfully, 
 * the Feedback contains the related Task (in case of the add, update, delete, done, open command),
 * tasks (in case of a display command) or nothing else (in case of the clear command). If the command 
 * wasn't executed successfully, the Feedback contains the respective error message instead.
 * 
 * @author A0128620M
 * 
 * TODO: might create subclasses for each case instead of using several constructors
 */

public class Feedback {
	
	private boolean _wasSuccesfullyExecuted;
	private Global.CommandType _commandType;
	private Task _commandRelatedTask;
	private ArrayList<Task> _commandRelatedTasks;	
	private String _errorMessage;
	
	public enum FeedbackType {
		STRING,
		TASKS
	}
	
	/**
	 * Constructor for the add, update, delete, done and open command.
	 * 
	 * @param	wasSuccesfullyExecuted
	 * @param	commandType			added, updated, deleted, done or open
	 * @param	commandRelatedTask	task which was added, updated, deleted, marked as done or open
	 */
	public Feedback(boolean wasSuccesfullyExecuted, Global.CommandType commandType, Task commandRelatedTask) {
		if (commandType == null || commandRelatedTask == null) {
			throw new IllegalArgumentException(String.format(Global.MESSAGE_ARGUMENTS_NULL)); 
		} else {
			_wasSuccesfullyExecuted = wasSuccesfullyExecuted;
			_commandType = commandType;
			_commandRelatedTask = commandRelatedTask;
		}
	}
	
	/**
	 * Constructor for the display command
	 * 
	 * @param	wasSuccesfullyExecuted
	 * @param	commandType			display
	 * @param	commandRelatedTasks	tasks to be displayed
	 */
	public Feedback(boolean wasSuccesfullyExecuted, Global.CommandType commandType, ArrayList<Task> commandRelatedTasks) {
		if (commandType == null || commandRelatedTasks == null) {
			throw new IllegalArgumentException(String.format(Global.MESSAGE_ARGUMENTS_NULL)); 
		} else {
			_wasSuccesfullyExecuted = wasSuccesfullyExecuted;
			_commandType = commandType;
			_commandRelatedTasks = commandRelatedTasks;
		}
	}
	
	/**
	 * Constructor for clear command.
	 * 
	 * @param	wasSuccesfullyExecuted
	 * @param	commandType			clear
	 */
	public Feedback(boolean wasSuccesfullyExecuted, Global.CommandType commandType) { 
		if (commandType == null) {
			throw new IllegalArgumentException(String.format(Global.MESSAGE_ARGUMENTS_NULL)); 
		} else {
			_wasSuccesfullyExecuted = wasSuccesfullyExecuted;
			_commandType = commandType;
		}
	}
	
	/**
	 * Constructor for error messages.
	 * 
	 * @param	wasSuccesfullyExecuted
	 * @param	errorMessage
	 */
	public Feedback(boolean wasSuccesfullyExecuted, String errorMessage ) {
		if (errorMessage == null) {
			throw new IllegalArgumentException(String.format(Global.MESSAGE_ARGUMENTS_NULL)); 
		} else {
			_wasSuccesfullyExecuted = wasSuccesfullyExecuted;
			_errorMessage = errorMessage;
		}
	}
	
	public boolean wasSuccesfullyExecuted() {
		return _wasSuccesfullyExecuted;
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
		
	public String getErrorMessage() {
		return _errorMessage;
	}
}
