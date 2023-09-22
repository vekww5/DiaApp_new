package com.example.diaapp.database;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Profile.class, parentColumns = "profile_id", childColumns = "profile_id"))
public class Insulin {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "insulin_name")
    String insulinName;

    @ColumnInfo(name = "coefficient_action")
    float coeffAction;

    @ColumnInfo(name = "duration_action")
    float durationAction;

    @ColumnInfo(name = "profile_id")
    public int profileId;

    public Insulin(String insulinName, float coeffAction, float durationAction, int profileId) {
        this.insulinName = insulinName;
        this.coeffAction = coeffAction;
        this.durationAction = durationAction;
        this.profileId = profileId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInsulinName() {
        return insulinName;
    }

    public void setInsulinName(String insulinName) {
        this.insulinName = insulinName;
    }

    public float getCoeffAction() {
        return coeffAction;
    }

    public void setCoeffAction(float coeffAction) {
        this.coeffAction = coeffAction;
    }

    public float getDurationAction() {
        return durationAction;
    }

    public void setDurationAction(float durationAction) {
        this.durationAction = durationAction;
    }

}
