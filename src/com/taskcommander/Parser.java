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

//@author A0128620M
/**
 * Analyzes the user's input and extracts values. Provides several methods to
 * parse the user's command and extract the command type and its related command
 * parameters like index, name or date, etc.
 */
public class Parser {
	private static Logger logger = Logger.getLogger(Parser.class.getName());

	private static final String MESSAGE_NO_COMMANDTYPE = "User command doesn't contain a command type.";
	private static final String MESSAGE_NO_TASKNAME = "User command doesn't contain a task name.";
	private static final String MESSAGE_NO_INDEX = "User command doesn't contain an index.";
	private static final String MESSAGE_NO_DATETIMES = "User command doesn't contain any dateTimes.";
	private static final int INVALID_INDEX = -1;

	// Singleton instance for Data
	private static Parser theOne;

	private Parser() {
	}

	/**
	 * Returns the only instance of Parser.
	 * 
	 * @return Parser instance
	 */
	public static Parser getInstance() {
		if (theOne == null) {
			theOne = new Parser();
		}
		return theOne;
	}

	/**
	 * Determines a supported command type from given String (not case
	 * sensitive).
	 * 
	 * @param userCommand
	 * @return commandType
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
			logger.log(Level.INFO, MESSAGE_NO_COMMANDTYPE);
			return Global.CommandType.INVALID;
		}

		if (commandTypeString.equalsIgnoreCase("add")) {
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
		} else if (commandTypeString.equalsIgnoreCase("exit")) {
			return Global.CommandType.EXIT;
		} else {
			return Global.CommandType.INVALID;
		}
	}

	/**
	 * Determines the name of the task which has to be put in quotation marks.
	 * Returns null if name not found.
	 * 
	 * @param userCommand
	 * @return taskName
	 */
	public String determineTaskName(String userCommand) {
		if (userCommand == null || userCommand.equals("")) {
			logger.log(Level.WARNING, Global.MESSAGE_ILLEGAL_ARGUMENTS);
			return null;
		}

		String userCommandWithoutCommandType = removeCommandType(userCommand);

		try {
			return getQuotedSubstring(userCommandWithoutCommandType);
		} catch (Exception e) {
			logger.log(Level.INFO, MESSAGE_NO_TASKNAME);
			return null;
		}
	}

	/**
	 * Determines the end date and/or start date of the stated task within given
	 * command string. Returns null if no date found. If the user command
	 * contains a numeric command parameter like an index in second place, it
	 * will be removed before parsing the dates and times in order to avoid mix
	 * ups.
	 * 
	 * @param userCommand
	 * @return dateTimes
	 */
	public List<Date> determineTaskDateTime(String userCommand) {
		if (userCommand == null || userCommand.equals("")) {
			logger.log(Level.WARNING, Global.MESSAGE_ILLEGAL_ARGUMENTS);
			return null;
		}

		String userCommandWithoutIndex = removeIndex(userCommand);
		String userCommandWithoutCommandType = removeCommandType(userCommandWithoutIndex);
		String userCommandWithoutTaskNameAndCommandType = removeTaskName(userCommandWithoutCommandType);
		List<Date> dateTimes;

		try {
			dateTimes = getDateTimes(userCommandWithoutTaskNameAndCommandType);
		} catch (Exception e) {
			logger.log(Level.INFO, MESSAGE_NO_DATETIMES);
			return null;
		}
		dateTimes = checkOrderOfDateTimes(dateTimes);
		return dateTimes;
	}

	/**
	 * Determines the index which is provided with the update, delete, done or
	 * open command. Returns INVALID_INDEX if not found.
	 * 
	 * @param userCommand
	 * @return index
	 */
	public int determineIndex(String userCommand) {
		if (userCommand == null || userCommand.equals("")) {
			logger.log(Level.WARNING, Global.MESSAGE_ILLEGAL_ARGUMENTS);
			return INVALID_INDEX;
		}

		try {
			String indexString = getSecondWord(userCommand);
			int indexInteger = Integer.parseInt(indexString);
			return indexInteger;
		} catch (Exception e) {
			logger.log(Level.INFO, MESSAGE_NO_INDEX);
			return INVALID_INDEX;
		}
	}

	/**
	 * Determines the single words and phrases one searches for and returns them
	 * as an ArrayList. Returns null if no word or phrase found.
	 * 
	 * @param userCommand
	 * @return ArrayList containing the searched words and phrases
	 */
	public ArrayList<String> determineSearchedWords(String userCommand) {
		if (userCommand == null || userCommand.equals("")) {
			logger.log(Level.WARNING, Global.MESSAGE_ILLEGAL_ARGUMENTS);
			return null;
		}

		String userCommandWithoutCommandType = removeCommandType(userCommand);

		ArrayList<String> searchedWords = new ArrayList<String>();
		Pattern pattern = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"");
		Matcher matcher = pattern.matcher(userCommandWithoutCommandType);

