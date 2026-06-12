package com.example.absensiapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    MaterialCardView cardAbsen, cardCuti, cardProfil, cardRiwayatCuti, cardLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // INIT VIEW
        cardAbsen = findViewById(R.id.cardAbsen);
        cardCuti = findViewById(R.id.cardCuti);
        cardProfil = findViewById(R.id.cardProfil);
        cardRiwayatCuti = findViewById(R.id.cardRiwayatCuti);
        cardLogout = findViewById(R.id.cardLogout);

        // ABSEN
        cardAbsen.setOnClickListener(v ->
                startActivity(new Intent(this, AbsenActivity.class))
        );

        // CUTI
        cardCuti.setOnClickListener(v ->
                startActivity(new Intent(this, CutiActivity.class))
        );

        // RIWAYAT CUTI
        cardRiwayatCuti.setOnClickListener(v ->
                startActivity(new Intent(this, RiwayatCutiActivity.class))
        );

        // PROFIL
        cardProfil.setOnClickListener(v ->
                startActivity(new Intent(this, ProfilActivity.class))
        );

        // LOGOUT
        cardLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show();

            // TODO: kalau pakai login session, clear di sini

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}