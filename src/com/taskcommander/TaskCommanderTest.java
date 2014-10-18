package com.taskcommander;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * This class is supposed to test the program automatically.
 * 
 * @author Group F11-1J
 */

public class TaskCommanderTest {
	
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
/*	@Test
	public void testAddTimeTask()throws Exception{
		String userCommand = "add \"little boy\" 3pm";
		assertEquals(String.format(Global.MESSAGE_ADDED,"[by "+ Global.dayFormat.format(deadlineTask.getEndDate())+ " "+ 
		Global.timeFormat.format(deadlineTask.getEndDate()) + "]"+ " \"" + "little boy" + "\""),
		TaskCommander.controller.executeCommand(userCommand));
	}*/
	
	
	
	/**Test Display Function
	 * */
	@Test
	public void testDisplayWithoutRestriction() throws Exception{
		String userCommand = "display";
		assertEquals("Displayed: all", TaskCommander.controller.executeCommand(userCommand));
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
	
}
