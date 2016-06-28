package com.weathersimple;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import static com.weathersimple.db.DBContract.*;

/**
 * Created by Diana on 28.06.2016 at 9:43.
 */
public class CityListAdapter extends CursorAdapter {
    public CityListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_city_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView cityTV = (TextView) view.findViewById(R.id.city);
        TextView countryTV = (TextView) view.findViewById(R.id.country);
        ImageView weatherIcon = (ImageView) view.findViewById(R.id.weather_icon);
        TextView tempTV = (TextView) view.findViewById(R.id.temperature);

        String cityName = getStringByColumnName(cursor, CityTable.COLUMN_CITY_NAME);
        String country = getStringByColumnName(cursor, CityTable.COLUMN_COUNTRY);
        cityTV.setText(cityName);
        countryTV.setText(country);
        tempTV.setText("+25");
    }

    private String getStringByColumnName(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
    }
}
