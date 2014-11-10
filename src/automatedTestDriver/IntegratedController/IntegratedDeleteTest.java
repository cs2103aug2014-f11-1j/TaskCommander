package automatedTestDriver.IntegratedController;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.taskcommander.TaskCommander;

/**
 * This class contains all test cases for the Integrated Testing of the delete method.
 */
//@author A0105753J
@RunWith(Parameterized.class)
public class IntegratedDeleteTest {

	private String userCommand;
	private String expectedResult;

	public IntegratedDeleteTest(String userCommand, String expectedResult) {
		this.userCommand = userCommand;
		this.expectedResult = expectedResult;
	}
	
	@Before
	public void ini(){
		TaskCommander.ini();
		TaskCommander.controller.executeCommand("display");
		TaskCommander.controller.getDisplayedTasks();
	}

	/*
	 * Test cases for delete
	 * Format: delete n (index)
	 */
	@Parameterized.Parameters
	public static Collection<Object[]>  cases() {
		String deleteCommand[] = {"delete","DELETE", "Delete", "deLete"};
		int noOfTestCases = 25;
		String index[] = new String[25];
		for(int i = 0; i < noOfTestCases; i++){
			index[i] = Integer.toString(i);
		}

		Calendar calendar = Calendar.getInstance();
		Date today = calendar.getTime();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		calendar.setTimeInMillis(today.getTime());
		calendar.add(Calendar.MINUTE, 20);
		
		return Arrays.asList(new Object[][] {
				{ deleteCommand[0], "Invalid command format: \""+deleteCommand[0]+"\". Refer to help tab to see the list of commands."},
				{ deleteCommand[1], "Invalid command format: \""+deleteCommand[1]+"\". Refer to help tab to see the list of commands."},
				{ deleteCommand[2], "Invalid command format: \""+deleteCommand[2]+"\". Refer to help tab to see the list of commands."},
				{ deleteCommand[3], "Invalid command format: \""+deleteCommand[3]+"\". Refer to help tab to see the list of commands."},
				{ deleteCommand[0]+" "+index[0], "Index does not exist. Please type a valid index."},
				{ deleteCommand[0]+" "+index[1], "Deleted: \"Be patient with friends\""},
				{ deleteCommand[0]+" "+index[2], "Deleted: \"Have fun with friends\""},
				{ deleteCommand[0]+" "+index[3], "Deleted: \"Prepare for CS2103 Final\""},
				{ deleteCommand[0]+" "+index[4], "Deleted: \"Read MA2214 textbook\""},
				{ deleteCommand[0]+" "+index[5], "Deleted: \"Talk to people\""},
				{ deleteCommand[0]+" "+index[23], "Index does not exist. Please type a valid index."},
				{ deleteCommand[0]+" "+index[24], "Index does not exist. Please type a valid index."},
	
		});
	}

   	@Test
	public void testcontainsParameter() {
		assertEquals(expectedResult, TaskCommander.controller.executeCommand(userCommand)); 
	}
}
