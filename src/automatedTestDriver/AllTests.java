package automatedTestDriver;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import automatedTestDriver.Controller.AllControllerTest;
import automatedTestDriver.Data.AllDataTest;
import automatedTestDriver.GoogleIntegration.GoogleAPIConnectorTest;
import automatedTestDriver.Integrated.AllIntegratedTest;
import automatedTestDriver.Parser.AllParserTest;
import automatedTestDriver.Storage.StorageTest;

//@author A0128620M
/**
 * This class runs both all integrated and unit tests by combining them into one test suite.
 */

@RunWith(Suite.class)
@SuiteClasses({ AllControllerTest.class, AllParserTest.class, AllDataTest.class,
    GoogleAPIConnectorTest.class, StorageTest.class, AllIntegratedTest.class })
public class AllTests {

}