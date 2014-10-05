package com.taskcommander;
/**
 * This class stores global variables for use in the program.
 * 
 * @author Michelle, Andreas, Sean Saito
 */
public class Global {
	
	public static final String APPLICATION_NAME = "Task Commander";
	
	public static final String MESSAGE_FILE_NOT_GIVEN = "File not given. Please enter a valid file name.";
	public static final String MESSAGE_FILE_NOT_FOUND = "File not found. Please enter a valid file name.";
	public static final String MESSAGE_WELCOME = "Welcome to TaskCommander.";
	public static final String MESSAGE_ADDED = "Added: %1$s";
	public static final String MESSAGE_UPDATED = "Updated: \"%1$s\"";
	public static final String MESSAGE_DELETED = "Deleted: \"%1$s\"";
	public static final String MESSAGE_CLEARED = "All content deleted.";
	public static final String MESSAGE_INVALID_FORMAT = "Invalid command format: %1$s. " + 
			"Type 'help' to see the list of commands.";
	public static final String MESSAGE_NO_COMMAND = "No command given.";
	public static final String MESSAGE_NO_TASK = "No task given.";
	public static final String MESSAGE_NO_INDEX = "Index %1$s does not exist. Please type a valid index.";
	public static final String MESSAGE_EMPTY = "No tasks available";
	public static final String MESSAGE_HELP = "Commands: add <string>, display, delete <index of string>, clear, sort, exit.";
	public static final String MESSAGE_LINE_FOUND = "Found \"%1$s\".";
	public static final String MESSAGE_LINE_NOT_FOUND = "The line \"%1$s\" does not exist.";
	public static final String MESSAGE_SORTED = "Tasks has been sorted.";
	public static final String MESSAGE_FILE_COULD_NOT_BE_WRITTEN = "Error: The File could not be written.";
	public static final String MESSAGE_FILE_COULD_NOT_BE_LOADED = "Error: The File could not be loaded.";
	public static final String MESSAGE_EXCEPTION_IO = "Unable to read the data retrieved.";
	public static final String MESSAGE_ARGUMENTS_NULL = "Null arguments given.";

	public static final int INDEX_OFFSET = 1; // Difference between the array index and actual line number

	// Possible command types
	public static enum CommandType {
		HELP,
		ADD, 
		UPDATE,
		DISPLAY, 
		DELETE,
		CLEAR,
		SORT,
		INVALID,
		SYNC,
		EXIT
	};
	
	// Name of Storage File
	public static String fileName;
}
