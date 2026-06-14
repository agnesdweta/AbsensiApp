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

                        Log.d("LOGIN_RESPONSE", response);

                        JSONObject obj = new JSONObject(response);

                        boolean status = obj.optBoolean("status", false);

                        if (!status) {
                            Toast.makeText(this,
                                    obj.optString("message", "Login gagal"),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONObject data = obj.getJSONObject("data");

                        String idUser = data.optString("id_user", "");
                        String idKaryawan = data.optString("id_karyawan", "");
                        String nama = data.optString("nama", "");
                        String role = data.optString("role", "");

                        if (idUser.isEmpty()) {
                            Toast.makeText(this,
                                    "Login gagal: data user kosong",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        // ================= SAVE USER =================
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString("id_user", idUser);
                        editor.putString("id_karyawan", idKaryawan);
                        editor.putString("nama", nama);
                        editor.putString("role", role);

                        // ================= RESET ABSENSI CACHE =================
                        editor.remove("jam_masuk");
                        editor.remove("jam_pulang");
                        editor.remove("status_absen");

                        editor.apply();

                        Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show();

                        Intent intent;

                        if ("admin".equalsIgnoreCase(role)) {
                            intent = new Intent(this, AdminDashboardActivity.class);
                        } else {
                            intent = new Intent(this, MainActivity.class);
                        }

                        startActivity(intent);
                        finish();

                    } catch (Exception e) {
                        Toast.makeText(this,
                                "JSON ERROR: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                },
                error -> {

                    error.printStackTrace();

                    Toast.makeText(this,
                            "Server error / tidak bisa diakses",
                            Toast.LENGTH_LONG).show();
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

        request.setShouldCache(false); // 🔥 ANTI CACHE WAJIB

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}