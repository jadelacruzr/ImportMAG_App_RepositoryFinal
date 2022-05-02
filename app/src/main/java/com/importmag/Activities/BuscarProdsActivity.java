package com.importmag.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.importmag.Interfaces.GetServiceProdFavs;
import com.importmag.Models.ProdsFavs;
import com.importmag.Models.Productos;

import com.importmag.Adapters.ProductosAdapter;
import com.importmag.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BuscarProdsActivity extends AppCompatActivity{


    //VARIABLES DEL RECYCLERVIEW
    //VARIABLES DEL CARRUSEL PRODUCTOS
    private RecyclerView rvSearchProducts;
    private ProductosAdapter productosAdapter;
    static  List<Productos> prodsEnconList;
    private ImageView cerrar3, cargando2;
    private String strBusqueda;
    private TextView txtBusqueda,none;
    final ArrayList<ProdsFavs> listafavoritoscli = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_prods);

        //IMPLEMENTACIÓN CARRUSEL PRODUCTOS EN DESCUENTO
        rvSearchProducts = findViewById(R.id.recyclerBusProductos);

        setProductosRecycler(rvSearchProducts);
        cerrar3 = findViewById(R.id.salirBusqueda);
        txtBusqueda = findViewById(R.id.txtBusqueda);
        txtBusqueda.setText("Resultados para " + strBusqueda + ":");
        none=findViewById(R.id.txtningunresultado);
        none.setVisibility(View.INVISIBLE);
        cargando2 = findViewById(R.id.img_cargando2);
        rvSearchProducts.setVisibility(View.INVISIBLE);


        /**
         * Método que cierra la actividad de Registro
         */
        cerrar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }

    /**
     * Método que genera un carrusel de Prods Encontrados
     */
    private void setProductosRecycler(RecyclerView recyclerViewTodosProductos) {
        consProdsFav();
        Intent i = getIntent();
        String palabra;
        palabra= i.getStringExtra("stringBusqueda");
        strBusqueda=palabra;
        String url = "https://import-mag.com/rest/productSearch?s=" + strBusqueda + "&resultsPerPage=1000";

        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject psdata = jsonObject.getJSONObject("psdata"); //encabezado de WS
                    JSONArray nods=psdata.getJSONArray("products");
                    int tam = nods.length();
                    if(tam>0) {
                        prodsEnconList = new ArrayList<>();
                        for (int i = 0; i < tam; i++) {
                            Boolean like = false;
                            JSONObject aux = nods.getJSONObject(i);
                            Integer id_product = aux.getInt("id_product");
                            //String description = psdata.getString("description");
                            String name = aux.getString("name");
                            JSONObject imgs = aux.getJSONObject("default_image");
                            String url_image = imgs.getString("url");
                            List<ProdsFavs> favsCli = listafavoritoscli;

                            for (ProdsFavs favs : favsCli) {

                                String id_prodfavs = favs.getId_product();
                                String idprods = id_product.toString();
                                System.out.println("ID LIKEADOS: " + id_prodfavs + " ID PRODS: " + idprods);
                                if (id_prodfavs.equals(idprods)) {
                                    like = true;
                                } else if(like!=true) {
                                    like = false;
                                }
                            }
                            prodsEnconList.add(new Productos(id_product, name, url_image,like));
                        }

                        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(BuscarProdsActivity.this, 2);
                        productosAdapter = new ProductosAdapter(BuscarProdsActivity.this, prodsEnconList);
                        rvSearchProducts.setLayoutManager(layoutManager);
                        rvSearchProducts.setAdapter(productosAdapter);
                        rvSearchProducts.setVisibility(View.VISIBLE);
                        cargando2.setVisibility(View.INVISIBLE);
                    }else{
                        rvSearchProducts.setVisibility(View.INVISIBLE);
                        cargando2.setVisibility(View.INVISIBLE);
                        none.setVisibility(View.VISIBLE);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), "Revisa tu conexión a Internet", Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(BuscarProdsActivity.this, R.color.mensajeinfo));
                snackbar.show();
            }
        };

        StringRequest request2 = new StringRequest(Request.Method.GET, url,
                responseListener,errorListener) {
        };
        Volley.newRequestQueue(BuscarProdsActivity.this).add(request2);
    }
    public void consProdsFav() {
        String custid = getFromSharedPreferences(getApplicationContext(), "cust_id");
        //ARRAY QUE ALMACENARÁ LAS ID´S DE LOS PRODUCTOS LIKEADOS DEL USUARIO

        //CONSUMO DE LA API LIKES DEL CLIENTE//
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://import-mag.com/getSlider/favs_user.php/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetServiceProdFavs getServiceProdsfavs = retrofit.create(GetServiceProdFavs.class);
        Call<List<ProdsFavs>> call = getServiceProdsfavs.find(custid);
        call.enqueue(new Callback<List<ProdsFavs>>() {

            @Override
            public void onResponse(Call<List<ProdsFavs>> call, retrofit2.Response<List<ProdsFavs>> response) {
                List<ProdsFavs> listaresponse = response.body();
                //Recorrido de los datos extraidos de la api
                for (ProdsFavs s : listaresponse) {
                    //System.out.println(s.getId_product());
                    listafavoritoscli.add(new ProdsFavs(s.getId_product(), s.getId_wishlist()));
                }
            }

            @Override
            public void onFailure(Call<List<ProdsFavs>> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(BuscarProdsActivity.this.findViewById(android.R.id.content), "Error de conexión con el servidor", Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(BuscarProdsActivity.this, R.color.mensajeinfo));
                snackbar.show();

            }
        });
        System.out.println("lista del metodo: " + listafavoritoscli.toString());
    }
    public static String getFromSharedPreferences(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences("logvalidate", Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }

}
