package com.taskcommander;

import java.lang.reflect.Type;

import com.google.gson.*;

/**
 * Adapter class for serializing and deserializing JSON objects into
 * Task objects using GSON.
 * 
 * Code adapted from: http://ovaraksin.blogspot.com.es/2011/05/json-with-gson-and-abstract-classes.html
 */

public class TaskAdapter implements JsonSerializer<Task>, JsonDeserializer<Task> {
    private static final String PACKAGE_NAME = "com.taskcommander.";

	@Override
    public JsonElement serialize(Task src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
        result.add("properties", context.serialize(src, src.getClass()));
 
        return result;
    }
 
    @Override
    public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");
 
        try {
            return context.deserialize(element, Class.forName(PACKAGE_NAME + type));
        } catch (ClassNotFoundException cnfe) {
            throw new JsonParseException("Unknown element type: " + type, cnfe);
        }
    }
}
