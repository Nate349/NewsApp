package com.example.newsaggregator;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewsViewHolder extends RecyclerView.ViewHolder{
    TextView headline;
    TextView date;
    TextView author;
    ImageView picture;
    TextView articleText;
    TextView pageNum;

    public NewsViewHolder(@NonNull View itemView){
        super(itemView);
        headline = itemView.findViewById(R.id.headline);
        date = itemView.findViewById(R.id.date);
        author = itemView.findViewById(R.id.author);
        picture = itemView.findViewById(R.id.picture);
        articleText = itemView.findViewById(R.id.articleText);
        pageNum = itemView.findViewById(R.id.pageNum);
    }
}
