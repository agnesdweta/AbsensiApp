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

public class ProfilActivity extends AppCompatActivity {

    ImageView btnBack;

    TextView tvNama, tvEmail;

    TextView btnEditProfil,
            btnGantiPassword,
            tvLogout;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        // INIT
        btnBack = findViewById(R.id.btnBack);

        tvNama = findViewById(R.id.tvNama);
        tvEmail = findViewById(R.id.tvEmail);

        btnEditProfil = findViewById(R.id.btnEditProfil);
        btnGantiPassword = findViewById(R.id.btnGantiPassword);
        tvLogout = findViewById(R.id.tvLogout);

        preferences = getSharedPreferences("LOGIN", MODE_PRIVATE);

        // LOAD DATA
        loadProfil();

        // BACK
        btnBack.setOnClickListener(v -> finish());

        // EDIT PROFIL
        btnEditProfil.setOnClickListener(v -> {
            showDialogEditProfil();
        });

        // GANTI PASSWORD
        btnGantiPassword.setOnClickListener(v -> {
            showDialogPassword();
        });

        // LOGOUT
        tvLogout.setOnClickListener(v -> {

            Toast.makeText(this,
                    "Logout berhasil",
                    Toast.LENGTH_SHORT).show();

            preferences.edit()
                    .remove("isLogin")
                    .apply();

            Intent intent =
                    new Intent(this, LoginActivity.class);

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });
    }

    // ================= LOAD PROFIL =================
    private void loadProfil() {

        String nama = preferences.getString(
                "nama",
                "Agnes Dwetasari"
        );

        String email = preferences.getString(
                "email",
                "agnes@gmail.com"
        );

        tvNama.setText(nama);
        tvEmail.setText(email);
    }

    // ================= EDIT PROFIL =================
    private void showDialogEditProfil() {

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_edit_profil, null);

        EditText etNama = view.findViewById(R.id.etNama);
        EditText etEmail = view.findViewById(R.id.etEmail);

        etNama.setText(tvNama.getText().toString());
        etEmail.setText(tvEmail.getText().toString());

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setView(view)
                        .create();

        dialog.show();

        view.findViewById(R.id.btnSimpan)
                .setOnClickListener(v -> {

                    String namaBaru =
                            etNama.getText().toString();

                    String emailBaru =
                            etEmail.getText().toString();

                    // SIMPAN
                    preferences.edit()
                            .putString("nama", namaBaru)
                            .putString("email", emailBaru)
                            .apply();

                    // REFRESH
                    loadProfil();

                    Toast.makeText(this,
                            "Profil berhasil diupdate",
                            Toast.LENGTH_SHORT).show();

                    dialog.dismiss();
                });

        view.findViewById(R.id.btnBatal)
                .setOnClickListener(v ->
                        dialog.dismiss());
    }

    // ================= GANTI PASSWORD =================
    private void showDialogPassword() {

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_ganti_password, null);

        AlertDialog dialog =
                new AlertDialog.Builder(this)
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
                .setOnClickListener(v ->
                        dialog.dismiss());
    }
}