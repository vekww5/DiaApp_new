package com.example.diaapp.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "InfoUser")public class InfoUser {
    @PrimaryKey(autoGenerate = true)
    public int uid;
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
    @ColumnInfo(name = "pol")
    public String pol;
    @ColumnInfo(name = "type_long_insulin")
    public String type_long_insulin;
    @ColumnInfo(name = "type_short_insulin")
    public String type_short_insulin;    @ColumnInfo(name = "birthday")
    public long birthday;
    public int getUid() {
        return uid;
    }
    public void setUid(int uid) {
        this.uid = uid;
    }
    public String getFirstName() {
        return firstName;    }

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
    public String getPol() {
        return pol;    }
    public void setPol(String pol) {
        this.pol = pol;    }
    public String getType_long_insulin() {
        return type_long_insulin;    }
    public void setType_long_insulin(String type_long_insulin) {
        this.type_long_insulin = type_long_insulin;    }
    public String getType_short_insulin() {
        return type_short_insulin;    }
    public void setType_short_insulin(String type_short_insulin) {
        this.type_short_insulin = type_short_insulin;    }
    public long getBirthday() {
        return birthday;    }
    public void setBirthday(long birthday) {
        this.birthday = birthday;    }
    public InfoUser(String firstName, String lastName, float height, float weight, String typeDia,
                    String pol, String type_long_insulin, String type_short_insulin, long birthday) {        this.firstName = firstName;
        this.lastName = lastName;        this.height = height;
        this.weight = weight;        this.typeDia = typeDia;
        this.pol = pol;        this.type_long_insulin = type_long_insulin;
        this.type_short_insulin = type_short_insulin;        this.birthday = birthday;
    }}