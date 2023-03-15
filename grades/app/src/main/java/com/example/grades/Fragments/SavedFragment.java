package com.example.grades.Fragments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grades.Adapters.RecyclerAdapter;
import com.example.grades.Models.Pollutants;
import com.example.grades.Models.Station;
import com.example.grades.R;
import com.example.grades.Misc.SharedViewModel;
import com.example.grades.Misc.VerticalSpaceItemDecoration;

import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SavedFragment extends Fragment {

    SharedViewModel sharedViewModel;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerAdapter adapter;
    private ArrayList<Station> stations = new ArrayList<>();
    private ArrayList<Station> saved = new ArrayList<>();
    private String temperature="", wind="", humidity="", weather="",psi="",uv="";
    private Long UVIndex;
    String windUnit="m/s";
    String tempUnit="C";
    TextView none1,none2;

    public static SavedFragment newInstance() {
        return new SavedFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_saved, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        none1 = getView().findViewById(R.id.noneText1);
        none2 = getView().findViewById(R.id.noneText2);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getTemperature().observe(getViewLifecycleOwner(),item -> {
            try {
                temperature = item;
                loadStations(temperature,wind,weather,humidity,psi,uv);
            } catch (Exception e) { e.printStackTrace(); }
            changeRecyclerView(windUnit,tempUnit);
        });
        sharedViewModel.getHumidity().observe(getViewLifecycleOwner(),item -> {
            try {
                humidity = item;
                loadStations(temperature,wind,weather,humidity,psi,uv);
            } catch (Exception e) {e.printStackTrace(); }
            changeRecyclerView(windUnit,tempUnit);
        });
        sharedViewModel.getWeather().observe(getViewLifecycleOwner(),item -> {
            try {
                weather = item;
                loadStations(temperature,wind,weather,humidity,psi,uv);
            } catch (Exception e) {  e.printStackTrace();}
            changeRecyclerView(windUnit,tempUnit);
        });
        sharedViewModel.getWind().observe(getViewLifecycleOwner(),item -> {
            try {
                wind = item;
                loadStations(temperature,wind,weather,humidity,psi,uv);
            } catch (Exception e) { e.printStackTrace();}
            changeRecyclerView(windUnit,tempUnit);
        });
        sharedViewModel.getPsi().observe(getViewLifecycleOwner(),item -> {
            try {
                psi = item;
                loadStations(temperature,wind,weather,humidity,psi,uv);
            } catch (Exception e) {e.printStackTrace(); }
            changeRecyclerView(windUnit,tempUnit);
        });
        sharedViewModel.getUv().observe(getViewLifecycleOwner(),item -> {
            try {
                uv = item;
                loadStations(temperature,wind,weather,humidity,psi,uv);
            } catch (Exception e) {e.printStackTrace();}
            changeRecyclerView(windUnit,tempUnit);
        });
        sharedViewModel.getWindUnit().observe(getViewLifecycleOwner(),item -> {
            windUnit = item;
            changeRecyclerView(windUnit,tempUnit);
        });
        sharedViewModel.getTempUnit().observe(getViewLifecycleOwner(),item -> {
            tempUnit = item;
            changeRecyclerView(windUnit,tempUnit);
        });
    }
    public void changeRecyclerView(String windUnit,String tempUnit) {
        recyclerView = getView().findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(saved,windUnit,tempUnit);
        recyclerView.setAdapter(adapter);
        VerticalSpaceItemDecoration decor = new VerticalSpaceItemDecoration(20);
        if(recyclerView.getItemDecorationCount() !=0) recyclerView.removeItemDecorationAt(0);
        recyclerView.addItemDecoration(decor);
    }
    public void loadStations(String temp, String wind, String weather, String humidity, String psi, String uv) throws Exception {
        String name;
        String id;
        double latitude, longitude;
        JSONParser jsonParser = new JSONParser();
        stations.clear();

        org.json.simple.JSONObject uvObj = ( org.json.simple.JSONObject) jsonParser.parse(uv);
        org.json.simple.JSONObject items6 = (org.json.simple.JSONObject) ((org.json.simple.JSONArray) uvObj.get("items")).get(0);
        org.json.simple.JSONArray index = (org.json.simple.JSONArray) items6.get("index");
        for(int i = 0; i < index.size(); i++) {
            org.json.simple.JSONObject value = (org.json.simple.JSONObject) index.get(i);
            String timeStamp = (String) value.get("timestamp");
            if(timeStamp.substring(11,13).equals(new SimpleDateFormat("HH").format(new Date()))) {
                UVIndex = (Long) value.get("value");
            }
        }

        org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) jsonParser.parse(temp);
        org.json.simple.JSONObject stationData = (org.json.simple.JSONObject) jsonObject.get("metadata");
        org.json.simple.JSONArray s = (org.json.simple.JSONArray) stationData.get("stations");
        for (int i = 0; i < s.size(); i++) {
            org.json.simple.JSONObject obj = (org.json.simple.JSONObject) s.get(i);
            id = (String) obj.get("device_id");
            name = (String) obj.get("name");
            latitude = (Double) ((org.json.simple.JSONObject) obj.get("location")).get("latitude");
            longitude = (Double) ((org.json.simple.JSONObject) obj.get("location")).get("longitude");
            org.json.simple.JSONObject items = (org.json.simple.JSONObject) ((org.json.simple.JSONArray) jsonObject.get("items")).get(0);
            org.json.simple.JSONArray readings = (org.json.simple.JSONArray) items.get("readings");
            for (int j = 0; j < readings.size(); j++) {
                org.json.simple.JSONObject reading = (org.json.simple.JSONObject) readings.get(j);
                if (reading.get("station_id").equals(id)) {
                    if (reading.get("value") instanceof Double)
                        stations.add(new Station(name, id, (Double) reading.get("value"), longitude, latitude));
                    else if (reading.get("value") instanceof Long)
                        stations.add(new Station(name, id, ((Long) reading.get("value")).doubleValue(), longitude, latitude));
                }
            }
        }
        org.json.simple.JSONObject jsonObject2 = (org.json.simple.JSONObject) jsonParser.parse(wind);
        org.json.simple.JSONObject items = (org.json.simple.JSONObject) ((org.json.simple.JSONArray) jsonObject2.get("items")).get(0);
        org.json.simple.JSONArray readings = (org.json.simple.JSONArray) (items.get("readings"));
        for (int i = 0; i < readings.size(); i++) {
            org.json.simple.JSONObject obj = (org.json.simple.JSONObject) readings.get(i);
            for (Station station : stations) {
                if (station.getId().equals(obj.get("station_id"))) {
                    if (obj.get("value") instanceof Double)
                        station.setWindSpeed((Double) obj.get("value"));
                    else if (obj.get("value") instanceof Long)
                        station.setWindSpeed(((Long) obj.get("value")).doubleValue());
                }
            }
        }
        org.json.simple.JSONObject jsonObject3 = (org.json.simple.JSONObject) jsonParser.parse(weather);
        org.json.simple.JSONObject weatherItems = (org.json.simple.JSONObject) ((org.json.simple.JSONArray) jsonObject3.get("items")).get(0);
        org.json.simple.JSONObject weathers = (org.json.simple.JSONObject) ((org.json.simple.JSONArray) weatherItems.get("periods")).get(0);
        org.json.simple.JSONObject data = (org.json.simple.JSONObject) weathers.get("regions");
        for (Station station : stations) {
            station.setWeather((String) data.get(station.getRegion()));
        }
        org.json.simple.JSONObject jsonObject4 = (org.json.simple.JSONObject) jsonParser.parse(humidity);
        org.json.simple.JSONObject items4 = (org.json.simple.JSONObject) ((org.json.simple.JSONArray) jsonObject4.get("items")).get(0);
        org.json.simple.JSONArray readings4 = (org.json.simple.JSONArray) (items4.get("readings"));
        for (int i = 0; i < readings4.size(); i++) {
            org.json.simple.JSONObject obj = (org.json.simple.JSONObject) readings4.get(i);
            for (Station station : stations) {
                if (station.getId().equals(obj.get("station_id"))) {
                    if (obj.get("value") instanceof Double)
                        station.setHumidity((Double) obj.get("value"));
                    else if (obj.get("value") instanceof Long)
                        station.setHumidity(((Long) obj.get("value")).doubleValue());
                }
            }
        }
        JSONParser jsonParser5 = new JSONParser();
        org.json.simple.JSONObject jsonObject5 = (org.json.simple.JSONObject) jsonParser5.parse(psi);
        org.json.simple.JSONObject items5 = (org.json.simple.JSONObject) ((org.json.simple.JSONArray) jsonObject5.get("items")).get(0);
        org.json.simple.JSONObject readings5 = (org.json.simple.JSONObject) items5.get("readings");
        org.json.simple.JSONObject o3 = (org.json.simple.JSONObject) readings5.get("o3_sub_index");
        org.json.simple.JSONObject pm10 = (org.json.simple.JSONObject) readings5.get("pm10_twenty_four_hourly");
        org.json.simple.JSONObject co = (org.json.simple.JSONObject) readings5.get("co_sub_index");
        org.json.simple.JSONObject pm25 = (org.json.simple.JSONObject) readings5.get("pm25_twenty_four_hourly");
        org.json.simple.JSONObject no2 = (org.json.simple.JSONObject) readings5.get("no2_one_hour_max");
        org.json.simple.JSONObject so2 = (org.json.simple.JSONObject) readings5.get("so2_twenty_four_hourly");
        org.json.simple.JSONObject avg = (org.json.simple.JSONObject) readings5.get("psi_twenty_four_hourly");
        org.json.simple.JSONObject[] pollutants = {pm10, co, pm25, so2, no2, avg, o3};
        String[] names = {"north", "south", "east", "west", "central"};
        for (int i = 0; i < names.length; i++) {
            double[] values = new double[7];
            for (int j = 0; j < pollutants.length; j++) {
                if (pollutants[j].get(names[i]) instanceof Double)
                    values[j] = (Double) pollutants[j].get(names[i]);
                else if (pollutants[j].get(names[i]) instanceof Long)
                    values[j] = ((Long) pollutants[j].get(names[i])).doubleValue();
            }
            Pollutants p =  new Pollutants(names[i], values[0], values[1], values[2], values[3], values[4], values[5], values[6]);
            for (Station station : stations) {
                if(station.getRegion().equals(p.getName())) {
                    station.setPsi(p);
                }
            }
        }
        sharedViewModel.setStations(stations);
        loadSaved();
    }
    public void loadSaved() {
        saved.clear();
        try{
            FileInputStream fIn = getActivity().openFileInput("saved.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));
            String line = "";
            while((line = reader.readLine()) != null){
                for(Station s2 : stations) {
                    if(line.equals(s2.getName())) {
                        saved.add(s2);
                    }
                }
            }
            if(saved.isEmpty()) {
                none1.setVisibility(View.VISIBLE);
                none2.setVisibility(View.VISIBLE);
            }else { none1.setVisibility(View.INVISIBLE);
                none2.setVisibility(View.INVISIBLE);}
            changeRecyclerView(windUnit,tempUnit); }
        catch(IOException ioe){
            Log.d("failed to read file",ioe.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSaved();
    }
}