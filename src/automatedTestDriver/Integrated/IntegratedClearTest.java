package automatedTestDriver.Integrated;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.taskcommander.Global;
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
				{ clearCommand[1], "All content deleted."},
				{ clearCommand[2], "All content deleted."},
				{ clearCommand[3], "All content deleted."},
		});
	}

	@Test
	public void testcontainsParameter() {
		assertEquals(expectedResult, TaskCommander.controller.executeCommand(userCommand)); 
	}
}
