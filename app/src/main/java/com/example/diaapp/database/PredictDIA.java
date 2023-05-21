package com.example.diaapp.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "PredictDIA")
public class PredictDIA {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "glucose")
    public float glucose;

    @ColumnInfo(name = "date_time")
    public long timestamp;
}
