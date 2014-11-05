package automatedTestDriver.Integrated;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.taskcommander.Global;
import com.taskcommander.TaskCommander;

/**
 * This class contains all test cases for the Integrated Testing of the sync method.
 */
//@author A0105753J
@RunWith(Parameterized.class)
public class IntegratedSyncTest {

	private String userCommand;
	private String expectedResult;

	public IntegratedSyncTest(String userCommand, String expectedResult) {
		this.userCommand = userCommand;
		this.expectedResult = expectedResult;
	}

	/*
	 * Test cases for sync 
	 * Format: sync
	 */
	@Parameterized.Parameters
	public static Collection<Object[]>  cases() {
		String syncCommand[] = {"sync","SYNC", "Sync", "sYNc"};
		return Arrays.asList(new Object[][] {
				{ syncCommand[0], "Sync in progress..."},
				{ syncCommand[1], "Sync in progress..."},
				{ syncCommand[2], "Sync in progress..."},
				{ syncCommand[3], "Sync in progress..."},
		});
	}

	@Test
	public void testcontainsParameter() {
		assertEquals(expectedResult, TaskCommander.controller.executeCommand(userCommand)); 
	}
}
