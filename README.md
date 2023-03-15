# Project Report

Run app-debug.apk to run the app.

## Project overview

Mapp is an application that displays real-time environmental data consisting of weather, temperature,
wind speed, humidity, pollutant and UV index at various locations in Singapore. The purpose of this
application is to help the user carry out appropriate preparations to ensure health safety and
convenience outdoors.

## Features

- Data fetching from API

```
Data is read from API using HttpURLConnection , which runs in background using AsyncTask. In the
main thread, a TimerTask executes the AsyncTask every 5 minutes, which updates the data.
Retrieved data is passed to a MutableLiveData in a ViewModel. Fragments will observe this
ViewModel and get the JSON strings, which will then be processed using an external library called
json-simple.
```
private void setRepeatingAsyncTask **() {**

final Handler handler **= new** Handler **();**
Timer timer **= new** Timer **();**
TimerTask task **= new** TimerTask **() {**
@Override
public void run **() {**
handler**.** post **(new** Runnable **() {**
public void run **() {
try {
new** MyTask **().** execute **();
} catch (** Exception e **) {
}
}
});
}
};**
timer**.** schedule **(** task **,** 0 **,** 300 ***** 1000 **);
}**

private class MyTask **extends** AsyncTask **<** String **,** Void **,** String **> {**

protected void onPreExecute **() {
super.** onPreExecute **();
}**

@Override


protected String doInBackground **(** String **...** params **) {
try {**
String date **= new** SimpleDateFormat **(** "yyyy-MM-
dd'T'HH'%3A'mm'%3A00'" **).** format **(new** Date **());**
HttpURLConnection temperature_API **= (** HttpURLConnection **) new**
URL **(** "https://api.data.gov.sg/v1/environment/air-temperature?date_time=" **+**
date **).** openConnection **();**
BufferedReader in **= new** BufferedReader **(new**
InputStreamReader **(** temperature_API**.** getInputStream **()));**

## sharedViewModel. setTemperature ( in. readLine ());

- Find available locations

```
The API provides data at specific stations. These stations are marked on a map. When the user taps
on the label of the marker, they can view data of the station and can also add it to their Saved list.
The map is also zoomed into the user’s location, which is acquired using LocationManager.
```
- Observe saved stations

```
Saved stations have their data displayed in a card view. By tapping on the cards, the user can view
more detailed information as well as receive health advice with reference to the pollutant and UV
index at the location. Saved stations may also be removed from the Saved list.
```
- Learn about pollutants

```
User can find information about 6 types of air pollutants.
```

- Change temperature and wind speed unit

The settings icon on the action bar when pressed displays a pop-up which allows the user to change
the units of the data displayed. This is done by passing the chosen unit into MutableLiveData objects
and observing them in fragments.

- Weather forecast

The temperature and weather of this day and the next four days are
displayed. The settings icon shows the same pop-up as described above.

- Tracking exposure to pollutants and UV

This feature tracks the user’s exposure to pollutants and UV. When going
outside, the user can press a button which starts a Foreground Service
that runs in the background even when the app is closed. This service
retrieves the user’s current location and the local pollutants and UV index.
When the user stops the service, the average index will be calculated and
displayed. The app will also provide possible consequences of frequent
exposure to similar levels of pollutants and UV.

A notification appears when the service is running to alert the user.


- OnBoarding screen

```
Upon first time use, 4 fragments containing key features are displayed to the
user.
```
- No Internet connection

As data is retrieved from API, Internet connection is required for the app to
function. When there is no Internet connection, the user cannot access any
features of the app. The app can be reloaded by pressing the refresh icon.


## Usability survey

A total of 4 people used the app, 3 using my device and 1 using their own device. The results were:

- Found a bug where pressing the back button on the navigation bar takes the app back to its
    splash screen. This was fixed by overriding onBackPressed().
- Recommended a confirmation dialog when exiting the app using the back navigation button.
- Found a bug where the 4-day forecasts are not displayed on first-time use. The cause was that
    files containing data units were empty, and the problem was fixed.
- Discovered that the scaling in the Track fragment can hide some views on different devices.
    Fixed by adding a ScrollView.
- Recommended adding background images to the Forecast fragment.

General feedback was that the app was overall usable, however the user interface was not particularly
attractive.

## Reflection

- Obstacles faced

```
Development of the application was not too difficult nor stressful. The most challenging process was
UI designing, as I struggled finding the right colors, fonts, background images, as well as creative
ideas for decoration.
```
- Learning points

```
I learnt how to use services in Android apps. The project also helped me develop my skills in UI
designing.
```
- Future improvements

```
If more time was given, I would try to explore Android widgets. Widgets can be used to display data
from locations, or start the tracking service. I may also try improving my user interface.
```
- Improvements for the task

```
I do not have any feedbacks on this matter.
```
## Remarks

As of April 9th, the API for Weather Forecast still returned data normally. However, since 10 th^ April, it has
been returning an empty string.


To compensate for this, the following displays when the Forecast tab is opened.


