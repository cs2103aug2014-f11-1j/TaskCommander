package automatedTestDriver;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import automatedTestDriver.Data.AllDataTest;
import automatedTestDriver.GoogleIntegration.GoogleAPIConnectorTest;
import automatedTestDriver.IntegratedController.AllIntegratedControllerTest;
import automatedTestDriver.Parser.AllParserTest;
import automatedTestDriver.Storage.StorageTest;

//@author A0128620M
/**
 * This class runs both all tests by combining them into one test suite.
 */

@RunWith(Suite.class)
@SuiteClasses({ AllParserTest.class, AllDataTest.class,
    GoogleAPIConnectorTest.class, StorageTest.class,
    AllIntegratedControllerTest.class })
public class AllTests {
}