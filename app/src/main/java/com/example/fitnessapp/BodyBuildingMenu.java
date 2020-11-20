package com.example.fitnessapp;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class BodyBuildingMenu extends AppCompatActivity {

    private ListView mListView4, mListView5, mListView6,mListView7,
            mListView8,mListView9,mListView10;

    String [] meatArray = {"Sirloin steak","Ground beef","Pork tenderloin","Venison","Chicken Breast","Salmon","Cod"};
    String [] dairyArray = {"Yogurt","Cheese","Low-fat milk"};
    String [] grainsArray ={"Bread","Cereal","Crackers","Oatmeal","Quinoa","Popcorn","Rice"};
    String [] nutsArray = {"Almonds","Walnuts","Sunflower seeds","Chia seeds","Flax seeds"};
    String [] beansArray ={"Chickpeas","Lentils","Kidney beans","Black beans","Pinto beans"};
    String [] oilsArray ={"Olive oil","Flaxseed oil","Avocado oil"};
    String [] avoidArray = {"Alcohol", "Sugar","Deep-fried food","Soft drinks"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bodybuilding);

        mListView4 = (ListView)findViewById(R.id.bodybuildingMeat);
        mListView5 = (ListView)findViewById(R.id.bodybuildingDairy);
        mListView6 = (ListView)findViewById(R.id.bodybuildingGrains);
        mListView7 = (ListView)findViewById(R.id.bodybuildingNuts);
        mListView8 = (ListView)findViewById(R.id.bodybuildingBeans);
        mListView9 = (ListView)findViewById(R.id.bodybuildingOils);
        mListView10 = (ListView)findViewById(R.id.bodybuildingAvoid);

        mListView4.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, meatArray));
        mListView5.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dairyArray));
        mListView6.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, grainsArray));
        mListView7.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nutsArray));
        mListView8.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, beansArray));
        mListView9.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, oilsArray));
        mListView10.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, avoidArray));

        ListUtils2.setDynamicHeight(mListView4);
        ListUtils2.setDynamicHeight(mListView5);
        ListUtils2.setDynamicHeight(mListView6);
        ListUtils2.setDynamicHeight(mListView7);
        ListUtils2.setDynamicHeight(mListView8);
        ListUtils2.setDynamicHeight(mListView9);
        ListUtils2.setDynamicHeight(mListView10);
    }
    public static class ListUtils2 {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }
}
