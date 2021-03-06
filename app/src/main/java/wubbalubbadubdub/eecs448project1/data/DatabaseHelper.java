package wubbalubbadubdub.eecs448project1.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * DatabaseHelper.java
 * @author Damian, Lane
 * @version 1.0
 * This class contains helper methods that interact with the Database. This replaced the Dataclass
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    /**
     * Default constructor for the databasehelper.
     * @param context Always the entire application context because we want the database to be for the whole application.
     */
    public DatabaseHelper(Context context) {
        super(context, DBContract.DATABASE_NAME, null, DBContract.DATABASE_VERSION);
    }

    /**
     * Called when the DatabaseHelper class is created. Will create database tables if they do not exist.
     * @param db Current writable database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create all tables
        db.execSQL(DBContract.UserTable.CREATE_TABLE);
        db.execSQL(DBContract.EventTable.CREATE_TABLE);
        db.execSQL(DBContract.SignupTable.CREATE_TABLE);
        db.execSQL(DBContract.TimeSlotTable.CREATE_TABLE);
        db.execSQL(DBContract.TaskTable.CREATE_TABLE);
    }

    /**
     * Called when the database version is changed in DBContract
     * @param db Current writable database
     * @param oldVersion Old version of DB. Set in DBContract
     * @param newVersion New version of DB. Set in DBContract
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Delete all tables
        db.execSQL(DBContract.UserTable.DROP_TABLE);
        db.execSQL(DBContract.EventTable.DROP_TABLE);
        db.execSQL(DBContract.SignupTable.DROP_TABLE);
        db.execSQL(DBContract.TimeSlotTable.DROP_TABLE);
        onCreate(db);
    }

    //region User Table Methods

    /**
     *
     * @param name the name of the new user to add
     * @return -1 for failure, otherwise will return the row inserted at.
     */
    public long addUser(String name) {
        SQLiteDatabase db = this.getWritableDatabase(); // is this okay?

        ContentValues values = new ContentValues();
        values.put(DBContract.UserTable.COLUMN_NAME_NAME, name);

        return db.insert(DBContract.UserTable.TABLE_NAME, null, values);
    }

    /**
     * This method queries our User table for all Usernames
     * @return List of strings containing all users
     */
    public List<String> getUsers() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Even though we only get one column, SQLiteDatabase.query() requires a string array
        String[] columns = {
                DBContract.UserTable.COLUMN_NAME_NAME
        };
        String sortOrder = DBContract.UserTable.COLUMN_NAME_NAME + " COLLATE NOCASE ASC";

        Cursor query = db.query(
                DBContract.UserTable.TABLE_NAME,
                columns,
                null, null, null, null,
                sortOrder
        );

        List<String> names = new ArrayList<>();
        while (query.moveToNext()) {
            String name = query.getString(query.getColumnIndexOrThrow(DBContract.UserTable.COLUMN_NAME_NAME));
            names.add(name);
        }

        query.close();

        return names;
    }

    //endregion



    //region Event Table Methods

    /**
     * This function will add an event to the event table
     * @param e - Event object passed when the save button is clicked with valid event params
     * @return event ID
     * @since 1.0
     */
    public int addEvent(Event e) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBContract.EventTable.COLUMN_NAME_TITLE, e.getName());
        values.put(DBContract.EventTable.COLUMN_NAME_CREATOR, e.getCreator());

        db.insert(DBContract.EventTable.TABLE_NAME, null, values); //Perform insertion

        //Get the ID of the event we just created and return it
        String[] columns = {DBContract.EventTable._ID};
        String sortOrder = DBContract.EventTable._ID + " DESC";
        
        Cursor query = db.query(
                DBContract.EventTable.TABLE_NAME,
                columns,
                null, null, null, null,
                sortOrder
        );
        query.moveToNext();
        int eventID = Integer.parseInt(query.getString(query.getColumnIndexOrThrow(DBContract.EventTable._ID)));
        query.close();

        for(int i = 0; i < e.getDateSlots().size(); i++) {
            values.clear();
            values.put(DBContract.TimeSlotTable.COLUMN_NAME_DAY, e.getDateSlots().get(i).getDate());
            values.put(DBContract.TimeSlotTable.COLUMN_NAME_TIMESLOTS, e.getDateSlots().get(i).getTimeslots());
            values.put(DBContract.TimeSlotTable.COLUMN_NAME_EVENT, eventID);
            db.insert(DBContract.TimeSlotTable.TABLE_NAME, null, values);
        }

        for(int i = 0; i < e.getTasks().size(); i++) {
            System.out.println(e.getTasks().get(i));
            values.clear();
            values.put(DBContract.TaskTable.COLUMN_NAME_TASKNAME, e.getTasks().get(i).getTaskName());
            values.put(DBContract.TaskTable.COLUMN_NAME_HELPER, e.getTasks().get(i).getTaskHelper());
            values.put(DBContract.TaskTable.COLUMN_NAME_EVENT, eventID);
            db.insert(DBContract.TaskTable.TABLE_NAME, null, values);
        }

        return eventID;
    }

    /**
     * This function will get all events from the event table
     * @return A sorted ArrayList of Events from the Database
     * @since 1.0
     */
    public ArrayList<Event> getAllEvents() {

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Event> sortedListOfEvents = new ArrayList<>(); // Will be sorted through SQL

        String[] columnsEvent = {
                DBContract.EventTable._ID,
                DBContract.EventTable.COLUMN_NAME_TITLE,
                DBContract.EventTable.COLUMN_NAME_CREATOR,
        };

        //Sort by day for now. Could hypothetically get weird when multiple years are involved
        String sortOrderEvent = DBContract.EventTable.COLUMN_NAME_CREATOR + " COLLATE NOCASE ASC";

        Cursor queryEvent = db.query(
                DBContract.EventTable.TABLE_NAME,
                columnsEvent,
                null, null, null, null,
                sortOrderEvent
        );

        //Populate event vector
        while (queryEvent.moveToNext()) {

            int id;
            String title, creator;

            id = Integer.parseInt(queryEvent.getString(queryEvent.getColumnIndexOrThrow(DBContract.EventTable._ID)));
            title = queryEvent.getString(queryEvent.getColumnIndexOrThrow(DBContract.EventTable.COLUMN_NAME_TITLE));
            creator = queryEvent.getString(queryEvent.getColumnIndexOrThrow(DBContract.EventTable.COLUMN_NAME_CREATOR));

            List<DateSlot> dateSlotList = getDateSlots(id,db);

           List<Task> taskList = getTasks(id, db);

            //Create Event object from row and add to Vector
            Event e = new Event(id, title, creator, dateSlotList, taskList); // LOL This stuff was in the wrong order... Come on guys...
            sortedListOfEvents.add(e);
        }

        queryEvent.close();

        return sortedListOfEvents;
    }

    /**
     * Get events from given user.
     * @param user - Username to retrieve events for.
     * @return Sorted Event vector of a given user's created events
     */
    public ArrayList<Event> getUserEvents(String user) {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Event> sortedListOfEvents = new ArrayList<>(); // Will be sorted through SQL

        String[] userArr = {user}; //(Needs to be in an array to use as a WHERE argument)

        String[] columnsEvent = {
                DBContract.EventTable._ID,
                DBContract.EventTable.COLUMN_NAME_TITLE,
                DBContract.EventTable.COLUMN_NAME_CREATOR,
        };

        //Sort by day for now. Could hypothetically get weird when multiple years are involved
        String sortOrder = DBContract.EventTable.COLUMN_NAME_CREATOR + " COLLATE NOCASE ASC";

        Cursor query = db.query(
                DBContract.EventTable.TABLE_NAME,
                columnsEvent,
                "creator = ?", userArr, null, null,
                sortOrder
        );

        //Populate event List
        while (query.moveToNext()) {

            int id;
            String title, creator;

            id = Integer.parseInt(query.getString(query.getColumnIndexOrThrow(DBContract.EventTable._ID)));
            title = query.getString(query.getColumnIndexOrThrow(DBContract.EventTable.COLUMN_NAME_TITLE));
            creator = query.getString(query.getColumnIndexOrThrow(DBContract.EventTable.COLUMN_NAME_CREATOR));

            List<DateSlot> dateSlotList = getDateSlots(id, db);

            List<Task> taskList = getTasks(id, db);

            //Create Event object from row and add to Vector
            Event e = new Event(id, title, creator, dateSlotList, taskList); // LOL This stuff was in the wrong order... Come on guys...
            sortedListOfEvents.add(e);
        }

        query.close();

        return sortedListOfEvents;
    }

    //endregion



    //region Signup Table Methods

    /**
     * @param eventID ID of event in Table
     * @return Event object containing all event info
     */
    public Event getEvent(int eventID) {

        SQLiteDatabase db = this.getReadableDatabase();

        String[] columnsEvent = {
                DBContract.EventTable.COLUMN_NAME_CREATOR,
                DBContract.EventTable.COLUMN_NAME_TITLE
        };

        String[] where = {Integer.toString(eventID)};

        Cursor query = db.query(
                DBContract.EventTable.TABLE_NAME,
                columnsEvent,
                "_ID = ?", where , null, null,
                null
        );

        query.moveToNext();

        String creator = query.getString(query.getColumnIndexOrThrow(DBContract.EventTable.COLUMN_NAME_CREATOR));
        String name = query.getString(query.getColumnIndexOrThrow(DBContract.EventTable.COLUMN_NAME_TITLE));

        query.close();

        List<DateSlot> dateSlotList = getDateSlots(eventID, db);
        List <Task> taskList = getTasks(eventID, db);

        Event returnEvent = new Event(eventID, name, creator, dateSlotList, taskList);

        return returnEvent;
    }

    /**
     * This method will add a users availability to the signup table
     * @param eventID int ID of event
     * @param user String username that is signing up
     * @param availability Integer List of timeslots
     * @return long value of the row in the table that was created
     */
    public long addSignup(int eventID, String user, List<Integer> availability, String date) {// TODO create entry in signups table
        SQLiteDatabase db = this.getWritableDatabase();

        String avail = HelperMethods.stringifyTimeslotInts(availability);

        ContentValues values = new ContentValues();
        values.put(DBContract.SignupTable.COLUMN_NAME_USER, user);
        values.put(DBContract.SignupTable.COLUMN_NAME_AVAIL, avail);
        values.put(DBContract.SignupTable.COLUMN_NAME_EVENT, eventID);
        values.put(DBContract.SignupTable.COLUMN_NAME_DAY, date);

        return db.insert(DBContract.SignupTable.TABLE_NAME, null, values);
    }

    /**
     * This method will return a Hashmap of users along with DateSlots for a given event
     * @param eventID int ID of event
     * @return Hashmap of user keypairs with DateSlot keyvalues
     */
    public Map<String, List<DateSlot>> getSignups(int eventID) { // TODO return list of signed up users(?) for given event
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String, List<DateSlot>> userSignup = new HashMap<>();

        String[] columns = {
                DBContract.SignupTable.COLUMN_NAME_USER,
                DBContract.SignupTable.COLUMN_NAME_AVAIL,
                DBContract.SignupTable.COLUMN_NAME_DAY
        };
        String[] where = {Integer.toString(eventID)};

        String sortOrder = "DATE("  + DBContract.SignupTable.COLUMN_NAME_DAY  + ") COLLATE NOCASE DESC, " + DBContract.SignupTable.COLUMN_NAME_USER + " COLLATE NOCASE ASC" ;

        Cursor query = db.query(
                DBContract.SignupTable.TABLE_NAME,
                columns,
                "eid = ?", where, null, null,
                sortOrder
        );

        while (query.moveToNext()) {
            String availablity = query.getString(query.getColumnIndexOrThrow(DBContract.SignupTable.COLUMN_NAME_AVAIL));
            String date = query.getString(query.getColumnIndexOrThrow(DBContract.SignupTable.COLUMN_NAME_DAY));
            DateSlot dateSlot = new DateSlot(availablity, date);
            String user = query.getString(query.getColumnIndexOrThrow(DBContract.SignupTable.COLUMN_NAME_USER));
            List<DateSlot> list = userSignup.get(user);
            if (list == null) {
                List<DateSlot> dateSlotList = new ArrayList<>();
                dateSlotList.add(dateSlot);
                userSignup.put(user, dateSlotList);
            }
            else {
                list.add(dateSlot);
                userSignup.put(user, list);
            }
        }

        return userSignup;
    }

    /**
     * This method will update a given user's availability for an event.
     * @param eventID int ID of event
     * @param user String username that is changing availability
     * @param availability Integer List of timeslots
     * @return int value of the row in the table that was updated
     */
    public int updateSignup(int eventID, String user, List<Integer> availability, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        String avail = HelperMethods.stringifyTimeslotInts(availability);

        ContentValues values = new ContentValues();
        values.put(DBContract.SignupTable.COLUMN_NAME_AVAIL, avail);

        String selection = DBContract.SignupTable.COLUMN_NAME_USER + " = ? AND " + DBContract.SignupTable.COLUMN_NAME_EVENT + " = ? AND " +
                DBContract.SignupTable.COLUMN_NAME_DAY + " = ?";
        String[] selectionArgs = {user, Integer.toString(eventID), date};

        return db.update(
                DBContract.SignupTable.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
    }

    public int updateTasks(int eventID, Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        String taskName = task.getTaskName();
        String volunteer = task.getTaskHelper();

        ContentValues values = new ContentValues();
        values.put(DBContract.TaskTable.COLUMN_NAME_HELPER, volunteer);

        String selection = DBContract.TaskTable.COLUMN_NAME_EVENT + " = ? AND " + DBContract.TaskTable.COLUMN_NAME_TASKNAME + " = ?";

        String[] selectionArgs = {Integer.toString(eventID), taskName};

        return  db.update(
                DBContract.TaskTable.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
    }

    private List<Task> getTasks (int eventID, SQLiteDatabase db) {

        String[] columnsTasks = {
                DBContract.TaskTable.COLUMN_NAME_TASKNAME,
                DBContract.TaskTable.COLUMN_NAME_HELPER
        };

        String[] where = {Integer.toString(eventID)};

        Cursor queryTasks = db.query(
                DBContract.TaskTable.TABLE_NAME,
                columnsTasks,
                "eid = ?", where, null, null, null
        );
        List<Task> taskList = new ArrayList<>();
        String taskName, helper;

        while(queryTasks.moveToNext()) {
            taskName = queryTasks.getString(queryTasks.getColumnIndexOrThrow(DBContract.TaskTable.COLUMN_NAME_TASKNAME));
            helper = queryTasks.getString(queryTasks.getColumnIndexOrThrow(DBContract.TaskTable.COLUMN_NAME_HELPER));
            Task task = new Task(taskName, helper);
            taskList.add(task);
        }

        queryTasks.close();

        return taskList;
    }

    private List<DateSlot> getDateSlots(int eventID, SQLiteDatabase db) {

        String[] columnsTime = {
                DBContract.TimeSlotTable.COLUMN_NAME_TIMESLOTS,
                DBContract.TimeSlotTable.COLUMN_NAME_DAY,
        };

        String[] where = {Integer.toString(eventID)};

        String sortOrderTime = "DATE(" + DBContract.TimeSlotTable.COLUMN_NAME_DAY + ") COLLATE NOCASE ASC";

        Cursor queryTime = db.query(
                DBContract.TimeSlotTable.TABLE_NAME,
                columnsTime,
                "eid = ?", where, null, null,
                sortOrderTime
        );

        List<DateSlot> dateSlotList = new ArrayList<>();
        String timeslots, date;

        while(queryTime.moveToNext()) {
            timeslots = queryTime.getString(queryTime.getColumnIndexOrThrow(DBContract.TimeSlotTable.COLUMN_NAME_TIMESLOTS));
            date = queryTime.getString(queryTime.getColumnIndexOrThrow(DBContract.TimeSlotTable.COLUMN_NAME_DAY));
            DateSlot dateSlot = new DateSlot(timeslots, date);
            dateSlotList.add(dateSlot);
        }
        queryTime.close();

        return  dateSlotList;
    }



    //endregion
}
