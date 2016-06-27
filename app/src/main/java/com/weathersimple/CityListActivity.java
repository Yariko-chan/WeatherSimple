package com.weathersimple;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityListActivity extends AppCompatActivity implements CityWeatherFragment.OnFragmentInteractionListener{
    ListView cityList;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);

        if (findViewById(R.id.city_detail_container) != null) {
            mTwoPane = true;
        }
        initControls();
    }

    private void initControls() {
        //fake data
        final ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
                5);
        Map<String, Object> m;
        for (int i = 0; i < 5; i++) {
            m = new HashMap<String, Object>();
            m.put("text", "text" + i);
            data.add(m);
        }
        String[] from = {"text"};
        int[] to = {R.id.info};

        cityList = (ListView) findViewById(R.id.city_list);
        cityList.setAdapter(new SimpleAdapter(this,data, R.layout.item_city_list, from, to));
        cityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mTwoPane){
                    bindWeatherFragment(position);
                }else {
                    startWeatherActivity(position);
                }
            }
        });
    }

    private void startWeatherActivity(int position) {
        Intent intent= new Intent(CityListActivity.this, CityWeatherActivity.class);
        intent.putExtra("text", "text" + position);
        startActivityForResult(intent, 0);
    }

    private void bindWeatherFragment(int position) {
        CityWeatherFragment fragment = new CityWeatherFragment();
        Bundle b = new Bundle();
        b.putString("text", "text" + position);
        fragment.setArguments(b);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.city_detail_container, fragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
