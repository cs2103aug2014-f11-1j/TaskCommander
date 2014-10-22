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
		con = GoogleAPIConnector.getInstance();
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
	}
	
	@Test
	public void testAddOneDeadlineTask() {
		login();
		DeadlineTask task = new DeadlineTask("Test Task 1", new Date(System.currentTimeMillis()));
		assertNotNull(con.addTask(task));
	}
	
	@Test
	public void testAddOneTimedTask() {
		login();
		TimedTask task = new TimedTask("Test Task 1", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()+2000));
		assertNotNull(con.addTask(task));
	}
	
	//CRUD 1 task of each type
	//CRUD mixed set of tasks
	
	@Test
	public void testGetTasks() {
		login();
		
	}
	
	@Test
	public void testLoginManager() {
		login();
		
	}

}
