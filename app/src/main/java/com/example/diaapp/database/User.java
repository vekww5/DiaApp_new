package com.example.diaapp.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
// Главная сущность
    @Entity(tableName = "User")
    public class User {
        @PrimaryKey(autoGenerate = true)
        public int id;
        @ColumnInfo(name = "email")  // TODO Сделать валидацию
        String email;
        @ColumnInfo(name = "password")
        String password;

        public User(String email, String password) {
            this.email = email;
            this.password = password;
        }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setId(int id) {
        this.id = id;
    }

    void validate() {
            //if (email.isBlank()) throw EmptyFieldException(Field.Email);
            //if (username.isBlank()) throw EmptyFieldException(Field.Username);
            //if (password.isBlank()) throw EmptyFieldException(Field.Password);
            //if (password != repeatPassword) throw PasswordMismatchException();    }
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", email='" + email + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }
