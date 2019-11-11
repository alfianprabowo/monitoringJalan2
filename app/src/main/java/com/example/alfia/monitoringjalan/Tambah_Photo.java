package com.example.alfia.monitoringjalan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.alfia.monitoringjalan.Kerusakan.Kerusakan;
import com.example.alfia.monitoringjalan.Kerusakan.Rusak;
import com.example.alfia.monitoringjalan.Image.Image;
import com.google.gson.JsonObject;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class Tambah_Photo extends AppCompatActivity {

    private Uri fileUri; // file url to store image/video
    ImageView imageView;
    DBHandler db;
    SessionManager sessionManager;
    private int user_id, session;
    private String ruas_id, rusak_id, username;
    CameraPhoto photo;
    final int CAMERA_REQUEST = 13323;
    String selectedPhoto;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_photo);
        db = new DBHandler(this);
        imageView = findViewById(R.id.addImageView);
        photo = new CameraPhoto(getApplicationContext());

        final Button btnCapturePicture = findViewById(R.id.btn_take_image);
        final Button btnAddPhoto = findViewById(R.id.btn_add_photo);
        final Button btnCancel = findViewById(R.id.btn_batal_photo);
        final EditText editNama = findViewById(R.id.editNamaPhoto);

        sessionManager = new SessionManager(this);
        session = sessionManager.getKeySession();
        username = sessionManager.getKeyUsername();
        user_id = db.getUserId(username);

        ruas_id = getIntent().getExtras().getString("ruas_id");
        rusak_id = getIntent().getExtras().getString("rusak_id");

        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Hp Anda tidak mendukung kamera",
                    Toast.LENGTH_LONG).show();
            finish();
        }

        btnCapturePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    takePhotoFromCamera();
                    addToGallery(); // pindah ke upload !!!!!
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPhoto == null || selectedPhoto.equals("")) {
                    Toast.makeText(getApplicationContext(), "Tidak ada foto yang dipilih!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String name = editNama.getText().toString();
                try {
                    if (isOnline()) {
                        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        //String hasilEncode = getEncodeString(getRotateImage(bitmap,90));
                        String hasilEncode = getEncodeString(bitmap);
                        sendPhoto(hasilEncode, name);

                    } else {
                        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        insertImg(Integer.parseInt(rusak_id),name, bitmap, 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getApplicationContext(), Daftar_Kerusakan.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("ruas_id", ruas_id);
                mBundle.putString("rusak_id", rusak_id);
                intent.putExtras(mBundle);
                startActivity(intent);
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Tambah_Photo.this, Daftar_Photo.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("ruas_id", ruas_id);
                mBundle.putString("rusak_id", rusak_id);
                intent.putExtras(mBundle);
                startActivity(intent);
                finish();
            }
        });
    }

    public void  sendPhoto(final String photo, final String name){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://kepegbima.com/pjn2jabar/android/upload_image.php?user_id=" + user_id + "&session=" + this.session;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.d("Response add photo", response);
                        if (response.contains("Session expired!")) {
                            db.deleteRecords();
                            sessionManager.clearData();
                            finish();
                        } else if (response.contains("true")) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                Log.d("responPhoto", "yyyyyyyyyyyyyyyyyyyyy    " + obj.getString("success"));
                                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                                insertImg(Integer.parseInt(rusak_id), name, bitmap, 0);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getApplicationContext(), "Data berhasill disimpan diserver.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Gagal menambahkan data.", Toast.LENGTH_SHORT).show();
                            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                            //insertImg(Integer.parseInt(rusak_id), name, bitmap, 1);
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
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("rusak_id", rusak_id);
                params.put("photo", "data:image/jpeg;base64,"+photo);
                params.put("name", name);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void takePhotoFromCamera() throws IOException {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            }
        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        startActivityForResult(intent, CAMERA);
    }

    private boolean isDeviceSupportCamera() {
        return getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    public void insertImg(int rusak_id,String name, Bitmap img,  int status) {
        byte[] data = getBitmapAsByteArray(img);
        Image image = new Image();
        image.setRusak_id(rusak_id);
        image.setPhoto(data);
        image.setName(name);
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
        Log.d("image", "imageeeeeeeeeeeeeeee   :" + imageString);
        return imageString;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA) {
                selectedPhoto = photoPath;
                try {
                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(1024, 1024).getBitmap(); // 512
                    imageView.setImageBitmap(getRotateImage(bitmap,90));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Bitmap getRotateImage(Bitmap src, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap btm = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return btm;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        photoPath = image.getAbsolutePath();
        return image;
    }

    public void addToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getApplicationContext().sendBroadcast(mediaScanIntent);
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
