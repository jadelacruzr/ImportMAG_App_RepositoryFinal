package com.importmag.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;


import com.android.volley.VolleyError;


import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.Volley;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.importmag.MainActivity;
import com.importmag.PersistentCookieStore;
import com.importmag.R;


import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    ExtendedFloatingActionButton btnLogin;
    EditText emailLogText, passwordLogText;
    TextView olvidarContraseña, btnRegistrar;


    private static final int CODIGO_ACTIVIDAD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //cookies store
        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(LoginActivity.this), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnIniciarSesion);
        btnRegistrar = findViewById(R.id.enlaceRegistro);

        emailLogText = findViewById(R.id.emailLogText);
        passwordLogText = findViewById(R.id.passwordLogText);



        olvidarContraseña = findViewById(R.id.olvidarContraseñaView);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailTxt = emailLogText.getText().toString();
                String passTxt = passwordLogText.getText().toString();

                if (!emailTxt.isEmpty() && !passTxt.isEmpty()) {
                    try {
                        final String url = "https://import-mag.com/rest/login";
                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("email", emailTxt);
                        jsonBody.put("password", passTxt);

                        final com.android.volley.Response.Listener<JSONObject> responseListener = new com.android.volley.Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    String codigo = response.getString("code");

                                    if (codigo.equals("200")) {
                                        JSONObject psdata = response.getJSONObject("psdata");
                                        String mensaje = psdata.getString("message");
                                        String cust_id = psdata.getString("customer_id");
                                        JSONObject user = psdata.getJSONObject("user");
                                        String lastname = user.getString("lastname");
                                        String firstname = user.getString("firstname");
                                        String gendern = user.getString("id_gender");


                                        Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), "Ingresando..", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null);
                                        View sbView = snackbar.getView();
                                        sbView.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.mensajeok));
                                        snackbar.show();
                                        saveLoginSharedPreference(emailTxt,passTxt,cust_id,lastname,firstname,gendern);
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        String mensajeerr = response.getString("psdata");
                                        Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), mensajeerr, Snackbar.LENGTH_LONG)
                                                .setAction("Action", null);
                                        View sbView = snackbar.getView();
                                        sbView.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.mensaerror));
                                        snackbar.show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        final com.android.volley.Response.ErrorListener errorListener = new com.android.volley.Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Boolean conected= MainActivity.isOnlineNet(getApplicationContext());
                                if(conected==false){
                                    Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), "Revisa tu conexión a Internet", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null);
                                    View sbView = snackbar.getView();
                                    sbView.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.mensajeinfo));
                                    snackbar.show();
                                }else {
                                    Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), "Error de conexión con el servidor", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null);
                                    View sbView = snackbar.getView();
                                    sbView.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.mensajeinfo));
                                    snackbar.show();
                                }
                            }
                        };

                        JsonObjectRequest request2 = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonBody,
                                responseListener, errorListener) {


                        };


                        Volley.newRequestQueue(LoginActivity.this).add(request2);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), "LLene todos los campos", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.mensaerror));
                    snackbar.show();


                }

            }
        });


        /**
         * Método que cierra la actividad de Registro
         */


        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrarseActivity.class));

            }
        });

        olvidarContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, ResetContrasena.class));
            }
        });

    }

    private void saveLoginSharedPreference(String emailTxt, String passTxt,String cus_id,String last_n,String first_n,String gender) {

        SharedPreferences sharedPref = getSharedPreferences("logvalidate",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("email",emailTxt);
        editor.putString("psswd",passTxt);
        editor.putString("cust_id",cus_id);
        editor.putString("last_name",last_n);
        editor.putString("first_name",first_n);
        editor.putString("gender",gender);
        editor.apply();
    }


}
