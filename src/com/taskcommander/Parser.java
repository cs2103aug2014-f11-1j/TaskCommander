package com.taskcommander;

/**
 * This class parses a given String into commands.
 * 
 * @author Michelle Tan
 */
public class Parser {
	
	public Parser(){
	}
	
	
	/**
	 * This operation determines which of the supported command types the user
	 * wants to perform.
	 * 
	 * @param commandTypeString  First word of the user command.
	 */
	public Global.CommandType determineCommandType(String commandTypeString) {
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

}
