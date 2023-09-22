package com.example.diaapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
@Dao
public interface SensorDAO {
    @Insert
    void insert(Sensor sensor);

    @Query("select * from Sensor where user_id = :id")
    List<Sensor> getSensorData(int id);

}
