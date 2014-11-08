package automatedTestDriver.Data;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Stack;
import java.util.Date;

import org.junit.Test;

import com.taskcommander.Data;
import com.taskcommander.Task;

public class InternalUpdateTest {
	Data tester;
	Stack<Task> preupdatedTasks;
	Stack<Task> updatedTasks;
	Stack<Task> changedTypeTasks;
	ArrayList<Task> tasks;
	
	public InternalUpdateTest() {
		tester = Data.getInstance();
	}
	
	@Test
	public void preupdatedTasksTest() {
		preupdatedTasks = tester.getPreupdatedTasks();
		assertNotNull(preupdatedTasks);
	}
	
	@Test
	public void updatedTasksTest() {
		updatedTasks = tester.getUpdatedTasks();
		assertNotNull(updatedTasks);
	}
	
	@Test
	public void changedTypeTasksTest() {
		changedTypeTasks = tester.getChangedTypeTasks();
		assertNotNull(changedTypeTasks);
	}
	
	@Test
	public void invalidIndexTest() {
		tester.addFloatingTask("Hello");
		int index1 = -1;
		int index2 = 10;
		String expected = "Index does not exist. Please type a valid index.";
		assertEquals(expected, tester.updateToFloatingTask(index1, "Hello"));
		assertEquals(expected, tester.updateToFloatingTask(index2, "Konnichiwa"));
	}
	
	@Test
	public void emptyUpdateTest() {
		tester.clearTasks();
		String expected = "No tasks available";
		assertEquals(expected, tester.updateToFloatingTask(0, "Dummy"));
		assertEquals(expected, tester.updateToDeadlineTask(0, null, null));
		assertEquals(expected, tester.updateToTimedTask(0, null, null, null));
	}
	
	@Test
	public void updateToFloatingTaskTest() {
		tester.clearTasks();
		tester.addFloatingTask("Become a philosopher king");
		String expected = "Updated: \"Become a metaphysics king\"";
		assertEquals(expected, tester.updateToFloatingTask(0, "Become a metaphysics king"));
		preupdatedTasks = tester.getPreupdatedTasks();
		updatedTasks = tester.getUpdatedTasks();
		
		tester.clearTasks();
		
		Calendar cal = Calendar.getInstance();
		cal.set(2014, Calendar.NOVEMBER, 10, 15, 00);
		Date date = cal.getTime();
		tester.addDeadlineTask("Eat", date);
		expected = "Updated: \"Eat Smores\"";
		assertEquals(expected, tester.updateToFloatingTask(0, "Eat Smores"));
		
		tester.clearTasks();
		
		cal.set(2014, Calendar.NOVEMBER, 10, 15, 00);
		Date start = cal.getTime();
		cal.set(2014, Calendar.DECEMBER, 10, 16, 00);
		Date end = cal.getTime();
		tester.addTimedTask("Find Nemo", start, end);
		expected = "Updated: \"Find Nemo and Father\"";
		assertEquals(expected, tester.updateToFloatingTask(0, "Find Nemo and Father"));
		
		tester.clearTasks();
	}
	
	@Test
	public void updateToDeadlineTaskTest(){
		tester.clearTasks();
		Calendar cal = Calendar.getInstance();
		cal.set(2014, Calendar.NOVEMBER, 10, 15, 00);
		Date date = cal.getTime();
		tester.addDeadlineTask("Have a KitKat", date);
		
		cal.set(2014, Calendar.NOVEMBER, 10, 16, 00);
		Date newDate = cal.getTime();
		String newTitle = "Have a Kit-Kat";
		String expected = "Updated: [by Mon Nov 10 '14 16:00] \"Have a Kit-Kat\"";
		assertEquals(expected, tester.updateToDeadlineTask(0, newTitle, newDate));
		
		tester.clearTasks();
		tester.addFloatingTask("Have a KitKat");
		assertEquals(expected, tester.updateToDeadlineTask(0, newTitle, newDate));
		
		tester.clearTasks();
		tester.addTimedTask("Have a KitKat", date, newDate);
		assertEquals(expected, tester.updateToDeadlineTask(0, newTitle, newDate));
		
		tester.clearTasks();
		tester.addFloatingTask("Have a KitKat");
		expected = "Invalid arguments given.";
		assertEquals(expected, tester.updateToDeadlineTask(0, newTitle, null));
		
		tester.clearTasks();
	}
	
	
	@Test
	public void updateToTimedTaskTest() {
		tester.clearTasks();
		Calendar cal = Calendar.getInstance();
		cal.set(2014, Calendar.NOVEMBER, 10, 15, 00);
		Date start = cal.getTime();
		cal.set(2014,  Calendar.NOVEMBER, 10, 16, 00);
		Date end = cal.getTime();
		String title = "Watch a Movie";
		tester.addTimedTask(title, start, end);
		
		cal.set(2014, Calendar.NOVEMBER, 11, 16, 00);
		Date newStart = cal.getTime();
		cal.set(2014, Calendar.NOVEMBER, 11, 17, 00);
		Date newEnd = cal.getTime();
		String newTitle = "Watch Interstellar";
		String expected = "Updated: [Tue Nov 11 '14 16:00-17:00] \"Watch Interstellar\"";
		assertEquals(expected, tester.updateToTimedTask(0, newTitle, newStart, newEnd));
		
		tester.clearTasks();
		tester.addFloatingTask("Watch a movie");
		assertEquals(expected, tester.updateToTimedTask(0, newTitle, newStart, newEnd));
		
		tester.clearTasks();
		tester.addDeadlineTask(title, end);
		assertEquals(expected, tester.updateToTimedTask(0, newTitle, newStart, newEnd));

		tester.clearTasks();
		tester.addFloatingTask("Have a KitKat");
		expected = "Invalid arguments given.";
		assertEquals(expected, tester.updateToTimedTask(0, newTitle, null, null));
		
		tester.clearTasks();
	}
}
