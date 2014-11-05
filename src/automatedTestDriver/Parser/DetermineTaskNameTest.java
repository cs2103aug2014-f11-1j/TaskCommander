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
* This class contains all test cases for the method determineTaskName(userCommand:String) of the component Parser.
*/

@RunWith(Parameterized.class)
public class DetermineTaskNameTest {
	private String userCommand;
	private String expectedResult;

	public DetermineTaskNameTest(String userCommand, String expectedResult) {
		this.userCommand = userCommand;
		this.expectedResult = expectedResult;
	}
	
	/* Test structure
	 * 
	 * Initial partition of 1st parameter "userCommand":
	 * - [commandType]+[space(s)]+[any string]+[space(s)]+[quoted taskName]+[space(s)]+[any string]			valid
	 * - [commandType]+[space(s)]+[any string]+[space(s)]+[quoted taskName]									valid
	 * - [commandType]+[space(s)]+[quoted taskName]															valid
	 * - [commandType]+[any string]+[quoted taskName]+[any string]											valid
	 * - [commandType]+[space(s)]+[any string]+[quoted taskName]											valid
	 * - [commandType]+[quoted taskName]																	valid
	 * - [any string]+[space(s)]+[quoted taskName]+[space(s)]+[any string]									valid
	 * - [any string]+[space(s)]+[quoted taskName]															valid
	 * - [space(s)]+[quoted taskName]																		valid
	 * - [any string]+[quoted taskName]+[any string]														valid	
	 * - [space(s)]+[any string]+[quoted taskName]															valid
	 * - [quoted taskName]																					valid
	 * - [empty string]																						invalid																				
	 * - [null]																								invalid
	 * Further partition of [quoted taskName]:
	 * - [quoted taskName without including quotes]															valid
	 * - [quoted taskName with including quotes]															valid
	 */
	
	// Test parameters
	@Parameterized.Parameters
	public static Collection<Object[]>  cases() {
		String commandType = "update";
		String anyNonEmptyString = "Nov 3rd 2pm";
		String quotedTaskNameWithoutIncludingQuotes = "\"Meeting with John.\"";
		String quotedTaskNameWithIncludingQuotes = "\"Meeting with John at the bar \"Relaxation\".\"";
		String expectedTaskNameWithoutIncludingQuotes = "Meeting with John.";
		String expectedTaskNameWithIncludingQuotes = "Meeting with John at the bar \"Relaxation\".";
		String emptyString = "";
		
		return Arrays.asList(new Object[][] {
				{ commandType+" "+anyNonEmptyString+" "+quotedTaskNameWithoutIncludingQuotes+" "+anyNonEmptyString, expectedTaskNameWithoutIncludingQuotes },
				{ commandType+" "+anyNonEmptyString+" "+quotedTaskNameWithoutIncludingQuotes, expectedTaskNameWithoutIncludingQuotes },
				{ commandType+" "+quotedTaskNameWithoutIncludingQuotes, expectedTaskNameWithoutIncludingQuotes },
				{ commandType+" "+anyNonEmptyString+quotedTaskNameWithoutIncludingQuotes+anyNonEmptyString, expectedTaskNameWithoutIncludingQuotes },
				{ commandType+" "+anyNonEmptyString+quotedTaskNameWithoutIncludingQuotes, expectedTaskNameWithoutIncludingQuotes },
				{ commandType+quotedTaskNameWithoutIncludingQuotes, expectedTaskNameWithoutIncludingQuotes },
				
				{ anyNonEmptyString+" "+quotedTaskNameWithoutIncludingQuotes+" "+anyNonEmptyString, expectedTaskNameWithoutIncludingQuotes },
				{ anyNonEmptyString+" "+quotedTaskNameWithoutIncludingQuotes, expectedTaskNameWithoutIncludingQuotes },
				{ " "+quotedTaskNameWithoutIncludingQuotes, expectedTaskNameWithoutIncludingQuotes },
				{ anyNonEmptyString+quotedTaskNameWithoutIncludingQuotes+anyNonEmptyString, expectedTaskNameWithoutIncludingQuotes },
				{ anyNonEmptyString+quotedTaskNameWithoutIncludingQuotes, expectedTaskNameWithoutIncludingQuotes },
				{ quotedTaskNameWithoutIncludingQuotes, expectedTaskNameWithoutIncludingQuotes },
				
				{ commandType+" "+anyNonEmptyString+" "+quotedTaskNameWithIncludingQuotes+" "+anyNonEmptyString, expectedTaskNameWithIncludingQuotes },
				{ commandType+" "+anyNonEmptyString+" "+quotedTaskNameWithIncludingQuotes, expectedTaskNameWithIncludingQuotes },
				{ commandType+" "+quotedTaskNameWithIncludingQuotes, expectedTaskNameWithIncludingQuotes },
				{ commandType+" "+anyNonEmptyString+quotedTaskNameWithIncludingQuotes+anyNonEmptyString, expectedTaskNameWithIncludingQuotes },
				{ commandType+" "+anyNonEmptyString+quotedTaskNameWithIncludingQuotes, expectedTaskNameWithIncludingQuotes },
				{ commandType+quotedTaskNameWithIncludingQuotes, expectedTaskNameWithIncludingQuotes },
				
				{ anyNonEmptyString+" "+quotedTaskNameWithIncludingQuotes+" "+anyNonEmptyString, expectedTaskNameWithIncludingQuotes },
				{ anyNonEmptyString+" "+quotedTaskNameWithIncludingQuotes, expectedTaskNameWithIncludingQuotes },
				{ " "+quotedTaskNameWithIncludingQuotes, expectedTaskNameWithIncludingQuotes },
				{ anyNonEmptyString+quotedTaskNameWithIncludingQuotes+anyNonEmptyString, expectedTaskNameWithIncludingQuotes },
				{ anyNonEmptyString+quotedTaskNameWithIncludingQuotes, expectedTaskNameWithIncludingQuotes },
				{ quotedTaskNameWithIncludingQuotes, expectedTaskNameWithIncludingQuotes },
				
				{ emptyString, null },
				{ null, null },
		});
	}

	// Test run
 	@Test
	public void testcontainsParameter() {
		assertEquals(expectedResult, TaskCommander.parser.determineTaskName(userCommand)); 
	}
}