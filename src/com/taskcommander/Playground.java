package com.taskcommander;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.joestelmach.natty.*;

/**
 * This class is supposed to be a playground to try out new things.
 *
 */

public class Playground {
/*
	public static void main(String[] args) {
		String command = "add \"Walk the dog\" Oktober 4th 9pm to 10pm";
		System.out.println(command.indexOf("\""));
		System.out.println(command.lastIndexOf("\""));
		
		String taskDescription = command.substring(command.indexOf("\"")+1,command.lastIndexOf("\""));
		System.out.println(taskDescription);
		
		String taskDate = command.substring(command.lastIndexOf("\"")+1).trim();
		System.out.println(taskDate);
		
	com.joestelmach.natty.Parser nattyParser = new com.joestelmach.natty.Parser();
	List<DateGroup> groups = nattyParser.parse(taskDate);
	List<Date> dates = null;
	
	for(DateGroup group:groups) {
	dates = group.getDates();
	System.out.println(dates);
	}
	
	String strDate = dates.get(0).toString(); 
	System.out.println(strDate);
	
	/*
	for(DateGroup group:groups) {
	  List dates = group.getDates();
	  int line = group.getLine();
	  int column = group.getPosition();
	  String matchingValue = group.getText();
	  String syntaxTree = group.getSyntaxTree().toStringTree();
	  Map> parseMap = group.getParseLocations();
	  boolean isRecurreing = group.isRecurring();
	  Date recursUntil = group.getRecursUntil();
	}
	
	}

	*/
}
