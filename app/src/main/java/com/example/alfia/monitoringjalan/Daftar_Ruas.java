package com.example.alfia.monitoringjalan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alfia.monitoringjalan.Database.DBHandler;
import com.example.alfia.monitoringjalan.Ruas.Ruas;
import com.example.alfia.monitoringjalan.Ruas.Ruas_ListAdapter;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Daftar_Ruas extends AppCompatActivity {

    private ListView lvRuas;
    private Ruas_ListAdapter adapter;
    private DBHandler db;
    private List<Ruas> listRuas = new ArrayList<>();
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_ruas);
        TextView namaText = findViewById(R.id.namaTextRuas);
        db = new DBHandler(this);
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        lvRuas = findViewById(R.id.listViewRuas);
        String username = sessionManager.getKeyUsername();
        int session = sessionManager.getKeySession();
        Log.d("username", ":::::::::::::::::::::::::::   " + username);
        if (username == null) {
            Intent intent = new Intent(Daftar_Ruas.this, LoginActivity.class);
            startActivity(intent);
        } else {
            Log.d("session", ":::::::::::::::::::::::::::   " + session);
            int user_id = db.getUserId(username);
            if (listRuas.size() == 0 || isOnline()) {
                try {
                    db.getRuas(user_id, session);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            listRuas = db.getTableRuas();
            if (listRuas != null) {

                namaText.setText(db.getNamaUser(username));
                adapter = new Ruas_ListAdapter(getApplicationContext(), listRuas);
                lvRuas.setAdapter(adapter);

                lvRuas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, final View view, final int position, long l) {
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("dd-mm-yyyy");
                        final String currentTime = df.format(c.getTime());

                        new AlertDialog.Builder(Daftar_Ruas.this)
                                .setMessage("Pilih Menu :")
                                .setIcon(R.drawable.ic_list)
                                .setPositiveButton("Ruang Jalan", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Ruas objRuas = db.getObjekRuas(listRuas.get(position).getId());

                                        Intent intent = new Intent(Daftar_Ruas.this, Daftar_Pemanfaatan.class);
                                        Bundle mBundle = new Bundle();
                                        mBundle.putString("ruas_id", "" + listRuas.get(position).getId());
                                        intent.putExtras(mBundle);
                                        startActivity(intent);

                                    }
                                })
                                .setNegativeButton("Kerusakan", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Ruas objRuas = db.getObjekRuas(listRuas.get(position).getId());

                                        Intent intent = new Intent(Daftar_Ruas.this, Daftar_Kerusakan.class);
                                        Bundle mBundle = new Bundle();
                                        mBundle.putString("ruas_id", "" + listRuas.get(position).getId());
                                        intent.putExtras(mBundle);
                                        startActivity(intent);
                                    }
                                })
                                .setNeutralButton("Detail Ruas", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Ruas objRuas = db.getObjekRuas(listRuas.get(position).getId());

                                        Intent intent = new Intent(Daftar_Ruas.this, Tambah_Coordinates.class);
                                        Bundle mBundle = new Bundle();
                                        mBundle.putString("ruas_id", "" + listRuas.get(position).getId());
                                        intent.putExtras(mBundle);
                                        startActivity(intent);
                                    }
                                }).show();
                    }
                });
            }
        }


        Button logoutBtn = findViewById(R.id.btn_logout);
        assert logoutBtn != null;
        logoutBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                db.deleteRecords();
                sessionManager.clearData();
                finish();
                isDestroyed();
            }
        });
    }

    private int backButton = 0;

    @Override
    public void onBackPressed() {
        if (this.backButton >= 1) {
            this.backButton = 0;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Tekan tombal back sekali lagi untuk keluar dari aplikasi", Toast.LENGTH_SHORT).show();
            this.backButton++;
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
