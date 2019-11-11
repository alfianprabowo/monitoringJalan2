package com.example.alfia.monitoringjalan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.alfia.monitoringjalan.Database.DBHandler;
import com.example.alfia.monitoringjalan.Pemanfaatan.Pemanfaatan;
import com.example.alfia.monitoringjalan.Pemanfaatan.Pemanfaatan_Ruang;
import com.example.alfia.monitoringjalan.Pemanfaatan.Pemanfaatan_Ruang_ListAdapter;
import com.example.alfia.monitoringjalan.Ruang_Jalan.Ruang_Jalan;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Daftar_Pemanfaatan extends AppCompatActivity {

    private ListView lvPemanfaatan;
    private Pemanfaatan_Ruang_ListAdapter adapter;
    private List<Pemanfaatan_Ruang> listPemanfaatan = new ArrayList<>();
    private DBHandler db;
    SessionManager sessionManager;
    private String ruas_id, namaRuas;
    private int user_id, session;
    private Pemanfaatan_Ruang objPemanfaatan;
    private List<Pemanfaatan_Ruang> localData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_pemanfaatan);
        db = new DBHandler(this);
        sessionManager = new SessionManager(this);
        session = sessionManager.getKeySession();
        String username = sessionManager.getKeyUsername();
        ruas_id = getIntent().getExtras().getString("ruas_id");
        user_id = db.getUserId(username);
        lvPemanfaatan = findViewById(R.id.listViewPemanfaatan);
        namaRuas = db.getNamaRuas(Integer.parseInt(ruas_id));

        final TextView tvNamaRuas = findViewById(R.id.tvNamaRuas_pemanfaatan);
        tvNamaRuas.setText("Ruas : " + namaRuas);
        localData = db.getLocalPemanfaatan(Integer.parseInt(ruas_id));


        if (isOnline()) {
            try {
                sendLocalData();
                db.getPemanfaatan(user_id, session, Integer.parseInt(ruas_id));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        listPemanfaatan = db.getTabelPemanfaatanRuang(Integer.parseInt(ruas_id));

        adapter = new Pemanfaatan_Ruang_ListAdapter(getApplicationContext(), listPemanfaatan);
        lvPemanfaatan.setAdapter(adapter);

        if (listPemanfaatan != null) {
            lvPemanfaatan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, final View view, final int position, long l) {
                    new AlertDialog.Builder(Daftar_Pemanfaatan.this)
                            .setMessage("Pilih : ")
                            .setIcon(R.drawable.perbaikan)
                            .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    objPemanfaatan = db.getObjekPemanfaatanRuang(listPemanfaatan.get(position).getId());
                                    Intent intent = new Intent(Daftar_Pemanfaatan.this, Edit_Pemanfaatan.class);
                                    Bundle mBundle = new Bundle();
                                    mBundle.putString("ruas_id", ruas_id);
                                    mBundle.putString("id_pemanfaatan", objPemanfaatan.getId() + "");
                                    intent.putExtras(mBundle);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    objPemanfaatan = db.getObjekPemanfaatanRuang(listPemanfaatan.get(position).getId());
                                    int val = (int) adapter.getItemId(position);
                                    deletePemanfaatan(val);
                                }
                            })
                            .show();
                }
            });
        }
    }

    private void deletePemanfaatan(final int i) {
        new AlertDialog.Builder(Daftar_Pemanfaatan.this)
                .setMessage("Hapus Pemanfaatan ?")
                .setIcon(R.drawable.perbaikan)
                .setNegativeButton("Tidak",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("Hapus Pemanfaatan", "id " + i);
                        Pemanfaatan_Ruang objPemanfaatan = db.getObjekPemanfaatanRuang(i);
                        Log.i("hapus pemanfaatan full", "id " + objPemanfaatan.getId() + " KM  " + objPemanfaatan.getPoint_km());
                        if(isOnline()){
                           /* try {
                                sendPemanfaatan(2, objPemanfaatan.getId(), objPemanfaatan.getRuang_jalan_id(), objPemanfaatan.getPemanfaatan_id(), objPemanfaatan.getDate_surveyed(), objPemanfaatan.getLat(), objPemanfaatan.getLng(), objPemanfaatan.getPoint_km(), objPemanfaatan.getPoint_m(), objPemanfaatan.getRuang_jalan(), objPemanfaatan.getPemanfaatan());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }*/
                            sendPemanfaatan(2, objPemanfaatan.getId(), objPemanfaatan.getRuang_jalan_id(), objPemanfaatan.getPemanfaatan_id(), objPemanfaatan.getDate_surveyed(), objPemanfaatan.getLat(), objPemanfaatan.getLng(), objPemanfaatan.getPoint_km(), objPemanfaatan.getPoint_m(), objPemanfaatan.getRuang_jalan(), objPemanfaatan.getPemanfaatan());
                        }else{
                            db.updatePemanfaatan(objPemanfaatan.getId(),Integer.parseInt(ruas_id), objPemanfaatan.getRuang_jalan_id(), objPemanfaatan.getPemanfaatan_id(), objPemanfaatan.getDate_surveyed(), objPemanfaatan.getLat(), objPemanfaatan.getLng(), objPemanfaatan.getPoint_km(), objPemanfaatan.getPoint_m(), objPemanfaatan.getRuang_jalan(), objPemanfaatan.getPemanfaatan(),3);
                        }

                        for (int j = 0; j< listPemanfaatan.size(); j++){
                            int x = listPemanfaatan.get(j).getId();
                            if(x == i){
                                listPemanfaatan.remove(j);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                })
                .show();
    }

    private void sendPemanfaatan(int action, final int id, final int id_ruang_jalan, final int id_pemanfaatan, final String date, final double lat, final double lng, final int km, final int m, final String ruang_jalan, final String pemanfaatan) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://kepegbima.com/pjn2jabar/android/set_pemanfaatan.php?user_id=" + this.user_id + "&session=" + this.session;

        JSONObject jsonParam = new JSONObject();
        try {
            if (action == 0) {
                jsonParam.put("action", "insert");
            } else if (action == 1){
                jsonParam.put("id", id);
                jsonParam.put("action", "update");
            } else{
                jsonParam.put("id", id);
                jsonParam.put("action", "delete");
            }
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
        final StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("respon send manfaat", response);
                        if (response.contains("true")) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                Log.d("resp edit pemanfaatan", "respon --------------------------- " + obj.getString("success"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(getApplicationContext(), "Data berhasill diubah di server.", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(getApplicationContext(), "Gagal menghapus data.", Toast.LENGTH_SHORT).show();
                            db.updatePemanfaatan(id, Integer.parseInt(ruas_id), id_ruang_jalan, id_pemanfaatan, date, lat, lng, km, m, ruang_jalan,pemanfaatan, 3);
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

    private void sendLocalData() throws JSONException {
        for (int i = 0; i < 50; i++) {
            if(i < localData.size()){
                if(localData.get(i).getStatus() == 1){
                    sendPemanfaatan(0, localData.get(i).getId(), localData.get(i).getRuang_jalan_id(), localData.get(i).getPemanfaatan_id(), localData.get(i).getDate_surveyed(), localData.get(i).getLat(), localData.get(i).getLng(), localData.get(i).getPoint_km(), localData.get(i).getPoint_m(), localData.get(i).getRuang_jalan(), localData.get(i).getPemanfaatan());
                    db.deletePemanfaatan(localData.get(i).getId(), 1);
                }
                else if(localData.get(i).getStatus() == 2){
                    sendPemanfaatan(1, localData.get(i).getId(), localData.get(i).getRuang_jalan_id(), localData.get(i).getPemanfaatan_id(), localData.get(i).getDate_surveyed(), localData.get(i).getLat(), localData.get(i).getLng(), localData.get(i).getPoint_km(), localData.get(i).getPoint_m(), localData.get(i).getRuang_jalan(), localData.get(i).getPemanfaatan());
                    db.deletePemanfaatan(localData.get(i).getId(), 2);
                }
                else if(localData.get(i).getStatus() == 3){
                    sendPemanfaatan(2, localData.get(i).getId(), localData.get(i).getRuang_jalan_id(), localData.get(i).getPemanfaatan_id(), localData.get(i).getDate_surveyed(), localData.get(i).getLat(), localData.get(i).getLng(), localData.get(i).getPoint_km(), localData.get(i).getPoint_m(), localData.get(i).getRuang_jalan(), localData.get(i).getPemanfaatan());
                    db.deletePemanfaatan(localData.get(i).getId(), 3);
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

}
