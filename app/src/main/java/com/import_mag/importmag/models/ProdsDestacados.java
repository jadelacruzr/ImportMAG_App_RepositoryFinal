package com.import_mag.importmag.models;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ProdsDestacados {

    Integer id_product;
    String name; // NombreProducto
    String url_image;


    public ProdsDestacados(Integer id_product, String name, String url_image) {
        this.id_product = id_product;
        this.name = name;
        this.url_image = url_image;
    }

    public Integer getId_product() {
        return id_product;
    }

    public void setId_product(Integer id_product) {
        this.id_product = id_product;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl_image() {
        return url_image;
    }

    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }

}
