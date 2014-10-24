package TestDriver;

/**
 * This class combines several test classes into a test suite. Running the test suite will execute all 
 * test classes in that suite in the specified order.
 * 
 * @author A0128620M
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TaskCommanderTest.class, ParserTest.class, GoogleAPIConnectorTest.class })
public class AllTests {

} 