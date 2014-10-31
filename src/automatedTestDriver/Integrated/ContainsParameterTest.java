package automatedTestDriver.Integrated;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import com.taskcommander.TaskCommander;

/**
 * This class contains all test cases for the method containsParameter(userCommand:String) of the component Integrated Testing.
 * 
 * 
 */
//@author A0105753J
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
	
	/* 
	 * Test cases:
	 * 
	 * Partition of parameter: 				[any searched string], [empty string], [null]
	 * 
	 * Initial partition of userCommand: 	[string with at least one word], [empty string], [null]
	 * 	For strings with at least one word:
	 * 	- One word:							[exact searched string], [String containing the searched string], [any other string],	
	 * 	- Two words: 						[exact searched string]+[any other string], [any other string]+[exact searched string]
	 *  - Three words:						[any other string]+[exact searched string]+[any other string]
	 */
	@Parameterized.Parameters
	public static Collection<Object[]>  cases() {
		String searchedString = "timed";
		String stringContainingSearchedString = "anytimed";
		String anyOtherString = "anyOtherString";
		
		return Arrays.asList(new Object[][] {
				{ anyOtherString, null, false},
				{ anyOtherString, "", false},
				
				{ null, searchedString, false },
				{ "", searchedString, false },
				
				{ searchedString, searchedString, true},
				{ stringContainingSearchedString, searchedString, false},
				{ anyOtherString, searchedString, false},
				
				{ searchedString + " " + anyOtherString, searchedString, true},
				{ anyOtherString + " " + searchedString, searchedString, true},
				
				{ anyOtherString + " " + searchedString + " " + anyOtherString, searchedString, true},

				
		});
	}

   	@Test
	public void testcontainsParameter() {
		assertEquals(expectedResult, TaskCommander.parser.containsParameter(userCommand, parameter)); 
	}
}
