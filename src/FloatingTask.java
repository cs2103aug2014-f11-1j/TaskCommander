import com.google.api.client.util.DateTime;

/*
 * A floating task that has no deadline. 
 * Has a name. May also hold a Google API ID.
 * Related Google API: Tasks
 * 
 * @author Michelle Tan
 */

public class FloatingTask extends Task {
	public FloatingTask(String name){
		super(name);
	}
}
