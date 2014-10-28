package automatedTestDriver.GoogleIntegration;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.taskcommander.DeadlineTask;
import com.taskcommander.FloatingTask;
import com.taskcommander.GoogleAPIConnector;
import com.taskcommander.LoginManager;
import com.taskcommander.TimedTask;

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
	
	// Test null inputs for CRUD
	@Test
	public void testAddNullTask() {
		login();
		assertNull(con.addTask(null));
	}
	
	@Test
	public void testGetNullTask() {
		login();
		assertNull(con.getTask(null));
	}
	
	@Test
	public void testUpdateNullTask() {
		login();
		assertFalse(con.updateTask(null));
	}
	
	@Test
	public void testDeleteNullTask() {
		login();
		assertFalse(con.deleteTask(null));
	}
	
	// Test unsynced task inputs for get, update and delete
	@Test
	public void testUpdateUnsyncedFloatingTask() {
		login();
		FloatingTask task = new FloatingTask("Update Unsynced Floating Task");
		assertFalse(con.updateTask(task));
	}
	
	@Test
	public void testUpdateUnsyncedDeadlineTask() {
		login();
		DeadlineTask task = new DeadlineTask("Update Unsynced Deadline Task", new Date(System.currentTimeMillis()));
		assertFalse(con.updateTask(task));
	}
	
	@Test
	public void testUpdateUnsyncedTimedTask() {
		login();
		TimedTask task = new TimedTask("Update Unsynced Timed Task", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()+2000));
		assertFalse(con.updateTask(task));
	}
	
	// null or false credentials
	
	// Test special case inputs for CRUD

	// Test normal task inputs for each task type for CRUD
	@Test
	public void testAddOneFloatingTask() {
		login();
		FloatingTask task = new FloatingTask("Add Floating Task");
		String id = con.addTask(task);
		assertNotNull(id);
		task.setId(id);
		assertNotNull(task.getId());
		assertTrue(con.deleteTask(task));
	}

	@Test
	public void testAddOneDeadlineTask() {
		login();
		DeadlineTask task = new DeadlineTask("Add Deadline Task", new Date(System.currentTimeMillis()));
		String id = con.addTask(task);
		assertNotNull(id);
		task.setId(id);
		assertNotNull(task.getId());
		assertTrue(con.deleteTask(task));
	}

	@Test
	public void testAddOneTimedTask() {
		login();
		TimedTask task = new TimedTask("Add Timed Task", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()+2000));
		String id = con.addTask(task);
		assertNotNull(id);
		task.setId(id);
		assertNotNull(task.getId());
		assertTrue(con.deleteTask(task));
	}
	
	@Test
	public void testGetOneFloatingTask() {
		login();
		FloatingTask task = new FloatingTask("Get Floating Task");
		String id = con.addTask(task);
		assertNotNull(id);
		task.setId(id);
		assertNotNull(task.getId());
		assertNotNull(con.getTask(task));
		assertTrue(con.deleteTask(task));
	}

	@Test
	public void testGetOneDeadlineTask() {
		login();
		DeadlineTask task = new DeadlineTask("Get Deadline Task", new Date(System.currentTimeMillis()));
		String id = con.addTask(task);
		assertNotNull(id);
		task.setId(id);
		assertNotNull(con.getTask(task));
		assertTrue(con.deleteTask(task));
	}

	@Test
	public void testGetOneTimedTask() {
		login();
		TimedTask task = new TimedTask("Get Timed Task", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()+2000));
		String id = con.addTask(task);
		assertNotNull(id);
		task.setId(id);
		assertNotNull(con.getTask(task));
		assertTrue(con.deleteTask(task));
	}
	
	@Test
	public void testUpdateOneFloatingTask() {
		login();
		FloatingTask task = new FloatingTask("Update Floating Task");
		String id = con.addTask(task);
		assertNotNull(id);
		task.setId(id);
		task.setName("Update Floating Task Changed");
		assertTrue(con.updateTask(task));
		assertTrue(con.deleteTask(task));
	}

	@Test
	public void testUpdateOneDeadlineTask() {
		login();
		DeadlineTask task = new DeadlineTask("Update Deadline Task", new Date(System.currentTimeMillis()));
		String id = con.addTask(task);
		assertNotNull(id);
		task.setId(id);
		task.setName("Update Deadline Task Changed");
		assertTrue(con.updateTask(task));
		assertTrue(con.deleteTask(task));
	}

	@Test
	public void testUpdateOneTimedTask() {
		login();
		TimedTask task = new TimedTask("Update Timed Task", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()+2000));
		String id = con.addTask(task);
		assertNotNull(id);
		task.setId(id);
		task.setName("Update Timed Task Changed");
		assertTrue(con.updateTask(task));
		assertTrue(con.deleteTask(task));
	}
	
	@Test
	public void testDeleteOneFloatingTask() {
		login();
		FloatingTask task = new FloatingTask("Delete Floating Task");
		String id = con.addTask(task);
		assertNotNull(id);
		task.setId(id);
		assertNotNull(task.getId());
		assertTrue(con.deleteTask(task));
		assertNull(con.getTask(task));
	}

	@Test
	public void testDeleteOneDeadlineTask() {
		login();
		DeadlineTask task = new DeadlineTask("Delete Deadline Task", new Date(System.currentTimeMillis()));
		String id = con.addTask(task);
		assertNotNull(id);
		task.setId(id);
		assertNotNull(task.getId());
		assertTrue(con.deleteTask(task));
		assertNull(con.getTask(task));
	}

	@Test
	public void testDeleteOneTimedTask() {
		login();
		TimedTask task = new TimedTask("Delete Timed Task", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()+2000));
		String id = con.addTask(task);
		assertNotNull(id);
		task.setId(id);
		assertNotNull(task.getId());
		assertTrue(con.deleteTask(task));
		assertNull(con.getTask(task));
	}

}
