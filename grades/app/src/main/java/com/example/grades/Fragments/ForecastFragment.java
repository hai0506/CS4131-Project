package com.example.grades.Fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.grades.Adapters.RecyclerAdapter2;
import com.example.grades.Models.Forecast;
import com.example.grades.R;
import com.example.grades.Misc.SharedViewModel;
import com.example.grades.Misc.VerticalSpaceItemDecoration;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class ForecastFragment extends Fragment {
    SharedViewModel sharedViewModel;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerAdapter2 adapter;
    ArrayList<Forecast> list = new ArrayList<>();
    String tempUnit = "C";
    TextView dateTV, tempTV, weatherTV;
    String data="",data2="";
    ImageButton imageButton;
    LinearLayout l;

    public static ForecastFragment newInstance() {
        return new ForecastFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.forecast_fragment, container, false);
        return v;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getForecast().observe(getViewLifecycleOwner(), item -> {
            data = item;
            loadList(item,data2);
        });
        sharedViewModel.getWeather().observe(getViewLifecycleOwner(), item -> {
            data2 = item;
            loadList(data,item);
        });
        sharedViewModel.getTempUnit().observe(getViewLifecycleOwner(), item -> {
            tempUnit = item;
            loadList(data,data2);
            recyclerView = getView().findViewById(R.id.recyclerView);
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            VerticalSpaceItemDecoration decor = new VerticalSpaceItemDecoration(10);
            if(recyclerView.getItemDecorationCount() !=0) recyclerView.removeItemDecorationAt(0);
            recyclerView.addItemDecoration(decor);
            adapter = new RecyclerAdapter2(list, tempUnit);
            recyclerView.setAdapter(adapter);
        });
        dateTV = getView().findViewById(R.id.dateTV);
        tempTV = getView().findViewById(R.id.tempTV);
        weatherTV = getView().findViewById(R.id.weatherTV);
        imageButton = getView().findViewById(R.id.imageButton);
        l = getView().findViewById(R.id.l2);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the popup_layout.xml
                LinearLayout viewGroup = getActivity().findViewById(R.id.popup);
                LayoutInflater layoutInflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = layoutInflater.inflate(R.layout.popup_settings, viewGroup);
                Spinner spinner = layout.findViewById(R.id.unitSpinner);
                ToggleButton tempC = layout.findViewById(R.id.tempC);
                ToggleButton tempF = layout.findViewById(R.id.tempF);
                String[] categories = {"m/s", "km/h","ft/s","mi/h"};
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(dataAdapter);

                try{
                    FileInputStream fIn = getActivity().openFileInput("windUnit.txt");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));
                    String line = reader.readLine();
                    if(line.equals("m/s")) spinner.setSelection(0);
                    else if(line.equals("km/h")) spinner.setSelection(1);
                    else if(line.equals("ft/s")) spinner.setSelection(2);
                    else spinner.setSelection(3);
                }
                catch(IOException ioe){ spinner.setSelection(0); }

                try{
                    FileInputStream fIn = getActivity().openFileInput("tempUnit.txt");
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
                            FileOutputStream fOut = getActivity(). openFileOutput("windUnit.txt", MODE_PRIVATE);
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
                                FileOutputStream fOut = getActivity().openFileOutput("tempUnit.txt", MODE_PRIVATE);
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
                                FileOutputStream fOut = getActivity().openFileOutput("tempUnit.txt", MODE_PRIVATE);
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
                                FileOutputStream fOut = getActivity().openFileOutput("tempUnit.txt", MODE_PRIVATE);
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
                                FileOutputStream fOut = getActivity().openFileOutput("tempUnit.txt", MODE_PRIVATE);
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
                final PopupWindow popup = new PopupWindow(getContext());
                popup.setContentView(layout);
                popup.setFocusable(true);

                // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.

                // Clear the default translucent background
                popup.setBackgroundDrawable(new BitmapDrawable());

                // Displaying the popup at the specified location, + offsets.
                popup.showAtLocation(layout, Gravity.NO_GRAVITY, 500,200);

            }
        });
    }

    public void loadList(String data,String data2) {
        if(data.isEmpty() || data2.isEmpty()) {return;}
        else {
            list.clear();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(data);
                JSONObject items = (JSONObject) ((JSONArray) jsonObject.get("items")).get(0);
                JSONArray forecastsItems = (JSONArray) items.get("forecasts");
                for (int i = 0; i < 4; i++) {
                    JSONObject obj = (JSONObject) forecastsItems.get(i);
                    Date d = sdf.parse((String) obj.get("date"));
                    JSONObject temp = (JSONObject) obj.get("temperature");
                    Long lowT = (Long) temp.get("low");
                    Long highT = (Long) temp.get("high");
                    String weather = (String) (obj.get("forecast"));
                    list.add(new Forecast(d, lowT, highT, weather));
                }

                JSONObject jsonObject2 = (JSONObject) jsonParser.parse(data2);
                JSONObject items2 = (JSONObject) ((JSONArray) jsonObject2.get("items")).get(0);
                JSONObject general = (JSONObject) items2.get("general");
                JSONObject temp2 = (JSONObject) general.get("temperature");
                Long lowT2 = (Long) temp2.get("low");
                Long highT2 = (Long) temp2.get("high");
                String weather2 = (String) (general.get("forecast"));
                Forecast f = new Forecast(new Date(), lowT2, highT2, weather2);
                dateTV.setText("Today");
                if (tempUnit.equals("C"))
                    tempTV.setText(f.getLowTemp() + " - " + f.getHighTemp() + " °C");
                else
                    tempTV.setText(String.format("%.1f", f.getLowTempF()) + " - " + String.format("%.1f", f.getHighTempF()) + " °F");
                weatherTV.setText(f.getWeather());
                dateTV.setVisibility(View.VISIBLE);
                l.setVisibility(View.VISIBLE);
                imageButton.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                dateTV.setVisibility(View.INVISIBLE);
                l.setVisibility(View.INVISIBLE);
                imageButton.setVisibility(View.INVISIBLE);
                tempTV.setText("\n\n\nSorry!");
                weatherTV.setText("The data source is currently not working");
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

}