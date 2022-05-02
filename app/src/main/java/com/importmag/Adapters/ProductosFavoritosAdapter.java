package com.importmag.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.importmag.Activities.DetallesProductosActivity;
import com.importmag.Models.Productos;
import com.importmag.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ProductosFavoritosAdapter extends RecyclerView.Adapter<ProductosFavoritosAdapter.ProductosFavoritosViewHolder> {
    Context context;
    List<Productos> ProductsList;
    Boolean compEliminacion = false;


    public ProductosFavoritosAdapter(Context context, List<Productos> ProductsList) {
        this.context = context;
        this.ProductsList = ProductsList;
    }

    @NotNull
    @Override
    public ProductosFavoritosViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_productos_favoritos, parent, false);
        return new ProductosFavoritosViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProductosFavoritosViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Picasso.get().load(ProductsList.get(position).getUrl_image()).resize(220, 220).into(holder.imageProds);
        String name = ProductsList.get(position).getName();

        holder.none.setVisibility(View.INVISIBLE);
        holder.txtNombre_T.setText(name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, DetallesProductosActivity.class);
                i.putExtra("id_product", ProductsList.get(position).getId_product());
                context.startActivity(i);
            }
        });
        holder.btnEliminarProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id_prod, id_wish;
                id_prod = (ProductsList.get(position).getId_product()).toString();
                id_wish = (ProductsList.get(position).getId_wish()).toString();
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
                                                ProductsList.remove(position);
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
        });

    }

    @Override
    public int getItemCount() {

        return ProductsList.size();
    }

    public static class ProductosFavoritosViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProds, btnEliminarProd;
        TextView txtNombre_T, none;

        public ProductosFavoritosViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            imageProds = itemView.findViewById(R.id.imgProdsTodos);
            txtNombre_T = itemView.findViewById(R.id.txtNombre_T);
            btnEliminarProd = itemView.findViewById(R.id.btn_eliminarPF);
            none = itemView.findViewById(R.id.txtNoneProdListR);

        }

    }

}