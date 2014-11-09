package automatedTestDriver.Data;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;
import java.util.Calendar;

import org.junit.Test;
import com.taskcommander.Data;
import com.taskcommander.Task;

public class AddInternalTest {
	Data tester;
	Stack<Task> addedTests;
	ArrayList<Task> tasks;
	
	public AddInternalTest() {
		tester = Data.getInstance();
	}
	
	@Test
	public void addedTasksTests() {
		addedTests = tester.getAddedTasks();
		assertNotNull(addedTests);
	}
	
	@Test
	public void tasksTests() {
		tasks = tester.getAllTasks();
		assertNotNull(tasks);
	}
	
	@Test
	public void addFloatingTaskTest() {
		String expected = "Added: \"hello\"";
		assertEquals(expected, tester.addFloatingTask("hello"));
		
		tester.clearTasks();
	}
	
	@Test
	public void addDeadlineTaskTest() {
		String expected = "Added: [by Mon Nov 10 '14 15:00] \"Enjoy Life\"";
		
		Calendar cal = Calendar.getInstance();
		cal.set(2014, Calendar.NOVEMBER, 10, 15, 00);
		Date date = cal.getTime();

		assertEquals(expected, tester.addDeadlineTask("Enjoy Life", date));
		
		tester.clearTasks();
	}
	
	@Test
	public void addTimedTaskTest() {
		String expected = "Added: [Sun Dec 14 '14 15:00-Mon Dec 15 '14 15:00] \"Sing in the Rain\"";
		
		Calendar cal = Calendar.getInstance();
		cal.set(2014, Calendar.DECEMBER, 14, 15, 00);
		Date startDate = cal.getTime();
		cal.set(2014, Calendar.DECEMBER, 15, 15, 00);
		Date endDate = cal.getTime();
		
		assertEquals(expected, tester.addTimedTask("Sing in the Rain", startDate, endDate));
		
		tester.clearTasks();
	}
}
