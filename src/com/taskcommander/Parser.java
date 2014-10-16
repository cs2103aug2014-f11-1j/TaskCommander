package com.taskcommander;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.joestelmach.natty.*;

/**
 * This class represents the Parser component. The Parser contains methods to parse the 
 * user's command and extract the command type and the related command parameters like index, 
 * name or dateTime of task.
 * 
 * @author A0128620M
 */

public class Parser {
	
	// Logging
	private static Logger logger = Logger.getLogger("Parser");
	
	/**
	 * This variable is initialized with the one and only instance of the Parser class 
	 * (see also getInstance() below)
	 */
	private static Parser theOne;
	
	/**
	 * This operation which returns either a new instance of the Parser or an existing one, if any.
	 * Therefore, it ensures that there will be only one instance of the Controller (see Singleton pattern)
	 */
	public static Parser getInstance(){
		if (theOne == null) {    
			theOne = new Parser();
		}
		return theOne;
	}
	
	/**
	 * Constructor
	 */
	private Parser(){
	}

	/**
	 * This operation determines which of the supported command types the user
	 * wants to perform.
	 * 
	 * @param userCommand
	 */
	public Global.CommandType determineCommandType(String userCommand) {
		if (userCommand == null) {
			throw new IllegalArgumentException(String.format(Global.MESSAGE_ARGUMENTS_NULL)); 
		}
		
		String commandTypeString = getFirstWort(userCommand);
		
		if (commandTypeString.equalsIgnoreCase("help")) {
			return Global.CommandType.HELP;
		} else if (commandTypeString.equalsIgnoreCase("add")) {
			return Global.CommandType.ADD;
		} else if (commandTypeString.equalsIgnoreCase("update")) {
			return Global.CommandType.UPDATE;
		} else if (commandTypeString.equalsIgnoreCase("done")) {
			return Global.CommandType.DONE;
		} else if (commandTypeString.equalsIgnoreCase("open")) {
			return Global.CommandType.OPEN;
		} else if (commandTypeString.equalsIgnoreCase("display")) {
			return Global.CommandType.DISPLAY;
		} else if (commandTypeString.equalsIgnoreCase("delete")) {
			return Global.CommandType.DELETE;
		} else if (commandTypeString.equalsIgnoreCase("clear")) {
			return Global.CommandType.CLEAR;
		} else if (commandTypeString.equalsIgnoreCase("sync")) {
			return Global.CommandType.SYNC;
		}else if (commandTypeString.equalsIgnoreCase("exit")) {
			return Global.CommandType.EXIT;
		} else {
			return Global.CommandType.INVALID;
		}
	}
	
	/**
	 * This operation determines the name of the task, which has to be written in quotation marks.
	 * Returns null pointer if no name found.
	 * 
	 * @param userCommand
	 */
	public String determineTaskName(String userCommand) {

		try {
			return userCommand.substring(userCommand.indexOf("\"") + 1,userCommand.lastIndexOf("\""));	// possible exception because of substring() when no " is found
		} catch (StringIndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * This operation determines the end date and/or start date of the task. If the user command
	 * contains a numeric command parameter like an index in second place, it will be removed before
	 * parsing in order to avoid mixing-up. Returns null pointer if no date found.
	 * 
	 * @param userCommand 
	 * @param existsCommandParameter e.g. the index of the command: update 2 "Call Boss" 3pm
	 */
	public List<Date> determineTaskDateTime(String userCommand, boolean existsCommandParameter) {
		
		String residualUserCommand;
		if (existsCommandParameter) {
			residualUserCommand = removeFirstWord(removeFirstWord(userCommand));
		} else {
			residualUserCommand = removeFirstWord(userCommand);
		}
		if (determineTaskName(userCommand) != null) {
			residualUserCommand = removeQuotedSubstring(residualUserCommand);
		}
		
		logger.log(Level.INFO, "ResidualUserCommand after removing commandType, commandParameter and TaskName if any: "+residualUserCommand);
		
		List<Date> dates = null;
		com.joestelmach.natty.Parser nattyParser = new com.joestelmach.natty.Parser();
		List<DateGroup> groups = nattyParser.parse(residualUserCommand);
		
		for(DateGroup group:groups) {
			dates = group.getDates();
		}
		return dates;
	}
	
	/**
	 * This operation determines the index which is provided with the update, delete, 
	 * done or open command and represents the position of the task within the recent display.
	 * Returns -1 if not found.
	 * 
	 * @param userCommand  
	 */
	public int determineIndex(String userCommand) {
		String indexString = getSecondWord(userCommand);

		int index;
		try {
			index = Integer.parseInt(indexString) - Global.INDEX_OFFSET; // Change the line number to an array index
		} catch (NumberFormatException e) {
			return -1;
		} 
		return index;
	}
	
	/**
	 * This operation determines if the user command contains the given String parameter,
	 * e.g. "none".
	 * 
	 * @param userCommand  
	 * @param parameter
	 */
	public boolean containsParameter(String userCommand, String parameter) {

		String residualUserCommand = userCommand;
		if (determineTaskName(userCommand) != null) {
			residualUserCommand = removeQuotedSubstring(residualUserCommand);
		}
		return residualUserCommand.contains(parameter);
	}
	
	/**
	 * Auxiliary String manipulating methods
	 */
	private String getFirstWort(String userCommand) {
		return userCommand.trim().split("\\s+")[0];
	}
	
	private String getSecondWord(String userCommand) {
		return userCommand.trim().split("\\s+")[1];
	}
	
	private  String removeFirstWord(String userCommand) {
		return userCommand.replaceFirst(getFirstWort(userCommand), "").trim();
	}
	
	private String removeQuotedSubstring(String userCommand) {
		if ( determineTaskName(userCommand) != null) {
			return userCommand.replaceFirst("\""+determineTaskName(userCommand)+"\"", "").trim();
		} else {
			return userCommand;
		}
	}
}
