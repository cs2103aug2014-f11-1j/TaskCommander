package com.taskcommander;

//@author A0112828H
/**
 * A floating task that has no deadline. Has a name. May also hold a Google API
 * ID. Related Google API: Tasks
 */

public class FloatingTask extends Task implements Comparable<FloatingTask> {

    /**
     * Creates a new FloatingTask.
     */
    public FloatingTask(String name) {
        super(name, TaskType.FLOATING);
    }

    /**
     * Creates a new FloatingTask with given name and a Google ID.
     */
    public FloatingTask(String name, String googleID) {
        super(name, TaskType.FLOATING);
        this.setId(googleID);
    }

    //@A0109194A
    /**
     * Creates a new FloatingTask from a given FloatingTask. Used for cloning.
     */
    public FloatingTask(FloatingTask otherFloatingTask) {
        super(otherFloatingTask.getName(), TaskType.FLOATING);
        this.setId(otherFloatingTask.getId());
        this.setDone(otherFloatingTask.isDone());
        this.setEdited(otherFloatingTask.isEdited());
    }

    //A0109194A
    /**
     * Compares the name of this floating task to the given floating task in
     * alphabetically order.
     * 
     * @param other floating task
     * @return less/more than zero if before/after the otherTask, zero when same
     *         equal
     */
    @Override
    public int compareTo(FloatingTask otherTask) {
        return (getName().compareTo(otherTask.getName()));
    }

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
        if (!(otherObject instanceof FloatingTask)) {
            return false;
        }
        FloatingTask otherFloatingTask = (FloatingTask) otherObject;
        if (this.getType().equals(otherFloatingTask.getType())
            && this.getName().equals(otherFloatingTask.getName())
            && this.getId() == otherFloatingTask.getId()) {
            return true;
        } else {
            return false;
        }
    }
}
