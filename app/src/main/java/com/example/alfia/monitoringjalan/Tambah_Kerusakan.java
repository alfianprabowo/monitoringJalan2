package com.example.alfia.monitoringjalan;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.alfia.monitoringjalan.Bagian_Jalan.Bagian_Jalan;
import com.example.alfia.monitoringjalan.Database.DBHandler;
import com.example.alfia.monitoringjalan.Kerusakan.Kerusakan;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Tambah_Kerusakan extends AppCompatActivity implements OnMapReadyCallback {

    private DBHandler db;
    private SessionManager sessionManager;
    private String value, ruas_id, username;
    int user_id, session;
    private Spinner spinnerKerusakan, spinnerBagianJalan;
    private String rusak_id;

    private GoogleMap mMap;
    private Location mLastKnownLocation;
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);
    private final LatLng mDefaultLocation = new LatLng(-6.914744, 107.609810);
    private CameraPosition mCameraPosition;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;

    private static final String TAG = Tambah_Kerusakan.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        setContentView(R.layout.activity_tambah_kerusakan);
        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_add_kerusakan);
        mapFragment.getMapAsync(this);

        db = new DBHandler(this);
        sessionManager = new SessionManager(this);
        session = sessionManager.getKeySession();
        username = sessionManager.getKeyUsername();
        ruas_id = getIntent().getExtras().getString("ruas_id");

        user_id = db.getUserId(username);

        spinnerKerusakan = findViewById(R.id.spinnerTambahKerusakan);
        spinnerBagianJalan = findViewById(R.id.spinnerTambahBagJalan);

        final EditText editKM = findViewById(R.id.editKM_kerusakan);
        final EditText editM = findViewById(R.id.editM_kerusakan);
        final EditText editVolume = findViewById(R.id.editVolume_kerusakan);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        final String currentTime = df.format(c.getTime());

        addItemOnSpinnerKerusakan();
        addItemOnSpinnerBagianJalan();

        Button tambahKerusakan = findViewById(R.id.btn_tambahKerusakan);
        Button batal = findViewById(R.id.btn_batal_kerusakan);
        tambahKerusakan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rusak = spinnerKerusakan.getSelectedItem().toString();
                String jalan = spinnerBagianJalan.getSelectedItem().toString();
                String volume = editVolume.getText().toString();
                String km = editKM.getText().toString();
                String m = editM.getText().toString();

                if ((rusak.equals("Pilih Kerusakan")) || (jalan.equals("Pilih Bagian Jalan"))) {
                    Toast.makeText(getApplicationContext(), "Pilih Kerusakan dan Bagian Jalan !", Toast.LENGTH_SHORT).show();
                } else {
                    int id_kerusakan = db.getKerusakanId(rusak);
                    int id_jalan = db.getBagianJalanId(jalan);

                    if ((volume.equals(""))) {
                        Toast.makeText(getApplicationContext(), "Volume Harus Diisi!" + volume, Toast.LENGTH_SHORT).show();
                    } else if ((!rusak.equals("Pilih Kerusakan")) && (!jalan.equals("Pilih Bagian Jalan")) && (!volume.equals(""))) {

                        getLocation(id_kerusakan, id_jalan, currentTime, Integer.parseInt(km), Integer.parseInt(m), Float.parseFloat(volume), rusak, jalan);
                        Intent intent = new Intent(Tambah_Kerusakan.this, Daftar_Ruas.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putString("ruas_id", ruas_id);
                        mBundle.putString("rusak_id", rusak_id);
                        intent.putExtras(mBundle);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Tambah_Kerusakan.this, Daftar_Kerusakan.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("ruas_id", ruas_id);
                mBundle.putString("rusak_id", rusak_id);
                intent.putExtras(mBundle);
                startActivity(intent);
                finish();
            }
        });
    }


    private void sendNewKerusakan(final int id_kerusakan, final int id_bagian_jalan, final String date, final double lat, final double lng, final int km, final int m, final float volume, final String kerusakan, final String bagian_jalan) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://kepegbima.com/pjn2jabar/android/set_kerusakan.php?user_id=" + this.user_id + "&session=" + this.session;

        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("action", "insert");
            jsonParam.put("ruas_id", ruas_id);
            jsonParam.put("kerusakan_id", id_kerusakan);
            jsonParam.put("bagian_jalan_id", id_bagian_jalan);
            jsonParam.put("date_surveyed", date);
            jsonParam.put("lat", lat);
            jsonParam.put("lng", lng);
            jsonParam.put("point_km", km);
            jsonParam.put("point_m", m);
            jsonParam.put("volume", volume);
            jsonParam.put("fixed", 0);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        final String requestBody = jsonParam.toString();
        Log.d("JSON SEND KERUSAKAN", "bbbbbbbbbbbbbbbbbbbbbbb   " + requestBody);
        final StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("Session expired!")) {
                            db.deleteRecords();
                            sessionManager.clearData();
                            finish();
                        } else if (response.contains("true")) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                rusak_id = obj.getString("id");
                                Log.d("respon id KERUSAKAN", "bbbbbbbbbbbbbbbbbbbbbbb   " + rusak_id);
                                //masalah kalau offline id nya
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            db.tambahKerusakan(Integer.parseInt(ruas_id), id_kerusakan, id_bagian_jalan, date, lat, lng, km, m, volume, 0, kerusakan, bagian_jalan, 0);
                            Toast.makeText(getApplicationContext(), "Data berhasill disimpan di server.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Gagal menambahkan data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Log.d("Error.Response", error.getMessage());
                        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
                            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                            volleyError = error;
                        }
                        Log.d("Error.Response", volleyError.getMessage());
                    }
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };
        queue.add(postRequest);
    }

    private void addItemOnSpinnerKerusakan() {
        DBHandler db = new DBHandler(this);
        List<Kerusakan> listKerusakan = new ArrayList<>();
        listKerusakan = db.getAllNamaKerusakan();
        List<String> list1 = new ArrayList<String>();
        list1.add("Pilih Kerusakan");
        for (int i = 0; i < listKerusakan.size(); i++) {
            list1.add(listKerusakan.get(i).getNama());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Tambah_Kerusakan.this,
                android.R.layout.simple_spinner_item, list1);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerKerusakan.setAdapter(dataAdapter);
    }

    private void addItemOnSpinnerBagianJalan() {
        DBHandler db = new DBHandler(this);
        List<Bagian_Jalan> listBagianJalan = new ArrayList<>();
        listBagianJalan = db.getAllNamaBagianJalan();
        List<String> list2 = new ArrayList<>();
        list2.add("Pilih Bagian Jalan");
        for (int i = 0; i < listBagianJalan.size(); i++) {
            list2.add(listBagianJalan.get(i).getNama());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list2);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBagianJalan.setAdapter(dataAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if(mLastKnownLocation != null){
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
//                                    new LatLng(1, 1), DEFAULT_ZOOM));

                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private LatLng getLocation(final int id_kerusakan, final int id_bagian_jalan, final String date, final int km, final int m, final float volume, final String kerusakan, final String bagian_jalan) {
        LatLng latLng = new LatLng(0, 0);
        try {
            if (mLocationPermissionGranted) {
                Log.d("get loc", "geeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeet");
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            double latitude = mLastKnownLocation.getLatitude();
                            double longitude = mLastKnownLocation.getLongitude();
                            if(isOnline()){
                                /*try {
                                    Log.d("JSON add KERUSAKAN", "lati   " + latitude + " loong " + longitude);
                                    sendNewKerusakan(id_kerusakan, id_bagian_jalan, date, latitude, longitude, km, m, volume, kerusakan, bagian_jalan);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }*/
                                sendNewKerusakan(id_kerusakan, id_bagian_jalan, date, latitude, longitude, km, m, volume, kerusakan, bagian_jalan);
                            }else{
                                Log.d("add local KERUSAKAN", "lati   " + latitude + " loong " + longitude);
                                db.tambahKerusakan(Integer.parseInt(ruas_id), id_kerusakan, id_bagian_jalan, date, latitude, longitude, km, m, volume, 0, kerusakan, bagian_jalan, 1);
                            }
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
        return latLng;
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}
