package com.example.grades.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grades.Activities.DisplayActivity;
import com.example.grades.Models.Station;
import com.example.grades.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private ArrayList<Station> list;
    String windUnit,tempUnit;

    public RecyclerAdapter(ArrayList<Station> list, String s, String s2) {
        this.list = list;
        windUnit = s;
        tempUnit = s2;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        Station station = list.get(position);
        holder.name.setText(station.getName());
        if(windUnit.equals("m/s")) holder.wind.setText(station.getWindSpeed()+" m/s");
        else if(windUnit.equals("km/h")) holder.wind.setText(String.format("%.1f",station.getWindSpeedkmh())+" km/h");
        else if(windUnit.equals("ft/s")) holder.wind.setText(String.format("%.1f",station.getWindSpeedfts())+" ft/s");
        else holder.wind.setText(String.format("%.1f",station.getWindSpeedmih())+" mi/h");

        if(tempUnit.equals("C")) holder.temp.setText(station.getTemperature()+" °C");
        else holder.temp.setText(String.format("%.1f",station.getFahrenheit())+" °F");
        holder.psi.setText(""+ station.getPsi().getPsi());
        holder.weather.setText(station.getWeather().toString());
        holder.humid.setText(station.getHumidity()+" %");
        holder.uv.setText(""+station.getUv());
        if(station.getPsi().getPsi() <= 50) { holder.psiView.setBackgroundColor(Color.parseColor("#00FF00")); }
        else if (station.getPsi().getPsi() <= 100) { holder.psiView.setBackgroundColor(Color.parseColor("#FFFF00")); }
        else if (station.getPsi().getPsi() <= 150) { holder.psiView.setBackgroundColor(Color.parseColor("#FF8000")); }
        else if (station.getPsi().getPsi() <= 200) { holder.psiView.setBackgroundColor(Color.parseColor("#FF0000")); }
        else if (station.getPsi().getPsi() <= 300) { holder.psiView.setBackgroundColor(Color.parseColor("#6A0888")); }
        else { holder.psiView.setBackgroundColor(Color.parseColor("#000000")); }

        if(station.getUv() <= 2) { holder.uvView.setBackgroundColor(Color.parseColor("#00FF00")); }
        else if (station.getUv() <= 5) { holder.uvView.setBackgroundColor(Color.parseColor("#FFFF00")); }
        else if (station.getUv() <= 7) { holder.uvView.setBackgroundColor(Color.parseColor("#FF8000")); }
        else if (station.getUv() <= 10) { holder.uvView.setBackgroundColor(Color.parseColor("#FF0000")); }
        else { holder.uvView.setBackgroundColor(Color.parseColor("#6A0888")); }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,temp,wind,humid,weather,psi,uv;
        LinearLayout psiView,uvView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameTV);
            temp = itemView.findViewById(R.id.tempTV);
            weather = itemView.findViewById(R.id.weatherTV);
            wind = itemView.findViewById(R.id.windTV);
            psi = itemView.findViewById(R.id.psiTV);
            humid = itemView.findViewById(R.id.humidTV);
            uv = itemView.findViewById(R.id.uvTV);
            psiView = itemView.findViewById(R.id.view);
            uvView = itemView.findViewById(R.id.view2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    int position = getAdapterPosition();
                    Intent intent = new Intent(context, DisplayActivity.class);
                    intent.putExtra("list",list);
                    intent.putExtra("pos",position);
                    intent.putExtra("type","remove");
                    context.startActivity(intent);
                }
            });
        }
    }
}
