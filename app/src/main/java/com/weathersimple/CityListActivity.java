package com.weathersimple;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
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

import com.weathersimple.db.DBContract;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CityListActivity extends AppCompatActivity implements CityWeatherFragment.OnFragmentInteractionListener{
    private static final String LOG_TAG = CityListActivity.class.getSimpleName();
    public static final String OWM_LIST = "list";
    public static final String OWM_WEATHER = "weather";
    public static final String OWM_DESCRIPTION = "description";
    public static final String OWM_MAIN = "main";
    public static final String OWM_TEMP = "temp";
    public static final String OWM_PRESSURE = "pressure";
    public static final String OWM_HUMIDITY = "humidity";
    public static final String OWM_WIND = "wind";
    public static final String OWM_SPEED = "speed";
    public static final String OWM_DEG = "deg";
    private static final String OWM_CITY_ID = "id";
    ListView cityList;
    private ArrayList<String> citiesList;
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
                .replace(R.id.city_detail_container, fragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void initCityListAdapter() {
        DBHelper handler = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = handler.getReadableDatabase();
        Cursor cursor = db.query(CityTable.CITY_TABLE_NAME, CityTable.columns, null, null, null, null, null);
        createCitiesList(cursor);
        CityListAdapter adapter = new CityListAdapter(this, cursor, 0);
        cityList.setAdapter(adapter);
    }

    private void createCitiesList(Cursor cursor) {
        citiesList = new ArrayList<String>();
        cursor.moveToPosition(0);
        while(!cursor.isAfterLast()) {
            int index = cursor.getColumnIndex(CityTable.COLUMN_CITY_ID);
            String id = cursor.getString(index);
            citiesList.add(id);
            cursor.moveToNext();
        }
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
        String domain = "http://api.openweathermap.org";
        StringBuilder url = new StringBuilder(domain);
        url.append("/data/2.5/group?");
        url.append("id=");
        for (String cityId:citiesList) {
            url.append(cityId);
            url.append(",");
        }
        url.append("&units=metric");
        url.append("&appid=");
        url.append(getString(R.string.owm_appid));
        return new URL(new String(url));
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
                updateUI();
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), R.string.error_connection, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateUI() {
    }

    private void parseJsonForecastToDB(String s) throws JSONException {
        JSONArray cityList = new JSONObject(s).getJSONArray(OWM_LIST);
        for (int i = 0; i < cityList.length(); i++){
            JSONObject cityForecastObject = cityList.getJSONObject(i);
            String cityId = cityForecastObject.getString(OWM_CITY_ID);

            JSONArray weatherArray = cityForecastObject.getJSONArray(OWM_WEATHER);
            String description = weatherArray.getJSONObject(0).getString(OWM_DESCRIPTION);

            JSONObject mainObject = cityForecastObject.getJSONObject(OWM_MAIN);
            double temperature = mainObject.getDouble(OWM_TEMP);
            int pressure = mainObject.getInt(OWM_PRESSURE);
            int humidity = mainObject.getInt(OWM_HUMIDITY);

            JSONObject windObject = cityForecastObject.getJSONObject(OWM_WIND);
            int windSpeed = windObject.getInt(OWM_SPEED);
            int windDegree = windObject.getInt(OWM_DEG);

            ContentValues row = new ContentValues();
            row.put(WeatherTable.COLUMN_CITY_ID, cityId);
            row.put(WeatherTable.COLUMN_WEATHER_DESCRIPTION, description);
            row.put(WeatherTable.COLUMN_TEMPERATURE, temperature);
            row.put(WeatherTable.COLUMN_PRESSURE, pressure);
            row.put(WeatherTable.COLUMN_HUMIDITY, humidity);
            row.put(WeatherTable.COLUMN_WIND_SPEED, windSpeed);
            row.put(WeatherTable.COLUMN_WIND_DIRECTION, windDegree);
            row.put(WeatherTable.COLUMN_STATUS, STATUS_NEW);

            DBHelper.insertCityForecast(getApplicationContext(), cityId, description, temperature, pressure, humidity, windSpeed, windDegree, STATUS_NEW);
        }

        return;
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