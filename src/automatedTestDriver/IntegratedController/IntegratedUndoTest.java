package automatedTestDriver.IntegratedController;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.taskcommander.TaskCommander;

/**
 * This class contains all test cases for the Integrated Testing of the undo method.
 */
//@author A0105753J
@RunWith(Parameterized.class)
public class IntegratedUndoTest {

	private String userCommand;
	private String expectedResult;

	public IntegratedUndoTest(String userCommand, String expectedResult) {
		this.userCommand = userCommand;
		this.expectedResult = expectedResult;
	}

	@Before
	public void ini(){
		TaskCommander.ini();
	}

	/*
	 * Test cases for undo.
	 * Format: undo n (index)
	 */
	@Parameterized.Parameters
	public static Collection<Object[]>  cases() {
		
		return Arrays.asList(new Object[][] {
				{"add \"Content\"", "Undone latest command: ADD."},
				{"delete 1", "Undone latest command: DELETE."},
				{"update 1 \"Content\"", "Undone latest command: UPDATE."},
				{"open 1", "Undone latest command: OPEN."},
				{"Done 2", "Undone latest command: DONE."},
				{"clear", "Undone latest command: CLEAR."},
		});
	}

	@Test
	public void testUndo() {
		TaskCommander.controller.executeCommand(userCommand);
		assertEquals(expectedResult, TaskCommander.controller.executeCommand("undo")); 
	}
}
