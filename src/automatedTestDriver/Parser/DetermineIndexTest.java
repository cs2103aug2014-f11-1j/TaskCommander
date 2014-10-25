package automatedTestDriver.Parser;
import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import com.taskcommander.TaskCommander;

/**
 * This class contains all test cases for the method determineIndex(userCommand:String) of the component Parser.
 * 
 * @author A0128620M
 */

@RunWith(Parameterized.class)
public class DetermineIndexTest {
	private String userCommand;
	private int expectedResult;

	public DetermineIndexTest(String userCommand, int expectedResult) {
		this.userCommand = userCommand;
		this.expectedResult = expectedResult;
	}
	
	/* 
	 * Test cases:
	 * 
	 * Initial partition of userCommand: 	[string with at least two words], [string with one word], [empty string], [null]
	 * 	For string with one word:			[any non Integer], [any Integer]
	 * 	For strings with at least two words:[any String]+[1 .. MAX_INT], [any String]+[MIN_INT .. 0], [any String]+[any non Integer], 
	 */
	@Parameterized.Parameters
	public static Collection<Object[]>  cases() {
		String anyString = "anyString";
		String anyNonInteger = "anyNonInteger";
		String anyInteger ="3";
		
		return Arrays.asList(new Object[][] {
				{ null, -1 },
				{ "", -1 },
				
				{ anyNonInteger, -1 },
				{ anyInteger, -1 },
				
				{ anyString+" "+ Integer.toString(Integer.MIN_VALUE-1), Integer.MIN_VALUE-1},
				{ anyString+" "+ Integer.toString(Integer.MIN_VALUE), -1},
				{ anyString+" "+ Integer.toString(Integer.MIN_VALUE+1), -1},
				{ anyString+" "+ Integer.toString(-1), -1},
				{ anyString+" "+ Integer.toString(0), -1},
				
				{ anyString+" "+ Integer.toString(1), 1},
				{ anyString+" "+ Integer.toString(2), 2},
				{ anyString+" "+ Integer.toString(Integer.MAX_VALUE-1), Integer.MAX_VALUE-1},
				{ anyString+" "+ Integer.toString(Integer.MAX_VALUE), Integer.MAX_VALUE},
				{ anyString+" "+ Integer.toString(Integer.MAX_VALUE+1), -1},
				
				{ anyString+" "+ anyNonInteger, -1},

				});
	}

   	@Test
	public void testDetermineIndex() {
		assertEquals(expectedResult, TaskCommander.parser.determineIndex(userCommand)); 
	}
}