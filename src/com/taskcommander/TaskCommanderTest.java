package com.taskcommander;
import static org.junit.Assert.*;


import org.junit.Test;

public class TaskCommanderTest {

	TaskCommander myTaskCommander= new TaskCommander();
	
	@Test
	public void  testExecuteCommand() {
		testOneCommand("addTask", "Added to "+"tasks.txt"+": \"oldTaskDescription\"",
				"add oldTaskDescription");
		testOneCommand("updateTask", "Updated "+"tasks.txt"+": \"newTaskDescription\"",
				"update 1 newTaskDescription");
	}
	
	private void testOneCommand(String description, String expected,
			String command) {
		assertEquals(description, expected, myTaskCommander.executeCommand(command));
	}

}
