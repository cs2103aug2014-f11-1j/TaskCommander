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

public class TableUI {
	public TableUI() {
		
	}
	
	public void open() {
		final Display display = Display.getDefault();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(3, false));
		shell.setText("StackOverflow");

		final Table table = new Table(shell, SWT.BORDER | SWT.MULTI);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Color red = display.getSystemColor(SWT.COLOR_RED);
		Color gray = display.getSystemColor(SWT.COLOR_GRAY);
		Color blue = display.getSystemColor(SWT.COLOR_BLUE);

		TableColumn column1 = new TableColumn(table, SWT.NONE);
		TableColumn column2 = new TableColumn(table, SWT.NONE);
		TableColumn column3 = new TableColumn(table, SWT.NONE);

		ArrayList<Task> tasks = TaskCommander.data.getAllTasks();
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
				item.setForeground(1, gray);
				item.setForeground(2, doneColor);
				break;
			case DEADLINE:
				DeadlineTask deadlineTask = (DeadlineTask) task;
				item.setText(new String[] { "[by "+ Global.dayFormat.format(deadlineTask.getEndDate())+ " "+ Global.timeFormat.format(deadlineTask.getEndDate()) + "]", 
											task.getName(),
											done });
				item.setForeground(0, blue);
				item.setForeground(1, gray);
				item.setForeground(2, doneColor);
				break;
			case TIMED:
				TimedTask timedTask = (TimedTask) task;
				item.setText(new String[] { "["+ Global.dayFormat.format(timedTask.getStartDate())+ " "+ Global.timeFormat.format(timedTask.getStartDate())+ "-"+ Global.timeFormat.format(timedTask.getEndDate()) + "]", 
											task.getName(),
											done });
				item.setForeground(0, blue);
				item.setForeground(1, gray);
				item.setForeground(2, doneColor);
				break;
			}
		}

		column1.pack();
		column2.pack();
		column3.pack();

		shell.pack();
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

}
