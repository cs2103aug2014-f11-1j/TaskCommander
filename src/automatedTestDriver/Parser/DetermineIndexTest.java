package automatedTestDriver.Parser;

import static org.junit.Assert.*;

import org.junit.Test;

public class DetermineIndexTest {

	/* 
	 * These are test cases for the operation determineIndex(userCommand:String)
	 * 
	 * Initial partition of userCommand: [commandType][index]
	 * Equivalence partitions:
	 * [commandtype]: ("update", "delete", "done", "open"), (any other String), (null)
	 * [index]: (MIN_INT - MAX_INT), (any non integer), (null)
	 */
	/*
	testDetermineIndex("Case 1", Integer.MIN_VALUE+1, "update "+Integer.MIN_VALUE);
	*/
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
