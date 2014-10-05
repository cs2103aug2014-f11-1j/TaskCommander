package com.taskcommander;


/**
 * In general the application “Task Commander” is a uncomplicated, command-line based “todo” list app for PC. 
 * In doing so, it represents a scaled-down version of a “Siri for keyboards” which manages command related 
 * “todo” tasks. Although these tasks are not truly formulated in natural language, the command format is still 
 * flexible yet comfortable to use.

Examples of commands:
...
..
.

 * @author Group F11-1J
 */

public class TaskCommander {
	
	/**
	 * Components
	 * (Please note: All components except for the framework class "TaskCommander" are  instantiated. 
	 * In other words, instances instead of classes are used.)
	 */
	public static Controller controller;
	public static Parser parser;
	public static Data data; // temporary memory containing a list of task objects
	public static Storage storage; 	// (permanent memory consisting of a local .txt-file
	public static UI ui; // User Interface
	public static SyncHandler syncHandler;

	/**
	 * Launch the application
	 * @param  args
	 */
	public static void main(String[] args) {
		
		// Creation of the components
		storage = new Storage();
		data = new Data();
		controller = new Controller();
		parser = new Parser();
		ui = new UI();
		
		// Start of the user interface
		UI.open();
	}
	
	//Note: only call this when the sync command is used
	//as user should not be asked to login until they
	//execute a sync command
	/**
	 * Sets a new SyncHandler instance.
	 * May start a login process if user is not
	 * already logged in.
	 */
	public static void getSyncHandler() {
		syncHandler = new SyncHandler();
	}
}
