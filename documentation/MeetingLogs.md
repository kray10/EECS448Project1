# Meeting Logs

## EECS448 Project 2

### Team J-Hawk

#### Meeting 1

#### 9/27/2017, 10:00 AM, EECS Computer Lab

* First meeting was to discuss general thought process for completeing project. Discuss:
    * For multi day selection, creat DateSlot object to hold the date and timeslots for each day in the event. Add List<DateSlot> to the Evnt class.
    * For tasks, create Task object to hold the task name and volunteer. Add a List<Task> to the Event class.
    * For the event view, need to add way to display tasks.
    * Add SQL tables and methods for storing and getting all of the new classes.
* Scheduled next meeting for 9/29/2017.

#### Meeting 2

#### 9/29/2017 10:00 AM, EECS Computer Lab

* Went over the design choices from the last meeting. Assigned tasks to team members to try and complete before next meeting.
    * Create tables for the SQL database and add the new methods.
    * Work on the Event View for displaying Tasks in Admin Mode.
    * Create the Task class.
    * Create the DateSlot class and work on the multi day selection for event creation.
    
#### Meeting 3

#### 10/2/2017 9:00 AM, EECS Computer Lab

* Completed since last meeting:
    * All of the SQL functions are finished
    * all of the new classes we discussed were created
    * event screen has the ability to select multiple days

* Discussed this meeting:
    * We currently allow selecting multiple days, but each day is getting populated with the same time slots. We are going to need some way to re-populate the time slots grid for each day and then also add a button to make the current time slot grid apply to all the tables.
    * The events all currently have tasks that would be getting saved to the SQL db, but we don't have a way for users to make tasks. We'll need to discuss how we want to implement that (ie, pop-up screen, new screen, etc).
    * The event view screen is currently just showing the first day of the event (event.getDateSlot.get(0) is hard coded in right now). The SQL is currently returning all of the dates so we just need to  decide how we want to display them all.
    * Once we allow the creation of tasks, we need to figure out how to display them.
    
#### Meeting 4

#### 10/6/2017 9:00 AM, EECS Computer Lab

* Completed since last meeting:
    * Switch between days on signup screen to all selection of multiple timeslots.
    * All selecting of differnt timeslots for different days on event creation screen and copying those timeslots to different days.
    * Tasks now displaying on Sign up screen in Admin mode.

* Discussed this meeting:
    * Need to finish up task creation on event create screen.
    * Assigned code documentation for Javadoc, works cited, and compilation of development artificats.

