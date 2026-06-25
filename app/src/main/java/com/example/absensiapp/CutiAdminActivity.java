package com.example.absensiapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CutiAdminActivity extends AppCompatActivity {

    RecyclerView rvCuti;
    ImageView btnBack;

    ArrayList<CutiModel> list = new ArrayList<>();
    CutiAdapter adapter;
    RequestQueue queue;

    private static final String URL_LIST = "http://10.0.2.2/absensi/public/api/cuti/riwayat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuti_admin);

        rvCuti = findViewById(R.id.rvCuti);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        queue = Volley.newRequestQueue(getApplicationContext());
        rvCuti.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CutiAdapter(list, new CutiAdapter.OnAction() {
            @Override
            public void onApprove(CutiModel c) {
                updateStatus(c.getId(), "DISETUJUI");
            }

            @Override
            public void onReject(CutiModel c) {
                updateStatus(c.getId(), "DITOLAK");
            }
        }, true);

        rvCuti.setAdapter(adapter);

        loadData();
    }

    // ================= LOAD DATA CUTI =================
    private void loadData() {
        String url =
                "http://10.0.2.2/absensi/public/api/cuti/riwayat?role=admin";

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        Log.d("CUTI_ADMIN_DEBUG", response);
                        JSONObject obj = new JSONObject(response);
                        String statusResponse =
                                obj.optString("status", "");

                        if (!statusResponse.equalsIgnoreCase("success")) {

                            Toast.makeText(
                                    this,
                                    obj.optString("message",
                                            "Gagal memuat data"),
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        JSONArray arr = obj.optJSONArray("data");
                        if (arr == null) {
                            Toast.makeText(this, "DATA KOSONG", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        list.clear();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);
                            list.add(new CutiModel(
                                    o.optString("id"),
                                    o.optString("id_karyawan"),
                                    o.optString("tanggal_mulai"),
                                    o.optString("tanggal_selesai"),
                                    o.optString("keterangan"),
                                    o.optString("status")
                            ));
                        }

                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {

                        Log.e("CUTI_ADMIN_DEBUG",
                                "JSON Error", e);

                        Toast.makeText(
                                this,
                                e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                },
                error -> {

                    Log.e("CUTI_ADMIN_DEBUG",
                            "Volley Error", error);

                    Toast.makeText(
                            this,
                            "Gagal terhubung ke server",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        queue.add(request);
    }
    // ================= UPDATE STATUS CUTI ================
    private void updateStatus(String id, String status) {

        String url = "http://10.0.2.2/absensi/public/api/cuti/updateStatus";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    try {

                        Log.d("CUTI_ADMIN_DEBUG", response);

                        JSONObject obj = new JSONObject(response);

                        boolean success = false;

                        if (obj.has("status")) {
                            Object st = obj.get("status");

                            if (st instanceof Boolean) {
                                success = (Boolean) st;
                            } else {
                                success = "success".equalsIgnoreCase(st.toString())
                                        || "true".equalsIgnoreCase(st.toString());
                            }
                        }

                        Toast.makeText(
                                CutiAdminActivity.this,
                                obj.optString("message"),
                                Toast.LENGTH_SHORT
                        ).show();

                        if (success) {
                            loadData();
                        }

                    } catch (Exception e) {
                        Log.e("CUTI_ADMIN_DEBUG", "JSON ERROR", e);

                        Toast.makeText(
                                CutiAdminActivity.this,
                                e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                },
                error -> {

                    Log.e("CUTI_ADMIN_DEBUG", "VOLLEY ERROR", error);

                    if (error.networkResponse != null) {
                        Log.e(
                                "CUTI_ADMIN_DEBUG",
                                "CODE = " + error.networkResponse.statusCode
                        );
                    }

                    Toast.makeText(
                            CutiAdminActivity.this,
                            "Gagal update status",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> p = new HashMap<>();

                p.put("id", id);          // WAJIB
                p.put("status", status);  // WAJIB

                return p;
            }
        };

        queue.add(request);
    }
}