package com.example.diaapp.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProfileDAO {

    @Query("SELECT * FROM Profile")
    List<Profile> getInfoUsers();

    @Update(onConflict = REPLACE)
    void updateInfoUser(Profile IUser);

    @Query("DELETE FROM Profile")
    void deleteInfoUser();


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Profile IUser);

    @Query("select * from Profile where user_id =:id")
    public Profile getInfo(int id);






}
