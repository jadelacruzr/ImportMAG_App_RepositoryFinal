package com.importmag.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.importmag.MainActivity;
import com.importmag.PersistentCookieStore;
import com.importmag.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class LoginValidateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //cookies store
        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(LoginValidateActivity.this), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);

        String email = getFromSharedPreferences(LoginValidateActivity.this, "email");
        String passwd = getFromSharedPreferences(LoginValidateActivity.this, "psswd");
        Boolean conected = MainActivity.isOnlineNet(getApplicationContext());
        if (conected == true) {
            Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), "Autenticando, un momento por favor!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(LoginValidateActivity.this, R.color.mensajeinfo));
            snackbar.show();
            consumoApiLogin(email, passwd, LoginValidateActivity.this);
        } else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("Sin conexión a Internet");
            builder1.setMessage("Verifique que está conectado a una red de Internet y vuelva a intentarlo");
            builder1.setCancelable(true);
            builder1.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    finish();
                }
            });
            AlertDialog alert11 = builder1.create();
            alert11.show();

        }
    }

    public static String getFromSharedPreferences(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences("logvalidate", Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }

    public void consumoApiLogin(String eml, String cont, Context context) {
        try {
            final String url = "https://import-mag.com/rest/login";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", eml);
            jsonBody.put("password", cont);

            final com.android.volley.Response.Listener<JSONObject> responseListener = new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        String codigo = response.getString("code");


                        if (codigo.equals("200")) {
                            Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String mensajeerr = response.getString("psdata");
                            Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), mensajeerr, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundColor(ContextCompat.getColor(LoginValidateActivity.this, R.color.mensaerror));
                            snackbar.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(LoginValidateActivity.this, LoginActivity.class));
                                    finish();
                                }
                            }, 1500);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            final com.android.volley.Response.ErrorListener errorListener = new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), "Error de conexión con el servidor", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(LoginValidateActivity.this, R.color.mensaerror));
                    snackbar.show();

                }
            };
            JsonObjectRequest request2 = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonBody,
                    responseListener, errorListener) {
            };
            Volley.newRequestQueue(context).add(request2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}