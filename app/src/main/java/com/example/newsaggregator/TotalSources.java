package com.example.newsaggregator;

import java.io.Serializable;

public class TotalSources implements Serializable {
    public String id;
    public String name;
    public String category;
    public String language;
    public String country;


    public TotalSources(String id, String name, String category, String language, String country) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.language = language;
        this.country = country;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() {
        return country;
    }



    @Override
    public String toString() {
        return this.name;
    }
}
