package automatedTestDriver.Controller;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

//@author A0128620M
/**
* This class runs all unit test cases of the Controller component.
*/

public class AllControllerTest {
    DataStub dataStub;

    
    @Before
    public void setTestEnvironment() {
        dataStub = new DataStub();
    }

	@Test
	public void testGetDisplayedTasks() {
		fail("Not yet implemented");
	}

}
