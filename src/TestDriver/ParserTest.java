package TestDriver;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import com.taskcommander.TaskCommander;

/**
 * This class contains all test cases for the Parser class of the Logic Component.
 * 
 * @author A0128620M
 */

public class ParserTest {

	@Test
	public void testParser() {
		
		/* 
		 * These are test cases for the operation determineIndex(userCommand:String)
		 * 
		 * Initial partition of userCommand: [commandType][index]
		 * Equivalence partitions:
		 * [commandtype]: ("update", "delete", "done", "open"), (any other String), (null)
		 * [index]: (MIN_INT - MAX_INT), (any non integer), (null)
		 */
		
		testDetermineIndex("Case 1", Integer.MIN_VALUE+1, "update "+Integer.MIN_VALUE);
	
	}

	private void testDetermineIndex(String description, int expected, String userCommand) {
	    assertEquals(description, expected, TaskCommander.parser.determineIndex(userCommand)); 
	}
	
	/*
	@Test
	public void testDetermineIndex() throws Exception{
		String userCommand = "update "+Integer.MIN_VALUE;
		TaskCommander.parser.determineIndex(userCommand);
		assertEquals(Integer.MIN_VALUE+1, TaskCommander.parser.determineIndex(userCommand));
	}
	
	@Test
	public void testDetermineIndex2() throws Exception{
		String userCommand = "update "+Integer.MAX_VALUE;
		TaskCommander.parser.determineIndex(userCommand);
		assertEquals(Integer.MAX_VALUE+1, TaskCommander.parser.determineIndex(userCommand));
	}
	
	@Test
	public void testDetermineIndex3() throws Exception{
		String userCommand = "update "+Integer.MIN_VALUE+1;
		TaskCommander.parser.determineIndex(userCommand);
		assertEquals(Integer.MIN_VALUE+2, TaskCommander.parser.determineIndex(userCommand));
	}
	
	@Test
	public void testDetermineIndex4() throws Exception{
		String userCommand = "update "+(Integer.MAX_VALUE-1);
		TaskCommander.parser.determineIndex(userCommand);
		assertEquals(Integer.MAX_VALUE, TaskCommander.parser.determineIndex(userCommand));
	}
	
	@Test
	public void testDetermineIndex5() throws Exception{
		String userCommand = "update "+"anyNonInteger";
		TaskCommander.parser.determineIndex(userCommand);
		assertEquals(-1, TaskCommander.parser.determineIndex(userCommand));
	}
	
	@Test
	public void testDetermineIndex6() throws Exception{
		String userCommand = "anyOtherString "+null;
		TaskCommander.parser.determineIndex(userCommand);
		assertEquals(-1, TaskCommander.parser.determineIndex(userCommand));
	}
	
	@Test
	public void testDetermineIndex7() throws Exception{
		String userCommand = "anyOtherString "+Integer.MIN_VALUE;
		TaskCommander.parser.determineIndex(userCommand);
		assertEquals(Integer.MIN_VALUE+1, TaskCommander.parser.determineIndex(userCommand));
	}
	
	@Test
	public void testDetermineIndex8() throws Exception{
		String userCommand = "anyOtherString "+Integer.MAX_VALUE;
		TaskCommander.parser.determineIndex(userCommand);
		assertEquals(Integer.MAX_VALUE+1, TaskCommander.parser.determineIndex(userCommand));
	}
	
	@Test
	public void testDetermineIndex9() throws Exception{
		String userCommand = "anyOtherString "+Integer.MIN_VALUE+1;
		TaskCommander.parser.determineIndex(userCommand);
		assertEquals(Integer.MIN_VALUE+2, TaskCommander.parser.determineIndex(userCommand));
	}
	
	@Test
	public void testDetermineIndex10() throws Exception{
		String userCommand = "anyOtherString "+(Integer.MAX_VALUE-1);
		TaskCommander.parser.determineIndex(userCommand);
		assertEquals(Integer.MAX_VALUE, TaskCommander.parser.determineIndex(userCommand));
	}
	
	@Test
	public void testDetermineIndex11() throws Exception{
		String userCommand = "anyOtherString "+"anyNonInteger";
		TaskCommander.parser.determineIndex(userCommand);
		assertEquals(-1, TaskCommander.parser.determineIndex(userCommand));
	}
	
	@Test
	public void testDetermineIndex12() throws Exception{
		String userCommand = "anyOtherString "+null;
		TaskCommander.parser.determineIndex(userCommand);
		assertEquals(-1, TaskCommander.parser.determineIndex(userCommand));
	}
	
	@Test
	public void testDetermineIndex13() throws Exception{
		String userCommand = Integer.toString(Integer.MIN_VALUE);
		TaskCommander.parser.determineIndex(userCommand);
		assertEquals(-1, TaskCommander.parser.determineIndex(userCommand));
	}
	
	@Test
	public void testDetermineIndex14() throws Exception{
		String userCommand = Integer.toString(Integer.MAX_VALUE);
		TaskCommander.parser.determineIndex(userCommand);
		assertEquals(-1, TaskCommander.parser.determineIndex(userCommand));
	}
	
	@Test
	public void testDetermineIndex15() throws Exception{
		String userCommand = Integer.toString(Integer.MIN_VALUE+1);
		TaskCommander.parser.determineIndex(userCommand);
		assertEquals(-1, TaskCommander.parser.determineIndex(userCommand));
	}
	
	@Test
	public void testDetermineIndex16() throws Exception{
		String userCommand = Integer.toString(Integer.MAX_VALUE-1);
		TaskCommander.parser.determineIndex(userCommand);
		assertEquals(-1, TaskCommander.parser.determineIndex(userCommand));
	}
	
	@Test
	public void testDetermineIndex17() throws Exception{
		String userCommand = "anyNonInteger";
		TaskCommander.parser.determineIndex(userCommand);
		assertEquals(-1, TaskCommander.parser.determineIndex(userCommand));
	}
	
	@Test
	public void testDetermineIndex18() throws Exception{
		String userCommand = null;
		TaskCommander.parser.determineIndex(userCommand);
		assertEquals(-1, TaskCommander.parser.determineIndex(userCommand));
	}
	*/
}
