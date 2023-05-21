package com.example.diaapp.database;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PredictDAO {

    @Query("SELECT * FROM predictdia")
    List<PredictDIA> getAll();

}
