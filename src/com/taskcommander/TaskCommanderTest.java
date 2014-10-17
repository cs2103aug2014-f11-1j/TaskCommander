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
		
		assertEquals("No command given.", String.format(Global.ERROR_MESSAGE_NO_COMMAND));
		assertEquals("No command given.", TaskCommander.controller.executeCommand(""));
		assertEquals(String.format(Global.ERROR_MESSAGE_NO_COMMAND), TaskCommander.controller.executeCommand(null));
	}

/*	@Test
	public void testAddWithoutContent(){
		String userCommand = "add";
		assertEquals(String.format(Global.ERROR_MESSAGE_INVALID_FORMAT, userCommand),TaskCommander.controller.executeCommand(userCommand));
	}
	*/
/*	@Test
	public void testAddWithoutDate() {

		assertEquals("Added: \"little boy\"", TaskCommander.controller.executeCommand("add \"little boy\""));
	}

*/

}
