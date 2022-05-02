package com.importmag.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.importmag.Adapters.ProductosFavoritosAdapter;
import com.importmag.Models.Productos;
import com.importmag.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FavoritoDetallesActivity extends AppCompatActivity {

    private RecyclerView rvProdsFavoritos;
    private ImageView cerrar3, edit, cargando, eliminar;
    private TextView nameFav, none;
    private String rempName, id_wish;
    private List<Productos> prodsEnconList;
    private ProductosFavoritosAdapter productosAdapter;
    private static final int CODIGO_ACTIVIDAD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorito_detalles);
        //IMPLEMENTACIÓN CARRUSEL PRODUCTOS EN DESCUENTO
        rvProdsFavoritos = findViewById(R.id.recyclerprodsFav);
        setProductosFavRecycler(rvProdsFavoritos);

        //ASIGNACION DE VARIABLES DEL LAYOUT
        cerrar3 = findViewById(R.id.btnCerrar);
        nameFav = findViewById(R.id.txtNombFavo);
        edit = findViewById(R.id.btnEditarFav);
        none = findViewById(R.id.txtNoneProdList);
        none.setVisibility(View.INVISIBLE);
        cargando = findViewById(R.id.img_cargandoFav);
        rvProdsFavoritos.setVisibility(View.INVISIBLE);
        edit.setVisibility(View.INVISIBLE);
        eliminar = findViewById(R.id.btnEliminarWish);
        eliminar.setVisibility(View.INVISIBLE);

        //BOTÓN QUE ABRE LA ACTIVIDAD EDITAR LISTA DE FAVORITOS
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FavoritoDetallesActivity.this, VentanaEditarListFavsActivity.class);
                i.putExtra("id_wishlist", id_wish);
                i.putExtra("namewish", nameFav.getText().toString());
                startActivityForResult(i, CODIGO_ACTIVIDAD);
            }
        });


        //BOTÓN QUE ACCEDE AL MÉTODO ELIMINAR LISTA DE FAVORITOS
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EliminarLisFav();
            }
        });


        //BOTÓN QUE CIERRA LA ACTIVIDAD
        cerrar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Método que genera un viewslider de Lista de favoritos
     */
    private void setProductosFavRecycler(RecyclerView recyclerViewProdFav) {
        Intent i = getIntent();
        id_wish = i.getStringExtra("id_wishlist");

        String url = "https://import-mag.com/rest/wishlist?action=viewWishlist&id_wishlist=" + id_wish;

        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject psdata = jsonObject.getJSONObject("psdata"); //encabezado de WS
                    String nameWish = psdata.getString("wishlistName");
                    /*if (nameWish.contains("My wishlist")) {
                        String nuevoName = nameWish.replaceAll("My wishlist", "Mis favoritos");
                        nameWish = nuevoName;
                    }*/
                    JSONArray nods = psdata.getJSONArray("products");
                    int tam = nods.length();
                    prodsEnconList = new ArrayList<>();
                    for (int i = 0; i < tam; i++) {
                        JSONObject aux = nods.getJSONObject(i);
                        Integer id_product = aux.getInt("id_product");
                        String name = aux.getString("name");
                        JSONObject imgs = aux.getJSONObject("default_image");
                        String url_image = imgs.getString("url");
                        prodsEnconList.add(new Productos(id_product, name, url_image, id_wish));
                    }
                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(FavoritoDetallesActivity.this, 2);
                    productosAdapter = new ProductosFavoritosAdapter(FavoritoDetallesActivity.this, prodsEnconList);
                    rvProdsFavoritos.setLayoutManager(layoutManager);
                    rempName = nameWish;
                    nameFav.setText(rempName);
                    rvProdsFavoritos.setAdapter(productosAdapter);
                    rvProdsFavoritos.setVisibility(View.VISIBLE);
                    cargando.setVisibility(View.INVISIBLE);
                    edit.setVisibility(View.VISIBLE);
                    eliminar.setVisibility(View.VISIBLE);
                    nameFav.setVisibility(View.VISIBLE);
                    edit.setVisibility(View.VISIBLE);
                    eliminar.setVisibility(View.VISIBLE);
                    /*if (rempName.contains("Mis favoritos")) {

                        edit.setVisibility(View.INVISIBLE);
                        eliminar.setVisibility(View.INVISIBLE);
                    }*/
                    if (tam == 0) {
                        rvProdsFavoritos.setVisibility(View.INVISIBLE);
                        none.setVisibility(View.VISIBLE);
                    }

                } catch (
                        JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), "Error de conexíon con el servidor", Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(FavoritoDetallesActivity.this, R.color.mensajeinfo));
                snackbar.show();


            }
        };

        StringRequest request2 = new StringRequest(Request.Method.GET, url,
                responseListener, errorListener) {
        };
        Volley.newRequestQueue(FavoritoDetallesActivity.this).

                add(request2);

    }

    /**
     * Método que elimina una lilsta de favoritos
     */

    public void EliminarLisFav() {

        AlertDialog.Builder alerta = new AlertDialog.Builder(FavoritoDetallesActivity.this);
        alerta.setMessage("¿Esta seguro de eliminar esta lista de favoritos?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String url = "https://import-mag.com/rest/wishlist?action=deleteWishlist&idWishList=" + id_wish;
                        final Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), "Lista eliminada correctamente", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null);
                                View sbView = snackbar.getView();
                                sbView.setBackgroundColor(ContextCompat.getColor(FavoritoDetallesActivity.this, R.color.mensajeok));
                                snackbar.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 1500);


                            }
                        };

                        final Response.ErrorListener errorListener = new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), "Error de conexión con el servidor", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null);
                                View sbView = snackbar.getView();
                                sbView.setBackgroundColor(ContextCompat.getColor(FavoritoDetallesActivity.this, R.color.mensajeinfo));
                                snackbar.show();


                            }
                        };

                        StringRequest request2 = new StringRequest(Request.Method.GET, url,
                                responseListener, errorListener) {
                        };
                        Volley.newRequestQueue(FavoritoDetallesActivity.this).add(request2);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog titulo = alerta.create();
        titulo.setTitle("Eliminar");
        titulo.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == CODIGO_ACTIVIDAD) && (resultCode == RESULT_OK)) {
            nameFav.setText(data.getDataString());
            Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), "Lista renombrada correctamente", Snackbar.LENGTH_LONG)
                    .setAction("Action", null);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(FavoritoDetallesActivity.this, R.color.mensajeok));
            snackbar.show();
        }
    }
}