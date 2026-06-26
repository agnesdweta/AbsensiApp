package com.example.absensiapp;

public class KaryawanModel {

    String id;
    String nama;
    String jabatan;
    String telp;
    String username;
    String password;

    public KaryawanModel(String id, String nama, String jabatan, String telp,
                         String username, String password) {
        this.id = id;
        this.nama = nama;
        this.jabatan = jabatan;
        this.telp = telp;
        this.username = username;
        this.password = password;
    }

    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getJabatan() { return jabatan; }
    public String getTelp() { return telp; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}