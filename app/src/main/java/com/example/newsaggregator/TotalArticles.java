package com.example.newsaggregator;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class TotalArticles implements Serializable {
    public String author;
    public String title;
    public String description;
    public String url;
    public String urlToImage;
    public String publishedAt;

    public TotalArticles(String author, String title, String description, String url, String urlToImage, String publishedAt) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
    }
}
