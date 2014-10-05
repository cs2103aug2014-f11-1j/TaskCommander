package com.taskcommander;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * This class stores data permanently as JSON in a local file on the computer.
 */

//@author A0112828H
public class Storage {
	
	private static String _fileName = "tasks.json";
	private static Gson gson;
	
	/**
	 * Returns a Storage object.
	 */
	public Storage() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Task.class, new TaskAdapter());
		gson = gsonBuilder.create();
	}
	
	/**
	 * Returns the contents of the file as an arraylist of Tasks.
	 */
	public ArrayList<Task> readFromFile(){
		ArrayList<Task> tasks = new ArrayList<Task>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(_fileName)));
			String line;
			while ((line = reader.readLine()) != null) {
				Task task = gson.fromJson(line, Task.class);
				System.out.println(task.getName());
				tasks.add(task);
			}
			reader.close();
			return tasks;
		} catch (IOException e) {
			System.out.println(Global.MESSAGE_EXCEPTION_IO);
		} 
		return null;
	}
	
	/**
	 * Writes the content of the data array into storage.
	 */
	public void writeToFile(ArrayList<Task> tasks){
		BufferedWriter bw;
		try {
		  bw = new BufferedWriter(new FileWriter(new File(_fileName)));
		  
		  for (Task t : tasks) {
			  bw.write(gson.toJson(t));
			  bw.newLine();
		  }
		  bw.close();
		} catch (Exception e) {
			System.out.println(Global.MESSAGE_EXCEPTION_IO);
		}
	}
}