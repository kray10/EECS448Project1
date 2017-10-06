package wubbalubbadubdub.eecs448project1;


import java.util.List;


/**
 * Created by simonyang on 2017/9/29.
 */

public class dayitem {
    private int day;
    private int year;
    private int month;

    private boolean emptytimeslot;
    private List<Integer> timeSlotes;
    public dayitem(int day, int year, int month){
        this.day = day;
        this.year = year;
        this.month = month + 1;
        this.emptytimeslot = true;
    }
    public void setTimeSlotes(List<Integer> timeSlotes){
        this.emptytimeslot = false;
        this.timeSlotes = timeSlotes;}


    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day){
        this.day = day;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }


    public List<Integer> getTimeSlotes(){ return timeSlotes;}

    public boolean Empty() {
        return emptytimeslot;
    }

}