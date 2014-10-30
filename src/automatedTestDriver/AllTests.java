package automatedTestDriver;

/**
 * This class combines several test classes into a test suite. Running the test suite will execute all 
 * test classes in that suite in the specified order.
 * 
 * @author A0128620M
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import automatedTestDriver.Controller.ControllerTest;
import automatedTestDriver.Data.DataTest;
import automatedTestDriver.GoogleIntegration.GoogleAPIConnectorTest;
import automatedTestDriver.Integrated.TaskCommanderTest;
import automatedTestDriver.Parser.AllParserTest;

@RunWith(Suite.class)
@SuiteClasses({  TaskCommanderTest.class, ControllerTest.class, AllParserTest.class, DataTest.class,
	GoogleAPIConnectorTest.class, StorageTest.class})
public class AllTests {

} 