package com.example.absensiapp;

import android.os.Bundle;
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

        // INIT VIEW
        recyclerView = findViewById(R.id.recyclerCuti);
        btnBack = findViewById(R.id.btnBack);

        // BACK BUTTON
        btnBack.setOnClickListener(v -> finish());

        // RECYCLER
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CutiAdapter(list, null, false);
        recyclerView.setAdapter(adapter);

        loadData();
    }

    private void loadData() {

        StringRequest request = new StringRequest(Request.Method.GET, URL,
                response -> {

                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");

                        list.clear();

                        for (int i = 0; i < arr.length(); i++) {

                            JSONObject item = arr.getJSONObject(i);

                            list.add(new CutiModel(
                                    "", // id kosong
                                    "", // nama kosong
                                    item.optString("tanggal_mulai"),
                                    item.optString("tanggal_selesai"),
                                    item.optString("keterangan"),
                                    item.optString("status")
                            ));
                        }

                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Toast.makeText(this,
                                "Parse Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                },
                error -> {

                    String msg = "Gagal load data";

                    if (error.networkResponse != null) {
                        msg = "CODE: " + error.networkResponse.statusCode;
                    }

                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }
}