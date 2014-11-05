package automatedTestDriver.Storage;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.taskcommander.FloatingTask;
import com.taskcommander.Global;
import com.taskcommander.Task;
import com.taskcommander.TaskAdapter;
import com.taskcommander.TaskCommander;

//@author A0112828H
/**
 * Tests reading and writing tasks to and from json storage file.
 * Clears all data in the global storage file before and after running.
 */
public class StorageTest {
	@Test
	public void testReadingNullTask() {
		clearFile();
		ArrayList<Task> tasks = new ArrayList<Task>();
		tasks.add(null);
		writeTasksToFile(tasks);
		assertEquals("Should not read null tasks.", 0, TaskCommander.storage.readFromFile().size());
		clearFile();
	}

	@Test
	public void testReadingOneTask() {
		clearFile();
		ArrayList<Task> tasks = new ArrayList<Task>();
		tasks.add(new FloatingTask("Test Task 1"));
		writeTasksToFile(tasks);
		assertEquals("Should read single task.", 1, TaskCommander.storage.readFromFile().size());
		assertEquals("Should have same task name.", "Test Task 1", TaskCommander.storage.readFromFile().get(0).getName());
		clearFile();
	}

	@Test
	public void testReadingSomeTasks() {
		clearFile();
		ArrayList<Task> tasks = new ArrayList<Task>();
		tasks.add(new FloatingTask("Test Task 1"));
		tasks.add(new FloatingTask("Test Task 2"));
		tasks.add(new FloatingTask("Test Task 3"));
		writeTasksToFile(tasks);
		assertEquals("Should read 3 tasks.", 3, TaskCommander.storage.readFromFile().size());
		assertEquals("Should have same task name.", "Test Task 1", TaskCommander.storage.readFromFile().get(0).getName());
		assertEquals("Should have same task name.", "Test Task 2", TaskCommander.storage.readFromFile().get(1).getName());
		assertEquals("Should have same task name.", "Test Task 3", TaskCommander.storage.readFromFile().get(2).getName());
		clearFile();
	}

	@Test
	public void testWritingNullTask() {
		clearFile();
		ArrayList<Task> tasks = new ArrayList<Task>();
		tasks.add(null);
		TaskCommander.storage.writeToFile(tasks);
		assertEquals("Make sure null task is not written to file.", 0, countLinesInFile());
		clearFile();
	}

	@Test
	public void testWritingOneTask() {
		clearFile();
		ArrayList<Task> tasks = new ArrayList<Task>();
		tasks.add(new FloatingTask("Test Task 1"));
		TaskCommander.storage.writeToFile(tasks);
		assertEquals("Make sure there is only 1 line", 1, countLinesInFile());
		clearFile();
	}

	@Test
	public void testWritingSomeTasks() {
		clearFile();
		ArrayList<Task> tasks = new ArrayList<Task>();
		tasks.add(new FloatingTask("Test Task 1"));
		tasks.add(new FloatingTask("Test Task 2"));
		tasks.add(new FloatingTask("Test Task 3"));
		TaskCommander.storage.writeToFile(tasks);
		assertEquals("Make sure there are 3 lines", 3, countLinesInFile());
		clearFile();
	}

	private void writeTasksToFile(ArrayList<Task> tasks) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Task.class, new TaskAdapter());
		Gson gson = gsonBuilder.create();
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(new File(Global.FILENAME)));
			for (Task t : tasks) {
				bw.write(gson.toJson(t));
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {
			System.out.println(Global.MESSAGE_EXCEPTION_IO);
		}
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
	
	private void clearFile() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(Global.FILENAME), false));
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
