package automatedTestDriver.Parser;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.taskcommander.TaskCommander;

//@author A0128620M
/**
 * This class contains all test cases for the method determineIndex(userCommand:String) of the component Parser.
 */

@RunWith(Parameterized.class)
public class DetermineIndexTest {
	private String userCommand;
	private String expectedResult;

	public DetermineIndexTest(String userCommand, String expectedResult) {
		this.userCommand = userCommand;
		this.expectedResult = expectedResult;
	}
	
	/* Test structure
	 * 
	 * Initial partition of parameter "userCommand":
	 * - [any word]+[space(s)]+[indexString]+[space(s)]+[any non-empty string]						depends on [indexString]
	 * - [any word]+[space(s)]+[indexString]														depends on [indexString]	
	 * - [any word]+[indexString]																	invalid
	 * - [indexString]+[any non-empty string]														invalid
	 * - [indexString]																				invalid
	 * - [space(s)]+[index]+[any non-empty string]													invalid
	 * - [space(s)]+[indexString]																	invalid
	 * - [empty string]																				invalid
	 * - [null]																						invalid 
	 * Further partition of [indexString]:
	 * - [1 .. MAX_INT]																				valid
	 * - [MIN_INT .. 0]																				valid 
	 * - [any non Integer]																			invalid	 
	 */

	// Test parameters
	@Parameterized.Parameters
	public static Collection<Object[]>  cases() {
		String anyWord = "update";
		String anyNonEmptyString = "/\"meeting\" Nov 3rd 2pm";
		String anyNonInteger = "O";	// letter O instead of number 0
		int[] index = { Integer.MIN_VALUE-1, Integer.MIN_VALUE, Integer.MIN_VALUE+1, -1, 0, 1, 2, -1, Integer.MAX_VALUE, Integer.MAX_VALUE+1 };
		int invalidIndex = -1;
		String emptyString = "";
		
		return Arrays.asList(new Object[][] {
				
				{ anyWord+" "+Integer.toString(index[0])+" "+anyNonEmptyString, Integer.toString(index[0])},
		
				{ anyWord+" "+Integer.toString(index[1])+" "+anyNonEmptyString, Integer.toString(index[1]) },
				{ anyWord+" "+Integer.toString(index[2])+" "+anyNonEmptyString, Integer.toString(index[2]) },
				{ anyWord+" "+Integer.toString(index[3])+" "+anyNonEmptyString, Integer.toString(index[3]) },
				{ anyWord+" "+Integer.toString(index[4])+" "+anyNonEmptyString, Integer.toString(index[4]) },
				{ anyWord+" "+Integer.toString(index[5])+" "+anyNonEmptyString, Integer.toString(index[5]) },
				{ anyWord+" "+Integer.toString(index[6])+" "+anyNonEmptyString, Integer.toString(index[6]) },
				{ anyWord+" "+Integer.toString(index[7])+" "+anyNonEmptyString, Integer.toString(index[7]) },
				{ anyWord+" "+Integer.toString(index[8])+" "+anyNonEmptyString, Integer.toString(index[8]) },
				{ anyWord+" "+Integer.toString(index[9])+" "+anyNonEmptyString, Integer.toString(index[9]) },
				{ anyWord+" "+anyNonInteger+" "+anyNonEmptyString, Integer.toString(invalidIndex) },
				
				{ anyWord+" "+Integer.toString(index[0]), Integer.toString(index[0]) },
				{ anyWord+" "+Integer.toString(index[1]), Integer.toString(index[1]) },
				{ anyWord+" "+Integer.toString(index[2]), Integer.toString(index[2]) },
				{ anyWord+" "+Integer.toString(index[3]), Integer.toString(index[3]) },
				{ anyWord+" "+Integer.toString(index[4]), Integer.toString(index[4]) },
				{ anyWord+" "+Integer.toString(index[5]), Integer.toString(index[5]) },
				{ anyWord+" "+Integer.toString(index[6]), Integer.toString(index[6]) },
				{ anyWord+" "+Integer.toString(index[7]), Integer.toString(index[7]) },
				{ anyWord+" "+Integer.toString(index[8]), Integer.toString(index[8]) },
				{ anyWord+" "+Integer.toString(index[9]), Integer.toString(index[9]) },
				{ anyWord+" "+anyNonInteger, Integer.toString(invalidIndex) },
				
				{ anyWord+Integer.toString(index[6]), Integer.toString(invalidIndex) },
				{ Integer.toString(index[6])+anyNonEmptyString, Integer.toString(invalidIndex) },
				{ " "+anyWord+Integer.toString(index[6]), Integer.toString(invalidIndex) },
				{ " "+Integer.toString(index[6])+anyNonEmptyString, Integer.toString(invalidIndex) },
				{ emptyString, Integer.toString(invalidIndex) },
				{ null, Integer.toString(invalidIndex) },
				});
	}

	// Test run
   	@Test
	public void testDetermineIndex() {
   		assertEquals(expectedResult, Integer.toString(TaskCommander.parser.determineIndex(userCommand))); 
	}
}