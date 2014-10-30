package automatedTestDriver.Integrated;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.taskcommander.Global;
import com.taskcommander.TaskCommander;

/**
 * This class is supposed to test the program integrated.
 * 
 * @author Group F11-1J A0105753J
 */

public class TaskCommanderTest {


	//Clear Operation testing
	@Test
	public void testClear() throws Exception {
		String userCommand = "clear";
		assertEquals("All content deleted.", TaskCommander.controller.executeCommand(userCommand));
	}

	@Test
	public void testWithNoCommand() throws Exception {
		String userCommand = "";
		assertEquals("No command given.", String.format(Global.MESSAGE_NO_COMMAND));
		assertEquals("No command given.", TaskCommander.controller.executeCommand(userCommand));
	}


	/**test add function
	 **/
	@Test
	public void testAddWithoutContent()throws Exception{
		String userCommand = "add";
		assertEquals("Invalid command format: add. Type 'help' to see the list of commands.",TaskCommander.controller.executeCommand(userCommand));
		TaskCommander.controller.executeCommand("clear");
	}	
	@Test
	public void testAddFloatingTask() throws Exception{
		String userCommand = "add \"little boy\"";
		assertEquals("Added: \"little boy\"", TaskCommander.controller.executeCommand(userCommand));
		TaskCommander.controller.executeCommand("clear");
	}
	@Test
	public void testAddDeadlineTask()throws Exception{
		String userCommand = "add \"little boy\" 3pm";
		Date date = new Date();
		assertEquals("Added: [by "+Global.dayFormat.format(date)+" "+"15:00] \"little boy\"",
				TaskCommander.controller.executeCommand(userCommand));
		TaskCommander.controller.executeCommand("clear");
	}
	@Test
	public void testAddDeadlineTaskWithSpecificDate()throws Exception{
		String userCommand = "add \"walk a dog\" Nov 11 2015 3pm";
		assertEquals("Added: [by Wed Nov 11 '15 15:00] \"walk a dog\"",
				TaskCommander.controller.executeCommand(userCommand));
		TaskCommander.controller.executeCommand("clear");
	}
	@Test
	public void testAddTimeTask()throws Exception{
		String userCommand = "add \"little boy\" 3pm - 4pm";
		Date date = new Date();
		assertEquals("Added: ["+Global.dayFormat.format(date)+" "+"15:00-16:00] \"little boy\"",
				TaskCommander.controller.executeCommand(userCommand));
		TaskCommander.controller.executeCommand("clear");
	}
	
	/**
	 * This test case is very strong.
	 * Similar testcases like 1 day later, 20 minutes later also can achive
	 * @throws Exception
	 */
	@Test
	public void testAddTimedTaskWithOneHourLaterCommand() throws Exception{

		String userCommand = "add \"onehourlater\" 1 hour later";
		Date now  = new Date();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(now.getTime());
		c.add(Calendar.HOUR, 1);
		Date onehourlater = c.getTime();
		assertEquals("Added: [by "+Global.dayFormat.format(onehourlater)+" "+ Global.timeFormat.format(onehourlater)+"] \"onehourlater\"",
				TaskCommander.controller.executeCommand(userCommand));
	}
	@Test
	public void testAddTimedTaskWithtwentyMinutesLaterCommand() throws Exception{

		String userCommand = "add \"later\" 20 Minutes later";
		Date now  = new Date();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(now.getTime());
		c.add(Calendar.MINUTE, 20);
		Date later = c.getTime();
		assertEquals("Added: [by "+Global.dayFormat.format(later)+" "+ Global.timeFormat.format(later)+"] \"later\"",
				TaskCommander.controller.executeCommand(userCommand));
	}

	@Test
	public void testAddTimeTaskWithDifferentDate()throws Exception{
		String userCommand = "add \"Meeting In ShangHai\" Nov 11 2014 3pm -  Dec 12 2014 4pm";
		assertEquals("Added: [Tue Nov 11 '14 15:00-Fri Dec 12 '14 16:00] \"Meeting In ShangHai\"",
				TaskCommander.controller.executeCommand(userCommand));
		TaskCommander.controller.executeCommand("clear");
	}

