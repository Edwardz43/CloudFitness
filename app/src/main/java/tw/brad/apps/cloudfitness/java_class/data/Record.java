package tw.brad.apps.cloudfitness.java_class.data;

import java.io.Serializable;

/**
 * Created by EdLo on 2018/3/15.
 */

public class Record implements Serializable {
    private long id, user_id, dateTime;
    private double weight, bmi, body_fat, body_water, muscle_mass, bone_mass, v_fat;

    public Record(long dateTime, double[] data, long user_id){
        this.dateTime = dateTime;
        this.weight = data[0];
        this.body_fat = data[1];
        this.body_water = data[2];
        this.muscle_mass = data[3];
        this.bone_mass = data[4];
        this.v_fat = data[5];
        this.bmi = data[6];
        this.user_id = user_id;
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
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
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
}
