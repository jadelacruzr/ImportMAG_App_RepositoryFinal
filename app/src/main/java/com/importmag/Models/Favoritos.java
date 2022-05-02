package com.importmag.Models;

public class Favoritos {

    String id_wishlist, wishlistName,nbProducts;

    public Favoritos(String id_wishlist, String wishlistName, String nbProducts) {
        this.id_wishlist = id_wishlist;
        this.wishlistName = wishlistName;
        this.nbProducts = nbProducts;
    }

    public String getId_wishlist() {
        return id_wishlist;
    }

    public void setId_wishlist(String id_wishlist) {
        this.id_wishlist = id_wishlist;
    }

    public String getWishlistName() {
        return wishlistName;
    }

    public void setWishlistName(String wishlistName) {
        this.wishlistName = wishlistName;
    }

    public String getNbProducts() {
        return nbProducts;
    }

    public void setNbProducts(String nbProducts) {
        this.nbProducts = nbProducts;
    }
}
