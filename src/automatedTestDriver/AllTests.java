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
import automatedTestDriver.Data.AllDataTest;
import automatedTestDriver.GoogleIntegration.GoogleAPIConnectorTest;
import automatedTestDriver.Integrated.AllIntegratedTest;
import automatedTestDriver.Parser.AllParserTest;
import automatedTestDriver.Storage.StorageTest;

@RunWith(Suite.class)
@SuiteClasses({   ControllerTest.class, AllParserTest.class, AllDataTest.class,
	GoogleAPIConnectorTest.class, StorageTest.class, AllIntegratedTest.class})
public class AllTests {

} 