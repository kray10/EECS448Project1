package wubbalubbadubdub.eecs448project1.data;

import java.util.List;

/**
 * Created by simonyang on 2017/9/29.
 */

public class Date {
    private List<String> timeslots;
    private List<String> tasks; // optional
    private String date;
    private int day;
    private int month;
    private int year;

    public Date(int day, int month, int year, List<String> tasks, List<String> timeslots){
        this.day = day;
        this.month = month;
        this.year = year;
        this.tasks = tasks;
        this.timeslots = timeslots;
        this.date = getDate();
    }

    public String getDate(){
        date = Integer.toString(year) + "/"+Integer.toString(month)+"/"+Integer.toString(day);
        return date;
    }
    public List<String> getTasks(){
        return tasks;
    }
    public int getDay(){
        return day;
    }
    public int getMonth(){
        return month;
    }
    public int getYear(){
        return year;
    }
    public List<String> getTimeslots() {
        return timeslots;
    }
    public void setDay(int day){
        this.day = day;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setDate(String date) { // sometimes you will use this one
        this.date = date;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setTasks(List<String> tasks) {
        this.tasks = tasks;
    }

    public void setTimeslots(List<String> timeslots) {
        this.timeslots = timeslots;
    }
}
