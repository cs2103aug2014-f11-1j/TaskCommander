package automatedTestDriver.Integrated;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.taskcommander.Global;
import com.taskcommander.TaskCommander;

/**
 * This class contains all test cases for the Integrated Testing of the display method.
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

	/*
	 * Test cases for display.
	 * Format: display timePeriod status
	 */
	@Parameterized.Parameters
	public static Collection<Object[]>  cases() {
		String displayCommand[] = {"display","Display", "DISPLAY", "diSPLay"};
		String tasktype[] = {"none", "deadline", "timed"};
		String timePeriod = "Oct 31 - Nov 18";
		String openStatus = "open";
		String doneStatus = "done";

		Calendar calendar = Calendar.getInstance();
		Date today = calendar.getTime();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		calendar.setTimeInMillis(today.getTime());
		calendar.add(Calendar.MINUTE, 20);

		return Arrays.asList(new Object[][] {
				{ displayCommand[0], "All"},
				{ displayCommand[1], "All"},
				{ displayCommand[2], "All"},
				{ displayCommand[3], "All"},
				{ displayCommand[0]+" " +tasktype[0], "Type: none"},
				{ displayCommand[0]+" " +tasktype[1], "Type: deadline"},
				{ displayCommand[0]+" " +tasktype[2], "Type: timed"},
				{ displayCommand[0]+" " +openStatus, "Status: open"},
				{ displayCommand[0]+" " +doneStatus, "Status: done"},
				{ displayCommand[0]+" " +timePeriod, "Date: Fri Oct 31 '14 "+ Global.timeFormat.format(today)+" - "+"Tue Nov 18 '14 "+Global.timeFormat.format(today)},
				
				{ displayCommand[0]+" " +tasktype[0]+" " + " "+openStatus, "Type: none Status: open"},
				{ displayCommand[0]+" " +tasktype[1]+" "+ timePeriod+ " "+openStatus, "Date: Fri Oct 31 '14 "+ Global.timeFormat.format(today)+" -"+" Tue Nov 18 '14 "+Global.timeFormat.format(today)+ " Type: deadline Status: open"},
				{ displayCommand[0]+" " +tasktype[2]+" "+ timePeriod+ " "+openStatus, "Date: Fri Oct 31 '14 "+ Global.timeFormat.format(today)+" -"+" Tue Nov 18 '14 "+Global.timeFormat.format(today)+ " Type: timed Status: open"},
				
				{ displayCommand[0]+" " +tasktype[0]+" " + " "+doneStatus, "Type: none Status: done"},
				{ displayCommand[0]+" " +tasktype[1]+" "+ timePeriod+ " "+doneStatus, "Date: Fri Oct 31 '14 "+ Global.timeFormat.format(today)+" -"+" Tue Nov 18 '14 "+Global.timeFormat.format(today)+ " Type: deadline Status: done"},
				{ displayCommand[0]+" " +tasktype[2]+" "+ timePeriod+ " "+doneStatus, "Date: Fri Oct 31 '14 "+ Global.timeFormat.format(today)+" -"+" Tue Nov 18 '14 "+Global.timeFormat.format(today)+ " Type: timed Status: done"},

		});
	}

	@Test
	public void testcontainsParameter() {
		TaskCommander.controller.executeCommand(userCommand);
		assertEquals(expectedResult, TaskCommander.controller.getDisplaySettingsDescription()); 
	}
}
