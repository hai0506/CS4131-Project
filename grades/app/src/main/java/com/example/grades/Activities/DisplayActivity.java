 package com.example.grades.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grades.Models.Station;
import com.example.grades.R;
import com.example.grades.Adapters.RecyclerAdapter3;
import com.example.grades.Misc.SharedViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class DisplayActivity extends FragmentActivity implements OnMapReadyCallback {

    AppBarLayout mAppBarLayout;
    private GoogleMap mMap;
    Station station;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerAdapter3 adapter;

    TextView name, temp, wind, humid, co, so2, no2, pm25,pm10,o3, uv, adviceTV;
    Button editSavedBtn;
    SharedViewModel sharedViewModel;
    ImageView coInfo,so2Info,no2Info,pm25Info,pm10Info,o3Info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.theme));
        }
        setContentView(R.layout.activity_display);
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        Intent i = getIntent();
        station = (Station) i.getParcelableArrayListExtra("list").get(i.getIntExtra("pos",0));
        mAppBarLayout = findViewById(R.id.appbar_lay);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(AppBarLayout appBarLayout) {
                return false;
            }
        });
        params.setBehavior(behavior);
        FloatingActionButton fab = findViewById(R.id.backBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        name = findViewById(R.id.nameTV);
        temp = findViewById(R.id.tempTV);
        wind = findViewById(R.id.windTV);
        humid = findViewById(R.id.humidTV);
        co = findViewById(R.id.coTV);
        so2 = findViewById(R.id.so2TV);
        no2 = findViewById(R.id.no2TV);
        pm10 = findViewById(R.id.pm10TV);
        pm25 = findViewById(R.id.pm25TV);
        o3 = findViewById(R.id.o3TV);
        uv = findViewById(R.id.uvTV);
        adviceTV = findViewById(R.id.adviceTV);
        coInfo = findViewById(R.id.coInfo);
        coInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayActivity.this, InfoActivity.class);
                intent.putExtra("item","CO");
                startActivity(intent);
            }
        });
        no2Info = findViewById(R.id.no2Info);
        no2Info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayActivity.this, InfoActivity.class);
                intent.putExtra("item","NO2");
                startActivity(intent);
            }
        });
        pm25Info = findViewById(R.id.pm25Info);
        pm25Info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayActivity.this, InfoActivity.class);
                intent.putExtra("item","PM25");
                startActivity(intent);
            }
        });
        pm10Info = findViewById(R.id.pm10Info);
        pm10Info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayActivity.this, InfoActivity.class);
                intent.putExtra("item","PM10");
                startActivity(intent);
            }
        });
        o3Info = findViewById(R.id.o3Info);
        o3Info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayActivity.this, InfoActivity.class);
                intent.putExtra("item","O3");
                startActivity(intent);
            }
        });
        so2Info = findViewById(R.id.so2Info);
        so2Info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayActivity.this, InfoActivity.class);
                intent.putExtra("item","SO2");
                startActivity(intent);
            }
        });


        editSavedBtn = findViewById(R.id.editSavedBtn);
        if(i.getStringExtra("type").equals("remove")) {
            editSavedBtn.setText("Remove from saved");
        } else {
            editSavedBtn.setText("Save location");
        }
        try {
            FileInputStream fIn = openFileInput("windUnit.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));
            String line = reader.readLine();
            if (line.equals("m/s")) wind.setText(station.getWindSpeed() + " m/s");
            else if (line.equals("km/h"))
                wind.setText(String.format("%.1f", station.getWindSpeedkmh()) + " km/h");
            else if (line.equals("ft/s"))
                wind.setText(String.format("%.1f", station.getWindSpeedfts()) + " ft/s");
            else wind.setText(String.format("%.1f", station.getWindSpeedmih()) + " mi/h");
        }
        catch(IOException ioe){wind.setText(station.getWindSpeed()+" m/s"); }
        try {
            FileInputStream fIn = openFileInput("tempUnit.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));
            String line = reader.readLine();
            if (line.equals("C")) temp.setText(station.getTemperature() + " °C");
            else temp.setText(String.format("%.1f",station.getFahrenheit()) + " °F");
        }
        catch(IOException ioe){temp.setText(station.getTemperature() + " °C"); }
        name.setText(station.getName());
        humid.setText(station.getHumidity()+" %");
        co.setText(station.getPsi().getCo()+" μg/g");
        so2.setText(station.getPsi().getSo2()+" μg/g");
        no2.setText(station.getPsi().getNo2()+" μg/g");
        pm25.setText(station.getPsi().getPm25()+" μg/g");
        pm10.setText(station.getPsi().getPm10()+" μg/g");
        o3.setText(station.getPsi().getO3()+" μg/g");

        recyclerView = findViewById(R.id.imageContainer);
        layoutManager = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<Integer> images = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        ArrayList<String> advice = new ArrayList<>();
        if(station.getPsi().getPsi() <= 50) {
            images.add(R.drawable.outside_icon);
            colors.add(getColor(R.color.green));
            advice.add("Enjoy outdoor activities");
        }
        else  if(station.getPsi().getPsi() <= 100) {
            images.add(R.drawable.outside_icon);
            colors.add(getColor(R.color.yellow));
            advice.add("Sensitive groups should reduce outdoor activities");
        }
        else if(station.getPsi().getPsi() <= 150) {
            images.add(R.drawable.outside_icon);
            colors.add(getColor(R.color.orange));
            advice.add("Reduce your time outdoors");
        }
        else if (station.getPsi().getPsi() <= 200) {
            images.add(R.drawable.outside_icon);
            images.add(R.drawable.mask_icon);
            colors.add(getColor(R.color.orange));
            colors.add(getColor(R.color.yellow));
            advice.add("Reduce your time outdoors");
            advice.add("Wear a mask outside");
        }
        else {
            images.add(R.drawable.outside_icon);
            images.add(R.drawable.mask_icon);
            colors.add(getColor(R.color.red));
            colors.add(getColor(R.color.yellow));
            advice.add("Avoid going outdoors");
            advice.add("Wear a mask outside");
        }

        if(station.getUv()<=2){
            uv.setText("UV Index: " + station.getUv()+" (Very Safe)");
            images.add(R.drawable.sun_icon);
            colors.add(getColor(R.color.green));
            advice.add("Safe UV level");
        }
        else if(station.getUv()<=5) {
            uv.setText("UV Index: " + station.getUv()+" (Moderately Safe)");
            images.add(R.drawable.sun_icon);
            colors.add(getColor(R.color.yellow));
            advice.add("Avoid being too long in direct sunlight");
        }
        else if(station.getUv()<=7) {
            uv.setText("UV Index: " + station.getUv() + " (Harmful)");
            images.add(R.drawable.sun_icon);
            images.add(R.drawable.sunscreen_icon);
            colors.add(getColor(R.color.yellow));
            colors.add(getColor(R.color.yellow));
            advice.add("Avoid being too long in direct sunlight");
            advice.add("Apply sunscreen outside");
        }
        else {
            uv.setText("UV Index: " + station.getUv()+" (Very Dangerous)");
            images.add(R.drawable.sun_icon);
            images.add(R.drawable.sunscreen_icon);
            colors.add(getColor(R.color.red));
            colors.add(getColor(R.color.yellow));
            advice.add("Avoid direct sunlight");
            advice.add("Apply sunscreen outside");
        }
        adapter = new RecyclerAdapter3(images,colors,advice);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(new RecyclerAdapter3.onItemClickListener() {
            @Override
            public void onClick(String str) {
                adviceTV.setText(str);
            }
        });

        editSavedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editSavedBtn.getText().equals("Save location")) {
                    try{
                        FileOutputStream fOut = openFileOutput("saved.txt", MODE_APPEND);
                        OutputStreamWriter osw = new OutputStreamWriter(fOut);
                        osw.write(station.getName()+"\n");
                        osw.flush();
                        osw.close();
                        Toast.makeText(getApplicationContext(),"Location saved successfully",Toast.LENGTH_SHORT).show();
                    }
                    catch (IOException ioe){

                    }
                }
                else {
                    ArrayList<Station> list = new ArrayList<>();
                    for(Object s2 : i.getParcelableArrayListExtra("list")) {
                        list.add((Station)s2);
                    }
                    list.remove(station);
                    try{
                        FileOutputStream fOut = openFileOutput("saved.txt", MODE_PRIVATE);
                        OutputStreamWriter osw = new OutputStreamWriter(fOut);
                        osw.write("");
                        osw.flush();
                        osw.close();
                        FileOutputStream fOut2 = openFileOutput("saved.txt", MODE_APPEND);
                        OutputStreamWriter osw2 = new OutputStreamWriter(fOut2);
                        for(Station s : list) {
                            osw2.write(s.getName()+"\n");
                        }
                        osw2.flush();
                        osw2.close();
                        Toast.makeText(getApplicationContext(),"Location removed successfully",Toast.LENGTH_SHORT).show();
                    }
                    catch (IOException ioe){

                    }
                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(station.getCoordinate().getLat(),station.getCoordinate().getLon()) , 14.0f) );
        googleMap.addMarker(new MarkerOptions().position(new LatLng(station.getCoordinate().getLat(),station.getCoordinate().getLon())));

    }
}