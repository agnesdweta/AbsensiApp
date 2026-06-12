package com.example.absensiapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AbsenActivity extends AppCompatActivity {

    MaterialButton btnMasuk, btnKeluar;

    TextView tvStatus,
            tvJamMasuk;

    ImageView btnBack;

    RequestQueue queue;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absen);

        // ================= INIT =================
        btnMasuk = findViewById(R.id.btnMasuk);
        btnKeluar = findViewById(R.id.btnKeluar);

        tvStatus = findViewById(R.id.tvStatus);
        tvJamMasuk = findViewById(R.id.tvJamMasuk);

        btnBack = findViewById(R.id.btnBack);

        queue = Volley.newRequestQueue(this);

        sharedPreferences =
                getSharedPreferences("ABSEN", MODE_PRIVATE);

        loadStatus();

        // ================= BACK =================
        btnBack.setOnClickListener(v -> finish());

        // ================= ABSEN MASUK =================
        btnMasuk.setOnClickListener(v -> absenMasuk());

        // ================= ABSEN KELUAR =================
        btnKeluar.setOnClickListener(v -> absenKeluar());
    }

    // =================================================
    // LOAD STATUS
    // =================================================
    private void loadStatus() {

        String status =
                sharedPreferences.getString(
                        "status",
                        "BELUM ABSEN"
                );

        String jamMasuk =
                sharedPreferences.getString(
                        "jam_masuk",
                        "--:--:--"
                );

        String jamKeluar =
                sharedPreferences.getString(
                        "jam_keluar",
                        "--:--:--"
                );

        tvStatus.setText("STATUS: " + status);
        // ================= TAMPIL JAM =================
        if (status.equals("PULANG")) {

            tvJamMasuk.setText(
                    "Jam Kerja : "
                            + jamMasuk
                            + " - "
                            + jamKeluar
            );
        } else {

            tvJamMasuk.setText(
                    "Jam Masuk : " + jamMasuk
            );
        }

        // ================= WARNA STATUS =================
        if (status.equals("BELUM ABSEN")) {

            tvStatus.setBackgroundColor(
                    getResources().getColor(
                            android.R.color.holo_red_dark
                    )
            );

        } else if (status.equals("HADIR")) {

            tvStatus.setBackgroundColor(
                    getResources().getColor(
                            android.R.color.holo_green_dark
                    )
            );

        } else if (status.equals("PULANG")) {

            tvStatus.setBackgroundColor(
                    getResources().getColor(
                            android.R.color.holo_blue_dark
                    )
            );
        }
    }

    // ====
    // ABSEN MASUK
    // ====
    private void absenMasuk() {

        String url =
                "http://10.0.2.2/absensi/public/api/absen/masuk";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,

                response -> {

                    String jamMasuk =
                            getJamSekarang();

                    String jamKeluar =
                            sharedPreferences.getString(
                                    "jam_keluar",
                                    "--:--:--"
                            );

                    // ================= UI =================
                    tvStatus.setText("STATUS: HADIR");

                    tvStatus.setBackgroundColor(
                            getResources().getColor(
                                    android.R.color.holo_green_dark
                            )
                    );

                    tvJamMasuk.setText(
                            "Jam Masuk : " + jamMasuk
                    );

                    // ================= SIMPAN =================
                    SharedPreferences.Editor editor =
                            sharedPreferences.edit();

                    editor.putString("status", "HADIR");
                    editor.putString("jam_masuk", jamMasuk);

                    editor.apply();

                    Toast.makeText(
                            this,
                            "Absen Masuk Berhasil",
                            Toast.LENGTH_SHORT
                    ).show();
                },

                error -> {

                    String pesan = "Gagal koneksi";

                    if (error.networkResponse != null) {

                        pesan =
                                "Error : "
                                        + error.networkResponse.statusCode;
                    }

                    Toast.makeText(
                            this,
                            pesan,
                            Toast.LENGTH_LONG
                    ).show();
                }

        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params =
                        new HashMap<>();

                params.put("id_karyawan", "1");
                params.put("jam_masuk", getJamSekarang());

                return params;
            }
        };

        queue.add(request);
    }

    // =================================================
    // ABSEN KELUAR
    // =================================================
    private void absenKeluar() {

        String url =
                "http://10.0.2.2/absensi/public/api/absen/keluar";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,

                response -> {

                    String jamMasuk =
                            sharedPreferences.getString(
                                    "jam_masuk",
                                    "--:--:--"
                            );

                    String jamKeluar =
                            getJamSekarang();

                    // ================= UI =================
                    tvStatus.setText("STATUS: PULANG");

                    tvStatus.setBackgroundColor(
                            getResources().getColor(
                                    android.R.color.holo_blue_dark
                            )
                    );

                    tvJamMasuk.setText(
                            "Jam Kerja : "
                                    + jamMasuk
                                    + " - "
                                    + jamKeluar
                    );

                    // ================= SIMPAN =================
                    SharedPreferences.Editor editor =
                            sharedPreferences.edit();

                    editor.putString("status", "PULANG");
                    editor.putString("jam_keluar", jamKeluar);

                    editor.apply();

                    Toast.makeText(
                            this,
                            "Absen Keluar Berhasil",
                            Toast.LENGTH_SHORT
                    ).show();
                },

                error -> {

                    String pesan = "Server Error";

                    if (error.networkResponse != null) {

                        pesan =
                                "Error : "
                                        + error.networkResponse.statusCode;
                    }

                    Toast.makeText(
                            this,
                            pesan,
                            Toast.LENGTH_LONG
                    ).show();
                }

        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params =
                        new HashMap<>();

                params.put("id_karyawan", "1");
                params.put("jam_keluar", getJamSekarang());

                return params;
            }
        };

        queue.add(request);
    }

    // =================================================
    // GET JAM SEKARANG
    // =================================================
    private String getJamSekarang() {

        return new SimpleDateFormat(
                "HH:mm:ss",
                Locale.getDefault()
        ).format(new Date());
    }
}