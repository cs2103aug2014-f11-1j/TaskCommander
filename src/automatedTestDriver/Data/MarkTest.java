package automatedTestDriver.Data;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

import org.junit.Test;

import com.taskcommander.Data;
import com.taskcommander.Task;

public class MarkTest {
	public Data tester;
	ArrayList<Task> tasks;
	Calendar cal;
	Date start;
	Date end;
	String title;
	
	public MarkTest() {
		tester = Data.getInstance();
		tester.clearTasks();
		tasks = tester.getAllTasks();
		cal = Calendar.getInstance();
		cal.set(2014, Calendar.NOVEMBER, 10, 15, 00);
		start = cal.getTime();
		cal.set(2014,  Calendar.NOVEMBER, 10, 16, 00);
		end = cal.getTime();
		title = "Watch a Movie";
	}
	
	@Test
	public void DoneTest() {		
		tester.clearTasks();
		
		tester.addFloatingTask(title);
		tester.open(0);
		tester.addDeadlineTask(title, start);
		tester.open(1);
		tester.addTimedTask(title, start, end);
		tester.open(2);
		
		assertEquals("Done: \"Watch a Movie\"", tester.done(0));
		assertEquals("Done: [by Mon Nov 10 '14 15:00] \"Watch a Movie\"", tester.done(1));
		assertEquals("Done: [Mon Nov 10 '14 15:00-16:00] \"Watch a Movie\"", tester.done(2));
		assertEquals("Index does not exist. Please type a valid index.", tester.done(3));
		assertEquals("Index does not exist. Please type a valid index.", tester.done(-1));
		assertEquals("Already done.", tester.done(0));		
	}
	
	@Test
	public void OpenTest() {
		tester.clearTasks();
		
		tester.addFloatingTask(title);
		tester.done(0);
		tester.addDeadlineTask(title, start);
		tester.done(1);
		tester.addTimedTask(title, start, end);
		tester.done(2);
		
		assertEquals("Opened: \"Watch a Movie\"", tester.open(0));
		assertEquals("Opened: [by Mon Nov 10 '14 15:00] \"Watch a Movie\"", tester.open(1));
		assertEquals("Opened: [Mon Nov 10 '14 15:00-16:00] \"Watch a Movie\"", tester.open(2));
		assertEquals("Index does not exist. Please type a valid index.", tester.open(3));
		assertEquals("Index does not exist. Please type a valid index.", tester.open(-1));
		assertEquals("Already opened.", tester.open(0));
	}
}
