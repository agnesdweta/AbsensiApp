package com.example.absensiapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AbsenActivity extends AppCompatActivity {

    private static final double OFFICE_LATITUDE = 37.421998333333335;
    private static final double OFFICE_LONGITUDE = -122.084;
    private static final float GEOFENCE_RADIUS_METERS = 100.0f;

    private static final int PERMISSION_REQUEST_CODE = 1001;

    private static final int OFFICE_START_HOUR = 3 ;
    private static final int OFFICE_START_GRACE_MINUTE = 10;
    private static final int OFFICE_END_HOUR = 19;

    private MaterialButton btnMasuk, btnKeluar;
    private TextView tvStatus, tvJamMasuk;
    private ImageView btnBack;
    private ImageView imgAbsen;

    private RequestQueue queue;
    private SharedPreferences sharedPreferences;
    private boolean cameraForCheckIn = true;
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d("ABSEN_DEBUG", "Kamera Closed");
                Log.d("ABSEN_DEBUG", "Result Code: " + result.getResultCode());

                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getExtras() != null) {
                        Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                        if (imageBitmap != null) {
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                            byte[] byteArray = byteArrayOutputStream.toByteArray();
                            String encodedImage = Base64.encodeToString(byteArray, Base64.NO_WRAP);

                            if (cameraForCheckIn) {
                                absenMasuk(currentLatitude, currentLongitude, encodedImage);
                            } else {
                                absenKeluar(currentLatitude, currentLongitude, encodedImage);
                            }
                        } else {
                            Toast.makeText(this, "Gagal mengambil foto: Data bitmap kosong", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Gagal mengambil foto: Data intent/extras kosong", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Pengambilan foto dibatalkan atau gagal (Code: " + result.getResultCode() + ")", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absen);

        btnMasuk = findViewById(R.id.btnMasuk);
        btnKeluar = findViewById(R.id.btnKeluar);

        tvStatus = findViewById(R.id.tvStatus);
        tvJamMasuk = findViewById(R.id.tvJamMasuk);

        btnBack = findViewById(R.id.btnBack);
        imgAbsen = findViewById(R.id.imgAbsen);

        queue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

        loadStatus();

        btnBack.setOnClickListener(v -> finish());
        btnMasuk.setOnClickListener(v -> checkPermissionAndStartFlow(true));
        btnKeluar.setOnClickListener(v -> {
            // Check-Out time validation (must be 07:00 PM / 19:00 or later)
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            if (hour < OFFICE_END_HOUR) {
                Toast.makeText(this, "Absen pulang hanya diperbolehkan mulai pukul 19:00 (07:00 PM)", Toast.LENGTH_LONG).show();
                return;
            }
            checkPermissionAndStartFlow(false);
        });
    }

    // ================= CEK ID =================
    private String getIdKaryawan() {
        String id = sharedPreferences.getString("id_karyawan", "");

        if (id == null || id.isEmpty()) {
            Toast.makeText(this, "ID Karyawan tidak ditemukan", Toast.LENGTH_SHORT).show();
            return null;
        }
        return id;
    }

    // ================= LOAD STATUS =================
    private void loadStatus() {

            String idKaryawan = getIdKaryawan();
            if (idKaryawan == null) return;

            String url =
                    "http://10.0.2.2/absensi/public/api/absen/status?id_karyawan=" + idKaryawan;

            StringRequest request = new StringRequest(
                    Request.Method.GET,
                    url,
                    response -> {
                        try {

                            Log.d("ABSEN_DEBUG", "loadStatus Response: " + response);

                            // ================= PARSE JSON (OBJECT, BUKAN ARRAY) =================
                            JSONObject responseObj = new JSONObject(response);
                            JSONObject data = responseObj.getJSONObject("data");

                            String statusAbsen = data.optString("status_absen", "BELUM ABSEN");
                            String jamMasuk = data.optString("jam_masuk", "--:--:--");
                            String jamPulang = data.optString("jam_pulang", "--:--:--");
                            String foto = data.optString("foto", "");

                            // ================= SET STATUS =================
                            tvStatus.setText("STATUS: " + statusAbsen);

                            if (statusAbsen.equalsIgnoreCase("BELUM ABSEN")) {

                                tvStatus.setBackgroundColor(
                                        getResources().getColor(android.R.color.holo_red_dark)
                                );
                                tvJamMasuk.setText("Belum absen hari ini");

                                imgAbsen.setImageDrawable(null);

                            } else if (statusAbsen.equalsIgnoreCase("HADIR")) {

                                tvStatus.setBackgroundColor(
                                        getResources().getColor(android.R.color.holo_green_dark)
                                );
                                tvJamMasuk.setText("Jam Masuk: " + jamMasuk);

                            } else if (statusAbsen.equalsIgnoreCase("PULANG")) {

                                tvStatus.setBackgroundColor(
                                        getResources().getColor(android.R.color.holo_blue_dark)
                                );
                                tvJamMasuk.setText("Jam Kerja: " + jamMasuk + " - " + jamPulang);
                            }

                            // ================= LOAD FOTO (GLIDE) =================
                            if (foto != null && !foto.isEmpty()) {

                                String imageUrl =
                                        "http://10.0.2.2/absensi/public/uploads/" + foto;

                                Log.d("ABSEN_DEBUG", "FOTO URL: " + imageUrl);

                                com.bumptech.glide.Glide.with(this)
                                        .load(imageUrl)
                                        .placeholder(android.R.drawable.ic_menu_gallery)
                                        .error(android.R.drawable.ic_delete)
                                        .into(imgAbsen);

                            } else {
                                imgAbsen.setImageDrawable(null);
                            }

                        } catch (Exception e) {
                            Log.e("ABSEN_DEBUG", "loadStatus JSON Error: ", e);
                            Toast.makeText(this,
                                    "Error JSON: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Log.e("ABSEN_DEBUG", "loadStatus Network Error: ", error);
                        Toast.makeText(this, "Gagal ambil status", Toast.LENGTH_SHORT).show();
                    }
            );

            queue.add(request);
        }
    // ================= ABSEN MASUK =================

    private void absenMasuk(final double lat, final double lng, final String photoBase64) {

        String idKaryawan = getIdKaryawan();
        if (idKaryawan == null) return;
        // Check-In time validation (09:00 AM start time + 10 mins grace period = 09:10 AM)
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        final String checkInStatus;
        if (hour < OFFICE_START_HOUR || (hour == OFFICE_START_HOUR && minute <= OFFICE_START_GRACE_MINUTE)) {
            checkInStatus = "On Time";
        } else {
            checkInStatus = "Late";
        }

        String url = "http://10.0.2.2/absensi/public/api/absen/masuk";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    try {
                        Log.d("ABSEN_DEBUG", "absenMasuk Response: " + response);
                        JSONObject obj = new JSONObject(response);

                        boolean status = obj.optString("status", "").equalsIgnoreCase("success") || obj.optBoolean("status", false);
                        String message = obj.getString("message");
                        Toast.makeText(this, message + " (" + checkInStatus + ")", Toast.LENGTH_SHORT).show();

                        if (status) {
                            loadStatus();
                        }

                    } catch (Exception e) {
                        Log.e("ABSEN_DEBUG", "absenMasuk Parsing Error: ", e);
                        Toast.makeText(this, "Response error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("ABSEN_DEBUG", "absenMasuk Network Error: ", error);
                    Toast.makeText(this, "Gagal koneksi", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("id_karyawan", idKaryawan);
                params.put("status", checkInStatus);
                params.put("latitude", String.valueOf(lat));
                params.put("longitude", String.valueOf(lng));
                params.put("foto", photoBase64);
                return params;
            }
        };

        queue.add(request);
    }

    // ================= ABSEN KELUAR =================
    private void absenKeluar(final double lat, final double lng, final String photoBase64) {

        String idKaryawan = getIdKaryawan();
        if (idKaryawan == null) return;

        String url = "http://10.0.2.2/absensi/public/api/absen/keluar";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    try {
                        Log.d("ABSEN_DEBUG", "absenKeluar Response: " + response);
                        JSONObject obj = new JSONObject(response);

                        boolean status = obj.optString("status", "").equalsIgnoreCase("success") || obj.optBoolean("status", false);
                        String message = obj.getString("message");

                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                        if (status) {
                            loadStatus();
                        }

                    } catch (Exception e) {
                        Log.e("ABSEN_DEBUG", "absenKeluar Parsing Error: ", e);
                        Toast.makeText(this, "Response error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("ABSEN_DEBUG", "absenKeluar Network Error: ", error);
                    Toast.makeText(this, "Server Error", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("id_karyawan", idKaryawan);
                params.put("latitude", String.valueOf(lat));
                params.put("longitude", String.valueOf(lng));
                params.put("foto", photoBase64);
                return params;
            }
        };

        queue.add(request);
    }
    // ================= GEOLOCATION & CAMERA FLOW =================
    private void checkPermissionAndStartFlow(boolean isCheckIn) {

        cameraForCheckIn = isCheckIn;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ||

                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED ||

                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);

        } else {
            retrieveLocationAndValidate();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                retrieveLocationAndValidate();
            } else {
                Toast.makeText(this, "Izin Lokasi dibutuhkan untuk melakukan absensi", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void retrieveLocationAndValidate() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            Toast.makeText(this, "Location Manager tidak tersedia", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGpsEnabled && !isNetworkEnabled) {
            Toast.makeText(this, "Harap aktifkan GPS / Layanan Lokasi Anda", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            Location bestLocation = null;
            if (isGpsEnabled) {
                bestLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (bestLocation == null && isNetworkEnabled) {
                bestLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (bestLocation != null) {
                validateGeofenceAndOpenCamera(bestLocation);
            } else {
                Toast.makeText(this, "Mendapatkan lokasi GPS...", Toast.LENGTH_SHORT).show();
                String provider = isGpsEnabled ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER;
                locationManager.requestSingleUpdate(provider, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        validateGeofenceAndOpenCamera(location);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    @Override
                    public void onProviderEnabled(@NonNull String provider) {
                    }

                    @Override
                    public void onProviderDisabled(@NonNull String provider) {
                    }
                }, null);
            }
        } catch (SecurityException e) {
            Log.e("ABSEN_DEBUG", "Location SecurityException: ", e);
            Toast.makeText(this, "Izin lokasi tidak diberikan", Toast.LENGTH_SHORT).show();
        }
    }
    private void validateGeofenceAndOpenCamera(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        Log.d("ABSEN_DEBUG", "Current Location: " + currentLatitude + ", " + currentLongitude);

        float[] results = new float[1];
        Location.distanceBetween(currentLatitude, currentLongitude, OFFICE_LATITUDE, OFFICE_LONGITUDE, results);
        float distance = results[0];

        Log.d("ABSEN_DEBUG", "Distance to Office: " + distance + " meters");

        if (distance > GEOFENCE_RADIUS_METERS) {
            Toast.makeText(this, "Anda Berada di Luar Radius Kantor! (Jarak: " + Math.round(distance) + "m)", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Lokasi terverifikasi. Membuka kamera...", Toast.LENGTH_SHORT).show();
            openFrontCamera();
        }
    }
    private void openFrontCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Log.d("ABSEN_DEBUG",
                "Camera App = " + intent.resolveActivity(getPackageManager()));

        if (intent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(intent);
        } else {
            Toast.makeText(this,
                    "Camera App Tidak Ditemukan",
                    Toast.LENGTH_LONG).show();
        }
    }
}