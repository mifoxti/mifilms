package com.example.mifilms;

public class Film {

    public String id, title, description, img_src, vide_src, nfk;

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
    public Film(String title, String img_src) {
        this.title = title;
        this.img_src = img_src;
        // Добавьте остальные поля по умолчанию, если это необходимо
    }
}