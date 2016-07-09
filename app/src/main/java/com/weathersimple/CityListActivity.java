package com.weathersimple;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.weathersimple.db.AndroidDatabaseManager;
import static com.weathersimple.db.DBContract.*;
import com.weathersimple.db.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class CityListActivity extends AppCompatActivity implements CityWeatherFragment.OnFragmentInteractionListener{
    private static final String LOG_TAG = CityListActivity.class.getSimpleName();
    ListView cityList;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);

        if (findViewById(R.id.city_detail_container) != null) {
            mTwoPane = true;
        }
        initControls();
        getForecast();
    }

    private void initControls() {
        Button addCity = (Button) findViewById(R.id.add_city);
        addCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
                startActivity(intent);
            }
        });

        cityList = (ListView) findViewById(R.id.city_list);
        initCityListAdapter();
        cityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mTwoPane) {
                    bindWeatherFragment(position);
                } else {
                    startWeatherActivity(position);
                }
            }
        });
    }

    private void startWeatherActivity(int position) {
        Intent intent= new Intent(CityListActivity.this, CityWeatherActivity.class);
        intent.putExtra("text", "text" + position);
        startActivityForResult(intent, 0);
    }

    private void bindWeatherFragment(int position) {
        CityWeatherFragment fragment = new CityWeatherFragment();
        Bundle b = new Bundle();
        b.putString("text", "text" + position);
        fragment.setArguments(b);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.city_detail_container, fragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void initCityListAdapter() {
        DBHelper handler = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = handler.getReadableDatabase();
        Cursor cursor = db.query(CityTable.CITY_TABLE_NAME, CityTable.columns, null, null, null, null, null);
        CityListAdapter adapter = new CityListAdapter(this, cursor, 0);
        cityList.setAdapter(adapter);
    }

    private void getForecast(){
        if (isNetworkConnected()) {
            try {
                URL url = getUrl();
                new GetForecastTask().execute(url);
            } catch (MalformedURLException e) {
                Toast.makeText(getApplicationContext(), R.string.error_url_creation, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.error_network_connection, Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    private URL getUrl() throws MalformedURLException {
        //TODO: creating url from list of cities in DB
        return new URL("http://api.openweathermap.org/data/2.5/group?id=524901,703448,2643743&units=metric&appid=4d9f72c19622360a7a3611c004c631f4");
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private class GetForecastTask extends AsyncTask<URL, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //TODO:show progressbar here
        }

        @Override
        protected String doInBackground(URL... params) {
            try {
                return queryForecast(params[0]);
            } catch (IOException e) {
                return String.valueOf(R.string.error_connection);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                parseJsonForecastToDB(s);
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), R.string.error_connection, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void parseJsonForecastToDB(String s) throws JSONException {
        
    }

    private String queryForecast(URL url) throws IOException {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJsonStr = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();

            int response = urlConnection.getResponseCode();
            Log.d(LOG_TAG, "response code " + response);

            inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return String.valueOf(R.string.error_connection);
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return String.valueOf(R.string.error_connection);
            }
            forecastJsonStr = buffer.toString();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null){
                inputStream.close();
            }
        }

        return forecastJsonStr;
    }
}
