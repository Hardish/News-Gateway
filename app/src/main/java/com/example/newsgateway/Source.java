package com.example.newsgateway;

import java.util.ArrayList;

public class Source {

    private String id;
    private String name;
    private String category;
    private String language;
    private String country;
    private static ArrayList<Source> source = new ArrayList<>();
    public Source(String id, String name, String category, String language, String country) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.language = language;
        this.country = country;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Source{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", language='" + language + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
