package com.example.alfia.monitoringjalan.Kerusakan;

import com.example.alfia.monitoringjalan.Image.Image;

public class Rusak {

    private int id;
    private int ruas_id;
    private int kerusakan_id;
    private int bagian_jalan_id;
    private String date_surveyed;
    private Double lat, lng;
    private int point_km, point_m;
    private float volume;
    private int fixed;
    private String bagian_jalan, kerusakan;
    private int status;

    public Rusak(int id, int ruas_id, int kerusakan_id, int bagian_jalan_id, String date_surveyed, double lat, double lng, int point_km, int point_m, float volume, int fixed, String bagian_jalan, String kerusakan, int status) {
        this.id = id;
        this.ruas_id = ruas_id;
        this.kerusakan_id = kerusakan_id;
        this.bagian_jalan_id = bagian_jalan_id;
        this.date_surveyed = date_surveyed;
        this.lat = lat;
        this.lng = lng;
        this.point_km = point_km;
        this.point_m = point_m;
        this.volume = volume;
        this.fixed = fixed;
        this.bagian_jalan = bagian_jalan;
        this.kerusakan = kerusakan;
        this.status = status;
    }

    public Rusak(){

    }

    public String getBagian_jalan() {
        return bagian_jalan;
    }

    public void setBagian_jalan(String bagian_jalan) {
        this.bagian_jalan = bagian_jalan;
    }

    public String getKerusakan() {
        return kerusakan;
    }

    public void setKerusakan(String kerusakan) {
        this.kerusakan = kerusakan;
    }

    public int getId() {
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

    public int getKerusakan_id() {
        return kerusakan_id;
    }

    public void setKerusakan_id(int kerusakan_id) {
        this.kerusakan_id = kerusakan_id;
    }

    public int getBagian_jalan_id() {
        return bagian_jalan_id;
    }

    public void setBagian_jalan_id(int bagian_jalan_id) {
        this.bagian_jalan_id = bagian_jalan_id;
    }

    public String getDate_surveyed() {
        return date_surveyed;
    }

    public void setDate_surveyed(String date_surveyed) {
        this.date_surveyed = date_surveyed;
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

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public int getFixed() {
        return fixed;
    }

    public void setFixed(int fixed) {
        this.fixed = fixed;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
