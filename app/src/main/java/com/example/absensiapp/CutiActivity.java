package com.example.absensiapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CutiActivity extends AppCompatActivity {

    EditText etTanggalMulai, etTanggalSelesai, etAlasan;
    MaterialButton btnAjukanCuti;
    ImageView btnBack;

    // URL API
    String URL_CUTI =
            "http://10.0.2.2/absensi/public/api/cuti/ajukan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuti);

        // ================= INIT =================
        etTanggalMulai = findViewById(R.id.etTanggalMulai);
        etTanggalSelesai = findViewById(R.id.etTanggalSelesai);
        etAlasan = findViewById(R.id.etAlasan);

        btnAjukanCuti = findViewById(R.id.btnAjukanCuti);
        btnBack = findViewById(R.id.btnBack);

        // ================= BUTTON BACK =================
        btnBack.setOnClickListener(v -> finish());

        // ================= DISABLE KEYBOARD =================
        etTanggalMulai.setFocusable(false);
        etTanggalSelesai.setFocusable(false);

        // ================= DATE PICKER =================
        etTanggalMulai.setOnClickListener(v ->
                showDatePicker(etTanggalMulai));

        etTanggalSelesai.setOnClickListener(v ->
                showDatePicker(etTanggalSelesai));

        // ================= BUTTON AJUKAN =================
        btnAjukanCuti.setOnClickListener(v ->
                kirimCuti());
    }

    // ================= DATE PICKER =================
    private void showDatePicker(EditText editText) {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,

                (view, selectedYear, selectedMonth, selectedDay) -> {

                    String tanggal =
                            selectedYear + "-" +
                                    String.format("%02d", selectedMonth + 1) + "-" +
                                    String.format("%02d", selectedDay);

                    editText.setText(tanggal);
                },

                year,
                month,
                day
        );

        dialog.show();
    }

    // ================= KIRIM CUTI =================
    private void kirimCuti() {

        String tanggalMulai =
                etTanggalMulai.getText().toString().trim();

        String tanggalSelesai =
                etTanggalSelesai.getText().toString().trim();

        String alasan =
                etAlasan.getText().toString().trim();

        String namaKaryawan = "Karyawan Android";

        // ================= VALIDASI =================

        if (tanggalMulai.isEmpty()) {

            etTanggalMulai.setError(
                    "Tanggal mulai wajib diisi");

            return;
        }

        if (tanggalSelesai.isEmpty()) {

            etTanggalSelesai.setError(
                    "Tanggal selesai wajib diisi");

            return;
        }

        if (alasan.isEmpty()) {

            etAlasan.setError(
                    "Alasan cuti wajib diisi");

            return;
        }

        // ================= LOADING =================

        btnAjukanCuti.setEnabled(false);
        btnAjukanCuti.setText("Mengirim...");

        // ================= REQUEST API =================

        StringRequest request = new StringRequest(

                Request.Method.POST,
                URL_CUTI,

                response -> {

                    btnAjukanCuti.setEnabled(true);
                    btnAjukanCuti.setText("Ajukan Cuti");

                    Toast.makeText(
                            CutiActivity.this,
                            response,
                            Toast.LENGTH_LONG
                    ).show();

                    // RESET FORM
                    etTanggalMulai.setText("");
                    etTanggalSelesai.setText("");
                    etAlasan.setText("");
                },

                error -> {

                    btnAjukanCuti.setEnabled(true);
                    btnAjukanCuti.setText("Ajukan Cuti");

                    Toast.makeText(
                            CutiActivity.this,
                            error.toString(),
                            Toast.LENGTH_LONG
                    ).show();
                }

        ) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params =
                        new HashMap<>();

                params.put(
                        "nama_karyawan",
                        namaKaryawan
                );

                params.put(
                        "tanggal_mulai",
                        tanggalMulai
                );

                params.put(
                        "tanggal_selesai",
                        tanggalSelesai
                );

                params.put(
                        "keterangan",
                        alasan
                );

                return params;
            }
        };

        // ================= JALANKAN REQUEST =================

        RequestQueue queue =
                Volley.newRequestQueue(this);

        queue.add(request);
    }
}