package com.importmag.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.importmag.R;

import org.json.JSONException;
import org.json.JSONObject;

public class VentanaNuevaListaActivity extends AppCompatActivity {
    EditText editName;
    Button cancelar, crearListFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_nueva_lista);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        DisplayMetrics medidasVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVentana);
        int ancho = medidasVentana.widthPixels;
        int alto = medidasVentana.heightPixels;
        getWindow().setLayout((int) (ancho * 0.85), (int) (alto * 0.4));


        editName = findViewById(R.id.edtxtCrearListFav);

        cancelar = findViewById(R.id.btnCancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        crearListFav=findViewById(R.id.btnCrearListNew);
        crearListFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name=editName.getText().toString();
                final String url = "https://import-mag.com/rest/wishlist?action=createWishlist&name="+name;

                final Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String mensaje =jsonObject.getString("message");
                            String code =jsonObject.getString("code");
                            if (code.equals("200")){

                                Snackbar snackbar = Snackbar.make(view, mensaje, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null);
                                View sbView = snackbar.getView();
                                sbView.setBackgroundColor(ContextCompat.getColor(VentanaNuevaListaActivity.this, R.color.mensajeok));
                                snackbar.show();
                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                                finish();

                            }else{
                                Snackbar snackbar = Snackbar.make(view, mensaje, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null);
                                View sbView = snackbar.getView();
                                sbView.setBackgroundColor(ContextCompat.getColor(VentanaNuevaListaActivity.this, R.color.mensaerror));
                                snackbar.show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                final Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar snackbar = Snackbar.make(view, " ! Error de conexión ", Snackbar.LENGTH_LONG)
                                .setAction("Action", null);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(VentanaNuevaListaActivity.this, R.color.mensajeinfo));
                        snackbar.show();
                        finish();
                    }
                };

                StringRequest request2 = new StringRequest(Request.Method.GET, url,
                        responseListener,errorListener) {
                };
                Volley.newRequestQueue(VentanaNuevaListaActivity.this).add(request2);
            }
        });

    }

}