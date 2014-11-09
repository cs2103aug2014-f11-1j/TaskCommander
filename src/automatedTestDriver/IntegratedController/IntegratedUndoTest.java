package automatedTestDriver.IntegratedController;


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
 * This class contains all test cases for the Integrated Testing of the undo method.
 */
//@author A0105753J
@RunWith(Parameterized.class)
public class IntegratedUndoTest {

	private String userCommand;
	private String expectedResult;

	public IntegratedUndoTest(String userCommand, String expectedResult) {
		this.userCommand = userCommand;
		this.expectedResult = expectedResult;
	}
/*
	@Before
	public void ini(){
		TaskCommander.ini();
	}
*/
	/*
	 * Test cases for undo.
	 * Format: undo n (index)
	 */
	@Parameterized.Parameters
	public static Collection<Object[]>  cases() {
		
		return Arrays.asList(new Object[][] {
				//{ openCommand[0]+" "+index[6], "Opened: [Thu Oct 30 '14 "+Global.timeFormat.format(today)+"-Mon Nov 10 '14 "+Global.timeFormat.format(today)+"] \"Finish V0.5 in 10 days\""},
				{"add \"Content\"", "Undo: Add"},
	
				
				

		});
	}

	@Test
	public void testUndoAdd() {
		TaskCommander.controller.executeCommand(userCommand);
		assertEquals(expectedResult, TaskCommander.controller.executeCommand("undo")); 
	}


}
