package com.example.grades.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grades.Models.Forecast;
import com.example.grades.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RecyclerAdapter2 extends RecyclerView.Adapter<RecyclerAdapter2.ViewHolder> {
    ArrayList<Forecast> data;
    String tempUnit;

    public RecyclerAdapter2(ArrayList<Forecast> data, String s2) {
        this.data = data;
        tempUnit = s2;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout2,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter2.ViewHolder holder, int position) {
        Forecast f = data.get(position);
        if(position == 0)holder.name.setText("Tomorrow");
        else holder.name.setText(new SimpleDateFormat("EE").format(f.getDate()));
        if(tempUnit.equals("C")) holder.temp.setText(f.getLowTemp()+" - "+f.getHighTemp()+" °C");
        else holder.temp.setText(String.format("%.1f",f.getLowTempF())+" - "+String.format("%.1f",f.getHighTempF())+" °F");
        holder.weather.setText(f.getWeather());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,temp,weather;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameTV);
            temp = itemView.findViewById(R.id.tempTV);
            weather = itemView.findViewById(R.id.weatherTV);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
