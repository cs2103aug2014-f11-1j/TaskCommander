package automatedTestDriver.Parser;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import com.taskcommander.TaskCommander;

//@author A0128620M
/**
 * This class is part of the unit test of the component Parser and contains all
 * test cases for the method determineSearchedWords(userCommand:String).
 */

@RunWith(Parameterized.class)
public class DetermineSearchedWords {
	private String userCommand;
	private List<String> expectedSearchedWordsAndPhrases;

	public DetermineSearchedWords(String userCommand,
			List<String> expectedSearchedWordsAndPhrases) {
		this.userCommand = userCommand;
		this.expectedSearchedWordsAndPhrases = expectedSearchedWordsAndPhrases;
	}
	
	/* Test structure
	 * 
	 * Initial partition of parameter "userCommand":
	 * 1. [commandType]+[space(s)]+[string containing the searched word(s) and/or phrase(s)]			
	 * 2. [space(s)]+[string containing the searched word(s) and/or phrase(s)]
	 * 3. [string containing the searched word(s) and/or phrase(s)]																
	 * 4. [empty String]																																											
	 * 5. [null]	
	 * 																				
	 * Further partition of [string containing the searched word(s) and/or phrase(s)]:
	 * a. [string with searched word(s) separated by spaces]	
	 * b. [string with searched phrase(s)]									
	 * c. [string with searched word(s) separated by spaces and m searched phrase(s)]				
	 * d. [string with searched word(s) not separated by spaces]									
	 * e. [string with searched word(s) not separated by spaces and m searched phrase(s)]		
	 */
	
