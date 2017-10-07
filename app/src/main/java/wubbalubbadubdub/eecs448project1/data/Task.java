package wubbalubbadubdub.eecs448project1.data;

/**
 * Created by martin on 9/30/2017.
 */

public class Task {

    private String taskName;
    private String taskHelper;


    public Task(String taskName, String taskHelper) {
        this.taskName = taskName;
        this.taskHelper = taskHelper;
    }

    public void setTaskName(String name) {
        taskName = name;
    }

    public void setTaskHelper(String helper) {
        taskHelper = helper;
    }

    public String getTaskHelper() {
        return taskHelper;
    }

    public String getTaskName() {
        return taskName;
    }

    public String toString() {
        if (taskHelper.isEmpty()) {
            return taskName;
        }
        return taskName + ": " + taskHelper;
    }
}

