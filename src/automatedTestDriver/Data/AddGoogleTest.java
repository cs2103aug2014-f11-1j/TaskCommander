package automatedTestDriver.Data;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

import org.junit.Test;

import com.taskcommander.Data;
import com.taskcommander.DeadlineTask;
import com.taskcommander.FloatingTask;
import com.taskcommander.Task;
import com.taskcommander.TimedTask;

//@author A0109194A
/**
 * Test for Adding Tasks, called by SyncHandler
 *
 */

public class AddGoogleTest {
	Data tester;
	ArrayList<Task> tasks;
	Calendar cal;
	Date start;
	Date end;
	String title;
	String expected;
	String id;
	
	public AddGoogleTest() {
		tester = Data.getInstance();
		tasks = tester.getAllTasks();
		cal = Calendar.getInstance();
		cal.set(2014, Calendar.NOVEMBER, 10, 15, 00);
		start = cal.getTime();
		cal.set(2014,  Calendar.NOVEMBER, 10, 16, 00);
		end = cal.getTime();
		title = "Watch a Movie";
		id = "Anything";
	}
	
	@Test
	public void nullIdTest() {
		FloatingTask task = new FloatingTask(title);
		expected = "Task has no ID.";
		assertEquals(expected, tester.addTask(task));
		
		tester.clearTasks();
	}
	
	
	@Test
	public void addFloatingTaskTest() {
		FloatingTask task = new FloatingTask(title);
		expected = "Added: \"Watch a Movie\"";
		task.setId(id);
		assertEquals(expected, tester.addTask(task));
		
		tester.clearTasks();
	}
	
	@Test
	public void addDeadlineTaskTest() {
		DeadlineTask task = new DeadlineTask(title, start);
		expected = "Added: [by Mon Nov 10 '14 15:00] \"Watch a Movie\"";
		task.setId(id);
		assertEquals(expected, tester.addTask(task));
		
		tester.clearTasks();
	}
	
	@Test
	public void addTimedTask() {
		TimedTask task = new TimedTask(title, start, end);
		expected = "Added: [Mon Nov 10 '14 15:00-16:00] \"Watch a Movie\"";
		task.setId(id);
		assertEquals(expected, tester.addTask(task));
		
		tester.clearTasks();
	}
}
