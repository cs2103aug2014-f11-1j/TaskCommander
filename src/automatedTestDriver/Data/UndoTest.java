package automatedTestDriver.Data;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Stack;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.taskcommander.Global.CommandType;
import com.taskcommander.TaskCommander;

@RunWith(Parameterized.class)
public class UndoTest {
	private String expectedResult;
	
	public UndoTest(String expectedResult) {
		this.expectedResult = expectedResult;
	}
	
	//Test Parameters
	@Parameterized.Parameters
	public static Collection<Object[]> cases() {
		TaskCommander.data.clearTasks();
		Stack<CommandType> operationHistory = TaskCommander.data.getOperationsHistory();
		operationHistory.clear();
		
		TaskCommander.data.addFloatingTask("hello");
		TaskCommander.data.updateToFloatingTask(0,"konnichiwa");
		TaskCommander.data.deleteTask(0);
		TaskCommander.data.addFloatingTask("Guten Morgen");
		TaskCommander.data.clearTasks();
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
		assertEquals(expectedResult, TaskCommander.data.undo()); 
	}

}
