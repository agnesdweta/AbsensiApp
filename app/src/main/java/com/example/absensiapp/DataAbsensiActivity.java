package com.example.absensiapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataAbsensiActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView btnBack;

    ArrayList<AbsensiModel> list = new ArrayList<>();
    AbsensiAdapter adapter;

    String URL = "http://10.0.2.2/absensi/public/api/absensi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_absensi);

        recyclerView = findViewById(R.id.recyclerAbsensi);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AbsensiAdapter(list, new AbsensiAdapter.OnAction() {

            @Override
            public void onEdit(AbsensiModel a) {
                Toast.makeText(DataAbsensiActivity.this,
                        "Edit : " + a.getNama(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDelete(AbsensiModel a) {

                String url = "http://10.0.2.2/absensi/public/api/absensi/delete/"
                        + a.getId();

                StringRequest request = new StringRequest(Request.Method.GET, url,
                        response -> {
                            Toast.makeText(DataAbsensiActivity.this,
                                    "Berhasil dihapus",
                                    Toast.LENGTH_SHORT).show();
                            loadData();
                        },
                        error -> {
                            Log.e("DELETE_ERROR", error.toString());
                            Toast.makeText(DataAbsensiActivity.this,
                                    "Gagal delete",
                                    Toast.LENGTH_SHORT).show();
                        }
                );

                Volley.newRequestQueue(DataAbsensiActivity.this).add(request);
            }
        });

        recyclerView.setAdapter(adapter);
        loadData();
    }

    private void loadData() {

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                response -> {

                    list.clear();

                    try {
                        for (int i = 0; i < response.length(); i++) {

                            JSONObject obj = response.getJSONObject(i);

                            list.add(new AbsensiModel(
                                    obj.getString("id"),
                                    obj.getString("nama"),
                                    obj.getString("tanggal"),
                                    obj.getString("jam_masuk"),
                                    obj.getString("jam_pulang")
                            ));
                        }

                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Toast.makeText(DataAbsensiActivity.this,
                                e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                },
                error -> Toast.makeText(DataAbsensiActivity.this,
                        "Gagal mengambil data",
                        Toast.LENGTH_LONG).show()
        );

        Volley.newRequestQueue(this).add(request);
    }
}