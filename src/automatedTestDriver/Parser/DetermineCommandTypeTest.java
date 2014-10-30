package automatedTestDriver.Parser;
import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import com.taskcommander.Global;
import com.taskcommander.TaskCommander;

/**
 * This class contains all test cases for the method determineCommandType(userCommand:String) of the component Parser.
 * 
 * @author A0128620M
 */

@RunWith(Parameterized.class)
public class DetermineCommandTypeTest {
	private String userCommand;
	private Global.CommandType expectedResult;

	public DetermineCommandTypeTest(String userCommand, Global.CommandType expectedResult) {
		this.userCommand = userCommand;
		this.expectedResult = expectedResult;
	}
	
	/* 
	 * Test cases:
	 * 
	 * Initial partition of userCommand: 	[string with at least one word], [empty string], [null]
	 * 	For strings with at least one word:
	 * 	- One word:							[valid commandType], [valid commandType with leading/following/surrounding spaces], [any word]	
	 * 	- Two words: 						[valid commandType]+[any word], [valid commandType with leading/following/surrounding spaces]+[any word], 
	 * 										[any non-commandType word]+[valid commandType]
	 * 		For valid commandType: 			["Help", "help", "HELP", ..., "Synch", "synch", "SYNCH"]
	 */
	@Parameterized.Parameters
	public static Collection<Object[]>  cases() {
		Global.CommandType validCommandType = Global.CommandType.HELP;
		String validCommandTypeFirstLetterCapitalized = "Help";
		String validCommandTypeCompletelyUncapitalized = "help";
		String validCommandTypeCompletelyCapitalized = "HELP";
		String validCommandTypeCompletelyUncapitalizedWithLeadingSpace = " help ";
		String validCommandTypeCompletelyUncapitalizedWithFollowingSpace = "help ";
		String validCommandTypeCompletelyUncapitalizedWithSurrounding = " help ";
		String anyWord = "anyWord";
		
		return Arrays.asList(new Object[][] {
				{ null, Global.CommandType.INVALID },
				{ "", Global.CommandType.INVALID },
				
				{ validCommandTypeFirstLetterCapitalized, validCommandType },
				{ validCommandTypeCompletelyUncapitalized, validCommandType },
				{ validCommandTypeCompletelyCapitalized, validCommandType },
				{ validCommandTypeCompletelyUncapitalizedWithLeadingSpace, validCommandType },
				{ validCommandTypeCompletelyUncapitalizedWithFollowingSpace, validCommandType },
				{ validCommandTypeCompletelyUncapitalizedWithSurrounding, validCommandType },
				{ anyWord, Global.CommandType.INVALID },
				
				{ validCommandTypeFirstLetterCapitalized + " " + anyWord, validCommandType },
				{ validCommandTypeCompletelyUncapitalized + " " + anyWord, validCommandType },
				{ validCommandTypeCompletelyCapitalized + " " + anyWord, validCommandType },
				{ validCommandTypeCompletelyUncapitalizedWithLeadingSpace + " " + anyWord, validCommandType },
				{ validCommandTypeCompletelyUncapitalizedWithFollowingSpace + " " + anyWord, validCommandType },
				{ validCommandTypeCompletelyUncapitalizedWithSurrounding + " " + anyWord, validCommandType },
				{ anyWord + " " + validCommandTypeFirstLetterCapitalized, Global.CommandType.INVALID },
				{ anyWord + " " + validCommandTypeCompletelyUncapitalized, Global.CommandType.INVALID },
				{ anyWord + " " + validCommandTypeCompletelyCapitalized, Global.CommandType.INVALID },

				});
	}

   	@Test
	public void testDetermineCommandType() {
		assertEquals(expectedResult, TaskCommander.parser.determineCommandType(userCommand)); 
	}
}