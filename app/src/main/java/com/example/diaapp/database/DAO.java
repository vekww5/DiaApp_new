package com.example.diaapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
@Dao
public interface DAO {
    @Query("SELECT * FROM recorddia")
    List<RecordDIA> getAll();

    @Query("SELECT * FROM recorddia ORDER BY date_time DESC")
    List<RecordDIA> getAllSortedTime();

    @Query("SELECT * FROM RecordDia WHERE date_time BETWEEN :start AND :end ORDER BY date_time ASC")
    List<RecordDIA> getDiaForPeriod(long start, long end);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(RecordDIA... dias);

    @Delete
    void delete(RecordDIA dia);

    @Query("DELETE FROM RecordDia WHERE uid = :id")
    void deleteID(int id);

    @Query("DELETE FROM RecordDia")
    void deleteAll();
}
