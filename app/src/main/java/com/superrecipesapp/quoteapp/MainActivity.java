package com.superrecipesapp.quoteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private OkHttpClient client = new OkHttpClient();
    JSONArray data;
    TextView author, content;
    Button share;
    String apiKey = "v3izIwxA4qPIm46Qa8Vymw==wGJwwcRxor0KEN0R";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        author = findViewById(R.id.author);
        content = findViewById(R.id.content);
        share = findViewById(R.id.sharebtn);
        new Thread(){
            @Override
            public void run() {
                    makeRequest("https://api.api-ninjas.com/v1/quotes");
            }
        }.start();

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg =  content.getText().toString() + "\n\n\t\t\t\t\t\t\t\t\t\t" + author.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.setType("text/plain");
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, msg);
                        startActivity(intent);
                    }
                }).start();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread() {
            @Override
            public void run() {
                makeRequest("https://api.api-ninjas.com/v1/quotes");
            }
        }.start();
    }


    public void makeRequest(String url) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Api-Key", apiKey)
                .build();

        // Asynchronous Call
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Handle failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Handle successful response
                    String responseBody = response.body().string();
                    try {
                        data = new JSONArray(responseBody);
                        new Thread()
                        {
                            @Override
                            public void run() {
                                try {
                                    author.setText(data.getJSONObject(0).getString("author"));
                                    content.setText(data.getJSONObject(0).getString("quote"));
                                }catch (Exception e)
                                {

                                }
                            }
                        }.start();

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}