	@Test
	public void testAddTimeTaskByConfiguringOutCorrectDate()throws Exception{
		String userCommand = "add \"little boy\" 9pm - 3am";
		// get a calendar instance, which defaults to "now"
		Calendar calendar = Calendar.getInstance();

		// get a date to represent "today"
		Date today = calendar.getTime();

		calendar.add(Calendar.DAY_OF_YEAR, 1);
		Date tomorrow = calendar.getTime();

		assertEquals("Added: ["+Global.dayFormat.format(today)+" "+"21:00-"+ Global.dayFormat.format(tomorrow)+" 03:00] \"little boy\"",
				TaskCommander.controller.executeCommand(userCommand));
		TaskCommander.controller.executeCommand("clear");
	}

	/**Test Display Function
	 * */
	@Test
	public void testDisplayWithoutRestriction() throws Exception{
		String userCommand = "display";
		assertEquals("Displayed: all", TaskCommander.controller.executeCommand(userCommand));
		/*Assert.AreEqual(TaskCommander.controller.getDisplayedTasks(), RightObject);*/
		TaskCommander.controller.executeCommand("clear");
	}
	@Test
	public void testDisplayDone() throws Exception{
		String userCommand = "display done";
		assertEquals("Displayed: Status: done ", TaskCommander.controller.executeCommand(userCommand));
		TaskCommander.controller.executeCommand("clear");
	}
	@Test
	public void testDisplayOpen() throws Exception{
		String userCommand = "display open";
		assertEquals("Displayed: all", TaskCommander.controller.executeCommand(userCommand));
		TaskCommander.controller.executeCommand("clear");
	}
	@Test
	public void testDisplayTimedDealineOpen() throws Exception{
		String userCommand = "display timed deadline open";
		assertEquals("Displayed: Type: deadline, timed ", TaskCommander.controller.executeCommand(userCommand));
		TaskCommander.controller.executeCommand("clear");

	}

	//In this test it uses current time, so sometimes the date will have a slite difference
		@Test
	public void testDisplayDealineInPeriod() throws Exception{
		String userCommand = "display open deadline 04 Nov to 18 Nov";
		Date date  = new Date();
		assertEquals("Displayed: Date: [Tue Nov 4 '14 "+ Global.timeFormat.format(date)+"-"+" Tue Nov 18 '14 "+
				Global.timeFormat.format(date)+ "]  Type: deadline Status: open ", TaskCommander.controller.executeCommand(userCommand));

	}

	/**Test Update Function
	 * */

	//This is a boundry testing for invalid index 
	// index >=1
	@Test
	public void testUpdateTaskWithInvalidIndex() throws Exception{
		String userCommand = "update 0 3pm";
		TaskCommander.controller.getDisplayedTasks();
		Date date  = new Date();
		assertEquals(String.format(Global.MESSAGE_NO_INDEX, -1), TaskCommander.controller.executeCommand(userCommand));
		TaskCommander.controller.executeCommand("clear");

	}

	@Test
	public void testUpdateTaskWithTimeAdded() throws Exception{
		String userCommand = "add \"little boy\" 3pm";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "update 1 3pm";
		TaskCommander.controller.getDisplayedTasks();
		Date date  = new Date();
		assertEquals("Updated: [by "+Global.dayFormat.format(date)+" "+ "15:00] \"little boy\"", TaskCommander.controller.executeCommand(userCommand));
		TaskCommander.controller.executeCommand("clear");

	}
	@Test
	public void testUpdateTaskChangeContent() throws Exception{
		String userCommand = "add \"DON ADD LITTLE BOY AGAIN MAN\" 3 pm";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "update 1 \"early bird catches the worm\"";
		TaskCommander.controller.getDisplayedTasks();
		Date date = new Date();
		assertEquals("Updated: [by "+Global.dayFormat.format(date)+" "+ "15:00] \"early bird catches the worm\"", TaskCommander.controller.executeCommand(userCommand));
		TaskCommander.controller.executeCommand("clear");
	}
	@Test
	public void testUpdateDeadlineTaskToTimedTask() throws Exception{
		TaskCommander.controller.executeCommand("clear");
		String userCommand = "add \"welcome boss\" 3pm";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "update 1 Nov 11 3pm - 4 pm";
		TaskCommander.controller.getDisplayedTasks();
		assertEquals("Updated: [Tue Nov 11 '14 15:00-16:00] \"welcome boss\"", TaskCommander.controller.executeCommand(userCommand));
		TaskCommander.controller.executeCommand("clear");
	}

