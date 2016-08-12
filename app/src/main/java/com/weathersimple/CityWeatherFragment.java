package com.weathersimple;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.weathersimple.Utils.getDrawable;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CityWeatherFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CityWeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CityWeatherFragment extends Fragment {
    private static final String CITY_ID = "id";
    public static final String OWM_WEATHER = "weather";
    public static final String OWM_DESCRIPTION = "description";
    private static final String OWM_ICON = "icon";
    public static final String OWM_MAIN = "main";
    public static final String OWM_TEMP = "temp";
    public static final String OWM_PRESSURE = "pressure";
    public static final String OWM_HUMIDITY = "humidity";
    public static final String OWM_WIND = "wind";
    public static final String OWM_WIND_SPEED = "speed";
    public static final String OWM_WIND_DIRECTION = "deg";
    private static final String OWM_CITY_NAME = "name";
    private static final String OWM_COUNTRY = "country";
    private String cityId;

    String s;

    private OnFragmentInteractionListener mListener;

    public CityWeatherFragment() {
        // Required empty public constructor
    }
//
//    public static CityWeatherFragment newInstance() {
//        CityWeatherFragment myFragment = new CityWeatherFragment();
//        return myFragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_weather, container, false);
        TextView city = (TextView) view.findViewById(R.id.city);
        TextView country = (TextView) view.findViewById(R.id.country);
        TextView description = (TextView) view.findViewById(R.id.description);
        TextView temperature = (TextView) view.findViewById(R.id.temperature);
        TextView wind = (TextView) view.findViewById(R.id.wind);
        TextView humidity = (TextView) view.findViewById(R.id.humidity);
        TextView pressure = (TextView) view.findViewById(R.id.pressure);
        if (getArguments() != null) {
            Bundle b = getArguments();
            setTextByKeyInBundle(city, b, OWM_CITY_NAME);
            setTextByKeyInBundle(country, b, OWM_COUNTRY);
            setTextByKeyInBundle(description, b, OWM_DESCRIPTION);
            setTemperature(b, temperature);
            setIntByKeyInBundle(wind, b, OWM_WIND_SPEED, getResources().getString(R.string.meters_per_second));
            setIntByKeyInBundle(humidity, b, OWM_HUMIDITY, getResources().getString(R.string.percent));
            setIntByKeyInBundle(pressure, b, OWM_PRESSURE, getResources().getString(R.string.hPA));

            String icon = b.getString(OWM_ICON);
            Drawable d = getDrawable(getContext(), icon);
            description.setCompoundDrawablesWithIntrinsicBounds(null, d, null, null);
        }
        return view;
    }

    private void setTextByKeyInBundle(TextView tv, Bundle b, String key){
        tv.setText(b.getString(key));
    }

    private void setIntByKeyInBundle(TextView tv, Bundle b, String key, String endText){
        s = "" + b.getInt(key) + " " + endText;
        tv.setText(s);
    }

    private void setTemperature(Bundle b, TextView tv) {
        int temp = b.getInt(OWM_TEMP);
        String t = (temp > 0)? "+" + temp : "" + temp;
        tv.setText(t);
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
