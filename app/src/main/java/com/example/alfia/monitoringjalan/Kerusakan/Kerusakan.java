package com.example.alfia.monitoringjalan.Kerusakan;

public class Kerusakan {

    private int id;
    private String nama;
    private String kode;
    private int bagian_jalan_id;

    public Kerusakan(int id, String nama, String kode, int bagian_jalan_id) {
        this.id = id;
        this.nama = nama;
        this.kode = kode;
        this.bagian_jalan_id = bagian_jalan_id;
    }

    public Kerusakan(){

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

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public int getBagian_jalan_id() {
        return bagian_jalan_id;
    }

    public void setBagian_jalan_id(int bagian_jalan_id) {
        this.bagian_jalan_id = bagian_jalan_id;
    }
}
