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
	public static SyncHandler syncHandler;
	public static UI ui;

	/**
	 * Launch the application
	 * @param  args
	 */
	public static void main(String[] args) {
		
		// Creation of the components
		storage = new Storage();
		data = Data.getInstance();
		controller = Controller.getInstance();
		parser = Parser.getInstance();

		ui = UI.getInstance();
		ui.open();
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
