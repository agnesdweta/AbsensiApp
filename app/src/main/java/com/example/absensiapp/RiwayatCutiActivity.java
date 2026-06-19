package com.example.absensiapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RiwayatCutiActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView btnBack;

    List<CutiModel> list = new ArrayList<>();
    CutiAdapter adapter;

    String URL = "http://10.0.2.2/absensi/public/api/cuti/riwayat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_cuti);

        recyclerView = findViewById(R.id.recyclerCuti);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CutiAdapter(list, null, false);
        recyclerView.setAdapter(adapter);

        loadData();
    }

    private void loadData() {
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);

        String id_karyawan = sp.getString("id", "");
        String role = sp.getString("role", "");

        Log.d("RIWAYAT_DEBUG", "id_karyawan login = " + id_karyawan);
        Log.d("RIWAYAT_DEBUG", "role login = " + role);

        if (id_karyawan.isEmpty()) {
            Toast.makeText(this, "User belum login atau ID kosong", Toast.LENGTH_LONG).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.GET, URL,
                response -> {
                    try {
                        Log.d("RIWAYAT_RESPONSE", response);
                        JSONObject obj = new JSONObject(response);

                        String statusResponse = obj.optString("status", "");
                        if (!statusResponse.equalsIgnoreCase("success")) {
                            Toast.makeText(this,
                                    obj.optString("message", "Gagal memuat riwayat cuti"),
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        JSONArray arr = obj.getJSONArray("data");
                        list.clear();

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject item = arr.getJSONObject(i);

                            String karyawanIdFromServer = item.optString("id_karyawan", "");

                            if (karyawanIdFromServer.equals(id_karyawan)) {
                                list.add(new CutiModel(
                                        item.optString("id"),
                                        karyawanIdFromServer,
                                        item.optString("tanggal_mulai"),
                                        item.optString("tanggal_selesai"),
                                        item.optString("keterangan"),
                                        item.optString("status")
                                ));
                            }
                        }

                        adapter.notifyDataSetChanged();

                        if (list.isEmpty()) {
                            Toast.makeText(this, "Tidak ada riwayat cuti untuk Anda", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Log.e("RIWAYAT_DEBUG", "Parsing JSON Error: ", e);
                        Toast.makeText(this, "Parse Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    String msg = "Gagal load data";
                    if (error.networkResponse != null) {
                        msg = "HTTP CODE: " + error.networkResponse.statusCode;
                    }
                    Log.e("RIWAYAT_DEBUG", "Network Error: ", error);
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                }
        );

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }
}