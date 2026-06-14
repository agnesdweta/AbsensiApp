package com.example.absensiapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfilActivity extends AppCompatActivity {

    ImageView btnBack;

    TextView tvNama, tvIdKaryawan, tvRole;

    TextView btnEditProfil,
            btnGantiPassword,
            tvLogout;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        btnBack = findViewById(R.id.btnBack);

        tvNama = findViewById(R.id.tvNama);
        tvIdKaryawan = findViewById(R.id.tvIdKaryawan);
        tvRole = findViewById(R.id.tvRole);

        btnEditProfil = findViewById(R.id.btnEditProfil);
        btnGantiPassword = findViewById(R.id.btnGantiPassword);
        tvLogout = findViewById(R.id.tvLogout);

        preferences = getSharedPreferences("user", MODE_PRIVATE);

        loadProfil();

        btnBack.setOnClickListener(v -> finish());

        btnEditProfil.setOnClickListener(v -> showDialogEditProfil());

        btnGantiPassword.setOnClickListener(v -> showDialogPassword());

        tvLogout.setOnClickListener(v -> {

            Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show();

            preferences.edit().clear().apply();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        });
    }

    // ================= LOAD PROFIL =================
    private void loadProfil() {

        String nama = preferences.getString("nama", "-");
        String idKaryawan = preferences.getString("id_karyawan", "-");
        String role = preferences.getString("role", "-");

        tvNama.setText(nama);
        tvIdKaryawan.setText("ID: " + idKaryawan);
        tvRole.setText(role);
    }

    // ================= EDIT PROFIL =================
    private void showDialogEditProfil() {

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_edit_profil, null);

        EditText etNama = view.findViewById(R.id.etNama);
        EditText etRole = view.findViewById(R.id.etRole);

        etNama.setText(tvNama.getText().toString());
        etRole.setText(tvRole.getText().toString());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        dialog.show();

        view.findViewById(R.id.btnSimpan)
                .setOnClickListener(v -> {

                    String namaBaru = etNama.getText().toString().trim();
                    String roleBaru = etRole.getText().toString().trim();
                    String idUser = preferences.getString("id_user", "");

                    String url = "http://10.0.2.2/absensi/public/api/auth/updateProfil";

                    StringRequest request = new StringRequest(
                            Request.Method.POST,
                            url,
                            response -> {

                                try {
                                    JSONObject obj = new JSONObject(response);

                                    if (obj.getBoolean("status")) {

                                        JSONObject data = obj.getJSONObject("data");

                                        // ================= UPDATE LOCAL STORAGE =================
                                        preferences.edit()
                                                .putString("id_user", idUser)
                                                .putString("nama", data.getString("nama"))
                                                .putString("role", data.getString("role"))
                                                .putString("id_karyawan", data.optString("id_karyawan", ""))
                                                .apply();

                                        loadProfil();

                                        Toast.makeText(this,
                                                "Profil berhasil diupdate",
                                                Toast.LENGTH_SHORT).show();

                                        dialog.dismiss();

                                    } else {
                                        Toast.makeText(this,
                                                obj.optString("message", "Gagal update"),
                                                Toast.LENGTH_SHORT).show();
                                    }

                                } catch (Exception e) {
                                    Toast.makeText(this,
                                            "JSON error: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            },
                            error -> Toast.makeText(this,
                                    "Server error",
                                    Toast.LENGTH_SHORT).show()
                    ) {
                        @Override
                        protected Map<String, String> getParams() {

                            Map<String, String> p = new HashMap<>();
                            p.put("id_user", idUser);
                            p.put("nama", namaBaru);
                            p.put("role", roleBaru);
                            return p;
                        }
                    };

                    Volley.newRequestQueue(this).add(request);
                });

        view.findViewById(R.id.btnBatal)
                .setOnClickListener(v -> dialog.dismiss());
    }

    // ================= GANTI PASSWORD =================
    private void showDialogPassword() {

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_ganti_password, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        dialog.show();

        view.findViewById(R.id.btnSimpanPassword)
                .setOnClickListener(v -> {

                    Toast.makeText(this,
                            "Password berhasil diganti",
                            Toast.LENGTH_SHORT).show();

                    dialog.dismiss();
                });

        view.findViewById(R.id.btnBatalPassword)
                .setOnClickListener(v -> dialog.dismiss());
    }
}