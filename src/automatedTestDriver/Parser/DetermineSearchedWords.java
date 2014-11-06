package automatedTestDriver.Parser;
import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Test;
import com.taskcommander.TaskCommander;

//@author A0128620M
/**
* This class contains all test cases for the method determineSearchedWords(userCommand:String) of the component Parser.
*/

public class DetermineSearchedWords {
	
	/* Test structure
	 * 
	 * Initial partition of parameter "userCommand":
	 * 1. [commandType]+[space(s)]+[string containing the searched words and/or phrases]			depends on [string containing the searched words and/or phrases]
	 * 2. [string containing the searched words and/or phrases]										depends	on [string containing the searched words and/or phrases]
	 * 3. [space(s)]+[string containing the searched words and/or phrases]							depends	on [string containing the searched words and/or phrases]
	 * 4. [empty String]																			invalid																								
	 * 5. [null]																					invalid
	 * Further partition of [string containing the searched words and/or phrases]:
	 * a. [string with n searched words separated by spaces]										valid
	 * b. [string with m searched phrases]															valid
	 * c. [string with n searched words separated by spaces and m searched phrases ]				valid
	 * d. [string with n searched words not separated by spaces]									invalid
	 * e. [string with n searched words not separated by spaces and m searched phrases]				invalid
	 */
	
	// Test parameters
		String commandType = "search";
		String[] searchedWords = { "meeting", "date", "John"};
		String[] searchedPhrases = { "\"Computer Club\"", "\"Meeting with John.\"", "\"Applying for a summer internship, maybe at Google or another big company.\""};
		String[] expectedSearchedPhrases = { "Computer Club", "Meeting with John.", "Applying for a summer internship, maybe at Google or another big company."};
		String emptyString = "";
		String userCommand;
		ArrayList<String> resultingSearchedWordsAndPhrases;
		ArrayList<String> expectedSearchedWordsAndPhrases = new ArrayList<String>();

