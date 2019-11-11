package com.example.alfia.monitoringjalan.Version;

import java.util.Date;

public class Version {

    private int id;
    private String nama;
    private float number;
    private String file;
    private String date_release;

    public Version(){}

    public Version(int id, String nama, float number, String file, String date_release) {
        this.id = id;
        this.nama = nama;
        this.number = number;
        this.file = file;
        this.date_release = date_release;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public float getNumber() {
        return number;
    }

    public void setNumber(float number) {
        this.number = number;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getDate_release() {
        return date_release;
    }

    public void setDate_release(String date_release) {
        this.date_release = date_release;
    }
}
