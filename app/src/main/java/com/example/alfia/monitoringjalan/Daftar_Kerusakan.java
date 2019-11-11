package com.example.alfia.monitoringjalan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import com.example.alfia.monitoringjalan.Kerusakan.Rusak;
import com.example.alfia.monitoringjalan.Kerusakan.Rusak_ListAdapter;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
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
import java.util.concurrent.ExecutionException;

import io.github.yavski.fabspeeddial.FabSpeedDial;

public class Daftar_Kerusakan extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener {

    private ListView lvKerusakan;
    private Rusak_ListAdapter adapter;
    private List<Rusak> listRusak = new ArrayList<>();
    private DBHandler db;
    private SessionManager sessionManager;
    private Rusak objRusak;
    private String ruas_id, namaRuas, username;
    int user_id, session;
    private List<Rusak> localData = new ArrayList<>();

    private static final String TAG = Daftar_Kerusakan.class.getSimpleName();
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
    private final LatLng mDefaultLocation = new LatLng(6.914744, 107.609810);
    private CameraPosition mCameraPosition;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        setContentView(R.layout.activity_daftar_kerusakan);
        db = new DBHandler(this);
        FabSpeedDial dial = findViewById(R.id.fabRusak);

        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_kerusakan);
        mapFragment.getMapAsync(this);

        sessionManager = new SessionManager(this);
        ruas_id = getIntent().getExtras().getString("ruas_id");
        namaRuas = db.getNamaRuas(Integer.parseInt(ruas_id));
        session = sessionManager.getKeySession();
        lvKerusakan = findViewById(R.id.listViewRusak);
        username = sessionManager.getKeyUsername();
        user_id = db.getUserId(username);
        localData = db.getLocalKerusakan(Integer.parseInt(ruas_id));

        if (isOnline()) {
            try {
                if (localData != null) {
                    sendLocalData();
                }
                db.getKerusakan(user_id, session, Integer.parseInt(ruas_id));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        listRusak = db.getTabelRusak(Integer.parseInt(ruas_id));
        adapter = new Rusak_ListAdapter(getApplicationContext(), listRusak);
        lvKerusakan.setAdapter(adapter);

        final TextView tvNamaRuas = findViewById(R.id.tvNamaRuas);
        tvNamaRuas.setText("Ruas : " + namaRuas);
        if (listRusak != null) {

            lvKerusakan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, final View view, final int position, long l) {
                    new AlertDialog.Builder(Daftar_Kerusakan.this)
                            .setMessage("Pilih : ")
                            .setIcon(R.drawable.perbaikan)
                            .setNeutralButton("Foto Kerusakan", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    objRusak = db.getObjekRusak(listRusak.get(position).getId());
                                    Intent intent = new Intent(Daftar_Kerusakan.this, Daftar_Photo.class);
                                    Bundle mBundle = new Bundle();
                                    mBundle.putString("ruas_id", ruas_id);
                                    mBundle.putString("rusak_id", "" + objRusak.getId());
                                    intent.putExtras(mBundle);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    objRusak = db.getObjekRusak(listRusak.get(position).getId());
                                    Intent intent = new Intent(Daftar_Kerusakan.this, Edit_Kerusakan.class);
                                    Bundle mBundle = new Bundle();
                                    mBundle.putString("ruas_id", ruas_id);
                                    mBundle.putString("rusak_id", "" + objRusak.getId());
                                    intent.putExtras(mBundle);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    objRusak = db.getObjekRusak(listRusak.get(position).getId());
                                    //int val = (int) adapter.getItemId(position);
                                    deleteKerusakan(objRusak.getId());
                                }
                            })
                            .show();

                }
            });
        }

        dial.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                Intent intent = new Intent(Daftar_Kerusakan.this, Tambah_Kerusakan.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("ruas_id", "" + ruas_id);
                intent.putExtras(mBundle);
                startActivity(intent);
                finish();
                return true;
            }

            @Override
            public void onMenuClosed() {

            }
        });
    }

    private void deleteKerusakan(final int i) {
        new AlertDialog.Builder(Daftar_Kerusakan.this)
                .setTitle("HAPUS")
                .setMessage("Apakah Anda akan menghapus kerusakan ?")
                .setIcon(R.drawable.perbaikan)
                .setNegativeButton("Tidak",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        objRusak = db.getObjekRusak(i);

                        if (isOnline()) {
                            /*try {
                                sendKerusakan(2, objRusak.getId(), objRusak.getKerusakan_id(), objRusak.getBagian_jalan_id(), objRusak.getDate_surveyed(), objRusak.getLat(), objRusak.getLng(), objRusak.getPoint_km(), objRusak.getPoint_m(), objRusak.getVolume(), objRusak.getFixed(), objRusak.getKerusakan(), objRusak.getBagian_jalan());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }*/
                            sendKerusakan(2, objRusak.getId(), objRusak.getKerusakan_id(), objRusak.getBagian_jalan_id(), objRusak.getDate_surveyed(), objRusak.getLat(), objRusak.getLng(), objRusak.getPoint_km(), objRusak.getPoint_m(), objRusak.getVolume(), objRusak.getFixed(), objRusak.getKerusakan(), objRusak.getBagian_jalan());
                        } else {
                            db.updateKerusakan(objRusak.getId(), Integer.parseInt(ruas_id), objRusak.getKerusakan_id(), objRusak.getBagian_jalan_id(), objRusak.getDate_surveyed(), objRusak.getLat(), objRusak.getLng(), objRusak.getPoint_km(), objRusak.getPoint_m(), objRusak.getVolume(), objRusak.getFixed(), objRusak.getKerusakan(), objRusak.getBagian_jalan(), 3);
                        }
                        for (int j = 0; j< listRusak.size(); j++){
                            int x = listRusak.get(j).getId();
                            if(x == i){
                                listRusak.remove(j);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                })
                .show();
    }

    private void sendKerusakan(final int action, final int id, final int id_kerusakan, final int id_bagian_jalan, final String date, final double lat, final double lng, final int km, final int m, final float volume, final int fixed, final String rusak, final String bagian_jalan) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://kepegbima.com/pjn2jabar/android/set_kerusakan.php?user_id=" + this.user_id + "&session=" + this.session;

        JSONObject jsonParam = new JSONObject();
        try {
            if (action == 0) {
                jsonParam.put("action", "insert");
            } else if (action == 1) {
                jsonParam.put("id", id);
                jsonParam.put("action", "update");
            } else {
                jsonParam.put("id", id);
                jsonParam.put("action", "delete");
            }
            jsonParam.put("ruas_id", ruas_id);
            jsonParam.put("kerusakan_id", id_kerusakan);
            jsonParam.put("bagian_jalan_id", id_bagian_jalan);
            jsonParam.put("date_surveyed", date);
            jsonParam.put("lat", lat);
            jsonParam.put("lng", lng);
            jsonParam.put("point_km", km);
            jsonParam.put("point_m", m);
            jsonParam.put("volume", volume + "");
            jsonParam.put("fixed", fixed + "");
        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        final String requestBody = jsonParam.toString();
        final StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("true")) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                Log.d("respons hapus kerusakan", "respon --------------------------- " + obj.getString("success"));
                                Toast.makeText(getApplicationContext(), "Berhasill mengirim data.", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } /*else {
                            Toast.makeText(getApplicationContext(), "Gagal mengirim data.", Toast.LENGTH_SHORT).show();
                        }*/
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.getMessage() + "");
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
        if (this.listRusak.size() > 0) {
            List<LatLng> p = new ArrayList<>();
            for (int i = 0; i < this.listRusak.size(); i++) {
                p.add(new LatLng(this.listRusak.get(i).getLat(), this.listRusak.get(i).getLng()));
                mMap.addMarker(new MarkerOptions().position(p.get(i)).title(this.listRusak.get(i).getLat() + ", " + this.listRusak.get(i).getLng()));
            }
            /*Polyline polyline2 = mMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .addAll(p));
            polyline2.setTag("--- Nama Rute ---");

            stylePolyline(polyline2);*/
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(p.get(0), 17));
            mMap.setOnPolylineClickListener(this);
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

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
//                                    new LatLng(1, 1), DEFAULT_ZOOM));
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

    private void sendLocalData() throws JSONException {
        for (int i = 0; i < 50; i++) {
            if (i < localData.size()) {
                if (localData.get(i).getStatus() == 1) {
                    sendKerusakan(0, localData.get(i).getId(), localData.get(i).getKerusakan_id(), localData.get(i).getBagian_jalan_id(), localData.get(i).getDate_surveyed(), localData.get(i).getLat(), localData.get(i).getLng(), localData.get(i).getPoint_km(), localData.get(i).getPoint_m(), localData.get(i).getVolume(), localData.get(i).getFixed(), localData.get(i).getKerusakan(), localData.get(i).getBagian_jalan());
                    //localData.remove(i);
                    db.deleteKerusakan(localData.get(i).getId(), 1);
                } else if (localData.get(i).getStatus() == 2) {
                    sendKerusakan(1, localData.get(i).getId(), localData.get(i).getKerusakan_id(), localData.get(i).getBagian_jalan_id(), localData.get(i).getDate_surveyed(), localData.get(i).getLat(), localData.get(i).getLng(), localData.get(i).getPoint_km(), localData.get(i).getPoint_m(), localData.get(i).getVolume(), localData.get(i).getFixed(), localData.get(i).getKerusakan(), localData.get(i).getBagian_jalan());
                    //localData.remove(i);
                    db.deleteKerusakan(localData.get(i).getId(), 2);
                } else if (localData.get(i).getStatus() == 3) {
                    sendKerusakan(2, localData.get(i).getId(), localData.get(i).getKerusakan_id(), localData.get(i).getBagian_jalan_id(), localData.get(i).getDate_surveyed(), localData.get(i).getLat(), localData.get(i).getLng(), localData.get(i).getPoint_km(), localData.get(i).getPoint_m(), localData.get(i).getVolume(), localData.get(i).getFixed(), localData.get(i).getKerusakan(), localData.get(i).getBagian_jalan());
                    //localData.remove(i);
                    db.deleteKerusakan(localData.get(i).getId(), 3);
                }
            }
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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
    }
}
