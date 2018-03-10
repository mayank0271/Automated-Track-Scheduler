package com.aloofwillow96.rangebar;

import java.util.ArrayList;

/**
 * Created by hp on 10/14/2017.
 */

public class FacultyTime {
    public ArrayList<String> getTime() {
        return time;
    }

    ArrayList<String> time=new ArrayList<>();
    String username;
    String userId;

    public FacultyTime(ArrayList<String> time, String username, String userId) {
        this.time = time;
        this.username = username;
        this.userId = userId;
    }


    public void setTime(ArrayList<String> time) {
        this.time = time;
    }

    public FacultyTime(){

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}
