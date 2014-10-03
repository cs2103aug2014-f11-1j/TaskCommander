package com.taskcommander;


/**
 * In general the application “Task Commander” is a uncomplicated, command-line based “todo” list app for PC. 
 * In doing so, it represents a scaled-down version of a “Siri for keyboards” which manages command related 
 * “todo” tasks. Although these tasks are not truly formulated in natural language, the command format is still 
 * flexible yet comfortable to use.

Examples of commands...

 * @author Group F11-1J
 */

public class TaskCommander {
	
	/**
	 * Static variables
	 */
	// Controller
	public static Controller controller;
	// This list stores the lines of tasks temporary
	public static Data tasks;
	// This file stores the lines of tasks permanently  on the computer
	public static Storage file;
	// User Interface
	public static UI ui;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		// Creation of the components:
		ui = new UI();
		controller = new Controller();
		tasks = new Data();
		file = new Storage();
		
		controller.readFromStorage();					// read old tasks from storage
		
		// Start the user interface
		UI.open();
	}
	
	/*
	public void run() {
		controller.readFromStorage();
		while (true) {
			System.out.print("Enter command: ");
			String userCommand = scanner.nextLine();
			String feedback = controller.executeCommand(userCommand);
			showToUser(feedback);
			controller.safeToStorage();
		}
	}


	private void showToUser(String s) {
		System.out.println(s);
	}
	*/
}
