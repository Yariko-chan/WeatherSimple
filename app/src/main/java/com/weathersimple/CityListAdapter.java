package com.weathersimple;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.weathersimple.R;

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
        View view = LayoutInflater.from(context).inflate(R.layout.item_city_list, parent, false);
        ViewHolder holder = new ViewHolder();

        holder.cityTV = (TextView) view.findViewById(R.id.city);
        holder.countryTV = (TextView) view.findViewById(R.id.country);
        holder.weatherIcon = (ImageView) view.findViewById(R.id.weather_icon);
        holder.tempTV = (TextView) view.findViewById(R.id.temperature);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        String cityName = getStringByColumnName(cursor, CityTable.COLUMN_CITY_NAME);
        String country = getStringByColumnName(cursor, CityTable.COLUMN_COUNTRY);
        double temp =  getIntByColumnName(cursor, WeatherTable.COLUMN_TEMPERATURE);
        holder.cityTV.setText(cityName);
        holder.countryTV.setText(country);
        holder.tempTV.setText(""+ temp);
    }

    private String getStringByColumnName(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
    }

    private double getIntByColumnName(Cursor cursor, String columnName) {
        return cursor.getDouble(cursor.getColumnIndexOrThrow(columnName));
    }

    static class ViewHolder {
        TextView cityTV;
        TextView countryTV;
        TextView tempTV;
        ImageView weatherIcon;
    }
}
