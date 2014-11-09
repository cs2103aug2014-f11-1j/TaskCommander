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
 * This class is part of the unit test of the component Parser and contains all test cases 
 * for the method containsParameter(userCommand:String, parameter:String).
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
	 * 1. [commandType]+[space(s)]+[quoted taskName]+[space(s)]+[any string]					
	 * 2. [commandType]+[space(s)]+[any string]																				
	 * 3. [any string]																																								
	 * 4. [null]																					
	 * Further partition of [any string]:
	 * a. [string which exactly represents the searched string]							
	 * b. [string exactly containing the searched string (surrounded by spaces)]					
	 * c. [string not exactly containing the searched string (not surrounded by spaces)]		
	 * d. [string not containing the searched string at all]				
	 * e. [empty string]														
	 * 
	 * Initial partition of 2nd parameter "parameter":
	 * i.   [any string]																			
	 * ii.  [empty string]																		
	 * iii. [null]																				
	 */
	
	// Test parameters
	@Parameterized.Parameters
	public static Collection<Object[]>  cases() {
		String commandType = "update";
		String taskName = "\"meeting\"";
		String stringExactlyRepresentingSearchedString = "none";
		String stringExactlyContainingSearchedString = "none timed";
		String stringNotExactlyContainingSearchedString = "nonetimed";
		String stringNotContainingSearchedString = "other";
		String emptyString = "";
		
		return Arrays.asList(new Object[][] {
				
			// 1ai
			{ commandType+" "+taskName+" "+stringExactlyRepresentingSearchedString, stringExactlyRepresentingSearchedString, true },
			
			// 1bi
			{ commandType+" "+taskName+" "+stringExactlyContainingSearchedString, stringExactlyRepresentingSearchedString, true },
			
			// 1ci
			{ commandType+" "+taskName+" "+stringNotExactlyContainingSearchedString, stringExactlyRepresentingSearchedString, false },
			
			// 1di
			{ commandType+" "+taskName+" "+stringNotContainingSearchedString, stringExactlyRepresentingSearchedString, false },
			
			// 1ei
			{ commandType+" "+taskName+" "+emptyString, stringExactlyRepresentingSearchedString, false },
			
			// 2ai
			{ commandType+" "+stringExactlyRepresentingSearchedString, stringExactlyRepresentingSearchedString, true },
			
			// 2bi
			{ commandType+" "+stringExactlyContainingSearchedString, stringExactlyRepresentingSearchedString, true },
			
			// 2ci
			{ commandType+" "+stringNotExactlyContainingSearchedString, stringExactlyRepresentingSearchedString, false },
			
			// 2di
			{ commandType+" "+stringNotContainingSearchedString, stringExactlyRepresentingSearchedString, false },
			
			// 2ei
			{ commandType+" "+emptyString, stringExactlyRepresentingSearchedString, false },
			
			// 3ai
			{ stringExactlyRepresentingSearchedString, stringExactlyRepresentingSearchedString, true },
			
			// 3bi
			{ stringExactlyContainingSearchedString, stringExactlyRepresentingSearchedString, true },
			
			// 3ci
			{ stringNotExactlyContainingSearchedString, stringExactlyRepresentingSearchedString, false },
			
			// 3di
			{ stringNotContainingSearchedString, stringExactlyRepresentingSearchedString, false },
			
			// 3ei
			{ emptyString, stringExactlyRepresentingSearchedString, false },
			
			// 4i
			{ null, stringExactlyRepresentingSearchedString, false },
			
			// 1aii
			{ commandType+" "+taskName+" "+stringExactlyRepresentingSearchedString, emptyString, false },
			
			// 1aiii
			{ commandType+" "+taskName+" "+stringExactlyRepresentingSearchedString, null, false },
		});
	}

	// Test run
   	@Test
	public void testcontainsParameter() {
		assertEquals(expectedResult, TaskCommander.parser.containsParameter(userCommand, parameter)); 
	}
}