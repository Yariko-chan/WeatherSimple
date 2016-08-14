package com.weathersimple;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.weathersimple.Utils.getDrawable;

public class CityWeatherFragment extends Fragment {

  private static final String OWM_DESCRIPTION = "description";
  private static final String OWM_ICON = "icon";
  private static final String OWM_TEMP = "temp";
  private static final String OWM_PRESSURE = "pressure";
  private static final String OWM_HUMIDITY = "humidity";
  private static final String OWM_WIND_SPEED = "speed";
  private static final String OWM_CITY_NAME = "name";
  private static final String OWM_COUNTRY = "country";

  private OnFragmentInteractionListener mListener;
  private TextView city;
  private TextView country;
  private TextView description;
  private TextView temperature;
  private TextView wind;
  private TextView humidity;
  private TextView pressure;

  public CityWeatherFragment() {
  }

  public static CityWeatherFragment newInstance() {
    CityWeatherFragment myFragment = new CityWeatherFragment();
    return myFragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view;
    view = inflater.inflate(R.layout.fragment_city_weather, container, false);
    city = (TextView) view.findViewById(R.id.city);
    country = (TextView) view.findViewById(R.id.country);
    description = (TextView) view.findViewById(R.id.description);
    temperature = (TextView) view.findViewById(R.id.temperature);
    wind = (TextView) view.findViewById(R.id.wind);
    humidity = (TextView) view.findViewById(R.id.humidity);
    pressure = (TextView) view.findViewById(R.id.pressure);
    return view;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
          + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (getArguments() != null) {
      Bundle b = getArguments();
      setTextByKeyInBundle(city, b, OWM_CITY_NAME);
      setTextByKeyInBundle(country, b, OWM_COUNTRY);
      setTextByKeyInBundle(description, b, OWM_DESCRIPTION);
      setTemperature(b, temperature);
      setIntByKeyInBundle(wind, b, OWM_WIND_SPEED, getResources().getString(R.string.meters_per_second));
      setIntByKeyInBundle(humidity, b, OWM_HUMIDITY, getResources().getString(R.string.percent));
      setIntByKeyInBundle(pressure, b, OWM_PRESSURE, getResources().getString(R.string.hPA));

      String icon = "w" + b.getString(OWM_ICON);
      try {
        Drawable d = getDrawable(getContext(), icon);
        description.setCompoundDrawablesWithIntrinsicBounds(null, d, null, null);
      } catch (Resources.NotFoundException e) {
        //no drawable will set, default icon will stay
      }
    }

  }

  private void setTextByKeyInBundle(TextView tv, Bundle b, String key) {
    tv.setText(b.getString(key));
  }

  private void setIntByKeyInBundle(TextView tv, Bundle b, String key, String endText) {
    String s = "" + b.getInt(key) + " " + endText;
    tv.setText(s);
  }

  private void setTemperature(Bundle b, TextView tv) {
    int temp = b.getInt(OWM_TEMP);
    String t = (temp > 0) ? "+" + temp : "" + temp;
    tv.setText(t);
  }

  public interface OnFragmentInteractionListener {
    void onFragmentInteraction(Uri uri);
  }
}
