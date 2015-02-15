package com.example.hikimori911.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by hikimori911 on 08.02.2015.
 */
public class ForecastFragment extends Fragment {

    public static final String SERVER_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";

    public ForecastFragment() {
    }

    protected ListView forecastList;
    protected ArrayAdapter<String> mForecastAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mForecastAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                new ArrayList<String>());
        forecastList = (ListView)rootView.findViewById(R.id.listview_forecast);
        forecastList.setAdapter(mForecastAdapter);
        forecastList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Toast.makeText(getActivity(),mForecastAdapter.getItem(position),Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(),DetailActivity.class)
                                .putExtra(Intent.EXTRA_TEXT, mForecastAdapter.getItem(position));
                        startActivity(intent);
                    }
                }
        );
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_refresh:
                updateWeather();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        new FetchWeatherTask().execute(prefs.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default)));
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]>{

        private final String TAG = FetchWeatherTask.class.getSimpleName();

        protected String[] doInBackground(String... urls) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            if(urls.length == 0){
                return null;
            }

            String defaultFormat = "json";
            String defaultUnits = "metric";
            int days = 7;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            Uri.Builder builder = Uri.parse(SERVER_URL).buildUpon()
                    .appendQueryParameter("q",urls[0])
                    .appendQueryParameter("mode", urls.length > 1 ? urls[1] : defaultFormat)
                    .appendQueryParameter("units",urls.length > 2 ? urls[2] : defaultUnits)
                    .appendQueryParameter("cnt",urls.length > 3 ? urls[3] : String.valueOf(days));

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                URL url = new URL(builder.build().toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                forecastJsonStr = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                if(forecastJsonStr!=null) {
                    return WeatherDataParser.getWeatherDataFromJson(forecastJsonStr, days, getActivity(),TAG);
                }
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String[] result) {
            if (result != null){
                mForecastAdapter.clear();
                for (int i = 0; i < result.length; i++) {
                    mForecastAdapter.add(result[i]);
                }
                mForecastAdapter.notifyDataSetChanged();
            }
        }
    }
}
