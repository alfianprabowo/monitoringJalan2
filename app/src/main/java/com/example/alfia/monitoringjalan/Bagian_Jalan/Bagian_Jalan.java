package com.example.alfia.monitoringjalan.Bagian_Jalan;

public class Bagian_Jalan {

    private int id;
    private String name;
    private int parent_id;
    private String kode;

    public Bagian_Jalan(){

    }

    public Bagian_Jalan(int id, String name, int parent_id, String kode) {
        this.id = id;
        this.name = name;
        this.parent_id = parent_id;
        this.kode = kode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return name;
    }

    public void setNama(String nama) {
        this.name = nama;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }
}
