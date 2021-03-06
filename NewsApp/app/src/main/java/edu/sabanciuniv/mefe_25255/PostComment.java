package edu.sabanciuniv.mefe_25255;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PostComment extends AppCompatActivity {

    TextView editName;
    TextView editMessage;
    Button btnPost;
    String username, comment_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment);

        getSupportActionBar().setTitle("Post Comment");

        ActionBar currentbar = getSupportActionBar();
        currentbar.setHomeButtonEnabled(true);
        currentbar.setDisplayHomeAsUpEnabled(true);
        currentbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_18dp);

        editName = findViewById(R.id.editTextName);
        editMessage = findViewById(R.id.editTextMessage);
        btnPost = findViewById(R.id.btn_post);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = editName.getText().toString();
                comment_input = editMessage.getText().toString();
                JsonTask tsk = new JsonTask();
                tsk.execute("http://94.138.207.51:8080/NewsApp/service/news/savecomment");

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }

    class JsonTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            StringBuilder strBuilder = new StringBuilder();
            String urlStr = strings[0];

            JSONObject obj = new JSONObject();
            try {
                obj.put("name", username);
                obj.put("text", comment_input);
                obj.put("news_id", Integer.toString((int) getIntent().getSerializableExtra("newsID")));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.connect();

                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes(obj.toString());

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = "";

                    while ((line = reader.readLine()) != null) {
                        strBuilder.append(line);
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return strBuilder.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject obj = new JSONObject(s);
                if (obj.getInt("serviceMessageCode") == 1) {
                    Intent i = new Intent(PostComment.this, Comments.class);
                    i.putExtra("newsId", (int) getIntent().getSerializableExtra("newsID"));
                    startActivity(i);
                } else {
                    //Show an Alert
                    AlertDialog alertDialog = new AlertDialog.Builder(PostComment.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Your message couldn't be sent!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
