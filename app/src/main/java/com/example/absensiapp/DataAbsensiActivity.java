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

                android.view.View view = getLayoutInflater()
                        .inflate(R.layout.dialog_edit_absensi, null);

                com.google.android.material.textfield.TextInputEditText etTanggal =
                        view.findViewById(R.id.etTanggal);

                com.google.android.material.textfield.TextInputEditText etJamMasuk =
                        view.findViewById(R.id.etJamMasuk);

                com.google.android.material.textfield.TextInputEditText etJamPulang =
                        view.findViewById(R.id.etJamPulang);

                com.google.android.material.textfield.TextInputEditText etStatus =
                        view.findViewById(R.id.etStatus);

                com.google.android.material.button.MaterialButton btnSimpan =
                        view.findViewById(R.id.btnSimpan);

                com.google.android.material.button.MaterialButton btnBatal =
                        view.findViewById(R.id.btnBatal);
                // isi data lama
                etTanggal.setText(a.getTanggal());
                etJamMasuk.setText(a.getJamMasuk());
                etJamPulang.setText(a.getJamKeluar()); // atau getJamPulang()
                etStatus.setText("hadir");
                androidx.appcompat.app.AlertDialog dialog =
                        new androidx.appcompat.app.AlertDialog.Builder(
                                DataAbsensiActivity.this)
                                .setView(view)
                                .create();
                dialog.show();

                btnBatal.setOnClickListener(v -> dialog.dismiss());

                btnSimpan.setOnClickListener(v -> {

                            String url =
                                    "http://10.0.2.2/absensi/public/api/absensi/update";

                            StringRequest request = new StringRequest(
                                    Request.Method.POST,
                                    url,
                                    response -> {

                                        Toast.makeText(
                                                DataAbsensiActivity.this,
                                                "Data berhasil diupdate",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        dialog.dismiss();
                                        loadData();
                                            },

                                    error -> {

                                        Toast.makeText(
                                                DataAbsensiActivity.this,
                                                error.toString(),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                            ) {
                                @Override
                                protected Map<String, String> getParams() {

                                    Map<String, String> params = new HashMap<>();

                                    params.put("id", a.getId());
                                    params.put("tanggal",
                                            etTanggal.getText().toString());

                                    params.put("jam_masuk",
                                            etJamMasuk.getText().toString());

                                    params.put("jam_pulang",
                                            etJamPulang.getText().toString());
                                    params.put("status",
                                            etStatus.getText().toString());

                                    return params;
                                }
                            };

                    Volley.newRequestQueue(
                            DataAbsensiActivity.this
                    ).add(request);

                });
            }
            @Override
            public void onDelete(AbsensiModel a) {

                String url = "http://10.0.2.2/absensi/public/api/absensi/delete";

                StringRequest request = new StringRequest(
                        Request.Method.POST,
                        url,
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
                ) {
                    @Override
                    protected Map<String, String> getParams() {

                        Map<String, String> params = new HashMap<>();

                        params.put("id", a.getId());

                        return params;
                    }
                };

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
                                    obj.getString("jam_pulang"),
                                    obj.getString("status")
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