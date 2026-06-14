package com.example.absensiapp;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
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

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CutiActivity extends AppCompatActivity {

    EditText etTanggalMulai, etTanggalSelesai, etAlasan;
    MaterialButton btnAjukanCuti;
    ImageView btnBack;

    RequestQueue queue;
    SharedPreferences sharedPreferences;

    String URL_CUTI = "http://10.0.2.2/absensi/public/api/cuti/ajukan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuti);

        etTanggalMulai = findViewById(R.id.etTanggalMulai);
        etTanggalSelesai = findViewById(R.id.etTanggalSelesai);
        etAlasan = findViewById(R.id.etAlasan);

        btnAjukanCuti = findViewById(R.id.btnAjukanCuti);
        btnBack = findViewById(R.id.btnBack);

        queue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

        btnBack.setOnClickListener(v -> finish());

        etTanggalMulai.setFocusable(false);
        etTanggalSelesai.setFocusable(false);

        etTanggalMulai.setOnClickListener(v -> showDatePicker(etTanggalMulai));
        etTanggalSelesai.setOnClickListener(v -> showDatePicker(etTanggalSelesai));

        btnAjukanCuti.setOnClickListener(v -> kirimCuti());
    }

    private String getIdKaryawan() {
        String id = sharedPreferences.getString("id_karyawan", "");

        if (id == null || id.isEmpty()) {
            Toast.makeText(this, "ID karyawan tidak ditemukan", Toast.LENGTH_SHORT).show();
            return null;
        }
        return id;
    }

    private void showDatePicker(EditText editText) {

        Calendar c = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {

                    String tanggal =
                            year + "-" +
                                    String.format("%02d", month + 1) + "-" +
                                    String.format("%02d", day);

                    editText.setText(tanggal);
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void kirimCuti() {

        String idKaryawan = getIdKaryawan();
        if (idKaryawan == null) return;

        String tanggalMulai = etTanggalMulai.getText().toString().trim();
        String tanggalSelesai = etTanggalSelesai.getText().toString().trim();
        String alasan = etAlasan.getText().toString().trim();

        if (tanggalMulai.isEmpty()) {
            etTanggalMulai.setError("Wajib diisi");
            return;
        }

        if (tanggalSelesai.isEmpty()) {
            etTanggalSelesai.setError("Wajib diisi");
            return;
        }

        if (alasan.isEmpty()) {
            etAlasan.setError("Wajib diisi");
            return;
        }

        btnAjukanCuti.setEnabled(false);
        btnAjukanCuti.setText("Mengirim...");

        StringRequest request = new StringRequest(
                Request.Method.POST,
                URL_CUTI,
                response -> {

                    btnAjukanCuti.setEnabled(true);
                    btnAjukanCuti.setText("Ajukan Cuti");

                    try {
                        JSONObject obj = new JSONObject(response);

                        boolean status = obj.optBoolean("status", false);
                        String message = obj.optString("message", "OK");

                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                        if (status) {
                            etTanggalMulai.setText("");
                            etTanggalSelesai.setText("");
                            etAlasan.setText("");
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Response error JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {

                    btnAjukanCuti.setEnabled(true);
                    btnAjukanCuti.setText("Ajukan Cuti");

                    Toast.makeText(this, "Server Error", Toast.LENGTH_SHORT).show();
                }
        ) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put("id_karyawan", idKaryawan);
                params.put("tanggal_mulai", tanggalMulai);
                params.put("tanggal_selesai", tanggalSelesai);
                params.put("keterangan", alasan);

                return params;
            }
        };

        queue.add(request);
    }
}