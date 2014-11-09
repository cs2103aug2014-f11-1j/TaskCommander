package automatedTestDriver.IntegratedController;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.taskcommander.Global;
import com.taskcommander.TaskCommander;

/**
 * This class contains all test cases for the Integrated Testing of the search method.
 */
//@author A0105753J
@RunWith(Parameterized.class)
public class IntegratedSearchTest {

	private String userCommand;
	private String expectedResult;

	public IntegratedSearchTest(String userCommand, String expectedResult) {
		this.userCommand = userCommand;
		this.expectedResult = expectedResult;
	}

	/*
	 * Test cases for search 
	 * Format: search
	 */
	@Parameterized.Parameters
	public static Collection<Object[]>  cases() {
		String searchCommand[] = {"search","SEARCH", "sEArch", "searcH"};
		String searchString = "make";
		String searchPhrase =  "make contribution";
		
		return Arrays.asList(new Object[][] {
				{ searchCommand[0], "Invalid command format: \"search\". Refer to help tab to see the list of commands."},
				{ searchCommand[1], "Invalid command format: \"SEARCH\". Refer to help tab to see the list of commands."},
				{ searchCommand[2], "Invalid command format: \"sEArch\". Refer to help tab to see the list of commands."},
				{ searchCommand[3], "Invalid command format: \"searcH\". Refer to help tab to see the list of commands."},
				{ searchCommand[0]+" "+searchPhrase, "Search task with: [make, contribution]"},
				{ searchCommand[1]+" "+searchPhrase, "Search task with: [make, contribution]"},
				{ searchCommand[2]+" "+searchPhrase, "Search task with: [make, contribution]"},
				{ searchCommand[3]+" "+searchPhrase, "Search task with: [make, contribution]"},
				{ searchCommand[0]+" "+searchString, "Search task with: [make]"},
				{ searchCommand[1]+" "+searchString, "Search task with: [make]"},
				{ searchCommand[2]+" "+searchString, "Search task with: [make]"},
				{ searchCommand[3]+" "+searchString, "Search task with: [make]"},
		});
	}

	@Test
	public void testcontainsParameter() {
		
		assertEquals(expectedResult, TaskCommander.controller.executeCommand(userCommand)); 
	}
}
