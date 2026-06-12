package com.example.absensiapp;

public class KaryawanModel {

    String id;
    String nama;
    String jabatan;
    String telp;

    public KaryawanModel(String id, String nama, String jabatan, String telp) {
        this.id = id;
        this.nama = nama;
        this.jabatan = jabatan;
        this.telp = telp;
    }

    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getJabatan() { return jabatan; }
    public String getTelp() { return telp; }
}