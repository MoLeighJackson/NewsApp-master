package com.example.moleigh.newsapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
            String url = "https://content.guardianapis.com/search?q=agriculture&from-date=2018-09-01&to-date=2018-09-06&api-key=727920b2-be9a-4727-841d-133401cf04ad";
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
                        JSONObject c = stories.getJSONObject(i);
                        String title = c.getString("webTitle");
                        String section = c.getString("sectionName");


                        // tmp hash map for a single pokemon
                        HashMap<String, String> story = new HashMap<>();

                        // add each child node to HashMap key => valu
                        // e
                        story.put("title", title);
                        story.put("section", section);
                        // adding a pokemon to our pokemon list
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
            ListAdapter adapter = new SimpleAdapter(StoryActivity.this, storyList,
                    R.layout.list_item, new String[]{"title", "section"},
                    new int[]{R.id.title, R.id.section});
            list_view.setAdapter(adapter);
        }
    }
}

