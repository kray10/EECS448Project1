# Team Wubba Lubba Dub Dub's Schedule Planner

## EECS448 Project 1
Members: Damian Vu, Dustin Bingham, Lane Gramling

### TODOLIST
(out-of-scope) items are ideas that we do NOT NEED, but want to implement if there is enough time

##### DatabaseHelper
- ~~Implement Database 1.0 Schema in DBContract~~ **DONE!**
- ~~Dummy method implementation~~ **DONE!**
- ~~Implement User Table methods~~ **DONE!**
- ~~Implement Event Table methods~~ **DONE!**
- ~~Implement Signup Table methods~~ **DONE!**

##### SelectUserActivity
- ~~Display list of users from database~~ **DONE!**
- ~~Add ability to create users~~ **DONE!**
- ~~Selecting a user takes you to ListActivity~~ **DONE!**
- ~~Proper username validation~~ **DONE!**
- Add ability to delete users (out-of-scope)

##### ListActivity
- ~~Display List of Events from database~~ **DONE!**
- ~~Toggle User/Admin mode (Switch displayed events)~~ **DONE!**
- ~~24hr Time Toggle~~ **DONE!**
- ~~Add Event button takes you to AddEventAcitivity~~ **DONE!**
- ~~Selecting an Event takes you to ViewActivity~~ **DONE!**
- Add ability to sort events (out-of-scope)

##### ViewActivity
- ~~Display info about event dependent on if current user is the creator~~ **DONE!**
- ~~24hr Time Toggle~~ **DONE!**
- ~~Admin Mode~~ **DONE!**
  - ~~Display event times and users signed up at each timeslot from Signup table~~ **DONE!**
- ~~User Mode~~ **DONE!**
  - ~~Pull previous availability from Signup table~~ **DONE!**
  - ~~Display availability and timeslot boxes~~ **DONE!**
  
##### AddEventActivity
- ~~Date picking widget for the day of the year (restrict to only today + future days???)~~ **DONE!**
- ~~Gray out (disable) timeslots of times that have already passed if Today is chosen date? (out-of-scope)?~~ **DONE!**
- ~~Ability to select timeslots in which the event will occur~~ **DONE!**
- ~~24hr Time Toggle~~ **DONE!**
- ~~Hook into database~~ **DONE!**
