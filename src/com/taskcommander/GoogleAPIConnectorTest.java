package com.taskcommander;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.taskcommander.LoginManager;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.tasks.Tasks;

//@author A0112828H
/**
 * Tests specific methods in the GoogleAPIConnector.
 * Requires saved login credentials.
 * Login through the normal flow to get login credentials first before testing this.
 *
 */
public class GoogleAPIConnectorTest {
	GoogleAPIConnector con;

	public void login() {
		if (con == null) {
			con = GoogleAPIConnector.getInstance();
		}
		
		if (!con.getServices()) {
			try {
				wait(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testLoginFlow() {
		login();
		assertNotNull("Able to get Tasks and Calendar services.", con.getServices());
	}

	@Test
	public void testAddOneFloatingTask() {
		login();
		FloatingTask task = new FloatingTask("Test Task 1");
		assertNotNull(con.addTask(task));
		con.deleteTask(task);
	}

	@Test
	public void testAddOneDeadlineTask() {
		login();
		DeadlineTask task = new DeadlineTask("Test Task 1", new Date(System.currentTimeMillis()));
		assertNotNull(con.addTask(task));
		con.deleteTask(task);
	}

	@Test
	public void testAddOneTimedTask() {
		login();
		TimedTask task = new TimedTask("Test Task 1", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()+2000));
		assertNotNull(con.addTask(task));
		con.deleteTask(task);
	}
	
	@Test
	public void testGetOneFloatingTask() {
		login();
		FloatingTask task = new FloatingTask("Test Task 1");
		String id = con.addTask(task);
		assertNotNull(id);
		task.setId(id);
		assertNotNull(con.getTask(task));
		con.deleteTask(task);
	}

	@Test
	public void testGetOneDeadlineTask() {
		login();
		DeadlineTask task = new DeadlineTask("Test Task 1", new Date(System.currentTimeMillis()));
		String id = con.addTask(task);
		assertNotNull(id);
		task.setId(id);
		assertNotNull(con.getTask(task));
		con.deleteTask(task);
	}

	@Test
	public void testGetOneTimedTask() {
		login();
		TimedTask task = new TimedTask("Test Task 1", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()+2000));
		String id = con.addTask(task);
		assertNotNull(id);
		task.setId(id);
		assertNotNull(con.getTask(task));
		con.deleteTask(task);
	}
	
	@Test
	public void testUpdateOneFloatingTask() {
		login();
		FloatingTask task = new FloatingTask("Test Task 1");
		String id = con.addTask(task);
		assertNotNull(id);
		task.setId(id);
		task.setName("Changed Test Task 1");
		assertTrue(con.updateTask(task));
		con.deleteTask(task);
	}

	@Test
	public void testUpdateOneDeadlineTask() {
		login();
		DeadlineTask task = new DeadlineTask("Test Task 1", new Date(System.currentTimeMillis()));
		String id = con.addTask(task);
		assertNotNull(id);
		task.setId(id);
		task.setName("Changed Test Task 1");
		assertTrue(con.updateTask(task));
		con.deleteTask(task);
	}

	@Test
	public void testUpdateOneTimedTask() {
		login();
		TimedTask task = new TimedTask("Test Task 1", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()+2000));
		String id = con.addTask(task);
		assertNotNull(id);
		task.setId(id);
		task.setName("Changed Test Task 1");
		assertTrue(con.updateTask(task));
		con.deleteTask(task);
	}
	
	@Test
	public void testDeleteOneFloatingTask() {
		login();
		FloatingTask task = new FloatingTask("Test Task 1");
		String id = con.addTask(task);
		assertNotNull(id);
		task.setId(id);
		assertNotNull(task.getId());
		assertTrue(con.deleteTask(task));
	}

	@Test
	public void testDeleteOneDeadlineTask() {
		login();
		DeadlineTask task = new DeadlineTask("Test Task 1", new Date(System.currentTimeMillis()));
		String id = con.addTask(task);
		assertNotNull(id);
		task.setId(id);
		assertNotNull(task.getId());
		assertTrue(con.deleteTask(task));
	}

	@Test
	public void testDeleteOneTimedTask() {
		login();
		TimedTask task = new TimedTask("Test Task 1", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()+2000));
		String id = con.addTask(task);
		assertNotNull(id);
		task.setId(id);
		assertNotNull(task.getId());
		assertTrue(con.deleteTask(task));
	}
	
	//TODO CRUD mixed set of tasks

}
