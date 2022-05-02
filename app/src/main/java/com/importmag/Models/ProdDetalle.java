package com.importmag.Models;

public class ProdDetalle {
    private Integer id_product;
    private String name;
    private String description;
    private String description_short;
    private String product_url;

    public ProdDetalle(Integer id_product, String name, String description, String description_short, String product_url) {
        this.id_product = id_product;
        this.name = name;
        this.description = description;
        this.description_short = description_short;
        this.product_url = product_url;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription_short() {
        return description_short;
    }

    public void setDescription_short(String description_short) {
        this.description_short = description_short;
    }

    public String getProduct_url() {
        return product_url;
    }

    public void setProduct_url(String product_url) {
        this.product_url = product_url;
    }
}
