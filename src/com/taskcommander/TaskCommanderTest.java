package com.taskcommander;
import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

/**
 * This class is supposed to test the program automatically.
 * 
 * @author Group F11-1J
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
	public void testAddTimeTask()throws Exception{
		String userCommand = "add \"little boy\" 3pm";
		assertEquals("Added: [by Sat Oct 18 '14 15:00] \"little boy\"",
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
		assertEquals("Displayed: Period: [Sat Oct 18 '14 "+Global.timeFormat.format(date)+"-"+
				Global.timeFormat.format(date)+ "]  Type: deadline Status: open ", TaskCommander.controller.executeCommand(userCommand));

	}


	@Test
	public void testUpdateTaskWithTimeAdded() throws Exception{
		String userCommand = "update 1 3pm";
		TaskCommander.controller.getDisplayedTasks();
		assertEquals("Updated: [by Sat Oct 18 '14 15:00] \"little boy\"", TaskCommander.controller.executeCommand(userCommand));

	}
	

}
