package com.taskcommander;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class stores the data permanently on the computer.
 * 
 * @author Andreas Christian Mayr
 */


public class Storage {
	
	// This string stores the name of the file being used
	private static String fileName;
	
	public Storage(){
		setFileName("tasks.txt");
	}
	
	/**
	 * Writes the content of the given data array into the file.
	 */
	public void getData(Data data){

		try {
			BufferedWriter myBufferedWriter = new BufferedWriter(
					new FileWriter(new File(fileName)));
			
			for(Task task: data) {
				myBufferedWriter.write(task.getName());
				myBufferedWriter.newLine();
			}
			
			myBufferedWriter.close();

		} catch (IOException e) {
			System.err.println(Global.MESSAGE_FILE_COULD_NOT_BE_WRITTEN);
		}
	}

	public static String getFileName() {
		return fileName;
	}

	public static void setFileName(String fileName) {
		Storage.fileName = fileName;
	}
	
}
