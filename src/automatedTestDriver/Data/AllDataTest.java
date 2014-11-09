package automatedTestDriver.Data;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This class contains all test cases for the Data class of the Logic Component.
 * 
 * @author A0109194A
 */

@RunWith(Suite.class)
@SuiteClasses({
	UndoTest.class,
	OpenTest.class,
	DoneTest.class,
	AddInternalTest.class,
	ClearInternalTest.class,
	DeleteInternalTest.class,
	UpdateInternalTest.class
})

public class AllDataTest {
	
}