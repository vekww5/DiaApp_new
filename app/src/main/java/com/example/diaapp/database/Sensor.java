package com.example.diaapp.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user_id"))
public class Sensor
{
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "glucose")
    public float glucose;

    @ColumnInfo(name = "date_time")
    public long timestamp;

    @ColumnInfo(name = "user_id")
    public int userId;

    public Sensor(int id, float glucose, long timestamp, int userId) {
        this.id = id;
        this.glucose = glucose;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getGlucose() {
        return glucose;
    }

    public void setGlucose(float glucose) {
        this.glucose = glucose;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
