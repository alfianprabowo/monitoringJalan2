package com.example.alfia.monitoringjalan.Ruas;

public class Ruas {

    private int id, unit_id, satker_id, ppk_id, penilik_id, nomor_1, nomor_2 ;
    private String nama, nomor_3;
    private float panjang;
    private String start, end, unit_name, satker_name, ppk_name, penilik;

    public Ruas(int id, int unit_id, String unit_name, int satker_id,String satker_name, int ppk_id, String ppk_name, int penilik_id, String penilik, int nomor_1, int nomor_2, String nomor_3, String nama, float panjang, String start, String end) {
        this.id = id;
        this.unit_id = unit_id;
        this.satker_id = satker_id;
        this.ppk_id = ppk_id;
        this.penilik_id = penilik_id;
        this.nomor_1 = nomor_1;
        this.nomor_2 = nomor_2;
        this.nomor_3 = nomor_3;
        this.nama = nama;
        this.panjang = panjang;
        this.start = start;
        this.end = end;
        this.satker_name = satker_name;
        this.ppk_name = ppk_name;
        this.penilik = penilik;
        this.unit_name = unit_name;
    }

    public String getUnit_name() {
        return unit_name;
    }

    public void setUnit_name(String unit_name) {
        this.unit_name = unit_name;
    }

    public String getSatker_name() {
        return satker_name;
    }

    public void setSatker_name(String satker_name) {
        this.satker_name = satker_name;
    }

    public String getPpk_name() {
        return ppk_name;
    }

    public void setPpk_name(String ppk_name) {
        this.ppk_name = ppk_name;
    }

    public String getPenilik() {
        return penilik;
    }

    public void setPenilik(String penilik) {
        this.penilik = penilik;
    }

    public Ruas(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(int unit_id) {
        this.unit_id = unit_id;
    }

    public int getSatker_id() {
        return satker_id;
    }

    public void setSatker_id(int satker_id) {
        this.satker_id = satker_id;
    }

    public int getPpk_id() {
        return ppk_id;
    }

    public void setPpk_id(int ppk_id) {
        this.ppk_id = ppk_id;
    }

    public int getPenilik_id() {
        return penilik_id;
    }

    public void setPenilik_id(int penilik_id) {
        this.penilik_id = penilik_id;
    }

    public int getNomor_1() {
        return nomor_1;
    }

    public void setNomor_1(int nomor_1) {
        this.nomor_1 = nomor_1;
    }

    public int getNomor_2() {
        return nomor_2;
    }

    public void setNomor_2(int nomor_2) {
        this.nomor_2 = nomor_2;
    }

    public String getNomor_3() {
        return nomor_3;
    }

    public void setNomor_3(String nomor_3) {
        this.nomor_3 = nomor_3;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public float getPanjang() {
        return panjang;
    }

    public void setPanjang(float panjang) {
        this.panjang = panjang;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
