package com.taskcommander;
import java.util.Date;
import java.util.List;

import com.joestelmach.natty.*;

/**
 * This class contains methods to extract the commandType, taskName or taskDateTime 
 * of the command entered by the user.
 * 
 * @author Andreas Christian Mayr
 */

public class Parser {
	
	/**
	 * Constructor
	 */
	public Parser(){
	}

	/**
	 * This operation determines which of the supported command types the user
	 * wants to perform.
	 * 
	 * @param userCommand  user command
	 */
	public Global.CommandType determineCommandType(String userCommand) {
		
		String commandTypeString = getFirstWord(userCommand);
		
		if (commandTypeString == null) {
			throw new Error("command type string cannot be null!");
		}

		if (commandTypeString.equalsIgnoreCase("help")) {
			return Global.CommandType.HELP;
		} else if (commandTypeString.equalsIgnoreCase("add")) {
			return Global.CommandType.ADD;
		} else if (commandTypeString.equalsIgnoreCase("update")) {
			return Global.CommandType.UPDATE;
		} else if (commandTypeString.equalsIgnoreCase("display")) {
			return Global.CommandType.DISPLAY;
		} else if (commandTypeString.equalsIgnoreCase("delete")) {
			return Global.CommandType.DELETE;
		} else if (commandTypeString.equalsIgnoreCase("clear")) {
			return Global.CommandType.CLEAR;
		} else if (commandTypeString.equalsIgnoreCase("exit")) {
			return Global.CommandType.EXIT;
		} else {
			return Global.CommandType.INVALID;
		}
	}
	
	/**
	 * This operation determines the name, that is to say the description of the task.
	 * 
	 * @param userCommand  user command
	 */
	public String determineTaskName(String userCommand) throws StringIndexOutOfBoundsException{
		return userCommand.substring(userCommand.indexOf("\"") + 1,userCommand.lastIndexOf("\""));  // possible exception because of substring() when no " is found
	}

	/**
	 * This operation determines the endTime and/or startTime of the task.
	 * 
	 * @param userCommand  user command
	 */
	public List<Date> determineTaskDateTime(String userCommand) {
		List<Date> dates = null;
		String dateTime = userCommand.substring(userCommand.lastIndexOf("\"")+1).trim();
		
		com.joestelmach.natty.Parser nattyParser = new com.joestelmach.natty.Parser();
		List<DateGroup> groups = nattyParser.parse(dateTime);
		
		for(DateGroup group:groups) {
			dates = group.getDates();
		}
		return dates;
	}
	
	
	// Helper methods
	private static String getFirstWord(String userCommand) {
		return userCommand.trim().split("\\s+")[0];
	}
}
