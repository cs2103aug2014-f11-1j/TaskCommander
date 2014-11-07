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
 * This class contains all test cases for the Integrated Testing of the done method.
 */
//@author A0105753J
@RunWith(Parameterized.class)
public class IntegratedMarkTest {

	private String userCommand;
	private String expectedResult;

	public IntegratedMarkTest(String userCommand, String expectedResult) {
		this.userCommand = userCommand;
		this.expectedResult = expectedResult;
	}
	
	@Before
	public void ini(){
		TaskCommander.ini();
	}

	/*
	 * Test cases for done.
	 * Format: done n (index)
	 * 
	 */
	@Parameterized.Parameters
	public static Collection<Object[]>  cases() {
		String doneCommand[] = {"done","Done", "DONE", "DonE"};
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
				{ doneCommand[0], "Invalid command format: "+doneCommand[0]+". Type 'help' to see the list of commands."},
				{ doneCommand[1], "Invalid command format: "+doneCommand[1]+". Type 'help' to see the list of commands."},
				{ doneCommand[2], "Invalid command format: "+doneCommand[2]+". Type 'help' to see the list of commands."},
				{ doneCommand[3], "Invalid command format: "+doneCommand[3]+". Type 'help' to see the list of commands."},
				{ doneCommand[0]+" "+index[0], "Index does not exist. Please type a valid index."},
				{ doneCommand[0]+" "+index[1], "Already done."},
				{ doneCommand[0]+" "+index[2], "Done: \"Have fun with friends\""},
				{ doneCommand[0]+" "+index[3], "Done: \"Prepare for CS2103 Final\""},
				{ doneCommand[0]+" "+index[4], "Done: \"Read MA2214 textbook\""},
				{ doneCommand[0]+" "+index[5], "Done: \"Talk to people\""},
				{ doneCommand[0]+" "+index[6], "Already done."},
				{ doneCommand[0]+" "+index[7], "Done: [Thu Oct 30 '14 "+Global.timeFormat.format(today)+"-Mon Nov 10 '14 "+Global.timeFormat.format(today)+"] \"Contribute to our project\""},
				{ doneCommand[0]+" "+index[8], "Done: [by "+Global.dayFormat.format(today)+" "+Global.timeFormat.format(today)+"] \"Make friends\""},
				{ doneCommand[0]+" "+index[9], "Done: [by "+Global.dayFormat.format(today)+" "+Global.timeFormat.format(today)+"] \"Eat an apple\""},
				{ doneCommand[0]+" "+index[10], "Done: [by "+Global.dayFormat.format(today)+" "+Global.timeFormat.format(later)+"] \"Relax!\""},
				{ doneCommand[0]+" "+index[11], "Done: [by "+Global.dayFormat.format(today)+" "+Global.timeFormat.format(later)+"] \"Get married\""},
				{ doneCommand[0]+" "+index[12], "Done: [by "+Global.dayFormat.format(today)+" 21:00] \"Get some exercise\""},
				{ doneCommand[0]+" "+index[23], "Index does not exist. Please type a valid index."},
				{ doneCommand[0]+" "+index[24], "Index does not exist. Please type a valid index."},
		});
	}

   	@Test
	public void testcontainsParameter() {
		assertEquals(expectedResult, TaskCommander.controller.executeCommand(userCommand)); 
	}
}