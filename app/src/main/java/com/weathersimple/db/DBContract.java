package com.weathersimple.db;

import android.provider.BaseColumns;

/**
 * Created by Diana on 27.06.2016 at 16:57.
 */
public class DBContract {

    public static abstract  class CityWeatherTable implements BaseColumns{
        public static final String TABLE_NAME = "city_weather";
        public static final String COLUMN_CITY_ID = "city_id";
        public static final String COLUMN_CITY_NAME = "city_name";
        public static final String COLUMN_COUNTRY = "country";
        public static final String COLUMN_WEATHER_DESCRIPTION = "description";
        public static final String COLUMN_TEMPERATURE = "temperature";
        public static final String COLUMN_WIND_SPEED = "wind_speed";
        public static final String COLUMN_WIND_DIRECTION = "wind_direction";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_WEATHER_ICON = "icon";
    }
}
