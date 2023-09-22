package com.example.diaapp.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

//@Entity(tableName = "Record")
@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user_id"))
public class Record {
    @PrimaryKey(autoGenerate = true)
    public int recordId;
    @ColumnInfo(name = "inject_long")
    public float injectLong;
    @ColumnInfo(name = "inject_short")
    public float injectShort;
    @ColumnInfo(name = "glucose")
    public float glucose;
    @ColumnInfo(name = "xe")
    public float xe;
    @ColumnInfo(name = "date_time")
    public long timestamp;

    @ColumnInfo(name = "user_id")
    public int userId;
    public Record(float injectLong, float injectShort, float glucose, float xe, long timestamp,
                  int userId) {
        this.injectLong = injectLong;
        this.injectShort = injectShort;
        this.glucose = glucose;
        this.xe = xe;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getSingDataId() {
        return userId;
    }

    public void setSingDataId(int singDataId) {
        this.userId = singDataId;
    }

    public float getInjectLong() {
        return injectLong;
    }

    public void setInjectLong(float injectLong) {
        this.injectLong = injectLong;
    }

    public String getInjectLongString() {
        return String.valueOf(injectLong);
    }

    public float getInjectShort() {
        return injectShort;
    }

    public void setInjectShort(float injectShort) {
        this.injectShort = injectShort;
    }

    public String getInjectShortString() {
        return String.valueOf(injectShort);
    }

    public float getGlucose() {
        return glucose;
    }

    public float getGlucoseMmol() {
        float glucoseMmol = glucose / 18.0f;
        return Math.round(glucoseMmol * 10) / 10f;
    }

    public String getGlucoseMmolString() {
        float glucoseMmol = glucose / 18.0f;
        return String.valueOf(Math.round(glucoseMmol * 10) / 10f);
    }

    public String getGlucoseString() {
        return String.valueOf(glucose);
    }

    public void setGlucose(float glucose) {
        this.glucose = glucose;
    }

    public float getXe() {
        return xe;
    }

    public String getXeString() {
        return String.valueOf(xe);
    }

    public void setXe(float xe) {
        this.xe = xe;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Record{" +
                ", injectLong=" + injectLong +
                ", injectShort=" + injectShort +
                ", glucose=" + glucose +
                ", xe=" + xe +
                ", timestamp=" + timestamp +
                '}';
    }
}

