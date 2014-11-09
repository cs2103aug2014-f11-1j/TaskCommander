package automatedTestDriver.Data;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

//@author A0109194A
/**
 * This class contains all test cases for the Data class of the Logic Component.
 * 
 */

@RunWith(Suite.class)
@SuiteClasses({
	UndoTest.class,
	MarkTest.class,
	AddInternalTest.class,
	ClearInternalTest.class,
	DeleteInternalTest.class,
	UpdateInternalTest.class
})

public class AllDataTest {
	
}