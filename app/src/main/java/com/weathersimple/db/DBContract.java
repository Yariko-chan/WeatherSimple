package com.weathersimple.db;

import android.provider.BaseColumns;

/**
 * Created by Diana on 27.06.2016 at 16:57.
 */
public class DBContract {

    public static abstract  class CityTable implements BaseColumns{
        public static final String CITY_TABLE_NAME = "cities";
        public static final String COLUMN_CITY_ID = "city_id";
        public static final String COLUMN_CITY_NAME = "city_name";
        public static final String COLUMN_COUNTRY = "country";

        public static final String[] columns = {_ID, COLUMN_CITY_ID, COLUMN_CITY_NAME, COLUMN_COUNTRY};
    }

    public static abstract class WeatherTable implements BaseColumns{
        public static final String WEATHER_TABLE_NAME = "weather";
        public static final String COLUMN_CITY_ID = "city_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_WEATHER_DESCRIPTION = "description";
        public static final String COLUMN_TEMPERATURE = "temperature";
        public static final String COLUMN_WIND_SPEED = "wind_speed";
        public static final String COLUMN_WIND_DIRECTION = "wind_direction";
        public static final String COLUMN_PRECIPITATION = "precipitations";
        public static final String COLUMN_PRESSURE = "pressure";
    }
}
