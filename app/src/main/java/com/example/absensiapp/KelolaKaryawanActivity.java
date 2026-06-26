package com.example.absensiapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KelolaKaryawanActivity extends AppCompatActivity {

    RecyclerView rvKaryawan;
    FloatingActionButton fabTambah;
    ImageView btnBack;

    ArrayList<KaryawanModel> list = new ArrayList<>();
    KaryawanAdapter adapter;
    RequestQueue requestQueue;

    private static final String BASE_URL =
            "http://10.0.2.2/absensi/public/api/karyawan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_karyawan);

        rvKaryawan = findViewById(R.id.rvKaryawan);
        fabTambah = findViewById(R.id.fabTambah);
        btnBack = findViewById(R.id.btnBack);

        requestQueue = Volley.newRequestQueue(this);

        rvKaryawan.setLayoutManager(new LinearLayoutManager(this));

        adapter = new KaryawanAdapter(list, new KaryawanAdapter.OnItemClick() {
            @Override
            public void onEdit(KaryawanModel karyawan) {
                showDialogEdit(karyawan);
            }

            @Override
            public void onDelete(KaryawanModel karyawan) {
                showDialogDelete(karyawan);
            }
        });

        rvKaryawan.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
        fabTambah.setOnClickListener(v -> showDialogTambah());

        loadKaryawan();
    }

    // ================= LOAD =================
    private void loadKaryawan() {

        StringRequest request = new StringRequest(Request.Method.GET, BASE_URL,
                response -> {
                    try {
                        list.clear();

                        JSONArray data;
                        try {
                            JSONObject obj = new JSONObject(response);
                            data = obj.getJSONArray("data");
                        } catch (Exception e) {
                            data = new JSONArray(response);
                        }

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);

                            list.add(new KaryawanModel(
                                    obj.optString("id"),
                                    obj.optString("nama"),
                                    obj.optString("jabatan"),
                                    obj.optString("telp"),
                                    obj.optString("username"),
                                    obj.optString("password")
                            ));
                        }

                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Toast.makeText(this, "Parse Error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal load", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }

    // ================= TAMBAH =================
    private void showDialogTambah() {

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_tambah_karyawan, null);

        EditText etNama = view.findViewById(R.id.etNama);
        EditText etJabatan = view.findViewById(R.id.etJabatan);
        EditText etTelp = view.findViewById(R.id.etTelp);
        EditText etUsername = view.findViewById(R.id.etUsername);
        EditText etPassword = view.findViewById(R.id.etPassword);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        dialog.show();

        view.findViewById(R.id.btnSimpan).setOnClickListener(v -> {

            StringRequest request = new StringRequest(Request.Method.POST,
                    BASE_URL + "/tambah",
                    response -> {
                        Toast.makeText(this, "Berhasil tambah", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadKaryawan();
                    },
                    error -> Toast.makeText(this, "Gagal tambah", Toast.LENGTH_SHORT).show()
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> p = new HashMap<>();
                    p.put("nama", etNama.getText().toString());
                    p.put("jabatan", etJabatan.getText().toString());
                    p.put("telp", etTelp.getText().toString());
                    p.put("username", etUsername.getText().toString());
                    p.put("password", etPassword.getText().toString());
                    return p;
                }
            };

            requestQueue.add(request);
        });

        view.findViewById(R.id.btnBatal).setOnClickListener(v -> dialog.dismiss());
    }

    // ================= EDIT =================
    private void showDialogEdit(KaryawanModel k) {

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_tambah_karyawan, null);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText("EDIT KARYAWAN");

        EditText etNama = view.findViewById(R.id.etNama);
        EditText etJabatan = view.findViewById(R.id.etJabatan);
        EditText etTelp = view.findViewById(R.id.etTelp);
        EditText etUsername = view.findViewById(R.id.etUsername);
        EditText etPassword = view.findViewById(R.id.etPassword);

        etNama.setText(k.getNama());
        etJabatan.setText(k.getJabatan());
        etTelp.setText(k.getTelp());
        etUsername.setText(k.getUsername());
        etPassword.setText(k.getPassword());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        dialog.show();

        view.findViewById(R.id.btnSimpan).setOnClickListener(v -> {

            updateKaryawan(
                    k.getId(),
                    etNama.getText().toString(),
                    etJabatan.getText().toString(),
                    etTelp.getText().toString(),
                    etUsername.getText().toString(),
                    etPassword.getText().toString(),
                    dialog
            );
        });

        view.findViewById(R.id.btnBatal).setOnClickListener(v -> dialog.dismiss());
    }

    // ================= UPDATE =================
    private void updateKaryawan(
            String id,
            String nama,
            String jabatan,
            String telp,
            String username,
            String password,
            AlertDialog dialog
    ) {

        String url = BASE_URL + "/update/" + id;

        StringRequest request = new StringRequest(Request.Method.POST,
                url,
                response -> {
                    Toast.makeText(this, "Berhasil update", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadKaryawan();
                },
                error -> Toast.makeText(this, "Gagal update", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("nama", nama);
                p.put("jabatan", jabatan);
                p.put("telp", telp);
                p.put("username", username);
                p.put("password", password);
                return p;
            }
        };

        requestQueue.add(request);
    }

    // ================= DELETE =================
    private void showDialogDelete(KaryawanModel k) {

        new AlertDialog.Builder(this)
                .setTitle("Hapus")
                .setMessage("Hapus " + k.getNama() + "?")
                .setPositiveButton("Hapus", (d, w) -> deleteKaryawan(k.getId()))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteKaryawan(String id) {

        String url = BASE_URL + "/" + id;

        StringRequest request = new StringRequest(Request.Method.DELETE,
                url,
                response -> {
                    Toast.makeText(this, "Berhasil hapus", Toast.LENGTH_SHORT).show();
                    loadKaryawan();
                },
                error -> Toast.makeText(this, "Gagal hapus", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }
}