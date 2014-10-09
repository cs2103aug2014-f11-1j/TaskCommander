package com.taskcommander;

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
	
	// Uncomment to run TableUI! 
	public static TableUI tableui;

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
		
		// Uncomment to run TableUI! 
		// Comment out 'UI.open()' before running this
		tableui = new TableUI();
		tableui.open();
		
		// Start of the user interface
		/*UI.open();*/
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