	// Test parameters
	@Parameterized.Parameters
	public static Collection<Object[]> cases() {
		ArrayList<Object[]> cases = new ArrayList<Object[]>();
		String commandType = "search";
		String[] searchedWords = { "meeting", "date", "John" };
		String[] searchedPhrases = { "\"Computer Club\"", "\"Meeting with John.\"",
				"\"Applying for a summer internship, maybe at Google or another big company.\"" };
		String[] expectedSearchedPhrases = { "Computer Club", "Meeting with John.",
				"Applying for a summer internship, maybe at Google or another big company." };
		String emptyString = "";

		// 1a
		cases.add(new Object[] { commandType + " " + searchedWords[0],
				Arrays.asList(new String[] { searchedWords[0] }) });
		cases.add(new Object[] {
				commandType + " " + searchedWords[0] + " " + searchedWords[1],
				Arrays.asList(new String[] { searchedWords[0], searchedWords[1] }) });
		cases.add(new Object[] {
				commandType + " " + searchedWords[0] + " " + searchedWords[1] + " "
						+ searchedWords[2],
				Arrays.asList(new String[] { searchedWords[0], searchedWords[1],
						searchedWords[2] }) });

		// 1b
		cases.add(new Object[] { commandType + " " + searchedPhrases[0],
				Arrays.asList(new String[] { expectedSearchedPhrases[0] }) });
		cases.add(new Object[] {
				commandType + " " + searchedPhrases[0] + " " + searchedPhrases[1],
				Arrays.asList(new String[] { expectedSearchedPhrases[0],
						expectedSearchedPhrases[1] }) });
		cases.add(new Object[] {
				commandType + " " + searchedPhrases[0] + " " + searchedPhrases[1] + " "
						+ searchedPhrases[2],
				Arrays.asList(new String[] { expectedSearchedPhrases[0],
						expectedSearchedPhrases[1], expectedSearchedPhrases[2] }) });
		cases.add(new Object[] {
				commandType + " " + searchedPhrases[0] + searchedPhrases[1],
				Arrays.asList(new String[] { expectedSearchedPhrases[0],
						expectedSearchedPhrases[1] }) });
		cases.add(new Object[] {
				commandType + " " + searchedPhrases[0] + searchedPhrases[1]
						+ searchedPhrases[2],
				Arrays.asList(new String[] { expectedSearchedPhrases[0],
						expectedSearchedPhrases[1], expectedSearchedPhrases[2] }) });

		// 1c
		cases.add(new Object[] {
				commandType + " " + searchedWords[0] + " " + searchedPhrases[0],
				Arrays.asList(new String[] { searchedWords[0],
						expectedSearchedPhrases[0] }) });
		cases.add(new Object[] {
				commandType + " " + searchedPhrases[0] + " " + searchedWords[0],
				Arrays.asList(new String[] { expectedSearchedPhrases[0],
						searchedWords[0] }) });
		cases.add(new Object[] {
				commandType + " " + searchedWords[0] + " " + searchedPhrases[0] + " "
						+ searchedWords[1],
				Arrays.asList(new String[] { searchedWords[0],
						expectedSearchedPhrases[0], searchedWords[1] }) });
		cases.add(new Object[] {
				commandType + " " + searchedWords[0] + " " + searchedWords[1] + " "
						+ searchedPhrases[0],
				Arrays.asList(new String[] { searchedWords[0], searchedWords[1],
						expectedSearchedPhrases[0] }) });
		cases.add(new Object[] {
				commandType + " " + searchedPhrases[0] + " " + searchedWords[0] + " "
						+ searchedWords[1],
				Arrays.asList(new String[] { expectedSearchedPhrases[0],
						searchedWords[0], searchedWords[1] }) });

		cases.add(new Object[] {
				commandType + " " + searchedPhrases[0] + " " + searchedWords[1] + " "
						+ searchedPhrases[1],
				Arrays.asList(new String[] { expectedSearchedPhrases[0],
						searchedWords[1], expectedSearchedPhrases[1] }) });
		cases.add(new Object[] {
				commandType + " " + searchedPhrases[0] + " " + searchedPhrases[1] + " "
						+ searchedWords[0],
				Arrays.asList(new String[] { expectedSearchedPhrases[0],
						expectedSearchedPhrases[1], searchedWords[0] }) });
		cases.add(new Object[] {
				commandType + " " + searchedWords[0] + " " + searchedPhrases[0] + " "
						+ searchedPhrases[1],
				Arrays.asList(new String[] { searchedWords[0],
						expectedSearchedPhrases[0], expectedSearchedPhrases[1] }) });

		// 1d
		cases.add(new Object[] {
				commandType + " " + searchedWords[0] + searchedWords[1],
				Arrays.asList(new String[] { searchedWords[0] + searchedWords[1] }) });
		cases.add(new Object[] {
				commandType + " " + searchedWords[0] + searchedWords[1]
						+ searchedWords[2],
				Arrays.asList(new String[] { searchedWords[0] + searchedWords[1]
						+ searchedWords[2] }) });

		// 1e
		cases.add(new Object[] {
				commandType + " " + searchedWords[0] + searchedWords[1] + " "
						+ searchedPhrases[0],
				Arrays.asList(new String[] { searchedWords[0] + searchedWords[1],
						expectedSearchedPhrases[0] }) });
		cases.add(new Object[] {
				commandType + " " + searchedPhrases[0] + " " + searchedWords[0]
						+ searchedWords[1],
				Arrays.asList(new String[] { expectedSearchedPhrases[0],
						searchedWords[0] + searchedWords[1] }) });

		// 2a
		cases.add(new Object[] { searchedWords[0],
				Arrays.asList(new String[] { searchedWords[0] }) });

		// 2b
		cases.add(new Object[] { searchedPhrases[0],
				Arrays.asList(new String[] { expectedSearchedPhrases[0] }) });

		// 2c
		cases.add(new Object[] {
				searchedWords[0] + " " + searchedPhrases[0],
				Arrays.asList(new String[] { searchedWords[0],
						expectedSearchedPhrases[0] }) });

		// 2d
		cases.add(new Object[] { searchedWords[0] + searchedWords[1],
				Arrays.asList(new String[] { searchedWords[0] + searchedWords[1] }) });

		// 2e
		cases.add(new Object[] {
				searchedWords[0] + searchedWords[1] + " " + searchedPhrases[0],
				Arrays.asList(new String[] { searchedWords[0] + searchedWords[1],
						expectedSearchedPhrases[0] }) });

		// 3a
		cases.add(new Object[] { " " + searchedWords[0],
				Arrays.asList(new String[] { searchedWords[0] }) });

		// 3b
		cases.add(new Object[] { searchedPhrases[0],
				Arrays.asList(new String[] { expectedSearchedPhrases[0] }) });

		// 3c
		cases.add(new Object[] {
				" " + searchedWords[0] + " " + searchedPhrases[0],
				Arrays.asList(new String[] { searchedWords[0],
						expectedSearchedPhrases[0] }) });

		// 3d
		cases.add(new Object[] { " " + searchedWords[0] + searchedWords[1],
				Arrays.asList(new String[] { searchedWords[0] + searchedWords[1] }) });

		// 3e
		cases.add(new Object[] {
				" " + searchedWords[0] + searchedWords[1] + " " + searchedPhrases[0],
				Arrays.asList(new String[] { searchedWords[0] + searchedWords[1],
						expectedSearchedPhrases[0] }) });

		// 4
		cases.add(new Object[] { emptyString, null });

		// 5
		cases.add(new Object[] { null, null });

		return cases;
	}

	// Test run
	@Test
	public void testDetermineSearchedWords() {
		ArrayList<String> actualSearchedWordsAndPhrases = TaskCommander.parser
				.determineSearchedWords(userCommand);
		String expectedResult = "";
		String actualResult = "";

		if (expectedSearchedWordsAndPhrases != null) {
			for (String expectedSearchedWordOrPhrase : expectedSearchedWordsAndPhrases) {
				expectedResult += " " + expectedSearchedWordOrPhrase;
			}
		}
		if (actualSearchedWordsAndPhrases != null) {
			for (String actualSearchedWordOrPhrase : actualSearchedWordsAndPhrases) {
				actualResult += " " + actualSearchedWordOrPhrase;
			}
		}
		assertEquals(expectedResult, actualResult);
	}
}