	@Test
	public void testUpdateTaskChangeTime() throws Exception{
		String userCommand = "add \"little boy\" 3 pm";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "update 1 none";
		TaskCommander.controller.getDisplayedTasks();
		assertEquals("Updated: \"little boy\"", TaskCommander.controller.executeCommand(userCommand));
		TaskCommander.controller.executeCommand("clear");
	}

	/**Test Mark Function
	 * */
	@Test
	public void testMarkDoneWithTimedTasksAdded() throws Exception{
		String userCommand = "add \"little boy\" 3 pm - 4 pm";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "done 1";
		TaskCommander.controller.executeCommand("display done");
		TaskCommander.controller.getDisplayedTasks();
		Date date = new Date();
		assertEquals("Done: ["+Global.dayFormat.format(date)+" "+ "15:00-16:00] \"little boy\"", TaskCommander.controller.executeCommand(userCommand));
		TaskCommander.controller.executeCommand("clear");
	}
	@Test
	public void testAlreadyOpen() throws Exception{
		String userCommand = "add \"open something\"";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "display open";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "open 1 ";
		TaskCommander.controller.getDisplayedTasks();
		assertEquals("Already opened.", TaskCommander.controller.executeCommand(userCommand));
		TaskCommander.controller.executeCommand("clear");
	}
	/**
	 * Testing delete
	 * @throws Exception
	 */
	@Test
	public void testDelete() throws Exception{
		String userCommand = "add \"little boy\" 3 pm";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "delete 1 ";
		TaskCommander.controller.getDisplayedTasks();
		Date date =  new Date();
		assertEquals("Deleted: [by "+Global.dayFormat.format(date)+" "+ "15:00] \"little boy\"", TaskCommander.controller.executeCommand(userCommand));
		TaskCommander.controller.executeCommand("clear");
	}
	/**
	 * Testing undo
	 * @throws Exception
	 */
	@Test
	public void testUndoAdd() throws Exception{
		String userCommand = "add \"little boy\" 3pm";
		Date date = new Date();
		assertEquals("Added: [by "+Global.dayFormat.format(date)+" "+"15:00] \"little boy\"",
				TaskCommander.controller.executeCommand(userCommand));
		userCommand = "undo";
		assertEquals("Undone latest command: ADD.",
				TaskCommander.controller.executeCommand(userCommand));
		TaskCommander.controller.executeCommand("clear");
	}

	@Test
	public void testUndoDelete() throws Exception{
		String userCommand = "add \"little boy\" 3pm";
		Date date = new Date();
		assertEquals("Added: [by "+Global.dayFormat.format(date)+" "+"15:00] \"little boy\"",
				TaskCommander.controller.executeCommand(userCommand));
		userCommand = "delete 1 ";
		TaskCommander.controller.getDisplayedTasks();
		assertEquals("Deleted: [by "+Global.dayFormat.format(date)+" "+ "15:00] \"little boy\"", TaskCommander.controller.executeCommand(userCommand));
		userCommand = "undo";
		assertEquals("Undone latest command: DELETE.",
				TaskCommander.controller.executeCommand(userCommand));
		TaskCommander.controller.executeCommand("clear");
	}

}
