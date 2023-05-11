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
public interface InfoUserDAO {
    @Query("SELECT * FROM InfoUser")    LiveData<List<InfoUser>> getInfoUser();
    @Update(onConflict = REPLACE)
    void updateInfoUser(InfoUser IUser);
    @Query("DELETE FROM InfoUser")    void deleteInfoUser();
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(InfoUser IUser);
}