package com.weathersimple.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.weathersimple.db.DBContract.*;

import java.util.ArrayList;

/**
 * Created by Diana on 27.06.2016 at 18:10.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "weather_simple.db";
    private static final String TAG = DBHelper.class.getSimpleName();
    private static DBHelper sInstance;
    private SQLiteDatabase db;
    private Context mContext;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
        db = getWritableDatabase();
    }


    private void addInitialData(SQLiteDatabase db) throws SQLiteConstraintException{
        db.beginTransaction();
        try {
            insertCity(db, "625144", "Minsk", "BY");
            insertCity(db, "703448", "Kiev", "UA");
            insertCity(db, "551487", "Kazan", "RU");
            insertCity(db, "1850147", "Tokyo", "JP");
            insertCity(db, "6692263", "Reykjavik", "IS");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
//                db.close();
        }
    }

    private void insertCity(SQLiteDatabase db, String id, String name, String country) {
        ContentValues row = new ContentValues();
        row.put(CityWeatherTable.COLUMN_CITY_ID, id);
        row.put(CityWeatherTable.COLUMN_CITY_NAME, name);
        row.put(CityWeatherTable.COLUMN_COUNTRY, country);
        db.insert(CityWeatherTable.TABLE_NAME, null, row);
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DBHelper(context);
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
        try {
            addInitialData(db);
        } catch (SQLiteConstraintException e) {
            Toast.makeText(mContext, "Error while adding initial data.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ", ";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + CityWeatherTable.TABLE_NAME + " (" +
                    CityWeatherTable._ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    CityWeatherTable.COLUMN_CITY_ID + TEXT_TYPE + " UNIQUE"+ COMMA_SEP +
                    CityWeatherTable.COLUMN_CITY_NAME + TEXT_TYPE + COMMA_SEP +
                    CityWeatherTable.COLUMN_COUNTRY + TEXT_TYPE + COMMA_SEP +
                    CityWeatherTable.COLUMN_WEATHER_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    CityWeatherTable.COLUMN_WEATHER_ICON + TEXT_TYPE  + COMMA_SEP +
                    CityWeatherTable.COLUMN_WIND_SPEED + INTEGER_TYPE + COMMA_SEP +
                    CityWeatherTable.COLUMN_WIND_DIRECTION + INTEGER_TYPE + COMMA_SEP +
                    CityWeatherTable.COLUMN_HUMIDITY + INTEGER_TYPE + COMMA_SEP +
                    CityWeatherTable.COLUMN_PRESSURE + INTEGER_TYPE + COMMA_SEP +
                    CityWeatherTable.COLUMN_TEMPERATURE + INTEGER_TYPE +
                    " )";

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
