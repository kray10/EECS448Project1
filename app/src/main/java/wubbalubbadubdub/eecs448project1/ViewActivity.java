package wubbalubbadubdub.eecs448project1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import wubbalubbadubdub.eecs448project1.data.DatabaseHelper;
import wubbalubbadubdub.eecs448project1.data.DateSlot;
import wubbalubbadubdub.eecs448project1.data.Event;
import wubbalubbadubdub.eecs448project1.data.HelperMethods;

/**
 * This activity is for viewing a certain activity.
 * The view will be different dependent on if the Current user was the creator of the event
 * @author Dustin, Lane, Damian
 * @version 1.0
 */
public class ViewActivity extends Activity {

    DatabaseHelper dbHelper;

    Boolean format = false;

    private int currentID;
    private String currentUser;
    private Event currentEvent;
    private List<String> eventDates;
    private int dateIndex;

    private List<List<Integer>> currentTimeslots;
    private List<List<Integer>> selectedTimeslots;

    private int selectedRow = -1;
    private int selectedSlot = -1;

    private Map<String, List<DateSlot>> userSignups;

    private Toast statusMessage;

    private boolean prevSignup;

    private boolean adminMode;

    //Color Variables - Material Design
    int BLUE_MAT = Color.rgb(2,136,209);
    int GREEN_MAT = Color.rgb(139,195,74);

