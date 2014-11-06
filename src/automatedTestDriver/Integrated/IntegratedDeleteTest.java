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
		Date later = calendar.getTime();
		
		return Arrays.asList(new Object[][] {
				{ deleteCommand[0], "Invalid command format: "+deleteCommand[0]+". Type 'help' to see the list of commands."},
				{ deleteCommand[1], "Invalid command format: "+deleteCommand[1]+". Type 'help' to see the list of commands."},
				{ deleteCommand[2], "Invalid command format: "+deleteCommand[2]+". Type 'help' to see the list of commands."},
				{ deleteCommand[3], "Invalid command format: "+deleteCommand[3]+". Type 'help' to see the list of commands."},
				{ deleteCommand[0]+" "+index[0], "Index does not exist. Please type a valid index."},
				{ deleteCommand[0]+" "+index[1], "Deleted: \"Be patient with friends\""},
				{ deleteCommand[0]+" "+index[2], "Deleted: \"Have fun with friends\""},
				{ deleteCommand[0]+" "+index[3], "Deleted: \"Prepare for CS2103 Final\""},
				{ deleteCommand[0]+" "+index[4], "Deleted: \"Read MA2214 textbook\""},
				{ deleteCommand[0]+" "+index[5], "Deleted: \"Talk to people\""},
				{ deleteCommand[0]+" "+index[6], "Deleted: [Thu Oct 30 '14 "+Global.timeFormat.format(today)+"-Mon Nov 10 '14 "+Global.timeFormat.format(today)+"] \"Finish V0.5 in 10 days\""},
				{ deleteCommand[0]+" "+index[7], "Deleted: [Thu Oct 30 '14 "+Global.timeFormat.format(today)+"-Mon Nov 10 '14 "+Global.timeFormat.format(today)+"] \"Contribute to our project\""},
				{ deleteCommand[0]+" "+index[8], "Deleted: [by "+Global.dayFormat.format(today)+" "+Global.timeFormat.format(today)+"] \"Make friends\""},
				{ deleteCommand[0]+" "+index[9], "Deleted: [by "+Global.dayFormat.format(today)+" "+Global.timeFormat.format(today)+"] \"Eat an apple\""},
				{ deleteCommand[0]+" "+index[10], "Deleted: [by "+Global.dayFormat.format(today)+" "+Global.timeFormat.format(later)+"] \"Relax!\""},
				{ deleteCommand[0]+" "+index[11], "Deleted: [by "+Global.dayFormat.format(today)+" "+Global.timeFormat.format(later)+"] \"Get married\""},
				{ deleteCommand[0]+" "+index[12], "Deleted: [by "+Global.dayFormat.format(today)+" 21:00] \"Get some exercise\""},
				{ deleteCommand[0]+" "+index[23], "Index does not exist. Please type a valid index."},
				{ deleteCommand[0]+" "+index[24], "Index does not exist. Please type a valid index."},
	
		});
	}

   	@Test
	public void testcontainsParameter() {
		assertEquals(expectedResult, TaskCommander.controller.executeCommand(userCommand)); 
	}
}
