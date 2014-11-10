package com.taskcommander;

import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

//@author A0112828H
/**
 * Singleton class that has the following functions:
 * - Creates the UI
 * - Receives user input and sends it to the controller
 * - Receives feedback from controller and shows it to the user
 * - Facilitates Google sync function by allowing user to login through a local browser
 * - Receives user's authorisation code and sends it to the controller
 */
public class UI extends Observable implements Observer {
	private static UI ui;
	
	// Variables to adjust UI element sizes and layout behaviour
	private static final int SHELL_MIN_HEIGHT = 400;
	private static final int SHELL_MIN_WIDTH = 500;

	private static final int TAB_GRID_COLUMNS_NUM = 1;
	private static final int MAIN_GRID_COLUMNS_NUM = 2;
	private static final int BROWSER_GRID_COLUMNS_NUM = 1;
	private static final boolean GRID_COLUMNS_EQUAL_SIZE = false;

	private static final boolean INPUT_FIT_HORIZONTAL = true;
	private static final boolean INPUT_FIT_VERTICAL = false;
	private static final int INPUT_COLUMNS_SPAN = 1;
	private static final int INPUT_ROWS_SPAN = 1;
	private static final int INPUT_PREFERRED_WIDTH = 500;

	private static final boolean OUTPUT_FIT_HORIZONTAL = true;
	private static final boolean OUTPUT_FIT_VERTICAL = false;
	private static final int OUTPUT_COLUMNS_SPAN = 2;
	private static final int OUTPUT_ROWS_SPAN = 1;
	private static final int OUTPUT_PREFERRED_WIDTH = 500;

	private static final int TABLE_STYLE = SWT.NONE;
	private static final boolean TABLE_FIT_HORIZONTAL = true;
	private static final boolean TABLE_FIT_VERTICAL = true;
	private static final int TABLE_COLUMNS_SPAN = 2;
	private static final int TABLE_ROWS_SPAN = 1;
	private static final int TABLE_PREFERRED_WIDTH = 500;
	private static final int TABLE_PREFERRED_HEIGHT = 200;
	private static final int TABLE_COLUMNS_NUM = 4;
	private static final String[] TABLE_COLUMNS_NAMES = {"No.", "Date", "Task", "Status"};

	private static final boolean HELP_FIT_HORIZONTAL = true;
	private static final boolean HELP_FIT_VERTICAL = true;
	private static final int HELP_COLUMNS_SPAN = 1;
	private static final int HELP_ROWS_SPAN = 1;
	
	private static final boolean BROWSER_FIT_HORIZONTAL = true;
	private static final boolean BROWSER_FIT_VERTICAL = true;
	private static final int BROWSER_COLUMNS_SPAN = 1;
	private static final int BROWSER_ROWS_SPAN = 1;
	private static final int BROWSER_PREFERRED_WIDTH = 500;
	private static final int BROWSER_PREFERRED_HEIGHT = 120;

	private static final String TAB_MAIN_NAME = "Tasks";
	private static final String TAB_HELP_NAME = "Help";
	private static final int TAB_BROWSER_INDEX = 2; // Third tab item
	private static final String TAB_BROWSER_NAME = "Google Login";

