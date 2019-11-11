package com.example.alfia.monitoringjalan.Pemanfaatan;

public class Pemanfaatan_Ruang {
    private int id;
    private int ruas_id;
    private int ruang_jalan_id;
    private int pemanfaatan_id;
    private double lat;
    private double lng;
    private int point_km;
    private int point_m;
    private String date_surveyed;
    private String pemanfaatan, ruang_jalan;
    private int status;

    public Pemanfaatan_Ruang(int id, int ruas_id, int ruang_jalan_id, int pemanfaatan_id, String date_surveyed, double lat, double lng, int point_km, int point_m, String ruang_jalan, String pemanfaatan, int status) {
        this.id = id;
        this.ruas_id = ruas_id;
        this.ruang_jalan_id = ruang_jalan_id;
        this.pemanfaatan_id = pemanfaatan_id;
        this.lat = lat;
        this.lng = lng;
        this.point_km = point_km;
        this.point_m = point_m;
        this.date_surveyed = date_surveyed;
        this.ruang_jalan = ruang_jalan;
        this.pemanfaatan = pemanfaatan;
        this.status = status;
    }

    public Pemanfaatan_Ruang(){

    }

    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRuas_id() {
        return ruas_id;
    }

    public void setRuas_id(int ruas_id) {
        this.ruas_id = ruas_id;
    }

    public int getRuang_jalan_id() {
        return ruang_jalan_id;
    }

    public void setRuang_jalan_id(int ruang_jalan_id) {
        this.ruang_jalan_id = ruang_jalan_id;
    }

    public int getPemanfaatan_id() {
        return pemanfaatan_id;
    }

    public void setPemanfaatan_id(int pemanfaatan_id) {
        this.pemanfaatan_id = pemanfaatan_id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getPoint_km() {
        return point_km;
    }

    public void setPoint_km(int point_km) {
        this.point_km = point_km;
    }

    public int getPoint_m() {
        return point_m;
    }

    public void setPoint_m(int point_m) {
        this.point_m = point_m;
    }

    public String getDate_surveyed() {
        return date_surveyed;
    }

    public void setDate_surveyed(String date_surveyed) {
        this.date_surveyed = date_surveyed;
    }

    public String getPemanfaatan() {
        return pemanfaatan;
    }

    public void setPemanfaatan(String pemanfaatan) {
        this.pemanfaatan = pemanfaatan;
    }

    public String getRuang_jalan() {
        return ruang_jalan;
    }

    public void setRuang_jalan(String ruang_jalan) {
        this.ruang_jalan = ruang_jalan;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
