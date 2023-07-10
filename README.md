# MINIGAIA: Automated Hidropony
This is the android app created for an automated hidropony project called MINIGAIA.
The final results can be seen on this link:

https://youtu.be/AtXSfCeaiyI

# App Summary
This app is an activity based app made on Android Studio for the android OS. It
uses bluetooth to communicate whith an ESP32 microcontroller, which handles an
entire automated domestic hidropony system.

## App Functionality
These are all the things that the user can do with this app:
   - Visulize hidropony data: pH level, humidity, temperature and water level;
   - Set desired pH level;
   - Set desired measuring times (each being 12h apart from each other);
   - Tell the system to make immediate measurements;
   - Sychronize ESP32 with new desired values.

## System Requirements to use source code
To open this code on your IDE, you'll need these settings:
   - AndroidStudio version: Flamingo (latest version)
   - Gradle version: 8.0.x
   - OpenJDK version: 17.0.x

"x" being any number in this main version.

# Classes Functionalities
## SplashActivity
Activity for the fade out intro of the app. It calls the main activity afterwards.

## MainActivity
This is the body of the main screen. Here you can do all of the app's functionalities
listed on the App Functionality section.

## TimesActivity
Activity used for setting the desired measuring times, whith it's own screen.

## Bluetooth
Class that handles all things regarding the bluetooth connection. It searches for
an already paired device with the name "minigaia" in it. If found, it'll try to
connect with this device.

## Memory
Class that handles object persistence on the app. All information is saved in an
local directory, to be later read when opening the app.

## SensorData
Class that holds all data related to the system sensors. It includes pH level,
desired pH level, temperature, humidity, water level and the first desired measure
time, the second one being 12h apart from it.

## Secondary classes
   - First and Second Fragments: classes used in graph oriented apps. It isn't
   really used in the app, besides the FirstFragment that holds some UI info.
   - WebServer: a communication method also implemented in case the bluetooth were
   not to work. Since it worked, this class was left behind, though being fully functional.