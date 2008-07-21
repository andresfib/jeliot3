Jeliot activity for moodle
--------------------------

By installing this module you can add Jeliot activities to your courses in Moodle. A Jeliot activity is composed of an introduction to the activity and a source file. Students will be presented with the introduction and a Web Start link to start Jeliot 3, which will load the source file from the moodle server.


Requirements
------------
* Clients should have a working Java Web Start installation.
* Server should be able to serve jnlp files properly.

Installing the activity
-----------------------

Tested in Moodle 1.8.2 (should work in newer versions as well)

1.- If logged in as admin in moodle, log out.
2.- Uncompress the file jeliot.zip to the moodle mod directory (e.g. /usr/share/moodle/mod in ubuntu)
3.- Log in as admin in moodle.
4.- In the admin panel, visit the "Notifications" tab. Tables will be created in your database.
5.- Now Jeliot Activity will be listed in the drop-down box of activities found when designing courses.
 