	// Immutable UI element instances
	private final Display display = Display.getDefault();
	private final Shell shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN & (~SWT.RESIZE));
	private final TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
	private final Composite mainWindow = new Composite(tabFolder, SWT.FILL & (~SWT.RESIZE));
	private final Composite helpWindow = new Composite(tabFolder, SWT.FILL & (~SWT.RESIZE));
	private final Composite browserWindow = new Composite(tabFolder, SWT.FILL & (~SWT.RESIZE));
	private final Table table = new Table(mainWindow, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);

	// System colours for use to colour UI elements
	private final Color red = display.getSystemColor(SWT.COLOR_RED);
	private final Color darkGreen = display.getSystemColor(SWT.COLOR_DARK_GREEN);
	private final Color blue = display.getSystemColor(SWT.COLOR_BLUE);
	private final Color darkGray = display.getSystemColor(SWT.COLOR_DARK_GRAY);
	private final Color darkBlue = display.getSystemColor(SWT.COLOR_DARK_BLUE);

	// Colours set for table columns
	private final Color COLOR_COL_FIRST = darkGray;
	private final Color COLOR_COL_SECOND = blue;
	private final Color COLOR_COL_THIRD = darkBlue;
	private final Color COLOR_DONE = darkGreen;
	private final Color COLOR_NOT_DONE = red;

	// String messages for display
	private static final String INFO_DISPLAY = "Displaying: ";
	private static final String INFO_HELP = "You can use the following commands: \n" +
	    "\n"+
	    "-  add <\"task title\"> <start date> <start time> <to|-> [end date] <end time> \n" +
	    "-  add \"<task title>\" <end date> <end time> \n" +
	    "-  add \"<task title>\" \n" +
	    "\n"+
			"-  display [timed] [deadline] [none] [done|open] [start date] [start time] [to|-] [end date] [end time] \n" +
			"-  display [all] \n" +
			"\n"+
			"-  update [\"task title\"] [ none | <end date> <end time> | <start  date> <start time> [end date>] <end time>] \n" +
	    "\n"+
			"-  open <index> \n" +
			"-  done <index> \n" +
			"\n"+
			"-  delete <index> \n" +
			"-  clear \n" +
	    "\n"+
	    "-  undo \n" +
      "\n"+
      "-  search <key word or phrase> | <\"exact key word or phrase\"> \n"+
      "\n"+
      "-  sync \n" +
      "\n"+
			"-  exit \n"+
			"\n"+
	    "For more help, you can access our user guide here: http://goo.gl/6bjc7i";
	private static final String INSTRUCTIONS_MAIN = "Enter command: ";
	private static final String INSTRUCTIONS_BROWSER = "Please login to Google and accept application permissions to sync your tasks.";
	
	// Logger instance
	private static Logger logger = Logger.getLogger(UI.class.getName());
	
	// Mutable UI element instances
	private TabItem mainTab;
	private TabItem browserTab;
	private Text input;
	private Text displayOutput;
	private Text output;
	private Browser browser;
	
	private String displaySettingText;
	private String code; // For authorisation code from Google

	//@author A0105753J
	/**
	 * Returns the only instance of UI. 
	 */
	public static UI getInstance(){
		if(ui == null) {
			ui = new UI();
		}
		return ui;
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		logger.log(Level.INFO,"Open UI");
		setupShell();
		setupTabFolder();
		createMainTab();
		createHelpTab();
		runUntilWindowClosed();
	}

	// Shell setup
	private void setupShell() {
		shell.setLayout(new FillLayout());
		shell.setText(Global.APPLICATION_NAME);
		shell.setMinimumSize(SHELL_MIN_WIDTH, SHELL_MIN_HEIGHT);
		shell.setBounds(shell.getBounds().x, shell.getBounds().y, SHELL_MIN_WIDTH, SHELL_MIN_HEIGHT);
	}

	// Tabs setup
	private void setupTabFolder() {
		tabFolder.setLayout(new GridLayout(TAB_GRID_COLUMNS_NUM, GRID_COLUMNS_EQUAL_SIZE));
		tabFolder.setSize(SHELL_MIN_WIDTH, SHELL_MIN_HEIGHT);
	}

	// Main tab setup
	private void createMainTab() {
		mainTab = new TabItem(tabFolder, SWT.NONE);
		mainTab.setText(TAB_MAIN_NAME);
		setupMainWindow();
		mainTab.setControl(mainWindow);
	}

	private void setupMainWindow() {
		GridLayout layout = new GridLayout(MAIN_GRID_COLUMNS_NUM, GRID_COLUMNS_EQUAL_SIZE);
		mainWindow.setLayout(layout);
		mainWindow.setBounds(shell.getBounds());

		createTextFieldsForMain();
		setupMainElements();
		addInputListenerForMain();
		updateDisplay();
	}

	private void createTextFieldsForMain() {
		new Label(mainWindow, SWT.NONE).setText(INFO_DISPLAY);
		displayOutput = new Text(mainWindow, SWT.WRAP);
		output = new Text(mainWindow, SWT.BORDER | SWT.WRAP);
		new Label(mainWindow, SWT.NONE).setText(INSTRUCTIONS_MAIN);
		input = new Text(mainWindow, SWT.BORDER);
	}

	private void setupMainElements() {
		setupDisplayOutput();
		setupInput();
		setupOutput();
		setupTable();
	}

	private void setupDisplayOutput() {
		displayOutput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, INPUT_FIT_HORIZONTAL, INPUT_FIT_VERTICAL, 
				INPUT_COLUMNS_SPAN, INPUT_ROWS_SPAN));
		updateDisplaySettings();
	}

	/**
	 * Gets the latest display settings in a String from the Controller
	 * and sets it as the text for the display output.
	 */
	private void updateDisplaySettings() {
		displaySettingText = TaskCommander.controller.getDisplaySettingsDescription();
		displayOutput.setText(displaySettingText);
	}

	private void setupInput() {
		GridData inputGridData = new GridData(SWT.FILL, SWT.CENTER, INPUT_FIT_HORIZONTAL, INPUT_FIT_VERTICAL, 
				INPUT_COLUMNS_SPAN, INPUT_ROWS_SPAN);
		inputGridData.widthHint = INPUT_PREFERRED_WIDTH;
		input.setLayoutData(inputGridData);
	}

	private void setupOutput() {
		GridData outputGridData = new GridData(SWT.FILL, SWT.CENTER, OUTPUT_FIT_HORIZONTAL, OUTPUT_FIT_VERTICAL, 
				OUTPUT_COLUMNS_SPAN, OUTPUT_ROWS_SPAN);
		outputGridData.widthHint = OUTPUT_PREFERRED_WIDTH;
		output.setLayoutData(outputGridData);
		output.setText(Global.MESSAGE_WELCOME);
		output.setForeground(darkBlue);
		output.setEditable(false);
	}

	private void setupTable() {
		GridData tableGridData = new GridData(SWT.FILL, SWT.FILL, TABLE_FIT_HORIZONTAL, TABLE_FIT_VERTICAL, 
				TABLE_COLUMNS_SPAN, TABLE_ROWS_SPAN);
		tableGridData.widthHint = TABLE_PREFERRED_WIDTH;
		tableGridData.heightHint = TABLE_PREFERRED_HEIGHT;
		table.setLayoutData(tableGridData);
		table.setHeaderVisible(true);
		for (int i = 0; i < TABLE_COLUMNS_NUM; i++) {
			TableColumn column = new TableColumn(table, TABLE_STYLE);
			column.setText(TABLE_COLUMNS_NAMES[i]);
		}
	}

	/**
	 * Adds a listener to the input text field. 
	 * 
	 * Listener is triggered when the return key (Enter) is hit. 
	 * The user input is then passed on to the Controller component,
	 * which returns a String to be displayed to the user.
	 * Calls the display to refresh after each user command.
	 */
	private void addInputListenerForMain() {
		input.addListener(SWT.Traverse, new Listener(){
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				if(event.detail == SWT.TRAVERSE_RETURN)
					try {
						logger.log(Level.INFO,"Receive input command with enter key");
						clearTableItems();
						String command = input.getText();
						String feedback = TaskCommander.controller.executeCommand(command);
						updateDisplay(feedback);
						updateDisplaySettings();
						clearInput();
					}catch (Exception e) {
						logger.log(Level.WARNING,"Exception while executing command flow", e);
					}
			}
		});
	}

	//@author A0112828H
	// Help tab setup
	private void createHelpTab() {
		TabItem item = new TabItem(tabFolder, SWT.NONE);
		item.setText(TAB_HELP_NAME);
		setupHelpWindow();
		item.setControl(helpWindow);
	}

	private void setupHelpWindow() {
		GridLayout layout = new GridLayout(TAB_GRID_COLUMNS_NUM, GRID_COLUMNS_EQUAL_SIZE);
		helpWindow.setLayout(layout);
		helpWindow.setBounds(shell.getBounds());

		createTextFieldsForHelp();
	}

	private void createTextFieldsForHelp() {
		Text helpOutput = new Text(helpWindow, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		helpOutput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, HELP_FIT_HORIZONTAL, HELP_FIT_VERTICAL, 
				HELP_COLUMNS_SPAN, HELP_ROWS_SPAN));
		helpOutput.setText(INFO_HELP);
		helpOutput.setEditable(false);
	}

	// Browser tab setup
	private void createBrowserTab(String url) {
		browserTab = new TabItem(tabFolder, SWT.NONE);
		browserTab.setText(TAB_BROWSER_NAME);
		setupBrowserWindow(url);
		browserTab.setControl(browserWindow);
		tabFolder.setSelection(browserTab);
	}

	private void setupBrowserWindow(String url) {
		GridLayout layout = new GridLayout(BROWSER_GRID_COLUMNS_NUM, GRID_COLUMNS_EQUAL_SIZE);
		browserWindow.setLayout(layout);
		browserWindow.setBounds(shell.getBounds());

		createTextFieldsForBrowser();
		setupBrowser();
		addInputListenerForBrowser();

		browser.setUrl(url);
	}

	private void createTextFieldsForBrowser() {
		new Label(browserWindow, SWT.NONE).setText(INSTRUCTIONS_BROWSER);
	}

	private void setupBrowser() {
		browser = new Browser(browserWindow, SWT.FILL | SWT.BORDER& (~SWT.RESIZE));
		GridData browserGridData = new GridData(SWT.FILL, SWT.FILL, BROWSER_FIT_HORIZONTAL, BROWSER_FIT_VERTICAL, 
				BROWSER_COLUMNS_SPAN, BROWSER_ROWS_SPAN);
		browserGridData.widthHint = BROWSER_PREFERRED_WIDTH;
		browserGridData.heightHint = BROWSER_PREFERRED_HEIGHT;
		browser.setLayoutData(browserGridData);
		browser.setUrl("http://www.google.com/");
	}

	/**
	 * Adds a listener to check if the web page title changes to
	 * a Success string, then parses and sets the authorisation code.
	 */
	private void addInputListenerForBrowser() {
		browser.addTitleListener(new TitleListener() {
			@Override
			public void changed(TitleEvent event) {
				logger.log(Level.INFO, "Checking title "+event.title);
				if(event.title.contains("Success")) {
					logger.log(Level.INFO, "Success "+event.title);
					setCode(event.title.replace("Success code=", ""));
					closeBrowserTab();
				}
			}
		});
	}

	// Closes browser tab and resets focus to main tab.
	private void closeBrowserTab() {
		tabFolder.setSelection(mainTab);
		tabFolder.getItem(TAB_BROWSER_INDEX).dispose();
	}

	/**
	 * Attempts to get authorisation code from user by creating
	 * browser tab.
	 * @param url
	 */
	public void getCodeFromUser(String url) {
		logger.log(Level.INFO, "Creating browser...");
		code = null;
		createBrowserTab(url);
	}

	/**
	 * Sets the Google authorisation code received and 
	 * notifies observers.
	 * @param text
	 */
	private void setCode(String text) {
		code = text;
		setChanged();
		notifyObservers(code);
	}

	//@author A0105753J
	/**
	 * Opens the shell and runs until the shell is closed.
	 * Disposes of the display.
	 */
	private void runUntilWindowClosed() {
		shell.open();
		while (!shell.isDisposed()) {
			if (!Global.syncing && !input.getEditable()) {
				if (tabFolder.getItemCount() == 3) { // Tab folder has 3 tabs -> browser tab is open
					closeBrowserTab();
				}
				// Accept user input when not syncing
				input.setEditable(true);
				updateDisplay();
			} else if (Global.syncing && input.getEditable()) {
				// Do not accept user input when syncing
				input.setEditable(false);
				updateDisplay();
			}
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		disposeElements();
	}

	private void disposeElements() {
		display.dispose();
	}

	//@author A0112828H
	/** 
	 * Updates the display by clearing table items and displaying the
	 * latest tasks for display from the Controller.
	 */
	private void updateDisplay() {
		clearTableItems();
		displayTasks(TaskCommander.controller.getDisplayedTasks());
	}

	/** 
	 * Updates the display by clearing table items and displaying the
	 * latest tasks for display from the Controller plus a given String 
	 * in the output field.
	 * @param  fb     Feedback for user
	 */
	private void updateDisplay(String fb) {
		clearTableItems();
		displayMessage(fb);
		displayTasks(TaskCommander.controller.getDisplayedTasks());
	}

	/**
	 * Creates table rows and arranges data for display from a given
	 * arraylist of TaskCommander Task objects. Displays the tasks
	 * differently based on their type.
	 * @param tasks   Arraylist of tasks to display
	 */
	public void displayTasks(ArrayList<Task> tasks) {
		int index = 1;
		String lastDate = null;
		for (Task task : tasks) { 
			switch(task.getType()) {
			case FLOATING:
				FloatingTask ft = (FloatingTask) task;
				createRowFromTask(index, ft);
				break;
			case DEADLINE:
				DeadlineTask dt = (DeadlineTask) task;
				if (isNewDay(dt, lastDate)) {
					lastDate = getDisplayDate(dt.getDate());
					createDateRow(lastDate);
				}
				createRowFromTask(index, dt);
				break;
			case TIMED:
				TimedTask tt = (TimedTask) task;
				if (isNewDay(tt, lastDate)) {
					lastDate = getDisplayDate(tt.getDate());
					createDateRow(lastDate);
				}
				createRowFromTask(index, tt);
				break;
			}
			index++;
		}
		packUI();
	}

	// Methods to create a table row from different types of tasks
	private void createRowFromTask(int index, FloatingTask task) {
		TableItem item = new TableItem(table, TABLE_STYLE);
		item.setText(new String[] {Integer.toString(index),
				" ", 
				task.getName(),
				getDoneMessage(task)});
		setColorsForTableItem(item, getDoneColor(task));
	}

	private void createRowFromTask(int index, DeadlineTask task) {
		TableItem item = new TableItem(table, TABLE_STYLE);
		item.setText(new String[] {Integer.toString(index),
				getDisplayDate(task), 
				task.getName(),
				getDoneMessage(task)});
		setColorsForTableItem(item, getDoneColor(task));
	}

	private void createRowFromTask(int index, TimedTask task) {
		TableItem item = new TableItem(table, TABLE_STYLE);
		item.setText(new String[] { Integer.toString(index),
				getDisplayDate(task), 
				task.getName(),
				getDoneMessage(task)});
		setColorsForTableItem(item, getDoneColor(task));
	}

	private void createDateRow(String date) {
		new TableItem(table, TABLE_STYLE);
	}

	// Helper methods that return formatted Strings for display when given a task
	/**
	 * Returns the display date for DeadlineTasks.
	 * The format is: Date1 Time
	 * @param task
	 * @return      String date to display.
	 */
	private String getDisplayDate(DeadlineTask task) {
		return Global.dayFormat.format(task.getEndDate()) + " by " + Global.timeFormat.format(task.getEndDate());
	}

	/**
	 * Returns the display date for TimedTasks.
	 * If the start and end days are different, the format is: Date1 Time - Date2 Time
	 * If the start and end days are the same, the format is: Date1 Time - Time
	 * @param task
	 * @return      String date to display.
	 */
	private String getDisplayDate(TimedTask task) {
		if (hasDifferentDay(task.getStartDate(), task.getEndDate())) {
			return Global.dayFormat.format(task.getStartDate()) + " " + Global.timeFormat.format(task.getStartDate())+ " - " + 
					Global.dayFormat.format(task.getEndDate()) + " " + Global.timeFormat.format(task.getEndDate());
		} else {
			return Global.dayFormat.format(task.getStartDate()) + " " + Global.timeFormat.format(task.getStartDate())+ " - " + 
					Global.timeFormat.format(task.getEndDate());
		}
	}

	private String getDisplayDate(Date date) {
		return "["+Global.dayFormat.format(date)+"]";
	}

	private String getDoneMessage(Task task) {
		if (task.isDone()) {
			return Global.PARAMETER_DONE;
		} else {
			return Global.PARAMETER_OPEN;
		}
	}

	private Color getDoneColor(Task task) {
		if (task.isDone()) {
			return COLOR_DONE;
		} else {
			return COLOR_NOT_DONE;
		}
	}

	/** Checks if the given task has a different day from the
	 *  last date string, by converting the task's date into
	 *  a display date string.
	 * @param task
	 * @param lastDate
	 * @return          If the given task has a later day.
	 */
	private boolean isNewDay(DatedTask task, String lastDate) {
		if (lastDate != null) {
			return !(getDisplayDate(task.getDate()).equals(lastDate));
		} else {
			return true;
		}
	}

	/** Checks if the given dates have different days.
	 * @param date1
	 * @param date2
	 * @return          If the given dates have different days.
	 */
	private boolean hasDifferentDay(Date date1, Date date2) {
		return !Global.dayFormat.format(date1).equals(Global.dayFormat.format(date2));
	}

	private void setColorsForTableItem(TableItem item, Color doneColor) {
		item.setForeground(0, COLOR_COL_FIRST);
		item.setForeground(1, COLOR_COL_SECOND);
		item.setForeground(2, COLOR_COL_THIRD);
		item.setForeground(3, doneColor);
	}

	// Calls pack() for UI elements.
	private void packUI() {
		for (TableColumn t : table.getColumns()) {
			t.pack();
		}
		shell.pack();
	}

	private void clearTableItems() {
		table.removeAll();
	}

	private void clearInput() {
		input.setText("");
	}

	private void displayMessage(String s) {
		output.setText(s);
		output.setForeground(darkBlue);
	}

	/**
	 * Updates the sync progress output with the given String.
	 * 
	 * For use with the sync method in the Google Integration component.
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 instanceof String) {
			final String m = (String) arg1;
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					output.setText(m);
				}
			});
		}
	}
}
