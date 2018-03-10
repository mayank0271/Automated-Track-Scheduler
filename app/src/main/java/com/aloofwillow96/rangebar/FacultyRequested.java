package com.aloofwillow96.rangebar;

/**
 * Created by hp on 10/14/2017.
 */

public class FacultyRequested {
    String name;
    String id;
    String time;

    public FacultyRequested() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public FacultyRequested(String name, String id, String time) {
        this.name = name;
        this.id = id;

        this.time = time;
    }
}
