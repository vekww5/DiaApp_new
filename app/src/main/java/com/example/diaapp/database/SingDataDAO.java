package com.example.diaapp.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
public interface SingDataDAO {
    @Query("SELECT * FROM SingData")
LiveData<List<SingData>> getAllSingData();
    @Update(onConflict = REPLACE)    void updateSingData(SingData SData);
    @Query("DELETE FROM SingData")
    void deleteAllSingData();
    @Insert(onConflict = OnConflictStrategy.IGNORE)    void insert(SingData SData);
    @Query("DELETE FROM SingData WHERE uid = :id")
    void deleteSingDataID(int id);
    @Query("SELECT * FROM SingData WHERE uid = :id")    void getSingDataID(int id);
    @Query("SELECT * FROM SingData WHERE email = :email")
    void getSingDataEmail(String email);}