package automatedTestDriver.Integrated;

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
				{ searchCommand[0], String.format(Global.MESSAGE_INVALID_FORMAT, searchCommand[0])},
				{ searchCommand[1], String.format(Global.MESSAGE_INVALID_FORMAT, searchCommand[1])},
				{ searchCommand[2], String.format(Global.MESSAGE_INVALID_FORMAT, searchCommand[2])},
				{ searchCommand[3], String.format(Global.MESSAGE_INVALID_FORMAT, searchCommand[3])},
				{ searchCommand[0]+" "+searchPhrase, String.format(Global.MESSAGE_INVALID_FORMAT, searchCommand[0]+" "+searchPhrase)},
				{ searchCommand[1]+" "+searchPhrase, String.format(Global.MESSAGE_INVALID_FORMAT, searchCommand[1]+" "+searchPhrase)},
				{ searchCommand[2]+" "+searchPhrase, String.format(Global.MESSAGE_INVALID_FORMAT, searchCommand[2]+" "+searchPhrase)},
				{ searchCommand[3]+" "+searchPhrase, String.format(Global.MESSAGE_INVALID_FORMAT, searchCommand[3]+" "+searchPhrase)},
				{ searchCommand[0]+" "+searchString, "search task with keywords "+searchString},
				{ searchCommand[1]+" "+searchString, "search task with keywords "+searchString},
				{ searchCommand[2]+" "+searchString, "search task with keywords "+searchString},
				{ searchCommand[3]+" "+searchString, "search task with keywords "+searchString},
		});
	}

	@Test
	public void testcontainsParameter() {
		
		assertEquals(expectedResult, TaskCommander.controller.executeCommand(userCommand)); 
	}
}
