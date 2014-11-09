package automatedTestDriver.Parser;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

//@author A0128620M
/**
 * This class runs all unit test cases of the Parser component.
 */

@RunWith(Suite.class)
@SuiteClasses({ DetermineCommandTypeTest.class, DetermineTaskNameTest.class,
    DetermineTaskDateTimeTest.class, DetermineIndexTest.class,
    DetermineSearchedWords.class, ContainsParameterTest.class })
public class AllParserTest {
}
