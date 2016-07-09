package com.weathersimple.db;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.weathersimple.db.DBContract.*;

import java.util.ArrayList;

/**
 * Created by Diana on 27.06.2016 at 18:10.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "weather_simple.db";
    private static DBHelper sInstance;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        addInitialData();
    }


    private void addInitialData() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(CityTable.CITY_TABLE_NAME, CityTable.columns, null, null, null, null, null);
        //TODO: find way to do check more efficiently or precreaate DB with initial data
        if (cursor.getCount() <= 0){
            db.execSQL(insertCity("625144", "Minsk", "BY"));
            db.execSQL(insertCity("703448", "Kiev", "UA"));
            db.execSQL(insertCity("551487", "Kazan", "RU"));
            db.execSQL(insertCity("1850147", "Tokyo", "JP"));
            db.execSQL(insertCity("6692263", "Reykjavik", "IS"));
        }
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

    public static String insertCity(String cityID, String cityName, String country){
        return "INSERT INTO " + CityTable.CITY_TABLE_NAME +
                " ( " + CityTable.COLUMN_CITY_ID + COMMA_SEP +CityTable.COLUMN_CITY_NAME + COMMA_SEP + CityTable.COLUMN_COUNTRY + " ) " +
                "VALUES ( '" + cityID + "', '" + cityName + "', '" + country + "' )";
    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }
}
