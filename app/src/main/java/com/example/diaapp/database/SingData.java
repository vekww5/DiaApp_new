package com.example.diaapp.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
    @Entity(tableName = "SingData")public class SingData {
        @PrimaryKey(autoGenerate = true)
        public int uid;
        @ColumnInfo(name = "username")    String username;
        @ColumnInfo(name = "email")
        String email;
        @ColumnInfo(name = "password")    String password;
        @ColumnInfo(name = "repeatPassword")
        String repeatPassword;
        void validate() {        //if (email.isBlank()) throw EmptyFieldException(Field.Email);
            //if (username.isBlank()) throw EmptyFieldException(Field.Username);        //if (password.isBlank()) throw EmptyFieldException(Field.Password);
            //if (password != repeatPassword) throw PasswordMismatchException();    }
        }
}
