package com.importmag.Models;

public class ProdAll {
    Integer id_product;
    String name,link_rewrite,id_image;

    public ProdAll(Integer id_product, String name, String link_rewrite, String id_image) {
        this.id_product = id_product;
        this.name = name;
        this.link_rewrite = link_rewrite;
        this.id_image = id_image;
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

    public String getLink_rewrite() {
        return link_rewrite;
    }

    public void setLink_rewrite(String link_rewrite) {
        this.link_rewrite = link_rewrite;
    }

    public String getId_image() {
        return id_image;
    }

    public void setId_image(String id_image) {
        this.id_image = id_image;
    }
}
