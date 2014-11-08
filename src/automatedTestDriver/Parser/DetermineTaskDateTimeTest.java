package automatedTestDriver.Parser;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import com.taskcommander.Global;
import com.taskcommander.TaskCommander;

//@author A0128620M
/**
* This class contains all test cases for the method determineTakDateTime(userCommand:String) of the component Parser.
*/
@RunWith(Parameterized.class)
public class DetermineTaskDateTimeTest {
	private String userCommand;
	private List<Date> expectedDateTimes;

	public DetermineTaskDateTimeTest(String userCommand, List<Date> expectedDateTimes) {
		this.userCommand = userCommand;
		this.expectedDateTimes = expectedDateTimes;
	}
	
	/* Test structure
	 * 
	 * Initial partition of parameter "userCommand":
	 * - [commandType with/without index]+[space(s)]+[any string]+[space(s)]+[DateTime(s)]+[space(s)]+[any string]								
	 * - [commandType with/without index]+[space(s)]+[any string]+[space(s)]+[DateTime(s)]														
	 * - [commandType with/without index]+[space(s)]+[DateTime(s)]+[space(s)]+[any string]														
	 * - [commandType with/without index]+[space(s)]+[DateTime(s)]																				
	 * - [space(s)]+[DateTime(s)]																									
	 * - [DateTime(s)]																										
	 * - [commandType with/without index]+[space(s)]+[any string]+[space(s)]+[DateTime(s)]+[space(s)]+[any string]+[space(s)]+[DateTime(s)]+[space(s)]+[any string]								
	 * - [commandType with/without index]+[space(s)]+[any string]+[space(s)]+[DateTime(s)]+[space(s)]+[any string]+[space(s)]+[DateTime(s)]													
	 * - [commandType with/without index]+[space(s)]+[DateTime(s)]+[space(s)]+[any string]+[space(s)]+[DateTime(s)]	
	 * - [space(s)]+[DateTime(s)]+[space(s)]+[any string]+[space(s)]+[DateTime(s)]
	 * - [DateTime(s)]+[space(s)]+[any string]+[space(s)]+[DateTime(s)]
	 * - [empty string]
	 * - [null]					
	 * Further partition of [DateTime(s)]:
	 * - [one DateTime]																											
	 * - [two DateTime]																										
	 * - [three and more DateTime]																										
	 * Further partition of [one date]:
	 * - [formal dates]: yyyy-mm-dd, yyyy/mm/dd, dd/mm/yyyy, mm/dd/yy ..
	 * - [relaxed dates]: Nov 5, Nov 5th, Nov 5 '14, Nov 5 2014 ..
	 * - [relative dates]: next monday, today, tomorrow, next week ..
	 * - [formal time]: hh:mm, hham, hhpm, hh.mmam, hh.mmpm,  hh am, hh pm, hh.mm am, hh.mm pm
	 * - [relaxed time]: noon, evening, midnight
	 * - [relative times]: in 30 minutes, 6 hours ago
	 * Further partition of [two DateTime]:	
	 * - [both DateTime separated by a "to"]																					
	 * - [both DateTime separated by a "-" which is surrounded by spaces] 	
	 * - [first date is before second date]
	 * - [first date is after second date]																												
	 * - [only one date with two times given where second time is after the first time]
	 * - [only one date with two times given where second time is before the first time]
	 */
	
