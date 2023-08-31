package com.example.diaapp.database;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {RecordDIA.class}, version = 1)
public abstract class DiaDataBase extends RoomDatabase {
    public abstract DAO diaDao();

    public static volatile DiaDataBase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static DiaDataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DiaDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    DiaDataBase.class, "DiaDataBase")
                            .allowMainThreadQueries()
                            .build();

                    INSTANCE.diaDao().deleteAll();

                    long time = System.currentTimeMillis();
                    time -= 70000000;


                    RecordDIA dia = new RecordDIA(0, 0, 135, 0, time);
                    INSTANCE.diaDao().insert(dia);
                    time -= 1000000;

                    dia = new RecordDIA(0, 0, 127, 0, time);
                    INSTANCE.diaDao().insert(dia);
                    time -= 1000000;

                    dia = new RecordDIA(0, 0, 125, 0, time);
                    INSTANCE.diaDao().insert(dia);
                    time -= 2000000;
                    dia = new RecordDIA(0, 0, 124, 0, time);
                    INSTANCE.diaDao().insert(dia);
                    time -= 2000000;
                    dia = new RecordDIA(0, 0, 123, 0, time);
                    INSTANCE.diaDao().insert(dia);
                    time -= 2000000;

                    dia = new RecordDIA(0, 0, 122, 0, time);
                    INSTANCE.diaDao().insert(dia);
                    time -= 2000000;

                    dia = new RecordDIA(0, 0, 118, 0, time);
                    INSTANCE.diaDao().insert(dia);
                    time -= 2000000;

                    dia = new RecordDIA(0, 0, 110, 0, time);
                    INSTANCE.diaDao().insert(dia);
                    time -= 2000000;

                }
            }
        }
        return INSTANCE;
    }
}