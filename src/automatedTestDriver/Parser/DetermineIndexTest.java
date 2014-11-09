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
 * This class is part of the unit test of the component Parser and contains all
 * test cases for the method determineIndex(userCommand:String).
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
     * 1. [any word]+[space(s)]+[indexString]+[space(s)]+[any non-empty string]						
     * 2. [any word]+[space(s)]+[indexString]															
     * 3. [any word]+[indexString]																	
     * 4. [indexString]+[any non-empty string]														
     * 5. [indexString]	
     * 6. [space(s)]+[any word]+[indexString]																			
     * 7. [space(s)]+[index]+[any non-empty string]													
     * 8. [space(s)]+[indexString]																	
     * 9. [empty string]																				
     * 10. [null]					
     * 																	 
     * Further partition of [indexString]:
     * a. [1 .. MAX_INT]																				
     * b. [MIN_INT .. 0]																				 
     * c. [any non Integer]																				 
     */

    // Test parameters
    @Parameterized.Parameters
    public static Collection<Object[]> cases() {
        String anyWord = "update";
        String anyNonEmptyString = "/\"meeting\" Nov 3rd 2pm";
        String anyNonInteger = "O"; // letter O instead of number 0
        int[] index = { Integer.MIN_VALUE - 1, Integer.MIN_VALUE,
            Integer.MIN_VALUE + 1, -1, 0, 1, 2, -1, Integer.MAX_VALUE,
            Integer.MAX_VALUE + 1 };
        int invalidIndex = -1;
        String emptyString = "";

        return Arrays
            .asList(new Object[][] {

                // 1a, 1b, 1c
                {
                    anyWord + " " + Integer.toString(index[0]) + " "
                        + anyNonEmptyString, Integer.toString(index[0]) },
                {
                    anyWord + " " + Integer.toString(index[1]) + " "
                        + anyNonEmptyString, Integer.toString(index[1]) },
                {
                    anyWord + " " + Integer.toString(index[2]) + " "
                        + anyNonEmptyString, Integer.toString(index[2]) },
                {
                    anyWord + " " + Integer.toString(index[3]) + " "
                        + anyNonEmptyString, Integer.toString(index[3]) },
                {
                    anyWord + " " + Integer.toString(index[4]) + " "
                        + anyNonEmptyString, Integer.toString(index[4]) },
                {
                    anyWord + " " + Integer.toString(index[5]) + " "
                        + anyNonEmptyString, Integer.toString(index[5]) },
                {
                    anyWord + " " + Integer.toString(index[6]) + " "
                        + anyNonEmptyString, Integer.toString(index[6]) },
                {
                    anyWord + " " + Integer.toString(index[7]) + " "
                        + anyNonEmptyString, Integer.toString(index[7]) },
                {
                    anyWord + " " + Integer.toString(index[8]) + " "
                        + anyNonEmptyString, Integer.toString(index[8]) },
                {
                    anyWord + " " + Integer.toString(index[9]) + " "
                        + anyNonEmptyString, Integer.toString(index[9]) },
                { anyWord + " " + anyNonInteger + " " + anyNonEmptyString,
                    Integer.toString(invalidIndex) },

                // 2a, 2b, 2c
                { anyWord + " " + Integer.toString(index[0]),
                    Integer.toString(index[0]) },
                { anyWord + " " + Integer.toString(index[1]),
                    Integer.toString(index[1]) },
                { anyWord + " " + Integer.toString(index[2]),
                    Integer.toString(index[2]) },
                { anyWord + " " + Integer.toString(index[3]),
                    Integer.toString(index[3]) },
                { anyWord + " " + Integer.toString(index[4]),
                    Integer.toString(index[4]) },
                { anyWord + " " + Integer.toString(index[5]),
                    Integer.toString(index[5]) },
                { anyWord + " " + Integer.toString(index[6]),
                    Integer.toString(index[6]) },
                { anyWord + " " + Integer.toString(index[7]),
                    Integer.toString(index[7]) },
                { anyWord + " " + Integer.toString(index[8]),
                    Integer.toString(index[8]) },
                { anyWord + " " + Integer.toString(index[9]),
                    Integer.toString(index[9]) },
                { anyWord + " " + anyNonInteger, Integer.toString(invalidIndex) },

                // 3b
                { anyWord + Integer.toString(index[6]),
                    Integer.toString(invalidIndex) },

                // 4b
                { Integer.toString(index[6]) + anyNonEmptyString,
                    Integer.toString(invalidIndex) },

                // 5b
                { Integer.toString(index[6]), Integer.toString(invalidIndex) },

                // 6b
                { " " + anyWord + Integer.toString(index[6]),
                    Integer.toString(invalidIndex) },

                // 7b
                { " " + Integer.toString(index[6]) + anyNonEmptyString,
                    Integer.toString(invalidIndex) },

                // 8b
                { " " + Integer.toString(index[6]),
                    Integer.toString(invalidIndex) },

                // 9
                { emptyString, Integer.toString(invalidIndex) },

                // 10
                { null, Integer.toString(invalidIndex) }, });
    }

    // Test run
    @Test
    public void testDetermineIndex() {
        assertEquals(expectedResult,
            Integer.toString(TaskCommander.parser.determineIndex(userCommand)));
    }
}