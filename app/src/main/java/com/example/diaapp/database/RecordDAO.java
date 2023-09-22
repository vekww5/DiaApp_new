package com.example.diaapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
@Dao
public interface RecordDAO {
    @Query("SELECT * FROM Record")
    List<Record> getAll();

    @Query("SELECT * FROM Record where user_id =:id  ORDER BY date_time DESC")
    List<Record> getAllSortedTime(int id);

    @Query("SELECT * FROM Record WHERE user_id = :id AND date_time BETWEEN :start AND :end ORDER BY date_time ASC")
    List<Record> getDiaForPeriod(long start, long end, int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Record record);

    @Delete
    void deleteDIA(Record dia);

    @Query("DELETE FROM Record WHERE recordId = :id")
    void deleteID(int id);

    @Query("DELETE FROM Record WHERE user_id = :id")
    void deleteAllByUderID(int id);

    @Query("DELETE FROM Record")
    void deleteAll();

    @Query("select * from Record where inject_long = :inject")
    Record get(float inject);


}
