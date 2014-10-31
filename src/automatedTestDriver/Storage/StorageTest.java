package automatedTestDriver.Storage;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import com.taskcommander.FloatingTask;
import com.taskcommander.Global;
import com.taskcommander.Task;
import com.taskcommander.TaskCommander;

//@author A0112828H
/**
* Tests specific methods in the GoogleAPIConnector.
* Requires saved login credentials.
* Login through the normal flow to get login credentials first before testing this.
*
*/
public class StorageTest {

	@Test
	public void testWritingOneTask() {
		ArrayList<Task> tasks = new ArrayList<Task>();
		tasks.add(new FloatingTask("Test Task 1"));
		TaskCommander.storage.writeToFile(tasks);
		assertEquals("Make sure there is only 1 line", countLinesInFile(), 1);
	}
	
	@Test
	public void testWritingSomeTasks() {
		ArrayList<Task> tasks = new ArrayList<Task>();
		tasks.add(new FloatingTask("Test Task 1"));
		tasks.add(new FloatingTask("Test Task 2"));
		tasks.add(new FloatingTask("Test Task 3"));
		TaskCommander.storage.writeToFile(tasks);
		assertEquals("Make sure there are 3 lines", countLinesInFile(), 3);
	}
	
	private int countLinesInFile() {
		File file = new File(Global.FILENAME);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			int count = 0;
			while (br.readLine()!= null) {
				count++;
			}
			br.close();
			return count;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
