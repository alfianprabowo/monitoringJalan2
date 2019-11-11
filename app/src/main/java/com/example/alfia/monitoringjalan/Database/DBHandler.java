package com.example.alfia.monitoringjalan.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.example.alfia.monitoringjalan.Bagian_Jalan.Bagian_Jalan;
import com.example.alfia.monitoringjalan.Data.Data;
import com.example.alfia.monitoringjalan.Image.Image;
import com.example.alfia.monitoringjalan.Kerusakan.Kerusakan;
import com.example.alfia.monitoringjalan.Kerusakan.Rusak;
import com.example.alfia.monitoringjalan.Module.Module;
import com.example.alfia.monitoringjalan.Pemanfaatan.Pemanfaatan;
import com.example.alfia.monitoringjalan.Pemanfaatan.Pemanfaatan_Ruang;
import com.example.alfia.monitoringjalan.Ruang_Jalan.Ruang_Jalan;
import com.example.alfia.monitoringjalan.Ruas.Ruas;
import com.example.alfia.monitoringjalan.Ruas.Ruas_Coordinates;
import com.example.alfia.monitoringjalan.User.User;
import com.example.alfia.monitoringjalan.Version.Version;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;


public class DBHandler extends SQLiteOpenHelper {

    protected static final String DATABASE_NAME = "MonitoringDatabase";
    private static final int DATABASE_VERSION = 1;
    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final String URL = "http://kepegbima.com/pjn2jabar/";

    private static final String TABLE_USER = "t_user";
    private static final String TABLE_DATA = "t_data";
    private static final String TABLE_BAGIAN_JALAN = "t_ref_bagian_jalan";
    private static final String TABLE_KERUSAKAN = "t_ref_kerusakan";
    private static final String TABLE_MODULE = "t_module";
    private static final String TABLE_PEMANFAATAN = "t_ref_pemanfaatan";
    private static final String TABLE_RUANG_JALAN = "t_ruang_jalan";
    private static final String TABLE_VERSION = "t_version";

    private static final String TABLE_RUAS = "t_ruas";
    private static final String TABLE_RUAS_COORDINATES = "t_ruas_coordinates";
    private static final String TABLE_RUSAK = "t_rusak";
    private static final String TABLE_RUSAK_PHOTOS = "t_rusak_photos";
    private static final String TABLE_PEMANFAATAN_RUANG = "t_pemanfaatan_ruang";

    private static final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " + TABLE_USER +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, structural tinyint(1))";

