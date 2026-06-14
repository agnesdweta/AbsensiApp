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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AbsenActivity extends AppCompatActivity {

    private MaterialButton btnMasuk, btnKeluar;
    private TextView tvStatus, tvJamMasuk;
    private ImageView btnBack;

    private RequestQueue queue;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absen);

        btnMasuk = findViewById(R.id.btnMasuk);
        btnKeluar = findViewById(R.id.btnKeluar);

        tvStatus = findViewById(R.id.tvStatus);
        tvJamMasuk = findViewById(R.id.tvJamMasuk);

        btnBack = findViewById(R.id.btnBack);

        queue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

        loadStatus();

        btnBack.setOnClickListener(v -> finish());
        btnMasuk.setOnClickListener(v -> absenMasuk());
        btnKeluar.setOnClickListener(v -> absenKeluar());
    }

    // ================= CEK ID =================
    private String getIdKaryawan() {
        String id = sharedPreferences.getString("id_karyawan", "");

        if (id == null || id.isEmpty()) {
            Toast.makeText(this, "ID Karyawan tidak ditemukan", Toast.LENGTH_SHORT).show();
            return null;
        }
        return id;
    }

    // ================= LOAD STATUS =================
    private void loadStatus() {

        String idKaryawan = getIdKaryawan();
        if (idKaryawan == null) return;

        String url =
                "http://10.0.2.2/absensi/public/api/absen/status?id_karyawan=" + idKaryawan;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {

                        JSONObject obj = new JSONObject(response);

                        boolean status = obj.getBoolean("status");
                        if (!status) {
                            Toast.makeText(this,
                                    obj.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONObject data = obj.getJSONObject("data");

                        String statusAbsen = data.optString("status_absen", "BELUM ABSEN");
                        String jamMasuk = data.optString("jam_masuk", "--:--:--");
                        String jamPulang = data.optString("jam_pulang", "--:--:--");

                        tvStatus.setText("STATUS: " + statusAbsen);

                        if (statusAbsen.equals("BELUM ABSEN")) {

                            tvStatus.setBackgroundColor(
                                    getResources().getColor(android.R.color.holo_red_dark)
                            );
                            tvJamMasuk.setText("Belum absen hari ini");

                        } else if (statusAbsen.equals("HADIR")) {

                            tvStatus.setBackgroundColor(
                                    getResources().getColor(android.R.color.holo_green_dark)
                            );
                            tvJamMasuk.setText("Jam Masuk: " + jamMasuk);

                        } else {

                            tvStatus.setBackgroundColor(
                                    getResources().getColor(android.R.color.holo_blue_dark)
                            );
                            tvJamMasuk.setText("Jam Kerja: " + jamMasuk + " - " + jamPulang);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error JSON: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal ambil status", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }

    // ================= ABSEN MASUK =================
    private void absenMasuk() {

        String idKaryawan = getIdKaryawan();
        if (idKaryawan == null) return;

        String url = "http://10.0.2.2/absensi/public/api/absen/masuk";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        boolean status = obj.getBoolean("status");
                        String message = obj.getString("message");

                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                        if (status) {
                            loadStatus();
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Response error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal koneksi", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("id_karyawan", idKaryawan);
                return params;
            }
        };

        queue.add(request);
    }

    // ================= ABSEN KELUAR =================
    private void absenKeluar() {

        String idKaryawan = getIdKaryawan();
        if (idKaryawan == null) return;

        String url = "http://10.0.2.2/absensi/public/api/absen/keluar";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        boolean status = obj.getBoolean("status");
                        String message = obj.getString("message");

                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                        if (status) {
                            loadStatus();
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Response error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Server Error", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("id_karyawan", idKaryawan);
                return params;
            }
        };

        queue.add(request);
    }
}