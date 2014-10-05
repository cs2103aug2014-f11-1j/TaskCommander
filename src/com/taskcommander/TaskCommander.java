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
	
	//Static variables
	public static Controller controller;
	public static Data tasks; // Temporary storage
	public static Storage file; // Permanent storage
	public static UI ui;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		// Create components
		ui = new UI();
		controller = new Controller();
		tasks = new Data();
		file = new Storage();
		
		controller.readFromStorage(); // Read old tasks from storage
		
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
