package com.example.moleigh.newsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class StoryActivity extends AppCompatActivity {

    private String TAG = StoryActivity.class.getSimpleName();
    private ListView list_view;
    ArrayList<HashMap<String, String>> storyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storyList = new ArrayList<>();
        list_view = (ListView) findViewById(R.id.list);

        new GetStory().execute();
    }

    private class GetStory extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // TODO: make a request to the URL
            String url = "https://content.guardianapis.com/search?q=agriculture&from-date=2018-09-01&to-date=2018-09-06&show-tags=contributor&api-key=727920b2-be9a-4727-841d-133401cf04ad";
            String jsonString = "";
            try {
                jsonString = sh.makeHttpRequest(createUrl(url));
            } catch (IOException e) {
                return null;
            }

            Log.e(TAG, "Response from url: " + jsonString);
            if (jsonString != null) {
                try {
                    //TODO: Create a new JSONObject
                    JSONObject jsonObj = new JSONObject(jsonString);

                    // TODO: Get the JSON Array node
                    JSONArray stories = jsonObj.getJSONObject("response").getJSONArray("results");

                    // looping through all Contacts
                    for (int i = 0; i < stories.length(); i++) {
                        //TODO: get the JSONObject
                        JSONObject content = stories.getJSONObject(i);
                        String title = content.getString("webTitle");
                        String section = content.getString("sectionName");
                        String date = content.getString("webPublicationDate");
                        String id = content.getString("id");
                        String author;


                        if(title.contains("|")) {
                            author = content.getString("webTitle");

                        } else {
                            author = "No Author Listed";
                        }

                        // tmp hash map for a single story
                        HashMap<String, String> story = new HashMap<>();

                        // add each child node to HashMap key => value
                        //Title is the full title including the author info
                        story.put("title", title);
                        story.put("section", section);
                        story.put("author", author);
                        story.put("date", date);
                        // adding a story to our story list
                        storyList.add(story);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                return null;
            }
            return url;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            final ListAdapter adapter = new SimpleAdapter(StoryActivity.this, storyList,
                    R.layout.list_item, new String[]{"title", "section", "author", "date"},
                    new int[]{R.id.title, R.id.section, R.id.author, R.id.date});
            list_view.setAdapter(adapter);
        }
    }
}

