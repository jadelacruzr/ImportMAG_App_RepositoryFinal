package com.importmag.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.importmag.Activities.CatProdsActivity;
import com.importmag.Models.Categoria;
import com.importmag.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoriasAdapter extends RecyclerView.Adapter<CategoriasAdapter.ViewHolderCat> {

    private List<Categoria> listCategoria;
    private Context context;


    public CategoriasAdapter(List<Categoria> listCategoria, Context context) {
        this.listCategoria = listCategoria;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderCat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_categorias, parent, false);
        return new ViewHolderCat(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCat holder, @SuppressLint("RecyclerView") int position) {

        holder.txtNameCat.setText(listCategoria.get(position).getName());
        String id =listCategoria.get(position).getId_category();
        String lnkf= listCategoria.get(position).getLink_rewwrite();


        Picasso.get().load("https://import-mag.com/c/"+id+"-category_default/"+lnkf+".jpg")
                .resize(140,180).into(holder.imageCat);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), CatProdsActivity.class);
                i.putExtra("id_categoria", listCategoria.get(position).getId_category());
                i.putExtra("name_cat", listCategoria.get(position).getName());
                context.startActivity(i);
            }
        });

    }


    @Override
    public int getItemCount() {
        return listCategoria.size();
    }

    public static class ViewHolderCat extends RecyclerView.ViewHolder {

        private TextView txtNameCat;
        private ImageView imageCat;


        public ViewHolderCat(@NonNull View itemView) {
            super(itemView);

            txtNameCat = itemView.findViewById(R.id.txt_nombFav);
            imageCat= itemView.findViewById(R.id.imageCategorys);

        }
    }
}
