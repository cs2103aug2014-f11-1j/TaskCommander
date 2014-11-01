package automatedTestDriver.Integrated;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * This runs all test cases for the Integrated class.
 * 
 * 
 */
//@author A0105753J
@RunWith(Suite.class)
@SuiteClasses({ IntegratedAddTest.class, IntegratedOpenTest.class, IntegratedDeleteTest.class,IntegratedMarkTest.class,
	IntegratedDisplayTest.class, IntegratedSyncTest.class, IntegratedClearTest.class,IntegratedUpdateTest.class})
public class AllIntegratedTest {
	
}
