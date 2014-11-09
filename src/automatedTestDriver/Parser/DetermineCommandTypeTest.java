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
 * This class is part of the unit test of the component Parser and contains all
 * test cases for the method determineCommandType(userCommand:String).
 */

@RunWith(Parameterized.class)
public class DetermineCommandTypeTest {
	private String userCommand;
	private Global.CommandType expectedResult;

	public DetermineCommandTypeTest(String userCommand,
			Global.CommandType expectedResult) {
		this.userCommand = userCommand;
		this.expectedResult = expectedResult;
	}

	/* Test structure
	 * 
	 * Initial partition of parameter "userCommand":
	 * 1. [commandType]+[space(s)]+[any non-empty string]										
	 * 2. [commandType]																				 
	 * 3. [space(s)]+[commandType]+[space(s)]+[any non-empty string]								
	 * 4. [space(s)]+[commandType]																 
	 * 5. [commandType]+[any non-empty string]														 
	 * 6. [any non-empty string]+[space(s)]+[commandType]											
	 * 7. [any non-empty string]+[commandType]																
	 * 8. [any non-empty string]																			
	 * 9. [empty string]																			
	 * 10.[null]			
	 * 
	 * Further partition of [commandType]
	 * a. [commandType with initial capital letter]
	 * b. [commandType in small letters]	
	 * c. [commandType in capital letters]																	
	 */

	// Test parameters
	@Parameterized.Parameters
	public static Collection<Object[]> cases() {
		Global.CommandType validCommandType = Global.CommandType.ADD;
		String[] commandType = { "Add", "add", "ADD" };
		Global.CommandType invalidCommandType = Global.CommandType.INVALID;
		String anyNonEmptyString = "/\"meeting\" Nov 3rd 2pm";
		String emptyString = "";

		return Arrays.asList(new Object[][] {

				// 1a, 1b, 1c
				{ commandType[0] + " " + anyNonEmptyString, validCommandType },
				{ commandType[1] + " " + anyNonEmptyString, validCommandType },
				{ commandType[2] + " " + anyNonEmptyString, validCommandType },

				// 2a, 2b, 2c
				{ commandType[0], validCommandType },
				{ commandType[1], validCommandType },
				{ commandType[2], validCommandType },

				// 3a, 3b, 3c
				{ " " + commandType[0] + " " + anyNonEmptyString, validCommandType },
				{ " " + commandType[1] + " " + anyNonEmptyString, validCommandType },
				{ " " + commandType[2] + " " + anyNonEmptyString, validCommandType },

				// 4a, 4b, 4c
				{ " " + commandType[2], validCommandType },
				{ " " + commandType[0], validCommandType },
				{ " " + commandType[1], validCommandType },

				// 5
				{ commandType[0] + anyNonEmptyString, invalidCommandType },

				// 6
				{ anyNonEmptyString + " " + commandType[0], invalidCommandType },

				// 7
				{ anyNonEmptyString + commandType[0], invalidCommandType },

				// 8
				{ anyNonEmptyString, invalidCommandType },

				// 9
				{ emptyString, invalidCommandType },

				// 10
				{ null, invalidCommandType }, });
	}

	// Test run
	@Test
	public void testDetermineCommandType() {
		assertEquals(expectedResult,
				TaskCommander.parser.determineCommandType(userCommand));
	}
}