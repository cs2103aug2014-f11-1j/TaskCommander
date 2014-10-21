package com.taskcommander;
import java.text.SimpleDateFormat;

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
	public static final String MESSAGE_UPDATED = "Updated: %1$s";
	public static final String MESSAGE_DONE = "Done: %1$s";
	public static final String MESSAGE_ALREADY_DONE = "Already done.";
	public static final String MESSAGE_OPEN = "Opened: %1$s";
	public static final String MESSAGE_ALREADY_OPEN = "Already opened.";
	public static final String MESSAGE_DELETED = "Deleted: %1$s";
	public static final String MESSAGE_CLEARED = "All content deleted.";
	public static final String MESSAGE_DISPLAYED = "Displayed: %1$s";
	public static final String MESSAGE_SEARCHED= "Searched for: %1$s";
	public static final String ERROR_MESSAGE_INVALID_FORMAT = "Invalid command format: %1$s. " + "Type 'help' to see the list of commands.";
	public static final String ERROR_MESSAGE_NO_COMMAND = "No command given.";
	public static final String ERROR_MESSAGE_NO_INDEX = "Index %1$s does not exist. Please type a valid index.";
	public static final String MESSAGE_EMPTY = "No tasks available";
	public static final String MESSAGE_HELP = "Commands: add ¡°<task title>¡± <date> <end time>,\n display [timed] [deadline] [floating] [done|open] [date] [start time] [end time],\n open <index>, done <index>, delete <index of string>, clear, sort, exit.";
	public static final String MESSAGE_LINE_FOUND = "Found \"%1$s\".";
	public static final String MESSAGE_LINE_NOT_FOUND = "The line \"%1$s\" does not exist.";
	public static final String MESSAGE_SORTED = "Tasks has been sorted.";
	public static final String MESSAGE_SYNC_SUCCESS = "Successfully synced to Google!";
	
	public static final String MESSAGE_FILE_COULD_NOT_BE_WRITTEN = "Error: The File could not be written.";
	public static final String MESSAGE_FILE_COULD_NOT_BE_LOADED = "Error: The File could not be loaded.";
	public static final String MESSAGE_EXCEPTION_IO = "Unable to read the data retrieved.";
	public static final String MESSAGE_ARGUMENTS_NULL = "Null arguments given.";
	public static final String MESSAGE_ARGUMENTS_INVALID = "Invalid arguments given.";
	public static final String MESSAGE_INVALID_SYNC_TOKEN = "Invalid sync token, clearing event store and re-syncing.";
	public static final String MESSAGE_NO_NEW_SYNC = "No new events to sync.";
	public static final String MESSAGE_FULL_SYNC = "Peforming full sync.";
	public static final String MESSAGE_INCREMENTAL_SYNC = "Performing incremental sync.";
	public static final String MESSAGE_COMPLETED_SYNC = "Sync complete.";
	public static final String MESSAGE_FAILED_PULL = "Could not pull data";
	
	public static final int INDEX_OFFSET = 1; // Difference between the array index and actual line number

	/**
	 *  Global Date Format
	 *  @author A0128620M
	 */
	public static final SimpleDateFormat dayFormat = new SimpleDateFormat("EEE MMM d ''yy");
	public static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");


	// Possible command types
	public static enum CommandType {
		HELP,
		ADD, 
		UPDATE,
		DONE,
		OPEN,
		DELETE,
		DISPLAY, 
		SEARCH,
		CLEAR,
		UNCLEAR,
		INVALID,
		SYNC,
		UNDO,
		EXIT
	};
	
	// Name of Storage File
	public static String fileName;
}
