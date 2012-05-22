8:07 PM 5/22/2012
Fixed THE bug in Scheduler. Add is Running Method.
TODO: re-scheduling adds new items in Alarm Manager queue instead of replacing an existing ones.

7:59 PM 4/15/2012
Resolved prev. Implemented launching/killing checking service at specified time (events are scheduled upon booting up)

8:40 PM 4/3/2012
Implemented Status Bar Notification, but Notification counter remains always 1.

5:53 PM 3/27/2012
Point #3 of prev. was not correct - Stop reporting already reported even is required -> Implemented.

9:05 PM 3/26/2012
1. Resolved Prev.
2. Implemented Bounds passing Check.
3. An attempt to stop reporting already reported Bound crossing implemented wrongly:
   MyBound.fired could help with it, but each TimerTask run() re-read all the Items 
   from DB and MyBound.fired is initialized to FALSE loosing just the value just set.
   Might not be required at all, when Notification will be implemented - Duplicated Notification just will not be fired.

7:51 PM 3/21/2012
1. Looping still does not work while reading data from File
2. Service has poor processing of erroneous data request completion.