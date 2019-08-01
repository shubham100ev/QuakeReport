package com.example.quakereport;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getName();

    private static String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";

    private EarthquakeAdapter mAdapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView earthquakeListView = findViewById(R.id.list);
        EarthquakeAsyncTask task=new EarthquakeAsyncTask();
        task.execute(USGS_REQUEST_URL);
        mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());
        earthquakeListView.setAdapter(mAdapter);


            earthquakeListView.setAdapter(mAdapter);

            earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Earthquake earthquake = mAdapter.getItem(position);
                    Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(earthquake.getLink()));
                    startActivity(intent);
                }
            });
    }

    private class EarthquakeAsyncTask extends AsyncTask<String, Void, List<Earthquake>> {

        @Override
        protected List<Earthquake> doInBackground(String... strings) {
            URL url = createUrl(strings[0]);

            String jsonResponse = "";

            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }


            return extractFeatureFromJson(jsonResponse);
        }

        @Override
        protected void onPostExecute(List<Earthquake> data) {
            mAdapter.clear();
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }        }

        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            if (url == null)
                return jsonResponse;

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else
                    Log.e("Status code", "Status code not equal to 200");
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem in connection", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private List<Earthquake> extractFeatureFromJson(String earthquakeJSON) {
            // If the JSON string is empty or null, then return early.
            if (TextUtils.isEmpty(earthquakeJSON)) {
                return null;
            }

            List<Earthquake> earthquakes = new ArrayList<>();


            try {

                JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);

                JSONArray earthquakeArray = baseJsonResponse.getJSONArray("features");

                for (int i = 0; i < earthquakeArray.length(); i++) {

                    JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);

                    JSONObject properties = currentEarthquake.getJSONObject("properties");

                    // Extract the value for the key called "mag"
                    float magnitude = (float) properties.getDouble("mag");

                    // Extract the value for the key called "place"
                    String[] location = properties.getString("place").split("of", 2);


                    // Extract the value for the key called "time"
                    long time = properties.getLong("time");

                    // Extract the value for the key called "url"
                    String url = properties.getString("url");

                    Date date = new java.util.Date(time);
                    SimpleDateFormat sdf = new java.text.SimpleDateFormat("DD-MM-YYYY");
                    sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+0530"));
                    String d = sdf.format(date);

                    sdf = new java.text.SimpleDateFormat("hh:mm a");
                    sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+0530"));
                    String t = sdf.format(date);
                    Earthquake earthquake = new Earthquake(magnitude, location[0], location[location.length - 1], d, t, url);

                    // Add the new {@link Earthquake} to the list of earthquakes.
                    earthquakes.add(earthquake);
                }

            } catch (JSONException e) {
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
            }

            // Return the list of earthquakes
            return earthquakes;
        }
    }
}
