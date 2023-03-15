package com.example.grades.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grades.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class RecyclerAdapter3 extends RecyclerView.Adapter<RecyclerAdapter3.ViewHolder> {
    ArrayList<Integer> images;
    ArrayList<Integer> colors;
    ArrayList<String> advice;
    onItemClickListener onItemClickListener;

    public RecyclerAdapter3(ArrayList<Integer> images,ArrayList<Integer> colors, ArrayList<String> advice) {
        this.images = images;
        this.colors = colors;
        this.advice = advice;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout3,parent,false);
        return new ViewHolder(v);
    }
    public void setOnItemClickListener(RecyclerAdapter3.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter3.ViewHolder holder, int position) {
        holder.imageView.setImageResource(images.get(position));
        holder.cardView.setStrokeColor(colors.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onClick(advice.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public interface onItemClickListener{
        void onClick(String str);//pass your object types.
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        MaterialCardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}
