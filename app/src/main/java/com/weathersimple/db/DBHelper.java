package com.weathersimple.db;

import android.content.Context;
import android.content.pm.InstrumentationInfo;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.weathersimple.db.DBContract.*;

/**
 * Created by Diana on 27.06.2016 at 18:10.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "weather_simple.db";
    private static DBHelper sInstance;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DBHelper(context);
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CITY_TABLE);
        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ", ";

    private static final String SQL_CREATE_CITY_TABLE =
            "CREATE TABLE " + CityTable.CITY_TABLE_NAME + " (" +
                    CityTable._ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    CityTable.COLUMN_CITY_ID + TEXT_TYPE + COMMA_SEP +
                    CityTable.COLUMN_CITY_NAME + TEXT_TYPE + COMMA_SEP +
                    CityTable.COLUMN_COUNTRY + TEXT_TYPE + " )";

    private static final String SQL_CREATE_WEATHER_TABLE =
            "CREATE TABLE " + WeatherTable.WEATHER_TABLE_NAME + " (" +
                    WeatherTable._ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    WeatherTable.COLUMN_CITY_ID + TEXT_TYPE + COMMA_SEP +
                    WeatherTable.COLUMN_DATE + INTEGER_TYPE + COMMA_SEP +
                    WeatherTable.COLUMN_WEATHER_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    WeatherTable.COLUMN_TEMPERATURE + INTEGER_TYPE + COMMA_SEP +
                    WeatherTable.COLUMN_WIND_SPEED + INTEGER_TYPE + COMMA_SEP +
                    WeatherTable.COLUMN_WIND_DIRECTION + TEXT_TYPE + COMMA_SEP +
                    WeatherTable.COLUMN_PRECIPITATION + INTEGER_TYPE+ COMMA_SEP +
                    WeatherTable.COLUMN_PRESSURE + INTEGER_TYPE + " )";

    private static final String SQL_DELETE_CITY_TABLE =
            "DROP TABLE IF EXISTS " + CityTable.CITY_TABLE_NAME;

    private static final String SQL_DELETE_WEATHER_TABLE =
            "DROP TABLE IF EXISTS " + WeatherTable.WEATHER_TABLE_NAME;
}
