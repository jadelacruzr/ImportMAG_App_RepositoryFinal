package com.importmag.Models;

public class Categoria {
    private String id_category, name,link_rewwrite;

    public Categoria(String id_category, String name, String link_rewwrite) {
        this.id_category = id_category;
        this.name = name;
        this.link_rewwrite = link_rewwrite;
    }

    public String getId_category() {
        return id_category;
    }

    public void setId_category(String id_category) {
        this.id_category = id_category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink_rewwrite() {
        return link_rewwrite;
    }

    public void setLink_rewwrite(String link_rewwrite) {
        this.link_rewwrite = link_rewwrite;
    }
}
