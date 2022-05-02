package com.importmag.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.importmag.MainActivity;
import com.importmag.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ResetContrasena extends AppCompatActivity {
    Button cerrar, enviar;
    EditText correo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_contrasena);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        DisplayMetrics medidasVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVentana);
        int ancho = medidasVentana.widthPixels;
        int alto = medidasVentana.heightPixels;
        getWindow().setLayout((int) (ancho * 0.85), (int) (alto * 0.5));

        cerrar = findViewById(R.id.btnCancelarRP);
        enviar = findViewById(R.id.btnResetPas);
        correo=findViewById(R.id.txtStringCorreoR);

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeteclado();
                finish();
            }
        });

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=correo.getText().toString();
                try {
                    final String url = "https://import-mag.com/rest/resetpasswordbyemail";
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("email", email);

                    final com.android.volley.Response.Listener<JSONObject> responseListener = new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                String codigo = response.getString("code");

                                if (codigo.equals("200")) {
                                    System.out.println(response.toString());
                                    System.out.println(email);
                                    String respuesta = response.getString("psdata");
                                    /*Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), respuesta, Snackbar.LENGTH_LONG)
                                            .setAction("Action", null);
                                    View sbView = snackbar.getView();
                                    sbView.setBackgroundColor(ContextCompat.getColor(ResetContrasena.this, R.color.mensajeinfo));
                                    snackbar.show();*/
                                    closeteclado();
                                    if(respuesta.equals("exitosamente")){
                                        toast_ok("Se ha mandado el enlace de restablecimiento de contraseña a"+email);
                                    }else{
                                        toast_ok(respuesta);
                                    }



                                } else {

                                    String mensajeerr = response.getString("psdata");
                                    Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), mensajeerr, Snackbar.LENGTH_LONG)
                                            .setAction("Action", null);
                                    View sbView = snackbar.getView();
                                    sbView.setBackgroundColor(ContextCompat.getColor(ResetContrasena.this, R.color.mensaerror));
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
                            Boolean conected = MainActivity.isOnlineNet(getApplicationContext());
                            if (conected == false) {
                                Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), "Revisa tu conexión a Internet", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null);
                                View sbView = snackbar.getView();
                                sbView.setBackgroundColor(ContextCompat.getColor(ResetContrasena.this, R.color.mensajeinfo));
                                snackbar.show();
                            } else {
                                Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), "Error de conexión con el servidor", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null);
                                View sbView = snackbar.getView();
                                sbView.setBackgroundColor(ContextCompat.getColor(ResetContrasena.this, R.color.mensajeinfo));
                                snackbar.show();
                            }
                        }
                    };

                    JsonObjectRequest request2 = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonBody,
                            responseListener, errorListener) {


                    };

                    Volley.newRequestQueue(ResetContrasena.this).add(request2);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void closeteclado() {
        View view=this.getCurrentFocus();
        if(view!=null){
            InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    public void toast_ok(String msg) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.toast_personalizado_info, (ViewGroup) findViewById(R.id.toastinfoid));
        TextView txtmensaje = view.findViewById(R.id.txtMensajeInfo);
        txtmensaje.setText(msg);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

}