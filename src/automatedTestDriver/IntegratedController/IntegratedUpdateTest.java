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

import com.taskcommander.Global;
import com.taskcommander.TaskCommander;

/**
 * This class contains all test cases for the Integrated Testing of the update method.
 */
//@author A0105753J
@RunWith(Parameterized.class)
public class IntegratedUpdateTest {

	private String userCommand;
	private String expectedResult;


	public IntegratedUpdateTest(String userCommand, String expectedResult) {
		this.userCommand = userCommand;
		this.expectedResult = expectedResult;
	}
	
	@Before
	public void ini(){
		TaskCommander.ini();
		TaskCommander.controller.executeCommand("display");
		TaskCommander.controller.getDisplayedTasks();
		System.out.println("This is a ini");
	}

	/*
	 * Test cases for update.
	 * Format:
	 * Update n "content"
	 * Update n "content" time
	 * Update n none (remove time constraint)
	 */
	@Parameterized.Parameters
	public static Collection<Object[]>  cases() {
		String updateCommand[] = {"update","UPDATE", "uPdate", "uPdAtE"};
		int noOfTestCases = 25;
		String index[] = new String[25];
		String changeContent  = "\"content\"";
		String changeTimeToBeFloating = "none";
		String changeTimeToBeDeadLine = "Nov 11 3 pm";
		String changeTimeToBePeriod = "Nov 11 3 pm - Nov 25 6 pm";
		for(int i = 0; i < noOfTestCases; i++){
			index[i] = Integer.toString(i);
		}

		Calendar calendar = Calendar.getInstance();
		Date today = calendar.getTime();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		calendar.setTimeInMillis(today.getTime());
		calendar.add(Calendar.MINUTE, 20);
		
		return Arrays.asList(new Object[][] {
				/*{ updateCommand[0], "Invalid command format: "+updateCommand[0]+". Type 'help' to see the list of commands."},
				{ updateCommand[1], "Invalid command format: "+updateCommand[1]+". Type 'help' to see the list of commands."},
				{ updateCommand[2], "Invalid command format: "+updateCommand[2]+". Type 'help' to see the list of commands."},
				{ updateCommand[3], "Invalid command format: "+updateCommand[3]+". Type 'help' to see the list of commands."},				
				{ updateCommand[0]+" "+index[0], "Index "+index[0]+" does not exist. Please type a valid index."},*/
				
				//Tasks 1-5 are floating tasks. Tasks 6 and 7 are timed tasks. Task 8 till the end are deadline tasks
				//Get from Ini function after sorting
				{ updateCommand[0]+" "+index[1], "Invalid command format: \""+updateCommand[0]+" "+index[1] +"\". Refer to help tab to see the list of commands."},
				{ updateCommand[0]+" "+index[1]+" "+changeTimeToBeFloating, "Updated: \"Be patient with friends\""},
				{ updateCommand[0]+" "+index[2]+" "+changeTimeToBeDeadLine, "Updated: [by Tue Nov 11 '14 15:00] \"Have fun with friends\""},
				{ updateCommand[0]+" "+index[3]+" "+changeTimeToBePeriod, "Updated: [Tue Nov 11 '14 15:00-Tue Nov 25 '14 18:00] \"Prepare for CS2103 Final\""},
				{ updateCommand[0]+" "+index[4]+" "+changeContent, "Updated: \"content\""},

				{ updateCommand[0]+" "+index[5]+" "+changeTimeToBeFloating, "Updated: \"Talk to people\""},
				{ updateCommand[0]+" "+index[5]+" "+changeTimeToBeDeadLine, "Updated: [by Tue Nov 11 '14 15:00] \"Talk to people\""},
				{ updateCommand[0]+" "+index[5]+" "+changeContent, "Updated: \"content\""},
				{ updateCommand[0]+" "+index[5]+" "+changeTimeToBePeriod, "Updated: [Tue Nov 11 '14 15:00-Tue Nov 25 '14 18:00] \"Talk to people\""},
				{ updateCommand[0]+" "+index[5]+" "+changeContent+" "+changeTimeToBePeriod, "Updated: [Tue Nov 11 '14 15:00-Tue Nov 25 '14 18:00] \"content\""},
				{ updateCommand[0]+" "+index[5]+" "+changeContent+" "+changeTimeToBeDeadLine, "Updated: [by Tue Nov 11 '14 15:00] \"content\""},

				//Update timed task
				{ updateCommand[0]+" "+index[6]+" "+changeTimeToBeFloating, "Updated: \"Finish V0.5 in 10 days\""},
				{ updateCommand[0]+" "+index[6]+" "+changeTimeToBeDeadLine, "Updated: [by Tue Nov 11 '14 15:00] \"Finish V0.5 in 10 days\""},
				//{ updateCommand[0]+" "+index[6]+" "+changeContent, "Updated: [Thu Oct 30 '14 "+Global.timeFormat.format(today)+"-Mon Nov 10 '14 "+Global.timeFormat.format(today)+"] \"content\""},
				{ updateCommand[0]+" "+index[6]+" "+changeTimeToBePeriod, "Updated: [Tue Nov 11 '14 15:00-Tue Nov 25 '14 18:00] \"Finish V0.5 in 10 days\""},
				{ updateCommand[0]+" "+index[6]+" "+changeContent+" "+changeTimeToBePeriod, "Updated: [Tue Nov 11 '14 15:00-Tue Nov 25 '14 18:00] \"content\""},
				{ updateCommand[0]+" "+index[6]+" "+changeContent+" "+changeTimeToBeDeadLine, "Updated: [by Tue Nov 11 '14 15:00] \"content\""},
				{ updateCommand[0]+" "+index[6]+" "+changeContent+" "+changeTimeToBeFloating, "Updated: \"content\""},
				
				{ updateCommand[0]+" "+index[7]+" "+changeTimeToBeFloating, "Updated: \"Contribute to our project\""},
				{ updateCommand[0]+" "+index[7]+" "+changeTimeToBeDeadLine, "Updated: [by Tue Nov 11 '14 15:00] \"Contribute to our project\""},
				//{ updateCommand[0]+" "+index[7]+" "+changeContent, "Updated: [Thu Oct 30 '14 "+Global.timeFormat.format(today)+"-Mon Nov 10 '14 "+Global.timeFormat.format(today)+"] \"content\""},
				{ updateCommand[0]+" "+index[7]+" "+changeTimeToBePeriod, "Updated: [Tue Nov 11 '14 15:00-Tue Nov 25 '14 18:00] \"Contribute to our project\""},
				{ updateCommand[0]+" "+index[7]+" "+changeContent+" "+changeTimeToBePeriod, "Updated: [Tue Nov 11 '14 15:00-Tue Nov 25 '14 18:00] \"content\""},
				{ updateCommand[0]+" "+index[7]+" "+changeContent+" "+changeTimeToBeDeadLine, "Updated: [by Tue Nov 11 '14 15:00] \"content\""},
				{ updateCommand[0]+" "+index[7]+" "+changeContent+" "+changeTimeToBeFloating, "Updated: \"content\""},
				
				{ updateCommand[0]+" "+index[8]+" "+changeTimeToBeFloating, "Updated: \"Make friends\""},
				{ updateCommand[0]+" "+index[8]+" "+changeTimeToBeDeadLine, "Updated: [by Tue Nov 11 '14 15:00] \"Make friends\""},
				//{ updateCommand[0]+" "+index[8]+" "+changeContent, "Updated: [by "+Global.dayFormat.format(today)+" "+Global.timeFormat.format(today)+"] \"content\""},
				{ updateCommand[0]+" "+index[8]+" "+changeTimeToBePeriod, "Updated: [Tue Nov 11 '14 15:00-Tue Nov 25 '14 18:00] \"Make friends\""},
				{ updateCommand[0]+" "+index[8]+" "+changeContent+" "+changeTimeToBePeriod, "Updated: [Tue Nov 11 '14 15:00-Tue Nov 25 '14 18:00] \"content\""},
				{ updateCommand[0]+" "+index[8]+" "+changeContent+" "+changeTimeToBeDeadLine, "Updated: [by Tue Nov 11 '14 15:00] \"content\""},
				{ updateCommand[0]+" "+index[8]+" "+changeContent+" "+changeTimeToBeFloating, "Updated: \"content\""},
				
				{ updateCommand[0]+" "+index[9]+" "+changeTimeToBeFloating, "Updated: \"Eat an apple\""},
				{ updateCommand[0]+" "+index[9]+" "+changeTimeToBeDeadLine, "Updated: [by Tue Nov 11 '14 15:00] \"Eat an apple\""},
				//{ updateCommand[0]+" "+index[9]+" "+changeContent, "Updated: [by "+Global.dayFormat.format(today)+" "+Global.timeFormat.format(today)+"] \"content\""},
				{ updateCommand[0]+" "+index[9]+" "+changeTimeToBePeriod, "Updated: [Tue Nov 11 '14 15:00-Tue Nov 25 '14 18:00] \"Eat an apple\""},
				{ updateCommand[0]+" "+index[9]+" "+changeContent+" "+changeTimeToBePeriod, "Updated: [Tue Nov 11 '14 15:00-Tue Nov 25 '14 18:00] \"content\""},
				{ updateCommand[0]+" "+index[9]+" "+changeContent+" "+changeTimeToBeDeadLine, "Updated: [by Tue Nov 11 '14 15:00] \"content\""},
				{ updateCommand[0]+" "+index[9]+" "+changeContent+" "+changeTimeToBeFloating, "Updated: \"content\""},
			
				{ updateCommand[0]+" "+index[23]+" "+changeTimeToBeFloating, "Index does not exist. Please type a valid index."},
				{ updateCommand[0]+" "+index[23]+" "+changeTimeToBeDeadLine, "Index does not exist. Please type a valid index."},
				{ updateCommand[0]+" "+index[23]+" "+changeContent, "Index does not exist. Please type a valid index."},
				{ updateCommand[0]+" "+index[23]+" "+changeTimeToBePeriod, "Index does not exist. Please type a valid index."},
				{ updateCommand[0]+" "+index[24], "Index does not exist. Please type a valid index."},
		});
	}

   	@Test
	public void testcontainsParameter() {
		assertEquals(expectedResult, TaskCommander.controller.executeCommand(userCommand)); 
	}
}
