package com.example.alfia.monitoringjalan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.internal.NavigationMenu;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.example.alfia.monitoringjalan.Image.Image;
import com.example.alfia.monitoringjalan.Image.Image_ListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.yavski.fabspeeddial.FabSpeedDial;

public class Daftar_Photo extends AppCompatActivity {

    private ListView lvPhoto;
    private Image_ListAdapter adapter;
    private DBHandler db;
    private List<Image> listImage = new ArrayList<>();
    Context context;
    SessionManager sessionManager;
    Spinner spinner;
    private String rusak_id,ruas_id;
    private int user_id, session;
    private List<Image> localData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_photo);
        db = new DBHandler(this);
        lvPhoto = findViewById(R.id.listPhoto);
        sessionManager = new SessionManager(this);
        String username = sessionManager.getKeyUsername();
        session = sessionManager.getKeySession();
        ruas_id = getIntent().getExtras().getString("ruas_id");
        rusak_id = getIntent().getExtras().getString("rusak_id");
        user_id = db.getUserId(username);

        localData = db.getLocalImage(Integer.parseInt(rusak_id));
        FabSpeedDial dial = findViewById(R.id.fabPhoto);

        if(isOnline()){
            if(localData != null){
                try {
                    sendLocalData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        listImage = db.getTabelRusakPhotos(Integer.parseInt(rusak_id));
        adapter = new Image_ListAdapter(getApplicationContext(), listImage );
        lvPhoto.setAdapter(adapter);
        if(this.listImage.size() != 0){
            lvPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view,final int position, long l) {

                }
            });
        }

        if(listImage.size() > 10){
            dial.setVisibility(View.GONE);
            dial.setEnabled(false);
        }

        dial.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                Intent intent = new Intent(Daftar_Photo.this, Tambah_Photo.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("ruas_id", "" + ruas_id);
                mBundle.putString("rusak_id", "" + rusak_id);
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

    // send local data
    private void sendNewPhoto(final Bitmap photo, final String name) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://kepegbima.com/pjn2jabar/android/upload_image.php?user_id=" + user_id + "&session=" + this.session;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.d("Response local photo", response);
                        if (response.contains("true")) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                Log.d("responPhoto", "yyyyyyyyyyyyyyyyyyyyy    " + obj.getString("success"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    volleyError = error;
                    Log.d("Error.Response", volleyError.getMessage());
                }
            }
        }) {

            String hasilEncode = getEncodeString(photo);
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("rusak_id", rusak_id);
                params.put("photo", "data:image/jpeg;base64,"+hasilEncode);
                params.put("name", name);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    public void saveImage(Context context, Bitmap b, String imageName) {
        FileOutputStream foStream;
        try {
            foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, foStream);
            foStream.close();
        } catch (Exception e) {
            Log.d("saveImage", "Exception 2, Something went wrong!");
            e.printStackTrace();
        }
    }


    public void insertImg(int rusak_id,String name, Bitmap img,  int status) {
        byte[] data = getBitmapAsByteArray(img);
        db.tambahPhoto(rusak_id, name,data, status);
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        return outputStream.toByteArray();
    }

    public String getEncodeString(Bitmap bitmap) {
        byte[] byteArray = getBitmapAsByteArray(bitmap);
        String imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return imageString;
    }

    private void sendLocalData()throws JSONException{
        byte[] gambar;
        Bitmap bitmap;
        for (int i = 0; i < 10; i++) {
            if(i < localData.size()){
                gambar = localData.get(i).getPhoto();
                bitmap = BitmapFactory.decodeByteArray(gambar, 0, gambar.length);
                if(localData.get(i).getStatus() == 1){
                    sendNewPhoto(bitmap, localData.get(i).getName());
                    db.deleteLocalGambar(localData.get(i).getId());
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
