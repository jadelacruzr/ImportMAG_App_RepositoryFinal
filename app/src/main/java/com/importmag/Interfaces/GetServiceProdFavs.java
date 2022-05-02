package com.importmag.Interfaces;
import com.importmag.Models.ProdsFavs;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetServiceProdFavs {
    //HACIENDO GET A LA API que obtiene las ids de las categorias

    @GET("favs_user.php") Call<List<ProdsFavs>> find (@Query("id_cust") String q);
}
