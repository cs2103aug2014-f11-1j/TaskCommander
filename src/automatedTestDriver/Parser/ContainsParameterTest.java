package automatedTestDriver.Parser;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.taskcommander.TaskCommander;

//@author A0128620M
/**
 * This class contains all test cases for the method containsParameter(userCommand:String) of the component Parser.
 */

@RunWith(Parameterized.class)
public class ContainsParameterTest {
	private String userCommand;
	private String parameter;
	private boolean expectedResult;

	public ContainsParameterTest(String userCommand, String parameter, boolean expectedResult) {
		this.userCommand = userCommand;
		this.parameter = parameter;
		this.expectedResult = expectedResult;
	}
	
	/* Test structure
	 * 
	 * Initial partition of 1st parameter "userCommand":
	 * - [commandType]+[space(s)]+[quoted taskName]+[space(s)]+[any string]						depends	on [any string]
	 * - [commandType]+[space(s)]+[any string]													depends	on [any string]								
	 * - [any string]																			depends	on [any string]																					
	 * - [null]																					invalid
	 * Further partition of [any string]:
	 * - [string which exactly represents the searched string]									valid
	 * - [string exactly containing the searched string (surrounded by spaces)]					valid
	 * - [string not exactly containing the searched string (not surrounded by spaces)]			invalid
	 * - [string not containing the searched string at all]										invalid
	 * - [empty string]																			invalid
	 * 
	 * Initial partition of 2nd parameter "parameter":
	 * - [any string]																			valid
	 * - [empty string]																			invalid
	 * - [null]																					invalid
	 */
	
	// Test parameters
	@Parameterized.Parameters
	public static Collection<Object[]>  cases() {
		String commandType = "update";
		String taskName = "\"meeting\"";
		String searchedString = "none";
		String stringExactlyContainingSearchedString = "none timed";
		String stringNotExactlyContainingSearchedString = "nonetimed";
		String stringNotContainingSearchedString = "other";
		String emptyString = "";
		
		return Arrays.asList(new Object[][] {
				{ commandType+" "+taskName+" "+searchedString, searchedString, true },
				{ commandType+" "+taskName+" "+stringExactlyContainingSearchedString, searchedString, true },
				{ commandType+" "+taskName+" "+stringNotExactlyContainingSearchedString, searchedString, false },
				{ commandType+" "+taskName+" "+stringNotContainingSearchedString, searchedString, false },
				{ commandType+" "+taskName+" "+emptyString, searchedString, false },
				
				{ commandType+" "+searchedString, searchedString, true },
				{ commandType+" "+stringExactlyContainingSearchedString, searchedString, true },
				{ commandType+" "+stringNotExactlyContainingSearchedString, searchedString, false },
				{ commandType+" "+stringNotContainingSearchedString, searchedString, false },
				{ commandType+" "+emptyString, searchedString, false },
				
				{ searchedString, searchedString, true },
				{ stringExactlyContainingSearchedString, searchedString, true },
				{ stringNotExactlyContainingSearchedString, searchedString, false },
				{ stringNotContainingSearchedString, searchedString, false },
				{ emptyString, searchedString, false },
				
				{ null, searchedString, false },
				
				{ commandType+" "+taskName+" "+searchedString, emptyString, false },
				{ commandType+" "+taskName+" "+searchedString, null, false },
		});
	}

	// Test run
   	@Test
	public void testcontainsParameter() {
		assertEquals(expectedResult, TaskCommander.parser.containsParameter(userCommand, parameter)); 
	}
}
