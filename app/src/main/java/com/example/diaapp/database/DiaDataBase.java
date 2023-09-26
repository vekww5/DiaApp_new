package com.example.diaapp.database;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.diaapp.utility.Crypt;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = { Record.class, Profile.class, User.class, Sensor.class, Insulin.class}, version = 18)
public abstract class DiaDataBase extends RoomDatabase {
    public abstract RecordDAO diaDao();
    public abstract ProfileDAO infoDao();
    public abstract UserDAO dataDao();
    public abstract SensorDAO sensorDao();
    public abstract InsulinDAO insulinDAO();

    public static volatile DiaDataBase INSTANCE;
    private static final int NUMBER_OF_THREADS = 9;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static DiaDataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DiaDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    DiaDataBase.class, "DiaDataBase")
                            //.fallbackToDestructiveMigration()                  // Уничтожащая миграция - просто стираем старую БД при запуске и создаем новую
                            .allowMainThreadQueries()
                            .build();



                    // Получение текущего времени
                    Date currentTime = new Date();

                    // Создание объекта Calendar и установка текущего времени
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(currentTime);

                    // Отнятие 20 часов
                    calendar.add(Calendar.HOUR_OF_DAY, -20);

                    // Получение нового времени
                    Date newTime = calendar.getTime();

                    // Вывод нового времени
                    System.out.println(newTime);

                    long time = newTime.getTime();

                    //long time = System.currentTimeMillis();
                    //time -= 70000000;

                    User user = null;
                    try {
                        user = new User("12", Crypt.encrypt("12"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    //INSTANCE.diaDao().deleteAll();

                    User sd = INSTANCE.dataDao().verify( user.email,  user.password);

                    List<User> ls =  INSTANCE.dataDao().getAll();

                    if (sd != null) {

                        INSTANCE.dataDao().add(user);

                        int uid = INSTANCE.dataDao().verify(user.email, user.password).getId();

                        INSTANCE.diaDao().deleteAllByUderID(uid);



                        Record dia = new Record(0, 0, 120, 0,
                                time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 115, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 110, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;


                        dia = new Record(0, 0, 100, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 99, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 90, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 79, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 70, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 69, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 65, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 60, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 70, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 80, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 100, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 125, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 135, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 140, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 142, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 140, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 137, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 130, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 124, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 120, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 117, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 115, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 111, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;

                        dia = new Record(0, 0, 110, 0, time, uid);
                        INSTANCE.diaDao().insert(dia);
                        time -= 1000000;
                    }
                }
            }
        }
        return INSTANCE;
    }
}