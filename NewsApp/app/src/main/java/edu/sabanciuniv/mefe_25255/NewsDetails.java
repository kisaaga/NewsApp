package edu.sabanciuniv.mefe_25255;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import edu.sabanciuniv.mefe_25255.model.NewsItem;

public class NewsDetails extends AppCompatActivity {

    NewsItem currNews;
    ImageView imgDetail;
    TextView newsTitle;
    TextView textDetail;
    TextView newsDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        imgDetail = findViewById(R.id.newsImg);
        textDetail = findViewById(R.id.newsText);
        newsTitle = findViewById(R.id.newsTitle);
        newsDate = findViewById(R.id.newsDate);


        currNews = (NewsItem)getIntent().getSerializableExtra("selectedNews");

        //imgDetail.setImageResource(currNews.getImageId());
        textDetail.setText(currNews.getText());
        newsTitle.setText(currNews.getTitle());
        newsDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(currNews.getNewsDate()));

        if (currNews.getBitmap() == null) {
            new ImageDownloadTask(imgDetail).execute(currNews);

        } else {
            imgDetail.setImageBitmap(currNews.getBitmap());
        }

        getSupportActionBar().setTitle("News Details");

        ActionBar currentbar = getSupportActionBar();
        currentbar.setHomeButtonEnabled(true);
        currentbar.setDisplayHomeAsUpEnabled(true);
        currentbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_18dp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.mn_comment) {
            Intent i = new Intent(this, Comments.class);
            i.putExtra("newsId", currNews.getId());
            startActivity(i);
        }

        return true;
    }
}
