package edu.sabanciuniv.mefe_25255;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
import java.util.Date;
import java.util.List;

import edu.sabanciuniv.mefe_25255.model.NewsItem;

public class MainActivity extends AppCompatActivity {
    ProgressDialog prgDialog;
    RecyclerView newsRecView;
    Spinner categories;
    List<NewsItem> data;
    NewsAdapter adp;
    List<String> cat_data;
    List<Integer> cat_id_list;
    ArrayAdapter<String> adp2;
    String current_cat = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("News");

        //List<NewsItem> items= NewsItem.getAllNews();
        //Log.i("DEV","Total of " + items.size() + " news exist");
        //List<CommentItem> comments = NewsItem.getCommentsByNewsId(0);
        //Log.i("DEV","Total of " + comments.size() + " comments exist for newsid 0");

        cat_data = new ArrayList<>();
        cat_data.add("All");
        cat_id_list = new ArrayList<>();
        cat_id_list.add(-1);
        categories = findViewById(R.id.sp_categ);
        // Spinner kategorilere gore olusturulacak

        data = new ArrayList<>();
        newsRecView = findViewById(R.id.recview);
        adp = new NewsAdapter(data, this, new NewsAdapter.NewsItemClickListener() {
            @Override
            public void newsItemClicked(NewsItem selectedNewsItem) {
                //Toast.makeText(MainActivity.this, selectedNewsItem.getTitle(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this, NewsDetails.class);
                i.putExtra("selectedNews", selectedNewsItem);
                startActivity(i);

            }
        });
        newsRecView.setLayoutManager(new LinearLayoutManager(this));
        newsRecView.setAdapter(adp);

        adp2
                = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                cat_data
        );


        categories.setAdapter(adp2);

        CategTask tsk2 = new CategTask();
        tsk2.execute("http://94.138.207.51:8080/NewsApp/service/news/getallnewscategories");

        categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                current_cat =
                        ((String)categories.getSelectedItem());

                NewsTask tsk = new NewsTask();

                //Toast.makeText(MainActivity.this, current_cat, Toast.LENGTH_SHORT).show();
                if (current_cat == "All") {
                    tsk.execute("http://94.138.207.51:8080/NewsApp/service/news/getall");
                } else {
                    for (int i = 1; i < cat_data.size(); i++) {
                        if (current_cat == cat_data.get(i)){
                            tsk.execute("http://94.138.207.51:8080/NewsApp/service/news/getbycategoryid/" + Integer.toString(cat_id_list.get(i)));
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    class NewsTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            prgDialog = new ProgressDialog(MainActivity.this);
            prgDialog.setTitle("Loading");
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

                        long date = current.getLong("date");
                        Date objDate = new Date(date);

                        NewsItem item = new NewsItem(current.getInt("id"),
                                current.getString("title"),
                                current.getString("text"),
                                current.getString("image"),
                                objDate
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

    class CategTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
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
            cat_data.clear();
            cat_id_list.clear();
            cat_data.add("All");
            cat_id_list.add(-1);
            try {
                JSONObject obj = new JSONObject(s);
                if (obj.getInt("serviceMessageCode") == 1) {
                    JSONArray arr = obj.getJSONArray("items");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject current = (JSONObject) arr.get(i);
                        String categ = current.getString("name");
                        int id = current.getInt("id");

                        cat_data.add(categ);
                        cat_id_list.add(id);
                    }
                }
                else {
                    //If there is a problem with service
                }
                adp2.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
