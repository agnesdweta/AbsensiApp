package com.example.absensiapp;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Isi semua data", Toast.LENGTH_SHORT).show();
            return;
        }

        // GANTI jika pakai HP asli
        String url = "http://10.0.2.2/absensi/public/api/login";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    try {
                        Log.d("LOGIN_RESPONSE", response);

                        JSONObject obj = new JSONObject(response);

                        boolean status = obj.getBoolean("status");

                        if (status) {

                            if (!obj.isNull("data")) {

                                JSONObject data = obj.getJSONObject("data");

                                String role = data.getString("role").trim().toLowerCase();
                                String usernameResult = data.getString("username");

                                Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show();

                                Intent intent;

                                if (role.equals("admin")) {
                                    intent = new Intent(this, AdminDashboardActivity.class);
                                } else {
                                    intent = new Intent(this, MainActivity.class);
                                }

                                intent.putExtra("username", usernameResult);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(this, "Data kosong dari server", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(this,
                                    obj.optString("message", "Login gagal"),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(this,
                                "Error JSON: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();

                        Log.e("LOGIN_ERROR", e.toString());
                    }

                },
                error -> {
                    error.printStackTrace();
                    Log.e("VOLLEY_ERROR", error.toString());

                    if (error.networkResponse != null) {
                        Toast.makeText(this,
                                "HTTP ERROR: " + error.networkResponse.statusCode,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this,
                                "Server tidak bisa diakses",
                                Toast.LENGTH_LONG).show();
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }

            // ✅ FIX: header penting untuk backend PHP / CI
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}