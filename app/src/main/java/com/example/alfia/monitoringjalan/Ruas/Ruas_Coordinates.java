package com.example.alfia.monitoringjalan.Ruas;

public class Ruas_Coordinates {

    private int ruas_id;
    private double lat, lng;
    private int point_km, point_m;
    private String date_surveyed;
    private int status;

    public Ruas_Coordinates(int ruas_id, double lat, double lng, int point_km, int point_m, String date_surveyed, int status) {
        this.ruas_id = ruas_id;
        this.lat = lat;
        this.lng = lng;
        this.point_km = point_km;
        this.point_m = point_m;
        this.date_surveyed = date_surveyed;
        this.status = status;
    }

    public Ruas_Coordinates(){

    }

    public int getRuas_id() {
        return ruas_id;
    }

    public void setRuas_id(int ruas_id) {
        this.ruas_id = ruas_id;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
