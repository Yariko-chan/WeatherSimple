package com.weathersimple;

import android.app.ProgressDialog;
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
import android.widget.CursorAdapter;
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
import java.net.URL;

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
    public static final String OWM_WIND_SPEED = "speed";
    public static final String OWM_WIND_DIRECTION = "deg";
    private static final String OWM_CITY_ID = "id";
    private static final String OWM_CITY_NAME = "name";
    private static final String OWM_COUNTRY = "country";
    private static final String OWM_ICON = "icon";
    private static final String OWM_SYS = "sys";
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
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                cursor.moveToPosition(position);
                Bundle weatherInfo = createBundleFromCursor(cursor);
                if (mTwoPane) {
                    bindWeatherFragment(weatherInfo);
                } else {
                    startWeatherActivity(weatherInfo);
                }
            }
        });
    }

    @NonNull
    private Bundle createBundleFromCursor(Cursor cursor) {
        Bundle weatherInfo = new Bundle();
        putValueFromCursorToBundle(cursor, weatherInfo, CityWeatherTable.COLUMN_CITY_NAME, OWM_CITY_NAME);
        putValueFromCursorToBundle(cursor, weatherInfo, CityWeatherTable.COLUMN_COUNTRY, OWM_COUNTRY);
        putValueFromCursorToBundle(cursor, weatherInfo, CityWeatherTable.COLUMN_WEATHER_DESCRIPTION, OWM_DESCRIPTION);
        putValueFromCursorToBundle(cursor, weatherInfo, CityWeatherTable.COLUMN_WEATHER_ICON, OWM_ICON);
        putValueFromCursorToBundle(cursor, weatherInfo, CityWeatherTable.COLUMN_TEMPERATURE, OWM_TEMP);
        putValueFromCursorToBundle(cursor, weatherInfo, CityWeatherTable.COLUMN_HUMIDITY, OWM_HUMIDITY);
        putValueFromCursorToBundle(cursor, weatherInfo, CityWeatherTable.COLUMN_PRESSURE, OWM_PRESSURE);
        putValueFromCursorToBundle(cursor, weatherInfo, CityWeatherTable.COLUMN_WIND_SPEED, OWM_WIND_SPEED);
        putValueFromCursorToBundle(cursor, weatherInfo, CityWeatherTable.COLUMN_WIND_DIRECTION, OWM_WIND_DIRECTION);
        return weatherInfo;
    }

    private void putValueFromCursorToBundle(Cursor cursor, Bundle b, String columnName, String key) {
        b.putString(key, getStringByColumnName(cursor, columnName));
    }

    private String getStringByColumnName(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
    }

    private void startWeatherActivity(Bundle weatherInfo) {
        Intent intent= new Intent(CityListActivity.this, CityWeatherActivity.class);
        intent.putExtra(OWM_WEATHER, weatherInfo);
        startActivityForResult(intent, 0);
    }

    private void bindWeatherFragment(Bundle weatherInfo) {
        CityWeatherFragment fragment = new CityWeatherFragment();
        fragment.setArguments(weatherInfo);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.city_detail_container, fragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void initCityListAdapter() {
        DBHelper handler = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = handler.getReadableDatabase();
        Cursor cursor = db.query(CityWeatherTable.TABLE_NAME, null, null, null, null, null, null);
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
        StringBuilder url = new StringBuilder("http://api.openweathermap.org");
        url.append("/data/2.5/group?id=");
        Cursor cursor = queryCitiesList();
        while(!cursor.isAfterLast()) {
            int index = cursor.getColumnIndex(CityWeatherTable.COLUMN_CITY_ID);
            String cityId = cursor.getString(index);
            url.append(cityId + ",");
            cursor.moveToNext();
        }
        url.append("&units=metric");
        url.append("&lang=ru");
        url.append("&appid=");
        url.append(getString(R.string.owm_appid));
        return new URL(new String(url));
    }

    @NonNull
    private Cursor queryCitiesList() {
        DBHelper helper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(true, CityWeatherTable.TABLE_NAME, new String[]{CityWeatherTable.COLUMN_CITY_ID}, null, null, null, null, null, null);
        cursor.moveToPosition(0);
        return cursor;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private class GetForecastTask extends AsyncTask<URL, Void, String>{
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //TODO:show progressbar here
            progress = new ProgressDialog(CityListActivity.this);
            progress.show();
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
            } finally {
                updateUI();
                progress.cancel();
            }
        }
    }

    private void updateUI() {
        CityListAdapter adapter = (CityListAdapter) cityList.getAdapter();
        adapter.getCursor().requery();
        adapter.notifyDataSetChanged();
    }

    private void parseJsonForecastToDB(String s) throws JSONException {
        JSONArray cityList = new JSONObject(s).getJSONArray(OWM_LIST);

        DBHelper helper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(CityWeatherTable.TABLE_NAME, null, null);

            for (int i = 0; i < cityList.length(); i++){
                JSONObject cityForecastObject = cityList.getJSONObject(i);
                ContentValues row = getWeatherRow(cityForecastObject);
                db.insert(CityWeatherTable.TABLE_NAME, null, row);
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }

    @NonNull
    private ContentValues getWeatherRow(JSONObject cityForecastObject) throws JSONException {
        String cityId = cityForecastObject.getString(OWM_CITY_ID);
        String cityName = cityForecastObject.getString(OWM_CITY_NAME);

        JSONObject sysObject = cityForecastObject.getJSONObject(OWM_SYS);
        String country = sysObject.getString(OWM_COUNTRY);

        JSONArray weatherArray = cityForecastObject.getJSONArray(OWM_WEATHER);
        String description = weatherArray.getJSONObject(0).getString(OWM_DESCRIPTION);
        String icon = weatherArray.getJSONObject(0).getString(OWM_ICON);

        JSONObject mainObject = cityForecastObject.getJSONObject(OWM_MAIN);
        double temperature = mainObject.getDouble(OWM_TEMP);
        int pressure = mainObject.getInt(OWM_PRESSURE);
        int humidity = mainObject.getInt(OWM_HUMIDITY);

        JSONObject windObject = cityForecastObject.getJSONObject(OWM_WIND);
        int windSpeed = windObject.getInt(OWM_WIND_SPEED);
        int windDegree = windObject.getInt(OWM_WIND_DIRECTION);

        ContentValues row = new ContentValues();
        row.put(CityWeatherTable.COLUMN_CITY_ID, cityId);
        row.put(CityWeatherTable.COLUMN_CITY_NAME, cityName);
        row.put(CityWeatherTable.COLUMN_COUNTRY, country);
        row.put(CityWeatherTable.COLUMN_WEATHER_DESCRIPTION, description);
        row.put(CityWeatherTable.COLUMN_WEATHER_ICON, icon);
        row.put(CityWeatherTable.COLUMN_TEMPERATURE, temperature);
        row.put(CityWeatherTable.COLUMN_PRESSURE, pressure);
        row.put(CityWeatherTable.COLUMN_HUMIDITY, humidity);
        row.put(CityWeatherTable.COLUMN_WIND_SPEED, windSpeed);
        row.put(CityWeatherTable.COLUMN_WIND_DIRECTION, windDegree);
        return row;
    }

    private String queryForecast(URL url) throws IOException {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJsonStr = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
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
