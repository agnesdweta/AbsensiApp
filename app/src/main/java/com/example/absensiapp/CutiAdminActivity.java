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
    ImageView btnBack; // 🔥 TAMBAH INI

    ArrayList<CutiModel> list = new ArrayList<>();
    CutiAdapter adapter;
    RequestQueue queue;

    private static final String URL =
            "http://10.0.2.2/absensi/public/api/cuti/riwayat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuti_admin);

        rvCuti = findViewById(R.id.rvCuti);

        btnBack = findViewById(R.id.btnBack); // 🔥 TAMBAH INI

        // 🔥 BUTTON KEMBALI
        btnBack.setOnClickListener(v -> finish());

        queue = Volley.newRequestQueue(this);

        rvCuti.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CutiAdapter(list, new CutiAdapter.OnAction() {
            @Override
            public void onApprove(CutiModel c) {
                updateStatus(c.getId(), "Disetujui");
            }

            @Override
            public void onReject(CutiModel c) {
                updateStatus(c.getId(), "Ditolak");
            }
        }, true);

        rvCuti.setAdapter(adapter);

        loadData();
    }

    private void loadData() {

        StringRequest request = new StringRequest(Request.Method.GET, URL,
                response -> {

                    try {

                        list.clear();

                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");

                        for (int i = 0; i < arr.length(); i++) {

                            JSONObject o = arr.getJSONObject(i);

                            list.add(new CutiModel(
                                    o.optString("id"),
                                    o.optString("nama_karyawan"),
                                    o.optString("tanggal_mulai"),
                                    o.optString("tanggal_selesai"),
                                    o.optString("keterangan"),
                                    o.optString("status")
                            ));
                        }

                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Toast.makeText(this,
                                "Parse Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                },
                error -> Toast.makeText(this,
                        "Gagal load data",
                        Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }

    private void updateStatus(String id, String status) {

        String url =
                "http://10.0.2.2/absensi/public/api/cuti/update/" + id;

        StringRequest request = new StringRequest(Request.Method.POST,
                url,
                response -> {
                    Toast.makeText(this,
                            "Status berhasil diupdate",
                            Toast.LENGTH_SHORT).show();

                    loadData();
                },
                error -> Toast.makeText(this,
                        "Gagal update status",
                        Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("status", status);

                return params;
            }
        };

        queue.add(request);
    }
}