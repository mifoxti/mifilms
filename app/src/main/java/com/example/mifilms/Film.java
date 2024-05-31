package com.example.mifilms;

public class Film {

    public String id, title, description, img_src, vide_src, nfk;

    // Конструктор без аргументов
    public Film() {
        // Пустой конструктор необходим для Firebase
    }

    // Конструктор для считывания данных из Firebase
    public Film(String id, String title, String description, String img_src, String vide_src, String nfk) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.img_src = img_src;
        this.vide_src = vide_src;
        this.nfk = nfk;
    }

    // Конструктор для удобства создания объектов Film
    public Film(String title, String img_src, String vide_src) {
        this.title = title;
        this.img_src = img_src;
        this.vide_src = vide_src;
        this.nfk = "true";
        this.description = "Описание";
    }

    public String getTitle() {
        return title;
    }

    public String getImg_src() {
        return img_src;
    }

    public String getVide_src() {
        return vide_src;
    }

    public String getDescription() {
        return description;
    }

    public boolean isNfk() {
        return Boolean.parseBoolean(nfk);
    }
}

