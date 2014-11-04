package com.taskcommander;

public class TaskCommander {
	
	/**
	 * Components
	 * (Please note: All components except for the framework class "TaskCommander" are  instantiated. 
	 * In other words, instances instead of classes are used.)
	 */
	public static Controller controller = Controller.getInstance();
	public static Parser parser = Parser.getInstance();
	public static Storage storage = new Storage(); 
	public static Data data = Data.getInstance(); // temporary memory containing a list of task objects
	public static SyncHandler syncHandler;
	public static UI ui = UI.getInstance();

	/**
	 * Launch the application
	 * @param  args
	 */
	public static void main(String[] args) {
		ini();
		ui.open();
	}
	
	/*
	 * This method is to create tasks in database for UI testing purpose
	 */
	public static void ini(){
		String userCommand="clear";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"prepare for CS2103 Final\"";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"basketball training\" sunday";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"CS2105 P3\" Nov 10";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"MA2214 reading textbook\"";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"finish V0.5 in 10 days\" Oct 30 - Nov 10";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"have fun with friends\"";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"be patiend to friends\"";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"go get your dream\" 9pm-4am";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"play games\" Tomorrow";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"go swiming\" weekend";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"talk to people\"";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"hey relax your neck it is hurt\" 20 minutes later";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"Help me come up with 20+ tasks pls\" Winter vacation";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"get married\" in 20 minutes";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"get a baby pls\" 10 years later";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"make friends\" today";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"visit grandma\" 3 month later";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"sing k\" winter vacation";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"make contribution to project\" Oct 30 - Nov 10";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"eat an apple\" today";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"get excersice\" 9pm";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "add \"say hello to tutor\" Friday 11 am";
		TaskCommander.controller.executeCommand(userCommand);
		TaskCommander.controller.executeCommand("display");
		TaskCommander.controller.getDisplayedTasks();
		userCommand = "done 1";
		TaskCommander.controller.executeCommand(userCommand);
		userCommand = "done 6";
		TaskCommander.controller.executeCommand(userCommand);
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
