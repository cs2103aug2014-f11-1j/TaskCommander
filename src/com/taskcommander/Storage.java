package com.taskcommander;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class stores the data permanently in a local file on the computer.
 */

//@author Andreas Christian Mayr
public class Storage {
	
	private static String _fileName = "tasks.json";
	
	/**
	 * Returns a Storage object.
	 */
	public Storage(){
		
	}
	
	/**
	 * Reads the content of the file into the data array.
	 */
	public void readFromFile(){	// has to be updated for the use of different taskTypes
		
	}
	
	/**
	 * Writes the content of the data array into the given storage.
	 */
	public void writeToFile(ArrayList<Task> tasks){
		
	}
}