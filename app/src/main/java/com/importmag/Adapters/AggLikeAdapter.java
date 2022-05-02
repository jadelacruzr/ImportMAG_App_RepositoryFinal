package com.importmag.Adapters;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.importmag.Models.Favoritos;
import com.importmag.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AggLikeAdapter extends RecyclerView.Adapter<AggLikeAdapter.AggLikeViewHolder> {
    Context context;
    List<Favoritos> favoritosList;
    static Integer id_productoObtenido = 0;
    ProductosAdapter adapter;
    public AggLikeAdapter(Context context) {
        this.context = context;
    }

    public AggLikeAdapter(Context context, List<Favoritos> favoritosList) {
        this.context = context;
        this.favoritosList = favoritosList;
    }

    @NotNull
    @Override
    public AggLikeViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_agg_like, parent, false);
        SharedPreferences.Editor editor = context.getSharedPreferences("likeornot", Context.MODE_PRIVATE).edit();
        editor.clear().apply();
        return new AggLikeViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AggLikeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String name = favoritosList.get(position).getWishlistName();
        holder.txtNombre_T.setText(name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idwish = favoritosList.get(position).getId_wishlist();
                final String url =
                        "https://import-mag.com/rest/wishlist?action=addProductToWishlist&id_product=" + id_productoObtenido + "&idWishList=" + idwish;
                System.out.println("URL: " + url);
                final Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject resp = new JSONObject(response);
                            String code = resp.getString("code");
                            String mensaje =resp.getString("message");
                            if(code.equals("200"))
                            {

                                Intent data = new Intent();
                                String ff= "true";
                                data.setData(Uri.parse(ff));
                                ((Activity) context).setResult(RESULT_OK, data);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(view.getContext(), "Agregado correctamente", Toast.LENGTH_SHORT).show();
                                        ((Activity) context).finish();

                                    }
                                }, 1000);
                            }else{
                                Snackbar snackbar = Snackbar.make(view, mensaje, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null);
                                View sbView = snackbar.getView();
                                sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.mensajeok));
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


                    }
                };

                StringRequest request2 = new StringRequest(Request.Method.GET, url,
                        responseListener, errorListener) {
                };
                Volley.newRequestQueue(view.getContext()).add(request2);

            }
        });
        ///////EDITAR METODO DE ELIMINACION
        /*holder.btnEliminarListFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id_prod, id_wish;
                id_prod = (favoritosList.get(position).getId_product()).toString();
                id_wish = (favoritosList.get(position).getId_wish()).toString();
                AlertDialog.Builder alerta = new AlertDialog.Builder(context);
                alerta.setMessage("¿Esta seguro de remover el producto de esta lista?")
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final String url = "https://import-mag.com/rest/wishlist?action=deleteProductFromWishList&id_product=" + id_prod + "&idWishList=" + id_wish;

                                final Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            String mensaje = jsonObject.getString("message");
                                            String code = jsonObject.getString("code");
                                            if (code.equals("200")) {
                                                Snackbar snackbar = Snackbar.make(view, "Producto removido correctamente", Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null);
                                                View sbView = snackbar.getView();
                                                sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.mensajeok));
                                                snackbar.show();
                                                favoritosList.remove(position);
                                                notifyDataSetChanged();
                                            } else {
                                                Snackbar snackbar = Snackbar.make(view, mensaje, Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null);
                                                View sbView = snackbar.getView();
                                                sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.mensaerror));
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
                                        Snackbar snackbar = Snackbar.make(view, " Error de conexión", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null);
                                        View sbView = snackbar.getView();
                                        sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.mensajeinfo));
                                        snackbar.show();
                                    }
                                };

                                StringRequest request2 = new StringRequest(Request.Method.GET, url,
                                        responseListener, errorListener) {
                                };
                                Volley.newRequestQueue(context).add(request2);
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
        });*/

    }

    @Override
    public int getItemCount() {

        return favoritosList.size();
    }

    public static class AggLikeViewHolder extends RecyclerView.ViewHolder {
        ImageView btnEliminarListFav;
        TextView txtNombre_T;

        public AggLikeViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            btnEliminarListFav = itemView.findViewById(R.id.btn_eliminarfav);
            txtNombre_T = itemView.findViewById(R.id.txtFavoritosAgglike);


        }

    }

    public static void enviarDatos(Integer idprod) {
        id_productoObtenido = idprod;
    }
    private void saveLoginSharedPreference(String like) {

        SharedPreferences sharedPref = ((Activity) context).getSharedPreferences("likeornot",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("like",like);
        editor.apply();
    }

}