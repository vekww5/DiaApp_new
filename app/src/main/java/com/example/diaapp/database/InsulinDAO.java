package com.example.diaapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InsulinDAO
{
    @Insert
    void insert(Insulin insulin);

    @Query("select * from Insulin where profile_id = :id")
    List<Insulin> getInsulin(int id);
}
