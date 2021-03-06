package edu.sabanciuniv.mefe_25255;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.sabanciuniv.mefe_25255.model.CommentItem;

public class Comments extends AppCompatActivity {

    ProgressDialog prgDialog;
    RecyclerView rec_comments;
    int currNewsID;
    List<CommentItem> data;
    CommentsAdapter adp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        getSupportActionBar().setTitle("Comments");

        ActionBar currentbar = getSupportActionBar();
        currentbar.setHomeButtonEnabled(true);
        currentbar.setDisplayHomeAsUpEnabled(true);
        currentbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_18dp);

        data = new ArrayList<>();
        rec_comments = findViewById(R.id.reccomment);
        currNewsID = (int) getIntent().getSerializableExtra("newsId");

        adp = new CommentsAdapter(data, this);
        rec_comments.setLayoutManager(new LinearLayoutManager(this));
        rec_comments.setAdapter(adp);

        CommentsTask tsk = new CommentsTask();
        tsk.execute("http://94.138.207.51:8080/NewsApp/service/news/getcommentsbynewsid/" + Integer.toString(currNewsID));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        CommentsTask tsk = new CommentsTask();
        tsk.execute("http://94.138.207.51:8080/NewsApp/service/news/getcommentsbynewsid/" + Integer.toString(currNewsID));
        super.onNewIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.comments_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.mn_post) {
            Intent i = new Intent(this, PostComment.class);
            i.putExtra("newsID", currNewsID);
            startActivity(i);
        }
        return true;
    }

    class CommentsTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            prgDialog = new ProgressDialog(Comments.this);
            prgDialog.setTitle("Loading Comments");
            prgDialog.setMessage("Please Wait...");
            prgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prgDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String urlStr = strings[0];
            StringBuilder buffer = new StringBuilder();
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            data.clear();
            try {
                JSONObject obj = new JSONObject(s);
                if (obj.getInt("serviceMessageCode") == 1) {
                    JSONArray arr = obj.getJSONArray("items");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject current = (JSONObject) arr.get(i);

                        CommentItem item = new CommentItem(current.getInt("id"),
                                current.getString("name"),
                                current.getString("text")
                        );

                        data.add(item);

                    }
                }
                else {
                    //If there is a problem with service
                }
                adp.notifyDataSetChanged();
                prgDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
