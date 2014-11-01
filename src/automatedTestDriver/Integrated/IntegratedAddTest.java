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
 * This class contains all test cases for the method containsParameter(userCommand:String) of the component Integrated Testing.
 * 
 * 
 */
//@author A0105753J
@RunWith(Parameterized.class)
public class IntegratedAddTest {

	private String userCommand;
	private String expectedResult;


	public IntegratedAddTest(String userCommand, String expectedResult) {
		this.userCommand = userCommand;
		this.expectedResult = expectedResult;
	}


	/*
	 * These care test cases for add method
	 * Format can be:
	 * Add "content"
	 * Add "Content" deadline
	 * Add "Content" period
	 * 
	 * There are multiple ways to indicate time. Like "in 2o minutes", "in weekend", 
	 * 	"winter vacation", "5 hours later" and so on
	 */
	@Parameterized.Parameters
	public static Collection<Object[]>  cases() {
		String addCommand = "add";
		String q = "\"";
		String content = "task content";
		String deadline = "Nov 11 5pm";
		String period = "3 Dec 5pm - 6 Dec 6pm";
		String periodLastDaysWithoutDate = "6pm - 3 am";
		String []deadlineInFormat = {"20 Minutes later","in 20 minutes"};
		

		Calendar calendar = Calendar.getInstance();
		Date today = calendar.getTime();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		Date tomorrow = calendar.getTime();
		calendar.setTimeInMillis(today.getTime());
		calendar.add(Calendar.MINUTE, 20);
		Date later = calendar.getTime();
		
		return Arrays.asList(new Object[][] {
				{ addCommand, "Invalid command format: add. Type 'help' to see the list of commands."},
				{ addCommand+" "+q+content+q, "Added: "+q+content+q},
				{ addCommand+" "+q+content+q+" " + deadline, "Added: [by Tue Nov 11 '14 17:00] "+q+content+q },
				{ addCommand+" "+q+content+q+" " + period, "Added: [Wed Dec 3 '14 17:00-Sat Dec 6 '14 18:00] "+q+content+q },
				{ addCommand+" "+q+content+q+" " + periodLastDaysWithoutDate, "Added: ["+Global.dayFormat.format(today)+" "+"18:00-"+ Global.dayFormat.format(tomorrow)+" 03:00] "+q+content+q },
				{ addCommand+" "+q+content+q+" " + deadlineInFormat[0], "Added: [by "+Global.dayFormat.format(later)+" "+ Global.timeFormat.format(later)+"] "+q+content+q },
				{ addCommand+" "+q+content+q+" " + deadlineInFormat[1], "Added: [by "+Global.dayFormat.format(later)+" "+ Global.timeFormat.format(later)+"] "+q+content+q },
		});
	}

   	@Test
	public void testcontainsParameter() {
		assertEquals(expectedResult, TaskCommander.controller.executeCommand(userCommand)); 
	}
}