	// Test run
   	@Test
	public void testdetermineSearchedWords() {
   		
   		//1a
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = commandType+" "+searchedWords[0];
   		expectedSearchedWordsAndPhrases.add(searchedWords[0]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = commandType+" "+searchedWords[0]+" "+searchedWords[1];
   		expectedSearchedWordsAndPhrases.add(searchedWords[0]);
   		expectedSearchedWordsAndPhrases.add(searchedWords[1]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = commandType+" "+searchedWords[0]+" "+searchedWords[1]+" "+searchedWords[2];
   		expectedSearchedWordsAndPhrases.add(searchedWords[0]);
   		expectedSearchedWordsAndPhrases.add(searchedWords[1]);
   		expectedSearchedWordsAndPhrases.add(searchedWords[2]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
   		//1b
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = commandType+" "+searchedPhrases[0];
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[0]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = commandType+" "+searchedPhrases[0]+" "+searchedPhrases[1];
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[0]);
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[1]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = commandType+" "+searchedPhrases[0]+" "+searchedPhrases[1]+" "+searchedPhrases[2];
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[0]);
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[1]);
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[2]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = commandType+" "+searchedPhrases[0]+searchedPhrases[1];
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[0]);
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[1]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = commandType+" "+searchedPhrases[0]+searchedPhrases[1]+searchedPhrases[2];
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[0]);
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[1]);
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[2]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
   		//1c
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = commandType+" "+searchedWords[0]+" "+searchedPhrases[0];
   		expectedSearchedWordsAndPhrases.add(searchedWords[0]);
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[0]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = commandType+" "+searchedPhrases[0]+" "+searchedWords[0];
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[0]);
   		expectedSearchedWordsAndPhrases.add(searchedWords[0]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = commandType+" "+searchedWords[0]+" "+searchedPhrases[1]+" "+searchedWords[1];
   		expectedSearchedWordsAndPhrases.add(searchedWords[0]);
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[1]);
   		expectedSearchedWordsAndPhrases.add(searchedWords[1]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = commandType+" "+searchedWords[0]+" "+searchedWords[1]+" "+searchedPhrases[0];
   		expectedSearchedWordsAndPhrases.add(searchedWords[0]);
   		expectedSearchedWordsAndPhrases.add(searchedWords[1]);
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[0]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = commandType+" "+searchedPhrases[0]+" "+searchedWords[0]+" "+searchedWords[1];
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[0]);
   		expectedSearchedWordsAndPhrases.add(searchedWords[0]);
   		expectedSearchedWordsAndPhrases.add(searchedWords[1]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = commandType+" "+searchedPhrases[0]+" "+searchedWords[1]+" "+searchedPhrases[1];
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[0]);
   		expectedSearchedWordsAndPhrases.add(searchedWords[1]);
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[1]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = commandType+" "+searchedPhrases[0]+" "+searchedPhrases[1]+" "+searchedWords[0];
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[0]);
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[1]);
   		expectedSearchedWordsAndPhrases.add(searchedWords[0]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = commandType+" "+searchedWords[0]+" "+searchedPhrases[0]+" "+searchedPhrases[1];
   		expectedSearchedWordsAndPhrases.add(searchedWords[0]);
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[0]);
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[1]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);

   		//1d
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = commandType+" "+searchedWords[0]+searchedWords[1];
   		expectedSearchedWordsAndPhrases.add(searchedWords[0]+searchedWords[1]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = commandType+" "+searchedWords[0]+searchedWords[1]+searchedWords[2];
   		expectedSearchedWordsAndPhrases.add(searchedWords[0]+searchedWords[1]+searchedWords[2]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
   		//1e
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = commandType+" "+searchedWords[0]+searchedWords[1]+" "+searchedPhrases[0];
   		expectedSearchedWordsAndPhrases.add(searchedWords[0]+searchedWords[1]);
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[0]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = commandType+" "+searchedPhrases[0]+" "+searchedWords[0]+searchedWords[1];
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[0]);
   		expectedSearchedWordsAndPhrases.add(searchedWords[0]+searchedWords[1]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
  		//2a
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = searchedWords[0];
   		expectedSearchedWordsAndPhrases.add(searchedWords[0]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
   		//2b
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = searchedPhrases[0];
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[0]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
   		//2c
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = searchedWords[0]+" "+searchedPhrases[0];
   		expectedSearchedWordsAndPhrases.add(searchedWords[0]);
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[0]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);

   		//2d
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = searchedWords[0]+searchedWords[1];
   		expectedSearchedWordsAndPhrases.add(searchedWords[0]+searchedWords[1]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);

   		//2e
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = searchedWords[0]+searchedWords[1]+" "+searchedPhrases[0];
   		expectedSearchedWordsAndPhrases.add(searchedWords[0]+searchedWords[1]);
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[0]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
  		//3a
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = " "+searchedWords[0];
   		expectedSearchedWordsAndPhrases.add(searchedWords[0]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
   		//3b
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = searchedPhrases[0];
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[0]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
   		
   		//3c
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = " "+searchedWords[0]+" "+searchedPhrases[0];
   		expectedSearchedWordsAndPhrases.add(searchedWords[0]);
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[0]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);

   		//3d
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = " "+searchedWords[0]+searchedWords[1];
   		expectedSearchedWordsAndPhrases.add(searchedWords[0]+searchedWords[1]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);

   		//3e
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = " "+searchedWords[0]+searchedWords[1]+" "+searchedPhrases[0];
   		expectedSearchedWordsAndPhrases.add(searchedWords[0]+searchedWords[1]);
   		expectedSearchedWordsAndPhrases.add(expectedSearchedPhrases[0]);
   		testCase(expectedSearchedWordsAndPhrases, userCommand);
	
   		//4
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = emptyString;
   		testCase(null, userCommand);
   		
  		//5
   		expectedSearchedWordsAndPhrases.clear();
   		userCommand = null;
   		testCase(null, userCommand);
	}
   	
	private void testCase(ArrayList<String> expectedSearchedWordsAndPhrases,
			String userCommand) {
		ArrayList<String> resultingSearchedWordsAndPhrases = TaskCommander.parser.determineSearchedWords(userCommand);
		if (expectedSearchedWordsAndPhrases != null) {
	   		for(int i = 0; i < expectedSearchedWordsAndPhrases.size(); i++) {
	   			assertEquals(expectedSearchedWordsAndPhrases.get(i),resultingSearchedWordsAndPhrases.get(i)); 
	   		}
		} else {
			assertEquals(expectedSearchedWordsAndPhrases, resultingSearchedWordsAndPhrases);
		}
	}
}

