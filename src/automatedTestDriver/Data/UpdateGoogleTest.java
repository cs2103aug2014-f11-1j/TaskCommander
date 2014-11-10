package automatedTestDriver.Data;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.taskcommander.Data;
import com.taskcommander.DeadlineTask;
import com.taskcommander.FloatingTask;
import com.taskcommander.Task;
import com.taskcommander.TimedTask;

//@author A0109194A
/**
 * Test for update method called by SyncHandler
 *
 */
public class UpdateGoogleTest {
	Data tester;
	ArrayList<Task> tasks;
	Calendar cal;
	Date start;
	Date end;
	Date newStart;
	Date newEnd;
	String title;
	String newTitle;
	String expected;
	
	public UpdateGoogleTest() {
		tester = Data.getInstance();
		tasks = tester.getAllTasks();
		cal = Calendar.getInstance();
		cal.set(2014, Calendar.NOVEMBER, 10, 15, 00);
		start = cal.getTime();
		cal.set(2014,  Calendar.NOVEMBER, 10, 16, 00);
		end = cal.getTime();
		cal.set(2014, Calendar.NOVEMBER, 11, 15, 00);
		newStart = cal.getTime();
		cal.set(2014, Calendar.NOVEMBER, 11, 16, 00);
		newEnd = cal.getTime();
		title = "Watch a Movie";
		newTitle = "Watch Interstellar";
	}
	
	@Test
	public void updateToFloatingTaskTest() {
		tester.clearTasks();
		tester.addFloatingTask(title);
		FloatingTask newTask = new FloatingTask(newTitle);
		expected = "Updated: \"Watch Interstellar\"";
		assertEquals(expected, tester.updateToFloatingTask(0, newTask));
		
		tester.addDeadlineTask(title, start);
		assertEquals(expected, tester.updateToFloatingTask(1, newTask));
		
		tester.addTimedTask(title, start, end);
		assertEquals(expected, tester.updateToFloatingTask(2, newTask));
		
		tester.clearTasks();
	}
	
	@Test
	public void updateToDeadlineTaskTest() {
		tester.clearTasks();
		tester.addDeadlineTask(title, start);
		DeadlineTask newTask = new DeadlineTask(newTitle, newStart);
		expected = "Updated: [by Tue Nov 11 '14 15:00] \"Watch Interstellar\"";
		assertEquals(expected, tester.updateToDeadlineTask(0, newTask));
		
		tester.addFloatingTask(title);
		assertEquals(expected, tester.updateToDeadlineTask(1, newTask));
		
		tester.addTimedTask(title, start, end);
		assertEquals(expected, tester.updateToDeadlineTask(2, newTask));
		
		tester.clearTasks();
	}
	
	@Test
	public void updateToTimedTaskTest() {
		tester.clearTasks();
		tester.addTimedTask(title, start, end);
		TimedTask newTask = new TimedTask(newTitle, newStart, newEnd);
		expected = "Updated: [Tue Nov 11 '14 15:00-16:00] \"Watch Interstellar\"";
		assertEquals(expected, tester.updateToTimedTask(0, newTask));
		
		tester.addFloatingTask(title);
		assertEquals(expected, tester.updateToTimedTask(1, newTask));
		
		tester.addDeadlineTask(title, start);
		assertEquals(expected, tester.updateToTimedTask(2, newTask));
		
		tester.clearTasks();
	}
	
}
