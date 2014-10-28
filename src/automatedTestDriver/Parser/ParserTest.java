package automatedTestDriver.Parser;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * This runs all test cases for the Parser class of the Logic Component.
 * 
 * @author A0128620M
 */

@RunWith(Suite.class)
@SuiteClasses({  DetermineCommandTypeTest.class, DetermineTaskNameTest.class, DetermineTaskDateTimeTest.class, DetermineIndexTest.class, DetermineSearchedWords.class, ContainsParameterTest.class })
public class ParserTest {
	
}
