package automatedTestDriver.Data;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.junit.Test;

import com.taskcommander.Data;
import com.taskcommander.Task;

public class DeleteGoogleTest {
	Data tester;
	ArrayList<Task> tasks;
	
	public DeleteGoogleTest() {
		tester = Data.getInstance();
		tasks = tester.getAllTasks();
	}
	
	@Test
	public void emptyTasksTests() {
		tester.clearTasks();
		int index = 0;
		assertFalse(tester.deleteFromGoogle(index));
	}
	
	@Test
	public void deleteTest() {
		tester.addFloatingTask("Watch a Movie");
		assertTrue(tester.deleteFromGoogle(0));
		tester.clearTasks();
	}
}
