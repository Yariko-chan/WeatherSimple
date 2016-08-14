package com.weathersimple;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class CityWeatherActivity extends AppCompatActivity
    implements CityWeatherFragment.OnFragmentInteractionListener {

  private static final String WEATHER = "weather";

  private Fragment mContent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_city_weather);
    if (savedInstanceState != null) {
      mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
    } else {
      bindNewWeatherFragment();
    }
  }

  private void bindNewWeatherFragment() {
    mContent = new CityWeatherFragment();
    Bundle b = getIntent().getBundleExtra(WEATHER);
    mContent.setArguments(b);
    getSupportFragmentManager().beginTransaction()
        .add(R.id.city_detail_container, mContent).commit();
  }

  @Override
  public void onFragmentInteraction(Uri uri) {
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    getSupportFragmentManager().putFragment(outState, "mContent", mContent);
  }
}
