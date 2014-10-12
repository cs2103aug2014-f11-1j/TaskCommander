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
	
	private final Display display = Display.getDefault();
	private Text input;
	
	private static final int GRID_COLUMNS_NUM = 3;
	private static final boolean GRID_COLUMNS_EQUAL_SIZE = false;
	
	private static final boolean INPUT_FIT_HORIZONTAL = true;
	private static final boolean INPUT_FIT_VERTICAL = false;
	private static final int INPUT_COLUMNS_NUM = 1;
	private static final int INPUT_ROWS_NUM = 1;
	private static final int INPUT_PREFERRED_WIDTH = 500;
	
	private static final boolean TABLE_FIT_HORIZONTAL = true;
	private static final boolean TABLE_FIT_VERTICAL = true;
	private static final int TABLE_PREFERRED_WIDTH = 500;
	private static final int TABLE_PREFERRED_HEIGHT = 200;
	
	public TableUI() {

	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(GRID_COLUMNS_NUM, GRID_COLUMNS_EQUAL_SIZE));
		shell.setText(Global.APPLICATION_NAME);
		
		final Color red = display.getSystemColor(SWT.COLOR_RED);
		final Color gray = display.getSystemColor(SWT.COLOR_GRAY);
		final Color blue = display.getSystemColor(SWT.COLOR_BLUE);
		final Color cyan = display.getSystemColor(SWT.COLOR_CYAN);

		input = new Text(shell, SWT.BORDER);

		GridData inputGridData = new GridData(SWT.FILL, SWT.CENTER, INPUT_FIT_HORIZONTAL, INPUT_FIT_VERTICAL, 
												INPUT_COLUMNS_NUM, INPUT_ROWS_NUM);
		inputGridData.widthHint = INPUT_PREFERRED_WIDTH;
		input.setLayoutData(inputGridData);
		//new Label(shell, SWT.NONE);
		//new Label(shell, SWT.NONE);

		final Table table = new Table(shell, SWT.BORDER | SWT.MULTI);
		GridData tableGridData = new GridData(SWT.FILL, SWT.FILL, TABLE_FIT_HORIZONTAL, TABLE_FIT_VERTICAL);
		tableGridData.widthHint = TABLE_PREFERRED_WIDTH;
		tableGridData.heightHint = TABLE_PREFERRED_HEIGHT;
		table.setLayoutData(tableGridData);

		final TableColumn column1 = new TableColumn(table, SWT.NONE);
		final TableColumn column2 = new TableColumn(table, SWT.NONE);
		final TableColumn column3 = new TableColumn(table, SWT.NONE);
		
		//display welcome tasks
		ArrayList<Task> tasks = TaskCommander.controller.executeCommand("display").getCommandRelatedTasks();
		displayTasks(shell, table, red, gray, blue, cyan, column1, column2, column3, tasks);

		input.addListener(SWT.Traverse, new Listener(){

			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				if(event.detail == SWT.TRAVERSE_RETURN)
					try {
						table.removeAll();
						String command = input.getText();
						Feedback fb = TaskCommander.controller.executeCommand(command);
						if(fb.wasSuccesfullyExecuted()){
							ArrayList<Task> tasks = getTasks(fb);
							displayTasks(shell, table, red, gray, blue, cyan, column1, column2, column3,
									tasks);
						}
						else{
							TableItem item = new TableItem(table, SWT.NONE);
							item.setText(fb.getErrorMessage());
							item.setForeground(red);
						}
						input.setText("");
					}catch (Exception e1) {
						e1.printStackTrace();
					}
			}

		});
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
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
	public void displayTasks(final Shell shell, final Table table,
			final Color red, final Color gray, final Color blue, final Color cyan,
			final TableColumn column1, final TableColumn column2,
			final TableColumn column3, ArrayList<Task> tasks) {
		for (Task task : tasks)
		{ 
			TableItem item = new TableItem(table, SWT.NONE);
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

		column1.pack();
		column2.pack();
		column3.pack();

		shell.pack();
	}

}
