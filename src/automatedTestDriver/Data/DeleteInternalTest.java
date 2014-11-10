package automatedTestDriver.Data;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.junit.Test;
import com.taskcommander.Data;
import com.taskcommander.Task;

//@author A0109194A
/**
 * Test for intenral delete command
 *
 */
public class DeleteInternalTest {
	Data tester;
	ArrayList<Task> deletedTasks;
	ArrayList<Task> tasks;
	
	public DeleteInternalTest() {
		tester = Data.getInstance();
	}
	
	@Test
	public void deletedTasksTest() {
		deletedTasks = tester.getDeletedTasks();
		assertNotNull(deletedTasks);
	}
		
	@Test
	public void invalidIndexTest() {
		tester.addFloatingTask("Hello");
		int index1 = -1;
		int index2 = 10;
		String expected = "Index does not exist. Please type a valid index.";
		assertEquals(expected, tester.deleteTask(index1));
		assertEquals(expected, tester.deleteTask(index2));
	}
	
	@Test
	public void emptyDeleteTest() {
		tester.clearTasks();
		String expected = "No tasks available";
		assertEquals(expected, tester.deleteTask(1));
	}
	
	@Test
	public void deleteTest() {
		tester.clearTasks();
		tester.addFloatingTask("Meet Richard");
		String expected = "Deleted: \"Meet Richard\"";
		assertEquals(expected, tester.deleteTask(0));
		tasks = tester.getAllTasks();
		deletedTasks = tester.getDeletedTasks();
	}
}
