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
	
<<<<<<< HEAD
	//Static variables
	public static Controller controller;
	public static Data tasks; // Temporary storage
	public static Storage file; // Permanent storage
=======
	/**
	 * Components
	 * (Please note: All components except for the framework class "TaskCommander" are  instantiated. 
	 * In other words, instances instead of classes are used.)
	 */
	// Controller
	public static Controller controller;
	// Parser
	public static Parser parser;
	// Data (temporary memory containing a list of task objects)
	public static Data data;
	// Storage (permanent memory consisting of a local .txt-file)
	public static Storage storage;
	// User Interface
>>>>>>> 2d86fe641f0fa3bbc5216b04a858c9bbda22ff70
	public static UI ui;

	/**
	 * Launch the application
	 * @param  args
	 */
	public static void main(String[] args) {
<<<<<<< HEAD
		// Create components
		ui = new UI();
		controller = new Controller();
		tasks = new Data();
		file = new Storage();
		
		controller.readFromStorage(); // Read old tasks from storage
=======
		
		// Creation of the components
		storage = new Storage();
		data = new Data();
		controller = new Controller();
		parser = new Parser();
		ui = new UI();
>>>>>>> 2d86fe641f0fa3bbc5216b04a858c9bbda22ff70
		
		// Start of the user interface
		UI.open();
	}
}
