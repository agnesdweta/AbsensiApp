package com.example.absensiapp;

import android.os.Bundle;
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

    // 🔥 FIX URL (HARUS SESUAI BACKEND list)
    private static final String URL_LIST =
            "http://10.0.2.2/absensi/public/api/cuti/list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuti_admin);

        rvCuti = findViewById(R.id.rvCuti);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        queue = Volley.newRequestQueue(this);

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

        StringRequest request = new StringRequest(
                Request.Method.GET,
                URL_LIST,
                response -> {

                    try {

                        JSONObject obj = new JSONObject(response);

                        boolean status = obj.optBoolean("status", false);

                        if (!status) {
                            Toast.makeText(this,
                                    obj.optString("message", "Gagal"),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray arr = obj.optJSONArray("data");

                        if (arr == null) {
                            Toast.makeText(this,
                                    "DATA NULL",
                                    Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(this,
                                "JSON ERROR: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this,
                            "NETWORK ERROR: " + error.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
        );

        queue.add(request);
    }

    // ================= UPDATE STATUS CUTI ================
    private void updateStatus(String id, String status) {

        // 🔥 FIX ROUTE SESUAI PHP
        String url = "http://10.0.2.2/absensi/public/api/cuti/updateStatus";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {

                    try {
                        JSONObject obj = new JSONObject(response);

                        boolean success = obj.optBoolean("status", false);
                        String msg = obj.optString("message", "OK");

                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

                        if (success) {
                            loadData(); // refresh
                        }

                    } catch (Exception e) {
                        Toast.makeText(this,
                                "Response error",
                                Toast.LENGTH_SHORT).show();
                    }

                },
                error -> Toast.makeText(this,
                        "Server Error",
                        Toast.LENGTH_SHORT).show()
        ) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> p = new HashMap<>();
                p.put("id", id);
                p.put("status", status); // DISETUJUI / DITOLAK

                return p;
            }
        };

        queue.add(request);
    }
}