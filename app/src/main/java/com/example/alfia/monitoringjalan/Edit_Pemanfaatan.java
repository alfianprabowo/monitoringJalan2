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

public class Edit_Pemanfaatan extends AppCompatActivity {

    private DBHandler db;
    SessionManager sessionManager;
    private String ruas_id, pemanfaatan, username, namaRuas;
    private Pemanfaatan_Ruang objPemanfaatan;
    private Spinner spinnerRuangJalan, spinnerPemanfaatan;
    private int user_id, session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pemanfaatan);
        db = new DBHandler(this);
        sessionManager = new SessionManager(this);
        username = sessionManager.getKeyUsername();
        ruas_id = getIntent().getExtras().getString("ruas_id");
        pemanfaatan = getIntent().getExtras().getString("id_pemanfaatan");
        namaRuas = db.getNamaRuas(Integer.parseInt(ruas_id));
        session = sessionManager.getKeySession();
        user_id = db.getUserId(username);

        spinnerPemanfaatan = findViewById(R.id.spinnerUpdatePemanfaatan);
        spinnerRuangJalan = findViewById(R.id.spinnerUpdateRuangJalan);

        final TextView tvLat = findViewById(R.id.tvUpdateLat_pemanfaatan);
        final TextView tvKM = findViewById(R.id.tvUpdateKm_pemanfaatan);
        final TextView tvDate = findViewById(R.id.tvUpdateSurveyed_pemanfaatan);
        final Button update = findViewById(R.id.btn_update_pemanfaatan);
        final Button batal = findViewById(R.id.btn_batal_pemanfaatan);

        objPemanfaatan = db.getObjekPemanfaatanRuang(Integer.parseInt(pemanfaatan));
        tvKM.setText("KM : " + objPemanfaatan.getPoint_km() + " + " + objPemanfaatan.getPoint_m());
        tvLat.setText("Lat : " + objPemanfaatan.getLat() + " , " + objPemanfaatan.getLng());
        tvDate.setText("Tanggal Survey : " + objPemanfaatan.getDate_surveyed());

        addItemOnSpinnerPemanfaatan();
        addItemOnSpinnerRuangJalan();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ruangJalan = spinnerRuangJalan.getSelectedItem().toString();
                String pemanfaatan = spinnerPemanfaatan.getSelectedItem().toString();

                if (ruangJalan.equals("Pilih Ruang Jalan") || (pemanfaatan.equals("Pilih Pemanfaatan"))) {
                    Toast.makeText(getApplicationContext(), "Pilih Ruang Jalan dan Pemanfaatan!", Toast.LENGTH_SHORT).show();
                } else {
                    int id_ruang_jalan = db.getRuangJalanId(ruangJalan);
                    int id_pemanfaatan = db.getPemanfaatanId(pemanfaatan);

                    if(isOnline()){
                       /* try {
                            sendPemanfaatan(objPemanfaatan.getId(), id_ruang_jalan, id_pemanfaatan, objPemanfaatan.getDate_surveyed(), objPemanfaatan.getLat(), objPemanfaatan.getLng(), objPemanfaatan.getPoint_km(), objPemanfaatan.getPoint_m(), objPemanfaatan.getRuang_jalan(), objPemanfaatan.getPemanfaatan());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                        sendPemanfaatan(objPemanfaatan.getId(), id_ruang_jalan, id_pemanfaatan, objPemanfaatan.getDate_surveyed(), objPemanfaatan.getLat(), objPemanfaatan.getLng(), objPemanfaatan.getPoint_km(), objPemanfaatan.getPoint_m(), objPemanfaatan.getRuang_jalan(), objPemanfaatan.getPemanfaatan());
                    }else{
                        db.updatePemanfaatan(objPemanfaatan.getId(), objPemanfaatan.getRuas_id(), id_ruang_jalan, id_pemanfaatan,  objPemanfaatan.getDate_surveyed(), objPemanfaatan.getLat(), objPemanfaatan.getLng(),objPemanfaatan.getPoint_km(), objPemanfaatan.getPoint_m(), ruangJalan, pemanfaatan, 2);
                    }

                    Intent intent = new Intent(Edit_Pemanfaatan.this, Daftar_Pemanfaatan.class);
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
                Intent intent = new Intent(Edit_Pemanfaatan.this, Daftar_Pemanfaatan.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("ruas_id", ruas_id);
                intent.putExtras(mBundle);
                startActivity(intent);
                finish();
            }
        });

    }

    private void sendPemanfaatan(final int id, final int id_ruang_jalan, final int id_pemanfaatan, final String date, final double lat, final double lng, final int km, final int m, final String ruang_jalan, final String pemanfaatan) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://kepegbima.com/pjn2jabar/android/set_pemanfaatan.php?user_id=" + this.user_id + "&session=" + this.session;

        Log.d("send kerusakan", "pemanfaataaaaaaaaaaaaaaaaaaaannn " + id + " " + id_pemanfaatan + date + lat + lng + km + m + ruang_jalan + pemanfaatan);
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("id", id);
            jsonParam.put("action", "update");
            jsonParam.put("ruas_id", ruas_id);
            jsonParam.put("ruang_jalan_id", id_ruang_jalan);
            jsonParam.put("pemanfaatan_id", id_pemanfaatan);
            jsonParam.put("date_surveyed", date);
            jsonParam.put("lat", lat);
            jsonParam.put("lng", lng);
            jsonParam.put("point_km", km);
            jsonParam.put("point_m", m);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        final String requestBody = jsonParam.toString();
        Log.d("json pemanfaatan ", requestBody);
        final StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("respon edit manfaat", response);
                        if (response.contains("Session expired!")) {
                            db.deleteRecords();
                            sessionManager.clearData();
                            finish();
                        }
                        if (response.contains("true")) {
                            try {
                                JSONObject obj = new JSONObject(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            db.updatePemanfaatan(objPemanfaatan.getId(), objPemanfaatan.getRuas_id(), id_ruang_jalan, id_pemanfaatan, date, lat, lng, km, m, ruang_jalan, pemanfaatan, 0);
                            Toast.makeText(getApplicationContext(), "Data berhasill diubah di server.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Gagal mengubah data di server.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.getMessage());
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
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list1);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRuangJalan.setAdapter(dataAdapter);
        String nama = db.getNamaRuangJalan(objPemanfaatan.getRuang_jalan_id());
        spinnerRuangJalan.setSelection(getIndex(spinnerRuangJalan, nama));
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
        String nama = db.getNamaPemanfaatan(objPemanfaatan.getPemanfaatan_id());
        spinnerPemanfaatan.setSelection(getIndex(spinnerPemanfaatan, nama));
    }

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(myString)) {
                index = i;
            }
        }
        return index;
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
