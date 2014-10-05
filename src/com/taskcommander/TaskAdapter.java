package com.taskcommander;

import java.lang.reflect.Type;

import com.google.gson.*;
import com.taskcommander.Task.TaskType;

//@author A0112828H
/**
 * Adapter class for serializing and deserializing JSON objects into
 * Task objects using GSON. 
 * 
 * Creates Task subclass objects based on the enum TaskType's string values.
 * 
 * Code adapted from: http://ovaraksin.blogspot.com.es/2011/05/json-with-gson-and-abstract-classes.html
 */

public class TaskAdapter implements JsonSerializer<Task>, JsonDeserializer<Task> {
	private static final String PACKAGE_NAME = "com.taskcommander.";

	@Override
	public JsonElement serialize(Task src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		//result.add("type", new JsonPrimitive(src.getType().toString()));
		switch (src.getType()) {
		case FLOATING:
			result.add("properties", context.serialize(src, FloatingTask.class));
			break;
		case TIMED:
			result.add("properties", context.serialize(src, TimedTask.class));
			break;
		case DEADLINE:
			result.add("properties", context.serialize(src, DeadlineTask.class));
			break;
		}
		System.out.println("ser" + result.toString());
		return result;
	}

	@Override
	public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		System.out.println(jsonObject.toString());
		int type = Integer.parseInt(jsonObject.get("taskType").getAsString());
		JsonElement element = jsonObject.get("properties");
		try {
			switch (type) {
			case 0:
				return context.deserialize(element, FloatingTask.class);
			case 1:
				return context.deserialize(element, TimedTask.class);
			case 2:
				return context.deserialize(element, DeadlineTask.class);
			default:
				return null;
			}
		} finally {
			
		}
	}
}
