package com.example.newsaggregator;

import android.content.Intent;
import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder> {

    private final MainActivity main;
    private final ArrayList<TotalArticles> articleList;

    public NewsAdapter(MainActivity main, ArrayList<TotalArticles> articleList){
        this.main = main;
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(
                LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.news_entry, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        TotalArticles a = articleList.get(position);
        holder.headline.setText(a.title);
        holder.headline.setOnClickListener(v -> click(a.url));
        holder.date.setText(formatDateTime(a.publishedAt));
        holder.author.setText(a.author);
        holder.picture.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        Picasso.get().load(a.urlToImage).placeholder(R.drawable.noimage).error(R.drawable.brokenimage).into(holder.picture);
        holder.picture.setOnClickListener(v -> click(a.url));
        holder.articleText.setMovementMethod(new ScrollingMovementMethod());
        holder.articleText.setText(a.description);
        holder.articleText.setOnClickListener(v -> click(a.url));
        holder.pageNum.setText(String.format(
                Locale.getDefault(),"%d of %d", (position+1), articleList.size()));
    }
    @Override
    public int getItemCount() {
        return articleList.size();
    }



    private void click(String url){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        main.startActivity(browserIntent);
    }

    private String formatDateTime(String time) {
        try {
            DateTimeFormatter parser = DateTimeFormatter.ISO_DATE_TIME;
            Instant instant = parser.parse(time, Instant::from);
            LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM d, yyyy hh:mm a");

            return ldt.format(dtf);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
