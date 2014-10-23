package com.taskcommander;
import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

/**
 * This class is supposed to test the program integrated.
 * 
 * @author Group F11-1J A0105753J
 */

public class TaskCommanderTest {


	//Clear Operation testing
	@Test
	public void testClear() throws Exception {
		String userCommand = "clear";
		assertEquals("All content deleted.", TaskCommander.controller.executeCommand(userCommand));
	}

	@Test
	public void testWithNoCommand() throws Exception {
		String userCommand = "";
		assertEquals("No command given.", String.format(Global.ERROR_MESSAGE_NO_COMMAND));
		assertEquals("No command given.", TaskCommander.controller.executeCommand(userCommand));
	}


	/**test add function
	 **/
	@Test
	public void testAddWithoutContent()throws Exception{
		String userCommand = "add";
		assertEquals("Invalid command format: add. Type 'help' to see the list of commands.",TaskCommander.controller.executeCommand(userCommand));
	}	
	@Test
	public void testAddFloatingTask() throws Exception{
		String userCommand = "add \"little boy\"";
		assertEquals("Added: \"little boy\"", TaskCommander.controller.executeCommand(userCommand));
	}
	@Test
	public void testAddDeadlineTask()throws Exception{
		String userCommand = "add \"little boy\" 3pm";
		Date date = new Date();
		assertEquals("Added: [by "+Global.dayFormat.format(date)+" "+"15:00] \"little boy\"",
				TaskCommander.controller.executeCommand(userCommand));
	}
	@Test
	public void testAddDeadlineTaskWithSpecificDate()throws Exception{
		String userCommand = "add \"walk a dog\" Nov 11 2015 3pm";
		assertEquals("Added: [by Wed Nov 11 '15 15:00] \"walk a dog\"",
				TaskCommander.controller.executeCommand(userCommand));
	}
	@Test
	public void testAddTimeTask()throws Exception{
		String userCommand = "add \"little boy\" 3pm - 4pm";
		Date date = new Date();
		assertEquals("Added: ["+Global.dayFormat.format(date)+" "+"15:00-16:00] \"little boy\"",
				TaskCommander.controller.executeCommand(userCommand));
	}
	
	
	@Test
	public void testAddTimeTaskWithDifferentDate()throws Exception{
		String userCommand = "add \"Meeting In ShangHai\" Nov 11 2014 3pm -  Dec 12 2014 4pm";
		assertEquals("Added: [Tue Nov 11 '14 15:00-Fri Dec 12 '14 16:00] \"Meeting In ShangHai\"",
				TaskCommander.controller.executeCommand(userCommand));
	}


	/**Test Display Function
	 * */
	@Test
	public void testDisplayWithoutRestriction() throws Exception{
		String userCommand = "display";
		assertEquals("Displayed: all", TaskCommander.controller.executeCommand(userCommand));
		/*Assert.AreEqual(TaskCommander.controller.getDisplayedTasks(), RightObject);*/
	}
	@Test
	public void testDisplayDone() throws Exception{
		String userCommand = "display done";
		assertEquals("Displayed: Status: done ", TaskCommander.controller.executeCommand(userCommand));
	}
	@Test
	public void testDisplayOpen() throws Exception{
		String userCommand = "display open";
		assertEquals("Displayed: Status: open ", TaskCommander.controller.executeCommand(userCommand));
	}
	@Test
	public void testDisplayTimedDealineOpen() throws Exception{
		String userCommand = "display timed deadline open";
		assertEquals("Displayed: Type: deadline, timed Status: open ", TaskCommander.controller.executeCommand(userCommand));

	}

	//In this test it uses current time, so sometimes the date will have a slite difference
	@Test
	public void testDisplayDealineInPeriod() throws Exception{
		String userCommand = "display open deadline 04/11/14 to 18/11/14";
		Date date  = new Date();
		assertEquals("Displayed: Period: ["+Global.dayFormat.format(date)+" "+ Global.timeFormat.format(date)+"-"+
				Global.timeFormat.format(date)+ "]  Type: deadline Status: open ", TaskCommander.controller.executeCommand(userCommand));

	}

	/**Test Update Function
	 * */
	
	//This is a boundry testing for invalid index 
	// index >=1
	@Test
	public void testUpdateTaskWithInvalidIndex() throws Exception{
		String userCommand = "update 0 3pm";
		TaskCommander.controller.getDisplayedTasks();
		Date date  = new Date();
		assertEquals(String.format(Global.ERROR_MESSAGE_NO_INDEX, 0), TaskCommander.controller.executeCommand(userCommand));

	}
	
	@Test
	public void testUpdateTaskWithTimeAdded() throws Exception{
		String userCommand = "update 1 3pm";
		TaskCommander.controller.getDisplayedTasks();
		Date date  = new Date();
		assertEquals("Updated: [by "+Global.dayFormat.format(date)+" "+ "15:00] \"little boy\"", TaskCommander.controller.executeCommand(userCommand));

	}
	@Test
	public void testUpdateTaskChangeContent() throws Exception{
		String userCommand = "update 1 \"early bird catches the worm\"";
		TaskCommander.controller.getDisplayedTasks();
		Date date = new Date();
		assertEquals("Updated: [by "+Global.dayFormat.format(date)+" "+ "15:00] \"early bird catches the worm\"", TaskCommander.controller.executeCommand(userCommand));
	}
	@Test
	public void testUpdateDeadlineTaskToTimedTask() throws Exception{
		String userCommand = "add \"welcome boss\" 3pm";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "update 1 Nov 11 3pm - 4 pm";
		TaskCommander.controller.getDisplayedTasks();
		assertEquals("Updated: [Tue Nov 11 '14 15:00-16:00] \"welcome boss\"", TaskCommander.controller.executeCommand(userCommand));
	}

	@Test
	public void testUpdateTaskChangeTime() throws Exception{
		String userCommand;
		userCommand = "update 1 none";
		TaskCommander.controller.getDisplayedTasks();
		assertEquals("Updated: \"little boy\"", TaskCommander.controller.executeCommand(userCommand));
	}

	/**Test Mark Function
	 * */
	@Test
	public void testMarkDone() throws Exception{
		String userCommand = "done 1";
		TaskCommander.controller.getDisplayedTasks();
		Date date = new Date();
		assertEquals("Done: [by "+Global.dayFormat.format(date)+" "+ "15:00] \"early bird catches the worm\"", TaskCommander.controller.executeCommand(userCommand));
	}
	@Test
	public void testAlreadyOpen() throws Exception{
		String userCommand = "open 1 ";
		TaskCommander.controller.getDisplayedTasks();
		assertEquals("Already opened.", TaskCommander.controller.executeCommand(userCommand));
	}


	@Test
	public void testDelete() throws Exception{
		String userCommand = "delete 1 ";
		TaskCommander.controller.getDisplayedTasks();
		Date date =  new Date();
		assertEquals("Deleted: [by "+Global.dayFormat.format(date)+" "+ "15:00] \"little boy\"", TaskCommander.controller.executeCommand(userCommand));
	}

}
