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
	InternalAddTest.class,
	InternalClearTest.class,
	InternalDeleteTest.class,
	InternalUpdateTest.class
})

public class DataTest {
	
}