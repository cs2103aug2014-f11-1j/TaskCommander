package com.taskcommander;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
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
	
	
	
	//Construct the Calendar.Events.List request, but don't execute yet		
	Calendar.Events.List eventRequest = con.getListEventRequest();	
	String syncToken = null;
		
		try {
			syncToken = con.getSyncSettingsDataStore().get(SYNC_TOKEN_KEY);
		} catch (IOException e) {
			System.out.println(Global.MESSAGE_EXCEPTION_IO);
		}
		
		if (syncToken == null) {
			System.out.println(Global.MESSAGE_FULL_SYNC);
		} else {
			System.out.println();
			eventRequest.setSyncToken(syncToken);
		}
		
		//Retrieve the events, one page at a time.
		String pageToken = null;
		Events events = null;
		do {
			eventRequest.setTimeMin(new DateTime(System.currentTimeMillis())); 
			eventRequest.setPageToken(pageToken);
			
			try {
				events = eventRequest.execute();
			} catch (IOException e) {
				System.out.println(Global.MESSAGE_INVALID_SYNC_TOKEN);
				con.getSyncSettingsDataStore().delete(SYNC_TOKEN_KEY);
				con.getEventDataStore().clear();
				pull();
			}
			
			List<Event> items = events.getItems();
			if (items.size() == 0) {
				System.out.println(Global.MESSAGE_NO_NEW_SYNC);
			} else {
				for (Event event : items) {
					syncEvent(event);
				}
			}
		} while (pageToken != null);
		
		
		con.getSyncSettingsDataStore().set(SYNC_TOKEN_KEY, events.getNextSyncToken());
		
		System.out.println(Global.MESSAGE_COMPLETED_SYNC);
	**/
}
