package automatedTestDriver.Data;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Stack;

import org.junit.Test;

import com.taskcommander.Data;
import com.taskcommander.Task;

public class ClearInternalTest {
	Data tester;
	Stack<ArrayList<Task>> clearedTasks;
	ArrayList<Task> tasks;

	public ClearInternalTest() {
		tester = Data.getInstance();
	}
	
	@Test
	public void clearedTasksTest() {
		clearedTasks = tester.getClearedTasks();
		assertNotNull(clearedTasks);
	}
	
	@Test
	public void emptyClearTest() {
		String expected = "No tasks available";
		assertEquals(expected, tester.clearTasks());
	}
	
	@Test
	public void clearTest() {
		tester.addFloatingTask("Have Coffee");
		tester.addFloatingTask("Buy groceries");
		String expected = "All content deleted.";
		assertEquals(expected, tester.clearTasks());
	}
}
