package automatedTestDriver.Parser;
import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import com.taskcommander.Global;
import com.taskcommander.TaskCommander;

//@author A0128620M
/**
 * This class contains all test cases for the method determineCommandType(userCommand:String) of the component Parser.
 */

@RunWith(Parameterized.class)
public class DetermineCommandTypeTest {
	private String userCommand;
	private Global.CommandType expectedResult;

	public DetermineCommandTypeTest(String userCommand, Global.CommandType expectedResult) {
		this.userCommand = userCommand;
		this.expectedResult = expectedResult;
	}
	
	/* Test structure
	 * 
	 * Initial partition of parameter "userCommand":
	 * - [commandType]+[space(s)]+[any non-empty string]										valid
	 * - [commandType]																			valid	 * 
	 * - [space(s)]+[commandType]+[space(s)]+[any non-empty string]								valid
	 * - [space(s)]+[commandType]																valid 
	 * - [commandType]+[any non-empty string]													invalid	 
	 * - [any non-empty string]+[space(s)]+[commandType]										invalid	
	 * - [any non-empty string]+[commandType]													invalid			
	 * - [any non-empty string]																	invalid		
	 * - [empty string]																			invalid
	 * - [null]																					invalid
	 */

	// Test parameters
	@Parameterized.Parameters
	public static Collection<Object[]>  cases() {
		Global.CommandType validCommandType = Global.CommandType.ADD;
		String[] commandType = {"Add", "add", "ADD"};
		Global.CommandType invalidCommandType = Global.CommandType.INVALID;
		String anyNonEmptyString = "/\"meeting\" Nov 3rd 2pm";
		String emptyString = "";
		
		return Arrays.asList(new Object[][] {
				
				{ commandType[0]+" "+anyNonEmptyString, validCommandType },
				{ commandType[0], validCommandType },
				{ " "+commandType[0]+" "+anyNonEmptyString, validCommandType },
				{ " "+commandType[0], validCommandType },
				
				{ commandType[1]+" "+anyNonEmptyString, validCommandType },
				{ commandType[1], validCommandType },
				{ " "+commandType[1]+" "+anyNonEmptyString, validCommandType },
				{ " "+commandType[1], validCommandType },
				
				{ commandType[2]+" "+anyNonEmptyString, validCommandType },
				{ commandType[2], validCommandType },
				{ " "+commandType[2]+" "+anyNonEmptyString, validCommandType },
				{ " "+commandType[2], validCommandType },
				
				{ commandType[0]+anyNonEmptyString, invalidCommandType },		
				{ anyNonEmptyString+" "+commandType[0], invalidCommandType },
				{ anyNonEmptyString+commandType[0], invalidCommandType },
				{ anyNonEmptyString, invalidCommandType },
				{ emptyString, invalidCommandType },
				{ null, invalidCommandType },
				});
	}

	// Test run
   	@Test
	public void testDetermineCommandType() {
		assertEquals(expectedResult, TaskCommander.parser.determineCommandType(userCommand)); 
	}
}
