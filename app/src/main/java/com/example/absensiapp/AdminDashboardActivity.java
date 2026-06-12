package com.example.absensiapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class AdminDashboardActivity extends AppCompatActivity {

    MaterialCardView cardDataKaryawan, cardAbsensi, cardDataCuti, cardLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        cardDataKaryawan = findViewById(R.id.cardDataKaryawan);
        cardAbsensi = findViewById(R.id.cardAbsensi);
        cardDataCuti = findViewById(R.id.cardDataCuti);
        cardLogout = findViewById(R.id.cardLogout);

        // DATA KARYAWAN → PINDAH HALAMAN
        cardDataKaryawan.setOnClickListener(v -> {
            Intent intent = new Intent(this, KelolaKaryawanActivity.class);
            startActivity(intent);
        });
        cardAbsensi.setOnClickListener(v -> {
            startActivity(
                    new Intent(
                            AdminDashboardActivity.this,
                            DataAbsensiActivity.class )
            );
        });

        // DATA CUTI → PINDAH HALAMAN
        cardDataCuti.setOnClickListener(v -> {
            Intent intent = new Intent(this, CutiAdminActivity.class);
            startActivity(intent);
        });

        // LOGOUT
        cardLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}