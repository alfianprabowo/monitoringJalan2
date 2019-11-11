package com.example.alfia.monitoringjalan.Pemanfaatan;

public class Pemanfaatan {

    private int id;
    private String nama;

    public Pemanfaatan(){
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

    public Pemanfaatan(int id, String nama) {
        this.id = id;
        this.nama = nama;
    }
}
