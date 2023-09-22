package com.example.diaapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDAO {
    @Insert
    void add(User user);
    @Query("select * from User where email = :email and password = :password")
    User verify(String email, String password);

    @Query("select * from User")
    List<User> getAll();

    @Query("DELETE FROM User")
    void deleteAll();
}
