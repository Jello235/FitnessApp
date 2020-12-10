package com.example.fitnessapp.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fitnessapp.R;

public class BodyBuildingMenuActivity extends AppCompatActivity {

    private ListView mListView4, mListView5, mListView6,mListView7;
    private ListView mListView8,mListView9,mListView10;
    private final String[] meatArray = {"Sirloin steak","Ground beef","Pork tenderloin","Venison","Chicken Breast","Salmon","Cod"};
    private final String[] dairyArray = {"Yogurt","Cheese","Low-fat milk"};
    private final String[] grainsArray ={"Bread","Cereal","Crackers","Oatmeal","Quinoa","Popcorn","Rice"};
    private final String[] nutsArray = {"Almonds","Walnuts","Sunflower seeds","Chia seeds","Flax seeds"};
    private final String[] beansArray ={"Chickpeas","Lentils","Kidney beans","Black beans","Pinto beans"};
    private final String[] oilsArray ={"Olive oil","Flaxseed oil","Avocado oil"};
    private final String[] avoidArray = {"Alcohol", "Sugar","Deep-fried food","Soft drinks"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_building_menu);
        initializeUI();
        setupAdapters();
        setupListViews();
    }

    private void initializeUI() {
        mListView4 = findViewById(R.id.bodybuildingMeat);
        mListView5 = findViewById(R.id.bodybuildingDairy);
        mListView6 = findViewById(R.id.bodybuildingGrains);
        mListView7 = findViewById(R.id.bodybuildingNuts);
        mListView8 = findViewById(R.id.bodybuildingBeans);
        mListView9 = findViewById(R.id.bodybuildingOils);
        mListView10 = findViewById(R.id.bodybuildingAvoid);
    }

    private void setupAdapters() {
        mListView4.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, meatArray));
        mListView5.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dairyArray));
        mListView6.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, grainsArray));
        mListView7.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nutsArray));
        mListView8.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, beansArray));
        mListView9.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, oilsArray));
        mListView10.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, avoidArray));
    }

    private void setupListViews() {
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
