package com.taskcommander;

public class TaskCommander {
	// Singleton instances of components
	public static Controller controller = Controller.getInstance();
	public static Parser parser = Parser.getInstance();
	public static Storage storage = new Storage(); 
	public static Data data = Data.getInstance(); // temporary memory containing a list of task objects
	public static SyncHandler syncHandler;
	public static UI ui = UI.getInstance();

	/**
	 * Launches the application.
	 * @param  args
	 */
	public static void main(String[] args) {
		ui.open();
	}
	
	/*
	 * Note: only call this when the sync command is used as 
	 * user should not be asked to login until they execute 
	 * a sync command
	 */
	/**
	 * Sets a new SyncHandler instance.
	 * May start a login process if user is not
	 * already logged in.
	 */
	public static void getSyncHandler() {
		syncHandler = new SyncHandler();
	}
	
	/**
	 * Create tasks in the database for UI testing purposes.
	 */
	public static void ini(){
		String userCommand="clear";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"Prepare for CS2103 Final\"";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"Basketball training\" sunday";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"Do CS2105 P3\" Nov 10";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"Read MA2214 textbook\"";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"Finish V0.5 in 10 days\" Oct 30 - Nov 10";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"Have fun with friends\"";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"Be patient with friends\"";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"Go for my dream\" 9pm-4am";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"Play games\" Tomorrow";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"Go swimming\" weekend";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"Talk to people\"";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"Relax!\" 20 minutes later";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"Come up with 20+ tasks\" Winter vacation";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"Get married\" in 20 minutes";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"Get a baby\" 10 years later";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"Make friends\" today";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"Visit grandma\" 3 month later";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"Sing karaoke\" winter vacation";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"Contribute to our project\" Oct 30 - Nov 10";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"Eat an apple\" today";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"Get some exercise\" 9pm";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"Say hello to tutor\" Friday 11 am";
		TaskCommander.controller.executeCommand(userCommand);
		TaskCommander.controller.executeCommand("display");
		TaskCommander.controller.getDisplayedTasks();
		userCommand = "done 1";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "done 6";
		TaskCommander.controller.executeCommand(userCommand);
	}
}
