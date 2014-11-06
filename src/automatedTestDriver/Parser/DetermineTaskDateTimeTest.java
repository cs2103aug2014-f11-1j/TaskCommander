package automatedTestDriver.Parser;

import static org.junit.Assert.*;

import java.util.ArrayList;

import com.taskcommander.TaskCommander;

//@author A0128620M
/**
* This class contains all test cases for the method determineTakDateTime(userCommand:String) of the component Parser.
*/

public class DetermineTaskDateTimeTest {
	
	/* Test structure
	 * 
	 * Initial partition of parameter "userCommand":
	 * - [commandType]+[space(s)]+[any string]+[space(s)]+[DateTime(s)]+[space(s)]+[any string]								
	 * - [commandType]+[space(s)]+[any string]+[space(s)]+[DateTime(s)]														
	 * - [commandType]+[space(s)]+[DateTime(s)]+[space(s)]+[any string]														
	 * - [commandType]+[space(s)]+[DateTime(s)]																				
	 * - [DateTime(s)]																									
	 * - [DateTime(s)]																										
	 * - [commandType]+[space(s)]+[any string]+[space(s)]+[DateTime(s)]+[space(s)]+[any string]+[space(s)]+[DateTime(s)]	
	 * - [commandType]+[space(s)]+[any string]+[space(s)]+[DateTime(s)]+[space(s)]+[any string]+[space(s)]+[DateTime(s)]	
	 * - [commandType]+[space(s)]+[any string]+[space(s)]+[DateTime(s)]+[space(s)]+[DateTime(s)]							
	 * - [commandType]+[space(s)]+[any string]+[space(s)]+[DateTime(s)]+[space(s)]+[DateTime(s)]	
	 * - [empty string]
	 * - [null]						
	 * Further partition of [DateTime(s)]:
	 * - [one date]																											
	 * - [two dates]																										
	 * - [more dates]
	 * - [invalid]																										
	 * Further partition of [one date]:
	 * - [formal dates]: yyyy-mm-dd, yyyy/mm/dd, dd/mm/yyyy, mm/dd/yy ..
	 * - [relaxed dates]: Nov 21, Nov 21th, Nov 21 '14, Nov 21 2014 ..
	 * - [relative dates]: next thursday, last wednesday, today, tomorrow, next week ..
	 * - [formal time]: hh:mm, hham, hhpm, hh.mmam, hh.mmpm,  hh am, hh pm, hh.mm am, hh.mm pm
	 * - [relaxed time]: noon, afternoon, midnight
	 * - [relative times]: in 30 minutes, 6 hours ago
	 * Further partition of [two dates]:	
	 * - [both dates separated by a "to"]																					
	 * - [both dates separated by a "-" which is surrounded by spaces] 														
	 * - [both dates not separated as described above]																		
	 * - [only one date given and time of second date is after the first date]
	 * - [only one date given and time of second date is before the first date]
	 */
	
	/*
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
 	*/
	@SuppressWarnings("unused")
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

