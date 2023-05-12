package com.example.HW2.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.example.HW2.fragments.Fragment_GoogleMap;
import com.example.HW2.R;
import com.example.HW2.fragments.Fragment_List;

public class Activity_topTen extends AppCompatActivity {

    private TextView top_ten_LBL_title;
    private TextView top_ten_LBL_map;

    Fragment_GoogleMap fragmentMap;
    Fragment_List fragmentList;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_ten);
        if (getIntent().getBundleExtra("Bundle") != null){
            this.bundle = getIntent().getBundleExtra("Bundle");
        } else {
            this.bundle = new Bundle();
        }
        findViews();
        //Init Fragments
        fragmentMap = new Fragment_GoogleMap();
        fragmentList = new Fragment_List();

        //Open Fragments
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.game_LAY_map, fragmentMap)
                .commit();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.game_LAY_list, fragmentList)
                .commit();

        //Set callBacks
        fragmentList.setActivity(this);
        fragmentList.setCallBackList((lat, lon, playerName) -> fragmentMap.locateOnMap(lat,lon));
    }

    private void findViews() {
        top_ten_LBL_title = findViewById(R.id.game_top_ten_LBL_title);
        top_ten_LBL_map = findViewById(R.id.game_top_ten_LBL_map);
    }

}