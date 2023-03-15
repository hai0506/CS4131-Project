package com.example.grades.Misc;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.navigation.NavDeepLinkBuilder;

import com.example.grades.Activities.MainActivity;
import com.example.grades.Models.Coordinate;
import com.example.grades.R;

import org.json.simple.parser.JSONParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ForegroundService extends Service {
    GPSLocationListener gpsLocationListener;
    LocationManager locationManager;
    private static Timer timer;
    long seconds=0;

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        timer.scheduleAtFixedRate(new UpdateTimeTask(), 0, 1000);
    }
    private class UpdateTimeTask extends TimerTask
    {
        public void run()
        {
            seconds++;
            Intent intent = new Intent("service_result");
            intent.putExtra("seconds",seconds);
            sendBroadcast(intent);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showNotification();
        getLocation(intent.getStringExtra("psi"),intent.getStringExtra("uv"));
        return START_STICKY;
    }

    private void showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "notificationID",
                    "Location Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
        PendingIntent pendingIntent = new NavDeepLinkBuilder(getApplicationContext())
                .setComponentName(MainActivity.class)
                     .setGraph(R.navigation.mobile_navigation)
                .setDestination(R.id.nav_track)
                .createPendingIntent();

        Notification notification = new NotificationCompat.Builder(this, "notificationID")
                .setContentTitle("Tracking exposure to pollutants and UV")
                .setSmallIcon(R.drawable.splash_icon)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(544, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent("service_result");
        Bundle b = new Bundle();
        b.putSerializable("result",gpsLocationListener.getData());
        b.putSerializable("result2",gpsLocationListener.getData2());
        intent.putExtras(b);
        sendBroadcast(intent);
        locationManager.removeUpdates(gpsLocationListener);
        timer.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case if services are bound (Bound Services).
        return null;
    }

    public void getLocation(String psi,String uv) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gpsLocationListener = new GPSLocationListener(psi,uv);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000, 0, gpsLocationListener);
    }
    private class GPSLocationListener implements LocationListener {
        String psi="",uv="";
        ArrayList<Double> data = new ArrayList<>();
        ArrayList<Long> data2 = new ArrayList<>();
        public GPSLocationListener(String s, String s2) {
            psi = s;
            uv = s2;
            data.clear();
            data2.clear();
        }

        @Override
        public void onLocationChanged(Location location) {
            Coordinate coordinate = new Coordinate(location.getLatitude(),location.getLongitude());
            String region="";
            double distFromN = coordinate.calcDist(new Coordinate(1.41803,103.82));
            double distFromS = coordinate.calcDist(new Coordinate(1.29587,103.82));
            double distFromW = coordinate.calcDist(new Coordinate(1.35735,103.7));
            double distFromE = coordinate.calcDist(new Coordinate(1.35735,103.94));
            double distFromC = coordinate.calcDist(new Coordinate(1.35735,103.82));
            double[] dist = {distFromN,distFromS,distFromC,distFromE,distFromW};
            Arrays.sort(dist);
            if(dist[0] == distFromC) region = "central";
            if(dist[0] == distFromN) region = "north";
            if(dist[0] == distFromS) region = "south";
            if(dist[0] == distFromW) region = "west";
            if(dist[0] == distFromE) region = "east";
            try {
                JSONParser jsonParser5 = new JSONParser();
                org.json.simple.JSONObject jsonObject5 = (org.json.simple.JSONObject) jsonParser5.parse(psi);
                org.json.simple.JSONObject items5 = (org.json.simple.JSONObject) ((org.json.simple.JSONArray) jsonObject5.get("items")).get(0);
                org.json.simple.JSONObject readings5 = (org.json.simple.JSONObject) items5.get("readings");
                org.json.simple.JSONObject avg = (org.json.simple.JSONObject) readings5.get("psi_twenty_four_hourly");
                if (avg.get(region) instanceof Double)
                    data.add((Double) avg.get(region));
                else if (avg.get(region) instanceof Long)
                    data.add(((Long) avg.get(region)).doubleValue());

                org.json.simple.JSONObject uvObj = ( org.json.simple.JSONObject) jsonParser5.parse(uv);
                org.json.simple.JSONObject items6 = (org.json.simple.JSONObject) ((org.json.simple.JSONArray) uvObj.get("items")).get(0);
                org.json.simple.JSONArray index = (org.json.simple.JSONArray) items6.get("index");
                long UVIndex = 0;
                for(int i = 0; i < index.size(); i++) {
                    org.json.simple.JSONObject value = (org.json.simple.JSONObject) index.get(i);
                    String timeStamp = (String) value.get("timestamp");
                    if(timeStamp.substring(11,13).equals(new SimpleDateFormat("HH").format(new Date()))) {
                        UVIndex = (Long) value.get("value");
                    }
                }
                data2.add(UVIndex);
            } catch (Exception e) {}
            
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("debug", "onStatusChanged: " + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        public ArrayList<Double> getData() {
            return data;
        }
        public ArrayList<Long> getData2() {
            return data2;
        }
    }
}
