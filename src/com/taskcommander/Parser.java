package com.taskcommander;
import java.util.ArrayList;
import java.util.Calendar;
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

	// Constructor, Variables and Logger
	private static Logger logger = Logger.getLogger(Parser.class.getName());
	private static final String MESSAGE_NO_COMMANDTYPE = "No command type found.";
	private static final String MESSAGE_NO_TASKNAME = "No task name found.";
	private static final String MESSAGE_NO_INDEX = "No index found.";
	private static final String MESSAGE_NO_DATETIMES = "No dateTimes found.";

	//@author A0128620M
	// Singleton instance for Data
	private static Parser theOne;

	private Parser(){
	}

	/**
	 * Returns the only instance of Parser.
	 * @return  Parser instance.
	 */
	public static Parser getInstance(){
		if (theOne == null) {    
			theOne = new Parser();
		}
		return theOne;
	}

	/**
	 * Determines a supported command type from given String
	 * (not case sensitive).
	 * @param	userCommand 
	 * @return	             Command type
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
	 * Determines the name of the task which has to be put in quotation marks.
	 * Returns null if name not found.
	 * @param	userCommand 
	 * @return	             Task name
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
	 * Determines the end date and/or start date of the stated task within given command string. 
	 * Returns null if no date found. 
	 * 
	 * If the user command contains a numeric command parameter like an index in second place, 
	 * it will be removed before parsing the dates and times in order to avoid mix ups.
	 * 
	 * @param	userCommand 
	 * @return	             DateTimes of task
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

		List<Date> dateTimes;
		try {
			dateTimes = getDateTimes(userCommandWithoutIndex);
		} catch (Exception e) {
			logger.log(Level.INFO, MESSAGE_NO_DATETIMES, e);
			return null;
		}

		if (dateTimes != null && dateTimes.size() == 2) {	// if recognized endDate would be before the startDate; that's the case when the endDate is not on the same day as the startDate.
			Date startDate = dateTimes.get(0);
			Date endDate = dateTimes.get(1);
			if ( endDate.compareTo(startDate) < 0 ) {
				Calendar c = Calendar.getInstance(); 
				c.setTime(endDate); 
				c.add(Calendar.DATE, 1);
				endDate = c.getTime();
				dateTimes.set(1, endDate);
			}
		}
		return dateTimes;
	}

	/**
	 * Determines the index which is provided with the update, delete, done or open command 
	 * and represents the position of the task within the recently displayed task table.
	 * Returns -1 if not found or if the index < 1.
	 * @param	userCommand 
	 * @return	             Task index
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
	 * Determines the single words and phrases one searches for and returns them as an ArrayList.
	 * Returns null if no word or phrase found.
	 * @param	userCommand 
	 * @return	             ArrayList containing the searched words and phrases
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
	 * Determines if the user command contains exactly the given string, e.g. "none", "timed", "deadline"
	 * (case sensitive).
	 * @param	userCommand 
	 * @param	parameter    Search keyword
	 * @return	             If user command contains given String
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

	// Helper methods
	private String getFirstWord(String str) throws Exception {
		return str.trim().split("\\s+")[0];	
	}

	private String getSecondWord(String str) throws Exception {
		return str.trim().split("\\s+")[1];	

	}

	private String getQuotedSubstring(String str) throws Exception  {
		return str.substring(str.indexOf("\"") + 1,str.lastIndexOf("\"")).trim();
	}

	/**
	 * Returns dateTimes within the given string. 
	 * @param 	str
	 * @return	       DateTimes in given string
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

	private  String removeFirstWord(String str) throws Exception {
		return str.replaceFirst(getFirstWord(str), "").trim();
	}

	private  String removeSecondWord(String str)  throws Exception {
		return str.replaceFirst(getSecondWord(str), "");
	}

	/**
	 * Removes the quoted substring within the given string. 
	 * It is assumed that all quotation marks between the first quote 
	 * and the last quote belong to the substring.
	 * @param 	str
	 * @return	      String without quoted substring
	 */
	private String removeQuotedSubstring(String str)  throws Exception {
		return str.replaceFirst(str.substring(str.indexOf("\"") + 1,str.lastIndexOf("\"")), "");
	}
}
