package tw.brad.apps.cloudfitness.java_class.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by EdLo on 2018/3/15.
 */

public class Record implements Serializable {
    private long id, user_id, datetime;
    private double weight, bmi, body_fat, body_water, muscle_mass, bone_mass, v_fat;
    private String date, time;

    public Record(){}

    public Record(long dateTime, double[] data, long user_id){
        this.datetime = dateTime;
        this.weight = data[0];
        this.body_fat = data[1];
        this.body_water = data[2];
        this.muscle_mass = data[3];
        this.bone_mass = data[4];
        this.v_fat = data[5];
        this.bmi = data[6];
        this.user_id = user_id;

        // 利用DateFormat 將時間標籤 轉為特定格式的時間字串 方便日後做sql查詢
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        this.date = format.format(dateTime);
        format = new SimpleDateFormat("HH:mm:ss");
        this.time = format.format(dateTime);

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getDateTime() {
        return datetime;
    }

    public void setDateTime(long dateTime) {
        this.datetime = dateTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public double getBody_fat() {
        return body_fat;
    }

    public void setBody_fat(double body_fat) {
        this.body_fat = body_fat;
    }

    public double getBody_water() {
        return body_water;
    }

    public void setBody_water(double body_water) {
        this.body_water = body_water;
    }

    public double getMuscle_mass() {
        return muscle_mass;
    }

    public void setMuscle_mass(double muscle_mass) {
        this.muscle_mass = muscle_mass;
    }

    public double getBone_mass() {
        return bone_mass;
    }

    public void setBone_mass(double bone_mass) {
        this.bone_mass = bone_mass;
    }

    public double getV_fat() {
        return v_fat;
    }

    public void setV_fat(double v_fat) {
        this.v_fat = v_fat;
    }

    // CRUD : 新增Record
    public boolean insert(SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put("datetime",this.datetime);
        values.put("weight",this.weight);
        values.put("bmi",this.bmi);
        values.put("body_fat",this.body_fat);
        values.put("body_water",this.body_water);
        values.put("muscle_mass",this.muscle_mass);
        values.put("bone_mass",this.bone_mass);
        values.put("v_fat",this.v_fat);
        values.put("user_id",this.user_id);
        this.setId(db.insert("record", null, values));
        Log.d("ed43", "Record Insert : "+new Gson().toJson(this));
        return this.id > 0;
    }

    // CRUD : 查詢所有Record 回傳Record List
    public static List<Record> query(long user_id, SQLiteDatabase db){
        List<Record> records = new ArrayList<>();
        Cursor cursor = db.query("record", null, "user_id=" + user_id, null, null, null, null);
        while (cursor.moveToNext()){
            Record record = new Record();
            record.setId(cursor.getLong(cursor.getColumnIndex("_id")));
            record.setDateTime(cursor.getLong(cursor.getColumnIndex("datetime")));
            record.setWeight(cursor.getDouble(cursor.getColumnIndex("weight")));
            record.setBmi(cursor.getDouble(cursor.getColumnIndex("bmi")));
            record.setBody_fat(cursor.getDouble(cursor.getColumnIndex("body_fat")));
            record.setBody_water(cursor.getDouble(cursor.getColumnIndex("body_water")));
            record.setMuscle_mass(cursor.getDouble(cursor.getColumnIndex("muscle_mass")));
            record.setBone_mass(cursor.getDouble(cursor.getColumnIndex("bone_mass")));
            record.setV_fat(cursor.getDouble(cursor.getColumnIndex("v_fat")));
            record.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
            records.add(record);
        }
        return records;
    }

    // CRUD : 查詢特定期間的Record 同一天有多筆取平均  回傳Record List
    public static List<Record> queryByCondition(long user_id, SQLiteDatabase db, long start_date, long end_date){
        List<Record> records = new ArrayList<>();
        String id = "" + user_id;
        String startDate = "" + start_date;
        String endDate = "" + end_date;
        Cursor cursor = db.query(
                "record", new String[]{"datetime", "date", "time", "AVG(weight)"},
                "user_id=? AND dateime BETWEEN ? AND ?",
                new String[]{id, startDate, endDate}, "date",
                null, null);
        Record record = new Record();
        while (cursor.moveToNext()){
            record.setId(cursor.getLong(cursor.getColumnIndex("_id")));
            record.setDateTime(cursor.getLong(cursor.getColumnIndex("datetime")));
            record.setWeight(cursor.getDouble(cursor.getColumnIndex("weight")));
            record.setBmi(cursor.getDouble(cursor.getColumnIndex("bmi")));
            record.setBody_fat(cursor.getDouble(cursor.getColumnIndex("body_fat")));
            record.setBody_water(cursor.getDouble(cursor.getColumnIndex("body_water")));
            record.setMuscle_mass(cursor.getDouble(cursor.getColumnIndex("muscle_mass")));
            record.setBone_mass(cursor.getDouble(cursor.getColumnIndex("bone_mass")));
            record.setV_fat(cursor.getDouble(cursor.getColumnIndex("v_fat")));
            record.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
            records.add(record);
        }
        return records;
    }

}
