package com.importmag.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.importmag.R;

import org.json.JSONException;
import org.json.JSONObject;

public class EditarPerfilActivity extends AppCompatActivity {


    EditText contractual, nuevacontra, confcontra;
    EditText nombre, apellidos, correo;
    Button btnCambiar;
    ImageView btncancelar;
    RadioGroup radiogroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        nombre = findViewById(R.id.nameText);
        apellidos = findViewById(R.id.nickText);
        correo = findViewById(R.id.emailText);

        String passwd = getFromSharedPreferences(EditarPerfilActivity.this, "psswd");
        String email = getFromSharedPreferences(EditarPerfilActivity.this, "email");
        String firstn = getFromSharedPreferences(EditarPerfilActivity.this, "first_name");
        String lastn = getFromSharedPreferences(EditarPerfilActivity.this, "last_name");
        String genero = getFromSharedPreferences(EditarPerfilActivity.this, "gender");

        nombre.setText(firstn);
        apellidos.setText(lastn);
        correo.setText(email);

        btncancelar = findViewById(R.id.btnCerrarEdperf);
        btnCambiar = findViewById(R.id.btnRenombrar);
        radiogroup = findViewById(R.id.rdGrupoEd);

        if (genero.equals("1")) {
            RadioButton opcion1 = (RadioButton) radiogroup.getChildAt(0);
            opcion1.setChecked(true);

        } else if (genero.equals("2")) {

            RadioButton opcion2 = (RadioButton) radiogroup.getChildAt(1);
            opcion2.setChecked(true);
        }


        btncancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnCambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                contractual = findViewById(R.id.txtContraActual);
                String validacontra = contractual.getText().toString();
                nuevacontra = findViewById(R.id.txtContra);
                String ncontra = nuevacontra.getText().toString();
                confcontra = findViewById(R.id.txtConfContra);
                String cncontra = confcontra.getText().toString();

                String nomb = nombre.getText().toString();
                String ape = apellidos.getText().toString();
                String corr = correo.getText().toString();

                RadioButton radioButton;
                String gender, genderE = "";
                int radioID = radiogroup.getCheckedRadioButtonId();
                radioButton = findViewById(radioID);
                gender = radioButton.getText().toString();
                if (gender.equals("Sr.")) {
                    genderE = "1";
                } else if (gender.equals("Sra./Srta.")) {
                    genderE = "2";
                }

                if (validacontra.equals(passwd)) {
                    if (ncontra.equals(cncontra)) {
                        if (!nomb.isEmpty() && !ape.isEmpty() && !corr.isEmpty()) {
                            cambContApi(corr, validacontra, nomb, ape, genderE, cncontra);
                        } else {
                            Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), "Llene todos los datos personales", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundColor(ContextCompat.getColor(EditarPerfilActivity.this, R.color.mensaerror));
                            snackbar.show();
                        }


                    } else {
                        Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), "Los campos de nueva contraseña no coinciden", Snackbar.LENGTH_LONG)
                                .setAction("Action", null);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(EditarPerfilActivity.this, R.color.mensaerror));
                        snackbar.show();
                    }


                } else {
                    Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), "Contraseña actual inválida", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(EditarPerfilActivity.this, R.color.mensaerror));
                    snackbar.show();
                }
            }
        });

    }

    public void cambContApi(String email_v, String contra_v, String nomb_v, String apell_v, String genero_v, String newp_v) {
        try {
            final String url = "https://import-mag.com/rest/accountedit";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email_v);
            jsonBody.put("password", contra_v);
            jsonBody.put("firstName", nomb_v);
            jsonBody.put("lastName", apell_v);
            jsonBody.put("gender", genero_v);
            jsonBody.put("new_password", newp_v);

            final com.android.volley.Response.Listener<JSONObject> responseListener = new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        String codigo = response.getString("code");

                        if (codigo.equals("200")) {

                            cerrandosesion();

                        } else {
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
                    sbView.setBackgroundColor(ContextCompat.getColor(EditarPerfilActivity.this, R.color.mensajeinfo));
                    snackbar.show();
                }
            };

            JsonObjectRequest request2 = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonBody,
                    responseListener, errorListener) {

            };

            Volley.newRequestQueue(EditarPerfilActivity.this).add(request2);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void cerrandosesion() {
        final String url = "https://import-mag.com/rest/logout";

        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        };

        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };

        StringRequest request2 = new StringRequest(Request.Method.GET, url,
                responseListener, errorListener) {
        };
        Volley.newRequestQueue(EditarPerfilActivity.this).add(request2);
        Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), "Datos modificados, vuelva a iniciar sesión", Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(EditarPerfilActivity.this, R.color.mensajeok));
        snackbar.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(EditarPerfilActivity.this, LoginActivity.class));
                finish();
            }
        }, 1500);

        SharedPreferences.Editor editor = getSharedPreferences("logvalidate", MODE_PRIVATE).edit();
        editor.clear().apply();
    }

    public static String getFromSharedPreferences(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences("logvalidate", Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }
}