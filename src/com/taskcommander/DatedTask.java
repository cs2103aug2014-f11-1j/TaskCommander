package com.taskcommander;
import java.util.Date;

//@author A0128620M
/**
 * A task with one comparable date the timedTask and FloatingTask inherit from.
 */

public abstract class DatedTask extends Task implements Comparable<DatedTask> {
    private Date _date;

    /**
     * Creates a new DatedTask with given name, task type and date. Throws
     * IllegalArgumentException if null arguments are given.
     * 
     * @param name
     * @param type
     * @param date
     * @return Date instance
     * 
     */
    public DatedTask(String name, TaskType taskType, Date date) {
        super(name, taskType);
        if (date != null) {
            _date = date;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public Date getDate() {
        return _date;
    }

    public void setDate(Date date) {
        _date = date;
    }

    /**
     * Compares the date of this task to the given task in chronological order.
     * 
     * @param other DatedTask
     * @return less/more than zero if before/after the otherTask, zero when same date
     */
    @Override
    public int compareTo(DatedTask otherTask) {
        return (_date.compareTo(otherTask.getDate()));
    }
    
    abstract public int compareTo(DeadlineTask otherTask);
    
    abstract int compareTo(TimedTask otherTask);

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
        if (!(otherObject instanceof DatedTask)) {
            return false;
        }
        switch (this.getType()) {
          case DEADLINE:
              return ((DeadlineTask) this).equals(otherObject);
          case TIMED:
              return ((TimedTask) this).equals(otherObject);
          default:
              return false;
        }
    }
}