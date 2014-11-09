package automatedTestDriver.IntegratedController;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.taskcommander.TaskCommander;

/**
 * This class contains all test cases for the Integrated Testing of the clear method.
 */
//@author A0105753J
@RunWith(Parameterized.class)
public class IntegratedClearTest {

	private String userCommand;
	private String expectedResult;

	public IntegratedClearTest(String userCommand, String expectedResult) {
		this.userCommand = userCommand;
		this.expectedResult = expectedResult;
	}

	/*
	 * Test cases for clear.
	 * Format: clear
	 * 
	 */
	@Parameterized.Parameters
	public static Collection<Object[]>  cases() {
		String clearCommand[] = {"clear","Clear", "CLEAR", "cleaR"};
		return Arrays.asList(new Object[][] {
				{ clearCommand[0], "All content deleted."},
				{ clearCommand[1], "No tasks available"},
				{ clearCommand[2], "No tasks available"},
				{ clearCommand[3], "No tasks available"},
		});
	}

	@Test
	public void testcontainsParameter() {
		assertEquals(expectedResult, TaskCommander.controller.executeCommand(userCommand)); 
	}
}