    private static final String CREATE_TABLE_DATA = "CREATE TABLE IF NOT EXISTS " + TABLE_DATA +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT, unit_id TEXT, satker_id TEXT, ppk_id TEXT, unit_name TEXT, satker_name TEXT, ppk_name TEXT, email TEXT, fullname TEXT, mobile_session integer, username TEXT)";

    private static final String CREATE_TABLE_BAGIAN_JALAN = "CREATE TABLE IF NOT EXISTS " + TABLE_BAGIAN_JALAN +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT , nama TEXT, parent_id integer, kode integer)";

    private static final String CREATE_TABLE_KERUSAKAN = "CREATE TABLE IF NOT EXISTS " + TABLE_KERUSAKAN +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT , nama TEXT, kode TEXT, bagian_jalan_id integer )";

    private static final String CREATE_TABLE_MODULE = "CREATE TABLE IF NOT EXISTS " + TABLE_MODULE +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT , reference tinyint(1), ruas tinyint(1), rusak tinyint(1), ruang_jalan tinyint(1), penilik tinyint(1), coa tinyint(1), journal_book tinyint(1), trial_balance tinyint(1), users tinyint(1), groups tinyint(1), group_privileges tinyint(1))";

    private static final String CREATE_TABLE_PEMANFAATAN = "CREATE TABLE IF NOT EXISTS " + TABLE_PEMANFAATAN +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT , nama TEXT )";

    private static final String CREATE_TABLE_RUANG_JALAN = "CREATE TABLE IF NOT EXISTS " + TABLE_RUANG_JALAN +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT , nama TEXT)";

    private static final String CREATE_TABLE_VERSION = "CREATE TABLE IF NOT EXISTS " + TABLE_VERSION +
            "(id INTEGER PRIMARY KEY , name TEXT, number integer, file TEXT, date_release TEXT)";

    private static final String CREATE_TABLE_RUAS = "CREATE TABLE IF NOT EXISTS " + TABLE_RUAS +
            "(id INTEGER PRIMARY KEY , unit_id integer, unit_name TEXT, satker_id integer, satker_name TEXT, ppk_id integer, ppk_name TEXT, penilik_id integer, penilik TEXT, nomor_1 integer, nomor_2 integer, nomor_3 TEXT, nama TEXT, panjang real, start TEXT, v_end TEXT )";

    private static final String CREATE_TABLE_RUAS_COORDINATES = "CREATE TABLE IF NOT EXISTS " + TABLE_RUAS_COORDINATES +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT , ruas_id INTEGER , lat REAL, lng REAL, point_km integer, point_m integer, date_surveyed TEXT, status integer )";

    private static final String CREATE_TABLE_RUSAK = "CREATE TABLE IF NOT EXISTS " + TABLE_RUSAK +
            "(id INTEGER PRIMARY KEY , ruas_id integer, kerusakan_id integer, bagian_jalan_id integer, date_surveyed TEXT, lat REAL, lng REAL, point_km integer, point_m integer, volume real, fixed tinyiny(1), kerusakan TEXT, bagian_jalan TEXT, status integer)";

    private static final String CREATE_TABLE_RUSAK_PHOTO = "CREATE TABLE IF NOT EXISTS " + TABLE_RUSAK_PHOTOS +
            "(id INTEGER PRIMARY KEY , rusak_id TEXT, name TEXT, photo BLOB, url TEXT, status integer)";

    private static final String CREATE_TABLE_PEMANFAATAN_RUANG = "CREATE TABLE IF NOT EXISTS " + TABLE_PEMANFAATAN_RUANG +
            "(id INTEGER PRIMARY KEY , ruas_id integer, ruang_jalan_id integer, pemanfaatan_id integer, date_surveyed TEXT, lat REAL, lng REAL, point_km integer, point_m integer, ruang_jalan TEXT, pemanfaatan TEXT, status integer )";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
    }

    public static String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_DATA);
        db.execSQL(CREATE_TABLE_BAGIAN_JALAN);
        db.execSQL(CREATE_TABLE_KERUSAKAN);
        db.execSQL(CREATE_TABLE_MODULE);
        db.execSQL(CREATE_TABLE_PEMANFAATAN);
        db.execSQL(CREATE_TABLE_RUANG_JALAN);
        db.execSQL(CREATE_TABLE_VERSION);

        db.execSQL(CREATE_TABLE_RUAS);
        db.execSQL(CREATE_TABLE_RUAS_COORDINATES);
        db.execSQL(CREATE_TABLE_RUSAK);
        db.execSQL(CREATE_TABLE_RUSAK_PHOTO);
        db.execSQL(CREATE_TABLE_PEMANFAATAN_RUANG);

        //db.execSQL("CREATE TABLE t_log_temp (v_id varchar(255), user_id integer, jsonText TEXT, sync_time varchar(255), status bigint(1), PRIMARY KEY(v_id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private String getJSONFile(String url) throws ExecutionException {
        String res = "";
        try {
            res = new HttpAsyncTask().execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }

    // cek user < 1 ambil semua || user > 1 get data only !!!!!
    //http://kepegbima.com/pjn2jabar/android/login.php?username=mikkir&password=penilik1
    public boolean login(String username, String password) throws JSONException, ExecutionException, InterruptedException {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM t_user WHERE username ='" + username + "' AND password='" + password + "' ", null);
        boolean success = false;
        String url = URL + "android/login.php?username=" + username + "&password=" + password /*+ "&structural=" + "0"*/;
        JSONObject jsonObject = new JSONObject(getJSONFile(url));

        if (jsonObject.getString("success") == "false") {
            return false;
        }
        if (cursor.getCount() < 1) {

            User user = new User(username, password, 0);
            ContentValues cvUser = new ContentValues();
            cvUser.put("username", username);
            cvUser.put("password", password);
            cvUser.put("structural", 0);
            success = db.insert(TABLE_USER, null, cvUser) > 0;

            Data data = new Data(
                    Integer.parseInt(jsonObject.getJSONArray("data").getJSONObject(0).getString("id")),
                    Integer.parseInt(jsonObject.getJSONArray("data").getJSONObject(0).getString("unit_id")),
                    Integer.parseInt(jsonObject.getJSONArray("data").getJSONObject(0).getString("satker_id")),
                    Integer.parseInt(jsonObject.getJSONArray("data").getJSONObject(0).getString("ppk_id")),
                    jsonObject.getJSONArray("data").getJSONObject(0).getString("unit_name"),
                    jsonObject.getJSONArray("data").getJSONObject(0).getString("satker_name"),
                    jsonObject.getJSONArray("data").getJSONObject(0).getString("ppk_name"),
                    jsonObject.getJSONArray("data").getJSONObject(0).getString("email"),
                    jsonObject.getJSONArray("data").getJSONObject(0).getString("fullname"),
                    Integer.parseInt(jsonObject.getJSONArray("data").getJSONObject(0).getString("mobile_session")));
            ContentValues cv = new ContentValues();
            cv.put("id", data.getId());
            cv.put("unit_id", data.getUnit_id());
            cv.put("satker_id", data.getSatker_id());
            cv.put("ppk_id", data.getPpk_id());
            cv.put("unit_name", data.getUnit_name());
            cv.put("satker_name", data.getSatker_name());
            cv.put("ppk_name", data.getPpk_name());
            cv.put("email", data.getEmail());
            cv.put("fullname", data.getFullname());
            cv.put("mobile_session", data.getMobile_session());
            cv.put("username", username);
            success = db.insert(TABLE_DATA, null, cv) > 0;

            Module module = new Module(
                    Integer.parseInt(jsonObject.getJSONObject("module").getString("references")),
                    Integer.parseInt(jsonObject.getJSONObject("module").getString("ruas")),
                    Integer.parseInt(jsonObject.getJSONObject("module").getString("rusak")),
                    Integer.parseInt(jsonObject.getJSONObject("module").getString("ruang_jalan")),
                    Integer.parseInt(jsonObject.getJSONObject("module").getString("penilik")),
                    Integer.parseInt(jsonObject.getJSONObject("module").getString("coa")),
                    Integer.parseInt(jsonObject.getJSONObject("module").getString("journal_book")),
                    Integer.parseInt(jsonObject.getJSONObject("module").getString("trial_balance")),
                    Integer.parseInt(jsonObject.getJSONObject("module").getString("users")),
                    Integer.parseInt(jsonObject.getJSONObject("module").getString("groups")),
                    Integer.parseInt(jsonObject.getJSONObject("module").getString("group_privileges"))
            );
            ContentValues content = new ContentValues();
            content.put("reference", module.getReferences());
            content.put("ruas", module.getRuas());
            content.put("rusak", module.getRusak());
            content.put("ruang_jalan", module.getRuang_jalan());
            content.put("penilik", module.getPenilik());
            content.put("coa", module.getCoa());
            content.put("journal_book", module.getJournal_book());
            content.put("trial_balance", module.getTrial_balance());
            content.put("users", module.getUsers());
            content.put("groups", module.getGroups());
            content.put("group_privileges", module.getGroup_privilages());
            success = db.insert(TABLE_MODULE, null, content) > 0;

            for (int i = 0; i < jsonObject.getJSONArray("bagian_jalan").length(); i++) {
                Bagian_Jalan bagian_jalan = new Bagian_Jalan(
                        Integer.parseInt(jsonObject.getJSONArray("bagian_jalan").getJSONObject(i).getString("id")),
                        jsonObject.getJSONArray("bagian_jalan").getJSONObject(i).getString("nama"),
                        Integer.parseInt(jsonObject.getJSONArray("bagian_jalan").getJSONObject(i).getString("parent_id")),
                        jsonObject.getJSONArray("bagian_jalan").getJSONObject(i).getString("kode")
                );
                ContentValues values = new ContentValues();
                values.put("id", bagian_jalan.getId());
                values.put("nama", bagian_jalan.getNama());
                values.put("parent_id", bagian_jalan.getParent_id());
                values.put("kode", bagian_jalan.getKode());
                success = db.insert(TABLE_BAGIAN_JALAN, null, values) > 0;
            }

            for (int i = 0; i < jsonObject.getJSONArray("kerusakan").length(); i++) {
                Kerusakan kerusakan = new Kerusakan(
                        Integer.parseInt(jsonObject.getJSONArray("kerusakan").getJSONObject(i).getString("id")),
                        jsonObject.getJSONArray("kerusakan").getJSONObject(i).getString("nama"),
                        jsonObject.getJSONArray("kerusakan").getJSONObject(i).getString("kode"),
                        Integer.parseInt(jsonObject.getJSONArray("kerusakan").getJSONObject(i).getString("bagian_jalan_id")));
                ContentValues contentValues = new ContentValues();
                contentValues.put("id", kerusakan.getId());
                contentValues.put("nama", kerusakan.getNama());
                contentValues.put("kode", kerusakan.getKode());
                contentValues.put("bagian_jalan_id", kerusakan.getBagian_jalan_id());
                success = db.insert(TABLE_KERUSAKAN, null, contentValues) > 0;
            }

            for (int i = 0; i < jsonObject.getJSONArray("ruang_jalan").length(); i++) {
                Ruang_Jalan ruang_jalan = new Ruang_Jalan(
                        Integer.parseInt(jsonObject.getJSONArray("ruang_jalan").getJSONObject(i).getString("id")),
                        jsonObject.getJSONArray("ruang_jalan").getJSONObject(i).getString("nama")
                );
                ContentValues contVal = new ContentValues();
                contVal.put("id", ruang_jalan.getId());
                contVal.put("nama", ruang_jalan.getNama());
                success = db.insert(TABLE_RUANG_JALAN, null, contVal) > 0;
            }

            for (int i = 0; i < jsonObject.getJSONArray("pemanfaatan").length(); i++) {
                Pemanfaatan pemanfaatan = new Pemanfaatan(
                        Integer.parseInt(jsonObject.getJSONArray("pemanfaatan").getJSONObject(i).getString("id")),
                        jsonObject.getJSONArray("pemanfaatan").getJSONObject(i).getString("nama")
                );
                ContentValues cont = new ContentValues();
                cont.put("id", pemanfaatan.getId());
                cont.put("nama", pemanfaatan.getNama());
                success = db.insert(TABLE_PEMANFAATAN, null, cont) > 0;
            }

            Version version = new Version(
                    Integer.parseInt(jsonObject.getJSONObject("version").getString("id")),
                    jsonObject.getJSONObject("version").getString("name"),
                    Float.parseFloat(jsonObject.getJSONObject("version").getString("number")),
                    jsonObject.getJSONObject("version").getString("file"),
                    jsonObject.getJSONObject("version").getString("date_release")
            );

            ContentValues cVersion = new ContentValues();
            cVersion.put("id", version.getId());
            cVersion.put("name", version.getNama());
            cVersion.put("number", version.getNumber());
            cVersion.put("file", version.getFile());
            cVersion.put("date_release", version.getDate_release());
            success = db.insert(TABLE_VERSION, null, cVersion) > 0;

        } else if (cursor.getCount() > 0) {
            db.delete(TABLE_USER, "username =?" , new String[]{username});
            db.delete(TABLE_DATA, "username =?" , new String[]{username});
            if (jsonObject.length() > 1) {
                User user = new User(username, password, 0);
                ContentValues cvUser = new ContentValues();
                cvUser.put("username", username);
                cvUser.put("password", password);
                cvUser.put("structural", 0);
                success = db.insert(TABLE_USER, null, cvUser) > 0;

                Data data = new Data(
                        Integer.parseInt(jsonObject.getJSONArray("data").getJSONObject(0).getString("id")),
                        Integer.parseInt(jsonObject.getJSONArray("data").getJSONObject(0).getString("unit_id")),
                        Integer.parseInt(jsonObject.getJSONArray("data").getJSONObject(0).getString("satker_id")),
                        Integer.parseInt(jsonObject.getJSONArray("data").getJSONObject(0).getString("ppk_id")),
                        jsonObject.getJSONArray("data").getJSONObject(0).getString("unit_name"),
                        jsonObject.getJSONArray("data").getJSONObject(0).getString("satker_name"),
                        jsonObject.getJSONArray("data").getJSONObject(0).getString("ppk_name"),
                        jsonObject.getJSONArray("data").getJSONObject(0).getString("email"),
                        jsonObject.getJSONArray("data").getJSONObject(0).getString("fullname"),
                        Integer.parseInt(jsonObject.getJSONArray("data").getJSONObject(0).getString("mobile_session"))
                );

                ContentValues cv = new ContentValues();
                cv.put("id", data.getId());
                cv.put("unit_id", data.getUnit_id());
                cv.put("satker_id", data.getSatker_id());
                cv.put("ppk_id", data.getPpk_id());
                cv.put("unit_name", data.getUnit_name());
                cv.put("satker_name", data.getSatker_name());
                cv.put("ppk_name", data.getPpk_name());
                cv.put("email", data.getEmail());
                cv.put("fullname", data.getFullname());
                cv.put("mobile_session", data.getMobile_session());
                cv.put("username", username);
                success = db.insert(TABLE_DATA, null, cv) > 0;

                db.execSQL("DELETE FROM " + TABLE_MODULE);
                Module module = new Module(
                        Integer.parseInt(jsonObject.getJSONObject("module").getString("references")),
                        Integer.parseInt(jsonObject.getJSONObject("module").getString("ruas")),
                        Integer.parseInt(jsonObject.getJSONObject("module").getString("rusak")),
                        Integer.parseInt(jsonObject.getJSONObject("module").getString("ruang_jalan")),
                        Integer.parseInt(jsonObject.getJSONObject("module").getString("penilik")),
                        Integer.parseInt(jsonObject.getJSONObject("module").getString("coa")),
                        Integer.parseInt(jsonObject.getJSONObject("module").getString("journal_book")),
                        Integer.parseInt(jsonObject.getJSONObject("module").getString("trial_balance")),
                        Integer.parseInt(jsonObject.getJSONObject("module").getString("users")),
                        Integer.parseInt(jsonObject.getJSONObject("module").getString("groups")),
                        Integer.parseInt(jsonObject.getJSONObject("module").getString("group_privileges"))
                );
                ContentValues content = new ContentValues();
                content.put("reference", module.getReferences());
                content.put("ruas", module.getRuas());
                content.put("rusak", module.getRusak());
                content.put("ruang_jalan", module.getRuang_jalan());
                content.put("penilik", module.getPenilik());
                content.put("coa", module.getCoa());
                content.put("journal_book", module.getJournal_book());
                content.put("trial_balance", module.getTrial_balance());
                content.put("users", module.getUsers());
                content.put("groups", module.getGroups());
                content.put("group_privileges", module.getGroup_privilages());
                success = db.insert(TABLE_MODULE, null, content) > 0;

                Version version = new Version(
                        Integer.parseInt(jsonObject.getJSONObject("version").getString("id")),
                        jsonObject.getJSONObject("version").getString("name"),
                        Float.parseFloat(jsonObject.getJSONObject("version").getString("number")),
                        jsonObject.getJSONObject("version").getString("file"),
                        jsonObject.getJSONObject("version").getString("date_release")
                );

                ContentValues cVersion = new ContentValues();
                cVersion.put("id", version.getId());
                cVersion.put("name", version.getNama());
                cVersion.put("number", version.getNumber());
                cVersion.put("file", version.getFile());
                cVersion.put("date_release", version.getDate_release());
                success = db.insert(TABLE_VERSION, null, cVersion) > 0;
            }
        }

        cursor.close();
        db.close();
        return success;
    }

    //get all ruas
    //http://kepegbima.com/pjn2jabar/android/get_ruas.php?user_id=1&session=1537324641
    public void getRuas(int user_id, int session) throws JSONException, ExecutionException, InterruptedException {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM t_data WHERE id ='" + user_id + "' AND mobile_session='" + session + "' ", null);
        String url = URL + "android/get_ruas.php?user_id=" + user_id + "&session=" + session;
        JSONObject jsonObject = new JSONObject(getJSONFile(url));
        int result = Integer.parseInt(jsonObject.getString("results"));
        if (cursor != null || cursor.getCount() != result) {
            if (jsonObject.length() > 1) {
                db.execSQL("DELETE FROM " + TABLE_RUAS);
                int nomor2;
                for (int i = 0; i < jsonObject.getJSONArray("rows").length(); i++) {
                    try{
                        nomor2 = Integer.parseInt(jsonObject.getJSONArray("rows").getJSONObject(i).getString("nomor_2"));
                    }catch(NumberFormatException ex){ // handle your exception
                        nomor2 = 0;
                    }
                    Ruas ruas = new Ruas(
                            Integer.parseInt(jsonObject.getJSONArray("rows").getJSONObject(i).getString("id")),
                            Integer.parseInt(jsonObject.getJSONArray("rows").getJSONObject(i).getString("unit_id")),
                            jsonObject.getJSONArray("rows").getJSONObject(i).getString("unit_name"),
                            Integer.parseInt(jsonObject.getJSONArray("rows").getJSONObject(i).getString("satker_id")),
                            jsonObject.getJSONArray("rows").getJSONObject(i).getString("satker_name"),
                            Integer.parseInt(jsonObject.getJSONArray("rows").getJSONObject(i).getString("ppk_id")),
                            jsonObject.getJSONArray("rows").getJSONObject(i).getString("ppk_name"),
                            Integer.parseInt(jsonObject.getJSONArray("rows").getJSONObject(i).getString("penilik_id")),
                            jsonObject.getJSONArray("rows").getJSONObject(i).getString("penilik"),
                            Integer.parseInt(jsonObject.getJSONArray("rows").getJSONObject(i).getString("nomor_1")),
                            nomor2,
                            jsonObject.getJSONArray("rows").getJSONObject(i).getString("nomor_3"),
                            jsonObject.getJSONArray("rows").getJSONObject(i).getString("nama"),
                            Float.parseFloat(jsonObject.getJSONArray("rows").getJSONObject(i).getString("panjang")),
                            jsonObject.getJSONArray("rows").getJSONObject(i).getString("start"),
                            jsonObject.getJSONArray("rows").getJSONObject(i).getString("end")
                    );
                    ContentValues cv = new ContentValues();
                    cv.put("id", ruas.getId());
                    cv.put("unit_id", ruas.getUnit_id());
                    cv.put("unit_name", ruas.getUnit_name());
                    cv.put("satker_id", ruas.getSatker_id());
                    cv.put("satker_name", ruas.getSatker_name());
                    cv.put("ppk_id", ruas.getPpk_id());
                    cv.put("ppk_name", ruas.getPpk_name());
                    cv.put("penilik_id", ruas.getPenilik_id());
                    cv.put("penilik", ruas.getPenilik());
                    cv.put("nomor_1", ruas.getNomor_1());
                    cv.put("nomor_2", ruas.getNomor_2());
                    cv.put("nomor_3", ruas.getNomor_3());
                    cv.put("nama", ruas.getNama());
                    cv.put("panjang", ruas.getPanjang());
                    cv.put("start", ruas.getStart());
                    cv.put("v_end", ruas.getEnd());
                    db.insert(TABLE_RUAS, null, cv);
                }
            }
        }
        cursor.close();
        db.close();
    }

    public void getRuas_Detail(int user_id, int session, int ruas_id) throws JSONException, ExecutionException, InterruptedException {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM t_ruas_coordinates WHERE ruas_id ='" + ruas_id + "'", null);
        String url = URL + "android/get_ruas_detail.php?user_id=" + user_id + "&ruas_id=" + ruas_id + "&session=" + session;
        JSONObject jsonObject = new JSONObject(getJSONFile(url));
        int result = Integer.parseInt(jsonObject.getString("results"));
        if (cursor != null || cursor.getCount() != result) {
            db.execSQL("DELETE FROM " + TABLE_RUAS_COORDINATES + " WHERE ruas_id = " + ruas_id + " AND status == 0");
            if (jsonObject.getString("success").equals("true")) {
                for (int i = 0; i < result; i++) {
                    Ruas_Coordinates ruas_coordinates = new Ruas_Coordinates(
                            ruas_id,
                            jsonObject.getJSONArray("rows").getJSONObject(i).getDouble("lat"),
                            jsonObject.getJSONArray("rows").getJSONObject(i).getDouble("lng"),
                            Integer.parseInt(jsonObject.getJSONArray("rows").getJSONObject(i).getString("point_km")),
                            Integer.parseInt(jsonObject.getJSONArray("rows").getJSONObject(i).getString("point_m")),
                            jsonObject.getJSONArray("rows").getJSONObject(i).getString("date_surveyed"),
                            0
                    );
                    ContentValues cv = new ContentValues();
                    cv.put("ruas_id", ruas_id);
                    cv.put("lat", ruas_coordinates.getLat());
                    cv.put("lng", ruas_coordinates.getLng());
                    cv.put("point_km", ruas_coordinates.getPoint_km());
                    cv.put("point_m", ruas_coordinates.getPoint_m());
                    cv.put("date_surveyed", ruas_coordinates.getDate_surveyed());
                    cv.put("status", ruas_coordinates.getStatus());
                    db.insert(TABLE_RUAS_COORDINATES, null, cv);
                }
            }
        }

        cursor.close();
        db.close();
    }

    public List<Ruas> getTableRuas() {
        List<Ruas> ruasList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RUAS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Ruas ruas = new Ruas();
                ruas.setId(cursor.getInt(cursor.getColumnIndex("id")));
                ruas.setUnit_id(cursor.getInt(cursor.getColumnIndex("unit_id")));
                ruas.setUnit_name(cursor.getString(cursor.getColumnIndex("unit_name")));
                ruas.setSatker_id(cursor.getInt(cursor.getColumnIndex("satker_id")));
                ruas.setSatker_name(cursor.getString(cursor.getColumnIndex("satker_name")));
                ruas.setPpk_id(cursor.getInt(cursor.getColumnIndex("ppk_id")));
                ruas.setPpk_name(cursor.getString(cursor.getColumnIndex("ppk_name")));
                ruas.setPenilik_id(cursor.getInt(cursor.getColumnIndex("penilik_id")));
                ruas.setPenilik(cursor.getString(cursor.getColumnIndex("penilik")));
                ruas.setNomor_1(cursor.getInt(cursor.getColumnIndex("nomor_1")));
                ruas.setNomor_2(cursor.getInt(cursor.getColumnIndex("nomor_2")));
                ruas.setNomor_3(cursor.getString(cursor.getColumnIndex("nomor_3")));
                ruas.setNama(cursor.getString(cursor.getColumnIndex("nama")));
                ruas.setPanjang(cursor.getFloat(cursor.getColumnIndex("panjang")));
                ruas.setStart(cursor.getString(cursor.getColumnIndex("start")));
                ruas.setEnd(cursor.getString(cursor.getColumnIndex("v_end")));

                ruasList.add(ruas);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return ruasList;
    }

    public List<Ruas_Coordinates> getTabelCoordinate(int ruas_id) {
        List<Ruas_Coordinates> coordinatesList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RUAS_COORDINATES + " WHERE ruas_id=" + ruas_id + " AND status != 3 ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Ruas_Coordinates coordinates = new Ruas_Coordinates();
                coordinates.setRuas_id(cursor.getInt(cursor.getColumnIndex("ruas_id")));
                coordinates.setLat(cursor.getDouble(cursor.getColumnIndex("lat")));
                coordinates.setLng(cursor.getDouble(cursor.getColumnIndex("lng")));
                coordinates.setPoint_km(cursor.getInt(cursor.getColumnIndex("point_km")));
                coordinates.setPoint_m(cursor.getInt(cursor.getColumnIndex("point_m")));
                coordinates.setDate_surveyed(cursor.getString(cursor.getColumnIndex("date_surveyed")));
                coordinates.setStatus(cursor.getInt(cursor.getColumnIndex("status")));

                coordinatesList.add(coordinates);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return coordinatesList;
    }

    public boolean tambahRuasCoordinate(int ruas_id, double lat, double lng, int km, int m, String date, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ruas_id", ruas_id);
        contentValues.put("lat", lat);
        contentValues.put("lng", lng);
        contentValues.put("point_km", km);
        contentValues.put("point_m", m);
        contentValues.put("date_surveyed", date);
        contentValues.put("status", status);

        Log.d(TAG, "addData: Adding " + ruas_id + " to " + TABLE_RUAS_COORDINATES);

        long result = db.insert(TABLE_RUAS_COORDINATES, null, contentValues);

        db.close();
        return result != -1;
    }

    public Ruas getObjekRuas(int item) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_RUAS,
                new String[]{"id", "unit_id", "unit_name", "satker_id", "satker_name", "ppk_id", "ppk_name", "penilik_id", "penilik", "nomor_1", "nomor_2", "nomor_3", "nama", "panjang", "start", "v_end"},
                "id =?",
                new String[]{String.valueOf(item)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToNext();
        }

        Ruas ruas = new Ruas(
                cursor.getInt(cursor.getColumnIndex("id")),
                cursor.getInt(cursor.getColumnIndex("unit_id")),
                cursor.getString(cursor.getColumnIndex("unit_name")),
                cursor.getInt(cursor.getColumnIndex("satker_id")),
                cursor.getString(cursor.getColumnIndex("satker_name")),
                cursor.getInt(cursor.getColumnIndex("ppk_id")),
                cursor.getString(cursor.getColumnIndex("ppk_name")),
                cursor.getInt(cursor.getColumnIndex("penilik_id")),
                cursor.getString(cursor.getColumnIndex("penilik")),
                cursor.getInt(cursor.getColumnIndex("nomor_1")),
                cursor.getInt(cursor.getColumnIndex("nomor_2")),
                cursor.getString(cursor.getColumnIndex("nomor_3")),
                cursor.getString(cursor.getColumnIndex("nama")),
                cursor.getFloat(cursor.getColumnIndex("panjang")),
                cursor.getString(cursor.getColumnIndex("start")),
                cursor.getString(cursor.getColumnIndex("v_end")));
        cursor.close();
        db.close();
        return ruas;
    }

    public Ruas_Coordinates getObjekCoordinate(int ruas_id, double lat) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RUAS_COORDINATES + " WHERE lat = " + lat + " AND ruas_id = " + ruas_id, null);
        if (cursor != null) {
            cursor.moveToNext();
        }
        Ruas_Coordinates ruas = new Ruas_Coordinates(
                cursor.getInt(cursor.getColumnIndex("ruas_id")),
                cursor.getDouble(cursor.getColumnIndex("lat")),
                cursor.getDouble(cursor.getColumnIndex("lng")),
                cursor.getInt(cursor.getColumnIndex("point_km")),
                cursor.getInt(cursor.getColumnIndex("point_m")),
                cursor.getString(cursor.getColumnIndex("date_surveyed")),
                cursor.getInt(cursor.getColumnIndex("status")));

        cursor.close();
        db.close();
        return ruas;
    }

    public Rusak getObjekRusak(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RUSAK,
                new String[]{"id", "ruas_id", "kerusakan_id", "bagian_jalan_id", "date_surveyed", "lat", "lng", "point_km", "point_m", "volume", "fixed", "bagian_jalan", "kerusakan", "status"},
                "id=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Rusak kegiatan = new Rusak(
                cursor.getInt(cursor.getColumnIndex("id")),
                cursor.getInt(cursor.getColumnIndex("ruas_id")),
                cursor.getInt(cursor.getColumnIndex("kerusakan_id")),
                cursor.getInt(cursor.getColumnIndex("bagian_jalan_id")),
                cursor.getString(cursor.getColumnIndex("date_surveyed")),
                cursor.getDouble(cursor.getColumnIndex("lat")),
                cursor.getDouble(cursor.getColumnIndex("lng")),
                cursor.getInt(cursor.getColumnIndex("point_km")),
                cursor.getInt(cursor.getColumnIndex("point_m")),
                cursor.getFloat(cursor.getColumnIndex("volume")),
                cursor.getInt(cursor.getColumnIndex("fixed")),
                cursor.getString(cursor.getColumnIndex("bagian_jalan")),
                cursor.getString(cursor.getColumnIndex("kerusakan")),
                cursor.getInt(cursor.getColumnIndex("status"))
        );
        cursor.close();
        db.close();
        return kegiatan;
    }

    public Pemanfaatan_Ruang getObjekPemanfaatanRuang(int item) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PEMANFAATAN_RUANG,
                new String[]{"id", "ruas_id", "ruang_jalan_id", "pemanfaatan_id", "date_surveyed", "lat", "lng", "point_km", "point_m", "ruang_jalan", "pemanfaatan", "status"},
                "id =?",
                new String[]{String.valueOf(item)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToNext();
        }

        Pemanfaatan_Ruang ruang = new Pemanfaatan_Ruang(
                cursor.getInt(cursor.getColumnIndex("id")),
                cursor.getInt(cursor.getColumnIndex("ruas_id")),
                cursor.getInt(cursor.getColumnIndex("ruang_jalan_id")),
                cursor.getInt(cursor.getColumnIndex("pemanfaatan_id")),
                cursor.getString(cursor.getColumnIndex("date_surveyed")),
                cursor.getDouble(cursor.getColumnIndex("lat")),
                cursor.getDouble(cursor.getColumnIndex("lng")),
                cursor.getInt(cursor.getColumnIndex("point_km")),
                cursor.getInt(cursor.getColumnIndex("point_m")),
                cursor.getString(cursor.getColumnIndex("ruang_jalan")),
                cursor.getString(cursor.getColumnIndex("pemanfaatan")),
                cursor.getInt(cursor.getColumnIndex("status")));

        cursor.close();
        db.close();
        return ruang;
    }

    //get all kerusakan
    //http://mtfco.ddns.net/pjn2jabar/android/get_kerusakan.php?user_id=1&ruas_id=1&session=1537324641
    public void getKerusakan(int user_id, int session, int ruas_id) throws JSONException, ExecutionException, InterruptedException,NullPointerException {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RUSAK + " WHERE ruas_id = " + ruas_id, null);
        String url = URL + "android/get_kerusakan.php?user_id=" + user_id + "&ruas_id=" + ruas_id + "&session=" + session;
        JSONObject jsonObject = new JSONObject(getJSONFile(url));
        int result = Integer.parseInt(jsonObject.getString("results"));

        if (cursor != null || cursor.getCount() != result) {
            db.execSQL("DELETE FROM " + TABLE_RUSAK + " WHERE ruas_id = " + ruas_id + " AND status == 0");
            if (jsonObject.getString("success").equals("true")) {
                for (int i = 0; i < result; i++) {


                    Rusak rusak = new Rusak(
                            Integer.parseInt(jsonObject.getJSONArray("rows").getJSONObject(i).getString("id")),
                            Integer.parseInt(jsonObject.getJSONArray("rows").getJSONObject(i).getString("ruas_id")),
                            Integer.parseInt(jsonObject.getJSONArray("rows").getJSONObject(i).getString("kerusakan_id")),
                            Integer.parseInt(jsonObject.getJSONArray("rows").getJSONObject(i).getString("bagian_jalan_id")),
                            jsonObject.getJSONArray("rows").getJSONObject(i).getString("date_surveyed"),
                            jsonObject.getJSONArray("rows").getJSONObject(i).getDouble("lat"),
                            jsonObject.getJSONArray("rows").getJSONObject(i).getDouble("lng"),
                            Integer.parseInt(jsonObject.getJSONArray("rows").getJSONObject(i).getString("point_km")),
                            Integer.parseInt(jsonObject.getJSONArray("rows").getJSONObject(i).getString("point_m")),
                            Float.parseFloat(jsonObject.getJSONArray("rows").getJSONObject(i).getString("volume")),
                            Integer.parseInt(jsonObject.getJSONArray("rows").getJSONObject(i).getString("fixed")),
                            jsonObject.getJSONArray("rows").getJSONObject(i).getString("bagian_jalan"),
                            jsonObject.getJSONArray("rows").getJSONObject(i).getString("kerusakan"),
                            0
                    );

                    ContentValues cv = new ContentValues();
                    cv.put("id", rusak.getId());
                    cv.put("ruas_id", rusak.getRuas_id());
                    cv.put("kerusakan_id", rusak.getKerusakan_id());
                    cv.put("bagian_jalan_id", rusak.getBagian_jalan_id());
                    cv.put("date_surveyed", rusak.getDate_surveyed());
                    cv.put("lat", rusak.getLat());
                    cv.put("lng", rusak.getLng());
                    cv.put("point_km", rusak.getPoint_km());
                    cv.put("point_m", rusak.getPoint_m());
                    cv.put("volume", rusak.getVolume());
                    cv.put("fixed", rusak.getFixed());
                    cv.put("bagian_jalan", rusak.getBagian_jalan());
                    cv.put("kerusakan", rusak.getKerusakan());
                    cv.put("status", rusak.getStatus());
                    db.insert(TABLE_RUSAK, null, cv);
                    db.execSQL("DELETE FROM " + TABLE_RUSAK_PHOTOS + " WHERE rusak_id = " + rusak.getId() + " AND status == 0");
                    Image image = new Image();

                    for (int j = 0; j < jsonObject.getJSONArray("rows").getJSONObject(i).getJSONArray("photos").length(); j++) {

                        String name;
                        try{
                            name = jsonObject.getJSONArray("rows").getJSONObject(i).getJSONArray("photos").getJSONObject(j).getString("name");
                        }catch(NullPointerException ex){ // handle your exception
                            name = "";
                            Log.d("ex" , ex.getMessage());
                        }
                        try {
                            image = new Image(
                                    Integer.parseInt(jsonObject.getJSONArray("rows").getJSONObject(i).getJSONArray("photos").getJSONObject(j).getString("id")),
                                    Integer.parseInt(jsonObject.getJSONArray("rows").getJSONObject(i).getString("id")),
                                    name,
                                    jsonObject.getJSONArray("rows").getJSONObject(i).getJSONArray("photos").getJSONObject(j).getString("photo").getBytes(Charset.forName("UTF-8").toString()),
                                    jsonObject.getJSONArray("rows").getJSONObject(i).getJSONArray("photos").getJSONObject(j).getString("photo"),
                                    0
                            );
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        ContentValues cc = new ContentValues();
                        cc.put("id", image.getId());
                        cc.put("rusak_id", image.getRusak_id());
                        cc.put("name", image.getName());
                        cc.put("photo", image.getPhoto());
                        cc.put("url", image.getUrl());
                        cc.put("status", image.getStatus());
                        db.insert(TABLE_RUSAK_PHOTOS, null, cc);
                        downloadImage(image.getId() + "", image.getUrl());

                    }
                }
            }
        }

        cursor.close();
        db.close();
    }

    public void downloadImage(String id, String url) {
        BackTask bt = new BackTask();
        bt.execute(url, id);
    }

    public List<Ruas_Coordinates> getLocalCoordinate(int ruas_id) {
        List<Ruas_Coordinates> coordinatesList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RUAS_COORDINATES + " WHERE ruas_id = " + ruas_id + " AND status != 0 ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Ruas_Coordinates coordinates = new Ruas_Coordinates();
                coordinates.setRuas_id(cursor.getInt(cursor.getColumnIndex("ruas_id")));
                coordinates.setLat(cursor.getDouble(cursor.getColumnIndex("lat")));
                coordinates.setLng(cursor.getDouble(cursor.getColumnIndex("lng")));
                coordinates.setPoint_km(cursor.getInt(cursor.getColumnIndex("point_km")));
                coordinates.setPoint_m(cursor.getInt(cursor.getColumnIndex("point_m")));
                coordinates.setDate_surveyed(cursor.getString(cursor.getColumnIndex("date_surveyed")));
                coordinates.setStatus(cursor.getInt(cursor.getColumnIndex("status")));

                coordinatesList.add(coordinates);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return coordinatesList;
    }

    public List<Rusak> getLocalKerusakan(int ruas_id) {
        List<Rusak> rusakList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RUSAK + " WHERE ruas_id = " + ruas_id + " AND status != 0 ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Rusak rusak = new Rusak();
                rusak.setId(cursor.getInt(cursor.getColumnIndex("id")));
                rusak.setRuas_id(cursor.getInt(cursor.getColumnIndex("ruas_id")));
                rusak.setKerusakan_id(cursor.getInt(cursor.getColumnIndex("kerusakan_id")));
                rusak.setBagian_jalan_id(cursor.getInt(cursor.getColumnIndex("bagian_jalan_id")));
                rusak.setDate_surveyed(cursor.getString(cursor.getColumnIndex("date_surveyed")));
                rusak.setLat(cursor.getDouble(cursor.getColumnIndex("lat")));
                rusak.setLng(cursor.getDouble(cursor.getColumnIndex("lng")));
                rusak.setPoint_km(cursor.getInt(cursor.getColumnIndex("point_km")));
                rusak.setPoint_m(cursor.getInt(cursor.getColumnIndex("point_m")));
                rusak.setVolume(cursor.getFloat(cursor.getColumnIndex("volume")));
                rusak.setFixed(cursor.getInt(cursor.getColumnIndex("fixed")));
                rusak.setBagian_jalan(cursor.getString(cursor.getColumnIndex("bagian_jalan")));
                rusak.setKerusakan(cursor.getString(cursor.getColumnIndex("kerusakan")));
                rusak.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                rusakList.add(rusak);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return rusakList;
    }

    public List<Pemanfaatan_Ruang> getLocalPemanfaatan(int ruas_id) {
        List<Pemanfaatan_Ruang> ruangList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PEMANFAATAN_RUANG + " WHERE ruas_id = " + ruas_id + " AND status != 0";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Pemanfaatan_Ruang ruang = new Pemanfaatan_Ruang();
                ruang.setId(cursor.getInt(cursor.getColumnIndex("id")));
                ruang.setRuas_id(cursor.getInt(cursor.getColumnIndex("ruas_id")));
                ruang.setRuang_jalan_id(cursor.getInt(cursor.getColumnIndex("ruang_jalan_id")));
                ruang.setPemanfaatan_id(cursor.getInt(cursor.getColumnIndex("pemanfaatan_id")));
                ruang.setDate_surveyed(cursor.getString(cursor.getColumnIndex("date_surveyed")));
                ruang.setLat(cursor.getDouble(cursor.getColumnIndex("lat")));
                ruang.setLng(cursor.getDouble(cursor.getColumnIndex("lng")));
                ruang.setPoint_km(cursor.getInt(cursor.getColumnIndex("point_km")));
                ruang.setPoint_m(cursor.getInt(cursor.getColumnIndex("point_m")));
                ruang.setRuang_jalan(cursor.getString(cursor.getColumnIndex("ruang_jalan")));
                ruang.setPemanfaatan(cursor.getString(cursor.getColumnIndex("pemanfaatan")));
                ruang.setStatus(cursor.getInt(cursor.getColumnIndex("status")));

                ruangList.add(ruang);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return ruangList;
    }

    public List<Image> getLocalImage(int rusak_id) {
        List<Image> imageList = new ArrayList<>();
        /*SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_RUSAK_PHOTOS,
                new String[]{"id", "rusak_id", "name", "photo"},
                "rusak_id =?",
                new String[]{String.valueOf(rusak_id)}, null, null, null, null);*/
        String selectQuery = "SELECT * FROM " + TABLE_RUSAK_PHOTOS + " WHERE rusak_id = " + rusak_id + " AND status != 0";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Image rusak = new Image();
                rusak.setId(cursor.getInt(cursor.getColumnIndex("id")));
                rusak.setRusak_id(cursor.getInt(cursor.getColumnIndex("rusak_id")));
                rusak.setName(cursor.getString(cursor.getColumnIndex("name")));
                rusak.setPhoto(cursor.getBlob(cursor.getColumnIndex("photo")));
                rusak.setStatus(cursor.getInt(cursor.getColumnIndex("status")));

                imageList.add(rusak);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return imageList;
    }

    private class BackTask extends AsyncTask<String, Void, Bitmap> {
        String id = "";

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            try {
                // Download the image
                URL url = new URL(strings[0]);
                id = new String(strings[1]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream is = connection.getInputStream();
                // Decode image to get smaller image to save memory
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                options.inSampleSize = 4;
                bitmap = BitmapFactory.decodeStream(is, null, options);
                is.close();
            } catch (IOException e) {
                return null;
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            // Insert bitmap to the database
            insertBitmap(result, id);
        }
    }

    public void insertBitmap(Bitmap bm, String id) {
        // Convert the image into byte array
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
        byte[] buffer = out.toByteArray();
        // Open the database for writing
        SQLiteDatabase db = this.getWritableDatabase();
        // Start the transaction.
        db.beginTransaction();
        ContentValues values;

        try {
            values = new ContentValues();
            values.put("photo", buffer);
            // Insert Row
            long i = db.update(TABLE_RUSAK_PHOTOS, values, "id = ?",
                    new String[]{id + ""});
            // Insert into database successfully.
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            // End the transaction.
            db.close();
            // Close database
        }
    }

    public List<Rusak> getTabelRusak(int ruas_id) {
        List<Rusak> rusakList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_RUSAK,
                new String[]{"id", "ruas_id", "kerusakan_id", "bagian_jalan_id", "date_surveyed", "lat", "lng", "point_km", "point_m", "volume", "fixed", "bagian_jalan", "kerusakan", "status"},
                "ruas_id =? AND status != 3",
                new String[]{String.valueOf(ruas_id)}, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Rusak rusak = new Rusak();
                rusak.setId(cursor.getInt(cursor.getColumnIndex("id")));
                rusak.setRuas_id(cursor.getInt(cursor.getColumnIndex("ruas_id")));
                rusak.setKerusakan_id(cursor.getInt(cursor.getColumnIndex("kerusakan_id")));
                rusak.setBagian_jalan_id(cursor.getInt(cursor.getColumnIndex("bagian_jalan_id")));
                rusak.setDate_surveyed(cursor.getString(cursor.getColumnIndex("date_surveyed")));
                rusak.setLat(cursor.getDouble(cursor.getColumnIndex("lat")));
                rusak.setLng(cursor.getDouble(cursor.getColumnIndex("lng")));
                rusak.setPoint_km(cursor.getInt(cursor.getColumnIndex("point_km")));
                rusak.setPoint_m(cursor.getInt(cursor.getColumnIndex("point_m")));
                rusak.setVolume(cursor.getFloat(cursor.getColumnIndex("volume")));
                rusak.setFixed(cursor.getInt(cursor.getColumnIndex("fixed")));
                rusak.setBagian_jalan(cursor.getString(cursor.getColumnIndex("bagian_jalan")));
                rusak.setKerusakan(cursor.getString(cursor.getColumnIndex("kerusakan")));
                rusak.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                rusakList.add(rusak);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return rusakList;
    }

    public boolean tambahKerusakan(int ruas_id, int kerusakan_id, int bagian_jalan_id, String
            date_surveyed, double lat, double lng, int point_km, int point_m, float volume,
                                   int fixed, String kerusakan, String bagian_jalan, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ruas_id", ruas_id);
        contentValues.put("kerusakan_id", kerusakan_id);
        contentValues.put("bagian_jalan_id", bagian_jalan_id);
        contentValues.put("date_surveyed", date_surveyed);
        contentValues.put("lat", lat);
        contentValues.put("lng", lng);
        contentValues.put("point_km", point_km);
        contentValues.put("point_m", point_m);
        contentValues.put("volume", volume);
        contentValues.put("fixed", fixed);
        contentValues.put("bagian_jalan", bagian_jalan);
        contentValues.put("kerusakan", kerusakan);
        contentValues.put("status", status);

        Log.d(TAG, "addData: Adding " + ruas_id + " to " + TABLE_RUSAK);

        long result = db.insert(TABLE_RUSAK, null, contentValues);
        db.close();

        return result != -1;
    }

    public int updateKerusakan(int rusak_id, int ruas_id, int kerusakan_id,
                               int bagian_jalan_id, String date_surveyed, double lat, double lng, int point_km, int point_m,
                               float volume, int fixed, String rusak, String bagian_jalan, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", rusak_id);
        cv.put("ruas_id", ruas_id);
        cv.put("kerusakan_id", kerusakan_id);
        cv.put("bagian_jalan_id", bagian_jalan_id);
        cv.put("date_surveyed", date_surveyed);
        cv.put("lat", lat);
        cv.put("lng", lng);
        cv.put("point_km", point_km);
        cv.put("point_m", point_m);
        cv.put("volume", volume);
        cv.put("fixed", fixed);
        cv.put("kerusakan", rusak);
        cv.put("bagian_jalan", bagian_jalan);
        cv.put("status", status);

        int res = db.update(TABLE_RUSAK, cv, "id = ?",
                new String[]{rusak_id + ""});
        return res;
    }

    public int updatePemanfaatan(int id, int ruas_id, int ruang_jalan_id,
                                 int pemanfaatan_id, String date_surveyed, double lat, double lng, int point_km,
                                 int point_m, String ruang_jalan, String pemanfaatan, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ruas_id", ruas_id);
        cv.put("ruang_jalan_id", ruang_jalan_id);
        cv.put("pemanfaatan_id", pemanfaatan_id);
        cv.put("date_surveyed", date_surveyed);
        cv.put("lat", lat);
        cv.put("lng", lng);
        cv.put("point_km", point_km);
        cv.put("point_m", point_m);
        cv.put("ruang_jalan", ruang_jalan);
        cv.put("pemanfaatan", pemanfaatan);
        cv.put("status", status);

        return db.update(TABLE_PEMANFAATAN_RUANG, cv, "id = ?",
                new String[]{id + ""});
    }

    public int updateImage(int id,int rusak_id,String name, byte[] img,  int status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("rusak_id", rusak_id);
        cv.put("name", img);
        cv.put("status", status);

        return db.update(TABLE_RUSAK_PHOTOS, cv, "id = ?",
                new String[]{id + ""});
    }

    public boolean deleteCoordinate(double lat, double lng,int km, int m, String date,int status ){
        boolean success = false;
        SQLiteDatabase db = this.getWritableDatabase();
        success = db.delete(TABLE_RUAS_COORDINATES, "lat =? AND lng =? AND km=? AND m=? AND date_surveyed =? AND status =?" , new String[]{lat+"", lng+ "", km + "" , m+"", date,status+""}) > 0;
        db.close();
        return success;
    }

    public boolean deleteKerusakan(int id, int status) {
        boolean success = false;
        if ((cekKerusakanPhoto(id))) {
            deleteLaporanGambar(id);
        }
        SQLiteDatabase db = this.getWritableDatabase();
        success = db.delete(TABLE_RUSAK, "id =? AND status =?" , new String[]{id+"", status + ""}) > 0;
        db.close();
        return success;
    }

    public boolean deleteLaporanGambar(int id) throws SQLException {
        boolean success = false;
        SQLiteDatabase db = this.getWritableDatabase();
        success = db.delete(TABLE_RUSAK_PHOTOS, "rusak_id=?", new String[]{id + ""}) > 0;
        db.close();
        return success;
    }

    public boolean deleteLocalGambar(int id) throws SQLException {
        boolean success = false;
        SQLiteDatabase db = this.getWritableDatabase();
        success = db.delete(TABLE_RUSAK_PHOTOS, "id=? AND status == 1", new String[]{id + ""}) > 0;
        db.close();
        return success;
    }

    public boolean cekKerusakanPhoto(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RUSAK_PHOTOS,// Selecting Table
                new String[]{"rusak_id"},
                "rusak_id" + "=?",
                new String[]{id + ""},//Where clause
                null, null, null);
        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
            return true;
        }
        db.close();
        return false;
    }

    public boolean deletePemanfaatan(int id, int status) {
        boolean success = false;
        SQLiteDatabase db = this.getWritableDatabase();
        success = db.delete(TABLE_PEMANFAATAN_RUANG, "id =? AND status =?", new String[]{id+"", status +""}) > 0;
        db.close();
        return success;
    }

    // get all photo
    public List<Image> getTabelRusakPhotos(int rusak_id) {
        List<Image> imageList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_RUSAK_PHOTOS,
                new String[]{"id", "rusak_id", "name", "photo", "status"},
                "rusak_id =? AND status != 3",
                new String[]{String.valueOf(rusak_id)}, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Image rusak = new Image();
                rusak.setId(cursor.getInt(cursor.getColumnIndex("id")));
                rusak.setRusak_id(cursor.getInt(cursor.getColumnIndex("rusak_id")));
                rusak.setName(cursor.getString(cursor.getColumnIndex("name")));
                rusak.setPhoto(cursor.getBlob(cursor.getColumnIndex("photo")));
                rusak.setStatus(cursor.getInt(cursor.getColumnIndex("status")));

                imageList.add(rusak);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return imageList;
    }

    public boolean tambahPhoto(int rusak_id, String name, byte[] image, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("rusak_id", rusak_id);
        contentValues.put("name", name);
        contentValues.put("photo", image);
        contentValues.put("status", status);

        Log.d(TAG, "addData: Adding " + rusak_id + " to " + TABLE_RUSAK_PHOTOS);

        long result = db.insert(TABLE_RUSAK_PHOTOS, null, contentValues);

        db.close();
        return result != -1;
    }

    //get All Pemanfaatan
    //http://mtfco.ddns.net/pjn2jabar/android/get_pemanfaatan.php?user_id=1&ruas_id=1&session=1537324641
    public void getPemanfaatan(int user_id, int session, int ruas_id) throws
            JSONException, ExecutionException, InterruptedException {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PEMANFAATAN_RUANG + " WHERE ruas_id ='" + ruas_id + "'", null);
        String url = URL + "android/get_pemanfaatan.php?user_id=" + user_id + "&ruas_id=" + ruas_id + "&session=" + session;
        JSONObject jsonObject = new JSONObject(getJSONFile(url));
        int result = Integer.parseInt(jsonObject.getString("results"));
        if (cursor != null || cursor.getCount() != result) {
            db.execSQL("DELETE FROM " + TABLE_PEMANFAATAN_RUANG + " WHERE ruas_id = " + ruas_id + " AND status == 0");
            if (jsonObject.getString("success").equals("true")) {
                for (int i = 0; i < result; i++) {
                    Pemanfaatan_Ruang ruang = new Pemanfaatan_Ruang(
                            Integer.parseInt(jsonObject.getJSONArray("rows").getJSONObject(i).getString("id")),
                            ruas_id,
                            Integer.parseInt(jsonObject.getJSONArray("rows").getJSONObject(i).getString("ruang_jalan_id")),
                            Integer.parseInt(jsonObject.getJSONArray("rows").getJSONObject(i).getString("pemanfaatan_id")),
                            jsonObject.getJSONArray("rows").getJSONObject(i).getString("date_surveyed"),
                            jsonObject.getJSONArray("rows").getJSONObject(i).getDouble("lat"),
                            jsonObject.getJSONArray("rows").getJSONObject(i).getDouble("lng"),
                            Integer.parseInt(jsonObject.getJSONArray("rows").getJSONObject(i).getString("point_km")),
                            Integer.parseInt(jsonObject.getJSONArray("rows").getJSONObject(i).getString("point_m")),
                            jsonObject.getJSONArray("rows").getJSONObject(i).getString("ruang_jalan"),
                            jsonObject.getJSONArray("rows").getJSONObject(i).getString("pemanfaatan"),
                            0
                    );
                    ContentValues cv = new ContentValues();
                    cv.put("id", ruang.getId());
                    cv.put("ruas_id", ruang.getRuas_id());
                    cv.put("ruang_jalan_id", ruang.getRuang_jalan_id());
                    cv.put("pemanfaatan_id", ruang.getPemanfaatan_id());
                    cv.put("date_surveyed", ruang.getDate_surveyed());
                    cv.put("lat", ruang.getLat());
                    cv.put("lng", ruang.getLng());
                    cv.put("point_km", ruang.getPoint_km());
                    cv.put("point_m", ruang.getPoint_m());
                    cv.put("ruang_jalan", ruang.getRuang_jalan());
                    cv.put("pemanfaatan", ruang.getPemanfaatan());
                    cv.put("status", ruang.getStatus());
                    db.insert(TABLE_PEMANFAATAN_RUANG, null, cv);
                }
            }
        }

        cursor.close();
        db.close();
    }

    public List<Pemanfaatan_Ruang> getTabelPemanfaatanRuang(int ruas_id) {
        List<Pemanfaatan_Ruang> ruangList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PEMANFAATAN_RUANG + " WHERE status != 3";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Pemanfaatan_Ruang ruang = new Pemanfaatan_Ruang();
                ruang.setId(cursor.getInt(cursor.getColumnIndex("id")));
                ruang.setRuas_id(cursor.getInt(cursor.getColumnIndex("ruas_id")));
                ruang.setRuang_jalan_id(cursor.getInt(cursor.getColumnIndex("ruang_jalan_id")));
                ruang.setPemanfaatan_id(cursor.getInt(cursor.getColumnIndex("pemanfaatan_id")));
                ruang.setDate_surveyed(cursor.getString(cursor.getColumnIndex("date_surveyed")));
                ruang.setLat(cursor.getDouble(cursor.getColumnIndex("lat")));
                ruang.setLng(cursor.getDouble(cursor.getColumnIndex("lng")));
                ruang.setPoint_km(cursor.getInt(cursor.getColumnIndex("point_km")));
                ruang.setPoint_m(cursor.getInt(cursor.getColumnIndex("point_m")));
                ruang.setRuang_jalan(cursor.getString(cursor.getColumnIndex("ruang_jalan")));
                ruang.setPemanfaatan(cursor.getString(cursor.getColumnIndex("pemanfaatan")));
                ruang.setStatus(cursor.getInt(cursor.getColumnIndex("status")));

                ruangList.add(ruang);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return ruangList;
    }

    public boolean tambahPemanfaatanRuang(int ruas_id, int ruang_jalan_id, int pemanfaatan_id, String date, double lat, double lng, int km, int m, String ruang_jalan, String pemanfaatan, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ruas_id", ruas_id);
        contentValues.put("ruang_jalan_id", ruang_jalan_id);
        contentValues.put("pemanfaatan_id", pemanfaatan_id);
        contentValues.put("lat", lat);
        contentValues.put("lng", lng);
        contentValues.put("point_km", km);
        contentValues.put("point_m", m);
        contentValues.put("date_surveyed", date);
        contentValues.put("ruang_jalan", ruang_jalan);
        contentValues.put("pemanfaatan", pemanfaatan);
        contentValues.put("status", status);

        Log.d(TAG, "addData: Adding " + ruas_id + " to " + TABLE_PEMANFAATAN_RUANG);

        long result = db.insert(TABLE_PEMANFAATAN_RUANG, null, contentValues);

        db.close();
        return result != -1;
    }

    public int getUserId(String user) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_DATA + " WHERE username = ?", new String[]{user + ""});
        if (cur != null) {
            cur.moveToLast();
            db.close();
            return cur.getInt(0);
        } else {
            db.close();
            return -1;
        }
    }

    public String getNamaUser(String user) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_DATA + " WHERE username = ?", new String[]{user + ""});
        if (cur != null) {
            cur.moveToLast();
            db.close();
            return cur.getString(8);
        } else {
            db.close();
            return "Tidak ada nama";
        }
    }

    public int getBagianJalanId(String namaBagianJalan) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_BAGIAN_JALAN + " WHERE nama = ?", new String[]{namaBagianJalan + ""});
        if (cur != null) {
            cur.moveToLast();
            db.close();
            return cur.getInt(0);
        } else {
            db.close();
            return 0;
        }
    }

    public String getNamaBagianJalan(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_BAGIAN_JALAN + " WHERE id = ?", new String[]{id + ""});
        if (cur != null) {
            cur.moveToLast();
            db.close();
            return cur.getString(1);
        } else {
            db.close();
            return "Kosong";
        }
    }

    public String getNamaKerusakan(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_KERUSAKAN + " WHERE id = ?", new String[]{id + ""});
        if (cur != null) {
            cur.moveToLast();
            db.close();
            return cur.getString(1);
        } else {
            db.close();
            return "Kosong";
        }
    }

    public String getNamaRuas(int ruas){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_RUAS + " WHERE id = ?", new String[]{ruas + ""});
        if (cur != null) {
            cur.moveToLast();
            db.close();
            return cur.getString(12);
        } else {
            db.close();
            return "Kosong";
        }
    }

    public String getNamaRuangJalan(int ruang_jalan_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_RUANG_JALAN + " WHERE id = ?", new String[]{ruang_jalan_id + ""});
        if (cur != null) {
            cur.moveToLast();
            db.close();
            return cur.getString(1);
        } else {
            db.close();
            return "Kosong";
        }
    }

    public String getNamaPemanfaatan(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_PEMANFAATAN + " WHERE id = ?", new String[]{id + ""});
        if (cur != null) {
            cur.moveToLast();
            db.close();
            return cur.getString(1);
        } else {
            db.close();
            return "Kosong";
        }
    }

    public List<Bagian_Jalan> getAllNamaBagianJalan() {
        List<Bagian_Jalan> bagian_jalanList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_BAGIAN_JALAN;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Bagian_Jalan bagian_jalan = new Bagian_Jalan();
                bagian_jalan.setNama(cursor.getString(cursor.getColumnIndex("nama")));

                bagian_jalanList.add(bagian_jalan);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return bagian_jalanList;
    }

    public int getKerusakanId(String namaKerusakan) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_KERUSAKAN + " WHERE nama = ?", new String[]{namaKerusakan + ""});
        if (cur != null) {
            cur.moveToLast();
            db.close();
            return cur.getInt(0);
        } else {
            db.close();
            return 0;
        }
    }

    public List<Kerusakan> getAllNamaKerusakan() {
        List<Kerusakan> kerusakanList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_KERUSAKAN;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery + " ORDER BY nama", null);

        if (cursor.moveToFirst()) {
            do {
                Kerusakan kerusakan = new Kerusakan();
                kerusakan.setNama(cursor.getString(cursor.getColumnIndex("nama")));

                kerusakanList.add(kerusakan);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return kerusakanList;
    }

    public List<Pemanfaatan> getAllNamaPemanfaatan() {
        List<Pemanfaatan> pemanfaatans = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_PEMANFAATAN;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Pemanfaatan pemanfaatan = new Pemanfaatan();
                pemanfaatan.setNama(cursor.getString(cursor.getColumnIndex("nama")));

                pemanfaatans.add(pemanfaatan);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return pemanfaatans;
    }

    public int getPemanfaatanId(String nama) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_PEMANFAATAN + " WHERE nama = ?", new String[]{nama + ""});
        if (cur != null) {
            cur.moveToLast();
            db.close();
            return cur.getInt(0);
        } else {
            db.close();
            return 0;
        }
    }

    public List<Ruang_Jalan> getAllNamaRuangJalan() {
        List<Ruang_Jalan> ruang_jalans = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_RUANG_JALAN;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Ruang_Jalan ruang_jalan = new Ruang_Jalan();
                ruang_jalan.setNama(cursor.getString(cursor.getColumnIndex("nama")));

                ruang_jalans.add(ruang_jalan);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ruang_jalans;
    }

    public int getRuangJalanId(String ruangJalan) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_RUANG_JALAN + " WHERE nama = ?", new String[]{ruangJalan + ""});
        if (cur != null) {
            cur.moveToLast();
            db.close();
            return cur.getInt(0);
        } else {
            db.close();
            return 0;
        }
    }

    public int getSession(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM t_data ", null);
        if (cur != null) {
            cur.moveToLast();
            db.close();
            return cur.getInt(9);
        } else {
            db.close();
            return 0;
        }
    }

    public boolean checkExisting(String tableName, String columnName, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + columnName + "='" + value + "'", null);
        if (c.getCount() > 0) {
            c.close();
            return true;
        } else {
            c.close();
            return false;
        }
    }

    public void deleteRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.execSQL("DROP TABLE " + TABLE_USER);
        /*db.execSQL("DROP TABLE  " + TABLE_DATA);
        db.execSQL("DROP TABLE  " + TABLE_MODULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BAGIAN_JALAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KERUSAKAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUANG_JALAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEMANFAATAN);*/
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VERSION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUAS_COORDINATES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUSAK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUSAK_PHOTOS);

        onCreate(db);
        db.close();
    }

    public void logout() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE t_data");
        //db.execSQL("CREATE TABLE t_data(id varchar(255) NOT NULL, v_password varchar(50), v_name varchar(50),  v_storeid varchar(255), v_email varchar(255), v_roleid varchar(255), b_isactive tinyint(1), v_retailid varchar(255), t_lastupdate, PRIMARY KEY (v_userid))");
    }

    public class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        @Override
        public void onPostExecute(String result) {
        }
    }

}
