package com.taskcommander;
import java.util.ArrayList;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;

import com.taskcommander.Task.TaskType;

public class UI {

	protected static Shell shell;
	private static Text input;
	private static StyledText output;
	private static Display display = Display.getDefault();

	/**
	 * Open the window.
	 * @wbp.parser.entryPoint
	 */
	
	public static void open() {
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	/**
	 * Constructor
	 * @wbp.parser.entryPoint
	 */
	public UI() {
	}
	
	/**
	 * Create contents of the window.
	 */
	protected static void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText(Global.APPLICATION_NAME);
		input = new Text(shell, SWT.NONE);
		input.setBounds(0, 0, 434, 23);
		//This event triggered on enter key
		input.addListener(SWT.Traverse, new Listener()
	    {
			
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				if(event.detail == SWT.TRAVERSE_RETURN)
					try {
						output.setText("");
						String command = input.getText();
						
						/**
						 *  Code Suggestion regarding the usage of the new return class Feedback;
						 *  Just a draft until the real, more sophisticated, multi-coloured code is written by you, Chenwei ;-)
						 *  @author A0128620M
						 */
						Feedback feedback = TaskCommander.controller.executeCommand(command);
					
						String text = "";
						Task task = null;
						String taskName = "";
						if (feedback.wasSuccesfullyExecuted()) {
							Global.CommandType commandtype = feedback.getCommandType();
							switch (commandtype) {
							case ADD:
								// Output example: added [3 Oct '14 18:00-19:00] "Call the boss"
								task = feedback.getCommandRelatedTask();

								taskName = task.getName();
						
								switch (task.getType()) {
								case TIMED:
									TimedTask timedTask = (TimedTask) task;
									text = String.format(Global.MESSAGE_ADDED,"["+ Global.dayFormat.format(timedTask.getStartDate())+ " "+ Global.timeFormat.format(timedTask.getStartDate())+ "-"+ Global.timeFormat.format(timedTask.getEndDate()) + "]"+ " \"" + taskName + "\"");
									break;
								case DEADLINE:
									DeadlineTask deadlineTask = (DeadlineTask) task;
									text = String.format(Global.MESSAGE_ADDED,"[by "+ Global.dayFormat.format(deadlineTask.getEndDate())+ " "+ Global.timeFormat.format(deadlineTask.getEndDate()) + "]"+ " \"" + taskName + "\"");
									break;
								case FLOATING:
									text = String.format(Global.MESSAGE_ADDED,"\"" + taskName + "\"");
								}
								break;
								
							case UPDATE:
								// Desired Output example: updated [3 Oct '14 18:00-19:00] "Call the boss"
								task = feedback.getCommandRelatedTask();

								taskName = task.getName();

								switch (task.getType()) {
									case TIMED:
										TimedTask timedTask = (TimedTask) task;
										text = String.format(Global.MESSAGE_UPDATED,"["+ Global.dayFormat.format(timedTask.getStartDate())+ " "+ Global.timeFormat.format(timedTask.getStartDate())+ "-"+ Global.timeFormat.format(timedTask.getEndDate()) + "]"+ " \"" + taskName + "\"");
										break;
									case DEADLINE:
										DeadlineTask deadlineTask = (DeadlineTask) task;
										text = String.format(Global.MESSAGE_UPDATED,"[by "+ Global.dayFormat.format(deadlineTask.getEndDate())+ " "+ Global.timeFormat.format(deadlineTask.getEndDate()) + "]"+ " \"" + taskName + "\"");
										break;
									case FLOATING:
										text = String.format(Global.MESSAGE_UPDATED,"\"" + taskName + "\"");
								}
								break;
								
							case DELETE:
								// DesiredOutput example: deleted [3 Oct '14 18:00-19:00] "Call the boss"
								task = feedback.getCommandRelatedTask();

								taskName = task.getName();

								switch (task.getType()) {
									case TIMED:
										TimedTask timedTask = (TimedTask) task;
										text = String.format(Global.MESSAGE_DELETED,"["+ Global.dayFormat.format(timedTask.getStartDate())+ " "+ Global.timeFormat.format(timedTask.getStartDate())+ "-"+ Global.timeFormat.format(timedTask.getEndDate()) + "]"+ " \"" + taskName + "\"");
										break;
									case DEADLINE:
										DeadlineTask deadlineTask = (DeadlineTask) task;
										text = String.format(Global.MESSAGE_DELETED,"[by "+ Global.dayFormat.format(deadlineTask.getEndDate())+ " "+ Global.timeFormat.format(deadlineTask.getEndDate()) + "]"+ " \"" + taskName + "\"");
										break;
									case FLOATING:
										text = String.format(Global.MESSAGE_DELETED,"\"" + taskName + "\"");
								}
								break;
								
							case CLEAR:
								text = String.format(Global.MESSAGE_CLEARED);
								break;
								
							case DISPLAY:
								/* Desired Output example (not yet implemented!!)
								 * 
								 * [floatingTasks] ================================================
								 * 1. "adfsdfsgsxfgf"
								 * 2. "sgsxfgsfgsfgdfhdgfghdg"
								 * 3. "sdgsfgfdgdfgfdgd"
								 * 
								 * [Today: 3 Oct '14] ================================================
								 * 4. [11:00-12:00] 	"afadfsdfsdf"
								 * 5. [18:00-19:00] 	"asdfasdfasdfsadf"
								 * 6. [by 19:30   ] 	"asdfasdfsadf"
								 * 7. [20:00-22:00] 	"asdfsadfasdfsad"
								 * 
								 * [Tomorrow: 4 Oct '14] ================================================
								 * 8. [10:00-12:00] 	"fgjhgjkjh"
								 * 9. [18:00-18:30] 	"gjkgjghj"
								 * 10. [by 19:30   ] 	"fghdfjhf"
								 * 11. [20:00-22:00] 	"dfhfghjkjhkfhjk"
								 * 
								 * [5 Oct '14] ================================================
								 * 12. [10:00-12:00] 	"fgjhgjkjh"
								 * 13. [by 19:30   ] 	"fghdfjhf"
								 * 14. [20:00-22:00] 	"dfhfghjkjhkfhjk"
								 * 
								*/
								
								
								ArrayList<Task> tasks = feedback.getCommandRelatedTasks();
								
								/*
								int j = 0;
								for (int i = 0; i < tasks.size(); i++) {
									
									task = tasks.get(i);
									taskName = tasks.get(i).getName();
									
									if (tasks.get(i).getType()==TaskType.FLOATING) {
											text += (j+1)+". "+"\t"+"\t"+"\t"+"\t"+"\t"+"   "+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\""+ taskName + "\"" + "\n";	
											j++;
											break;
									}
								}
								*/

								for (int i = 0; i < tasks.size(); i++) {
									
									task = tasks.get(i);
									taskName = tasks.get(i).getName();
									
									switch (tasks.get(i).getType()) {
										case TIMED:
											TimedTask timedTask = (TimedTask) task;
											text += (i+1)+". "+"\t"+"["+ Global.dayFormat.format(timedTask.getStartDate())+ "  "+ Global.timeFormat.format(timedTask.getStartDate())+ "-"+ Global.timeFormat.format(timedTask.getEndDate()) + "]"+"\t"+ " \"" + taskName + "\"" + "\n";
											//j++;
											break;
										case DEADLINE:
											DeadlineTask deadlineTask = (DeadlineTask) task;
											text += (i+1)+". "+"\t"+"[by "+ Global.dayFormat.format(deadlineTask.getEndDate())+ "  "+ Global.timeFormat.format(deadlineTask.getEndDate()) + "]"+"\t"+"\t"+ " \"" + taskName + "\"" + "\n";
											//j++;
											break;
										case FLOATING:
											text += (i+1)+". "+"\t"+"\t"+"\t"+"\t"+"   "+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"\""+ taskName + "\"" + "\n";	
											break;
									}
								}
								break;
								
							case HELP:
								// Desired Output has to be discussed, but low priority anyway
								break;
								
							case SORT:
								// Desired Output has to be discussed
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
							
						} else {
							text = feedback.getErrorMessage();
						}
						// End of Code Suggestion	
						
						output.setText(text);
						StyleRange styleRange = new StyleRange();
						styleRange.start = 0;
						styleRange.length = text.length();
						styleRange.fontStyle = SWT.BOLD;
						styleRange.foreground = display.getSystemColor(SWT.COLOR_BLUE);
						output.setStyleRange(styleRange);
						input.setText("");
						TaskCommander.data.save();		// write new tasks in storage
					} catch (Exception e1) {
						e1.printStackTrace();
					}
			}
	    });
		
		output = new StyledText(shell, SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		output.setText(Global.MESSAGE_WELCOME);
		output.setBounds(0, 29, 434, 233);

		/*Button btnEnter = new Button(shell, SWT.NONE);
		// This one triggered on btn pressed.
		btnEnter.addSelectionListener(new SelectionAdapter() {
			TextBuddy tb = new TextBuddy();
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					tb.content = tb.readFileToList();
					output.setText("Welcome to TextBuddy."+tb.fileName+" is ready for use. ");
					String command = input.getText();
					output.setText(tb.executeCommand(command));
					tb.writeIntoFile();
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}
		});
		
		btnEnter.setBounds(272, 31, 65, 27);
		btnEnter.setText("enter");*/
	}
}
