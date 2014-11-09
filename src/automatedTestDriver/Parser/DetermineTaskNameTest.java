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
 * This class is part of the unit test of the component Parser and contains all
 * test cases for the method determineTaskName(userCommand:String).
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
     * Initial partition of parameter "userCommand":
     * 1. [commandType]+[space(s)]+[any string]+[space(s)]+[quoted taskName]+[space(s)]+[any string]			
     * 2. [commandType]+[space(s)]+[any string]+[space(s)]+[quoted taskName]									
     * 3. [commandType]+[space(s)]+[quoted taskName]															
     * 4. [commandType]+[any string]+[quoted taskName]+[any string]											
     * 5. [commandType]+[space(s)]+[any string]+[quoted taskName]											
     * 6. [commandType]+[quoted taskName]																	
     * 7. [any string]+[space(s)]+[quoted taskName]+[space(s)]+[any string]									
     * 8. [any string]+[space(s)]+[quoted taskName]															
     * 9. [space(s)]+[quoted taskName]																		
     * 10.[any string]+[quoted taskName]+[any string]															
     * 11.[space(s)]+[any string]+[quoted taskName]															
     * 12.[quoted taskName]																					
     * 13.[empty string]																																										
     * 14.[null]																								
     * Further partition of [quoted taskName]:
     * a. [quoted taskName without including quotes]															
     * b. [quoted taskName with including quotes]															
     */

    // Test parameters
    @Parameterized.Parameters
    public static Collection<Object[]> cases() {
        String commandType = "update";
        String anyNonEmptyString = "Nov 3rd 2pm";
        String quotedTaskNameWithoutIncludingQuotes = "\"Meeting with John.\"";
        String quotedTaskNameWithIncludingQuotes = "\"Meeting with John at the bar \"Relaxation\".\"";
        String expectedTaskNameWithoutIncludingQuotes = "Meeting with John.";
        String expectedTaskNameWithIncludingQuotes = "Meeting with John at the bar \"Relaxation\".";
        String emptyString = "";

        return Arrays
            .asList(new Object[][] {
                // 1a - 12a
                {
                    commandType + " " + anyNonEmptyString + " "
                        + quotedTaskNameWithoutIncludingQuotes + " "
                        + anyNonEmptyString,
                    expectedTaskNameWithoutIncludingQuotes },
                {
                    commandType + " " + anyNonEmptyString + " "
                        + quotedTaskNameWithoutIncludingQuotes,
                    expectedTaskNameWithoutIncludingQuotes },
                { commandType + " " + quotedTaskNameWithoutIncludingQuotes,
                    expectedTaskNameWithoutIncludingQuotes },
                {
                    commandType + " " + anyNonEmptyString
                        + quotedTaskNameWithoutIncludingQuotes
                        + anyNonEmptyString,
                    expectedTaskNameWithoutIncludingQuotes },
                {
                    commandType + " " + anyNonEmptyString
                        + quotedTaskNameWithoutIncludingQuotes,
                    expectedTaskNameWithoutIncludingQuotes },
                { commandType + quotedTaskNameWithoutIncludingQuotes,
                    expectedTaskNameWithoutIncludingQuotes },
                {
                    anyNonEmptyString + " "
                        + quotedTaskNameWithoutIncludingQuotes + " "
                        + anyNonEmptyString,
                    expectedTaskNameWithoutIncludingQuotes },
                {
                    anyNonEmptyString + " "
                        + quotedTaskNameWithoutIncludingQuotes,
                    expectedTaskNameWithoutIncludingQuotes },
                { " " + quotedTaskNameWithoutIncludingQuotes,
                    expectedTaskNameWithoutIncludingQuotes },
                {
                    anyNonEmptyString + quotedTaskNameWithoutIncludingQuotes
                        + anyNonEmptyString,
                    expectedTaskNameWithoutIncludingQuotes },
                { anyNonEmptyString + quotedTaskNameWithoutIncludingQuotes,
                    expectedTaskNameWithoutIncludingQuotes },
                { quotedTaskNameWithoutIncludingQuotes,
                    expectedTaskNameWithoutIncludingQuotes },

                // 1b - 12b
                {
                    commandType + " " + anyNonEmptyString + " "
                        + quotedTaskNameWithIncludingQuotes + " "
                        + anyNonEmptyString,
                    expectedTaskNameWithIncludingQuotes },
                {
                    commandType + " " + anyNonEmptyString + " "
                        + quotedTaskNameWithIncludingQuotes,
                    expectedTaskNameWithIncludingQuotes },
                { commandType + " " + quotedTaskNameWithIncludingQuotes,
                    expectedTaskNameWithIncludingQuotes },
                {
                    commandType + " " + anyNonEmptyString
                        + quotedTaskNameWithIncludingQuotes + anyNonEmptyString,
                    expectedTaskNameWithIncludingQuotes },
                {
                    commandType + " " + anyNonEmptyString
                        + quotedTaskNameWithIncludingQuotes,
                    expectedTaskNameWithIncludingQuotes },
                { commandType + quotedTaskNameWithIncludingQuotes,
                    expectedTaskNameWithIncludingQuotes },
                {
                    anyNonEmptyString + " " + quotedTaskNameWithIncludingQuotes
                        + " " + anyNonEmptyString,
                    expectedTaskNameWithIncludingQuotes },
                { anyNonEmptyString + " " + quotedTaskNameWithIncludingQuotes,
                    expectedTaskNameWithIncludingQuotes },
                { " " + quotedTaskNameWithIncludingQuotes,
                    expectedTaskNameWithIncludingQuotes },
                {
                    anyNonEmptyString + quotedTaskNameWithIncludingQuotes
                        + anyNonEmptyString,
                    expectedTaskNameWithIncludingQuotes },
                { anyNonEmptyString + quotedTaskNameWithIncludingQuotes,
                    expectedTaskNameWithIncludingQuotes },
                { quotedTaskNameWithIncludingQuotes,
                    expectedTaskNameWithIncludingQuotes },

                // 13, 14
                { emptyString, null }, { null, null }, });
    }

    // Test run
    @Test
    public void testcontainsParameter() {
        assertEquals(expectedResult,
            TaskCommander.parser.determineTaskName(userCommand));
    }
}