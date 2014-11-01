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
	 * These care test cases for delete method
	 * Format would be:delete n (index)
	 * 
	 */
	@Parameterized.Parameters
	public static Collection<Object[]>  cases() {
		String addCommand = "add";
		String addCommandCapital[] = {"Add", "ADD", "AdD"};
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
				{ addCommand, "Invalid command format: "+addCommand+". Type 'help' to see the list of commands."},
				{ addCommand+" "+q+content+q, "Added: "+q+content+q},
				{ addCommandCapital[0], "Invalid command format: "+addCommandCapital[0]+". Type 'help' to see the list of commands."},
				{ addCommandCapital[0]+" "+q+content+q, "Added: "+q+content+q},
				{ addCommandCapital[1], "Invalid command format: "+addCommandCapital[1]+". Type 'help' to see the list of commands."},
				{ addCommandCapital[1]+" "+q+content+q, "Added: "+q+content+q},
				{ addCommandCapital[2], "Invalid command format: "+addCommandCapital[2]+". Type 'help' to see the list of commands."},
				{ addCommandCapital[2]+" "+q+content+q, "Added: "+q+content+q},
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
