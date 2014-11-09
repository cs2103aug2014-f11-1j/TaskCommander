package com.taskcommander;
import com.google.gson.annotations.SerializedName;
import com.google.api.client.util.DateTime;

//@author A0112828H
/**
 * A basic task object. Has a name. May also hold a Google API ID.
 * Related Google API: Tasks
 */

public abstract class Task {
    @SerializedName("taskType")
    // For use with GSON
    private TaskType _taskType;
    private String _name;
    private String _id;
    private boolean _done;
    private boolean _edited;
    private DateTime _updated;

    /**
     * Possible task types
     */
    public enum TaskType {
        @SerializedName("0")
        FLOATING("FloatingTask"), @SerializedName("1")
        TIMED("TimedTask"), @SerializedName("2")
        DEADLINE("DeadlineTask");

        String name;

        private TaskType(String s) {
            name = s;
        }

        public String toString() {
            return name;
        }
    }

    /**
     * Creates a new Task with given name. Throws IllegalArgumentException if
     * name is not given.
     * 
     * @param name
     * @param type
     * @return Task instance
     */
    public Task(String name, TaskType taskType) {
        if (name != null) {
            _name = name;
            _taskType = taskType;
            _done = false;
            _edited = true;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public TaskType getType() {
        return _taskType;
    }

    public String getName() {
        return _name;
    }

    public String getId() {
        return _id;
    }

    public boolean isDone() {
        return _done;
    }

    public boolean isEdited() {
        return _edited;
    }

    public DateTime getUpdated() {
        return _updated;
    }

    /**
     * Returns true if task has a google id and has not been edited since last
     * sync
     * 
     * @return If task is synced.
     */
    public boolean isSynced() {
        return _id != null && !_edited;
    }

    public void setType(TaskType taskType) {
        _taskType = taskType;
    }

    public void setName(String name) {
        _name = name;
    }

    public void setId(String id) {
        _id = id;
    }

    public void setDone(boolean done) {
        _done = done;
    }

    public void setEdited(boolean edited) {
        this._edited = edited;
    }

    public void setUpdated(DateTime updated) {
        _updated = updated;
    }

    public void markDone() {
        _done = true;
    }

    public void markOpen() {
        _done = false;
    }

    public void markSynced() {
        _edited = false;
    }

    //@author A0128620M
    /**
     * Checks if the given object is equal.
     * 
     * @param other Object
     * @return true if so, false if not
     */
    @Override
    public boolean equals(Object otherObject) {
        if (otherObject == null) {
            return false;
        }
        
        if (!(otherObject instanceof Task)) {
            return false;
        }
        switch (this.getType()) {
          case FLOATING:
              return ((FloatingTask) this).equals(otherObject);
          case DEADLINE:
              return ((DeadlineTask) this).equals(otherObject);
          case TIMED:
              return ((TimedTask) this).equals(otherObject);
          default:
              return false;
        }
    }
}