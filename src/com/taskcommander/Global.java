package com.taskcommander;
/**
 * This class stores global variables for use in the program.
 * 
 * @author Michelle Tan
 */
public class Global {
	
	public static final String MESSAGE_FILE_NOT_GIVEN = "No file given. Please enter a file name.";
	public static final String MESSAGE_FILE_NOT_FOUND = "File not found. Please enter a valid file name.";
	public static final String MESSAGE_WELCOME = "Welcome to TextBuddy. %1$s is ready for use. " + 
			"Type 'help' to see the list of commands.";
	public static final String MESSAGE_ADDED = "Added to %1$s: \"%2$s\"";
	public static final String MESSAGE_DELETED = "Deleted from %1$s: \"%2$s\"";
	public static final String MESSAGE_CLEARED = "All content deleted from %1$s";
	public static final String MESSAGE_INVALID_FORMAT = "Invalid command format: %1$s. " + 
			"Type 'help' to see the list of commands.";
	public static final String MESSAGE_NO_COMMAND = "No command given.";
	public static final String MESSAGE_NO_LINE = "No line given.";
	public static final String MESSAGE_NO_INDEX = "Index %1$s does not exist. Please type a valid index.";
	public static final String MESSAGE_EMPTY = "%1$s is empty";
	public static final String MESSAGE_HELP = "Commands: add <string>, display, delete <index of string>, clear, sort, search <word>, exit.";
	public static final String MESSAGE_LINE_FOUND = "Found \"%1$s\".";
	public static final String MESSAGE_LINE_NOT_FOUND = "The line \"%1$s\" does not exist.";
	public static final String MESSAGE_SORTED = "%1$s has been sorted.";

	public static final int INDEX_OFFSET = 1; // Difference between the array index and actual line number


	public static String fileName;
	// Possible command types
	public static enum CommandType {
		HELP,
		ADD, 
		DISPLAY, 
		DELETE,
		CLEAR,
		SEARCH,
		SORT,
		INVALID,
		EXIT
	};
}
