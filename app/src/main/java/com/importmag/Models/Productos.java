package com.importmag.Models;

public class Productos {

    Integer id_product;
    String name,id_wish,url_image;
    Boolean likeornot;



    public Productos(Integer id_product, String name, String url_image,Boolean like) {
        this.id_product = id_product;
        this.name = name;
        this.url_image = url_image;
        this.likeornot=like;
    }

    public Productos(Integer id_product, String name, String url_image, String id_wish) {
        this.id_product = id_product;
        this.name = name;
        this.id_wish = id_wish;
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

    public String getId_wish() {
        return id_wish;
    }

    public void setId_wish(String id_wish) {
        this.id_wish = id_wish;
    }

    public String getUrl_image() {
        return url_image;
    }

    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }
    public Boolean getLikeornot() {
        return likeornot;
    }

    public void setLikeornot(Boolean likeornot) {
        this.likeornot = likeornot;
    }
}
