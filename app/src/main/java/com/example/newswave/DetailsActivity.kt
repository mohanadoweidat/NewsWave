package com.example.newswave

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.newswave.Models.NewsHeadlines
import com.squareup.picasso.Picasso

class DetailsActivity : AppCompatActivity() {

    private lateinit var headlines : NewsHeadlines
    private lateinit var text_title: TextView
    private lateinit var text_author:TextView
    private lateinit var text_time: TextView
    private lateinit var text_detail: TextView
    private lateinit var text_content: TextView
    private lateinit var img_news: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        text_title = findViewById(R.id.text_details_title)
        text_author = findViewById(R.id.text_details_author)
        text_time = findViewById(R.id.text_details_time)
        text_detail = findViewById(R.id.text_details_detail)
        text_content = findViewById(R.id.text_details_content)
        img_news = findViewById(R.id.img_detail_news)


        headlines = intent.getSerializableExtra("data") as NewsHeadlines


        text_title.setText(headlines.title)
        text_author.setText(headlines.author)
        text_time.setText(headlines.publishedAt)
        text_detail.setText(headlines.description)
        text_content.setText(headlines.content)
        Picasso.get().load(headlines.urlToImage).into(img_news)
    }
}