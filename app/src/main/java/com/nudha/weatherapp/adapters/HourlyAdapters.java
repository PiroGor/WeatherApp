package com.nudha.weatherapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nudha.weatherapp.R;
import com.nudha.weatherapp.domains.HourlyDomain;

import java.util.ArrayList;

public class HourlyAdapters extends RecyclerView.Adapter<HourlyAdapters.viewHolder> {
    private ArrayList<HourlyDomain> items;
    private Context context;

    public HourlyAdapters(ArrayList<HourlyDomain> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_hourly, parent, false);
        context = parent.getContext();
        return new viewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.hourTxt.setText(items.get(position).getHour());
        holder.tempTxt.setText(items.get(position).getTemp() + "°");

        int drawableResourceId = holder.itemView.getContext().getResources()
                .getIdentifier(items.get(position).getPicPath(), "drawable", holder.itemView.getContext().getPackageName());

        Glide.with(context)
                .load(drawableResourceId)
                .into(holder.pic);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Новый метод для обновления данных адаптера
    public void updateData(ArrayList<HourlyDomain> newItems) {
        this.items.clear();           // Очищаем текущий список
        this.items.addAll(newItems);   // Добавляем новые данные
        notifyDataSetChanged();        // Уведомляем адаптер об изменении данных
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView hourTxt, tempTxt;
        ImageView pic;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            hourTxt = itemView.findViewById(R.id.hourTxt);
            tempTxt = itemView.findViewById(R.id.tempTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}
