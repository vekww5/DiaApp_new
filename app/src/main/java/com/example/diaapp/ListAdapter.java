package com.example.diaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diaapp.database.RecordDIA;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private Context context;
    private List<RecordDIA> recordList;
    public ListAdapter(Context context) {
        this.context = context;
    }

    public void setUserList(List<RecordDIA> diaList) {
        this.recordList = diaList;
        //notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.diary_single_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecordDIA dia = this.recordList.get(position);

        holder.inject_short.setText(dia.getInjectShortString());
        holder.inject_long.setText(dia.getInjectLongString());
        holder.xe.setText(dia.getXeString());
        holder.glucose.setText(dia.getGlucoseMmolString());

        // Преобразование timestamp в объект Date
        Date date = new Date(dia.getTimestamp());

        // Создание объекта SimpleDateFormat для формата даты и времени
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        // Получение даты и времени отдельно
        String dateString = dateFormat.format(date);
        String timeString = timeFormat.format(date);

        holder.time.setText(timeString);
        holder.date.setText(dateString);
    }


    @Override
    public int getItemCount() {
        return this.recordList.size();
    }

    public RecordDIA getDiaAtPosition (int position) {
        return recordList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView inject_short, inject_long, xe, glucose, date, time;

        public ViewHolder(View view) {
            super(view);

            inject_short = itemView.findViewById(R.id.inject_short_text);
            inject_long = itemView.findViewById(R.id.inject_long_text);
            glucose = itemView.findViewById(R.id.glucose_text);
            xe = itemView.findViewById(R.id.xe_text);
            date = itemView.findViewById(R.id.text_view_date);
            time = itemView.findViewById(R.id.text_view_time);

        }
    }
}
