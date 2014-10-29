package com.taskcommander;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.joestelmach.natty.*;

/**
 * This class represents the Parser, a subcomponent of the Logic component. The Parser analyzes the user's
 * input and makes meaning out of it. Therefore, the Parser provides several methods to parse the user's
 * command and extract the command type and its related command parameters like index, name or date among others.
 * 
 * @author A0128620M
 */

public class Parser {
	
	/* 
	 * Logger and related logging messages
	 */
	private static Logger logger = Logger.getLogger(Parser.class.getName());
	private static final String MESSAGE_NO_COMMANDTYPE = "No command type found.";
	private static final String MESSAGE_NO_TASKNAME = "No task name found.";
	private static final String MESSAGE_NO_INDEX = "No index found.";
	private static final String MESSAGE_NO_DATETIMES = "No dateTimes found.";
	
	/*
	 * This variable is initialized with the one and only instance of the Parser class.
	 */
	private static Parser theOne;
	
	/*
	 * Private Constructor, only called by the getInstance() method.
	 */
	private Parser(){
	}
	
	/* ============================================ API ============================================= */
	
	/**
	 * This operation returns either a new instance of the Parser or an existing one, if any.
	 * In doing so, it ensures that there will be only one instance of the Controller (Singleton pattern).
	 */
	public static Parser getInstance(){
		if (theOne == null) {    
			theOne = new Parser();
		}
		return theOne;
	}

