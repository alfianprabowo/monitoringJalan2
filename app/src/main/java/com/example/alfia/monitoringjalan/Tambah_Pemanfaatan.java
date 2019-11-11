package com.example.alfia.monitoringjalan;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.example.alfia.monitoringjalan.Database.DBHandler;
import com.example.alfia.monitoringjalan.Pemanfaatan.Pemanfaatan;
import com.example.alfia.monitoringjalan.Pemanfaatan.Pemanfaatan_Ruang;
import com.example.alfia.monitoringjalan.Ruang_Jalan.Ruang_Jalan;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Tambah_Pemanfaatan extends AppCompatActivity {

    private Pemanfaatan_Ruang ruang;
    private DBHandler db;
    private SessionManager sessionManager;
    private String ruas_id, ruas_date, ruas_lat, ruas_lng, ruas_km, ruas_m, username, namaRuas;
    int user_id, session;
    private Spinner spinnerRuangJalan, spinnerPemanfaatan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_pemanfaatan);
        db = new DBHandler(this);
        sessionManager = new SessionManager(this);
        session = sessionManager.getKeySession();
        username = sessionManager.getKeyUsername();
        ruas_id = getIntent().getExtras().getString("ruas_id");
        ruas_date = getIntent().getExtras().getString("ruas_date");
        ruas_lat = getIntent().getExtras().getString("ruas_lat");
        ruas_lng = getIntent().getExtras().getString("ruas_lng");
        ruas_km = getIntent().getExtras().getString("ruas_km");
        ruas_m = getIntent().getExtras().getString("ruas_m");
        namaRuas = db.getNamaRuas(Integer.parseInt(ruas_id));

        user_id = db.getUserId(username);

        spinnerRuangJalan = findViewById(R.id.spinnerTambahRuangJalan);
        spinnerPemanfaatan = findViewById(R.id.spinnerTambahPemanfaatan);

        final TextView tvLat = findViewById(R.id.tvLat_pemanfaatan);
        final TextView tvKM = findViewById(R.id.tvKm_pemanfaatan);
        final TextView tvDate = findViewById(R.id.tvUpdateSurveyed_pemanfaatan);

        tvLat.setText("Latitude " + ruas_lat + " , " + ruas_lng);
        tvKM.setText("KM " + ruas_km + " + " + ruas_m);
        tvDate.setText("Tanggal Survey " + ruas_date);

        addItemOnSpinnerRuangJalan();
        addItemOnSpinnerPemanfaatan();

        Button tambahPemanfaatan = findViewById(R.id.btn_tambahPemanfaatan);
        Button batal = findViewById(R.id.btn_batal_pemanfaatan);
        tambahPemanfaatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ruangJalan = spinnerRuangJalan.getSelectedItem().toString();
                String pemanfaatan = spinnerPemanfaatan.getSelectedItem().toString();

                if (ruangJalan.equals("Pilih Ruang Jalan") || (pemanfaatan.equals("Pilih Pemanfaatan"))) {
                    Toast.makeText(getApplicationContext(), "Pilih Ruang Jalan dan Pemanfaatan!", Toast.LENGTH_SHORT).show();
                } else {
                    int id_ruang_jalan = db.getRuangJalanId(ruangJalan);
                    int id_pemanfaatan = db.getPemanfaatanId(pemanfaatan);

                    //db.tambahPemanfaatanRuang(Integer.parseInt(ruas_id), id_ruang_jalan, id_pemanfaatan, ruas_date, Double.parseDouble(ruas_lat), Double.parseDouble(ruas_lng), Integer.parseInt(ruas_km), Integer.parseInt(ruas_m), ruangJalan, pemanfaatan);
                    Log.d("tambah manfaat ", "ffffffffffffff ruang jalan " + ruangJalan + " id " + id_ruang_jalan + " pemanfaataan " + pemanfaatan + " id " + id_pemanfaatan);
                    if (isOnline()) {
                        /*try {
                            sendNewPemanfaatan(id_ruang_jalan, id_pemanfaatan, ruas_date, Double.parseDouble(ruas_lat), Double.parseDouble(ruas_lng), Integer.parseInt(ruas_km), Integer.parseInt(ruas_m), ruangJalan, pemanfaatan);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                        sendNewPemanfaatan(id_ruang_jalan, id_pemanfaatan, ruas_date, Double.parseDouble(ruas_lat), Double.parseDouble(ruas_lng), Integer.parseInt(ruas_km), Integer.parseInt(ruas_m), ruangJalan, pemanfaatan);
                    } else {
                        db.tambahPemanfaatanRuang(Integer.parseInt(ruas_id), id_ruang_jalan, id_pemanfaatan, ruas_date, Double.parseDouble(ruas_lat), Double.parseDouble(ruas_lng), Integer.parseInt(ruas_km), Integer.parseInt(ruas_m), ruangJalan, pemanfaatan, 1);
                    }


                    Intent intent = new Intent(Tambah_Pemanfaatan.this, Daftar_Ruas.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("ruas_id", ruas_id);
                    intent.putExtras(mBundle);
                    startActivity(intent);
                    finish();
                }
            }
        });

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Tambah_Pemanfaatan.this, Daftar_Pemanfaatan.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("ruas_id", ruas_id);
                intent.putExtras(mBundle);
                startActivity(intent);
                finish();
            }
        });

    }

    private void sendNewPemanfaatan(final int id_ruang_jalan, final int id_pemanfaatan, final String date, final double lat, final double lng, final int km, final int m, final String ruang_jalan, final String pemanfaatan) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://kepegbima.com/pjn2jabar/android/set_pemanfaatan.php?user_id=" + user_id + "&session=" + session;

        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("action", "insert");
            jsonParam.put("ruas_id", ruas_id + "");
            jsonParam.put("ruang_jalan_id", id_ruang_jalan + "");
            jsonParam.put("pemanfaatan_id", id_pemanfaatan + "");
            jsonParam.put("date_surveyed", ruas_date);
            jsonParam.put("lat", ruas_lat);
            jsonParam.put("lng", ruas_lng);
            jsonParam.put("point_km", ruas_km);
            jsonParam.put("point_m", ruas_m);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        final String requestBody = jsonParam.toString();
        final StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Respon add pemanfaatan", response);
                        if (response.contains("Session expired!")) {
                            db.deleteRecords();
                            sessionManager.clearData();
                            finish();
                        } else if (response.contains("true")) {
                            Toast.makeText(getApplicationContext(), "Data berhasill disimpan diserver.", Toast.LENGTH_SHORT).show();
                            db.tambahPemanfaatanRuang(Integer.parseInt(ruas_id), id_ruang_jalan, id_pemanfaatan, date, lat, lng, km, m, ruang_jalan, pemanfaatan, 0);
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

    private void addItemOnSpinnerRuangJalan() {
        DBHandler db = new DBHandler(this);
        List<Ruang_Jalan> listRuang = new ArrayList<>();
        listRuang = db.getAllNamaRuangJalan();
        List<String> list1 = new ArrayList<String>();
        list1.add("Pilih Ruang Jalan");
        for (int i = 0; i < listRuang.size(); i++) {
            list1.add(listRuang.get(i).getNama());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Tambah_Pemanfaatan.this,
                android.R.layout.simple_spinner_item, list1);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerRuangJalan.setAdapter(dataAdapter);
    }

    private void addItemOnSpinnerPemanfaatan() {
        DBHandler db = new DBHandler(this);
        List<Pemanfaatan> listPemanfaatan = new ArrayList<>();
        listPemanfaatan = db.getAllNamaPemanfaatan();
        List<String> list2 = new ArrayList<>();
        list2.add("Pilih Pemanfaatan");
        for (int i = 0; i < listPemanfaatan.size(); i++) {
            list2.add(listPemanfaatan.get(i).getNama());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list2);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPemanfaatan.setAdapter(dataAdapter);
    }


    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