    /**
     * Method called when the activity is first created and displayed to the screen
     * @param savedInstanceState Unused Bundle object. Usually used if the app is killed then we can resume
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        Intent intent = getIntent();
        currentID = intent.getIntExtra("eventID", -1);
        currentUser = intent.getStringExtra("currentUser");

        statusMessage = Toast.makeText(this, "", Toast.LENGTH_SHORT);


        dbHelper = new DatabaseHelper(getApplicationContext());

        currentEvent = dbHelper.getEvent(currentID);

        eventDates = new ArrayList<>();

        for (int i = 0; i < currentEvent.getDateSlots().size(); i++) {
            eventDates.add(currentEvent.getDateSlots().get(i).getDate());
        }

        Collections.sort(eventDates, new Comparator<String>() {
            SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy");
            @Override
            public int compare(String o1, String o2) {
                try {
                    return f.parse(o1).compareTo(f.parse(o2));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });

        Spinner dateSpinner = (Spinner) findViewById(R.id.tvMultiDates);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, eventDates);
        dateSpinner.setAdapter(adapter);

        adminMode = currentUser.equals(currentEvent.getCreator());

        String creatorString = "Created by: " + currentEvent.getCreator();
        String eventString = currentEvent.getName() + " - " + ((adminMode) ? "Admin Mode" : "Select Availability");

        TextView eventName = (TextView) findViewById(R.id.tvEventName);
        TextView eventCreator = (TextView) findViewById(R.id.tvCreator);
        TextView eventDate = (TextView) findViewById(R.id.tvDate);

        eventName.setText(eventString);
        eventCreator.setText(creatorString);
        eventDate.setText(currentEvent.getDateSlots().get(0).getDate());

        currentTimeslots = new ArrayList<>();
        selectedTimeslots = new ArrayList<>();
        for (int i = 0; i < currentEvent.getDateSlots().size(); i++)  {
            List<Integer> tempList = HelperMethods.listifyTimeslotInts(currentEvent.getDateSlots().get(i).getTimeslots());
            currentTimeslots.add(tempList);
            tempList = HelperMethods.listifyTimeslotInts("");
            selectedTimeslots.add(tempList);
        }

        userSignups = dbHelper.getSignups(currentID);
        prevSignup = userSignups.containsKey(currentUser);
        if (prevSignup) {
            for (Map.Entry<String, List<DateSlot>> entry : userSignups.entrySet()) {
                if (entry.getKey().equals(currentUser)) {
                    for (int i = 0; i < entry.getValue().size(); i++) {
                        selectedTimeslots.set(i, HelperMethods.listifyTimeslotInts(entry.getValue().get(i).getTimeslots()));
                    }
                }
            }
        }



        if (adminMode) {
            // View event status
            displayEventSignups();

            ((Button)findViewById(R.id.btnSave)).setVisibility(View.GONE);
            ((Spinner)findViewById(R.id.tvMultiDates)).setVisibility(View.GONE);
        } else {
            // Set availability

            ((TextView)findViewById(R.id.tvSelectedUser)).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.tvDate)).setVisibility(View.GONE);
            updateTimeframe(0);

            dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    dateIndex = position;
                    populateTimeslotTable();
                    updateTimeframe(dateIndex);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });
        }

    }

    /**
     * This method fills the timeslot table with the timeslots local to the current event
     */
    private void populateTimeslotTable() {
        TableLayout layout = (TableLayout) findViewById(R.id.tbLayout);

        // Clear table
        for (int i = 0; i < layout.getChildCount(); i++) {
            View row = layout.getChildAt(i);
            if (row instanceof TableRow) ((ViewGroup) row).removeAllViews();
            layout.removeAllViews();
        }

        List<Integer> currentUserSelection = selectedTimeslots.get(dateIndex);

        int count = 0;
        for (int i = 0; i < 4; i++) {
            TableRow tr = new TableRow(this);
            for (int j = 0; j < 12; j++) {
                final int current = count;
                Button b = new Button(this);
                b.setText(HelperMethods.toTime(count,format));
                b.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                TableRow.LayoutParams cellParams = new TableRow.LayoutParams();
                cellParams.rightMargin = 5;
                b.setLayoutParams(cellParams);
                if (currentTimeslots.get(dateIndex).contains(count)) {
                    boolean intSelect = false;
                    if (currentUserSelection != null && currentUserSelection.contains(count)) {
                        intSelect = true;
                        b.setBackgroundColor(BLUE_MAT);
                    } else {
                        b.setBackgroundColor(GREEN_MAT);
                    }
                    final boolean select = intSelect;

                    b.setOnClickListener(new Button.OnClickListener() {
                        int id = current;
                        boolean selected = select;

                        @Override
                        public void onClick(View v) {
                            Button obj = (Button) v;
                            if (selected) {
                                obj.setBackgroundColor(GREEN_MAT);
                                selectedTimeslots.get(dateIndex).remove(Integer.valueOf(id));
                            } else {
                                obj.setBackgroundColor(BLUE_MAT);
                                selectedTimeslots.get(dateIndex).add(id);
                            }
                            selected = !selected;
                            updateTimeDisplay(dateIndex);
                        }
                    });
                } else {
                    b.setBackgroundColor(Color.DKGRAY);
                }
                tr.addView(b);
                count++;
            }
            TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);

            tableRowParams.setMargins(10, 2, 10, 2);

            tr.setLayoutParams(tableRowParams);

            layout.addView(tr, tableRowParams);
        }
        updateTimeDisplay(dateIndex);
    }

    /**
     * This method displays the Event timeframe and which users are signed up
     */
    private void displayEventSignups() {
        TableLayout layout = (TableLayout) findViewById(R.id.tbLayout);

        TableRow header = new TableRow(this);


        // Clear table
        for (int i = 0; i < layout.getChildCount(); i++) {
            View row = layout.getChildAt(i);
            if (row instanceof TableRow) ((ViewGroup) row).removeAllViews();
            layout.removeAllViews();
        }


        TableRow.LayoutParams cellParams = new TableRow.LayoutParams();
        cellParams.setMargins(20, 20, 20, 20);

        TextView userHeader = new TextView(this);
        userHeader.setText("User");
        userHeader.setTextSize(15);
        userHeader.setTypeface(null, Typeface.BOLD);
        userHeader.setLayoutParams(cellParams);
        header.addView(userHeader);

        for (int i = 0; i < currentTimeslots.size(); i++) {
            for (int slot : currentTimeslots.get(i)) {
                TextView slotHeader = new TextView(this);
                slotHeader.setText(HelperMethods.toTime(slot, format));
                slotHeader.setTextSize(15);
                slotHeader.setTypeface(null, Typeface.BOLD);
                slotHeader.setLayoutParams(cellParams);
                final int thisSlot = slot;

                slotHeader.setOnClickListener(new View.OnClickListener() {
                    int slot = thisSlot;

                    @Override
                    public void onClick(View view) {
                        selectedRow = -1;
                        selectedSlot = slot;

                        highlightSelection();

                    }
                });

                header.addView(slotHeader);
            }
        }

        header.setBackgroundColor(Color.GRAY);

        layout.addView(header);
        int count = 1;
        for (Map.Entry<String, List<DateSlot>> entry : userSignups.entrySet()) {
            TableRow signupRow = new TableRow(this);

            TextView username = new TextView(this);
            username.setPadding(10, 20, 10, 20);
            username.setText(entry.getKey());
            username.setTypeface(null, Typeface.BOLD);
            signupRow.addView(username);
            List<Integer> slots = HelperMethods.listifyTimeslotInts(entry.getValue().get(0).getTimeslots());

            for (int i = 0; i < currentTimeslots.size(); i++) {
                for (int slot : currentTimeslots.get(i)) {
                    TextView avail = new TextView(this);

                    if (slots.contains(slot)) {
                        // User is signed up for this
                        avail.setText("AVAILABLE");
                        avail.setBackgroundColor(GREEN_MAT);
                    } else if (entry.getValue().get(0).getTimeslots().isEmpty()) {
                        avail.setBackgroundColor(Color.RED);
                    } else {
                        avail.setBackgroundColor(Color.LTGRAY);
                    }
                    avail.setPadding(20, 20, 20, 20);

                    signupRow.addView(avail);
                }
            }

            final int currentRow = count;

            signupRow.setOnClickListener(new View.OnClickListener() {
                int thisRow = currentRow;

                @Override
                public void onClick(View view) {
                    selectedRow = thisRow;
                    highlightSelection();
                }
            });

            layout.addView(signupRow);
            count++;

        }
    }

    /**
     * This method allows for selection of a table row and displaying a user-friendly list of
     * the given user's availability
     */
    private void highlightSelection() {
        String disp;
        TableLayout layout = (TableLayout) findViewById(R.id.tbLayout);

        TableRow highlight = (TableRow)layout.getChildAt(selectedRow);
        if (selectedRow != -1) {

            String user = ((TextView)highlight.getChildAt(0)).getText().toString();

            disp = user + "'s Availability: " + HelperMethods.getTimeString(HelperMethods.listifyTimeslotInts((userSignups.get(user).get(0).getTimeslots())), format);
        } else {

            String users = "";
            int userCount = 0;

            for (Map.Entry<String, List<DateSlot>> entry : userSignups.entrySet()) {
                if (HelperMethods.listifyTimeslotInts(entry.getValue().get(0).getTimeslots()).contains(selectedSlot)) {
                    userCount++;
                    users = users + entry.getKey() + ", ";
                }
            }

            if (userCount > 0) users = users.substring(0, users.length() - 3);

            disp = "For timeslot " + HelperMethods.toTime(selectedSlot, format) + " " + userCount + " user(s) are available: " + users;
        }

        ((TextView)findViewById(R.id.tvSelectedUser)).setText(disp);
    }

    /**
     * This function saves the user's current availability for an event
     * @param v View of the button that was pressed
     */
    public void saveSelection(View v) {
        if (prevSignup) {
            // User has signed up previously, so call the update method
            for (int i = 0; i < selectedTimeslots.size(); i++) {
                if (dbHelper.updateSignup(currentID, currentUser, selectedTimeslots.get(i), eventDates.get(i)) > 0) {
                    statusMessage.setText("Successfully saved your availability");
                } else {
                    statusMessage.setText("Something went wrong");
                }
            }
        } else {
            for (int i = 0; i < selectedTimeslots.size(); i++) {
                // User has not signed up before, so call the insert method
                if (dbHelper.addSignup(currentID, currentUser, selectedTimeslots.get(i), eventDates.get(i)) != -1) {
                    statusMessage.setText("Successfully saved your availability");
                } else {
                    statusMessage.setText("Somethign went wrong");
                }
            }
        }
        statusMessage.show();
        finish();
    }

    /**
     * This function updates the display of the user's current selected availability.
     */
    private void updateTimeDisplay(int pos) {
        TextView timeDisplay = (TextView) findViewById(R.id.tvSelectedTimes);

        String disp = "Your Selected Availability: " + HelperMethods.getTimeString(selectedTimeslots.get(dateIndex), format);

        timeDisplay.setText(disp);
    }

    /**
     * This function updates the timeframe of the event on creation and when the 12h/24h is toggled.
     */
    private void updateTimeframe(int pos) {

        TextView eventTimeframe = (TextView) findViewById(R.id.tvEventTimeframe);

        eventTimeframe.setText("Event timeframe: " + HelperMethods.getTimeString(currentTimeslots.get(pos), format));
    }

    /**
     * This function toggles the 12h/24h format
     * @param v View of the button that was pressed
     */
    public void toggleTimeFormat(View v) {
        format = !format;

        if (adminMode) {
            displayEventSignups();
            highlightSelection();
        } else {
            populateTimeslotTable();

            updateTimeDisplay(dateIndex);
        }
        updateTimeframe(dateIndex);
    }
}
