package com.importmag.Fragments;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.importmag.Activities.VentanaNuevaListaActivity;
import com.importmag.Adapters.FavoritosAdapter;
import com.importmag.Models.Favoritos;
import com.importmag.R;
import com.importmag.databinding.FragmentFavoritosBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FavoritosFragment extends Fragment {
    public FavoritosFragment() {
    }

    private FragmentFavoritosBinding binding;

    //VARIABLES DEL RECYCLERVIEW
    FavoritosAdapter favsAdapter;
    RecyclerView recyclerViewcFavs;
    ImageView caragndo2;
    LinearLayout ll;
    ExtendedFloatingActionButton crearNewList;
    TextView none;
    //LISTA DE FAVORITOS
    ArrayList<Favoritos> favsList = new ArrayList();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            binding = FragmentFavoritosBinding.inflate(inflater, container, false);
            View view = binding.getRoot();

            //IMPLEMENTACIÓN Y LLAMADO AL RECYCLERVIEW
            recyclerViewcFavs = binding.recyclerFavoritos;
            caragndo2 = binding.imgCargandoFav;
            ll = binding.llHome;
            none = binding.txtnoneFavoritoslist;
            crearNewList = binding.btnCraerList;
            none.setVisibility(View.INVISIBLE);
            crearNewList.setVisibility(View.INVISIBLE);
            crearNewList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), VentanaNuevaListaActivity.class);
                    startActivity(i);

                }
            });

            caragndo2.setVisibility(View.VISIBLE);
            recyclerViewcFavs.setVisibility(View.INVISIBLE);


            if (isOnlineNet() == true) {
                consultaFavs(recyclerViewcFavs);
            } else {
                Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Revisa tu conexión a Internet", Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.mensajeinfo));
                snackbar.show();

            }


            return view;
        } catch (Exception e) {
            Log.e(TAG, "onCreateView", e);
            throw e;
        }
    }

    private void consultaFavs(RecyclerView recyclerViewcFavs) {

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

                    favsAdapter = new FavoritosAdapter(getActivity(), favsList);
                    recyclerViewcFavs.setAdapter(favsAdapter);
                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
                    recyclerViewcFavs.setLayoutManager(layoutManager);
                    ll.setVisibility(View.VISIBLE);
                    caragndo2.setVisibility(View.GONE);
                    recyclerViewcFavs.setVisibility(View.VISIBLE);
                    crearNewList.setVisibility(View.VISIBLE);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                caragndo2.setVisibility(View.INVISIBLE);
                recyclerViewcFavs.setVisibility(View.INVISIBLE);
                none.setVisibility(View.VISIBLE);
                crearNewList.setVisibility(View.VISIBLE);

            }
        };

        StringRequest request2 = new StringRequest(Request.Method.GET, url,
                responseListener, errorListener) {
        };
        Volley.newRequestQueue(getContext()).add(request2);


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
    public void onResume() {

        super.onResume();
        try {
            if (isOnlineNet() == true) {
                consultaFavs(recyclerViewcFavs);
            } else {
                Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Revisa tu conexión a Internet", Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.mensajeinfo));
                snackbar.show();

            }
        } catch (Exception e) {
            Log.e(TAG, "onCreateView", e);
            throw e;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}


