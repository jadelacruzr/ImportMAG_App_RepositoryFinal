package com.import_mag.importmag.interfaces;
import com.import_mag.importmag.models.AllProds;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetServiceProds {
    //HACIENDO GET A LA API que obtiene las ids de los productos

    @GET("prods.php") Call<List<AllProds>> find ();
}
