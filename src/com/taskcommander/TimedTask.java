package com.taskcommander;
import java.util.Date;

//@author A0109194A
/**
 * A task that has a name, a start date and an end date. May also hold a Google
 * API ID. Related Google API: Calendar
 */

public class TimedTask extends DatedTask {
    private Date _startDate;
    private Date _endDate;

    /**
     * Creates a new TimedTask with given name, start time and end time. Throws
     * IllegalArgumentException if null arguments are given.
     */
    public TimedTask(String name, Date startTime, Date endTime) {
        super(name, TaskType.TIMED, startTime);
        if (startTime != null && endTime != null) {
            _startDate = startTime;
            _endDate = endTime;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Creates a new TimedTask with given name, start time, end time and a
     * Google ID. Throws IllegalArgumentException if null arguments are given.
     */
    public TimedTask(String name, Date startTime, Date endTime, String googleID) {
        super(name, TaskType.TIMED, startTime);
        this.setId(googleID);
        if (startTime != null && endTime != null) {
            _startDate = startTime;
            _endDate = endTime;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Creates a new TimedTask from a given TimedTask. Used for cloning.
     */
    public TimedTask(TimedTask otherTimedTask) {
        super(otherTimedTask.getName(), TaskType.TIMED, otherTimedTask
            .getStartDate());
        _startDate = otherTimedTask.getStartDate();
        _endDate = otherTimedTask.getEndDate();
        this.setId(otherTimedTask.getId());
        this.setDone(otherTimedTask.isDone());
        this.setEdited(otherTimedTask.isEdited());
    }

    //@author A0112828H
    public Date getStartDate() {
        return _startDate;
    }

    public Date getEndDate() {
        return _endDate;
    }

    public void setStartDate(Date startDate) {
        _startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        _endDate = endDate;
    }

    //@author A0128620M
    /**
     * Compares the start date of this timed task to the given timed task in
     * chronological order.
     * 
     * @param other Timed task
     * @return less/more than zero if before/after the otherTask, zero when same
     *         date
     */
    @Override
    public int compareTo(TimedTask otherTask) {
        return (_startDate.compareTo(otherTask.getStartDate()));
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
        if (!(otherObject instanceof TimedTask)) {
            return false;
        }
        TimedTask otherTimedTask = (TimedTask) otherObject;

        if (this.getType().equals(otherTimedTask.getType())
            && this.getName().equals(otherTimedTask.getName())
            && this.getId() == otherTimedTask.getId()
            && this.getStartDate().equals(otherTimedTask.getStartDate())
            && this.getEndDate().equals(otherTimedTask.getEndDate())) {
            return true;
        } else {
            return false;
        }
    }
}
