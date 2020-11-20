package com.example.fitnessapp;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WeightLossMenu extends AppCompatActivity {
    private ListView mListView1, mListView2, mListView3;

    String [] proteinArray = {"Beef","Chicken","Pork","Lamb","Eggs","Seafood","Plant-based(Tofu)"};
    String [] vegeArray = {"Broccoli","Cauliflower","Spinach","Tomatoes","Kale","Brussels Sprouts","Cabbage",
    "Swiss chard","Lettuce","Cucumber"};
    String [] fatsArray ={"Olive oil","Avocado Oil"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weightloss);

        mListView1 = (ListView)findViewById(R.id.weightLostProteinListView);
        mListView2 = (ListView)findViewById(R.id.weightLostVegeListView);
        mListView3 = (ListView)findViewById(R.id.weightLostFatsListView);

        mListView1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, proteinArray));
        mListView2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, vegeArray));
        mListView3.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fatsArray));

        ListUtils.setDynamicHeight(mListView1);
        ListUtils.setDynamicHeight(mListView2);
        ListUtils.setDynamicHeight(mListView3);
    }

    public static class ListUtils {
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
