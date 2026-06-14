package com.example.absensiapp;

public class AbsensiModel {

    String id;
    String nama;
    String tanggal;
    String jamMasuk;
    String jamKeluar;
    String status;

    public AbsensiModel(
            String id,
            String nama,
            String tanggal,
            String jamMasuk,
            String jamKeluar,
            String status) {

        this.id = id;
        this.nama = nama;
        this.tanggal = tanggal;
        this.jamMasuk = jamMasuk;
        this.jamKeluar = jamKeluar;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getJamMasuk() {
        return jamMasuk;
    }

    public String getJamKeluar() {
        return jamKeluar;
    }

    public String getStatus() {
        return status;
    }
}