	/**
	 * This operation determines which of the supported command types the user wants to perform
	 * (not case sensitive).
	 * 
	 * @param	user command
	 * @return	type of command 
	 */
	public Global.CommandType determineCommandType(String userCommand) {
		if (userCommand == null || userCommand.equals("")) {
			logger.log(Level.WARNING, Global.MESSAGE_ILLEGAL_ARGUMENTS);
			return Global.CommandType.INVALID;
		}
		
		String commandTypeString;
	
		try {
			commandTypeString = getFirstWord(userCommand);
		} catch (Exception e) {
			logger.log(Level.INFO, MESSAGE_NO_COMMANDTYPE, e);
			return Global.CommandType.INVALID;
		}
		
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
		} else if (commandTypeString.equalsIgnoreCase("delete")) {
			return Global.CommandType.DELETE;		
		} else if (commandTypeString.equalsIgnoreCase("clear")) {
			return Global.CommandType.CLEAR;	
		} else if (commandTypeString.equalsIgnoreCase("display")) {
			return Global.CommandType.DISPLAY;
		} else if (commandTypeString.equalsIgnoreCase("search")) {
			return Global.CommandType.SEARCH;
		} else if (commandTypeString.equalsIgnoreCase("sync")) {
			return Global.CommandType.SYNC;
		} else if (commandTypeString.equalsIgnoreCase("undo")) {
			return Global.CommandType.UNDO;
		}else if (commandTypeString.equalsIgnoreCase("exit")) {
			return Global.CommandType.EXIT;
		} else {
			return Global.CommandType.INVALID;
		}
	}
	
	/**
	 * This operation determines the name of the task which has to be put in quotation marks.
	 * Returns null if name not found.
	 * 
	 * @param 	user command
	 * @return	name of task
	 */
	public String determineTaskName(String userCommand) {
		if (userCommand == null || userCommand.equals("")) {
			logger.log(Level.WARNING, Global.MESSAGE_ILLEGAL_ARGUMENTS);
			return null;
		}
		
		try {
			return getQuotedSubstring(userCommand);
		} catch (Exception e) {
			logger.log(Level.INFO, MESSAGE_NO_TASKNAME, e);
			return null;
		}
	}

	/**
	 * This operation determines the end date and/or start date of the stated task within given command string. 
	 * Returns null if no date found. ( Remark: If the user command contains a numeric command parameter 
	 * like an index in second place, it will be removed before parsing the dates and times in order to avoid mixing-up.)
	 * 
	 * @param 	user command
	 * @return	dateTime(s) of task
	 */
	public List<Date> determineTaskDateTime(String userCommand) {
		if (userCommand == null || userCommand.equals("")) {
			logger.log(Level.WARNING, Global.MESSAGE_ILLEGAL_ARGUMENTS);
			return null;
		}
		
		String userCommandWithoutIndex;
		Global.CommandType commandType = TaskCommander.parser.determineCommandType(userCommand);
	
		if (commandType.equals(Global.CommandType.UPDATE) | commandType.equals(Global.CommandType.DONE) | commandType.equals(Global.CommandType.OPEN) | commandType.equals(Global.CommandType.DELETE)) {
			try {
				userCommandWithoutIndex = removeSecondWord(userCommand);
			} catch (Exception e) {
				logger.log(Level.INFO, MESSAGE_NO_INDEX, e);
				return null;
			}
		} else {
			userCommandWithoutIndex = userCommand;
		}
		
		try {
			return getDateTimes(userCommandWithoutIndex);
		} catch (Exception e) {
			logger.log(Level.INFO, MESSAGE_NO_DATETIMES, e);
			return null;
		}
	}
	
	/**
	 * This operation determines the index which is provided with the update, delete, 
	 * done or open command and represents the position of the task within the recently displayed task table.
	 * Returns -1 if not found or below one.
	 * 
	 * @param 	user command
	 * @return	index of task
	 */
	public int determineIndex(String userCommand) {
		if (userCommand == null || userCommand.equals("")) {
			logger.log(Level.WARNING, Global.MESSAGE_ILLEGAL_ARGUMENTS);
			return -1;
		}
		
		try {
			String indexString = getSecondWord(userCommand);
			int indexInteger = Integer.parseInt(indexString); 
			if (indexInteger > 0) {
				return indexInteger;
			} else {
				return -1;
			}
		} catch (Exception e) {
			logger.log(Level.INFO, MESSAGE_NO_INDEX, e);
			return -1;
		} 
	}
	
	/**
	 * This operation determines the single words and phrases one searches for and returns them as an ArrayList.
	 * Returns null if no word or phrase found.
	 * 
	 * @param 	user command
	 * @return	ArrayList containing the searched words and phrases
	 */
	public ArrayList<String> determineSearchedWords(String userCommand) {
		if (userCommand == null || userCommand.equals("")) {
			logger.log(Level.WARNING, Global.MESSAGE_ILLEGAL_ARGUMENTS);
			return null;
		}
		
		String userCommandWithoutCommandType;
	
		try {
			userCommandWithoutCommandType = removeFirstWord(userCommand);
		} catch (Exception e) {
			logger.log(Level.INFO, MESSAGE_NO_COMMANDTYPE, e);
			return null;
		}

		ArrayList<String> searchedWords = new ArrayList<String>();
		Pattern pattern = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"");
		Matcher matcher = pattern.matcher(userCommandWithoutCommandType);
		
		while (matcher.find()) {
		    if (matcher.group(1) != null) {
		    	// phrase in double quotes
		    	searchedWords.add(matcher.group(1));
		    } else {
		    	// single word
		    	searchedWords.add(matcher.group());
		    }
		}
		return searchedWords;
	}
	
	/**
	 * This operation determines if the user command contains exactly the given string, e.g. "none", "timed", "deadline"
	 * (case sensitive).
	 * 
	 * @param 	string
	 * @param 	parameter which is looked for
	 * @return	true if found, false if not
	 */
	public boolean containsParameter(String userCommand, String parameter) {	
		if (userCommand == null || userCommand.equals("")|| parameter == null || parameter.equals("")) {
			logger.log(Level.WARNING, Global.MESSAGE_ILLEGAL_ARGUMENTS);
			return false;
		}
		String userCommandWithoutTaskName;

		try {
			userCommandWithoutTaskName = removeQuotedSubstring(userCommand);
		} catch (Exception e) {
			logger.log(Level.INFO, MESSAGE_NO_TASKNAME, e);
			userCommandWithoutTaskName = userCommand;
		}
		
		return userCommandWithoutTaskName.matches(".*\\b" + parameter + "\\b.*");
	}	
	
	/* ================================ General auxiliary methods =================================== */
	
	/**
	 * This operation returns the first word of the given string.
	 * 
	 * @param 	string
	 * @return	first word
	 */
	private String getFirstWord(String str) throws Exception {
		return str.trim().split("\\s+")[0];	
	}
	
	/**
	 * This operation returns the second word of the given string.
	 * 
	 * @param 	string
	 * @return	second word
	 */
	private String getSecondWord(String str) throws Exception {
		return str.trim().split("\\s+")[1];	

	}
	
	/**
	 * This operation gets the quoted substring within the given string. 
	 * 
	 * @param 	string
	 * @return	quoted substring
	 */
	private String getQuotedSubstring(String str) throws Exception  {
		return str.substring(str.indexOf("\"") + 1,str.lastIndexOf("\"")).trim();
	}
	
	/**
	 * This operation gets dateTimes within the given string. 
	 * 
	 * @param 	string
	 * @return	dateTimes list
	 */
	private List<Date> getDateTimes(String str)  throws Exception {
		List<Date> dates = null;

		com.joestelmach.natty.Parser nattyParser = new com.joestelmach.natty.Parser();
		List<DateGroup> groups = nattyParser.parse(str);
		
		for(DateGroup group:groups) {
			dates = group.getDates();
		}
		return dates;
	}
	
	/**
	 * This operation removes the first word of the given string. 
	 * 
	 * @param 	string
	 * @return	string without first word
	 */
	private  String removeFirstWord(String str) throws Exception {
		return str.replaceFirst(getFirstWord(str), "").trim();
	}
	
	/**
	 * This operation removes the second word of the given string. 
	 * 
	 * @param 	string
	 * @return	string without second word
	 */
	private  String removeSecondWord(String str)  throws Exception {
		return str.replaceFirst(getSecondWord(str), "");
	}
	
	/**
	 * This operation removes the quoted substring within the given string. It is assumed that all quotation marks 
	 * between the first quote and the last quote belong to the substring.
	 * 
	 * @param 	string
	 * @return	string without quoted substring
	 */
	private String removeQuotedSubstring(String str)  throws Exception {
		return str.replaceFirst(str.substring(str.indexOf("\"") + 1,str.lastIndexOf("\"")), "");
	}
}