	// Test parameters
	@Parameterized.Parameters
	public static Collection<Object[]>  cases() {
		String[] commandTypeWithOrWithoutIndex = {"add", "update 3"};
		String[] anyString = {"/\"meeting with John\"", "none", "another thing"};
		String emptyString = "";
		
		Calendar calendar = Calendar.getInstance();
		
		String inputDateTime1 = "2014-10-03 15:00";
		calendar.set(2014,9,3,15,0);
		Date dateTime1 = calendar.getTime();
		List<Date> expectedDateTimes1 = new ArrayList<Date>();
		expectedDateTimes1.add(dateTime1);
		
		String inputDateTime2 = "2014/10/03 3pm";
		calendar.set(2014,9,3,15,0);
		Date dateTime2 = calendar.getTime();
		List<Date> expectedDateTimes2 = new ArrayList<Date>();
		expectedDateTimes2.add(dateTime2);
		
		String inputDateTime3 = "10/03 3.00pm";
		calendar.set(2014,9,3,15,0);
		Date dateTime3 = calendar.getTime();
		List<Date> expectedDateTimes3 = new ArrayList<Date>();
		expectedDateTimes3.add(dateTime3);
		
		String inputDateTime4 = "10/03/14 3 pm";
		calendar.set(2014,9,3,15,0);
		Date dateTime4 = calendar.getTime();
		List<Date> expectedDateTimes4 = new ArrayList<Date>();
		expectedDateTimes4.add(dateTime4);
		
		String inputDateTime5 = "Oct 3 3.00 pm";
		calendar.set(2014,9,3,15,0);
		Date dateTime5 = calendar.getTime();
		List<Date> expectedDateTimes5 = new ArrayList<Date>();
		expectedDateTimes5.add(dateTime5);
		
		String inputDateTime6 = "Oct 3rd 3.00 Pm";
		calendar.set(2014,9,3,15,0);
		Date dateTime6 = calendar.getTime();
		List<Date> expectedDateTimes6 = new ArrayList<Date>();
		expectedDateTimes6.add(dateTime6);
		
		String inputDateTime7 = "Oct 3 '14 3:00 PM";
		calendar.set(2014,9,3,15,0);
		Date dateTime7 = calendar.getTime();
		List<Date> expectedDateTimes7 = new ArrayList<Date>();
		expectedDateTimes7.add(dateTime7);
		
		String inputDateTime8 = "Oct 3 2014 15";
		calendar.set(2014,9,3,15,0);
		Date dateTime8 = calendar.getTime();
		List<Date> expectedDateTimes8 = new ArrayList<Date>();
		expectedDateTimes8.add(dateTime8);
	
		String inputDateTime9 = "next Monday";
		calendar = Calendar.getInstance();
		int weekday = calendar.get(Calendar.DAY_OF_WEEK);  
		if (weekday != Calendar.MONDAY) {  
		    int days = (Calendar.SATURDAY - weekday + 2) % 7;  
		    calendar.add(Calendar.DAY_OF_YEAR, days);  
		}  
		Date dateTime9 = calendar.getTime();
		List<Date> expectedDateTimes9 = new ArrayList<Date>();
		expectedDateTimes9.add(dateTime9);
		
		String inputDateTime10 = "today";
		Calendar today = Calendar.getInstance();  
		Date dateTime10 = today.getTime();
		List<Date> expectedDateTimes10 = new ArrayList<Date>();
		expectedDateTimes10.add(dateTime10);
		
		String inputDateTime11 = "tommorrow";
		Calendar tommorrow = Calendar.getInstance(); 
		tommorrow.add(Calendar.DATE, 1);
		Date dateTime11 = tommorrow.getTime();
		List<Date> expectedDateTimes11 = new ArrayList<Date>();
		expectedDateTimes11.add(dateTime11);
		
		String inputDateTime12 = "next week";
		Calendar nextWeek = Calendar.getInstance(); 
		nextWeek.add(Calendar.DATE, 7);
		Date dateTime12 = nextWeek.getTime();
		List<Date> expectedDateTimes12 = new ArrayList<Date>();
		expectedDateTimes12.add(dateTime12);
		
		String inputDateTime13 = "Nov 5th noon";
		calendar.set(2014,10,5,12,0);
		Date dateTime13 = calendar.getTime();
		List<Date> expectedDateTimes13 = new ArrayList<Date>();
		expectedDateTimes13.add(dateTime13);	
		
		String inputDateTime14 = "Nov 5th evening";
		calendar.set(2014,10,5,19,0);
		Date dateTime14 = calendar.getTime();
		List<Date> expectedDateTimes14 = new ArrayList<Date>();
		expectedDateTimes14.add(dateTime14);
		
		String inputDateTime15 = "Nov 5th midnight";
		calendar.set(2014,10,5,00,0);
		Date dateTime15 = calendar.getTime();
		List<Date> expectedDateTimes15 = new ArrayList<Date>();
		expectedDateTimes15.add(dateTime15);
		
		String inputDateTime16 = "in 30 min";
		Calendar in30Min = Calendar.getInstance(); 
		in30Min.add(Calendar.MINUTE, 30);
		Date dateTime16 = in30Min.getTime();
		List<Date> expectedDateTimes16 = new ArrayList<Date>();
		expectedDateTimes16.add(dateTime16);
		
		String inputDateTime17 = "2014-10-03 15:00 to 16:00";
		calendar.set(2014,9,3,15,0);
		Date dateTime17a = calendar.getTime();
		calendar.set(2014,9,3,16,0);
		Date dateTime17b = calendar.getTime();
		List<Date> expectedDateTimes17 = new ArrayList<Date>();
		expectedDateTimes17.add(dateTime17a);
		expectedDateTimes17.add(dateTime17b);

		String inputDateTime18 = "2014-10-03 15:00 To 16:00";
		calendar.set(2014,9,3,15,0);
		Date dateTime18a = calendar.getTime();
		calendar.set(2014,9,3,16,0);
		Date dateTime18b = calendar.getTime();
		List<Date> expectedDateTimes18 = new ArrayList<Date>();
		expectedDateTimes18.add(dateTime18a);
		expectedDateTimes18.add(dateTime18b);
		
		String inputDateTime19 = "2014-10-03 15:00 TO 16:00";
		calendar.set(2014,9,3,15,0);
		Date dateTime19a = calendar.getTime();
		calendar.set(2014,9,3,16,0);
		Date dateTime19b = calendar.getTime();
		List<Date> expectedDateTimes19 = new ArrayList<Date>();
		expectedDateTimes19.add(dateTime19a);
		expectedDateTimes19.add(dateTime19b);
		
		String inputDateTime20 = "2014-10-03 15:00 - 16:00";
		calendar.set(2014,9,3,15,0);
		Date dateTime20a = calendar.getTime();
		calendar.set(2014,9,3,16,0);
		Date dateTime20b = calendar.getTime();
		List<Date> expectedDateTimes20 = new ArrayList<Date>();
		expectedDateTimes20.add(dateTime20a);
		expectedDateTimes20.add(dateTime20b);
		
		String inputDateTime21 = "2014-10-03 15:00 - Oct 4th '14 12:00";
		calendar.set(2014,9,3,15,0);
		Date dateTime21a = calendar.getTime();
		calendar.set(2014,9,4,12,0);
		Date dateTime21b = calendar.getTime();
		List<Date> expectedDateTimes21 = new ArrayList<Date>();
		expectedDateTimes21.add(dateTime21a);
		expectedDateTimes21.add(dateTime21b);
		
		String inputDateTime22 = "2014-10-03 15:00 - Oct 1st '14 12:00";
		calendar.set(2014,9,3,15,0);
		Date dateTime22a = calendar.getTime();
		calendar.set(2014,9,1,12,0);
		Date dateTime22b = calendar.getTime();
		List<Date> expectedDateTimes22 = new ArrayList<Date>();
		expectedDateTimes22.add(dateTime22b);
		expectedDateTimes22.add(dateTime22a);
		
		String inputDateTime23 = "2014-10-03 15:00 - 12:00";
		calendar.set(2014,9,3,15,0);
		Date dateTime23a = calendar.getTime();
		calendar.set(2014,9,4,12,0);
		Date dateTime23b = calendar.getTime();
		List<Date> expectedDateTimes23 = new ArrayList<Date>();
		expectedDateTimes23.add(dateTime23a);
		expectedDateTimes23.add(dateTime23b);
		
		return Arrays.asList(new Object[][] {
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime1+" "+"anyString[1]", expectedDateTimes1},
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime1, expectedDateTimes1},
				{ commandTypeWithOrWithoutIndex[0]+" "+inputDateTime1+" "+anyString[0], expectedDateTimes1},
				{ commandTypeWithOrWithoutIndex[0]+" "+inputDateTime1, expectedDateTimes1},
				{ " "+inputDateTime1, expectedDateTimes1},
				{ inputDateTime1, expectedDateTimes1},
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime1+" "+"anyString[1]"+" "+inputDateTime13+" "+"anyString[2]", expectedDateTimes13},
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime1+" "+"anyString[1]"+" "+inputDateTime13, expectedDateTimes13},
				{ commandTypeWithOrWithoutIndex[0]+" "+inputDateTime1+" "+"anyString[1]"+" "+inputDateTime13, expectedDateTimes13},
				{ " "+inputDateTime1+" "+"anyString[1]"+" "+inputDateTime13, expectedDateTimes13},
				{ inputDateTime1+" "+"anyString[1]"+" "+inputDateTime13, expectedDateTimes13},
				{ emptyString, null},
				{ null, null},
				
				{ commandTypeWithOrWithoutIndex[1]+" "+anyString[0]+" "+inputDateTime1+" "+"anyString[1]", expectedDateTimes1},
				{ commandTypeWithOrWithoutIndex[1]+" "+anyString[0]+" "+inputDateTime1, expectedDateTimes1},
				{ commandTypeWithOrWithoutIndex[1]+" "+inputDateTime1+" "+anyString[0], expectedDateTimes1},
				{ commandTypeWithOrWithoutIndex[1]+" "+inputDateTime1, expectedDateTimes1},
				{ commandTypeWithOrWithoutIndex[1]+" "+anyString[0]+" "+inputDateTime1+" "+"anyString[1]"+" "+inputDateTime13+" "+"anyString[2]", expectedDateTimes13},
				{ commandTypeWithOrWithoutIndex[1]+" "+anyString[0]+" "+inputDateTime1+" "+"anyString[1]"+" "+inputDateTime13, expectedDateTimes13},
				{ commandTypeWithOrWithoutIndex[1]+" "+inputDateTime1+" "+"anyString[1]"+" "+inputDateTime13, expectedDateTimes13},
				
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime2+" "+"anyString[1]", expectedDateTimes2},
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime3+" "+"anyString[1]", expectedDateTimes3},
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime4+" "+"anyString[1]", expectedDateTimes4},
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime5+" "+"anyString[1]", expectedDateTimes5},
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime6+" "+"anyString[1]", expectedDateTimes6},
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime7+" "+"anyString[1]", expectedDateTimes7},
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime8+" "+"anyString[1]", expectedDateTimes8},
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime9+" "+"anyString[1]", expectedDateTimes9},
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime10+" "+"anyString[1]", expectedDateTimes10},
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime11+" "+"anyString[1]", expectedDateTimes11},
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime12+" "+"anyString[1]", expectedDateTimes12},
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime13+" "+"anyString[1]", expectedDateTimes13},
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime14+" "+"anyString[1]", expectedDateTimes14},
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime15+" "+"anyString[1]", expectedDateTimes15},
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime16+" "+"anyString[1]", expectedDateTimes16},
				
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime17+" "+"anyString[1]", expectedDateTimes17},
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime18+" "+"anyString[1]", expectedDateTimes18},
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime20+" "+"anyString[1]", expectedDateTimes20},
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime21+" "+"anyString[1]", expectedDateTimes21},
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime22+" "+"anyString[1]", expectedDateTimes22},
				{ commandTypeWithOrWithoutIndex[0]+" "+anyString[0]+" "+inputDateTime23+" "+"anyString[1]", expectedDateTimes23},
		});
	}
	
	// Test run
	@Test
	public void testDetermineTaskDateTime() {
		List<Date> actualDateTimes = TaskCommander.parser
				.determineTaskDateTime(userCommand);
		String expectedResult = "";
		String actualResult = "";
		if (expectedDateTimes != null) {
			System.out.println(expectedDateTimes.size());
			System.out.println(expectedResult);
			System.out.println(actualDateTimes.size());
			System.out.println(actualResult);

			for (Date expectedDateTime : expectedDateTimes) {
				expectedResult += " "
						+ Global.dayFormat.format(expectedDateTime) + " "
						+ Global.timeFormat.format(expectedDateTime);
			}
		}
		if (actualDateTimes != null) {
			System.out.println(expectedDateTimes.size());
			System.out.println(expectedResult);
			System.out.println(actualDateTimes.size());
			System.out.println(actualResult);
			for (Date actualDateTime : actualDateTimes) {
				actualResult += " " + Global.dayFormat.format(actualDateTime)
						+ " " + Global.timeFormat.format(actualDateTime);
			}
		}
		assertEquals(expectedResult, actualResult);
	}
}