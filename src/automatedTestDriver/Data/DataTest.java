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
	DoneTest.class,
	InternalAddTest.class,
	InternalClearTest.class,
	InternalDeleteTest.class,
	InternalUpdateTest.class,
	UndoTest.class
})

public class DataTest {
	
}