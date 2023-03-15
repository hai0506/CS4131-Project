package com.example.grades.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.example.grades.R;
import com.example.grades.Misc.SharedViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    SharedViewModel sharedViewModel;
    BottomNavigationView bottomNavigationView;
    LinearLayout noInternetLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_main);


        SharedPreferences preferences =  getSharedPreferences("my_preferences", MODE_PRIVATE);

        if(!preferences.getBoolean("onboarding_complete",false)) {
            Intent onboarding = new Intent(this, OnBoardingActivity.class);
            startActivity(onboarding);
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ImageButton imageButton = toolbar.findViewById(R.id.settingsBtn);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(MainActivity.this, new Point(500,200));

            }
        });

        noInternetLayout = findViewById(R.id.noInternetLayout);
        bottomNavigationView = findViewById(R.id.nav_view);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.setStations(new ArrayList<>());

        noInternetLayout.setVisibility(View.INVISIBLE);
        setRepeatingAsyncTask();


        try{
            FileInputStream fIn = openFileInput("windUnit.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));
            String line = reader.readLine();
            sharedViewModel.setWindUnit(line);
        }
        catch(IOException ioe){
            try {
                FileOutputStream fOut = openFileOutput("windUnit.txt", MODE_PRIVATE);
                OutputStreamWriter osw = new OutputStreamWriter(fOut);
                osw.write("m/s");
                osw.flush();
                osw.close();
                sharedViewModel.setWindUnit("m/s");
            } catch (Exception e) {}
        }
        try{
            FileInputStream fIn = openFileInput("tempUnit.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));
            String line = reader.readLine();
            sharedViewModel.setTempUnit(line);
        }
        catch(IOException ioe){
            try {
                FileOutputStream fOut = openFileOutput("tempUnit.txt", MODE_PRIVATE);
                OutputStreamWriter osw = new OutputStreamWriter(fOut);
                osw.write("C");
                osw.flush();
                osw.close();
                sharedViewModel.setTempUnit("C");
            } catch (Exception e) {}
        }
        NavHostFragment navHostFragment = (NavHostFragment)getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView,
                navHostFragment.getNavController());
        sharedViewModel.getInternet().observe(this, item -> {
            if(item){
                noInternetLayout.setVisibility(View.INVISIBLE);
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
            else {
                noInternetLayout.setVisibility(View.VISIBLE);
                bottomNavigationView.setVisibility(View.INVISIBLE);
                findViewById(R.id.retryBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mStartActivity = new Intent(MainActivity.this, MainActivity.class);
                        int mPendingIntentId = 123456;
                        PendingIntent mPendingIntent = PendingIntent.getActivity(MainActivity.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager)MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        System.exit(0);
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed(){
          new AlertDialog.Builder(this)
                  .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle("Exit Application")
        .setMessage("Are you sure you want to exit the app?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which) {
        finishAffinity();
    }

    })
            .setNegativeButton("No", null)
    .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    private void setRepeatingAsyncTask() {

        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            new MyTask().execute();
                            Log.d("debug","updated");
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };

        timer.schedule(task, 0, 300 * 1000);

    }


    private class MyTask extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String date = new SimpleDateFormat("yyyy-MM-dd'T'HH'%3A'mm'%3A00'").format(new Date());
                HttpURLConnection temperature_API = (HttpURLConnection) new URL("https://api.data.gov.sg/v1/environment/air-temperature?date_time=" + date).openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(temperature_API.getInputStream()));
                sharedViewModel.setTemperature(in.readLine());

                HttpURLConnection wind_API = (HttpURLConnection) new URL("https://api.data.gov.sg/v1/environment/wind-speed?date_time=" + date).openConnection();
                in = new BufferedReader(new InputStreamReader(wind_API.getInputStream()));
                sharedViewModel.setWind(in.readLine());

                HttpURLConnection weather_API = (HttpURLConnection) new URL("https://api.data.gov.sg/v1/environment/24-hour-weather-forecast?date_time=" + date).openConnection();
                in = new BufferedReader(new InputStreamReader(weather_API.getInputStream()));
                sharedViewModel.setWeather(in.readLine());

                HttpURLConnection humidity_API = (HttpURLConnection) new URL("https://api.data.gov.sg/v1/environment/relative-humidity?date_time=" + date).openConnection();
                in = new BufferedReader(new InputStreamReader(humidity_API.getInputStream()));
                sharedViewModel.setHumidity(in.readLine());

                HttpURLConnection PSI_API = (HttpURLConnection) new URL("https://api.data.gov.sg/v1/environment/psi?date_time=" + date).openConnection();
                in = new BufferedReader(new InputStreamReader(PSI_API.getInputStream()));
                sharedViewModel.setPsi(in.readLine());

                HttpURLConnection UV_API = (HttpURLConnection) new URL("https://api.data.gov.sg/v1/environment/uv-index?date_time=" + date).openConnection();
                in = new BufferedReader(new InputStreamReader(UV_API.getInputStream()));
                sharedViewModel.setUv(in.readLine());

                HttpURLConnection forecast_API = (HttpURLConnection) new URL("https://api.data.gov.sg/v1/environment/4-day-weather-forecast?date_time=" + date).openConnection();
                in = new BufferedReader(new InputStreamReader(forecast_API.getInputStream()));
                sharedViewModel.setForecast(in.readLine());
//                sharedViewModel.setForecast("{\"items\":[{\"update_timestamp\":\"2021-04-09T05:32:11+08:00\",\"timestamp\":\"2021-04-09T05:20:00+08:00\",\"forecasts\":[{\"temperature\":{\"low\":24,\"high\":34},\"date\":\"2021-04-10\",\"forecast\":\"Afternoon thundery showers.\",\"relative_humidity\":{\"low\":55,\"high\":95},\"wind\":{\"speed\":{\"low\":10,\"high\":20},\"direction\":\"N\"},\"timestamp\":\"2021-04-10T00:00:00+08:00\"},{\"temperature\":{\"low\":24,\"high\":34},\"date\":\"2021-04-11\",\"forecast\":\"Afternoon thundery showers.\",\"relative_humidity\":{\"low\":55,\"high\":95},\"wind\":{\"speed\":{\"low\":5,\"high\":15},\"direction\":\"VARIABLE\"},\"timestamp\":\"2021-04-11T00:00:00+08:00\"},{\"temperature\":{\"low\":24,\"high\":34},\"date\":\"2021-04-12\",\"forecast\":\"Afternoon thundery showers.\",\"relative_humidity\":{\"low\":55,\"high\":95},\"wind\":{\"speed\":{\"low\":5,\"high\":10},\"direction\":\"VARIABLE\"},\"timestamp\":\"2021-04-12T00:00:00+08:00\"},{\"temperature\":{\"low\":24,\"high\":34},\"date\":\"2021-04-13\",\"forecast\":\"Afternoon thundery showers.\",\"relative_humidity\":{\"low\":55,\"high\":90},\"wind\":{\"speed\":{\"low\":5,\"high\":10},\"direction\":\"VARIABLE\"},\"timestamp\":\"2021-04-13T00:00:00+08:00\"}]}],\"api_info\":{\"status\":\"healthy\"}}");
                sharedViewModel.setInternet(true);
            } catch (Exception e) {
                sharedViewModel.setInternet(false);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }


    private void showPopup(final Activity context, Point p) {

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_settings, viewGroup);
        Spinner spinner = layout.findViewById(R.id.unitSpinner);
        ToggleButton tempC = layout.findViewById(R.id.tempC);
        ToggleButton tempF = layout.findViewById(R.id.tempF);
        String[] categories = {"m/s", "km/h","ft/s","mi/h"};
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        try{
            FileInputStream fIn = openFileInput("windUnit.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));
            String line = reader.readLine();
            if(line.equals("m/s")) spinner.setSelection(0);
            else if(line.equals("km/h")) spinner.setSelection(1);
            else if(line.equals("ft/s")) spinner.setSelection(2);
            else spinner.setSelection(3);
        }
        catch(IOException ioe){ spinner.setSelection(0); }

        try{
            FileInputStream fIn = openFileInput("tempUnit.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));
            String line = reader.readLine();
            if(line.equals("C")) {tempC.setChecked(true); tempF.setChecked(false);}
            else{tempC.setChecked(false); tempF.setChecked(true);}

        }
        catch(IOException ioe){ }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try{
                    FileOutputStream fOut = openFileOutput("windUnit.txt", MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);
                    osw.write(categories[position]);
                    osw.flush();
                    osw.close();
                    sharedViewModel.setWindUnit(categories[position]);
                }
                catch (IOException ioe){

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tempC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    tempF.setChecked(false);
                    try{
                        FileOutputStream fOut = openFileOutput("tempUnit.txt", MODE_PRIVATE);
                        OutputStreamWriter osw = new OutputStreamWriter(fOut);
                        osw.write("C");
                        osw.flush();
                        osw.close();
                        sharedViewModel.setTempUnit("C");
                    }
                    catch (IOException ioe){ }
                } else {
                    tempF.setChecked(true);
                    try{
                        FileOutputStream fOut = openFileOutput("tempUnit.txt", MODE_PRIVATE);
                        OutputStreamWriter osw = new OutputStreamWriter(fOut);
                        osw.write("F");
                        osw.flush();
                        osw.close();
                        sharedViewModel.setTempUnit("F");
                    }
                    catch (IOException ioe){ }

                }
            }
        });
        tempF.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    tempC.setChecked(false);
                    try{
                        FileOutputStream fOut = openFileOutput("tempUnit.txt", MODE_PRIVATE);
                        OutputStreamWriter osw = new OutputStreamWriter(fOut);
                        osw.write("F");
                        osw.flush();
                        osw.close();
                        sharedViewModel.setTempUnit("F");
                    }
                    catch (IOException ioe){ }

                } else {
                    tempC.setChecked(true);
                    try{
                        FileOutputStream fOut = openFileOutput("tempUnit.txt", MODE_PRIVATE);
                        OutputStreamWriter osw = new OutputStreamWriter(fOut);
                        osw.write("C");
                        osw.flush();
                        osw.close();
                        sharedViewModel.setTempUnit("C");
                    }
                    catch (IOException ioe){ }
                }
            }
        });
        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setFocusable(true);

        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.

        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x, p.y);

        // Getting a reference to Close button, and close the popup when clicked.
    }
}