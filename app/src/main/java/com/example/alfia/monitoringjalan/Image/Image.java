package com.example.alfia.monitoringjalan.Image;

public class Image {

    private int id;
    private int rusak_id;
    private String name;
    private byte[] photo;
    private String url;
    private int status;

    public Image(int id, int rusak_id, String name,byte[] photo, String url, int status) {
        this.id = id;
        this.rusak_id = rusak_id;
        this.name = name;
        this.photo = photo;
        this.url = url;
        this.status = status;
    }

    public Image(){

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String imageName) {
        this.name= imageName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRusak_id() {
        return rusak_id;
    }

    public void setRusak_id(int rusak_id) {
        this.rusak_id = rusak_id;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
