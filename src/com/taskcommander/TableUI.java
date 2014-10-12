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

	private static final int GRID_COLUMNS_NUM = 1;
	private static final boolean GRID_COLUMNS_EQUAL_SIZE = false;

	private static final boolean INPUT_FIT_HORIZONTAL = true;
	private static final boolean INPUT_FIT_VERTICAL = false;
	private static final int INPUT_COLUMNS_NUM = 1;
	private static final int INPUT_ROWS_NUM = 1;
	private static final int INPUT_PREFERRED_WIDTH = 500;
	
	private static final boolean OUTPUT_FIT_HORIZONTAL = true;
	private static final boolean OUTPUT_FIT_VERTICAL = false;
	private static final int OUTPUT_COLUMNS_NUM = 1;
	private static final int OUTPUT_ROWS_NUM = 2;
	private static final int OUTPUT_PREFERRED_WIDTH = 500;
	private static final int OUTPUT_PREFERRED_HEIGHT = 50;

	private static final int TABLE_STYLE = SWT.NONE;
	private static final boolean TABLE_FIT_HORIZONTAL = true;
	private static final boolean TABLE_FIT_VERTICAL = true;
	private static final int TABLE_PREFERRED_WIDTH = 500;
	private static final int TABLE_PREFERRED_HEIGHT = 200;

	private final Display display = Display.getDefault();
	private final Shell shell = new Shell(display);
	private final Table table = new Table(shell, SWT.BORDER | SWT.MULTI);

	private final TableColumn tableColumn1 = new TableColumn(table, TABLE_STYLE);
	private final TableColumn tableColumn2 = new TableColumn(table, TABLE_STYLE);
	private final TableColumn tableColumn3 = new TableColumn(table, TABLE_STYLE);

	private final Color red = display.getSystemColor(SWT.COLOR_RED);
	private final Color gray = display.getSystemColor(SWT.COLOR_GRAY);
	private final Color blue = display.getSystemColor(SWT.COLOR_BLUE);
	private final Color cyan = display.getSystemColor(SWT.COLOR_CYAN);

	private Text input;
	private Text output;

	public TableUI() {

	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		shell.setLayout(new GridLayout(GRID_COLUMNS_NUM, GRID_COLUMNS_EQUAL_SIZE));
		shell.setText(Global.APPLICATION_NAME);
		shell.setMinimumSize(SHELL_MIN_WIDTH, SHELL_MIN_HEIGHT);
		new Label(shell, SWT.NONE).setText("Enter command: ");
		input = new Text(shell, SWT.BORDER);
		output = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);

		GridData inputGridData = new GridData(SWT.FILL, SWT.CENTER, INPUT_FIT_HORIZONTAL, INPUT_FIT_VERTICAL, 
				INPUT_COLUMNS_NUM, INPUT_ROWS_NUM);
		inputGridData.widthHint = INPUT_PREFERRED_WIDTH;
		input.setLayoutData(inputGridData);
		
		GridData outputGridData = new GridData(SWT.FILL, SWT.CENTER, OUTPUT_FIT_HORIZONTAL, OUTPUT_FIT_VERTICAL, 
				OUTPUT_COLUMNS_NUM, OUTPUT_ROWS_NUM);
		outputGridData.widthHint = OUTPUT_PREFERRED_WIDTH;
		outputGridData.heightHint = OUTPUT_PREFERRED_HEIGHT;
		output.setLayoutData(outputGridData);
		
		output.setMessage(Global.MESSAGE_WELCOME);

		GridData tableGridData = new GridData(SWT.FILL, SWT.FILL, TABLE_FIT_HORIZONTAL, TABLE_FIT_VERTICAL);
		tableGridData.widthHint = TABLE_PREFERRED_WIDTH;
		tableGridData.heightHint = TABLE_PREFERRED_HEIGHT;
		table.setLayoutData(tableGridData);

		//display welcome tasks
		ArrayList<Task> tasks = TaskCommander.controller.executeCommand("display").getCommandRelatedTasks();
		displayTasks(tasks);

		input.addListener(SWT.Traverse, new Listener(){
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				if(event.detail == SWT.TRAVERSE_RETURN)
					try {
						clearTableItems();
						String command = input.getText();
						Feedback fb = TaskCommander.controller.executeCommand(command);
						if(fb.wasSuccesfullyExecuted()){
							ArrayList<Task> tasks = getTasks(fb);
							displayTasks(tasks);
						} else{
							TableItem item = new TableItem(table, TABLE_STYLE);
							item.setText(fb.getErrorMessage());
							item.setForeground(red);
						}
						input.setText("");
					}catch (Exception e1) {
						e1.printStackTrace();
					}
			}
		});

		runUntilWindowClosed();
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
				item.setText(new String[] { " ", 
						task.getName(),
						done });
				item.setForeground(0, blue);
				item.setForeground(1, cyan);
				item.setForeground(2, doneColor);
				break;
			case DEADLINE:
				DeadlineTask deadlineTask = (DeadlineTask) task;
				item.setText(new String[] { "[by "+ Global.dayFormat.format(deadlineTask.getEndDate())+ " "+ Global.timeFormat.format(deadlineTask.getEndDate()) + "]", 
						task.getName(),
						done });
				item.setForeground(0, blue);
				item.setForeground(1, cyan);
				item.setForeground(2, doneColor);
				break;
			case TIMED:
				TimedTask timedTask = (TimedTask) task;
				item.setText(new String[] { "["+ Global.dayFormat.format(timedTask.getStartDate())+ " "+ Global.timeFormat.format(timedTask.getStartDate())+ "-"+ Global.timeFormat.format(timedTask.getEndDate()) + "]", 
						task.getName(),
						done });
				item.setForeground(0, blue);
				item.setForeground(1, cyan);
				item.setForeground(2, doneColor);
				break;
			}
		}

		packUI();
	}

	/**
	 * Calls pack() for UI elements.
	 */
	private void packUI() {
		tableColumn1.pack();
		tableColumn2.pack();
		tableColumn3.pack();
		shell.pack();
	}

	private void clearTableItems() {
		table.removeAll();
	}

}
