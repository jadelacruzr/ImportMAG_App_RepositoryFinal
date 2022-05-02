package com.importmag.Models;

public class ProdsFavs {
    String id_product;
    String id_wishlist;

    public ProdsFavs(String id_product, String id_wishlist) {
        this.id_product = id_product;
        this.id_wishlist = id_wishlist;
    }

    public String getId_product() {
        return id_product;
    }

    public void setId_product(String id_product) {
        this.id_product = id_product;
    }

    public String getId_wishlist() {
        return id_wishlist;
    }

    public void setId_wishlist(String id_wishlist) {
        this.id_wishlist = id_wishlist;
    }
}
