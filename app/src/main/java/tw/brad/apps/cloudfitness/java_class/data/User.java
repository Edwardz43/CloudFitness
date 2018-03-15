package tw.brad.apps.cloudfitness.java_class.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;

/**
 * Created by EdLo on 2018/3/15.
 */

public class User implements Serializable {
    private String email, password, fisrtname, lastname,
            birthdate, height_in, height_ft, height_cm, weight_lb, weight_kg, gender;
    private Integer unit_type, activity_level, rememberMe;
    private Long id, fb_id;

    public User(){}

    public User(String email, String password, String fisrtname, String lastname, String birthdate, String gender, Integer unit_type,
                String height_ft, String height_in, String height_cm, String weight_lb, String weight_kg, Integer activity_level){
        this.email = email;
        this.password = password;
        this.fisrtname = fisrtname;
        this.lastname = lastname;
        this.birthdate = birthdate;
        this.gender = gender;
        this.unit_type = unit_type;
        this.height_ft = height_ft;
        this.height_in = height_in;
        this.height_cm = height_cm;
        this.weight_lb = weight_lb;
        this.weight_kg = weight_kg;
        this.activity_level = activity_level;
    }

    public Integer getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Integer rememberMe) {
        this.rememberMe = rememberMe;
    }

    public Long getFb_id() {
        return fb_id;
    }

    public void setFb_id(Long fb_id) {
        this.fb_id = fb_id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getUnit_type() {
        return unit_type;
    }

    public void setUnit_type(Integer unit_type) {
        this.unit_type = unit_type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFisrtname() {
        return fisrtname;
    }

    public void setFisrtname(String fisrtname) {
        this.fisrtname = fisrtname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getHeight_in() {
        return height_in;
    }

    public void setHeight_in(String height_in) {
        this.height_in = height_in;
    }

    public String getHeight_ft() {
        return height_ft;
    }

    public void setHeight_ft(String height_ft) {
        this.height_ft = height_ft;
    }

    public String getHeight_cm() {
        return height_cm;
    }

    public void setHeight_cm(String height_cm) {
        this.height_cm = height_cm;
    }

    public String getWeight_lb() {
        return weight_lb;
    }

    public void setWeight_lb(String weight_lb) {
        this.weight_lb = weight_lb;
    }

    public String getWeight_kg() {
        return weight_kg;
    }

    public void setWeight_kg(String weight_kg) {
        this.weight_kg = weight_kg;
    }

    public Integer getActivity_level() {
        return activity_level;
    }

    public void setActivity_level(Integer activity_level) {
        this.activity_level = activity_level;
    }

    // CRUD : 新增User
    public boolean insert(SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put("email",this.email);
        values.put("password",this.password);
        values.put("first_name",this.fisrtname);
        values.put("last_name",this.lastname);
        values.put("birth_date",this.birthdate);
        values.put("height_in",this.height_in);
        values.put("height_ft",this.height_ft);
        values.put("height_cm",this.height_cm);
        values.put("weight_lb",this.weight_lb);
        values.put("weight_kg",this.weight_kg);
        values.put("gender",this.gender);
        values.put("unit_type",this.unit_type);
        values.put("activity_level",this.activity_level);
        values.put("fb_id",this.fb_id);
        this.setId(db.insert("users", null, values));
        return this.id > 0;
    }

    // CRUD : 更新User
    public boolean update(SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put("email",this.email);
        values.put("password",this.password);
        values.put("first_name",this.fisrtname);
        values.put("last_name",this.lastname);
        values.put("birth_date",this.birthdate);
        values.put("height_in",this.height_in);
        values.put("height_ft",this.height_ft);
        values.put("height_cm",this.height_cm);
        values.put("weight_lb",this.weight_lb);
        values.put("weight_kg",this.weight_kg);
        values.put("gender",this.gender);
        values.put("unit_type",this.unit_type);
        values.put("activity_level",this.activity_level);
        values.put("fb_id",this.fb_id);

        return db.update("users", values, "_id=?",new String[] {""+this.id}) > 0 ;
    }

    // CRUD : 查詢User  若有查到 就回傳User物件 否則回傳空值
    public static User query(String email, SQLiteDatabase db){
        Cursor cursor = db.query("users", null, "email='" + email+"'", null, null, null, null);
        User user = new User();
        while (cursor.moveToNext()){
            user.setId(cursor.getLong(cursor.getColumnIndex("_id")));
            user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            user.setFisrtname(cursor.getString(cursor.getColumnIndex("first_name")));
            user.setLastname(cursor.getString(cursor.getColumnIndex("last_name")));
            user.setBirthdate(cursor.getString(cursor.getColumnIndex("birth_date")));
            user.setHeight_ft(cursor.getString(cursor.getColumnIndex("height_ft")));
            user.setHeight_in(cursor.getString(cursor.getColumnIndex("height_in")));
            user.setHeight_cm(cursor.getString(cursor.getColumnIndex("height_cm")));
            user.setWeight_lb(cursor.getString(cursor.getColumnIndex("weight_lb")));
            user.setWeight_kg(cursor.getString(cursor.getColumnIndex("weight_kg")));
            user.setGender(cursor.getString(cursor.getColumnIndex("gender")));
            user.setUnit_type(cursor.getInt(cursor.getColumnIndex("unit_type")));
            user.setActivity_level(cursor.getInt(cursor.getColumnIndex("activity_level")));
            user.setFb_id(cursor.getLong(cursor.getColumnIndex("fb_id")));
            return user;
        }
        return null;
    }


}
