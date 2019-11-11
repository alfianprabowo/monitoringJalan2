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
import com.example.alfia.monitoringjalan.Kerusakan.Rusak;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Edit_Kerusakan extends AppCompatActivity {

    private DBHandler db;
    private SessionManager sessionManager;
    private Rusak objRusak;
    private String ruas_id, rusak_id, namaRuas, username;
    int user_id, session;
    private Spinner spinnerKerusakan, spinnerBagianJalan, spinnerStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_kerusakan);
        db = new DBHandler(this);
        sessionManager = new SessionManager(this);
        username = sessionManager.getKeyUsername();
        ruas_id = getIntent().getExtras().getString("ruas_id");
        rusak_id = getIntent().getExtras().getString("rusak_id");
        namaRuas = db.getNamaRuas(Integer.parseInt(ruas_id));
        session = sessionManager.getKeySession();
        user_id = db.getUserId(username);

        spinnerKerusakan = findViewById(R.id.spinnerUpdateKerusakan);
        spinnerBagianJalan = findViewById(R.id.spinnerUpdateBagJalan);
        spinnerStatus = findViewById(R.id.spinnerUpdateStatus_kerusakan);
        final EditText editVolume = findViewById(R.id.updateVolume_kerusakan);
        final EditText editKm = findViewById(R.id.updateKM_kerusakan);
        final EditText editM = findViewById(R.id.updateM_kerusakan);
        final TextView tvlat = findViewById(R.id.tvUpdateLat_kerusakan);
        final TextView tvDat = findViewById(R.id.tvUpdateSurveyed_kerusakan);
        final Button update = findViewById(R.id.btn_update_kerusakan);
        final Button batal = findViewById(R.id.btn_batal_kerusakan);

        objRusak = db.getObjekRusak(Integer.parseInt(rusak_id));
        final int objId= objRusak.getId();
        final Float objVolume = objRusak.getVolume();
        final int objKm = objRusak.getPoint_km();
        final int objM = objRusak.getPoint_m();
        final Double objLat = objRusak.getLat();
        final Double objLng = objRusak.getLng();
        final String objDate = objRusak.getDate_surveyed();

        editKm.setText(objKm +"");
        editM.setText(objM +"");
        editVolume.setText(objVolume + "");
        tvlat.setText("Lat " + objLat + " , " + objLng);
        tvDat.setText("Tanggal Survey " + objDate);

        addItemOnSpinnerBagianJalan();
        addItemOnSpinnerKerusakan();
        addItemsOnSpinnerStatus();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rusak = spinnerKerusakan.getSelectedItem().toString();
                String jalan = spinnerBagianJalan.getSelectedItem().toString();
                String status = spinnerStatus.getSelectedItem().toString();

                if ((rusak.equals("Pilih Kerusakan")) || (jalan.equals("Pilih Bagian Jalan")) || (status.equals("Pilih Status"))) {
                    Toast.makeText(getApplicationContext(), "Pilih Kerusakan, Bagian Jalan dan Status !", Toast.LENGTH_SHORT).show();
                } else {
                    int id_kerusakan = db.getKerusakanId(rusak);
                    int id_jalan = db.getBagianJalanId(jalan);
                    String volume = editVolume.getText().toString();
                    String km = editKm.getText().toString();
                    String m = editM.getText().toString();
                    int fix = -1;

                    if (status.equals("Belum Diperbaiki")) {
                        fix = 0;
                    } else if (status.equals("Telah Diperbaiki")) {
                        fix = 1;
                    }

                    if ((volume.equals(""))) {
                        Toast.makeText(getApplicationContext(), "Volume Harus Diisi! " + volume, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if(isOnline()){
                            /*try {
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }*/
                            sendKerusakan(objId, id_kerusakan, id_jalan, objDate, objLat, objLng, Integer.parseInt(km), Integer.parseInt(m), Float.parseFloat(volume), fix, rusak, jalan);
                        }else{
                            db.updateKerusakan(objId,objRusak.getRuas_id(), id_kerusakan, id_jalan, objDate, objLat, objLng, Integer.parseInt(km),Integer.parseInt(m),Float.parseFloat(volume),fix, rusak, jalan,2);
                        }

                        Intent intent = new Intent(Edit_Kerusakan.this, Daftar_Kerusakan.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putString("ruas_id", ruas_id);
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
                Intent intent = new Intent(Edit_Kerusakan.this, Daftar_Kerusakan.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("ruas_id", ruas_id);
                intent.putExtras(mBundle);
                startActivity(intent);
                finish();
            }
        });
    }

    private void sendKerusakan(final int id, final int id_kerusakan, final int id_bagian_jalan, final String date, final double lat, final double lng, final int km, final int m, final float volume, final int fixed, final String kerusakan, final String bagian_jalan) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://kepegbima.com/pjn2jabar/android/set_kerusakan.php?user_id=" + this.user_id + "&session=" + this.session;

        Log.d("send kerusakan", "rusaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaak " + id + " volume " + volume);
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("id", id);
            jsonParam.put("action", "update");
            jsonParam.put("ruas_id", ruas_id);
            jsonParam.put("kerusakan_id", id_kerusakan );
            jsonParam.put("bagian_jalan_id", id_bagian_jalan );
            jsonParam.put("date_surveyed", date);
            jsonParam.put("lat", lat);
            jsonParam.put("lng", lng);
            jsonParam.put("point_km", km);
            jsonParam.put("point_m", m);
            jsonParam.put("volume", volume);
            jsonParam.put("fixed", fixed );
        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        final String requestBody = jsonParam.toString();
        Log.d("json kerusakan ", requestBody);
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
                                Log.d("respons ubah kerusakan", "respon --------------------------- " + obj.getString("success"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getApplicationContext(), "Data berhasill diubah di server.", Toast.LENGTH_SHORT).show();
                            db.updateKerusakan(objRusak.getId(),objRusak.getRuas_id(), id_kerusakan, id_bagian_jalan, date, lat, lng, km, m,volume,fixed, kerusakan, bagian_jalan,0);
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

    private void addItemsOnSpinnerStatus() {
        List<String> list1 = new ArrayList<String>();
        list1.add("Pilih Status");
        list1.add("Belum Diperbaiki");
        list1.add("Telah Diperbaiki");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list1);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(dataAdapter);
        int stat = objRusak.getFixed();
        if (stat == 0) {
            spinnerStatus.setSelection(getIndex(spinnerStatus, "Belum Diperbaiki"));
        } else if (stat == 1) {
            spinnerStatus.setSelection(getIndex(spinnerStatus, "Telah Diperbaiki"));
        }
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
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list1);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        String nama = db.getNamaKerusakan(objRusak.getKerusakan_id());
        spinnerKerusakan.setAdapter(dataAdapter);
        spinnerKerusakan.setSelection(getIndex(spinnerKerusakan, nama));
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
        String nama = db.getNamaBagianJalan(objRusak.getBagian_jalan_id());
        spinnerBagianJalan.setSelection(getIndex(spinnerBagianJalan, nama));
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
