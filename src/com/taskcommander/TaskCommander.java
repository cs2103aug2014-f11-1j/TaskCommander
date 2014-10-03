package com.taskcommander;

import java.util.Scanner;

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
	
	// This list stores the lines of tasks temporary
	public static Data tasks;
	
	// This file stores the lines of tasks permanently  on the computer
	public static Storage file;
	
	// Controller
	public Controller controller;

	/*
	 * This variable is declared for the whole class (instead of declaring it
	 * inside the readUserCommand() method to facilitate automated testing using
	 * the I/O redirection technique. If not, only the first line of the input
	 * text file will be processed.
	 */
	private static Scanner scanner;

	/**
	 * Constructor
	 */
	public TaskCommander() {
		controller = new Controller();
		scanner = new Scanner(System.in);
	}

	
	
	
	
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
}
