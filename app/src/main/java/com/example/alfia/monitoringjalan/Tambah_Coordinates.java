package com.example.alfia.monitoringjalan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import com.example.alfia.monitoringjalan.Database.DBHandler;
import com.example.alfia.monitoringjalan.Ruas.Ruas_Coordinates;
import com.example.alfia.monitoringjalan.Ruas.Ruas_Coordinates_ListAdapter;
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
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Tambah_Coordinates extends AppCompatActivity implements AdapterView.OnItemClickListener, OnMapReadyCallback, GoogleMap.OnPolylineClickListener {
    private static final String TAG = Tambah_Coordinates.class.getSimpleName();
    private GoogleMap mMap;
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
    private Location mLastKnownLocation;

    private ListView listView;
    private Ruas_Coordinates_ListAdapter adapter;
    private List<Ruas_Coordinates> listCoordinates = new ArrayList<>();

    private Marker marker;
    DBHandler db;
    SessionManager sessionManager;
    private String ruas_id, namaRuas, username;
    private int user_id, session;
    List<Ruas_Coordinates> localData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        setContentView(R.layout.activity_maps);
        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sessionManager = new SessionManager(this);
        username = sessionManager.getKeyUsername();
        ruas_id = getIntent().getExtras().getString("ruas_id");


        final TextView tvRuas = findViewById(R.id.tvRuas);
        final Button button_tambah = findViewById(R.id.tambah_button);
        button_tambah.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NewPoint(v.getContext());
            }
        });
        db = new DBHandler(this);
        listView = findViewById(R.id.lvCoordinates);

        user_id = db.getUserId(username);
        session = sessionManager.getKeySession();
        namaRuas = db.getNamaRuas(Integer.parseInt(ruas_id));
        tvRuas.setText("Ruas : " + namaRuas);
        localData = db.getLocalCoordinate(Integer.parseInt(ruas_id));

        if (isOnline()) {
            try {
                if (localData != null) {
                    sendLocalData();
                }
                db.getRuas_Detail(user_id, session, Integer.parseInt(ruas_id));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        listCoordinates = db.getTabelCoordinate(Integer.parseInt(ruas_id));
        adapter = new Ruas_Coordinates_ListAdapter(getApplicationContext(), listCoordinates);
        listView.setAdapter(adapter);
        if (listCoordinates != null) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, final View view, final int position, long l) {
                    new AlertDialog.Builder(Tambah_Coordinates.this)
                            .setTitle("Pilih : ")
                            .setIcon(R.drawable.perbaikan)

                            .setNegativeButton("Hapus", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    AlertDialog diaBox = AskOption(position);
                                    diaBox.show();
                                }
                            })
                            .setPositiveButton("Batal", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            })
                            .setNeutralButton("Tambah Pemanfaatan", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Ruas_Coordinates objCoordinates = db.getObjekCoordinate(Integer.parseInt(ruas_id), listCoordinates.get(position).getLat());

                                    Intent intent = new Intent(Tambah_Coordinates.this, Tambah_Pemanfaatan.class);
                                    Bundle mBundle = new Bundle();
                                    mBundle.putString("ruas_id", "" + ruas_id);
                                    mBundle.putString("ruas_date", "" + listCoordinates.get(position).getDate_surveyed());
                                    mBundle.putString("ruas_lat", "" + listCoordinates.get(position).getLat());
                                    mBundle.putString("ruas_lng", "" + listCoordinates.get(position).getLng());
                                    mBundle.putString("ruas_km", "" + listCoordinates.get(position).getPoint_km());
                                    mBundle.putString("ruas_m", "" + listCoordinates.get(position).getPoint_m());
                                    intent.putExtras(mBundle);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .show();
                }
            });

        }

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog diaBox = AskOption(i);
                diaBox.show();
                return true;
            }
        });
    }

    private void SendNewPoint(final double lat, final double lng, final int km, final int m, final String time) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://kepegbima.com/pjn2jabar/android/set_ruas_coordinate.php?user_id=" + this.user_id + "&session=" + this.session;
        JSONObject all_json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < listCoordinates.size(); i++) {
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("lat", listCoordinates.get(i).getLat() + "");
                jsonParam.put("lng", listCoordinates.get(i).getLng() + "");
                jsonParam.put("point_km", listCoordinates.get(i).getPoint_km() + "");
                jsonParam.put("point_m", listCoordinates.get(i).getPoint_m() + "");
                jsonParam.put("date_surveyed", listCoordinates.get(i).getDate_surveyed());

                jsonArray.put(jsonParam);
            }
            all_json.put("ruas_id", ruas_id);
            all_json.put("coordinates", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = all_json.toString();
        Log.d("JSON SEND RUTE", requestBody);

        final StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response add coordinate", response);
                        if (response.contains("Session expired!")) {
                            db.deleteRecords();
                            sessionManager.clearData();
                            finish();
                        } else if (response.contains("true")) {
                            Toast.makeText(getApplicationContext(), "Data berhasill disimpan diserver.", Toast.LENGTH_SHORT).show();
                            db.tambahRuasCoordinate(Integer.parseInt(ruas_id), lat, lng, km, m, time, 0);
                        } else {
                            Toast.makeText(getApplicationContext(), "Gagal menambahkan data.", Toast.LENGTH_SHORT).show();
                            //db.deleteLocalCoordinate(localData);

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Log.d("Error.Response", error.getMessage());
                        if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
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

    private void drawLine() {
        mMap.clear();
        if (this.listCoordinates.size() > 0) {
            List<LatLng> p = new ArrayList<>();
            for (int i = 0; i < this.listCoordinates.size(); i++) {
                p.add(new LatLng(this.listCoordinates.get(i).getLat(), this.listCoordinates.get(i).getLng()));
                mMap.addMarker(new MarkerOptions().position(p.get(i)).title(this.listCoordinates.get(i).getLat() + ", " + this.listCoordinates.get(i).getLng()));
            }
            Polyline polyline2 = mMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .addAll(p));
            polyline2.setTag("--- Nama Rute ---");

            stylePolyline(polyline2);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(p.get(0), 17));
            mMap.setOnPolylineClickListener(this);
        }
    }

    private void NewPoint(Context ctx) {
        LayoutInflater li = LayoutInflater.from(ctx);
        View promptsView = li.inflate(R.layout.activity_tambah_ruas_coordinate, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
        alertDialogBuilder.setView(promptsView);

        final EditText km = promptsView.findViewById(R.id.km);
        final EditText m = promptsView.findViewById(R.id.m);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("TAMBAH",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String km_string = km.getText().toString();
                                String m_string = m.getText().toString();
                                if ((km_string != "") && (m_string != "")) {
                                    int km = Integer.parseInt(km_string);
                                    int m = Integer.parseInt(m_string);
                                    getLocation(km, m);
                                }
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("BATAL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private AlertDialog AskOption(int idx) {
        final int i = idx;
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                .setTitle("HAPUS")
                .setMessage("Apakah Anda akan menghapus titik koordinat (" + listCoordinates.get(i).getLat() + "," + listCoordinates.get(i).getLng() + ") ?")
                .setPositiveButton("HAPUS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (isOnline()) {
                            removePoints(i, listCoordinates.get(i).getLat(), listCoordinates.get(i).getLng(), listCoordinates.get(i).getPoint_km(), listCoordinates.get(i).getPoint_m(), listCoordinates.get(i).getDate_surveyed());
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        } else {
                            //Toast.makeText(getApplicationContext(), "Tidak Ada koneksi internet.", Toast.LENGTH_SHORT).show();
                        }
                    }

                })
                .setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;
    }

    private void removePoints(int i, double lat, double lng, int km, int m, String time) {
        listCoordinates.remove(i);
        adapter.notifyDataSetChanged();
        drawLine();
        SendNewPoint(lat, lng, km, m, time);
        /*
        db.deletePemanfaatan();*/
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
    public void onMapReady(GoogleMap map) {
        mMap = map;
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
        drawLine();
    }

    private void stylePolyline(Polyline polyline) {
        String type = "";
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }
        switch (type) {
            case "A":
                polyline.setStartCap(new RoundCap());
            case "B":
                polyline.setStartCap(new RoundCap());
                break;
        }

        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_ORANGE_ARGB);
        polyline.setJointType(JointType.ROUND);
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }
        Log.d("Tambah coordinate", "ffffffffffffffffffffffffffff " + polyline.getTag().toString());
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            //if(mLastKnownLocation!=  null){
                                mLastKnownLocation = task.getResult();
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                //                                    new LatLng(1, 1), DEFAULT_ZOOM));

                            //}

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

    private LatLng getLocation(final int km, final int m) {
        LatLng latLng = new LatLng(0, 0);

        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            double latitude = mLastKnownLocation.getLatitude();
                            double longitude = mLastKnownLocation.getLongitude();
                            Log.d("lat ", "asfasfafasfasf" + latitude + " " + longitude);
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                            final String currentTime = df.format(c.getTime());
                            listCoordinates.add(new Ruas_Coordinates(Integer.parseInt(ruas_id), latitude, longitude, km, m, currentTime, 1));
                            adapter.notifyDataSetChanged();
                            if (isOnline()) {
                                SendNewPoint(latitude, longitude, km, m, currentTime);
                            } else {
                                db.tambahRuasCoordinate(Integer.parseInt(ruas_id), latitude, longitude, km, m, currentTime, 1);
                            }
                            drawLine();
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

    private void sendLocalData() {
        for (int i = 0; i < 50; i++) {
            if (i < localData.size()) {
                listCoordinates.add(i, localData.get(i));
                SendNewPoint(listCoordinates.get(i).getLat(), listCoordinates.get(i).getLng(), listCoordinates.get(i).getPoint_km(), listCoordinates.get(i).getPoint_m(), listCoordinates.get(i).getDate_surveyed());
                db.deleteCoordinate(listCoordinates.get(i).getLat(), listCoordinates.get(i).getLng(), listCoordinates.get(i).getPoint_km(), listCoordinates.get(i).getPoint_m(), listCoordinates.get(i).getDate_surveyed(), 1);
            }

        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
