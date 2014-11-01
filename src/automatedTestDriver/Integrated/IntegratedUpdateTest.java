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
 * This class contains all test cases for the method Update of the Integrated Testing.
 * 
 * 
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
	 * These care test cases for delete method
	 * Format would be:
	 * Update n "content"
	 * Update n "content" time
	 * Update n none (remove time constrain)
	 */
	@Parameterized.Parameters
	public static Collection<Object[]>  cases() {
		String updateCommand[] = {"update","UPDATE", "uPdate", "uPdAtE"};
		int noOfTestCases = 25;
		String index[] = new String[25];
		for(int i = 0; i < noOfTestCases; i++){
			index[i] = Integer.toString(i);
		}

		

		Calendar calendar = Calendar.getInstance();
		Date today = calendar.getTime();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		Date tomorrow = calendar.getTime();
		calendar.setTimeInMillis(today.getTime());
		calendar.add(Calendar.MINUTE, 20);
		Date later = calendar.getTime();
		
		return Arrays.asList(new Object[][] {
				{ updateCommand[0], "Invalid command format: "+updateCommand[0]+". Type 'help' to see the list of commands."},
				{ updateCommand[1], "Invalid command format: "+updateCommand[1]+". Type 'help' to see the list of commands."},
				{ updateCommand[2], "Invalid command format: "+updateCommand[2]+". Type 'help' to see the list of commands."},
				{ updateCommand[3], "Invalid command format: "+updateCommand[3]+". Type 'help' to see the list of commands."},
				//{ updateCommand[0]+" "+index[0], "Index "+index[0]+" does not exist. Please type a valid index."},
				{ updateCommand[0]+" "+index[1], "Deleted: \"MA2214 reading textbook\""},
				{ updateCommand[0]+" "+index[2], "Deleted: \"be patiend to friends\""},
				{ updateCommand[0]+" "+index[3], "Deleted: \"have fun with friends\""},
				{ updateCommand[0]+" "+index[4], "Deleted: \"prepare for CS2103 Final\""},
				{ updateCommand[0]+" "+index[5], "Deleted: \"talk to people\""},
				{ updateCommand[0]+" "+index[6], "Deleted: [Thu Oct 30 '14 "+Global.timeFormat.format(today)+"-Mon Nov 10 '14 "+Global.timeFormat.format(today)+"] \"finish V0.5 in 10 days\""},
				{ updateCommand[0]+" "+index[7], "Deleted: [Thu Oct 30 '14 "+Global.timeFormat.format(today)+"-Mon Nov 10 '14 "+Global.timeFormat.format(today)+"] \"make contribution to project\""},
				{ updateCommand[0]+" "+index[8], "Deleted: [by "+Global.dayFormat.format(today)+" "+Global.timeFormat.format(today)+"] \"make friends\""},
				{ updateCommand[0]+" "+index[9], "Deleted: [by "+Global.dayFormat.format(today)+" "+Global.timeFormat.format(today)+"] \"eat an apple\""},
				{ updateCommand[0]+" "+index[10], "Deleted: [by "+Global.dayFormat.format(today)+" "+Global.timeFormat.format(later)+"] \"hey relax your neck it is hurt\""},
				{ updateCommand[0]+" "+index[11], "Deleted: [by "+Global.dayFormat.format(today)+" "+Global.timeFormat.format(later)+"] \"get married\""},
				{ updateCommand[0]+" "+index[12], "Deleted: [by "+Global.dayFormat.format(today)+" 21:00] \"get excersice\""},
/*				{ updateCommand[0]+" "+index[23], "Index "+index[23]+" does not exist. Please type a valid index."},
				{ updateCommand[0]+" "+index[24], "Index "+index[24]+" does not exist. Please type a valid index."},*/
	
		});
	}

   	@Test
	public void testcontainsParameter() {
		assertEquals(expectedResult, TaskCommander.controller.executeCommand(userCommand)); 
	}
}
