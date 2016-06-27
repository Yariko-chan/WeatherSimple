package com.weathersimple;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class CityWeatherActivity extends AppCompatActivity implements CityWeatherFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_weather);
        bindWeatherFragment();
    }

    private void bindWeatherFragment() {
        CityWeatherFragment fragment = new CityWeatherFragment();
        Bundle b = new Bundle();
        b.putString("text", getIntent().getStringExtra("text"));
        fragment.setArguments(b);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.city_detail_container, fragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}
