package com.taskcommander;
import static org.junit.Assert.*;


import org.junit.Test;

/**
 * This class is supposed to test the program automatically.
 * 
 * @author Group F11-1J
 */

public class TaskCommanderTest {

	TaskCommander myTaskCommander= new TaskCommander();

	@Test
	public void  testExecuteCommand() {
		testOneCommand("addTask", "Added: \"oldTaskDescription\"",
				"add oldTaskDescription");
		testOneCommand("updateTask", "Updated: \"newTaskDescription\"",
				"update 1 newTaskDescription");
	}
	
	@Test
	public void testOneCommand(String description, String expected,
			String command) {
		assertEquals(description, expected, myTaskCommander.controller.executeCommand(command));
	}
	
}
