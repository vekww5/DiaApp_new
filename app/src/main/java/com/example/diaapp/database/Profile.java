package com.example.diaapp.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

//@Entity(tableName = "Profile")

@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user_id"))
public class Profile {
    @PrimaryKey(autoGenerate = true)
    public int profile_id;
    @ColumnInfo(name = "first_name")
    public String firstName;
    @ColumnInfo(name = "last_name")
    public String lastName;
    @ColumnInfo(name = "height")
    public float height;
    @ColumnInfo(name = "weight")
    public float weight;
    @ColumnInfo(name = "type_dia")
    public String typeDia;

    @ColumnInfo(name = "gender")
    public String gender;
    @ColumnInfo(name = "birthday")
    public long birthday;

    public String getFirstName() {
        return firstName;    }

    @ColumnInfo(name = "user_id")
    public int userId;


    public Profile(String firstName, String lastName, float height, float weight,
                   String typeDia, String gender, long birthday, int userId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.height = height;
        this.weight = weight;
        this.typeDia = typeDia;
        this.gender = gender;
        this.birthday = birthday;
        this.userId = userId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;    }
    public void setLastName(String lastName) {
        this.lastName = lastName;    }
    public float getHeight() {
        return height;    }
    public void setHeight(float height) {
        this.height = height;    }
    public float getWeight() {
        return weight;    }
    public void setWeight(float weight) {
        this.weight = weight;    }
    public String getTypeDia() {
        return typeDia;    }
    public void setTypeDia(String typeDia) {
        this.typeDia = typeDia;    }

    public long getBirthday() {
        return birthday;    }
    public void setBirthday(long birthday) {
        this.birthday = birthday;    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}