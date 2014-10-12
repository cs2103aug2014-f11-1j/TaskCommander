package com.taskcommander;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.TraverseEvent;

public class TableUI {

	private static final int SHELL_MIN_HEIGHT = 500;
	private static final int SHELL_MIN_WIDTH = 200;

	private static final int GRID_COLUMNS_SPAN = 2;
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
	private static final int OUTPUT_PREFERRED_HEIGHT = 50;

	private static final int TABLE_STYLE = SWT.NONE;
	private static final boolean TABLE_FIT_HORIZONTAL = true;
	private static final boolean TABLE_FIT_VERTICAL = true;
	private static final int TABLE_COLUMNS_SPAN = 2;
	private static final int TABLE_ROWS_SPAN = 1;
	private static final int TABLE_PREFERRED_WIDTH = 500;
	private static final int TABLE_PREFERRED_HEIGHT = 100;
	private static final int TABLE_COLUMNS_NUM = 4;
	private static final String[] TABLE_COLUMNS_NAMES = {"No.", "Date", "Task", "Status"};

	private final Display display = Display.getDefault();
	private final Shell shell = new Shell(display);
	private final Table table = new Table(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);

	private final Color red = display.getSystemColor(SWT.COLOR_RED);
	private final Color gray = display.getSystemColor(SWT.COLOR_GRAY);
	private final Color blue = display.getSystemColor(SWT.COLOR_BLUE);
	private final Color black = display.getSystemColor(SWT.COLOR_BLACK);
	
	private final Color COLOR_COL_FIRST = gray;
	private final Color COLOR_COL_SECOND = blue;
	private final Color COLOR_COL_THIRD = black;

	private Text input;
	private Text output;

	public TableUI() {

	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		shell.setLayout(new GridLayout(GRID_COLUMNS_SPAN, GRID_COLUMNS_EQUAL_SIZE));
		shell.setText(Global.APPLICATION_NAME);
		shell.setMinimumSize(SHELL_MIN_WIDTH, SHELL_MIN_HEIGHT);

		output = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		new Label(shell, SWT.NONE).setText("Enter command: ");
		input = new Text(shell, SWT.BORDER);


		GridData inputGridData = new GridData(SWT.FILL, SWT.CENTER, INPUT_FIT_HORIZONTAL, INPUT_FIT_VERTICAL, 
				INPUT_COLUMNS_SPAN, INPUT_ROWS_SPAN);
		inputGridData.widthHint = INPUT_PREFERRED_WIDTH;
		input.setLayoutData(inputGridData);

		GridData outputGridData = new GridData(SWT.FILL, SWT.CENTER, OUTPUT_FIT_HORIZONTAL, OUTPUT_FIT_VERTICAL, 
				OUTPUT_COLUMNS_SPAN, OUTPUT_ROWS_SPAN);
		outputGridData.widthHint = OUTPUT_PREFERRED_WIDTH;
		outputGridData.heightHint = OUTPUT_PREFERRED_HEIGHT;
		output.setLayoutData(outputGridData);
		output.setText(Global.MESSAGE_WELCOME);
		output.setEditable(false);

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

		displayTasksUponOpening();

		input.addListener(SWT.Traverse, new Listener(){
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				if(event.detail == SWT.TRAVERSE_RETURN)
					try {
						clearTableItems();
						String command = input.getText();
						Feedback fb = TaskCommander.controller.executeCommand(command);
						// Uncomment this line after Controller implements new parts
						// displayFeedback(fb);
						
						// Delete this part after Controller implements new parts
						// ---- START DELETE ----- 
						if(fb.wasSuccesfullyExecuted()){
							ArrayList<Task> tasks = getTasks(fb);
							displayTasks(tasks);
							// Add this line after Controller implements new parts
							// output.setText(fb.getMessage());
							// output.setForeground(blue);
						} else{
							output.setText(fb.getErrorMessage());
							output.setForeground(red);
						}
						// ---- END DELETE ----- 
						input.setText("");
					}catch (Exception e1) {
						output.setText(e1.getMessage());
						output.setForeground(red);
					}
			}
		});

		runUntilWindowClosed();
	}

	private void displayTasksUponOpening() {
		// Switch to this line after Controller implements new parts
		// ArrayList<Task> tasks = TaskCommander.controller.getTasks().getCommandRelatedTasks();
		ArrayList<Task> tasks = TaskCommander.controller.executeCommand("display").getCommandRelatedTasks();
		displayTasks(tasks);
	}
	
	private void displayFeedback(Feedback fb) {
		if (fb.wasSuccesfullyExecuted()) {
			displayTasks(fb.getCommandRelatedTasks());
			// Uncomment this line after Controller implements new parts
			//output.setText(fb.getMessage());
			//output.setForeground(blue);
		} else {
			output.setText(fb.getErrorMessage());
			output.setForeground(red);
		}
	}

	public ArrayList<Task> getTasks(Feedback fb) {
		ArrayList<Task> tasks = new ArrayList<Task>();
		switch(fb.getCommandType()){
		case DISPLAY: 
			tasks = fb.getCommandRelatedTasks();
			break;
		case ADD:case DELETE: case UPDATE:case DONE: case OPEN:
			tasks.add(fb.getCommandRelatedTask());
		case HELP:
			// Desired Output has to be discussed, but low priority anyway
			break;

		case SYNC:
			// Desired Output has to be discussed
			break;

		case EXIT:
			break;

		case INVALID:
			break;

		default:
			break;
		}
		return tasks;
	}

	public void displayTasks(ArrayList<Task> tasks) {
		int index = 1;
		for (Task task : tasks) { 
			TableItem item = new TableItem(table, TABLE_STYLE);
			String done;
			Color doneColor;
			if (task.isDone()) {
				done = "done";
				doneColor = gray;
			} else {
				done = "not done";
				doneColor = red;
			}
			switch(task.getType()) {
			case FLOATING:
				item.setText(new String[] {Integer.toString(index),
						" ", 
						task.getName(),
						done });
				break;
			case DEADLINE:
				DeadlineTask deadlineTask = (DeadlineTask) task;
				item.setText(new String[] {Integer.toString(index),
						"[by "+ Global.dayFormat.format(deadlineTask.getEndDate())+ " "+ Global.timeFormat.format(deadlineTask.getEndDate()) + "]", 
						task.getName(),
						done });
				break;
			case TIMED:
				TimedTask timedTask = (TimedTask) task;
				item.setText(new String[] { Integer.toString(index),
						"["+ Global.dayFormat.format(timedTask.getStartDate())+ " "+ Global.timeFormat.format(timedTask.getStartDate())+ "-"+ Global.timeFormat.format(timedTask.getEndDate()) + "]", 
						task.getName(),
						done });
				break;
			}
			setColorsForTableItem(item, doneColor);
			index++;
		}
		packUI();
	}
	
	/**
	 * Opens the shell and runs until the shell is closed.
	 * Disposes of the display.
	 */
	private void runUntilWindowClosed() {
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	private void setColorsForTableItem(TableItem item, Color doneColor) {
		item.setForeground(0, COLOR_COL_FIRST);
		item.setForeground(1, COLOR_COL_SECOND);
		item.setForeground(2, COLOR_COL_THIRD);
		item.setForeground(3, doneColor);
	}

	/**
	 * Calls pack() for UI elements.
	 */
	private void packUI() {
		for (TableColumn t : table.getColumns()) {
			t.pack();
		}
		shell.pack();
	}

	private void clearTableItems() {
		table.removeAll();
	}

}
