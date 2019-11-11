package com.example.alfia.monitoringjalan.Ruang_Jalan;

public class Ruang_Jalan {

    private int id;
    private String nama;

    public Ruang_Jalan(int id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    public Ruang_Jalan(){

    }

    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
