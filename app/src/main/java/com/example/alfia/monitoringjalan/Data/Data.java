package com.example.alfia.monitoringjalan.Data;

public class Data {

    private int id;
    private int unit_id;
    private int satker_id;
    private int ppk_id;
    private String unit_name;
    private String satker_name;
    private String ppk_name;
    private String email;
    private String fullname;
    private int mobile_session;

    public Data(){

    }

    public Data(int id, int unit_id, int satker_id, int ppk_id, String unit_name, String satker_name, String ppk_name, String email, String fullname, int mobile_session) {
        this.id = id;
        this.unit_id = unit_id;
        this.satker_id = satker_id;
        this.ppk_id = ppk_id;
        this.unit_name = unit_name;
        this.satker_name = satker_name;
        this.ppk_name = ppk_name;
        this.email = email;
        this.fullname = fullname;
        this.mobile_session = mobile_session;
    }

    public int getId() {
        return id;
    }

    public int getUnit_id() {
        return unit_id;
    }

    public int getSatker_id() {
        return satker_id;
    }

    public int getPpk_id() {
        return ppk_id;
    }

    public String getUnit_name() {
        return unit_name;
    }

    public String getSatker_name() {
        return satker_name;
    }

    public String getPpk_name() {
        return ppk_name;
    }

    public String getEmail() {
        return email;
    }

    public String getFullname() {
        return fullname;
    }

    public int getMobile_session() {
        return mobile_session;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUnit_id(int unit_id) {
        this.unit_id = unit_id;
    }

    public void setSatker_id(int satker_id) {
        this.satker_id = satker_id;
    }

    public void setPpk_id(int ppk_id) {
        this.ppk_id = ppk_id;
    }

    public void setUnit_name(String unit_name) {
        this.unit_name = unit_name;
    }

    public void setSatker_name(String satker_name) {
        this.satker_name = satker_name;
    }

    public void setPpk_name(String ppk_name) {
        this.ppk_name = ppk_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setMobile_session(int mobile_session) {
        this.mobile_session = mobile_session;
    }
}
