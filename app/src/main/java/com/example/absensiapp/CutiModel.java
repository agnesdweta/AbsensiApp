package com.example.absensiapp;

public class CutiModel {

    private String id;
    private String nama;
    private String mulai;
    private String selesai;
    private String alasan;
    private String status;

    public CutiModel(String id, String nama, String mulai, String selesai, String alasan, String status) {
        this.id = id;
        this.nama = nama;
        this.mulai = mulai;
        this.selesai = selesai;
        this.alasan = alasan;
        this.status = status;
    }

    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getMulai() { return mulai; }
    public String getSelesai() { return selesai; }
    public String getAlasan() { return alasan; }
    public String getStatus() { return status; }
}