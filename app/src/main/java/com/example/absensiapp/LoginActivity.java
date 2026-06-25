package com.example.absensiapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etUsername, etPassword;
    MaterialButton btnLogin;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Isi semua data", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/absensi/public/api/login";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {

                    try {

                        Log.d("LOGIN_DEBUG", "Response = " + response);

                        JSONObject obj = new JSONObject(response);

                        boolean status = obj.optBoolean("status", false);

                        if (!status) {
                            Toast.makeText(
                                    this,
                                    obj.optString("message", "Login gagal"),
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }

                        // ================= AMBIL DATA USER =================
                        JSONObject data = obj.optJSONObject("data");

                        if (data == null) {
                            Toast.makeText(
                                    this,
                                    "Login gagal: data user kosong",
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }

                        Log.d("LOGIN_DEBUG", "DATA = " + data.toString());

                        String idKaryawan = data.optString("id_karyawan", "");
                        String idUser = data.optString("id_user", "");
                        String nama = data.optString("nama", "");
                        String role = data.optString("role", "");

                        // Fallback jika API hanya mengirim field id
                        if (idKaryawan.isEmpty()) {
                            idKaryawan = data.optString("id", "");
                        }

                        if (idUser.isEmpty()) {
                            idUser = data.optString("id", "");
                        }

                        Log.d("LOGIN_DEBUG", "idKaryawan = " + idKaryawan);
                        Log.d("LOGIN_DEBUG", "idUser = " + idUser);
                        Log.d("LOGIN_DEBUG", "nama = " + nama);
                        Log.d("LOGIN_DEBUG", "role = " + role);

                        // Admin tidak wajib punya id_karyawan
                        if (!role.equalsIgnoreCase("admin") && idKaryawan.isEmpty()) {
                            Toast.makeText(
                                    this,
                                    "Login gagal: ID karyawan tidak ditemukan",
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }

                        // ================= SIMPAN SESSION =================
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString("id_karyawan", idKaryawan);
                        editor.putString("id_user", idUser);
                        editor.putString("nama", nama);
                        editor.putString("role", role);

                        editor.remove("jam_masuk");
                        editor.remove("jam_pulang");
                        editor.remove("status_absen");

                        editor.apply();

                        Log.d("LOGIN_DEBUG",
                                "SESSION TERSIMPAN : " +
                                        sharedPreferences.getString("id_karyawan", ""));

                        Toast.makeText(
                                this,
                                "Login berhasil",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent intent;

                        if ("admin".equalsIgnoreCase(role)) {
                            intent = new Intent(
                                    LoginActivity.this,
                                    AdminDashboardActivity.class
                            );
                        } else {
                            intent = new Intent(
                                    LoginActivity.this,
                                    MainActivity.class
                            );
                        }

                        startActivity(intent);
                        finish();

                    } catch (Exception e) {

                        Log.e("LOGIN_DEBUG", "ERROR", e);

                        Toast.makeText(
                                this,
                                "JSON ERROR : " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                },
                error -> {

                    Log.e("LOGIN_DEBUG", "VOLLEY ERROR", error);

                    Toast.makeText(
                            this,
                            "Server error / tidak bisa diakses",
                            Toast.LENGTH_LONG
                    ).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put("username", username);
                params.put("password", password);

                return params;
            }
        };

        request.setShouldCache(false);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}