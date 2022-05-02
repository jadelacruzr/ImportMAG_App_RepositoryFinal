package com.importmag.Activities;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.importmag.Adapters.AggLikeAdapter;
import com.importmag.Models.Favoritos;
import com.importmag.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VentanaAgregarLike extends AppCompatActivity {

    TextView txtid_product, none;
    Button btnagglike;
    ImageView btnCancelar;
    AggLikeAdapter adapter;

    //LISTA DE FAVORITOS
    ArrayList<Favoritos> favsList = new ArrayList();
    AggLikeAdapter favsAdapter;
    RecyclerView recyclerViewcFavsLikes;
    ExtendedFloatingActionButton agglist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_agregar_like);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        DisplayMetrics medidasVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVentana);
        int ancho = medidasVentana.widthPixels;
        int alto = medidasVentana.heightPixels;
        getWindow().setLayout((int) (ancho * 0.7), (int) (alto * 0.50));
        Intent i = getIntent();
        Integer id_product = i.getIntExtra("id_productoo", 0);

        adapter = new AggLikeAdapter(getApplicationContext());
        adapter.enviarDatos(id_product);

        //Inizializando valores del layout

        recyclerViewcFavsLikes = findViewById(R.id.recycler_add_like);
        agglist = findViewById(R.id.btn_Agg_favorito);
        agglist.setVisibility(View.INVISIBLE);
        none = findViewById(R.id.txtnoneFavoritoslistlike);
        none.setVisibility(View.INVISIBLE);
        consumir(recyclerViewcFavsLikes);

        agglist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(VentanaAgregarLike.this, VentanaNuevaListaActivity.class);
                startActivity(i);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            }
        });
        btnCancelar = findViewById(R.id.btn_cerrar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void consumir(RecyclerView recycler) {
        final String url = "https://import-mag.com/rest/wishlist?action=list";

        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jasonArray = jsonObject.getJSONArray("psdata"); //encabezado de WS

                    int tam = jasonArray.length();

                    favsList = new ArrayList<>();
                    for (int i = 0; i < tam; i++) {
                        JSONObject psdata = jasonArray.getJSONObject(i);
                        String id_wish = psdata.getString("id_wishlist");
                        String name = psdata.getString("name");
                        /*if (name.contains("My wishlist")) {
                            String nuevoName = name.replaceAll("My wishlist", "Mis favoritos");
                            name = nuevoName;
                        }*/
                        String cantList = psdata.getString("nbProducts");

                        favsList.add(new Favoritos(id_wish, name, cantList));
                    }

                    favsAdapter = new AggLikeAdapter(VentanaAgregarLike.this, favsList);
                    recycler.setAdapter(favsAdapter);
                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(VentanaAgregarLike.this, 1);
                    recycler.setLayoutManager(layoutManager);
                    none.setVisibility(View.INVISIBLE);
                    agglist.setVisibility(View.VISIBLE);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                recyclerViewcFavsLikes.setVisibility(View.INVISIBLE);
                none.setVisibility(View.VISIBLE);
                agglist.setVisibility(View.VISIBLE);


            }
        };

        StringRequest request2 = new StringRequest(Request.Method.GET, url,
                responseListener, errorListener) {
        };
        Volley.newRequestQueue(VentanaAgregarLike.this).add(request2);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            consumir(recyclerViewcFavsLikes);
            recyclerViewcFavsLikes.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.e(TAG, "onCreateView", e);
            throw e;
        }

    }
}