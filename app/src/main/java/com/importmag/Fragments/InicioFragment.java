package com.importmag.Fragments;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.importmag.Adapters.CategoriasAdapterInicio;
import com.importmag.Adapters.ProductosDestacadosAdapter;
import com.importmag.Interfaces.GetServiceCategorias;
import com.importmag.Interfaces.GetServiceProdFavs;
import com.importmag.Models.Categoria;
import com.importmag.Models.ProdsFavs;
import com.importmag.Models.Productos;
import com.importmag.R;
import com.importmag.databinding.FragmentInicioBinding;

import java.util.ArrayList;
import java.util.List;

//IMPORTACIONES DE SLIDER
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.importmag.Interfaces.GetServiceSlider;
import com.importmag.Models.Slider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InicioFragment extends Fragment {

    private FragmentInicioBinding binding;

    //VARIABLES DEL RECYCLER VIEW
    RecyclerView recyclerViewProds;

    //RECYCLER PARA PRODCUTOS DESTACADOS
    ProductosDestacadosAdapter productosDestacadosAdapter;
    List<Productos> featured_products;
    //VARIABLE DEL SLIDER
    ImageSlider slider;
    LinearLayout ll_home;
    ImageView img;
    //RECYCLER PARA CATEGORIAS
    CategoriasAdapterInicio CatAdapter;
    List<Categoria> listCategoria = new ArrayList<>();
    RecyclerView recyclerViewCat;
    final ArrayList<ProdsFavs> listafavoritoscli = new ArrayList();


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            binding = FragmentInicioBinding.inflate(inflater, container, false);
            View view = binding.getRoot();
            if (isOnlineNet() == true) {
                //IMPLEMENTACIÓN DEL SLIDER
                slider = binding.imageSlider;
                SliderView(slider);

                ////IMPLEMENTACIÓN CARRUSEL DE PRODUCTOS
                recyclerViewCat = binding.recyclerCategorias;
                setCategoriasRecycler(recyclerViewCat);

                //IMPLEMENTACIÓN CARRUSEL PRODUCTOS DESTACADOS
                recyclerViewProds = binding.recyclerProdDestacados;
                setProductosDestacadosRecycler(recyclerViewProds);
            } else {
                Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Revisa tu conexión a Internet", Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.mensajeinfo));
                snackbar.show();


            }
            ll_home = binding.llHome;
            img = binding.imgCargando2;
            ll_home.setVisibility(View.INVISIBLE);
            return view;

        } catch (Exception e) {
            Log.e(TAG, "onCreateView", e);
            throw e;
        }

    }

    /**
     * Método que genera un View Slider
     *
     * @param slider
     */
    private void SliderView(ImageSlider slider) {
        //ARRAY QUE ALMACENARÁ LAS IMÁGENES DEL SLIDER

        ArrayList<SlideModel> remoteimages = new ArrayList();
        //CONSUMO DE LA API SLIDER//
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.import-mag.com/getSlider/revSlider.php/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetServiceSlider getServiceSlider = retrofit.create(GetServiceSlider.class);
        Call<List<Slider>> call = getServiceSlider.find();
        call.enqueue(new Callback<List<Slider>>() {

            @Override
            public void onResponse(Call<List<Slider>> call, retrofit2.Response<List<Slider>> response) {
                List<Slider> sliderList = response.body();
                //Recorrido de los datos extraidos de la api e inserción en el View Slider
                for (Slider s : sliderList) {
                    remoteimages.add(new SlideModel("https://www.import-mag.com/modules/ps_imageslider/images/"
                            + s.getImage(), s.getLegend(), ScaleTypes.FIT));
                }
                slider.setImageList(remoteimages);
                slider.startSliding(1500);
            }

            @Override
            public void onFailure(Call<List<Slider>> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Error de conexión con el servidor", Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.mensajeinfo));
                snackbar.show();

            }
        });
    }


    /**
     * Método que genera un carrusel de Productos Destacados
     */
    private void setProductosDestacadosRecycler(RecyclerView recyclerViewcprodDestacados) {
        String url = "https://import-mag.com/rest/featuredproducts";

        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jasonArray = jsonObject.getJSONArray("psdata"); //encabezado de WS
                    int tam = jasonArray.length();

                    featured_products = new ArrayList<>();
                    for (int i = 0; i < tam; i++) {
                        Boolean like = false;
                        JSONObject psdata = jasonArray.getJSONObject(i);
                        Integer id_product = psdata.getInt("id_product");
                        String name = psdata.getString("name");
                        JSONObject imgs = psdata.getJSONObject("default_image");
                        String url_image = imgs.getString("url");
                        System.out.println("entrando alfor");
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
                        featured_products.add(new Productos(id_product, name, url_image,like));

                    }

                    productosDestacadosAdapter = new ProductosDestacadosAdapter(getActivity(), featured_products);
                    recyclerViewcprodDestacados.setAdapter(productosDestacadosAdapter);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                            LinearLayoutManager.HORIZONTAL, false);
                    recyclerViewcprodDestacados.setLayoutManager(layoutManager);
                    ll_home.setVisibility(View.VISIBLE);
                    img.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Error de conexión con el servidor", Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.mensajeinfo));
                snackbar.show();
            }
        };

        StringRequest request2 = new StringRequest(Request.Method.GET, url,
                responseListener, errorListener) {
        };
        Volley.newRequestQueue(getActivity()).add(request2);

    }

    /**
     * MÉTODO QUE GENERA UN RECYCLERVIEW DE CATEGORIAS
     */
    private void setCategoriasRecycler(RecyclerView recyclerViewcategorias) {
        consProdsFav();
        //CONSUMO DE LA API CATEGORIAS//
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://import-mag.com/getSlider/cat.php/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetServiceCategorias getServiceCategorias = retrofit.create(GetServiceCategorias.class);
        Call<List<Categoria>> call = getServiceCategorias.find();

        if (isOnlineNet() == true) {
            call.enqueue(new Callback<List<Categoria>>() {
                @Override
                public void onResponse(Call<List<Categoria>> call, retrofit2.Response<List<Categoria>> response) {
                    List<Categoria> categoriaList = response.body();

                    for (Categoria s : categoriaList) {
                        String link_r = s.getLink_rewwrite();
                        String descr = s.getName().toString();
                        String nuevapalabra = descr;
                        if (descr.contains("Ã¡") || descr.contains("Ã©") || descr.contains("Ã\u00AD") || descr.contains("Ã³") || descr.contains("Ãº")) {
                            nuevapalabra = descr.replaceAll("Ã¡", "á").replaceAll("Ã©", "é").replaceAll
                                    ("Ã\u00AD", "í").replaceAll("Ã³", "ó").replaceAll
                                    ("Ãº", "ú");
                        }
                        listCategoria.add(new Categoria(s.getId_category(), nuevapalabra, link_r));
                    }
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                            LinearLayoutManager.HORIZONTAL, false);
                    recyclerViewcategorias.setLayoutManager(layoutManager);
                    CatAdapter = new CategoriasAdapterInicio(listCategoria, getActivity());
                    recyclerViewcategorias.setAdapter(CatAdapter);
                }

                @Override
                public void onFailure(Call<List<Categoria>> call, Throwable t) {
                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Error de conexión con el servidor", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.mensajeinfo));
                    snackbar.show();
                }
            });
        } else {
            Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Revisa tu conexión a Internet", Snackbar.LENGTH_LONG)
                    .setAction("Action", null);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.mensajeinfo));
            snackbar.show();
        }
    }

    /**
     * MÉTODO QUE VERIFICA CONECTIVIDAD DEL DISPOSITIVO
     */
    public Boolean isOnlineNet() {

        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public void consProdsFav() {
        String custid = getFromSharedPreferences(getContext(), "cust_id");
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
                Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Error de conexión con el servidor", Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.mensajeinfo));
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