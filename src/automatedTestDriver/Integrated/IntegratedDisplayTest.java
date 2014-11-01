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
 * This class contains all test cases for the method display of Integrated Testing.
 * 
 * 
 */
//@author A0105753J
@RunWith(Parameterized.class)
public class IntegratedDisplayTest {

	private String userCommand;
	private String expectedResult;


	public IntegratedDisplayTest(String userCommand, String expectedResult) {
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
	 * Format would be:delete n (index)
	 * 
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
		Date tomorrow = calendar.getTime();
		calendar.setTimeInMillis(today.getTime());
		calendar.add(Calendar.MINUTE, 20);
		Date later = calendar.getTime();
		
		return Arrays.asList(new Object[][] {
				{ deleteCommand[0], "Invalid command format: "+deleteCommand[0]+". Type 'help' to see the list of commands."},
				{ deleteCommand[1], "Invalid command format: "+deleteCommand[1]+". Type 'help' to see the list of commands."},
				{ deleteCommand[2], "Invalid command format: "+deleteCommand[2]+". Type 'help' to see the list of commands."},
				{ deleteCommand[3], "Invalid command format: "+deleteCommand[3]+". Type 'help' to see the list of commands."},
				//{ deleteCommand[0]+" "+index[0], "Index "+index[0]+" does not exist. Please type a valid index."},
				{ deleteCommand[0]+" "+index[1], "Deleted: \"MA2214 reading textbook\""},
				{ deleteCommand[0]+" "+index[2], "Deleted: \"be patiend to friends\""},
				{ deleteCommand[0]+" "+index[3], "Deleted: \"have fun with friends\""},
				{ deleteCommand[0]+" "+index[4], "Deleted: \"prepare for CS2103 Final\""},
				{ deleteCommand[0]+" "+index[5], "Deleted: \"talk to people\""},
				{ deleteCommand[0]+" "+index[6], "Deleted: [Thu Oct 30 '14 "+Global.timeFormat.format(today)+"-Mon Nov 10 '14 "+Global.timeFormat.format(today)+"] \"finish V0.5 in 10 days\""},
				{ deleteCommand[0]+" "+index[7], "Deleted: [Thu Oct 30 '14 "+Global.timeFormat.format(today)+"-Mon Nov 10 '14 "+Global.timeFormat.format(today)+"] \"make contribution to project\""},
				{ deleteCommand[0]+" "+index[8], "Deleted: [by "+Global.dayFormat.format(today)+" "+Global.timeFormat.format(today)+"] \"make friends\""},
				{ deleteCommand[0]+" "+index[9], "Deleted: [by "+Global.dayFormat.format(today)+" "+Global.timeFormat.format(today)+"] \"eat an apple\""},
				{ deleteCommand[0]+" "+index[10], "Deleted: [by "+Global.dayFormat.format(today)+" "+Global.timeFormat.format(later)+"] \"hey relax your neck it is hurt\""},
				{ deleteCommand[0]+" "+index[11], "Deleted: [by "+Global.dayFormat.format(today)+" "+Global.timeFormat.format(later)+"] \"get married\""},
				{ deleteCommand[0]+" "+index[12], "Deleted: [by "+Global.dayFormat.format(today)+" 21:00] \"get excersice\""},
/*				{ deleteCommand[0]+" "+index[23], "Index "+index[23]+" does not exist. Please type a valid index."},
				{ deleteCommand[0]+" "+index[24], "Index "+index[24]+" does not exist. Please type a valid index."},*/
	
		});
	}

   	@Test
	public void testcontainsParameter() {
		assertEquals(expectedResult, TaskCommander.controller.executeCommand(userCommand)); 
	}
}
