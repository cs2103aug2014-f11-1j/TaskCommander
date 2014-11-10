package automatedTestDriver.Data;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.taskcommander.Global.CommandType;
import com.taskcommander.Data;

//@author A0109194A
/**
 * Test for undo command
 *
 */
@RunWith(Parameterized.class)
public class UndoTest {
	private String expectedResult;
	public static Data tester;
	
	public UndoTest(String expectedResult) {
		this.expectedResult = expectedResult;
	}
	
	//Test Parameters
	@Parameterized.Parameters
	public static Collection<Object[]> cases() {
		tester = Data.getInstance();
		tester.clearTasks();
		tester.clearOperationHistory();
		
		tester.addFloatingTask("hello");
		tester.updateToFloatingTask(0,"konnichiwa");
		tester.deleteTask(0);
		tester.addFloatingTask("Guten Morgen");
		tester.clearTasks();
		String undoMessage = "Undone latest command: ";
		
		
		return Arrays.asList(new Object[][] {
			{ undoMessage + CommandType.CLEAR + "." },
			{ undoMessage + CommandType.ADD + "." },
			{ undoMessage + CommandType.DELETE + "." },
			{ undoMessage + CommandType.UPDATE + "." },
			{ undoMessage + CommandType.ADD + "." },
			{ "No commands to undo" }
		});
	}
	
	// Test run
   	@Test
	public void testUndo() {
		assertEquals(expectedResult, tester.undo()); 
	}

}
