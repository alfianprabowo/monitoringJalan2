package com.example.alfia.monitoringjalan;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alfia.monitoringjalan.Data.Data;
import com.example.alfia.monitoringjalan.Database.DBHandler;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    EditText usernameLogin, passwordLogin;
    SessionManager session;
    DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DBHandler(this);
        session = new SessionManager(this);

        usernameLogin = findViewById(R.id.usernameLogin);
        passwordLogin = findViewById(R.id.passwordLogin);

        final Button loginButton = findViewById(R.id.button_Login);
        if(isOnline()){
            loginButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Context context = view.getContext();
                    final String username = usernameLogin.getText().toString();
                    final String password = passwordLogin.getText().toString();
                    boolean success = false;
                    if (username.equals("") && username != null){
                        usernameLogin.requestFocus();
                        Toast.makeText(getApplicationContext(), "Silakan isi username", Toast.LENGTH_SHORT).show();
                    } else if (password.equals("") && password != null){
                        passwordLogin.requestFocus();
                        Toast.makeText(getApplicationContext(), "Silakan isi password", Toast.LENGTH_SHORT).show();
                    } else if(username.length() != 0 && password.length() != 0 ){
                        Log.d("login ", "::::::::::::::::::::::::::: username  " + username + " password " + password);
                        try{
                            success = db.login(username,password);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d("db login ", "::::::::::::::::::::::::::: login  " + success);
                        if(success){
                            session.clearData();
                            int mobileSession = db.getSession(username);
                            Toast.makeText(context, "LOGIN SUCCESS.", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(LoginActivity.this, Daftar_Ruas.class);
                            session.createLoginSession(username, password, mobileSession);
                            Bundle mBundle = new Bundle();
                            mBundle.putString("username", username);
                            i.putExtras(mBundle);
                            startActivity(i);
                        } else{
                            Toast.makeText(context, "LOGIN Gagal.", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Silakan isi username dan password", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }else {
            Toast.makeText(getApplicationContext() , "TIdak ada Koneksi Internet!", Toast.LENGTH_SHORT).show();
        }


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
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}

