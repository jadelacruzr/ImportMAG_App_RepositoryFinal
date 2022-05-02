package com.importmag.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.importmag.Adapters.ProductosAdapter;

import java.util.ArrayList;
import java.util.List;

//IMPORTACIONES DE SLIDER
import com.importmag.Interfaces.GetServiceProdFavs;
import com.importmag.Models.ProdAll;
import com.importmag.Models.Productos;
import com.importmag.Models.ProdsFavs;
import com.importmag.R;
import com.importmag.databinding.FragmentTodosproductosBinding;

import com.importmag.Interfaces.GetServiceProds;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TodosProductosFragment extends Fragment {

    private FragmentTodosproductosBinding binding;

    //VARIABLES DEL RECYCLERVIEW
    ProductosAdapter productosAdapter;
    RecyclerView recyclerViewcAllProds;
    ImageView caragndo2;

    //LISTA DE PRODUCTOS
    ArrayList<Productos> prodsList = new ArrayList();
    final ArrayList<ProdsFavs> listafavoritoscli = new ArrayList();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTodosproductosBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //IMPLEMENTACIÓN Y LLAMADO AL RECYCLERVIEW
        recyclerViewcAllProds = binding.recyclerTodosProductos;
        //likebtn=productosAdapter.getOnClickListenersi()
        caragndo2 = binding.imgCargando2;
        recyclerViewcAllProds.setVisibility(View.INVISIBLE);
        productosAdapter = new ProductosAdapter(getActivity(), prodsList);
        recyclerAllProducts(recyclerViewcAllProds);
        return view;
    }

    /**
     * Método que genera un recycler view para mostrar los productos
     */
    private void recyclerAllProducts(RecyclerView recyclerViewTodosProductos) {
        consProdsFav();
        //CONSUMO DE LA API PRODUCTOS//
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.import-mag.com/getSlider/prods.php/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetServiceProds getServiceProds = retrofit.create(GetServiceProds.class);
        Call<List<ProdAll>> call = getServiceProds.find();
        if (isOnlineNet() == true) {
            call.enqueue(new Callback<List<ProdAll>>() {

                @Override
                public void onResponse(Call<List<ProdAll>> call, retrofit2.Response<List<ProdAll>> response) {
                    List<ProdAll> Prods = response.body();
                    //RECORRIDO DE LOS DATOS EXTRAIDOS DE LA API E INSERCIÓN EN EL VIEW SLIDER
                    for (ProdAll s : Prods) {
                        String nuevapalabra = "";
                        Boolean like = false;
                        String name = s.getName();
                        if (name.contains("Ã¡") || name.contains("Ã©") || name.contains("Ã\u00AD") || name.contains("Ã³") || name.contains("Ãº")) {
                            nuevapalabra = name.replaceAll("Ã¡", "á").replaceAll("Ã©", "é").replaceAll
                                    ("Ã\u00AD", "í").replaceAll("Ã³", "ó").replaceAll
                                    ("Ãº", "ú");

                        } else nuevapalabra = s.getName();

                        List<ProdsFavs> favsCli = listafavoritoscli;

                        for (ProdsFavs favs : favsCli) {
                            String id_prodfavs = favs.getId_product();
                            String idprods = s.getId_product().toString();
                            System.out.println("ID LIKEADOS: " + id_prodfavs + " ID PRODS: " + idprods);
                            if (id_prodfavs.equals(idprods)) {
                                like = true;
                            } else if(like!=true) {
                                like = false;
                            }
                        }

                        prodsList.add(new Productos(s.getId_product(), nuevapalabra, "https://import-mag.com/" + s.getId_image() + "-large_default/" + s.getLink_rewrite() + ".jpg", like));
                    }
                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
                    recyclerViewcAllProds.setLayoutManager(layoutManager);
                    productosAdapter = new ProductosAdapter(getActivity(), prodsList);
                    recyclerViewcAllProds.setAdapter(productosAdapter);

                    recyclerViewcAllProds.setVisibility(View.VISIBLE);

                    caragndo2.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(Call<List<ProdAll>> call, Throwable t) {

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

    public static String getFromSharedPreferences(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences("logvalidate", Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }


}