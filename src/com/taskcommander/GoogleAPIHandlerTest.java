package com.taskcommander;
import com.taskcommander.LoginManager;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.tasks.Tasks;

public class GoogleAPIHandlerTest {

	@Test
	public void testLoginManager() {
		LoginManager manager = LoginManager.getInstance();
		Tasks tasks = manager.getTasksService();
		Calendar calendar = manager.getCalendarService();
		assertEquals("Able to get TasksService", tasks!=null);
		assertEquals("Able to get CalendarService", calendar!=null);
	}

}