		while (matcher.find()) {
			if (matcher.group(1) != null) {
				searchedWords.add(matcher.group(1)); // Phrase in double quotes
			} else {
				searchedWords.add(matcher.group()); // Single word
			}
		}
		return searchedWords;
	}

	/**
	 * Determines if the user command contains exactly the given string, e.g.
	 * "none", "timed", "deadline" (not case sensitive).
	 * @param userCommand
	 * @param parameter
	 * @return true if user command contains given parameter
	 */
	public boolean containsParameter(String userCommand, String parameter) {
		if (userCommand == null || userCommand.equals("") || parameter == null
				|| parameter.equals("")) {
			logger.log(Level.WARNING, Global.MESSAGE_ILLEGAL_ARGUMENTS);
			return false;
		}
		String userCommandWithoutCommandType = removeCommandType(userCommand);
		String userCommandWithoutTaskNameAndCommandType = removeTaskName(userCommandWithoutCommandType);

		return userCommandWithoutTaskNameAndCommandType.matches("(?i).*\\b"
				+ parameter + "\\b.*");
	}

	/**
	 * Checks if the given list contains a start and an end date, and if the
	 * parsed end date occurs before the start date. If so and if both dates are
	 * on the same day, it indicates that the time period should actually start
	 * from one day and end the next day. However, if the end date is on a
	 * different day before the start date, it switches both days so that the
	 * prior one comes first.
	 */
	private List<Date> checkOrderOfDateTimes(List<Date> dateTimes) {
		if (dateTimes != null && dateTimes.size() == 2) {
			Date startDate = dateTimes.get(0);
			Date endDate = dateTimes.get(1);
			if (endDate.compareTo(startDate) < 0) {
				Calendar startCal = Calendar.getInstance();
				startCal.setTime(startDate);
				startCal.set(Calendar.HOUR_OF_DAY, 0);
				Calendar endCal = Calendar.getInstance();
				endCal.setTime(endDate);
				endCal.set(Calendar.HOUR_OF_DAY, 0);

				if (endCal.compareTo(startCal) == 0) {
					Calendar c = Calendar.getInstance();
					c.setTime(endDate);
					c.add(Calendar.DATE, 1);
					endDate = c.getTime();
					dateTimes.set(1, endDate);
				} else {
					dateTimes.set(0, endDate);
					dateTimes.set(1, startDate);
				}
			}
		}
		return dateTimes;
	}

	// Returns dateTimes within the given string using external Natty library.
	private List<Date> getDateTimes(String str) throws Exception {
		List<Date> dates = null;

		com.joestelmach.natty.Parser nattyParser = new com.joestelmach.natty.Parser();
		List<DateGroup> groups = nattyParser.parse(str);

		for (DateGroup group : groups) {
			dates = group.getDates();
		}
		return dates;
	}

	// Removes the index, if it exists in the given string.
	private String removeIndex(String userCommand) {
		String result;
		Global.CommandType commandType = TaskCommander.parser
				.determineCommandType(userCommand);

		if (commandType.equals(Global.CommandType.UPDATE)
				|| commandType.equals(Global.CommandType.DONE)
				|| commandType.equals(Global.CommandType.OPEN)
				|| commandType.equals(Global.CommandType.DELETE)) {

			try {
				result = removeSecondWord(userCommand);
			} catch (Exception e) {
				logger.log(Level.INFO, MESSAGE_NO_INDEX);
				return null;
			}
		} else {
			result = userCommand;
		}
		return result;
	}

	// Removes the commandType if existing.
	private String removeCommandType(String userCommand) {
		Global.CommandType commandType = TaskCommander.parser
				.determineCommandType(userCommand);
		if (!commandType.equals(Global.CommandType.INVALID)) {
			try {
				return removeFirstWord(userCommand);
			} catch (Exception e) {
				logger.log(Level.INFO, MESSAGE_NO_COMMANDTYPE);
				return userCommand;
			}
		} else {
			return userCommand;
		}
	}

	// Removes the quoted task name if existing.
	private String removeTaskName(String userCommand) {
		try {
			return removeQuotedSubstring(userCommand);
		} catch (Exception e) {
			logger.log(Level.INFO, MESSAGE_NO_TASKNAME);
			return userCommand;
		}
	}

	/*
	 * Removes the quoted substring within the given string. It is assumed that
	 * all quotation marks between the first quote and the last quote belong to
	 * the substring.
	 */
	private String removeQuotedSubstring(String str) throws Exception {
		return str.replaceFirst(
				str.substring(str.indexOf("\"") + 1, str.lastIndexOf("\"")), "");
	}

	private String getFirstWord(String str) throws Exception {
		return str.trim().split("\\s+")[0];
	}

	private String getSecondWord(String str) throws Exception {
		return str.trim().split("\\s+")[1];

	}

	private String getQuotedSubstring(String str) throws Exception {
		return str.substring(str.indexOf("\"") + 1, str.lastIndexOf("\""))
				.trim();
	}

	private String removeFirstWord(String str) throws Exception {
		return str.replaceFirst(getFirstWord(str), "").trim();
	}

	private String removeSecondWord(String str) throws Exception {
		return str.replaceFirst(getSecondWord(str), "");
	}
}