package com.importmag.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.importmag.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class VentanaEditarListFavsActivity extends AppCompatActivity {
    EditText editName;
    Button cancelar, renombrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_editar_list_favs);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        DisplayMetrics medidasVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVentana);
        int ancho = medidasVentana.widthPixels;
        int alto = medidasVentana.heightPixels;
        getWindow().setLayout((int) (ancho * 0.85), (int) (alto * 0.4));

        Intent i = getIntent();
        String nameFavorito = i.getStringExtra("namewish");

        editName = findViewById(R.id.edittxtnomFav);
        editName.setText(nameFavorito);
        cancelar = findViewById(R.id.btnCancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        
        renombrar=findViewById(R.id.btnRenombrar);
        renombrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idWish = i.getStringExtra("id_wishlist");
                String name=editName.getText().toString();
                final String url = "https://import-mag.com/rest/wishlist?action=renameWishlist&idWishList="+idWish+"&name="+name;

                final Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String mensaje =jsonObject.getString("message");
                            String code =jsonObject.getString("code");
                            if (code.equals("200")){


                               Intent data = new Intent();
                                data.setData(Uri.parse(name));
                                setResult(RESULT_OK, data);
                                finish();

                            }else{
                                Snackbar snackbar = Snackbar.make(view, mensaje, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null);
                                View sbView = snackbar.getView();
                                sbView.setBackgroundColor(ContextCompat.getColor(VentanaEditarListFavsActivity.this, R.color.mensaerror));
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

                        Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), "Error de conexión con el servidor", Snackbar.LENGTH_LONG)
                                .setAction("Action", null);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(VentanaEditarListFavsActivity.this, R.color.mensajeinfo));
                        snackbar.show();
                    }
                };

                StringRequest request2 = new StringRequest(Request.Method.GET, url,
                        responseListener,errorListener) {
                };
                Volley.newRequestQueue(VentanaEditarListFavsActivity.this).add(request2);
            }
        });
     
    }


}